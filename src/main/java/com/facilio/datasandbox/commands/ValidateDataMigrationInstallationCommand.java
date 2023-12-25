package com.facilio.datasandbox.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datasandbox.util.DataPackageFileUtil;
import com.facilio.datasandbox.util.SandboxModuleConfigUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateDataMigrationInstallationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long packageId = (Long) context.get(DataMigrationConstants.PACKAGE_ID);
        Long sourceOrgId = (Long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        Long targetOrgId = (Long) context.get(DataMigrationConstants.TARGET_ORG_ID);
        String bucketName = (String) context.get(DataMigrationConstants.BUCKET_NAME);
        String bucketRegion = (String) context.get(DataMigrationConstants.BUCKET_REGION);
        Long dataMigrationId = (Long) context.get(DataMigrationConstants.DATA_MIGRATION_ID);
        String packageFileURL = (String) context.get(DataMigrationConstants.PACKAGE_FILE_URL);

        FacilioUtil.throwIllegalArgumentException(sourceOrgId == null || sourceOrgId <= 0, "Source OrgId cannot be null");
        FacilioUtil.throwIllegalArgumentException(targetOrgId == null || targetOrgId <= 0, "Target OrgId cannot be null");
        FacilioUtil.throwIllegalArgumentException(packageId == null || packageId <= 0, "Meta Package Id cannot be null");
        FacilioUtil.throwIllegalArgumentException(StringUtils.isBlank(packageFileURL), "Package File URL cannot be null");

        if (StringUtils.isNotEmpty(bucketName) && StringUtils.isNotEmpty(bucketRegion)) {
            PackageUtil.setSandboxBucketName(bucketName);
            PackageUtil.setSandboxBucketRegion(bucketRegion);
        }

//        boolean validDirectory = DataPackageFileUtil.isValidDirectory(packageFileURL);
//        FacilioUtil.throwIllegalArgumentException(!validDirectory, "Enter a valid path for Package File");

        PackageUtil.addRootFolderPath(packageFileURL);

        AccountUtil.setCurrentAccount(targetOrgId);

        return false;
    }
}
