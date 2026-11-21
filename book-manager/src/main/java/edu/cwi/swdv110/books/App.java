package edu.cwi.swdv110.books;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Year;
import java.util.List;
import java.util.Scanner;

public class App {
    private static final String URL = "jdbc:sqlite:books.db";
    private static final BookDAO dao = new BookDAO(URL);
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        try {
            Database.initialize(URL);
            if (dao.getAll().isEmpty()) {
                Database.runScript(URL, Path.of("seed.sql"));
                System.out.println("New database – loaded seed.sql");
            }
        } catch (SQLException e) {
            System.out.println("Could not open the database: " + e.getMessage());
            return;
        }

        while (true) {
            System.out.println("""
                    1) List all  2) Search  3) Unread  4) Add  5) Mark read
                    6) Edit      7) Delete  8) Stats   0) Quit""");
            String choice = prompt("Choice");
            try {
                switch (choice) {
                    case "1" -> print(dao.getAll());
                    case "2" -> print(dao.search(prompt("Title or author contains")));
                    case "3" -> print(dao.unread());
                    case "4" -> addBook();
                    case "5" -> {
                        String isbn = cleanIsbn(prompt("ISBN"));
                        int rating = Integer.parseInt(prompt("Rating 1-5"));
                        System.out.println(dao.markRead(isbn, rating) ? "Updated" : "No book with that ISBN");
                    }
                    case "6" -> editBook();
                    case "7" -> {
                        String isbn = cleanIsbn(prompt("ISBN to delete"));
                        if (prompt("Are you sure? (y/n)").equalsIgnoreCase("y")) {
                            System.out.println(dao.delete(isbn) ? "Deleted" : "No book with that ISBN");
                        }
                    }
                    case "8" -> {
                        System.out.println("Books per author: " + dao.countByAuthor());
                        System.out.printf("Average rating: %.2f%n", dao.averageRating());
                    }
                    case "0" -> {
                        System.out.println("Bye!");
                        return;
                    }
                    default -> System.out.println("Pick 0-8");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
    }

    private static String prompt(String label) {
        System.out.print(label + ": ");
        return sc.hasNextLine() ? sc.nextLine().trim() : "0";
    }

    static String cleanIsbn(String raw) {
        String cleaned = raw.replace("-", "").replace(" ", "");
        if (!cleaned.matches("\\d{10}|\\d{13}")) {
            throw new IllegalArgumentException("ISBN must be 10 or 13 digits");
        }
        return cleaned;
    }

    private static void addBook() throws SQLException {
        String isbn = cleanIsbn(prompt("ISBN"));

        String title = prompt("Title");
        if (title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }

        String author = prompt("Author");
        if (author.isBlank()) {
            throw new IllegalArgumentException("Author is required");
        }

        int maxYear = Year.now().getValue();
        int year = Integer.parseInt(prompt("Year"));
        if (year < 1450 || year > maxYear) {
            throw new IllegalArgumentException("Year must be between 1450 and " + maxYear);
        }

        int id = dao.add(new Book(0, isbn, title, author, year, 0, false));
        System.out.println("Added with id " + id);
    }

    private static void editBook() throws SQLException {
        String isbn = cleanIsbn(prompt("ISBN"));
        Book existing = dao.findByIsbn(isbn).orElse(null);
        if (existing == null) {
            System.out.println("No book with that ISBN");
            return;
        }

        String newTitle = prompt("Title [" + existing.title() + "]");
        if (newTitle.isBlank()) {
            newTitle = existing.title();
        }

        String newAuthor = prompt("Author [" + existing.author() + "]");
        if (newAuthor.isBlank()) {
            newAuthor = existing.author();
        }

        Book updated = new Book(
                existing.id(),
                existing.isbn(),
                newTitle,
                newAuthor,
                existing.year(),
                existing.rating(),
                existing.read()
        );
        System.out.println(dao.update(updated) ? "Updated" : "No book with that ISBN");
    }

    private static void print(List<Book> books) {
        System.out.printf("%-14s %-28s %-20s %4s %-5s %s%n", "ISBN", "TITLE", "AUTHOR", "YEAR", "READ", "RATING");
        for (Book b : books) {
            System.out.printf("%-14s %-28s %-20s %4d %-5s %s%n", b.isbn(), b.title(), b.author(), b.year(),
                    b.read() ? "yes" : "no", "*".repeat(b.rating()));
        }
        System.out.println(books.size() + " book(s)");
    }
}