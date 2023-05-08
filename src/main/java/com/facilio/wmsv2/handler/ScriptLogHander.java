package com.facilio.wmsv2.handler;

import com.facilio.fw.BeanFactory;
import com.facilio.queueingservice.QueueingServiceBean;
import com.facilio.wmsv2.message.Message;
import lombok.extern.log4j.Log4j;

@Log4j
public class ScriptLogHander extends BaseHandler {
	
	public static final String TOPIC = "script-log";

	@Override
    public void processOutgoingMessage(Message message) {
        try {
        	if(message.getOrgId() != null && message.getOrgId() > 0) {
        		QueueingServiceBean bean = (QueueingServiceBean) BeanFactory.lookup("QueueingServiceBean", message.getOrgId());
        		bean.addScriptLog(message.getContent());
        	}
        } catch (Exception e) {
        	LOGGER.info("ERROR IN ADDING SCRIPT LOGS", e);
        }
    }
}
