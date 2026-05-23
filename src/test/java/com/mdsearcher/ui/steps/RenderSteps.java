package com.mdsearcher.ui.steps;

import com.mdsearcher.ui.base.AbstractSteps;
import com.mdsearcher.ui.pages.MainPage;
import org.junit.jupiter.api.Assertions;

/**
 * Steps class for verifying the HTML rendering of markdown documents in the UI.
 * Follows the same pattern as UploadSteps: receives the MainPage in the constructor
 * and contains business-level operations and assertions (no page interaction details here).
 */
public class RenderSteps extends AbstractSteps {

    private final MainPage mainPage;

    public RenderSteps(MainPage mainPage) {
        this.mainPage = mainPage;
    }

    /**
     * Opens the main page, uploads a markdown file and searches for it so that
     * a clickable result card is available on the UI.
     */
    public void prepareUploadedDocument(java.io.File file, String searchToken) {
        mainPage.open();
        mainPage.uploadFile(file.getAbsolutePath());
        // Wait until the upload status confirms success before searching
        String status = mainPage.getUploadStatusText();
        Assertions.assertTrue(status.contains("successfully"),
                "Upload precondition failed, status was: " + status);
        mainPage.enterSearchQuery(searchToken);
        mainPage.clickSearch();
        Assertions.assertTrue(mainPage.isFilePresentInResults(searchToken),
                "Uploaded document is not searchable on the UI");
    }

    /**
     * Clicks the first search result so the document detail modal opens.
     */
    public void openDocument() {
        mainPage.openFirstResultDocument();
    }

    /**
     * Asserts that the modal contains HTML-rendered content originating from the markdown source.
     * The template document starts with a level-1 heading, which must appear as an &lt;h1&gt; tag.
     */
    public void verifyContentRenderedAsHtml() {
        String html = mainPage.getModalBodyHtml();
        Assertions.assertNotNull(html, "Modal body HTML is null");
        Assertions.assertTrue(html.toLowerCase().contains("<h1"),
                "Expected an <h1> tag in modal HTML, but got: " + html);
        Assertions.assertTrue(html.contains("Template Document"),
                "Expected rendered title text in modal HTML, but got: " + html);
    }
}
