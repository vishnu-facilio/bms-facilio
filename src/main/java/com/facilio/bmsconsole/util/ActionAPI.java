package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class ActionAPI {
	public static List<ActionContext> getAllActionsFromWorkflowRule(long orgId, long workflowRuleId) throws Exception {
		GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getActionFields())
				.table("Action")
				.innerJoin("Workflow_Rule_Action")
				.on("Action.ID = Workflow_Rule_Action.ACTION_ID")
				.andCustomWhere("Action.ORGID = ? AND Workflow_Rule_Action.WORKFLOW_RULE_ID = ?", orgId, workflowRuleId);
		return getActionsFromPropList(actionBuilder.get());
	}
	
	public static List<ActionContext> getActiveActionsFromWorkflowRule(long orgId, long workflowRuleId) throws Exception {
		GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getActionFields())
				.table("Action")
				.innerJoin("Workflow_Rule_Action")
				.on("Action.ID = Workflow_Rule_Action.ACTION_ID")
				.andCustomWhere("Action.ORGID = ? AND Workflow_Rule_Action.WORKFLOW_RULE_ID = ? AND Action.STATUS = ?", orgId, workflowRuleId, true);
		return getActionsFromPropList(actionBuilder.get());
	}
	
	public static ActionContext getAction(long id) throws Exception {
		FacilioModule module = ModuleFactory.getActionModule();
		GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getActionFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module));
		
		List<ActionContext> actions = getActionsFromPropList(actionBuilder.get());
		if(actions != null && !actions.isEmpty()) {
			return actions.get(0);
		}
		return null;
	}
	
	public static List<Long> addActions(List<ActionContext> actions) throws Exception {
		List<Long> actionIds = new ArrayList<>();
		List<Map<String, Object>> actionProps = new ArrayList<>();
		long orgId = AccountUtil.getCurrentOrg().getId();
		for(ActionContext action:actions) {
			action.setOrgId(orgId);
			action.setStatus(true);
			actionProps.add(FieldUtil.getAsProperties(action));
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table("Action")
														.fields(FieldFactory.getActionFields())
														.addRecords(actionProps);
		insertBuilder.save();
		for(int i=0; i<actionProps.size(); i++) {
			long actionId = (long) actionProps.get(i).get("id");
			actionIds.add(actionId);
			actions.get(i).setId(actionId);
		}
		return actionIds;
	}
	
	public static List<ActionContext> getActionsFromWorkflowRuleName(long orgId, String workflowName) throws Exception {
		GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getActionFields())
				.table("Action")
				.innerJoin("Workflow_Rule_Action")
				.on("Action.ID = Workflow_Rule_Action.ACTION_ID")
				.innerJoin("Workflow_Rule")
				.on("Workflow_Rule_Action.WORKFLOW_RULE_ID = Workflow_Rule.ID")
				.andCustomWhere("Action.ORGID = ? AND Workflow_Rule.NAME = ?", orgId, workflowName);
		return getActionsFromPropList(actionBuilder.get());
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
