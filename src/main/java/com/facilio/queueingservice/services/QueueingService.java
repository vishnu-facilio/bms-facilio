package com.facilio.queueingservice.services;

import java.util.List;

import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.processor.FacilioProcessor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class QueueingService {
	
	public abstract void processRecords(List<FacilioRecord> records);
}
