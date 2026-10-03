# Project Team Finder

## Run the web application

Open PowerShell in the project root and compile the Java sources:

```powershell
$sources = Get-ChildItem .\src -Recurse -Filter *.java | ForEach-Object FullName
javac -cp ".\lib\sqlite-jdbc-3.50.3.0.jar" -d .\out $sources
```

Start the backend from the project root so SQLite uses the existing
`projectfinder.db` file:

```powershell
java -cp ".\out;.\lib\*" server.ApiServer
```

Open <http://localhost:8080>. Keep the server terminal open while using the
site. If port 8080 is already in use, stop the earlier server with Ctrl+C
before starting it again.

The backend serves the frontend files from either `frontend` or `forntend`.
The frontend directory must contain `index.html`, `script.js`, and a stylesheet
named `style.css` or `styles.css`.
