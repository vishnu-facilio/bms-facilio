package com.facilio.agentv2.iotmessage;

import com.facilio.agent.AgentType;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.fw.constants.PublishType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.modbusrtu.ModbusRtuControllerContext;
import com.facilio.agentv2.modbusrtu.RtuNetworkContext;
import com.facilio.agentv2.modbustcp.ModbusTcpControllerContext;
import com.facilio.agentv2.opcua.OpcUaControllerContext;
import com.facilio.agentv2.opcxmlda.OpcXmlDaControllerContext;
import com.facilio.agentv2.rdm.RdmControllerContext;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class AgentMessenger {

    private static final Logger LOGGER = LogManager.getLogger(AgentMessenger.class.getName());

    private static final int MAX_BUFFER = 45000; //45000 fix for db insert 112640  110KiB;  AWS IOT limits max publish message size to 128KiB


    private static IotData constructNewIotAgentMessage(long agentId, FacilioCommand command, FacilioContext extraMsgContent, FacilioControllerType type) throws Exception {
        AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
        FacilioAgent agent = agentBean.getAgent(agentId);

        Objects.requireNonNull(agent, "Agent can't be null");
        return constructNewIotAgentMessage(command, agent, extraMsgContent, type);
    }

    private static IotData constructNewIotAgentMessage(FacilioCommand command, FacilioAgent agent, FacilioContext extraMsgContent, FacilioControllerType type) throws Exception {
        if ((command != FacilioCommand.PING) && (!agent.getConnected())) {
            throw new Exception("Agent is not connected");
        }
        JSONObject messageBody = new JSONObject();
        IotData iotData = new IotData();
        iotData.setAgentId(agent.getId());
        iotData.setCommand(command.asInt());
        messageBody.put(AgentConstants.AGENT, agent.getName());
        if (type != null) {
            messageBody.put(AgentConstants.CONTROLLER_TYPE, type.asInt());
            }
        messageBody.put(AgentConstants.TIMESTAMP, System.currentTimeMillis());
        messageBody.put(AgentConstants.AGENT_ID, agent.getId());
        messageBody.put(AgentConstants.COMMAND, command.asInt());

            switch (command) {
                case PING:
                    messageBody.put(AgentConstants.PUBLISH_TYPE, PublishType.ACK.asInt());
                    break;
                case PROPERTY:
                    messageBody.put(AgentConstants.PROPERTY, extraMsgContent.get(AgentConstants.PROPERTY));
                    break;
                case DISCOVER_CONTROLLERS:
                    if(agent.getDiscoverControllersTimeOut()>0) {
                        messageBody.put(AgentConstants.TIMEOUT, agent.getDiscoverControllersTimeOut());
                    }
                    if (extraMsgContent != null && type == FacilioControllerType.E2 && extraMsgContent.containsKey(AgentConstants.IP_ADDRESS) && extraMsgContent.containsKey(AgentConstants.PORT)){
                        messageBody.put(AgentConstants.IP_ADDRESS, extraMsgContent.get(AgentConstants.IP_ADDRESS));
                        messageBody.put(AgentConstants.PORT, extraMsgContent.get(AgentConstants.PORT));
                    }
                    if (extraMsgContent != null && type == FacilioControllerType.BACNET_IP && AgentType.FACILIO == agent.getAgentTypeEnum()){
                        messageBody.put(AgentConstants.BACNET_PORT, extraMsgContent.get(AgentConstants.BACNET_PORT));
                        if(extraMsgContent.containsKey(AgentConstants.RANGE) && extraMsgContent.get(AgentConstants.RANGE) != null){
                            messageBody.put(AgentConstants.RANGE, extraMsgContent.get(AgentConstants.RANGE));
                        }
                        messageBody.put(AgentConstants.TIMEOUT_SEC, extraMsgContent.get(AgentConstants.TIMEOUT_SEC));
                    }
                     break;
                case DISCOVER_POINTS:
                    if(agent.getDiscoverPointsTimeOut()>0) {
                        messageBody.put(AgentConstants.TIMEOUT, agent.getDiscoverPointsTimeOut());
                    }
                    break;
                case SHUTDOWN:
                case DISCOVER_ALARM_SOURCE:
                    break;
                case CONTROLLER_STATUS:
                case CONFIGURE:
                case EDIT_CONTROLLER:
                    if (extraMsgContent.containsKey(AgentConstants.DATA)) {
                        messageBody.put(AgentConstants.CONTROLLER, extraMsgContent.get(AgentConstants.DATA));
                        LOGGER.info(type);
                    } else {
                        LOGGER.info("Exception occurred , no data in context for " + command.name());
                        throw new Exception("No data in context for " + command.name());
                    }
                    break;
                case UPGRADE:
                    messageBody.putAll(extraMsgContent);
                    break;

                case ADD_CONTROLLER:
                    if (extraMsgContent.containsKey(AgentConstants.DATA)) {
                        messageBody.put(AgentConstants.CONTROLLERS, extraMsgContent.get(AgentConstants.DATA));
                        LOGGER.info(type);
                    } else {
                        LOGGER.info("Exception occurred , no data in context for " + command.name());
                        throw new Exception("No data in context for " + command.name());
                    }
                    break;
            }
            List<IotMessage> messages = new ArrayList<>();
            messages.add(MessengerUtil.getMessageObject(messageBody, command));
            iotData.setMessages(messages);
            iotData.setAgent(agent);
            return iotData;
    }


    public static IotData publishNewIotAgentMessage(Controller controller, FacilioCommand command, JSONObject data) throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(AgentConstants.DATA, data);
        IotData iotData = constructNewIotAgentMessage(controller.getAgentId(), command, context, FacilioControllerType.valueOf(controller.getControllerType()));
        return MessengerUtil.addAndPublishNewAgentData(iotData);
    }


    public static void pingAgent(long agentId) throws Exception {
        if ((agentId > 0)) {
            IotData pingData = constructAgentPing(agentId);
            MessengerUtil.addAndPublishNewAgentData(pingData);
        } else {
            throw new Exception(" agentId cant be less than 1");
        }
    }

    private static IotData constructAgentPing(long agentId) throws Exception {
        return constructNewIotAgentMessage(agentId, FacilioCommand.PING, (FacilioContext) null, (FacilioControllerType) null);
    }


    public static boolean restart(long agentId) { //TODO not yet tested
        if (agentId > 0) {
            try {
                IotData restart = constructAgentRestart(agentId);
                MessengerUtil.addAndPublishNewAgentData(restart);
                return true;
            } catch (Exception e) {
                LOGGER.info("Exception occurred while restarting the agent ->" + agentId + "-> ", e);
            }
        }
        return false;
    }

    private static IotData constructAgentRestart(long agentId) throws Exception {
        return constructNewIotAgentMessage(agentId, FacilioCommand.SHUTDOWN, (FacilioContext) null, (FacilioControllerType) null);
    }

    public static boolean getJVMStatus(Long agentId) {
        try {
            if (agentId > 0) {
                IotData data = constructNewIotAgentMessage((long) agentId, FacilioCommand.STATS, (FacilioContext) null, (FacilioControllerType) null);
                MessengerUtil.addAndPublishNewAgentData(data);
                return true;
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while Agent status command construction", e);
        }
        return false;
    }

    public static boolean getThreadDump(Long agentId) {
        try {
            if (agentId > 0) {
                IotData data = constructNewIotAgentMessage((long) agentId, FacilioCommand.THREAD_DUMP, (FacilioContext) null, (FacilioControllerType) null);
                MessengerUtil.addAndPublishNewAgentData(data);
                return true;
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while Agent status command construction", e);
        }
        return false;
    }

    public static boolean discoverController(Long agentId, FacilioControllerType controllerType) throws Exception {
        if (agentId > 0) {
            IotData data = constructNewIotAgentMessage((long) agentId, FacilioCommand.DISCOVER_CONTROLLERS, (FacilioContext) null, controllerType);
            MessengerUtil.addAndPublishNewAgentData(data);
            return true;
        }
        return true;
    }

    public static boolean discoverController(Long agentId, FacilioControllerType controllerType, String ipAddress, Long port) throws Exception {
        if (agentId > 0) {
            FacilioContext context = new FacilioContext();
            context.put(AgentConstants.IP_ADDRESS, ipAddress);
            context.put(AgentConstants.PORT, port);
            IotData data = constructNewIotAgentMessage(agentId, FacilioCommand.DISCOVER_CONTROLLERS, context, controllerType);
            MessengerUtil.addAndPublishNewAgentData(data);
            return true;
        }
        return true;
    }

    public static boolean discoverBacnetController(Long agentId, FacilioControllerType controllerType, Long bacnetPort, JSONObject range, Long timeout_sec) throws Exception {
        if (agentId > 0) {
            FacilioContext context = new FacilioContext();
            context.put(AgentConstants.TIMEOUT_SEC, timeout_sec);
            context.put(AgentConstants.BACNET_PORT, bacnetPort);
            context.put(AgentConstants.RANGE, range);
            IotData data = constructNewIotAgentMessage(agentId, FacilioCommand.DISCOVER_CONTROLLERS, context, controllerType);
            MessengerUtil.addAndPublishNewAgentData(data);
            return true;
        }
        return true;
    }

    public static boolean discoverSources(Long agentId) throws Exception { // will change commad for discover sources..
        if (agentId > 0) {
            IotData data = constructNewIotAgentMessage(agentId, FacilioCommand.DISCOVER_ALARM_SOURCE, (FacilioContext) null, null);
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
            FacilioContext context = new FacilioContext();
            context.put(AgentConstants.DATA, networkJson);
            context.put(AgentConstants.CONFIGURE, AgentConstants.MODBUS_NETWORK);
            IotData data = constructNewIotAgentMessage((long) rtuNetwork.getAgentId(), FacilioCommand.ADD_CONTROLLER, context, FacilioControllerType.MODBUS_RTU);
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

    public static void sendConfigureModbusIpControllers(List<Controller> controllersToAdd) throws Exception {
        Objects.requireNonNull(controllersToAdd, "controllers can't be null");
        if (controllersToAdd.isEmpty()) {
            throw new Exception("controllers empty");
        }
        IotData iotDataForControllerContext = getIotDataForControllerContext(controllersToAdd, controllersToAdd.get(0).getAgent());
        MessengerUtil.addAndPublishNewAgentData(iotDataForControllerContext);
    }

    private static boolean sendControllerConfig(Controller controllerContext) throws Exception {
        IotData iotDataforControllerContext = getIotDataForControllerContext(controllerContext);
        MessengerUtil.addAndPublishNewAgentData(iotDataforControllerContext);
        return true;
    }

    private static IotData getIotDataForControllerContext(Controller controllerContext) throws Exception {
        Objects.requireNonNull(controllerContext, "controller cant be null");
        long agentId = controllerContext.getAgentId();
        AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
        FacilioAgent agent = agentBean.getAgent(agentId);
        return getIotDataForControllerContext(Collections.singletonList(controllerContext), agent);
    }

    private static IotData getIotDataForControllerContext(List<Controller> controllerList, FacilioAgent agent) throws Exception {
        Objects.requireNonNull(controllerList, "controller can't be empty");
        if (controllerList.isEmpty()) {
            throw new Exception(" controller list can't be empty");
        }
        FacilioContext context = new FacilioContext();
        JSONArray controllerArray = new JSONArray();
        for (Controller controller : controllerList) {
            JSONObject controllerData = controller.getChildJSON();
            controllerData.put(AgentConstants.NAME, controller.getName());
            controllerArray.add(controllerData);
            context.put(AgentConstants.CONFIGURE, AgentConstants.CONTROLLER);
        }
        context.put(AgentConstants.DATA, controllerArray);
        return constructNewIotAgentMessage(FacilioCommand.ADD_CONTROLLER, agent, context, FacilioControllerType.valueOf(controllerList.get(0).getControllerType()));
    }
    public static boolean sendAddModbusRtuControllerCommand(ModbusRtuControllerContext controllerContext) throws Exception {
        return sendControllerConfig(controllerContext);
    }

    public static boolean sendAddModbusIpControllerCommand(ModbusTcpControllerContext controllerContext) throws Exception {
        return sendControllerConfig(controllerContext);
    }

    public static boolean sendAddControllerCommand(Controller controllerContext) throws Exception {
        return sendControllerConfig(controllerContext);
    }

    public static IotData sendAgentUpgradeCommand(long agentId, String version, String url, String authKey) throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(AgentConstants.HEADER, authKey);
        context.put(AgentConstants.VERSION, version);
        context.put(AgentConstants.URL, url);
        return constructNewIotAgentMessage(agentId, FacilioCommand.UPGRADE, context, (FacilioControllerType) null);
    }

    public static boolean sendRdmAddControllerCommand(RdmControllerContext rdmControllerContext) throws Exception {
        return sendControllerConfig(rdmControllerContext);
    }

    public static void setProperty(long agentId, String property, long value) throws Exception {
        FacilioContext context = new FacilioContext();
        Map<String, Long> props = Collections.singletonMap(property, value);
        context.put(AgentConstants.PROPERTY, props);
        IotData iotData = constructNewIotAgentMessage(agentId, FacilioCommand.PROPERTY, context, null);
        MessengerUtil.addAndPublishNewAgentData(iotData);
    }
}