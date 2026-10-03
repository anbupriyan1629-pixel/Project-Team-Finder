package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Student;

public class StudentDAO {

    // Add Student
    public void addStudent(Student student) {

        String sql = "INSERT INTO students (name, email, skill, availability) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, student.getName());
            statement.setString(2, student.getEmail());
            statement.setString(3, student.getSkill());
            statement.setString(4, student.getAvailability());

            statement.executeUpdate();

            System.out.println("Student added successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // View All Students
    public List<Student> getAllStudents() {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM students";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                Student student = new Student(
                    result.getInt("id"),
                    result.getString("name"),
                    result.getString("email"),
                    result.getString("skill"),
                    result.getString("availability")
                );

                students.add(student);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return students;
    }

    // Find Students By Skill
    public List<Student> findStudentsBySkill(String skill) {

        List<Student> students = new ArrayList<>();

        String sql = "SELECT * FROM students WHERE skill LIKE ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "%" + skill + "%");

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                Student student = new Student(
                    result.getInt("id"),
                    result.getString("name"),
                    result.getString("email"),
                    result.getString("skill"),
                    result.getString("availability")
                );

                students.add(student);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return students;
    }

    // Delete Student
    public void deleteStudent(int id) {

        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            int rows = statement.executeUpdate();

            if (rows > 0) {
                System.out.println("Student deleted successfully!");
            } else {
                System.out.println("Student ID not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update Student
    public void updateStudent(Student student) {

        String sql = "UPDATE students SET name = ?, email = ?, skill = ?, availability = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, student.getName());
            statement.setString(2, student.getEmail());
            statement.setString(3, student.getSkill());
            statement.setString(4, student.getAvailability());
            statement.setInt(5, student.getId());

            int rows = statement.executeUpdate();

            if (rows > 0) {
                System.out.println("Student updated successfully!");
            } else {
                System.out.println("Student ID not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}