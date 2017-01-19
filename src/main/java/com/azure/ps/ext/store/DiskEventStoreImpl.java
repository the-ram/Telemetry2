package com.azure.ps.ext.store;

import com.github.psamsotha.jersey.properties.Prop;
import com.google.common.io.Files;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.UUID;

/**
 * Created by RGOVIND on 1/12/2017.
 */
public class DiskEventStoreImpl implements IEventStore {


    private String receivedAtHour;
    private String partitionId;
    @Prop("config.memorystore.maxbuffer")
    private int maxBufferSize;
    private StringBuilder buffer;
    @Prop("config.filestore.temppath")
    private String tempFolderPath;

    private String gzipFilePath;

    @Override
    public int getLevel() {
        return 2;
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
        File eventFile = null;
        this.partitionId = partitionId;
        this.receivedAtHour = receivedAtHour;
        this.buffer = new StringBuilder(maxBufferSize);
        String tempFileName = partitionId + '-' + receivedAtHour + '-' +
                UUID.randomUUID().toString().substring(4) + ".json.gz";
        if (tempFolderPath == null) {
            File tempFolder = Files.createTempDir();
            gzipFilePath = tempFolder + "/" + tempFileName;
            eventFile = new File(tempFolder, tempFileName);
        } else {
            gzipFilePath = tempFolderPath + "/" + tempFileName;
            eventFile = new File(tempFolderPath, tempFileName);

        }
    }

    @Override
    public void write(byte[] value) {
        try (ByteArrayInputStream bytesToAppend = new ByteArrayInputStream(value);
             FileInputStream existingGzipFileInputStream = new FileInputStream(gzipFilePath);
             FileOutputStream addedGzipFileOutputStream = new FileOutputStream(gzipFilePath);
             CompressorOutputStream gzipOutputStream = new CompressorStreamFactory().
                     createCompressorOutputStream(CompressorStreamFactory.GZIP, addedGzipFileOutputStream);
             BufferedWriter writer = new BufferedWriter(
                     new OutputStreamWriter(gzipOutputStream, "UTF-8"));
        ) {
            IOUtils.copy(existingGzipFileInputStream, gzipOutputStream);
            writer.append(new String(value));
            writer.newLine();

        } catch (Exception ioEx) {

        }
    }
}
