package edu.cwi.swdv110.books;

public record Book(
        int id,
        String isbn,
        String title,
        String author,
        int year,
        int rating,
        boolean read
) { }