package com.facilio.queueingservice;

import java.util.List;

import com.facilio.queue.source.KafkaMessageSource;
import com.facilio.queueingservice.services.QueueingService;
import com.facilio.server.ServerInfo;
import com.facilio.services.kafka.FacilioKafkaConsumer;
import com.facilio.services.kafka.FacilioKafkaProducer;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.processor.FacilioProcessor;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class FacilioQueueingServiceKafkaProcessor extends FacilioProcessor {
	private QueueingService queueingService;
	public FacilioQueueingServiceKafkaProcessor(@NonNull String topic,@NonNull String consumerGroup, @NonNull KafkaMessageSource source,@NonNull QueueingService queueingService) {
		super(-1, topic);
		this.queueingService = queueingService;
		setConsumer(new FacilioKafkaConsumer(ServerInfo.getHostname(), consumerGroup, getTopic(), source));
		setProducer(new FacilioKafkaProducer(source));
        setEventType("processor");
	}

	@Override
	protected String getThreadName() {
		return getTopic();
	}

	@Override
	public void processRecords(List<FacilioRecord> records) {
		// TODO Auto-generated method stub
		queueingService.processRecords(records);
		for (FacilioRecord record : records) {
			getConsumer().commit(record);
		}
	}

}
