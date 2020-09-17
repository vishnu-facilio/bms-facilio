package com.facilio.services.procon.consumer;

import java.util.List;

import com.facilio.services.procon.message.FacilioRecord;

public interface FacilioConsumer {

    List<FacilioRecord> getRecords();

    List<FacilioRecord> getRecords(long timeout);

    void commit(FacilioRecord record);

    void subscribe(String topic);
    
    void seek(String topic,long offset);
    
    void close();
}
