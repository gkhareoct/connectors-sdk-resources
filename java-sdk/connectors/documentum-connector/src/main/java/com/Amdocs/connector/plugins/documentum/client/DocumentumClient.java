package com.amdocs.connector.plugins.documentum.client;

import com.amdocs.connector.plugins.documentum.config.DocumentumConfig;
import com.amdocs.connector.plugins.documentum.exception.DocumentumConnectionException;
import com.documentum.com.DfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfLoginInfo;
import com.google.inject.Inject;

import java.io.InputStream;

public class DocumentumClient {

    public static final String CONNECTOR_PLUGIN_DIR = "plugin.dir";

    public static final String CONNECTOR_PLUGIN_ID = "amdocs.demo.documentum";
    public static final String FILE_DFC_PROPERTIES = "dfc.properties";
    public static final String FILE_DFC_KEYSTORE = "dfc.keystore";

    private final DocumentumConfig config;

    private IDfSessionManager sessionManager;
    private IDfSession session;

    @Inject
    public DocumentumClient(DocumentumConfig config) {
        this.config = config;

        // Set system properties before connecting to documentum
        String pluginDir = System.getProperty(CONNECTOR_PLUGIN_DIR, "app/plugin") + "/" + CONNECTOR_PLUGIN_ID;
        System.setProperty("dfc.properties.file", pluginDir + "/" + FILE_DFC_PROPERTIES);
        System.setProperty("dfc.keystore.file", pluginDir + "/" + FILE_DFC_KEYSTORE);
        System.setProperty("dfc.diagnose_config", String.valueOf(config.diagnosticLogging()));
        System.setProperty("dfc.logging.verbose", String.valueOf(config.diagnosticLogging()));
        System.setProperty("dfc.logging.level_to_force_stack",
                Boolean.FALSE.equals(config.diagnosticLogging()) ? "OFF" : "ALL");
    }

    public void connect() {
        try {
            // The only class we instantiate directly is DfClientX.
            DfClientX clientX = new DfClientX();

            // Create a client based on the DfClientX object.
            IDfClient client = clientX.getLocalClient();

            // Create a session manager based on the local client.
            sessionManager = client.newSessionManager();

            // Set the user information in the login information variable.
            IDfLoginInfo loginInfo = clientX.getLoginInfo();
            loginInfo.setUser(config.properties().authentication().username());
            loginInfo.setPassword(config.properties().authentication().password());

            // Set the identity of the session manager object based on the repository
            // name and login information.
            sessionManager.setIdentity(config.properties().authentication().repository(), loginInfo);

            session = sessionManager.getSession(config.properties().authentication().repository());
        } catch (DfException e) {
            throw new DocumentumConnectionException("Failed to connect to Documentum Server", e);
        }
    }

    public void disconnect() {
        if (sessionManager != null && session != null) {
            sessionManager.release(session);
        }
    }

    public IDfCollection getDocumentsByQuery(String docQuery) throws DfException {
        IDfQuery query = new DfClientX().getQuery();
        query.setDQL(docQuery);
        return query.execute(session, IDfQuery.DF_QUERY);
    }

    public InputStream getDocumentContents(String docId) throws DfException {
        IDfId id = new DfId(docId);
        IDfSysObject sysObj = (IDfSysObject) session.getObject(id);
        return sysObj.getContent();
    }
}
