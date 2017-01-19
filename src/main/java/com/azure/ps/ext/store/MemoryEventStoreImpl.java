package com.azure.ps.ext.store;

import com.github.psamsotha.jersey.properties.Prop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


/**
 * Created by RGOVIND on 1/10/2017.
 */
@MemoryEventStore
public class MemoryEventStoreImpl implements IEventStore {

    private final Logger logger = LoggerFactory.getLogger(MemoryEventStoreImpl.class);
    private String receivedAtHour;
    private String partitionId;
    @Prop("config.memorystore.maxbuffer")
    private int maxBufferSize;
    private StringBuilder buffer;
    private Semaphore semaphore = new Semaphore(1, true);
    @Inject
    @DiskEventStore
    private IEventStore nextStore;

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public String getReceivedAtHour() {
        return receivedAtHour;
    }

    @Override
    public String getPartitionId() {
        return partitionId;
    }

    @Override
    public void initialise(String partitionId, String receivedAtHour) {
        this.partitionId = partitionId;
        this.receivedAtHour = receivedAtHour;
        this.buffer = new StringBuilder(maxBufferSize);
        if (nextStore != null) {
            nextStore.initialise(partitionId, receivedAtHour);
        }

    }

    @Override
    public void write(byte[] value) {
        int byteCount = value.length;
        String eventData = new String(value);

        if (buffer.length() + byteCount > maxBufferSize) {
            flushToDisk();
        }
        try {
            semaphore.wait();
            buffer.append(eventData);
        } catch (InterruptedException iex) {
            logger.error("Exception in acquiring lock in write {} ", iex.getMessage());
        } finally {
            //release the semaphore
            semaphore.release();
        }


    }
    //TODO encode the string to UTF-8

    private void flushToDisk() {
        String dataBlock = new String();
        if (buffer != null && buffer.length() > 0) {
            try {
                logger.debug("Performing write into disk from memory event store");
                semaphore.acquire();
                dataBlock = buffer.toString();
                //clear the buffer
                buffer.setLength(0);

            } catch (InterruptedException iex) {
                logger.error("Exception in acquiring lock in flushToDisk {} ", iex.getMessage());
            } finally {
                //release the semaphore
                semaphore.release();
            }
            final String finalDataBlock = dataBlock;
            Executors.newSingleThreadExecutor().submit(() -> {
                nextStore.write(finalDataBlock.getBytes());
            });

        }
    }
}
