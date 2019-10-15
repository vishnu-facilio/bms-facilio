package com.facilio.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.agent.AgentKeys;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.fw.BeanFactory;

public class AckUtil
{
    private static final Logger LOGGER = LogManager.getLogger(AckUtil.class.getName());

    /**
     *
     * @param payLoad
     * @param orgId
     */
    public void processAck(JSONObject payLoad, String agent, long orgId)  {

        try {
            Long msgId = (Long) payLoad.get(AgentKeys.MESSAGE_ID);
            String message = (String) payLoad.get(AgentKeys.MESSAGE);
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            JSONObject newObj = new JSONObject();
            newObj.putAll(payLoad);
            newObj.put("agent", agent);
            bean.acknowledgePublishedMessage(msgId, message, newObj);
        }catch(Exception e){
            LOGGER.info("EXxception occured",e);
        }
    }

}
