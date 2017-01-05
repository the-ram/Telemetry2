package com.azure.ps;

import com.azure.ps.config.EventHubClientFactory;
import com.azure.ps.interceptors.GzipReaderInterceptor;
import com.github.psamsotha.jersey.properties.JerseyPropertiesFeature;
import com.microsoft.azure.eventhubs.EventHubClient;
import junit.framework.Assert;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class EventControllerTest extends JerseyTest {

    private static final Logger logger = LoggerFactory.getLogger(EventControllerTest.class);

    @Before
    public void setUpTest() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDownTest() throws Exception {
        //server.shutdown();
    }

    @Test
    public void testCreated_RC201() {

        String eventSample = "{\"events\": [{\"deviceId\": \"123\",\"timestamp\": 1483203939718,\"eventName\": \"Randomevent-1\"}]}";
        Entity<String> entity = Entity.json(eventSample);
        int httpReturnCode = target("events").request().header("x-device-id", "123").
                header(HttpHeaders.CONTENT_ENCODING, "application/gzip").post(entity).getStatus();
        Assert.assertEquals(201, httpReturnCode);
    }

    @Test
    public void testServerError_RC500() {
        String eventSample = "{\"events\": {\"deviceId\": \"123\",\"timestamp\": 1483203939718,\"eventName\": \"Randomevent-1\"}";
        Entity<String> entity = Entity.json(eventSample);
        int httpReturnCode = target("events").request().header("x-device-id", "123").
                header(HttpHeaders.CONTENT_ENCODING, "application/gzip").post(entity).getStatus();
        Assert.assertEquals(500, httpReturnCode);
    }

    @Test
    public void testInvalidRequest_RC400() {
        String eventSample = "{\"event\": [{\"deviceId\": \"123\",\"timestamp\": 1483203939718,\"eventName\": \"Randomevent-1\"}]}";
        Entity<String> entity = Entity.json(eventSample);
        logger.info("Calling test on " + target("events").getUri().toString());
        int httpReturnCode = target("events").request().header("x-device-id", "123").
                header(HttpHeaders.CONTENT_ENCODING, "application/gzip").post(entity).getStatus();
        Assert.assertEquals(400, httpReturnCode);
    }

    @Override
    protected Application configure() {
        logger.info("Invoking Application Configure method and registering classes");
        final ResourceConfig rc = new ResourceConfig().packages("com.azure.ps.config", "com.azure.ps.interceptors");
        rc.register(JerseyPropertiesFeature.class);
        rc.property(JerseyPropertiesFeature.RESOURCE_PATH, "application.properties");
        rc.register(GzipReaderInterceptor.class);
        rc.register(new AbstractBinder() {
            @Override
            public void configure() {
                bindFactory(EventHubClientFactory.class).to(EventHubClient.class).in(RequestScoped.class);
            }
        });
        rc.register(GZipEncoder.class);
        rc.register(EncodingFilter.class);
        rc.property(ClientProperties.USE_ENCODING, "gzip");

        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        return rc;
    }

    @Override
    public TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }

    @Override
    protected DeploymentContext configureDeployment() {

        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put("jersey.config.server.provider.packages", "com.azure.ps.config,com.ps.azure.interceptors,com.azure.ps.controllers");
        initParams.put("com.sun.jersey.spi.container.ContainerRequestFilters", "com.ps.azure.interceptors.GzipRequestFilter");
        initParams.put("javax.ws.rs.Application", "com.azure.ps.config.AppConfig");


        DeploymentContext context = ServletDeploymentContext.
                forPackages("com.azure.ps.controllers,com.azure.ps.config").
                contextPath("/telemetry").
                initParams(initParams).
                build();
        logger.info("Invoking configureDeployment");
        return context;
    }
}
