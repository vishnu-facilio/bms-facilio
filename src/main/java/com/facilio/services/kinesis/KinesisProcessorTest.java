package com.facilio.services.kinesis;

import com.amazonaws.services.kinesis.model.Record;
import com.facilio.services.procon.message.FacilioRecord;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

class KinesisProcessorTest {


    private Record getRecord(){
        Date date =  new Date();
        Record record = new Record();
        record.setSequenceNumber("sequenceNumber");
        record.setPartitionKey("1");
        record.setApproximateArrivalTimestamp(date);
        return record;
    }


    @Test
    void testToFacilioRecord(){
        Record record = getRecord();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("timestamp", "" + record.getApproximateArrivalTimestamp().getTime());
        jsonObject.put("key", record.getPartitionKey());
        jsonObject.put("sequenceNumber", record.getSequenceNumber());
        FacilioRecord facilioRecord = new FacilioRecord(record.getPartitionKey(), jsonObject);
        facilioRecord.setId(record.getSequenceNumber());
        FacilioRecord newFacilioRecord = KinesisProcessor.toFacilioRecord(record);

        Assertions.assertEquals(facilioRecord.getId(),newFacilioRecord.getId());
        Assertions.assertEquals(facilioRecord.getData(),newFacilioRecord.getData());
        Assertions.assertEquals(facilioRecord.getPartitionKey(),newFacilioRecord.getPartitionKey());
        Assertions.assertEquals(facilioRecord.getTimeStamp(),newFacilioRecord.getTimeStamp());

    }

    @Test
    void testToFacilioRecotdNull(){
        Assertions.assertEquals(null,KinesisProcessor.toFacilioRecord(null));
    }
}