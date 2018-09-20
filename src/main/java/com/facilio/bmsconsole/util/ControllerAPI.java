package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.logging.log4j.util.Strings;

import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ControllerAPI {
	private static org.apache.log4j.Logger log = LogManager.getLogger(ControllerAPI.class.getName());

	public static List<ControllerContext> getControllerSettings(long orgId) throws Exception {
		try {
			GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getControllerFields())
					.table("Controller")
					.andCustomWhere("Controller.ORGID = ? ", orgId);
			
			List<Map<String, Object>> props = ruleBuilder.get();
			Map<Long, List<Long>> controllerBuilding = new HashMap<>();
			if (props != null && !props.isEmpty()) {
				List<Long> ids = new ArrayList<>();
				for(Map<String, Object> prop : props) {
					ids.add((long) prop.get("id"));
				}
				
				List<Map<String, Object>> relProps = new GenericSelectRecordBuilder()
					.select(FieldFactory.getControllerBuildingRelFields())
					.table("Controller_Building_Rel")
					.andCondition(CriteriaAPI.getCondition("CONTROLLER_ID", "controllerId", Strings.join(ids, ','), NumberOperators.EQUALS))
					.get();
				
				for (Map<String, Object> p: relProps) {
					long controllerId = (long) p.get("controllerId");
					if (controllerBuilding.get(controllerId) == null) {
						controllerBuilding.put(controllerId, new ArrayList<>());
					}
					controllerBuilding.get((long) p.get("controllerId")).add((long) p.get("buildingId"));
				}
			}			
			
			return getControllerFromMapList(props, controllerBuilding, orgId);
		}
		catch(SQLException e) {
			throw e;
		}
	}

	
	public static ControllerContext getControllerSettingsFromId(long orgId, long id) throws Exception {
		try {
			GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
					.table("Controller")
					.select(FieldFactory.getControllerFields())
					.andCustomWhere("ORGID = ? AND ID = ?",orgId, id);
			
			List<Map<String, Object>> controllerList = ruleBuilder.get();
			if(controllerList != null && controllerList.size() > 0) {
				Map<String, Object> controller = controllerList.get(0);
				return FieldUtil.getAsBeanFromMap(controller, ControllerContext.class);
//				return getSupportEmailFromMap(email);
			}
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		return null;
	}
	
	
	
	
	private static List<ControllerContext> getControllerFromMapList(List<Map<String, Object>> props, long orgId) throws Exception {
		if(props != null && props.size() > 0) {
			List<ControllerContext> controllers = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				ControllerContext controller = FieldUtil.getAsBeanFromMap(prop, ControllerContext.class);
				//long criteriaId = controller.getCriteriaId();
//				controller.setCriteria(CriteriaAPI.getCriteria(orgId, criteriaId));
				controllers.add(controller);
			}
			return controllers;
		}
		return null;
	}
	
	private static List<ControllerContext> getControllerFromMapList(List<Map<String, Object>> props, Map<Long, List<Long>> controllerBuilding, long orgId) throws Exception {
		if(props != null && props.size() > 0) {
			List<ControllerContext> controllerSettings = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				ControllerContext controllerSetting = FieldUtil.getAsBeanFromMap(prop, ControllerContext.class);
				controllerSetting.setBuildingIds(controllerBuilding.get(controllerSetting.getId()));
				controllerSettings.add(controllerSetting);
			}
			return controllerSettings;
		}
		return null;
	}
}
