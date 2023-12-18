package com.facilio.datamigration.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class DataMigrationUpdateInsertDataLookupDetails extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        long targetOrgId = (long) context.getOrDefault(DataMigrationConstants.TARGET_ORG_ID, -1l);
        File file = (File) context.get(DataMigrationConstants.FILE);
        long packageId = (long) context.get(DataMigrationConstants.PACKAGE_ID);
        long sourceOrgId = (long) context.getOrDefault(DataMigrationConstants.SOURCE_ORG_ID, -1l);
        long dataMigrationId = (long) context.getOrDefault(DataMigrationConstants.DATA_MIGRATION_ID, -1l);
        DataMigrationStatusContext dataMigrationObj = (DataMigrationStatusContext) context.get(DataMigrationConstants.DATA_MIGRATION_CONTEXT);
        PackageFolderContext rootFolder = (PackageFolderContext) context.get(PackageConstants.PACKAGE_ROOT_FOLDER);
        long transactionTimeout = (long) context.get(DataMigrationConstants.DATA_MIGRATION_ID);
        boolean allowNotesAndAttachments = (boolean) context.get(DataMigrationConstants.ALLOW_NOTES_AND__ATTACHMENTS);
        List<String> skipDataMigrationModules = (List<String>) context.get(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES);
        Map<ComponentType, List<PackageChangeSetMappingContext>> packageChangSets = (Map<ComponentType, List<PackageChangeSetMappingContext>>) context.get(DataMigrationConstants.PACKAGE_CHANGE_SET);

        List<String> logModulesNames = (List<String>) context.get(DataMigrationConstants.LOG_MODULES_LIST);
        List<String> dataConfigModuleNames = (List<String>) context.get(DataMigrationConstants.DATA_MIGRATION_MODULE_NAMES);
        Map<String, String> moduleNameVsXmlFileName = (Map<String, String>) context.get(DataMigrationConstants.MODULE_NAMES_XML_FILE_NAME);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (Map<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);
        Map<String,Map<String,String>> nonNullableModuleVsFieldVsLookupModules = DataMigrationUtil.getNonNullableModuleVsFieldVsLookupModules();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        DataMigrationBean targetConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);


        for (String moduleName : dataConfigModuleNames) {

            LOGGER.info("Data Migration - Update Insert Data - Started for moduleName -" + moduleName);

            if(skipDataMigrationModules.contains(moduleName)){
                LOGGER.info("Data Migration - Update Insert Data - skipped for moduleName -" + moduleName);
                continue;
            }

            boolean addLogger = (CollectionUtils.isNotEmpty(logModulesNames) && logModulesNames.contains(moduleName));

            Map<String, Object> moduleDetails = migrationModuleNameVsDetails.get(moduleName);
            FacilioModule targetModule = null;
            Map<String, Map<String, Object>> numberLookupDetails = new HashMap<>();

            if (MapUtils.isNotEmpty(moduleDetails)) {
                targetModule = (FacilioModule) moduleDetails.get("sourceModule");
                numberLookupDetails = (Map<String, Map<String, Object>>) moduleDetails.get("numberLookups");
            } else {
                targetModule = modBean.getModule(moduleName);
            }

            String moduleFileName = moduleNameVsXmlFileName.get(moduleName);
            File moduleCsvFile = PackageFileUtil.getModuleCsvFile(rootFolder, moduleFileName);

            Map<String, FacilioField> targetFieldNameVsFields = getLookupTypeFields(moduleName, modBean, numberLookupDetails);
            if(MapUtils.isEmpty(targetFieldNameVsFields)){
                LOGGER.info("Data Migration - Update Insert Data - field null skipped for moduleName -" + moduleName);
                continue;
            }

            List<SupplementRecord> targetSupplements = DataMigrationUtil.getSupplementFields((Collection<FacilioField>) targetFieldNameVsFields.values());

            List<Map<String,Object>> updatedDataProps = DataMigrationUtil.getUpdateDataProps(moduleCsvFile,targetFieldNameVsFields,targetModule,numberLookupDetails,targetConnection,dataMigrationObj,nonNullableModuleVsFieldVsLookupModules,packageChangSets);

            targetConnection.updateModuleData(targetModule, new ArrayList<>(targetFieldNameVsFields.values()), targetSupplements, updatedDataProps, addLogger);

            LOGGER.info("Data Migration - Update Insert Data - Ended for moduleName -" + moduleName);

        }

        return false;
    }


    private static Map<String, FacilioField> getLookupTypeFields(String moduleName, ModuleBean targetModuleBean, Map<String, Map<String, Object>> numberLookups) throws Exception {

        Set<String> siteIdAllowedModules = FieldUtil.getSiteIdAllowedModules();
        List<FacilioField> targetFields = targetModuleBean.getAllFields(moduleName);
        FacilioModule facilioModule = targetModuleBean.getModule(moduleName);
        Map<String, FacilioField> targetFieldNameVsFields = new HashMap<>();

        if (CollectionUtils.isNotEmpty(targetFields)) {
            targetFields = targetFields.stream().filter(field -> (field.getName().equals("id") || field.getName().equals("siteId") || (field.getDataTypeEnum().equals(FieldType.LOOKUP)
                            || field.getDataTypeEnum().equals(FieldType.MULTI_LOOKUP)))
                            || (MapUtils.isNotEmpty(numberLookups) && numberLookups.containsKey(field.getName())))
                    .collect(Collectors.toList());
            targetFieldNameVsFields = targetFields.stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));
        }

        if(siteIdAllowedModules.contains(moduleName)){
            FacilioField siteIdField = FieldFactory.getSiteIdField(facilioModule);
            targetFieldNameVsFields.putIfAbsent("siteId",siteIdField);
        }
        return targetFieldNameVsFields;
    }

}
