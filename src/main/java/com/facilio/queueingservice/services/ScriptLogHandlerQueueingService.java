package com.facilio.queueingservice.services;

import java.util.List;

import com.facilio.queueingservice.FacilioQueueingServiceAnnotation;
import com.facilio.services.procon.message.FacilioRecord;

import lombok.Getter;
import lombok.Setter;

@FacilioQueueingServiceAnnotation (
	consumerGroup = ScriptLogHandlerQueueingService.CONSUMER_GROUP, 
	topic = ScriptLogHandlerQueueingService.TOPIC
)
@Getter
@Setter
public class ScriptLogHandlerQueueingService extends QueueingService {
	public static final String TOPIC = "script-log";
	public static final String CONSUMER_GROUP = "script-log-consumer";
	
	@Override
	public void processRecords(List<FacilioRecord> records) {
		// TODO Auto-generated method stub
		for (FacilioRecord record : records) {
			System.out.println("ScriptLogHandlerQueueingService record --- "+record.getData());
		}
	}
}
