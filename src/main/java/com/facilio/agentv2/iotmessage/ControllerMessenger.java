package com.facilio.agentv2.iotmessage;

import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.point.Point;
import com.facilio.chain.FacilioContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ControllerMessenger {
    private static final Logger LOGGER = LogManager.getLogger(ControllerMessenger.class.getName());

    private static final int MAX_BUFFER = 45000; //45000 fix for db insert 112640  110KiB;  AWS IOT limits max publish message size to 128KiB

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
        return constructNewIotMessage((long) points.get(0).getControllerId(), command, points, null);
    }


    private static IotData constructNewIotMessage(long controllerId, FacilioCommand command) throws Exception {
        return constructNewIotMessage(controllerId, command, null, null);
    }

    private static IotData constructNewIotMessage(long controllerId, FacilioCommand command, List<Point> points, FacilioContext context) throws Exception {
            LOGGER.info(" constructing controller message ");
            Controller controller = ControllerApiV2.getControllerFromDb(controllerId);
            if (controller == null) {
                throw new Exception("Exception occurred,  Controller not present");
            }
            IotData iotData = new IotData();
            long agentId = controller.getAgentId();
            LOGGER.info(" controller json " + controller.toJSON());
            LOGGER.info(" agentId  " + agentId);
            FacilioAgent agent = AgentApiV2.getAgent(agentId);
            if (agent != null) {
                iotData.setAgentId(agentId);
                JSONObject object = new JSONObject();
                object.put(AgentConstants.COMMAND, command.asInt());
                LOGGER.info(" controller type  "+controller.getControllerType());
                object.put(AgentConstants.TYPE, controller.getControllerType());
                object.put(AgentConstants.IDENTIFIER, controller.makeIdentifier());
                object.putAll(controller.getChildJSON());
                object.put("timestamp", System.currentTimeMillis());
                object.put("agent", agent.getName());
                object.put(AgentConstants.AGENT_ID, agent.getId()); // Agent_Id key must be changed to camelcase.

                switch (command) {
                    case RESET:
                        //build reset message
                        break;
                    //
                    case SET:
                        JSONArray pointsData = MessengerUtil.getPointsData(points);
                        JSONObject pointData = (JSONObject) pointsData.get(0);
                        pointData.put(AgentConstants.VALUE, context.get(AgentConstants.VALUE));
                        object.put(AgentConstants.POINTS, pointsData);
                        break;
                    case CONFIGURE:
                    case REMOVE:
                    case SUBSCRIBE:
                    case UNSUBSCRIBE:
                    case GET:
                        object.put(AgentConstants.POINTS, MessengerUtil.getPointsData(points));
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
                        break;
                    case EDIT_CONTROLLER:
                        break;
                    case CONTROLLER_STATUS:
                        break;
                    case DISCOVER_CONTROLLERS:
                        break;
                }
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
                            object.put("points", pointsArray);
                            messages.add(MessengerUtil.getMessageObject(object));
                            pointsArray.clear();
                            pointsSize = point.toJSONString().length();
                            pointsArray.add(point);
                        }
                    }
                    if (pointsArray.size() > 0) {
                        object.put("points", pointsArray);
                        messages.add(MessengerUtil.getMessageObject(object));
                    }
                    iotData.setMessages(messages);
                } else {
                    iotData.setMessages(Collections.singletonList(MessengerUtil.getMessageObject(object)));
                }
            } else {
                LOGGER.info("Exception Occurred, agent is null -> ");
            }
        return iotData;
    }

public static boolean discoverPoints(long controllerId){
        try {
            IotData iotData = constructNewIotMessage(controllerId, FacilioCommand.DISCOVER_POINTS);
            LOGGER.info(" iot data "+iotData);
            MessengerUtil.addAndPublishNewAgentData(iotData);
        }catch (Exception e){
            LOGGER.info("Exception occurred ",e);
        }
        return false;
}

}
