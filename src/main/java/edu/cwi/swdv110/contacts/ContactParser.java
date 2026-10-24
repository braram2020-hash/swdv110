package edu.cwi.swdv110.contacts;

public final class ContactParser {

    private ContactParser() { }

    // Pone en mayúscula la primera letra y tras cada guion
    public static String titleCase(String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(word.toLowerCase());
        boolean capNext = true;
        
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            if (c == '-') {
                capNext = true;
            } else if (capNext) {
                sb.setCharAt(i, Character.toUpperCase(c));
                capNext = false;
            }
        }
        return sb.toString();
    }

    // Limpia espacios y acomoda formato Apellido, Nombre
    public static String normalizeName(String raw) {
        if (raw == null) return "";
        raw = raw.trim();
        if (raw.isEmpty()) return "";
        
        if (raw.contains(",")) {
            String[] parts = raw.split(",", 2);
            raw = parts[1].trim() + " " + parts[0].trim();
        }
        
        String[] words = raw.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            words[i] = titleCase(words[i]);
        }
        
        return String.join(" ", words);
    }

    // Valida las reglas básicas del correo
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        email = email.trim();
        int at = email.indexOf('@');
        if (at <= 0 || at != email.lastIndexOf('@') || at == email.length() - 1) {
            return false;
        }
        String domain = email.substring(at + 1);
        if (!domain.contains(".") || domain.startsWith(".") || domain.endsWith(".") || domain.contains("..")) {
            return false;
        }
        return !email.contains(" ");
    }

    public static String normalizeEmail(String email) {
        if (email == null) return "";
        return email.trim().toLowerCase();
    }

    // Enmascara el correo por privacidad
    public static String maskEmail(String email) {
        if (!isValidEmail(email)) return normalizeEmail(email);
        email = normalizeEmail(email);
        int at = email.indexOf('@');
        String local = email.substring(0, at);
        String domain = email.substring(at);

        if (local.length() <= 2) {
            return local.charAt(0) + "***" + domain;
        }
        return local.charAt(0) + "***" + local.charAt(local.length() - 1) + domain;
    }

    // Extrae dígitos y da formato de teléfono
    public static String formatPhone(String raw) {
        if (raw == null) return "";
        StringBuilder digits = new StringBuilder();
        for (char c : raw.toCharArray()) {
            if (Character.isDigit(c)) {
                digits.append(c);
            }
        }
        
        String d = digits.toString();
        if (d.length() == 11 && d.startsWith("1")) {
            d = d.substring(1);
        }
        if (d.length() != 10) {
            return "INVALID";
        }
        return String.format("(%s) %s-%s", d.substring(0, 3), d.substring(3, 6), d.substring(6, 10));
    }

    // Genera el usuario con la inicial y el apellido
    public static String makeUsername(String fullName) {
        if (fullName == null) return "";
        String cleaned = normalizeName(fullName).toLowerCase();
        if (cleaned.isEmpty()) return "";
        
        // Quitar apóstrofes u otros caracteres si es necesario
        cleaned = cleaned.replace("'", "");
        
        String[] parts = cleaned.split("\\s+");
        if (parts.length == 1) return parts[0];
        
        char first = parts[0].charAt(0);
        String last = parts[parts.length - 1];
        
        String user = "" + first + last;
        if (user.length() > 8) {
            user = user.substring(0, 8);
        }
        return user;
    }

    // Lee y divide cada línea del archivo
    public static Contact parseLine(String line) {
        if (line == null) return null;
        line = line.trim();
        if (line.isEmpty()) return null;

        String delimiter = line.contains("|") ? "\\|" : ",";
        String[] parts = line.split(delimiter);

        if (parts.length != 3) {
            throw new IllegalArgumentException("expected 3 fields but found " + parts.length);
        }

        String name = normalizeName(parts[0]);
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name is blank");
        }

        String email = normalizeEmail(parts[1]);
        String phone = formatPhone(parts[2]);

        return new Contact(name, email, phone);
    }
}