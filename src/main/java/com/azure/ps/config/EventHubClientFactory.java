package com.azure.ps.config;

import com.github.psamsotha.jersey.properties.Prop;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.servicebus.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.ServiceBusException;
import org.glassfish.hk2.api.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by adithya on 4/1/17.
 */
public class EventHubClientFactory implements Factory<EventHubClient> {
    private final Logger logger = LoggerFactory.getLogger(EventHubClientFactory.class);
    //EventHubClient eventHubClient = null;
    @Prop("config.eventhub.namespaceName")
    private String eventHubNamespace;
    @Prop("config.eventhub.eventHubName")
    private String eventHubName;
    @Prop("config.eventhub.sasKeyName")
    private String sasKeyName;
    @Prop("config.eventhub.sasKey")
    private String sasKey;

    @Override
    public EventHubClient provide() {
        EventHubClient client = null;
        try {
            String connectionString = new ConnectionStringBuilder(eventHubNamespace,
                    eventHubName, sasKeyName, sasKey).toString();
            logger.info("Connection string for connecting to hub " + connectionString);
            client = EventHubClient.createFromConnectionStringSync(connectionString);
            return client;

        } catch (ServiceBusException e) {
            logger.error("Error initializing sender " + e.toString());
            //e.printStackTrace();
            return client;
        } catch (IOException e) {
            logger.error("IO Error initializing sender " + e.toString());
            return client;
        }
    }

    @Override
    public void dispose(EventHubClient eventHubClient) {

    }
}
