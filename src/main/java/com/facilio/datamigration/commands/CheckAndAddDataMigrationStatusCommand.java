package com.facilio.datamigration.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;

public class CheckAndAddDataMigrationStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long sourceOrgId = (long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        long targetOrgId = (long) context.get(DataMigrationConstants.TARGET_ORG_ID);
        Long dataMigrationId = (Long) context.get(DataMigrationConstants.DATA_MIGRATION_ID);

        DataMigrationBean targetConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);
        DataMigrationStatusContext dataMigrationObj = targetConnection.checkAndAddDataMigrationStatus(sourceOrgId, dataMigrationId);
        context.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, dataMigrationObj);

        return false;
    }
}
