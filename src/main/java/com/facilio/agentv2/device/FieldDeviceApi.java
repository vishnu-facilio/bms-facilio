package com.facilio.agentv2.device;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.*;

public class FieldDeviceApi {

    private static final Logger LOGGER = LogManager.getLogger(FieldDeviceApi.class.getName());


    public static int deleteDevices(Collection<Long> deviceId) throws SQLException {
        FacilioModule fieldDeviceModule = ModuleFactory.getFieldDeviceModule();
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder().table(fieldDeviceModule.getTableName())
                .fields(FieldFactory.getFieldDeviceFields())
                .andCondition(CriteriaAPI.getIdCondition(deviceId, ModuleFactory.getFieldDeviceModule()));
        Map<String, Object> toUpdateMap = new HashMap<>();
        toUpdateMap.put(AgentConstants.DELETED_TIME, System.currentTimeMillis());
        return builder.update(toUpdateMap);
    }

    public static void addFieldDevices(List<Device> devices) throws Exception {
        FacilioModule fieldDeviceModule = ModuleFactory.getFieldDeviceModule();
        try {
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(fieldDeviceModule.getTableName())
                    .fields(FieldFactory.getFieldDeviceFields())
                    .addRecords(FieldUtil.getAsMapList(devices, Device.class));
            builder.save();
            return;
        } catch (Exception e) {
            LOGGER.info(" Bulk insert failed ");
            for (Device device : devices) {
                addFieldDevice(device);
            }
        }
        //try multiple insert if bulk fails
    }

    public static void addFieldDevice(Device device) throws Exception {
        FacilioModule fieldDeviceModule = ModuleFactory.getFieldDeviceModule();
        if (device.getCreatedTime() < 100) {
            device.setCreatedTime(System.currentTimeMillis());
        }
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(fieldDeviceModule.getTableName())
                .fields(FieldFactory.getFieldDeviceFields());
        long deviceId = builder.insert(FieldUtil.getAsProperties(device));
        if (deviceId > 0) {
            device.setId(deviceId);
        } else {
            LOGGER.info(" failed to insert device ->" + FieldUtil.getAsJSON(device));

        }
    }

    public static Device getDevice(long agentId, String identifier) throws Exception {
        FacilioModule fieldDeviceModule = ModuleFactory.getFieldDeviceModule();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFieldDeviceFields());
        if (agentId > 0) {
            if ((identifier != null) && (!identifier.isEmpty())) {
                GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                        .table(fieldDeviceModule.getTableName())
                        .select(fieldMap.values())
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.IDENTIFIER), identifier.trim(), StringOperators.IS))
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS));
                List<Map<String, Object>> result = builder.get();
                if (result.size() == 1) {
                    Device device = FieldUtil.getAsBeanFromMap(result.get(0), Device.class);
                    if (device.getControllerProps().containsKey("type"))
                        device.setControllerType(Integer.parseInt(device.getControllerProps().get("type").toString()));
                    return device;
                } else {
                    LOGGER.info("Exception, unexpected results, only one row should be selected for device->" + identifier + " agentId->" + agentId + " rowsSelected->" + result.size());
                }
            } else {
                throw new Exception(" device name can't be null or empty");
            }
        } else {
            throw new Exception("agentId can't be less than 1");
        }
        return null;
    }


    public static List<Map<String, Object>> getDevices(List<Long> deviceIds) {
        try {
            FacilioContext context = new FacilioContext();
            context.put(AgentConstants.RECORD_IDS, deviceIds);
            return getDeviceData(context);
        } catch (Exception e) {
            LOGGER.info(" Exception occurred ", e);
        }
        return new ArrayList<>();
    }

    /**
     * @param agentId can be null
     * @return
     */
    public static List<Map<String, Object>> getDevicesForAgent(Long agentId) {
        try {
            FacilioContext context = new FacilioContext();
            context.put(AgentConstants.AGENT_ID, agentId);
            return getDeviceData(context);
        } catch (Exception e) {
            LOGGER.info(" Exception occurred ", e);
        }
        return new ArrayList<>();
    }


    private static List<Map<String, Object>> getDeviceData(FacilioContext context) throws Exception {
        FacilioModule fieldDeviceModule = ModuleFactory.getFieldDeviceModule();
        Long agentId = (Long) context.get(AgentConstants.AGENT_ID);
        List<Long> ids = (List<Long>) context.get(AgentConstants.RECORD_IDS);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(fieldDeviceModule.getTableName())
                .select(FieldFactory.getFieldDeviceFields());
        Criteria criteria = new Criteria();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFieldDeviceFields());
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.DELETED_TIME), "NULL", CommonOperators.IS_EMPTY));
        if ((agentId != null) && (agentId > 0)) {
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS));
        }
        if ((ids != null) && (!ids.isEmpty())) {
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.ID), StringUtils.join(ids, ","), NumberOperators.EQUALS));
        }
        if (containsCheck(FacilioConstants.ContextNames.PAGINATION,context)) {
            JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
            boolean fetchCount = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_COUNT, false);
            if (pagination != null && !fetchCount) {
                int page = (int) pagination.get("page");
                int perPage = (int) pagination.get("perPage");

                int offset = ((page - 1) * perPage);
                if (offset < 0) {
                    offset = 0;
                }
                builder.offset(offset);
                builder.limit(perPage);
            } else if (fetchCount) {
                builder.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(fieldDeviceModule));
                builder.select(new ArrayList<>());
            }
        }
        else {
            LOGGER.info("no pagination");
            builder.limit(50);
        }
        List<Map<String, Object>> rows = builder.andCriteria(criteria).get();
        if(FacilioProperties.isDevelopment()){
            LOGGER.info(" query "+builder.toString());
        }
        return rows;
    }

    public static boolean discoverPoints(List<Long> ids) {
        Device device;
        Controller controller;
        for (Map<String, Object> deviceMap : getDevices(ids)) {
            try {
                device = FieldUtil.getAsBeanFromMap(deviceMap, Device.class);
                controller = ControllerUtilV2.makeControllerFromFieldDevice(device);
                if (controller == null) {
                    throw new Exception(" controller cant be created ");
                }
                controller.setAgentId(device.getAgentId());
                ControllerMessenger.discoverPoints(controller);
                return true;
            } catch (Exception e) {
                LOGGER.info("Exception while making Device bean from json -> " + deviceMap + "  ", e);
                return false;
            }
        }
        return false;
    }


    public static long getAgentDeviceCount(List<Long> agentIds) {
        return getDeviceCount(agentIds, null);
    }

    public static long getTypeDeviceCount(FacilioControllerType type) {
        return getDeviceCount(null, type);
    }

    public static long getTypeDeviceCount(List<Long> agentIds, FacilioControllerType type) {
        return getDeviceCount(agentIds, type);
    }

    private static long getDeviceCount(List<Long> agentIds, FacilioControllerType type) {
        FacilioModule fieldDeviceModule = ModuleFactory.getFieldDeviceModule();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFieldDeviceFields());
        try {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(fieldDeviceModule.getTableName())
                    .select(new HashSet<>())
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, fieldMap.get(AgentConstants.ID));
            if ((agentIds != null) && ( ! agentIds.isEmpty())) {
                builder.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), agentIds, NumberOperators.EQUALS));
            }
            if ((type != null)) {
                builder.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.CONTROLLER_TYPE), String.valueOf(type.asInt()), NumberOperators.EQUALS));
            }
            return (long) builder.get().get(0).get(AgentConstants.ID);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting FieldDevices count", e);
        }
        return -1;
    }

    public static long getDeviceCount() {
        return getDeviceCount(null, null);
    }

    public static Map<String, Object> getDeviceData(Long deviceId) {
        List<Map<String, Object>> devices = getDevices(Collections.singletonList(deviceId));
        if ((devices != null) && (!devices.isEmpty())) {
            return devices.get(0);
        }
        return new HashMap<>();
    }

    public static Device getDevice(Long deviceId) {
        Map<String, Object> deviceData = getDeviceData(deviceId);
        if (!deviceData.isEmpty()) {
            return FieldUtil.getAsBeanFromMap(deviceData, Device.class);
        }
        return null;
    }

    public static List<Map<String, Object>> getDeviceFilterData(Long agentId) throws Exception {
        FacilioModule fieldDeviceModule = ModuleFactory.getFieldDeviceModule();
        List<FacilioField> deviceFilterFields = new ArrayList<>();
        deviceFilterFields.add(FieldFactory.getIdField(fieldDeviceModule));
        deviceFilterFields.add(FieldFactory.getNameField(fieldDeviceModule));
        deviceFilterFields.add(FieldFactory.getFieldDeviceTypeField(fieldDeviceModule));
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(fieldDeviceModule.getTableName())
                .select(deviceFilterFields)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(fieldDeviceModule), String.valueOf(agentId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getDeletedTimeField(fieldDeviceModule), "NULL", CommonOperators.IS_EMPTY));

        return selectRecordBuilder.get();
    }

    public static List<Map<String,Object>> getModbusDeviceFilter(Long agentId) throws Exception {
        FacilioModule fieldDeviceModule = ModuleFactory.getFieldDeviceModule();
        List<FacilioField> deviceFilterFields = new ArrayList<>();
        deviceFilterFields.add(FieldFactory.getIdField(fieldDeviceModule));
        deviceFilterFields.add(FieldFactory.getNameField(fieldDeviceModule));
        deviceFilterFields.add(FieldFactory.getFieldDeviceTypeField(fieldDeviceModule));
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(fieldDeviceModule.getTableName())
                .select(deviceFilterFields)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(fieldDeviceModule), String.valueOf(agentId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getDeletedTimeField(fieldDeviceModule), "NULL", CommonOperators.IS_EMPTY))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getFieldDeviceTypeField(fieldDeviceModule), String.valueOf(FacilioControllerType.MODBUS_IP.asInt()),NumberOperators.EQUALS));
        return selectRecordBuilder.get();
    }

    public static boolean containsCheck(String key, Map map) {
        if ((key != null) && (!key.isEmpty()) && (map != null) && (!map.isEmpty()) && (map.containsKey(key)) && (map.get(key) != null)) {
            return true;
        }
        return false;
    }

    public static boolean checkNumber(Number number) {
        return (number.intValue() > 0);
    }

    public static List<Map<String, Object>> getDevices(Long agentId, FacilioContext constructListContext) throws Exception {
        constructListContext.put(AgentConstants.AGENT_ID, agentId);
        return getDeviceData(constructListContext);
    }

    public static void addControllerAsDevice(Controller controllerContext) throws Exception {
        Device device = new Device();
        device.setIdentifier(controllerContext.getIdentifier());
        device.setControllerProps(controllerContext.toJSON());
        device.setAgentId(controllerContext.getAgentId());
        device.setSiteId(controllerContext.getSiteId());
        device.setName(controllerContext.getName());
        device.setControllerType(controllerContext.getControllerType());
        FieldDeviceApi.addFieldDevice(device);
        controllerContext.setDeviceId(device.getId());
    }
}
