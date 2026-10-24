package edu.cwi.swdv110.contacts;

import java.util.List;

public final class ContactReport {

    private static final String ROW = "%-22s %-30s %-15s %-9s%n";

    private ContactReport() { }

    public static String buildReport(List<Contact> contacts) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(String.format(ROW, "NAME", "EMAIL", "PHONE", "USERNAME"));
        sb.append("-".repeat(78)).append(System.lineSeparator());

        int badEmails = 0;
        int badPhones = 0;

        for (Contact c : contacts) {
            boolean okEmail = ContactParser.isValidEmail(c.email());
            boolean okPhone = !c.phone().equals("INVALID");

            if (!okEmail) badEmails++;
            if (!okPhone) badPhones++;

            String emailToShow = okEmail ? c.email() : "!! " + c.email();
            String user = ContactParser.makeUsername(c.name());

            sb.append(String.format(ROW, c.name(), emailToShow, c.phone(), user));
        }

        sb.append("-".repeat(78)).append(System.lineSeparator());
        sb.append(String.format("Total contacts: %d | Invalid emails: %d | Invalid phones: %d%n",
                contacts.size(), badEmails, badPhones));

        return sb.toString();
    }
}