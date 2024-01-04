package com.facilio.datasandbox.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.datasandbox.context.ModuleCSVFileContext;
import com.facilio.datasandbox.util.DataPackageFileUtil;
import com.facilio.datasandbox.util.SandboxModuleConfigUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class AddNecessaryParamsForPackageCreation extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long sourceOrgId = (Long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        Long dataMigrationId = (Long) context.getOrDefault(DataMigrationConstants.DATA_MIGRATION_ID, -1L);
        Map<String, JSONObject> moduleNameVsFilter = (Map<String, JSONObject>) context.get(FacilioConstants.ContextNames.FILTERS);

        DataMigrationBean targetConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, sourceOrgId);
        DataMigrationStatusContext dataMigrationObj = targetConnection.checkAndAddDataMigrationStatus(sourceOrgId, dataMigrationId, true);

        String rootFolderName;
        List<String> moduleSequenceList = new ArrayList<>();
        String packageFilePath = dataMigrationObj.getPackageFilePath();
        Map<String, Stack<ModuleCSVFileContext>> moduleNameVsCsvFileContext = new HashMap<>();

        if (StringUtils.isEmpty(packageFilePath)) {
            rootFolderName = DataMigrationConstants.getDataPackageFolderName(sourceOrgId);
            PackageUtil.addRootFolderPath(rootFolderName);
        } else {
            rootFolderName = DataPackageFileUtil.getPackageFolderName(packageFilePath);
            rootFolderName = StringUtils.isNotEmpty(rootFolderName) ? rootFolderName : DataMigrationConstants.getDataPackageFolderName(sourceOrgId);
            PackageUtil.addRootFolderPath(rootFolderName);

            moduleSequenceList = DataPackageFileUtil.getModuleSequence(packageFilePath);
            moduleNameVsCsvFileContext = DataPackageFileUtil.getModuleNameVsXmlFileName(packageFilePath);
        }
        context.put(DataMigrationConstants.MODULENAME_VS_CSV_FILE_CONTEXT, moduleNameVsCsvFileContext);

        Map<String, Criteria> moduleNameVsClientCriteria = SandboxModuleConfigUtil.getClientCriteria(moduleNameVsFilter);
        context.put(DataMigrationConstants.MODULE_VS_CRITERIA, moduleNameVsClientCriteria);

        context.put(DataMigrationConstants.MODULE_SEQUENCE, moduleSequenceList);
        context.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, dataMigrationObj);
        context.put(DataMigrationConstants.DATA_MIGRATION_ID, dataMigrationObj.getId());
        context.put(DataMigrationConstants.ROOT_FOLDER_PATH, DataPackageFileUtil.getFileUrl(rootFolderName));

        long transactionTimeOut = (long) context.getOrDefault(DataMigrationConstants.TRANSACTION_TIME_OUT, DataMigrationConstants.MAX_THREAD_TIME);
        context.put(DataMigrationConstants.TRANSACTION_START_TIME, System.currentTimeMillis());
        context.put(DataMigrationConstants.TRANSACTION_TIME_OUT, transactionTimeOut);

        return false;
    }
}
