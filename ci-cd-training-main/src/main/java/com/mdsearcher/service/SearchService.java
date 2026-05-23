package com.mdsearcher.service;

import com.mdsearcher.model.Document;
import com.mdsearcher.model.SearchResultDto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchService {

    private static final int TITLE_MATCH_SCORE = 2;
    private static final int BODY_MATCH_SCORE = 1;
    private static final int SNIPPET_MAX_LENGTH = 200;

    private final IndexService indexService;

    public SearchService(IndexService indexService) {
        this.indexService = indexService;
    }

    public List<SearchResultDto> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String[] terms = query.trim().toLowerCase().split("\\s+");
        List<SearchResultDto> results = new ArrayList<>();

        for (Document document : indexService.getAllDocuments()) {
            int score = calculateScore(document, terms);
            if (score > 0) {
                String snippet = buildSnippet(document.getRawContent(), terms);
                results.add(new SearchResultDto(document.getId(), document.getTitle(), score, snippet));
            }
        }

        results.sort(Comparator.comparingInt(SearchResultDto::getScore).reversed());
        return results;
    }

    private int calculateScore(Document document, String[] terms) {
        int score = 0;
        String titleLower = document.getTitle() != null ? document.getTitle().toLowerCase() : "";
        String contentLower = document.getRawContent() != null ? document.getRawContent().toLowerCase() : "";

        for (String term : terms) {
            if (titleLower.contains(term)) {
                score += TITLE_MATCH_SCORE;
            }
            if (contentLower.contains(term)) {
                score += BODY_MATCH_SCORE;
            }
        }
        return score;
    }

    private String buildSnippet(String rawContent, String[] terms) {
        if (rawContent == null || rawContent.isEmpty()) {
            return "";
        }
        String contentLower = rawContent.toLowerCase();
        for (String term : terms) {
            int index = contentLower.indexOf(term);
            if (index >= 0) {
                int start = Math.max(0, index - 60);
                int end = Math.min(rawContent.length(), index + SNIPPET_MAX_LENGTH);
                String snippet = rawContent.substring(start, end).trim();
                if (start > 0) {
                    snippet = "..." + snippet;
                }
                if (end < rawContent.length()) {
                    snippet = snippet + "...";
                }
                return snippet;
            }
        }
        // No term found in content, return the beginning
        int end = Math.min(rawContent.length(), SNIPPET_MAX_LENGTH);
        return rawContent.substring(0, end).trim() + (rawContent.length() > SNIPPET_MAX_LENGTH ? "..." : "");
    }
}
