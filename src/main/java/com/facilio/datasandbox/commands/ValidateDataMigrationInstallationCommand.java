package com.facilio.datasandbox.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datasandbox.util.DataPackageFileUtil;
import com.facilio.datasandbox.util.SandboxModuleConfigUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

public class ValidateDataMigrationInstallationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long packageId = (Long) context.get(DataMigrationConstants.PACKAGE_ID);
        Long sourceOrgId = (Long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        Long targetOrgId = (Long) context.get(DataMigrationConstants.TARGET_ORG_ID);
        String bucketName = (String) context.get(DataMigrationConstants.BUCKET_NAME);
        Long dataMigrationId = (Long) context.get(DataMigrationConstants.DATA_MIGRATION_ID);
        String packageFileURL = (String) context.get(DataMigrationConstants.PACKAGE_FILE_URL);

        FacilioUtil.throwIllegalArgumentException(sourceOrgId == null || sourceOrgId <= 0, "Source OrgId cannot be null");
        FacilioUtil.throwIllegalArgumentException(targetOrgId == null || targetOrgId <= 0, "Target OrgId cannot be null");
        FacilioUtil.throwIllegalArgumentException(packageId == null || packageId <= 0, "Meta Package Id cannot be null");
        FacilioUtil.throwIllegalArgumentException(StringUtils.isBlank(packageFileURL), "Package File URL cannot be null");

        boolean validDirectory = DataPackageFileUtil.isValidDirectory(packageFileURL);
        FacilioUtil.throwIllegalArgumentException(!validDirectory, "Enter a valid path for Package File");

        PackageUtil.addRootFolderPath(packageFileURL);

        if (StringUtils.isNotEmpty(bucketName)) {
            PackageUtil.setSandboxBucketName(bucketName);
        }

        // Modules copied in Meta are reUpdated in DataMigration flow
        List<String> updateOnlyModulesList = SandboxModuleConfigUtil.updateOnlyModulesList();
        context.put(DataMigrationConstants.UPDATE_ONLY_MODULES, updateOnlyModulesList);

        AccountUtil.setCurrentAccount(targetOrgId);

        DataMigrationBean targetConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);
        DataMigrationStatusContext dataMigrationObj = targetConnection.checkAndAddDataMigrationStatus(sourceOrgId, dataMigrationId);
        context.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, dataMigrationObj);

        return false;
    }
}
