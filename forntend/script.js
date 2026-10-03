const API_URL = "/api";

document.addEventListener("DOMContentLoaded", () => {
    const page = document.body.dataset.page;

    if (page === "dashboard") {
        loadDashboard();
    } else if (page === "find-team") {
        initializeTeamSearch();
    } else if (page === "students") {
        initializeStudentDirectory();
    } else if (page === "projects") {
        initializeProjectDirectory();
    } else if (page === "post-project") {
        initializeProjectForm();
    }
});

async function fetchJson(path, options) {
    const response = await fetch(`${API_URL}${path}`, options);
    if (!response.ok) {
        throw new Error(`Request failed (${response.status}).`);
    }
    return response.json();
}

async function loadDashboard() {
    const status = document.getElementById("dashboard-status");
    try {
        const [students, projects] = await Promise.all([
            fetchJson("/students"),
            fetchJson("/projects")
        ]);
        const skills = new Set();
        students.forEach(student => splitSkills(student.skill).forEach(skill => skills.add(skill.toLowerCase())));
        projects.forEach(project => splitSkills(project.requiredSkill).forEach(skill => skills.add(skill.toLowerCase())));

        document.getElementById("stat-students").textContent = students.length;
        document.getElementById("stat-available").textContent =
            students.filter(student => String(student.availability || "").trim()).length;
        document.getElementById("stat-projects").textContent = projects.length;
        document.getElementById("stat-skills").textContent = skills.size;
    } catch (error) {
        status.textContent = "We could not load the latest community statistics. Please refresh to try again.";
        status.classList.add("status-error");
        console.error("Dashboard loading error:", error);
    }
}

async function initializeTeamSearch() {
    const form = document.getElementById("team-filter-form");
    const results = document.getElementById("team-results");
    form.addEventListener("submit", event => {
        event.preventDefault();
        renderTeamResults();
    });
    form.addEventListener("reset", () => requestAnimationFrame(renderTeamResults));
    document.getElementById("team-query").addEventListener("input", debounce(renderTeamResults, 180));
    const params = new URLSearchParams(window.location.search);
    document.getElementById("team-query").value = params.get("q") || "";
    document.getElementById("team-skill").value = params.get("skill") || "";
    document.getElementById("team-language").value = params.get("language") || "";
    document.getElementById("team-availability").value = params.get("availability") || "";
    try {
        results.innerHTML = loadingMarkup("Finding students…");
        const students = await fetchJson("/students");
        window.finderStudents = students;
        updateSuggestions("skill-suggestions", students.map(student => student.skill));
        renderTeamResults();
    } catch (error) {
        results.innerHTML = errorMarkup("Students could not be loaded", "Check your connection and try again.");
        console.error("Teammate search error:", error);
    }
}

function renderTeamResults() {
    if (!Array.isArray(window.finderStudents)) {
        return;
    }
    const query = inputValue("team-query").toLowerCase();
    const skill = inputValue("team-skill").toLowerCase();
    const language = inputValue("team-language").toLowerCase();
    const availability = inputValue("team-availability").toLowerCase();
    const matches = window.finderStudents.filter(student =>
        contains(student.name, query) || contains(student.email, query) || contains(student.skill, query)
    ).filter(student =>
        contains(student.skill, skill)
        && contains(student.skill, language)
        && contains(student.availability, availability)
    );
    renderStudentCards("team-results", matches, "No teammates match these filters", "Try a different name, skill, language, or availability.");
    document.getElementById("team-result-count").textContent =
        `${matches.length} ${matches.length === 1 ? "student" : "students"}`;
}

async function initializeStudentDirectory() {
    const form = document.getElementById("student-filter-form");
    form.addEventListener("submit", event => {
        event.preventDefault();
        renderDirectoryResults();
    });
    form.addEventListener("reset", () => requestAnimationFrame(renderDirectoryResults));
    document.getElementById("student-query").addEventListener("input", debounce(renderDirectoryResults, 180));
    document.getElementById("add-student-form").addEventListener("submit", addStudent);
    document.getElementById("directory-results").addEventListener("click", handleStudentAction);
    await refreshStudents();
}

async function refreshStudents() {
    const results = document.getElementById("directory-results");
    results.innerHTML = loadingMarkup("Loading students…");
    try {
        window.finderStudents = await fetchJson("/students");
        updateSuggestions("student-skill-suggestions", window.finderStudents.map(student => student.skill));
        renderDirectoryResults();
    } catch (error) {
        results.innerHTML = errorMarkup("Students could not be loaded", "Check your connection and try again.");
        console.error("Student directory error:", error);
    }
}

function renderDirectoryResults() {
    if (!Array.isArray(window.finderStudents)) {
        return;
    }
    const query = inputValue("student-query").toLowerCase();
    const skill = inputValue("student-skill-filter").toLowerCase();
    const availability = inputValue("student-availability-filter").toLowerCase();
    const matches = window.finderStudents.filter(student =>
        (!query || [student.name, student.email, student.skill, student.availability]
            .some(value => contains(value, query)))
        && contains(student.skill, skill)
        && contains(student.availability, availability)
    );
    renderStudentCards("directory-results", matches, "No students found", "Try changing your search or filters.");
    document.getElementById("student-result-count").textContent =
        `${matches.length} ${matches.length === 1 ? "student" : "students"}`;
}

function renderStudentCards(containerId, students, emptyTitle, emptyDescription) {
    const container = document.getElementById(containerId);
    if (!students.length) {
        container.innerHTML = emptyMarkup(emptyTitle, emptyDescription);
        return;
    }
    container.innerHTML = students.map(student => {
        const email = String(student.email || "").trim();
        const contactAction = email
            ? `<a class="button button-small button-secondary" href="mailto:${escapeAttribute(email)}">Contact</a>`
            : `<span class="button button-small button-disabled" aria-disabled="true">No email listed</span>`;
        const actions = containerId === "directory-results"
            ? `<div class="card-actions"><button class="button button-small button-secondary" type="button" data-action="edit" data-id="${Number(student.id)}">Edit</button><button class="button button-small button-danger" type="button" data-action="delete" data-id="${Number(student.id)}">Delete</button>${contactAction}</div>`
            : `<div class="card-actions">${contactAction}</div>`;
        return `
            <article class="person-card">
                <div class="person-heading">
                    <span class="avatar">${escapeHtml(initials(student.name))}</span>
                    <div><h3>${escapeHtml(student.name || "Student")}</h3><p class="muted">${escapeHtml(email || "Email not provided")}</p></div>
                </div>
                <div class="card-detail"><span>Skills &amp; languages</span><strong>${escapeHtml(student.skill || "Not listed")}</strong></div>
                <div class="card-detail"><span>Availability</span><strong>${escapeHtml(student.availability || "Not listed")}</strong></div>
                ${actions}
            </article>`;
    }).join("");
}

async function addStudent(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const data = new FormData(form);
    const submit = form.querySelector('button[type="submit"]');
    submit.disabled = true;
    try {
        await fetchJson("/students", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                name: String(data.get("name")).trim(),
                email: String(data.get("email")).trim(),
                skill: String(data.get("skill")).trim(),
                availability: String(data.get("availability")).trim()
            })
        });
        form.reset();
        await refreshStudents();
    } catch (error) {
        alert(`Student could not be added: ${error.message}`);
        console.error("Add student error:", error);
    } finally {
        submit.disabled = false;
    }
}

async function handleStudentAction(event) {
    const button = event.target.closest("button[data-action]");
    if (!button) {
        return;
    }
    const student = window.finderStudents.find(item => Number(item.id) === Number(button.dataset.id));
    if (!student) {
        return;
    }
    if (button.dataset.action === "edit") {
        await editStudent(student);
    } else if (button.dataset.action === "delete") {
        await deleteStudent(student);
    }
}

async function editStudent(student) {
    const name = prompt("Enter student name:", student.name || "");
    if (name === null) return;
    const email = prompt("Enter email:", student.email || "");
    if (email === null) return;
    const skill = prompt("Enter skills or programming languages:", student.skill || "");
    if (skill === null) return;
    const availability = prompt("Enter availability:", student.availability || "");
    if (availability === null) return;
    try {
        await fetchJson("/students", {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                id: String(student.id),
                name: name.trim(),
                email: email.trim(),
                skill: skill.trim(),
                availability: availability.trim()
            })
        });
        await refreshStudents();
    } catch (error) {
        alert(`Student could not be updated: ${error.message}`);
        console.error("Update student error:", error);
    }
}

async function deleteStudent(student) {
    if (!confirm(`Delete ${student.name || "this student"}?`)) {
        return;
    }
    try {
        await fetchJson("/students", {
            method: "DELETE",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ id: String(student.id) })
        });
        await refreshStudents();
    } catch (error) {
        alert(`Student could not be deleted: ${error.message}`);
        console.error("Delete student error:", error);
    }
}

async function initializeProjectDirectory() {
    const form = document.getElementById("project-filter-form");
    form.addEventListener("submit", event => {
        event.preventDefault();
        renderProjectResults();
    });
    form.addEventListener("reset", () => requestAnimationFrame(renderProjectResults));
    document.getElementById("project-query").addEventListener("input", debounce(renderProjectResults, 180));
    document.getElementById("project-skill-filter").addEventListener("input", debounce(renderProjectResults, 180));
    document.getElementById("project-results").addEventListener("click", handleProjectAction);
    document.getElementById("project-dialog").addEventListener("click", event => {
        if (event.target === event.currentTarget) event.currentTarget.close();
    });
    const querySkill = new URLSearchParams(window.location.search).get("skill");
    if (querySkill) document.getElementById("project-skill-filter").value = querySkill;
    const results = document.getElementById("project-results");
    results.innerHTML = loadingMarkup("Loading projects…");
    try {
        window.finderProjects = await fetchJson("/projects");
        updateSuggestions("project-skill-suggestions", window.finderProjects.map(project => project.requiredSkill));
        renderProjectResults();
    } catch (error) {
        results.innerHTML = errorMarkup("Projects could not be loaded", "Check your connection and try again.");
        console.error("Project directory error:", error);
    }
}

function renderProjectResults() {
    if (!Array.isArray(window.finderProjects)) {
        return;
    }
    const query = inputValue("project-query").toLowerCase();
    const skill = inputValue("project-skill-filter").toLowerCase();
    const matches = window.finderProjects.filter(project =>
        (!query || [project.title, project.description, project.requiredSkill]
            .some(value => contains(value, query)))
        && contains(project.requiredSkill, skill)
    );
    const container = document.getElementById("project-results");
    if (!matches.length) {
        container.innerHTML = emptyMarkup("No projects found", "Try a different search or skill, or be the first to post an idea.", "/post-project.html", "Post a project");
    } else {
        container.innerHTML = matches.map(project => `
            <article class="project-card">
                <div class="project-card-top"><span class="project-label">PROJECT</span><span class="project-id">#${Number(project.id)}</span></div>
                <h3>${escapeHtml(project.title || "Untitled project")}</h3>
                <p class="project-description">${escapeHtml(project.description || "No description provided yet.")}</p>
                <div class="skill-tags">${splitSkills(project.requiredSkill).map(skill => `<span class="skill-tag">${escapeHtml(skill)}</span>`).join("") || '<span class="skill-tag tag-muted">Skills open</span>'}</div>
                <div class="project-card-bottom"><span class="muted">Created by student #${Number(project.createdBy) || "—"}</span><button class="button button-small button-secondary" type="button" data-project-id="${Number(project.id)}">View Project</button></div>
            </article>`).join("");
    }
    document.getElementById("project-result-count").textContent =
        `${matches.length} ${matches.length === 1 ? "project" : "projects"}`;
}

function handleProjectAction(event) {
    const button = event.target.closest("button[data-project-id]");
    if (!button) return;
    const project = window.finderProjects.find(item => Number(item.id) === Number(button.dataset.projectId));
    if (!project) return;
    document.getElementById("project-dialog-content").innerHTML = `
        <p class="eyebrow">Project details</p>
        <h2>${escapeHtml(project.title || "Untitled project")}</h2>
        <p>${escapeHtml(project.description || "No description provided yet.")}</p>
        <div class="card-detail"><span>Required skills</span><strong>${escapeHtml(project.requiredSkill || "Open to all skills")}</strong></div>
        <div class="card-detail"><span>Created by</span><strong>Student #${Number(project.createdBy) || "—"}</strong></div>`;
    document.getElementById("project-dialog-team-link").href =
        `/find-team.html?skill=${encodeURIComponent(project.requiredSkill || "")}`;
    document.getElementById("project-dialog").showModal();
}

function initializeProjectForm() {
    const form = document.getElementById("post-project-form");
    form.addEventListener("submit", async event => {
        event.preventDefault();
        const data = new FormData(form);
        const title = String(data.get("title")).trim();
        const description = String(data.get("description")).trim();
        const requiredSkill = String(data.get("requiredSkill")).trim();
        const programmingLanguage = String(data.get("programmingLanguage")).trim();
        const availability = String(data.get("availability")).trim();
        const createdBy = String(data.get("createdBy")).trim();
        const skillParts = [requiredSkill, programmingLanguage].filter(Boolean);
        const descriptionParts = [description];
        if (availability) descriptionParts.push(`Availability needed: ${availability}`);
        const submit = form.querySelector('button[type="submit"]');
        const status = document.getElementById("post-project-status");
        submit.disabled = true;
        status.textContent = "Publishing your project…";
        status.className = "status-message";
        try {
            await fetchJson("/projects", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    title,
                    description: descriptionParts.filter(Boolean).join(" | "),
                    requiredSkill: skillParts.join(", "),
                    createdBy
                })
            });
            form.reset();
            status.textContent = "Project published. View it in the project marketplace.";
            status.classList.add("status-success");
        } catch (error) {
            status.textContent = `Project could not be published: ${error.message}`;
            status.classList.add("status-error");
            console.error("Post project error:", error);
        } finally {
            submit.disabled = false;
        }
    });
}

function updateSuggestions(listId, values) {
    const list = document.getElementById(listId);
    if (!list) return;
    const suggestions = new Set();
    values.forEach(value => splitSkills(value).forEach(skill => suggestions.add(skill)));
    list.innerHTML = [...suggestions]
        .sort((left, right) => left.localeCompare(right))
        .map(value => `<option value="${escapeAttribute(value)}">`)
        .join("");
}

function splitSkills(value) {
    return String(value || "").split(/[,;\n]/).map(skill => skill.trim()).filter(Boolean);
}

function contains(value, query) {
    return !query || String(value || "").toLocaleLowerCase().includes(query);
}

function inputValue(id) {
    return document.getElementById(id).value.trim();
}

function initials(name) {
    return String(name || "?").trim().split(/\s+/).slice(0, 2).map(part => part[0]).join("").toUpperCase() || "?";
}

function debounce(callback, delay) {
    let timeout;
    return (...args) => {
        clearTimeout(timeout);
        timeout = setTimeout(() => callback(...args), delay);
    };
}

function loadingMarkup(message) {
    return `<div class="loading-state"><span class="loading-dot" aria-hidden="true"></span>${escapeHtml(message)}</div>`;
}

function emptyMarkup(title, description, href, actionLabel) {
    const action = href
        ? `<a class="button button-primary button-small" href="${escapeAttribute(href)}">${escapeHtml(actionLabel)}</a>`
        : "";
    return `<div class="empty-state"><span class="empty-icon" aria-hidden="true">⌕</span><h3>${escapeHtml(title)}</h3><p>${escapeHtml(description)}</p>${action}</div>`;
}

function errorMarkup(title, description) {
    return `<div class="empty-state error-state"><span class="empty-icon" aria-hidden="true">!</span><h3>${escapeHtml(title)}</h3><p>${escapeHtml(description)}</p></div>`;
}

function escapeHtml(value) {
    return String(value ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function escapeAttribute(value) {
    return escapeHtml(value);
}
