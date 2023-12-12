package com.facilio.agentv2.commands;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.misc.MiscPoint;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.agentv2.point.PointsUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.MetersAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.unitconversion.Unit;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

import static com.facilio.agentv2.point.PointsAPI.getPointsFromDb;

public class ProcessDataCommandV3 extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(ProcessDataCommandV3.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);
        Controller controller = (Controller) context.get(AgentConstants.CONTROLLER);
        int payloadVersion = (int) context.get(AgentConstants.VERSION);

        JSONObject payload = (JSONObject) context.remove(AgentConstants.PAYLOAD); // Payload won't be available here after
        JSONArray dataArr = (JSONArray) payload.get(AgentConstants.DATA);

        List<String> pointNames = new ArrayList<>();
        Map<String, Integer> pointNameVsUnitId = new HashMap<>();
        Map<String, Map<String, Object>> deviceData = new HashMap<>();

        setPointValues(dataArr, payloadVersion, deviceData, pointNames, pointNameVsUnitId);

        if (!pointNames.isEmpty()) {
            FacilioControllerType controllerType = FacilioControllerType.valueOf(controller.getControllerType());
            List<Point> pointsFromDb = FieldUtil.getAsBeanListFromMapList(getPointsFromDb(pointNames, controller), PointsAPI.getPointType(controllerType));
            if (pointsFromDb.size() < pointNames.size() && agent.getAgentTypeEnum().allowAutoAddition(controllerType)) {
                Set<String> pointsFromDbSet = new HashSet<>();
                Set<String> pointNamesSet = addNewPoints(controller, agent, pointNames, pointsFromDb, pointsFromDbSet, payload, pointNameVsUnitId);
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
        JSONObject state = (JSONObject) payload.get("state");
        if (state != null && !state.isEmpty()) {
            context.put("state", state);
        }
        context.put(AgentConstants.PAYLOAD_STR, payload.toJSONString()); // For adding in logs

        return false;
    }

    private void setPointValues(JSONArray dataArr, int payloadVersion, Map<String, Map<String, Object>> deviceData, List<String> pointNames, Map<String, Integer> pointNameVsUnitId) {
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
        } else {
            for (int i = 0; i < dataArr.size(); i++) {
                JSONObject pointData = (JSONObject) dataArr.get(i);
                Object value = pointData.get(AgentConstants.VALUE);
                Object unitObj = pointData.get(AgentConstants.UNIT);
                if (value != null && !value.toString().equalsIgnoreCase("NaN")) {
                    String name = ((String) pointData.get(AgentConstants.NAME)).trim();
                    deviceData.put(name, pointData);
                    pointNames.add(name);
                    if(unitObj != null) {
                        Unit unit = Unit.getUnitFromSymbol(unitObj.toString());
                        if(unit != null){
                            pointNameVsUnitId.put(name, unit.getUnitId());
                        }
                    }
                }
            }
        }
    }

    private Set<String> addNewPoints(Controller controller, FacilioAgent agent, List<String> pointNames, List<Point> pointsFromDb, Set<String> pointsFromDbSet, JSONObject payload, Map<String, Integer> pointNameVsUnitId) throws Exception {
        List<ReadingDataMeta> rdmList = new ArrayList<>();
        boolean allowAutoMap = agent.isAllowAutoMapping() && agent.getReadingScope() > 0 && agent.getAutoMappingParentFieldId() > 0;
        Long categoryId = 0L, parentId = 0L;
        Map<String, FacilioField> nameVsField = new HashMap<>();
        try{
            if (allowAutoMap) {
                LOGGER.info("Auto Mapping for agent : " + agent.getDisplayName() + " and controller : " + controller.getName());
                String fieldValue = getParentIdentifierFieldValue(controller.getName(), payload);
                Pair<Long, Long> categoryIdAndParentId = PointsUtil.getCategoryAndParentId(agent.getReadingScope(), agent.getAutoMappingParentFieldId(), fieldValue);
                if (categoryIdAndParentId != null) {
                    categoryId = categoryIdAndParentId.getKey();
                    parentId = categoryIdAndParentId.getValue();
                    nameVsField = ResourceType.valueOf(agent.getReadingScope()).getScopeHandler().getReadings(categoryId, parentId);
                }
                LOGGER.info("Category/UtilityType Id " + categoryId + ", Parent Id : " + parentId);
            }
        } catch (Exception e) {
            LOGGER.error("Exception while auto mapping", e);
        }

        pointsFromDb.forEach(point -> pointsFromDbSet.add(point.getName()));
        Set<String> pointNamesSet = new HashSet<>(pointNames);
        pointNamesSet.removeAll(pointsFromDbSet);
        List<Map<String, Object>> pointRecordsToAdd = new ArrayList<>();
        for (String name : pointNamesSet) {
            MiscPoint point = getMiscPoint(controller, agent, name, allowAutoMap, categoryId, parentId, nameVsField.get(name), rdmList, pointNameVsUnitId.get(name));
            Map<String, Object> pointMap = FieldUtil.getAsProperties(point.toJSON());
            pointRecordsToAdd.add(pointMap);
        }
        bulkAddPoints(controller, pointRecordsToAdd, agent);
        updateRDMAndAssetConnectedStatus(agent.getReadingScope(), rdmList);
        return pointNamesSet;
    }

    private static MiscPoint getMiscPoint(Controller controller, FacilioAgent agent, String name, boolean allowAutoMap,
                                          Long categoryId, Long parentId, FacilioField field, List<ReadingDataMeta> rdmList, Integer unitId) {
        MiscPoint point = new MiscPoint(agent.getId(), controller.getControllerId());
        point.setName(name);
        point.setDisplayName(name);
        point.setDeviceName(controller.getName());
        point.setConfigureStatus(PointEnum.ConfigureStatus.CONFIGURED.getIndex());
        point.setControllerId(controller.getId());
        point.setPath(name);
        point.setCreatedTime(System.currentTimeMillis());
        point.setAgentWritable(true);
        if (allowAutoMap) {
            commissionPoint(categoryId, agent.getReadingScope(), parentId, point, field, rdmList, unitId);
        }
        return point;
    }

    private static void commissionPoint(Long categoryId, Integer scope, Long parentId, MiscPoint point, FacilioField field, List<ReadingDataMeta> rdmList, Integer unitId) {
        if (categoryId > 0 && parentId > 0) {
            point.setCategoryId(categoryId);
            point.setResourceId(parentId);
            point.setReadingScope(scope);
            if (field != null) {
                LOGGER.info("Mapping " + point.getName() + " with field " + field.getName() + ", fieldId " + field.getFieldId());
                point.setFieldId(field.getFieldId());
                if(unitId!=null && unitId > 0){
                    point.setUnit(unitId);
                }
                point.setMappedTime(System.currentTimeMillis());
                point.setMappedType(PointEnum.MappedType.AUTO.getIndex());
                rdmList.add(PointsUtil.getRDM(point));
            }
        }
    }

    private static void updateRDMAndAssetConnectedStatus(int scope, List<ReadingDataMeta> rdmList) throws Exception {
        if (!rdmList.isEmpty()) {
            List<String> fields = Arrays.asList("unit", "inputType", "readingType");
            ReadingsAPI.updateReadingDataMetaList(rdmList, fields);
            ResourceType.valueOf(scope).getScopeHandler().updateConnectionStatus(Collections.singleton(rdmList.get(0).getResourceId()),true);
        }
    }

    private String getParentIdentifierFieldValue(String controllerName, JSONObject payload) {
        if (payload.containsKey(AgentConstants.UNIQUE_ID)) {
            return payload.get(AgentConstants.UNIQUE_ID).toString();
        }
        return controllerName;
    }

    public void bulkAddPoints(Controller controller, List<Map<String, Object>> pointsToBeAdded, FacilioAgent agent) throws Exception {
        FacilioChain addPointsChain = TransactionChainFactory.getAddPointsChain();
        FacilioContext context1 = new FacilioContext();
        context1.put(AgentConstants.AGENT, agent);
        context1.put(AgentConstants.CONTROLLER, controller);
        context1.put(AgentConstants.POINTS, pointsToBeAdded);
        addPointsChain.setContext(context1);
        addPointsChain.execute();
    }

}
