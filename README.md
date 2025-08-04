# spring-boot-ai-version-upgrade

## Overview

This project automates upgrading Spring Boot versions in Java repositories using AI-powered tools and OpenRewrite. It scans repositories, applies upgrade recipes, and creates pull requests for version updates.

## Features
- Scans multiple repositories for Spring Boot projects
- Uses OpenRewrite to apply upgrade recipes
- Handles Git operations (clone, commit, push)
- Creates GitHub pull requests for upgraded projects
- Configurable via YAML catalog

## Prerequisites
- Java 17+
- Maven
- Git
- GitHub access token (for PR creation)

## GitHub Token Setup
To create pull requests, you need a GitHub personal access token. Generate one from your GitHub account with `repo` permissions.

Set the token as an environment variable before running the application:

**Windows:**
```
set GITHUB_TOKEN=your_token_here
```
**Linux/macOS:**
```
export GITHUB_TOKEN=your_token_here
```

The application retrieves the token from the `GITHUB_TOKEN` environment variable.

## Getting Started
1. **Clone the repository:**
   ```
   git clone <repo-url>
   ```
2. **Configure repositories and upgrade settings:**
    - Edit `src/main/resources/catalog.yml` to specify repositories and upgrade parameters.
3. **Build the project:**
   ```
   mvn clean package
   ```
4. **Run the application:**
   ```
   java -jar target/spring-boot-ai-version-upgrade-1.0-SNAPSHOT.jar
   ```

## Configuration
- `catalog.yml`: List of repositories and upgrade settings.
- `ReWritePlugin.xml`: OpenRewrite plugin configuration.

## Project Structure
- `src/main/java/org/innovation/`: Main source code
- `src/main/resources/`: Configuration files
- `repos/`: Cloned repositories
- `target/`: Build output