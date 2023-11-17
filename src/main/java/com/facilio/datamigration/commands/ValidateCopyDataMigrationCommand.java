package com.facilio.datamigration.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.datamigration.util.DataMigrationConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class ValidateCopyDataMigrationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long sourceOrgId = (long) context.getOrDefault(DataMigrationConstants.SOURCE_ORG_ID, -1l);
        List<String> dataMigrationModules = (List<String>) context.get(DataMigrationConstants.DATA_MIGRATION_MODULE_NAMES);
        List<String> runDataMigrationOnlyForModules = (List<String>) context.get(DataMigrationConstants.RUN_ONLY_FOR_MODULES);

        if (sourceOrgId <= 0l) {
            throw new IllegalArgumentException("Invalid Org details");
        }

        if (CollectionUtils.isEmpty(dataMigrationModules) && CollectionUtils.isEmpty(runDataMigrationOnlyForModules)) {
            throw new IllegalArgumentException("Data migration modules cannot be empty.");
        }

        return false;
    }
}
