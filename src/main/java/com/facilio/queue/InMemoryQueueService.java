package com.facilio.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryQueueService implements FacilioQueue {
    private static final Map<String, InMemoryQueue> MAP = new ConcurrentHashMap<>();
    private static final long TIME = System.currentTimeMillis();
    private static final QueueMessage EMPTY_MSG = new QueueMessage("","");
    private static final FacilioQueue INSTANCE = new InMemoryQueueService();
    private static final long DEFAULT_VISIBILITY = 3000L;

    private InMemoryQueueService(){
    }

    private InMemoryQueue getOrCreateQueue(String queueName) {
        if( ! MAP.containsKey(queueName)) {
            MAP.put(queueName, new InMemoryQueue());
        }
        return MAP.get(queueName);
    }

    @Override
    public boolean push(String queueName, String message) {
        InMemoryQueue inMemoryQueue = getOrCreateQueue(queueName);
        inMemoryQueue.push(message);
        return true;
    }

    @Override
    public QueueMessage pull(String queueName) {
        InMemoryQueue inMemoryQueue = getOrCreateQueue(queueName);
        return inMemoryQueue.pull();
    }

    @Override
    public void delete(String queueName, String messageId) {
        InMemoryQueue inMemoryQueue = getOrCreateQueue(queueName);
        inMemoryQueue.delete(messageId);
    }

    @Override
    public List<QueueMessage> pull(String queueName, int limit) {
        InMemoryQueue inMemoryQueue = getOrCreateQueue(queueName);
        return inMemoryQueue.pull(limit);
    }

    static FacilioQueue getInstance(){
        return INSTANCE;
    }

    private class InMemoryQueue {
        private LinkedBlockingQueue<QueueMessage> queue = new LinkedBlockingQueue<>();
        private LinkedBlockingQueue<QueueMessage> consumedMessages = new LinkedBlockingQueue<>();
        private ConcurrentHashMap<String, QueueMessage> deletedMessages = new ConcurrentHashMap<>();
        private AtomicLong counter = new AtomicLong(0L);


        private void push(String message){
        	String id = TIME+"-"+ String.format("%015d", counter.getAndIncrement());
        	QueueMessage msg = new QueueMessage(id, message);
        	msg.setReceiptHandle(id);
            queue.add(msg);
        }

        private QueueMessage pull() {
            QueueMessage message;

            synchronized (this) {
                message = consumedMessages.peek();

                while (message != null) {
                    if (deletedMessages.containsKey(message.getId())) {
                        consumedMessages.remove();
                        deletedMessages.remove(message.getId());
                        message = consumedMessages.peek();
                    } else {
                        if (message.getVisibilityTimeout() > System.currentTimeMillis()) {
                            message = null;
                        } else {
                            message = consumedMessages.poll();
                            break;
                        }
                    }
                }
            }

            if(message == null){
                message = queue.poll();
            }

            addToRetrievedMessage(message);

            return message;
        }


        private List<QueueMessage> pull(int limit) {
            List<QueueMessage> messageList = new ArrayList<>(limit);

            QueueMessage message;
            int i  = 0;
            synchronized (this) {
                message = consumedMessages.peek();

                while (message != null && i < limit) {
                    if (deletedMessages.containsKey(message.getId())) {
                        consumedMessages.remove();
                        deletedMessages.remove(message.getId());
                        message = consumedMessages.peek();
                    } else {
                        if (message.getVisibilityTimeout() > System.currentTimeMillis()) {
                            message = null;
                        } else {
                            message = consumedMessages.poll();
                            messageList.add(message);
                            addToRetrievedMessage(message);
                            i++;
                            message = consumedMessages.peek();
                            if(i == limit) {
                                break;
                            }
                        }
                    }
                }
            }

            if (message == null) {
                message = queue.poll();
                while (message != null && i < limit) {
                    messageList.add(message);
                    addToRetrievedMessage(message);
                    i++;
                    message = queue.poll();
                }
            }

            return messageList;
        }

        private void addToRetrievedMessage(QueueMessage message){
            if(message != null) {
                message.setVisibilityTimeout(System.currentTimeMillis() + DEFAULT_VISIBILITY);
                consumedMessages.add(message);
            }
        }

        private void delete(String messageId){
            deletedMessages.putIfAbsent(messageId, EMPTY_MSG);
        }

    }

    public boolean changeVisibilityTimeout(String queueName, String receiptHandle, int visibilityTimeout) {
        return false;
    }
}
