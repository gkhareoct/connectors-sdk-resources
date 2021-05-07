package com.amdocs.connector.plugins.documentum;

import com.amdocs.connector.plugins.documentum.config.DocumentumConfig;
import com.amdocs.connector.plugins.documentum.fetcher.DocumentumFetcher;
import com.documentum.xml.jaxp.DfDocumentBuilderFactoryImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.amdocs.connector.plugins.documentum.client.DocumentumClient;
import com.google.inject.Singleton;
import com.lucidworks.fusion.connector.plugin.api.plugin.ConnectorPlugin;
import com.lucidworks.fusion.connector.plugin.api.plugin.ConnectorPluginProvider;

public class DocumentumPlugin implements ConnectorPluginProvider {

  @Override
  public ConnectorPlugin get() {
    Module fetchModule = new AbstractModule() {
      @Override
      protected void configure() {
        bind(DfDocumentBuilderFactoryImpl.class).in(Singleton.class);
        bind(DocumentumClient.class).in(Singleton.class);
      }
    };

    return ConnectorPlugin.builder(DocumentumConfig.class)
        .withFetcher("content", DocumentumFetcher.class, fetchModule)
        .build();
  }
}