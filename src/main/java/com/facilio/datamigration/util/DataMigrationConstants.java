package com.facilio.datamigration.util;

import com.facilio.constants.FacilioConstants;

import java.util.Arrays;
import java.util.List;

public class DataMigrationConstants {

    public static final List<String> SYSTEM_MODULES = Arrays.asList(
            FacilioConstants.ContextNames.WORK_ORDER,
            FacilioConstants.ContextNames.ASSET,
            FacilioConstants.ContextNames.VENDORS,
            FacilioConstants.ContextNames.INSURANCE,
            FacilioConstants.ContextNames.BASE_VISIT,
            FacilioConstants.ContextNames.VISITOR_LOG,
            FacilioConstants.ContextNames.INVITE_VISITOR,
            FacilioConstants.ContextNames.PURCHASE_CONTRACTS,
            FacilioConstants.ContextNames.LABOUR_CONTRACTS,
            FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS,
            FacilioConstants.ContextNames.WARRANTY_CONTRACTS,
            FacilioConstants.ContextNames.SERVICE,
            FacilioConstants.ContextNames.TENANT,
            FacilioConstants.ContextNames.TENANT_UNIT_SPACE,
            FacilioConstants.ContextNames.PURCHASE_REQUEST,
            FacilioConstants.ContextNames.PURCHASE_ORDER,
            FacilioConstants.ContextNames.SERVICE_REQUEST,
            FacilioConstants.ContextNames.QUOTE,
            FacilioConstants.ContextNames.WorkPermit.WORKPERMIT,
            FacilioConstants.ContextNames.FacilityBooking.FACILITY,
            FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING,
            FacilioConstants.ContextNames.SITE,
            FacilioConstants.ContextNames.BUILDING,
            FacilioConstants.ContextNames.FLOOR,
            FacilioConstants.ContextNames.SPACE,
            FacilioConstants.Inspection.INSPECTION_TEMPLATE,
            FacilioConstants.Inspection.INSPECTION_RESPONSE,
            FacilioConstants.Induction.INDUCTION_TEMPLATE,
            FacilioConstants.Induction.INDUCTION_RESPONSE,
            FacilioConstants.ContextNames.TRANSFER_REQUEST,
            FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT,
            FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION,
            FacilioConstants.ContextNames.PLANNEDMAINTENANCE,
            FacilioConstants.ContextNames.ANNOUNCEMENT,
            FacilioConstants.ContextNames.JOB_PLAN,
            FacilioConstants.PeopleGroup.PEOPLE_GROUP);

    public static final String SOURCE_ORG_ID = "sourceOrgId";
    public static final String TARGET_ORG_ID = "targetOrgId";
    public static final String DATA_MIGRATION_ID = "migrationId";
    public static final String MODULES_TO_SKIP = "modulesToSkip";
    public static final String USER_ID_MAPPING = "userIdMapping";
    public static final String GROUP_ID_MAPPING = "groupIdMapping";
    public static final String ROLE_ID_MAPPING = "roleIdMapping";
    public static final String FORM_ID_MAPPING = "formIdMapping";
    public static final String STATEFLOW_ID_MAPPING = "stateflowIdMapping";
    public static final String SLA_ID_MAPPING = "slaIdMapping";
    public static final String IS_SITE_SCOPED = "isSiteScoped";
    public static final String SITE_MAPPING = "siteMapping";
    public static final String TRANSACTION_TIME_OUT = "transactionTimeOut";
    public static final String TRANSACTION_START_TIME = "transactionStartTime";
    public static final Object DATA_MIGRATION_CONTEXT = "dataMigrationContext";
    public static final String MODULE_SEQUENCE = "moduleSequence";
    public static final String SKIP_MODULES_LIST = "skipModules";
    public static final String LOG_MODULES_LIST = "logModules";
    public static final String SKIP_DATA_MIGRATION_MODULE_NAMES = "skipDataMigrationModuleNames";
    public static final String RUN_ONLY_FOR_MODULES = "runOnlyForModules";
    public static final String DATA_MIGRATION_MODULE_NAMES = "dataMigrationModuleNames";
    public static final String DATA_MIGRATION_MODULES = "dataMigrationModules";
    public static final String ALL_SYSTEM_MODULES = "allSystemModules";
    public static final String ALL_DATA_MIGRATION_MODULES = "allDataMigrationModules";
    public static final String MODULES_VS_DETAILS = "ModulesVsInfo";
    public static final String LIMIT = "limit";
    public static final String ALLOW_NOTES_AND__ATTACHMENTS = "allowNotesAndAttachments";
    public static final String ALLOW_UPDATE_DATA_ONLY = "allowUpdateDataOnly";
    public static final String PACKAGE_ID = "packageId";
    public static final String PACKAGE_CHANGE_SET = "packageChangSets";
    public static final String FILE = "file";
    public static final String MODULE_NAMES_XML_FILE_NAME = "moduleNameVsXmlFileName";
    public static final String DATA_INSERT_PROCESS = "moduleDataInsertProcess";


}
