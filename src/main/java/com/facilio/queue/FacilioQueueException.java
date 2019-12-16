package com.facilio.queue;

import java.util.List;

import com.facilio.queue.service.FacilioDbQueue;
import com.facilio.queue.service.FacilioQueueService;
import com.facilio.queue.service.QueueMessage;
import com.facilio.service.FacilioService;
public class FacilioQueueException {

	private static final String TABLE_QUEUE_OLD = "FacilioExceptionQueue";
	private static final String TABLE_QUEUE_NEW = "FacilioExceptionQueue_Data";
	private static final FacilioQueueService INSTANCE = new FacilioDbQueue(TABLE_QUEUE_OLD,TABLE_QUEUE_NEW);
	static FacilioQueueService getInstance() {
		return INSTANCE;
	}
	public static boolean addException(String message) throws Exception {
		return  FacilioService.runAsServiceWihReturn(() ->getInstance().push(message));
	}

//	public static QueueMessage pull() throws Exception{
//		return FacilioService.runAsServiceWihReturn(() ->getInstance().pull());
//	}
	
	public static List<QueueMessage> pull(int limit) throws Exception{
		return FacilioService.runAsServiceWihReturn(() ->getInstance().pull(limit));
	}		

	public static void deleteQueue(String messageId) throws Exception {
		FacilioService.runAsService(() ->getInstance().delete(messageId));
	}

	public static boolean changeVisibilityTimeout(String receiptHandle, int visibilityTimeout) throws Exception {
		return FacilioService.runAsServiceWihReturn(() ->getInstance().changeVisibilityTimeout(receiptHandle, visibilityTimeout));
	}
}
