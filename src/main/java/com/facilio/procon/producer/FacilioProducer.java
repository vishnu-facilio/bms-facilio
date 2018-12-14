package com.facilio.procon.producer;

import com.facilio.procon.FacilioRecord;

public interface FacilioProducer {

    Object putRecord(FacilioRecord record);
}
