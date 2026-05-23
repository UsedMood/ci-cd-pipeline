package com.mdsearcher.api.glue;

import com.mdsearcher.api.manager.BackendManager;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;

/**
 * Cucumber hooks for managing global test lifecycle.
 * Ensures the backend starts before all tests and stops after all tests.
 */
public class Hooks {

    @BeforeAll
    public static void beforeAll() {
        BackendManager.startBackend();
    }

    @AfterAll
    public static void afterAll() {
        BackendManager.stopBackend();
    }
}
