package com.facilio.queue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.facilio.constants.FacilioConstants;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;

import com.amazonaws.util.Base64;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.queue.service.FacilioDbQueue;
import com.facilio.queue.service.FacilioQueueService;
import com.facilio.queue.service.QueueMessage;
import com.facilio.service.FacilioService;

public class FacilioObjectQueue {
	private static final Logger LOGGER = Logger.getLogger(FacilioObjectQueue.class.getName());

	private FacilioQueueService queueInstance = null;
	public FacilioObjectQueue(String tableName) {
		this(tableName, null, null);
	}
	public FacilioObjectQueue(String tableName, List<Long> includeOrg, List<Long> excludeOrg) {
		queueInstance = new FacilioDbQueue(tableName, includeOrg, excludeOrg);
	}

	public boolean sendMessage(Serializable serializable) throws Exception {
		if (serializable == null) {
			return false;
		}
		String serializedString = Base64.encodeAsString(SerializationUtils.serialize(serializable));
		if (AccountUtil.getCurrentOrg() != null) {
			long orgId = AccountUtil.getCurrentOrg().getOrgId();
			return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.INSTANT_JOB_SERVICE,() -> queueInstance.push(serializedString, orgId));
		}
		else {
			return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.INSTANT_JOB_SERVICE,() -> queueInstance.push(serializedString,-1l));
		}
	}

	public List<ObjectMessage> getObjects( int limit) throws Exception {
		List<QueueMessage> receivedMessages = FacilioService
				.runAsServiceWihReturn(FacilioConstants.Services.INSTANT_JOB_SERVICE,() -> queueInstance.pull(limit));
		List<ObjectMessage> serializableList = new ArrayList<>();
		for (QueueMessage message : receivedMessages) {
			String serialized = message.getMessage();
			ObjectMessage objectMessage = new ObjectMessage(message.getId(), message.getMessage());
			Object deserializedObject = null;
			try {
				if (serialized != null) {
					deserializedObject = SerializationUtils.deserialize(Base64.decode(serialized));
				}
			} catch (Exception e) {
				LOGGER.info("Exception while deserializing msg :  " + serialized);
			}
			objectMessage.setSerializable(deserializedObject);
			objectMessage.setReceiptHandle(message.getId());
			serializableList.add(objectMessage);
		}
		return serializableList;
	}

	public void deleteObject( String receiptHandle) throws Exception {
		FacilioService.runAsService(FacilioConstants.Services.INSTANT_JOB_SERVICE,() -> queueInstance.delete(receiptHandle));
	}

	public boolean changeVisibilityTimeout( String receiptHandle, int visibilityTimeout)
			throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.INSTANT_JOB_SERVICE,
				() -> queueInstance.changeVisibilityTimeout( receiptHandle, visibilityTimeout));
	}

	public void delete(long ttime)throws Exception{
		try {
			queueInstance.deleteQueue(ttime);
		}catch(Exception e) {
			throw e;
		}
	}
}
