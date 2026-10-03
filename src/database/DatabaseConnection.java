package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:projectfinder.db";

    public static Connection connect() {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(URL);
            System.out.println("SQLite Database Connected Successfully!");
        } catch (SQLException e) {
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
        }

        return connection;
    }
}