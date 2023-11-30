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
import org.apache.commons.collections4.MapUtils;

import java.io.File;
import java.util.*;

@Log4j
public class DataMigrationInsertRecordCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        boolean allowUpdateDataOnly = (boolean) context.get(DataMigrationConstants.ALLOW_UPDATE_DATA_ONLY);
        long targetOrgId = (long) context.getOrDefault(DataMigrationConstants.TARGET_ORG_ID, -1l);
        File file = (File) context.get(DataMigrationConstants.FILE);
        long packageId = (long) context.get(DataMigrationConstants.PACKAGE_ID);
        long sourceOrgId = (long) context.getOrDefault(DataMigrationConstants.SOURCE_ORG_ID, -1l);
        long dataMigrationId = (long) context.getOrDefault(DataMigrationConstants.DATA_MIGRATION_ID, -1l);
        DataMigrationStatusContext dataMigrationObj = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        PackageFolderContext rootFolder = (PackageFolderContext) context.get(PackageConstants.PACKAGE_ROOT_FOLDER);
        int transactionTimeout = (int) context.get(DataMigrationConstants.TRANSACTION_TIME_OUT);
        List<String> logModulesNames = (List<String>) context.get(DataMigrationConstants.LOG_MODULES_LIST);
        List<String> skipDataMigrationModules = (List<String>) context.get(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES);
        boolean allowNotesAndAttachments = (boolean) context.get(DataMigrationConstants.ALLOW_NOTES_AND__ATTACHMENTS);
        HashMap<String, Map<String, Object>> migrationModuleNameVsDetails = (HashMap<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        DataMigrationBean targetConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);

        List<String> dataConfigModuleNames = PackageFileUtil.getDataConfigModuleNames(rootFolder);
        Map<String, String> moduleNameVsXmlFileName = PackageFileUtil.getModuleNameVsXmlFileName(rootFolder);
        Map<ComponentType, List<PackageChangeSetMappingContext>> packageChangSets = PackageUtil.getAllPackageChangsets(packageId);

        if(allowUpdateDataOnly){
            context.put(DataMigrationConstants.DATA_MIGRATION_MODULE_NAMES, dataConfigModuleNames);
            context.put(DataMigrationConstants.MODULE_NAMES_XML_FILE_NAME, moduleNameVsXmlFileName);
            context.put(DataMigrationConstants.PACKAGE_CHANGE_SET,packageChangSets);
            return false;
        }


        Map<String,Map<String,String>> nonNullableModuleVsFieldVsLookupModules = new HashMap<>(DataMigrationUtil.getNonNullableModuleVsFieldVsLookupModules());


        List<String> allDataConfigModuleNames = new ArrayList<>();

        for(String dataConfigModuleName :dataConfigModuleNames){
            if(!allDataConfigModuleNames.contains(dataConfigModuleName)){
                allDataConfigModuleNames.add(dataConfigModuleName);
            }
        }

        Map<Long, Long> siteIdMappings =  new HashMap<>();

        for (String moduleName : allDataConfigModuleNames) {

            Map<String, ComponentType> nameVsComponentType = PackageUtil.nameVsComponentType;

            if(nameVsComponentType.containsKey(moduleName)){
                LOGGER.info("Data Migration - Insert Data - skipped for meta moduleName -" + moduleName);
                continue;
            }

            if(skipDataMigrationModules.contains(moduleName)){
                LOGGER.info("Data Migration - Insert Data - skipped for moduleName -" + moduleName);
                continue;
            }

            Map<String, Object> moduleDetails = migrationModuleNameVsDetails.get(moduleName);
            FacilioModule targetModule = null;
            Map<String, Map<String, Object>> numberLookupDetails = new HashMap<>();

            if(MapUtils.isNotEmpty(moduleDetails) && moduleDetails.get("sourceModule")!=null){
                targetModule = (FacilioModule) moduleDetails.get("sourceModule");
                numberLookupDetails = (Map<String, Map<String, Object>>) moduleDetails.get("numberLookups");
            }else {
                targetModule = modBean.getModule(moduleName);
            }

            if(targetModule ==null){
                LOGGER.info("Data Migration - Insert Data - module not found  for moduleName -" + moduleName);
                continue;
            }

            if(!allowNotesAndAttachments && (targetModule.getTypeEnum()== FacilioModule.ModuleType.ATTACHMENTS || targetModule.getTypeEnum()==  FacilioModule.ModuleType.NOTES) ){
                continue;
            }

            LOGGER.info("Migration - Insert - Started for moduleName -" + moduleName);

            List<Long> extendedModuleIds = targetModule.getExtendedModuleIds();
            List<FacilioField> targetFields = modBean.getAllFields(moduleName);
            List<SupplementRecord> targetSupplements = DataMigrationUtil.getSupplementFields(targetFields);

            boolean addLogger = (CollectionUtils.isNotEmpty(logModulesNames) && logModulesNames.contains(moduleName));

            String moduleFileName = moduleNameVsXmlFileName.get(moduleName);
            File moduleCsvFile = PackageFileUtil.getModuleCsvFile(rootFolder, moduleFileName);

            List<Map<String, Object>> insertDataProps = DataMigrationUtil.getInsertDataPropsFromCsv(moduleCsvFile, targetModule, packageChangSets, nonNullableModuleVsFieldVsLookupModules,targetConnection,dataMigrationObj,siteIdMappings,allowNotesAndAttachments,numberLookupDetails);

            if(Objects.equals(moduleName, "workorder")){
                for(Map<String, Object> i :insertDataProps){
                    i.put("approvalRuleId",-1l);
                }
            }

            if (targetModule.getTypeEnum() == FacilioModule.ModuleType.ACTIVITY) {
                List<Map<String, Object>> insertActivityDataProps = new ArrayList<>(insertDataProps);
                for (Map<String, Object> p : insertActivityDataProps) {
                    Object parentId = p.get("parentId");
                    long activityParentId = (long) parentId;
                    if (activityParentId < 1l) {
                        insertDataProps.remove(p);
                    }
                }
            }



            Map<Long, Long> oldVsNewIds = targetConnection.createModuleData(targetModule, targetFields, targetSupplements, insertDataProps, addLogger);

            if(moduleName.equals("site")){
                siteIdMappings.putAll(oldVsNewIds);
            }
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
