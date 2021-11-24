package com.facilio.services.messageQueue;

import java.util.*;

import com.facilio.fw.FacilioException;

import com.facilio.agentv2.AgentConstants;
import com.facilio.queue.source.MessageSource;
import com.facilio.queue.source.MessageSourceUtil;

import lombok.extern.log4j.Log4j;

@Log4j
public class MessageQueueFactory {
	
	private static final Map<MessageSource, MessageQueue> messageQueues = new HashMap<>();
	private static int currentThreadCount = 0;
	public static void start() {
        try {
			List<Map<String, Object>> orgMessageTopics = MessageQueueTopic.getTopics(null, null);
			for (Map<String, Object> topicDetails : orgMessageTopics) {
				currentThreadCount = start(topicDetails,currentThreadCount);
			}
		} catch (Exception e) {
			LOGGER.info("Exception occurred on starting processor", e);
		}
	}

	public static int start(Map<String, Object> topicDetails,int currentThreadCount) throws Exception {

		String sourceName = (String) topicDetails.get(AgentConstants.MESSAGE_SOURCE);
		MessageSource source = MessageSourceUtil.getSource(sourceName);
		MessageQueue mq = messageQueues.get(source);
		boolean fetchStream = false;
		if (mq == null) {
			mq = getMessageQueueFromSource(source);
			fetchStream = true;
			messageQueues.put(source, mq);
		}
		return mq.startIfNotStarted(topicDetails, fetchStream,currentThreadCount);
	}
	
	public static MessageQueue getMessageQueue(MessageSource source) throws FacilioException {
		MessageQueue mq = messageQueues.get(source);
		if (mq == null) {
			mq = getMessageQueueFromSource(source);
			messageQueues.put(source, mq);
		}
		return mq;
	}
	
	private static MessageQueue getMessageQueueFromSource(MessageSource source) throws FacilioException {
		if (source.getType() == MessageSource.QueueType.KAFKA) {
			return new KafkaMessageQueue(source);
		}
		throw new FacilioException("Unknown source type");
	}

}
