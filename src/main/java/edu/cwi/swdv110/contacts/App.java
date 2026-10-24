package edu.cwi.swdv110.contacts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("contacts.txt"));
        List<Contact> contacts = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            try {
                Contact c = ContactParser.parseLine(line);
                contacts.add(c);
            } catch (IllegalArgumentException e) {
                System.out.printf("Skipped line %d: %s%n", (i + 1), e.getMessage());
            }
        }

        System.out.println();
        System.out.print(ContactReport.buildReport(contacts));
        System.out.println();

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("Search (or 'quit'): ");
            String term = sc.nextLine().trim();

            if (term.equalsIgnoreCase("quit")) {
                System.out.println("Bye!");
                break;
            }

            if (term.isEmpty()) {
                continue;
            }

            int matches = 0;
            for (Contact c : contacts) {
                if (c.name().toLowerCase().contains(term.toLowerCase())) {
                    String masked = ContactParser.maskEmail(c.email());
                    System.out.println(c.name() + " " + masked);
                    matches++;
                }
            }
            System.out.println(matches + " match(es)\n");
        }
        sc.close();
    }
}