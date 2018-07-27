package com.facilio.queue;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.util.Base64;
import org.apache.commons.lang.SerializationException;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ObjectQueue {

    private static final Logger LOGGER = Logger.getLogger(ObjectQueue.class.getName());

    public static void sendMessage(String queueName, Serializable serializable) throws SerializationException {
        if (serializable == null) {
            return;
        }
        String serializedString;
        ObjectOutputStream objectOutputStream = null;
        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(bytesOut);
            objectOutputStream.writeObject(serializable);
            objectOutputStream.flush();
            serializedString = Base64.encodeAsString(bytesOut.toByteArray());
            FAWSQueue.sendMessage(queueName, serializedString);
        } catch (IOException e) {
            LOGGER.error("IOException: cannot serialize objectMessage", e);
            throw new SerializationException("Unable to serialize Object :  " + serializable.toString());
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    LOGGER.warn(e.getMessage());
                }
            }
        }
    }

    public static List<QueueMessage> getObjects(String queueName) {
        List<Message> receivedMessages = FAWSQueue.receiveMessages(queueName);
        List<QueueMessage> serializableList = new ArrayList<>();
        for (Message message: receivedMessages) {
            String serialized = message.getBody();
            if (serialized == null) {
                return null;
            }
            Serializable deserializedObject;
            ObjectInputStream objectInputStream = null;
            try {
                byte[] bytes = Base64.decode(serialized);
                objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
                deserializedObject = (Serializable) objectInputStream.readObject();
                serializableList.add(new QueueMessage(message.getReceiptHandle(), deserializedObject));
            } catch (IOException e) {
                LOGGER.error("IOException: Message cannot be written", e);
            } catch (Exception e) {
                LOGGER.error("Unexpected exception: ", e);
            } finally {
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (IOException e) {
                        LOGGER.warn(e.getMessage());
                    }
                }
            }
        }
        return serializableList;
    }

    public static void deleteObject(String queueName, String reciptHandle) {
        FAWSQueue.deleteMessage(queueName, reciptHandle);
    }
}
