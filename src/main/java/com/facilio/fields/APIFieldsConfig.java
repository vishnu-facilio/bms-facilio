package com.facilio.fields;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fields.fieldBuilder.FieldConfig;
import com.facilio.fields.util.FieldsConfigList;
import com.facilio.mailtracking.MailConstants;
import com.facilio.modules.FieldType;
import com.facilio.remotemonitoring.signup.*;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;

import java.util.Arrays;
import java.util.function.Supplier;

@Config
@SuppressWarnings("unchecked")
public class APIFieldsConfig {

    @Module(FacilioConstants.ContextNames.ASSET)
    public static Supplier<FieldConfig> getAssetFieldConfig(){
        return () -> new FieldConfig()

                .addLicenseBasedFields(AccountUtil.FeatureLicense.INVENTORY,Arrays.asList("rotatingItemType","storeRoom"))

                .sortFields()
                .add(FieldsConfigList.ASSET_FIELDS_INCLUDE)
                .done()

                .advancedFields()
                .add(FieldsConfigList.ASSET_FIELDS_INCLUDE)
                .skipOnelevelFields(FieldsConfigList.ASSET_FILTER_FIELDS_EXCLUDE)
                .done()

                .summaryWidgetFields()
                .add(FieldsConfigList.ASSET_SUMMARY_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.ASSET_VIEW_FIELDS)
                .addFixedFields(FieldsConfigList.getNameField())
                .done();
    }

    @Module(FacilioConstants.ContextNames.WORK_ORDER)
    public static Supplier<FieldConfig> getWorkorderFieldConfig(){
        return () -> new FieldConfig()
                .addLicenseBasedFields(AccountUtil.FeatureLicense.CLIENT, Arrays.asList("client"))
                .addLicenseBasedFields(AccountUtil.FeatureLicense.TENANTS, Arrays.asList("tenant"))
                .addLicenseBasedFields(AccountUtil.FeatureLicense.VENDOR, Arrays.asList("vendor"))

                .sortFields()
                .add(FieldsConfigList.WORK_ORDER_FIELDS_INCLUDE)
                .done()

                .advancedFields()
                .add(FieldsConfigList.WORK_ORDER_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.WORKORDER_VIEW_FIELDS_INCLUDE)
                .addFixedFields(FieldsConfigList.WORKORDER_VIEW_FIXED_FIELDS)
                .done();
    }

    @Module(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT)
    public static Supplier<FieldConfig> getWorkPermitFieldConfig(){
        return () -> new FieldConfig()

                .sortFields()
                .skip(FieldsConfigList.WORKPERMIT_VIEW_FIELDS_EXCLUDE)
                .done();
    }
    @Module(FacilioConstants.ContextNames.NEW_READING_ALARM)
    public static Supplier<FieldConfig> getNewReadingAlarmFieldConfig(){
        return () -> new FieldConfig()

                .sortFields()
                .add(FieldsConfigList.NEW_READING_ALARM_FIELDS_INCLUDE)
                .done()

                .advancedFields()
                .add(FieldsConfigList.NEW_READING_ALARM_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.NEW_READING_ALARM_FIELDS_INCLUDE)
                .addFixedFields(Arrays.asList("severity", "subject"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.BMS_ALARM)
    public static Supplier<FieldConfig> getBMSAlarmFieldConfig(){
        return () -> new FieldConfig()

                .sortFields()
                .add(FieldsConfigList.BMS_ALARM_FIELDS_INCLUDE)
                .done()

                .advancedFields()
                .add(FieldsConfigList.BMS_ALARM_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.BMS_ALARM_FIELDS_INCLUDE)
                .addFixedFields(Arrays.asList("severity", "subject"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.ML_ANOMALY_ALARM)
    public static Supplier<FieldConfig> getMLAnamolyAlarmFieldConfig(){
        return () -> new FieldConfig()

                .sortFields()
                .add(FieldsConfigList.NEW_ALARMS_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(Arrays.asList("subject","resource", "lastOccurredTime", "lastCreatedTime", "acknowledgedBy",
                        "acknowledged", "readingalarmcategory", "acknowledgedTime", "severity", "noOfOccurrences"))
                .addFixedSelectableFields(Arrays.asList("severity", "noOfOccurrences", "acknowledgedBy"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.OPERATION_ALARM)
    public static Supplier<FieldConfig> getOperationAlarmFieldConfig(){
        return () -> new FieldConfig()

                .sortFields()
                .add(FieldsConfigList.NEW_OP_ALARMS_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(Arrays.asList("resource", "lastOccurredTime", "lastCreatedTime", "acknowledgedBy",
                        "acknowledged", "readingalarmcategory", "acknowledgedTime",
                        "severity","subject", "noOfOccurrences"))
                .addFixedSelectableFields(Arrays.asList("severity", "noOfOccurrences", "acknowledgedBy"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.QUOTE)
    public static Supplier<FieldConfig> getQuoteFieldConfig(){
        return () -> new FieldConfig()
                .advancedFields()
                .add(FieldsConfigList.QUOTE_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .skip(Arrays.asList("workorder", "approvalFlowId", "stateFlowId", "parentId"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM)
    public static Supplier<FieldConfig> getSensorRollupAlarmFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.SENSOR_ALARM_FIELDS_INCLUDE)
                .done()

                .sortFields()
                .add(FieldsConfigList.SENSOR_ALARM_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.SENSOR_ALARM_FIELDS_INCLUDE)
                .addFixedFields(Arrays.asList("readingFieldId", "subject"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.RESERVATION)
    public static Supplier<FieldConfig> getReservationFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .add(Arrays.asList("name","startTime", "endTime"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.PURCHASE_REQUEST)
    public static Supplier<FieldConfig> getPurchaseRequestFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("name", "localId"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.PURCHASE_ORDER)
    public static Supplier<FieldConfig> getPurchaseOrderFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("name", "localId"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.ATTENDANCE)
    public static Supplier<FieldConfig> getAttendanceFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .add(Arrays.asList( "user", "checkInTime", "checkOutTime", "workingHours",
                        "status", "lastCheckInTime", "totalPaidBreakHrs", "lastBreakStartTime"))
                .addFixedFields(Arrays.asList("day"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.ALARM)
    public static Supplier<FieldConfig> getAlarmFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .add(Arrays.asList(
                        "subject","severity", "acknowledgedBy", "noOfEvents",
                        "alarmClass", "alarmType", "previousSeverity", "condition",
                        "modifiedTime", "createdTime", "clearedTime", "source",
                        "resource"))
                .addFixedSelectableFields(Arrays.asList("severity", "acknowledgedBy", "noOfEvents"))
                .done();
    }
    @Module(FacilioConstants.ContextNames.SPACE)
    public static Supplier<FieldConfig> getSpaceFieldConfig(){
        return () -> new FieldConfig()

                .sortFields()
                .add(FieldsConfigList.SPACE_FIELDS_INCLUDE)
                .done()

                .advancedFields()
                .add(FieldsConfigList.SPACE_FIELDS_INCLUDE)
                .skipOnelevelFields(FieldsConfigList.SPACE_FILTER_FIELDS_EXCLUDE)
                .done()

                .summaryWidgetFields()
                .add(FieldsConfigList.SPACE_SUMMARY_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .addFixedFields(FieldsConfigList.SPACE_VIEW_FIXED_FIELDS)
                .add(FieldsConfigList.SPACE_VIEW_FIELDS_INCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.TENANT_UNIT_SPACE)
    public static Supplier<FieldConfig> getTenantUnitSpaceFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("name"))
                .addFixedSelectableFields(Arrays.asList("photo"))
                .done();
    }
    @Module(FacilioConstants.ContextNames.TENANT)
    public static Supplier<FieldConfig> getTenantFieldConfig(){
        return () -> new FieldConfig()
                .viewFields()
                .add(FieldsConfigList.TENANT_MODULE_VIEW_FIELDS_INCLUDE)
                .done()
                .summaryWidgetFields()
                .add(FieldsConfigList.TENANT_MODULE_SUMMARY_FIELDS_INCLUDE)
                .done()
                .advancedFields()
                .add(FieldsConfigList.TENANT_MODULE_ADVANCED_FIELDS_INCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.BUDGET)
    public static Supplier<FieldConfig> getBudgetFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("name", "localId"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.PURCHASE_CONTRACTS)
    public static Supplier<FieldConfig> getPurchaseContractsFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("name", "localId"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.Budget.CHART_OF_ACCOUNT)
    public static Supplier<FieldConfig> getChartOfAccountFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .skip(Arrays.asList("localId"))
                .addFixedFields(Arrays.asList("name"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.RECEIVABLE)
    public static Supplier<FieldConfig> getReceivableFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("poId"))
                .done();
    }
    @Module(FacilioConstants.ReadingRules.NEW_READING_RULE)
    public static Supplier<FieldConfig> getNewReadingRuleFieldConfig(){
        return () -> new FieldConfig()

                .sortFields()
                .add(FieldsConfigList.NEW_READING_RULE_FIELDS_INCLUDE)
                .done()

                .advancedFields()
                .add(FieldsConfigList.NEW_READING_RULE_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.NEW_READING_RULE_FIELDS_INCLUDE)
                .addFixedFields(Arrays.asList("id","name"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.POINTS)
    public static Supplier<FieldConfig> getPointsFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.POINT_FIELDS_INCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.PLANNEDMAINTENANCE)
    public static Supplier<FieldConfig> getPlannedMaintenanceFieldConfig(){
        return () -> new FieldConfig()
                .addLicenseBasedFields(AccountUtil.FeatureLicense.TENANTS, Arrays.asList("tenant"))
                .addLicenseBasedFields(AccountUtil.FeatureLicense.VENDOR, Arrays.asList("vendor"))

                .advancedFields()
                .add(FieldsConfigList.PM_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.PM_VIEW_FIELDS_INCLUDE)
                .addFixedFields(FieldsConfigList.PM_VIEW_FIXED_FIELDS_INCLUDE)
                .done()

                .sortFields()
                .add(FieldsConfigList.PM_VIEW_FIXED_FIELDS_INCLUDE)
                .done();
    }

    @Module(MailConstants.ModuleNames.OUTGOING_MAIL_LOGGER)
    public static Supplier<FieldConfig> getOutgoingMailLoggerFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.OUTGOING_MAIL_LOGGER_INCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.VENDOR_QUOTES)
    public static Supplier<FieldConfig> getVendorQuoteFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("requestForQuotation", "id", "vendor"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.ACCOUNT_TYPE)
    public static Supplier<FieldConfig> getAccountTypeFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(FieldsConfigList.getNameField())
                .done();

    }

    @Module(FacilioConstants.ContextNames.INSURANCE)
    public static Supplier<FieldConfig> getInsuranceFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedSelectableFields(Arrays.asList("photo"))
                .done();

    }

    @Module(FacilioConstants.ContextNames.VISITOR_LOG)
    public static Supplier<FieldConfig> getVisitorLogFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .skip(Arrays.asList("passCode"))
                .addFixedFields(Arrays.asList("visitor", "localId", "visitorName"))
                .done();

    }

    @Module(FacilioConstants.ContextNames.INVITE_VISITOR)
    public static Supplier<FieldConfig> getInviteVisitorFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("visitor", "localId", "visitorName"))
                .done();

    }

    @Module(FacilioConstants.ContextNames.WATCHLIST)
    public static Supplier<FieldConfig> getWatchListFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("localId"))
                .done();

    }

    @Module(FacilioConstants.ContextNames.ControlGroup.CONTROL_GROUP_V2)
    public static Supplier<FieldConfig> getControlGroupV2FieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("id", "name"))
                .done();

    }

    @Module(FacilioConstants.ContextNames.ControlGroup.CONTROL_SCHEDULE)
    public static Supplier<FieldConfig> getControlScheduleFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("id","name","sysCreatedTime", "sysCreatedBy"))
                .done();

    }

    @Module(FacilioConstants.ContextNames.CONTACT_DIRECTORY)
    public static Supplier<FieldConfig> getContactDirectoryFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .skip(Arrays.asList("approvalFlowId", "stateFlowId"))
                .done();

    }

    @Module(FacilioConstants.ContextNames.ADMIN_DOCUMENTS)
    public static Supplier<FieldConfig> getAdminDocumentsFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .skip(Arrays.asList("approvalFlowId", "stateFlowId"))
                .done();

    }

    @Module(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD)
    public static Supplier<FieldConfig> getNeighbourhoodFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .skip(Arrays.asList("approvalFlowId", "stateFlowId"))
                .done();

    }

    @Module(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS)
    public static Supplier<FieldConfig> getDealsAndOffersFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .skip(Arrays.asList("approvalFlowId", "stateFlowId"))
                .done();

    }

    @Module(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION)
    public static Supplier<FieldConfig> getNewsAndInformationFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .skip(Arrays.asList("approvalFlowId", "stateFlowId"))
                .done();

    }

    @Module(FacilioConstants.ContextNames.GATE_PASS)
    public static Supplier<FieldConfig> getGatePassFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .skip(Arrays.asList("localId", "fromStoreRoom", "toStoreRoom", "parentWorkOrderId", "parentPoId",
                        "status", "issuedToPhoneNumber", "vehicleNo", "isReturnable", "gatePassType",
                        "issuedBy", "issuedTo", "returnTime", "issuedTime"))
                .addFixedFields(Arrays.asList("localId"))
                .done();

    }
    @Module(FacilioConstants.ContextNames.AGENT_ALARM)
    public static Supplier<FieldConfig> getAgentAlarmFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.AGENT_ALARM_FILTER_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.AGENT_ALARM_VIEW_FIELDS_INCLUDE)
                .addFixedFields(FieldsConfigList.AGENT_ALARM_VIEW_FIXED_FIELDS)
                .addFixedSelectableFields(FieldsConfigList.AGENT_ALARM_VIEW_FIXED_SELECTABLE_FIELDS)
                .done();
    }

    @Module(FacilioConstants.Workflow.WORKFLOW_LOG)
    public static Supplier<FieldConfig> getWorkflowLogFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.WORKFLOW_LOG_FIELDS_INCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.BASE_MAIL_MESSAGE)
    public static Supplier<FieldConfig> getBaseMailMessageFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.BASEMAIL_LOG_FIELDS_INCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.AGENT)
    public static Supplier<FieldConfig> getAgentFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.AGENT_FIELDS_INCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.SHIFT)
    public static Supplier<FieldConfig> getShiftFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.SHIFT_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(Arrays.asList("name","startTime", "endTime", "defaultShift"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.SHIFT_ROTATION)
    public static Supplier<FieldConfig> getShiftRotationFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .add(Arrays.asList("name","startTime", "endTime", "defaultShiftRotation"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.SHIPMENT)
    public static Supplier<FieldConfig> getShipmentFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .add(Arrays.asList("transferredBy", "receivedBy", "fromStore", "toStore",
                        "status", "localId"))
                .addFixedFields(Arrays.asList("localId"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.LABOUR_CONTRACTS)
    public static Supplier<FieldConfig> getLabourContractFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("localId"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS)
    public static Supplier<FieldConfig> getRentalLeaseContractFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("localId"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.WARRANTY_CONTRACTS)
    public static Supplier<FieldConfig> getWarrantyContractsFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("localId"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.CONTACT)
    public static Supplier<FieldConfig> getContactFieldConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("localId"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.BREAK)
    public static Supplier<FieldConfig> getBreakFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.BREAK_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(Arrays.asList("name", "breakTime", "breakType"))
                .done();
    }

    @Module(FacilioConstants.ContextNames.WORKFLOW_RULE_LOGS)
    public static Supplier<FieldConfig> getWorkflowRuleFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.WORKFLOW_RULE_LOGS_FIELDS_INCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.CONTROLLER)
    public static Supplier<FieldConfig> getControllerFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_INCLUDE)
                .done()
                .sortFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_INCLUDE)
                .done()
                .viewFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_INCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.MISC_CONTROLLER_MODULE_NAME)
    public static Supplier<FieldConfig> getMiscControllerFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_EXCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.BACNET_IP_CONTROLLER_MODULE_NAME)
    public static Supplier<FieldConfig> getBacnetIPControllerFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_EXCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.E2_CONTROLLER_MODULE_NAME)
    public static Supplier<FieldConfig> getE2ControllerFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_EXCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.OPC_UA_CONTROLLER_MODULE_NAME)
    public static Supplier<FieldConfig> getOPCUAControllerFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_EXCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.OPC_XML_DA_CONTROLLER_MODULE_NAME)
    public static Supplier<FieldConfig> getOPCXMLControllerFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_EXCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.MODBUS_RTU_CONTROLLER_MODULE_NAME)
    public static Supplier<FieldConfig> getModbusRTUControllerFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_EXCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.MODBUS_TCP_CONTROLLER_MODULE_NAME)
    public static Supplier<FieldConfig> getModbusTCPControllerFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_EXCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.RDM_CONTROLLER_MODULE_NAME)
    public static Supplier<FieldConfig> getRDMControllerFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_EXCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.NIAGARA_CONTROLLER_MODULE_NAME)
    public static Supplier<FieldConfig> getNiagaraControllerFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_EXCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.LON_WORKS_CONTROLLER_MODULE_NAME)
    public static Supplier<FieldConfig> getLonWorksControllerFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_EXCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.CUSTOM_CONTROLLER_MODULE_NAME)
    public static Supplier<FieldConfig> getCustomControllerFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_EXCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.REST_CONTROLLER_MODULE_NAME)
    public static Supplier<FieldConfig> getRestControllerFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_EXCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.SYSTEM_CONTROLLER_MODULE_NAME)
    public static Supplier<FieldConfig> getSystemControllerFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.CONTROLLER_FIELDS_TO_EXCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.SITE)
    public static Supplier<FieldConfig> getSiteFieldsConfig(){
        return () -> new FieldConfig()

                .addLicenseBasedFields(AccountUtil.FeatureLicense.FSM, Arrays.asList("territory"))
                .addLicenseBasedFields(AccountUtil.FeatureLicense.CLIENT, Arrays.asList("client"))

                .sortFields()
                .add(FieldsConfigList.SITE_FIELDS_INCLUDE)
                .done()

                .advancedFields()
                .add(FieldsConfigList.SITE_FIELDS_INCLUDE)
                .skipOnelevelFields(FieldsConfigList.SITE_FILTER_FIELDS_EXCLUDE)
                .done()

                .summaryWidgetFields()
                .add(FieldsConfigList.SITE_SUMMARY_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .addFixedFields(FieldsConfigList.SITE_VIEW_FIXED_FIELDS)
                .add(FieldsConfigList.SITE_VIEW_FIELDS_INCLUDE)

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.ENERGY_APP)
                .skipFields(Arrays.asList("territory"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.FSM_APP)
                .skipFields(Arrays.asList("territory"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)
                .skipFields(Arrays.asList("territory"))
                .appFieldConfigDone()

                .addConfigForDomain(AppDomain.AppDomainType.TENANT_PORTAL)
                .skipFields(Arrays.asList("territory"))
                .domainFieldConfigDone()

                .addConfigForDomain(AppDomain.AppDomainType.VENDOR_PORTAL)
                .skipFields(Arrays.asList("territory"))
                .domainFieldConfigDone()

                .addConfigForDomain(AppDomain.AppDomainType.SERVICE_PORTAL)
                .skipFields(Arrays.asList("territory"))
                .domainFieldConfigDone()

                .addConfigForDomain(AppDomain.AppDomainType.CLIENT_PORTAL)
                .skipFields(Arrays.asList("territory"))
                .domainFieldConfigDone()

                .addConfigForDomain(AppDomain.AppDomainType.EMPLOYEE_PORTAL)
                .skipFields(Arrays.asList("territory"))
                .domainFieldConfigDone()

                .done();
    }

    @Module(FacilioConstants.ContextNames.FLOOR)
    public static Supplier<FieldConfig> getFloorFieldsConfig(){
        return () -> new FieldConfig()

                .sortFields()
                .add(FieldsConfigList.FLOOR_FIELDS_INCLUDE)
                .done()

                .advancedFields()
                .add(FieldsConfigList.FLOOR_FIELDS_INCLUDE)
                .skipOnelevelFields(FieldsConfigList.FLOOR_FILTER_FIELDS_EXCLUDE)
                .done()

                .summaryWidgetFields()
                .add(FieldsConfigList.FLOOR_SUMMARY_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .addFixedFields(FieldsConfigList.FLOOR_VIEW_FIXED_FIELDS)
                .add(FieldsConfigList.FLOOR_VIEW_FIELDS_INCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.BUILDING)
    public static Supplier<FieldConfig> getBuidingFieldsConfig(){
        return () -> new FieldConfig()

                .sortFields()
                .add(FieldsConfigList.BUILDING_FIELDS_INCLUDE)
                .done()

                .summaryWidgetFields()
                .add(FieldsConfigList.BUILDING_SUMMARY_FIELDS_INCLUDE)
                .done()

                .advancedFields()
                .add(FieldsConfigList.BUILDING_FIELDS_INCLUDE)
                .skipOnelevelFields(FieldsConfigList.BUILDING_FILTER_FIELDS_EXCLUDE)
                .done()

                .viewFields()
                .addFixedFields(FieldsConfigList.BUILDING_VIEW_FIXED_FIELDS)
                .add(FieldsConfigList.BUILDING_VIEW_FIELDS_INCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.SERVICE)
    public static Supplier<FieldConfig> getServiceFieldsConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .add(FieldsConfigList.SERVICE_VIEW_FIELDS_INCLUDE)
                .done();

    }

    @Module(FacilioConstants.ContextNames.TRANSFER_REQUEST)
    public static Supplier<FieldConfig> getTransferRequestFieldsConfig(){
        return () -> new FieldConfig()

                .viewFields()
                .addFixedFields(Arrays.asList("requestSubject", "id"))
                .done();

    }

    @Module(RawAlarmModule.MODULE_NAME)
    public static Supplier<FieldConfig> getRawAlarmFieldsConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.RAW_ALARM_FIELDS_TO_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.RAW_ALARM_FIELDS_TO_INCLUDE)
                .done();
    }

    @Module(AlarmDefinitionMappingModule.MODULE_NAME)
    public static Supplier<FieldConfig> getAlarmDefinitionMappingModuleFields(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.ALARM_DEFINITION_MAPPING_FIELDS_TO_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.ALARM_DEFINITION_MAPPING_FIELDS_TO_INCLUDE)
                .done();
    }

    @Module(AlarmDefinitionTaggingModule.MODULE_NAME)
    public static Supplier<FieldConfig> getAlarmDefinitionTaggingModuleFields(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.ALARM_DEFINITION_TAGGING_FIELDS_TO_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.ALARM_DEFINITION_TAGGING_FIELDS_TO_INCLUDE)
                .done();
    }

    @Module(AlarmTypeModule.MODULE_NAME)
    public static Supplier<FieldConfig> getAlarmTypeModuleFields(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.ALARM_TYPE_FIELDS_TO_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.ALARM_TYPE_FIELDS_TO_INCLUDE)
                .done();

    }

    @Module(AlarmCategoryModule.MODULE_NAME)
    public static Supplier<FieldConfig> getAlarmCategoryModuleFields(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.ALARM_CATEGORY_FIELDS_TO_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.ALARM_CATEGORY_FIELDS_TO_INCLUDE)
                .done();
    }

    @Module(AlarmFilterRuleModule.MODULE_NAME)
    public static Supplier<FieldConfig> getAlarmFilterRuleModuleFields(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.ALARM_FILTER_RULE_FIELDS_TO_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.ALARM_FILTER_RULE_FIELDS_TO_INCLUDE)
                .done();

    }

    @Module(FilteredAlarmModule.MODULE_NAME)
    public static Supplier<FieldConfig> getFilteredAlarmModuleFields(){
        return  () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.FILTERED_ALARM_FIELDS_TO_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.FILTERED_ALARM_FIELDS_TO_INCLUDE)
                .done();
    }

    @Module(FlaggedEventRuleModule.MODULE_NAME)
    public static Supplier<FieldConfig> getFlaggedEventRuleModuleFields(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.FLAGGED_EVENT_RULE_FIELDS_TO_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.FLAGGED_EVENT_RULE_FIELDS_TO_INCLUDE)
                .done();

    }

    @Module((FlaggedEventModule.MODULE_NAME))
    public static Supplier<FieldConfig> getFlaggedEventModuleFields(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.FLAGGED_EVENT_FIELDS_TO_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.FLAGGED_EVENT_FIELDS_TO_INCLUDE)
                .done();

    }

    @Module(value = FacilioConstants.FieldsConfig.CUSTOM)
    public static Supplier<FieldConfig> getCustomModuleConfig() {
        return () ->  new FieldConfig()

                .sortFields()
                .done()

                .viewFields()
                .addFixedSelectableFields(Arrays.asList("photo"))
                .done()

                .advancedFields()
                .done()

                .pageBuilderCriteriaFields()
                .done();
    }

    @Module(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER)
    public static Supplier<FieldConfig> getUtilityIntegrationCustomerFieldConfig(){
        return () -> new FieldConfig()
                .sortFields()
                .add(FieldsConfigList.UTILITY_INTEGRATION_CUSTOMER_FIELDS_INCLUDE)
                .done()

                .advancedFields()
                .add(FieldsConfigList.UTILITY_INTEGRATION_CUSTOMER_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.UTILITY_INTEGRATION_CUSTOMER_FIELDS_INCLUDE)
                .done();
    }
    @Module(FacilioConstants.UTILITY_INTEGRATION_TARIFF)
    public static Supplier<FieldConfig> getUtilityIntegrationBillFieldConfig(){
        return () -> new FieldConfig()
                .sortFields()
                .add(FieldsConfigList.UTILITY_INTEGRATION_TARIFF_FIELDS_INCLUDE)
                .done()

                .advancedFields()
                .add(FieldsConfigList.UTILITY_INTEGRATION_TARIFF_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.UTILITY_INTEGRATION_TARIFF_FIELDS_INCLUDE)
                .done();
    }

    @Module(FacilioConstants.UTILITY_DISPUTE)
    public static Supplier<FieldConfig> getUtilityDisputeFieldConfig(){
        return () -> new FieldConfig()
                .sortFields()
                .add(FieldsConfigList.UTILITY_DISPUTE_FIELDS_INCLUDE)
                .done()

                .advancedFields()
                .add(FieldsConfigList.UTILITY_DISPUTE_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.UTILITY_DISPUTE_FIELDS_INCLUDE)
                .done();
    }
    @Module(FacilioConstants.UTILITY_INTEGRATION_BILLS)
    public static Supplier<FieldConfig> getUtilityIntegrationBillsFieldConfig(){
        return () -> new FieldConfig()
                .sortFields()
                .add(FieldsConfigList.UTILITY_INTEGRATION_BILLS_FIELDS_INCLUDE)
                .done()

                .advancedFields()
                .add(FieldsConfigList.UTILITY_INTEGRATION_BILLS_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.UTILITY_INTEGRATION_BILLS_FIELDS_INCLUDE)
                .done();
    }
    @Module(FacilioConstants.ContextNames.SERVICE_REQUEST)
    public static Supplier<FieldConfig> getServiceRequestFieldConfig(){
        return () -> new FieldConfig()

                .sortFields()
                .add(FieldsConfigList.SERVICE_REQUEST_FIELDS)
                .done()

                .advancedFields()
                .add(FieldsConfigList.SERVICE_REQUEST_FIELDS)
                .done()

                .viewFields()
                .add(FieldsConfigList.SERVICE_REQUEST_FIELDS)
                .done();
    }
    @Module(FacilioConstants.ContextNames.INVENTORY_REQUEST)
    public static Supplier<FieldConfig> getInventoryRequestFieldConfig(){
        return () -> new FieldConfig()

                .sortFields()
                .add(FieldsConfigList.INVENTORY_REQUEST_FIELDS)
                .done()

                .advancedFields()
                .add(FieldsConfigList.INVENTORY_REQUEST_FIELDS)
                .done()

                .viewFields()
                .add(FieldsConfigList.INVENTORY_REQUEST_FIELDS)
                .done();
    }
    @Module(FacilioConstants.ContextNames.FacilityBooking.FACILITY)
    public static Supplier<FieldConfig> getFacilityFieldConfig(){
        return () -> new FieldConfig()

                .sortFields()
                .add(FieldsConfigList.FACILITY_FIELDS)
                .done()

                .advancedFields()
                .add(FieldsConfigList.FACILITY_FIELDS)
                .done()

                .viewFields()
                .add(FieldsConfigList.FACILITY_FIELDS)
                .done();
    }
    @Module(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING)
    public static Supplier<FieldConfig> getBookingFieldConfig(){
        return () -> new FieldConfig()

                .sortFields()
                .add(FieldsConfigList.BOOKING_FIELDS)
                .done()

                .advancedFields()
                .add(FieldsConfigList.BOOKING_FIELDS)
                .done()

                .viewFields()
                .add(FieldsConfigList.BOOKING_FIELDS)
                .done();
    }
    @Module(FacilioConstants.Meter.METER)
    public static Supplier<FieldConfig> getMeterFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .skip(FieldsConfigList.METER_FIELDS_EXCLUDE)
                .done()

                .viewFields()
                .skip(FieldsConfigList.METER_FIELDS_EXCLUDE)
                .done();
    }
    @Module(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE)
    public static Supplier<FieldConfig> getVirtualMeterTemplateFieldConfig(){
        return () -> new FieldConfig()

                .advancedFields()
                .add(FieldsConfigList.VIRTUAL_METER_TEMPLATE_FIELDS_INCLUDE)
                .done()

                .viewFields()
                .add(FieldsConfigList.VIRTUAL_METER_TEMPLATE_FIELDS_INCLUDE)
                .done();
     }           
    @Module(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME)
    public static Supplier<FieldConfig> getControlActionFieldConfig(){
        return () -> new FieldConfig()
                .sortFields()
                .add(FieldsConfigList.CONTROL_ACTION_FIELDS_INCLUDE)
                .done()
                .advancedFields()
                .add(FieldsConfigList.CONTROL_ACTION_FIELDS_INCLUDE)
                .done()
                .viewFields()
                .skip(FieldsConfigList.CONTROL_ACTION_FIELDS_EXCLUDE)
                .done();
    }
    @Module(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME)
    public static Supplier<FieldConfig> getControlActionTemplateFieldConfig(){
        return () -> new FieldConfig()
                .sortFields()
                .add(FieldsConfigList.CONTROL_ACTION_TEMPLATE_FIELDS_INCLUDE)
                .done()
                .advancedFields()
                .add(FieldsConfigList.CONTROL_ACTION_TEMPLATE_FIELDS_INCLUDE)
                .done()
                .viewFields()
                .skip(FieldsConfigList.CONTROL_ACTION_TEMPLATE_FIELDS_EXCLUDE)
                .done();
    }

    @Module(FacilioConstants.Calendar.CALENDAR_MODULE_NAME)
    public static Supplier<FieldConfig> getCalendarFieldConfig() {
        return () -> new FieldConfig()
                .advancedFields()
                .add(FieldsConfigList.CALENDAR_FIELDS_INCLUDE)
                .done()
                .viewFields()
                .add(FieldsConfigList.CALENDAR_FIELDS_INCLUDE)
                .done()
                .sortFields()
                .add(FieldsConfigList.CALENDAR_FIELDS_INCLUDE)
                .done();
    }
    @Module(FacilioConstants.Calendar.EVENT_MODULE_NAME)
    public static Supplier<FieldConfig> getCalendarEventFieldConfig() {
        return () -> new FieldConfig()
                .advancedFields()
                .add(FieldsConfigList.CALENDAR_EVENTS_FIELDS_INCLUDE)
                .done()
                .viewFields()
                .add(FieldsConfigList.CALENDAR_EVENTS_FIELDS_INCLUDE)
                .done()
                .sortFields()
                .add(FieldsConfigList.CALENDAR_EVENTS_SORT_FIELDS_INCLUDE)
                .done();
    }
    @Module(FacilioConstants.ContextNames.CLIENT)
    public static Supplier<FieldConfig> getCientModuleFieldConfig() {
        return () -> new FieldConfig()
                .advancedFields()
                .add(FieldsConfigList.CLIENT_MODULE_FIELDS_ADVANCE_SEARCH_INCLUDE)
                .skipOnelevelFields(FieldsConfigList.CLIENT_MODULE_SKIP_ONE_LEVEL_FIELDS)
                .done()
                .viewFields()
                .add(FieldsConfigList.CLIENT_MODULE_FIELDS_VIEW_SEARCH_INCLUDE)
                .done()
                .sortFields()
                .add(FieldsConfigList.CLIENT_MODULE_SORT_FIELDS_INCLUDE)
                .done();

    }
    @Module(FacilioConstants.ContextNames.CLIENT_CONTACT)
    public static Supplier<FieldConfig> getCientContactModuleFieldConfig() {
        return () -> new FieldConfig()
                .advancedFields()
                .add(FieldsConfigList.CLIENT_CONTACT_MODULE_FIELDS_ADVANCE_SEARCH_INCLUDE)
                .skipOnelevelFields(FieldsConfigList.CLIENT_COMTACT_MODULE_SKIP_ONE_LEVEL_FIELDS)
                .done()
                .viewFields()
                .add(FieldsConfigList.CLIENT_CONTACT_MODULE_FIELDS_VIEW_SEARCH_INCLUDE)
                .done()
                .sortFields()
                .add(FieldsConfigList.CLIENT_CONTACT_MODULE_SORT_FIELDS_SEARCH_INCLUDE)
                .done();

    }

    @Module(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT)
    public static Supplier<FieldConfig> getServiceAppointmentFieldConfig(){
        return () -> new FieldConfig()
                .sortFields()
                .add(FieldsConfigList.APPOINTMENT_SORT_INCLUDE)
                .done()
                .viewFields()
                .add(FieldsConfigList.APPOINTMENT_FIELDS_INCLUDE)
                .done()
                .advancedFields()
                .add(FieldsConfigList.APPOINTMENT_FIELDS_INCLUDE)
                .done();

    }

    @Module(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER)
    public static Supplier<FieldConfig> getServiceOrderFieldConfig(){
        return () -> new FieldConfig()
                .sortFields()
                .add(FieldsConfigList.SERVICE_ORDER_SORT_FIELDS_INCLUDE)
                .done()
                .viewFields()
                .add(FieldsConfigList.SERVICE_ORDER_VIEW_FIELDS_INCLUDE)
                .done()
                .advancedFields()
                .add(FieldsConfigList.SERVICE_ORDER_FILTER_FIELDS_INCLUDE)
                .done();

    }

    @Module(FacilioConstants.TimeSheet.TIME_SHEET)
    public static Supplier<FieldConfig> getTimeSheetFieldConfig(){
        return () -> new FieldConfig()
                .sortFields()
                .add(FieldsConfigList.TIME_SHEET_FIELDS_INCLUDE)
                .done()
                .viewFields()
                .add(FieldsConfigList.TIME_SHEET_FIELDS_INCLUDE)
                .done()
                .advancedFields()
                .add(FieldsConfigList.TIME_SHEET_FIELDS_INCLUDE)
                .done();
    }
    @Module(FacilioConstants.Trip.TRIP)
    public static Supplier<FieldConfig> getTripFieldConfig(){
        return () -> new FieldConfig()
                .sortFields()
                .add(FieldsConfigList.TRIP_FIELDS_INCLUDE)
                .done()
                .viewFields()
                .add(FieldsConfigList.TRIP_FIELDS_INCLUDE)
                .done()
                .advancedFields()
                .add(FieldsConfigList.TRIP_FIELDS_INCLUDE)
                .done();
    }

    @Module(FacilioConstants.ContextNames.EMPLOYEE)
    public static Supplier<FieldConfig> getEmployeeFieldConfig(){
        return () -> new FieldConfig()
                .sortFields()
                .add(FieldsConfigList.EMPLOYEE_SORT_FIELDS_INCLUDE)

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)
                .skipFields(Arrays.asList("rate"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.FSM_APP)
                .skipFields(Arrays.asList("phone","email","mobile","currentFreeCapacity"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.ENERGY_APP)
                .skipFields(Arrays.asList("rate"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)
                .skipFields(Arrays.asList("rate"))
                .appFieldConfigDone()
                .done()

                .viewFields()
                .add(FieldsConfigList.EMPLOYEE_VIEW_FIELDS_INCLUDE)

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)
                .skipFields(Arrays.asList("rate","dispatchable","trackGeoLocation"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.FSM_APP)
                .skipFields(Arrays.asList("mobile","peopleType","locatedSpace","active","isOccupantPortalAccess","employeePortalAccess","moduleState","approvalStatus","currentFreeCapacity","user","labour","department","isAssignable","isAppAccess","isLabour","space"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.ENERGY_APP)
                .skipFields(Arrays.asList("rate","dispatchable","trackGeoLocation"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)
                .skipFields(Arrays.asList("rate","dispatchable","trackGeoLocation"))
                .appFieldConfigDone()
                .done()

                .advancedFields()
                .add(FieldsConfigList.EMPLOYEE_FILTER_FIELDS_INCLUDE)

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)
                .skipFields(Arrays.asList("rate","dispatchable","trackGeoLocation"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.FSM_APP)
                .skipFields(Arrays.asList("name","phone","email","mobile","peopleType","locatedSpace","active","isOccupantPortalAccess","employeePortalAccess","moduleState","approvalStatus","currentFreeCapacity","user","labour","department","isAssignable","isAppAccess","isLabour","space"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.ENERGY_APP)
                .skipFields(Arrays.asList("rate","dispatchable","trackGeoLocation"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)
                .skipFields(Arrays.asList("rate","dispatchable","trackGeoLocation"))
                .appFieldConfigDone()
                .done();

    }

    @Module(FacilioConstants.ContextNames.VENDOR_CONTACT)
    public static Supplier<FieldConfig> getVendorContactFieldConfig(){
        return () -> new FieldConfig()
                .sortFields()
                .add(FieldsConfigList.VENDOR_CONTACT_SORT_FIELDS_INCLUDE)

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)
                .skipFields(Arrays.asList("rate"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.FSM_APP)
                .skipFields(Arrays.asList("phone","email","mobile","currentFreeCapacity"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.ENERGY_APP)
                .skipFields(Arrays.asList("rate"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)
                .skipFields(Arrays.asList("rate"))
                .appFieldConfigDone()
                .done()

                .viewFields()
                .add(FieldsConfigList.VENDOR_CONTACT_VIEW_FIELDS_INCLUDE)

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)
                .skipFields(Arrays.asList("rate","dispatchable","trackGeoLocation"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.FSM_APP)
                .skipFields(Arrays.asList("mobile","peopleType","locatedSpace","active","isOccupantPortalAccess","employeePortalAccess","moduleState","approvalStatus","currentFreeCapacity","user","labour","vendor","isPrimaryContact","isVendorPortalAccess","isLabour"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.ENERGY_APP)
                .skipFields(Arrays.asList("rate","dispatchable","trackGeoLocation"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)
                .skipFields(Arrays.asList("rate","dispatchable","trackGeoLocation"))
                .appFieldConfigDone()
                .done()

                .advancedFields()
                .add(FieldsConfigList.VENDOR_CONTACT_FILTER_FIELDS_INCLUDE)

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP)
                .skipFields(Arrays.asList("rate","dispatchable","trackGeoLocation"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.FSM_APP)
                .skipFields(Arrays.asList("name","phone","email","mobile","peopleType","locatedSpace","active","isOccupantPortalAccess","employeePortalAccess","moduleState","approvalStatus","currentFreeCapacity","user","labour","vendor","isPrimaryContact","isVendorPortalAccess","isLabour"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.ENERGY_APP)
                .skipFields(Arrays.asList("rate","dispatchable","trackGeoLocation"))
                .appFieldConfigDone()

                .addConfigForApp(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)
                .skipFields(Arrays.asList("rate","dispatchable","trackGeoLocation"))
                .appFieldConfigDone()
                .done();
    }


}
