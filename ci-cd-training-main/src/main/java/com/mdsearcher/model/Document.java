package com.mdsearcher.model;

public class Document {

    private String id;
    private String title;
    private String rawContent;
    private String htmlContent;
    private String filePath;

    public Document() {
    }

    public Document(String id, String title, String rawContent, String htmlContent, String filePath) {
        this.id = id;
        this.title = title;
        this.rawContent = rawContent;
        this.htmlContent = htmlContent;
        this.filePath = filePath;
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

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "Document{id='" + id + "', title='" + title + "', filePath='" + filePath + "'}";
    }
}
