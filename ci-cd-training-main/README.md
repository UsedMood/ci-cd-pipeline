# MD-Searcher

A standalone Java web application for indexing and searching Markdown files from a local directory.

## Features

- **File Ingestion:** Recursively scans a local directory (default: `./data`) for `.md` files on startup.
- **In-Memory Indexing:** Parses Markdown files to extract titles (from the first H1 or filename) and renders content to HTML using [Flexmark-java](https://github.com/vsch/flexmark-java).
- **Search Logic:** Rank-based search (Title match = 2, Body match = 1) with contextual snippets.
- **File Upload:** Upload new `.md` files through the web interface with automatic re-indexing.
- **REST API:**
  - `GET /api/search?q={query}`: Returns a JSON list of search results.
  - `GET /api/document/{id}`: Returns the HTML-rendered content of a specific document.
  - `POST /api/upload`: Handles multipart/form-data for uploading new Markdown files.
- **Simple UI:** A clean, vanilla JavaScript frontend for searching, viewing, and uploading documents.

## Tech Stack

- **Language:** Java 11
- **Build Tool:** Maven
- **Server:** Embedded Jetty 11
- **Markdown Parsing:** Flexmark-java
- **JSON:** Jackson Databind
- **Testing:** JUnit 5 and Mockito

## Getting Started

### Prerequisites

- Java 11 JDK
- Maven 3.x

### Running the Application

1.  **Clone the repository.**
2.  **Build the project:**
    ```bash
    mvn clean package
    ```
3.  **Run the application:**
    ```bash
    java -cp target/classes;target/dependencies/* com.mdsearcher.Main
    ```
    By default, it starts on port 8080 and searches for files in the `./data` directory.

    You can customize these using arguments:
    ```bash
    java -cp target/classes;target/dependencies/* com.mdsearcher.Main --port 9090 --data /path/to/my/md/files
    ```

4.  **Access the UI:**
    Open [http://localhost:8080](http://localhost:8080) in your browser.

## Testing

Run all unit tests using Maven:
```bash
mvn test
```

The test suite covers:
- `SearchService`: Relevance scoring, ranking, and snippet generation.
- `MarkdownParserService`: Title extraction, HTML rendering, and content parsing.
- `UploadServlet`: File upload handling, validation, and re-indexing.

## Project Structure

- `src/main/java`: Core application logic (Model, Service, Handler).
- `src/main/resources/static`: Frontend assets (index.html).
- `src/test/java`: Unit and integration tests.
- `data/`: Sample Markdown files for initial testing.

## Publish the app with docker

Create the image based on the Dockerfile:

```bash
docker build -t ibello/ci-cd-training .
```

Start a container from the image:

```bash
docker run -p 8080:8080 -v ./data:/data ibello/ci-cd-training
```

Tag the image with version number:

```bash
docker tag ibello/ci-cd-training ibello/ci-cd-training:1.0
```

Push the image to docker hub:

```bash
docker push ibello/ci-cd-training:1.0
```

## GitLab CI/CD configuration

Install GitLab Runner on local machine (windows):

https://docs.gitlab.com/runner/install/windows/

