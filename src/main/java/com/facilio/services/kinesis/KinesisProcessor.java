package com.facilio.services.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.dataprocessor.DataProcessorUtil;
import com.facilio.services.procon.message.FacilioRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;


public class KinesisProcessor implements IRecordProcessor {
    private static final Logger LOGGER = LogManager.getLogger(KinesisProcessor.class.getName());

    private String orgDomainName;
    private String shardId;
    private String errorStream;
    private final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
    private final DataProcessorUtil dataProcessorUtil;

    private final JSONParser parser = new JSONParser();

    KinesisProcessor(long orgId, String orgDomainName){
        this.orgDomainName = orgDomainName;
        this.errorStream = orgDomainName + "-error";
        dataProcessorUtil = new DataProcessorUtil(orgId, orgDomainName);
    }

    private void sendToKafka(Record record, String data) {
        JSONObject dataMap = new JSONObject();
        try {
            dataMap.put("timestamp", ""+record.getApproximateArrivalTimestamp().getTime());
            dataMap.put("key", record.getPartitionKey());
            dataMap.put("data", data);
            dataMap.put("sequenceNumber", record.getSequenceNumber());
            ErrorDataProducer.send(new ProducerRecord<>(errorStream, record.getPartitionKey(), dataMap.toString()));
        } catch (Exception e) {
            LOGGER.info(errorStream + " : " + dataMap);
            LOGGER.info("Exception while producing to kafka ", e);
        }
    }

    @Override
    public void initialize(InitializationInput initializationInput) {
        Thread thread = Thread.currentThread();
        String threadName = orgDomainName +"-processor";
        thread.setName(threadName);
        this.shardId = initializationInput.getShardId();
    }

    public void toDataProcessor(ProcessRecordsInput processRecordsInput){
        for (Record record : processRecordsInput.getRecords()) {
            FacilioRecord facilioRecord = toFacilioRecord(record);
            if( facilioRecord != null){
                try{
                    if( dataProcessorUtil.processRecord(facilioRecord)) {
                        try {
                            processRecordsInput.getCheckpointer().checkpoint(DataProcessorUtil.getLastRecordChecked());
                        } catch (Exception e) {
                            LOGGER.info("Exception occurred while changing checkpoint", e);
                        }
                    } else {
                        LOGGER.info(record.getPartitionKey() + " processing record failed " + record.getSequenceNumber());
                    }
                } catch (Exception e) {
                    LOGGER.info("Exception occurred while processing record " + record.getSequenceNumber() + " ",e);
                }
            }
        }
    }

    public FacilioRecord toFacilioRecord(Record record) {
        if(record != null) {
            try {
                JSONObject jsonData = byteDataToJSON( record.getData());
                long timestamp = record.getApproximateArrivalTimestamp().getTime();
                jsonData.put("timestamp", timestamp);
                jsonData.put("key", record.getPartitionKey());
                jsonData.put("sequenceNumber", record.getSequenceNumber());
                FacilioRecord facilioRecord = new FacilioRecord(record.getPartitionKey(), jsonData);
                facilioRecord.setTimeStamp(timestamp);
                facilioRecord.setId(record.getSequenceNumber());
                return facilioRecord;
            } catch (Exception e) {
                LOGGER.info("Exception occurred while converting kinesis record to facilio record ", e);
            }
        }
        return null;
    }

    private JSONObject byteDataToJSON(ByteBuffer byteData) throws Exception {
        if ((byteData != null)) {
            return (JSONObject) parser.parse(decoder.decode(byteData).toString());
        } else {
            throw new Exception("ByteData can't be null");
        }
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        toDataProcessor(processRecordsInput);
    }

    @Override
    public void shutdown(ShutdownInput shutdownInput) {
        LOGGER.info("Shutting down record processor for stream: "+ orgDomainName +" and shard: " + shardId);
    }
}
