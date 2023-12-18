package com.facilio.datasandbox.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datasandbox.util.SandboxModuleConfigUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class ComputeModuleSequenceForPackageCreation extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long sourceOrgId = (long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        List<String> dataMigrationModuleNames = (List<String>) context.get(DataMigrationConstants.DATA_MIGRATION_MODULE_NAMES);
        List<String> runDataMigrationOnlyForModulesNames = (List<String>) context.get(DataMigrationConstants.RUN_ONLY_FOR_MODULES);
        Map<String, Criteria> allModuleVsCriteria = (Map<String, Criteria>) context.getOrDefault("moduleVsCriteria", new HashMap());
        boolean createFullDataPackage = (boolean) context.getOrDefault(DataMigrationConstants.CREATE_FULL_PACKAGE, false);
        List<String> skipDataMigrationModules = (List<String>) context.get(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES);

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", sourceOrgId);
        DataMigrationBean migrationBean = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, sourceOrgId);

        List<FacilioModule> allModules = migrationBean.getAllModules();
        Map<String, FacilioModule> allModulesMap = allModules.stream().collect(Collectors.toMap(FacilioModule::getName, Function.identity(), (a, b) -> b));

        List<FacilioModule> migrationModules = new ArrayList<>();
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = null;
        Map<String, Map<String, Object>> orderedModuleNameVsDetails = new LinkedHashMap<>();

        if (createFullDataPackage) {
            migrationModules = SandboxModuleConfigUtil.getFilteredModuleList(allModules);

            List<Long> allDataModulesExtendsIds = new ArrayList<>();
            HashMap<String, FacilioModule> migrationModuleNameVsModObj = new HashMap<>();
            for (FacilioModule dataMigrationModule : migrationModules) {
                List<Long> extendedModuleIds = dataMigrationModule.getExtendedModuleIds();
                extendedModuleIds.remove(dataMigrationModule.getModuleId());
                allDataModulesExtendsIds.addAll(extendedModuleIds);
                migrationModuleNameVsModObj.put(dataMigrationModule.getName(), dataMigrationModule);
            }

            migrationModuleNameVsDetails = SandboxModuleConfigUtil.getModuleDetails(moduleBean, migrationModules, allModuleVsCriteria, migrationModuleNameVsModObj);
            orderedModuleNameVsDetails = SandboxModuleConfigUtil.getModuleSequence(migrationModuleNameVsDetails, allDataModulesExtendsIds);

            if (CollectionUtils.isNotEmpty(skipDataMigrationModules)) {
                for (String moduleName : skipDataMigrationModules) {
                    orderedModuleNameVsDetails.remove(moduleName);
                }
            }
        }
        else if (CollectionUtils.isNotEmpty(runDataMigrationOnlyForModulesNames)) {
            // if runDataMigrationOnlyForModulesNames is specified (run only for specified modules)
            migrationModules = SandboxModuleConfigUtil.getModuleObjects(runDataMigrationOnlyForModulesNames, allModulesMap);

            Map<String, FacilioModule> migrationModuleNameVsModObj = migrationModules.stream().collect(Collectors.toMap(FacilioModule::getName, Function.identity(), (a, b) -> b));
            migrationModuleNameVsDetails = SandboxModuleConfigUtil.getModuleDetails(moduleBean, migrationModules, allModuleVsCriteria, migrationModuleNameVsModObj);
            orderedModuleNameVsDetails = SandboxModuleConfigUtil.getModuleSequence(migrationModuleNameVsDetails, new ArrayList<>());
        }
        else if (CollectionUtils.isNotEmpty(dataMigrationModuleNames)) {
            // if dataMigrationModuleNames is specified (run for specified and dependent modules)
            dataMigrationModuleNames.add(0, FacilioConstants.ContextNames.SITE);
            migrationModules = SandboxModuleConfigUtil.getModuleObjects(dataMigrationModuleNames, allModulesMap);

            // if migrationModules contains a custom module, need to "customactivity", it has no relation in SubModuleRel
            boolean hasCustomModule = migrationModules.stream().anyMatch(FacilioModule::isCustom);
            if (hasCustomModule) {
                migrationModules.add(moduleBean.getModule(FacilioConstants.ContextNames.CUSTOM_ACTIVITY));
            }

            Set<Long> allDataMigrationModuleIds = migrationModules.stream().map(FacilioModule::getModuleId).collect(Collectors.toSet());

            // extended modules
            List<FacilioModule> extendedModules = SandboxModuleConfigUtil.getExtendedModulesForSelectedModules(migrationModules, allModulesMap);
            while (CollectionUtils.isNotEmpty(extendedModules)) {
                migrationModules.addAll(extendedModules);
                extendedModules = SandboxModuleConfigUtil.getExtendedModulesForSelectedModules(extendedModules, allModulesMap);
            }

            // sub modules
            Map<Long, FacilioModule> subModuleIdVsModule = new HashMap<>();
            Map<String, List<FacilioField>> moduleNameVsRelatedFields = new HashMap<>();
            while (CollectionUtils.isNotEmpty(allDataMigrationModuleIds)) {
                List<FacilioModule> subModules = migrationBean.getSystemSubModules(new ArrayList<>(allDataMigrationModuleIds));
                Set<Long> currSubModuleIDs = subModules.stream().map(FacilioModule::getModuleId).collect(Collectors.toSet());

                // compute related fields
                SandboxModuleConfigUtil.getRelatedFields(migrationBean, new ArrayList<>(allDataMigrationModuleIds), new ArrayList<>(currSubModuleIDs), moduleNameVsRelatedFields);

                allDataMigrationModuleIds = currSubModuleIDs;
                allDataMigrationModuleIds.removeAll(subModuleIdVsModule.keySet());
                subModuleIdVsModule.putAll(subModules.stream().collect(Collectors.toMap(FacilioModule::getModuleId, Function.identity(), (a, b) -> b)));
            }
            List<FacilioModule> allSubModuleList = new ArrayList<>(subModuleIdVsModule.values());

            // if migrationModules contains a module of Notes Type, need to "commentattachments", it has no relation in SubModuleRel
            boolean hasNotesModule = allSubModuleList.stream().anyMatch(module -> module.getTypeEnum() == FacilioModule.ModuleType.NOTES);
            if (hasNotesModule) {
                migrationModules.add(moduleBean.getModule(FacilioConstants.ContextNames.COMMENT_ATTACHMENTS));
            }

            // compute extended moduleIds & moduleNameVsObj map
            List<Long> allDataModulesExtendsIds = new ArrayList<>();
            HashMap<String, FacilioModule> allDataModuleNameVsObj = new HashMap<>();
            for (FacilioModule dataMigrationModule : migrationModules) {
                List<Long> extendedModuleIds = dataMigrationModule.getExtendedModuleIds();
                extendedModuleIds.remove(dataMigrationModule.getModuleId());
                allDataModulesExtendsIds.addAll(extendedModuleIds);
                allDataModuleNameVsObj.put(dataMigrationModule.getName(), dataMigrationModule);
            }

            // compute extended moduleIds & moduleNameVsObj map for SubModules
            List<Long> allSubModulesExtendsIds = new ArrayList<>();
            HashMap<String, FacilioModule> allSubModuleNameVsObj = new HashMap<>();
            for (FacilioModule subModule : allSubModuleList) {
                List<Long> extendedModuleIds = subModule.getExtendedModuleIds();
                extendedModuleIds.remove(subModule.getModuleId());
                allSubModulesExtendsIds.addAll(extendedModuleIds);
                allSubModuleNameVsObj.put(subModule.getName(), subModule);
            }

            Map<String, Map<String, Object>> allModuleNameVsDetails = SandboxModuleConfigUtil.getModuleDetails(moduleBean, migrationModules, allModuleVsCriteria, allDataModuleNameVsObj);
            Map<String, Map<String, Object>> allSubModuleNameVsDetails = SandboxModuleConfigUtil.getModuleDetails(moduleBean, allSubModuleList, allModuleVsCriteria, allSubModuleNameVsObj);
            SandboxModuleConfigUtil.addRelatedFields(moduleNameVsRelatedFields, allSubModuleNameVsDetails);

            orderedModuleNameVsDetails = SandboxModuleConfigUtil.getModuleSequence(allModuleNameVsDetails, allDataModulesExtendsIds);
            HashMap<String, Map<String, Object>> allSubMigrationModuleNameVsDetails = SandboxModuleConfigUtil.getModuleSequence(allSubModuleNameVsDetails, allSubModulesExtendsIds);

            context.put(DataMigrationConstants.SUB_MODULES_VS_DETAILS, allSubMigrationModuleNameVsDetails);
        }

        context.put(DataMigrationConstants.MODULES_VS_DETAILS, orderedModuleNameVsDetails);
        LOGGER.info("####Data Package - Ordered Module Sequence - " + orderedModuleNameVsDetails.keySet());

        return false;
    }
}
