package com.mdsearcher.unit.service;

import com.mdsearcher.model.Document;
import com.mdsearcher.service.IndexService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class IndexServiceTest {

    private IndexService indexService;

    @BeforeEach
    void setUp() {
        indexService = new IndexService();
    }

    @Test
    void addDocument_newDocument_increasesSize() {
        indexService.addDocument(document("doc-1", "First"));

        assertEquals(1, indexService.size());
    }

    @Test
    void getById_existingDocument_returnsDocument() {
        Document document = document("doc-1", "First");
        indexService.addDocument(document);

        Optional<Document> result = indexService.getById("doc-1");

        assertTrue(result.isPresent());
        assertSame(document, result.get());
    }

    @Test
    void clear_existingDocuments_removesAllDocuments() {
        indexService.addDocument(document("doc-1", "First"));
        indexService.addDocument(document("doc-2", "Second"));

        indexService.clear();

        assertEquals(0, indexService.size());
        assertTrue(indexService.getAllDocuments().isEmpty());
    }

    private Document document(String id, String title) {
        return new Document(id, title, "# " + title, "<h1>" + title + "</h1>", "/docs/" + id + ".md");
    }
}
