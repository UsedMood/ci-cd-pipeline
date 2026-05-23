package com.mdsearcher.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdsearcher.AppInitializer;
import com.mdsearcher.model.SearchResultDto;
import com.mdsearcher.service.SearchService;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/search")
public class SearchServlet extends HttpServlet {

    private SearchService searchService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** WAR-os inicializálás: service a ServletContext-ből */

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (this.searchService == null) {
            this.searchService = (SearchService)
                    config.getServletContext().getAttribute(AppInitializer.SEARCH_SERVICE);
        }
    }

    /** IDE-s futtatáshoz: konstruktor alapú DI (Main.java használja) */
    public SearchServlet(SearchService searchService) {
        this.searchService = searchService;
    }

    public SearchServlet() { /* WAR konténer hívja */ }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String query = req.getParameter("q");
        List<SearchResultDto> results = searchService.search(query);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        objectMapper.writeValue(resp.getWriter(), results);
    }
}