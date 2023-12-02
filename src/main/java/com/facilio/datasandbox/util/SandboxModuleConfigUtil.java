package com.facilio.datasandbox.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SandboxModuleConfigUtil {
    public static List<String> updateOnlyModulesList() {
        return new ArrayList<String>(){{
            add(FacilioConstants.ContextNames.PEOPLE);
            add(FacilioConstants.ContextNames.VENDORS);
            add(FacilioConstants.ContextNames.CLIENT);
            add(FacilioConstants.ContextNames.TENANT);
            add(FacilioConstants.ContextNames.EMPLOYEE);
            add(FacilioConstants.ContextNames.TENANT_CONTACT);
            add(FacilioConstants.ContextNames.VENDOR_CONTACT);
            add(FacilioConstants.ContextNames.CLIENT_CONTACT);
            add(FacilioConstants.PeopleGroup.PEOPLE_GROUP);
            add(FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER);
            add(FacilioConstants.ContextNames.ASSET_TYPE);
            add(FacilioConstants.ContextNames.SPACE_CATEGORY);
            add(FacilioConstants.ContextNames.ASSET_CATEGORY);
            add(FacilioConstants.ContextNames.ASSET_TYPE);
            add(FacilioConstants.ContextNames.ASSET_DEPARTMENT);
            add(FacilioConstants.ContextNames.TICKET_CATEGORY);
            add(FacilioConstants.ContextNames.TICKET_STATUS);
            add(FacilioConstants.ContextNames.TICKET_PRIORITY);
        }};
    }

    public static List<FacilioModule> getModuleObjects(List<String> dataMigrationModules, Map<String, FacilioModule> allSystemModulesMap) throws Exception {
        List<FacilioModule> moduleDetails = new ArrayList<>();
        for (String moduleName : dataMigrationModules) {
            if (allSystemModulesMap.get(moduleName) != null) {
                moduleDetails.add(allSystemModulesMap.get(moduleName));
            }
        }

        return moduleDetails;
    }

    public static Map<String, Map<String, Object>> getModuleDetails(ModuleBean modBean, List<FacilioModule> dataMigrationModules, Map<String, Criteria> reqModuleVsCriteria,
                                                                    Map<String, FacilioModule> dataModuleNameVsObj) throws Exception {

        Map<String, Map<String, Object>> moduleNameVsDetails = new LinkedHashMap<>();

        for (FacilioModule module : dataMigrationModules) {
            String moduleName = module.getName();
            Map<String, Object> details = (moduleNameVsDetails.containsKey(moduleName)) ? moduleNameVsDetails.get(moduleName) : new HashMap<>();
            details.put("sourceModule", module);

            Map<String, Map<String, Object>> numberLookUps = new HashMap<>();
            List<String> numberFileFields = new ArrayList<>();
            FacilioModule currentModule = module;
            while (currentModule != null) {
                numberLookUps.putAll(DataMigrationUtil.getNumberLookups(currentModule, modBean, dataModuleNameVsObj));
                numberFileFields.addAll(DataMigrationUtil.getNumberFileFields(currentModule));
                currentModule = currentModule.getExtendModule();
            }
            details.put("numberLookups", numberLookUps);
            details.put("fileFields", numberFileFields);

            Criteria moduleSpecificCriteria = DataMigrationUtil.getModuleSpecificCriteria(module);
            Criteria requestCriteria = reqModuleVsCriteria.containsKey(module.getName()) ? reqModuleVsCriteria.get(moduleName) : null;

            Criteria criteria = new Criteria();
            if (moduleSpecificCriteria != null) {
                criteria.andCriteria(moduleSpecificCriteria);
            }

            if (requestCriteria != null) {
                criteria.andCriteria(requestCriteria);
            }

            if (!criteria.isEmpty()) {
                details.put("criteria", criteria);
            }

            if (module.getExtendModule() != null) {
                String parentModuleName = module.getExtendModule().getName();
                Map<String, Object> parentModuleDetails = moduleNameVsDetails.containsKey(parentModuleName) ? moduleNameVsDetails.get(parentModuleName) : new HashMap();
                List<Long> childModuleIds = (parentModuleDetails.containsKey("childModuleIds")) ? (List<Long>) parentModuleDetails.get("childModuleIds") : new ArrayList<Long>();
                List<String> childModuleNames = (parentModuleDetails.containsKey("childModules")) ? (List<String>) parentModuleDetails.get("childModules") : new ArrayList<String>();

                childModuleNames.add(moduleName);
                parentModuleDetails.put("childModules", childModuleNames);
                childModuleIds.add(module.getModuleId());
                parentModuleDetails.put("childModuleIds", childModuleIds);
                moduleNameVsDetails.put(parentModuleName, parentModuleDetails);
            }
            moduleNameVsDetails.put(moduleName, details);
        }

        return moduleNameVsDetails;
    }

    public static HashMap<String, Map<String, Object>> getModuleSequence(Map<String, Map<String, Object>> moduleNameVsDetails, List<Long> extendedModuleIds) {

        HashMap<String, Map<String, Object>> migrationModuleNameVsDetails = new LinkedHashMap<>();
        HashMap<String, Map<String, Object>> moduleWithParentWithoutChild = new LinkedHashMap<>();
        HashMap<String, Map<String, Object>> moduleWithBothParentChild = new LinkedHashMap<>();
        HashMap<String, Map<String, Object>> notesAndAttachments = new LinkedHashMap<>();
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
            } else if (module.getTypeEnum() == FacilioModule.ModuleType.PHOTOS ||
                    module.getTypeEnum() == FacilioModule.ModuleType.NOTES ||
                    module.getTypeEnum() == FacilioModule.ModuleType.ATTACHMENTS) {
                notesAndAttachments.put(key, value);
            } else {
                otherModules.put(key, value);
            }
        }

        migrationModuleNameVsDetails.putAll(moduleWithParentWithoutChild);
        migrationModuleNameVsDetails.putAll(moduleWithBothParentChild);
        migrationModuleNameVsDetails.putAll(moduleWithOnlyChild);
        migrationModuleNameVsDetails.putAll(baseEntityModules);
        migrationModuleNameVsDetails.putAll(notesAndAttachments);
        migrationModuleNameVsDetails.putAll(otherModules);

        return migrationModuleNameVsDetails;
    }

    public static List<FacilioModule> getFilteredModuleList(List<FacilioModule> allModules) {
        List<Integer> moduleTypesToSkip = DataMigrationUtil.getModuleTypesToSkip();
        Set<String> skipMigrationModules = DataMigrationUtil.getMisConfiguredModules();
        return allModules.stream()
                .filter(module -> !moduleTypesToSkip.contains(module.getType()) && !skipMigrationModules.contains(module.getName())).collect(Collectors.toList());
    }

    public static List<FacilioModule> getExtendedModulesForSelectedModules(List<FacilioModule> selectedModules, Map<String, FacilioModule> allSystemModulesMap) throws Exception {
        List<FacilioModule> allExtendedModules = new ArrayList<>();
        List<FacilioModule> allSystemModules = new ArrayList<>(allSystemModulesMap.values());

        for (FacilioModule selectedModule : selectedModules) {
            if (LookupSpecialTypeUtil.isSpecialType(selectedModule.getName())) {
                continue;
            }
            List<FacilioModule> extendedModules = allSystemModules.stream()
                    .filter(facilioModule -> (facilioModule.getExtendModule() != null && facilioModule.getExtendModule().getModuleId() == selectedModule.getModuleId()))
                    .collect(Collectors.toList());
            allExtendedModules.addAll(extendedModules);
        }

        return allExtendedModules;

    }

    public static void getRelatedFields(DataMigrationBean migrationBean, List<Long> parentModuleIds, List<Long> childModuleIds, Map<String, List<FacilioField>> moduleNameVsRelatedFields) throws Exception {
        if (CollectionUtils.isEmpty(parentModuleIds) || CollectionUtils.isEmpty(childModuleIds)) {
            return;
        }
        List<FacilioField> relatedFields = migrationBean.getRelatedFields(parentModuleIds, childModuleIds);
        if (CollectionUtils.isNotEmpty(relatedFields)) {
            for (FacilioField field : relatedFields) {
                moduleNameVsRelatedFields.computeIfAbsent(field.getModule().getName(), k -> new ArrayList<>());
                moduleNameVsRelatedFields.get(field.getModule().getName()).add(field);
            }
        }
    }

    public static void addRelatedFields(Map<String, List<FacilioField>> moduleNameVsRelatedFields, Map<String, Map<String, Object>> moduleNameVsDetails) {
        if (MapUtils.isEmpty(moduleNameVsRelatedFields) || MapUtils.isEmpty(moduleNameVsDetails)) {
            return;
        }

        for (String moduleName : moduleNameVsDetails.keySet()) {
            if (moduleNameVsRelatedFields.containsKey(moduleName)) {
                List<FacilioField> relatedFields = moduleNameVsRelatedFields.get(moduleName);
                moduleNameVsDetails.get(moduleName).put("relatedFields", relatedFields);
            }
        }
    }

    public static Criteria computeRelatedFieldCriteriaForSubModules(List<LookupField> relatedFields, Map<String, List<Long>> fetchedRecords) {
        if (CollectionUtils.isNotEmpty(relatedFields)) {
            Criteria relatedCriteria = new Criteria();
            for (LookupField field : relatedFields) {
                String lookupModuleName = field.getLookupModule().getName();
                if (fetchedRecords.containsKey(lookupModuleName)) {
                    List<Long> parentRecordIds = fetchedRecords.get(lookupModuleName);
                    relatedCriteria.addOrCondition(CriteriaAPI.getCondition(field, parentRecordIds, NumberOperators.EQUALS));
                }
            }
            return relatedCriteria;
        }
        return null;
    }
}
