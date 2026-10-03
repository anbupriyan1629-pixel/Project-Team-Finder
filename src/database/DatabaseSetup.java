package database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {

    public static void main(String[] args) {

        String studentsTable =
            "CREATE TABLE IF NOT EXISTS students (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL," +
            "email TEXT," +
            "skill TEXT," +
            "availability TEXT" +
            ")";

        String projectsTable =
            "CREATE TABLE IF NOT EXISTS projects (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "title TEXT NOT NULL," +
            "description TEXT," +
            "required_skill TEXT," +
            "created_by INTEGER," +
            "FOREIGN KEY (created_by) REFERENCES students(id)" +
            ")";

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement()) {

            statement.execute(studentsTable);
            statement.execute(projectsTable);

            System.out.println("Students table created successfully!");
            System.out.println("Projects table created successfully!");

        } catch (Exception e) {
            System.out.println("Table creation failed!");
            e.printStackTrace();
        }
    }
}