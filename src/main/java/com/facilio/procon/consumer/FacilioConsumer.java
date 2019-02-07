package com.facilio.procon.consumer;

import java.util.List;

import com.facilio.procon.message.FacilioRecord;

public interface FacilioConsumer {

    List<FacilioRecord> getRecords();

    List<FacilioRecord> getRecords(long timeout);

    void commit(FacilioRecord record);

    void subscribe(String topic);

    void close();
}
