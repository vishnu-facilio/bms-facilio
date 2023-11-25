package com.facilio.bmsconsole.util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.templates.*;

import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.flows.action.FlowAction;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.services.email.EmailClient;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext.AlarmType;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.templates.DefaultTemplate.DefaultTemplateType;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.cb.context.ChatBotIntentAction;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

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
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere("Workflow_Rule_Action.WORKFLOW_RULE_ID = ? AND Action.STATUS = ?", workflowRuleId, true);
		return getActionsFromPropList(actionBuilder.get());
	}
	
	public static ActionContext getAction(long id) throws Exception {
		FacilioModule module = ModuleFactory.getActionModule();
		GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getActionFields())
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module));

		List<ActionContext> actions = getActionsFromPropList(actionBuilder.get());
		if(actions != null && !actions.isEmpty()) {
			return actions.get(0);
		}
		return null;
	}

	public static List<ActionContext> getActions(List<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getActionModule();
		GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getActionFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(ids,module));

		List<ActionContext> actions = getActionsFromPropList(actionBuilder.get());
		if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(actions)) {
			return actions;
		}
		return Collections.EMPTY_LIST;
	}
	
	public static Map<Long,ActionContext> getActionsAsMap(List<Long> ids) throws Exception {
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		
		FacilioModule module = ModuleFactory.getActionModule();
		GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getActionFields())
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
	
	public static Map<Long,ActionContext> getActionsFromTemplate(List<Long> templateIds, boolean fetchDetails) throws Exception {
		if (templateIds == null || templateIds.isEmpty()) {
			return null;
		}
		
		List<FacilioField> fields = FieldFactory.getActionFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		FacilioModule module = ModuleFactory.getActionModule();
		GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCondition(fieldMap.get("templateId"), templateIds, NumberOperators.EQUALS))
														;
		
		List<Map<String, Object>> props = actionBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			List<ActionContext> actions;
			if (fetchDetails) {
				actions = getActionsFromPropList(props);
			}
			else {
				actions = FieldUtil.getAsBeanListFromMapList(props, ActionContext.class);
			}
			return actions.stream().collect(Collectors.toMap(ActionContext::getTemplateId, Function.identity()));
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
				
				if (action.getTemplateId() == -1 && TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACTION,action.getDefaultTemplateId()) == null) {
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
	
	public static List<ChatBotIntentAction> getActionsFromChatBotIntent(long intentId) throws Exception {
		
		List<FacilioField> cbIntentActionFields = FieldFactory.getCBIntentActionFields();
		List<FacilioField> actionFields = FieldFactory.getActionFields();
		
		Map<String, FacilioField> cbActionFieldMap = FieldFactory.getAsMap(cbIntentActionFields);
		
		Map<String, FacilioField> actionFieldMap = FieldFactory.getAsMap(actionFields);
		
		actionFields.addAll(cbIntentActionFields);
		
		GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
				.select(actionFields)
				.table(ModuleFactory.getActionModule().getTableName())
				.innerJoin(ModuleFactory.getCBIntentActionModule().getTableName())
				.on(ModuleFactory.getActionModule().getTableName()+".ID = "+ ModuleFactory.getCBIntentActionModule().getTableName() +".ACTION_ID")
				.andCondition(CriteriaAPI.getCondition(cbActionFieldMap.get("intentId"), intentId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(actionFieldMap.get("status"), Boolean.TRUE.toString(),BooleanOperators.IS));
		
		List<Map<String, Object>> props = actionBuilder.get();
		
		if(props != null && props.size() > 0) {
			List<ChatBotIntentAction> actions = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				ActionContext action = FieldUtil.getAsBeanFromMap(prop, ActionContext.class);
				
				if(action.getTemplateId() != -1) {
					action.setTemplate(TemplateAPI.getTemplate(action.getTemplateId())); //Template should be obtained from some api
				}
				if(action.getActionTypeEnum().isTemplateNeeded() && action.getTemplate() == null) {
					throw new IllegalArgumentException("Invalid template for action : "+action.getId());
				}
				
				ChatBotIntentAction cbAction = FieldUtil.getAsBeanFromMap(prop, ChatBotIntentAction.class);
				
				cbAction.setAction(action);
				
				actions.add(cbAction);
			}
			return actions;
		}
		return null;
	}
	
	public static List<ActionContext> getActionsFromPropList(List<Map<String, Object>> props) throws Exception {
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
		if (CollectionUtils.isEmpty(actionIds)) {
			return;
		}
		
		FacilioModule module = ModuleFactory.getActionModule();
		GenericSelectRecordBuilder actionBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getActionFields())
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														;
		if (actionIds != null && !actionIds.isEmpty()) {
			actionBuilder.andCondition(CriteriaAPI.getIdCondition(actionIds, module));
		}
		
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
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(actionModule))
					.andCondition(CriteriaAPI.getIdCondition(actionIds, actionModule));
			deleteBuilder.delete();
			TemplateAPI.deleteTemplates(templateIds);
		}
	}

	public static List<ActionContext> addActions(List<ActionContext> actions,WorkflowRuleContext rule) throws Exception {
		return addActions(actions, rule, true);
	}
	public static List<ActionContext> addQandARuleActions(List<ActionContext> actions,String templatePrefix) throws Exception {
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
							case MAKE_CALL:
								setCallTemplate(action);
								break;
							case WHATSAPP_MESSAGE:
								setWhatsappMessageTemplate(action);
								break;
							case PUSH_NOTIFICATION:
								setMobileTemplate(action);
								break;
							case WORKFLOW_ACTION:
							case WORKFLOW_ACTION_WITH_LIST_PARAMS:
								setWorkflowTemplate(action,templatePrefix,Type.WORKFLOW);
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
	public static List<ActionContext> addActions(List<ActionContext> actions,WorkflowRuleContext rule, boolean shouldSave) throws Exception {
		if (actions != null && !actions.isEmpty()) {
			List<ActionContext> actionsToBeAdded = new ArrayList<>();
			for(ActionContext action : actions) {
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
							case MAKE_CALL:
								setCallTemplate(action);
								break;
							case WHATSAPP_MESSAGE:
								setWhatsappMessageTemplate(action);
								break;
							case PUSH_NOTIFICATION:
								setMobileTemplate(action);
								break;
							case ADD_ALARM:
							case ADD_VIOLATION_ALARM:
								setJsonTemplate(action, rule, Type.ALARM);
								break;
							case CREATE_WO_FROM_ALARM:
							case CLOSE_WO_FROM_ALARM:
								setWorkorderTemplate(action, rule);
								break;
							case FIELD_CHANGE:
							case CREATE_WORK_ORDER:
								setJsonTemplate(action, rule, Type.JSON);
								break;
							case CREATE_RECORD:
								if(action.getTemplateJson().containsKey("moduleName") && action.getTemplateJson().containsKey("data") &&
										action.getTemplateJson().get("moduleName") != null && action.getTemplateJson().get("data") != null){
									setDefaultTemplate(action,rule);
								}else{
									throw new Exception("Invalid create record action configuration");
								}
								break;
							case ACTIVITY_FOR_MODULE_RECORD:
								setDefaultTemplate(action, rule);
								break;
							case FORMULA_FIELD_CHANGE:
							case ALARM_IMPACT_ACTION:
							case IMPACTS:
							case WORKFLOW_ACTION:
							case WORKFLOW_ACTION_WITH_LIST_PARAMS:
								setWorkflowTemplate(action,rule.getName(),Type.WORKFLOW);
								break;
							case CONTROL_ACTION:
								setControlActionTemplate(action, rule);
								break;
							case REPORT_DOWNTIME_ACTION:
							case CHANGE_STATE:
							case INVOKE_TRIGGER:
							case FLOW_ACTION:
								setDefaultTemplate(action, rule);
								break;
							case MAIL_TO_CUSTOM_MODULE_RECORD:
							case MAIL_TO_CREATEWO:
							case EMAIL_CONVERSATION:
								setFormTemplate(action, SourceType.EMAIL_REQUEST);
								break;
							case CREATE_SATISFACTION_SURVEY:
								setSatisfactionSurveyRuleTemplate (action,rule);
								break;
							default:
								break;
						}
					}
					if (action.getTemplateId() == -1 && action.getTemplate() != null) {
						action.setTemplateId(TemplateAPI.addTemplate(action.getTemplate()));
					}
					actionsToBeAdded.add(action);
				}
			}
			if (shouldSave) {
				ActionAPI.addActions(actionsToBeAdded);
			}
		}
		return actions;
	}

	private static void setSatisfactionSurveyRuleTemplate (ActionContext action,WorkflowRuleContext rule ) throws Exception {
		if(rule.getModuleName().equals(FacilioConstants.ContextNames.WORK_ORDER)) {
			WorkOrderSatisfactionSurveyRuleTemplateContext template = FieldUtil.getAsBeanFromJson(action.getTemplateJson(), WorkOrderSatisfactionSurveyRuleTemplateContext.class);
			if (StringUtils.isEmpty(template.getName())) {
				template.setName(rule.getName() + "_Satisfaction_Action_Template");
			}
			action.setTemplate(template);
			checkAndSetWorkflow(action.getTemplateJson(), template);
		}
		if(rule.getModuleName().equals(FacilioConstants.ContextNames.SERVICE_REQUEST)) {
			ServiceRequestSatisfactionSurveyRuleTemplateContext template = FieldUtil.getAsBeanFromJson(action.getTemplateJson(), ServiceRequestSatisfactionSurveyRuleTemplateContext.class);
			if (StringUtils.isEmpty(template.getName())) {
				template.setName(rule.getName() + "_ServiceRequest_Satisfaction_Action_Template");
			}
			action.setTemplate(template);
			checkAndSetWorkflow(action.getTemplateJson(), template);
		}
	}
	private static void setControlActionTemplate(ActionContext action, WorkflowRuleContext rule) throws IOException {
		ControlActionTemplate template = FieldUtil.getAsBeanFromJson(action.getTemplateJson(), ControlActionTemplate.class);
		if (StringUtils.isEmpty(template.getName())) {
			template.setName(rule.getName() + "_Control_Action_Template");
		}
		action.setTemplate(template);
		checkAndSetWorkflow(action.getTemplateJson(), template);
	}

	public static long getEMailAddressID(String email) {
		long emailAddressID = -1L;
		try {
			GenericSelectRecordBuilder selectBdr = new GenericSelectRecordBuilder()
					.select(Collections.singletonList(FieldFactory.getField("ID", "ID", FieldType.ID)))
					.table("Email_From_Address")
					.andCondition(CriteriaAPI.getCondition("email", "EMAIL", email, StringOperators.IS));

			List<Map<String, Object>> resultSet = selectBdr.get();
			emailAddressID = (long) resultSet.get(0).get("ID");
		} catch (Exception e) {
			LOGGER.error("unable to fetch email address id", e);
		}
		return emailAddressID;
	}

	public static String getEMailAddress(long id) {
		String emailAddress = "";
		try {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			SelectRecordsBuilder<EmailFromAddress> select = new SelectRecordsBuilder<EmailFromAddress>()
					.moduleName(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME)
					.beanClass(EmailFromAddress.class)
					.select(modBean.getAllFields(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME))
					.andCondition(CriteriaAPI.getIdCondition(id, modBean.getModule(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME)));
			
			EmailFromAddress fromAddress = select.fetchFirst();
			
			emailAddress = MailMessageUtil.getWholeEmailFromNameAndEmail.apply(fromAddress.getDisplayName(), fromAddress.getEmail());
		} catch (Exception e) {
			LOGGER.error("unable to fetch email address", e);
		}
		return emailAddress;
	}

	private static void setEmailTemplate(ActionContext action) {
		EMailTemplate emailTemplate = new EMailTemplate();

		if (action.getTemplateJson().get("fromAddr") == null) {
			String noReplyAddr = EmailClient.getNoReplyFromEmail();
			emailTemplate.setFromID(getEMailAddressID(EmailClient.getNoReplyFromEmail()));
			emailTemplate.setFrom(noReplyAddr);
		} else {
			long fromAddr = FacilioUtil.parseLong(action.getTemplateJson().get("fromAddr"));
			emailTemplate.setFromID(fromAddr);
			emailTemplate.setFrom(getEMailAddress(fromAddr));
		}

		String toAdresses = action.getTemplateJson().get("to").toString();
//		toAdresses = toAdresses.substring(1, toAdresses.length()-1);
		String ccAddress = null;
		String bccAddress = null;
		if (action.getTemplateJson().get("cc") != null) {
			ccAddress = action.getTemplateJson().get("cc").toString();
			emailTemplate.setCc(ccAddress);
		}
		if (action.getTemplateJson().get("bcc") != null) {
			bccAddress = action.getTemplateJson().get("bcc").toString();
			emailTemplate.setBcc(bccAddress);
		}
		if(toAdresses.contains(",") || (ccAddress != null && StringUtils.isNotEmpty(ccAddress)) || (bccAddress != null && StringUtils.isNotEmpty(bccAddress))) {
			action.setActionType(ActionType.BULK_EMAIL_NOTIFICATION);
		}
		
		emailTemplate.setTo(toAdresses);
		emailTemplate.setName((String) action.getTemplateJson().get("name"));
		emailTemplate.setSubject((String) action.getTemplateJson().get("subject"));
		emailTemplate.setMessage((String) action.getTemplateJson().get("message"));
		emailTemplate.setType(Type.EMAIL);

		if (action.getTemplateJson().containsKey("emailStructureId")) {
			emailTemplate.setEmailStructureId(((Number) action.getTemplateJson().get("emailStructureId")).longValue());
		}
				
		if (action.getTemplateJson().containsKey("sendAsSeparateMail")) {
			emailTemplate.setSendAsSeparateMail((Boolean) action.getTemplateJson().get("sendAsSeparateMail"));
		}
		if (action.getTemplateJson().containsKey("ftl")) {
			emailTemplate.setFtl((Boolean) action.getTemplateJson().get("ftl"));
		}
		if (action.getTemplateJson().containsKey("html")) {
			emailTemplate.setHtml((Boolean) action.getTemplateJson().get("html"));
		}
		
		TemplateAPI.setAttachments((List<Map<String, Object>>) action.getTemplateJson().get("attachmentList"), emailTemplate);
		
		action.setTemplate(emailTemplate);
		
		checkAndSetWorkflow(action.getTemplateJson(), emailTemplate);
		checkAndSetUserWorkflow(action.getTemplateJson(), emailTemplate);
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
	
	
	private static void setCallTemplate(ActionContext action) {
		CallTemplate callTemplate = new CallTemplate();
		String toPhones = action.getTemplateJson().get("to").toString();

		callTemplate.setName((String) action.getTemplateJson().get("name"));
		callTemplate.setTo(toPhones);
		callTemplate.setMessage((String) action.getTemplateJson().get("body"));
		callTemplate.setType(Type.CALL);
		action.setTemplate(callTemplate);
		checkAndSetWorkflow(action.getTemplateJson(), callTemplate);
	}
	
	private static void setWhatsappMessageTemplate(ActionContext action) {
		WhatsappMessageTemplate whatsappMessageTemplate = new WhatsappMessageTemplate();
		String toPhones = action.getTemplateJson().get("to").toString();

		whatsappMessageTemplate.setName((String) action.getTemplateJson().get("name"));
		whatsappMessageTemplate.setTo(toPhones);
		whatsappMessageTemplate.setMessage((String) action.getTemplateJson().get("body"));
		whatsappMessageTemplate.setType(Type.WHATSAPP);
		if (action.getTemplateJson().containsKey("ftl")) {
			whatsappMessageTemplate.setFtl((Boolean) action.getTemplateJson().get("ftl"));
		}
		action.setTemplate(whatsappMessageTemplate);
		checkAndSetWorkflow(action.getTemplateJson(), whatsappMessageTemplate);
	}
	
	private static void setMobileTemplate(ActionContext action) throws Exception {
		Template template = generateMobileTemplate(action.getTemplateJson());
		action.setTemplate(template);
	}
	public static Template generateMobileTemplate(JSONObject templateJson) throws Exception {
		PushNotificationTemplate pushNotificationTemplate = new PushNotificationTemplate();
		pushNotificationTemplate.setTo((String) templateJson.get("id"));
		pushNotificationTemplate.setBody((String) templateJson.get("body"));	// TODO needs to save only message...now saving entire json structure
		pushNotificationTemplate.setName((String) templateJson.get("name"));
		pushNotificationTemplate.setType(Type.PUSH_NOTIFICATION);
		if (templateJson.containsKey("application") && templateJson.get("application") != null) {
			pushNotificationTemplate.setApplication(((Number) templateJson.get("application")).longValue());
		} else {
			// should be removed once the application id is migrated in ActionArray in SLA
			pushNotificationTemplate.setApplication(ApplicationApi.getApplicationIdForLinkName("newApp"));
		}
		if (templateJson.containsKey("isSendNotification")) {
			pushNotificationTemplate.setIsSendNotification((boolean) templateJson.get("isSendNotification"));
		} else {
			pushNotificationTemplate.setIsSendNotification(true);
		}
		checkAndSetWorkflow(templateJson, pushNotificationTemplate);
		checkAndSetUserWorkflow(templateJson, pushNotificationTemplate);
		return pushNotificationTemplate;
	}
	
	private static void checkAndSetWorkflow(JSONObject templateJson, Template template) {
		if (templateJson.containsKey("workflow")) {
			Map<String, Object> workflow = (Map<String, Object>) templateJson.get("workflow");
			WorkflowContext workflowContext = FieldUtil.getAsBeanFromMap(workflow, WorkflowContext.class);
			template.setWorkflow(workflowContext);
		}
	}
	
	private static void checkAndSetUserWorkflow(JSONObject templateJson, Template template) {
		if (templateJson.containsKey("userWorkflow") && templateJson.get("userWorkflow") != null) {
			Map<String, Object> workflow = (Map<String, Object>) templateJson.get("userWorkflow");
			if (workflow != null && !workflow.isEmpty()) {
				WorkflowContext workflowContext = FieldUtil.getAsBeanFromMap(workflow, WorkflowContext.class);
				template.setUserWorkflow(workflowContext);
			}
		}
	}
	
	
	private static void setDefaultTemplate(ActionContext action, WorkflowRuleContext rule) {
		JSONTemplate jsonTemplate = new JSONTemplate();
		jsonTemplate.setName(rule.getName());
		jsonTemplate.setContent(action.getTemplateJson().toString());
		action.setTemplate(jsonTemplate);
		checkAndSetWorkflow(action.getTemplateJson(),jsonTemplate);
	}
	
	private static void setJsonTemplate(ActionContext action, WorkflowRuleContext rule, Type templateType) throws Exception {
		List<Map> fieldMatcher = (ArrayList) action.getTemplateJson().get("fieldMatcher");
		JSONObject content = new JSONObject();
		JSONTemplate jsonTemplate = new JSONTemplate();
		for(Map field:fieldMatcher) {
			content.put(field.get("field").toString(), field.get("value"));
		}
		if (rule != null) {
			jsonTemplate.setName(rule.getName()+"_json_template");
			
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
		}
		
		jsonTemplate.setContent(content.toJSONString());
		jsonTemplate.setType(templateType);
		action.setTemplate(jsonTemplate);
		checkAndSetWorkflow(action.getTemplateJson(), jsonTemplate);
	}
	
	private static void setWorkflowTemplate(ActionContext action, String templatePrefix, Type templateType) throws Exception {
		
		JSONObject workflowTemplateJson = action.getTemplateJson();
		
		WorkflowTemplate workflowTemplate = FieldUtil.getAsBeanFromJson(workflowTemplateJson, WorkflowTemplate.class);
		
		workflowTemplate.getResultWorkflowContext().setWorkflowString(null);
		WorkflowContext workflowContext = workflowTemplate.getResultWorkflowContext();
		Long workflowId = workflowContext.getId();
		if(workflowId != null && workflowId > 0){
			WorkflowUtil.updateWorkflow(workflowContext, workflowId);
		}
		else{
			workflowId = WorkflowUtil.addWorkflow(workflowContext);
		}
		if(templatePrefix != null) {
			workflowTemplate.setName(templatePrefix+"_json_template");
		}
		else {
			workflowTemplate.setName("json_template");
		}
		workflowTemplate.setType(templateType);
		workflowTemplate.setResultWorkflowId(workflowId);
		
		action.setTemplate(workflowTemplate);
	}
	
	private static void setWorkorderTemplate(ActionContext action, WorkflowRuleContext rule) throws Exception {
		JSONObject woJson = action.getTemplateJson();
		WorkorderTemplate woTemplate = new WorkorderTemplate();
		woTemplate.setWorkorder(FieldUtil.getAsBeanFromJson(woJson, WorkOrderContext.class));
		if (woTemplate.getName() == null || woTemplate.getName().isEmpty()) {
			woTemplate.setName(rule.getName()+"_WO_Template");
		}
		action.setTemplate(woTemplate);
		woTemplate.setWorkflow(TemplateAPI.getWorkflow(woTemplate));
//		checkAndSetWorkflow(action.getTemplateJson(), woTemplate);
	}
	public static ActionContext getDefaultPropsForDowntimeAction(ActionType actionType){
		ActionContext action=new ActionContext();
		action.setActionType(actionType);
		action.setStatus(Boolean.TRUE);
		action.setTemplateJson(new JSONObject());
		return action;
	}
	
	private static void setFormTemplate(ActionContext action, SourceType sourceType) throws Exception {
		FormTemplate formTemplate = new FormTemplate();
		formTemplate.setFormId(((long) action.getTemplateJson().get("formId")));
		formTemplate.setName((String) action.getTemplateJson().get("name"));
		Map mappingJson = (HashMap) action.getTemplateJson().get("mappingJson");
		if (mappingJson != null) {
			JSONObject json = new JSONObject();
			json.putAll(mappingJson);
			formTemplate.setMappingJson(json);
		}
		// formTemplate.setMappingJson((JSONObject) action.getTemplateJson().get("mappingJson"));
		formTemplate.setSourceType(sourceType);
		formTemplate.setWorkflow(TemplateAPI.getWorkflow(formTemplate));
		action.setTemplate(formTemplate);
		
		//checkAndSetWorkflow(action.getTemplateJson(), formTemplate);
	}
}
