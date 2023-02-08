package com.facilio.agentv2.iotmessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.agent.AgentType;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.rdm.RdmControllerContext;
import com.facilio.fw.BeanFactory;
import com.facilio.wms.message.Message;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.protocol.ProtocolUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.modbusrtu.ModbusRtuPointContext;
import com.facilio.agentv2.modbustcp.ModbusTcpPointContext;
import com.facilio.agentv2.point.Point;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.bmsconsole.util.IoTMessageAPI.IotCommandType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;

public class ControllerMessenger {
    private static final Logger LOGGER = LogManager.getLogger(ControllerMessenger.class.getName());
    private static final long DEFAULT_TIMEOUT = 900000;
    private static final int MAX_BUFFER = 4500; //4500 fix for db insert 112640  110KiB;  AWS IOT limits max publish message size to 128KiB

    /**
     * use this for point relates functionality
     * @param points
     * @param command
     * @return
     * @throws Exception
     */
    private static IotData constructNewIotMessage(List<Point> points, FacilioCommand command) throws Exception {
        if ((points == null) || (points.isEmpty())) {
            throw new Exception("Exception occurred, points map is empty");
        }
        long controllerId = (long) points.get(0).getControllerId();
        LOGGER.info("Point Context value :"+points);
        return constructNewIotMessage(ControllerApiV2.getControllerFromDb(controllerId), command, points, null);
    }

    private static IotData constructNewIotMessage(List<Point> points, FacilioCommand command, Controller controller, int interval) throws Exception {
        if ((points == null) || (points.isEmpty())) {
            throw new Exception("Exception occurred, points map is empty");
        }
        FacilioContext context = new FacilioContext();
        context.put(AgentConstants.DATA_INTERVAL, interval);
        return constructNewIotMessage(controller, command, points, context);
    }
    private static IotData constructNewIotMessage(List<Point> points, FacilioCommand command,Controller controller) throws Exception {
        if ((points == null) || (points.isEmpty())) {
            throw new Exception("Exception occurred, points map is empty");
        }
        return constructNewIotMessage(controller, command, points, null);
    }

    private static IotData constructNewIotMessage(Controller controller, FacilioCommand command) throws Exception {
        return constructNewIotMessage(controller, command, null, null);
    }


    /**
     * THis method can buse to construct iot-messages specific for Facilio-agent's point related functionality
     *
     * @param controller {@link Controller} to which we need to send the command
     * @param command    {@link FacilioCommand} point related commands
     * @param points     {@link Point} points which should handled
     * @param context    {@link FacilioContext } for any extra parameters
     * @return {@link IotData} the actual data
     * @throws Exception
     */
    private static IotData constructNewIotMessage(Controller controller, FacilioCommand command, List<Point> points, FacilioContext context) throws Exception {
        if (controller == null) {
            throw new Exception("Exception occurred,  Controller not present");
        }
        IotData iotData = new IotData();
        long agentId = controller.getAgentId();
        AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
        FacilioAgent agent = agentBean.getAgent(agentId);
        if (agent != null) {
            if( ! agent.getConnected()){
                throw new Exception("Agent is not connected");
            }
            iotData.setAgentId(agentId);
            iotData.setAgent(agent);
            iotData.setCommand(command.asInt());
            JSONObject object = new JSONObject();
            if (command == FacilioCommand.SET) {
            		Map<String, String> orgInfo = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.IS_OLD_AGENT);
            		String isOldAgent = orgInfo.get(FacilioConstants.OrgInfoKeys.IS_OLD_AGENT);
            		if (isOldAgent != null && isOldAgent.equals("true")) {
            			constructMessageForOldAgent(controller, command, points, object);
            		}
            }
            if (object.isEmpty()) {
	            	object.put(AgentConstants.COMMAND, command.asInt());
	            	object.put(AgentConstants.CONTROLLER_TYPE, controller.getControllerType());
                JSONObject controllerJson = controller.getChildJSON();
                controllerJson.put(AgentConstants.NAME, controller.getName());
                object.put(AgentConstants.CONTROLLER, controllerJson);
	            	//object.putAll(controller.getChildJSON());
	            
	            	
	            	switch (command) {
	            	case RESET:
	            		//build reset message
	            		break;
	            		//
	            	case SET:
	            		JSONArray pointsData = MessengerUtil.getPointsData(points);
	            		for (Object pointsDatumObject : pointsData) {
	            			JSONObject pointdatum = (JSONObject) pointsDatumObject;
	            			Point point = points.get(pointsData.indexOf(pointdatum));
                            String actionName = point.getActionName();
                            if(actionName == null || actionName.equals(AgentConstants.OVERRIDE) || actionName.equals(AgentConstants.EMERGENCY_OVERRIDE)) {
                                pointdatum.put(AgentConstants.VALUE, point.getValue());
                            }
	            			pointdatum.put("controlId", point.getControlActionId());
	            			if(point.getCommandRetryCount() != null) {
	            				pointdatum.put("retryCount", point.getCommandRetryCount());
	            			}
                            if(actionName != null) {
                                pointdatum.put(AgentConstants.ACTION_NAME, actionName);
                            }
	            			if(pointdatum.containsKey(AgentConstants.DEVICE_ID)){
	            				pointdatum.remove(AgentConstants.DEVICE_ID);
	            			}
	            		}
	            		object.put(AgentConstants.POINTS, pointsData);
	            		break;
	            	case SUBSCRIBE:
	            	case CONFIGURE:
	            	case REMOVE:
	            	case UNSUBSCRIBE:
	            	case GET:
	            		JSONArray pointsArray = MessengerUtil.getPointsData(points);
                        LOGGER.info("JSON Array: " + pointsArray);
                        if (context != null && context.containsKey(AgentConstants.DATA_INTERVAL)) {
                            object.put(AgentConstants.DATA_INTERVAL, Integer.parseInt(context.get(AgentConstants.DATA_INTERVAL).toString()));
                        }
	            		object.put(AgentConstants.POINTS, pointsArray);
	            		break;
	            		//
	            	case SHUTDOWN:
	            		break;
	            	case PROPERTY:
	            		object.put(AgentConstants.PROPERTY, context.get(AgentConstants.PROPERTY));
	            	case STATS:
	            		break;
	            	case UPGRADE:
	            		break;
	            	case THREAD_DUMP:
	            		break;
	            	case ADD_CONTROLLER:
	            		break;
	            	case DISCOVER_POINTS:
                        if(agent.getAgentTypeEnum() == AgentType.FACILIO &&  (controller.getControllerType() == FacilioControllerType.BACNET_IP.asInt() || controller.getControllerType() == ControllerType.OPC_UA.getKey() ||
                                controller.getControllerType() == ControllerType.OPC_DA.getKey())){
                            object.put(AgentConstants.TIMEOUT_SEC, 300); // 5 mins
                        } else if(agent.getDiscoverPointsTimeOut()>0){
                            object.put(AgentConstants.TIMEOUT,agent.getDiscoverPointsTimeOut());
                        }
	            		break;
	            	case EDIT_CONTROLLER:
	            		break;
	            	case CONTROLLER_STATUS:
	            		break;
	            	case DISCOVER_CONTROLLERS:
                        if(agent.getDiscoverControllersTimeOut()>0) {
                            object.put(AgentConstants.TIMEOUT, agent.getDiscoverControllersTimeOut());
                        }
	            		break;
	            	}
            	
            }
            
	        	object.put("timestamp", System.currentTimeMillis());
	        	object.put("agent", agent.getName());
	        	object.put(AgentConstants.AGENT_ID, agent.getId()); // Agent_Id key must be changed to camelcase.
        	
            String objString = object.toJSONString();
            if (objString.length() > MAX_BUFFER) {
                List<IotMessage> messages = new ArrayList<>();
                JSONArray array = (JSONArray) object.get(AgentConstants.POINTS);
                int pointsSize = 0;
                JSONArray pointsArray = new JSONArray();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject point = (JSONObject) array.get(i);
                    pointsSize = pointsSize + point.toJSONString().length();
                    if (pointsSize < MAX_BUFFER) {
                        pointsArray.add(point);
                    } else {
                        JSONArray pointsArrayCopy = new JSONArray();
                        pointsArrayCopy.addAll(pointsArray);
                        object.put("points", pointsArrayCopy);
                        messages.add(MessengerUtil.getMessageObject(object, command));
                        pointsArray.clear();
                        pointsSize = point.toJSONString().length();
                        pointsArray.add(point);
                    }
                }
                if (pointsArray.size() > 0) {
                    object.put("points", pointsArray);
                    messages.add(MessengerUtil.getMessageObject(object, command));
                }
                iotData.setMessages(messages);
            } else {
                iotData.setMessages(Collections.singletonList(MessengerUtil.getMessageObject(object, command)));
            }
        } else {
            LOGGER.info("Exception Occurred, agent is null -> ");
        }
        return iotData;
    }
    
    @SuppressWarnings("unchecked")
	private static void constructMessageForOldAgent(Controller controller, FacilioCommand facilioCommand, List<Point> points, JSONObject object) throws Exception {
		
		IotCommandType command = IotCommandType.getCommandType(facilioCommand.toString());
		object.put("command", command.getName());
		
		object.put("deviceName", controller.getName());
		object.put("type", controller.getControllerType());
		object.put("subnetPrefix", 1l);
		object.put("broadcastAddress", "1");
		
		List<Map<String, Object>> instances = FieldUtil.getAsMapList(points, Point.class);
		instances.forEach(instance -> instance.put("instance", instance.get("name")));
		
		ControllerContext oldController = new ControllerContext();
		oldController.setControllerType(controller.getControllerType());
		JSONArray pointsList = ProtocolUtil.getPointsToPublish(oldController, instances, command);
		object.put("points", pointsList);
    }

public static boolean discoverPoints(long controllerId) throws Exception {
            IotData iotData = constructNewIotMessage(ControllerApiV2.getControllerFromDb(controllerId), FacilioCommand.DISCOVER_POINTS);
            MessengerUtil.addAndPublishNewAgentData(iotData);
            return true;
}

    public static boolean discoverPoints(Controller controller){
        try {
            IotData iotData = constructNewIotMessage(controller, FacilioCommand.DISCOVER_POINTS);
            MessengerUtil.addAndPublishNewAgentData(iotData);
            return true;
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        return false;
    }

    public static void configurePoints(List<Point> points, Controller controller, int interval) throws Exception {
        IotData iotData;
        if (interval > 0) {
            iotData = constructNewIotMessage(points, FacilioCommand.CONFIGURE, controller, interval);
        } else {
            iotData = constructNewIotMessage(points, FacilioCommand.CONFIGURE, controller);
        }
        MessengerUtil.addAndPublishNewAgentData(iotData);

    }

    public static void resetController(long controllerId) throws Exception {
        IotData iotDat = constructNewIotMessage(ControllerApiV2.getControllerFromDb(controllerId), FacilioCommand.RESET);
        MessengerUtil.addAndPublishNewAgentData(iotDat);
    }

    public static void setValue(Point point) throws Exception {
    	setValue(Collections.singletonList(point));
    }
    
    public static void setValue(List<Point> points) throws Exception {
        IotData iotData = constructNewIotMessage(points, FacilioCommand.SET);
        MessengerUtil.addAndPublishNewAgentData(iotData);
    }

    public static void deletePoints(List<Point> points) throws Exception {
        IotData iotData = constructNewIotMessage(points, FacilioCommand.REMOVE);
        MessengerUtil.addAndPublishNewAgentData(iotData);
    }

    public static void unConfigurePoints(List<Point> points) throws Exception {
        IotData iotData = constructNewIotMessage(points, FacilioCommand.REMOVE);
        MessengerUtil.addAndPublishNewAgentData(iotData);
    }

    public static void subscribeUnscbscribePoints(List<Point> points, FacilioCommand command) throws Exception {
        IotData iotData = constructNewIotMessage(points, command);
        MessengerUtil.addAndPublishNewAgentData(iotData);
    }

    public static void sendConfigureModbusTcpPoint(ModbusTcpPointContext tcpPointContext) throws Exception {
        IotData iotData = constructNewIotMessage(new ArrayList<>(Arrays.asList(tcpPointContext)), FacilioCommand.CONFIGURE);
        MessengerUtil.addAndPublishNewAgentData(iotData);
    }

    public static void sendConfigureModbusTcpPoint(ModbusTcpPointContext tcpPointContext, Controller controller) throws Exception {
        ArrayList<Point> points = new ArrayList<>(Arrays.asList(tcpPointContext));
        sendConfigureModbusTcpPoints(controller, points);
        return;
    }

    public static void sendConfigureModbusTcpPoints(Controller controller, List<Point> points) throws Exception {
        IotData iotData = constructNewIotMessage(points, FacilioCommand.CONFIGURE, controller);
        MessengerUtil.addAndPublishNewAgentData(iotData);
    }

    public static void sendConfigureModbusRtuPoint(ModbusRtuPointContext tcpPointContext) throws Exception {
        IotData iotData = constructNewIotMessage(new ArrayList<>(Arrays.asList(tcpPointContext)), FacilioCommand.CONFIGURE);
        MessengerUtil.addAndPublishNewAgentData(iotData);
    }

}
