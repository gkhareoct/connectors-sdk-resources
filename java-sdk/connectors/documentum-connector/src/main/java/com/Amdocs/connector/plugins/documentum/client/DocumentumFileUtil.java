package com.amdocs.connector.plugins.documentum.client;

import com.amdocs.connector.plugins.documentum.fetcher.DocumentumFetcher;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.common.DfException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentumFileUtil {

    public static final String UNIQUE_DOCUMENT_ID = "r_object_id";
    public static final String DOCUMENT_NAME = "object_name";
    public static final String TITLE = "title";
    public static final String CONTENT_TYPE = "a_content_type";
    public static final String CREATION_DATE = "r_creation_date";
    public static final String MODIFIED_DATE = "r_modify_date";
    public static final String GROUP_NAME = "group_name";
    public static final String PERMIT = "group_name";
    public static final String ACL = "acl_name";
    private static final Logger logger = LoggerFactory.getLogger(DocumentumFileUtil.class);


    private DocumentumFileUtil() {}

    public static boolean isArchive(IDfDocument doc) throws DfException {
        return doc.getContentType().equals("zip");
    }

    public static MetaDocument extractMetaData(IDfDocument dfDoc) throws DfException {
        MetaDocument metaDoc = new MetaDocument();
        metaDoc.setContentType(dfDoc.getContentType());
        metaDoc.setId(dfDoc.getString(UNIQUE_DOCUMENT_ID));
        logger.info("document id=" +dfDoc.getString(UNIQUE_DOCUMENT_ID));
        metaDoc.setTitle(dfDoc.getString(DOCUMENT_NAME));
        logger.info("document name = " +dfDoc.getString(DOCUMENT_NAME));
        return metaDoc;
    }
}
