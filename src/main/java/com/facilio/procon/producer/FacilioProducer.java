package com.facilio.procon.producer;

import com.facilio.procon.message.FacilioRecord;

public interface FacilioProducer {

    Object putRecord(FacilioRecord record);
}
