package com.facilio.queue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	public FacilioObjectQueue(String tableName, String dataTableName) {
		queueInstance = new FacilioDbQueue(tableName,dataTableName);
	}

	private static final String TABLE_QUEUE_OLD = "FacilioInstantJobQueue";
	private static final String TABLE_QUEUE_NEW = "FacilioInstantJobQueue_Data";
	public boolean sendMessage(Serializable serializable) throws Exception {
		if (serializable == null) {
			return false;
		}
		String serializedString = Base64.encodeAsString(SerializationUtils.serialize(serializable));
		long orgId = AccountUtil.getCurrentOrg().getId();
		return FacilioService.runAsServiceWihReturn(() -> queueInstance.push(serializedString,orgId));
	}

	public List<ObjectMessage> getObjects( int limit) throws Exception {
		List<QueueMessage> receivedMessages = FacilioService
				.runAsServiceWihReturn(() -> queueInstance.pull(limit));
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
			objectMessage.setReceiptHandle(message.getReceiptHandle());
			serializableList.add(objectMessage);
		}
		return serializableList;
	}

	public void deleteObject( String receiptHandle) throws Exception {
		FacilioService.runAsService(() -> queueInstance.delete(receiptHandle));
	}

	public boolean changeVisibilityTimeout( String receiptHandle, int visibilityTimeout)
			throws Exception {
		return FacilioService.runAsServiceWihReturn(
				() -> queueInstance.changeVisibilityTimeout( receiptHandle, visibilityTimeout));
	}
}
