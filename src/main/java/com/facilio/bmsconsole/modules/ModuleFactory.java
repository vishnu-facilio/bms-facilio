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
		moduleMap.put("jsontemplate", getJSONTemplateModule());
		moduleMap.put("preventivemaintenance", getPreventiveMaintenancetModule());
		moduleMap.put("connectedapp", getConnectedAppModule());
		moduleMap.put("tabwidget", getTabWidgetModule());
		moduleMap.put(FacilioConstants.ContextNames.SPACE_CATEGORY, getSpaceCategoryModule());
		moduleMap.put("singledaybusinesshour", getSingleDayBusinessHourModule());
		moduleMap.put("businesshours", getBusinessHoursModule());
		moduleMap.put("files", getFilesModule());
		return moduleMap;
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
	
	public static FacilioModule getSpaceCategoryReadingRelModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("spacecategoryreading");
		fieldModule.setDisplayName("Space Readings");
		fieldModule.setTableName("Space_Category_Readings");
		
		return fieldModule;
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
	
	public static FacilioModule getReadingRuleModule() {
		FacilioModule readingRuleModule = new FacilioModule();
		readingRuleModule.setName("readingrule");
		readingRuleModule.setDisplayName("Reading Rule");
		readingRuleModule.setTableName("Reading_Rule");
		readingRuleModule.setExtendModule(getWorkflowRuleModule());
		
		return readingRuleModule;
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
	
	public static FacilioModule getOrgUserModule(){
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
	
	public static FacilioModule getTemplatePlaceholder() {
		FacilioModule templatePlaceholder = new FacilioModule();
		templatePlaceholder.setName("templatePlaceholder");
		templatePlaceholder.setDisplayName("Template Placeholder");
		templatePlaceholder.setTableName("Template_placeholder");
		return templatePlaceholder;
		
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
		return jsonTemplateModule;
	}
	
	public static FacilioModule getPreventiveMaintenancetModule() {
		FacilioModule alarmModule = new FacilioModule();
		alarmModule.setName("preventivemaintenance");
		alarmModule.setDisplayName("Preventive Maintenance");
		alarmModule.setTableName("Preventive_Maintenance");
		return alarmModule;
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
	
	public static FacilioModule getFilesModule() {
		FacilioModule filesModule = new FacilioModule();
		filesModule.setName("files");
		filesModule.setDisplayName("Files");
		filesModule.setTableName("File");
		
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
	
	public static FacilioModule getPmToWoRelModule() {
		FacilioModule pmToWoRelModule = new FacilioModule();
		pmToWoRelModule.setName("pmtoworel");
		pmToWoRelModule.setDisplayName("PM To WO Rel");
		pmToWoRelModule.setTableName("PM_To_WO");
		
		return pmToWoRelModule;
	}
	public static FacilioModule getDashboardModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Dashboard");
		return dashboardWigetModule;
	}
	public static FacilioModule getWidgetModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Widget");
		return dashboardWigetModule;
	}
	public static FacilioModule getDashboardVsWidgetModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Dashboard_vs_Widget");
		return dashboardWigetModule;
	}
	public static FacilioModule getWidgetChartModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Widget_Chart");
		return dashboardWigetModule;
	}
	public static FacilioModule getWidgetListViewModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Widget_List_View");
		return dashboardWigetModule;
	}
	public static FacilioModule getWidgetStaticModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Widget_Static");
		return dashboardWigetModule;
	}
	public static FacilioModule getWidgetWebModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Widget_Web");
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
	public static FacilioModule getAlarmVsEnergyData() {
		FacilioModule alarmVsEnergyData = new FacilioModule();
		alarmVsEnergyData.setTableName("Alarm_vs_Energy_Data");
		return alarmVsEnergyData;
	}
	public static FacilioModule getPMReminderModule() {
		FacilioModule reminderModule = new FacilioModule();
		reminderModule.setName("pmreminder");
		reminderModule.setDisplayName("Preventive Maintenance Reminder");
		reminderModule.setTableName("PM_Reminders");
		
		return reminderModule;
	}
	
	public static FacilioModule getPMTriggersModule() {
		FacilioModule pmTriggers = new FacilioModule();
		pmTriggers.setName("pmtrigger");
		pmTriggers.setDisplayName("Preventive Maintenance Triggers");
		pmTriggers.setTableName("PM_Triggers");
		return pmTriggers;
	}
	
	public static FacilioModule getPMJobsModule() {
		FacilioModule pmJobs = new FacilioModule();
		pmJobs.setName("pmJobs");
		pmJobs.setDisplayName("Preventive Maintenance Jobs");
		pmJobs.setTableName("PM_Jobs");
		
		return pmJobs;
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
	public static FacilioModule getWorkflowModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Workflow");
		return dashboardWigetModule;
	}
	
	public static FacilioModule getReportScheduleInfoModule() {
		FacilioModule reportScheduleInfo = new FacilioModule();
		reportScheduleInfo.setName("reportScheduleInfo");
		reportScheduleInfo.setDisplayName("Report Schedule Info");
		reportScheduleInfo.setTableName("Report_Schedule_Info");
		return reportScheduleInfo;
	}
	
	public static FacilioModule getCalendarColorModule() {
		FacilioModule calendarColor = new FacilioModule();
		calendarColor.setName("calendarColor");
		calendarColor.setDisplayName("Calendar Color");
		calendarColor.setTableName("Calendar_Color");
		return calendarColor;
	}
	
	public static FacilioModule getBaseLineModule()  {
		FacilioModule blModule = new FacilioModule();
		blModule.setName("baseline");
		blModule.setDisplayName("Base Lines");
		blModule.setTableName("BaseLines");
		return  blModule;
	}
	
	public static FacilioModule getBaseLineReportRelModule() {
		FacilioModule relModule = new FacilioModule();
		relModule.setName("baselinereportrel");
		relModule.setDisplayName("Base Line Reports Rel");
		relModule.setTableName("Report_BaseLine_Rel");
		
		return relModule;
	}
	
	public static FacilioModule getLastReadingModule() {
		FacilioModule lastReading = new FacilioModule();
		lastReading.setName("lastReading");
		lastReading.setDisplayName("Last Reading");
		lastReading.setTableName("Last_Reading");
		return lastReading;
	}
	
	public static FacilioModule getReportVsBaseLine() {
		FacilioModule reportFieldModule = new FacilioModule();
		reportFieldModule.setTableName("Report_BaseLine_Rel");
		return reportFieldModule;
	}
	public static FacilioModule getWorkOrderTemplateModule() {
		FacilioModule workOrderTemplate = new FacilioModule();
		workOrderTemplate.setName("workordertemplate");
		workOrderTemplate.setDisplayName("Workorder Templates");
		workOrderTemplate.setTableName("Workorder_Template");
		return workOrderTemplate;
	}
	
	public static FacilioModule getTaskSectionTemplateModule() {
		FacilioModule taskSectionModule = new FacilioModule();
		taskSectionModule.setName("tasksectiontemplate");
		taskSectionModule.setDisplayName("Task Section Template");
		taskSectionModule.setTableName("Task_Section_Template");
		return taskSectionModule;
	}
 	
	public static FacilioModule getTaskTemplateModule() {
		FacilioModule taskTemplate = new FacilioModule();
		taskTemplate.setName("tasktemplate");
		taskTemplate.setDisplayName("Task Template");
		taskTemplate.setTableName("Task_Template");
		return taskTemplate;
	}
	
	public static FacilioModule getBasespaceReadingsModule() {
		FacilioModule baseSpaceReadings = new FacilioModule();
		baseSpaceReadings.setName("basespacereadings");
		baseSpaceReadings.setDisplayName("Basespace Readings");
		baseSpaceReadings.setTableName("BaseSpace_Readings");
		return baseSpaceReadings;
	}
	
	public static FacilioModule getENPIModule() {
		FacilioModule enpi = new FacilioModule();
		enpi.setName("energyperformanceindicator");
		enpi.setDisplayName("Energy Performance Indicators");
		enpi.setTableName("EnergyPerformanceIndicator");
		return enpi;
	}
}
