package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Project;

public class ProjectDAO {

    // Add new project
    public void addProject(Project project) {

        String sql = """
                INSERT INTO projects
                (title, description, required_skill, created_by)
                VALUES (?, ?, ?, ?)
                """;

        try (
            Connection connection = DatabaseConnection.connect();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, project.getTitle());
            statement.setString(2, project.getDescription());
            statement.setString(3, project.getRequiredSkill());
            statement.setInt(4, project.getCreatedBy());

            statement.executeUpdate();

            System.out.println("Project added successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Get all projects
    public List<Project> getAllProjects() {

        List<Project> projects = new ArrayList<>();

        String sql = """
                SELECT id, title, description,
                       required_skill, created_by
                FROM projects
                ORDER BY id DESC
                """;

        try (
            Connection connection = DatabaseConnection.connect();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery()
        ) {

            while (result.next()) {

                Project project = new Project(
                    result.getInt("id"),
                    result.getString("title"),
                    result.getString("description"),
                    result.getString("required_skill"),
                    result.getInt("created_by")
                );

                projects.add(project);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return projects;
    }
}