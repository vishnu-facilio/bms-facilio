package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class ActionAPI {

    private static final Logger LOGGER = LogManager.getLogger(ActionAPI.class.getName());
	public static List<ActionContext> getAllActionsFromWorkflowRule(long orgId, long workflowRuleId) throws Exception {
		GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getActionFields())
				.table("Action")
				.innerJoin("Workflow_Rule_Action")
				.on("Action.ID = Workflow_Rule_Action.ACTION_ID")
				.andCustomWhere("Action.ORGID = ? AND Workflow_Rule_Action.WORKFLOW_RULE_ID = ?", orgId, workflowRuleId);
		return getActionsFromPropList(actionBuilder.get());
	}
	
//	public static int updateActionOfRule(long orgId,  long workflowRuleId) throws Exception{
//		GenericUpdateRecordBuilder
//	}
	
	public static List<ActionContext> getActiveActionsFromWorkflowRule(long workflowRuleId) throws Exception {
		FacilioModule module = ModuleFactory.getActionModule();
		GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getActionFields())
				.table(module.getTableName())
				.innerJoin("Workflow_Rule_Action")
				.on("Action.ID = Workflow_Rule_Action.ACTION_ID")
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere("Workflow_Rule_Action.WORKFLOW_RULE_ID = ? AND Action.STATUS = ?", workflowRuleId, true);
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
			if (action.getStatus() == null) {
				action.setStatus(true);
			}
			if (action.getActionTypeEnum() == null) {
				throw new IllegalArgumentException("Action Type cannot be null during addition of action");
			}
			
			if (action.getActionTypeEnum().isTemplateNeeded()) {
				if (action.getTemplateId() == -1 && action.getDefaultTemplateId() == -1) {
					throw new IllegalArgumentException("Either template ID / default template has to be set for Action during addition");
				}
				
				if (action.getTemplateId() == -1 && TemplateAPI.getDefaultTemplate(action.getDefaultTemplateId()) == null) {
					throw new IllegalArgumentException("Invalid default template id for action during addition.");
				}
			}
			
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
	
	public static void addWorkflowRuleActionRel(long ruleId, List<ActionContext> actions) throws SQLException, RuntimeException {
		for(ActionContext action:actions) {
			Map<String, Object> workflowRuleActionProps = new HashMap<String, Object>();
			workflowRuleActionProps.put("workflowRuleId", ruleId);
			workflowRuleActionProps.put("actionId", action.getId());
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table("Workflow_Rule_Action")
														.fields(FieldFactory.getWorkflowRuleActionFields())
														.addRecord(workflowRuleActionProps);
			insertBuilder.save();
		}
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
		Map<String, Object> actionProps = FieldUtil.getAsProperties(action);
		
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
															.table("Action")
															.fields(FieldFactory.getActionFields())
															.andCustomWhere("ORGID = ? AND ID = ?", orgId, id);

		return updateRecordBuilder.update(actionProps);

	}
	
	private static List<ActionContext> getActionsFromPropList(List<Map<String, Object>> props) throws Exception {
		if(props != null && props.size() > 0) {
			List<ActionContext> actions = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				ActionContext action = FieldUtil.getAsBeanFromMap(prop, ActionContext.class);
				if(action.getTemplateId() != -1) {
					action.setTemplate(TemplateAPI.getTemplate(action.getTemplateId())); //Template should be obtained from some api
				}
				if(action.getActionTypeEnum().isTemplateNeeded() && action.getTemplate() == null) {
					throw new IllegalArgumentException("Invalid template for action : "+action.getId());
				}
				actions.add(action);
			}
			return actions;
		}
		return null;
	}
	
	public static void deleteAllActionsFromWorkflowRules(List<Long> workflowRuleIds) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowRuleActionModule();
		List<FacilioField> fields = FieldFactory.getWorkflowRuleActionFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		FacilioField ruleField = fieldsMap.get("workflowRuleId");
		GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(ruleField,workflowRuleIds, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = actionBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<Long> actionIds = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				long actionId = (long) prop.get("actionId");
				actionIds.add(actionId);
			}
			deleteActions(actionIds);
		}
		
	}
	
	public static void deleteActions(List<Long> actionIds) throws Exception {
		FacilioModule module = ModuleFactory.getActionModule();
		GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getActionFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(actionIds, module));
		
		List<Map<String, Object>> props = actionBuilder.get();
		if(props != null && !props.isEmpty()) {
			List<Long> templateIds = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				Long templateId = (Long) prop.get("templateId");
				if (templateId != null) {
					templateIds.add(templateId);
				}
			}
			
			FacilioModule actionModule = ModuleFactory.getActionModule();
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(actionModule.getTableName())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(actionModule))
					.andCondition(CriteriaAPI.getIdCondition(actionIds, actionModule));
			deleteBuilder.delete();
			TemplateAPI.deleteTemplates(templateIds);
		}
	}
}
