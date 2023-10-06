package com.facilio.datamigration.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

@Log4j
public class DataMigrationInsertRecordCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long targetOrgId = (long) context.getOrDefault(DataMigrationConstants.TARGET_ORG_ID, -1l);
        File file = (File) context.get(DataMigrationConstants.FILE);
        long packageId = (long) context.get(DataMigrationConstants.PACKAGE_ID);
        long sourceOrgId = (long) context.getOrDefault(DataMigrationConstants.SOURCE_ORG_ID, -1l);
        long dataMigrationId = (long) context.getOrDefault(DataMigrationConstants.DATA_MIGRATION_ID, -1l);
        DataMigrationStatusContext dataMigrationObj = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        PackageFolderContext rootFolder = (PackageFolderContext) context.get(PackageConstants.PACKAGE_ROOT_FOLDER);
        long transactionTimeout = (long) context.get(DataMigrationConstants.TRANSACTION_TIME_OUT);
        List<String> logModulesNames = (List<String>) context.get(DataMigrationConstants.LOG_MODULES_LIST);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        DataMigrationBean targetConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);

        List<String> dataConfigModuleNames = PackageFileUtil.getDataConfigModuleNames(rootFolder);
        Map<String, String> moduleNameVsXmlFileName = PackageFileUtil.getModuleNameVsXmlFileName(rootFolder);
        Map<ComponentType, List<PackageChangeSetMappingContext>> packageChangSets = PackageUtil.getAllPackageChangsets(packageId);
        Map<String,Map<String,String>> nonNullableModuleVsFieldVsLookupModules = DataMigrationUtil.getNonNullableModuleVsFieldVsLookupModules();

        for (String moduleName : dataConfigModuleNames) {

            LOGGER.info("Migration - Insert - Started for moduleName -" + moduleName);

            FacilioModule targetModule = modBean.getModule(moduleName);
            String targetModuleName = targetModule.getName();
            List<Long> extendedModuleIds = targetModule.getExtendedModuleIds();
            List<FacilioField> targetFields = modBean.getAllFields(targetModuleName);
            List<SupplementRecord> targetSupplements = DataMigrationUtil.getSupplementFields(targetFields);

            boolean addLogger = (CollectionUtils.isNotEmpty(logModulesNames) && logModulesNames.contains(moduleName));

            String moduleFileName = moduleNameVsXmlFileName.get(moduleName);
            File moduleCsvFile = PackageFileUtil.getModuleCsvFile(rootFolder, moduleFileName);

            List<Map<String, Object>> insertDataProps = DataMigrationUtil.getInsertDataPropsFromCsv(moduleCsvFile, targetModule, packageChangSets, nonNullableModuleVsFieldVsLookupModules,targetConnection,dataMigrationObj);

            Map<Long, Long> oldVsNewIds = targetConnection.createModuleData(targetModule, targetFields, targetSupplements, insertDataProps, addLogger);

            targetConnection.addIntoDataMappingTable(dataMigrationObj.getId(), targetModule.getModuleId(), oldVsNewIds);

            if (CollectionUtils.isNotEmpty(extendedModuleIds)) {
                for (Long moduleId : extendedModuleIds) {
                    if (moduleId == targetModule.getModuleId()) {
                        continue;
                    }

                    targetConnection.addIntoDataMappingTable(dataMigrationObj.getId(), moduleId, oldVsNewIds);
                }
            }

            LOGGER.info("Migration - Insert - Ended for moduleName -" + moduleName);
        }

        context.put(DataMigrationConstants.DATA_MIGRATION_MODULE_NAMES, dataConfigModuleNames);
        context.put(DataMigrationConstants.MODULE_NAMES_XML_FILE_NAME, moduleNameVsXmlFileName);
        context.put(DataMigrationConstants.DATA_INSERT_PROCESS, true);
        context.put(DataMigrationConstants.PACKAGE_CHANGE_SET,packageChangSets);

        return false;
    }

}
