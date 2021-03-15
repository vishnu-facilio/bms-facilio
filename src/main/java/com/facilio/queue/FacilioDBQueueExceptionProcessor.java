package com.facilio.queue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.constants.FacilioConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.queue.service.QueueMessage;
import com.facilio.service.FacilioService;
import com.facilio.services.factory.FacilioFactory;
public class FacilioDBQueueExceptionProcessor extends TimerTask {

    private static final HashMap<String, Integer> EXCEPTION_COUNT = new HashMap<>();
    private static final HashMap<String, String> EXCEPTION_MESSAGES = new HashMap<>();
    private static final List<String> RECEIPT_HANDLE_LIST = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger(FacilioDBQueueExceptionProcessor.class.getName());
	
	@Override
	public void run() {
		
        List<QueueMessage> messageList = new ArrayList<>();
        try {				
            messageList = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.INSTANT_JOB_SERVICE,() -> FacilioQueueException.pull(20));
        } catch (Exception e1) {
            LOGGER.info("Exception Occurred in  FacilioQueue  : ",e1);
        }
        while(messageList.size() > 0 && EXCEPTION_MESSAGES.size() < 20) {
            processMessages(messageList);
            try {
				messageList =  FacilioService.runAsServiceWihReturn(FacilioConstants.Services.INSTANT_JOB_SERVICE,() -> FacilioQueueException.pull(20));
			} catch (Exception e) {
				 LOGGER.info("Exception Occurred in  FacilioQueue  : ",e);
			}
        }

//        if(messageList.size() > 0 && EXCEPTION_MESSAGES.size() < 100) {
//            processMessages(messageList);
//        }
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
                    FacilioFactory.getEmailClient().sendEmail(json);
                    LOGGER.info("calling delete msg with "+ RECEIPT_HANDLE_LIST.size());
                    for(String deleteMsgId : RECEIPT_HANDLE_LIST) {
                    	 FacilioService.runAsService(FacilioConstants.Services.INSTANT_JOB_SERVICE,() -> FacilioQueueException.deleteQueue(deleteMsgId));
                    }
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

	private void processMessages(List<QueueMessage> messageList) {
		for (QueueMessage msg : messageList) {

			String body = msg.getMessage();
			int index = body.indexOf(CommonCommandUtil.DELIMITER);
			String orgWithClassName = "";
			if (index > 1) {
				orgWithClassName = body.substring(0, index);
				RECEIPT_HANDLE_LIST.add(msg.getId());
				int count = EXCEPTION_COUNT.getOrDefault(orgWithClassName, 0);
				EXCEPTION_COUNT.put(orgWithClassName, count + 1);
				if (count == 0) {
					EXCEPTION_MESSAGES.put(orgWithClassName, body);
				}
			}
		}
	}

}
