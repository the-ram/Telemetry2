package com.azure.ps.ext.store;

import com.github.psamsotha.jersey.properties.Prop;

import javax.inject.Inject;


/**
 * Created by RGOVIND on 1/10/2017.
 */
@MemoryEventStore
public class MemoryEventStoreImpl implements IEventStore {

    private String receivedAtHour;
    private String partitionId;
    @Prop("config.memorystore.maxbuffer")
    private int maxBufferSize;
    private StringBuilder buffer;

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

        } else {
            buffer.append(eventData);
        }

    }

    private void flushToDisk() {
        String dataBlock = buffer.toString();
        //buffer.
    }
}
