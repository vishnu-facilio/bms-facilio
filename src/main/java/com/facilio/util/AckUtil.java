package com.facilio.util;

import com.facilio.agent.AgentKeys;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.logs.LogsApi;
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
    public static void processAck(JSONObject payLoad, String agent, long orgId)  {
        try {
            long msgId = getMessageIdFromPayload(payLoad);
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

    public static boolean processAgentAck(JSONObject payload, long agentId, long orgId) throws Exception {
                long msgId = getMessageIdFromPayload(payload);
                if(payload.containsKey(AgentConstants.STATUS)){
                    int status = ((Number) payload.get(AgentConstants.STATUS)).intValue();
                    long timeStamp = getTimestampFromPayload(payload);
                    ackAndLogAgentAck(agentId, orgId, msgId, status, timeStamp);
                    return true;
                }else {
                    throw new Exception(" status missing from payload ");
                }

    }

    private static void ackAndLogAgentAck(long agentId, long orgId, long msgId, int status, long timeStamp) throws Exception {
        LogsApi.logAgentMessages(agentId,msgId,null, Status.valueOf(status),timeStamp);
        FacilioChain chain = TransactionChainFactory.getAckProcessorChain();
        FacilioContext context = chain.getContext();
        context.put(AgentConstants.ID, msgId);
        context.put(AgentConstants.STATUS, Status.valueOf(status));
        context.put(AgentConstants.ORGID, orgId);
        chain.execute();
    }

    public static boolean ackPing(long agentId,long orgId, JSONObject payload) throws Exception{
        long timestamp = getTimestampFromPayload(payload);
        long msgId = getMessageIdFromPayload(payload);
        ackAndLogAgentAck(agentId,orgId,msgId,Status.MESSAGE_PROCESSING_SUCCESS.asInt(),timestamp);
        return true;
    }

    private static long getMessageIdFromPayload(JSONObject payload) throws Exception {
        if(payload.containsKey(AgentConstants.MESSAGE_ID)) {
            return Long.parseLong(payload.get(AgentConstants.MESSAGE_ID).toString());
        }
        throw new Exception(" msgid missing from payload ");
    }

    private static long getTimestampFromPayload(JSONObject payload) {
        long timeStamp = System.currentTimeMillis();
        if (payload.containsKey(payload.get(AgentConstants.TIMESTAMP)) && (payload.get(AgentConstants.TIMESTAMP) != null)) {
            timeStamp = Long.parseLong(payload.get(AgentConstants.TIMESTAMP).toString());
        }
        return timeStamp;
    }

}
