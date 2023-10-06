package com.facilio.datamigration.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;

import java.io.File;

public class ValidateInsertDataMigrationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        long targetOrgId = (long) context.getOrDefault(DataMigrationConstants.TARGET_ORG_ID, -1l);
        File file = (File) context.get(DataMigrationConstants.FILE);
        long packageId = (long) context.get(DataMigrationConstants.PACKAGE_ID);
        long sourceOrgId = (long) context.getOrDefault(DataMigrationConstants.SOURCE_ORG_ID, -1l);
        long dataMigrationId = (long) context.getOrDefault(DataMigrationConstants.DATA_MIGRATION_ID, -1l);

        if (targetOrgId < 0 || sourceOrgId < 0) {
            throw new IllegalArgumentException("Invalid Org details");
        }
        if (packageId < 0) {
            throw new IllegalArgumentException("Package Id cannot be null");
        }
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        AccountUtil.setCurrentAccount(targetOrgId);

        DataMigrationBean targetConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);
        DataMigrationStatusContext dataMigrationObj = targetConnection.checkAndAddDataMigrationStatus(sourceOrgId, dataMigrationId);
        context.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, dataMigrationObj);

        return false;
    }
}
