package com.mdsearcher.unit.service;
import com.mdsearcher.service.IndexService;
import com.mdsearcher.service.SearchService;

import com.mdsearcher.model.Document;
import com.mdsearcher.model.SearchResultDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private IndexService indexService;

    private SearchService searchService;

    private Document docJava;
    private Document docRest;
    private Document docEmpty;

    @BeforeEach
    void setUp() {
        searchService = new SearchService(indexService);

        docJava = new Document(
                "id-java",
                "Java Best Practices",
                "Thread safety is important. Use ConcurrentHashMap for thread-safe maps in Java.",
                "<p>Thread safety is important.</p>",
                "/data/java-best-practices.md"
        );

        docRest = new Document(
                "id-rest",
                "REST API Guide",
                "This guide describes the search endpoint and document endpoint for the REST API.",
                "<p>This guide describes the search endpoint.</p>",
                "/data/rest-api-guide.md"
        );

        docEmpty = new Document(
                "id-empty",
                "Empty Document",
                "",
                "",
                "/data/empty.md"
        );
    }

    @Test
    void search_nullQuery_returnsEmptyList() {
        List<SearchResultDto> results = searchService.search(null);
        assertTrue(results.isEmpty());
        verifyNoInteractions(indexService);
    }

    @Test
    void search_blankQuery_returnsEmptyList() {
        List<SearchResultDto> results = searchService.search("   ");
        assertTrue(results.isEmpty());
        verifyNoInteractions(indexService);
    }

    @Test
    void search_emptyQuery_returnsEmptyList() {
        List<SearchResultDto> results = searchService.search("");
        assertTrue(results.isEmpty());
        verifyNoInteractions(indexService);
    }

    @Test
    void search_noMatchingDocuments_returnsEmptyList() {
        when(indexService.getAllDocuments()).thenReturn(Arrays.asList(docJava, docRest));

        List<SearchResultDto> results = searchService.search("kubernetes");

        assertTrue(results.isEmpty());
    }

    @Test
    void search_titleMatchScoresHigherThanBodyMatch() {
        // titleAndBody: "java" in title (+2) and body (+1) = 3
        // bodyOnly:     "java" in body only (+1) = 1
        Document titleAndBody = new Document("id-tb", "Java Programming",
                "Java is great for thread safety.", "", "/tb.md");
        Document bodyOnly = new Document("id-bo", "Programming Guide",
                "Java is used in many places.", "", "/bo.md");
        when(indexService.getAllDocuments()).thenReturn(Arrays.asList(bodyOnly, titleAndBody));

        List<SearchResultDto> results = searchService.search("java");

        assertEquals(2, results.size());
        assertEquals("id-tb", results.get(0).getId());
        assertTrue(results.get(0).getScore() > results.get(1).getScore());
    }

    @Test
    void search_titleOnlyMatch_scoreIsTwo() {
        Document titleOnly = new Document("id-t", "Unique Title Term", "No relevant body.", "", "/t.md");
        when(indexService.getAllDocuments()).thenReturn(Collections.singletonList(titleOnly));

        List<SearchResultDto> results = searchService.search("unique");

        assertEquals(1, results.size());
        // title match = 2 points
        assertEquals(2, results.get(0).getScore());
    }

    @Test
    void search_bodyOnlyMatch_scoreIsOne() {
        Document bodyOnly = new Document("id-b", "Unrelated Title", "body contains the keyword lambda.", "", "/b.md");
        when(indexService.getAllDocuments()).thenReturn(Collections.singletonList(bodyOnly));

        List<SearchResultDto> results = searchService.search("lambda");

        assertEquals(1, results.size());
        // body match only = 1 point
        assertEquals(1, results.get(0).getScore());
    }

    @Test
    void search_multipleTerms_scoresAccumulated() {
        // "thread safety" → "thread" in title? no. "thread" in body? yes (+1). "safety" in title? no. "safety" in body? yes (+1).
        when(indexService.getAllDocuments()).thenReturn(Collections.singletonList(docJava));

        List<SearchResultDto> results = searchService.search("thread safety");

        assertEquals(1, results.size());
        assertEquals(2, results.get(0).getScore()); // 1+1 body matches
    }

    @Test
    void search_resultsSortedByScoreDescending() {
        // highScore: "api" in title (+2) and body (+1) = 3
        // lowScore:  "api" in body only (+1) = 1
        Document highScore = new Document("id-high", "API Reference",
                "The API endpoint is documented here.", "", "/high.md");
        Document lowScore = new Document("id-low", "Developer Guide",
                "Use the API to access resources.", "", "/low.md");
        when(indexService.getAllDocuments()).thenReturn(Arrays.asList(lowScore, highScore));

        List<SearchResultDto> results = searchService.search("api");

        assertEquals(2, results.size());
        assertEquals("id-high", results.get(0).getId());
        assertTrue(results.get(0).getScore() >= results.get(1).getScore());
    }

    @Test
    void search_caseInsensitiveMatching() {
        when(indexService.getAllDocuments()).thenReturn(Collections.singletonList(docJava));

        List<SearchResultDto> resultLower = searchService.search("java");
        List<SearchResultDto> resultUpper = searchService.search("JAVA");
        List<SearchResultDto> resultMixed = searchService.search("Java");

        assertEquals(resultLower.size(), resultUpper.size());
        assertEquals(resultLower.size(), resultMixed.size());
        assertEquals(resultLower.get(0).getScore(), resultUpper.get(0).getScore());
    }

    @Test
    void search_emptyDocument_noMatch() {
        when(indexService.getAllDocuments()).thenReturn(Collections.singletonList(docEmpty));

        List<SearchResultDto> results = searchService.search("anything");

        assertTrue(results.isEmpty());
    }

    @Test
    void search_snippetIsNotNull() {
        when(indexService.getAllDocuments()).thenReturn(Collections.singletonList(docJava));

        List<SearchResultDto> results = searchService.search("thread");

        assertFalse(results.isEmpty());
        assertNotNull(results.get(0).getSnippet());
    }

    @Test
    void search_snippetContainsMatchContext() {
        when(indexService.getAllDocuments()).thenReturn(Collections.singletonList(docJava));

        List<SearchResultDto> results = searchService.search("concurrenthashmap");

        assertFalse(results.isEmpty());
        assertTrue(results.get(0).getSnippet().toLowerCase().contains("concurrenthashmap"));
    }

    @Test
    void search_resultDtoHasCorrectTitle() {
        when(indexService.getAllDocuments()).thenReturn(Collections.singletonList(docJava));

        List<SearchResultDto> results = searchService.search("java");

        assertEquals("Java Best Practices", results.get(0).getTitle());
    }

    @Test
    void search_resultDtoHasCorrectId() {
        when(indexService.getAllDocuments()).thenReturn(Collections.singletonList(docJava));

        List<SearchResultDto> results = searchService.search("java");

        assertEquals("id-java", results.get(0).getId());
    }
}
