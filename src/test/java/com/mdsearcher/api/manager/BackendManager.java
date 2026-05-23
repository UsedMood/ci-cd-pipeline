package com.mdsearcher.api.manager;

import com.mdsearcher.Main;
import com.mdsearcher.api.config.TestConfig;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Manages the backend lifecycle for tests.
 * Starts the application Main class in a separate thread.
 */
public class BackendManager {
    private static final Logger LOGGER = Logger.getLogger(BackendManager.class.getName());
    private static ExecutorService executor;
    private static boolean running = false;

    public static synchronized void startBackend() {
        if (running) return;

        TestConfig config = TestConfig.getInstance();
        int port = config.getPort();
        String dataDir = config.getDataDir();

        try {
            LOGGER.info("Starting backend on port " + port + " with data dir " + dataDir);
            Main.start(port, dataDir);
            running = true;
        } catch (Exception e) {
            LOGGER.severe("Backend failed to start: " + e.getMessage());
            throw new RuntimeException("Failed to start backend", e);
        }
    }

    public static synchronized void stopBackend() {
        if (!running) return;
        
        try {
            LOGGER.info("Stopping backend");
            Main.stop();
            running = false;
        } catch (Exception e) {
            LOGGER.severe("Error stopping backend: " + e.getMessage());
        }
    }
}
