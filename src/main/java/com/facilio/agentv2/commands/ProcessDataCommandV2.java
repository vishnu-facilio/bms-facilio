package com.facilio.agentv2.commands;

import com.facilio.agent.AgentType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.misc.MiscPoint;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.agentv2.point.PointsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

import static com.facilio.agentv2.point.PointsAPI.getDevicePointsAsMapList;
import static com.facilio.agentv2.point.PointsAPI.getPointsFromDb;

public class ProcessDataCommandV2 extends AgentV2Command {

    private static final Logger LOGGER = LogManager.getLogger(ProcessDataCommandV2.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.FORK_READING_POST_PROCESSING);
        boolean forkPostProcessing = orgInfoMap == null ? false : Boolean.parseBoolean(orgInfoMap.get(FacilioConstants.OrgInfoKeys.FORK_READING_POST_PROCESSING));
        LOGGER.info("Fork post processing in data process v2 => "+forkPostProcessing);
        context.put(FacilioConstants.ContextNames.FORK_POST_READING_PROCESSING, forkPostProcessing);

        if( containsCheck(AgentConstants.DATA,context) ){
            Object payloadObject = context.get(AgentConstants.DATA);
            if( payloadObject instanceof JSONObject){
                JSONObject payload = (JSONObject) payloadObject;

                Controller controller = (Controller) context.get(AgentConstants.CONTROLLER);
                FacilioAgent agent = controller.getAgent();
                if( containsCheck(AgentConstants.DATA,payload)){
                    JSONArray pointData = (JSONArray) payload.get(AgentConstants.DATA);
                    List<String> pointNames = new ArrayList<>();
                    JSONObject pointsJSON;
                    Map<String, Map<String,String>> deviceData= new HashMap<>();
                    if (controller !=null) {
                        deviceData.put(controller.getName(),new HashMap<>());
                    }
                    else  {
                        deviceData.put("UNKNOWN",new HashMap<>());
                    }
                    pointsJSON = (JSONObject) pointData.get(0);

                    for (Object key : pointsJSON.keySet()) {
                        String pointName = (String) key;
                        if (containsCheck(pointName, pointsJSON)) {
                        	pointName = pointName.trim();
                            pointNames.add(pointName);
                            if (controller != null) {
                                deviceData.get(controller.getName()).put(pointName, String.valueOf(pointsJSON.get(key)));
                            } else {
                                deviceData.get("UNKNOWN").put(pointName, String.valueOf(pointsJSON.get(key)));
                            }
                        }
                    }
                    context.put("DEVICE_DATA_2", deviceData);

                    if( ! pointNames.isEmpty()){
                        List<Map<String, Object>> pointsFromDb = getPointsFromDb(pointNames,controller);

                            if (pointsFromDb.size() < pointNames.size() && controller != null &&
                                    (controller.getAgent().getAgentType() == AgentType.CLOUD.getKey()
                                            || controller.getAgent().getAgentType() == AgentType.REST.getKey()
                                            || controller.getAgent().getAgentType() == AgentType.FACILIO.getKey()
                                            || controller.getAgent().getAgentType() == AgentType.MQTT.getKey())) {
                                Set<String> pointsFromDbSet = new HashSet<>();
                                pointsFromDb.forEach(row -> pointsFromDbSet.add(row.get("name").toString()));
                                Set<String> pointNamesSet = new HashSet<>(pointNames);
                                pointNamesSet.removeAll(pointsFromDbSet);
                                List<Map<String, Object>> points = new ArrayList<>();
                                for (String name : pointNamesSet) {
                                    MiscPoint point = new MiscPoint(agent.getId(), controller.getControllerId());
                                    point.setName(name);
                                    point.setDeviceName(controller.getName());
                                    point.setConfigureStatus(3);
                                    point.setControllerId(controller.getId());
                                    point.setPath(name);
                                    point.setCreatedTime(System.currentTimeMillis());
                                    pointsFromDb.add(point.getPointJSON());
                                    
                                    Map<String, Object> pointMap = FieldUtil.getAsProperties(point.toJSON());
                                    points.add(pointMap);
                                }
                                PointsUtil.addPoints(controller, points);
                                
                        }
                        if( ! pointsFromDb.isEmpty() && controller!=null){
                            for (Map<String, Object> pointRow : pointsFromDb) {
                                if( ! pointRow.isEmpty()){
                                    pointRow.put(AgentConstants.CONTROLLER_NAME, controller.getName());
                                    reformatDataPoint(pointRow);
                                }
                            }

                            context.put("DATA_POINTS",pointsFromDb);
                        }
                        if(! pointsFromDb.isEmpty() && controller==null){
                            for (Map<String, Object> pointRow : pointsFromDb) {
                                if( ! pointRow.isEmpty()){
                                    pointRow.put(AgentConstants.CONTROLLER_NAME, "UNKNOWN");
                                    reformatDataPoint(pointRow);
                                }
                            }
                            context.put("DATA_POINTS_WITHOUT_CONTROLLER",pointsFromDb);
                        }

                    }else {
                        LOGGER.info("points empty");
                    }
                }

            }else {
                throw new Exception(" payload must be a jsonObject");
            }

        }else {
            throw new Exception("payload missing from context->"+context);
        }
       /* LOGGER.info("--------process data command v2---------");
        for (Object key : context.keySet()) {
            LOGGER.info(key+"->"+context.get(key));
        }
        LOGGER.info("----------------------------------------");*/

        return false;
    }

    private void reformatDataPoint(Map<String, Object> pointRow) {

        if(containsCheck(AgentConstants.NAME,pointRow)){
            pointRow.put("instance",pointRow.get(AgentConstants.NAME));
        }
        if(containsCheck(AgentConstants.CONTROLLER_NAME,pointRow)){
            pointRow.put("device",pointRow.get(AgentConstants.CONTROLLER_NAME));
        }
        if(containsCheck(AgentConstants.NAME,pointRow)){
            pointRow.put("instanceName",pointRow.get(AgentConstants.NAME));
        }
    }




}
