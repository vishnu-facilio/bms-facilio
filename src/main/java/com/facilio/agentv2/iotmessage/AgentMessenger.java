package com.facilio.agentv2.iotmessage;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.fw.constants.PublishType;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
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
        FacilioAgent agent = AgentApiV2.getAgent(agentId);
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
            messages.add(MessengerUtil.getMessageObject(object));
            iotData.setMessages(messages);
            LOGGER.info(" iot data generated is "+iotData);
            return iotData;
        }else {
            throw new Exception(" Agent can't be null ");
        }
    }





    public static IotData publishNewIotAgentMessage(Controller controller, FacilioCommand command, JSONObject data) throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(AgentConstants.DATA,data);
        IotData iotData = constructNewIotAgentMessage(controller.getAgentId(), command, context, FacilioControllerType.valueOf(controller.getControllerType()));
        return MessengerUtil.addAndPublishNewAgentData(iotData);
    }



    public static void pingAgent(long agentId) throws Exception {
        if( (agentId > 0) ){
                    IotData pingData = constructAgentPing(agentId);
                    MessengerUtil.addAndPublishNewAgentData(pingData);
        }else {
            throw new Exception(" agentId cant be less than 1");
        }
    }

    private static IotData constructAgentPing(long agentId) throws Exception {
        return constructNewIotAgentMessage(agentId,FacilioCommand.PING,null,null);
    }


    public static boolean shutDown(long agentId) { //TODO not yet tested
        if(agentId > 0){
            try {
                LOGGER.info(" shutting agent ->"+agentId+" down");
                IotData shutDown = constructAgentShutDown(agentId);
                MessengerUtil.addAndPublishNewAgentData(shutDown);
                return true;
            } catch (Exception e) {
                LOGGER.info("Exception occurred while shutting agent down ->"+agentId+"-> ",e);
            }
        }
        return false;
    }

    private static IotData constructAgentShutDown(long agentId) throws Exception {
        return  constructNewIotAgentMessage(agentId,FacilioCommand.SHUTDOWN,null,null);
    }

    public static boolean getJVMStatus(Long agentId) {
        try{
            if(agentId > 0){
                IotData data = constructNewIotAgentMessage(agentId,FacilioCommand.STATS,null,null);
                MessengerUtil.addAndPublishNewAgentData(data);
                return true;
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while Agent status command construction",e);
        }
        return false;
    }

    public static boolean getThreadDump(Long agentId) {
        try{
            if(agentId > 0){
                IotData data = constructNewIotAgentMessage(agentId,FacilioCommand.THREAD_DUMP,null,null);
                MessengerUtil.addAndPublishNewAgentData(data);
                return true;
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while Agent status command construction",e);
        }
        return false;
    }

    public static boolean discoverController(Long agentId, FacilioControllerType controllerType) {
        try{
            if(agentId > 0){
                IotData data = constructNewIotAgentMessage(agentId,FacilioCommand.DISCOVER_CONTROLLERS,null,controllerType);
                MessengerUtil.addAndPublishNewAgentData(data);
                return true;
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while Agent discover controller command construction",e);
        }
        return false;
    }
}