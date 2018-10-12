package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ControllerActivityWatcherContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.time.SecondsChronoUnit;

public class ControllerAPI {
	private static final Logger LOGGER = LogManager.getLogger(ControllerAPI.class.getName());

	public static List<ControllerContext> getControllerSettings() throws Exception {
		FacilioModule module = ModuleFactory.getControllerModule();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getControllerFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				;
		
		List<Map<String, Object>> props = ruleBuilder.get();
		return getControllerFromMapList(props, true);
	}
	
	public static ControllerContext getController (String macAddress) throws Exception {
		return getController(macAddress, false);
	}
	
	public static ControllerContext getController (String macAddress, boolean fetchBuilding) throws Exception {
		FacilioModule module = ModuleFactory.getControllerModule();
		List<FacilioField> fields = FieldFactory.getControllerFields();
		FacilioField macAddrField = FieldFactory.getAsMap(fields).get("macAddr");
		
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(macAddrField, macAddress, StringOperators.IS))
				;
		
		List<Map<String, Object>> controllerList = ruleBuilder.get();
		List<ControllerContext> controllers = getControllerFromMapList(controllerList, fetchBuilding);
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
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
	
	private static Map<Long, List<Long>> getControllerBuildingIds(List<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getControllerBuildingRelModule();
		List<FacilioField> fields = FieldFactory.getControllerBuildingRelFields();
		FacilioField controllerIdField = FieldFactory.getAsMap(fields).get("controllerId");
		
		List<Map<String, Object>> relProps = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
	
	private static long adjustTime (long time, int dataInterval) throws Exception {
		ZonedDateTime zdt = DateTimeUtil.getDateTime(time);
		return zdt.truncatedTo(new SecondsChronoUnit(dataInterval * 60)).toInstant().toEpochMilli();
	}
	
	public static long addControllerActivity (ControllerContext controller, long time, Map<String, List<ReadingContext>> readingMap) throws Exception {
		Map<String, Object> prop = new HashMap<>();
		prop.put("orgId", AccountUtil.getCurrentOrg().getId());
		prop.put("siteId", controller.getSiteId());
		prop.put("controllerMacAddr", controller.getMacAddr());
		prop.put("createdTime", System.currentTimeMillis());
		
		int dataInterval = getDataIntervalInMin(controller);
		prop.put("recordTime", adjustTime(time, dataInterval));
		
		long id = new GenericInsertRecordBuilder()
						.fields(FieldFactory.getControllerActivityFields())
						.table(ModuleFactory.getControllerActivityModule().getTableName())
						.insert(prop);
		
		if (readingMap != null && !readingMap.isEmpty()) {
			prop.put("currentRecords", FieldUtil.getAsJSON(readingMap).toJSONString());
			
			new GenericInsertRecordBuilder()
				.fields(FieldFactory.getContollerActivityRecordsFields())
				.table(ModuleFactory.getControllerActivityRecordsModule().getTableName())
				.insert(prop);
		}
		
		return id;
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
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCondition(CriteriaAPI.getCondition(macAddrField, macAddr, StringOperators.IS))
																.andCondition(CriteriaAPI.getCondition(timeField, String.valueOf(timeField), NumberOperators.EQUALS))
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
	
	public static ControllerActivityWatcherContext getActivityWatcher (long time, int dataInterval) throws Exception {
		FacilioModule module = ModuleFactory.getControllerActivityWatcherModule();
		List<FacilioField> fields = FieldFactory.getContollerActivityWatcherFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField recordTimeField = fieldMap.get("recordTime");
		FacilioField intervalField = fieldMap.get("dataInterval");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(recordTimeField, String.valueOf(time), DateOperators.IS))
														.andCondition(CriteriaAPI.getCondition(intervalField, String.valueOf(adjustTime(time, dataInterval)), NumberOperators.EQUALS))
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(props.get(0), ControllerActivityWatcherContext.class);
		}
		return null;
	}
	
	public static ControllerActivityWatcherContext addActivityWatcher (long time, int dataInterval) throws Exception {
		FacilioModule module = ModuleFactory.getControllerActivityWatcherModule();
		List<FacilioField> fields = FieldFactory.getContollerActivityWatcherFields();
		
		Map<String, Object> prop = new HashMap<>();
		prop.put("orgId", AccountUtil.getCurrentOrg().getId());
		prop.put("recordTime", adjustTime(time, dataInterval));
		prop.put("createdTime", System.currentTimeMillis());
		prop.put("dataInterval", dataInterval);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.fields(fields)
														.table(module.getTableName())
														.addRecord(prop)
														;
		
		insertBuilder.insert(prop);
		
		return FieldUtil.getAsBeanFromMap(prop, ControllerActivityWatcherContext.class);
	}
	
	public static int markWatcherAsComplete (long id) throws SQLException {
		FacilioModule module = ModuleFactory.getControllerActivityWatcherModule();
		List<FacilioField> fields = FieldFactory.getContollerActivityWatcherFields();
		
		Map<String, Object> prop = new HashMap<>();
		prop.put("completionStatus", true);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module))
														;
		return updateBuilder.update(prop);
	}
	
	public static List<ControllerContext> getControllers (int dataInterval) throws Exception {
		FacilioModule module = ModuleFactory.getControllerModule();
		List<FacilioField> fields = FieldFactory.getControllerFields();
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														;
		
		FacilioField intervalField = FieldFactory.getAsMap(fields).get("");
		if (dataInterval == -1) {
			ruleBuilder.andCondition(CriteriaAPI.getCondition(intervalField, CommonOperators.IS_EMPTY));
		}
		else {
			ruleBuilder.andCondition(CriteriaAPI.getCondition(intervalField, String.valueOf(dataInterval), NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> props = ruleBuilder.get();
		return getControllerFromMapList(props, false);
	}
}
