package com.facilio.queue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;

import com.amazonaws.util.Base64;

public class ObjectQueue {

    private static final Logger LOGGER = Logger.getLogger(ObjectQueue.class.getName());

    public static void sendMessage(String queueName, Serializable serializable) throws SerializationException {
        if (serializable == null) {
            return;
        }
        String serializedString = Base64.encodeAsString(SerializationUtils.serialize(serializable));
        QueueFactory.getQueue().push(queueName, serializedString);
    }

    public static List<ObjectMessage> getObjects(String queueName, int limit) {
        List<QueueMessage> receivedMessages = QueueFactory.getQueue().pull(queueName, limit);
        List<ObjectMessage> serializableList = new ArrayList<>();
        for (QueueMessage message: receivedMessages) {
            String serialized = message.getMessage();
            ObjectMessage objectMessage = new ObjectMessage(message.getId(), message.getMessage());
            Object deserializedObject = null;
            try {
                if(serialized != null) {
                    deserializedObject = SerializationUtils.deserialize(Base64.decode(serialized));
                }
            } catch (Exception e) {
                LOGGER.info("Exception while deserializing msg :  "+ serialized);
            }
            objectMessage.setSerializable(deserializedObject);
            objectMessage.setReceiptHandle(message.getReceiptHandle());
            serializableList.add(objectMessage);
        }
        return serializableList;
    }

    public static void deleteObject(String queueName, String receiptHandle) {
    	QueueFactory.getQueue().delete(queueName, receiptHandle);
    }
}
