package com.amdocs.connector.plugins.documentum.client;

import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.common.DfException;

public class DocumentumFileUtil {

    public static final String UNIQUE_DOCUMENT_ID = "r_object_id";
    public static final String DOCUMENT_NAME = "r_object_name";

    private DocumentumFileUtil() {}

    public static boolean isArchive(IDfDocument doc) throws DfException {
        return doc.getContentType().equals("zip");
    }

    public static MetaDocument extractMetaData(IDfDocument dfDoc) throws DfException {
        MetaDocument metaDoc = new MetaDocument();
        metaDoc.setContentType(dfDoc.getContentType());
        metaDoc.setId(dfDoc.getString(UNIQUE_DOCUMENT_ID));
        metaDoc.setTitle(dfDoc.getString(DOCUMENT_NAME));
        return metaDoc;
    }
}
