package com.facilio.agentv2.point;

import com.facilio.agent.AgentType;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.bacnet.BacnetIpPointContext;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.GetControllerRequest;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.modbusrtu.RtuNetworkContext;
import com.facilio.bacnet.BACNetUtil;
import com.facilio.db.builder.DBUtil;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.Json;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class PointsUtil
{
    private static final Logger LOGGER = LogManager.getLogger(PointsUtil.class.getName());
    private long agentId;
    private long controllerId;

    public PointsUtil(long agentId, long controllerId) {
        this.agentId = agentId;
        this.controllerId = controllerId;
    }

    public long getAgentId() {
        return agentId;
    }


    public long getControllerId() {
        return controllerId;
    }

   /* public boolean processPoints(JSONObject payload, Controller controller) throws Exception{
        LOGGER.info(" processing points ");
        String identifier;
        identifier = controller.makeIdentifier();
        if(containsValueCheck(AgentConstants.DATA,payload)){
            JSONArray pointsJSON = (JSONArray) payload.get(AgentConstants.DATA);
            List<Point> points = new ArrayList<>();
            for (Object o : pointsJSON) {
                JSONObject pointJSON = (JSONObject) o;
                pointJSON.put(AgentConstants.DEVICE_NAME,identifier);
                pointJSON.put(AgentConstants.FIELD_DEVICES,);
                pointJSON.put(AgentConstants.CONTROLLER_ID,controller.getId());
                pointJSON.put(AgentConstants.POINT_TYPE,payload.get(AgentConstants.TYPE));
                try {
                    Point point = PointsAPI.getPointFromJSON(pointJSON);
                    point.setControllerId(controllerId);
                    if(point != null){
                        points.add(point);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LOGGER.info(" points processed "+points.size());
            for (Point point : points) {
                boolean pointEntry = PointsAPI.addPoint(point);
                LOGGER.info("point add status -> "+pointEntry);;
            }
        }else {
            LOGGER.info(" Exception occurred, pointsData missing from payload -> "+payload);
        }
        return false;
    }*/

    public static boolean processPoints(JSONObject payload, Controller controller, FacilioAgent agent) throws Exception {
        LOGGER.info(" processing point " + controller.toJSON());


        if (containsValueCheck(AgentConstants.DATA, payload)) {
            JSONArray pointsJSON = (JSONArray) payload.get(AgentConstants.DATA);
            long incomingCount = pointsJSON.size();
            if (incomingCount == 0) {
                throw new Exception(" pointJSON cant be empty");
            }
            // Controller controller;
           /* int deviceType = device.getControllerType();

            GetControllerRequest getControllerRequest = new GetControllerRequest()
                    .forDevice(device.getId()).ofType(FacilioControllerType.valueOf(deviceType));
            if (deviceType == FacilioControllerType.MODBUS_RTU.asInt()) {
                JSONObject controllerProps = (JSONObject) device.getControllerProps().get(AgentConstants.CONTROLLER);
                long agentId = device.getAgentId();
                String comPort;
                if (controllerProps.containsKey(AgentConstants.NETWORK)) {
                    JSONObject network = (JSONObject) controllerProps.get(AgentConstants.NETWORK);
                    comPort = network.get(AgentConstants.COM_PORT).toString();
                } else {
                    comPort = controllerProps.get(AgentConstants.COM_PORT).toString();
                }
                RtuNetworkContext network = RtuNetworkContext.getRtuNetworkContext(agentId, comPort);
                if (network != null) {
                    controllerProps.put(AgentConstants.NETWORK_ID, network.getId());
                }
                getControllerRequest.withControllerProperties((JSONObject) device.getControllerProps().get(AgentConstants.CONTROLLER), FacilioControllerType.MODBUS_RTU)
                        .withAgentId(device.getAgentId());
            }
            controller = getControllerRequest.getController();*/

            //getting points name
            List<String>pointName = (List<String>) pointsJSON.stream().map(x -> ((JSONObject)x).get(AgentConstants.NAME).toString()).collect(Collectors.toList());

            //getting existing points name from DB
            List<String>existingPoints = PointsAPI.getPointsFromDb(pointName,controller).stream()
                    .map(name -> name.get(AgentConstants.NAME).toString())
                    .collect(Collectors.toList());


            List<Map<String,Object>> points = new ArrayList<>();
            for (Object o : pointsJSON) {
                JSONObject pointJSON = (JSONObject) o;
              /*  pointJSON.put(AgentConstants.DEVICE_NAME, device.getName());
                pointJSON.put(AgentConstants.DEVICE_ID, device.getId());*/
                pointJSON.put(AgentConstants.POINT_TYPE, controller.getControllerType());
                pointJSON.put(AgentConstants.CONTROLLER_ID, controller.getId());
                try {
                    if(!existingPoints.contains(pointJSON.get(AgentConstants.NAME))){
                        Point point = PointsAPI.getPointFromJSON(pointJSON);
                      if (!pointJSON.containsKey(AgentConstants.DISPLAY_NAME) && pointJSON.containsKey(AgentConstants.NAME)) {
                        point.setDisplayName(pointJSON.get(AgentConstants.NAME).toString());
                     }
                      	point.setCreatedTime(System.currentTimeMillis());
                        setPointWritable(pointJSON, point);
                        if (point != null) {
                         point.setControllerId(controller.getId());
                         point.setAgentId(controller.getAgentId());
                         int agentType = agent.getAgentType();
                         if (agentType == AgentType.CUSTOM.getKey() || agentType == AgentType.REST.getKey() || agentType == AgentType.CLOUD.getKey()) {
                            point.setConfigureStatus(PointEnum.ConfigureStatus.CONFIGURED.getIndex());
                        }
                        if (controller.getControllerType() == FacilioControllerType.MODBUS_IP.asInt() ||
                                controller.getControllerType() == FacilioControllerType.MODBUS_RTU.asInt() ||
                                controller.getControllerType() == FacilioControllerType.RDM.asInt()) {
                                    if (agentType == AgentType.FACILIO.getKey() || agentType == AgentType.AGENT_SERVICE.getKey()) {
                                        point.setConfigureStatus(PointEnum.ConfigureStatus.CONFIGURED.getIndex());
                                    }
                        }
                        Map<String, Object> pointMap = FieldUtil.getAsProperties(point.toJSON());

                        points.add(pointMap);

                    }
                }
                } catch (Exception e) {
                    LOGGER.info("Exception occurred while getting point",e);
                }
            }

            if (points.size() == 0) {
                throw new Exception(" points to add can't be empty");
            }

            addPoints(controller,points);




         /*   long pointsToBeAdded = points.size();
            //TODO try bulk insert first
            long pointsAdded = 0;
            for (Point point : points) {
                if(!existingPoints.contains(point.getName())) {
                    PointsAPI.applyBacnetDefaultWritableRule(point);
                    //TODO make it bulk add
                    boolean pointEntry = PointsAPI.addPoint(point);

                    if (!pointEntry) {
                        LOGGER.info("Exception while adding point," + point.toJSON());
                    } else {
                        pointsAdded++;
                    }
                }
            }
            LOGGER.info("-----DISCOVERPOINTS SUMMARY POINTDATAIN->" + incomingCount + "  POINTSTOBEADDED->" + pointsToBeAdded + "  POINTSADDED->" + pointsAdded); */


        }else {
            LOGGER.info(" Exception occurred, pointsData missing from payload -> "+payload);
        }
        return true;
    }

    private static void setPointWritable(JSONObject pointJSON, Point point) {

        if(point.getControllerType() == FacilioControllerType.BACNET_IP){
            BacnetIpPointContext bacnetIpPoint = (BacnetIpPointContext) point;
            if(BACNetUtil.InstanceType.valueOf(bacnetIpPoint.getInstanceType()).isWritable()){
                point.setWritable(true);
                point.setAgentWritable(true);
            }
        }
		// TODO Auto-generated method stub
		if(pointJSON.containsKey(AgentConstants.WRITABLE)) {
			Boolean value = Boolean.parseBoolean(pointJSON.get(AgentConstants.WRITABLE).toString());
			if(value != null && value) {
				point.setAgentWritable(value);
			}else {
				point.setAgentWritable(false);
			}
			
		}
	}

	private static boolean containsValueCheck(String key, Map<String,Object> jsonObject){
        if(jsonObject.containsKey(key) && ( jsonObject.get(key) != null) ){
            return true;
        }
        return false;
    }
    //BULK INSERT
    public static void addPoints(Controller controller,List<Map<String,Object>>points) throws Exception {
        FacilioControllerType controllerType = FacilioControllerType.valueOf(controller.getControllerType());

        List<FacilioField> fields = PointsAPI.getChildPointFields(controllerType);

        DBUtil.insertValuesWithJoin(PointsAPI.getPointModule(controllerType),fields, FieldUtil.getAsMapList(points,PointsAPI.getPointType(controllerType)));

    }
}
