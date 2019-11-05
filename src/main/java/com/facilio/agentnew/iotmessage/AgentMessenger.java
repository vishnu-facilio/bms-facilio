package com.facilio.agentnew.iotmessage;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.fw.constants.PublishType;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.FacilioAgent;
import com.facilio.agentnew.NewAgentAPI;
import com.facilio.agentnew.controller.Controller;
import com.facilio.chain.FacilioContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AgentMessenger {

    private static final Logger LOGGER = LogManager.getLogger(AgentMessenger.class.getName());

    private static final int MAX_BUFFER = 45000; //45000 fix for db insert 112640  110KiB;  AWS IOT limits max publish message size to 128KiB



    private static IotData constructNewIotAgentMessage(long agentId, FacilioCommand command, FacilioContext context, FacilioControllerType type) throws Exception{
        FacilioAgent agent = NewAgentAPI.getAgent(agentId);
        if(agent != null){
            JSONObject object = new JSONObject();
           IotData iotData = new IotData();
            iotData.setAgentId(agentId);
            iotData.setCommand(command.asInt());
            object.put(AgentConstants.AGENT,agent.getName());
            if(type != null){
                object.put(AgentConstants.TYPE,type.asInt());
            }
            object.put(AgentConstants.TIMESTAMP,System.currentTimeMillis());
            object.put(AgentConstants.AGENT_ID,agentId);
            object.put(AgentConstants.COMMAND,command.asInt());

            switch (command){
                case PING:
                    object.put(AgentConstants.PUBLISH_TYPE, PublishType.ACK.asInt());
                    break;
                case SHUTDOWN:
                    break;
                case CONTROLLER_STATUS:
                case DISCOVER_CONTROLLERS:
                case CONFIGURE:
                case ADD_CONTROLLER:
                case EDIT_CONTROLLER:
                    if(context.containsKey(AgentConstants.DATA)){
                        object.putAll((Map) context.get(AgentConstants.DATA));
                    }else {
                        LOGGER.info("Exception occurred , no data in context for "+command.name());
                        throw new Exception("No data in context for "+command.name());
                    }
                    break;
            }
            List<IotMessage> messages = new ArrayList<>();
            messages.add(getMessageObject(object));
            iotData.setMessages(messages);
            return iotData;
        }else {
            throw new Exception(" Agent can't be null ");
        }
    }


    private static IotMessage getMessageObject(JSONObject message) {
       IotMessage msg = new IotMessage();
        JSONObject data = new JSONObject();
        data.putAll(message);
        msg.setMessageData(data);
        return msg;
    }


    public static IotData publishNewIotAgentMessage(Controller controller, FacilioCommand command, JSONObject data) throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(AgentConstants.DATA,data);
        IotData iotData = constructNewIotAgentMessage(controller.getAgentId(), command, context, FacilioControllerType.valueOf(controller.getControllerType()));
        return addAndPublishNewAgentData(iotData);
    }



    public static boolean pingAgent(long agentId){
        try {
            if( (agentId > 0) ){
                    IotData pingData = constructAgentPing(agentId);
                    addAndPublishNewAgentData(pingData);
                    return true;
                }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ",e);
        }
        return false;
    }



    static IotData addAndPublishNewAgentData(IotData data) throws Exception {

        NewIotMessageAPI.addIotData(data);
        NewIotMessageAPI.addIotMessage(data.getId(),data.getMessages());
        NewIotMessageAPI.publishIotMessage(data);
        return data;
    }

    private static IotData constructAgentPing(long agentId) throws Exception {
        return constructNewIotAgentMessage(agentId,FacilioCommand.PING,null,null);
    }


}