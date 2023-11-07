package com.facilio.datamigration.action;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.datamigration.commands.DataMigrationChainFactory;
import com.facilio.datamigration.util.DataMigrationConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@Log4j
public class CopyDataMigrationAction extends FacilioAction {

    private long sourceOrgId;
    private long targetOrgId;
    private String dataMigrationModules;
    private int limit;
    private boolean fromAdminTool = false;
    private int transactionTimeout;
    private boolean allowNotesAndAttachments = true;

    public String execute() throws Exception {

        LOGGER.info("####Sandbox - Initiating Data Package creation");

        int transactionTimeout = getTransactionTimeout();
        if(transactionTimeout<1){
            transactionTimeout = 6000000;
        }

        FacilioChain copyDataMigrationChain = DataMigrationChainFactory.getCopyDataMigrationChain(transactionTimeout);
        FacilioContext dataMigrationContext = copyDataMigrationChain.getContext();

        this.setFetchStackTrace(true);

        List<String> dataMigrationModulesList = new ArrayList<>();

        if (StringUtils.isNotEmpty(dataMigrationModules)) {
            dataMigrationModulesList = Arrays.asList(dataMigrationModules.split(","));
        }

        dataMigrationContext.put(DataMigrationConstants.LIMIT, getLimit());
        dataMigrationContext.put(DataMigrationConstants.ALLOW_NOTES_AND__ATTACHMENTS,isAllowNotesAndAttachments());
        dataMigrationContext.put(DataMigrationConstants.SOURCE_ORG_ID, getSourceOrgId());
        dataMigrationContext.put(DataMigrationConstants.TARGET_ORG_ID, getTargetOrgId());
        dataMigrationContext.put(DataMigrationConstants.DATA_MIGRATION_MODULE_NAMES, new ArrayList<String>(dataMigrationModulesList));
        dataMigrationContext.put(PackageConstants.FROM_ADMIN_TOOL,isFromAdminTool());
        dataMigrationContext.put(DataMigrationConstants.TRANSACTION_TIME_OUT, transactionTimeout);

        copyDataMigrationChain.execute();
        LOGGER.info("####Sandbox - Completed Data Package creation");

        setResult(PackageConstants.DOWNLOAD_URL, dataMigrationContext.get(PackageConstants.DOWNLOAD_URL));
        setResult(PackageConstants.FILE_ID, dataMigrationContext.get(PackageConstants.FILE_ID));
        setResult("result", "success");

        ServletActionContext.getResponse().setStatus(200);
        AccountUtil.cleanCurrentAccount();
        return SUCCESS;

    }
}
