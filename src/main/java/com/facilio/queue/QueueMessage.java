package com.facilio.queue;

public class QueueMessage {

    private String message;
    private final String id;
    private long visibilityTimeout;
    private String receiptHandle;

    public QueueMessage(String id, String message){
        this.message = message;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public long getVisibilityTimeout() {
        return visibilityTimeout;
    }

    void setVisibilityTimeout(long visibilityTimeout) {
        this.visibilityTimeout = visibilityTimeout;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiptHandle() {
        return receiptHandle;
    }

    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }

    public String toString() {
        return getId() + ":" + getMessage() +":"+ getVisibilityTimeout();
    }
}
