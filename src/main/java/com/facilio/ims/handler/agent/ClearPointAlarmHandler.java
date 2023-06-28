package com.facilio.ims.handler.agent;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.fms.message.Message;
import com.facilio.ims.handler.ImsHandler;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Log4j
public class ClearPointAlarmHandler extends ImsHandler {

    public static String KEY = "__agent__/point/alarm/org/";

    @Override
    public void processMessage(Message message) {
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
            LOGGER.error("Exception while clear point alarm", e);
        }
    }

}