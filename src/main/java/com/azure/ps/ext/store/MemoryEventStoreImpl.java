package com.azure.ps.ext.store;

import com.github.psamsotha.jersey.properties.Prop;


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
    }

    @Override
    public void write(byte[] value) {

    }
}
