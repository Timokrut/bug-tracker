package tracker.util;

import java.sql.Connection;
import java.sql.DriverManager; 

public class DB {
    private static final String URL = "jdbc:postgresql://localhost:5432/bugtracker";
    private static final String USER = "my_user";
    private static final String PASSWORD = "my_pass";

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to db", e);
        }
    }
}
