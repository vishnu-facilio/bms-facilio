package com.facilio.services.messageQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.agentv2.AgentConstants;
import com.facilio.queue.source.MessageSource;
import com.facilio.queue.source.MessageSourceUtil;

import lombok.extern.log4j.Log4j;

@Log4j
public class MessageQueueFactory {
	
	private static Map<MessageSource, MessageQueue> messageQueues = new HashMap<>();

	public static void start() {
        try {
			List<Map<String, Object>> orgMessageTopics = MessageQueueTopic.getTopics(null, null);
			start(orgMessageTopics);
		} catch (Exception e) {
			LOGGER.info("Exception occurred on starting processor", e);
		}
	}

	// This is also used when a new message topic is added 
	public static void start(List<Map<String, Object>> messageTopics) {
		if (CollectionUtils.isNotEmpty(messageTopics)) {
			for(Map<String, Object> topicDetails: messageTopics) {
				String sourceName = (String) topicDetails.get(AgentConstants.MESSAGE_SOURCE);
				MessageSource source = MessageSourceUtil.getSource(sourceName);
				MessageQueue mq = messageQueues.get(source);
				boolean fetchStream = false;
				if (mq == null) {
					mq = getMessageQueueFromSource(source);
					messageQueues.put(source, mq);
					fetchStream = true;
				}
				mq.start(topicDetails, fetchStream);
			}
		}
	}
	
	public static MessageQueue getMessageQueue(MessageSource source) {
		MessageQueue mq = messageQueues.get(source);
		if (mq == null) {
			mq = getMessageQueueFromSource(source);
			messageQueues.put(source, mq);
		}
		return mq;
	}
	
	private static MessageQueue getMessageQueueFromSource(MessageSource source) {
		switch(source.getType()) {
			case KAFKA:
				return new KafkaMessageQueue(source);
		}
		return null;
	}

}
