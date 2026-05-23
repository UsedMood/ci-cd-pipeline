package com.mdsearcher.ui.workflows;

import com.mdsearcher.ui.base.AbstractWorkflow;
import com.mdsearcher.ui.pages.MainPage;
import com.mdsearcher.ui.steps.RenderSteps;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.File;
import java.io.IOException;

/**
 * Workflow that orchestrates the "markdown rendered as HTML" UI scenario.
 * The Before/After hooks for the @ui tag live in UploadWorkflow and run once per scenario,
 * so this class focuses only on the gherkin-to-step mapping (no driver lifecycle here).
 *
 * Call chain: workflow -> steps -> page.
 */
public class RenderWorkflow extends AbstractWorkflow {

    private RenderSteps renderSteps;
    private String timestamp;

    private void initStepsIfNeeded() {
        if (renderSteps == null) {
            MainPage mainPage = new MainPage(driver);
            renderSteps = new RenderSteps(mainPage);
        }
    }

    @Given("Egy feltöltött dokumentum kereshető a UI-on")
    public void an_uploaded_document_is_searchable_on_the_ui() throws IOException {
        initStepsIfNeeded();
        timestamp = String.valueOf(System.currentTimeMillis());
        String filename = "ui-render-" + timestamp + ".md";

        String content = loadResource("templates/test-template.md")
                .replace("[TIMESTAMP]", timestamp);

        File file = createTempFile("target/ui-temp-uploads", filename, content);
        renderSteps.prepareUploadedDocument(file, timestamp);
    }

    @When("Megnyitom a dokumentumot")
    public void i_open_the_document() {
        renderSteps.openDocument();
    }

    @Then("A tartalom HTML formában jelenik meg")
    public void the_content_is_displayed_as_html() {
        renderSteps.verifyContentRenderedAsHtml();
    }
}
