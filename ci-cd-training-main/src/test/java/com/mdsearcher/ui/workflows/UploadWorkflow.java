package com.mdsearcher.ui.workflows;

import com.mdsearcher.api.manager.BackendManager;
import com.mdsearcher.ui.base.AbstractWorkflow;
import com.mdsearcher.ui.pages.MainPage;
import com.mdsearcher.ui.steps.UploadSteps;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.File;
import java.io.IOException;

public class UploadWorkflow extends AbstractWorkflow {

    private UploadSteps uploadSteps;
    private String fileNameTimestamp;
    private String markdownDocumentTitle;
    private String markdownSearchToken;
    private String markdownBoldText;
    private String markdownListItem;

    @Before("@ui")
    public void setupUi() throws Exception {
        BackendManager.startBackend();
        super.setUp();
        MainPage mainPage = new MainPage(driver);
        this.uploadSteps = new UploadSteps(mainPage);
    }

    @After("@ui")
    public void tearDownUi() throws Exception {
        super.tearDown();
        BackendManager.stopBackend();
    }

    @Given("A főoldalon vagyok")
    public void i_am_on_the_main_page() {
        uploadSteps.openMainPage();
    }

    @When("Feltöltök egy új fájlt")
    public void i_upload_a_new_file() throws IOException {
        fileNameTimestamp = String.valueOf(System.currentTimeMillis());
        String filename = "ui-test-" + fileNameTimestamp + ".md";

        String content = loadResource("templates/test-template.md");
        content = content.replace("[TIMESTAMP]", fileNameTimestamp);

        File file = createTempFile("target/ui-temp-uploads", filename, content);

        uploadSteps.uploadNewFile(file);
    }

    @Then("Látom, hogy a feltöltés sikeres")
    public void i_see_upload_success() {
        uploadSteps.verifyUploadSuccess();
    }

    @Then("Látom, hogy a fájl kereshető")
    public void i_see_file_is_searchable() {
        uploadSteps.verifyFileIsSearchable(fileNameTimestamp);
    }

    @When("Feltöltök egy markdown dokumentumot")
    public void i_upload_a_markdown_document() throws IOException {
        fileNameTimestamp = String.valueOf(System.currentTimeMillis());
        markdownDocumentTitle = "HTML megjelenites " + fileNameTimestamp;
        markdownSearchToken = "html-view-" + fileNameTimestamp;
        markdownBoldText = "kiemelt HTML szoveg " + fileNameTimestamp;
        markdownListItem = "Lista elem " + fileNameTimestamp;

        String filename = "html-view-" + fileNameTimestamp + ".md";
        String content = "# " + markdownDocumentTitle + "\n\n"
                + "Egyedi kereso token: " + markdownSearchToken + "\n\n"
                + "Ez a **" + markdownBoldText + "** HTML elemkent jelenik meg.\n\n"
                + "- " + markdownListItem + "\n";

        File file = createTempFile("target/ui-temp-uploads", filename, content);

        uploadSteps.uploadNewFile(file);
        uploadSteps.verifyUploadSuccess();
    }

    @When("Megnyitom a dokumentumot")
    public void i_open_the_document() {
        uploadSteps.openDocumentFromSearchResults(markdownSearchToken);
    }

    @Then("HTML formában látom a dokumentum tartalmát")
    public void i_see_the_document_content_as_html() {
        uploadSteps.verifyDocumentContentIsRenderedAsHtml(
                markdownDocumentTitle,
                markdownBoldText,
                markdownListItem
        );
    }
}
