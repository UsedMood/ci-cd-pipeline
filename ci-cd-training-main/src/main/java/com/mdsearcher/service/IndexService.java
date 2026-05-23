package com.mdsearcher.service;

import com.mdsearcher.model.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class IndexService {

    private final ConcurrentHashMap<String, Document> store = new ConcurrentHashMap<>();

    public void addDocument(Document document) {
        store.put(document.getId(), document);
    }

    public Optional<Document> getById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Document> getAllDocuments() {
        return new ArrayList<>(store.values());
    }

    public int size() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }
}
