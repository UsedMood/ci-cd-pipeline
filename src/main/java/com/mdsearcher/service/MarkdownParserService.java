package com.mdsearcher.service;

import com.mdsearcher.model.Document;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class MarkdownParserService {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkdownParserService() {
        MutableDataSet options = new MutableDataSet();
        this.parser = Parser.builder(options).build();
        this.renderer = HtmlRenderer.builder(options).build();
    }

    public Document parse(Path filePath) throws IOException {
        String rawContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
        return parseContent(rawContent, filePath.toString(), deriveId(filePath));
    }

    public Document parseContent(String rawContent, String filePath, String id) {
        Node document = parser.parse(rawContent);
        String title = extractTitle(document, filePath);
        String htmlContent = renderer.render(document);
        return new Document(id, title, rawContent, htmlContent, filePath);
    }

    private String extractTitle(Node document, String filePath) {
        for (Node node = document.getFirstChild(); node != null; node = node.getNext()) {
            if (node instanceof Heading) {
                Heading heading = (Heading) node;
                if (heading.getLevel() == 1) {
                    return heading.getText().toString();
                }
            }
        }
        // Fall back to filename without extension
        String name = Path.of(filePath).getFileName().toString();
        if (name.contains(".")) {
            name = name.substring(0, name.lastIndexOf('.'));
        }
        return name;
    }

    private String deriveId(Path filePath) {
        return UUID.nameUUIDFromBytes(filePath.toAbsolutePath().toString()
                .getBytes(StandardCharsets.UTF_8)).toString();
    }
}
