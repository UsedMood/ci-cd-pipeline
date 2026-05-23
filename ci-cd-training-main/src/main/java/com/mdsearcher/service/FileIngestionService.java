package com.mdsearcher.service;

import com.mdsearcher.model.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class FileIngestionService {

    private static final Logger LOGGER = Logger.getLogger(FileIngestionService.class.getName());

    private final MarkdownParserService parserService;
    private final IndexService indexService;

    public FileIngestionService(MarkdownParserService parserService, IndexService indexService) {
        this.parserService = parserService;
        this.indexService = indexService;
    }

    public void ingest(String directoryPath) {
        Path dir = Paths.get(directoryPath);
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            LOGGER.warning("Data directory does not exist or is not a directory: " + directoryPath);
            return;
        }

        try (Stream<Path> walk = Files.walk(dir)) {
            walk.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".md"))
                .forEach(this::ingestFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to walk directory: " + directoryPath, e);
        }

        LOGGER.info("Ingestion complete. Indexed " + indexService.size() + " document(s).");
    }

    private void ingestFile(Path filePath) {
        try {
            Document document = parserService.parse(filePath);
            indexService.addDocument(document);
            LOGGER.info("Indexed: " + filePath);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to parse file: " + filePath, e);
        }
    }
}
