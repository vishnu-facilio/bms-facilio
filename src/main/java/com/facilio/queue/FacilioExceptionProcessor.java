package com.facilio.queue;

import com.amazonaws.services.sqs.model.Message;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

public class FacilioExceptionProcessor extends TimerTask {


    private static final String QUEUE = "Exception";
    private static final HashMap<String, Integer> EXCEPTION_COUNT = new HashMap<>();
    private static final HashMap<String, String> EXCEPTION_MESSAGES = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(FacilioExceptionProcessor.class.getName());


    public void run() {
        List<Message> messageList = FAWSQueue.receiveMessages(QUEUE);
        for(Message msg : messageList) {
            String body = msg.getBody();
            int index = body.indexOf(CommonCommandUtil.DELIMITER);
            String orgWithClassName = "";
            if(index > 1) {
                orgWithClassName = body.substring(0, index);
                int count = EXCEPTION_COUNT.getOrDefault(orgWithClassName, 0);
                EXCEPTION_COUNT.put(orgWithClassName, count+1);
                if(count == 0) {
                    EXCEPTION_MESSAGES.put(orgWithClassName, body);
                }
            }

        }
        StringBuilder builder = new StringBuilder();
        for(String orgWithClass : EXCEPTION_COUNT.keySet()) {
            builder.append(orgWithClass).append("  :   ").append(EXCEPTION_COUNT.get(orgWithClass)).append(System.lineSeparator())
                    .append(EXCEPTION_MESSAGES.get(orgWithClass)).append(System.lineSeparator());
        }

        JSONObject json = new JSONObject();

        json.put("sender", "error@facilio.com");
        json.put("to", "error@facilio.com");
        json.put("subject", "Exception Mail");
        json.put("message", builder.toString());
        try {
            AwsUtil.sendEmail(json);
        } catch (Exception e) {
            LOGGER.info("Exception while sending email ", e);
        }
        for(Message msg : messageList) {
            FAWSQueue.deleteMessage(QUEUE, msg.getReceiptHandle());
        }
    }
}
