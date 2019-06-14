package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ControllerActivityWatcherContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.MultiModuleReadingData;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.SecondsChronoUnit;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

;

public class ControllerAPI {
	private static final Logger LOGGER = LogManager.getLogger(ControllerAPI.class.getName());

	public static List<ControllerContext> getAllControllers() throws Exception {
		FacilioModule module = ModuleFactory.getControllerModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getControllerFields())
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				;
		
		List<Map<String, Object>> props = ruleBuilder.get();
		return getControllerFromMapList(props, true);
	}
	
	public static ControllerContext getActiveController (String macAddress) throws Exception {
		return getActiveController(macAddress, false);
	}
	
	public static ControllerContext getActiveController (String macAddress, boolean fetchBuilding) throws Exception {
		FacilioModule module = ModuleFactory.getControllerModule();
		List<FacilioField> fields = FieldFactory.getControllerFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields); 
		FacilioField macAddrField = fieldMap.get("macAddr");
		FacilioField activeField = fieldMap.get("active");
		
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(macAddrField, macAddress, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(activeField, String.valueOf(true), BooleanOperators.IS))
				;
		
		List<Map<String, Object>> controllerList = ruleBuilder.get();
		List<ControllerContext> controllers = getControllerFromMapList(controllerList, fetchBuilding);
		if (controllers != null && !controllers.isEmpty()) {
			return controllers.get(0);
		}
		return null;
	}
	
	public static ControllerContext getController (String macAddress) throws Exception {
		return getController(null, macAddress, false);
	}
	
	public static ControllerContext getController (String deviceName, String macAddress, boolean fetchBuilding) throws Exception {
		FacilioModule module = ModuleFactory.getControllerModule();
		List<FacilioField> fields = FieldFactory.getControllerFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields); 
		FacilioField macAddrField = fieldMap.get("macAddr");
		
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(macAddrField, macAddress, StringOperators.IS))
				;
		if(deviceName != null) {
			FacilioField deviceNameField = fieldMap.get("name");
			ruleBuilder = ruleBuilder.andCondition(CriteriaAPI.getCondition(deviceNameField, deviceName, StringOperators.IS));
		}
		
		List<Map<String, Object>> controllerList = ruleBuilder.get();
		List<ControllerContext> controllers = getControllerFromMapList(controllerList, fetchBuilding);
		if(AccountUtil.getCurrentOrg().getOrgId()==152) {
			
			LOGGER.info("#####Mac Address:  "+macAddress+"  ControllerList: "+controllerList+"  Controllers: "+controllers+" Sql String: "+ruleBuilder.toString());
		}
		if (controllers != null && !controllers.isEmpty()) {
			return controllers.get(0);
		}
		return null;
	}

	public static ControllerContext getController(long id) throws Exception {
		return getController(id, false);
	}
	
	public static ControllerContext getController(long id, boolean fetchBuilding) throws Exception {
		FacilioModule module = ModuleFactory.getControllerModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getControllerFields())
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(id, module))
				;
		
		List<Map<String, Object>> controllerList = ruleBuilder.get();
		List<ControllerContext> controllers = getControllerFromMapList(controllerList, fetchBuilding);
		if (controllers != null && !controllers.isEmpty()) {
			return controllers.get(0);
		}
		return null;
	}
	
	private static List<ControllerContext> getControllerFromMapList(List<Map<String, Object>> props, boolean fetchBuilding) throws Exception {
		if(props != null && !props.isEmpty()) {
			List<ControllerContext> controllers = new ArrayList<>();
			List<Long> ids = fetchBuilding ? new ArrayList<>() : null;
			for(Map<String, Object> prop : props) {
				ControllerContext controllerSetting = FieldUtil.getAsBeanFromMap(prop, ControllerContext.class);
				controllers.add(controllerSetting);
				
				if (fetchBuilding) {
					ids.add(controllerSetting.getId());
				}
			}
			
			if (fetchBuilding && !controllers.isEmpty()) {
				Map<Long, List<Long>> controllerMap = getControllerBuildingIds(ids);
				if (controllerMap != null && !controllerMap.isEmpty()) {
					for (ControllerContext controller : controllers) {
						controller.setBuildingIds(controllerMap.get(controller.getId()));
					}
				}
			}
			
			return controllers;
		}
		return null;
	}
	
	public static Map<Long, ControllerContext> getControllersMap(Collection<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getControllerModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getControllerFields())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(ids, module))
				;

		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			Map<Long, ControllerContext> controllers = new HashMap<>();
			for(Map<String, Object> prop : props) {
				ControllerContext controller = FieldUtil.getAsBeanFromMap(prop, ControllerContext.class); 
				controllers.put(controller.getId(), controller);
			}
			return controllers;
		}
		return null;
	}
	
	private static Map<Long, List<Long>> getControllerBuildingIds(List<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getControllerBuildingRelModule();
		List<FacilioField> fields = FieldFactory.getControllerBuildingRelFields();
		FacilioField controllerIdField = FieldFactory.getAsMap(fields).get("controllerId");
		
		List<Map<String, Object>> relProps = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getCondition(controllerIdField, ids, NumberOperators.EQUALS))
													.get();
		
		if (relProps != null && !relProps.isEmpty()) {
			Map<Long, List<Long>> controllerMap = new HashMap<>();
			for (Map<String, Object> prop : relProps) {
				Long controllerId = (Long) prop.get("controllerId");
				List<Long> buildingIds = controllerMap.get(controllerId);
				if (buildingIds == null) {
					buildingIds = new ArrayList<>();
					controllerMap.put(controllerId, buildingIds);
				}
				buildingIds.add((Long) prop.get("buildingId"));
			}
			return controllerMap;
		}
		return null;
	}
	
	public static int getDataIntervalInMin(ControllerContext controller) throws Exception {
		if (controller.getDataInterval() != -1) {
			return controller.getDataInterval();
		}
		else {
			return ReadingsAPI.getOrgDefaultDataIntervalInMin();
		}
	}
	
	public static long adjustTime (long time, int dataInterval) throws Exception {
		ZonedDateTime zdt = DateTimeUtil.getDateTime(time);
		return zdt.truncatedTo(new SecondsChronoUnit(dataInterval * 60)).toInstant().toEpochMilli();
	}
	
	public static long addControllerActivity (ControllerContext controller, long time, MultiModuleReadingData readingData) throws Exception {
		Map<String, Object> prop = new HashMap<>();
		prop.put("orgId", AccountUtil.getCurrentOrg().getId());
		prop.put("siteId", controller.getSiteId());
		prop.put("controllerMacAddr", controller.getMacAddr());
		prop.put("createdTime", System.currentTimeMillis());
		prop.put("actualTime", time);
		
		int dataInterval = getDataIntervalInMin(controller);
		long recordTime = adjustTime(time, dataInterval);
		prop.put("recordTime", recordTime);
		
		long id = new GenericInsertRecordBuilder()
						.fields(FieldFactory.getControllerActivityFields())
						.table(ModuleFactory.getControllerActivityModule().getTableName())
						.insert(prop);
		
		if (readingData != null) {
			prop.put("currentRecords", FieldUtil.getAsJSON(readingData).toJSONString());
			
			new GenericInsertRecordBuilder()
				.fields(FieldFactory.getContollerActivityRecordsFields())
				.table(ModuleFactory.getControllerActivityRecordsModule().getTableName())
				.insert(prop);
		}
		
		updateController(Collections.singletonMap("lastDataReceivedTime", recordTime), Collections.singletonList(controller.getId()));
		
		return id;
	}
	
	public static Map<Long, JSONObject> getControllerActivityRecords (Collection<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getControllerActivityRecordsModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(FieldFactory.getContollerActivityRecordsFields())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(ids, module))
														;
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			JSONParser parser = new JSONParser();
			Map<Long, JSONObject> records = new HashMap<>();
			for (Map<String, Object> prop : props) {
				JSONObject json = (JSONObject) parser.parse(prop.get("currentRecords").toString());
				records.put((Long) prop.get("id"), json);
			}
			return records;
		}
		return null;
	}
	
	public static List<Map<String, Object>> getControllerActivities (Collection<ControllerContext> controller, long time) throws Exception {
		FacilioModule module = ModuleFactory.getControllerActivityModule();
		List<FacilioField> fields = FieldFactory.getControllerActivityFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField macAddrField = fieldMap.get("controllerMacAddr");
		FacilioField timeField = fieldMap.get("recordTime");
		
		String macAddr = controller.stream().map(ControllerContext::getMacAddr).collect(Collectors.joining(","));
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.select(fields)
																.table(module.getTableName())
//																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCondition(CriteriaAPI.getCondition(macAddrField, macAddr, StringOperators.IS))
																.andCondition(CriteriaAPI.getCondition(timeField, String.valueOf(time), DateOperators.IS))
																;
		
		List<Map<String, Object>> props = selectRecordBuilder.get();
		return props;
	}
	
	public static void addControllerBuildingRel (ControllerContext controller) throws Exception {
		if (controller.getBuildingIds() != null && !controller.getBuildingIds().isEmpty()) {
			GenericInsertRecordBuilder relBuilder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getControllerBuildingRelModule().getTableName())
														.fields(FieldFactory.getControllerBuildingRelFields());
			for (long buildingId: controller.getBuildingIds()) {
				Map<String, Object> prop = new HashMap<>();
				prop.put("orgId", AccountUtil.getCurrentOrg().getId());
				prop.put("siteId", controller.getSiteId());
				prop.put("buildingId", buildingId);
				prop.put("controllerId", controller.getId());
				relBuilder.addRecord(prop);
			}
			relBuilder.save();
		}
	}
	
	public static ControllerActivityWatcherContext getActivityWatcher (long time, int dataInterval, int level) throws Exception {
		FacilioModule module = ModuleFactory.getControllerActivityWatcherModule();
		List<FacilioField> fields = FieldFactory.getContollerActivityWatcherFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField recordTimeField = fieldMap.get("recordTime");
		FacilioField intervalField = fieldMap.get("dataInterval");
		FacilioField levelField = fieldMap.get("level");
		time = adjustTime(time, dataInterval);
		LOGGER.debug("Fetching watcher for time : "+time+" and interval : "+dataInterval);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(recordTimeField, String.valueOf(time), DateOperators.IS))
														.andCondition(CriteriaAPI.getCondition(intervalField, String.valueOf(dataInterval), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(levelField, String.valueOf(level), NumberOperators.EQUALS))
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(props.get(0), ControllerActivityWatcherContext.class);
		}
		return null;
	}
	
	public static ControllerActivityWatcherContext addActivityWatcher (long time, int dataInterval, int level) throws Exception {
		FacilioModule module = ModuleFactory.getControllerActivityWatcherModule();
		List<FacilioField> fields = FieldFactory.getContollerActivityWatcherFields();
		
		Map<String, Object> prop = new HashMap<>();
		prop.put("orgId", AccountUtil.getCurrentOrg().getId());
		prop.put("recordTime", adjustTime(time, dataInterval));
		prop.put("createdTime", System.currentTimeMillis());
		prop.put("dataInterval", dataInterval);
		prop.put("level", level);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.fields(fields)
														.table(module.getTableName())
														;
		
		insertBuilder.insert(prop);
		
		return FieldUtil.getAsBeanFromMap(prop, ControllerActivityWatcherContext.class);
	}
	
	public static void scheduleControllerActivityJob (ControllerActivityWatcherContext watcher, List<ControllerContext> controllers) {
		FacilioContext jobContext = new FacilioContext();
		jobContext.put(FacilioConstants.ContextNames.CONTROLLER_ACTIVITY_WATCHER, watcher);
		jobContext.put(FacilioConstants.ContextNames.CONTROLLER_LIST, controllers);
		jobContext.put(FacilioConstants.ContextNames.START_TIME, System.currentTimeMillis());
		
		FacilioTimer.scheduleInstantJob("ControllerActivityWatcher", jobContext);
	}
	
	public static int markWatcherAsComplete (long id) throws SQLException {
		FacilioModule module = ModuleFactory.getControllerActivityWatcherModule();
		List<FacilioField> fields = FieldFactory.getContollerActivityWatcherFields();
		
		Map<String, Object> prop = new HashMap<>();
		prop.put("completionStatus", true);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(fields)
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module))
														;
		return updateBuilder.update(prop);
	}
	
	public static List<ControllerContext> getActtiveControllers (int dataInterval) throws Exception {
		FacilioModule module = ModuleFactory.getControllerModule();
		List<FacilioField> fields = FieldFactory.getControllerFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField activeField = fieldMap.get("active");
		
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(activeField, String.valueOf(true), BooleanOperators.IS))
														;
		
		FacilioField intervalField = fieldMap.get("dataInterval");
		if (dataInterval == -1) {
			ruleBuilder.andCondition(CriteriaAPI.getCondition(intervalField, CommonOperators.IS_EMPTY));
		}
		else {
			ruleBuilder.andCondition(CriteriaAPI.getCondition(intervalField, String.valueOf(dataInterval), NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> props = ruleBuilder.get();
		return getControllerFromMapList(props, false);
	}
	
	public static int makeControllerInActive (List<Long> ids) throws SQLException {
		Map<String, Object> prop = new HashMap<>();
		prop.put("active", false);
		
		return updateController(prop, ids);
	}
	
	public static int makeControllerActive (List<Long> ids) throws SQLException {
		Map<String, Object> prop = new HashMap<>();
		prop.put("active", true);
		
		return updateController(prop, ids);
	}
	
	public static int updateControllerModifiedTime (long id) throws Exception {
		return updateController(Collections.singletonMap("lastModifiedTime", System.currentTimeMillis()), id);
	}
	
	public static int updateController (Map<String,Object> prop, long id) throws Exception {
		return updateController(prop, Collections.singletonList(id));
	}
	
	public static int updateController (Map<String,Object> prop, List<Long> ids) throws SQLException {
		FacilioModule module = ModuleFactory.getControllerModule();
		List<FacilioField> fields = FieldFactory.getControllerFields();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(ids, module))
				;

		return updateBuilder.update(prop);
	}

	public static ControllerContext getController(String deviceName, String deviceId) throws Exception {
		return getController(deviceName, deviceId, false);
	}
}
