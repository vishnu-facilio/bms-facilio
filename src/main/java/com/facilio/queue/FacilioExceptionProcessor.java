package com.facilio.queue;

import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.Message;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

public class FacilioExceptionProcessor extends TimerTask {


    private static final String QUEUE = "Exception";
    private static final HashMap<String, Integer> EXCEPTION_COUNT = new HashMap<>();
    private static final HashMap<String, String> EXCEPTION_MESSAGES = new HashMap<>();
    private static final HashMap<String, List<DeleteMessageBatchRequestEntry>> RECEIPT_HANDLE_LIST = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(FacilioExceptionProcessor.class.getName());


    public void run() {
        List<Message> messageList = FAWSQueue.receiveMessages(QUEUE);


        while(messageList.size() > 0 && EXCEPTION_MESSAGES.size() < 20) {
            processMessages(messageList);
            messageList = FAWSQueue.receiveMessages(QUEUE);;
        }

        if(EXCEPTION_MESSAGES.size() > 0 ) {
        	JSONObject json = new JSONObject();

            json.put("sender", "error@facilio.com");
            json.put("to", "error@facilio.com");
            for(String orgWithClass : EXCEPTION_COUNT.keySet()) {
            	StringBuilder builder = new StringBuilder();
                builder.append(orgWithClass).append("  :   ").append(EXCEPTION_COUNT.get(orgWithClass)).append(System.lineSeparator())
                        .append(EXCEPTION_MESSAGES.get(orgWithClass)).append(System.lineSeparator());
                
                json.put("subject", orgWithClass+" - Exception Mail");
                json.put("message", builder.toString());
                try {
                    AwsUtil.sendEmail(json);
                    LOGGER.info("calling delete msg with "+ RECEIPT_HANDLE_LIST.get(orgWithClass).size());
                    FAWSQueue.deleteMessage(QUEUE, RECEIPT_HANDLE_LIST.get(orgWithClass));
                } catch (Exception e) {
                    LOGGER.info("Exception while sending email ", e);
                }
            }
            resetFields();
        }
    }

    private void resetFields() {
        EXCEPTION_MESSAGES.clear();
        EXCEPTION_COUNT.clear();
        RECEIPT_HANDLE_LIST.clear();
    }

    private void processMessages(List<Message> messageList) {
        for(Message msg : messageList) {
            String body = msg.getBody();
            int index = body.indexOf(CommonCommandUtil.DELIMITER);
            String orgWithClassName = "";
            if(index > 1) {
                orgWithClassName = body.substring(0, index);
                List<DeleteMessageBatchRequestEntry> receiptHandleList = RECEIPT_HANDLE_LIST.getOrDefault(orgWithClassName, new ArrayList<>());
                receiptHandleList.add(new DeleteMessageBatchRequestEntry().withId(msg.getMessageId()).withReceiptHandle(msg.getReceiptHandle()));
                RECEIPT_HANDLE_LIST.put(orgWithClassName, receiptHandleList);
                int count = EXCEPTION_COUNT.getOrDefault(orgWithClassName, 0);
                EXCEPTION_COUNT.put(orgWithClassName, count+1);
                if(count == 0) {
                    EXCEPTION_MESSAGES.put(orgWithClassName, body);
                }
            }
        }
    }

}
