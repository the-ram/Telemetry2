package com.azure.ps.config;

import com.azure.ps.ext.EventHubClientFactory;
import com.azure.ps.interceptors.GzipReaderInterceptor;
import com.github.psamsotha.jersey.properties.JerseyPropertiesFeature;
import com.microsoft.azure.eventhubs.EventHubClient;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ws.rs.ApplicationPath;

/**
 * Created by adithya on 31/12/16.
 */
@ApplicationPath("/")

public class AppConfig extends ResourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    public AppConfig() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        register(GzipReaderInterceptor.class);
        register(JerseyPropertiesFeature.class);
        property(JerseyPropertiesFeature.RESOURCE_PATH, "application.properties");

        logger.info("Now running the registration process in resource config");

        register(new AbstractBinder() {
            @Override
            public void configure() {
                bindFactory(EventHubClientFactory.class).to(EventHubClient.class)
                        .in(RequestScoped.class);
            }
        });
    }
}
