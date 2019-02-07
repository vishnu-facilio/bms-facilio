package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.AlarmContext.AlarmType;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.templates.PushNotificationTemplate;
import com.facilio.bmsconsole.templates.SMSTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.workflows.context.WorkflowContext;

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
	
	public static Map<Long,ActionContext> getActionsAsMap(List<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getActionModule();
		GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getActionFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		List<ActionContext> actions = getActionsFromPropList(actionBuilder.get());
		if(actions != null && !actions.isEmpty()) {
			Map<Long,ActionContext> actionMap = new HashMap<>();
			for(ActionContext action :actions) {
				actionMap.put(action.getId(), action);
			}
			return actionMap;
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
	
	public static List<ActionContext> addActions(List<ActionContext> actions,WorkflowRuleContext rule) throws Exception {
		
		if (actions != null && !actions.isEmpty()) {
			List<ActionContext> actionsToBeAdded = new ArrayList<>();
			for(ActionContext action:actions) {
				if (action.getId() == -1) {
					if (action.getTemplate() == null && action.getTemplateJson() != null) {
						System.out.println(action.getTemplateJson());
						switch (action.getActionTypeEnum()) {
							case EMAIL_NOTIFICATION:
							case BULK_EMAIL_NOTIFICATION:
								setEmailTemplate(action);
								break;
							case SMS_NOTIFICATION:
							case BULK_SMS_NOTIFICATION:
								setSMSTemplate(action);
								break;
							case PUSH_NOTIFICATION:
								setMobileTemplate(action);
								break;
							case ADD_ALARM:
								setJsonTemplate(action, rule, Type.ALARM);
								break;
							case CREATE_WO_FROM_ALARM:
								setWorkorderTemplate(action, rule);
								break;
							case FIELD_CHANGE:
							case CREATE_WORK_ORDER:
								setJsonTemplate(action, rule, Type.JSON);
								break;
							default:
								break;
						}
					}
					if (action.getTemplateId() == -1) {
						action.setTemplateId(TemplateAPI.addTemplate(action.getTemplate()));
					}
					actionsToBeAdded.add(action);
				}
			}
			ActionAPI.addActions(actionsToBeAdded);
		}
		return actions;
	}
	
	private static void setEmailTemplate(ActionContext action) {
		EMailTemplate emailTemplate = new EMailTemplate();
		emailTemplate.setFrom("noreply@${org.domain}.facilio.com");
		String toAdresses = action.getTemplateJson().get("to").toString();
//		toAdresses = toAdresses.substring(1, toAdresses.length()-1);
		
		if(toAdresses.contains(",")) {
			action.setActionType(ActionType.BULK_EMAIL_NOTIFICATION);
		}
		
		emailTemplate.setTo(toAdresses);
		emailTemplate.setName((String) action.getTemplateJson().get("name"));
		emailTemplate.setSubject((String) action.getTemplateJson().get("subject"));
		emailTemplate.setMessage((String) action.getTemplateJson().get("message"));
		emailTemplate.setType(Type.EMAIL);
		if (action.getTemplateJson().containsKey("ftl")) {
			emailTemplate.setFtl((Boolean) action.getTemplateJson().get("ftl"));
		}
		action.setTemplate(emailTemplate);
		
		checkAndSetWorkflow(action.getTemplateJson(), emailTemplate);
	}
	
	private static void setSMSTemplate(ActionContext action) {
		SMSTemplate smsTemplate = new SMSTemplate();
		String toPhones = action.getTemplateJson().get("to").toString();
//		toPhones = toPhones.substring(1, toPhones.length()-1);
		
		if(toPhones.contains(",")) {
			action.setActionType(ActionType.BULK_SMS_NOTIFICATION);
		}
		smsTemplate.setName((String) action.getTemplateJson().get("name"));
		smsTemplate.setTo(toPhones);
		smsTemplate.setMessage((String) action.getTemplateJson().get("body"));
		smsTemplate.setType(Type.SMS);
		action.setTemplate(smsTemplate);
		checkAndSetWorkflow(action.getTemplateJson(), smsTemplate);
	}
	
	private static void setMobileTemplate(ActionContext action) {
		PushNotificationTemplate pushNotificationTemplate = new PushNotificationTemplate();
		pushNotificationTemplate.setTo((String) action.getTemplateJson().get("id"));
		pushNotificationTemplate.setBody((String) action.getTemplateJson().get("body"));	// TODO needs to save only message...now saving entire json structure
		pushNotificationTemplate.setName((String) action.getTemplateJson().get("name"));
		pushNotificationTemplate.setType(Type.PUSH_NOTIFICATION);
		
		action.setTemplate(pushNotificationTemplate);
		checkAndSetWorkflow(action.getTemplateJson(), pushNotificationTemplate);
	}
	
	private static void checkAndSetWorkflow(JSONObject templateJson, Template template) {
		if (templateJson.containsKey("workflow")) {
			Map<String, Object> workflow = (Map<String, Object>) templateJson.get("workflow");
			WorkflowContext workflowContext = FieldUtil.getAsBeanFromMap(workflow, WorkflowContext.class);
			template.setWorkflow(workflowContext);
		}
	}
	
	private static void setJsonTemplate(ActionContext action, WorkflowRuleContext rule, Type templateType) throws Exception {
		List<Map> fieldMatcher = (ArrayList) action.getTemplateJson().get("fieldMatcher");
		JSONObject content = new JSONObject();
		for(Map field:fieldMatcher) {
			content.put(field.get("field").toString(), field.get("value"));
		}
		if (rule instanceof ReadingRuleContext) {
			ReadingRuleContext readingRule = (ReadingRuleContext) rule;
			
			AlarmType alarmType = null;
			if (readingRule.getAssetCategoryId() != -1) {
				alarmType = AlarmAPI.getAlarmTypeFromAssetCategory(readingRule.getAssetCategoryId());
			}
			else {
				alarmType = AlarmAPI.getAlarmTypeFromResource(readingRule.getResourceId()); 
			}
			if (alarmType != null) {
				content.put("alarmType", alarmType.getIntVal());
			}
		}
		JSONTemplate jsonTemplate = new JSONTemplate();
		jsonTemplate.setName(rule.getName()+"_json_template");
		jsonTemplate.setContent(content.toJSONString());
		jsonTemplate.setType(templateType);
		action.setTemplate(jsonTemplate);
		checkAndSetWorkflow(action.getTemplateJson(), jsonTemplate);
	}
	
	private static void setWorkorderTemplate(ActionContext action, WorkflowRuleContext rule) throws Exception {
		JSONObject woJson = action.getTemplateJson();
		WorkorderTemplate woTemplate = new WorkorderTemplate();
		woTemplate.setWorkorder(FieldUtil.getAsBeanFromJson(woJson, WorkOrderContext.class));
		if (woTemplate.getName() == null || woTemplate.getName().isEmpty()) {
			woTemplate.setName(rule.getName()+"_WO_Template");
		}
		action.setTemplate(woTemplate);
		checkAndSetWorkflow(action.getTemplateJson(), woTemplate);
	}
}
