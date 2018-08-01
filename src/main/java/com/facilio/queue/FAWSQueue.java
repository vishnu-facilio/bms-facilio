package com.facilio.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.facilio.aws.util.AwsUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class FAWSQueue {

    private static final ConcurrentHashMap<String, String> nameVsURL = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(FAWSQueue.class.getName());

    public static void sendMessage(String queueName,  String message) {
        String url = nameVsURL.get(queueName);
        AmazonSQS sqs = AwsUtil.getSQSClient();
        if(url == null) {
            GetQueueUrlResult result = sqs.getQueueUrl(queueName);
            url = result.getQueueUrl();
            nameVsURL.put(queueName, url);
        }
        sqs.sendMessage(url, message);
    }

    static List<Message> receiveMessages(String queueName) {
        String url = nameVsURL.get(queueName);
        AmazonSQS sqs = AwsUtil.getSQSClient();
        if(url == null) {
            GetQueueUrlResult result = sqs.getQueueUrl(queueName);
            url = result.getQueueUrl();
            nameVsURL.put(queueName, url);
        }
        List<Message> messageList = new ArrayList<>();
        while (messageList.size() < 20) {
            ReceiveMessageRequest request = new ReceiveMessageRequest().withMaxNumberOfMessages(10).withVisibilityTimeout(7200).withQueueUrl(url);
            ReceiveMessageResult result = sqs.receiveMessage(request);
            if(result.getMessages().size() == 0) {
                break;
            }
            messageList.addAll(result.getMessages());
        }
        return messageList;
    }

    static void deleteMessage(String queueName, String receiptHandle) {
        String url = nameVsURL.get(queueName);
        AmazonSQS sqs = AwsUtil.getSQSClient();
        if(url == null) {
            GetQueueUrlResult result = sqs.getQueueUrl(queueName);
            url = result.getQueueUrl();
            nameVsURL.put(queueName, url);
        }
        if(url != null) {
            sqs.deleteMessage(url, receiptHandle);
        }
    }

    static void deleteMessage(String queueName, List<String> receiptHandles) {
        String url = nameVsURL.get(queueName);
        AmazonSQS sqs = AwsUtil.getSQSClient();
        if(url == null) {
            GetQueueUrlResult result = sqs.getQueueUrl(queueName);
            url = result.getQueueUrl();
            nameVsURL.put(queueName, url);
        }
        List<DeleteMessageBatchRequestEntry> deleteMessageBatchRequestEntries = new ArrayList<>();
        for(String receiptHandle : receiptHandles ) {
            DeleteMessageBatchRequestEntry entry = new DeleteMessageBatchRequestEntry().withReceiptHandle(receiptHandle);
            deleteMessageBatchRequestEntries.add(entry);
        }
        if(receiptHandles.size() > 0) {
            LOGGER.info("Deleting messages with size " + receiptHandles.size());
            if(url != null) {
                DeleteMessageBatchResult result = sqs.deleteMessageBatch(url, deleteMessageBatchRequestEntries);
                List<DeleteMessageBatchResultEntry> resultList = result.getSuccessful();
                LOGGER.info("Successfully deleted " + resultList.size());
            }
        }
    }
}
