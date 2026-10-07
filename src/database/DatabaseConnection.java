package database;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_FILE_NAME = "projectfinder.db";
    private static final String URL = buildUrl();

    private static String buildUrl() {
        try {
            Path projectRoot = resolveProjectRoot();
            Path dbPath = projectRoot.resolve(DB_FILE_NAME).toAbsolutePath().normalize();

            if (!Files.exists(dbPath)) {
                System.out.println("SQLite database not found at: " + dbPath);
            }

            System.out.println("Using SQLite database: " + dbPath);
            return "jdbc:sqlite:" + dbPath;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to resolve SQLite database path.", e);
        }
    }

    private static Path resolveProjectRoot() throws URISyntaxException, IOException {
        Path current = Paths.get("").toAbsolutePath().normalize();

        if (Files.isDirectory(current.resolve("src")) && Files.isDirectory(current.resolve("lib"))) {
            return current;
        }

        Path classLocation = Paths.get(
                DatabaseConnection.class.getProtectionDomain().getCodeSource().getLocation().toURI()
        ).toAbsolutePath().normalize();

        Path search = Files.isRegularFile(classLocation) ? classLocation.getParent() : classLocation;

        while (search != null) {
            if (Files.isDirectory(search.resolve("src")) && Files.isDirectory(search.resolve("lib"))) {
                return search;
            }
            search = search.getParent();
        }

        return current;
    }

    public static Connection connect() {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(URL);
            connection.setAutoCommit(true);
            System.out.println("SQLite Database Connected Successfully!");
        } catch (SQLException e) {
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
        }

        return connection;
    }
}