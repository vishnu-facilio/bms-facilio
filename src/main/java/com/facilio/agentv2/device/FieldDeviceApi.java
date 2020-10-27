package com.facilio.agentv2.device;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.actions.GetPointsAction;
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
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

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

    public static long addFieldDevice(Device device) throws Exception {
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
        return deviceId;
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
    public static Device getDeviceByName(long agentId, String name) throws Exception {
        FacilioModule fieldDeviceModule = ModuleFactory.getFieldDeviceModule();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFieldDeviceFields());
        if (agentId > 0) {
            if ((name != null) && (!name.isEmpty())) {
                GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                        .table(fieldDeviceModule.getTableName())
                        .select(fieldMap.values())
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.NAME), name.trim(), StringOperators.IS))
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS));
                List<Map<String, Object>> result = builder.get();
                if (result.size() == 1) {
                    Device device = FieldUtil.getAsBeanFromMap(result.get(0), Device.class);
                    if (device.getControllerProps().containsKey("type"))
                        device.setControllerType(Integer.parseInt(device.getControllerProps().get("type").toString()));
                    return device;
                } else {
                    LOGGER.info("Exception, unexpected results, only one row should be selected for device->" + name + " agentId->" + agentId + " rowsSelected->" + result.size());
                }
            } else {
                throw new Exception(" device name can't be null or empty");
            }
        } else {
            throw new Exception("agentId can't be less than 1");
        }
        return null;
    }

    public static List<Device> getDevicesByNames(long agentId, Set<String> name) throws Exception {
        List<Device> deviceList = new ArrayList<>();
        FacilioModule fieldDeviceModule = ModuleFactory.getFieldDeviceModule();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFieldDeviceFields());
        if (agentId > 0) {
            if ((name != null) && (!name.isEmpty())) {
                GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                        .table(fieldDeviceModule.getTableName())
                        .select(fieldMap.values())
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.NAME), StringUtils.join(name, ","), StringOperators.IS))
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS));
                List<Map<String, Object>> result = builder.get();
                result.forEach(row -> {
                    Device device = FieldUtil.getAsBeanFromMap(row, Device.class);
                    if (device.getControllerProps().containsKey("type"))
                        device.setControllerType(Integer.parseInt(device.getControllerProps().get("type").toString()));
                    deviceList.add(device);
                });
                if (result.size() == 0) {
                    LOGGER.info("No devices found ");
                }
            } else {
                throw new Exception(" device name can't be null or empty");
            }
        } else {
            throw new Exception("agentId can't be less than 1");
        }
        return deviceList;
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
		Integer controllerType = (Integer) context.get(AgentConstants.CONTROLLER_TYPE);
		String searchKey = (String) context.get(AgentConstants.SEARCH_KEY);
		List<Long> ids = (List<Long>) context.get(AgentConstants.RECORD_IDS);
		boolean fetchCount = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_COUNT, false);
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		Boolean typeCheck = (Boolean) context.get(AgentConstants.TYPE);

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(fieldDeviceModule.getTableName());
		Criteria criteria = new Criteria();
		List<FacilioField> fields = FieldFactory.getFieldDeviceFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.DELETED_TIME), "NULL", CommonOperators.IS_EMPTY));
		builder.select(fields);
		if ((agentId != null) && (agentId > 0)) {
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID),
					String.valueOf(agentId), NumberOperators.EQUALS));
		}
		if ((controllerType != null) && (controllerType >= 0)) {
            if (!context.containsKey(FacilioConstants.ContextNames.CONFIGURE)) {
                builder.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.CONFIGURE), "NULL", CommonOperators.IS_EMPTY));
            } else {
                if (context.get(FacilioConstants.ContextNames.CONFIGURE) != null) {
                    String val = context.get(FacilioConstants.ContextNames.CONFIGURE).toString();
                    builder.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.CONFIGURE), val, NumberOperators.EQUALS));
                }
            }
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.CONTROLLER_TYPE),
					String.valueOf(controllerType), NumberOperators.EQUALS));
		} 
		if(typeCheck != null && typeCheck == true){
			FacilioField type = FieldFactory.getAsMap(fields).get(AgentConstants.CONTROLLER_TYPE);
			FacilioField idColumn = FieldFactory.getAsMap(fields).get(AgentConstants.ID);
			FacilioField agentIdColumn = FieldFactory.getAsMap(fields).get(AgentConstants.AGENT_ID);
			FacilioField agentName = FieldFactory.getAsMap(fields).get(AgentConstants.NAME);
			List<FacilioField> fieldList = new ArrayList<>();
			fieldList.add(idColumn);
			fieldList.add(type);
			fieldList.add(agentIdColumn);
			fieldList.add(agentName);
			builder.select(fieldList);
		}
		if ((ids != null) && (!ids.isEmpty())) {
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.ID),
					StringUtils.join(ids, ","), NumberOperators.EQUALS));
		}
		if (StringUtils.isNotEmpty(searchKey)) {
			builder.andCustomWhere("NAME = ? OR NAME LIKE ?", searchKey, "%"+searchKey +"%");
		}

		if (pagination != null && !pagination.isEmpty()) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");

			int offset = ((page - 1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			builder.offset(offset);
			builder.limit(perPage);
		} else {
			LOGGER.info("no pagination");
			builder.limit(50);
		}
		if (fetchCount) {
			builder.select(new ArrayList<>()).aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT,
					FieldFactory.getIdField(fieldDeviceModule));
		}

		List<Map<String, Object>> rows = builder.andCriteria(criteria).get();
		if (FacilioProperties.isDevelopment()) {
			LOGGER.info(" query " + builder.toString());
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

        List<Map<String, Object>> props = selectRecordBuilder.get();
        boolean isMiscType=false;
		for (Map<String, Object> prop : props) {
			Integer val = (Integer) prop.get("controllerType");
			if (val != null && val == 0L) {
				isMiscType = true;
				break;
			}
		}
        if(isMiscType && GetPointsAction.isVirtualPointExist(agentId)) {
        	Map<String,Object> prop = new HashMap<String, Object>();
        	prop.put("id", 0);
        	prop.put("controllerType",0);
        	prop.put("name", "Logical");
        	props.add(prop);
        }
        return props;
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

    public static List<Map<String, Object>> getDevices(FacilioContext constructListContext) throws Exception{
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

	public static int updateDeviceConfigured(long deviceId) throws SQLException {

		Map<String, Object> updateValue = new HashMap<>();
		FacilioModule module = ModuleFactory.getFieldDeviceModule();
		updateValue.put(AgentConstants.CONFIGURE, true);
		GenericUpdateRecordBuilder updateVal = new GenericUpdateRecordBuilder().table(module.getTableName())
				.fields(FieldFactory.getFieldDeviceFields()).andCondition(CriteriaAPI.getIdCondition(deviceId, module));
		return updateVal.update(updateValue);
	}
}
