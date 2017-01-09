package com.azure.ps.ext.processor;

import com.azure.ps.ext.store.IEventStore;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventprocessorhost.CloseReason;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.PartitionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by adithya on 8/1/17.
 */
public class DeepStorageEventProcessor implements IEventProcessor {
    private static ConcurrentMap<String, IEventStore> keyStoreMap = new ConcurrentHashMap<String, IEventStore>();
    private final Logger logger = LoggerFactory.getLogger(DeepStorageEventProcessor.class);

    @Override
    public void onOpen(PartitionContext partitionContext) throws Exception {
        logger.trace("On Open in processor event hub path {} ", partitionContext.getEventHubPath());
        logger.trace("On Open in processor partition id {} ", partitionContext.getPartitionId());
    }

    @Override
    public void onClose(PartitionContext partitionContext, CloseReason closeReason) throws Exception {
        logger.trace("On Close in processor event hub path {} ", partitionContext.getEventHubPath());
        logger.trace("On Close in processor partition id {} ", partitionContext.getPartitionId());
        logger.trace("On Close in processor close reason {} ", closeReason.name());

    }

    @Override
    public void onEvents(PartitionContext partitionContext, Iterable<EventData> iterable) throws Exception {
        logger.trace("On Event in processor event hub path {} ", partitionContext.getEventHubPath());
        logger.trace("On Event in processor partition id  {} ", partitionContext.getPartitionId());

        for (EventData eventData : iterable) {
            String message = new String(eventData.getBody());
            logger.trace("Event message is {} ", message);
            logger.trace("Event message offset is {} ", eventData.getBodyOffset());
        }
    }

    @Override
    public void onError(PartitionContext partitionContext, Throwable throwable) {
        logger.trace("On Error in processor event hub path {} ", partitionContext.getEventHubPath());
        logger.trace("On Error in processor partition id {} ", partitionContext.getPartitionId());

    }

    private IEventStore getEventStore(EventData eventData, String partitionId) {
        String message = new String(eventData.getBody());
        Map<String, String> properties = eventData.getProperties();
        String epochReceivedAt = properties.get("receivedAt");
        return null;
    }
}
