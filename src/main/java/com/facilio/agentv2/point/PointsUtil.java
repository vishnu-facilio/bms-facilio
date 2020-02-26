package com.facilio.agentv2.point;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.device.Device;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                pointJSON.put(AgentConstants.FIELD_DEIVICES,);
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

    public static boolean processPoints(JSONObject payload, Device device) throws Exception {
        LOGGER.info(" processing point "+device.getControllerProps());
        if (containsValueCheck(AgentConstants.DATA, payload)) {
            JSONArray pointsJSON = (JSONArray) payload.get(AgentConstants.DATA);
            if (pointsJSON.size() == 0) {
                throw new Exception(" pointJSON cant be empty");
            }
            List<Point> points = new ArrayList<>();
            for (Object o : pointsJSON) {
                JSONObject pointJSON = (JSONObject) o;
                pointJSON.put(AgentConstants.DEVICE_NAME, device.getName());
                pointJSON.put(AgentConstants.DEVICE_ID, device.getId());
                pointJSON.put(AgentConstants.POINT_TYPE, device.getControllerType());
                LOGGER.info(" point json " + pointJSON);
                try {
                    Point point = PointsAPI.getPointFromJSON(pointJSON);
                    LOGGER.info("point made");
                    LOGGER.info("point to json ->" + FieldUtil.getAsJSON(point));
                    if (point != null) {
                        if (point.getControllerId() > 0) {
                            point.setControllerId(-1);
                        }
                        points.add(point);
                    }
                } catch (Exception e) {
                    LOGGER.info("Exception occurred while getting point",e);
                }
            }
            if (points.size() == 0) {
                throw new Exception(" points to cadd can't be empty");
            }
            LOGGER.info(" points processed " + points.size());
            //TODO try bulk insert first
            for (Point point : points) {
                boolean pointEntry = PointsAPI.addPoint(point); //TODO make it bulk add
                if (!pointEntry) {
                    LOGGER.info("Exception while adding point," + point.toJSON());
                }
                LOGGER.info("point add status -> " + pointEntry); //TODO
            }
        }else {
            LOGGER.info(" Exception occurred, pointsData missing from payload -> "+payload);
        }
        return true;
    }

    private static boolean containsValueCheck(String key, Map<String,Object> jsonObject){
        if(jsonObject.containsKey(key) && ( jsonObject.get(key) != null) ){
            return true;
        }
        return false;
    }








}
