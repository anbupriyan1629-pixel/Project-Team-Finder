package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dao.ProjectDAO;
import dao.StudentDAO;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import model.Project;
import model.Student;

public class ApiServer {

    private static Path frontendDirectory;
    private static String stylesheetName;

    // =====================================================
    // MAIN
    // =====================================================

    public static void main(String[] args) throws IOException {

        frontendDirectory = findFrontendDirectory();

        stylesheetName =
                Files.isRegularFile(
                        frontendDirectory.resolve("style.css"))
                        ? "style.css"
                        : "styles.css";

        // Render provides PORT environment variable.
        // Local computer will use 8080.
        int port = Integer.parseInt(
                System.getenv().getOrDefault("PORT", "8080")
        );

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress("0.0.0.0", port),
                        0
                );

        // =================================================
        // FRONTEND
        // =================================================

        server.createContext(
                "/",
                ApiServer::handleFrontend
        );

        // =================================================
        // APIs
        // =================================================

        server.createContext(
                "/api/test",
                ApiServer::handleTest
        );

        server.createContext(
                "/api/students",
                ApiServer::handleStudents
        );

        server.createContext(
                "/api/projects",
                ApiServer::handleProjects
        );

        server.setExecutor(null);

        System.out.println(
                "Java Backend Server Started!"
        );

        System.out.println(
                "Server running on port: " + port
        );

        System.out.println(
                "Serving frontend from: "
                        + frontendDirectory.toAbsolutePath()
        );

        server.start();
    }

    // =====================================================
    // FIND FRONTEND
    // =====================================================

    private static Path findFrontendDirectory()
            throws IOException {

        Path workingDirectory =
                Paths.get("")
                        .toAbsolutePath();

        Path classDirectory;

        try {

            classDirectory =
                    Paths.get(
                            ApiServer.class
                                    .getProtectionDomain()
                                    .getCodeSource()
                                    .getLocation()
                                    .toURI()
                    );

        } catch (URISyntaxException e) {

            throw new IOException(
                    "Unable to locate Java application files.",
                    e
            );
        }

        Path[] searchLocations = {
                workingDirectory,
                classDirectory
        };

        for (Path location : searchLocations) {

            Path root =
                    Files.isDirectory(location)
                            ? location
                            : location.getParent();

            while (root != null) {

                for (
                        String directoryName :
                        new String[]{
                                "frontend",
                                "forntend"
                        }
                ) {

                    Path candidate =
                            root.resolve(directoryName);

                    if (
                            Files.isRegularFile(
                                    candidate.resolve(
                                            "index.html"
                                    )
                            )
                    ) {

                        return candidate;
                    }
                }

                root =
                        root.getParent();
            }
        }

        throw new IOException(
                "Could not find frontend/index.html."
        );
    }

    // =====================================================
    // FRONTEND
    // =====================================================

    static void handleFrontend(
            HttpExchange exchange)
            throws IOException {

        String path =
                exchange.getRequestURI()
                        .getPath();

        if (path.equals("/")) {
            path = "/index.html";
        }

        Path filePath;

        switch (path) {

            case "/index.html":

                filePath =
                        frontendDirectory
                                .resolve("index.html");

                break;

            case "/style.css":

                filePath =
                        frontendDirectory
                                .resolve(stylesheetName);

                break;

            case "/script.js":

                filePath =
                        frontendDirectory
                                .resolve("script.js");

                break;

            default:

                sendResponse(
                        exchange,
                        404,
                        "Page not found"
                );

                return;
        }

        System.out.println(
                "Request: " + path
        );

        System.out.println(
                "File: "
                        + filePath.toAbsolutePath()
        );

        if (!Files.exists(filePath)) {

            sendResponse(
                    exchange,
                    404,
                    "File not found: "
                            + filePath.toAbsolutePath()
            );

            return;
        }

        byte[] data =
                Files.readAllBytes(filePath);

        if (path.endsWith(".html")) {

            exchange.getResponseHeaders()
                    .set(
                            "Content-Type",
                            "text/html; charset=UTF-8"
                    );

        } else if (path.endsWith(".css")) {

            exchange.getResponseHeaders()
                    .set(
                            "Content-Type",
                            "text/css; charset=UTF-8"
                    );

        } else if (path.endsWith(".js")) {

            exchange.getResponseHeaders()
                    .set(
                            "Content-Type",
                            "application/javascript; charset=UTF-8"
                    );
        }

        exchange.sendResponseHeaders(
                200,
                data.length
        );

        try (
                OutputStream output =
                        exchange.getResponseBody()
        ) {

            output.write(data);
        }
    }

    // =====================================================
    // TEST API
    // =====================================================

    static void handleTest(
            HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        String response =
                "{\"message\":\"Java Backend Connected Successfully!\"}";

        sendJson(
                exchange,
                200,
                response
        );
    }

    // =====================================================
    // STUDENTS API
    // =====================================================

    static void handleStudents(
            HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        String method =
                exchange.getRequestMethod();

        StudentDAO studentDAO =
                new StudentDAO();

        // =================================================
        // GET
        // =================================================

        if (method.equalsIgnoreCase("GET")) {

            List<Student> students =
                    studentDAO.getAllStudents();

            StringBuilder json =
                    new StringBuilder();

            json.append("[");

            for (
                    int i = 0;
                    i < students.size();
                    i++
            ) {

                Student student =
                        students.get(i);

                json.append("{")
                        .append("\"id\":")
                        .append(student.getId())
                        .append(",")
                        .append("\"name\":\"")
                        .append(
                                escapeJson(
                                        student.getName()
                                )
                        )
                        .append("\",")
                        .append("\"email\":\"")
                        .append(
                                escapeJson(
                                        student.getEmail()
                                )
                        )
                        .append("\",")
                        .append("\"skill\":\"")
                        .append(
                                escapeJson(
                                        student.getSkill()
                                )
                        )
                        .append("\",")
                        .append("\"availability\":\"")
                        .append(
                                escapeJson(
                                        student.getAvailability()
                                )
                        )
                        .append("\"")
                        .append("}");

                if (
                        i <
                                students.size() - 1
                ) {
                    json.append(",");
                }
            }

            json.append("]");

            sendJson(
                    exchange,
                    200,
                    json.toString()
            );

            return;
        }

        // =================================================
        // POST
        // =================================================

        if (method.equalsIgnoreCase("POST")) {

            String body =
                    new String(
                            exchange.getRequestBody()
                                    .readAllBytes(),
                            StandardCharsets.UTF_8
                    );

            System.out.println(
                    "POST Student Data:"
            );

            System.out.println(body);

            String name =
                    getJsonValue(
                            body,
                            "name"
                    );

            String email =
                    getJsonValue(
                            body,
                            "email"
                    );

            String skill =
                    getJsonValue(
                            body,
                            "skill"
                    );

            String availability =
                    getJsonValue(
                            body,
                            "availability"
                    );

            if (
                    name == null ||
                            name.isEmpty()
            ) {

                sendJson(
                        exchange,
                        400,
                        "{\"error\":\"Name is required\"}"
                );

                return;
            }

            Student student =
                    new Student(
                            name,
                            email,
                            skill,
                            availability
                    );

            studentDAO.addStudent(student);

            sendJson(
                    exchange,
                    201,
                    "{\"message\":\"Student added successfully!\"}"
            );

            return;
        }

        // =================================================
        // PUT
        // =================================================

        if (method.equalsIgnoreCase("PUT")) {

            String body =
                    new String(
                            exchange.getRequestBody()
                                    .readAllBytes(),
                            StandardCharsets.UTF_8
                    );

            System.out.println(
                    "PUT Student Data:"
            );

            System.out.println(body);

            String idText =
                    getJsonValue(
                            body,
                            "id"
                    );

            String name =
                    getJsonValue(
                            body,
                            "name"
                    );

            String email =
                    getJsonValue(
                            body,
                            "email"
                    );

            String skill =
                    getJsonValue(
                            body,
                            "skill"
                    );

            String availability =
                    getJsonValue(
                            body,
                            "availability"
                    );

            if (
                    idText == null ||
                            idText.isEmpty()
            ) {

                sendJson(
                        exchange,
                        400,
                        "{\"error\":\"Student ID is required\"}"
                );

                return;
            }

            int id;

            try {

                id =
                        Integer.parseInt(idText);

            } catch (NumberFormatException e) {

                sendJson(
                        exchange,
                        400,
                        "{\"error\":\"Invalid student ID\"}"
                );

                return;
            }

            if (
                    name == null ||
                            name.isEmpty()
            ) {

                sendJson(
                        exchange,
                        400,
                        "{\"error\":\"Name is required\"}"
                );

                return;
            }

            Student student =
                    new Student(
                            id,
                            name,
                            email,
                            skill,
                            availability
                    );

            studentDAO.updateStudent(student);

            sendJson(
                    exchange,
                    200,
                    "{\"message\":\"Student updated successfully!\"}"
            );

            return;
        }

        // =================================================
        // DELETE
        // =================================================

        if (method.equalsIgnoreCase("DELETE")) {

            String body =
                    new String(
                            exchange.getRequestBody()
                                    .readAllBytes(),
                            StandardCharsets.UTF_8
                    );

            System.out.println(
                    "DELETE Student Data:"
            );

            System.out.println(body);

            String idText =
                    getJsonValue(
                            body,
                            "id"
                    );

            if (
                    idText == null ||
                            idText.isEmpty()
            ) {

                sendJson(
                        exchange,
                        400,
                        "{\"error\":\"Student ID is required\"}"
                );

                return;
            }

            int id;

            try {

                id =
                        Integer.parseInt(idText);

            } catch (NumberFormatException e) {

                sendJson(
                        exchange,
                        400,
                        "{\"error\":\"Invalid student ID\"}"
                );

                return;
            }

            studentDAO.deleteStudent(id);

            sendJson(
                    exchange,
                    200,
                    "{\"message\":\"Student deleted successfully!\"}"
            );

            return;
        }

        // =================================================
        // OPTIONS
        // =================================================

        if (method.equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(
                    204,
                    -1
            );

            return;
        }

        sendJson(
                exchange,
                405,
                "{\"error\":\"Method not allowed\"}"
        );
    }

    // =====================================================
    // PROJECTS API
    // =====================================================

    static void handleProjects(
            HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        String method =
                exchange.getRequestMethod();

        ProjectDAO projectDAO =
                new ProjectDAO();

        // =================================================
        // GET
        // =================================================

        if (method.equalsIgnoreCase("GET")) {

            List<Project> projects =
                    projectDAO.getAllProjects();

            StringBuilder json =
                    new StringBuilder();

            json.append("[");

            for (
                    int i = 0;
                    i < projects.size();
                    i++
            ) {

                Project project =
                        projects.get(i);

                json.append("{")
                        .append("\"id\":")
                        .append(project.getId())
                        .append(",")
                        .append("\"title\":\"")
                        .append(
                                escapeJson(
                                        project.getTitle()
                                )
                        )
                        .append("\",")
                        .append("\"description\":\"")
                        .append(
                                escapeJson(
                                        project.getDescription()
                                )
                        )
                        .append("\",")
                        .append("\"requiredSkill\":\"")
                        .append(
                                escapeJson(
                                        project.getRequiredSkill()
                                )
                        )
                        .append("\",")
                        .append("\"createdBy\":")
                        .append(project.getCreatedBy())
                        .append("}");

                if (
                        i <
                                projects.size() - 1
                ) {

                    json.append(",");
                }
            }

            json.append("]");

            sendJson(
                    exchange,
                    200,
                    json.toString()
            );

            return;
        }

        // =================================================
        // POST
        // =================================================

        if (method.equalsIgnoreCase("POST")) {

            String body =
                    new String(
                            exchange.getRequestBody()
                                    .readAllBytes(),
                            StandardCharsets.UTF_8
                    );

            System.out.println(
                    "POST Project Data:"
            );

            System.out.println(body);

            String title =
                    getJsonValue(
                            body,
                            "title"
                    );

            String description =
                    getJsonValue(
                            body,
                            "description"
                    );

            String requiredSkill =
                    getJsonValue(
                            body,
                            "requiredSkill"
                    );

            String createdByText =
                    getJsonValue(
                            body,
                            "createdBy"
                    );

            if (
                    title == null ||
                            title.isEmpty()
            ) {

                sendJson(
                        exchange,
                        400,
                        "{\"error\":\"Project title is required\"}"
                );

                return;
            }

            if (
                    createdByText == null ||
                            createdByText.isEmpty()
            ) {

                sendJson(
                        exchange,
                        400,
                        "{\"error\":\"Created by student ID is required\"}"
                );

                return;
            }

            int createdBy;

            try {

                createdBy =
                        Integer.parseInt(
                                createdByText
                        );

            } catch (NumberFormatException e) {

                sendJson(
                        exchange,
                        400,
                        "{\"error\":\"Invalid student ID\"}"
                );

                return;
            }

            Project project =
                    new Project(
                            title,
                            description,
                            requiredSkill,
                            createdBy
                    );

            projectDAO.addProject(project);

            sendJson(
                    exchange,
                    201,
                    "{\"message\":\"Project added successfully!\"}"
            );

            return;
        }

        // =================================================
        // OPTIONS
        // =================================================

        if (method.equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(
                    204,
                    -1
            );

            return;
        }

        sendJson(
                exchange,
                405,
                "{\"error\":\"Method not allowed\"}"
        );
    }

    // =====================================================
    // JSON VALUE READER
    // =====================================================

    static String getJsonValue(
            String json,
            String key) {

        String search =
                "\"" + key + "\"";

        int keyIndex =
                json.indexOf(search);

        if (keyIndex == -1) {
            return null;
        }

        int colonIndex =
                json.indexOf(
                        ":",
                        keyIndex
                );

        if (colonIndex == -1) {
            return null;
        }

        int firstQuote =
                json.indexOf(
                        "\"",
                        colonIndex
                );

        if (firstQuote == -1) {
            return null;
        }

        int secondQuote =
                json.indexOf(
                        "\"",
                        firstQuote + 1
                );

        if (secondQuote == -1) {
            return null;
        }

        return json.substring(
                firstQuote + 1,
                secondQuote
        );
    }

    // =====================================================
    // JSON ESCAPE
    // =====================================================

    static String escapeJson(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\n",
                        "\\n"
                )
                .replace(
                        "\r",
                        "\\r"
                );
    }

    // =====================================================
    // CORS
    // =====================================================

    static void addCorsHeaders(
            HttpExchange exchange) {

        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Origin",
                        "*"
                );

        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Methods",
                        "GET, POST, PUT, DELETE, OPTIONS"
                );

        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Headers",
                        "Content-Type"
                );
    }

    // =====================================================
    // JSON RESPONSE
    // =====================================================

    static void sendJson(
            HttpExchange exchange,
            int statusCode,
            String response
    ) throws IOException {

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                );

        byte[] bytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.sendResponseHeaders(
                statusCode,
                bytes.length
        );

        try (
                OutputStream output =
                        exchange.getResponseBody()
        ) {

            output.write(bytes);
        }
    }

    // =====================================================
    // NORMAL RESPONSE
    // =====================================================

    static void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response
    ) throws IOException {

        byte[] bytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.sendResponseHeaders(
                statusCode,
                bytes.length
        );

        try (
                OutputStream output =
                        exchange.getResponseBody()
        ) {

            output.write(bytes);
        }
    }
}