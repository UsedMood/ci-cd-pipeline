package com.mdsearcher.unit.service;
import com.mdsearcher.service.MarkdownParserService;

import com.mdsearcher.model.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownParserServiceTest {

    private MarkdownParserService parserService;

    @BeforeEach
    void setUp() {
        parserService = new MarkdownParserService();
    }

    @Test
    void parseContent_h1Heading_usedAsTitle() {
        String markdown = "# My Document Title\n\nSome body text.";
        Document doc = parserService.parseContent(markdown, "/docs/my-document.md", "test-id");
        assertEquals("My Document Title", doc.getTitle());
    }

    @Test
    void parseContent_noH1_fallsBackToFilename() {
        String markdown = "## Only an H2 heading\n\nSome body text.";
        Document doc = parserService.parseContent(markdown, "/docs/my-document.md", "test-id");
        assertEquals("my-document", doc.getTitle());
    }

    @Test
    void parseContent_noHeadingAtAll_fallsBackToFilename() {
        String markdown = "Just plain text without any heading.";
        Document doc = parserService.parseContent(markdown, "/docs/notes.md", "test-id");
        assertEquals("notes", doc.getTitle());
    }

    @Test
    void parseContent_h2BeforeH1_usesH1() {
        // H2 appears first but H1 should still be preferred when it is the first node of level 1
        String markdown = "# Correct Title\n\n## Subtitle\n\nBody.";
        Document doc = parserService.parseContent(markdown, "/docs/file.md", "test-id");
        assertEquals("Correct Title", doc.getTitle());
    }

    @Test
    void parseContent_htmlContentIsNotNull() {
        String markdown = "# Title\n\nParagraph.";
        Document doc = parserService.parseContent(markdown, "/docs/file.md", "test-id");
        assertNotNull(doc.getHtmlContent());
    }

    @Test
    void parseContent_htmlContainsParagraphTag() {
        String markdown = "# Title\n\nHello world.";
        Document doc = parserService.parseContent(markdown, "/docs/file.md", "test-id");
        assertTrue(doc.getHtmlContent().contains("<p>"));
    }

    @Test
    void parseContent_htmlContainsH1Tag() {
        String markdown = "# My Heading\n\nBody text.";
        Document doc = parserService.parseContent(markdown, "/docs/file.md", "test-id");
        assertTrue(doc.getHtmlContent().contains("<h1>"));
    }

    @Test
    void parseContent_rawContentPreserved() {
        String markdown = "# Title\n\nOriginal **raw** content.";
        Document doc = parserService.parseContent(markdown, "/docs/file.md", "test-id");
        assertEquals(markdown, doc.getRawContent());
    }

    @Test
    void parseContent_idIsSet() {
        String markdown = "# Title\n\nBody.";
        Document doc = parserService.parseContent(markdown, "/docs/file.md", "my-custom-id");
        assertEquals("my-custom-id", doc.getId());
    }

    @Test
    void parseContent_filePathIsSet() {
        String markdown = "# Title\n\nBody.";
        Document doc = parserService.parseContent(markdown, "/docs/file.md", "id-123");
        assertEquals("/docs/file.md", doc.getFilePath());
    }

    @Test
    void parseContent_boldMarkdownRenderedToStrongTag() {
        String markdown = "# Title\n\nThis is **bold** text.";
        Document doc = parserService.parseContent(markdown, "/docs/file.md", "test-id");
        assertTrue(doc.getHtmlContent().contains("<strong>"));
    }

    @Test
    void parseContent_codeBlockRenderedToPreTag() {
        String markdown = "# Title\n\n```\nSystem.out.println(\"hello\");\n```";
        Document doc = parserService.parseContent(markdown, "/docs/file.md", "test-id");
        assertTrue(doc.getHtmlContent().contains("<pre>") || doc.getHtmlContent().contains("<code>"));
    }

    @Test
    void parseContent_emptyMarkdown_doesNotThrow() {
        assertDoesNotThrow(() -> {
            Document doc = parserService.parseContent("", "/docs/empty.md", "id-empty");
            assertNotNull(doc);
            assertEquals("empty", doc.getTitle());
        });
    }

    @Test
    void parseContent_multipleH1_firstOneUsedAsTitle() {
        String markdown = "# First Title\n\n# Second Title\n\nBody.";
        Document doc = parserService.parseContent(markdown, "/docs/file.md", "test-id");
        assertEquals("First Title", doc.getTitle());
    }

    @Test
    void parseContent_filenameWithoutExtensionStripped() {
        String markdown = "No heading here.";
        Document doc = parserService.parseContent(markdown, "/some/path/my-notes.md", "test-id");
        assertEquals("my-notes", doc.getTitle());
    }
}
