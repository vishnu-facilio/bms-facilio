/*
package com.facilio.agentnew.iotmessage;

import com.facilio.agent.AgentKeys;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.fw.constants.PublishType;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.FacilioAgent;
import com.facilio.agentnew.NewAgentAPI;
import com.facilio.agentnew.controller.Controller;
import com.facilio.agentnew.controller.NewControllerAPI;
import com.facilio.agentnew.point.Point;
import com.facilio.bmsconsole.context.PublishData;
import com.facilio.bmsconsole.context.PublishMessage;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.chain.FacilioContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ControllerMessenger {
    private static final Logger LOGGER = LogManager.getLogger(ControllerMessenger.class.getName());

    private static final int MAX_BUFFER = 45000; //45000 fix for db insert 112640  110KiB;  AWS IOT limits max publish message size to 128KiB

    private static PublishData constructNewIotMessage(List<Point> points, FacilioCommand command) throws Exception {
        if ((points == null) || (points.isEmpty())) {
            throw new Exception("Exception occurred, points map is empty");
        }
        return constructNewIotMessage((long) points.get(0).getControllerId(), command, points, null);
    }

    private static PublishData constructNewIotMessage(long controllerId, FacilioContext context) throws Exception {
        return constructNewIotMessage(controllerId, FacilioCommand.PROPERTY, null, context);
    }

    private static PublishData constructNewIotMessage(long controllerId, FacilioCommand command) throws Exception {
        return constructNewIotMessage(controllerId, command, null, null);
    }

    private static PublishData constructNewIotMessage(long controllerId, FacilioCommand command, List<Point> points, FacilioContext context) throws Exception {
        Controller controller = NewControllerAPI.getControllerFromDb(controllerId);
        if (controller == null) {
            throw new Exception("Exception occurred,  Controller not present");
        }
        PublishData NewPublishData = new PublishData();
        long agentId = controller.getAgentId();
        LOGGER.info(" controller json " + controller.toJSON());
        LOGGER.info(" agentId  " + agentId);
        FacilioAgent agent = NewAgentAPI.getAgent(agentId);
        if (agent != null) {
            JSONObject object = new JSONObject();
            object.put(AgentConstants.COMMAND, command.asInt());
            object.put(AgentConstants.TYPE, controller.getType());
            object.put(AgentConstants.IDENTIFIER, controller.makeIdentifier());
            object.putAll(controller.getChildJSON());
            object.put("timestamp", System.currentTimeMillis());
            object.put("agent", agent.getName());
            object.put(AgentKeys.AGENT_ID, agent.getId()); // Agent_Id key must be changed to camelcase.

            switch (command) {
                case RESET:
                    //build reset message
                    break;
                case PING:
                    object.put(AgentConstants.PUBLISH_TYPE, PublishType.ACK.asInt());
                    object.put("pingAgent", agent.getName());
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
                List<PublishMessage> messages = new ArrayList<>();
                JSONArray array = (JSONArray) object.get("points");
                int pointsSize = 0;
                JSONArray pointsArray = new JSONArray();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject point = (JSONObject) array.get(i);
                    pointsSize = pointsSize + point.toJSONString().length();
                    if (pointsSize < MAX_BUFFER) {
                        pointsArray.add(point);
                    } else {
                        object.put("points", pointsArray);
                        // messages.add(IoTMessageAPI.getMessageObject(object));
                        pointsArray.clear();
                        pointsSize = point.toJSONString().length();
                        pointsArray.add(point);
                    }
                }
                if (pointsArray.size() > 0) {
                    object.put("points", pointsArray);
                    //messages.add(IoTMessageAPI.getMessageObject(object));
                }
                NewPublishData.setMessages(messages);
            } else {
                // NewPublishData.setMessages(Collections.singletonList(IoTMessageAPI.getMessageObject(object)));
            }
        } else {
            LOGGER.info("Exception Occurred, agent is null -> ");
        }
        return NewPublishData;
    }

    public static PublishData publishNewIotMessage(List<Point> points, FacilioCommand command) throws Exception {
        PublishData data = constructNewIotMessage(points, command);
        return addAndNewPublishData(data);
    }

    public static PublishData publishIotMessage(long controllerId, FacilioCommand command) throws Exception {
        PublishData data = constructNewIotMessage(controllerId, command);
        return addAndNewPublishData(data);
    }

public static IotData publishIotMessageToAgent(long agentId, FacilioCommand command) throws Exception{
        IorD data = constructNewIotMessage()
    }




    public static PublishData publishIotMessage(long controllerId, FacilioContext context) throws Exception {
        PublishData data = constructNewIotMessage(controllerId, context);
        return addAndNewPublishData(data);
    }

    static PublishData addAndNewPublishData(PublishData data) throws Exception {

        //NewIotMessageAPI.addIotData(data);
        //NewIotMessageAPI.addPublishMessage(data.getId(), data.getMessages());

        if (data.getCommandEnum() != IoTMessageAPI.IotCommandType.GET) {
            // Pinging device to check if it is active
            PublishData pingData = constructNewIotMessage(data.getControllerId(), FacilioCommand.PING);
            pingData.setId(data.getId());
            // IoTMessageAPI.publishIotMessage(pingData, data.getId());
        } else {
            // IoTMessageAPI.publishIotMessage(data, -1);
        }

        return data;
    }


}
*/
