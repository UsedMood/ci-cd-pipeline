package com.mdsearcher.api.client;

import com.mdsearcher.api.config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.File;

/**
 * Dedicated API client for MD-Searcher.
 * Handles HTTP communication using Rest Assured.
 * No assertions here, only execution.
 */
public class MDServiceClient {
    private final String baseUrl;
    private Response lastResponse;

    public MDServiceClient() {
        this.baseUrl = TestConfig.getInstance().getFullBaseUrl();
    }

    private RequestSpecification request() {
        return RestAssured.given().baseUri(baseUrl);
    }

    /**
     * Uploads a file to the /api/upload endpoint.
     * @param file The file to upload.
     * @return The Response object.
     */
    public Response uploadFile(File file) {
        lastResponse = request()
                .multiPart("file", file)
                .post("/api/upload");
        return lastResponse;
    }

    /**
     * Searches for documents using a query string.
     * @param query The search query.
     * @return The Response object.
     */
    public Response search(String query) {
        lastResponse = request()
                .queryParam("q", query)
                .get("/api/search");
        return lastResponse;
    }

    /**
     * Retrieves a document in HTML format by its id.
     * @param id The document id.
     * @return The Response object.
     */
    public Response getDocument(String id) {
        lastResponse = request()
                .get("/api/document/" + id);
        return lastResponse;
    }

    /**
     * Returns the last response received by this client.
     * @return The last Response object.
     */
    public Response getLastResponse() {
        return lastResponse;
    }
}
