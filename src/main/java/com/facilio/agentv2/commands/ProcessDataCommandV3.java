package com.facilio.agentv2.commands;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.misc.MiscPoint;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

import static com.facilio.agentv2.point.PointsAPI.getPointsFromDb;

public class ProcessDataCommandV3  extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(ProcessDataCommandV3.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);
        Controller controller = (Controller) context.get(AgentConstants.CONTROLLER);
        int payloadVersion = (int) context.get(AgentConstants.VERSION);

        JSONObject payload = (JSONObject) context.remove(AgentConstants.PAYLOAD); // Payload wont be available in context hereafter
        JSONArray dataArr = (JSONArray) payload.get(AgentConstants.DATA);

        List<String> pointNames = new ArrayList<>();
        Map<String, Map<String, Object>> deviceData = new HashMap<>();

        setPointValues(dataArr, payloadVersion, deviceData, pointNames);

        if (!pointNames.isEmpty()) {
            FacilioControllerType controllerType = FacilioControllerType.valueOf(controller.getControllerType());
            List<Point> pointsFromDb = FieldUtil.getAsBeanListFromMapList(getPointsFromDb(pointNames, controller), PointsAPI.getPointType(controllerType));
            if (pointsFromDb.size() < pointNames.size() && agent.getAgentTypeEnum().allowAutoAddition(controllerType)) {
                Set<String> pointsFromDbSet = new HashSet<>();
                Set<String> pointNamesSet = addNewPoints(controller, agent, pointNames, pointsFromDb, pointsFromDbSet);
                //get newly added points from db with point ids
                List<Point> newlyAddedPoints = FieldUtil.getAsBeanListFromMapList(getPointsFromDb(new ArrayList<>(pointNamesSet), controller), PointsAPI.getPointType(FacilioControllerType.valueOf(controller.getControllerType())));
                pointsFromDb.addAll(newlyAddedPoints);
            }
            Map<String, Point> pointRecords = new HashMap<>();
            for (Point p : pointsFromDb) {
                pointRecords.put(p.getName(), p);
            }
            context.put(FacilioConstants.ContextNames.DataProcessor.POINT_RECORDS, pointRecords);
        } else {
            LOGGER.info("points empty");
        }
        context.put(FacilioConstants.ContextNames.DataProcessor.DATA_SNAPSHOT, deviceData);

        JSONArray controlIds = (JSONArray) payload.get(FacilioConstants.ContextNames.DataProcessor.CONTROL_IDS);
        if (controlIds != null) {
            context.put(FacilioConstants.ContextNames.DataProcessor.CONTROL_IDS, controlIds);
        }
        context.put(AgentConstants.PAYLOAD_STR, payload.toJSONString()); // For adding in logs

        return false;
    }

    private void setPointValues(JSONArray dataArr, int payloadVersion, Map<String, Map<String, Object>> deviceData, List<String> pointNames) {
//        JSONObject pointData = (JSONObject) (payloadVersion == 2 ? ((JSONArray) payload.get(AgentConstants.DATA)).get(0)
//                : payload.get(AgentConstants.DATA));
        if (payloadVersion == 2) {
            JSONObject pointData = (JSONObject) dataArr.get(0);
            for (Object key : pointData.keySet()) {
                String pointName = ((String) key).trim();
                Object value = pointData.get(key);
                if (value != null && !value.toString().equalsIgnoreCase("NaN")) {
                    Map dataObj = new HashMap<>();
                    dataObj.put(AgentConstants.VALUE, value);

                    deviceData.put(pointName.trim(), dataObj);
                    pointNames.add(pointName);
                }
            }
        }
        else {
            for(int i = 0; i < dataArr.size(); i++) {
                JSONObject pointData = (JSONObject) dataArr.get(i);
                Object value = pointData.get(AgentConstants.VALUE);
                if (value != null && !value.toString().equalsIgnoreCase("NaN")) {
                    String name = ((String)pointData.get(AgentConstants.NAME)).trim();
                    deviceData.put(name, pointData);
                    pointNames.add(name);
                }
            }
        }
    }

    private Set<String> addNewPoints(Controller controller, FacilioAgent agent, List<String> pointNames, List<Point> pointsFromDb, Set<String> pointsFromDbSet) throws Exception {
        pointsFromDb.forEach(point -> pointsFromDbSet.add(point.getName()));
        Set<String> pointNamesSet = new HashSet<>(pointNames);
        pointNamesSet.removeAll(pointsFromDbSet);
        List<Map<String, Object>> pointRecordsToAdd = new ArrayList<>();
        List<Point> points = new ArrayList<>();
        for (String name : pointNamesSet) {
            MiscPoint point = new MiscPoint(agent.getId(), controller.getControllerId());
            point.setName(name);
            point.setDeviceName(controller.getName());
            point.setConfigureStatus(PointEnum.ConfigureStatus.CONFIGURED.getIndex());
            point.setControllerId(controller.getId());
            point.setPath(name);
            point.setCreatedTime(System.currentTimeMillis());
            point.setAgentWritable(true);
            points.add(point);
            Map<String, Object> pointMap = FieldUtil.getAsProperties(point.toJSON());
            pointRecordsToAdd.add(pointMap);
        }
        //PointsUtil.addPoints(controller, pointRecordsToAdd);
        //get newly added points from db with point ids
        bulkAddPoints(controller,pointRecordsToAdd,agent);
        return pointNamesSet;
    }
    public void bulkAddPoints(Controller controller,List<Map<String,Object>>pointsToBeAdded,FacilioAgent agent) throws Exception {
        FacilioChain addPointsChain = TransactionChainFactory.getAddPointsChain();
        FacilioContext context1 = new FacilioContext();
        context1.put(AgentConstants.AGENT,agent);
        context1.put(AgentConstants.CONTROLLER,controller);
        context1.put(AgentConstants.POINTS,pointsToBeAdded);
        addPointsChain.setContext(context1);
        addPointsChain.execute();
    }

}
