# CONTRIBUTING.md

---

## Prerequisites

Before contributing, ensure the following tools and dependencies are installed on your local machine:

- **Java JDK 23**
- **Maven (latest version)**
- **Spring Boot**
- **Guice for Dependency Injection**
- **JavaFX/OpenJFX**
- **Git**

You will also need access to the project repository on GitLab.

---

## Setting Up the Project

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd <repository-directory>
   ```

2. Install dependencies:
   ```bash
   mvn clean install
   ```

3. Configure your IDE:
    - Use IntelliJ IDEA (recommended).
    - Set the Java version to JDK 23.
    - Configure Checkstyle with the project's custom rules.

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

---

## Branching Strategy

We follow a **feature branching** strategy. Follow these steps for branching:

- Create a branch for your feature:
  ```bash
  git checkout -b feature/<feature-name>
  ```
- For bug fixes:
  ```bash
  git checkout -b bugfix/<issue-id>
  ```

---

## Coding Standards

1. **Formatting:** Use the project's Checkstyle configuration.
2. **Naming Conventions:**
    - Classes: `PascalCase`.
    - Methods and variables: `camelCase`.
    - Constants: `UPPER_SNAKE_CASE`.

3. **Code Documentation:**  
   Use Javadoc comments for all classes and methods:
   ```java
   /**
    * This method performs XYZ operation.
    *
    * @param param Description of the parameter.
    * @return Description of the return value.
    */
   ```

---

## Commit Messages

We use the **Conventional Commits** format:

```
<type>(<scope>): <description>

[optional body]

[optional footer(s)]
```

- **Type examples:**
    - `feat`: A new feature.
    - `fix`: A bug fix.
    - `docs`: Documentation changes.
    - `style`: Code style changes (no functional changes).
    - `test`: Adding or modifying tests.
- **Example commit message:**
  ```
  feat(NoteController): add support for Markdown parsing
  ```

---

## Testing

All code changes must include tests. We use **JUnit 5** for testing:

1. Unit tests for all methods.
2. Integration tests for new features.
3. Ensure all tests pass before pushing changes:
   ```bash
   mvn test
   ```

---

## Submitting a Merge Request (MR)

1. Push your branch to the repository:
   ```bash
   git push origin <branch-name>
   ```

2. Create a Merge Request in GitLab:
    - Provide a descriptive title and summary.
    - Link related issues (if applicable).
    - Request at least one reviewer.

3. Ensure your MR meets the following criteria:
    - All tests pass.
    - Code adheres to Checkstyle rules.
    - Changes are documented.

---

## Code Review Process

1. Reviewers will check:
    - Code quality and adherence to standards.
    - Completeness of tests.
    - Impact on existing features.
2. Respond to feedback by updating your MR.

