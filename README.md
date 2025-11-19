# CSEP Template Project

A multi-module Java project that implements a simple notes application with a Spring Boot REST server and a JavaFX desktop client, sharing common data models. This README consolidates setup and run instructions and documents the project structure.

![Demo](https://github.com/user-attachments/assets/d34ec486-2935-487a-9c2c-191f7a6c0948)

---

## Overview
- Server: Spring Boot application exposing REST APIs for managing notes, collections, and tags.
- Client: JavaFX desktop application that consumes the server APIs.
- Commons: Shared data models (e.g., `Note`, `NoteCollection`, `NoteTag`).
- Build system: Maven (multi-module).
- Database: H2 (development database files are present in the project root: `h2-database.*`).

## Tech Stack
- Language: Java (runtime version TBD; CI static analysis uses JDK 23 per `qodana.yaml`)
- Build/Package: Maven (with Maven Wrapper `mvnw`/`mvnw.cmd`)
- Server Framework: Spring Boot
- Client UI: JavaFX
- Database: H2 (embedded / file based for development)

## Requirements
- JDK: JDK 23
- Maven 3.9+ (optional if using the Maven Wrapper `./mvnw`)
- OS: Windows, macOS, or Linux

## Quick Start
From the project root:

```bash
# Build all modules
./mvnw clean install

# In a separate terminal: start the server
cd server
../mvnw spring-boot:run

# After the server is running, start the client
cd ../client
../mvnw javafx:run
```

Notes:
- Start the server before launching the client.
- If you prefer a global Maven installation, replace `./mvnw` with `mvn`.

## Common Maven Commands
```bash
# Build all modules and run unit tests
./mvnw clean verify

# Run tests only
./mvnw test

# Package artifacts (jars)
./mvnw clean package

# Run the Spring Boot server from the server module
cd server && ../mvnw spring-boot:run

# Run the JavaFX client from the client module
cd client && ../mvnw javafx:run
```

## Configuration & Environment Variables
Typical Spring Boot environment variables and properties may be used. Some commonly relevant ones are listed below; set them as needed before running the server.

- `SERVER_PORT` — override HTTP port (default is usually 8080).
- `SPRING_PROFILES_ACTIVE` — choose Spring profile (e.g., `dev`, `prod`).
- Database configuration (if not using the embedded/file H2):
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`

H2 development database
- The repository contains `h2-database.mv.db` and `h2-database.trace.db` in the project root. These indicate a file-based H2 database used in development.
- To reset the dev database, stop the server and remove these files, then restart the server.

## Tests
- Run all tests at the root: `./mvnw test`
- Run tests within a specific module (e.g., server):
  ```bash
  cd server
  ../mvnw test
  ```

## Project Structure
This is a Maven multi-module build (see `pom.xml` in the root):

```
root (packaging=pom)
├── commons/        # Shared data models (e.g., Note, NoteCollection, NoteTag)
├── client/         # JavaFX desktop application
└── server/         # Spring Boot REST API server
```

Module internals (as implemented):
- Client Module
  - `client.scenes` — JavaFX controllers
    - `config` — saves settings between runs
    - Multiple controllers for: AddCollection, AddNote, etc.
  - `client.resources` — FXML views and visual assets
- Commons Module
  - Shared data models: `Note`, `NoteCollection`, `NoteTag`
- Server Module
  - `server.api` — REST controllers: `NoteController`, `NoteCollectionController`, `TagController`
  - `server.database` — Repository layer for data persistence
  - `server.service` — Business logic layer (includes Markdown service)

## Implemented Features
### Basic Requirements
- All basic requirements have been implemented.

### Extensions
- Multi-Collection
- Interconnected Content
- Live Language Switch
  - Supported languages: English, Dutch, German, Spanish, and Polish (uses Unicode for special characters)

### HCI Features
#### Accessibility
- Color Contrast
- Keyboard Shortcuts
- Multi-modal Visualization

#### Navigation
- Logical Navigation
- Keyboard Navigation

> Note: `Undo Actions` is NOT implemented.

#### User Feedback
- Error Messages
- Informative Feedback
- Confirmation for Key Actions

## Keyboard Shortcuts
- `ESC` — Focus on the search bar
- `Ctrl + N` — Add a new note
- `Ctrl + Up/Down` — Navigate notes up and down
- `Ctrl + Left/Right` — Navigate collections left and right

## Entry Points
- Server entry point: Spring Boot application main class within the `server` module. Run via:
  ```bash
  cd server && ../mvnw spring-boot:run
  ```
- Client entry point: JavaFX application main class within the `client` module. Run via:
  ```bash
  cd client && ../mvnw javafx:run
  ```

## Linting / Code Quality
- A `checkstyle.xml` file exists in the repository root.
- A `qodana.yaml` is present for JetBrains Qodana static analysis. See https://www.jetbrains.com/qodana/ for running instructions.

## Troubleshooting
- Port already in use when starting the server: set `SERVER_PORT` to a free port.
- Client cannot connect to server: ensure the server is running and reachable at the expected URL/port.
- JavaFX runtime errors: verify the installed JDK version and that Maven downloads the JavaFX dependencies via the plugin.

