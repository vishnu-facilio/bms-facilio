package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ControllerSettingsContext;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.workflow.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class ControllerAPI {
	
	public static List<ControllerSettingsContext> getControllerSettings(long orgId) throws Exception {
		try {
			GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getControllerFields())
					.table("Controller")
					.andCustomWhere("Controller.ORGID = ? ", orgId);
			return getControllerFromMapList(ruleBuilder.get(), orgId);
		}
		catch(SQLException e) {
			throw e;
		}
	}

	
	public static ControllerSettingsContext getControllerSettingsFromId(long orgId, long id) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
					.connection(conn)
					.table("Controller")
					.select(FieldFactory.getControllerFields())
					.andCustomWhere("ORGID = ? AND ID = ?",orgId, id);
			
			List<Map<String, Object>> controllerList = ruleBuilder.get();
			if(controllerList != null && controllerList.size() > 0) {
				Map<String, Object> controller = controllerList.get(0);
				return FieldUtil.getAsBeanFromMap(controller, ControllerSettingsContext.class);
//				return getSupportEmailFromMap(email);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}
	
	
	
	
	private static List<ControllerSettingsContext> getControllerFromMapList(List<Map<String, Object>> props, long orgId) throws Exception {
		if(props != null && props.size() > 0) {
			List<ControllerSettingsContext> controllers = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				ControllerSettingsContext controller = FieldUtil.getAsBeanFromMap(prop, ControllerSettingsContext.class);
				//long criteriaId = controller.getCriteriaId();
//				controller.setCriteria(CriteriaAPI.getCriteria(orgId, criteriaId));
				controllers.add(controller);
			}
			return controllers;
		}
		return null;
	}
	
	private static List<ControllerSettingsContext> getControllerFromMapList(List<Map<String, Object>> props, long orgId, Connection conn) throws Exception {
		if(props != null && props.size() > 0) {
			List<ControllerSettingsContext> controllerSettings = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				ControllerSettingsContext controllerSetting = FieldUtil.getAsBeanFromMap(prop, ControllerSettingsContext.class);
				controllerSettings.add(controllerSetting);
			}
			return controllerSettings;
		}
		return null;
	}
}
