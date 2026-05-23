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
}
