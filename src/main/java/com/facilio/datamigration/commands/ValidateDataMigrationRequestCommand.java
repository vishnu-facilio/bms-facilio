package com.facilio.datamigration.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.datamigration.util.DataMigrationConstants;
import org.apache.commons.chain.Context;

public class ValidateDataMigrationRequestCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long sourceOrgId = (long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        long targetOrgId = (long) context.get(DataMigrationConstants.TARGET_ORG_ID);

        if(sourceOrgId <= 0l || targetOrgId <= 0l) {
            throw new IllegalArgumentException("Invalid Org details");
        }

        return false;
    }
}
