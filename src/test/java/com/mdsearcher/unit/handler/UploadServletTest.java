package com.mdsearcher.unit.handler;
import com.mdsearcher.handler.UploadServlet;
import com.mdsearcher.service.FileIngestionService;

import com.mdsearcher.service.FileIngestionService;
import com.mdsearcher.service.IndexService;
import com.mdsearcher.service.MarkdownParserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class UploadServletTest {

    @TempDir
    Path tempDir;

    private IndexService indexService;
    private FileIngestionService ingestionService;
    private static class TestUploadServlet extends UploadServlet {
        public TestUploadServlet(String dataDir, FileIngestionService ingestionService, IndexService indexService) {
            super(dataDir, ingestionService, indexService);
        }

        @Override
        public void doPost(HttpServletRequest req, HttpServletResponse resp) throws jakarta.servlet.ServletException, IOException {
            super.doPost(req, resp);
        }
    }

    private TestUploadServlet uploadServlet;

    @BeforeEach
    void setUp() {
        indexService = new IndexService();
        MarkdownParserService parserService = new MarkdownParserService();
        ingestionService = new FileIngestionService(parserService, indexService);
        uploadServlet = new TestUploadServlet(tempDir.toString(), ingestionService, indexService);
    }

    @Test
    void testUploadMarkdownFile() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Part filePart = mock(Part.class);

        String fileName = "test-upload.md";
        String content = "# Test Upload\nThis is a test.";
        
        when(request.getPart("file")).thenReturn(filePart);
        when(filePart.getHeader("content-disposition")).thenReturn("form-data; name=\"file\"; filename=\"" + fileName + "\"");
        when(filePart.getInputStream()).thenReturn(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
        
        // Mock filePart.write() behavior
        doAnswer(invocation -> {
            String pathStr = invocation.getArgument(0);
            Files.write(Path.of(pathStr), content.getBytes(StandardCharsets.UTF_8));
            return null;
        }).when(filePart).write(anyString());

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        uploadServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(Files.exists(tempDir.resolve(fileName)));
        assertEquals(content, Files.readString(tempDir.resolve(fileName)));
        
        // Verify indexing
        assertEquals(1, indexService.size());
        assertTrue(indexService.getAllDocuments().stream().anyMatch(d -> d.getTitle().equals("Test Upload")));
    }

    @Test
    void testUploadNonMarkdownFileFails() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Part filePart = mock(Part.class);

        when(request.getPart("file")).thenReturn(filePart);
        when(filePart.getHeader("content-disposition")).thenReturn("form-data; name=\"file\"; filename=\"test.txt\"");

        uploadServlet.doPost(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), contains("Only .md files are allowed"));
        assertEquals(0, indexService.size());
    }

    @Test
    void testUploadWithoutFilePartReturns404() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // No file part is provided in the request
        when(request.getPart("file")).thenReturn(null);

        uploadServlet.doPost(request, response);

        // Expected behavior: service must return HTTP 404 when the file part is missing
        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), contains("Missing file part"));
        assertEquals(0, indexService.size());
    }
}
