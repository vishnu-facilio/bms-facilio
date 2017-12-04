package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class ActionAPI {
	public static List<ActionContext> getActionsFromWorkflowRule(long orgId, long workflowRuleId) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(FieldFactory.getActionFields())
					.table("Action")
					.innerJoin("Workflow_Rule_Action")
					.on("Action.ID = Workflow_Rule_Action.ACTION_ID")
					.andCustomWhere("Action.ORGID = ? AND Workflow_Rule_Action.WORKFLOW_RULE_ID = ?", orgId, workflowRuleId);
			return getActionsFromPropList(actionBuilder.get());
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
	public static List<Long> addActions(List<ActionContext> actions) throws Exception {
		List<Long> actionIds = new ArrayList<>();
		
		for(ActionContext action:actions) {
			Map<String, Object> actionProps = FieldUtil.getAsProperties(action);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table("Action")
														.fields(FieldFactory.getActionFields())
														.addRecord(actionProps);
			insertBuilder.save();
			actionIds.add((Long) actionProps.get("id"));
			action.setId((Long) actionProps.get("id"));
		}
		return actionIds;
		
	}
	public static List<ActionContext> getActionsFromWorkflowRuleName(long orgId, String workflowName) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(FieldFactory.getActionFields())
					.table("Action")
					.innerJoin("Workflow_Rule_Action")
					.on("Action.ID = Workflow_Rule_Action.ACTION_ID")
					.innerJoin("Workflow_Rule")
					.on("Workflow_Rule_Action.WORKFLOW_RULE_ID = Workflow_Rule.ID")
					.andCustomWhere("Action.ORGID = ? AND Workflow_Rule.NAME = ?", orgId, workflowName);
			return getActionsFromPropList(actionBuilder.get());
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static int updateAction(long orgId, ActionContext action, long id) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			Map<String, Object> actionProps = FieldUtil.getAsProperties(action);
			
			GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
																.connection(conn)
																.table("Action")
																.fields(FieldFactory.getActionFields())
																.andCustomWhere("ORGID = ? AND ID = ?", orgId, id);

			return updateRecordBuilder.update(actionProps);

		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private static List<ActionContext> getActionsFromPropList(List<Map<String, Object>> props) throws Exception {
		if(props != null && props.size() > 0) {
			List<ActionContext> actions = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				ActionContext action = FieldUtil.getAsBeanFromMap(prop, ActionContext.class);
				if(action.getTemplateId() != -1) {
					action.setTemplate(TemplateAPI.getTemplate(action.getOrgId(), action.getTemplateId())); //Template should be obtained from some api
					
					if(action.getTemplate() == null) {
						throw new IllegalArgumentException("Invalid template ID for action : "+action.getId());
					}
				}
				actions.add(action);
			}
			return actions;
		}
		return null;
	}
}
