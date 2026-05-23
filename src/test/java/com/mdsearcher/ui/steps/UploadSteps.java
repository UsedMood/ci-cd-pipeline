package com.mdsearcher.ui.steps;

import com.mdsearcher.ui.base.AbstractSteps;
import com.mdsearcher.ui.pages.MainPage;
import org.junit.jupiter.api.Assertions;

import java.io.File;

/**
 * Steps class for Upload-related business logic.
 * Decoupled from Page instantiation; receives MainPage in constructor.
 */
public class UploadSteps extends AbstractSteps {
    private final MainPage mainPage;

    public UploadSteps(MainPage mainPage) {
        this.mainPage = mainPage;
    }

    public void openMainPage() {
        mainPage.open();
    }

    public void uploadNewFile(File file) {
        mainPage.uploadFile(file.getAbsolutePath());
    }

    public void verifyUploadSuccess() {
        String status = mainPage.getUploadStatusText();
        Assertions.assertTrue(status.contains("successfully"), "Upload status message was: " + status);
    }

    public void verifyFileIsSearchable(String query) {
        mainPage.enterSearchQuery(query);
        mainPage.clickSearch();
        boolean found = mainPage.isFilePresentInResults(query);
        Assertions.assertTrue(found, "File with query " + query + " not found in UI search results");
    }
}
