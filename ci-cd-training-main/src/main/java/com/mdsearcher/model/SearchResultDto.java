package com.mdsearcher.model;

public class SearchResultDto {

    private String id;
    private String title;
    private int score;
    private String snippet;

    public SearchResultDto() {
    }

    public SearchResultDto(String id, String title, int score, String snippet) {
        this.id = id;
        this.title = title;
        this.score = score;
        this.snippet = snippet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    @Override
    public String toString() {
        return "SearchResultDto{id='" + id + "', title='" + title + "', score=" + score + "}";
    }
}
