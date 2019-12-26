package com.facilio.util;

import com.facilio.agent.AgentKeys;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.AgentConstants;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

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

    public void processAgentAck(JSONObject payload, String agent, long orgId) {
        try {
            Long msgId = (Long) payload.get(AgentConstants.MESSAGE_ID);
            int status = ((Number) payload.get(AgentConstants.STATUS)).intValue();

            JSONObject newObj = (JSONObject) payload.clone();
            newObj.put("agent", agent);
            FacilioChain chain = TransactionChainFactory.getAckProcessorChain();
            FacilioContext context = chain.getContext();
            context.put(AgentConstants.ID, msgId);
            context.put(AgentConstants.STATUS, Status.valueOf(status));
            context.put(AgentConstants.DATA, newObj);
            context.put(AgentConstants.ORGID, orgId);
            chain.execute();

        } catch (Exception e) {
            LOGGER.info("EXxception occured", e);
        }
    }

}
