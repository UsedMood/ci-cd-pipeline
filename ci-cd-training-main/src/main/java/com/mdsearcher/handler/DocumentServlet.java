package com.mdsearcher.handler;

import com.mdsearcher.AppInitializer;
import com.mdsearcher.model.Document;
import com.mdsearcher.service.IndexService;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/api/document/*")
public class DocumentServlet extends HttpServlet {

    private IndexService indexService;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (this.indexService == null) {
            this.indexService = (IndexService)
                    config.getServletContext().getAttribute(AppInitializer.INDEX_SERVICE);
        }
    }

    public DocumentServlet(IndexService indexService) {
        this.indexService = indexService;
    }

    public DocumentServlet() { /* WAR konténer hívja */ }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Document ID is required.");
            return;
        }

        String id = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        Optional<Document> documentOpt = indexService.getById(id);

        if (!documentOpt.isPresent()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Document not found: " + id);
            return;
        }

        Document document = documentOpt.get();
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String page = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>"
                + escapeHtml(document.getTitle())
                + "</title></head><body>"
                + document.getHtmlContent()
                + "</body></html>";
        resp.getWriter().write(page);
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}