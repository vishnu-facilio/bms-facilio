package com.facilio.agentv2.point;

import com.facilio.agent.AgentType;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.bacnet.BacnetIpPointContext;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.modbustcp.ModbusTcpPointContext;
import com.facilio.agentv2.modbustcp.ModbusUtils;
import com.facilio.agentv2.rdm.RdmControllerContext;
import com.facilio.bacnet.BACNetUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.DBUtil;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;


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


            //getting points name
            List<String>pointName = (List<String>) pointsJSON.stream().map(x -> ((JSONObject)x).get(AgentConstants.NAME).toString()).collect(Collectors.toList());

            //getting existing points name from DB
            List<String>existingPoints = PointsAPI.getPointsFromDb(pointName,controller).stream()
                    .map(name -> name.get(AgentConstants.NAME).toString())
                    .collect(Collectors.toList());

            LOGGER.info("Existing Points count : " + existingPoints.size());

            List<Map<String,Object>> points = new ArrayList<>();
            for (Object o : pointsJSON) {
                JSONObject pointJSON = (JSONObject) o;
              /*  pointJSON.put(AgentConstants.DEVICE_NAME, device.getName());
                pointJSON.put(AgentConstants.DEVICE_ID, device.getId());*/
                pointJSON.put(AgentConstants.POINT_TYPE, controller.getControllerType());
                pointJSON.put(AgentConstants.CONTROLLER_ID, controller.getId());
                try {
                    Object pName = pointJSON.get(AgentConstants.NAME);
                    if(!existingPoints.contains(pName)){
                        Point point = PointsAPI.getPointFromJSON(pointJSON);
                        if (!pointJSON.containsKey(AgentConstants.DISPLAY_NAME) && pointJSON.containsKey(AgentConstants.NAME)) {
                            point.setDisplayName(pointJSON.get(AgentConstants.NAME).toString());
                        }
                        point.setCreatedTime(System.currentTimeMillis());
                        setPointWritable(pointJSON, point);
                        if (point != null) {
                            point.setControllerId(controller.getId());
                            point.setAgentId(controller.getAgentId());
                            point.setDeviceName(controller.getName());
                            int agentType = agent.getAgentType();
                            if (agentType == AgentType.CUSTOM.getKey() || agentType == AgentType.REST.getKey() || agentType == AgentType.CLOUD.getKey() || agentType == AgentType.MQTT.getKey()) {
                                point.setConfigureStatus(PointEnum.ConfigureStatus.CONFIGURED.getIndex());
                            }
                            if (controller.getControllerType() == FacilioControllerType.MODBUS_IP.asInt() ||
                                    controller.getControllerType() == FacilioControllerType.MODBUS_RTU.asInt() ||
                                    (controller.getControllerType() == FacilioControllerType.RDM.asInt() && ((RdmControllerContext) controller).getIsTdb())) {
                                if (agentType == AgentType.FACILIO.getKey() || agent.getAgentTypeEnum().isAgentService()) {
                                    point.setConfigureStatus(PointEnum.ConfigureStatus.CONFIGURED.getIndex());
                                }
                            }
                            Map<String, Object> pointMap = FieldUtil.getAsProperties(point.toJSON());

                            points.add(pointMap);

                        }
                    }else{
                        LOGGER.info("Point already exists : " + pName);
                    }
                } catch (Exception e) {
                    LOGGER.info("Exception occurred while getting point",e);
                }
            }

            if (points.size() == 0) {
                throw new Exception(" points to add can't be empty");
            }
            FacilioChain addPointsChain = TransactionChainFactory.getAddPointsChain();
            FacilioContext context = new FacilioContext();
            context.put(AgentConstants.CONTROLLER,controller);
            context.put(AgentConstants.AGENT,agent);
            context.put(AgentConstants.POINTS,points);
            addPointsChain.setContext(context);
            addPointsChain.execute();





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
        if(point.getControllerType() == FacilioControllerType.MODBUS_IP){
            ModbusTcpPointContext modbusTcpPoint = (ModbusTcpPointContext) point;
            if(ModbusUtils.RegisterType.valueOf(Math.toIntExact(modbusTcpPoint.getRegisterType())).isWritable()){
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
        addPoints(controllerType,points);
    }

    public static void addPoints(FacilioControllerType controllerType, List<Map<String,Object>>points) throws Exception {
        List<FacilioField> fields = PointsAPI.getChildPointFields(controllerType);
        DBUtil.insertValuesWithJoin(PointsAPI.getPointModule(controllerType),fields, FieldUtil.getAsMapList(points,PointsAPI.getPointType(controllerType)));

    }
}
