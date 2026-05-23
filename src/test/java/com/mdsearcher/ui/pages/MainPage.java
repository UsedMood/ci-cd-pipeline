package com.mdsearcher.ui.pages;

import com.mdsearcher.api.config.TestConfig;
import com.mdsearcher.ui.base.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object for the Main Page of MD-Searcher.
 * Uses PageFactory and @FindBy for element location.
 */
public class MainPage extends AbstractPage {

    @FindBy(id = "searchInput")
    private WebElement searchInput;

    @FindBy(id = "searchBtn")
    private WebElement searchBtn;

    @FindBy(id = "fileInput")
    private WebElement fileInput;

    @FindBy(id = "uploadBtn")
    private WebElement uploadBtn;

    @FindBy(id = "uploadStatus")
    private WebElement uploadStatus;

    @FindBy(className = "results-list")
    private WebElement resultsList;

    @FindBy(id = "modal")
    private WebElement modal;

    @FindBy(id = "modalBody")
    private WebElement modalBody;

    public MainPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        String url = TestConfig.getInstance().getFullBaseUrl();
        driver.get(url);
    }

    public void enterSearchQuery(String query) {
        wait.until(ExpectedConditions.visibilityOf(searchInput));
        searchInput.clear();
        searchInput.sendKeys(query);
    }

    public void clickSearch() {
        searchBtn.click();
    }

    public void uploadFile(String absolutePath) {
        fileInput.sendKeys(absolutePath);
        uploadBtn.click();
    }

    public String getUploadStatusText() {
        wait.until(driver -> {
            String text = uploadStatus.getText();
            return text != null && !text.isEmpty() && !text.equals("Uploading…");
        });
        return uploadStatus.getText();
    }

    public boolean isFilePresentInResults(String filenameSnippet) {
        // Wait for results to be populated
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("result-card")));
        List<WebElement> results = resultsList.findElements(By.className("result-card"));
        return results.stream().anyMatch(res -> res.getText().contains(filenameSnippet));
    }

    /**
     * Opens the first matching document by clicking its link in the result list.
     * The result cards contain an anchor with a data-id attribute that triggers the modal.
     */
    public void openFirstResultDocument() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".result-card a[data-id]")));
        WebElement firstLink = resultsList.findElement(By.cssSelector(".result-card a[data-id]"));
        firstLink.click();
    }

    /**
     * Returns the inner HTML content of the document modal once it is rendered.
     * Waits for the modal to be visible and for its body to be populated with content (not the "Loading…" placeholder).
     */
    public String getModalBodyHtml() {
        wait.until(ExpectedConditions.visibilityOf(modal));
        wait.until(driver -> {
            String html = modalBody.getAttribute("innerHTML");
            return html != null && !html.isEmpty() && !html.contains("Loading…");
        });
        return modalBody.getAttribute("innerHTML");
    }
}
