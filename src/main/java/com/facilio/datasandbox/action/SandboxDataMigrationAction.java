package com.facilio.datasandbox.action;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datasandbox.commands.SandboxDataMigrationChainFactory;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@Log4j
public class SandboxDataMigrationAction extends FacilioAction {
    private int limit;
    private int offset;
    private long packageId;
    private long sourceOrgId;
    private long targetOrgId;
    private String bucketName;
    private String bucketRegion;
    private boolean fetchDeleted;
    private long dataMigrationId;
    private String packageFileURL;
    private String moduleSequence;
    private int transactionTimeout;
    private boolean fullPackageType;
    private String dataMigrationModules;
    private boolean fromAdminTool = true;
    private String dataMigrationLogModules;
    private String skipDataMigrationModules;
    private String dataMigrationForOnlyMentionedModules;

    public String createDataPackage() throws Exception {

        LOGGER.info("####Sandbox - Initiating Data Package creation");

        int transactionTimeout = getTransactionTimeout();
        if (transactionTimeout < 1) {
            transactionTimeout = 6000000;
        }

        FacilioChain copyDataMigrationChain = SandboxDataMigrationChainFactory.createDataPackageChain(transactionTimeout);
        FacilioContext dataMigrationContext = copyDataMigrationChain.getContext();

        this.setFetchStackTrace(true);
        List<String> dataMigrationModulesList = new ArrayList<>();
        List<String> skipDataMigrationLogModulesList = new ArrayList<>();
        List<String> dataMigrationForOnlyMentionedModulesList = new ArrayList<>();

        if (StringUtils.isNotEmpty(dataMigrationModules)) {
            dataMigrationModulesList = Arrays.asList(FacilioUtil.splitByComma(getDataMigrationModules()));
        }

        if (StringUtils.isNotEmpty(dataMigrationForOnlyMentionedModules)) {
            dataMigrationForOnlyMentionedModulesList = Arrays.asList(FacilioUtil.splitByComma(getDataMigrationForOnlyMentionedModules()));
        }

        if (StringUtils.isNotEmpty(getSkipDataMigrationModules())) {
            skipDataMigrationLogModulesList = Arrays.asList(FacilioUtil.splitByComma(getSkipDataMigrationModules()));
        }

        dataMigrationContext.put(DataMigrationConstants.LIMIT, getLimit());
        dataMigrationContext.put(DataMigrationConstants.OFFSET, getOffset());
        dataMigrationContext.put(PackageConstants.FROM_ADMIN_TOOL, isFromAdminTool());
        dataMigrationContext.put(DataMigrationConstants.SOURCE_ORG_ID, getSourceOrgId());
        dataMigrationContext.put(DataMigrationConstants.CREATE_FULL_PACKAGE, fullPackageType);
        dataMigrationContext.put(FacilioConstants.ContextNames.FETCH_DELETED_RECORDS, fetchDeleted);
        dataMigrationContext.put(DataMigrationConstants.TRANSACTION_TIME_OUT, transactionTimeout);
        dataMigrationContext.put(DataMigrationConstants.DATA_MIGRATION_MODULE_NAMES, dataMigrationModulesList);
        dataMigrationContext.put(DataMigrationConstants.RUN_ONLY_FOR_MODULES, dataMigrationForOnlyMentionedModulesList);
        dataMigrationContext.put(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES, skipDataMigrationLogModulesList);

        copyDataMigrationChain.execute();
        LOGGER.info("####Sandbox - Completed Data Package creation");

        setResult(DataMigrationConstants.ROOT_FOLDER_PATH, dataMigrationContext.get(DataMigrationConstants.ROOT_FOLDER_PATH));
        setResult("result", "success");

        ServletActionContext.getResponse().setStatus(200);
        AccountUtil.cleanCurrentAccount();
        return SUCCESS;
    }

    public String installDataPackage() throws Exception {

        LOGGER.info("####Sandbox - Initiating Data Package installation");

        int transactionTimeout = getTransactionTimeout();
        if (transactionTimeout < 1) {
            transactionTimeout = 6000000;
        }
        FacilioChain installDataMigrationChain = SandboxDataMigrationChainFactory.getInstallDataMigrationChain(transactionTimeout);
        FacilioContext dataMigrationContext = installDataMigrationChain.getContext();

        this.setFetchStackTrace(true);
        List<String> moduleSequenceList = new ArrayList<>();
        List<String> dataMigrationLogModulesList = new ArrayList<>();
        List<String> skipDataMigrationLogModulesList = new ArrayList<>();

        if (StringUtils.isNotEmpty(getModuleSequence())) {
            moduleSequenceList = Arrays.asList(FacilioUtil.splitByComma(getModuleSequence()));
        }
        if (StringUtils.isNotEmpty(getDataMigrationLogModules())) {
            dataMigrationLogModulesList = Arrays.asList(FacilioUtil.splitByComma(getDataMigrationLogModules()));
        }

        if (StringUtils.isNotEmpty(getSkipDataMigrationModules())) {
            skipDataMigrationLogModulesList = Arrays.asList(FacilioUtil.splitByComma(getSkipDataMigrationModules()));
        }

        dataMigrationContext.put(DataMigrationConstants.LIMIT, getLimit());
        dataMigrationContext.put(DataMigrationConstants.PACKAGE_ID, packageId);
        dataMigrationContext.put(DataMigrationConstants.BUCKET_NAME, getBucketName());
        dataMigrationContext.put(DataMigrationConstants.BUCKET_REGION, getBucketRegion());
        dataMigrationContext.put(PackageConstants.FROM_ADMIN_TOOL, isFromAdminTool());
        dataMigrationContext.put(DataMigrationConstants.SOURCE_ORG_ID, getSourceOrgId());
        dataMigrationContext.put(DataMigrationConstants.TARGET_ORG_ID, getTargetOrgId());
        dataMigrationContext.put(DataMigrationConstants.PACKAGE_FILE_URL, packageFileURL);
        dataMigrationContext.put(DataMigrationConstants.MODULE_SEQUENCE, moduleSequenceList);
        dataMigrationContext.put(DataMigrationConstants.DATA_MIGRATION_ID, getDataMigrationId());
        dataMigrationContext.put(DataMigrationConstants.TRANSACTION_TIME_OUT, Long.parseLong(transactionTimeout + ""));
        dataMigrationContext.put(DataMigrationConstants.LOG_MODULES_LIST, dataMigrationLogModulesList);
        dataMigrationContext.put(DataMigrationConstants.TRANSACTION_START_TIME, System.currentTimeMillis());
        dataMigrationContext.put(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES, skipDataMigrationLogModulesList);

        installDataMigrationChain.execute();
        PackageUtil.removeRootFolderPath();
        PackageUtil.removeSandboxBucketName();
        PackageUtil.removeSandboxBucketRegion();

        LOGGER.info("####Sandbox - Completed Data Package installation");
        setResult("result", "success");

        ServletActionContext.getResponse().setStatus(200);
        AccountUtil.cleanCurrentAccount();

        return SUCCESS;
    }

}
