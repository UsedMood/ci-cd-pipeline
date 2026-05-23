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

    public void openFileFromResults(String filenameSnippet) {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("result-card")));
        WebElement matchingResult = resultsList.findElements(By.className("result-card")).stream()
                .filter(result -> result.getText().contains(filenameSnippet))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No search result contains: " + filenameSnippet));

        matchingResult.findElement(By.cssSelector(".result-title a")).click();
        wait.until(ExpectedConditions.attributeContains(modal, "class", "open"));
        wait.until(ExpectedConditions.visibilityOf(modalBody));
    }

    public boolean isDocumentHeadingVisible(String heading) {
        return isRenderedDocumentElementVisible("h1", heading);
    }

    public boolean isDocumentStrongTextVisible(String text) {
        return isRenderedDocumentElementVisible("strong", text);
    }

    public boolean isDocumentListItemVisible(String text) {
        return isRenderedDocumentElementVisible("li", text);
    }

    private boolean isRenderedDocumentElementVisible(String tagName, String text) {
        By locator = By.xpath(".//" + tagName + "[normalize-space(.)=" + xpathLiteral(text) + "]");
        wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(modalBody, locator));
        return !modalBody.findElements(locator).isEmpty();
    }

    private String xpathLiteral(String text) {
        if (!text.contains("'")) {
            return "'" + text + "'";
        }
        if (!text.contains("\"")) {
            return "\"" + text + "\"";
        }
        return "concat('" + text.replace("'", "', \"'\", '") + "')";
    }
}
