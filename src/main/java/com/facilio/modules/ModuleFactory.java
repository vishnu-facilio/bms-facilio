package com.facilio.modules;

import com.facilio.agent.AgentKeys;
import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.constants.FacilioConstants.ModuleNames;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
//		moduleMap.put("workflowevent", getWorkflowEventModule());
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
		moduleMap.put(FacilioConstants.ContextNames.ENERGY_METER, getEnergyMeterModule());
		moduleMap.put(FacilioConstants.ContextNames.TENANT, getTenantModule());
		moduleMap.put("jsontemplate", getJSONTemplateModule());
		moduleMap.put("preventivemaintenance", getPreventiveMaintenanceModule());
		moduleMap.put("connectedApps", getConnectedAppsModule());
		moduleMap.put("tabwidget", getTabWidgetModule());
		moduleMap.put(FacilioConstants.ContextNames.SPACE_CATEGORY, getSpaceCategoryModule());
		moduleMap.put("singledaybusinesshour", getSingleDayBusinessHourModule());
		moduleMap.put("businesshours", getBusinessHoursModule());
		moduleMap.put("anomalyScheduler", getAnalyticsAnomalyModule());
		moduleMap.put("form", getFormModule());
		moduleMap.put("formFields", getFormFieldsModule());
		moduleMap.put(FacilioConstants.ContextNames.INVENTORY, getInventoryModule());
		moduleMap.put(FacilioConstants.ContextNames.INVENTORY_CATEGORY, getInventoryCategoryModule());
		moduleMap.put(FacilioConstants.ContextNames.STORE_ROOM, getStoreRoomModule());
		moduleMap.put(FacilioConstants.ContextNames.ITEM_TYPES, getItemTypesModule());
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
		moduleMap.put(FacilioConstants.ContextNames.PURCHASE_ORDER, getPurchaseOrderModule());
		moduleMap.put(FacilioConstants.ContextNames.PURCHASE_REQUEST, getPurchaseRequestModule());
		moduleMap.put(FacilioConstants.ContextNames.RECEIPT, getReceiptModule());
		moduleMap.put(FacilioConstants.ContextNames.CONTRACTS, getContractsModule());
		moduleMap.put(FacilioConstants.ContextNames.PURCHASE_CONTRACTS, getPurchaseContractModule());
		moduleMap.put(FacilioConstants.ContextNames.LABOUR_CONTRACTS, getLabourContractModule());
		moduleMap.put(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS, getRentalLeaseContractModule());
		moduleMap.put(FacilioConstants.ContextNames.WARRANTY_CONTRACTS, getWarrantyContractModule());
		moduleMap.put(FacilioConstants.ContextNames.INVENTORY_REQUEST, getInventoryRequestModule());
		moduleMap.put(FacilioConstants.ContextNames.SHIPMENT, getShipmentModule());
		moduleMap.put(FacilioConstants.ContextNames.ATTENDANCE, getAttendanceModule());
		moduleMap.put(ContextNames.ASSET_SPARE_PARTS,getAssetSparePartsModule());
		moduleMap.put(FacilioConstants.ContextNames.ATTENDANCE_TRANSACTIONS, getAttendanceTransactionModule());
		moduleMap.put(FacilioConstants.ContextNames.GRAPHICS, getGraphicsModule());
		moduleMap.put(FacilioConstants.ContextNames.GRAPHICS_FOLDER, getGraphicsFolderModule());
		moduleMap.put(FacilioConstants.ContextNames.SERVICE, getServiceModule());
		moduleMap.put(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS, getTermsAndConditionModule());
		moduleMap.put(FacilioConstants.ContextNames.CONTRACTS, getContractsModule());
		moduleMap.put(FacilioConstants.ContextNames.NEW_READING_ALARM, getReadingAlarmModule());
		moduleMap.put(FacilioConstants.ContextNames.Reservation.RESERVATION, getReservationModule());
		moduleMap.put(FacilioConstants.ContextNames.BMS_ALARM, getBmsAlarmModule());
		moduleMap.put(FacilioConstants.ModuleNames.DEVICES,getDevicesModule());
		moduleMap.put(FacilioConstants.ModuleNames.PRINTERS,getPrinterModule());
		moduleMap.put(FacilioConstants.ContextNames.VISITOR,getVisitorModule());
		moduleMap.put(FacilioConstants.ContextNames.VISITOR_LOGGING,getVisitorLoggingModule());
		moduleMap.put(FacilioConstants.ContextNames.VISITOR_INVITE,getVisitorInviteModule());
		moduleMap.put(FacilioConstants.ContextNames.INSURANCE, getInsuranceModule());
		moduleMap.put(ContextNames.VISITOR_LOGGING, getVisitorLogModule());
		moduleMap.put(ContextNames.VISITOR_LOG, getVisitorLogModule());
		moduleMap.put(ContextNames.WATCHLIST, getWatchListModule());
        moduleMap.put(ContextNames.CONTACT, getContactModule());
		moduleMap.put(ContextNames.WorkPermit.WORKPERMIT, getWorkPermitModule());
		moduleMap.put(ContextNames.OCCUPANT, getOccupantModule());
		moduleMap.put(ContextNames.SERVICE_REQUEST, getServiceRequestModule());
		moduleMap.put(ContextNames.VENDOR_DOCUMENTS, getVendorDocumentsModule());
		moduleMap.put(ContextNames.SAFETY_PLAN, getSafetyPlanModule());
		moduleMap.put(ContextNames.HAZARD, getHazardModule());
		moduleMap.put(ContextNames.PRECAUTION, getPrecautionModule());
		moduleMap.put(ContextNames.CLIENT, getClientModule());
		moduleMap.put(ContextNames.SITE, getSiteModule());
		moduleMap.put(ContextNames.BUILDING, getBuildingModule());
		moduleMap.put(ContextNames.FLOOR, getFloorModule());
		moduleMap.put(ContextNames.BASE_SPACE, getBaseSpaceModule());
		moduleMap.put(ContextNames.TENANT_CONTACT, getTenantContactModule());
		moduleMap.put(ContextNames.PEOPLE, getPeopleModule());
		moduleMap.put(ContextNames.VENDOR_CONTACT, getVendorContactModule());
		moduleMap.put(ContextNames.CLIENT_CONTACT, getClientContactModule());
		moduleMap.put(FacilioConstants.ContextNames.OPERATION_ALARM, getOperationAlarmsModule());
		moduleMap.put(ContextNames.SPACE, getSpaceModule());
		moduleMap.put(ContextNames.TENANT_UNIT_SPACE, getTenantUnitSpaceModule());
		moduleMap.put(ContextNames.EMPLOYEE, getEmployeeModule());
		moduleMap.put(ContextNames.QUOTE, getQuotationModule());
		moduleMap.put(ContextNames.Tenant.NEWS_AND_INFORMATION, getNewsAndInformationModule());
		moduleMap.put(ContextNames.Tenant.DEALS_AND_OFFERS, getDealsAndOffersModule());
		moduleMap.put(ContextNames.Tenant.NEIGHBOURHOOD, getNeighbourhoodModule());
		moduleMap.put(ContextNames.Tenant.ANNOUNCEMENT, getAnnouncementModule());
		moduleMap.put(ContextNames.Tenant.AUDIENCE, getAudienceModule());
		moduleMap.put(ContextNames.Tenant.ADMIN_DOCUMENTS, getAdminDocumentsModule());
		moduleMap.put(ContextNames.Tenant.CONTACT_DIRECTORY, getContactDirectoryModule());
		moduleMap.put(ContextNames.Budget.BUDGET, getBudgetModule());
		moduleMap.put(ContextNames.FacilityBooking.FACILITY_BOOKING, getFacilityBookingModule());
		moduleMap.put(ContextNames.FacilityBooking.FACILITY, getFacilityModule());
		moduleMap.put(ContextNames.TRANSFER_REQUEST, getTransferRequestModule());
		moduleMap.put(ContextNames.TRANSFER_REQUEST_SHIPMENT, getTransferRequestShipmentModule());
		moduleMap.put(ContextNames.NEW_READING_RULE_MODULE, getNewReadingRuleModule());
		moduleMap.put(ContextNames.REQUEST_FOR_QUOTATION, getRequestForQuotationModule());
		moduleMap.put(ContextNames.TRANSACTION, getTransactionModule());
		moduleMap.put(ContextNames.VENDOR_QUOTES, getVendorQuotesModule());
		moduleMap.put(ContextNames.WO_PLANNED_ITEMS, getWorkOrderPlannedItemsModule());
		moduleMap.put(ContextNames.WO_PLANNED_TOOLS, getWorkOrderPlannedToolsModule());
		moduleMap.put(ContextNames.WO_PLANNED_SERVICES, getWorkOrderPlannedServicesModule());
		moduleMap.put(ContextNames.WORKORDER_ITEMS,getWorkOrderItemsModule());
		moduleMap.put(ContextNames.WORKORDER_TOOLS,getWorkOrderToolsModule());
		moduleMap.put(ContextNames.INVENTORY_RESERVATION,getInventoryReservationModule());
		moduleMap.put(ContextNames.WO_SERVICE,getWorkOrderServiceModule());
		moduleMap.put("customPageWidget", getCustomPageWidgetModule());
		moduleMap.put("customWidgetGroup", getSummaryWidgetGroupModule());
		moduleMap.put("customWidgetGroupFields", getSummaryWidgetGroupFieldsModule());
		moduleMap.put("currency", getCurrencyModule());

		moduleMap.put(ContextNames.SpaceCategoryFormRelation.SPACE_CATEGORY_FORM_RELATION,getSpaceBookingFormRelationModule());
		moduleMap.put(ContextNames.FORM_SHARING, getFormSharingModule());
		moduleMap.put(ContextNames.GLIMPSE, getGlimpseModule());
		moduleMap.put(ContextNames.MODULE_CONFIGURATION, getModuleConfigurationModule());
		moduleMap.put(ContextNames.GLIMPSE_FIELDS, getGlimpseFieldsModule());

		return moduleMap;
	}
	
	private static FacilioModule constructModule (String name, String displayName, String tableName) {
		FacilioModule module = new FacilioModule();
		module.setName(name);
		module.setDisplayName(displayName);
		module.setTableName(tableName);
		
		return module;
	}
	
	private static FacilioModule constructModule (String name, String displayName, String tableName,FacilioModule extendedModule) {
		FacilioModule module = new FacilioModule();
		module.setName(name);
		module.setDisplayName(displayName);
		module.setTableName(tableName);
		module.setExtendModule(extendedModule);
		
		return module;
	}

	public static FacilioModule getFormModule() {
		FacilioModule formModule = new FacilioModule();
		formModule.setName("form");
		formModule.setDisplayName("Form");
		formModule.setTableName("Forms");
		return formModule;
	}
	public static FacilioModule getFormSharingModule() {
		FacilioModule formSharing = new FacilioModule();
		formSharing.setName("formSharing");
		formSharing.setDisplayName("Form Sharing");
		formSharing.setTableName("Form_Sharing");
		return formSharing;
	}

	public static FacilioModule getGlimpseModule() {

		FacilioModule glimpse = new FacilioModule();
		glimpse.setName("glimpse");
		glimpse.setDisplayName("Glimpse");
		glimpse.setTableName("Glimpse");
		return glimpse;
	}

	public static FacilioModule getModuleConfigurationModule() {
		FacilioModule moduleConfiguration = new FacilioModule();
		moduleConfiguration.setName("moduleConfiguration");
		moduleConfiguration.setDisplayName("Module Configuration");
		moduleConfiguration.setTableName("Module_Configuration");
		return moduleConfiguration;
	}

	public static FacilioModule getGlimpseFieldsModule(){

		FacilioModule glimpseFields = new FacilioModule();
		glimpseFields.setName("glimpseFields");
		glimpseFields.setDisplayName("Glimpse Fields");
		glimpseFields.setTableName("Glimpse_Fields");
		return glimpseFields;
	}

	public static FacilioModule getReadingToolsModule() {
		FacilioModule formModule = new FacilioModule();
		formModule.setName("readingTools");
		formModule.setDisplayName("ReadingTools");
		formModule.setTableName("Reading_Tools");
		return formModule;
	}
	
	public static FacilioModule getFormRuleModule() {
		FacilioModule formModule = new FacilioModule();
		formModule.setName("formRule");
		formModule.setDisplayName("Form Rule");
		formModule.setTableName("Form_Rule");
		return formModule;
	}
	
	public static FacilioModule getFormRuleTriggerFieldModule() {
		FacilioModule formModule = new FacilioModule();
		formModule.setName("formRuleTriggerField");
		formModule.setDisplayName("Form Rule Trigger Field");
		formModule.setTableName("Form_Rule_Trigger_Field");
		return formModule;
	}
	
	public static FacilioModule getFormRuleActionModule() {
		FacilioModule formModule = new FacilioModule();
		formModule.setName("formRuleAction");
		formModule.setDisplayName("Form Rule Action");
		formModule.setTableName("Form_Rule_Action");
		return formModule;
	}

	public static FacilioModule getFormRuleActionFieldModule() {
		FacilioModule formModule = new FacilioModule();
		formModule.setName("formRuleActionField");
		formModule.setDisplayName("Form Rule Action Field");
		formModule.setTableName("Form_Rule_Action_Field");
		return formModule;
	}

	public static FacilioModule getAgentDataModule() {
		FacilioModule agentDataModule = new FacilioModule();
		agentDataModule.setName("agentData");
		agentDataModule.setDisplayName("agentData");
		agentDataModule.setTableName(AgentKeys.AGENT_TABLE);
		return agentDataModule;
	}

	public static FacilioModule getNewAgentModule() {
		FacilioModule agentDataModule = new FacilioModule();
		agentDataModule.setName("agent");
		agentDataModule.setDisplayName("agent");
		agentDataModule.setTableName(AgentConstants.AGENT_TABLE);
		return agentDataModule;
	}
	
	public static FacilioModule getCloudAgentDomainModule() {
		FacilioModule agentDataModule = new FacilioModule();
		agentDataModule.setName("cloudAgentDomain");
		agentDataModule.setDisplayName("Cloud Agent Domain");
		agentDataModule.setTableName("Cloud_Agent_Domain");
		return agentDataModule;
	}

	public static FacilioModule getAgentMetricsModule() {
		FacilioModule agentMetricsModule = new FacilioModule();
		agentMetricsModule.setName("addAgentMetrics");
        agentMetricsModule.setDisplayName("addAgentMetrics");
        agentMetricsModule.setTableName(AgentKeys.METRICS_TABLE);
		return agentMetricsModule;
	}

	public static FacilioModule getControllerReadingsModule() {
		FacilioModule controllerReadingsModule = new FacilioModule();
		controllerReadingsModule.setName("controllerReadings");
		controllerReadingsModule.setDisplayName("controller Readings");
		controllerReadingsModule.setTableName("Controller_Readings");
		
		return controllerReadingsModule;
	}

	public static FacilioModule getAgentLogModule() {
		FacilioModule agentLogModule = new FacilioModule();
		agentLogModule.setName("agentLog");
		agentLogModule.setDisplayName("agentLog");
		agentLogModule.setTableName(AgentKeys.AGENT_LOG_TABLE);
		return agentLogModule;
	}

	public static FacilioModule	getAgentMessageModule(){
		FacilioModule agentMessageModule = new FacilioModule();
		agentMessageModule.setName("agentMessage");
		agentMessageModule.setDisplayName("agentMessage");
		agentMessageModule.setTableName(AgentKeys.AGENT_MESSAGE_TABLE);
		return agentMessageModule;
	}

	public static FacilioModule getAgentVersionModule(){
		FacilioModule agentVersionModule = new FacilioModule();
		agentVersionModule.setDisplayName("agentVersion");
		agentVersionModule.setDisplayName("agentVersion");
		agentVersionModule.setTableName("Agent_Version");
		return agentVersionModule;
	}

	public static FacilioModule getFormFieldsModule() {
		FacilioModule formFieldsModule = new FacilioModule();
		formFieldsModule.setName("formFields");
		formFieldsModule.setDisplayName("Form Fields");
		formFieldsModule.setTableName("Form_Fields");
		return formFieldsModule;
	}

	public static FacilioModule getFormSectionModule() {
		FacilioModule formFieldsModule = new FacilioModule();
		formFieldsModule.setName("formSection");
		formFieldsModule.setDisplayName("Form Section");
		formFieldsModule.setTableName("Form_Section");
		return formFieldsModule;
	}

	public static FacilioModule getModuleModule() {
		FacilioModule module = new FacilioModule();
		module.setName("module");
		module.setDisplayName("Module");
		module.setTableName("Modules");
		return module;
	}
	
	public static FacilioModule getSubModulesRelModule() {
		FacilioModule module = new FacilioModule();
		module.setName("submodulesrel");
		module.setDisplayName("Sub Modules Rel");
		module.setTableName("SubModulesRel");
		return module;
	}

	public static FacilioModule getFieldsModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("field");
		fieldModule.setDisplayName("Fields");
		fieldModule.setTableName("Fields");

		return fieldModule;
	}
	
	public static FacilioModule getRollUpFieldsModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("rollUpField");
		fieldModule.setDisplayName("RollUp Fields");
		fieldModule.setTableName("RollUpFields");
		 fieldModule.setExtendModule(getFieldsModule());
		return fieldModule;
	}
	
	public static FacilioModule getSensorRuleModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("sensorRule");
		fieldModule.setDisplayName("Sensor Rule");
		fieldModule.setTableName("SensorRule");
		return fieldModule;
	}
	
	public static FacilioModule getSensorRulePropsModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("sensorRuleProps");
		fieldModule.setDisplayName("Sensor Rule Props");
		fieldModule.setTableName("SensorRuleProps");
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

	public static FacilioModule getLineItemFieldsModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("lineItemField");
		fieldModule.setDisplayName("Line Item Fields");
		fieldModule.setTableName("LineItemFields");
		fieldModule.setExtendModule(getFieldsModule());
		return fieldModule;
	}

	public static FacilioModule getLargeTextFieldsModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("largeTextField");
		fieldModule.setDisplayName("Large Text Field");
		fieldModule.setTableName("LargeTextFields");
		fieldModule.setExtendModule(getFieldsModule());
		return fieldModule;
	}

	public static FacilioModule getUrlFieldsModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("urlField");
		fieldModule.setDisplayName("URL Field");
		fieldModule.setTableName("UrlFields");
		fieldModule.setExtendModule(getFieldsModule());
		return fieldModule;
	}

	public static FacilioModule getCurrencyFieldsModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("currencyField");
		fieldModule.setDisplayName("Currency Field");
		fieldModule.setTableName("CurrencyFields");
		fieldModule.setExtendModule(getFieldsModule());
		return fieldModule;
	}

	public static FacilioModule getMultiLookupFieldsModule() {
		FacilioModule multiLookup = new FacilioModule();
		multiLookup.setName("multiLookupField");
		multiLookup.setDisplayName("Multi Lookup Fields");
		multiLookup.setTableName("MultiLookupFields");
		multiLookup.setExtendModule(getFieldsModule());
		return multiLookup;
	}

	public static FacilioModule getMultiEnumFieldsModule() {
		FacilioModule multiLookup = new FacilioModule();
		multiLookup.setName("multiEnumField");
		multiLookup.setDisplayName("Multi Enum Fields");
		multiLookup.setTableName("MultiEnumFields");
		multiLookup.setExtendModule(getFieldsModule());
		return multiLookup;
	}

	public static FacilioModule getNumberFieldModule() {
		FacilioModule numberModule = new FacilioModule();
		numberModule.setName("numberField");
		numberModule.setDisplayName("Number Fields");
		numberModule.setTableName("NumberFields");
		numberModule.setExtendModule(getFieldsModule());
		return numberModule;
	}


	public static FacilioModule getDateFieldModule() {
		FacilioModule dateModule = new FacilioModule();
		dateModule.setName("dateField");
		dateModule.setDisplayName("Date Fields");
		dateModule.setTableName("DateFields");
		dateModule.setExtendModule(getFieldsModule());
		return dateModule;
	}
	
	public static FacilioModule getStringFieldModule() {
		FacilioModule stringModule = new FacilioModule();
		stringModule.setName("stringField");
		stringModule.setDisplayName("String Fields");
		stringModule.setTableName("StringFields");
		stringModule.setExtendModule(getFieldsModule());
		return stringModule;
	}



	public static FacilioModule getDateFieldChildModule () {

		FacilioModule dateModule = new FacilioModule ();
		dateModule.setName ("dateFieldChild");
		dateModule.setDisplayName ("Date Field Child");
		dateModule.setTableName ("DateFieldChild");
		dateModule.setExtendModule (getDateFieldModule ());
		return dateModule;
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

	public static FacilioModule getScoreFieldModule() {
		FacilioModule fileModule = new FacilioModule();
		fileModule.setName("scoreFields");
		fileModule.setDisplayName("Score Fields");
		fileModule.setTableName("ScoreFields");
		fileModule.setExtendModule(getFieldsModule());
		return fileModule;
	}

	public static FacilioModule getSystemEnumFieldModule() {
		FacilioModule systemModule = new FacilioModule();
		systemModule.setName("systemEnumFields");
		systemModule.setDisplayName("System Enum Fields");
		systemModule.setTableName("SystemEnumFields");
		systemModule.setExtendModule(getFieldsModule());

		return systemModule;
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
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.ASSET);
		module.setDisplayName("Assets");
		module.setTableName("Assets");
		return module;
	}

	public static FacilioModule getAssetDepreciationRelModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.ASSET_DEPRECIATION_REL);
		module.setDisplayName("Asset Depreciation Rel");
		module.setTableName("Asset_Depreciation_Rel");
		return module;
	}

	public static FacilioModule getAssetDepreciationCalculationModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.ASSET_DEPRECIATION_CALCULATION);
		module.setDisplayName("Asset Depreciation Calculation");
		module.setTableName("Asset_Depreciation_Calculation");
		module.setType(FacilioModule.ModuleType.SUB_ENTITY);
		return module;
	}
	
	public static FacilioModule getEnergyMeterModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName(FacilioConstants.ContextNames.ENERGY_METER);
		fieldModule.setDisplayName("Energy Meter");
		fieldModule.setTableName("Energy_Meter");

		return fieldModule;
	}
	
	public static FacilioModule getRelationshipModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("relationship");
		fieldModule.setDisplayName("Relationship");
		fieldModule.setTableName("Relationship");
		return fieldModule;
	}
	public static FacilioModule getRelatedAssetsModule() {
		FacilioModule fieldModule = new FacilioModule();
		fieldModule.setName("relatedAssets");
		fieldModule.setDisplayName("Related Assets");
		fieldModule.setTableName("Related_Assets");
		return fieldModule;
	}

	public static FacilioModule getClassificationAppliedModules() {
		FacilioModule module = new FacilioModule();
		module.setName("classificationAppliedModule");
		module.setDisplayName("Classification Applied Module");
		module.setTableName("Classification_Applied_Module");
		return module;
	}

	public static FacilioModule getClassificationAttributeModule() {
		FacilioModule module = new FacilioModule();
		module.setName("classificationAttribute");
		module.setDisplayName("Classification Attribute");
		module.setTableName("Classification_Attribute");
		return module;
	}
	public static FacilioModule getClassificationAttributeRelModule(){
		FacilioModule module = new FacilioModule();
		module.setName("classificationAttributeRel");
		module.setDisplayName("Classification Attribute Rel");
		module.setTableName("Classification_Attribute_Rel");
		return module;
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

	public static FacilioModule getWorkflowRuleModule() {
		FacilioModule workflowRuleModule = new FacilioModule();
		workflowRuleModule.setName("workflowrule");
		workflowRuleModule.setDisplayName("Workflow Rule");
		workflowRuleModule.setTableName("Workflow_Rule");
		return workflowRuleModule;
	}

	public static FacilioModule getWorkflowRuleRCAMapping() {
		FacilioModule workflowRuleModule = new FacilioModule();
		workflowRuleModule.setName("workflowruleRCAMapping");
		workflowRuleModule.setDisplayName("Workflow Rule RCA Mapping");
		workflowRuleModule.setTableName("Workflow_RCA_Mapping");
		return workflowRuleModule;
	}

	public static FacilioModule getReadingRuleRCAMapping() {
		FacilioModule workflowRuleModule = new FacilioModule();
		workflowRuleModule.setName("reading_rule_rca_mapping");
		workflowRuleModule.setDisplayName("Reading Rule RCA Mapping");
		workflowRuleModule.setTableName("ReadingRule_RCA_Mapping");
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

	public static FacilioModule getNewReadingRuleModule() {
		FacilioModule readingRuleModule = new FacilioModule();
		readingRuleModule.setName("newreadingrule");
		readingRuleModule.setDisplayName("New Reading Rule");
		readingRuleModule.setTableName("New_Reading_Rule");
		return readingRuleModule;
	}

	public static FacilioModule getFlowModule(){
		FacilioModule flowModule = new FacilioModule();
		flowModule.setName("flow");
		flowModule.setDisplayName("Flow");
		flowModule.setTableName("Flows");
		return flowModule;
	}

	public static FacilioModule getFlowTransitionModule(){
		FacilioModule flowTransitionModule = new FacilioModule();
		flowTransitionModule.setName("flowTransition");
		flowTransitionModule.setDisplayName("Flow Transition");
		flowTransitionModule.setTableName("FlowTransition");
		return flowTransitionModule;
	}

	public static FacilioModule getFlowParameters(){
		FacilioModule flowParameterModule = new FacilioModule();
		flowParameterModule.setName("parameter");
		flowParameterModule.setDisplayName("Parameter");
		flowParameterModule.setTableName("Parameter");
		return flowParameterModule;
	}

	public static FacilioModule getReadingRuleMetricModule() {
		FacilioModule readingRuleModule = new FacilioModule();
		readingRuleModule.setName("readingruleMetrics");
		readingRuleModule.setDisplayName("Reading Rule Metrics");
		readingRuleModule.setTableName("Reading_Rule_Metrics");

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

	public static FacilioModule getImportDataDetailsModule(){
		FacilioModule importDataDetails = new FacilioModule();
		importDataDetails.setName("importDataDetails");
		importDataDetails.setDisplayName("Import Data Details");
		importDataDetails.setTableName("ImportDataDetails");
		return importDataDetails;
	}

	public static FacilioModule getImportFileModule(){
		FacilioModule importFile = new FacilioModule();
		importFile.setName("importFile");
		importFile.setDisplayName("Import File");
		importFile.setTableName("ImportFile");
		return importFile;
	}

	public static FacilioModule getImportFileSheetsModule(){
		FacilioModule importFileSheets = new FacilioModule();
		importFileSheets.setName("importFileSheets");
		importFileSheets.setDisplayName("Import File Sheets");
		importFileSheets.setTableName("ImportFileSheets");
		return importFileSheets;
	}
	public static FacilioModule getImportSheetFieldMappingModule(){
		FacilioModule importFileSheets = new FacilioModule();
		importFileSheets.setName("importSheetFieldMapping");
		importFileSheets.setDisplayName("Import Sheet Field Mapping");
		importFileSheets.setTableName("ImportSheetFieldMapping");
		return importFileSheets;
	}
	public static FacilioModule getMultiImportProcessLogModule(){
		FacilioModule multiImportProcessLog = new FacilioModule();
		multiImportProcessLog.setName("multiImportProcessLog");
		multiImportProcessLog.setDisplayName("MultiImport Process Log");
		multiImportProcessLog.setTableName("MultiImportProcessLog");
		return multiImportProcessLog;
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

	public static FacilioModule getSLAEntityModule() {
		FacilioModule module = new FacilioModule();
		module.setName("slaEntity");
		module.setDisplayName("SLA Entity");
		module.setTableName("SLA_Entity");

		return module;
	}

//	public static FacilioModule getSLAPolicyRuleModule() {
//		FacilioModule module = new FacilioModule();
//		module.setName("slaPolicyRule");
//		module.setDisplayName("SLA Policy Rule");
//		module.setTableName("SLA_Policy");
//
//		return module;
//	}

//	public static FacilioModule getSLAWorkflowRuleModule() {
//		FacilioModule module = new FacilioModule();
//		module.setName("slaWorkflowRule");
//		module.setDisplayName("SLA Workflow Rule");
//		module.setTableName("SLA_Workflow_Rule");
//
//		return module;
//	}

	public static FacilioModule getSLACommitmentDurationModule() {
		FacilioModule module = new FacilioModule();
		module.setName("slaCommitmentDuration");
		module.setDisplayName("SLA Commitment Duration");
		module.setTableName("SLA_Workflow_Commitment_Duration");

		return module;
	}

	public static FacilioModule getSLAWorkflowEscalationModule() {
		FacilioModule module = new FacilioModule();
		module.setName("slaWorkflowEscalation");
		module.setDisplayName("SLA Workflow Escalation");
		module.setTableName("SLA_Workflow_Escalation");

		return module;
	}

	public static FacilioModule getSLAEscalationWorkflowRuleRelModule() {
		FacilioModule module = new FacilioModule();
		module.setName("slaWorkflowRuleRel");
		module.setDisplayName("SLA WorkflowRule Rel");
		module.setTableName("SLA_WorkflowRule_Rel");

		return module;
	}

	public static FacilioModule getSLAEditJobDetailsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("slaEditJobDetails");
		module.setDisplayName("SLA Edit Job Details");
		module.setTableName("SLA_Edit_Job_Details");

		return module;
	}

	public static FacilioModule getSLARuleModule() {
		FacilioModule slarule = new FacilioModule();
		slarule.setName("slarule");
		slarule.setDisplayName("SLA Rule");
		slarule.setTableName("SLA_Rule");
		slarule.setExtendModule(getWorkflowRuleModule());

		return slarule;
	}

	public static FacilioModule getScoringRuleModule() {
		FacilioModule module = new FacilioModule();
		module.setName("scoringRuleModule");
		module.setDisplayName("Scoring Rule Module");
		module.setTableName("Scoring_Rule");
		module.setExtendModule(getWorkflowRuleModule());

		return module;
	}

	public static FacilioModule getScoringRuleTriggerCallRelModule() {
		FacilioModule module = new FacilioModule();
		module.setName("scoringRuleTriggerCallRel");
		module.setDisplayName("Scoring Rule Trigger Call Rel");
		module.setTableName("ScoringRule_Trigger_Rel");
		return module;
	}

	public static FacilioModule getScoringCommitmentModule() {
		FacilioModule module = new FacilioModule();
		module.setName("scoringCommitment");
		module.setDisplayName("Scoring Commitment");
		module.setTableName("Scoring_Commitment");
		return module;
	}

	public static FacilioModule getBaseScoringModule() {
		FacilioModule module = new FacilioModule();
		module.setName("baseScoringModule");
		module.setDisplayName("Base Scoring Module");
		module.setTableName("Base_Scoring");

		return module;
	}

	public static FacilioModule getConditionScoringModule() {
		FacilioModule module = new FacilioModule();
		module.setName("conditionScoringModule");
		module.setDisplayName("Condition Scoring Module");
		module.setTableName("Condition_Scoring");
		module.setExtendModule(getBaseScoringModule());

		return module;
	}

	public static FacilioModule getNodeScoringModule() {
		FacilioModule module = new FacilioModule();
		module.setName("nodeScoringModule");
		module.setDisplayName("Node Scoring Module");
		module.setTableName("Node_Scoring");
		module.setExtendModule(getBaseScoringModule());

		return module;
	}

	public static FacilioModule getActualScoreModule() {
		FacilioModule module = new FacilioModule();
		module.setName("actualScoreModule");
		module.setDisplayName("Actual Score");
		module.setTableName("Actual_Score");

		return module;
	}

	public static FacilioModule getBaseAlarmImpactModule() {
		FacilioModule module = new FacilioModule();
		module.setName("baseAlarmImpactModule");
		module.setDisplayName("Base Alarm Impact");
		module.setTableName("Base_Alarm_Impact");

		return module;
	}

	public static FacilioModule getConstantAlarmImpactModule() {
		FacilioModule module = new FacilioModule();
		module.setExtendModule(getBaseAlarmImpactModule());
		module.setName("constantAlarmImpactModule");
		module.setDisplayName("Constant Alarm Impact");
		module.setTableName("Constant_Alarm_Impact");

		return module;
	}
	
	public static FacilioModule getScheduledActionModule() {
		FacilioModule scheduledRule = new FacilioModule();
		scheduledRule.setName("scheduledAction");
		scheduledRule.setDisplayName("Scheduled Actions");
		scheduledRule.setTableName("Scheduled_Actions");
		
		return scheduledRule;
	}

	public static FacilioModule getScheduledWorkflowModule() {
		FacilioModule scheduledRule = new FacilioModule();
		scheduledRule.setName("ScheduledWorkflow");
		scheduledRule.setDisplayName("Scheduled Workflow");
		scheduledRule.setTableName("Scheduled_Workflow");

		return scheduledRule;
	}
	
	public static FacilioModule getScheduledRuleJobsMetaModule() {
		FacilioModule scheduledRule = new FacilioModule();
		scheduledRule.setName("ScheduledRuleJobsMeta");
		scheduledRule.setDisplayName("Scheduled Rule Jobs Meta");
		scheduledRule.setTableName("Scheduled_Rule_Jobs_Meta");
		return scheduledRule;
	}

	public static FacilioModule getSchedulerActionRelModule() {
		FacilioModule module = new FacilioModule();
		module.setName("ScheduledWorkflowActionRel");
		module.setDisplayName("ScheduledWorkflow Action Rel");
		module.setTableName("Scheduled_Workflow_Action_Rel");
		return module;
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


	public static FacilioModule getRuleTemplatesRelModule() {
		return constructModule("ruleTemplatesRel", "Rule Templates Rel", "Rule_Templates_Rel");
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
	
	public static FacilioModule getFormSiteRelationModule() {
		FacilioModule notesModule = new FacilioModule();
		notesModule.setName("formsiterelation");
		notesModule.setDisplayName("Form Site Relation");
		notesModule.setTableName("Form_Site_Relation");
		return notesModule;
	}

	public static FacilioModule getAlarmFollowersModule() {
		FacilioModule alarmFollowersModule = new FacilioModule();
		alarmFollowersModule.setName("alarmfollowers");
		alarmFollowersModule.setDisplayName("Alarm Followers");
		alarmFollowersModule.setTableName("AlarmFollowers");
		return alarmFollowersModule;
	}
	
	public static FacilioModule getEnergyStarCustomerModule() {
		return constructModule("energyStarCustomer", "Energy Star Customer", "Energy_Star_Customer");
	}
	public static FacilioModule getEnergyStarPropertyModule() {
		return constructModule("energyStarProperty", "Energy Star Property", "Energy_Star_Property");
	}
	public static FacilioModule getEnergyStarPropertyUseModule() {
		return constructModule("energyStarPropertyUse", "Energy Star Property Use", "Energy_Star_Property_Use");
	}
	public static FacilioModule getEnergyStarMeterModule() {
		return constructModule("energyStarMeter", "Energy Star Meter", "Energy_Star_Meter");
	}
	
	public static FacilioModule getEnergyStarMeterPointModule() {
		return constructModule("energyStarMeterPoint", "Energy Star Meter Point", "Energy_Star_Meter_Point");
	}

	public static FacilioModule getPMJobPlanModule() {
		return constructModule("pmjobplan", "pm job plan", "PM_Job_Plan");
	}
	public static FacilioModule getPMJobPlanTriggerModule() {
		return constructModule("PMJobPlanTriggers", "PM Job Plan Triggers", "PM_Job_Plan_Triggers");
	}
	
	public static FacilioModule getCBModelModule() {
		return constructModule("cbmodel", "Chat Bot Model", "CB_Model");
	}
	
	public static FacilioModule getCBModelVersionModule() {
		return constructModule("cbmodelversion", "Chat Bot Model Version", "CB_Model_Versions");
	}
	
	public static FacilioModule getCBIntentModule() {
		return constructModule("cbintent", "Chat Bot Intent", "CB_Intent");
	}
	
	public static FacilioModule getCBIntentChildModule() {
		return constructModule("cbintentchild", "Chat Bot Intent Child", "CB_Intent_Child");
	}

	public static FacilioModule getCBIntentActionModule() {
		return constructModule("cbintentaction", "Chat Bot Intent Action", "CB_Intent_Action");
	}
	
	public static FacilioModule getCBIntentInvokeSamplesModule() {
		return constructModule("cbintentinvokesamples", "Chat Bot Intent Invoke Samples", "CB_Intent_Invoke_Sample");
	}
	
	public static FacilioModule getCBIntentParamModule() {
		return constructModule("cbintentparam", "Chat Intent Param", "CB_Intent_Params");
	}
	
	public static FacilioModule getCBSessionModule() {
		return constructModule("cbsession", "Chat Bot Session", "CB_Session");
	}
	
	public static FacilioModule getCBSessionConversationModule() {
		return constructModule("cbsessionconversation", "Chat Bot Session Conversation", "CB_Session_Conversation");
	}
	
	public static FacilioModule getCBSessionParamsModule() {
		return constructModule("cbsessionparams", "Chat Bot Session Params", "CB_Session_Params");
	}

	public static FacilioModule getRelationModule() {
		return constructModule("relation", "Relation", "Relation");
	}

	public static FacilioModule getRelationMappingModule() {
		return constructModule("relationMapping", "Relation Mapping", "Relation_Mapping");
	}

	public static FacilioModule getTemplatesModule() {
		FacilioModule templatesModule = new FacilioModule();
		templatesModule.setName("templates");
		templatesModule.setDisplayName("Templates");
		templatesModule.setTableName("Templates");
		return templatesModule;
	}
	
	public static FacilioModule getTemplateFileModule() {
		FacilioModule templatesModule = new FacilioModule();
		templatesModule.setName("templatefileattachment");
		templatesModule.setDisplayName("Template File Attachment");
		templatesModule.setTableName("Template_File_Attachment");
		return templatesModule;
	}
	
	public static FacilioModule getTemplateUrlAttachmentModule() {
		FacilioModule templatesModule = new FacilioModule();
		templatesModule.setName("templateurlattachment");
		templatesModule.setDisplayName("Template Url Attachment");
		templatesModule.setTableName("Template_Url_Attachment");
		return templatesModule;
	}
	
	public static FacilioModule getTemplateFileFieldAttachmentModule() {
		FacilioModule templatesModule = new FacilioModule();
		templatesModule.setName("templatefilefieldattachment");
		templatesModule.setDisplayName("Template File Field Attachment");
		templatesModule.setTableName("Template_FileField_Attachment");
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

	public static FacilioModule getEMailStructureModule() {
		FacilioModule eMailTemplatesModule = new FacilioModule();
		eMailTemplatesModule.setName("emailstructure");
		eMailTemplatesModule.setDisplayName("EMail Structure");
		eMailTemplatesModule.setTableName("EMail_Structure");
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
	
	public static FacilioModule getCallTemplatesModule() {
		FacilioModule smsTemplatesModule = new FacilioModule();
		smsTemplatesModule.setName("calltemplates");
		smsTemplatesModule.setDisplayName("CAll Templates");
		smsTemplatesModule.setTableName("Call_Template");
		smsTemplatesModule.setExtendModule(getTemplatesModule());
		return smsTemplatesModule;
	}
	
	public static FacilioModule getWhatsappMessageTemplatesModule() {
		FacilioModule smsTemplatesModule = new FacilioModule();
		smsTemplatesModule.setName("whatsappMessageTemplatesModule");
		smsTemplatesModule.setDisplayName("WhatsappMessage Templates");
		smsTemplatesModule.setTableName("Whatsapp_Message_Template");
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
	
	public static FacilioModule getTenantSpacesModule() {
		FacilioModule tenantSpaceModule = new FacilioModule();
		tenantSpaceModule.setName("tenantspaces");
		tenantSpaceModule.setDisplayName("Tenant Spaces");
		tenantSpaceModule.setTableName("Tenant_spaces");
		return tenantSpaceModule;
	}

	public static FacilioModule getCriteriaModule() {
		FacilioModule criteriaModule = new FacilioModule();
		criteriaModule.setName("criteria");
		criteriaModule.setDisplayName("Criteria");
		criteriaModule.setTableName("Criteria");
		return criteriaModule;
	}

	public static FacilioModule getModuleAppPermissionModule() {
		FacilioModule criteriaModule = new FacilioModule();
		criteriaModule.setName("moduleAppPermission");
		criteriaModule.setDisplayName("Module App Permission");
		criteriaModule.setTableName("ModuleAppPermission");
		return criteriaModule;
	}

	public static FacilioModule getModuleAppPermissionChildModule() {
		FacilioModule criteriaModule = new FacilioModule();
		criteriaModule.setName("moduleAppPermissionChild");
		criteriaModule.setDisplayName("Module App Permission Child");
		criteriaModule.setTableName("ModuleAppPermissionChild");
		return criteriaModule;
	}

	public static FacilioModule getConditionsModule() {
		FacilioModule conditionModule = new FacilioModule();
		conditionModule.setName("conditions");
		conditionModule.setDisplayName("Conditions");
		conditionModule.setTableName("Conditions");
		return conditionModule;
	}
	
	public static FacilioModule getViewGroupsModule() {
		FacilioModule viewsModule = new FacilioModule();
		viewsModule.setName("viewGroups");
		viewsModule.setDisplayName("View Groups");
		viewsModule.setTableName("View_Groups");
		return viewsModule;
	}

	public static FacilioModule getNamedCriteriaModule() {
		FacilioModule module = new FacilioModule();
		module.setName("namedCriteria");
		module.setDisplayName("Named Criteria");
		module.setTableName("Named_Criteria");
		return module;
	}

	public static FacilioModule getNamedConditionModule() {
		FacilioModule module = new FacilioModule();
		module.setName("namedCondition");
		module.setDisplayName("Named Condition");
		module.setTableName("Named_Condition");
		return module;
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

	public static FacilioModule getTimelineViewModule() {
		FacilioModule module = new FacilioModule();
		module.setName("timelineView");
		module.setDisplayName("Timeline View");
		module.setTableName("TimelineView");
		module.setExtendModule(getViewsModule());
		return module;
	}

	/*
	public static FacilioModule getFieldDisplayPatternModule() {
		FacilioModule module = new FacilioModule();
		module.setName("fieldDisplayPattern");
		module.setDisplayName("Field Display Pattern");
		module.setTableName("FieldDisplayPattern");
		return module;
	}
	 */

	public static FacilioModule getWeekendsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("weekends");
		module.setDisplayName("Weekends");
		module.setTableName("Weekends");
		return module;
	}

	public static FacilioModule getRecordCustomizationModule() {
		FacilioModule module = new FacilioModule();
		module.setName("recordCustomization");
		module.setDisplayName("Record Customization");
		module.setTableName("RecordCustomization");
		return module;
	}

	public static FacilioModule getRecordCustomizationValuesModule() {
		FacilioModule module = new FacilioModule();
		module.setName("recordCustomizationValues");
		module.setDisplayName("Record Customization Values");
		module.setTableName("RecordCustomizationValues");
		return module;
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

	public static FacilioModule getAssetBreakdownModule() {
		FacilioModule alarmModule = new FacilioModule();
		alarmModule.setName(ContextNames.ASSET_BREAKDOWN);
		alarmModule.setDisplayName("Asset Breakdown");
		alarmModule.setTableName("Asset_Breakdown");
		return alarmModule;
	}
	
	public static FacilioModule getAlarmOccurenceModule() {
		FacilioModule alarmModule = new FacilioModule();
		alarmModule.setName(FacilioConstants.ContextNames.ALARM_OCCURRENCE);
		alarmModule.setDisplayName("Alarm Occurrences");
		alarmModule.setTableName("AlarmOccurrence");
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

	public static FacilioModule getPMSites() {
		FacilioModule pmSites = new FacilioModule();
		pmSites.setName("pmsites");
		pmSites.setDisplayName("PM Sites");
		pmSites.setTableName("PM_Sites");
		return pmSites;
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

	public static FacilioModule getConnectedAppsModule() {
		FacilioModule connectedApp = new FacilioModule();
		connectedApp.setName("connectedApps");
		connectedApp.setDisplayName("Connected Apps");
		connectedApp.setTableName("ConnectedApps");
		return connectedApp;
	}

	public static FacilioModule getConnectedAppSAMLModule() {
		FacilioModule connectedApp = new FacilioModule();
		connectedApp.setName("connectedAppSAML");
		connectedApp.setDisplayName("Connected Apps SAML");
		connectedApp.setTableName("ConnectedApps_SAML");
		return connectedApp;
	}

	public static FacilioModule getConnectedAppWidgetsModule() {
		FacilioModule connectedApp = new FacilioModule();
		connectedApp.setName("connectedAppWidgets");
		connectedApp.setDisplayName("Connected App Widgets");
		connectedApp.setTableName("ConnectedApp_Widgets");
		return connectedApp;
	}

	public static FacilioModule getVariablesModule() {
		FacilioModule connectedApp = new FacilioModule();
		connectedApp.setName("variables");
		connectedApp.setDisplayName("Variables");
		connectedApp.setTableName("Variables");
		return connectedApp;
	}

	public static FacilioModule getConnectedAppConnectorsModule() {
		FacilioModule connectedApp = new FacilioModule();
		connectedApp.setName("connectedAppConnectors");
		connectedApp.setDisplayName("Connected App Connectors");
		connectedApp.setTableName("ConnectedApp_Connectors");
		return connectedApp;
	}

	public static FacilioModule getConnectionModule() {
		FacilioModule connectedApp = new FacilioModule();
		connectedApp.setName("connection");
		connectedApp.setDisplayName("Connection");
		connectedApp.setTableName("Connection");
		return connectedApp;
	}
	public static FacilioModule getConnectionParamsModule() {
		FacilioModule connectedApp = new FacilioModule();
		connectedApp.setName("connectionParams");
		connectedApp.setDisplayName("Connection Params");
		connectedApp.setTableName("Connection_Params");
		return connectedApp;
	}
	public static FacilioModule getIntegrationApiModule() {
		FacilioModule integrationApi = new FacilioModule();
		integrationApi.setName("integrationapi");
		integrationApi.setDisplayName("IntegrationApi");
		integrationApi.setTableName("Integration_Api");
		return integrationApi;
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
		module.setType(ModuleType.BASE_ENTITY);
		module.setName("shift");
		module.setDisplayName("Shift");
		module.setTableName("Shift");
		return module;
	}

	public static FacilioModule getBreakModule() {
		FacilioModule module = new FacilioModule();
		module.setName("break");
		module.setDisplayName("Break");
		module.setTableName("Break");
		return module;
	}

	public static FacilioModule getFilesModule(String tableName) {
		FacilioModule filesModule = new FacilioModule();
		filesModule.setName("files");
		filesModule.setDisplayName("Files");
		filesModule.setTableName(tableName);

		return filesModule;
	}

	public static FacilioModule getResizedFilesModule() {
		FacilioModule filesModule = new FacilioModule();
		filesModule.setName("resilzedFiles");
		filesModule.setDisplayName("Resized Files");
		filesModule.setTableName("ResizedFile");

		return filesModule;
	}
	
	public static FacilioModule getPublicFilesModule() {
		FacilioModule filesModule = new FacilioModule();
		filesModule.setName("publicfiles");
		filesModule.setDisplayName("Public Files");
		filesModule.setTableName("Public_Files");

		return filesModule;
	}

	public static FacilioModule getZoneRelModule() {
		FacilioModule zoneRelModule = new FacilioModule();
		zoneRelModule.setName("zonespacerel");
		zoneRelModule.setDisplayName("Zone Space Rel");
		zoneRelModule.setTableName("Zone_Space");

		return zoneRelModule;
	}
	
	public static FacilioModule getZoneModule() {
		FacilioModule zoneRelModule = new FacilioModule();
		zoneRelModule.setName("zone");
		zoneRelModule.setDisplayName("Zone");
		zoneRelModule.setTableName("Zone");

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

	public static FacilioModule getNewControllerModule() {
		FacilioModule controllerModule = new FacilioModule();
		controllerModule.setName("controller");
		controllerModule.setDisplayName("ControllerV2");
		controllerModule.setTableName("Controllers");

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
	
	public static FacilioModule getHistoricalLoggerModule() {
		FacilioModule historicalLoggerModule = new FacilioModule();
		historicalLoggerModule.setName("historicalLoggerModuleCalculation");
		historicalLoggerModule.setDisplayName("Historical Logger Module");
		historicalLoggerModule.setTableName("Historical_Logger");
		return historicalLoggerModule;
	}
	
	public static FacilioModule getWorkflowRuleHistoricalLoggerModule() {
		FacilioModule workflowRuleHistoricalLoggerModule = new FacilioModule();
		workflowRuleHistoricalLoggerModule.setName("workflowRuleHistoricalLoggerModule");
		workflowRuleHistoricalLoggerModule.setDisplayName("Workflow Rule Historical Logger");
		workflowRuleHistoricalLoggerModule.setTableName("Workflow_Rule_Historical_Logger");
		return workflowRuleHistoricalLoggerModule;
	}
	
	public static FacilioModule getFormulaFieldHistoricalLoggerModule() {
		FacilioModule formulaFieldHistoricalLoggerModule = new FacilioModule();
		formulaFieldHistoricalLoggerModule.setName("formulaFieldHistoricalLoggerModule");
		formulaFieldHistoricalLoggerModule.setDisplayName("Formula Field Historical Logger Module");
		formulaFieldHistoricalLoggerModule.setTableName("Formula_Field_Historical_Logger");
		return formulaFieldHistoricalLoggerModule;
	}

	public static FacilioModule getWorkflowRuleLoggerModule() {
		FacilioModule workflowRuleLoggerModule = new FacilioModule();
		workflowRuleLoggerModule.setName("workflowRuleLoggerModule");
		workflowRuleLoggerModule.setDisplayName("Workflow Rule Logger");
		workflowRuleLoggerModule.setTableName("Workflow_Rule_Logger");
		return workflowRuleLoggerModule;
	}
	
	public static FacilioModule getWorkflowRuleResourceLoggerModule() {
		FacilioModule workflowRuleResourceLoggerModule = new FacilioModule();
		workflowRuleResourceLoggerModule.setName("workflowRuleResourceLoggerModule");
		workflowRuleResourceLoggerModule.setDisplayName("Workflow Rule Resource Logger");
		workflowRuleResourceLoggerModule.setTableName("Workflow_Rule_Resource_Logger");
		return workflowRuleResourceLoggerModule;
	}
	
	public static FacilioModule getWorkflowRuleHistoricalLogsModule() {
		FacilioModule workflowRuleHistoricalLogsModule = new FacilioModule();
		workflowRuleHistoricalLogsModule.setName("workflowRuleHistoricalLogsModule");
		workflowRuleHistoricalLogsModule.setDisplayName("Workflow Rule Historical Logs");
		workflowRuleHistoricalLogsModule.setTableName("Workflow_Rule_Historical_Logs");
		return workflowRuleHistoricalLogsModule;
	}
	
	public static FacilioModule getFormulaFieldDependenciesModule() {
		FacilioModule formulaFieldDependenciesModule = new FacilioModule();
		formulaFieldDependenciesModule.setName("formulaFieldDependenciesModule");
		formulaFieldDependenciesModule.setDisplayName("Formula Field Dependencies Module");
		formulaFieldDependenciesModule.setTableName("Formula_Field_Dependencies");
		return formulaFieldDependenciesModule;
	}
	
	public static FacilioModule getFormulaFieldResourceStatusModule() {
		FacilioModule formulaFieldResourceStatusModule = new FacilioModule();
		formulaFieldResourceStatusModule.setName("formulaFieldResourceStatusModule");
		formulaFieldResourceStatusModule.setDisplayName("Formula Field Resource Status Module");
		formulaFieldResourceStatusModule.setTableName("Formula_Field_Resource_Status");
		return formulaFieldResourceStatusModule;
	}
	
	public static FacilioModule getDashboardFolderModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Dashboard_Folder");
		dashboardWigetModule.setName("dashboardFolder");
		dashboardWigetModule.setDisplayName("Dashboard Folder");
		return dashboardWigetModule;
	}

	public static FacilioModule getDashboardRuleModule() {
		FacilioModule dashboardRule = new FacilioModule();
		dashboardRule.setTableName("Dashboard_Rules");
		return dashboardRule;
	}

	public static FacilioModule getDashboardTriggerWidgetModule() {
		FacilioModule dashboardTriggerWidget = new FacilioModule();
		dashboardTriggerWidget.setTableName("Dashboard_Trigger_Widgets");
		return dashboardTriggerWidget;
	}

	public static FacilioModule getDashboardRuleActionModule() {
		FacilioModule dashboardRule = new FacilioModule();
		dashboardRule.setTableName("Dashboard_Rule_Action");
		return dashboardRule;
	}
	public static FacilioModule getDashboardTriggerWidgetsModule() {
		FacilioModule dashboardRule = new FacilioModule();
		dashboardRule.setTableName("Dashboard_Trigger_Widgets");
		return dashboardRule;
	}
	public static FacilioModule getDashboardRuleActionMetaModule() {
		FacilioModule dashboardRule = new FacilioModule();
		dashboardRule.setTableName("Dashboard_Rules_Action_Meta");
		return dashboardRule;
	}
	public static FacilioModule getDashboardRuleTargetWidgetModule() {
		FacilioModule dashboardRule = new FacilioModule();
		dashboardRule.setTableName("Dashboard_Target_Widget_Mapping");
		return dashboardRule;
	}
	public static FacilioModule getDashboardModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Dashboard");
		return dashboardWigetModule;
	}
	public static FacilioModule getDashboardTabModule() {
		FacilioModule dashboardTabModule = new FacilioModule();
		dashboardTabModule.setTableName("Dashboard_Tab");
		return dashboardTabModule;
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
	public static FacilioModule getWidgetSectionModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Widget_Sections");
		dashboardWigetModule.setExtendModule(getWidgetModule());
		return dashboardWigetModule;
	}
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

	public static FacilioModule getWidgetGraphicsModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Widget_Graphics");
		dashboardWigetModule.setExtendModule(getWidgetModule());
		return dashboardWigetModule;
	}
	
	public static FacilioModule getWidgetCardModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Widget_Card");
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
		pmTriggers.setName("pmTrigger");
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

	
	public static FacilioModule getCustomFiltersModule() {
		FacilioModule customFiltersModule = new FacilioModule();
		customFiltersModule.setName("customFilters");
		customFiltersModule.setDisplayName("Custom Filters");
		customFiltersModule.setTableName("Custom_Filters");
		return customFiltersModule;
	}
	
	public static FacilioModule getQuickFilterModule() {
		FacilioModule quickFiltersModule = new FacilioModule();
		quickFiltersModule.setName("quickFilters");
		quickFiltersModule.setDisplayName("Quick Filters");
		quickFiltersModule.setTableName("Quick_Filters");
		return quickFiltersModule;
	}

	public static FacilioModule getTaskInputOptionModule() {
		FacilioModule taskInputOptionsModule = new FacilioModule();
		taskInputOptionsModule.setName("taskInputOpyion");
		taskInputOptionsModule.setDisplayName("Task Input Options");
		taskInputOptionsModule.setTableName("Task_Input_Options");
		return taskInputOptionsModule;
	}

	public static FacilioModule getJobPlanTaskInputOptionsModule() {
		// change module name to jobPlanTaskInputOptions
		FacilioModule taskInputOptionsModule = new FacilioModule("jobPlanTaskInputOptions", "JobPlan Task Input Options",
				"JobPlan_Task_Input_Options", FacilioModule.ModuleType.SUB_ENTITY, null,
				false); // in future need to enable the trash

		return taskInputOptionsModule;
	}

	public static FacilioModule getJobPlanSectionInputOptionsModule() {
		FacilioModule sectionInputOptionsModule = new FacilioModule("jobPlanSectionInputOptions", "JobPlan Section Input Options",
				"JobPlan_Section_Input_Options", FacilioModule.ModuleType.SUB_ENTITY, null,
				false); // in future need to enable the trash

		return sectionInputOptionsModule;
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
		FacilioModule module = new FacilioModule();
		module.setTableName("Workflow");
		return module;
	}

	public static FacilioModule getAggregationMetaModule() {
		FacilioModule module = new FacilioModule();
		module.setName("aggregationMeta");
		module.setDisplayName("Aggregation Meta");
		module.setTableName("Aggregation_Meta");
		return module;
	}

	public static FacilioModule getAggregationColumnMetaModule() {
		FacilioModule module = new FacilioModule();
		module.setName("aggregationColumnMeta");
		module.setDisplayName("Aggregation Column Meta");
		module.setTableName("Aggregation_Column_Meta");
		return module;
	}


	public static FacilioModule getWorkflowUserFunctionModule() {
		FacilioModule module = new FacilioModule();
		module.setExtendModule(getWorkflowModule());
		module.setTableName("Workflow_User_Function");
		return module;
	}

	public static FacilioModule getWorkflowNamespaceModule() {
		FacilioModule module = new FacilioModule();
		module.setTableName("Workflow_Namespace");
		return module;
	}

	public static FacilioModule getWorkflowLogModule() {
		FacilioModule dashboardWigetModule = new FacilioModule();
		dashboardWigetModule.setTableName("Workflow_Log");
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
	public static FacilioModule getReportScheduleInfoRel() {
		FacilioModule reportScheduleInfo = new FacilioModule();
		reportScheduleInfo.setName("reportScheduleInfoRel");
		reportScheduleInfo.setDisplayName("Report Schedule Info Rel");
		reportScheduleInfo.setTableName("Report_Schedule_Info1_Rel");
		return reportScheduleInfo;
	}
	public static FacilioModule getCalendarColorModule() {
		FacilioModule calendarColor = new FacilioModule();
		calendarColor.setName("calendarColor");
		calendarColor.setDisplayName("Calendar Color");
		calendarColor.setTableName("Calendar_Color");
		return calendarColor;
	}

	public static FacilioModule getPMPlannerSettingsModule() {
		FacilioModule pmPLannerSettingsModule = new FacilioModule();
		pmPLannerSettingsModule.setName("pmPlannerSettings");
		pmPLannerSettingsModule.setDisplayName("PM Planner settings");
		pmPLannerSettingsModule.setTableName("PM_Planner_Settings");
		return pmPLannerSettingsModule;
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
	public static FacilioModule getOdataModule(){
		FacilioModule module = new FacilioModule();
		module.setName("OData_Module_Settings");
		module.setDisplayName("OData Module Settings");
		module.setTableName("OData_Module_Settings");
		return module;
	}
	public static FacilioModule getODataReadingModule(){
		FacilioModule module = new FacilioModule();
		module.setName("OData_Readings");
		module.setDisplayName("OData Reading Settings");
		module.setTableName("OData_Reading_Settings");
		return module;
	}
	public static FacilioModule getResetCounterMetaModule() {
		FacilioModule resetcountermeta = new FacilioModule();
		resetcountermeta.setName("resetcountermeta");
		resetcountermeta.setDisplayName("Reset Counter Meta");
		resetcountermeta.setTableName("Reset_Counter_Meta");
		return resetcountermeta;
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
	
	public static FacilioModule getPrerequisiteApproverTemplateModule() {
		FacilioModule prerequisiteApproverModule = new FacilioModule();
		prerequisiteApproverModule.setName("prerequsiteApproversTemplate");
		prerequisiteApproverModule.setDisplayName("Prerequisite Approvers Template");
		prerequisiteApproverModule.setTableName("Prerequisite_Approvers_Template");
		prerequisiteApproverModule.setExtendModule(getTemplatesModule());
		return prerequisiteApproverModule;
	}
	public static FacilioModule getPrerequisiteApproversModule() {
		FacilioModule prerequisiteApproverModule = new FacilioModule();
		prerequisiteApproverModule.setName("prerequisiteApprovers");
		prerequisiteApproverModule.setDisplayName("Prerequisite Approvers");
		prerequisiteApproverModule.setTableName("Prerequisite_Approvers");
		return prerequisiteApproverModule;
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

	public static FacilioModule getKpiModule() {
		FacilioModule enpi = new FacilioModule();
		enpi.setName("kpi");
		enpi.setDisplayName("KPI");
		enpi.setTableName("Module_KPIs");
		return enpi;
	}

	public static FacilioModule getKPICategoryModule() {
		FacilioModule enpi = new FacilioModule();
		enpi.setName("kpiCategory");
		enpi.setDisplayName("KPI Categroy");
		enpi.setTableName("KPI_Category");
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

	public static FacilioModule getBaseFieldRelationModule() {
		FacilioModule module = new FacilioModule();
		module.setName("baseFieldRelation");
		module.setDisplayName("Base Field Relation");
		module.setTableName("Base_Field_Relation");
		return module;
	}

	public static FacilioModule getTimeDeltaFieldRelation() {
		FacilioModule module = new FacilioModule();
		module.setExtendModule(getBaseFieldRelationModule());
		module.setName("timeDeltaFieldRelation");
		module.setDisplayName("Time Delta Field Relation");
		module.setTableName("TimeDelta_Field_Relation");
		return module;
	}

	public static FacilioModule getDependencyJobDetailModule() {
		FacilioModule module = new FacilioModule();
		module.setName("dependencyJobDetail");
		module.setDisplayName("Dependency Job Detail");
		module.setTableName("Dependency_Job_Detail");
		return module;
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
	
	public static FacilioModule getDashboardPublishingModule() {
		FacilioModule dashboardPublishing = new FacilioModule();
		dashboardPublishing.setName("dashboardPublishing");
		dashboardPublishing.setDisplayName("Dashboard Publishing");
		dashboardPublishing.setTableName("Dashboard_Publishing");
		return dashboardPublishing;
	}

	public static FacilioModule getViewSharingModule() {
		FacilioModule viewSharing = new FacilioModule();
		viewSharing.setName("viewSharing");
		viewSharing.setDisplayName("View Sharing");
		viewSharing.setTableName("View_Sharing");
		return viewSharing;
	}

	public static FacilioModule getViewGroupSharingModule() {
		FacilioModule viewGroupSharing = new FacilioModule();
		viewGroupSharing.setName("viewGroupSharing");
		viewGroupSharing.setDisplayName("View Group Sharing");
		viewGroupSharing.setTableName("View_Group_Sharing");
		return viewGroupSharing;
	}
	
	public static FacilioModule getServiceCatalogItemSharingModule() {
		FacilioModule viewSharing = new FacilioModule();
		viewSharing.setName("serviceCatalogItemSharing");
		viewSharing.setDisplayName("Service Catalog Item Sharing");
		viewSharing.setTableName("Service_Catalog_Item_Sharing");
		return viewSharing;
	}
	
	public static FacilioModule getReportSharingModule() {
		FacilioModule viewSharing = new FacilioModule();
		viewSharing.setName("reportSharing");
		viewSharing.setDisplayName("Report Sharing");
		viewSharing.setTableName("Report_Folder_Sharing");
		return viewSharing;
	}

	public static FacilioModule getPMExecSharingModule() {
		FacilioModule pmExecSharing = new FacilioModule();
		pmExecSharing.setName("pmExecSharing");
		pmExecSharing.setDisplayName("PM Exec Sharing");
		pmExecSharing.setTableName("Pm_Exec_Sharing");
		return pmExecSharing;
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
	
	public static FacilioModule getImportPointsLogModule() {
		FacilioModule importPointsLog = new FacilioModule();
		importPointsLog.setName("importPointsLog");
		importPointsLog.setDisplayName("Import Points Log");
		importPointsLog.setTableName("ImportPointsLog");
		return importPointsLog;
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
	
	public static FacilioModule getInstantJobDeletionPropsModule() {
		FacilioModule commonJobProps = new FacilioModule();
		commonJobProps.setName("instantJobDeletionProps");
		commonJobProps.setDisplayName("Instant Job Deletion Props");
		commonJobProps.setTableName("Instant_Job_Deletion_Props");
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

	@Deprecated
	public static FacilioModule getShiftUserRelModule() {
		FacilioModule shiftUserRel = new FacilioModule();
		shiftUserRel.setName("shiftUserRel");
		shiftUserRel.setDisplayName("Shift User Rel");
		shiftUserRel.setTableName("Shift_User_Rel");
		return shiftUserRel;
	}
	
	public static FacilioModule getShiftBreakRelModule() {
		FacilioModule module = new FacilioModule();
		module.setName("ShiftBreakRel");
		module.setDisplayName("Shift Break Rel");
		module.setTableName("Shift_Break_Rel");
		return module;
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

	public static FacilioModule getReportShareModule() {
		FacilioModule costAssets = new FacilioModule();
		costAssets.setName("reportShare");
		costAssets.setDisplayName("Report Share");
		costAssets.setTableName("Report_Sharing");
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

	public static FacilioModule getPointsModule() {
		FacilioModule syncModule = new FacilioModule();
		syncModule.setName("points");
		syncModule.setDisplayName("Points");
		syncModule.setTableName("Points");
		return syncModule;
	}
	
	
	public static FacilioModule getPointModule() {
		FacilioModule pointModule = new FacilioModule();
		pointModule.setName(AgentConstants.POINTS);
		pointModule.setDisplayName("Point");
		pointModule.setTableName(AgentConstants.POINTS_TABLE);
		return pointModule;
	}
	
	public static FacilioModule getNiagaraPointModule() {
		FacilioModule niagaraPointModule = new FacilioModule();
		niagaraPointModule.setName("niagaraPoint");
		niagaraPointModule.setDisplayName("NiagaraPoint");
		niagaraPointModule.setTableName("Niagara_Point");
		niagaraPointModule.setExtendModule(getPointModule());
		return niagaraPointModule;
	}
	
	public static FacilioModule getBACnetIPPointModule() {
		FacilioModule bacnetIpPointModule = new FacilioModule();
		bacnetIpPointModule.setName("bacnetIpPoint");
		bacnetIpPointModule.setDisplayName("BACnetIPPoint");
		bacnetIpPointModule.setTableName("BACnet_IP_Point");
		bacnetIpPointModule.setExtendModule(getPointModule());
		return bacnetIpPointModule;
	}
	
	public static FacilioModule getModbusTcpPointModule() {
		FacilioModule modbusPointModule = new FacilioModule();
		modbusPointModule.setName("modbusTcpPoint");
		modbusPointModule.setDisplayName("ModbusTcpPoint");
		modbusPointModule.setTableName("Modbus_Tcp_Point");
		modbusPointModule.setExtendModule(getPointModule());
		return modbusPointModule;
	}
	public static FacilioModule getModbusRtuPointModule() {
		FacilioModule modbusPointModule = new FacilioModule();
		modbusPointModule.setName("modbusRtuPoint");
		modbusPointModule.setDisplayName("ModbusRtuPoint");
		modbusPointModule.setTableName("Modbus_Rtu_Point");
		modbusPointModule.setExtendModule(getPointModule());
		return modbusPointModule;
	}
	
	public static FacilioModule getOPCUAPointModule() {
		FacilioModule opcUAPointModule = new FacilioModule();
		opcUAPointModule.setName("opcUAPoint");
		opcUAPointModule.setDisplayName("OPCUAPoint");
		opcUAPointModule.setTableName("OPC_UA_Point");
		opcUAPointModule.setExtendModule(getPointModule());
		return opcUAPointModule;
	}

	public static FacilioModule getOPCXmlDAPointModule() {
		FacilioModule opcXmlDAPointModule = new FacilioModule();
		opcXmlDAPointModule.setName("opcXmlDAPoint");
		opcXmlDAPointModule.setDisplayName("OPCXMLDAPoint");
		opcXmlDAPointModule.setTableName("OPC_XML_DA_Point");
		opcXmlDAPointModule.setExtendModule(getPointModule());
		return opcXmlDAPointModule;
	}

	public static FacilioModule getRtuNetworkModule() {
		FacilioModule module = new FacilioModule();
		module.setName(AgentConstants.MODBUS_RTU_NETWORK_MODULE);
		module.setTableName("Rtu_Network");
		module.setDisplayName(" modbus rtu network ");
		return module;
	}

	public static FacilioModule getMiscPointModule() {
		FacilioModule opcXmlDAPointModule = new FacilioModule();
		opcXmlDAPointModule.setName("miscPoint");
		opcXmlDAPointModule.setDisplayName("MiscPoint");
		opcXmlDAPointModule.setTableName("Misc_Point");
		opcXmlDAPointModule.setExtendModule(getPointModule());
		return opcXmlDAPointModule;
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
	public static FacilioModule getWorkOrderItemsModule() {
		FacilioModule workOrderItemsModule = new FacilioModule();
		workOrderItemsModule.setName(ContextNames.WORKORDER_ITEMS);
		workOrderItemsModule.setDisplayName("Work Order Items");
		workOrderItemsModule.setTableName("Workorder_items");
		return workOrderItemsModule;
	}
	public static FacilioModule getWorkOrderToolsModule() {
		FacilioModule workOrderToolsModule = new FacilioModule();
		workOrderToolsModule.setName(ContextNames.WORKORDER_TOOLS);
		workOrderToolsModule.setDisplayName("Work Order Tools");
		workOrderToolsModule.setTableName("Workorder_tools");
		return workOrderToolsModule;
	}
	public static FacilioModule getWorkOrderServiceModule() {
		FacilioModule workOrderServiceModule = new FacilioModule();
		workOrderServiceModule.setName(ContextNames.WO_SERVICE);
		workOrderServiceModule.setDisplayName("Work Order Service");
		workOrderServiceModule.setTableName("Workorder_service");
		return workOrderServiceModule;
	}
	public static FacilioModule getInventoryReservationModule() {
		FacilioModule inventoryReservationModule = new FacilioModule();
		inventoryReservationModule.setName(ContextNames.INVENTORY_RESERVATION);
		inventoryReservationModule.setDisplayName("Inventory Reservation");
		inventoryReservationModule.setTableName("Inventory_Reservation");
		return inventoryReservationModule;
	}
	public static FacilioModule getJobPlanItemsModule() {
		FacilioModule jobPlanItemsModule = new FacilioModule();
		jobPlanItemsModule.setName(ContextNames.JOB_PLAN_ITEMS);
		jobPlanItemsModule.setDisplayName("Job Plan Items");
		jobPlanItemsModule.setTableName("Job_Plan_Items");
		return jobPlanItemsModule;
	}
	public static FacilioModule getJobPlanToolsModule() {
		FacilioModule jobPlanToolsModule = new FacilioModule();
		jobPlanToolsModule.setName(ContextNames.JOB_PLAN_TOOLS);
		jobPlanToolsModule.setDisplayName("Job Plan Tools");
		jobPlanToolsModule.setTableName("Job_Plan_Tools");
		return jobPlanToolsModule;
	}
	public static FacilioModule getJobPlanServiceModule() {
		FacilioModule jobPlanServiceModule = new FacilioModule();
		jobPlanServiceModule.setName(ContextNames.JOB_PLAN_SERVICES);
		jobPlanServiceModule.setDisplayName("Job Plan Services");
		jobPlanServiceModule.setTableName("Job_Plan_Services");
		return jobPlanServiceModule;
	}
	public static FacilioModule getVendorsModule() {
		FacilioModule inventoryModule = new FacilioModule();
		inventoryModule.setName(FacilioConstants.ContextNames.VENDORS);
		inventoryModule.setDisplayName("Vendors");
		inventoryModule.setTableName("Vendors");
		return inventoryModule;
	}
	public static FacilioModule getWorkOrderPlannedItemsModule() {
		FacilioModule plannedItemsModule = new FacilioModule();
		plannedItemsModule.setName(ContextNames.WO_PLANNED_ITEMS);
		plannedItemsModule.setDisplayName("WorkOrder Planned Items");
		plannedItemsModule.setTableName("Work_Order_Planned_Items");
		return plannedItemsModule;
	}
	public static FacilioModule getWorkOrderPlannedToolsModule() {
		FacilioModule plannedToolsModule = new FacilioModule();
		plannedToolsModule.setName(ContextNames.WO_PLANNED_TOOLS);
		plannedToolsModule.setDisplayName("WorkOrder Planned Tools");
		plannedToolsModule.setTableName("Work_Order_Planned_Tools");
		return plannedToolsModule;
	}
	public static FacilioModule getWorkOrderPlannedServicesModule() {
		FacilioModule plannedServicesModule = new FacilioModule();
		plannedServicesModule.setName(ContextNames.WO_PLANNED_SERVICES);
		plannedServicesModule.setDisplayName("WorkOrder Planned Services");
		plannedServicesModule.setTableName("Work_Order_Planned_Services");
		return plannedServicesModule;
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
		inventoryModule.setTableName("Tool");
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
  	
  	public static FacilioModule getPurchaseRequestModule() {
		FacilioModule purchaseRequest = new FacilioModule();
		purchaseRequest.setName("purchaserequest");
		purchaseRequest.setDisplayName("Purchase Request");
		purchaseRequest.setTableName("Purchase_Requests");
		return purchaseRequest;
	}
  	
  	public static FacilioModule getPurchaseOrderModule() {
		FacilioModule purchaseOrder = new FacilioModule();
		purchaseOrder.setName("purchaseorder");
		purchaseOrder.setDisplayName("Purchase Order");
		purchaseOrder.setTableName("Purchase_Orders");
		return purchaseOrder;
	}
  	
	public static FacilioModule getReceivableModule() {
		FacilioModule receivable = new FacilioModule();
		receivable.setName("receivable");
		receivable.setDisplayName("Receivables");
		receivable.setTableName("Receivables");
		return receivable;
	}
  	
  	
	public static FacilioModule getReceiptModule() {
		FacilioModule purchaseOrder = new FacilioModule();
		purchaseOrder.setName("receipts");
		purchaseOrder.setDisplayName("Receipts");
		purchaseOrder.setTableName("Receipts");
		return purchaseOrder;
	}
	
	public static FacilioModule getContractsModule() {
		FacilioModule contract = new FacilioModule();
		contract.setName("contracts");
		contract.setDisplayName("Contracts");
		contract.setTableName("Contracts");
		return contract;
	}

	public static FacilioModule getPlannedMaintenanceModule() {
		FacilioModule contract = new FacilioModule();
		contract.setName("plannedmaintenance");
		contract.setDisplayName("PlannedMaintenance");
		contract.setTableName("PM_V2");
		return contract;
	}

	public static FacilioModule getPurchaseContractModule() {
		FacilioModule purchasecontract = new FacilioModule();
		purchasecontract.setName("purchasecontracts");
		purchasecontract.setDisplayName("Purchase Contracts");
		purchasecontract.setTableName("Purchase_Contracts");
		return purchasecontract;
	}

	public static FacilioModule getLabourContractModule() {
		FacilioModule labourcontract = new FacilioModule();
		labourcontract.setName("labourcontracts");
		labourcontract.setDisplayName("Labour Contracts");
		labourcontract.setTableName("Labour_Contracts");
		return labourcontract;
	}

	public static FacilioModule getControlActionTemplateModule() {
		FacilioModule controlActionTemplate = new FacilioModule();
		controlActionTemplate.setName("controlActionTemplate");
		controlActionTemplate.setDisplayName("Control Action Template");
		controlActionTemplate.setTableName("Control_Action_Template");
		controlActionTemplate.setExtendModule(getTemplatesModule());
		return controlActionTemplate;
	}
	
	public static FacilioModule getFormTemplatesModule() {
		FacilioModule formTemplate = new FacilioModule();
		formTemplate.setName("formTemplate");
		formTemplate.setDisplayName("Form Template");
		formTemplate.setTableName("Form_Template");
		formTemplate.setExtendModule(getTemplatesModule());
		return formTemplate;
	}

	public static FacilioModule getSatisfactionSurveyTemplateModule () {
		FacilioModule satisfactionSurveyTemplateModule = new FacilioModule();
		satisfactionSurveyTemplateModule.setName("satisfactionSurveyTemplate");
		satisfactionSurveyTemplateModule.setDisplayName("Satisfaction Survey Templates");
		satisfactionSurveyTemplateModule.setTableName("Satisfaction_Survey_Templates");
		satisfactionSurveyTemplateModule.setExtendModule(getTemplatesModule());
		return satisfactionSurveyTemplateModule;
	}
	
	public static FacilioModule getSatisfactionSurveyRuleModule() {
		FacilioModule satisfactionSurveyRuleModule = new FacilioModule();
		satisfactionSurveyRuleModule.setName("satisfactionSurveyRule");
		satisfactionSurveyRuleModule.setDisplayName("Satisfaction Survey Rule");
		satisfactionSurveyRuleModule.setTableName("Satisfaction_Survey_Rules");
		satisfactionSurveyRuleModule.setExtendModule(getWorkflowRuleModule());
		return satisfactionSurveyRuleModule;
	}

	public static FacilioModule getSurveyResponseRuleModule() {
		FacilioModule satisfactionSurveyRuleModule = new FacilioModule();
		satisfactionSurveyRuleModule.setName("surveyResponseRule");
		satisfactionSurveyRuleModule.setDisplayName("Survey Response Rule");
		satisfactionSurveyRuleModule.setTableName("Survey_Response_Rules");
		satisfactionSurveyRuleModule.setExtendModule(getWorkflowRuleModule());
		return satisfactionSurveyRuleModule;
	}
	
	public static FacilioModule getMLModule() 
	{
		FacilioModule mlModule = new FacilioModule();
		mlModule.setName("ml");
		mlModule.setDisplayName("Ml");
		mlModule.setTableName("ML");
		return mlModule;
	}
	
	public static FacilioModule getMLAssetModule() 
	{
		FacilioModule mlAssetModule = new FacilioModule();
		mlAssetModule.setName("mlAssets");
		mlAssetModule.setDisplayName("MlAssets");
		mlAssetModule.setTableName("ML_Assets");
		return mlAssetModule;
	}
	
	public static FacilioModule getMLAssetVariablesModule() 
	{
		FacilioModule mlAssetVariablesModule = new FacilioModule();
		mlAssetVariablesModule.setName("mlAssetVariables");
		mlAssetVariablesModule.setDisplayName("MLAssetVariables");
		mlAssetVariablesModule.setTableName("ML_Asset_Variables");
		return mlAssetVariablesModule;
	}
	
	public static FacilioModule getMLServiceModule() {
		FacilioModule mlServiceModule = new FacilioModule();
		mlServiceModule.setName("mlService");
		mlServiceModule.setDisplayName("MLService");
		mlServiceModule.setTableName("ML_Service");
		return mlServiceModule;
	}
	public static FacilioModule getMLBmsPointsTaggingModule() {
		FacilioModule mlBmsPointsTaggingModule = new FacilioModule();
		mlBmsPointsTaggingModule.setName("mlBmsPointsTagging");
		mlBmsPointsTaggingModule.setDisplayName("ML_BmsPredictedPoints");
		mlBmsPointsTaggingModule.setTableName("ML_BmsPredictedPoints");
		return mlBmsPointsTaggingModule;
	}
	
	public static FacilioModule getMLModelVariablesModule()
	{
		FacilioModule mlModelVariableModule = new FacilioModule();
		mlModelVariableModule.setName("mlModelVariables");
		mlModelVariableModule.setDisplayName("MLModelVariables");
		mlModelVariableModule.setTableName("ML_Model_Variables");
		return mlModelVariableModule;
	}
	
	public static FacilioModule getMLVariablesModule()
	{
		FacilioModule mlVariableModule = new FacilioModule();
		mlVariableModule.setName("mlVariables");
		mlVariableModule.setDisplayName("MLVariables");
		mlVariableModule.setTableName("ML_Variables");
		return mlVariableModule;
	}
	
	public static FacilioModule getMLCriteriaVariablesModule()
	{
		FacilioModule mlCriteriaVariableModule = new FacilioModule();
		mlCriteriaVariableModule.setName("mlCriteriaVariables");
		mlCriteriaVariableModule.setDisplayName("MLCriteriaVariables");
		mlCriteriaVariableModule.setTableName("ML_Criteria_Variables");
		return mlCriteriaVariableModule;
	}
	
	public static FacilioModule getMLLogReadingModule(){
		FacilioModule module = new FacilioModule();
		module.setName("mlLogReadings");
		module.setDisplayName("ML Log Readings");
		module.setTableName("ML_Log_Readings");
		return module;
	}

	public static FacilioModule getMLReadingModule(){
		FacilioModule module = new FacilioModule();
		module.setName("mlReadings");
		module.setDisplayName("ML Readings");
		module.setTableName("ML_Readings");
		return module;
	}

	public static FacilioModule getMLModelParamsModule(){
		FacilioModule module = new FacilioModule();
		module.setName("mlModelParams");
		module.setDisplayName("ML Model Params");
		module.setTableName("ML_Model_Params");
		return module;
	}

	public static FacilioModule getSitesForStoreRoomModule() {
		FacilioModule accessbileSpaceModule = new FacilioModule();
		accessbileSpaceModule.setName(FacilioConstants.ContextNames.SITES_FOR_STORE_ROOM);
		accessbileSpaceModule.setDisplayName("Store Room Sites");
		accessbileSpaceModule.setTableName("Storeroom_Sites");
		return accessbileSpaceModule;
	}

	public static FacilioModule getPoLineItemsSerialNumberModule() {
		FacilioModule tenants = new FacilioModule();
		tenants.setName("poLineItemSerialNumbers");
		tenants.setDisplayName("Serial Numbers");
		tenants.setTableName("PO_Line_Item_Serial_Numbers");
		return tenants;
	}
	
	public static FacilioModule getGatePassModule() {
		FacilioModule gatePass = new FacilioModule();
		gatePass.setName("gatePass");
		gatePass.setDisplayName("Gate Pass");
		gatePass.setTableName("Gate_Pass");
		return gatePass;
	}
	
	public static FacilioModule getInventoryRequestModule() {
		FacilioModule invReq = new FacilioModule();
		invReq.setName("inventoryrequest");
		invReq.setDisplayName("Inventory Request");
		invReq.setTableName("Inventory_Requests");
		return invReq;
	}

	public static FacilioModule getInventoryRequestLineItemsModule() {
		FacilioModule invReqLineItems = new FacilioModule();
		invReqLineItems.setName("inventoryrequestlineitems");
		invReqLineItems.setDisplayName("Inventory Request Line Items");
		invReqLineItems.setTableName("InventoryRequestLineItems");
		return invReqLineItems;
	}

	public static FacilioModule getPageModule() {
		FacilioModule module = new FacilioModule();
		module.setName("page");
		module.setDisplayName("Page");
		module.setTableName("Page");
		return module;
	}

	public static FacilioModule getCustomButtonRuleModule() {
		FacilioModule module = new FacilioModule();
		module.setName("customButtonRule");
		module.setDisplayName("Custom Button Workflow");
		module.setTableName("CustomButton");
		module.setExtendModule(getWorkflowRuleModule());
		return module;
	}

	public static FacilioModule getSystemButtonRuleModule(){
		FacilioModule module = new FacilioModule();
		module.setName("system_Button");
		module.setDisplayName("System Button");
		module.setTableName("SystemButton");
		return module;
	}
	public static FacilioModule getStateRuleTransitionModule() {
		FacilioModule module = new FacilioModule();
		module.setName("stateruleTransitionWorkflow");
		module.setDisplayName("State Rule Transition Workflow");
		module.setTableName("StateFlowTransition");
		module.setExtendModule(getWorkflowRuleModule());
		return module;
	}

	public static FacilioModule getParallelStateTransitionsStatusModule() {
		FacilioModule module = new FacilioModule();
		module.setName("parallelStateRuleTransitionStatus");
		module.setDisplayName("Parallel State Rule Transition Status");
		module.setTableName("ParallelStateTransitionStatus");
		return module;
	}

	public static FacilioModule getStateFlowModule() {
		FacilioModule module = new FacilioModule();
		module.setName("stateflow");
		module.setDisplayName("State Flow");
		module.setTableName("StateFlow");
		return module;
	}

	public static FacilioModule getAlarmWorkflowRuleModule() {
		FacilioModule module = new FacilioModule();
		module.setName("alarmworkflow_rule");
		module.setDisplayName("Alarm Workflow Rule");
		module.setTableName("AlarmWorkflow_Rule");
		return module;
	}

	public static FacilioModule getStateFlowScheduleModule() {
		FacilioModule module = new FacilioModule();
		module.setName("stateflowScheduler");
		module.setDisplayName("State Flow Scheduler");
		module.setTableName("StateFlowScheduler");
		return module;
	}
	
//	public static FacilioModule getStateModule() {
//		FacilioModule module = new FacilioModule();
//		module.setName("state");
//		module.setDisplayName("State");
//		module.setTableName("TicketStatus");
//		return module;
//	}

//	public static FacilioModule getStateFlowRuleModule() {
//		FacilioModule module = new FacilioModule();
//		module.setName("stateFlowRuleWorkflow");
//		module.setDisplayName("State Flow Rule Workflow");
//		module.setTableName("StateFlowRule");
//		module.setExtendModule(getWorkflowRuleModule());
//		return module;
//	}

	public static FacilioModule getGlobalVariableGroupModule() {
		FacilioModule module = new FacilioModule();
		module.setName("globalVariableGroup");
		module.setDisplayName("Global Variable Group");
		module.setTableName("Global_Variable_Group");
		return module;
	}

	public static FacilioModule getGlobalVariableModule() {
		FacilioModule module = new FacilioModule();
		module.setName("globalVariable");
		module.setDisplayName("Global Variable");
		module.setTableName("Global_Variable");
		return module;
	}
	
	public static FacilioModule getMobileDetailsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("mobileDetails");
		module.setDisplayName("Mobile Details");
		module.setTableName("MobileDetails");
		return module;
	}

	public static FacilioModule getJobPlanModule() {
		FacilioModule module = new FacilioModule();
		module.setName("jobplan");
		module.setDisplayName("Job Plan");
		module.setTableName("JobPlan");
		return module;
	}

	public static FacilioModule getShippedAssetRelModule() {
		FacilioModule module = new FacilioModule();
		module.setName("shippedAssetRel");
		module.setDisplayName("Shipped_Asset_Rel");
		module.setTableName("Shipped_Asset_Rel");
		return module;
	}

	public static FacilioModule getValidationModule() {
		FacilioModule module = new FacilioModule();
		module.setName("workflowValidation");
		module.setDisplayName("Workflow Validations");
		module.setTableName("Workflow_Validation");
		return module;
	}

	public static FacilioModule getFormValidationRuleModule() {
		FacilioModule module = new FacilioModule();
		module.setName("formValidationRule");
		module.setDisplayName("Form Validation Rule");
		module.setTableName("Form_Validation_Rule");
		return module;
	}

	public static FacilioModule getConfirmationDialogModule() {
		FacilioModule module = new FacilioModule();
		module.setName("confirmationDialog");
		module.setDisplayName("Confirmation Dialog");
		module.setTableName("Confirmation_Dialog");
		return module;
	}

	public static FacilioModule getContractAssociatedAssetsModule() {
		FacilioModule associatedAssetModule = new FacilioModule();
		associatedAssetModule.setName("associatedassets");
		associatedAssetModule.setDisplayName("Associated Assets");
		associatedAssetModule.setTableName("Contracts_Associated_Assets");
		return associatedAssetModule;
	}

	public static FacilioModule getShipmentModule() {
		FacilioModule labourcontract = new FacilioModule();
		labourcontract.setName("shipment");
		labourcontract.setDisplayName("Shipments");
		labourcontract.setTableName("Shipment");
		return labourcontract;
	}

	public static FacilioModule getAttendanceModule() {
		FacilioModule module = new FacilioModule();
		module.setName("attendance");
		module.setDisplayName("Attendance");
		module.setTableName("Attendance");
		return module;
	}
	public static FacilioModule getAssetSparePartsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("assetSpareParts");
		module.setDisplayName("Asset Spare Parts");
		module.setTableName("Asset_Spare_Parts");
		return module;
	}

	public static FacilioModule getAttendanceTransactionModule() {
		FacilioModule module = new FacilioModule();
		module.setName("attendanceTransaction");
		module.setDisplayName("Attendance Transaction");
		module.setTableName("Attendance_Transactions");
		return module;
	}

	public static FacilioModule getGraphicsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("graphics");
		module.setDisplayName("Graphics");
		module.setTableName("Graphics");
		return module;
	}
	
	public static FacilioModule getGraphicsFolderModule() {
		FacilioModule module = new FacilioModule();
		module.setName("graphicsFolder");
		module.setDisplayName("Graphics Folder");
		module.setTableName("Graphics_Folder");
		return module;
	}
	
	public static FacilioModule getShiftRotationApplicableForModule() {
		FacilioModule module = new FacilioModule();
		module.setName("ShiftRotationApplicableFor");
		module.setDisplayName("Shift Rotation Applicable For");
		module.setTableName("Shift_Rotation_Applicable_For");
		return module;
	}
	
	public static FacilioModule getShiftRotationDetailsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("ShiftRotationDetails");
		module.setDisplayName("Shift Rotation Details");
		module.setTableName("Shift_Rotation_Details");
		return module;
	}
	
	public static FacilioModule getShiftRotationModule() {
		FacilioModule module = new FacilioModule();
		module.setName("shiftRotation");
		module.setDisplayName("Shift Rotation");
		module.setTableName("Shift_Rotation");
		return module;
	}
	
	public static FacilioModule getBreakTransactionModule() {
		FacilioModule module = new FacilioModule();
		module.setName("breakTransaction");
		module.setDisplayName("Break Transaction");
		module.setTableName("Break_Transaction");
		return module;
	}
	
	public static FacilioModule getRentalLeaseContractModule() {
		FacilioModule module = new FacilioModule();
		module.setName("rentalleasecontracts");
		module.setDisplayName("Lease/Rental Contracts");
		module.setTableName("Rental_Lease_Contracts");
		return module;
	}

	public static FacilioModule getWarrantyContractModule() {
		FacilioModule module = new FacilioModule();
		module.setName("warrantycontracts");
		module.setDisplayName("Warranty Contracts");
		module.setTableName("Warranty_Contracts");
		return module;
	}

	public static FacilioModule getStoreNotificationConfigModule() {
		FacilioModule module = new FacilioModule();
		module.setName("storeRoomRule");
		module.setDisplayName("Store Room Notification rules");
		module.setTableName("Store_Notification_config");
		return module;
	}


	public static FacilioModule getTermsAndConditionModule() {
		FacilioModule module = new FacilioModule();
		module.setName("termsandconditions");
		module.setDisplayName("Terms and Conditions");
		module.setTableName("Terms_And_Conditions");
		return module;
	}
	
	public static FacilioModule getServiceModule() {
		FacilioModule module = new FacilioModule();
		module.setName("service");
		module.setDisplayName("Service");
		module.setTableName("Service");
		return module;
	}
	


	public static FacilioModule getReadingAlarmModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.NEW_READING_ALARM);
		module.setDisplayName("Faults");
		module.setTableName("ReadingAlarm");
		return module;
	}
	public static FacilioModule getBmsAlarmModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.BMS_ALARM);
		module.setDisplayName("BMS Alarm");
		module.setTableName("BMSAlarm");
		return module;
	}
	
	public static FacilioModule getSensorAlarmModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.SENSOR_ALARM);
		module.setDisplayName("Sensor Child Alarm");
		module.setTableName("SensorAlarm");
		return module;
	}
	
	public static FacilioModule getSensorRollUpAlarmModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.SENSOR_ROLLUP_ALARM);
		module.setDisplayName("Sensor Alarm");
		module.setTableName("SensorRollUpAlarm");
		return module;
	}

	public static FacilioModule getDigestConfigModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.DIGEST_CONFIG);
		module.setDisplayName("Digest Config");
		module.setTableName("Digest_Configuration");
		return module;
	}

	public static FacilioModule getBaseAlarmModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.BASE_ALARM);
		module.setDisplayName("Base Alarm");
		module.setTableName("BaseAlarm");
		return module;
	}
	
	public static FacilioModule getBaseEventModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.BASE_EVENT);
		module.setDisplayName("Base Event");
		module.setTableName("BaseEvent");
		return module;
	}
	
	public static FacilioModule getPreferenceMetaModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.PREFERENCE_META);
		module.setDisplayName("Preferences");
		module.setTableName("Preferences");
		return module;
	}
	
	public static FacilioModule getPreferenceRuleModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.PREFERENCE_RULES);
		module.setDisplayName("Preferences Rules");
		module.setTableName("Preference_Rules");
		return module;
	}

	
	public static FacilioModule getReservationModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.Reservation.RESERVATION);
		module.setDisplayName("Reservation");
		module.setTableName("Reservation");
		return module;
	}
	
	public static FacilioModule getMlAnomalyAlarmModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.ML_ANOMALY_ALARM);
		module.setDisplayName("ML Anomaly Alarm");
		module.setTableName("ML_Anomaly_Alarm");
		return module;
	}
	
	public static FacilioModule getControlGroupModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXT);
		module.setDisplayName(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXT);
		module.setTableName("Control_Groups");
		return module;
	}
	
	public static FacilioModule getControllablePointModule() {
		return constructModule("contollablepoint", "Controllable Point", "Controllable_Point");
	}
	
	public static FacilioModule getControlPointModule() {
		return constructModule("controlpoint", "Control Point", "Control_Point",getReadingDataMetaModule());
	}
	
	public static FacilioModule getControlGroupSpaceModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXT_SPACE);
		module.setDisplayName(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXT_SPACE);
		module.setTableName("Control_Group_Spaces");
		return module;
	}
	
	public static FacilioModule getControlGroupInclExclModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXT_INCL_EXLC);
		module.setDisplayName(ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXT_INCL_EXLC);
		module.setTableName("Control_Group_Include_Exclude_Resource");
		return module;
	}


	public static FacilioModule getImportPointsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("importPoints");
		module.setDisplayName("Import Points");
		module.setTableName("ImportPoints");
		return module;
	}
	public static FacilioModule getDevicePasscodesModule() {
		FacilioModule module = new FacilioModule();
		module.setName("devicePasscodes");
		module.setDisplayName("Device Passcodes");
		module.setTableName("DevicePasscodes");
		return module;
	}
	public static FacilioModule getConnectedDevicesModule() {
		FacilioModule module = new FacilioModule();
		module.setName("connectedDevices");
		module.setDisplayName("Connected Devices");
		module.setTableName("ConnectedDevices");
		return module;
	}
	


	public static FacilioModule getAgentControllerModule() {
		FacilioModule module = new FacilioModule();
		module.setName("agentController");
		module.setDisplayName("agentController");
		module.setTableName(AgentConstants.AGENT_CONTROLLER_TABLE);
		return module;
	}

    public static FacilioModule getModbusControllerModbus() {
        FacilioModule module = new FacilioModule();
        module.setName("modbusController");
        module.setDisplayName("modbusController");
        module.setTableName(AgentConstants.MODBUS_CONTROLLER_TABLE);
        return module;
    }

	public static FacilioModule getFieldDeviceModule() {
		FacilioModule module = new FacilioModule();
		module.setTableName("Field_Device");
		module.setDisplayName("field device");
		module.setName(AgentConstants.FIELD_DEVICE_TABLE);
		return module;
	}
	//devices as in TV,KIOSK etc
	public static FacilioModule getDevicesModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ModuleNames.DEVICES);
		module.setDisplayName("Devices");
		module.setTableName("Devices");
		return module;
	}
	public static FacilioModule getLogBookModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.DIGITAL_LOG_BOOK);
		module.setDisplayName("Digital Log book");
		module.setTableName("Digital_Log_Book");
		return module;
	}
	
	public static FacilioModule getVisitorModule()
	{
		FacilioModule module=new FacilioModule();		
		module.setName(ContextNames.VISITOR);
		module.setDisplayName("Visitors");
		module.setTableName("Visitors");
		return module;
	}
	public static FacilioModule getVisitorLoggingModule()
	{
		FacilioModule module=new FacilioModule();		
		module.setName(ContextNames.VISITOR_LOGGING);
		module.setDisplayName("Visitor Logging");
		module.setTableName("Visitor_Logging");
		return module;
	}

	public static FacilioModule getVisitorLogModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.VISITOR_LOGGING);
		module.setDisplayName("Visitor Logging");
		module.setTableName("Visitor_Logging");
		return module;
	}
	
	public static FacilioModule getBaseVisitorLogCheckInModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.BASE_VISIT);
		module.setDisplayName("Base Visits");
		module.setTableName("BaseVisit");
		return module;
	}
	
	public static FacilioModule getVisitorLogCheckInModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.VISITOR_LOG);
		module.setDisplayName("Visits");
		module.setTableName("VisitorLog");
		return module;
	}
	
	public static FacilioModule getInviteVisitorLogModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.INVITE_VISITOR);
		module.setDisplayName("Invites");
		module.setTableName("InviteVisitor");
		return module;
	}
	
	public static FacilioModule getVisitorInviteModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.VISITOR_INVITE);
		module.setDisplayName("Visitor Invites");
		module.setTableName("Visitor_Invites");
		return module;
	}
	
	public static FacilioModule getInsuranceModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.INSURANCE);
		module.setDisplayName("Insurance");
		module.setTableName("Insurance");
		return module;
	}
	
	public static FacilioModule getWorkPermitModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.WorkPermit.WORKPERMIT);
		module.setDisplayName("Work Permit");
		module.setTableName("WorkPermit");
		return module;
	}
	
	public static FacilioModule getFacilioQueueModule() {
		FacilioModule module = new FacilioModule();
		module.setTableName("FacilioQueue");
		module.setDisplayName("FacilioQueue");
		module.setName("facilioQueue");
		return module;

	}

	public static FacilioModule getIotDataModule() {
		FacilioModule module = new FacilioModule();
		module.setTableName("Iot_Data");
		module.setDisplayName("Iot Data");
		module.setName(AgentConstants.IOT_DATA);
		return module;
	}

	public static FacilioModule getIotMessageModule() {
		FacilioModule module = new FacilioModule();
		module.setTableName("Iot_Message");
		module.setDisplayName("Iot Message");
		module.setName(AgentConstants.IOT_MESSAGE);
		return module;
	}

    public static FacilioModule getConnectionApiModule() {
		FacilioModule module = new FacilioModule();
		module.setTableName("Connection_Api");
		module.setDisplayName("ConnectionApi");
		module.setName("connectionApi");
		return module;
    }

    public static FacilioModule getServiceCatalogModule() {
		FacilioModule module = new FacilioModule();
		module.setTableName("Service_Catalog");
		module.setDisplayName("Service Catalog");
		module.setName("serviceCatalog");
		return module;
	}

	public static FacilioModule getServiceCatalogGroupModule() {
		FacilioModule module = new FacilioModule();
		module.setTableName("Service_Catalog_Group");
		module.setDisplayName("Service Catalog Group");
		module.setName("serviceCatalogGroup");
		return module;
	}
    
    public static FacilioModule getVisitorSettingsModule()
    {
    	FacilioModule module=new FacilioModule();
    	module.setTableName("VisitorSettings");
    	module.setDisplayName("Visitor Settings");
		module.setName("visitorSettings");
		return module;
    	
    }
    
    public static FacilioModule getVisitorTypeFormsModule()
    {
    	FacilioModule module=new FacilioModule();
    	module.setTableName("VisitorTypeForms");
    	module.setDisplayName("VisitorTypeForms");
		module.setName("visitorTypeForms");
		return module;
    	
    }
    
	public static FacilioModule getWatchListModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.WATCHLIST);
		module.setDisplayName("Watchlist");
		module.setTableName("WatchList");
		return module;
	}

    public static FacilioModule getContactModule()
    {
        FacilioModule module=new FacilioModule();
        module.setName(ContextNames.CONTACT);
        module.setDisplayName("Contact");
        module.setTableName("Contacts");
        return module;
    }

	public static FacilioModule getAgentMessageIntegrationModule() {
		FacilioModule module=new FacilioModule();
		module.setTableName("Agent_Message_Integration");
		module.setDisplayName("Agent Message Integration");
		module.setName("agentMessageIntegration");
		return module;
	}
	
	public static FacilioModule getRelatedWorkorderModule() {
		FacilioModule module=new FacilioModule();
		module.setTableName("Related_Workorders");
		module.setDisplayName("Related Workorders");
		module.setName("relatedWorkorders");
		return module;
	}
	public static FacilioModule getVisitorLogTriggersModule() {
		FacilioModule pmTriggers = new FacilioModule();
		pmTriggers.setName("visitorLogTrigger");
		pmTriggers.setDisplayName("Visitor Log Triggers");
		pmTriggers.setTableName("VisitorLog_Triggers");
		return pmTriggers;
	}
	
	public static FacilioModule getBaseSchedulerModule() {
		FacilioModule pmTriggers = new FacilioModule();
		pmTriggers.setName("baseScheduler");
		pmTriggers.setDisplayName("Base Scheduler");
		pmTriggers.setTableName("BaseScheduler");
		return pmTriggers;
	}

	public static FacilioModule getAgentVersionLogModule() {
		FacilioModule agentVersionLogModule = new FacilioModule();
		agentVersionLogModule.setName("agentVersionLog");
		agentVersionLogModule.setDisplayName("Agent Version Log Module");
		agentVersionLogModule.setTableName("Agent_VersionLog");
		return agentVersionLogModule;
	}
	public static FacilioModule getSecretFileModule() {
		FacilioModule secretFilesModule = new FacilioModule();
		secretFilesModule.setName("secretFile");
		secretFilesModule.setDisplayName("Secret Files");
		secretFilesModule.setTableName("Secret_File");
		return secretFilesModule;
	}
	
	public static FacilioModule getPrinterModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ModuleNames.PRINTERS);
		module.setDisplayName("Printers");
		module.setTableName("Printers");
		return module;
	}
	public static FacilioModule getVisitorKioskModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ModuleNames.VISITOR_KIOSK);
		module.setDisplayName("Visitor Kiosk");
		module.setTableName("Devices_Visitor_Kiosk");
		return module;
	}


	public static FacilioModule getDeviceCatalogMappingModule() {
		FacilioModule module = new FacilioModule();
		module.setName("deviceCatalogMapping");
		module.setDisplayName("Device Catalog Mapping");
		module.setTableName("Device_Catalog_Mapping");
		return module;
	}

	public static FacilioModule getOccupantModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.OCCUPANT);
		module.setDisplayName("Occupants");
		module.setTableName("Occupants");
		return module;
	}
	
	public static FacilioModule getApplicationModule() {
		FacilioModule module = new FacilioModule();
		module.setName("application");
		module.setDisplayName("Application");
		module.setTableName("Application");
		return module;
	}

	public static FacilioModule getApplicationLayoutModule() {
		FacilioModule module = new FacilioModule();
		module.setName("applicationlayout");
		module.setDisplayName("Application Layout");
		module.setTableName("Application_Layout");
		return module;
	}

	public static FacilioModule getApplicationRelatedAppsModule(){
		FacilioModule module = new FacilioModule();
		module.setName("applicationRelatedApps");
		module.setDisplayName("Application Related Apps");
		module.setTableName("Application_Related_Apps");
		return module;
	}

	public static FacilioModule getWebTabGroupModule() {
		FacilioModule module = new FacilioModule();
		module.setName("webTabGroup");
		module.setDisplayName("WebTab Group");
		module.setTableName("WebTab_Group");
		return module;
	}

	public static FacilioModule getWebTabModule() {
		FacilioModule module = new FacilioModule();
		module.setName("webTab");
		module.setDisplayName("WebTab");
		module.setTableName("WebTab");
		return module;
	}

	public static FacilioModule getWebTabWebGroupModule() {
		FacilioModule module = new FacilioModule();
		module.setName("webTabWdbGroup");
		module.setDisplayName("WebTab WebGroup");
		module.setTableName("WebTab_WebGroup");
		return module;
	}

	public static FacilioModule getUserDelegationModule() {
		FacilioModule module = new FacilioModule();
		module.setName("userDelegation");
		module.setDisplayName("User Delegation");
		module.setTableName("User_Delegation");
		return module;
	}

	
	public static FacilioModule getServiceRequestModule() {
		FacilioModule ticketStatusModule = new FacilioModule();
		ticketStatusModule.setName(FacilioConstants.ContextNames.SERVICE_REQUEST);
		ticketStatusModule.setDisplayName("Service Requests");
		ticketStatusModule.setTableName("Service_Requests");
		return ticketStatusModule;
	}
	
	public static FacilioModule getVendorDocumentsModule() {
		FacilioModule vendorDocumentsModule = new FacilioModule();
		vendorDocumentsModule.setName(FacilioConstants.ContextNames.VENDOR_DOCUMENTS);
		vendorDocumentsModule.setDisplayName("Vendor Documents");
		vendorDocumentsModule.setTableName("Vendor_Documents");
		return vendorDocumentsModule;
	}
	public static FacilioModule getAgentMetricsV2Module() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.AGENT_METRICS_MODULE);
		module.setDisplayName(" agent metrics v2 module");
		module.setTableName("Agent_V2_Metrics");
		return module;
	}

	public static FacilioModule getAgentV2LogModule() {
		FacilioModule module = new FacilioModule();
		module.setName(AgentConstants.AGENT_V2_LOG_MODULE);
		module.setTableName("Agent_V2_Log");
		module.setDisplayName("agent v2 log");
		return module;
	}

	public static FacilioModule getNewPermissionModule() throws Exception {
		return getNewPermissionModule(false);
	}
	public static FacilioModule getNewPermissionModule(boolean skipLicence) throws Exception {
		if(!skipLicence) {
			if (V3PermissionUtil.isFeatureEnabled()) {
				return getNewTabPermissionModule();
			}
		}
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.NEW_PERMISSIONS);
		module.setTableName("NewPermission");
		module.setDisplayName("New Permissions");
		return module;
	}

	public static FacilioModule getNewTabPermissionModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.NEW_PERMISSIONS);
		module.setTableName("NewTabPermission");
		module.setDisplayName("New Permissions");
		return module;
	}

	public static FacilioModule getTabIdAppIdMappingModule() {
		FacilioModule module = new FacilioModule();
		module.setName("tabIdAppIdMapping");
		module.setTableName("TABID_MODULEID_APPID_MAPPING");
		module.setDisplayName("Tab IdApp Id Mapping");
		return module;
	}

	public static FacilioModule getFaceCollectionsModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.FACE_COLLECTIONS);
		module.setTableName("FaceCollections");
		module.setDisplayName("Face Collections");
		return module;
	}

	public static FacilioModule getVisitorFacesModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.VISITOR_FACES);
		module.setTableName("Visitor_Faces");
		module.setDisplayName("Visitor Faces");
		return module;
	}

	public static FacilioModule getAgentAlarmsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("agentAlarm");
		module.setTableName("Agent_Alarm");
		module.setDisplayName("Agent Alarm");
		return module;
	}

	public static FacilioModule getAgentAlarmOccurrenceModule() {
		FacilioModule module = new FacilioModule();
		module.setName("agentAlarmOccurrence");
		module.setTableName("Agent_AlarmOccurrence");
		module.setDisplayName("Agent Alarm Occurrence");
		return module;
	}

	public static FacilioModule getRuleRollupSummaryModule() {
		FacilioModule module = new FacilioModule();
		module.setName("ruleRollupSummary");
		module.setTableName("Rule_Rollup_Summary");
		module.setDisplayName("Rule Rollup Summary");
		return module;
	}

	public static FacilioModule getAssetRollupSummaryModule() {
		FacilioModule module = new FacilioModule();
		module.setName("assetRollupSummary");
		module.setTableName("Asset_Rollup_Summary");
		module.setDisplayName("Asset Rollup Summary");
		return module;
	}
	
	public static FacilioModule getHazardModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.HAZARD);
		module.setDisplayName("Hazard");
		module.setTableName("Hazard");
		return module;
	}
	public static FacilioModule getPrecautionModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.PRECAUTION);
		module.setDisplayName("Precaution");
		module.setTableName("Precaution");
		return module;
	}
	public static FacilioModule getSafetyPlanModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.SAFETY_PLAN);
		module.setDisplayName("Safety Plan");
		module.setTableName("Safety_Plan");
		return module;
	}
	public static FacilioModule getClientModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.CLIENT);
		module.setDisplayName("Client");
		module.setTableName("Clients");
		return module;
	}
	public static FacilioModule getFeedbackKioskModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ModuleNames.FEEDBACK_KIOSK);
		module.setDisplayName("Feedback Kiosk");
		module.setTableName("Feedback_Kiosk");
		return module;
	}
	public static FacilioModule getFeedbackTypeModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ModuleNames.FEEDBACK_TYPE);
		module.setDisplayName("Feedback Type");
		module.setTableName("Feedback_Type");
		return module;
	}
	public static FacilioModule getFeedbackTypeCatalogMappingModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ModuleNames.FEEDBACK_TYPE_CATALOG_MAPPING);
		module.setDisplayName("Feedback Type Catalog mapping");
		module.setTableName("Feedback_Type_Catalog_Mapping");
		return module;
	}
		
	public static FacilioModule getFloorPlanModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.FLOOR_PLAN);
		module.setDisplayName("Floor Plan");
		module.setTableName("Floor_Plan");
		return module;
	}
	public static FacilioModule getFloorPlanObjectModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.FLOORPLAN_OBJECT);
		module.setDisplayName("Floor Plan Object");
		module.setTableName("Floorplan_Objects");
		return module;
	}

	public static FacilioModule getBannerModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.FACILIO_BANNER);
		module.setDisplayName("Facilio Banner");
		module.setTableName("Facilio_Banner");
		return module;
	}
	
	public static FacilioModule getIndoorFloorPlanModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN);
		module.setDisplayName("Indoor Floor Plan");
		module.setTableName("Indoor_Floor_Plan");
		return module;
	}
	
	public static FacilioModule getIndoorFloorPlanObjectModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN_OBJECTS);
		module.setDisplayName("Indoor Floor Plan Object");
		module.setTableName("Indoor_Floorplan_Objects");
		return module;
	}

	public static FacilioModule getSiteModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.SITE);
		module.setDisplayName("Site");
		module.setTableName("Site");
		module.setExtendModule(getResourceModule());
		module.setType(ModuleType.BASE_ENTITY);
		return module;
	}
	public static FacilioModule getRecommendedRuleModule() {
		FacilioModule module = new FacilioModule();
		module.setTableName("RecommendedRule");
		module.setDisplayName("recommended rule");
		module.setName("recommended rule ids");
		return module;
    }

	public static FacilioModule getBuildingModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.BUILDING);
		module.setDisplayName("Building");
		module.setTableName("Building");
		module.setExtendModule(getResourceModule());
		return module;
	}

	public static FacilioModule getTenantContactModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.TENANT_CONTACT);
		module.setDisplayName("Tenant Contact");
		module.setTableName("Tenant_Contacts");
		module.setExtendModule(getPeopleModule());
		return module;
	}

	public static FacilioModule getFailureCodeModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.FAILURE_CODE);
		module.setDisplayName("Failure Code");
		module.setTableName("Failure_Code");
		return module;
	}

	public static FacilioModule getFailureClassModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.FAILURE_CLASS);
		module.setDisplayName("Failure Class");
		module.setTableName("Failure_Class");
		return module;
	}

	public static FacilioModule getWorkOrderFailureClassRelModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.WORKORDER_FAILURE_CLASS_RELATIONSHIP);
		module.setDisplayName("Failure Report");
		module.setTableName("WorkOrder_FailureClass_Relationship");
		return module;
	}

	public static FacilioModule getFailureCodeProblemModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.FAILURE_CODE_PROBLEMS);
		module.setDisplayName("Failure Code Problem");
		module.setTableName("Failure_Code_Problems");
		return module;
	}

	public static FacilioModule getFailureCodeCausesModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.FAILURE_CODE_CAUSES);
		module.setDisplayName("Failure Code Causes");
		module.setTableName("Failure_Code_Causes");
		return module;
	}

	public static FacilioModule getFailureCodeRemediesModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.FAILURE_CODE_REMEDIES);
		module.setDisplayName("Failure Code Remedies");
		module.setTableName("Failure_Code_Remedies");
		return module;
	}

	public static FacilioModule getVendorContactModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.VENDOR_CONTACT);
		module.setDisplayName("Vendor Contact");
		module.setTableName("Vendor_Contacts");
		module.setExtendModule(getPeopleModule());
		return module;
	}

	public static FacilioModule getClientContactModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.CLIENT_CONTACT);
		module.setDisplayName("Client Contact");
		module.setTableName("Client_Contacts");
		module.setExtendModule(getPeopleModule());
		return module;
	}


	public static FacilioModule getFloorModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ContextNames.FLOOR);
		module.setDisplayName("Floor");
		module.setTableName("Floor");
		module.setExtendModule(getResourceModule());
		return module;
	}
	public static FacilioModule getTemplateModule () {
		FacilioModule module = new FacilioModule();
		module.setName("readingtemplate");
		module.setDisplayName("templateRuleModule");
		
		return module;
	}

	public static FacilioModule getSmartControlKioskModule() {
		FacilioModule module = new FacilioModule();
		module.setName(FacilioConstants.ModuleNames.SMART_CONTROL_KIOSK);
		module.setDisplayName("Smart control Kiosk");
		module.setTableName("Smart_Control_Kiosk");
		return module;
	}
	
	public static FacilioModule getCommissioningLogModule() {
		FacilioModule module = new FacilioModule();
		module.setName("commissioninglog");
		module.setDisplayName("Commissioning Log");
		module.setTableName("CommissioningLog");
		return module;
	}
	
	public static FacilioModule getCommissioningLogControllerModule() {
		FacilioModule module = new FacilioModule();
		module.setName("commissioninglogcontroller");
		module.setDisplayName("Commissioning Log Controller");
		module.setTableName("CommissioningLogController");
		return module;
	}

	public static FacilioModule getPeopleModule() {
		FacilioModule peopleModule = new FacilioModule();
		peopleModule.setName(FacilioConstants.ContextNames.PEOPLE);
		peopleModule.setDisplayName("People");
		peopleModule.setTableName("People");

		return peopleModule;
	}

	public static FacilioModule getBimIntegrationLogsModule(){
		FacilioModule module = new FacilioModule();
		module.setName("bimIntegrationLogs");
		module.setDisplayName("Bim Integration Logs");
		module.setTableName("Bim_Integration_Logs");
		return module;
	}

	public static FacilioModule getBimImportProcessMappingModule(){
		FacilioModule module = new FacilioModule();
		module.setName("bimImportProcessMapping");
		module.setDisplayName("Bim Import Process Mapping");
		module.setTableName("Bim_Import_Process_Mapping");
		return module;
	}

	public static FacilioModule getBimDefaultValuesModule(){
		FacilioModule module = new FacilioModule();
		module.setName("bimDefaultValues");
		module.setDisplayName("Bim Default Values");
		module.setTableName("Bim_Default_Values");
		return module;
	}
	public static FacilioModule getBaseSpaceModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.BASE_SPACE);
		module.setDisplayName("BaseSpace");
		module.setTableName("BaseSpace");
		module.setExtendModule(getResourceModule());
		return module;
	}

	public static FacilioModule getOperationAlarmsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("operationalarm");
		module.setTableName("Operation_Alarm");
		module.setDisplayName("Operation Alarm");
		return module;
	}
	public static FacilioModule getOperationAlarmsOccurenceModule() {
		FacilioModule module = new FacilioModule();
		module.setName("operationalarmoccurrence");
		module.setTableName("Operation_Alarm_Occurrence");
		module.setDisplayName("Operation Alarm");
		return module;
	}
	public static FacilioModule getOperationEventModule() {
		FacilioModule module = new FacilioModule();
		module.setName("operationevent");
		module.setTableName("Operation_Event");
		module.setDisplayName("Operation Alarm");
		return module;
	}
	public static FacilioModule getOperationAlarmHistoricalLogsModule() {
		FacilioModule operationAlarmHistoricalLogsModule = new FacilioModule();
		operationAlarmHistoricalLogsModule.setName("operationAlarmHistoricalLogsModule");
		operationAlarmHistoricalLogsModule.setDisplayName("Operation Alarm Historical Logs Module");
		operationAlarmHistoricalLogsModule.setTableName("Operation_Alarm_Historical_Logs");
		return operationAlarmHistoricalLogsModule;
	}
	public static FacilioModule getSpaceModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.SPACE);
		module.setDisplayName("Space");
		module.setTableName("BaseSpace");
		module.setExtendModule(getBaseSpaceModule());
		return module;
	}

	public static FacilioModule getTenantUnitSpaceModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.TENANT_UNIT_SPACE);
		module.setDisplayName("Tenant Unit Space");
		module.setTableName("Tenant_Unit_Space");
		module.setExtendModule(getSpaceModule());
		return module;
	}

	public static FacilioModule getModbusImportModule() {
		FacilioModule module = new FacilioModule();
		module.setName("modbus import module");
		module.setDisplayName("Modbus Import Module");
		module.setTableName("Modbus_Import");
		return module;
	}

	public static FacilioModule getEmployeeModule() {
		FacilioModule module = new FacilioModule();
		module.setName(ContextNames.EMPLOYEE);
		module.setDisplayName("Employee");
		module.setTableName("Employee");
		return module;
	}

	public static FacilioModule getAgentThreadDumpModule() {
		FacilioModule module = new FacilioModule();
		module.setName("agentThreadDumpModule");
		module.setTableName("Agent_Thread_Dump");
		module.setDisplayName("AgentThreadDump");
		return module;
	}

	public static FacilioModule getScopingConfigModule() {
		FacilioModule module = new FacilioModule();
		module.setName("scopingconfig");
		module.setTableName("Scoping_Config");
		module.setDisplayName("Scoping Config");
		return module;
	}

	public static FacilioModule getScopingModule() {
		FacilioModule module = new FacilioModule();
		module.setName("scoping");
		module.setTableName("Scoping");
		module.setDisplayName("Scoping");
		return module;
	}

	public static FacilioModule getPeopleUserScopingModule() {
		FacilioModule module = new FacilioModule();
		module.setName("peopleuserscoping");
		module.setTableName("People_Userscoping");
		module.setDisplayName("People User Scoping");
		return module;
	}
	public static FacilioModule getFieldModulePermissionModule() {
		FacilioModule module = new FacilioModule();
		module.setName("fieldModulePermission");
		module.setTableName("Field_Module_Permission");
		module.setDisplayName("Field Module Permission");
		return module;
	}

	public static FacilioModule getAgentStatsModule() {
		FacilioModule facilioModule = new FacilioModule();
		facilioModule.setName(AgentConstants.STATS_MODULE_NAME);
		facilioModule.setTableName("Agent_Stats");
		facilioModule.setDisplayName("AgentStats");
		return facilioModule;
	}

	public static FacilioModule getSystemPointModule() {
		FacilioModule facilioModule = new FacilioModule();
		facilioModule.setTableName(AgentConstants.SYSTEM_POINT_MODULE_NAME);
		facilioModule.setTableName("System_Point");
		facilioModule.setDisplayName("System Point");
		facilioModule.setExtendModule(getPointModule());
		return facilioModule;
	}

    public static FacilioModule getQuotationModule() {
        FacilioModule module = new FacilioModule();
        module.setName("quote");
        module.setDisplayName("Quote");
        module.setTableName("Quotation");
        return module;
    }

	public static FacilioModule getTaxModule() {
		FacilioModule module = new FacilioModule();
		module.setName("tax");
		module.setDisplayName("Tax");
		module.setTableName("Tax");
		return module;
	}

	public static FacilioModule getQuotationTermsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("quoteterms");
		module.setDisplayName("Quotation Associated Terms");
		module.setTableName("QuotationAssociatedTerms");
		return module;
	}

	public static FacilioModule getPOTermsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("poterms");
		module.setDisplayName("PO Associated Terms");
		module.setTableName("PO_Associated_Terms");
		return module;
	}

	public static FacilioModule getPRTermsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("prterms");
		module.setDisplayName("PR Associated Terms");
		module.setTableName("PR_Associated_Terms");
		return module;
	}

	public static FacilioModule getAnnouncementModule() {
		FacilioModule module = new FacilioModule();
		module.setName("announcement");
		module.setDisplayName("Announcements");
		module.setTableName("Announcements");
		return module;
	}

	public static FacilioModule getAudienceModule() {
		FacilioModule module = new FacilioModule();
		module.setName("audience");
		module.setDisplayName("Audience");
		module.setTableName("Sharing_Audience");
		return module;
	}

	public static FacilioModule getPeopleAnnouncementModule() {
		FacilioModule module = new FacilioModule();
		module.setName("peopleannouncement");
		module.setDisplayName("People Announcements");
		module.setTableName("People_Announcements");
		return module;
	}

	public static FacilioModule getInboundConnectionsModule(){
		FacilioModule module = new FacilioModule();
		module.setName("inboundConnections");
		module.setDisplayName("Inbound Connections");
		module.setTableName("Inbound_Connections");
		return module;
	}

	public static FacilioModule getWorkorderCostModule() {
		FacilioModule module=new FacilioModule();
		module.setTableName("Workorder_cost");
		module.setDisplayName("Workorder Cost");
		module.setName("workorderCost");
		return module;
	}

	
	public static FacilioModule getDashboardFilterModule()
	{	
		FacilioModule module = new FacilioModule();
		module.setName("dashboardFilter");
		module.setDisplayName("Dashboard Filter");
		module.setTableName("Dashboard_Filter");
		return module;
		
	}

	public static FacilioModule getESSyncContextModule() {
		FacilioModule module = new FacilioModule();
		module.setName("syncToES");
		module.setDisplayName("Sync To ES");
		module.setTableName("Sync_To_ES");
		return module;
	}
	
	public static FacilioModule getDashboardUserFilterModule()
	{	
		FacilioModule module = new FacilioModule();
		module.setName("dashboardUserFilter");
		module.setDisplayName("Dashboard User Filter");
		module.setTableName("Dashboard_User_Filter");
		return module;
		
	}
	public static FacilioModule getDashboardUserFilterWidgetFieldMappingModule()
	{	
		FacilioModule module = new FacilioModule();
		module.setName("dashboardUserFilterWidgetFieldMapping");
		module.setDisplayName("Dashboard user filter widget field mapping");
		module.setTableName("Dashboard_User_Filter_Widget_Field_Mapping");
		return module;
		
	}

	public static FacilioModule getNewsAndInformationModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.Tenant.NEWS_AND_INFORMATION);
		module.setDisplayName("News and Information");
		module.setTableName("NewsAndInformation");
		return module;
	}
	public static FacilioModule getDealsAndOffersModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.Tenant.DEALS_AND_OFFERS);
		module.setDisplayName("Deals and Offers");
		module.setTableName("DealsAndOffers");
		return module;
	}
	public static FacilioModule getNeighbourhoodModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.Tenant.NEIGHBOURHOOD);
		module.setDisplayName("Neighbourhood");
		module.setTableName("Neighbourhood");
		return module;
	}
	public static FacilioModule getContactDirectoryModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.Tenant.CONTACT_DIRECTORY);
		module.setDisplayName("Contact Directory");
		module.setTableName("ContactDirectory");
		return module;
	}
	public static FacilioModule getAdminDocumentsModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.Tenant.ADMIN_DOCUMENTS);
		module.setDisplayName("Admin Documents");
		module.setTableName("AdminDocuments");
		return module;
	}
	public static FacilioModule getAgentPreProcessorModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName("agentPreProcessor");
		module.setDisplayName("Agent Pre Processor");
		module.setTableName("AGENT_PRE_PROCESSOR");
		return module;
	}
	
	public static FacilioModule getAgentProcessorModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName("agentProcessor");
		module.setDisplayName("Agent Processor");
		module.setTableName("AGENT_PROCESSOR");
		return module;
	}
	
	public static FacilioModule getAgentControlModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName("agentControl");
		module.setDisplayName("Agent Control");
		module.setTableName("AgentControl");
		return module;
	}

	public  static FacilioModule getUserNotificationSeenMapping() {
		FacilioModule seenMapping = new FacilioModule();
		seenMapping.setName("userNotificationSeenMapping");
		seenMapping.setDisplayName("User Notification Seen Mapping");
		seenMapping.setTableName("User_Notification_Seen_Mapping");
		return seenMapping;
	}
	
	public static FacilioModule getOrgWeatherStationModule() {
		FacilioModule module = new FacilioModule();
		module.setName("org weather station");
		module.setDisplayName("Org Weather Station");
		module.setTableName("Org_Weather_Station");
		return module;
	}

	public static FacilioModule getLiveSessionModule() {
		FacilioModule module = new FacilioModule();
		module.setName("live_session");
		module.setDisplayName("Live Session");
		module.setTableName("Live_Sessions");
		return module;
	}

	public static FacilioModule getTransactionRuleModule() {
		FacilioModule module = new FacilioModule();
		module.setName("transaction_rule");
		module.setDisplayName("Transaction Rule");
		module.setTableName("Transaction_Rule_Config");
		return module;
	}

    public static FacilioModule getAgentFileLogModule() {
        FacilioModule module = new FacilioModule();
        module.setName("agentFileLog");
        module.setDisplayName("Agent File Log");
        module.setTableName("Agent_Files_Update_Log");
        return module;
    }

	public static FacilioModule getBudgetModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.Budget.BUDGET);
		module.setDisplayName("Budget");
		module.setTableName("Budget");
		return module;
	}
	public static FacilioModule getTransferRequestModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.TRANSFER_REQUEST);
		module.setDisplayName("Transfer Request");
		module.setTableName("Transfer_Request");
		return module;
	}
	public static FacilioModule getTransferRequestShipmentModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.TRANSFER_REQUEST_SHIPMENT);
		module.setDisplayName("Transfer Request Shipment");
		module.setTableName("Transfer_Request_Shipment");
		return module;
	}
	public static FacilioModule getRequestForQuotationModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.REQUEST_FOR_QUOTATION);
		module.setDisplayName("Request For Quotation");
		module.setTableName("Request_For_Quotation");
		return module;
	}

	public static FacilioModule getTransactionModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.TRANSACTION);
		module.setDisplayName("Budget Transaction");
		module.setTableName("transactions");
		return module;
	}

	public static FacilioModule getVendorQuotesModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.VENDOR_QUOTES);
		module.setDisplayName("Vendor Quotes");
		module.setTableName("Vendor_Quotes");
		return module;
	}
	public static FacilioModule getTriggerModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName("trigger");
		module.setDisplayName("Trigger");
		module.setTableName("Facilio_Trigger");
		return module;
	}
	
	public static FacilioModule getTriggerActionModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName("triggerAction");
		module.setDisplayName("Trigger Action");
		module.setTableName("Trigger_Action");
		return module;
	}
	
//	public static FacilioModule getTriggerActionRelModule()
//	{
//		FacilioModule module=new FacilioModule();
//		module.setName("triggerActionRel");
//		module.setDisplayName("Trigger_Action_Rel");
//		module.setTableName("Trigger_Action_Rel");
//		return module;
//	}
	
	public static FacilioModule getTriggerLogModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName("triggerLog");
		module.setDisplayName("Trigger_Log");
		module.setTableName("Trigger_Log");
		return module;
	}

	public static FacilioModule getColorPaletteModule(){
		FacilioModule module=new FacilioModule();
		module.setName("colourPalette");
		module.setDisplayName("Color_Palette");
		module.setTableName("Color_Palette");
		return module;
	}

	public static FacilioModule getTriggerInclExclModule() {
		FacilioModule module = new FacilioModule();
		module.setName("triggerInclExcl");
		module.setDisplayName("Trgiger incl excl");
		module.setTableName("Trigger_Include_Exclude_Resource");
		return module;
	}
	
	public static FacilioModule getControlScheduleVsExceptionModule() {
		FacilioModule module = new FacilioModule();
		module.setName("controlScheduleVsException");
		module.setDisplayName("ControlScheduleVsException");
		module.setTableName("Control_Schedule_vs_Exception");
		return module;
	}

	public static FacilioModule getFacilityBookingModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.FacilityBooking.FACILITY_BOOKING);
		module.setDisplayName("Facility Booking");
		module.setTableName("FacilityBooking");
		return module;
	}

	public static FacilioModule getFacilityModule()
	{
		FacilioModule module=new FacilioModule();
		module.setName(ContextNames.FacilityBooking.FACILITY);
		module.setDisplayName("Facility");
		module.setTableName("Facility");
		return module;
	}

	public static FacilioModule getPMJobPlanV3Module() {
		FacilioModule pmJobPlan = new FacilioModule();
		pmJobPlan.setName("pmJobPlan");
		pmJobPlan.setDisplayName("PM Job Plans");
		pmJobPlan.setTableName("PM_Job_Plans");
		return pmJobPlan;
	}

	public static FacilioModule getPMJobPlanTriggersV3Module() {
		FacilioModule pmJobPlanTriggers = new FacilioModule();
		pmJobPlanTriggers.setName("pmJobPlanTriggers");
		pmJobPlanTriggers.setDisplayName("PM Job Plan Triggers");
		pmJobPlanTriggers.setTableName("PM_Job_Plans_Triggers");
		return pmJobPlanTriggers;
	}

	public static FacilioModule getFacilioAuditModule() {
		FacilioModule facilioAuditModule = new FacilioModule();
		facilioAuditModule.setName("facilioAudit");
		facilioAuditModule.setDisplayName("Facilio Audit");
		facilioAuditModule.setTableName("FacilioAudit");
		return facilioAuditModule;
	}

    public static FacilioModule getAgentTriggerModule() {
        FacilioModule module = new FacilioModule();
        module.setName("agentTrigger");
        module.setDisplayName("Agent Trigger");
		module.setTableName("Agent_Trigger");
        module.setExtendModule(getTriggerModule());
        return module;
    }

	public static FacilioModule getScatterGraphLineModule() {
		FacilioModule facilioScatterGraphModule = new FacilioModule();
		facilioScatterGraphModule.setName("facilioScatterGraph");
		facilioScatterGraphModule.setDisplayName("Scatter Graph Line");
		facilioScatterGraphModule.setTableName("SCATTER_GRAPH_LINE");
		return facilioScatterGraphModule;
	}
	
	//	Bundle Related Modules starts
	
	public static FacilioModule getInstalledBundleModule() {
        FacilioModule module = new FacilioModule();
        module.setName("installedBundle");
        module.setDisplayName("Installed Bundle");
		module.setTableName("Bundle_Installed");
        return module;
    }
    
	public static FacilioModule getBundleModule() {
        FacilioModule module = new FacilioModule();
        module.setName("bundle");
        module.setDisplayName("Bundle");
		module.setTableName("Bundle");
        return module;
    }
    
    public static FacilioModule getBundleChangeSetModule() {
        FacilioModule module = new FacilioModule();
        module.setName("bundleChangeSet");
        module.setDisplayName("Bundle Change Set");
		module.setTableName("Bundle_Change_Set");
        return module;
    }
    
    public static FacilioModule getSandboxModule() {
        FacilioModule module = new FacilioModule();
        module.setName("sandbox");
        module.setDisplayName("Sandbox");
		module.setTableName("Sandbox");
        return module;
    }

	public static FacilioModule getFacilioSandboxModule() {
		FacilioModule module = new FacilioModule();
		module.setName("facilioSandbox");
		module.setDisplayName("FacilioSandbox");
		module.setTableName("Facilio_Sandbox");
		return module;
	}

	public static FacilioModule getSandboxSharingModule() {
		FacilioModule sandboxSharing = new FacilioModule();
		sandboxSharing.setName("sandboxSharing");
		sandboxSharing.setDisplayName("Sandbox Sharing");
		sandboxSharing.setTableName("Sandbox_Sharing");
		return sandboxSharing;
	}

	public static FacilioModule getPackageModule() {
		FacilioModule sandboxSharing = new FacilioModule();
		sandboxSharing.setName("package");
		sandboxSharing.setDisplayName("Package");
		sandboxSharing.setTableName("Package");
		return sandboxSharing;
	}

	public static FacilioModule getPackageChangesetsModule() {
		FacilioModule sandboxSharing = new FacilioModule();
		sandboxSharing.setName("packageChangeset");
		sandboxSharing.setDisplayName("Package Changesets");
		sandboxSharing.setTableName("Package_Changesets");
		return sandboxSharing;
	}

	public static FacilioModule getRuleAlarmDetailsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("alarm_details");
		module.setDisplayName("New Reading Rule Alarm Details");
		module.setTableName("New_Reading_Rule_AlarmDetails");
		return module;
	}
	
	public static FacilioModule getQAndADisplayLogicModule() {
		FacilioModule module = new FacilioModule();
		module.setName("qAndADisplayLogic");
		module.setTableName("Q_And_A_Display_Logic");
		return module;
	}
	
	public static FacilioModule getQAndADisplayLogicTriggerQuestionModule() {
		FacilioModule module = new FacilioModule();
		module.setName("qAndADisplayLogic");
		module.setTableName("Q_And_A_Display_Logic_Trigger_Questions");
		return module;
	}
	
	public static FacilioModule getQAndADisplayLogicActionModule() {
		FacilioModule module = new FacilioModule();
		module.setName("qAndADisplayLogic");
		module.setTableName("Q_And_A_Display_Logic_Action");
		return module;
	}

	public  static FacilioModule getHomePageFactoryModule() {
		FacilioModule module = new FacilioModule();
		module.setName("homePage");
		module.setTableName("Home_Page");
		return module;
	}

	public static FacilioModule getLicensingInfoModule() {

		FacilioModule licensingInfoModule = new FacilioModule();

		licensingInfoModule.setName(ContextNames.LICENSING_INFO);

		licensingInfoModule.setDisplayName("LICENSING INFO");

		licensingInfoModule.setTableName("LicensingInfo");

		return licensingInfoModule;

	}
	//Bundle Related Modules ends

    public static FacilioModule getAutoCAD_Import(){
		FacilioModule autoCADModule = new FacilioModule();
		autoCADModule.setName("autocadimport");
		autoCADModule.setDisplayName("AutoCAD File Import");
		autoCADModule.setTableName("AutoCAD_Import");
		return autoCADModule;
	}

	public static FacilioModule getAutoCAD_Import_Layers(){
		FacilioModule autoCADLayerModule = new FacilioModule();
		autoCADLayerModule.setName("autoCadImportLayer");
		autoCADLayerModule.setDisplayName("AutoCAD Import Layer");
		autoCADLayerModule.setTableName("AutoCAD_Import_Layers");
		return autoCADLayerModule;
	}


	public static FacilioModule getReadingImportAPPModule() {
		FacilioModule readingImportModule = new FacilioModule();
		readingImportModule.setName(ContextNames.READING_IMPORT_APP);
		readingImportModule.setDisplayName("Reading Import APP");
		readingImportModule.setTableName("Reading_Import_APP");
		return readingImportModule;
	}

	public static FacilioModule getMailMapperModule() {
		FacilioModule mailMapperModule = new FacilioModule();
		mailMapperModule.setName("outgoingMailMapper");
		mailMapperModule.setDisplayName("OutgoingMail Mapper");
		mailMapperModule.setTableName("Outgoing_Mail_Mapper");
		return mailMapperModule;
	}

	public static FacilioModule getMailResponseModule() {
		FacilioModule mailMapperModule = new FacilioModule();
		mailMapperModule.setName("outgoingMailResponses");
		mailMapperModule.setDisplayName("Outgoing Mail Responses");
		mailMapperModule.setTableName("Outgoing_Mail_Responses");
		return mailMapperModule;
	}

	public static FacilioModule getMisccontrolerModule() {
		FacilioModule miscController = new FacilioModule();
		miscController.setName("misccontroller");
		miscController.setDisplayName("Misc Controller");
		miscController.setTableName("Misc_Controller");
		return miscController;
	}
	public static FacilioModule getBacnetipcontrollerModule() {
		FacilioModule bacnetController = new FacilioModule();
		bacnetController.setName("bacnetipcontroller");
		bacnetController.setDisplayName("BACnetip Controller");
		bacnetController.setTableName("BACnet_IP_Controller");
		return bacnetController;
	}
//	public static FacilioModule getBacnetmstControllerModule() {
//		FacilioModule bacnetMstController = new FacilioModule();
//		bacnetMstController.setName("bacnetmstpcontroller");
//		bacnetMstController.setDisplayName("BACnetmst Controller");
//		bacnetMstController.setTableName("BACnet_Mst_Controller");
//		return bacnetMstController;
//	}
	public static FacilioModule getNiagaraControllerModule() {
		FacilioModule niagaraController = new FacilioModule();
		niagaraController.setName("niagaracontroller");
		niagaraController.setDisplayName("Niagara Controller");
		niagaraController.setTableName("Niagra_Controller");
		return niagaraController;
	}
	public static FacilioModule getModbusTcpControllerModule() {
		FacilioModule modbusTcpController = new FacilioModule();
		modbusTcpController.setName("modbustcpcontroller");
		modbusTcpController.setDisplayName("Modbus Tcp Controller");
		modbusTcpController.setTableName("Modbus_Tcp_Controller");
		return modbusTcpController;
	}
	public static FacilioModule getModbusRtuControllerModule() {
		FacilioModule modbusRtu = new FacilioModule();
		modbusRtu.setName("modbusrtucontroller");
		modbusRtu.setDisplayName("Modbus Rtu Controller");
		modbusRtu.setTableName("Modbus_Rtu_Controller");
		return modbusRtu;
	}
	public static FacilioModule getOpcUaControllerModule() {
		FacilioModule opcUA_controller = new FacilioModule();
		opcUA_controller.setName("opcuacontroller");
		opcUA_controller.setDisplayName("Opc UA Controller");
		opcUA_controller.setTableName("OpcUA_Controller");
		return opcUA_controller;
	}
	public static FacilioModule getOpcXmlDaControllerModule() {
		FacilioModule opcXMLDA_controller = new FacilioModule();
		opcXMLDA_controller.setName("opcxmldacontroller");
		opcXMLDA_controller.setDisplayName("Opc XML DA Controller");
		opcXMLDA_controller.setTableName("OpcXMLDA_Controller");
		return opcXMLDA_controller;
	}
	public static FacilioModule getLonWorksontrollerModule() {
		FacilioModule lonWorksController = new FacilioModule();
		lonWorksController.setName("lonworkscontroller");
		lonWorksController.setDisplayName("Lon WorksController");
		lonWorksController.setTableName("LonWorks_Controller");
		return lonWorksController;
	}
//	public static FacilioModule getKnxControllerModule() {
//		FacilioModule misccontroller = new FacilioModule();
//		misccontroller.setName("knxcontroller");
//		misccontroller.setDisplayName("Knx Controller");
//		misccontroller.setTableName("BACnet_IP_Controller");
//		return misccontroller;
//	}
	public static FacilioModule getCustomControllerModule() {
		FacilioModule customController = new FacilioModule();
		customController.setName("customController");
		customController.setDisplayName("Custom Controller");
		customController.setTableName("Custom_Controller");
		return customController;
	}
	public static FacilioModule getRestControllerModule() {
		FacilioModule restController = new FacilioModule();
		restController.setName("restcontroller");
		restController.setDisplayName("Rest Controller");
		restController.setTableName("Rest_Controller");
		return restController;
	}
	public static FacilioModule getSystemControllerModule() {
		FacilioModule systemController = new FacilioModule();
		systemController.setName("systemController");
		systemController.setDisplayName("System Controller");
		systemController.setTableName("System_Controller");
		return systemController;
	}
	public static FacilioModule getRdmControllerModule() {
		FacilioModule RDM_Controller = new FacilioModule();
		RDM_Controller.setName("rdmcontroller");
		RDM_Controller.setDisplayName("RDM Controller");
		RDM_Controller.setTableName("RDM_Controller");
		return RDM_Controller;
	}

public static FacilioModule getSpaceBookingFormRelationModule(){
		FacilioModule spaceBookingFormRelationModule = new FacilioModule();
		spaceBookingFormRelationModule.setName(ContextNames.SpaceCategoryFormRelation.SPACE_CATEGORY_FORM_RELATION);
		spaceBookingFormRelationModule.setDisplayName("Space Category Form Relation");
		spaceBookingFormRelationModule.setTableName("Space_Category_Form_Relation");
		return spaceBookingFormRelationModule;
	}

	public static FacilioModule getCommentsSharingModule() {
		FacilioModule commentsSharing = new FacilioModule();
		commentsSharing.setName("commentsSharing");
		commentsSharing.setDisplayName("Comments Sharing");
		commentsSharing.setTableName("Comments_Sharing");
		return commentsSharing;
	}

	public static FacilioModule getCommentsSharingPreferenceModule() {
		FacilioModule commentsSharing = new FacilioModule();
		commentsSharing.setName("commentsSharingPreference");
		commentsSharing.setDisplayName("Comments Sharing Preference");
		commentsSharing.setTableName("Comments_Sharing_Preferences");
		return commentsSharing;
	}

	public static FacilioModule getCommentMentionsModule() {
		FacilioModule commentMentions = new FacilioModule();
		commentMentions.setName("commentMentions");
		commentMentions.setDisplayName("Comment Mentions");
		commentMentions.setTableName("Comment_Mentions");
		return commentMentions;
	}


	public static FacilioModule getPeopleNotificationSettingsModule() {
		FacilioModule peopleNotificationSettings = new FacilioModule();
		peopleNotificationSettings.setName("peopleNotificationSettings");
		peopleNotificationSettings.setDisplayName("People Notification Settings");
		peopleNotificationSettings.setTableName("People_Notification_Settings");
		return peopleNotificationSettings;
	}

	public static FacilioModule getValueGeneratorModule(){
		FacilioModule valueGenModule = new FacilioModule();
		valueGenModule.setName("valueGenerator");
		valueGenModule.setDisplayName("Value Generator");
		valueGenModule.setTableName("Value_Generators");
		return valueGenModule;
	}

	public static FacilioModule getGlobalScopeVariableModule(){
		FacilioModule scopeVariableModule = new FacilioModule();
		scopeVariableModule.setName("globalscopeVariable");
		scopeVariableModule.setDisplayName("Global Scope Variable");
		scopeVariableModule.setTableName("Global_Scope_Variable");
		return scopeVariableModule;
	}

	public static FacilioModule getGlobalScopeVariableModulesFieldsModule(){
		FacilioModule scopeVariableModulesFieldsModule = new FacilioModule();
		scopeVariableModulesFieldsModule.setName("globalscopeVariableModulesFields");
		scopeVariableModulesFieldsModule.setDisplayName("Global Scope Variable Modules Fields");
		scopeVariableModulesFieldsModule.setTableName("Global_Scope_Variable_Modules_Fields");
		return scopeVariableModulesFieldsModule;
	}

	public static FacilioModule getCustomPageWidgetModule() {
		FacilioModule module = new FacilioModule();
		module.setName("customPageWidget");
		module.setDisplayName("CustomPageWidget");
		module.setTableName("Custom_Page_Widget");
		return module;
	}
	public static FacilioModule getSummaryWidgetGroupModule() {
		FacilioModule module = new FacilioModule();
		module.setName("summaryWidgetGroup");
		module.setDisplayName("SummaryWidgetGroup");
		module.setTableName("Summary_Widget_Group");
		return module;
	}
	public static FacilioModule getSummaryWidgetGroupFieldsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("summaryWidgetGroupFields");
		module.setDisplayName("SummaryWidgetGroupFields");
		module.setTableName("Summary_Widget_Group_Fields");
		return module;
	}

	public static FacilioModule getE2ControllerModule() {
		FacilioModule module = new FacilioModule();
		module.setName("e2controller");
		module.setDisplayName("E2 Controller");
		module.setTableName("E2_Controller");
		module.setExtendModule(getControllerModule());
		return module;
	}

	public static FacilioModule getShiftPeopleRelPseudoModule() {
		FacilioModule shiftUserRel = new FacilioModule();
		shiftUserRel.setName("shiftPeopleRel");
		shiftUserRel.setDisplayName("Shift People Rel");
		shiftUserRel.setTableName("Shift_People_Rel");
		return shiftUserRel;
	}

	public static FacilioModule getCurrencyModule() {
		FacilioModule currencyModule = new FacilioModule();
		currencyModule.setName("currencies");
		currencyModule.setDisplayName("Currencies");
		currencyModule.setTableName("Currencies");
		return currencyModule;
	}

	public static FacilioModule getDataMigrationStatusModule() {
		FacilioModule currencyModule = new FacilioModule();
		currencyModule.setName("dataMigrationStatus");
		currencyModule.setDisplayName("Data Migration Status");
		currencyModule.setTableName("Data_Migration_Status");
		return currencyModule;
	}

	public static FacilioModule getDataMigrationMappingModule() {
		FacilioModule currencyModule = new FacilioModule();
		currencyModule.setName("dataMigrationMapping");
		currencyModule.setDisplayName("Data Migration Mapping");
		currencyModule.setTableName("Data_Migration_Mapping");
		return currencyModule;
	}

	public static FacilioModule getConnectedAppDeploymentsModule() {
		FacilioModule connectedAppDeployments = new FacilioModule();
		connectedAppDeployments.setName("connectedAppDeployments");
		connectedAppDeployments.setDisplayName("ConnectedApp Deployments");
		connectedAppDeployments.setTableName("ConnectedApp_Deployments");
		return connectedAppDeployments;
	}

	public static FacilioModule getConnectedAppFilesModule() {
		FacilioModule connectedAppFiles = new FacilioModule();
		connectedAppFiles.setName("connectedAppFiles");
		connectedAppFiles.setDisplayName("ConnectedApp Files");
		connectedAppFiles.setTableName("ConnectedApp_Files");
		return connectedAppFiles;
	}


	public static FacilioModule getCalendarViewModule() {
		FacilioModule calendarView = new FacilioModule();
		calendarView.setName("calendarView");
		calendarView.setTableName("CalendarView");
		calendarView.setDisplayName("Calendar View");
		return calendarView;
	}
	public static FacilioModule getReadingRuleWorkOrderModule(){
		FacilioModule ruleWoModule = new FacilioModule();
		ruleWoModule.setName("newreadingrulewo");
		ruleWoModule.setDisplayName("NewReadingRule_WorkOrder");
		ruleWoModule.setTableName("NewReadingRule_Wo_WorkflowRule");
		return ruleWoModule;

	}

	public static FacilioModule getWoSettingModule() {
		FacilioModule woSettingModule = new FacilioModule();
		woSettingModule.setName("workordermodulesetting");
		woSettingModule.setTableName("WorkOrder_Module_Setting");
		woSettingModule.setDisplayName("WorkOrder Module Setting");
		return woSettingModule;
	}
	public static FacilioModule getWoFeatureSettingsModule() {
		FacilioModule workOrderFeatureSettingsModule = new FacilioModule();
		workOrderFeatureSettingsModule.setName("workOrderFeatureSettings");
		workOrderFeatureSettingsModule.setTableName("WorkOrderFeatureSettings");
		workOrderFeatureSettingsModule.setDisplayName("WorkOrder Feature Settings");
		return workOrderFeatureSettingsModule;
	}
	public static FacilioModule getPagesModule() {
		FacilioModule module = new FacilioModule();
		module.setName("pages");
		module.setDisplayName("Pages");
		module.setTableName("Pages");
		return module;
	}

	public static FacilioModule getTemplatePageAppDomainModule() {
		return constructModule("templatePageAppDomain", "TemplatePage AppDomain", "TemplatePage_AppDomain");
	}
	public static FacilioModule getPageSharingModule(){
		return constructModule("pageSharing", "Page Sharing", "Page_Sharing");
	}
	public static FacilioModule getPageLayoutsModule() {
		return constructModule("pageLayouts", "Page Layouts", "Page_Layouts");
	}
	public static FacilioModule getPageTabsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("pageTabs");
		module.setDisplayName("Tabs");
		module.setTableName("Page_Tabs");
		return module;
	}
	public static FacilioModule getPageColumnsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("pageColumns");
		module.setDisplayName("Columns");
		module.setTableName("Page_Columns");
		return module;
	}

	public static FacilioModule getPageSectionsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("pageSections");
		module.setDisplayName("Sections");
		module.setTableName("Page_Sections");
		return module;
	}

	public static FacilioModule getPageSectionWidgetsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("pageSectionWidgets");
		module.setDisplayName("PageSectionWidgets");
		module.setTableName("Page_Section_Widgets");
		return module;
	}
	public static FacilioModule getWidgetListModule(){
		FacilioModule module = new FacilioModule();
		module.setName("widgetlist");
		module.setDisplayName("Widget List");
		module.setTableName("Widget_List");
		return module;
	}
	public static FacilioModule getPageSummaryWidgetModule() {
		FacilioModule module = new FacilioModule();
		module.setName("pageSummaryWidget");
		module.setDisplayName("Page Summary Widget");
		module.setTableName("Page_Summary_Widget");
		return module;
	}

	public static FacilioModule getPageRelatedListWidgetsModule() {
		FacilioModule module = new FacilioModule();
		module.setName("pageRelatedListWidgets");
		module.setDisplayName("Page Related List Widgets");
		module.setTableName("Page_Related_List_Widgets");
		return module;
	}

	public static FacilioModule getWidgetModuleModule(){
		FacilioModule module = new FacilioModule();
		module.setName("WidgetModule");
		module.setDisplayName("Widget Module");
		module.setTableName("Widget_Modules");
		return module;
	}
	public static FacilioModule getWidgetConfigsModule(){
		FacilioModule module = new FacilioModule();
		module.setName("widgetConfigs");
		module.setDisplayName("Widget Configs");
		module.setTableName("Widget_Configs");
		return module;
	}
	public static FacilioModule getWidgetGroupConfigModule() {
		return constructModule("widgetGroupConfig","WidgetGroup Config","WidgetGroup_Config");
	}
	public static FacilioModule getWidgetGroupSectionsModule(){
		return constructModule("widgetGroupSections","WidgetGroup Sections","WidgetGroup_Sections");
	}

	public static FacilioModule getWidgetGroupWidgetsModule(){
		return constructModule("widgetGroupWidgets","WidgetGroup Widgets","WidgetGroup_Widgets");
	}

	public static FacilioModule getPageRelationShipWidgetsModule(){
		return constructModule("pageRelationShipsWidgets", "Page Relationship Widgets","Page_Relationship_Widgets");
	}
	public static FacilioModule getAttendanceSettingsPseudoModule() {
		FacilioModule attendanceSettings = new FacilioModule();
		attendanceSettings.setName("attendanceSettings");
		attendanceSettings.setDisplayName("Attendance Settings");
		attendanceSettings.setTableName("Attendance_Settings");
		return attendanceSettings;
	}

	public static FacilioModule getOfflineRecordRegisterModule() {
		FacilioModule module = new FacilioModule();
		module.setName("offlineRecordRegister");
		module.setTableName("Offline_Record_Register");
		module.setDisplayName("Offline Record Register");
		return module;
	}
}
