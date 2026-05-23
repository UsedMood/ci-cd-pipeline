package com.mdsearcher.ui.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Base class for Cucumber Glue code (Workflows).
 * Handles the lifecycle of the WebDriver and Backend.
 */
public abstract class AbstractWorkflow {
    protected static WebDriver driver;

    public void setUp() {
        if (driver == null) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless"); // Headless mode for CI/non-GUI environments
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            driver = new ChromeDriver(options);
        }
    }

    public void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public static WebDriver getDriver() {
        return driver;
    }

    /**
     * Betölt egy szöveges fájlt a test classpath-ról (src/test/resources).
     *
     * @param classpathPath az erőforrás elérési útja a classpath gyökeréhez képest (pl. "templates/test-template.md")
     * @return a fájl tartalma UTF-8 szövegként
     * @throws IOException ha az erőforrás nem található vagy nem olvasható
     */
    protected String loadResource(String classpathPath) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(classpathPath)) {
            if (is == null) {
                throw new IOException("Az erőforrás nem található a classpath-on: " + classpathPath);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Létrehoz egy ideiglenes fájlt a megadott könyvtárban a megadott tartalommal.
     *
     * @param tmpDirPath a célkönyvtár elérési útja (pl. "target/ui-temp-uploads")
     * @param filename   a létrehozandó fájl neve
     * @param content    a fájlba írandó tartalom
     * @return a létrehozott {@link File} objektum
     * @throws IOException ha a könyvtár nem hozható létre vagy a fájl nem írható
     */
    protected File createTempFile(String tmpDirPath, String filename, String content) throws IOException {
        Path tmpDir = Paths.get(tmpDirPath);
        if (!Files.exists(tmpDir)) {
            Files.createDirectories(tmpDir);
        }
        File file = new File(tmpDir.toFile(), filename);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }
}