package com.mdsearcher.api.steps;

import com.mdsearcher.api.client.MDServiceClient;
import com.mdsearcher.api.config.TestConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Steps layer containing business logic and state.
 * It uses MDServiceClient for API calls.
 * Stores the lastResponse and does not return values to the Glue code.
 */
public class MDTestSteps {
    private final MDServiceClient client;
    private String lastUploadedFilename;
    private String lastTimestamp;

    public MDTestSteps() {
        this.client = new MDServiceClient();
    }

    /**
     * Uploads a new markdown file with a unique timestamp using a template.
     */
    public void uploadMd() {
        long timestamp = System.currentTimeMillis();
        lastTimestamp = String.valueOf(timestamp);
        String filename = "test-" + timestamp + ".md";

        try {
            // Read template from resources
            Path templatePath = Paths.get("src/test/resources/templates/test-template.md");
            if (!Files.exists(templatePath)) {
                throw new RuntimeException("Template file not found at: " + templatePath.toAbsolutePath());
            }
            String content = Files.readString(templatePath);
            // Replace placeholder with actual timestamp
            content = content.replace("[TIMESTAMP]", lastTimestamp);

            // Create a temporary file to upload
            Path uploadSourceDir = Paths.get("target/temp-uploads");
            if (!Files.exists(uploadSourceDir)) {
                Files.createDirectories(uploadSourceDir);
            }
            File file = new File(uploadSourceDir.toFile(), filename);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
            }

            client.uploadFile(file);
            lastUploadedFilename = filename;

            if (client.getLastResponse().getStatusCode() != 200) {
                throw new RuntimeException("Upload failed with status code: " + client.getLastResponse().getStatusCode());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to process template or create temporary file for upload", e);
        }
    }

    /**
     * Verifies that the last uploaded file is available in search results.
     */
    public void verifyFileInSearchResults() {
        if (lastUploadedFilename == null) {
            throw new IllegalStateException("No file was uploaded in the previous step");
        }

        // Search for the unique timestamp to find exactly our document
        client.search(lastTimestamp);
        
        Assertions.assertEquals(200, client.getLastResponse().getStatusCode(), "Search API returned an error");

        // Parse search results (expecting List of DTOs)
        List<Map<String, Object>> results = client.getLastResponse().jsonPath().getList("");
        
        // Check if any result contains our unique timestamp in the snippet or title
        boolean found = results.stream()
                .anyMatch(res -> (res.get("snippet") != null && res.get("snippet").toString().contains(lastTimestamp)));

        if (!found) {
            throw new RuntimeException("Document with timestamp " + lastTimestamp + " not found in search results. Results: " + client.getLastResponse().asString());
        }
    }

    /**
     * Verifies that the last uploaded markdown file's content is available in HTML format.
     * It searches for the file by its unique timestamp, then fetches the document's HTML representation
     * and asserts that markdown was converted to HTML (e.g. the H1 became <h1>).
     */
    public void verifyDocumentIsServedAsHtml() {
        if (lastUploadedFilename == null) {
            throw new IllegalStateException("No file was uploaded in the previous step");
        }

        // First locate the document id via the search API using the unique timestamp
        client.search(lastTimestamp);
        Assertions.assertEquals(200, client.getLastResponse().getStatusCode(), "Search API returned an error");

        List<Map<String, Object>> results = client.getLastResponse().jsonPath().getList("");
        String documentId = results.stream()
                .filter(res -> res.get("id") != null)
                .map(res -> res.get("id").toString())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find uploaded document in search results"));

        // Fetch the document's HTML representation
        client.getDocument(documentId);

        Assertions.assertEquals(200, client.getLastResponse().getStatusCode(),
                "Document endpoint returned an error");

        String htmlBody = client.getLastResponse().asString();
        Assertions.assertNotNull(htmlBody, "Document HTML body is null");

        // The template's "# Template Document" markdown heading must be rendered as an HTML <h1>
        Assertions.assertTrue(htmlBody.contains("<h1>"),
                "Expected an <h1> tag in HTML output, but got: " + htmlBody);
        Assertions.assertTrue(htmlBody.contains("Template Document"),
                "Expected the title text in HTML output, but got: " + htmlBody);
    }

    /**
     * Basic check if the backend is responsive.
     */
    public void verifyBackendRunning() {
        // We can just try a search with a dummy query
        client.search("health-check-dummy");
        if (client.getLastResponse() == null) {
             throw new RuntimeException("Backend is not responding");
        }
    }
}
