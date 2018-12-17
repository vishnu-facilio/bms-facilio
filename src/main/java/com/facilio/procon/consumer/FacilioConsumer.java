package com.facilio.procon.consumer;

import com.facilio.procon.message.FacilioRecord;

import java.util.List;

public interface FacilioConsumer {

    List<FacilioRecord> getRecords();

    List<FacilioRecord> getRecords(long timeout);

    void commit(FacilioRecord record);

    void subscribe(String topic);

    void close();
}
