package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.facilio.bmsconsole.util.AuditLogUtil;
import com.facilio.ims.handler.AuditLogHandler;
import org.apache.commons.chain.Command;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.NotificationContext;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.templates.SMSTemplate;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.util.NotificationAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class NotificationAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String notificationList() throws Exception {
		
		long ouid = AccountUtil.getCurrentUser().getOuid();
		
		setNotifications(NotificationAPI.getNotifications(ouid, 0, 30));
		setUnread(NotificationAPI.getUnreadNotificationsCount(ouid));
		setUnseen(NotificationAPI.getUnseenNotificationsCount(ouid));
		
		return SUCCESS;
	}
	
	public String markAsRead() throws Exception {
		
		long ouid = AccountUtil.getCurrentUser().getOuid();
		
		NotificationAPI.markAsRead(ouid, getId());
		
		setUnread(NotificationAPI.getUnreadNotificationsCount(ouid));
		
		return SUCCESS;
	}
	
	public String markAllAsRead() throws Exception {
		
		long ouid = AccountUtil.getCurrentUser().getOuid();
		
		NotificationAPI.markAllAsRead(ouid);
		
		setUnread(NotificationAPI.getUnreadNotificationsCount(ouid));
		
		return SUCCESS;
	}
	
	public String markAllAsSeen() throws Exception {
		
		long ouid = AccountUtil.getCurrentUser().getOuid();
		
		NotificationAPI.markAllAsSeen(ouid);
		
		setUnread(NotificationAPI.getUnseenNotificationsCount(ouid));
		
		return SUCCESS;
	}
	
	private List<NotificationContext> notifications;
	
	public List<NotificationContext> getNotifications() {
		return notifications;
	}
	public void setNotifications(List<NotificationContext> notifications) {
		this.notifications = notifications;
	}
	
	private int unread;
	
	public int getUnread() {
		return unread;
	}
	
	public void setUnread(int unread) {
		this.unread = unread;
	}
	
	private int unseen;
	
	public int getUnseen() {
		return unseen;
	}
	
	public void setUnseen(int unseen) {
		this.unseen = unseen;
	}
	
	private List<Long> id;
	public List<Long> getId() {
		return id;
	}
	public void setId(List<Long> id) {
		this.id = id;
	}

	public String addAlarmEMailNotifier() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.Workflow.NOTIFICATION_EMAIL, email);
		
		FacilioChain addAlarmEmailChain = FacilioChainFactory.getAddAlarmEMailNotifierChain();
		addAlarmEmailChain.execute(context);
		
		Boolean result = (Boolean) context.get(FacilioConstants.ContextNames.RESULT);
		if(result != null && result) {
			setResult("success");
		}
		
		return SUCCESS;
	}

	private String result;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

	private String email;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String addAlarmSMSNotifier() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.Workflow.NOTIFICATION_SMS, phone);
		
		FacilioChain addAlarmSMSChain = FacilioChainFactory.getAddAlarmSMSNotifierChain();
		addAlarmSMSChain.execute(context);
		
		Boolean result = (Boolean) context.get(FacilioConstants.ContextNames.RESULT);
		if(result != null && result) {
			setResult("success");
		}
		
		return SUCCESS;
	}
	
	private String phone;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String alarmCreationRulesList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		FacilioChain fetchAlarmCreationRules = FacilioChainFactory.getAlarmCreationRulesChain();
		fetchAlarmCreationRules.execute(context);
		
		alarmCreationRules = (List<WorkflowRuleContext>) context.get(FacilioConstants.Workflow.WORKFLOW_LIST);
		
		EMailTemplate emailTemplate = (EMailTemplate) TemplateAPI.getTemplate(AccountUtil.getCurrentOrg().getOrgId(), "New Alarm Raised", Type.EMAIL);
		if(emailTemplate != null)
		{
			setEmails(Arrays.asList(emailTemplate.getTo().split(", ")));
		}
		else
		{
			User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
			
			List<String> emails = new ArrayList<>();
			emails.add(superAdmin.getEmail());
			setEmails(emails);
		}
		
		SMSTemplate smsTemplate = (SMSTemplate) TemplateAPI.getTemplate(AccountUtil.getCurrentOrg().getOrgId(), "New Alarm Raised", Type.SMS);
		if(smsTemplate != null)
		{
			setPhoneNumbers(Arrays.asList(smsTemplate.getTo().split(", ")));
		}
		else
		{
			User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
			
			List<String> phoneNumbers = new ArrayList<>();
			phoneNumbers.add(superAdmin.getPhone());
			setPhoneNumbers(phoneNumbers);
		}
		
		return SUCCESS;
	}
	
	private List<String> emails;
	public List<String> getEmails() {
		return this.emails;
	}
	
	public void setEmails(List<String> emails) {
		this.emails = emails;
	}
	
	private List<String> phoneNumbers;
	public List<String> getPhoneNumbers() {
		return this.phoneNumbers;
	}
	
	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}
	
	private List<WorkflowRuleContext> alarmCreationRules;
	public List<WorkflowRuleContext> getAlarmCreationRules() {
		return alarmCreationRules;
	}
	public void setAlarmCreationRules(List<WorkflowRuleContext> alarmCreationRules) {
		this.alarmCreationRules = alarmCreationRules;
	}
	
	public String turnOffWorkflow() throws Exception {
		if(workflowId != -1) {
			WorkflowRuleContext workFlow = new WorkflowRuleContext();
			workFlow.setStatus(false);
			workFlow.setId(workflowId);
			WorkflowRuleAPI.updateWorkflowRule(workFlow);
			setResult("success");
			WorkflowRuleContext workflowRule = WorkflowRuleAPI.getWorkflowRule(workflowId);
			if (workflowRule != null) {
				String moduleName = workflowRule.getModuleName();
				AuditLogUtil.sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Automation Rule {%s} has been %s for %s module", workflowRule.getName(), "Turned off", moduleName),
						null,
						AuditLogHandler.RecordType.SETTING,
						"AutomationRule", workflowId)
						.setActionType(AuditLogHandler.ActionType.MISCELLANEOUS)
						.setLinkConfig(((Function<Void, String>) o -> {
							JSONArray array = new JSONArray();
							JSONObject json = new JSONObject();
							json.put("id", workflowId);
							json.put("moduleName", moduleName);
							json.put("navigateTo", "AutomationRule");
							array.add(json);
							return array.toJSONString();
						}).apply(null))
				);
			}
		}
		return SUCCESS;
	}
	
	public String turnOnWorkflow() throws Exception {
		if(workflowId != -1) {
			WorkflowRuleContext workFlow = new WorkflowRuleContext();
			workFlow.setStatus(true);
			workFlow.setId(workflowId);
			WorkflowRuleAPI.updateWorkflowRule(workFlow);
			setResult("success");
			WorkflowRuleContext workflowRule = WorkflowRuleAPI.getWorkflowRule(workflowId);
			if (workflowRule != null) {
				String moduleName = workflowRule.getModuleName();
				AuditLogUtil.sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Automation Rule {%s} has been %s for %s module", workflowRule.getName(), "Turned on", moduleName),
						null,
						AuditLogHandler.RecordType.SETTING,
						"AutomationRule", workflowId)
						.setActionType(AuditLogHandler.ActionType.MISCELLANEOUS)
						.setLinkConfig(((Function<Void, String>) o -> {
							JSONArray array = new JSONArray();
							JSONObject json = new JSONObject();
							json.put("id", workflowId);
							json.put("moduleName", moduleName);
							json.put("navigateTo", "AutomationRule");
							array.add(json);
							return array.toJSONString();
						}).apply(null))
				);
			}
		}
		return SUCCESS;
	}
	
	private ActionContext action;
	public ActionContext getAction() {
		return action;
	}
	public void setAction(ActionContext action) {
		this.action = action;
	}
	
	public String updateWorkFlowRuleAction() throws Exception { 
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, action);

		Command updateAction = FacilioChainFactory.getUpdateWorkflowRuleAction();
		updateAction.execute(context);
				
		return SUCCESS;
	}
	
	public JSONObject template;
	public JSONObject getTemplate() {
		return template;
	}
	public void setTemplate(JSONObject template) {
		this.template = template;
	}

	public String updateActionTemplateWorkFlowRule() throws Exception { 
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.Workflow.TEMPLATE_ID, templateId);
		context.put(FacilioConstants.Workflow.ACTION_TEMPLATE, template);				
		Command templateUpdateAction = FacilioChainFactory.getAddTemplateOfWorkflowRuleAction();
		templateUpdateAction.execute(context);
		
//		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);				
		return SUCCESS;
	}
	
	private long templateId = -1;
	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	private List<WorkflowRuleContext> workFlowNotifications;
	
	public List<WorkflowRuleContext> getWorkFlowNotifications() {
		return workFlowNotifications;
	}
	public void setWorkFlowNotifications(List<WorkflowRuleContext> workFlowNotifications) {
		this.workFlowNotifications = workFlowNotifications;
	}
	
	public String getWONotificationRules() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_TYPE, RuleType.valueOf(ruleType));
		
		FacilioChain workflowRuleType = FacilioChainFactory.getWorkflowRuleOfTypeChain();
		workflowRuleType.execute(context);
		workFlowNotifications = (List<WorkflowRuleContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST);
		return SUCCESS;
	}
	
	
	private int ruleType;
	public int getRuleType() {
		return ruleType;
	}

	public void setRuleType(int ruleType) {
		this.ruleType = ruleType;
	}

	private long workflowId = -1;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	
	private int rowsUpdated;
	public int getRowsUpdated() {
		return rowsUpdated;
	}
	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}
}
