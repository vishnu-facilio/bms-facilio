package com.facilio.procon.consumer;

import com.facilio.procon.FacilioRecord;

import java.util.List;

public interface FacilioConsumer {

    List<FacilioRecord> getRecords(String topic);

    List<FacilioRecord> getRecords(long timeout);

    void commit(FacilioRecord record);
}
