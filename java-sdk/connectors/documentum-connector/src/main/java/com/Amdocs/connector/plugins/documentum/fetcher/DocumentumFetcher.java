package com.amdocs.connector.plugins.documentum.fetcher;

import com.amdocs.connector.plugins.documentum.client.DocumentumClient;
import com.amdocs.connector.plugins.documentum.client.DocumentumFileUtil;
import com.amdocs.connector.plugins.documentum.client.MetaDocument;
import com.amdocs.connector.plugins.documentum.config.DocumentumConfig;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.common.DfException;
import com.lucidworks.fusion.connector.plugin.api.fetcher.result.FetchResult;
import com.lucidworks.fusion.connector.plugin.api.fetcher.result.StartResult;
import com.lucidworks.fusion.connector.plugin.api.fetcher.result.StopResult;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.ContentFetcher;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.FetchInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.InputStream;

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
        
        // debug
        try {
            logger.info(String.format("Loaded class: %s",
                    Thread.currentThread()
                            .getContextClassLoader()
                            .loadClass("com.documentum.xml.jaxp.DfDocumentBuilderFactoryImpl")
                            .getSimpleName()
            ));
        } catch (ClassNotFoundException e) {
            logger.error("Error loading com.documentum.xml.jaxp.DfDocumentBuilderFactoryImpl", e);
        }

        try {
            logger.info(String.format("Loaded class: %s",
                    Thread.currentThread()
                            .getContextClassLoader()
                            .loadClass("com.documentum.xml.jaxp.DfFactoryFinder")
                            .getSimpleName()
            ));
        } catch (ClassNotFoundException e) {
            logger.error("Error loading com.documentum.xml.jaxp.DfFactoryFinder", e);
        }

        this.client.connect();
        return context.newResult();
    }

    @Override
    public FetchResult fetch(FetchContext context) {

        FetchInput input = context.getFetchInput();
        logger.info("Received FetchInput -> {}", input);

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
                emitDocument(context, doc);
                emitContent(context, doc);
            }
        } catch (DfException e) {
            if (doc != null) {
                logger.error(String.format("Error emitting document: %s [%s]", doc.getTitle(), doc.getId()), e);
                context.newError(doc.getId(), e.getMessage()).emit();
            }
            else {
                logger.error("Error extracting metadata", e);
            }
        }
    }

    private void emitDocument(FetchContext context, MetaDocument doc) {
        context.newDocument(doc.getId())
                .fields(f -> f.merge(doc.getAllFields()))
                .emit();
    }

    private void emitContent(FetchContext context, MetaDocument doc) throws DfException {
        InputStream is = client.getDocumentContents(doc.getId());
        context.newContent(doc.getId(), () -> is).emit();
    }

    @Override
    public StopResult stop(StopContext context) {
        this.client.disconnect();
        return context.newResult();
    }
}