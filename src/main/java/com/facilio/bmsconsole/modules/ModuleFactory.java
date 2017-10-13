package com.facilio.bmsconsole.modules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.constants.FacilioConstants;

public class ModuleFactory {
	
	private static final Map<String, FacilioModule> MODULE_MAP = Collections.unmodifiableMap(initMap());
	
	public static FacilioModule getModule(String moduleName) {
		return MODULE_MAP.get(moduleName);
	}
	
	private static Map<String, FacilioModule> initMap() {
		Map<String, FacilioModule> moduleMap = new HashMap<>();
		moduleMap.put("requester", getRequesterModule());
		moduleMap.put("emailsettings", getEmailSettingModule());
		moduleMap.put("workflowevent", getWorkflowEventModule());
		moduleMap.put("workflowrule", getWorkflowRuleModule());
		moduleMap.put("action", getActionModule());
		moduleMap.put("supportemails", getSupportEmailsModule());
		moduleMap.put("notes", getNotesModule());
		moduleMap.put("workorderrequestemail", getWorkOrderRequestEMailModule());
		moduleMap.put("ticketactivity", getTicketActivityModule());
		moduleMap.put("alarmfollowers", getAlarmFollowersModule());
		moduleMap.put("templates", getTemplatesModule());
		moduleMap.put("emailtemplates", getEMailTemplatesModule());
		moduleMap.put("smstemplates", getSMSTemplatesModule());
		moduleMap.put("criteria", getCriteriaModule());
		moduleMap.put("conditions", getConditionsModule());
		moduleMap.put("views", getViewsModule());
		moduleMap.put("users", getUsersModule());
		moduleMap.put(FacilioConstants.ContextNames.TICKET_STATUS, getTicketStatusModule());
		moduleMap.put(FacilioConstants.ContextNames.TICKET_CATEGORY, getTicketCategoryModule());
		moduleMap.put(FacilioConstants.ContextNames.TICKET, getTicketsModule());
		moduleMap.put(FacilioConstants.ContextNames.WORK_ORDER, getWorkOrdersModule());
		moduleMap.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST, getWorkOrderRequestsModule());
		moduleMap.put(FacilioConstants.ContextNames.ALARM, getAlarmsModule());
		return moduleMap;
	}
			
	public static FacilioModule getRequesterModule() {
		FacilioModule requesterModule = new FacilioModule();
		requesterModule.setName("requester");
		requesterModule.setDisplayName("Requester");
		requesterModule.setTableName("Requester");
			//prevModule.setExtendModule(currentModule);
		return requesterModule;
	}
	
	public static FacilioModule getEmailSettingModule() {
		FacilioModule emailSettingModule = new FacilioModule();
		emailSettingModule.setName("emailsettings");
		emailSettingModule.setDisplayName("Email Settings");
		emailSettingModule.setTableName("EmailSettings");
		return emailSettingModule;
	}
	
	public static FacilioModule getWorkflowEventModule() {
		FacilioModule workflowEventModule = new FacilioModule();
		workflowEventModule.setName("workflowevent");
		workflowEventModule.setDisplayName("Workflow Event");
		workflowEventModule.setTableName("Workflow_Event");
		return workflowEventModule;
	}
	
	public static FacilioModule getWorkflowRuleModule() {
		FacilioModule workflowRuleModule = new FacilioModule();
		workflowRuleModule.setName("workflowrule");
		workflowRuleModule.setDisplayName("Workflow Rule");
		workflowRuleModule.setTableName("Workflow_Rule");
		return workflowRuleModule;
	}
	
	public static FacilioModule getActionModule() {
		FacilioModule actionModule = new FacilioModule();
		actionModule.setName("action");
		actionModule.setDisplayName("Action");
		actionModule.setTableName("Action");
		return actionModule;
	}
	
	public static FacilioModule getSupportEmailsModule() {
		FacilioModule supportEmailsModule = new FacilioModule();
		supportEmailsModule.setName("supportemails");
		supportEmailsModule.setDisplayName("Support Emails");
		supportEmailsModule.setTableName("SupportEmails");
		return supportEmailsModule;
	}
	
	public static FacilioModule getNotesModule() {
		FacilioModule notesModule = new FacilioModule();
		notesModule.setName("notes");
		notesModule.setDisplayName("Notes");
		notesModule.setTableName("Notes");
		return notesModule;
	}
	
	public static FacilioModule getWorkOrderRequestEMailModule() {
		FacilioModule workOrderRequestEMailModule = new FacilioModule();
		workOrderRequestEMailModule.setName("workorderrequestemail");
		workOrderRequestEMailModule.setDisplayName("WorkOrder Request EMail");
		workOrderRequestEMailModule.setTableName("WorkOrderRequest_EMail");
		return workOrderRequestEMailModule;
	}
	
	public static FacilioModule getTicketActivityModule() {
		FacilioModule ticketActivityModule = new FacilioModule();
		ticketActivityModule.setName("ticketactivity");
		ticketActivityModule.setDisplayName("Ticket Activity");
		ticketActivityModule.setTableName("Ticket_Activity");
		return ticketActivityModule;
	}
	
	public static FacilioModule getAlarmFollowersModule() {
		FacilioModule alarmFollowersModule = new FacilioModule();
		alarmFollowersModule.setName("alarmfollowers");
		alarmFollowersModule.setDisplayName("Alarm Followers");
		alarmFollowersModule.setTableName("AlarmFollowers");
		return alarmFollowersModule;
	}
	
	public static FacilioModule getTemplatesModule() {
		FacilioModule templatesModule = new FacilioModule();
		templatesModule.setName("templates");
		templatesModule.setDisplayName("Templates");
		templatesModule.setTableName("Templates");
		return templatesModule;
	}
	
	public static FacilioModule getEMailTemplatesModule() {
		FacilioModule eMailTemplatesModule = new FacilioModule();
		eMailTemplatesModule.setName("emailtemplates");
		eMailTemplatesModule.setDisplayName("EMail Templates");
		eMailTemplatesModule.setTableName("EMail_Templates");
		eMailTemplatesModule.setExtendModule(getTemplatesModule());
		return eMailTemplatesModule;
	}
	
	public static FacilioModule getSMSTemplatesModule() {
		FacilioModule smsTemplatesModule = new FacilioModule();
		smsTemplatesModule.setName("smstemplates");
		smsTemplatesModule.setDisplayName("SMS Templates");
		smsTemplatesModule.setTableName("SMS_Templates");
		smsTemplatesModule.setExtendModule(getTemplatesModule());
		return smsTemplatesModule;
	}
	
	public static FacilioModule getCriteriaModule() {
		FacilioModule criteriaModule = new FacilioModule();
		criteriaModule.setName("criteria");
		criteriaModule.setDisplayName("Criteria");
		criteriaModule.setTableName("Criteria");
		return criteriaModule;
	}
	
	public static FacilioModule getConditionsModule() {
		FacilioModule conditionModule = new FacilioModule();
		conditionModule.setName("conditions");
		conditionModule.setDisplayName("Conditions");
		conditionModule.setTableName("Conditions");
		return conditionModule;
	}
	
	public static FacilioModule getViewsModule() {
		FacilioModule viewsModule = new FacilioModule();
		viewsModule.setName("views");
		viewsModule.setDisplayName("Views");
		viewsModule.setTableName("Views");
		return viewsModule;
	}
	
	public static FacilioModule getUsersModule() {
		FacilioModule usersModule = new FacilioModule();
		usersModule.setName("users");
		usersModule.setDisplayName("Users");
		usersModule.setTableName("Users");
		return usersModule;
	}
	
	public static FacilioModule getTicketStatusModule() {
		FacilioModule ticketStatusModule = new FacilioModule();
		ticketStatusModule.setName(FacilioConstants.ContextNames.TICKET_STATUS);
		ticketStatusModule.setDisplayName("Ticket Status");
		ticketStatusModule.setTableName("TicketStatus");
		return ticketStatusModule;
	}
	
	public static FacilioModule getTicketCategoryModule() {
		FacilioModule ticketCategoryModule = new FacilioModule();
		ticketCategoryModule.setName(FacilioConstants.ContextNames.TICKET_CATEGORY);
		ticketCategoryModule.setDisplayName("Ticket Category");
		ticketCategoryModule.setTableName("TicketCategory");
		return ticketCategoryModule;
	}
	
	public static FacilioModule getTicketsModule() {
		FacilioModule ticketStatusModule = new FacilioModule();
		ticketStatusModule.setName(FacilioConstants.ContextNames.TICKET);
		ticketStatusModule.setDisplayName("Tickets");
		ticketStatusModule.setTableName("Tickets");
		return ticketStatusModule;
	}
	
	public static FacilioModule getWorkOrdersModule() {
		FacilioModule workOrdersModule = new FacilioModule();
		workOrdersModule.setName(FacilioConstants.ContextNames.WORK_ORDER);
		workOrdersModule.setDisplayName("Work Orders");
		workOrdersModule.setTableName("WorkOrders");
		workOrdersModule.setExtendModule(getTicketsModule());
		return workOrdersModule;
	}
	
	public static FacilioModule getWorkOrderRequestsModule() {
		FacilioModule workOrderRequestsModule = new FacilioModule();
		workOrderRequestsModule.setName(FacilioConstants.ContextNames.WORK_ORDER_REQUEST);
		workOrderRequestsModule.setDisplayName("Work Order Requests");
		workOrderRequestsModule.setTableName("WorkOrderRequests");
		workOrderRequestsModule.setExtendModule(getTicketsModule());
		return workOrderRequestsModule;
	}
	
	public static FacilioModule getAlarmsModule() {
		FacilioModule alarmModule = new FacilioModule();
		alarmModule.setName(FacilioConstants.ContextNames.ALARM);
		alarmModule.setDisplayName("Alarms");
		alarmModule.setTableName("Alarms");
		alarmModule.setExtendModule(getTicketsModule());
		return alarmModule;
	}
}
