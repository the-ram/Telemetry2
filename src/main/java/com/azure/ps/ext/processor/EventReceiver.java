package com.azure.ps.ext.processor;

import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.EventProcessorOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by adithya on 8/1/17.
 */
public class EventReceiver {

    private final Logger logger = LoggerFactory.getLogger(EventReceiver.class);
    private EventProcessorHost eventProcessorHost;


    private String hostName;
    private String eventHubPath;
    private String consumerGroupName;
    private String eventHubConnectionString;
    private String storageConnectionString;
    private String storageContainerName;
    private String storageBlobPrefix;


    public EventReceiver() {
        logger.trace("Event Receiver Parameters");
        logger.trace("Hostname : " + hostName);
        logger.trace("Event Hub Path : " + eventHubPath);
        logger.trace("Consumer Group Name : " + consumerGroupName);
        logger.trace("Event Hub Connection String : " + eventHubConnectionString);
        logger.trace("Storage Connection String : " + storageConnectionString);
        logger.trace("Storage Container Name : " + storageContainerName);
        logger.trace("Storage Blob Prefix : " + storageBlobPrefix);

        eventProcessorHost = new EventProcessorHost(hostName,
                eventHubPath,
                consumerGroupName,
                eventHubConnectionString,
                storageConnectionString,
                storageContainerName,
                storageBlobPrefix);
    }

    public void registerProcessorAsync() {
        try {
            logger.debug("Starting processor registration");
            //TODO to find out what is this batch size and prefetch
            EventProcessorOptions eventProcessorOptions = new EventProcessorOptions();
            eventProcessorOptions.setMaxBatchSize(5000);
            eventProcessorOptions.setPrefetchCount(1000);
            eventProcessorHost.registerEventProcessor(DeepStorageEventProcessor.class,
                    eventProcessorOptions);
            logger.debug("Processor registration complete");
        } catch (Exception exception) {
            logger.error("Error while registering event processor ");
            logger.error("Exception detail " + exception);
        }
    }

}
