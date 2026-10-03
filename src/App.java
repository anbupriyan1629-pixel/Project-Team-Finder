import dao.ProjectDAO;
import dao.StudentDAO;
import java.util.List;
import java.util.Scanner;
import model.Project;
import model.Student;

public class App {

    static Scanner scanner = new Scanner(System.in);
    static StudentDAO studentDAO = new StudentDAO();
    static ProjectDAO projectDAO = new ProjectDAO();

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n================================");
            System.out.println("       PROJECT TEAM FINDER");
            System.out.println("================================");
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Create Project");
            System.out.println("4. View Projects");
            System.out.println("5. Find Team Members");
            System.out.println("6. Delete Student");
            System.out.println("7. Update Student");
            System.out.println("8. Exit");
            System.out.println("================================");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    addStudent();
                    break;

                case 2:
                    viewStudents();
                    break;

                case 3:
                    createProject();
                    break;

                case 4:
                    viewProjects();
                    break;
                case 5:
                    findTeamMembers();
                    break;
                case 6:
                    deleteStudent();
                    break;
                case 7:
                    updateStudent();
                    break;
                case 8:
                    System.out.println("Thank you for using Project Team Finder!");
                    scanner.close();
                    return;

               
            }
        }
    }

    // Add Student
    static void addStudent() {

        System.out.println("\n--- Add Student ---");

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter skill: ");
        String skill = scanner.nextLine();

        System.out.print("Enter availability: ");
        String availability = scanner.nextLine();

        Student student = new Student(
                name,
                email,
                skill,
                availability
        );

        studentDAO.addStudent(student);
    }

    // View Students
    static void viewStudents() {

        System.out.println("\n--- Students ---");

        List<Student> students = studentDAO.getAllStudents();

        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        for (Student s : students) {

            System.out.println(
                    "ID: " + s.getId() +
                    " | Name: " + s.getName() +
                    " | Email: " + s.getEmail() +
                    " | Skill: " + s.getSkill() +
                    " | Availability: " + s.getAvailability()
            );
        }
    }

    // Create Project
    static void createProject() {

        System.out.println("\n--- Create Project ---");

        System.out.print("Enter project title: ");
        String title = scanner.nextLine();

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter required skill: ");
        String requiredSkill = scanner.nextLine();

        System.out.print("Enter your student ID: ");
        int createdBy = scanner.nextInt();
        scanner.nextLine();

        Project project = new Project(
                title,
                description,
                requiredSkill,
                createdBy
        );

        projectDAO.addProject(project);
    }

    // View Projects
    static void viewProjects() {

        System.out.println("\n--- Projects ---");

        List<Project> projects = projectDAO.getAllProjects();

        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }

        for (Project p : projects) {

            System.out.println(
                    "ID: " + p.getId() +
                    " | Title: " + p.getTitle() +
                    " | Description: " + p.getDescription() +
                    " | Required Skill: " + p.getRequiredSkill() +
                    " | Created By: " + p.getCreatedBy()
            );
        }
    }

    // Find Team Members
    static void findTeamMembers() {

        System.out.println("\n--- Find Team Members ---");

        System.out.print("Enter required skill: ");
        String skill = scanner.nextLine();

        List<Student> students =
                studentDAO.findStudentsBySkill(skill);

        if (students.isEmpty()) {
            System.out.println("No matching students found.");
            return;
        }

        System.out.println("\nMatching Team Members:");

        for (Student s : students) {

            System.out.println(
                    "ID: " + s.getId() +
                    " | Name: " + s.getName() +
                    " | Skill: " + s.getSkill() +
                    " | Availability: " + s.getAvailability()
            );
        }
    }
    // Delete Student
static void deleteStudent() {

    System.out.println("\n--- Delete Student ---");

    System.out.print("Enter student ID to delete: ");
    int id = scanner.nextInt();
    scanner.nextLine();

    studentDAO.deleteStudent(id);
}
// Update Student
static void updateStudent() {

    System.out.println("\n--- Update Student ---");

    System.out.print("Enter student ID: ");
    int id = scanner.nextInt();
    scanner.nextLine();

    System.out.print("Enter new name: ");
    String name = scanner.nextLine();

    System.out.print("Enter new email: ");
    String email = scanner.nextLine();

    System.out.print("Enter new skill: ");
    String skill = scanner.nextLine();

    System.out.print("Enter new availability: ");
    String availability = scanner.nextLine();

    Student student = new Student(
            id,
            name,
            email,
            skill,
            availability
    );

    studentDAO.updateStudent(student);
}
}