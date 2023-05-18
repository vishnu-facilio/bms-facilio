package com.facilio.wmsv2.handler.agent;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.wmsv2.handler.BaseHandler;
import com.facilio.wmsv2.message.Message;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Log4j
public class ClearPointAlarmHandler extends BaseHandler {

    @Override
    public void processOutgoingMessage(Message message) {
        JSONObject content = message.getContent();
        try{
            if(content.containsKey(AgentConstants.AGENT) && content.get(AgentConstants.AGENT) != null){
                String agentName = content.get(AgentConstants.AGENT).toString();
                FacilioAgent agent = AgentConstants.getAgentBean().getAgent(agentName);
                if(agent!=null){
                    AgentUtilV2.clearPointsDataMissingAlarm(agent);
                }
            }
        } catch (Exception e){
            LOGGER.info("Exception while clear point alarm");
        }
    }

}