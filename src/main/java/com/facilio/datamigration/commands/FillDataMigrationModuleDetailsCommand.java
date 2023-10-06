package com.facilio.datamigration.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FillDataMigrationModuleDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<String> dataMigrationModuleNames = (List<String>) context.get(DataMigrationConstants.DATA_MIGRATION_MODULE_NAMES);
        long sourceOrgId = (long) context.getOrDefault(DataMigrationConstants.SOURCE_ORG_ID, -1l);
        long targetOrgId = (long) context.getOrDefault(DataMigrationConstants.TARGET_ORG_ID, -1l);
        Boolean moduleDataInsertProcess = (Boolean) context.getOrDefault(DataMigrationConstants.DATA_INSERT_PROCESS,false);

        DataMigrationBean connection = null;
        if(moduleDataInsertProcess){
            AccountUtil.setCurrentAccount(targetOrgId);
            connection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);
        }else{
            AccountUtil.setCurrentAccount(sourceOrgId);
            connection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, sourceOrgId);
        }

        List<FacilioModule> allSystemModules = connection.getAllModules();
        Map<String,FacilioModule> allSystemModulesMap = allSystemModules.stream().collect(Collectors.toMap(FacilioModule::getName, Function.identity(), (a, b) -> b));

        dataMigrationModuleNames.add(0,FacilioConstants.ContextNames.SITE);
        Map<String,Map<String,String>> nonNullableModuleVsFieldVsLookupModules = DataMigrationUtil.getNonNullableModuleVsFieldVsLookupModules();
        Set<String> nonNullableFieldModuleName = nonNullableModuleVsFieldVsLookupModules.keySet();

        List<FacilioModule> allDataMigrationModules = new ArrayList<>();
        List<FacilioModule> dataMigrationModules = DataMigrationUtil.getModuleDetails(dataMigrationModuleNames,allSystemModulesMap);
        allDataMigrationModules.addAll(dataMigrationModules);

        List<FacilioModule> selectedModulesExtendedModules = DataMigrationUtil.getSelectedModulesExtendedModules(dataMigrationModules,allSystemModulesMap);
        while (CollectionUtils.isNotEmpty(selectedModulesExtendedModules)){
            allDataMigrationModules.addAll(selectedModulesExtendedModules);
            selectedModulesExtendedModules = DataMigrationUtil.getSelectedModulesExtendedModules(selectedModulesExtendedModules,allSystemModulesMap);
        }


        Set<Long> allDataMigrationModuleIds = allDataMigrationModules.stream().map(facilioModule -> facilioModule.getModuleId()).collect(Collectors.toSet());
        List<FacilioModule> allSubModuleList  = new ArrayList<>();
        Set<Long>tempSubDataMigrationModuleIds = new HashSet<>();
        while (CollectionUtils.isNotEmpty(allDataMigrationModuleIds)){
            List<FacilioModule> subModules =  connection.getSystemSubModules(new ArrayList<>(allDataMigrationModuleIds));
            allSubModuleList.addAll(subModules);
            allDataMigrationModuleIds = subModules.stream().map(facilioModule -> facilioModule.getModuleId()).collect(Collectors.toSet());
            Set<Long>tempSubDataMigrationModules = new HashSet<>();
            for(Long moduleId : allDataMigrationModuleIds){
                if(!tempSubDataMigrationModuleIds.contains(moduleId)){
                    tempSubDataMigrationModules.add(moduleId);
                    tempSubDataMigrationModuleIds.add(moduleId);
                }
            }
            allDataMigrationModuleIds = new HashSet<>(tempSubDataMigrationModules);
        }

        List<Long> allDataModulesExtendsIds = new ArrayList<>();
        HashMap<String,FacilioModule> allDataModuleNameVsObj = new HashMap<>();

        for(FacilioModule dataMigrationModule : allDataMigrationModules){
            List<Long> extendedModuleIds = dataMigrationModule.getExtendedModuleIds();
            extendedModuleIds.remove(dataMigrationModule.getModuleId());
            allDataModulesExtendsIds.addAll(extendedModuleIds);
            allDataModuleNameVsObj.put(dataMigrationModule.getName(),dataMigrationModule);
        }

        List<Long> allSubModulesExtendsIds = new ArrayList<>();
        HashMap<String,FacilioModule> allSubModuleNameVsObj = new HashMap<>();
        for(FacilioModule subModule : allSubModuleList){
            List<Long> extendedModuleIds = subModule.getExtendedModuleIds();
            extendedModuleIds.remove(subModule.getModuleId());
            allSubModulesExtendsIds.addAll(extendedModuleIds);
            allSubModuleNameVsObj.put(subModule.getName(),subModule);
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, Map<String, Object>> allModuleNameVsDetails = new LinkedHashMap<>();
        Map<String,Criteria> allModuleVsCriteria = (Map<String,Criteria>) context.getOrDefault("moduleVsCriteria", new HashMap());
        Map<String, Map<String, Object>> allSubModuleNameVsDetails = new LinkedHashMap<>();
        Map<String,Criteria> allSubModuleVsCriteria = (Map<String,Criteria>) context.getOrDefault("moduleVsCriteria", new HashMap());

        getModuleDetails(allDataMigrationModules,modBean,allModuleNameVsDetails,allModuleVsCriteria,allDataModuleNameVsObj);
        getModuleDetails(allSubModuleList,modBean,allSubModuleNameVsDetails,allSubModuleVsCriteria,allSubModuleNameVsObj);

        HashMap<String, Map<String, Object>> allMigrationModuleNameVsDetails = getModuleSequence(allModuleNameVsDetails,allDataModulesExtendsIds);
        HashMap<String, Map<String, Object>> allSubMigrationModuleNameVsDetails = getModuleSequence(allSubModuleNameVsDetails,allSubModulesExtendsIds);

        HashMap<String, Map<String, Object>> allDataMigrationModuleNameVsDetails = new HashMap<>();
        allDataMigrationModuleNameVsDetails.putAll(allMigrationModuleNameVsDetails);
        for (Map.Entry<String, Map<String, Object>> entry : allSubMigrationModuleNameVsDetails.entrySet()) {
            if (!allMigrationModuleNameVsDetails.containsKey(entry.getKey())) {
                allDataMigrationModuleNameVsDetails.put(entry.getKey(), entry.getValue());
            }
        }

        List<FacilioModule> allDataMigrationModulesList = new ArrayList<>();
        allDataMigrationModulesList.addAll(allDataModuleNameVsObj.values());
        for(FacilioModule subModule: allSubModuleNameVsObj.values()){
            if(!allMigrationModuleNameVsDetails.containsKey(subModule.getName())){
                allDataMigrationModulesList.add(subModule);
            }
        }

        context.put(DataMigrationConstants.MODULES_VS_DETAILS, allDataMigrationModuleNameVsDetails);
        context.put(DataMigrationConstants.DATA_MIGRATION_MODULES, allDataMigrationModulesList);
        context.put(DataMigrationConstants.ALL_SYSTEM_MODULES,allSystemModulesMap);

        return false;
    }


    private static HashMap<String, Map<String, Object>> getModuleSequence(Map<String, Map<String, Object>> moduleNameVsDetails,List<Long>extendedModuleIds){

        HashMap<String, Map<String, Object>> migrationModuleNameVsDetails = new LinkedHashMap<>();

        HashMap<String, Map<String, Object>> moduleWithParentWithoutChild = new LinkedHashMap<>();
        HashMap<String, Map<String, Object>> moduleWithBothParentChild = new LinkedHashMap<>();
        HashMap<String, Map<String, Object>> moduleWithOnlyChild = new LinkedHashMap<>();
        HashMap<String, Map<String, Object>> baseEntityModules = new LinkedHashMap<>();
        HashMap<String, Map<String, Object>> otherModules = new LinkedHashMap<>();

        for (Map.Entry<String, Map<String, Object>> moduleVsDetails : moduleNameVsDetails.entrySet()) {
            String key = moduleVsDetails.getKey();
            Map<String, Object> value = moduleVsDetails.getValue();
            FacilioModule module = (FacilioModule) value.get("sourceModule");
            if (module.getExtendModule() != null && !extendedModuleIds.contains(module.getModuleId())) {
                moduleWithParentWithoutChild.put(key, value);
            } else if (module.getExtendModule() != null && extendedModuleIds.contains(module.getModuleId())) {
                moduleWithBothParentChild.put(key, value);
            } else if (module.getExtendModule() == null && extendedModuleIds.contains(module.getModuleId())) {
                moduleWithOnlyChild.put(key, value);
            } else if (module.getTypeEnum() == FacilioModule.ModuleType.BASE_ENTITY ||
                    module.getTypeEnum() == FacilioModule.ModuleType.Q_AND_A_RESPONSE ||
                    module.getTypeEnum() == FacilioModule.ModuleType.Q_AND_A) {
                baseEntityModules.put(key, value);
            } else {
                otherModules.put(key, value);
            }
        }

        migrationModuleNameVsDetails.putAll(moduleWithParentWithoutChild);
        migrationModuleNameVsDetails.putAll(moduleWithBothParentChild);
        migrationModuleNameVsDetails.putAll(moduleWithOnlyChild);
        migrationModuleNameVsDetails.putAll(baseEntityModules);
        migrationModuleNameVsDetails.putAll(otherModules);

        return migrationModuleNameVsDetails;
    }

    private static void getModuleDetails(List<FacilioModule> allDataMigrationModules,ModuleBean modBean,Map<String, Map<String, Object>> moduleNameVsDetails,Map<String,Criteria> moduleVsCriteria,HashMap<String, FacilioModule> allDataModuleNameVsObj) throws Exception{

        for(FacilioModule module : allDataMigrationModules){

            String moduleName = module.getName();
            Map<String, Object> details = (moduleNameVsDetails.containsKey(moduleName))? moduleNameVsDetails.get(moduleName): new HashMap<>();
            details.put("sourceModule", module);

            Map<String,Map<String, Object>> numberLookUps = new HashMap<>();
            List<String> numberFileFields = new ArrayList<>();
            FacilioModule currentModule = module;
            while (currentModule != null) {
                numberLookUps.putAll(DataMigrationUtil.getNumberLookups(currentModule, modBean, allDataModuleNameVsObj));
                numberFileFields.addAll(DataMigrationUtil.getNumberFileFields(currentModule));
                currentModule = currentModule.getExtendModule();
            }
            details.put("numberLookups", numberLookUps);
            details.put("fileFields", numberFileFields);

            Criteria moduleSpecificCriteria = DataMigrationUtil.getModuleSpecificCriteria(module);
            Criteria requestCriteria = moduleVsCriteria.containsKey(module.getName()) ? moduleVsCriteria.get(moduleName) : null;

            Criteria criteria = new Criteria();
            if(moduleSpecificCriteria != null) {
                criteria.andCriteria(moduleSpecificCriteria);
            }

            if(requestCriteria != null) {
                criteria.andCriteria(requestCriteria);
            }

            if(!criteria.isEmpty()) {
                details.put("criteria", criteria);
            }
            moduleNameVsDetails.put(moduleName, details);
        }

    }
}
