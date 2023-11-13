package com.facilio.datamigration.action;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.datamigration.commands.DataMigrationChainFactory;
import com.facilio.datamigration.util.DataMigrationConstants;
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
public class InstallDataMigrationAction extends FacilioAction {

    private File file;
    private long targetOrgId;
    private long sourceOrgId;
    private long packageId;
    private String dataMigrationLogModules;
    private String skipDataMigrationModules;
    private long dataMigrationId;
    private int transactionTimeout;
    private boolean moduleDataInsertProcess;
    private boolean allowNotesAndAttachments = false;
    private boolean allowUpdateDataOnly = false;


    public String execute() throws Exception{

        LOGGER.info("####Sandbox - Initiating Data Package installation");

        int transactionTimeout = getTransactionTimeout();
        if(transactionTimeout<1){
            transactionTimeout = 6000000;
        }
        FacilioChain installDataMigrationChain = DataMigrationChainFactory.getInstallDataMigrationChain(transactionTimeout);
        FacilioContext dataMigrationContext = installDataMigrationChain.getContext();

        this.setFetchStackTrace(true);

        List<String> dataMigrationLogModulesList = new ArrayList<>();
        List<String> skipDataMigrationLogModulesList = new ArrayList<>();

        if (StringUtils.isNotEmpty(getDataMigrationLogModules())) {
            dataMigrationLogModulesList = Arrays.asList(getDataMigrationLogModules().split(","));
        }

        if (StringUtils.isNotEmpty(getSkipDataMigrationModules())) {
            skipDataMigrationLogModulesList = Arrays.asList(getSkipDataMigrationModules().split(","));
        }

        dataMigrationContext.put(DataMigrationConstants.ALLOW_NOTES_AND__ATTACHMENTS,isAllowNotesAndAttachments());
        dataMigrationContext.put(DataMigrationConstants.ALLOW_UPDATE_DATA_ONLY,isAllowUpdateDataOnly());
        dataMigrationContext.put(DataMigrationConstants.SOURCE_ORG_ID, getSourceOrgId());
        dataMigrationContext.put(DataMigrationConstants.FILE, getFile());
        dataMigrationContext.put(DataMigrationConstants.TARGET_ORG_ID, getTargetOrgId());
        dataMigrationContext.put(DataMigrationConstants.PACKAGE_ID, getPackageId());
        dataMigrationContext.put(DataMigrationConstants.TRANSACTION_TIME_OUT, transactionTimeout);
        dataMigrationContext.put(DataMigrationConstants.DATA_MIGRATION_ID, getDataMigrationId());
        dataMigrationContext.put(DataMigrationConstants.LOG_MODULES_LIST, dataMigrationLogModulesList);
        dataMigrationContext.put(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES, skipDataMigrationLogModulesList);
        dataMigrationContext.put(DataMigrationConstants.DATA_INSERT_PROCESS,isModuleDataInsertProcess());

        installDataMigrationChain.execute();

        LOGGER.info("####Sandbox - Completed Data Package installation");
        setResult("result", "success");

        ServletActionContext.getResponse().setStatus(200);
        AccountUtil.cleanCurrentAccount();

        return SUCCESS;
    }
}
