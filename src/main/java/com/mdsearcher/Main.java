package com.mdsearcher;

import com.mdsearcher.handler.DocumentServlet;
import com.mdsearcher.handler.SearchServlet;
import com.mdsearcher.handler.UploadServlet;
import com.mdsearcher.service.FileIngestionService;
import com.mdsearcher.service.IndexService;
import com.mdsearcher.service.MarkdownParserService;
import com.mdsearcher.service.SearchService;
import jakarta.servlet.MultipartConfigElement;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import java.io.File;
import java.util.logging.Logger;

/**
 * Belépési pont IDE-ből való futtatáshoz (embedded Jetty).
 * WAR deploymentnél ezt az osztályt a konténer NEM használja –
 * az inicializálást az AppInitializer végzi.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_DATA_DIR = "./data";

    private static Server server;

    public static void main(String[] args) throws Exception {
        int port = DEFAULT_PORT;
        String dataDir = DEFAULT_DATA_DIR;

        for (int i = 0; i < args.length; i++) {
            if ("--port".equals(args[i]) && i + 1 < args.length) {
                port = Integer.parseInt(args[++i]);
            } else if ("--data".equals(args[i]) && i + 1 < args.length) {
                dataDir = args[++i];
            }
        }

        start(port, dataDir);
        server.join();
    }

    public static void start(int port, String dataDir) throws Exception {
        // Service wiring
        IndexService indexService             = new IndexService();
        MarkdownParserService parserService   = new MarkdownParserService();
        FileIngestionService ingestionService = new FileIngestionService(parserService, indexService);
        SearchService searchService           = new SearchService(indexService);

        LOGGER.info("Ingesting Markdown files from: " + dataDir);
        ingestionService.ingest(dataDir);

        // Embedded Jetty konfiguráció
        server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        Resource staticBase = Resource.newClassPathResource("/static");
        if (staticBase == null) {
            // WAR-struktúra esetén: src/main/webapp közvetlenül
            staticBase = Resource.newResource("src/main/webapp");
        }
        context.setBaseResource(staticBase);

        context.addServlet(new ServletHolder(new SearchServlet(searchService)), "/api/search");
        context.addServlet(new ServletHolder(new DocumentServlet(indexService)), "/api/document/*");
        
        ServletHolder uploadHolder = new ServletHolder(new UploadServlet(dataDir, ingestionService, indexService));
        uploadHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(
                new File(dataDir).getAbsolutePath(),
                10 * 1024 * 1024,
                20 * 1024 * 1024,
                1024 * 1024
        ));
        context.addServlet(uploadHolder, "/api/upload");

        ServletHolder defaultHolder = new ServletHolder("default", DefaultServlet.class);
        defaultHolder.setInitParameter("dirAllowed", "false");
        defaultHolder.setInitParameter("welcomeServlets", "false");
        defaultHolder.setInitParameter("redirectWelcome", "false");
        context.addServlet(defaultHolder, "/");
        context.setWelcomeFiles(new String[]{"index.html"});

        server.setHandler(context);
        server.start();

        LOGGER.info("MD-Searcher started on http://localhost:" + port);
    }

    public static void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }
}