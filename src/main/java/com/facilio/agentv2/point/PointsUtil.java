package com.facilio.agentv2.point;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
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


    public boolean processPoints(JSONObject payload, Controller controller){
        LOGGER.info(" processing points ");
        String identifier;
        try {
            identifier = controller.makeIdentifier();
        } catch (Exception e) {
            LOGGER.info("Exception occurred ",e);
            return false;
        }
        if(containsValueCheck(AgentConstants.DATA,payload)){
            JSONArray pointsJSON = (JSONArray) payload.get(AgentConstants.DATA);
            List<Point> points = new ArrayList<>();
            for (Object o : pointsJSON) {
                JSONObject pointJSON = (JSONObject) o;
                pointJSON.put(AgentConstants.DEVICE_NAME,identifier);
                pointJSON.put(AgentConstants.CONTROLLER_ID,controller.getId());
                pointJSON.put(AgentConstants.POINT_TYPE,payload.get(AgentConstants.TYPE));
                try {
                    Point point = PointsAPI.getPointFromJSON(pointJSON,agentId,controllerId);
                    if(point != null){
                        points.add(point);
                        LOGGER.info(" point generated " + point.toJSON());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LOGGER.info(" points processed "+points.size());
            for (Point point : points) {
                LOGGER.info("point to add to db point -> " + point.toJSON());
                boolean pointEntry = PointsAPI.addPoint(point);
                LOGGER.info("point add status -> "+pointEntry);;
            }
        }else {
            LOGGER.info(" Exception occurred, pointsData missing from payload -> "+payload);
        }
        return false;
    }



    private static boolean containsValueCheck(String key, Map<String,Object> jsonObject){
        if(jsonObject.containsKey(key) && ( jsonObject.get(key) != null) ){
            return true;
        }
        return false;
    }








}
