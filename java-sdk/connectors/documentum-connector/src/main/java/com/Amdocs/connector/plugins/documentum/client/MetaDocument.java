package com.amdocs.connector.plugins.documentum.client;

import java.util.HashMap;
import java.util.Map;

public class MetaDocument {

    private static final String UNIQUE_ID = "unique_id";
    private static final String DOC_TITLE = "doc_title";
    private static final String CONTENT_TYPE = "content_type";

    private final HashMap<String, Object> fields;

    public MetaDocument() {
        this.fields = new HashMap<>();
    }

    public Map<String, Object> getAllFields() {
        return this.fields;
    }

    public String getId() {
        return (String) this.fields.get(UNIQUE_ID);
    }
    public void setId(String id) {
        this.fields.put(UNIQUE_ID, id);
    }

    public String getTitle() {
        return (String) this.fields.get(DOC_TITLE);
    }
    public void setTitle(String title) {
        this.fields.put(DOC_TITLE, title);
    }

    public void setContentType(String contentType) {
        this.fields.put(CONTENT_TYPE, contentType);
    }
}
