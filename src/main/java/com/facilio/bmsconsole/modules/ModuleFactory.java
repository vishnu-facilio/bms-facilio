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
		moduleMap.put("field", getFieldsModule());
		moduleMap.put("lookupField", getLookupFieldsModule());
		moduleMap.put("spacecategoryreading", getSpaceCategoryReadingRelModule());
		moduleMap.put("assetcategoryreading", getAssetCategoryReadingRelModule());
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
		moduleMap.put(FacilioConstants.ContextNames.TASK, getTasksModule());
		moduleMap.put(FacilioConstants.ContextNames.ALARM, getAlarmsModule());
		moduleMap.put(FacilioConstants.ContextNames.ASSET, getAssetsModule());
		moduleMap.put("jsontemplate", getJSONTemplateModule());
		moduleMap.put("preventivemaintenance", getPreventiveMaintenanceModule());
		moduleMap.put("connectedapp", getConnectedAppModule());
		moduleMap.put("tabwidget", getTabWidgetModule());
		moduleMap.put(FacilioConstants.ContextNames.SPACE_CATEGORY, getSpaceCategoryModule());
		moduleMap.put("singledaybusinesshour", getSingleDayBusinessHourModule());
		moduleMap.put("businesshours", getBusinessHoursModule());
		moduleMap.put("files", getFilesModule());
		moduleMap.put("anomalyScheduler", getAnalyticsAnomalyModule());
		moduleMap.put("form", getFormModule());
		moduleMap.put("formFields", getFormFieldsModule());
		moduleMap.put(FacilioConstants.ContextNames.INVENTORY, getInventoryModule());
		moduleMap.put(FacilioConstants.ContextNames.INVENTORY_CATEGORY, getInventoryCategoryModule());
		moduleMap.put(FacilioConstants.ContextNames.STORE_ROOM, getStoreRoomModule());
		moduleMap.put(FacilioConstants.ContextNames.ITEM_TYPES, getItemTypesModule());
		moduleMap.put(FacilioConstants.ContextNames.ITEM_TYPES_CATEGORY, getItemCategoryModule());
		moduleMap.put(FacilioConstants.ContextNames.TOOL_TYPES, getToolTypesModule());
		moduleMap.put(FacilioConstants.ContextNames.VENDORS, getVendorsModule());
		moduleMap.put(FacilioConstants.ContextNames.ITEM, getInventryModule());
		moduleMap.put(FacilioConstants.ContextNames.ITEM_STATUS, getInventoryStatusModule());
		moduleMap.put(FacilioConstants.ContextNames.PURCHASED_ITEM, getPurchasedItemModule());
		moduleMap.put(FacilioConstants.ContextNames.TOOL, getToolModule());
		moduleMap.put(FacilioConstants.ContextNames.LOCATION, getLocationsModule());
		moduleMap.put(FacilioConstants.ContextNames.TOOL_TRANSACTIONS, getToolTransactionsModule());
		moduleMap.put(FacilioConstants.ContextNames.ITEM_TRANSACTIONS, getItemTransactionsModule());
		moduleMap.put(FacilioConstants.ContextNames.TENANT, getTenantsModule());
		moduleMap.put(FacilioConstants.ContextNames.LABOUR, getLabourModule());
	
		return moduleMap;
	}
	
	private static FacilioModule constructModule (String name, String displayName, String tableName) {
		FacilioModule module = new FacilioModule();
		module.setName(name);
		module.setDisplayName(displayName);
		module.setTableName(tableName);
		
		return module;
	}

	public static FacilioModule getFormModule() {
		FacilioModule formModule = new FacilioModule();
		formModule.setName("form");
		formModule.setDisplayName("Form");
		formModule.setTableName("Forms");
		return formModule;
	}

	public static FacilioModule getAgentdataModule() {
		FacilioModule AgentdataModul = new FacilioModule();
		AgentdataModul.setName("agentdata");
		AgentdataModul.setDisplayName("agentdata");
		AgentdataModul.setTableName("Agent_Data");
		return AgentdataModul;
	}


	public static FacilioModule getFormFieldsModule() {
		FacilioModule formFieldsModule = new FacilioModule();
		formFieldsModule.setName("formFields");
		formFieldsModule.setDisplayName("Form Fields");
		formFieldsModule.setTableName("Form_Fields");
		return formFieldsModule;
	}
	

	public static FacilioModule getFieldsModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("field");
		fieldModule.setDisplayName("Fields");
		fieldModule.setTableName("Fields");

		return fieldModule;
	}

	public static FacilioModule getLookupFieldsModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("lookupField");
		fieldModule.setDisplayName("Lookup Fields");
		fieldModule.setTableName("LookupFields");
		 fieldModule.setExtendModule(getFieldsModule());
		return fieldModule;
	}

	public static FacilioModule getNumberFieldModule() {
		FacilioModule numberModule = new FacilioModule();
		numberModule.setName("numberField");
		numberModule.setDisplayName("Number Fields");
		numberModule.setTableName("NumberFields");
		numberModule.setExtendModule(getFieldsModule());
		return numberModule;
	}

	public static FacilioModule getBooleanFieldsModule() {
		FacilioModule booleanModule = new FacilioModule();
		booleanModule.setName("booleanField");
		booleanModule.setDisplayName("Boolean Fields");
		booleanModule.setTableName("BooleanFields");
		 booleanModule.setExtendModule(getFieldsModule());
		return booleanModule;
	}

	public static FacilioModule getEnumFieldValuesModule() {
		FacilioModule enumFieldValues = new FacilioModule();
		enumFieldValues.setName("enumFieldValues");
		enumFieldValues.setDisplayName("Enum Field Values");
		enumFieldValues.setTableName("EnumFieldValues");
		return enumFieldValues;
	}
	
	public static FacilioModule getFileFieldModule() {
		FacilioModule fileModule = new FacilioModule();
		fileModule.setName("fileFields");
		fileModule.setDisplayName("File Fields");
		fileModule.setTableName("FileFields");
		fileModule.setExtendModule(getFieldsModule());
		return fileModule;
	}

	public static FacilioModule getSpaceCategoryReadingRelModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("spacecategoryreading");
		fieldModule.setDisplayName("Space Readings");
		fieldModule.setTableName("Space_Category_Readings");

		return fieldModule;
	}

	public static FacilioModule getImportTemplateModule() {
		FacilioModule importTemplateModule = new FacilioModule();
		importTemplateModule.setDisplayName("ImportTemplate");
		importTemplateModule.setName("importTemplate");
		importTemplateModule.setTableName("ImportTemplate");
		return importTemplateModule;
	}
	
	public static FacilioModule getAssetsModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName(FacilioConstants.ContextNames.ASSET);
		fieldModule.setDisplayName("Assets");
		fieldModule.setTableName("Assets");

		return fieldModule;
	}

	public static FacilioModule getAssetCategoryReadingRelModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("assetcategoryreading");
		fieldModule.setDisplayName("Asset Readings");
		fieldModule.setTableName("Asset_Category_Readings");

		return fieldModule;
	}

	public static FacilioModule getRequesterModule() {
		FacilioModule requesterModule = new FacilioModule();
		requesterModule.setName("requester");
		requesterModule.setDisplayName("Requester");
		requesterModule.setTableName("Requester");
		// prevModule.setExtendModule(currentModule);
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
	
	public static FacilioModule getWorkflowFieldChangeFieldsModule() {
		FacilioModule workflowFieldChangeFields = new FacilioModule();
		workflowFieldChangeFields.setName("workflowFieldChangeFields");
		workflowFieldChangeFields.setDisplayName("Workflow Field Change Fields");
		workflowFieldChangeFields.setTableName("Workflow_Field_Change_Fields");
		return workflowFieldChangeFields;
	}

	public static FacilioModule getReadingRuleModule() {
		FacilioModule readingRuleModule = new FacilioModule();
		readingRuleModule.setName("readingrule");
		readingRuleModule.setDisplayName("Reading Rule");
		readingRuleModule.setTableName("Reading_Rule");
		readingRuleModule.setExtendModule(getWorkflowRuleModule());

		return readingRuleModule;
	}
	public static FacilioModule getReadingAlarmRuleModule() {
		FacilioModule readingRuleModule = new FacilioModule();
		readingRuleModule.setName("readingalarmrule");
		readingRuleModule.setDisplayName("Reading Alarm Rule");
		readingRuleModule.setTableName("Reading_Alarm_Rule");
		readingRuleModule.setExtendModule(getWorkflowRuleModule());

		return readingRuleModule;
	}

	public static FacilioModule getDeviceDetailsModule() {
		FacilioModule deviceModule = new FacilioModule();
		deviceModule.setName("devicedetails");
		deviceModule.setDisplayName("Device Details");
		deviceModule.setTableName("DeviceDetails");
		return deviceModule;
	}

	public static FacilioModule getReadingRuleInclusionsExclusionsModule() {
		FacilioModule readingRuleInclusionsExclusions = new FacilioModule();
		readingRuleInclusionsExclusions.setName("readingruleInclusionsExclusions");
		readingRuleInclusionsExclusions.setDisplayName("Reading Rule Include/ Exclude list");
		readingRuleInclusionsExclusions.setTableName("Reading_Rule_Inclusions_Exclusions");
		return readingRuleInclusionsExclusions;
	}
	
	public static FacilioModule getReadingRuleAlarmMetaModule() {
		return constructModule("readingRuleAlarmMeta", "Reading Rule Alarm Meta", "Reading_Rule_Alarm_Meta");
	}

	public static FacilioModule getSLARuleModule() {
		FacilioModule slarule = new FacilioModule();
		slarule.setName("slarule");
		slarule.setDisplayName("SLA Rule");
		slarule.setTableName("SLA_Rule");
		slarule.setExtendModule(getWorkflowRuleModule());

		return slarule;
	}
	
	public static FacilioModule getScheduledActionModule() {
		FacilioModule scheduledRule = new FacilioModule();
		scheduledRule.setName("scheduledAction");
		scheduledRule.setDisplayName("Scheduled Actions");
		scheduledRule.setTableName("Scheduled_Actions");
		
		return scheduledRule;
	}
	
	public static FacilioModule getApprovalRulesModule() {
		FacilioModule approvalRules = new FacilioModule();
		approvalRules.setName("approvalRules");
		approvalRules.setDisplayName("Approval Rules");
		approvalRules.setTableName("Approval_Rules");
		approvalRules.setExtendModule(getWorkflowRuleModule());
		
		return approvalRules;
	}
	
	public static FacilioModule getApproversModule() {
		FacilioModule approvers = new FacilioModule();
		approvers.setName("approvers");
		approvers.setDisplayName("Approvers");
		approvers.setTableName("Approvers");
		
		return approvers;
	}
	
	public static FacilioModule getApproverActionsRelModule() {
		return constructModule("approverActionsRel", "Approver Actions Rel", "Approver_Actions_Rel");
	}
	
	public static FacilioModule getApprovalStepsModule() {
		return constructModule("approvalSteps", "Approval Steps", "Approval_Steps");
	}

	public static FacilioModule getWorkflowRuleActionModule() {
		FacilioModule workflowRuleModule = new FacilioModule();
		workflowRuleModule.setName("workflowruleaction");
		workflowRuleModule.setDisplayName("Workflow Rule Action");
		workflowRuleModule.setTableName("Workflow_Rule_Action");
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

	public static FacilioModule getOrgUserModule() {
		FacilioModule orgUserModule = new FacilioModule();
		orgUserModule.setName("orgusers");
		orgUserModule.setDisplayName("Org Users");
		orgUserModule.setTableName("ORG_Users");
		return orgUserModule;

	}

	public static FacilioModule getUserModule() {
		FacilioModule userModule = new FacilioModule();
		userModule.setName(FacilioConstants.ContextNames.USER);
		userModule.setDisplayName("Users");
		userModule.setTableName("Users");
		return userModule;
	}

	public static FacilioModule getAccessibleSpaceModule() {
		FacilioModule accessbileSpaceModule = new FacilioModule();
		accessbileSpaceModule.setName(FacilioConstants.ContextNames.ACCESSIBLE_SPACE);
		accessbileSpaceModule.setDisplayName("Accessible Space");
		accessbileSpaceModule.setTableName("Accessible_Space");
		return accessbileSpaceModule;
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

	public static FacilioModule getWorkflowTemplatesModule() {
		FacilioModule eMailTemplatesModule = new FacilioModule();
		eMailTemplatesModule.setName("workflowtemplates");
		eMailTemplatesModule.setDisplayName("Workflow Templates");
		eMailTemplatesModule.setTableName("Workflow_Template");
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

	public static FacilioModule getAssignmentTemplatesModule() {
		FacilioModule assignmentTemplatesModule = new FacilioModule();
		assignmentTemplatesModule.setName("assignmenttemplates");
		assignmentTemplatesModule.setDisplayName("Assignment Templates");
		assignmentTemplatesModule.setTableName("Assignment_Templates");
		assignmentTemplatesModule.setExtendModule(getTemplatesModule());
		return assignmentTemplatesModule;
	}

	public static FacilioModule getSlaTemplatesModule() {
		FacilioModule assignmentTemplatesModule = new FacilioModule();
		assignmentTemplatesModule.setName("slatemplates");
		assignmentTemplatesModule.setDisplayName("SLA Templates");
		assignmentTemplatesModule.setTableName("SLA_Templates");
		assignmentTemplatesModule.setExtendModule(getTemplatesModule());
		return assignmentTemplatesModule;
	}

	public static FacilioModule getPushNotificationTemplateModule() {
		FacilioModule pushNotificationTemplatesModule = new FacilioModule();
		pushNotificationTemplatesModule.setName("pushNotificationTemplates");
		pushNotificationTemplatesModule.setDisplayName("Push Notification Templates");
		pushNotificationTemplatesModule.setTableName("Push_Notification_Templates");
		pushNotificationTemplatesModule.setExtendModule(getTemplatesModule());
		return pushNotificationTemplatesModule;
	}

	public static FacilioModule getWebNotificationTemplateModule() {
		FacilioModule webNotificationTemplatesModule = new FacilioModule();
		webNotificationTemplatesModule.setName("webNotificationTemplates");
		webNotificationTemplatesModule.setDisplayName("Web Notification Templates");
		webNotificationTemplatesModule.setTableName("Web_Notification_Templates");
		webNotificationTemplatesModule.setExtendModule(getTemplatesModule());
		return webNotificationTemplatesModule;
	}

	public static FacilioModule getExcelTemplatesModule() {
		FacilioModule excelTemplatesModule = new FacilioModule();
		excelTemplatesModule.setName("exceltemplates");
		excelTemplatesModule.setDisplayName("Excel Templates");
		excelTemplatesModule.setTableName("Excel_Templates");
		excelTemplatesModule.setExtendModule(getTemplatesModule());
		return excelTemplatesModule;
	}

	public static FacilioModule getTenantModule() {
		FacilioModule tenantModule = new FacilioModule();
		tenantModule.setName("tenant");
		tenantModule.setDisplayName("Tenant");
		tenantModule.setTableName("Tenant");
		return tenantModule;
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

	public static FacilioField getTicketStatusIdField() {
		FacilioModule module = getTicketStatusModule();
		FacilioField idField = new FacilioField();
		idField.setName("id");
		idField.setDataType(FieldType.NUMBER);
		idField.setColumnName("ID");
		idField.setModule(module);

		return idField;
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

	public static FacilioModule getTicketTypeModule() {
		FacilioModule ticketTypeModule = new FacilioModule();
		ticketTypeModule.setName(FacilioConstants.ContextNames.TICKET_TYPE);
		ticketTypeModule.setDisplayName("Ticket Type");
		ticketTypeModule.setTableName("TicketType");
		return ticketTypeModule;
	}

	public static FacilioModule getTicketPriorityModule() {
		FacilioModule ticketTypeModule = new FacilioModule();
		ticketTypeModule.setName(FacilioConstants.ContextNames.TICKET_PRIORITY);
		ticketTypeModule.setDisplayName("Ticket Priority");
		ticketTypeModule.setTableName("TicketPriority");
		return ticketTypeModule;
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

	public static FacilioModule getTasksModule() {
		FacilioModule workOrderRequestsModule = new FacilioModule();
		workOrderRequestsModule.setName(FacilioConstants.ContextNames.TASK);
		workOrderRequestsModule.setDisplayName("Tasks");
		workOrderRequestsModule.setTableName("Tasks");
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

	public static FacilioModule getAlarmSeverityModule() {
		FacilioModule alarmSeverityModule = new FacilioModule();
		alarmSeverityModule.setName(FacilioConstants.ContextNames.ALARM_SEVERITY);
		alarmSeverityModule.setDisplayName("Alarm Severiry");
		alarmSeverityModule.setTableName("Alarm_Severity");
		return alarmSeverityModule;
	}

	public static FacilioModule getJSONTemplateModule() {
		FacilioModule jsonTemplateModule = new FacilioModule();
		jsonTemplateModule.setName("jsontemplate");
		jsonTemplateModule.setDisplayName("JSON Template");
		jsonTemplateModule.setTableName("JSON_Template");
		jsonTemplateModule.setExtendModule(getTemplatesModule());
		return jsonTemplateModule;
	}

	public static FacilioModule getPreventiveMaintenanceModule() {
		FacilioModule alarmModule = new FacilioModule();
		alarmModule.setName("preventivemaintenance");
		alarmModule.setDisplayName("Preventive Maintenance");
		alarmModule.setTableName("Preventive_Maintenance");
		alarmModule.setExtendModule(getResourceModule());
		return alarmModule;
	}

	public static FacilioModule getResourceModule() {
		FacilioModule resourceModule = new FacilioModule();
		resourceModule.setName("resource");
		resourceModule.setDisplayName("Resources");
		resourceModule.setTableName("Resources");
		return resourceModule;
	}

	public static FacilioModule getJobsModule() {
		FacilioModule alarmModule = new FacilioModule();
		alarmModule.setName("jobs");
		alarmModule.setDisplayName("Jobs");
		alarmModule.setTableName("Jobs");
		return alarmModule;
	}

	public static FacilioModule getConnectedAppModule() {
		FacilioModule connectedApp = new FacilioModule();
		connectedApp.setName("connectedapp");
		connectedApp.setDisplayName("Connected App");
		connectedApp.setTableName("Connected_App");
		return connectedApp;
	}

	public static FacilioModule getTabWidgetModule() {
		FacilioModule tabWidget = new FacilioModule();
		tabWidget.setName("tabwidget");
		tabWidget.setDisplayName("Tab Widget");
		tabWidget.setTableName("Tab_Widget");
		return tabWidget;
	}

	public static FacilioModule getSpaceCategoryModule() {
		FacilioModule ticketCategoryModule = new FacilioModule();
		ticketCategoryModule.setName(FacilioConstants.ContextNames.SPACE_CATEGORY);
		ticketCategoryModule.setDisplayName("Space Category");
		ticketCategoryModule.setTableName("Space_Category");
		return ticketCategoryModule;
	}

	public static FacilioModule getSingleDayBusinessHourModule() {
		FacilioModule singleDayBusinessHour = new FacilioModule();
		singleDayBusinessHour.setName("singledaybusinesshour");
		singleDayBusinessHour.setDisplayName("Single Day Business Hour");
		singleDayBusinessHour.setTableName("SingleDayBusinessHours");

		return singleDayBusinessHour;
	}

	public static FacilioModule getBusinessHoursModule() {
		FacilioModule businessHours = new FacilioModule();
		businessHours.setName("businesshours");
		businessHours.setDisplayName("Business Hours");
		businessHours.setTableName("BusinessHours");

		return businessHours;
	}
	
	public static FacilioModule getShiftModule() {
		FacilioModule module = new FacilioModule();
		module.setName("shift");
		module.setDisplayName("Shift");
		module.setTableName("Shift");
		return module;
	}

	public static FacilioModule getFilesModule() {
		FacilioModule filesModule = new FacilioModule();
		filesModule.setName("files");
		filesModule.setDisplayName("Files");
		filesModule.setTableName("FacilioFile");

		return filesModule;
	}

	public static FacilioModule getZoneRelModule() {
		FacilioModule zoneRelModule = new FacilioModule();
		zoneRelModule.setName("zonespacerel");
		zoneRelModule.setDisplayName("Zone Space Rel");
		zoneRelModule.setTableName("Zone_Space");

		return zoneRelModule;
	}

	public static FacilioModule getAssetCategoryModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("assetcategory");
		fieldModule.setDisplayName("Asset Category");
		fieldModule.setTableName("Asset_Categories");

		return fieldModule;
	}

	public static FacilioModule getControllerModule() {
		FacilioModule controllerModule = new FacilioModule();
		controllerModule.setName("controller");
		controllerModule.setDisplayName("Controllers");
		controllerModule.setTableName("Controller");

		return controllerModule;
	}
	
	public static FacilioModule getControllerActivityModule() {
		return constructModule("controllerActivity", "Controller Activity", "Controller_Activity");
	}
	
	public static FacilioModule getControllerActivityRecordsModule() {
		FacilioModule controllerActivityRecordsModule = constructModule("controllerActivityRecords", "Controller Activity Records", "Controller_Activity_Records");;
		controllerActivityRecordsModule.setExtendModule(getControllerActivityModule());
		return controllerActivityRecordsModule;
	}
	
	public static FacilioModule getControllerActivityWatcherModule() {
		return constructModule("controllerActivityWatcher", "Controller Activity Watcher", "Controller_Activity_Watcher");
	}
	
	public static FacilioModule getControllerBuildingRelModule() {
		FacilioModule module = new FacilioModule();
		module.setName("controllerBuildingRel");
		module.setDisplayName("Controller Building Rel");
		module.setTableName("Controller_Building_Rel");
		
		return module;
	}
	
	public static FacilioModule getPublishDataModule() {
		return constructModule("publishData", "Publish Data", "Publish_Data");
	}
	
	public static FacilioModule getPublishMessageModule() {
		return constructModule("publishMessage", "Publish Message", "Publish_Message");
	}

	public static FacilioModule getNotificationModule() {
		FacilioModule notificationModule = new FacilioModule();
		notificationModule.setName("notification");
		notificationModule.setDisplayName("Notifications");
		notificationModule.setTableName("Notification");

		return notificationModule;
	}

	public static FacilioModule getVirtualMeterRelModule() {
		FacilioModule virtualMeterRelModule = new FacilioModule();
		virtualMeterRelModule.setName("virtualmeterrelmodule");
		virtualMeterRelModule.setDisplayName("Virtual Meter Rel");
		virtualMeterRelModule.setTableName("Virtual_Energy_Meter_Rel");

		return virtualMeterRelModule;
	}

	public static FacilioModule getHistoricalVMModule() {
		FacilioModule historicalVMCalModule = new FacilioModule();
		historicalVMCalModule.setName("historicalvmcalcualtionmodule");
		historicalVMCalModule.setDisplayName("Historical VM Calcuation");
		historicalVMCalModule.setTableName("Historical_VM_Calculation");

		return historicalVMCalModule;
	}

	public static FacilioModule getDashboardFolderModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Dashboard_Folder");
		return dashboardWigetModule;
	}
	public static FacilioModule getDashboardModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Dashboard");
		return dashboardWigetModule;
	}
	public static FacilioModule getSpaceFilteredDashboardSettingsModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Space_Filtered_Dashboard_Settings");
		return dashboardWigetModule;
	}
	public static FacilioModule getWidgetModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Widget");
		return dashboardWigetModule;
	}

//	public static FacilioModule getDashboardVsWidgetModule() {
//		FacilioModule dashboardWigetModule = new FacilioModule();
//		dashboardWigetModule.setTableName("Dashboard_vs_Widget");
//		return dashboardWigetModule;
//	}

	public static FacilioModule getWidgetChartModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Widget_Chart");
		dashboardWigetModule.setExtendModule(getWidgetModule());
		return dashboardWigetModule;
	}

	public static FacilioModule getWidgetListViewModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Widget_List_View");
		dashboardWigetModule.setExtendModule(getWidgetModule());
		return dashboardWigetModule;
	}

	public static FacilioModule getWidgetStaticModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Widget_Static");
		dashboardWigetModule.setExtendModule(getWidgetModule());
		return dashboardWigetModule;
	}

	public static FacilioModule getWidgetWebModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Widget_Web");
		dashboardWigetModule.setExtendModule(getWidgetModule());
		return dashboardWigetModule;
	}

	public static FacilioModule getFormulaModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Formula");
		return dashboardWigetModule;
	}

	public static FacilioModule getReportFolder() {
		FacilioModule reportFolderModule = new FacilioModule();
		reportFolderModule.setTableName("Report_Folder");
		return reportFolderModule;
	}

	public static FacilioModule getReport() {
		FacilioModule reportModule = new FacilioModule();
		reportModule.setTableName("Report");
		return reportModule;
	}

	public static FacilioModule getReportField() {
		FacilioModule reportFieldModule = new FacilioModule();
		reportFieldModule.setTableName("Report_Field");
		return reportFieldModule;
	}

	public static FacilioModule getReportDateFilter() {
		FacilioModule reportFieldModule = new FacilioModule();
		reportFieldModule.setTableName("Report_DateFilter");
		return reportFieldModule;
	}

	public static FacilioModule getReportEnergyMeter() {
		FacilioModule reportFieldModule = new FacilioModule();
		reportFieldModule.setTableName("Report_EnergyMeter");
		return reportFieldModule;
	}

	public static FacilioModule getReportCriteria() {
		FacilioModule reportCriteriaModule = new FacilioModule();
		reportCriteriaModule.setTableName("Report_Criteria");
		return reportCriteriaModule;
	}

	public static FacilioModule getReportFormulaField() {
		FacilioModule reportFormulaFieldModule = new FacilioModule();
		reportFormulaFieldModule.setTableName("Report_Formula_Field");
		return reportFormulaFieldModule;
	}

	public static FacilioModule getReportThreshold() {
		FacilioModule reportThresholdModule = new FacilioModule();
		reportThresholdModule.setTableName("Report_Threshold");
		return reportThresholdModule;
	}

	public static FacilioModule getReportUserFilter() {
		FacilioModule reportThresholdModule = new FacilioModule();
		reportThresholdModule.setTableName("Report_User_Filter");
		return reportThresholdModule;
	}

	public static FacilioModule getPMReminderModule() {
		FacilioModule reminderModule = new FacilioModule();
		reminderModule.setName("pmreminder");
		reminderModule.setDisplayName("Preventive Maintenance Reminder");
		reminderModule.setTableName("PM_Reminders");

		return reminderModule;
	}
	public static FacilioModule getPMReminderActionModule() {
		FacilioModule reminderModule = new FacilioModule();
		reminderModule.setName("pmreminderaction ");
		reminderModule.setDisplayName("Preventive Maintenance Reminder Action");
		reminderModule.setTableName("PM_Reminder_Action");

		return reminderModule;
	}

	public static FacilioModule getPMTriggersModule() {
		FacilioModule pmTriggers = new FacilioModule();
		pmTriggers.setName("pmtrigger");
		pmTriggers.setDisplayName("Preventive Maintenance Triggers");
		pmTriggers.setTableName("PM_Triggers");
		return pmTriggers;
	}
	
	public static FacilioModule getPMResourcePlannerModule() {
		FacilioModule pmTriggers = new FacilioModule();
		pmTriggers.setName("PMResourcePlanner");
		pmTriggers.setDisplayName("PM Resource Planner");
		pmTriggers.setTableName("PM_Resource_Planner");
		return pmTriggers;
	}

	public static FacilioModule getPMResourcePlannerTriggersModule() {
		FacilioModule pmTriggers = new FacilioModule();
		pmTriggers.setName("PMResourcePlannerTriggers");
		pmTriggers.setDisplayName("PM Resource Planner Triggers");
		pmTriggers.setTableName("PM_Resource_Planner_Triggers");
		return pmTriggers;
	}
	
	public static FacilioModule getPMResourcePlannerReminderModule() {
		FacilioModule pmTriggers = new FacilioModule();
		pmTriggers.setName("PMResourcePlannerReminder");
		pmTriggers.setDisplayName("PM Resource Planner Reminder");
		pmTriggers.setTableName("PM_Resource_Planner_Reminder");
		return pmTriggers;
	}

	public static FacilioModule getPMJobsModule() {
		FacilioModule pmJobs = new FacilioModule();
		pmJobs.setName("pmJobs");
		pmJobs.setDisplayName("Preventive Maintenance Jobs");
		pmJobs.setTableName("PM_Jobs");

		return pmJobs;
	}

	public static FacilioModule getPMResourceScheduleRuleRelModule() {
		FacilioModule module = new FacilioModule();
		module.setName("pmResourceScheduleRuleRel");
		module.setDisplayName("PM Resource Schedule Rule Rel");
		module.setTableName("PM_Resource_Schedule_Rule_Rel");

		return module;
	}

	public static FacilioModule getBeforePMRemindersTriggerRelModule() {
		FacilioModule beforePMRemindersTriggerRel = new FacilioModule();
		beforePMRemindersTriggerRel.setName("beforepmreminderstriggerrel");
		beforePMRemindersTriggerRel.setDisplayName("Before PM Reminders Trigger Rel");
		beforePMRemindersTriggerRel.setTableName("Before_PM_Reminder_Trigger_Rel");

		return beforePMRemindersTriggerRel;
	}

	public static FacilioModule getAfterPMRemindersWORelModule() {
		FacilioModule pmReminderJobWORel = new FacilioModule();
		pmReminderJobWORel.setName("afterpmreminderworel");
		pmReminderJobWORel.setDisplayName("After PM Reminder WO Rel");
		pmReminderJobWORel.setTableName("After_PM_Reminder_WO_Rel");
		return pmReminderJobWORel;
	}

	public static FacilioModule getViewColumnsModule() {
		FacilioModule viewFieldsModule = new FacilioModule();
		viewFieldsModule.setTableName("View_Column");
		return viewFieldsModule;
	}

	public static FacilioModule getViewSortColumnsModule() {
		FacilioModule viewSortColumnsModule = new FacilioModule();
		viewSortColumnsModule.setTableName("View_Sort_Columns");
		return viewSortColumnsModule;
	}

	public static FacilioModule getTaskInputOptionModule() {
		FacilioModule taskInputOptionsModule = new FacilioModule();
		taskInputOptionsModule.setName("taskInputOpyion");
		taskInputOptionsModule.setDisplayName("Task Input Options");
		taskInputOptionsModule.setTableName("Task_Input_Options");
		return taskInputOptionsModule;
	}

	public static FacilioModule getTaskSectionModule() {
		FacilioModule taskSection = new FacilioModule();
		taskSection.setName("tasksection");
		taskSection.setDisplayName("Task Section");
		taskSection.setTableName("Task_Section");
		return taskSection;
	}

	public static FacilioModule getAlarmEntityModule() {
		FacilioModule alarmentity = new FacilioModule();
		alarmentity.setName("alarmentity");
		alarmentity.setDisplayName("Alarm Entity");
		alarmentity.setTableName("Alarm_Entity");
		return alarmentity;
	}

	public static FacilioModule getReportEntityModule() {
		FacilioModule alarmentity = new FacilioModule();
		alarmentity.setName("reportentity");
		alarmentity.setDisplayName("Report Entity");
		alarmentity.setTableName("Report_Entity");
		return alarmentity;
	}

	public static FacilioModule getWorkflowModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Workflow");
		return dashboardWigetModule;
	}

	public static FacilioModule getWorkflowFieldModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Workflow_Field");
		return dashboardWigetModule;
	}

	public static FacilioModule getExpressionModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Expression");
		return dashboardWigetModule;
	}

	public static FacilioModule getReportScheduleInfoModule() {
		FacilioModule reportScheduleInfo = new FacilioModule();
		reportScheduleInfo.setName("reportScheduleInfo");
		reportScheduleInfo.setDisplayName("Report Schedule Info");
		reportScheduleInfo.setTableName("Report_Schedule_Info");
		return reportScheduleInfo;
	}

	public static FacilioModule getViewScheduleInfoModule() {
		FacilioModule viewScheduleInfo = new FacilioModule();
		viewScheduleInfo.setName("viewScheduleInfo");
		viewScheduleInfo.setDisplayName("View Schedule Info");
		viewScheduleInfo.setTableName("View_Schedule_Info");
		return viewScheduleInfo;
	}
	
	public static FacilioModule getReportScheduleInfo() {
		FacilioModule reportScheduleInfo = new FacilioModule();
		reportScheduleInfo.setName("reportScheduleInfo");
		reportScheduleInfo.setDisplayName("Report Schedule Info");
		reportScheduleInfo.setTableName("Report_Schedule_Info1");
		return reportScheduleInfo;
	}

	public static FacilioModule getCalendarColorModule() {
		FacilioModule calendarColor = new FacilioModule();
		calendarColor.setName("calendarColor");
		calendarColor.setDisplayName("Calendar Color");
		calendarColor.setTableName("Calendar_Color");
		return calendarColor;
	}

	public static FacilioModule getBaseLineModule() {
		FacilioModule blModule = new FacilioModule();
		blModule.setName("baseline");
		blModule.setDisplayName("Base Lines");
		blModule.setTableName("BaseLines");
		return blModule;
	}

	public static FacilioModule getBaseLineReportRelModule() {
		FacilioModule relModule = new FacilioModule();
		relModule.setName("baselinereportrel");
		relModule.setDisplayName("Base Line Reports Rel");
		relModule.setTableName("Report_BaseLine_Rel");

		return relModule;
	}

	public static FacilioModule getReadingDataMetaModule() {
		FacilioModule readingDataMeta = new FacilioModule();
		readingDataMeta.setName("readingdatameta");
		readingDataMeta.setDisplayName("Reading Data Meta");
		readingDataMeta.setTableName("Reading_Data_Meta");
		return readingDataMeta;
	}

	public static FacilioModule getWorkOrderTemplateModule() {
		FacilioModule workOrderTemplate = new FacilioModule();
		workOrderTemplate.setName("workordertemplate");
		workOrderTemplate.setDisplayName("Workorder Templates");
		workOrderTemplate.setTableName("Workorder_Template");
		workOrderTemplate.setExtendModule(getTemplatesModule());
		return workOrderTemplate;
	}

	public static FacilioModule getTaskSectionTemplateModule() {
		FacilioModule taskSectionModule = new FacilioModule();
		taskSectionModule.setName("tasksectiontemplate");
		taskSectionModule.setDisplayName("Task Section Template");
		taskSectionModule.setTableName("Task_Section_Template");
		taskSectionModule.setExtendModule(getTemplatesModule());
		return taskSectionModule;
	}
	
	public static FacilioModule getTaskSectionTemplateTriggersModule() {
		FacilioModule taskSectionTriggerModule = new FacilioModule();
		taskSectionTriggerModule.setName("tasksectiontemplatetriggers");
		taskSectionTriggerModule.setDisplayName("Task Section Template Triggers");
		taskSectionTriggerModule.setTableName("Task_Section_Template_Triggers");
		return taskSectionTriggerModule;
	}

	public static FacilioModule getTaskTemplateModule() {
		FacilioModule taskTemplate = new FacilioModule();
		taskTemplate.setName("tasktemplate");
		taskTemplate.setDisplayName("Task Template");
		taskTemplate.setTableName("Task_Template");
		taskTemplate.setExtendModule(getTemplatesModule());
		return taskTemplate;
	}
	
	public static FacilioModule getPMIncludeExcludeResourceModule() {
		FacilioModule taskTemplate = new FacilioModule();
		taskTemplate.setName("PMIncludeExcludeResource");
		taskTemplate.setDisplayName("PM Include Exclude Resource");
		taskTemplate.setTableName("PM_Include_Exclude_Resource");
		return taskTemplate;
	}

	public static FacilioModule getResourceReadingsModule() {
		FacilioModule baseSpaceReadings = new FacilioModule();
		baseSpaceReadings.setName("resourcereadings");
		baseSpaceReadings.setDisplayName("Resource Readings");
		baseSpaceReadings.setTableName("Resource_Readings");
		return baseSpaceReadings;
	}

	public static FacilioModule getFormulaFieldModule() {
		FacilioModule enpi = new FacilioModule();
		enpi.setName("formulaField");
		enpi.setDisplayName("Formula Field");
		enpi.setTableName("Formula_Field");
		return enpi;
	}

	public static FacilioModule getFormulaFieldInclusionsModule() {
		FacilioModule readingRuleInclusionsExclusions = new FacilioModule();
		readingRuleInclusionsExclusions.setName("formulaFieldInclusions");
		readingRuleInclusionsExclusions.setDisplayName("Formula Field Include list");
		readingRuleInclusionsExclusions.setTableName("Formula_Field_Inclusions");
		return readingRuleInclusionsExclusions;
	}

	public static FacilioModule getFormulaFieldResourceJobModule() {
		FacilioModule readingRuleInclusionsExclusions = new FacilioModule();
		readingRuleInclusionsExclusions.setName("formulaFieldResourceJob");
		readingRuleInclusionsExclusions.setDisplayName("Formula Field Resource Job");
		readingRuleInclusionsExclusions.setTableName("Formula_Field_Resource_Jobs");
		return readingRuleInclusionsExclusions;
	}

	public static FacilioModule getReadingRuleFlapsModule() {
		FacilioModule readingRuleFlaps = new FacilioModule();
		readingRuleFlaps.setName("readingRuleFlaps");
		readingRuleFlaps.setDisplayName("Reading Rule Flaps");
		readingRuleFlaps.setTableName("Reading_Rule_Flaps");

		return readingRuleFlaps;

	}

	public static FacilioModule getServicePortalModule() {
		FacilioModule enpi = new FacilioModule();
		enpi.setName("serviceportal");
		enpi.setDisplayName("Service Portal");
		enpi.setTableName("PortalInfo");
		return enpi;
	}

	public static FacilioModule getReportSpaceFilter() {
		FacilioModule repSpaceFilter = new FacilioModule();
		repSpaceFilter.setName("reportspacefilter");
		repSpaceFilter.setDisplayName("Report Space Filter");
		repSpaceFilter.setTableName("Report_SpaceFilter");
		return repSpaceFilter;
	}

	public static FacilioModule getMarkedReadingModule() {
		FacilioModule markedReading = new FacilioModule();
		markedReading.setName("markedReading");
		markedReading.setDisplayName("Marked Reading");
		markedReading.setTableName("Marked_Reading");
		return markedReading;
	}

	public static FacilioModule getReportColumnsModule() {
		FacilioModule reportColumns = new FacilioModule();
		reportColumns.setName("reportColumns");
		reportColumns.setDisplayName("Report Columns");
		reportColumns.setTableName("Report_Columns");
		return reportColumns;
	}

	public static FacilioModule getDashboardSharingModule() {
		FacilioModule dashboardSharing = new FacilioModule();
		dashboardSharing.setName("dashboardSharing");
		dashboardSharing.setDisplayName("Dashboard Sharing");
		dashboardSharing.setTableName("Dashboard_Sharing");
		return dashboardSharing;
	}

	public static FacilioModule getViewSharingModule() {
		FacilioModule viewSharing = new FacilioModule();
		viewSharing.setName("viewSharing");
		viewSharing.setDisplayName("View Sharing");
		viewSharing.setTableName("View_Sharing");
		return viewSharing;
	}

	public static FacilioModule getWidgetVsWorkflowModule() {
		FacilioModule dashboardSharing = new FacilioModule();
		dashboardSharing.setName("widgetVsWorkflow");
		dashboardSharing.setDisplayName("Widget Vs Workflow");
		dashboardSharing.setTableName("Widget_Vs_Workflow");
		return dashboardSharing;
	}

	public static FacilioModule getImportProcessModule() {
		FacilioModule dashboardSharing = new FacilioModule();
		dashboardSharing.setName("importProcess");
		dashboardSharing.setDisplayName("Import Process");
		dashboardSharing.setTableName("ImportProcess");
		return dashboardSharing;
	}
	
	public static FacilioModule getImportProcessLogModule() {
		FacilioModule importProcessLogModule = new FacilioModule();
		importProcessLogModule.setName("importProcessLogModule");
		importProcessLogModule.setDisplayName("Import Log Module");
		importProcessLogModule.setTableName("ImportProcessLog");
		return importProcessLogModule;
	}

	public static FacilioModule getAnalyticsAnomalyModule() {
		FacilioModule analyticsAnomalyJob = new FacilioModule();
		analyticsAnomalyJob.setName("anomalyScheduler");
		analyticsAnomalyJob.setDisplayName("Analytics Scheduler");
		analyticsAnomalyJob.setTableName("Energy_Data");

		return analyticsAnomalyJob;
	}
	
	public static FacilioModule getAnalyticsAnomalyConfigModule() {
		FacilioModule analyticsAnomalyJob = new FacilioModule();
		analyticsAnomalyJob.setName("anomalyConfig");
		analyticsAnomalyJob.setDisplayName("Analytics Config");
		analyticsAnomalyJob.setTableName("Anomaly_Asset_Config");

		return analyticsAnomalyJob;
	}


	public static FacilioModule getAnalyticsAnomalyModuleWeatherData() {
		FacilioModule analyticsAnomalyJob = new FacilioModule();
		analyticsAnomalyJob.setName("anomalyWeatherScheduler");
		analyticsAnomalyJob.setDisplayName("Analytics Scheduler");
		analyticsAnomalyJob.setTableName("Weather_Reading");

		return analyticsAnomalyJob;
	}


	public static FacilioModule getAnalyticsAnomalyIDListModule() {
		FacilioModule analyticsAnomalyIDListJob = new FacilioModule();
		analyticsAnomalyIDListJob.setName("anomalyIDList");
		analyticsAnomalyIDListJob.setDisplayName("Anomaly List");
		analyticsAnomalyIDListJob.setTableName("Time_Series_Anomaly");

		return analyticsAnomalyIDListJob;
	}
	
	public static FacilioModule getAnalyticsAnomalyS3URLModule() {
		FacilioModule analyticsAnomalyS3URL = new FacilioModule();
		analyticsAnomalyS3URL.setName("s3URLList");
		analyticsAnomalyS3URL.setDisplayName("s3 URL List");
		analyticsAnomalyS3URL.setTableName("Anomaly_S3_URL_Data");
		
		return analyticsAnomalyS3URL;
	}
	
	public static FacilioModule getDerivationsModule() {
		FacilioModule taskSection = new FacilioModule();
		taskSection.setName("derivations");
		taskSection.setDisplayName("Derivations");
		taskSection.setTableName("Derivations");
		return taskSection;
	}

	public static FacilioModule getOrgUnitsModule() {
		FacilioModule taskSection = new FacilioModule();
		taskSection.setName("orgUnits");
		taskSection.setDisplayName("Org Units");
		taskSection.setTableName("Org_Units");
		return taskSection;
	}

	public static FacilioModule getCommonJobPropsModule() {
		FacilioModule commonJobProps = new FacilioModule();
		commonJobProps.setName("commonJobProps");
		commonJobProps.setDisplayName("Common Job Props");
		commonJobProps.setTableName("Common_Job_Props");
		return commonJobProps;
	}

	public static FacilioModule getTenantsModule() {
		FacilioModule tenants = new FacilioModule();
		tenants.setName("tenant");
		tenants.setDisplayName("Tenants");
		tenants.setTableName("Tenants");
		return tenants;
	}
	
	public static FacilioModule getLabourModule() {
		FacilioModule labour = new FacilioModule();
		labour.setName("labour");
		labour.setDisplayName("Labour");
		labour.setTableName("Labour");
		return labour;
	}
	
	public static FacilioModule getTenantsuserModule() {
		FacilioModule tenants = new FacilioModule();
		tenants.setName("tenant_users");
		tenants.setDisplayName("Tenant_Users");
		tenants.setTableName("Tenant_Users");
		return tenants;
	}

	public static FacilioModule getTenantsUtilityMappingModule() {
		FacilioModule mapping = new FacilioModule();
		mapping.setName("tenantsUtilityMapping");
		mapping.setDisplayName("Tenants Utility Mapping");
		mapping.setTableName("Tenants_Utility_Mapping");
		return mapping;
	}

	public static FacilioModule getRateCardModule() {
		FacilioModule rateCard = new FacilioModule();
		rateCard.setName("rateCard");
		rateCard.setDisplayName("Rate Card");
		rateCard.setTableName("Rate_Card");
		return rateCard;
	}

	public static FacilioModule getRateCardServiceModule() {
		FacilioModule rateCardService = new FacilioModule();
		rateCardService.setName("rateCardService");
		rateCardService.setDisplayName("Rate Card Service");
		rateCardService.setTableName("Rate_Card_Services");
		return rateCardService;
	}

	public static FacilioModule getModuleLocalIdModule() {
		FacilioModule rateCardService = new FacilioModule();
		rateCardService.setName("moduleLocalId");
		rateCardService.setDisplayName("Module Local Id");
		rateCardService.setTableName("Module_Local_ID");
		return rateCardService;
	}

	public static FacilioModule getBenchmarkModule() {
		FacilioModule benchmark = new FacilioModule();
		benchmark.setName("benchmark");
		benchmark.setDisplayName("Benchmark");
		benchmark.setTableName("Benchmark");
		return benchmark;
	}

	public static FacilioModule getBenchmarkUnitModule() {
		FacilioModule benchmarkUnit = new FacilioModule();
		benchmarkUnit.setName("benchmarkUnit");
		benchmarkUnit.setDisplayName("Benchmark Units");
		benchmarkUnit.setTableName("Benchmark_Units");
		return benchmarkUnit;
	}

	public static FacilioModule getReportBenchmarkRelModule() {
		FacilioModule rateCardService = new FacilioModule();
		rateCardService.setName("reportBenchmarkRel");
		rateCardService.setDisplayName("Report Benchmark Rel");
		rateCardService.setTableName("Report_Benchmark_Rel");
		return rateCardService;
	}

	public static FacilioModule getShiftUserRelModule() {
		FacilioModule shiftUserRel = new FacilioModule();
		shiftUserRel.setName("shiftUserRel");
		shiftUserRel.setDisplayName("Shift User Rel");
		shiftUserRel.setTableName("Shift_User_Rel");
		return shiftUserRel;
	}
	
	public static FacilioModule getCostsModule() {
		FacilioModule costs = new FacilioModule();
		costs.setName("cost");
		costs.setDisplayName("Costs");
		costs.setTableName("Costs");
		return costs;
	}
	
	public static FacilioModule getCostSlabsModule() {
		FacilioModule costSlabs = new FacilioModule();
		costSlabs.setName("costSlab");
		costSlabs.setDisplayName("Cost Slabs");
		costSlabs.setTableName("Cost_Slabs");
		return costSlabs;
	}
	
	public static FacilioModule getAdditionalCostModule() {
		FacilioModule additionalCosts = new FacilioModule();
		additionalCosts.setName("additionalCost");
		additionalCosts.setDisplayName("Additional Costs");
		additionalCosts.setTableName("Additional_Costs");
		return additionalCosts;
	}
	
	public static FacilioModule getCostAssetsModule() {
		FacilioModule costAssets = new FacilioModule();
		costAssets.setName("costAssets");
		costAssets.setDisplayName("Cost Assets");
		costAssets.setTableName("Cost_Assets");
		return costAssets;
	}
	
	public static FacilioModule getReportFolderModule() {
		FacilioModule costAssets = new FacilioModule();
		costAssets.setName("reportFolder");
		costAssets.setDisplayName("Report Folder");
		costAssets.setTableName("Report1_Folder");
		return costAssets;
	}
	public static FacilioModule getReportModule() {
		FacilioModule costAssets = new FacilioModule();
		costAssets.setName("report");
		costAssets.setDisplayName("Report");
		costAssets.setTableName("Report1");
		return costAssets;
	}
	public static FacilioModule getReportFieldsModule() {
		FacilioModule costAssets = new FacilioModule();
		costAssets.setName("reportFields");
		costAssets.setDisplayName("Report Fields");
		costAssets.setTableName("Report_Fields");
		return costAssets;
	}
	
//	public static FacilioModule getReportDataPointModule() {
//		FacilioModule costAssets = new FacilioModule();
//		costAssets.setName("reportDataPoint");
//		costAssets.setDisplayName("Report Data Point");
//		costAssets.setTableName("Report_Data_Point");
//		return costAssets;
//	}
//	
//	public static FacilioModule getReportXCriteriaModule() {
//		FacilioModule costAssets = new FacilioModule();
//		costAssets.setName("ReportXCriteria");
//		costAssets.setDisplayName("Report X Criteria");
//		costAssets.setTableName("Report_X_Criteria");
//		return costAssets;
//	}
//	
//	public static FacilioModule getReportBaselineModule() {
//		FacilioModule costAssets = new FacilioModule();
//		costAssets.setName("ReportBaseLineRel");
//		costAssets.setDisplayName("Report1 BaseLine Rel");
//		costAssets.setTableName("Report1_BaseLine_Rel");
//		return costAssets;
//	}
//	public static FacilioModule getReportBenchmarkModule() {
//		FacilioModule costAssets = new FacilioModule();
//		costAssets.setName("ReportBenchmarkRel");
//		costAssets.setDisplayName("Report Benchmark Rel");
//		costAssets.setTableName("Report1_Benchmark_Rel");
//		return costAssets;
//	}
	public static FacilioModule getOfflineSyncErrorModule() {
		FacilioModule syncModule = new FacilioModule();
		syncModule.setName("offlinesyncerror");
		syncModule.setDisplayName("Offline Sync Error");
		syncModule.setTableName("Sync_Errors");
		return syncModule;
	}
	
	public static FacilioModule getScreenModule() {
		FacilioModule syncModule = new FacilioModule();
		syncModule.setName("screen");
		syncModule.setDisplayName("Screen");
		syncModule.setTableName("Screen");
		return syncModule;
	}
	
	public static FacilioModule getScreenDashboardRelModule() {
		FacilioModule syncModule = new FacilioModule();
		syncModule.setName("screenDashboardRel");
		syncModule.setDisplayName("Screen Dashboard Rel");
		syncModule.setTableName("Screen_Dashboard_Rel");
		return syncModule;
	}
	
	public static FacilioModule getRemoteScreenModule() {
		FacilioModule syncModule = new FacilioModule();
		syncModule.setName("remoteScreens");
		syncModule.setDisplayName("Remote Screens");
		syncModule.setTableName("Remote_Screens");
		return syncModule;
	}
	
	public static FacilioModule getTVPasscodeModule() {
		FacilioModule syncModule = new FacilioModule();
		syncModule.setName("tvpasscode");
		syncModule.setDisplayName("TV Passcode");
		syncModule.setTableName("TVPasscodes");
		return syncModule;
	}
	
	public static FacilioModule getUnmodeledInstancesModule() {
		FacilioModule syncModule = new FacilioModule();
		syncModule.setName("unmodeledInstance");
		syncModule.setDisplayName("Unmodeled Instance");
		syncModule.setTableName("Unmodeled_Instance");
		return syncModule;
	}
	
	public static FacilioModule getUnmodeledDataModule() {
		FacilioModule syncModule = new FacilioModule();
		syncModule.setName("unmodeledData");
		syncModule.setDisplayName("Unmodeled Data");
		syncModule.setTableName("Unmodeled_Data");
		return syncModule;
	}
	
	public static FacilioModule getInstanceMappingModule() {
		FacilioModule syncModule = new FacilioModule();
		syncModule.setName("instanceToAssetMapping");
		syncModule.setDisplayName("Instance To Asset Mapping");
		syncModule.setTableName("Instance_To_Asset_Mapping");
		return syncModule;
	}
	
	public static FacilioModule getReadingInputValuesModule() {
		FacilioModule enumFieldValues = new FacilioModule();
		enumFieldValues.setName("readingInputValues");
		enumFieldValues.setDisplayName("Reading Input Values");
		enumFieldValues.setTableName("Reading_Input_Values");
		return enumFieldValues;
	}
	
	public static FacilioModule getInventoryModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.INVENTORY);
		inventoryModule.setDisplayName("Inventory");
		inventoryModule.setTableName("Inventory");
		return inventoryModule;
	}
	
	public static FacilioModule getInventoryCategoryModule() {
		FacilioModule ticketCategoryModule = new FacilioModule();
		ticketCategoryModule.setName(FacilioConstants.ContextNames.INVENTORY_CATEGORY);
		ticketCategoryModule.setDisplayName("Inventory Category");
		ticketCategoryModule.setTableName("Inventory_category");
		return ticketCategoryModule;
	}
	
	public static FacilioModule getInventoryVendorsModule() {
		FacilioModule ticketCategoryModule = new FacilioModule();
		ticketCategoryModule.setName(FacilioConstants.ContextNames.INVENTORY_VENDOR);
		ticketCategoryModule.setDisplayName("Inventory Vendor");
		ticketCategoryModule.setTableName("Inventory_vendors");
		return ticketCategoryModule;
	}
	public static FacilioModule getAnalyticsV1AnomalyConfigModule() {
		FacilioModule analyticsAnomalyJob = new FacilioModule();
		analyticsAnomalyJob.setName("anomalyConfigV1");
		analyticsAnomalyJob.setDisplayName("Analytics ConfigV1");
		analyticsAnomalyJob.setTableName("Anomaly_Asset_Config_v1");

		return analyticsAnomalyJob;
	}

	public static FacilioModule getAssetTreeHeirarchyModule() {
		FacilioModule assetHeirarchy = new FacilioModule();
		assetHeirarchy.setName("assetTreeHeirarchyV1");
		assetHeirarchy.setDisplayName("AssetTreeHeirarchy V1");
		assetHeirarchy.setTableName("Asset_Tree_Heirarchy");
		return assetHeirarchy;
	}

	public static FacilioModule getMlForecastingLifetimeModule() {
		FacilioModule mlForecastingModule = new FacilioModule();
		mlForecastingModule.setName("mlforecastinglifetime");
		mlForecastingModule.setDisplayName("Ml Forecasting Lifetime");
		mlForecastingModule.setTableName("Ml_Forecasting_Lifetime");
		return mlForecastingModule;
	}

	public static FacilioModule getMlForecastingModule() {
		FacilioModule mlForecastingModule = new FacilioModule();
		mlForecastingModule.setName("mlforecasting");
		mlForecastingModule.setDisplayName("Ml Forecasting");
		mlForecastingModule.setTableName("Ml_Forecasting");
		return mlForecastingModule;
	}
	
	public static FacilioModule getMlForecastingFieldsModule() {
		FacilioModule mlForecastingFieldModule = new FacilioModule();
		mlForecastingFieldModule.setName("mlforecastingfields");
		mlForecastingFieldModule.setDisplayName("Ml Forecasting Fields");
		mlForecastingFieldModule.setTableName("Ml_Forecasting_Fields");
		return mlForecastingFieldModule;
	}
	
	public static FacilioModule getStoreRoomModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.STORE_ROOM);
		inventoryModule.setDisplayName("Store Room");
		inventoryModule.setTableName("Store_room");
		return inventoryModule;
	}
	
	public static FacilioModule getItemTypesModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.ITEM_TYPES);
		inventoryModule.setDisplayName("itemTypes");
		inventoryModule.setTableName("Item_Types");
		return inventoryModule;
	}
	
	public static FacilioModule getToolTypesModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.TOOL_TYPES);
		inventoryModule.setDisplayName("Tool Types");
		inventoryModule.setTableName("Tool_types");
		return inventoryModule;
	}
	
	public static FacilioModule getVendorsModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.VENDORS);
		inventoryModule.setDisplayName("Vendors");
		inventoryModule.setTableName("Vendors");
		return inventoryModule;
	}
	
	public static FacilioModule getInventryModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.ITEM);
		inventoryModule.setDisplayName("Item");
		inventoryModule.setTableName("Item");
		return inventoryModule;
	}
	
	public static FacilioModule getInventoryStatusModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.ITEM_STATUS);
		inventoryModule.setDisplayName("Inventory Status");
		inventoryModule.setTableName("Inventory_status");
		return inventoryModule;
	}
	
	public static FacilioModule getPurchasedItemModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.PURCHASED_ITEM);
		inventoryModule.setDisplayName("Purchased Items");
		inventoryModule.setTableName("Purchased_Item");
		return inventoryModule;
	}
	
	public static FacilioModule getToolModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.TOOL);
		inventoryModule.setDisplayName("Tool");
		inventoryModule.setTableName("Tools");
		return inventoryModule;
	}

	public static FacilioModule getLocationsModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.LOCATION);
		inventoryModule.setDisplayName("Locations");
		inventoryModule.setTableName("Locations");
		return inventoryModule;
	}

  	public static FacilioModule getWeatherStationModule() {
		FacilioModule weatherStation = new FacilioModule();
		weatherStation.setName("weatherStations");
		weatherStation.setDisplayName("Weather Stations");
		weatherStation.setTableName("Weather_Stations");

		return weatherStation;
	}
  	
  	public static FacilioModule getNotificationLoggerModule() {
  		FacilioModule notificationLogger = new FacilioModule();
  		notificationLogger.setName("notificationLogger");
  		notificationLogger.setDisplayName("Notification Logger");
  		notificationLogger.setTableName("Notification_Logger");
  		
  		return notificationLogger;
	  }

  	public static FacilioModule getItemStatusModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.ITEM_TYPES_STATUS);
		inventoryModule.setDisplayName("itemStatus");
		inventoryModule.setTableName("Item_status");
		return inventoryModule;
	}

  	public static FacilioModule getItemTypeStatusModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.ITEM_TYPES_STATUS);
		inventoryModule.setDisplayName("itemTypeStatus");
		inventoryModule.setTableName("Item_Types_status");
		return inventoryModule;
	}


  	public static FacilioModule getItemCategoryModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.ITEM_TYPES_CATEGORY);
		inventoryModule.setDisplayName("toolTypesCategory");
		inventoryModule.setTableName("Tool_types_category");
		return inventoryModule;
	}

  	public static FacilioModule getToolTypeStatusModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.TOOL_TYPES_STATUS);
		inventoryModule.setDisplayName("toolTypeStatus");
		inventoryModule.setTableName("Tool_types_status");
		return inventoryModule;
	}
  	
  	public static FacilioModule getItemTransactionsModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		inventoryModule.setDisplayName("itemTransactions");
		inventoryModule.setTableName("Item_Transactions");
		return inventoryModule;
	}
  	
  	public static FacilioModule getToolTransactionsModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
		inventoryModule.setDisplayName("toolTransactions");
		inventoryModule.setTableName("Tool_transactions");
		return inventoryModule;
	}
}