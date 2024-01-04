package com.facilio.datasandbox.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datasandbox.util.DataPackageFileUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class ValidateDataMigrationCreationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long sourceOrgId = (Long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        List<String> runOnlyForModules = (List<String>) context.get(DataMigrationConstants.RUN_ONLY_FOR_MODULES);
        boolean createFullDataPackage = (boolean) context.getOrDefault(DataMigrationConstants.CREATE_FULL_PACKAGE, false);

        boolean getDependantModuleData = !(createFullDataPackage || CollectionUtils.isNotEmpty(runOnlyForModules));
        FacilioUtil.throwIllegalArgumentException(sourceOrgId == null || sourceOrgId <= 0, "Source OrgId cannot be null");

        AccountUtil.setCurrentAccount(sourceOrgId);

        context.put(DataMigrationConstants.GET_DEPENDANT_MODULE_DATA, getDependantModuleData);
        return false;
    }
}
