const API_URL = "/api";


// ===============================
// PAGE LOAD
// ===============================

document.addEventListener("DOMContentLoaded", () => {
    loadStudents();
    loadProjects();

    const searchType = document.getElementById("searchType");

    searchType.addEventListener("change", () => {
        const input = document.getElementById("searchInput");

        if (searchType.value === "students") {
            input.placeholder = "Search students...";
        } else {
            input.placeholder = "Search projects...";
        }

        input.value = "";
        handleSearch();
    });
});


// ===============================
// STUDENTS
// ===============================

async function loadStudents() {

    try {
        const response = await fetch(`${API_URL}/students`);

        if (!response.ok) {
            throw new Error("Failed to load students");
        }

        const students = await response.json();

        displayStudents(students);

        document.getElementById("studentCount").textContent =
            students.length;

    } catch (error) {
        console.error("Student loading error:", error);
    }
}


function displayStudents(students) {

    const container = document.getElementById("studentList");

    container.innerHTML = "";

    if (students.length === 0) {
        container.innerHTML =
            "<p>No students found.</p>";
        return;
    }

    students.forEach(student => {

        const card = document.createElement("div");

        card.className = "card";

        card.innerHTML = `
            <h3>${escapeHtml(student.name)}</h3>

            <p>
                <strong>Email:</strong>
                ${escapeHtml(student.email || "-")}
            </p>

            <p>
                <strong>Skill:</strong>
                ${escapeHtml(student.skill || "-")}
            </p>

            <p>
                <strong>Availability:</strong>
                ${escapeHtml(student.availability || "-")}
            </p>

            <div class="card-buttons">

                <button onclick="editStudent(
                    ${student.id},
                    '${escapeJs(student.name)}',
                    '${escapeJs(student.email || "")}',
                    '${escapeJs(student.skill || "")}',
                    '${escapeJs(student.availability || "")}'
                )">
                    Edit
                </button>

                <button
                    class="delete-btn"
                    onclick="deleteStudent(${student.id})">
                    Delete
                </button>

            </div>
        `;

        container.appendChild(card);
    });
}


// ===============================
// ADD STUDENT
// ===============================

async function addStudent() {

    const name =
        document.getElementById("studentName").value.trim();

    const email =
        document.getElementById("studentEmail").value.trim();

    const skill =
        document.getElementById("studentSkill").value.trim();

    const availability =
        document.getElementById("studentAvailability").value.trim();


    if (!name) {
        alert("Please enter student name.");
        return;
    }


    try {

        const response = await fetch(
            `${API_URL}/students`,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    name: name,
                    email: email,
                    skill: skill,
                    availability: availability
                })
            }
        );


        if (!response.ok) {
            throw new Error("Failed to add student");
        }


        alert("Student added successfully!");


        document.getElementById("studentName").value = "";
        document.getElementById("studentEmail").value = "";
        document.getElementById("studentSkill").value = "";
        document.getElementById("studentAvailability").value = "";


        loadStudents();

    } catch (error) {

        console.error(error);

        alert("Error adding student.");
    }
}


// ===============================
// EDIT STUDENT
// ===============================

async function editStudent(
    id,
    name,
    email,
    skill,
    availability
) {

    const newName =
        prompt("Enter student name:", name);

    if (newName === null) {
        return;
    }


    const newEmail =
        prompt("Enter email:", email);

    if (newEmail === null) {
        return;
    }


    const newSkill =
        prompt("Enter skill:", skill);

    if (newSkill === null) {
        return;
    }


    const newAvailability =
        prompt(
            "Enter availability:",
            availability
        );

    if (newAvailability === null) {
        return;
    }


    try {

        const response = await fetch(
            `${API_URL}/students`,
            {
                method: "PUT",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    id: id.toString(),
                    name: newName,
                    email: newEmail,
                    skill: newSkill,
                    availability: newAvailability
                })
            }
        );


        if (!response.ok) {
            throw new Error("Update failed");
        }


        alert("Student updated successfully!");

        loadStudents();

    } catch (error) {

        console.error(error);

        alert("Error updating student.");
    }
}


// ===============================
// DELETE STUDENT
// ===============================

async function deleteStudent(id) {

    const confirmDelete =
        confirm("Are you sure you want to delete this student?");

    if (!confirmDelete) {
        return;
    }


    try {

        const response = await fetch(
            `${API_URL}/students`,
            {
                method: "DELETE",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    id: id.toString()
                })
            }
        );


        if (!response.ok) {
            throw new Error("Delete failed");
        }


        alert("Student deleted successfully!");

        loadStudents();

    } catch (error) {

        console.error(error);

        alert("Error deleting student.");
    }
}


// ===============================
// PROJECTS
// ===============================

async function loadProjects() {

    try {

        const response =
            await fetch(`${API_URL}/projects`);


        if (!response.ok) {
            throw new Error("Failed to load projects");
        }


        const projects =
            await response.json();


        displayProjects(projects);


        document.getElementById("projectCount").textContent =
            projects.length;

    } catch (error) {

        console.error("Project loading error:", error);
    }
}


function displayProjects(projects) {

    const container =
        document.getElementById("projectList");


    container.innerHTML = "";


    if (projects.length === 0) {

        container.innerHTML =
            "<p>No projects found.</p>";

        return;
    }


    projects.forEach(project => {

        const card =
            document.createElement("div");


        card.className = "card";


        card.innerHTML = `
            <h3>
                ${escapeHtml(project.title)}
            </h3>

            <p>
                <strong>Description:</strong>
                ${escapeHtml(project.description || "-")}
            </p>

            <p>
                <strong>Required Skill:</strong>
                ${escapeHtml(project.requiredSkill || "-")}
            </p>

            <p>
                <strong>Created By Student ID:</strong>
                ${escapeHtml(String(project.createdBy || "-"))}
            </p>
        `;


        container.appendChild(card);
    });
}


// ===============================
// ADD PROJECT
// ===============================

async function addProject() {

    const title =
        document.getElementById("projectTitle")
            .value.trim();


    const description =
        document.getElementById("projectDescription")
            .value.trim();


    const requiredSkill =
        document.getElementById("projectSkill")
            .value.trim();


    const createdBy =
        document.getElementById("projectCreatedBy")
            .value.trim();


    if (!title) {

        alert("Please enter project title.");

        return;
    }


    if (!createdBy) {

        alert("Please enter Student ID.");

        return;
    }


    try {

        const response =
            await fetch(
                `${API_URL}/projects`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({

                        title: title,

                        description: description,

                        requiredSkill: requiredSkill,

                        // IMPORTANT:
                        // Send createdBy as STRING
                        createdBy: createdBy.toString()
                    })
                }
            );


        if (!response.ok) {

            throw new Error(
                "Failed to create project"
            );
        }


        alert(
            "Project created successfully!"
        );


        document.getElementById(
            "projectTitle"
        ).value = "";


        document.getElementById(
            "projectDescription"
        ).value = "";


        document.getElementById(
            "projectSkill"
        ).value = "";


        document.getElementById(
            "projectCreatedBy"
        ).value = "";


        loadProjects();

    } catch (error) {

        console.error(error);

        alert(
            "Error creating project."
        );
    }
}


// ===============================
// SEARCH
// ===============================

function handleSearch() {

    const type =
        document.getElementById("searchType").value;


    const keyword =
        document.getElementById("searchInput")
            .value.trim()
            .toLowerCase();


    if (type === "students") {

        searchStudents(keyword);

    } else {

        searchProjects(keyword);
    }
}


// ===============================
// SEARCH STUDENTS
// ===============================

async function searchStudents(keyword) {

    try {

        const response =
            await fetch(`${API_URL}/students`);


        const students =
            await response.json();


        if (!keyword) {

            displayStudents(students);

            return;
        }


        const filtered =
            students.filter(student =>

                (student.name || "")
                    .toLowerCase()
                    .includes(keyword)

                ||

                (student.email || "")
                    .toLowerCase()
                    .includes(keyword)

                ||

                (student.skill || "")
                    .toLowerCase()
                    .includes(keyword)

                ||

                (student.availability || "")
                    .toLowerCase()
                    .includes(keyword)
            );


        displayStudents(filtered);

    } catch (error) {

        console.error(error);
    }
}


// ===============================
// SEARCH PROJECTS
// ===============================

async function searchProjects(keyword) {

    try {

        const response =
            await fetch(`${API_URL}/projects`);


        const projects =
            await response.json();


        if (!keyword) {

            displayProjects(projects);

            return;
        }


        const filtered =
            projects.filter(project =>

                (project.title || "")
                    .toLowerCase()
                    .includes(keyword)

                ||

                (project.description || "")
                    .toLowerCase()
                    .includes(keyword)

                ||

                (project.requiredSkill || "")
                    .toLowerCase()
                    .includes(keyword)
            );


        displayProjects(filtered);

    } catch (error) {

        console.error(error);
    }
}

// ===============================
// SKILL AND AVAILABILITY FILTERS
// ===============================

async function filterMatches() {
    const skill = document.getElementById("filterSkill").value.trim().toLowerCase();
    const availability = document.getElementById("filterAvailability").value.trim().toLowerCase();
    const type = document.getElementById("searchType").value;
    const keyword = document.getElementById("searchInput").value.trim().toLowerCase();

    try {
        const [studentsResponse, projectsResponse] = await Promise.all([
            fetch(`${API_URL}/students`),
            fetch(`${API_URL}/projects`)
        ]);

        if (!studentsResponse.ok || !projectsResponse.ok) {
            throw new Error("Failed to load students or projects for filtering.");
        }

        const [students, projects] = await Promise.all([
            studentsResponse.json(),
            projectsResponse.json()
        ]);

        const filteredStudents = students.filter(student =>
            matchesFilter(student.skill, skill)
            && matchesFilter(student.availability, availability)
            && (type !== "students" || matchesSearchText(student, keyword))
        );

        const filteredProjects = projects.filter(project =>
            matchesFilter(project.requiredSkill, skill)
            && (type !== "projects" || matchesSearchText(project, keyword))
        );

        displayStudents(filteredStudents);
        displayProjects(filteredProjects);
    } catch (error) {
        console.error("Filter error:", error);
    }
}

function matchesFilter(value, filter) {
    return !filter || String(value || "").toLowerCase().includes(filter);
}

function matchesSearchText(item, keyword) {
    if (!keyword) {
        return true;
    }

    return Object.values(item).some(value =>
        String(value || "").toLowerCase().includes(keyword)
    );
}

function showAllResults() {
    document.getElementById("filterSkill").value = "";
    document.getElementById("filterAvailability").value = "";
    document.getElementById("searchInput").value = "";
    loadStudents();
    loadProjects();
}


// ===============================
// HTML SECURITY
// ===============================

function escapeHtml(value) {

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


function escapeJs(value) {

    return String(value)
        .replace(/\\/g, "\\\\")
        .replace(/'/g, "\\'");
}
