package com.mdsearcher.handler;

import com.mdsearcher.AppInitializer;
import com.mdsearcher.service.FileIngestionService;

import com.mdsearcher.service.IndexService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/upload")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize       = 10 * 1024 * 1024,
        maxRequestSize    = 20 * 1024 * 1024
)
public class UploadServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UploadServlet.class.getName());

    private String dataDir;
    private FileIngestionService ingestionService;
    private IndexService indexService;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (this.indexService == null) {
            this.indexService = (IndexService)
                    config.getServletContext().getAttribute(AppInitializer.INDEX_SERVICE);
        }
        if (this.ingestionService == null) {
            this.ingestionService = (FileIngestionService)
                    config.getServletContext().getAttribute(AppInitializer.INGESTION_SERVICE);
        }
        if (this.dataDir == null) {
            this.dataDir = (String) config.getServletContext().getAttribute(AppInitializer.DATA_DIR);
        }
    }

    public UploadServlet(String dataDir, FileIngestionService ingestionService, IndexService indexService) {
        this.dataDir = dataDir;
        this.ingestionService = ingestionService;
        this.indexService = indexService;
    }

    public UploadServlet() { /* WAR konténer hívja */ }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Part filePart = req.getPart("file");
            if (filePart == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Missing file part");
                return;
            }

            String fileName = getFileName(filePart);
            if (fileName == null || !fileName.toLowerCase().endsWith(".md")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Only .md files are allowed");
                return;
            }

            Path targetDir = Paths.get(dataDir);
            if (!Files.exists(targetDir)) Files.createDirectories(targetDir);
            if (!Files.isWritable(targetDir)) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Data directory is not writable");
                return;
            }

            Path targetPath = targetDir.resolve(fileName);
            filePart.write(targetPath.toAbsolutePath().toString());
            LOGGER.info("File uploaded: " + targetPath);

            ingestionService.ingest(dataDir);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("File uploaded and indexed successfully");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Upload failed", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Upload failed: " + e.getMessage());
        }
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String cd : contentDisposition.split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
