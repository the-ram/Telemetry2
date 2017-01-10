package com.azure.ps.ext.store;

/**
 * Created by adithya on 8/1/17.
 */
public interface IEventStore {

    int level = 0;
    String receivedAtHour = null;
    String partitionId = null;

    int getLevel();

    String getReceivedAtHour();

    String getPartitionId();
    void initialise(String partitionId, String receivedAtHour);
    void write(byte[] value);


}
