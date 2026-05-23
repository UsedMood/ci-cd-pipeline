# Java Best Practices

A collection of best practices for writing clean, maintainable Java code.

## Naming Conventions

- Classes should use `PascalCase` (e.g. `SearchService`).
- Methods and variables should use `camelCase` (e.g. `findDocuments`).
- Constants should use `UPPER_SNAKE_CASE` (e.g. `MAX_RESULTS`).

## Immutability

Prefer immutable objects wherever possible. Use `final` fields and avoid setters when the state does not need to change after construction.

```java
public final class Config {
    private final int port;
    private final String dataDir;

    public Config(int port, String dataDir) {
        this.port = port;
        this.dataDir = dataDir;
    }

    public int getPort() { return port; }
    public String getDataDir() { return dataDir; }
}
```

## Exception Handling

- Catch specific exceptions, not `Exception` or `Throwable`.
- Always log the original exception when re-throwing.
- Use checked exceptions for recoverable situations and runtime exceptions for programming errors.

## Thread Safety

When sharing state across threads, prefer `java.util.concurrent` classes such as `ConcurrentHashMap` or `CopyOnWriteArrayList` over manual synchronisation.

## Testing

Write unit tests for every public method. Use JUnit 5 and Mockito to keep tests fast and isolated from external dependencies.
