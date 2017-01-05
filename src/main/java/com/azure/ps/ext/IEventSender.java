package com.azure.ps.ext;

/**
 * Created by adithya on 1/1/17.
 */

import com.microsoft.azure.eventhubs.EventData;

import java.util.concurrent.CompletableFuture;

public interface IEventSender {
    CompletableFuture<Void> send(final Iterable<EventData> edatas);

    CompletableFuture<Void> send(final Iterable<EventData> edatas, final String partitionKey);
}