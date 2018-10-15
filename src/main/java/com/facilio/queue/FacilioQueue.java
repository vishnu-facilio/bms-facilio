package com.facilio.queue;

import java.util.List;

public interface FacilioQueue {

    public boolean push(String queueName, String message);
    public QueueMessage pull(String queueName);
    public void delete(String queueName, String messageId);
    public List<QueueMessage> pull(String queueName, int limit);
    public boolean changeVisibilityTimeout(String queueName, String receiptHandle, int visibilityTimeout);

}
