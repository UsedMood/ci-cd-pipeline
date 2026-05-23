# Getting Started with MD-Searcher

Welcome to **MD-Searcher** — a fast, lightweight tool for indexing and searching your Markdown files.

## Installation

To build and run the application, you need Java 11 and Maven installed on your system.

```bash
mvn package
java -jar target/md-searcher-1.0-SNAPSHOT.jar
```

By default the server starts on port **8080** and scans the `./data` directory.

## Configuration

You can override the defaults with command-line flags:

```bash
java -jar target/md-searcher-1.0-SNAPSHOT.jar --port 9090 --data /path/to/docs
```

## How It Works

1. On startup, all `.md` files are read recursively from the data directory.
2. Each file is parsed: the first `# H1` heading becomes the document title.
3. Documents are stored in a thread-safe in-memory index.
4. The REST API exposes search and document retrieval endpoints.

## Tips

- Use specific keywords for better relevance scores.
- Title matches are weighted higher than body matches.
