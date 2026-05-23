package com.mdsearcher.api.glue;

import com.mdsearcher.api.steps.MDTestSteps;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Glue code that maps Gherkin steps to MDTestSteps methods.
 * It must NOT call the MDServiceClient directly.
 */
public class MDFilesStepDefinitions {

    private final MDTestSteps testSteps;

    public MDFilesStepDefinitions() {
        // In a real project, we might use Dependency Injection (e.g. cucumber-picocontainer)
        // For this education purpose, we instantiate it here or via a shared state.
        this.testSteps = new MDTestSteps();
    }

    @Given("Az alkalmazás backendje fut")
    public void the_application_be_is_running() {
        testSteps.verifyBackendRunning();
    }

    @When("Feltöltök egy új fájlt a megadott végponton")
    public void upload_new_file() {
        testSteps.uploadMd();
    }

    @Then("Látom, hogy a fájl elérhető az alkalmazásban")
    public void the_file_is_available() {
        testSteps.verifyFileInSearchResults();
    }

    @Then("A dokumentum tartalma HTML formátumban lekérdezhető")
    public void the_document_content_is_available_as_html() {
        testSteps.verifyDocumentIsServedAsHtml();
    }
}
