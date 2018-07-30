package com.facilio.queue;

import java.util.List;

public interface FacilioQueue {

    public void push(String queueName, String message);
    public QueueMessage pull(String queueName);
    public void delete(String queueName, String messageId);
    public List<QueueMessage> pull(String queueName, int limit);

}
