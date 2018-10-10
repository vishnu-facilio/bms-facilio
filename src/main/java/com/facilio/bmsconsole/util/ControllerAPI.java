package com.facilio.bmsconsole.util;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
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
		FacilioField controllerIdField = FieldFactory.getAsIdMap(fields).get("controllerId");
		
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
	
	public static long getControllerDataInterval(ControllerContext controller) throws Exception {
		if (controller.getDataInterval() != -1) {
			return controller.getDataInterval() * 60;
		}
		else {
			return ReadingsAPI.getOrgDefaultDataIntervalInMin() * 60;
		}
	}
	
	private static long adjustTime (ControllerContext controller, long time) throws Exception {
		long dataInterval = getControllerDataInterval(controller);
		ZonedDateTime zdt = DateTimeUtil.getDateTime(time);
		return zdt.truncatedTo(new SecondsChronoUnit(dataInterval)).toInstant().toEpochMilli();
	}
	
	public static long addControllerActivity (ControllerContext controller, long time) throws Exception {
		FacilioModule module = ModuleFactory.getControllerActivityModule();
		Map<String, Object> prop = new HashMap<>();
		prop.put("orgId", AccountUtil.getCurrentOrg().getId());
		prop.put("siteId", controller.getSiteId());
		prop.put("controllerId", controller.getId());
		prop.put("createdTime", System.currentTimeMillis());
		prop.put("recordTime", adjustTime(controller, time));
		
		return new GenericInsertRecordBuilder()
					.fields(FieldFactory.getContollerActivityFields())
					.table(module.getTableName())
					.insert(prop);
	}
}
