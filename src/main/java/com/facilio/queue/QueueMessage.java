package com.facilio.queue;

import java.io.Serializable;

import com.amazonaws.services.sqs.model.Message;

public class QueueMessage {

    private String receiptHandle;
    private Serializable object;
    private Message message;

    public QueueMessage(String receiptHandle, Serializable object) {
        this(receiptHandle, object, null);
    }

    public QueueMessage(String receiptHandle, Serializable object, Message message) {
        this.receiptHandle = receiptHandle;
        this.object = object;
        this.message = message;
    }

    public String getReceiptHandle() {
        return receiptHandle;
    }

    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }

    public Serializable getObject() {
        return object;
    }

    public void setObject(Serializable object) {
        this.object = object;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
