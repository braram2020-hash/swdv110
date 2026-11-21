package edu.cwi.swdv110.books;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private Database() { }

    public static Connection connect(String url) throws SQLException {
        return DriverManager.getConnection(url);
    }

    public static void runScript(String url, Path script) throws SQLException, IOException {
        StringBuilder sql = new StringBuilder();
        for (String line : Files.readAllLines(script)) {
            if (!line.trim().startsWith("--")) {
                sql.append(line).append('\n');
            }
        }
        try (Connection c = connect(url); Statement st = c.createStatement()) {
            for (String statement : sql.toString().split(";")) {
                if (!statement.isBlank()) {
                    st.executeUpdate(statement);
                }
            }
        }
    }

    public static void initialize(String url) throws SQLException, IOException {
        runScript(url, Path.of("schema.sql"));
    }
}