package com.facilio.agentv2.commands;

import com.facilio.agent.AgentType;
import com.facilio.agent.controller.FacilioControllerType;
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
                    Map<String, Object> deviceData = new HashMap<>();
                    pointsJSON = (JSONObject) pointData.get(0);

                    for (Object key : pointsJSON.keySet()) {
                        String pointName = (String) key;
                        if (pointsJSON.containsKey(pointName)) {
                            Object value = pointsJSON.get(key);
                            if (value != null) {
                                deviceData.put(pointName.trim(), value);
                                pointNames.add(pointName);
                            }
                        }
                    }

                    if (!pointNames.isEmpty()) {
                        List<Point> pointsFromDb = FieldUtil.getAsBeanListFromMapList(getPointsFromDb(pointNames, controller), PointsAPI.getPointType(FacilioControllerType.valueOf(controller.getControllerType())));
                        if (pointsFromDb.size() < pointNames.size() && controller.getAgent().getAgentTypeEnum().allowAutoAddition()) {
                            Set<String> pointsFromDbSet = new HashSet<>();
                            pointsFromDb.forEach(point -> pointsFromDbSet.add(point.getName()));
                            Set<String> pointNamesSet = new HashSet<>(pointNames);
                            pointNamesSet.removeAll(pointsFromDbSet);
                            List<Map<String, Object>> pointRecordsToAdd = new ArrayList<>();
                            List<Point> points = new ArrayList<>();
                            for (String name : pointNamesSet) {
                                MiscPoint point = new MiscPoint(agent.getId(), controller.getControllerId());
                                point.setName(name);
                                point.setDeviceName(controller.getName());
                                point.setConfigureStatus(3);
                                point.setControllerId(controller.getId());
                                point.setPath(name);
                                point.setCreatedTime(System.currentTimeMillis());
                                points.add(point);
                                Map<String, Object> pointMap = FieldUtil.getAsProperties(point.toJSON());
                                pointRecordsToAdd.add(pointMap);
                            }
                            PointsUtil.addPoints(controller, pointRecordsToAdd);
                            //get newly added points from db with point ids
                            List<Point> newlyAddedPoints = FieldUtil.getAsBeanListFromMapList(getPointsFromDb(new ArrayList<>(pointNamesSet), controller), PointsAPI.getPointType(FacilioControllerType.valueOf(controller.getControllerType())));
                            pointsFromDb.addAll(newlyAddedPoints);
                        }
                        context.put(FacilioConstants.ContextNames.DataProcessor.DATA_SNAPSHOT, deviceData);
                        Map<String, Point> pointRecords = new HashMap<>();
                        for (Point p : pointsFromDb) {
                            pointRecords.put(p.getName(), p);
                        }
                        context.put(FacilioConstants.ContextNames.DataProcessor.POINT_RECORDS, pointRecords);

                    } else {
                        LOGGER.info("points empty");
                    }
                }

            }else {
                throw new Exception(" payload must be a jsonObject");
            }

        }else {
            throw new Exception("payload missing from context->"+context);
        }

        return false;
    }


}
