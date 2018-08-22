package com.facilio.wms.message;

import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class NotificationProcessorCheckpointer implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(NotificationProcessorCheckpointer.class.getName());
    private ConcurrentHashMap<String, IRecordProcessorCheckpointer> checkpointerMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> processedMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> updatedMap = new ConcurrentHashMap<>();

    void addCheckPointer(String shardId, IRecordProcessorCheckpointer checkpointer) {
        checkpointerMap.put(shardId, checkpointer);
    }

    void addProcessedSequenceNumber(String shardId, String sequenceNumber) {
        processedMap.put(shardId, sequenceNumber);
    }

    public void run() {
        while(true) {
            for(Map.Entry<String, String> entry : processedMap.entrySet()) {
                String recordSequenceNumber = entry.getValue();
                String updatedSequenceNumber = "";
                if(updatedMap.containsKey(entry.getKey())) {
                    updatedSequenceNumber = updatedMap.get(entry.getKey());
                }
                try {
                    if(updatedSequenceNumber.equals(recordSequenceNumber)) {
                        Thread.sleep(5000L);
                    } else {
                        try {
                            checkpointerMap.get(entry.getKey()).checkpoint(recordSequenceNumber);
                            updatedMap.put(entry.getKey(), recordSequenceNumber);
                            Thread.sleep(500L); // Write capacity of dynamoDB is 1 write/sec
                        } catch (InvalidStateException | ShutdownException e) {
                            LOGGER.info("Exception occurred ", e);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.info("InterruptedException ", e);
                }
            }
        }

    }
}
