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
import com.facilio.datasandbox.util.DataPackageFileUtil;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
@Log4j
public class SandboxDataMigrationAction extends FacilioAction {
    private int limit;
    private int offset;
    private int queryLimit;
    private long packageId;
    private String filters;
    private long sourceOrgId;
    private long targetOrgId;
    private String bucketName;
    private String bucketRegion;
    private boolean fetchDeleted;
    private long dataMigrationId;
    private String packageFileURL;
    private String moduleSequence;
    private boolean useIms = true;
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

        FacilioChain copyDataMigrationChain = useIms ? SandboxDataMigrationChainFactory.imsCreateDataPackageChain() : SandboxDataMigrationChainFactory.createDataPackageChain();
        FacilioContext dataMigrationContext = copyDataMigrationChain.getContext();

        this.setFetchStackTrace(true);
        List<String> dataMigrationModulesList = getAsList(dataMigrationModules);
        List<String> skipDataMigrationLogModulesList = getAsList(skipDataMigrationModules);
        List<String> dataMigrationForOnlyMentionedModulesList = getAsList(dataMigrationForOnlyMentionedModules);

        dataMigrationContext.put(DataMigrationConstants.LIMIT, getLimit());
        dataMigrationContext.put(DataMigrationConstants.OFFSET, getOffset());
        dataMigrationContext.put(PackageConstants.FROM_ADMIN_TOOL, isFromAdminTool());
        dataMigrationContext.put(DataMigrationConstants.SOURCE_ORG_ID, getSourceOrgId());
        dataMigrationContext.put(DataMigrationConstants.FILTERS, getFilterJson(filters));
        dataMigrationContext.put(DataMigrationConstants.CREATE_FULL_PACKAGE, fullPackageType);
        dataMigrationContext.put(DataMigrationConstants.DATA_MIGRATION_ID, getDataMigrationId());
        dataMigrationContext.put(FacilioConstants.ContextNames.FETCH_DELETED_RECORDS, fetchDeleted);
        dataMigrationContext.put(DataMigrationConstants.TRANSACTION_TIME_OUT, Long.parseLong(transactionTimeout + ""));
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

        FacilioChain installDataMigrationChain = useIms ? SandboxDataMigrationChainFactory.imsInstallDataPackageChain() : SandboxDataMigrationChainFactory.getInstallDataMigrationChain();
        FacilioContext dataMigrationContext = installDataMigrationChain.getContext();

        this.setFetchStackTrace(true);
        List<String> moduleSequenceList = getAsList(moduleSequence);
        List<String> dataMigrationLogModulesList = getAsList(dataMigrationLogModules);
        List<String> skipDataMigrationLogModulesList = getAsList(skipDataMigrationModules);

        dataMigrationContext.put(DataMigrationConstants.QUERY_LIMIT, getQueryLimit());
        dataMigrationContext.put(DataMigrationConstants.PACKAGE_ID, packageId);
        dataMigrationContext.put(DataMigrationConstants.BUCKET_NAME, getBucketName());
        dataMigrationContext.put(DataMigrationConstants.BUCKET_REGION, getBucketRegion());
        dataMigrationContext.put(PackageConstants.FROM_ADMIN_TOOL, isFromAdminTool());
        dataMigrationContext.put(DataMigrationConstants.FILTERS, getFilterJson(filters));
        dataMigrationContext.put(DataMigrationConstants.SOURCE_ORG_ID, getSourceOrgId());
        dataMigrationContext.put(DataMigrationConstants.TARGET_ORG_ID, getTargetOrgId());
        dataMigrationContext.put(DataMigrationConstants.PACKAGE_FILE_URL, packageFileURL);
        dataMigrationContext.put(DataMigrationConstants.MODULE_SEQUENCE, moduleSequenceList);
        dataMigrationContext.put(DataMigrationConstants.DATA_MIGRATION_ID, getDataMigrationId());
        dataMigrationContext.put(DataMigrationConstants.TRANSACTION_TIME_OUT, Long.parseLong(transactionTimeout + ""));
        dataMigrationContext.put(DataMigrationConstants.LOG_MODULES_LIST, dataMigrationLogModulesList);
        dataMigrationContext.put(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES, skipDataMigrationLogModulesList);

        installDataMigrationChain.execute();
        // clean temp folder
        FileUtils.deleteDirectory(DataPackageFileUtil.getTempFolderRoot());

        PackageUtil.removeRootFolderPath();
        PackageUtil.removeSandboxBucketName();
        PackageUtil.removeSandboxBucketRegion();

        LOGGER.info("####Sandbox - Completed Data Package installation");
        setResult("result", "success");

        ServletActionContext.getResponse().setStatus(200);
        AccountUtil.cleanCurrentAccount();

        return SUCCESS;
    }

    private static List<String> getAsList(String valueStr) {
        if (StringUtils.isNotEmpty(valueStr)) {
            String[] stringArr = FacilioUtil.splitByComma(valueStr);
            if (stringArr != null && stringArr.length > 0) {
                return Arrays.stream(stringArr).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    private static Map<String, JSONObject> getFilterJson(String filters) {
        Map<String, JSONObject> filtersJson = new HashMap<>();
        if (StringUtils.isNotEmpty(filters)) {
            try {
                filtersJson = (JSONObject) new JSONParser().parse(filters);
            } catch (Exception e) {
                LOGGER.info("####Sandbox - Exception while parsing filter criteria - " + e);
            }
        }
        return filtersJson;
    }

}
