# 📝 TaskList Application

TaskList is a simple task and project management application built in Java using Spring Boot. It supports both a **console-based interface** and a **REST API**, allowing you to manage projects and tasks either from the command line or programmatically via HTTP requests.

## ✨ Features

- 📁 Create and view projects
- 📝 Add tasks to projects
- ✅ Mark tasks as done/undone
- 📅 Update task deadlines
- 📊 View tasks grouped by deadlines
- 💻 Console-based interface for interactive use
- 🌐 REST API for programmatic access

## 🛠 Technologies Used

- Java 21
- Spring Boot
- Lombok
- Maven
- In-memory storage (no external database required)

## 🚀 Getting Started

### ⚙️ Prerequisites

- Java 21 or later
- Maven

## ▶️ How to Run

Currently, the application supports 2 modes - console and REST API.

### 🖥 Run the console mode

To run this application in console, open the project in your IDE (e.g., IntelliJ IDEA or Eclipse) and run the `TaskListApplication` class.

Alternatively, you can execute the following command via Maven: 
```bash
mvn clean compile spring-boot:run
```

The application will start, and you can type your commands in the console. 

### 🌐 Run the REST API mode

To be able to use the REST API mode, this command via Maven:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=web
```

The application will start at:  
👉 http://localhost:8080

## 📮 Postman Collection

You can easily test all API endpoints using the provided Postman collection:

**File:**  
`/postman/Tasks.postman_collection.json`

**How to use:**
1. Open Postman.
2. Click **Import** → **files**.
3. Select the collection file from the `postman` folder.
4. Run the requests directly against your local server (`http://localhost:8080`).

## 💻 Console Commands

You can use the following commands in **console mode**:

- **`help`** – Show all available commands and their descriptions
- **`add_project <project_name>`** – Create a new project
- **`show_all`** – Display all projects and tasks
- **`add_task <project_name> <task_description>`** – Add a new task to a project
- **`check <task_id>`** – Mark a task as done
- **`uncheck <task_id>`** – Mark a task as not done
- **`update_deadline <task_id> <deadline>`** – Update the deadline of a task (format: `dd-MM-yyyy`)
- **`view_today`** – Show all tasks with today’s deadline
- **`view_by_deadline`** – Show all tasks grouped by deadline

---

## 🌐 REST API Endpoints

You can use these endpoints when the application is running in **REST API mode**:

| Method | Endpoint | Description | Body / Params |
|--------|---------|-------------|---------------|
| POST | `/projects` | Create a new project | `{"name": "Home"}` |
| GET  | `/projects` | Get all projects and their tasks | — |
| POST | `/projects/{projectName}/tasks` | Add a new task to a project | `{"description": "Buy groceries"}` |
| PUT  | `/projects/{projectName}/tasks/{taskId}?deadline=<date>` | Update task deadline | `deadline` param format: `dd-MM-yyyy` |
| GET  | `/projects/view_by_deadline` | Get all tasks grouped by deadline | — |

---

### ▶️ Run all tests via Maven
```bash
mvn clean test
```