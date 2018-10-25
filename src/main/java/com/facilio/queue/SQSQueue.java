package com.facilio.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityResult;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.facilio.aws.util.AwsUtil;
import com.facilio.tasker.config.InstantJobConf;

public class SQSQueue  implements FacilioQueue {

    private static final ConcurrentHashMap<String, String> nameVsURL = new ConcurrentHashMap<>();
    private static final FacilioQueue INSTANCE = new SQSQueue();

    static FacilioQueue getInstance() {
        return INSTANCE;
    }

    public boolean push(String queueName, String message) {
        String url = nameVsURL.get(queueName);
        AmazonSQS sqs = AwsUtil.getSQSClient();
        if(url == null) {
            GetQueueUrlResult result = sqs.getQueueUrl(queueName);
            url = result.getQueueUrl();
            nameVsURL.put(queueName, url);
        }
        SendMessageResult result = sqs.sendMessage(url, message);
        return (result.getSdkHttpMetadata().getHttpStatusCode() == 200);
    }

    public QueueMessage pull(String queueName) {
        List<QueueMessage> messages = pull(queueName, 1);
        if(messages.size() > 0) {
            return messages.get(0);
        }
        return null;
    }

    public void delete(String queueName, String receiptHandle) {
        String url = nameVsURL.get(queueName);
        AmazonSQS sqs = AwsUtil.getSQSClient();
        if(url == null) {
            GetQueueUrlResult result = sqs.getQueueUrl(queueName);
            url = result.getQueueUrl();
            nameVsURL.put(queueName, url);
        }
        sqs.deleteMessage(queueName, receiptHandle);
    }

    public List<QueueMessage> pull(String queueName, int limit) {
        if(limit > 20) {
            limit = 20;
        }

        String url = nameVsURL.get(queueName);
        AmazonSQS sqs = AwsUtil.getSQSClient();
        if(url == null) {
            GetQueueUrlResult result = sqs.getQueueUrl(queueName);
            url = result.getQueueUrl();
            nameVsURL.put(queueName, url);
        }
        List<Message> messageList = new ArrayList<>();
        List<QueueMessage> queueMessages = new ArrayList<>();
        while (messageList.size() < limit) {
            ReceiveMessageRequest request = new ReceiveMessageRequest().withMaxNumberOfMessages(10).withVisibilityTimeout(InstantJobConf.getDefaultTimeOut()).withQueueUrl(url);
            ReceiveMessageResult result = sqs.receiveMessage(request);
            if(result.getMessages().size() == 0) {
                break;
            }
            messageList.addAll(result.getMessages());
        }

        for(Message msg : messageList ) {
         QueueMessage qMsg = new QueueMessage(msg.getMessageId(), msg.getBody());
         qMsg.setReceiptHandle(msg.getReceiptHandle());
         queueMessages.add(qMsg);
        }
        return queueMessages;
    }

    public boolean changeVisibilityTimeout(String queueName, String receiptHandle, int visibilityTimeout) {
        String url = nameVsURL.get(queueName);
        if(url != null) {
            AmazonSQS sqs = AwsUtil.getSQSClient();
            ChangeMessageVisibilityResult result = sqs.changeMessageVisibility(url, receiptHandle, visibilityTimeout);
            return (result.getSdkHttpMetadata().getHttpStatusCode() == 200);
        }
        return false;
    }
}
