package com.azure.ps;

import com.azure.ps.ext.EventHubClientFactory;
import com.microsoft.azure.eventhubs.EventHubClient;
import junit.framework.Assert;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.message.GZipEncoder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;

@RunWith(MockitoJUnitRunner.class)
public class EventControllerTest {

    //private HttpServer server;
    private WebTarget target;

    @Mock
    private EventHubClientFactory eventHubClientFactory;

    @Before
    public void setUp() throws Exception {
        // start the server
        //server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();
        c.register(GZipEncoder.class);
        c.register(EncodingFilter.class);
        c.property(ClientProperties.USE_ENCODING, "gzip");
        //c.register(MultiPartFeature.class);
        //c.register(LoggingFilter.class);
        /*c.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(IEventSender).to(IEventSender.class);
            }
        });*/
        c.register(new AbstractBinder() {
            @Override
            public void configure() {
                bindFactory(EventHubClientFactory.class).to(EventHubClient.class);
                //.in(RequestScoped.class);
            }
        });
        MockitoAnnotations.initMocks(this);


        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        //server.shutdown();
    }

    @Test
    public void testCreated_RC201() {
        String eventSample = "{\"events\": [{\"deviceId\": \"123\",\"timestamp\": 1483203939718,\"eventName\": \"Randomevent-1\"}]}";
        Entity<String> entity = Entity.json(eventSample);
        int httpReturnCode = target.path("events").request().header("x-device-id", "123").
                header(HttpHeaders.CONTENT_ENCODING, "application/gzip").post(entity).getStatus();
        Assert.assertEquals(201, httpReturnCode);
    }

    @Test
    public void testServerError_RC500() {
        String eventSample = "{\"events\": {\"deviceId\": \"123\",\"timestamp\": 1483203939718,\"eventName\": \"Randomevent-1\"}";
        Entity<String> entity = Entity.json(eventSample);
        int httpReturnCode = target.path("events").request().header("x-device-id", "123").
                header(HttpHeaders.CONTENT_ENCODING, "application/gzip").post(entity).getStatus();
        Assert.assertEquals(500, httpReturnCode);
    }

    @Test
    public void testInvalidRequest_RC400() {
        String eventSample = "{\"event\": [{\"deviceId\": \"123\",\"timestamp\": 1483203939718,\"eventName\": \"Randomevent-1\"}]}";
        Entity<String> entity = Entity.json(eventSample);
        int httpReturnCode = target.path("events").request().header("x-device-id", "123").
                header(HttpHeaders.CONTENT_ENCODING, "application/gzip").post(entity).getStatus();
        Assert.assertEquals(400, httpReturnCode);
    }

}
