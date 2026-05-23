package com.mdsearcher.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton configuration class that loads test properties.
 * Provides easy access to application-test.properties values.
 */
public class TestConfig {
    private static TestConfig instance;
    private final Properties properties;

    private TestConfig() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application-test.properties")) {
            if (input == null) {
                throw new RuntimeException("Sorry, unable to find application-test.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading application-test.properties", ex);
        }
    }

    public static synchronized TestConfig getInstance() {
        if (instance == null) {
            instance = new TestConfig();
        }
        return instance;
    }

    public String getBaseUrl() {
        return properties.getProperty("server.baseurl");
    }

    public int getPort() {
        return Integer.parseInt(properties.getProperty("server.port"));
    }

    public String getDataDir() {
        return properties.getProperty("server.data.dir");
    }

    public String getFullBaseUrl() {
        return getBaseUrl() + ":" + getPort();
    }
}
