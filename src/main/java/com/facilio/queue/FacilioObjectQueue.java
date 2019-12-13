package com.facilio.queue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;

import com.amazonaws.util.Base64;
import com.facilio.queue.service.FacilioDbQueue;
import com.facilio.queue.service.FacilioQueueService;
import com.facilio.queue.service.QueueMessage;
import com.facilio.service.FacilioService;

public class FacilioObjectQueue {
	private static final Logger LOGGER = Logger.getLogger(FacilioObjectQueue.class.getName());
	private static final String TABLE_OLD = "FacilioInstantJobQueue";
	private static final String NEW_TABLE_DATA ="FacilioInstantJobQueue_Data" ;
	private static final FacilioQueueService INSTANCE = new FacilioDbQueue(TABLE_OLD,NEW_TABLE_DATA,false);
	private static FacilioQueueService getInstance() {
		
		return INSTANCE;
	}
	public static boolean sendMessage(Serializable serializable) throws Exception {
		if (serializable == null) {
			return false;
		}
		String serializedString = Base64.encodeAsString(SerializationUtils.serialize(serializable));
		return FacilioService.runAsServiceWihReturn(() -> getInstance().push(serializedString));
	}

	public static List<ObjectMessage> getObjects( int limit) throws Exception {
		List<QueueMessage> receivedMessages = FacilioService
				.runAsServiceWihReturn(() -> getInstance().pull(limit));
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

	public static void deleteObject( String receiptHandle) throws Exception {
		FacilioService.runAsService(() -> getInstance().delete(receiptHandle));
	}

	public static boolean changeVisibilityTimeout(String receiptHandle, int visibilityTimeout)
			throws Exception {
		return FacilioService.runAsServiceWihReturn(
				() -> getInstance().changeVisibilityTimeout(receiptHandle, visibilityTimeout));
	}
}
