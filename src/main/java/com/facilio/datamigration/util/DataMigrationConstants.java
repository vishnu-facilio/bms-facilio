package com.facilio.datamigration.util;

import org.apache.commons.chain.Context;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.fw.BeanFactory;

import java.util.Arrays;
import java.util.List;

public class DataMigrationConstants {
    public static final long MAX_THREAD_TIME = 840000;
    public static final long MAX_RECORDS_PER_FILE = 500000;
    public static final long MAX_RECORDS_PER_ITERATION = 5000;
    public static final String DATA_PACKAGE = "Data_Package";

    public static String getDataPackageFolderName(long orgId) {
        return DATA_PACKAGE + "_" + orgId + "_" + System.currentTimeMillis();
    }

    public static DataMigrationBean getDataMigrationBean(long orgId) throws Exception {
        return (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, orgId);
    }

    public static long getTransactionStartTime(Context context) {
        return (long) context.getOrDefault(DataMigrationConstants.TRANSACTION_START_TIME, -1);
    }

    public static DataMigrationStatusContext getDataMigrationStatusContext(Context context) {
        return (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
    }

    public static boolean isTransactionTimeOutReached(long transStartTime, long maxThreadTime) {
        maxThreadTime = maxThreadTime > 0 ? maxThreadTime : MAX_THREAD_TIME;
        return (System.currentTimeMillis() - transStartTime) > maxThreadTime;
    }

    public static boolean hasCompletedCurrentStep(Context context, DataMigrationStatusContext.DataMigrationStatus status) {
        DataMigrationStatusContext dataMigrationObj = getDataMigrationStatusContext(context);
        return dataMigrationObj.getStatusEnum().getIndex() > status.getIndex();
    }

    public static class LongTaskBeanMethodNames {
        public static final String CREATE_DATA_PACKAGE = "createDataCSVPackageForSandbox";
        public static final String INSTALL_DATA_PACKAGE = "installDataCSVPackageForSandbox";
    }

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
    public static final String PACKAGE_FILE_URL = "packageFileURL";
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
    public static final String UPDATE_ONLY_MODULES = "updateOnlyModules";
    public static final String LOG_MODULES_LIST = "logModules";
    public static final String SKIP_DATA_MIGRATION_MODULE_NAMES = "skipDataMigrationModuleNames";
    public static final String CREATE_FULL_PACKAGE = "createFullDataPackage";
    public static final String ROOT_FOLDER_PATH = "rootFolderPath";
    public static final String SANDBOX_DATA_PACKAGE_URL = "sandboxDataPackageUrl";
    public static final String RUN_ONLY_FOR_MODULES = "runOnlyForModules";
    public static final String GET_DEPENDANT_MODULE_DATA = "getDependantModuleData";
    public static final String MODULENAME_VS_CSV_FILE_CONTEXT = "moduleNameVsCsvFileContext";
    public static final String RESTRICT_DEPENDANT_MODULES = "restrictDependantModules";
    public static final String DATA_MIGRATION_MODULE_NAMES = "dataMigrationModuleNames";
    public static final String DATA_MIGRATION_SUB_MODULES = "dataMigrationSubModules";
    public static final String DATA_MIGRATION_MODULES = "dataMigrationModules";
    public static final String ALL_SYSTEM_MODULES = "allSystemModules";
    public static final String ALL_DATA_MIGRATION_MODULES = "allDataMigrationModules";
    public static final String ALL_DATA_SUB_MIGRATION_MODULES = "allDataMigrationSubModules";
    public static final String SUB_MODULES_VS_DETAILS = "subModulesVsInfo";
    public static final String MODULES_VS_DETAILS = "ModulesVsInfo";
    public static final String LIMIT = "limit";
    public static final String QUERY_LIMIT = "queryLimit";
    public static final String OFFSET = "offset";
    public static final String BUCKET_NAME = "bucketName";
    public static final String BUCKET_REGION = "bucketRegion";
    public static final String ALLOW_NOTES_AND__ATTACHMENTS = "allowNotesAndAttachments";
    public static final String ALLOW_UPDATE_DATA_ONLY = "allowUpdateDataOnly";
    public static final String PACKAGE_ID = "packageId";
    public static final String PACKAGE_CHANGE_SET = "packageChangSets";
    public static final String FILE = "file";
    public static final String MODULE_NAMES_XML_FILE_NAME = "moduleNameVsXmlFileName";
    public static final String DATA_INSERT_PROCESS = "moduleDataInsertProcess";
    public static final String FILTERS = "filters";
    public static final String MODULE_VS_CRITERIA = "moduleVsCriteria";
    public static final String FETCH_DELETED_RECORDS = "fetchDeletedRecords";
    public static final String ERROR_OCCURRED = "errorOccurred";
}
