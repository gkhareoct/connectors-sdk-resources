package com.amdocs.connector.plugins.documentum.fetcher;

import com.amdocs.connector.plugins.documentum.client.DocumentumClient;
import com.amdocs.connector.plugins.documentum.client.DocumentumFileUtil;
import com.amdocs.connector.plugins.documentum.client.MetaDocument;
import com.amdocs.connector.plugins.documentum.config.DocumentumConfig;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.common.DfException;
import com.lucidworks.fusion.connector.plugin.api.exceptions.ContentEmitException;
import com.lucidworks.fusion.connector.plugin.api.fetcher.result.FetchResult;
import com.lucidworks.fusion.connector.plugin.api.fetcher.result.StartResult;
import com.lucidworks.fusion.connector.plugin.api.fetcher.result.StopResult;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.ContentFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class DocumentumFetcher implements ContentFetcher {

    private static final Logger logger = LoggerFactory.getLogger(DocumentumFetcher.class);

    private final DocumentumClient client;
    private final DocumentumConfig config;

    @Inject
    public DocumentumFetcher(DocumentumClient client, DocumentumConfig config) {
        this.client = client;
        this.config = config;
    }

    @Override
    public StartResult start(StartContext context) {
        this.client.connect();
        return context.newResult();
    }

    @Override
    public FetchResult fetch(FetchContext context) {

        logger.info("Received FetchInput -> {}", context.getFetchInput());

        try {
            IDfCollection collection = client.getDocumentsByQuery(config.properties().docQuery());
            while (collection.next()) {
                processFile(context, client.getDocumentByObject(collection.getTypedObject()));
            }
        }
        catch (DfException e) {
            logger.error("Failed retrieving list of documents", e);
        }

        return context.newResult();
    }

    private void processFile(FetchContext context, IDfDocument dfDoc) {
        MetaDocument doc = null;

        try {
            doc = DocumentumFileUtil.extractMetaData(dfDoc);
            if (!DocumentumFileUtil.isArchive(dfDoc)) {
                emitContent(context, doc);
            }
        } catch (DfException e) {
            if (doc != null) {
                logger.error(String.format("Error emitting document: %s [%s]", doc.getTitle(), doc.getId()), e);
                context.newError(doc.getId(), e.getMessage()).emit();
            }
            else {
                logger.error("Error extracting documentum metadata", e);
            }
        } catch (ContentEmitException e) {
            logger.error("Error emitting document content content", e);
            context.newError(doc.getId(), e.getMessage()).emit();
        }
    }

    private void emitContent(FetchContext context, MetaDocument doc) throws DfException, ContentEmitException {
        context.newContent(doc.getId(), client.getDocumentContents(doc.getId()))
                .fields(f -> f.merge(doc.getAllFields()))
                .emit();
    }

    @Override
    public StopResult stop(StopContext context) {
        this.client.disconnect();
        return context.newResult();
    }
}