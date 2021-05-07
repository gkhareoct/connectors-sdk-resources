package com.amdocs.connector.plugins.documentum.config;

import com.lucidworks.fusion.connector.plugin.api.config.ConnectorConfig;
import com.lucidworks.fusion.connector.plugin.api.config.ConnectorPluginProperties;
import com.lucidworks.fusion.schema.Model;
import com.lucidworks.fusion.schema.SchemaAnnotations;
import com.lucidworks.fusion.schema.SchemaAnnotations.Property;
import com.lucidworks.fusion.schema.SchemaAnnotations.RootSchema;
import com.lucidworks.fusion.schema.SchemaAnnotations.StringSchema;
import com.lucidworks.fusion.schema.UIHints;

@RootSchema(
        title = "Documentum Connector",
        description = "A connector to connect fusion to documentum",
        category = "Document"
)
public interface DocumentumConfig extends ConnectorConfig<DocumentumConfig.Properties> {

    @Property(
            title = "Properties",
            required = true
    )
    Properties properties();

    /**
     * Connector specific settings
     */
    interface Properties extends ConnectorPluginProperties {

        @Property(
                title = "Authentication Settings",
                order = 1,
                required = true
        )
        AuthenticationConfig authentication();

        @Property(
                title = "DQL",
                description = "Documentum Query to be used to retrieve documents",
                required = true,
                order = 2
        )
        @StringSchema
        String docQuery();
    }

    interface AuthenticationConfig extends Model {

        @Property(
                title = "Host",
                description = "The hostname of the documentum server",
                required = true,
                order = 0
        )
        @StringSchema
        String host();

        @Property(
                title = "Port",
                description = "The port number",
                required = true,
                order = 1
        )
        @StringSchema
        String port();

        @Property(
                title = "Repository",
                description = "The repository detail",
                required = true,
                order = 2
        )
        @StringSchema
        String repository();

        @Property(
                title = "username",
                description = "The username",
                required = true,
                order = 4

        )
        @StringSchema
        String username();

        @Property(
                title = "Password",
                description = "The password",
                required = true,
                order = 5,
                hints = {UIHints.SECRET}
        )
        @StringSchema
        String password();
    }

}
