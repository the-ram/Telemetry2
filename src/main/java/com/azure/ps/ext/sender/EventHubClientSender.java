package com.azure.ps.ext.sender;

/**
 * Created by adithya on 3/1/17.
 */

import com.github.psamsotha.jersey.properties.Prop;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class EventHubClientSender implements IEventSender {

    private final Logger logger = LoggerFactory.getLogger(EventHubClientSender.class);
    EventHubClient eventHubClient = null;
    @Prop("config.eventhub.namespaceName")
    private String eventHubNamespace;
    @Prop("config.eventhub.eventHubName")
    private String eventHubName;
    @Prop("config.eventhub.sasKeyName")
    private String sasKeyName;
    @Prop("config.eventhub.sasKey")
    private String sasKey;

    public EventHubClientSender(final EventHubClient eventHubClient) {
        this.eventHubClient = eventHubClient;
    }

    @Override
    public CompletableFuture<Void> send(Iterable<EventData> edatas, String partitionKey) {
        return this.eventHubClient.send(edatas, partitionKey);
    }

    @Override
    public CompletableFuture<Void> send(Iterable<EventData> edatas) {
        return this.eventHubClient.send(edatas);
    }
}
