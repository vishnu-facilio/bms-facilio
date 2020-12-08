package com.facilio.services.procon.producer;

import com.facilio.services.procon.message.FacilioRecord;

public interface FacilioProducer {

    Object putRecord(String topic, FacilioRecord record) throws Exception;

    void close();
}
