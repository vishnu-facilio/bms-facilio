package com.facilio.services.procon.producer;

import com.facilio.services.procon.message.FacilioRecord;

public interface FacilioProducer {

    Object putRecord(FacilioRecord record);

    void close();
}
