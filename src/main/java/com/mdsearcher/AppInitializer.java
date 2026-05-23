package com.mdsearcher;

import com.mdsearcher.service.FileIngestionService;
import com.mdsearcher.service.IndexService;
import com.mdsearcher.service.MarkdownParserService;
import com.mdsearcher.service.SearchService;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.logging.Logger;

@WebListener
public class AppInitializer implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppInitializer.class.getName());
    private static final String DEFAULT_DATA_DIR = "./data";

    // Kulcsok, amelyeken a service-ek elérhetők a ServletContext-ben
    public static final String INDEX_SERVICE    = "indexService";
    public static final String SEARCH_SERVICE   = "searchService";
    public static final String INGESTION_SERVICE = "ingestionService";
    public static final String DATA_DIR         = "dataDir";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        String dataDir = System.getProperty("mdsearcher.dataDir", DEFAULT_DATA_DIR);

        IndexService indexService           = new IndexService();
        MarkdownParserService parserService = new MarkdownParserService();
        FileIngestionService ingestionService =
                new FileIngestionService(parserService, indexService);
        SearchService searchService         = new SearchService(indexService);

        LOGGER.info("Ingesting Markdown files from: " + dataDir);
        ingestionService.ingest(dataDir);

        ctx.setAttribute(DATA_DIR,          dataDir);
        ctx.setAttribute(INDEX_SERVICE,     indexService);
        ctx.setAttribute(SEARCH_SERVICE,    searchService);
        ctx.setAttribute(INGESTION_SERVICE, ingestionService);

        LOGGER.info("MD-Searcher application initialized.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("MD-Searcher application stopped.");
    }
}
