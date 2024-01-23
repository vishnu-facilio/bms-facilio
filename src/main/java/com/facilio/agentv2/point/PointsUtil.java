package com.facilio.agentv2.point;

import com.facilio.agent.AgentType;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.bacnet.BacnetIpPointContext;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.iotmessage.IotMessage;
import com.facilio.agentv2.iotmessage.IotMessageApiV2;
import com.facilio.agentv2.misc.MiscPoint;
import com.facilio.agentv2.modbustcp.ModbusTcpPointContext;
import com.facilio.agentv2.modbustcp.ModbusUtils;
import com.facilio.agentv2.rdm.RdmControllerContext;
import com.facilio.bacnet.BACNetUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.connected.ResourceType;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.unitconversion.Unit;
import com.facilio.util.AckUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;


public class PointsUtil {
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

    public static boolean processPoints(JSONObject payload, Controller controller, FacilioAgent agent) throws Exception {
        LOGGER.info("Processing points for controller " + controller.getName());
        List<ReadingDataMeta> rdmList = new ArrayList<>();

        boolean configurePoint = (boolean) payload.getOrDefault("configure", false);
        if (containsValueCheck(AgentConstants.DATA, payload)) {
            JSONArray pointsJSON = (JSONArray) payload.get(AgentConstants.DATA);
            long incomingCount = pointsJSON.size();
            if (incomingCount == 0) {
                throw new Exception("PointJSON can't be empty");
            }

            //getting points name
            List<String> pointName = (List<String>) pointsJSON.stream().map(x -> ((JSONObject) x).get(AgentConstants.NAME).toString()).collect(Collectors.toList());

            //getting existing points name from DB
            List<String> existingPoints = PointsAPI.getPointsFromDb(pointName, controller).stream()
                    .map(name -> name.get(AgentConstants.NAME).toString())
                    .collect(Collectors.toList());

            boolean allowAutoMap = agent.isAllowAutoMapping() && agent.getAutoMappingParentFieldId() > 0;
            Map<String, FacilioField> readingFieldMap = new HashMap<>();
            Pair<Long, Long> categoryIdAndParentId = PointsUtil.getCategoryIdAndParentId(agent, controller, payload, readingFieldMap);
            Long categoryId = categoryIdAndParentId.getLeft(), parentId = categoryIdAndParentId.getRight();

            LOGGER.info("Existing Points count : " + existingPoints.size());
            LOGGER.info("New Points count : " + (pointName.size() - existingPoints.size()));

            List<Map<String, Object>> points = new ArrayList<>();
            for (Object o : pointsJSON) {
                JSONObject pointJSON = (JSONObject) o;
                pointJSON.put(AgentConstants.POINT_TYPE, controller.getControllerType());
                pointJSON.put(AgentConstants.CONTROLLER_ID, controller.getId());
                try {
                    Object pName = pointJSON.get(AgentConstants.NAME);
                    if (!existingPoints.contains(pName)) {
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
                            AgentType agentType = agent.getAgentTypeEnum();
                            if (configurePoint || agentType.isCustomPayload()) {
                                point.setConfigureStatus(PointEnum.ConfigureStatus.CONFIGURED.getIndex());
                            }
                            if (controller.getControllerType() == FacilioControllerType.MODBUS_IP.asInt() ||
                                    controller.getControllerType() == FacilioControllerType.MODBUS_RTU.asInt() ||
                                    (controller.getControllerType() == FacilioControllerType.RDM.asInt() && ((RdmControllerContext) controller).getIsTdb())) {
                                if (agentType == AgentType.FACILIO || agent.getAgentTypeEnum().isAgentService()) {
                                    point.setConfigureStatus(PointEnum.ConfigureStatus.CONFIGURED.getIndex());
                                }
                            }
                            if (pointJSON.containsKey(AgentConstants.STATE_TEXT_ENUMS)) {
                                JSONObject stateTextEnums = (JSONObject) pointJSON.get(AgentConstants.STATE_TEXT_ENUMS);
                                point.setStates(stateTextEnums);
                            }
                            Integer unitId = getUnitId(pointJSON.get(AgentConstants.UNIT));
                            if(allowAutoMap){
                                commissionPoint(categoryId, agent.getReadingScope(), parentId, point, readingFieldMap.get(point.getName()), rdmList, unitId);
                            }
                            Map<String, Object> pointMap = FieldUtil.getAsProperties(point.toJSON());

                            points.add(pointMap);

                        }
                    } else {
                        LOGGER.info("Point already exists : " + pName);
                    }
                } catch (Exception e) {
                    LOGGER.info("Exception occurred while getting point", e);
                }
            }

            if (points.size() == 0) {
                throw new Exception("Points to add can't be empty");
            }
            FacilioChain addPointsChain = TransactionChainFactory.getAddPointsChain();
            FacilioContext context = new FacilioContext();
            context.put(AgentConstants.CONTROLLER, controller);
            context.put(AgentConstants.AGENT, agent);
            context.put(AgentConstants.POINTS, points);
            addPointsChain.setContext(context);
            addPointsChain.execute();
            updateRDMAndAssetConnectedStatus(agent.getReadingScope(), rdmList);
        } else {
            LOGGER.info(" Exception occurred, pointsData missing from payload -> " + payload);
        }
        return true;
    }

    private static Integer getUnitId(Object unitObj) {
        Unit unit = null;
        if(unitObj != null) {
            unit = Unit.getUnitFromSymbol(unitObj.toString());
        }
        Integer unitId = unit != null ? unit.getUnitId() : null;
        return unitId;
    }

    private static void setPointWritable(JSONObject pointJSON, Point point) {

        if (point.getControllerType() == FacilioControllerType.BACNET_IP) {
            BacnetIpPointContext bacnetIpPoint = (BacnetIpPointContext) point;
            if (BACNetUtil.InstanceType.valueOf(bacnetIpPoint.getInstanceType()).isWritable()) {
                point.setWritable(true);
                point.setAgentWritable(true);
            }
        }
        if (point.getControllerType() == FacilioControllerType.MODBUS_IP) {
            ModbusTcpPointContext modbusTcpPoint = (ModbusTcpPointContext) point;
            if (ModbusUtils.RegisterType.valueOf(Math.toIntExact(modbusTcpPoint.getRegisterType())).isWritable()) {
                point.setWritable(true);
                point.setAgentWritable(true);
            }
        }
        if (pointJSON.containsKey(AgentConstants.WRITABLE)) {
            Boolean value = Boolean.parseBoolean(pointJSON.get(AgentConstants.WRITABLE).toString());
            if (value != null && value) {
                point.setAgentWritable(value);
            } else {
                point.setAgentWritable(false);
            }

        }
    }

    private static boolean containsValueCheck(String key, Map<String, Object> jsonObject) {
        if (jsonObject.containsKey(key) && (jsonObject.get(key) != null)) {
            return true;
        }
        return false;
    }

    //BULK INSERT
    public static void addPoints(Controller controller, List<Map<String, Object>> points) throws Exception {
        FacilioControllerType controllerType = FacilioControllerType.valueOf(controller.getControllerType());
        addPoints(controllerType, points);
    }

    public static void addPoints(FacilioControllerType controllerType, List<Map<String, Object>> points) throws Exception {
        FacilioModule pointModule = AgentConstants.getPointModule();
        List<FacilioField> fields = PointsAPI.getChildPointFields(controllerType);
        if (pointModule == null) {
            DBUtil.insertValuesWithJoin(PointsAPI.getPointModule(controllerType), fields, FieldUtil.getAsMapList(points, PointsAPI.getPointType(controllerType)));
        } else {
            FacilioModule childPointModule = PointsAPI.getPointModule(controllerType);
            InsertRecordBuilder builder = new InsertRecordBuilder<>()
                    .table(childPointModule.getTableName())
                    .fields(fields)
                    .module(childPointModule)
                    .addRecordProps(points);
            builder.save();

        }
    }

    public static ReadingDataMeta getRDM(Point point) {
        ReadingDataMeta meta = new ReadingDataMeta();
        meta.setResourceId(point.getResourceId());
        meta.setFieldId(point.getFieldId());
        if (point.getUnit() > 0) {
            meta.setUnit(point.getUnit());
        }
        meta.setInputType(ReadingDataMeta.ReadingInputType.CONTROLLER_MAPPED);
        if (point.isWritable()) {
            meta.setReadingType(ReadingDataMeta.ReadingType.WRITE);
        }
        meta.setValue("-1");
        return meta;
    }

    public static Pair<Long, Long> getCategoryAndParentId(int scope, long parentFieldId, String fieldValue) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        ResourceType resourceType = ResourceType.valueOf(scope);
        FacilioModule module = modBean.getModule(resourceType.getModuleName());
        List<FacilioField> fields = getFields(module, resourceType);
        V3Context parent = getParentUsingFieldId(module, fields, parentFieldId, fieldValue);

        if (parent == null) {
            LOGGER.info("Auto Commission :: Parent is not found with field value of " + fieldValue + ", field id " + parentFieldId +
                    " or found more than one record");
            return null;
        }

        return resourceType.getScopeHandler().getParentIdAndCategoryId(parent);
    }

    private static V3Context getParentUsingFieldId(FacilioModule module, List<FacilioField> fields, long fieldId, String fieldValue) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField field = modBean.getField(fieldId, module.getModuleId());
        Class beanClass = getBeanClass(module);

        SelectRecordsBuilder<V3Context> selectBuilder = new SelectRecordsBuilder<V3Context>()
                .module(module)
                .beanClass(beanClass)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(field, fieldValue, StringOperators.IS));

        List<V3Context> v3Contexts = selectBuilder.get();
        if (v3Contexts != null && !v3Contexts.isEmpty() && v3Contexts.size() == 1) {
            return v3Contexts.get(0);
        }
        return null;
    }

    private static Class getBeanClass(FacilioModule module) throws Exception {
        V3Config v3Config = ChainUtil.getV3Config(module.getName());
        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        return beanClass;
    }

    private static List<FacilioField> getFields(FacilioModule module, ResourceType resourceType) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getIdField(module));
        fields.add(resourceType.getScopeHandler().getTypeField());
        return fields;
    }

    public static Pair<Long, Long> getCategoryIdAndParentId(FacilioAgent agent, Controller controller, JSONObject payload, Map<String, FacilioField> readingFieldMap) throws Exception {
        Pair<Long, Long> categoryIdAndParentId = Pair.of(0L, 0L);
        try{
            if (agent.isAllowAutoMapping() && agent.getAutoMappingParentFieldId() > 0) {
                LOGGER.info("Auto Mapping for agent : " + agent.getDisplayName() + " and controller : " + controller.getName());
                String fieldValue = getParentIdentifierFieldValue(controller.getName(), payload);
                categoryIdAndParentId = PointsUtil.getCategoryAndParentId(agent.getReadingScope(), agent.getAutoMappingParentFieldId(), fieldValue);
                if (categoryIdAndParentId != null) {
                    Long categoryId = categoryIdAndParentId.getLeft();
                    Long parentId = categoryIdAndParentId.getRight();
                    readingFieldMap.putAll(ResourceType.valueOf(agent.getReadingScope()).getScopeHandler().getReadings(categoryId, parentId, agent.getAutoMappingReadingFieldNameEnum()));
                    LOGGER.info("Category/UtilityType Id " + categoryId + ", Parent Id : " + parentId);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception while fetching CategoryId And ParentId", e);
        }

        return categoryIdAndParentId;
    }

    private static String getParentIdentifierFieldValue(String controllerName, JSONObject payload) {
        if (payload.containsKey(AgentConstants.UNIQUE_ID)) {
            return payload.get(AgentConstants.UNIQUE_ID).toString();
        }
        return controllerName;
    }

    public static void commissionPoint(Long categoryId, Integer scope, Long parentId, Point point, FacilioField field, List<ReadingDataMeta> rdmList, Integer unitId) {
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

    public static void updateRDMAndAssetConnectedStatus(int scope, List<ReadingDataMeta> rdmList) throws Exception {
        if (!rdmList.isEmpty()) {
            List<String> fields = Arrays.asList("unit", "inputType", "readingType");
            ReadingsAPI.updateReadingDataMetaList(rdmList, fields);
            ResourceType.valueOf(scope).getScopeHandler().updateConnectionStatus(Collections.singleton(rdmList.get(0).getResourceId()),true);
        }
    }
}
