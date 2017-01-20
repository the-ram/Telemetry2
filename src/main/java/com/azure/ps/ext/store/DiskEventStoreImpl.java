package com.azure.ps.ext.store;

import com.github.psamsotha.jersey.properties.Prop;
import com.google.common.io.Files;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.zip.GZIPOutputStream;

/**
 * Created by RGOVIND on 1/12/2017.
 */
public class DiskEventStoreImpl implements IEventStore {

    private final Logger logger = LoggerFactory.getLogger(DiskEventStoreImpl.class);
    private Semaphore semaphore = new Semaphore(1, true);

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
        try (CompressorInputStream gzipInputStream = new CompressorStreamFactory()
                .createCompressorInputStream(CompressorStreamFactory.GZIP, new FileInputStream(gzipFilePath));
             CompressorOutputStream gzipOutputStream = new CompressorStreamFactory()
                     .createCompressorOutputStream(CompressorStreamFactory.GZIP, new FileOutputStream(gzipFilePath, true))){

            semaphore.acquire();
            IOUtils.copy(gzipInputStream, gzipOutputStream);
            gzipOutputStream.write(value);

        }catch(Exception ioEx){
            logger.error("Exception while appending file {} " ,ioEx.getMessage());
        }finally {
            semaphore.release();
        }

    }
}
