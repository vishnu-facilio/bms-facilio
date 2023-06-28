package com.facilio.ims.handler;

import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.queueingservice.QueueingServiceBean;
import lombok.extern.log4j.Log4j;

@Log4j
public class ScriptLogHander extends ImsHandler {
	
	public static final String KEY = "script-log";

	@Override
    public void processMessage(Message message) {
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
