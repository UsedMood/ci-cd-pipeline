package com.mdsearcher.api.steps;

import com.mdsearcher.api.client.MDServiceClient;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private String expectedHtmlTitle;
    private String expectedStrongText;

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

            uploadMarkdownFile(filename, content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process template or create temporary file for upload", e);
        }
    }

    /**
     * Uploads a markdown document containing syntax that should be rendered to HTML.
     */
    public void uploadMdForHtmlConversion() {
        long timestamp = System.currentTimeMillis();
        lastTimestamp = String.valueOf(timestamp);
        expectedHtmlTitle = "HTML Conversion " + lastTimestamp;
        expectedStrongText = "bold marker " + lastTimestamp;

        String filename = "html-conversion-" + timestamp + ".md";
        String content = "# " + expectedHtmlTitle + "\n\n"
                + "This paragraph contains **" + expectedStrongText + "**.\n\n"
                + "- first converted list item " + lastTimestamp + "\n";

        try {
            uploadMarkdownFile(filename, content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary markdown file for HTML conversion", e);
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
     * Verifies that the uploaded markdown document can be fetched as rendered HTML.
     */
    public void verifyUploadedDocumentConvertedToHtml() {
        if (lastTimestamp == null) {
            throw new IllegalStateException("No markdown document was uploaded in the previous step");
        }

        String documentId = findUploadedDocumentId();

        client.getDocument(documentId);

        Assertions.assertEquals(200, client.getLastResponse().getStatusCode(), "Document API returned an error");
        String html = client.getLastResponse().asString();

        Assertions.assertTrue(html.contains("<html>"), "Document response should contain an HTML document");
        Assertions.assertTrue(html.contains("<h1>" + expectedHtmlTitle + "</h1>"), "Markdown H1 heading was not converted to HTML");
        Assertions.assertTrue(html.contains("<strong>" + expectedStrongText + "</strong>"), "Markdown bold text was not converted to HTML");
        Assertions.assertTrue(html.contains("<li>first converted list item " + lastTimestamp + "</li>"), "Markdown list item was not converted to HTML");
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

    private void uploadMarkdownFile(String filename, String content) throws IOException {
        Path uploadSourceDir = Paths.get("target/temp-uploads");
        if (!Files.exists(uploadSourceDir)) {
            Files.createDirectories(uploadSourceDir);
        }

        File file = new File(uploadSourceDir.toFile(), filename);
        Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);

        client.uploadFile(file);
        lastUploadedFilename = filename;

        if (client.getLastResponse().getStatusCode() != 200) {
            throw new RuntimeException("Upload failed with status code: " + client.getLastResponse().getStatusCode());
        }
    }

    private String findUploadedDocumentId() {
        client.search(lastTimestamp);

        Assertions.assertEquals(200, client.getLastResponse().getStatusCode(), "Search API returned an error");

        List<Map<String, Object>> results = client.getLastResponse().jsonPath().getList("");

        return results.stream()
                .filter(res -> res.get("snippet") != null && res.get("snippet").toString().contains(lastTimestamp))
                .map(res -> res.get("id"))
                .filter(id -> id != null && !id.toString().isBlank())
                .map(Object::toString)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Uploaded document with timestamp " + lastTimestamp
                        + " was not found in search results. Results: " + client.getLastResponse().asString()));
    }
}
