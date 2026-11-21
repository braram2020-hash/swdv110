package edu.cwi.swdv110.books;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BookDAOTest {

    @TempDir
    Path tmp;
    String url;
    BookDAO dao;

    static final Book DUNE = new Book(0, "9780441172719", "Dune", "Frank Herbert", 1965, 5, true);
    static final Book MARTIAN = new Book(0, "9780553418026", "The Martian", "Andy Weir", 2014, 0, false);
    static final Book HAIL_MARY = new Book(0, "9780593135204", "Project Hail Mary", "Andy Weir", 2021, 4, true);

    @BeforeEach
    void setUp() throws Exception {
        url = "jdbc:sqlite:" + tmp.resolve("test.db");
        Database.initialize(url);
        dao = new BookDAO(url);
        dao.add(DUNE);
        dao.add(MARTIAN);
        dao.add(HAIL_MARY);
    }

    private static List<String> titles(List<Book> books) {
        return books.stream().map(Book::title).toList();
    }

    @Test
    void schemaRejectsDuplicateIsbnAndBadRating() throws Exception {
        try (Connection c = Database.connect(url); Statement st = c.createStatement()) {
            assertThrows(SQLException.class, () -> st.executeUpdate(
                    "INSERT INTO books (isbn, title, author, year) VALUES ('9780441172719', 'Copy', 'X', 2000)"),
                    "isbn must be UNIQUE");
            assertThrows(SQLException.class, () -> st.executeUpdate(
                    "INSERT INTO books (isbn, title, author, year, rating) VALUES ('1111111111', 'T', 'A', 2000, 9)"),
                    "rating needs a CHECK constraint (0-5)");
            assertThrows(SQLException.class, () -> st.executeUpdate(
                    "INSERT INTO books (isbn, author, year) VALUES ('2222222222', 'A', 2000)"),
                    "title must be NOT NULL");
        }
    }

    @Test
    void schemaHasDefaults() throws Exception {
        try (Connection c = Database.connect(url); Statement st = c.createStatement()) {
            st.executeUpdate("INSERT INTO books (isbn, title, author, year) VALUES ('3333333333', 'Defaults', 'A', 2000)");
        }
        Book b = dao.findByIsbn("3333333333").orElseThrow();
        assertEquals(0, b.rating());
        assertFalse(b.read());
    }

    @Test
    void getAllOrderedByTitle() throws SQLException {
        assertEquals(List.of("Dune", "Project Hail Mary", "The Martian"), titles(dao.getAll()));
    }

    @Test
    void findByIsbnMapsEveryColumn() throws SQLException {
        Book b = dao.findByIsbn("9780593135204").orElseThrow();
        assertTrue(b.id() > 0, "id comes from the database");
        assertEquals(new Book(b.id(), "9780593135204", "Project Hail Mary", "Andy Weir", 2021, 4, true), b);
        assertEquals(Optional.empty(), dao.findByIsbn("0000000000"));
    }

    @Test
    void searchTitleOrAuthorIgnoringCase() throws SQLException {
        assertEquals(List.of("Project Hail Mary", "The Martian"), titles(dao.search("weir")));
        assertEquals(List.of("Dune"), titles(dao.search("UN")));
        assertEquals(List.of(), titles(dao.search("tolkien")));
    }

    @Test
    void unread() throws SQLException {
        assertEquals(List.of("The Martian"), titles(dao.unread()));
    }

    @Test
    void addReturnsNewIdAndRejectsDuplicates() throws SQLException {
        int id = dao.add(new Book(0, "9780547928227", "The Hobbit", "J.R.R. Tolkien", 1937, 0, false));
        assertTrue(id > 0);
        assertEquals(id, dao.findByIsbn("9780547928227").orElseThrow().id());
        assertThrows(SQLException.class, () -> dao.add(DUNE), "duplicate ISBN");
    }

    @Test
    void updateChangesTheRowWithThatId() throws SQLException {
        Book m = dao.findByIsbn("9780553418026").orElseThrow();
        assertTrue(dao.update(new Book(m.id(), m.isbn(), "The Martian (Classroom Ed.)", m.author(), 2016, 3, true)));
        assertEquals(new Book(m.id(), m.isbn(), "The Martian (Classroom Ed.)", "Andy Weir", 2016, 3, true),
                dao.findByIsbn(m.isbn()).orElseThrow());
        assertFalse(dao.update(new Book(9999, "x", "x", "x", 2000, 0, false)), "no row with id 9999");
    }

    @Test
    void markRead() throws SQLException {
        assertTrue(dao.markRead("9780553418026", 4));
        Book m = dao.findByIsbn("9780553418026").orElseThrow();
        assertTrue(m.read());
        assertEquals(4, m.rating());
        assertFalse(dao.markRead("0000000000", 3));
        assertThrows(IllegalArgumentException.class, () -> dao.markRead("9780553418026", 6));
        assertThrows(IllegalArgumentException.class, () -> dao.markRead("9780553418026", 0));
    }

    @Test
    void delete() throws SQLException {
        assertTrue(dao.delete("9780441172719"));
        assertFalse(dao.delete("9780441172719"), "already gone");
        assertEquals(2, dao.getAll().size());
    }

    @Test
    void countByAuthorMostFirst() throws SQLException {
        Map<String, Integer> counts = dao.countByAuthor();
        assertEquals(Map.of("Andy Weir", 2, "Frank Herbert", 1), counts);
        assertEquals("Andy Weir", counts.keySet().iterator().next());
    }

    @Test
    void averageRatingIgnoresUnrated() throws SQLException {
        assertEquals(4.5, dao.averageRating(), 0.0001);
        dao.delete("9780441172719");
        dao.delete("9780593135204");
        assertEquals(0.0, dao.averageRating(), 0.0001);
    }

    @Test
    void parametersBlockSqlInjection() throws SQLException {
        assertEquals(List.of(), dao.search("' OR '1'='1"));
        assertEquals(Optional.empty(), dao.findByIsbn("x' OR 1=1 --"));
        assertFalse(dao.delete("' OR 1=1 --"));
        assertEquals(3, dao.getAll().size(), "nothing was deleted");
    }
}