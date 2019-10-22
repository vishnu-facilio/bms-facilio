package com.facilio.queue;

import java.util.List;

import com.facilio.queue.service.FacilioDbQueue;
import com.facilio.queue.service.FacilioQueueService;
import com.facilio.queue.service.QueueMessage;
import com.facilio.service.FacilioService;
public class FacilioQueueException {
	
	private static final FacilioQueueService INSTANCE = new FacilioDbQueue("FacilioExceptionQueue",true);

	public static boolean addException(String queueName, String message) throws Exception {
		return  FacilioService.runAsServiceWihReturn(() ->INSTANCE.push(queueName, message));
	}

	public static QueueMessage pull(String queueName) throws Exception{
		return FacilioService.runAsServiceWihReturn(() ->INSTANCE.pull(queueName));
	}
	
	public static List<QueueMessage> pull(String queueName, int limit) throws Exception{
		return FacilioService.runAsServiceWihReturn(() ->INSTANCE.pull(queueName, limit));
	}		

	public static void deleteQueue(String queueName, String messageId) throws Exception {
		FacilioService.runAsService(() ->INSTANCE.delete(queueName, messageId));
	}

	public static boolean changeVisibilityTimeout(String queueName, String receiptHandle, int visibilityTimeout) throws Exception {
		return FacilioService.runAsServiceWihReturn(() ->INSTANCE.changeVisibilityTimeout(queueName, receiptHandle, visibilityTimeout));
	}
}
