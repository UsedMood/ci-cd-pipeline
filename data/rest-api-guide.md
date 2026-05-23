# REST API Guide

This document describes the REST API endpoints exposed by MD-Searcher.

## Search Endpoint

**GET** `/api/search?q={query}`

Returns a JSON array of search results ranked by relevance score.

### Query Parameters

| Parameter | Type   | Required | Description                  |
|-----------|--------|----------|------------------------------|
| q         | string | yes      | The search query string      |

### Example Request

```
GET /api/search?q=java+thread+safety
```

### Example Response

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "title": "Java Best Practices",
    "score": 5,
    "snippet": "...prefer java.util.concurrent classes such as ConcurrentHashMap..."
  }
]
```

## Document Endpoint

**GET** `/api/document/{id}`

Returns the HTML-rendered content of the document with the given ID.

### Path Parameters

| Parameter | Type   | Description             |
|-----------|--------|-------------------------|
| id        | string | The document UUID       |

### Response

Returns `text/html` with the full rendered Markdown content.

Returns `404 Not Found` if the document ID does not exist in the index.

## Error Handling

All errors are returned as standard HTTP status codes:

- `400 Bad Request` — missing required parameter
- `404 Not Found` — document not found
- `500 Internal Server Error` — unexpected server error
