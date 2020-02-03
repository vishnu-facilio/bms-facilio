package com.facilio.agentv2.iotmessage;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.fw.constants.PublishType;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.modbusrtu.ModbusRtuControllerContext;
import com.facilio.agentv2.modbusrtu.RtuNetworkContext;
import com.facilio.agentv2.modbustcp.ModbusTcpControllerContext;
import com.facilio.agentv2.opcua.OpcUaControllerContext;
import com.facilio.agentv2.opcxmlda.OpcXmlDaControllerContext;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

            switch (command) {
                case PING:
                    object.put(AgentConstants.PUBLISH_TYPE, PublishType.ACK.asInt());
                    break;
                case SHUTDOWN:
                    break;
                case CONTROLLER_STATUS:
                case CONFIGURE:
                case EDIT_CONTROLLER:
                    if (context.containsKey(AgentConstants.DATA)) {
                        object.put(AgentConstants.DATA, context.get(AgentConstants.DATA));
                    } else {
                        LOGGER.info("Exception occurred , no data in context for " + command.name());
                        throw new Exception("No data in context for " + command.name());
                    }
                    break;
                case ADD_CONTROLLER:
                    if (context.containsKey(AgentConstants.DATA)) {
                        JSONArray controllersArray = new JSONArray();
                        controllersArray.add(context.get(AgentConstants.DATA));
                        object.put(AgentConstants.CONTROLLERS, controllersArray);
                    } else {
                        LOGGER.info("Exception occurred , no data in context for " + command.name());
                        throw new Exception("No data in context for " + command.name());
                    }
                    break;

            }
            List<IotMessage> messages = new ArrayList<>();
            messages.add(MessengerUtil.getMessageObject(object, command));
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

    public static boolean discoverController(Long agentId, FacilioControllerType controllerType) throws Exception {
        if (agentId > 0) {
            IotData data = constructNewIotAgentMessage(agentId, FacilioCommand.DISCOVER_CONTROLLERS, null, controllerType);
            MessengerUtil.addAndPublishNewAgentData(data);
            return true;
        }
        return true;
    }

  /*  public static boolean sendConfigureModbusControllercommand(long agentId, long slaveId, String ip, long netwokId, FacilioControllerType controllerType) throws Exception {
        if((agentId > 0) && (slaveId > 0)){
            FacilioContext context = new FacilioContext();
            JSONObject jsonObject = new JSONObject();
            JSONObject configJson = new JSONObject();

            configJson.put(AgentConstants.SLAVE_ID,slaveId);
            if((controllerType == FacilioControllerType.MODBUS_RTU) && (netwokId > 0)){
                configJson.put(AgentConstants.NETWORK_ID,netwokId);
            }else if((controllerType == FacilioControllerType.MODBUS_IP) && (ip != null) && ( ! ip.isEmpty())){
                configJson.put(AgentConstants.IP_ADDRESS,ip);
            }
            jsonObject.put(AgentConstants.CONFIGURE,AgentConstants.CONTROLLER);
            jsonObject.put(AgentConstants.DATA,configJson);
            context.put(AgentConstants.DATA,jsonObject);

            IotData data = constructNewIotAgentMessage(agentId,FacilioCommand.CONFIGURE,context,controllerType);
            MessengerUtil.addAndPublishNewAgentData(data);
            return true;
        }
        return false;
    }*/

    public static boolean sendConfigureModbusRtuNetworkCommand(RtuNetworkContext rtuNetwork) throws Exception {
        if (rtuNetwork != null) {
            JSONObject networkJson = new JSONObject();
            networkJson.put(AgentConstants.DATA, FieldUtil.getAsJSON(rtuNetwork));
            networkJson.put(AgentConstants.CONFIGURE, AgentConstants.MODBUS_NETWORK);
            FacilioContext context = new FacilioContext();
            context.put(AgentConstants.DATA, networkJson);
            IotData data = constructNewIotAgentMessage(rtuNetwork.getAgentId(), FacilioCommand.CONFIGURE, context, FacilioControllerType.MODBUS_RTU);
            MessengerUtil.addAndPublishNewAgentData(data);
            return true;
        } else {
            throw new Exception(" rtu network cant be null");
        }
    }

    public static boolean sendConfigureOpcXmlDaController(OpcXmlDaControllerContext controllerContext) throws Exception {
        return sendControllerConfig(controllerContext);
    }

    public static boolean sendConfigureOpcUaControllerCommand(OpcUaControllerContext controllerContext) throws Exception {
        return sendControllerConfig(controllerContext);
    }

    private static boolean sendControllerConfig(Controller controllerContext) throws Exception {
        if (controllerContext != null) {
            if (controllerContext.getControllerType() >= 0) {

                if (controllerContext.getAgentId() > 0) {
                    FacilioContext context = new FacilioContext();
                    JSONObject controllerData = controllerContext.getChildJSON();
                    context.put(AgentConstants.DATA, controllerData);
                    IotData data;
                    if (controllerContext.getControllerType()== ControllerType.OPC_DA.getKey() || controllerContext.getControllerType() == ControllerType.OPC_UA.getKey()){
                        data = constructNewIotAgentMessage(controllerContext.getAgentId(), FacilioCommand.ADD_CONTROLLER, context, FacilioControllerType.valueOf(controllerContext.getControllerType()));

                    }else{
                        data = constructNewIotAgentMessage(controllerContext.getAgentId(), FacilioCommand.CONFIGURE, context, FacilioControllerType.valueOf(controllerContext.getControllerType()));
                    }
                    MessengerUtil.addAndPublishNewAgentData(data);
                    return true;
                } else {
                    throw new Exception(" agentId not set ");
                }
            } else {
                throw new Exception(" controller type not set ");
            }
        } else {
            throw new Exception(" opcxmlda controller context can't be null");
        }
    }

    public static boolean sendConfigureModbusRtuControllerCommand(ModbusRtuControllerContext controllerContext) throws Exception {
        return sendControllerConfig(controllerContext);
    }

    public static boolean sendConfigModbusIpControllerCommand(ModbusTcpControllerContext controllerContext) throws Exception {
        return sendControllerConfig(controllerContext);
    }

}