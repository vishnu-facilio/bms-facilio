package com.facilio.queue;

import java.util.List;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.queue.service.FacilioDbQueue;
import com.facilio.queue.service.FacilioQueueService;
import com.facilio.queue.service.QueueMessage;
import com.facilio.service.FacilioService;
public class FacilioQueueException {

	private static final String TABLE_NAME = "ExceptionQueue";
	private static final FacilioQueueService INSTANCE = new FacilioDbQueue(TABLE_NAME);
	static FacilioQueueService getInstance() {
		return INSTANCE;
	}
	public static boolean addException(String fromClass,String message) throws Exception {
		long orgId = AccountUtil.getCurrentOrg().getId();
		return  FacilioService.runAsServiceWihReturn(FacilioConstants.Services.INSTANT_JOB_SERVICE,() ->getInstance().push(fromClass,message,orgId));
	}

	public static List<QueueMessage> pull(int limit) throws Exception{
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.INSTANT_JOB_SERVICE,() ->getInstance().pull(limit));
	}

	public static void deleteQueue(String messageId) throws Exception {
		FacilioService.runAsService(FacilioConstants.Services.INSTANT_JOB_SERVICE,() ->getInstance().delete(messageId));
	}

	public static boolean changeVisibilityTimeout(String receiptHandle, int visibilityTimeout) throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.INSTANT_JOB_SERVICE,() ->getInstance().changeVisibilityTimeout(receiptHandle, visibilityTimeout));
	}

	public static void deleteExceptionQueue(long deleteTime) throws Exception {
		getInstance().deleteQueue(deleteTime);
	}
}

