package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.*;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.bouncycastle.math.raw.Mod;
import org.reflections.Reflections;

import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.util.FacilioStreamUtil.distinctByKey;

public class ScopingUtil {

    public static ValueGenerator getValueGeneratorForLinkName(String linkName) throws Exception {
        Class<? extends ValueGenerator> classObject = (Class<? extends ValueGenerator>) Class.forName(linkName);
        if(classObject != null){
            return classObject.newInstance();
        }
        return null;
    }

    public static List<ValueGeneratorContext> getApplicableValueGenerators(String moduleName) throws Exception {
        if (StringUtils.isEmpty(moduleName)) {
            throw new IllegalArgumentException("Modulename cannot be null");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule applicableModule = modBean.getModule(moduleName);
        List<FacilioModule> joinModules = getJoinModules(applicableModule);
        joinModules.add(applicableModule);
        if (CollectionUtils.isNotEmpty(joinModules)) {
            List<String> joinModulesName = new ArrayList<>();
            List<Long> joinModuleIds = new ArrayList<>();
            for (FacilioModule module : joinModules) {
                if (module.getModuleId() > -1) {
                    joinModuleIds.add(module.getModuleId());
                } else if (module.getName() != null) {
                    joinModulesName.add(module.getName());
                }
            }
            ValueGeneratorBean valGenBean = (ValueGeneratorBean) BeanFactory.lookup("ValueGeneratorBean");
            return valGenBean.getValueGenerators(joinModulesName, joinModuleIds);
        }

        return null;
    }

    private static List<FacilioModule> getJoinModules(FacilioModule module) {
        List<FacilioModule> joinModules = new ArrayList<>();
        if (module != null) {
            FacilioModule extendedModule = module.getExtendModule();
            while (extendedModule != null) {
                joinModules.add(extendedModule);
                extendedModule = extendedModule.getExtendModule();
            }
        }
        return joinModules;
    }

    public static List<Map<String, Object>> getFieldsMapDetails(List<FacilioModule> modules, String lookupModuleName) throws Exception {
        if (StringUtils.isEmpty(lookupModuleName)) {
            throw new IllegalArgumentException("Lookup modulename cannot be null");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Map<String, Object>> moduleFieldsMap = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(modules)) {
            if (CollectionUtils.isNotEmpty(modules)) {
                modules = modules.stream()
                        .sorted(Comparator.comparing(FacilioModule::getDisplayName))
                        .collect(Collectors.toList());
            }
            for (FacilioModule module : modules) {
                if(module != null && !module.getName().equals(lookupModuleName)){
                    List<Map<String, Object>> fieldDetails = new ArrayList<>();
                    List<FacilioField> fieldsList = modBean.getAllFields(module.getName());
                    if (CollectionUtils.isNotEmpty(fieldsList)) {
                        for (FacilioField field : fieldsList) {
                            if (field != null) {
                                if ((field instanceof BaseLookupField && ((((BaseLookupField) field).getLookupModule().getName().equals(lookupModuleName))))) {
                                    Map<String, Object> fieldsMap = new HashMap<>();
                                    fieldsMap.put("name", field.getName());
                                    fieldsMap.put("displayName", field.getDisplayName());
                                    fieldsMap.put("dataType", field.getDataType());
                                    fieldDetails.add(fieldsMap);
                                } else if ((lookupModuleName.equals(FacilioConstants.ContextNames.USERS) || lookupModuleName.equals(FacilioConstants.ContextNames.PEOPLE)) && field.getName().equals(FacilioConstants.ContextNames.REQUESTER)) {
                                    Map<String, Object> fieldsMap = new HashMap<>();
                                    fieldsMap.put("name", field.getName());
                                    fieldsMap.put("displayName", field.getDisplayName());
                                    fieldsMap.put("dataType", field.getDataType());
                                    fieldDetails.add(fieldsMap);
                                }
                            }
                        }
                    }
                    //site ID field temp handling
                    if ((FieldUtil.isSiteIdFieldPresent(module) || module.isCustom()) && lookupModuleName.equals("site")) {
                        Map<String, Object> fieldsMap = new HashMap<>();
                        fieldsMap.put("name", "siteId");
                        fieldsMap.put("displayName", "Site");
                        fieldsMap.put("dataType", 7);
                        fieldDetails.add(fieldsMap);
                    }
                    if (CollectionUtils.isNotEmpty(fieldDetails)) {
                        Map<String, Object> moduleMap = new HashMap<>();
                        moduleMap.put("name", module.getName());
                        moduleMap.put("displayName", module.getDisplayName());
                        moduleMap.put("moduleId", module.getModuleId());
                        moduleMap.put("fieldsList", fieldDetails);
                        moduleFieldsMap.add(moduleMap);
                    }
                }
            }
        }
        return moduleFieldsMap;
    }

    public static List<FacilioModule> getLookupModulesListForModules(List<FacilioModule> modules) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> moduleIds = new ArrayList<>();
        List<FacilioModule> moduleList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(modules)) {
            for (FacilioModule module : modules) {
                if (!moduleIds.contains(module.getModuleId()) && !module.isModuleHidden()) {
                    moduleList.add(module);
                    moduleIds.add(module.getModuleId());
                }
                if (module != null) {
                    List<FacilioField> fieldList = modBean.getAllFields(module.getName());
                    if (CollectionUtils.isNotEmpty(fieldList)) {
                        for (FacilioField field : fieldList) {
                            if (field instanceof BaseLookupField) {
                                FacilioModule lookupModule = modBean.getModule(((BaseLookupField) field).getLookupModuleId());
                                if (lookupModule != null && lookupModule.getTypeEnum() == FacilioModule.ModuleType.BASE_ENTITY) {
                                    if (!moduleIds.contains(lookupModule.getModuleId()) && !module.isModuleHidden()) {
                                        moduleList.add(lookupModule);
                                        moduleIds.add(lookupModule.getModuleId());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return moduleList;
    }


    public static List<FacilioModule> getModulesList(boolean includeOthers) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> moduleList = V3ModuleAPI.getSystemModuleWithFeatureLicenceCheck();

        List<String> otherModulesList = Arrays.asList("users", "people","ticket");
        List<String> excludeModules = Arrays.asList(FacilioConstants.Inspection.INSPECTION_TEMPLATE,FacilioConstants.Induction.INDUCTION_TEMPLATE);

        //temp handling - can be removed once all fields are moved to people
        if(includeOthers) {
            for (String modName : otherModulesList) {
                moduleList.add(modBean.getModule(modName));
            }
        }
        List<FacilioModule> customModuleList = modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true);
        if (CollectionUtils.isNotEmpty(customModuleList)) {
            moduleList.addAll(customModuleList);
        }
        if (CollectionUtils.isNotEmpty(moduleList)) {
            moduleList = moduleList.stream()
                    .filter(mod -> mod != null)
                    .filter(module -> !excludeModules.contains(module.getName()))
                    .filter(distinctByKey(mod -> mod.getName()))
                    .sorted(Comparator.comparing(FacilioModule::getDisplayName))
                    .collect(Collectors.toList());
        }
        return moduleList;
    }

    public static List<FacilioModule> getUserScopingModulesList() throws Exception{
        return getModulesList(false);
    }

    public static Long getScopeVariableId(String linkName) throws Exception {
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        GlobalScopeVariableContext scopeVariableContext = scopeBean.getScopeVariable(linkName);
        if (scopeVariableContext != null) {
            return scopeVariableContext.getId();
        }
        return null;
    }

    //As user is special module it cannot be scoped in select record builder
    public static void checkUserSwitchAndThrowError(GlobalScopeVariableContext scopeVariableContext){
        if(scopeVariableContext != null) {
            String applicableModuleName = scopeVariableContext.getApplicableModuleName();
            if (applicableModuleName != null && applicableModuleName.equals(FacilioConstants.ContextNames.USERS) && scopeVariableContext.isSwitch()) {
                throw new IllegalArgumentException("User module cannot be enabled as switch");
            }
        }
    }

    public static void deleteUserscopeConfigForGlobalScope(long scopeVariableId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        GlobalScopeVariableContext scopeVariable = scopeBean.getScopeVariable(scopeVariableId);
        if (scopeVariable != null) {
            if (scopeVariable.getAppId() == null) {
                throw new IllegalArgumentException("Appid cannot be null");
            }
            List<Long> moduleIds = new ArrayList<>();
            if (scopeVariable.getId() > 0) {
                GlobalScopeVariableContext fetchedScopeVariable = scopeBean.getScopeVariable(scopeVariable.getId());
                List<ScopeVariableModulesFields> scopeVariableModulesFieldsList = fetchedScopeVariable.getScopeVariableModulesFieldsList();
                List<String> fieldsToRemove = new ArrayList<>();
                Map<Long,String> fieldToRemoveMap = new HashMap<>();
                fieldsToRemove.add("id");
                fieldToRemoveMap.put(fetchedScopeVariable.getApplicableModuleId(),"id");
                moduleIds.add(fetchedScopeVariable.getApplicableModuleId());
                if(CollectionUtils.isNotEmpty(scopeVariableModulesFieldsList)){
                    for(ScopeVariableModulesFields moduleField : scopeVariableModulesFieldsList) {
                        String fieldName = moduleField.getFieldName();
                        fieldsToRemove.add(fieldName);
                        fieldToRemoveMap.put(moduleField.getModuleId(),moduleField.getFieldName());
                        moduleIds.add(moduleField.getModuleId());
                    }
                }
                List<ScopingConfigContext> newScopingConfigList = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(moduleIds)) {
                    for(Long moduleId : moduleIds) {
                        if (moduleId != null) {
                            List<ScopingConfigContext> scopingConfigList = ApplicationApi.getScopingConfigForModuleApp(scopeVariable.getAppId(), moduleId);
                            if (CollectionUtils.isNotEmpty(scopingConfigList)) {
                                for (ScopingConfigContext scopingConfig : scopingConfigList) {
                                    Criteria newCriteria = new Criteria();
                                    Criteria criteria = CriteriaAPI.getCriteria(scopingConfig.getCriteriaId());
                                    Map<String, Condition> conditionsMap = criteria.getConditions();
                                    if (MapUtils.isNotEmpty(conditionsMap)) {
                                        for (Map.Entry<String, Condition> conditionEntry : conditionsMap.entrySet()) {
                                            Condition condition = conditionEntry.getValue();
                                            if (condition != null) {
                                                FacilioModule mod = modBean.getModule(scopingConfig.getModuleId());
                                                FacilioField field = modBean.getField(condition.getFieldName(), mod.getName());
                                                if(field == null) {
                                                    String[] modFieldArr = condition.getFieldName().split("\\.");
                                                    field = modBean.getField(modFieldArr[1], mod.getName());
                                                }
                                                String actualFieldName = field.getName();
                                                if (!fieldToRemoveMap.containsKey(mod.getModuleId()) || (fieldToRemoveMap.containsKey(mod.getModuleId()) && !fieldToRemoveMap.get(mod.getModuleId()).equals(actualFieldName))) {
                                                    newCriteria.addAndCondition(condition);
                                                }
                                            }
                                        }
                                    }
                                    if(MapUtils.isNotEmpty(newCriteria.getConditions())) {
                                        scopingConfig.setCriteria(newCriteria);
                                        newScopingConfigList.add(scopingConfig);
                                    }
                                    UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
                                    userScopeBean.deleteScopingConfigForId(scopingConfig.getId());
                                }
                            }
                        }
                    }
                }
                if(CollectionUtils.isNotEmpty(newScopingConfigList)) {
                    ApplicationApi.addScopingConfigForApp(newScopingConfigList);
                }
            }
        }
    }

    public static void addUserscopeConfigForGlobalScope(long scopeVariableId) throws Exception {
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        ValueGeneratorBean valGenBean = (ValueGeneratorBean) BeanFactory.lookup("ValueGeneratorBean");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        GlobalScopeVariableContext scopeVariable = scopeBean.getScopeVariable(scopeVariableId);
        if (scopeVariable != null && scopeVariable.getStatus()) {
            if (scopeVariable.getValueGeneratorId() != null) {
                ValueGeneratorContext valueGeneratorContext = valGenBean.getValueGenerator(scopeVariable.getValueGeneratorId());
                if (scopeVariable.getAppId() == null) {
                    throw new IllegalArgumentException("Appid cannot be null");
                }
                if (scopeVariable.getId() > 0) {
                    GlobalScopeVariableContext fetchedScopeVariable = scopeBean.getScopeVariable(scopeVariable.getId());
                    List<ScopeVariableModulesFields> scopeVariableModulesFieldsList = fetchedScopeVariable.getScopeVariableModulesFieldsList();
                    Map<Long, String> moduleIdVsFieldNameMap = new HashMap<>();
                    ValueGeneratorContext valueGenerator = valGenBean.getValueGenerator(scopeVariable.getValueGeneratorId());
                    if(includeSelfModule(fetchedScopeVariable.getApplicableModuleId())) {
                        moduleIdVsFieldNameMap.put(fetchedScopeVariable.getApplicableModuleId(), "id");
                    }
                    if (CollectionUtils.isNotEmpty(scopeVariableModulesFieldsList)) {
                        for (ScopeVariableModulesFields moduleField : scopeVariableModulesFieldsList) {
                            if(moduleField.getModuleId() != null) {
                                moduleIdVsFieldNameMap.put(moduleField.getModuleId(), moduleField.getFieldName());
                                moduleIdVsFieldNameMap.put(moduleField.getModuleId(), moduleField.getFieldName());
                            }
                        }
                    }
                    List<ScopingConfigContext> newScopingConfigList = new ArrayList<>();
                    List<ScopingContext> scopings = ApplicationApi.getScopingForApp(scopeVariable.getAppId());
                    if (CollectionUtils.isNotEmpty(scopings)) {
                        for (ScopingContext scoping : scopings) {
                            Map<Long, ScopingConfigContext> scopingConfigMap = ApplicationApi.getScopingMapForApp(scoping.getId());
                            List<Long> visited = new ArrayList<>();
                            if (MapUtils.isNotEmpty(moduleIdVsFieldNameMap)) {
                                for (Map.Entry<Long, String> modIdFieldEntry : moduleIdVsFieldNameMap.entrySet()) {
                                    if(modIdFieldEntry.getKey() != null) {
                                        boolean hasConfig = false;
                                        if (MapUtils.isNotEmpty(scopingConfigMap)) {
                                            for (ScopingConfigContext scopingConfig : scopingConfigMap.values()) {
                                                if (scopingConfig.getModuleId() == modIdFieldEntry.getKey() && moduleIdVsFieldNameMap.containsKey(scopingConfig.getModuleId()) && !visited.contains(scopingConfig.getModuleId())) {
                                                    hasConfig = true;
                                                    Criteria criteria = CriteriaAPI.getCriteria(scopingConfig.getCriteriaId());
                                                    if (criteria != null) {
                                                        FacilioModule module = modBean.getModule(modIdFieldEntry.getKey());
                                                        FacilioField field = modBean.getField(modIdFieldEntry.getValue(), module.getName());
                                                        if (field instanceof MultiLookupField) {
                                                            String valGenLinkName = getAlternateLinkNameForMultiLookupHandling(valueGenerator.getLinkName(),field);
                                                            criteria.addAndCondition(CriteriaAPI.getCondition(field.getCompleteColumnName(), module.getName() + "." + field.getName(), valGenLinkName, ScopeOperator.SCOPING_IS));
                                                        } else {
                                                            criteria.addAndCondition(CriteriaAPI.getCondition(field, valueGenerator.getLinkName(), ScopeOperator.SCOPING_IS));
                                                        }
                                                        scopingConfig.setCriteria(criteria);
                                                    }
                                                    UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
                                                    userScopeBean.deleteScopingConfigForId(scopingConfig.getId());
                                                    newScopingConfigList.add(scopingConfig);
                                                    visited.add(scopingConfig.getModuleId());
                                                    break;
                                                }
                                            }
                                        }
                                        if (!hasConfig) {
                                            ScopingConfigContext newScopingConfig = new ScopingConfigContext();
                                            newScopingConfig.setModuleId(modIdFieldEntry.getKey());
                                            newScopingConfig.setScopingId(scoping.getId());
                                            Criteria criteria = new Criteria();
                                            if (criteria != null) {
                                                FacilioModule module = modBean.getModule(modIdFieldEntry.getKey());
                                                FacilioField field = modBean.getField(modIdFieldEntry.getValue(), module.getName());
                                                if (field instanceof MultiLookupField) {
                                                    String valGenLinkName = getAlternateLinkNameForMultiLookupHandling(valueGenerator.getLinkName(),field);
                                                    criteria.addAndCondition(CriteriaAPI.getCondition(field.getCompleteColumnName(), module.getName() + "." + field.getName(), valGenLinkName, ScopeOperator.SCOPING_IS));
                                                } else {
                                                    criteria.addAndCondition(CriteriaAPI.getCondition(field, valueGenerator.getLinkName(), ScopeOperator.SCOPING_IS));
                                                }
                                                newScopingConfig.setCriteria(criteria);
                                            }
                                            newScopingConfigList.add(newScopingConfig);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    List<ScopingConfigContext> newNewScopingConfigList = newScopingConfigList;
                    ApplicationApi.addScopingConfigForApp(newNewScopingConfigList);
                }
            }
        }
    }

    private static boolean includeSelfModule(Long modId) throws Exception {
        if(modId == null) {
            return true;
        }
        List<String> excludeModules = Arrays.asList(FacilioConstants.ContextNames.STORE_ROOM);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(modId);
        if(module != null && excludeModules.contains(module.getName())) {
            return false;
        }
        return true;
    }

    public static ScopeHandler.ScopeFieldsAndCriteria constructSiteFieldsAndCriteria (FacilioModule module, boolean isUpdate) throws Exception{
        long currentSiteId = AccountUtil.getCurrentSiteId();
        return constructSiteFieldsAndCriteria(module, isUpdate, Collections.singletonList(currentSiteId));
    }

    public static ScopeHandler.ScopeFieldsAndCriteria constructSiteFieldsAndCriteria (FacilioModule module, boolean isUpdate, List<Long> siteIds) throws Exception{
        FacilioField siteIdField = FieldFactory.getSiteIdField(module);

        Criteria criteria = new Criteria();
        if (CollectionUtils.isNotEmpty(siteIds)) {// Currently we are handling site id criteria only for the module and not for all parent modules
            Condition siteCondition = CriteriaAPI.getCondition(siteIdField, StringUtils.join(siteIds, ","), NumberOperators.EQUALS);
            criteria.addAndCondition(siteCondition);
        }
        else {
            List<Long> mySitIds = CommonCommandUtil.getMySiteIds();
            if(CollectionUtils.isNotEmpty(mySitIds)) {
                Condition siteCondition = CriteriaAPI.getCondition(siteIdField, StringUtils.join(mySitIds, ","), NumberOperators.EQUALS);
                criteria.addAndCondition(siteCondition);
            }
        }
        List<FacilioField> fields = null;
        if (isUpdate) { //Will allow site id to be updated only if current site is -1. Also site fields of all parent modules needs to be added in fields because those have to be updated too
            if (CollectionUtils.isEmpty(siteIds)) {
                fields = new ArrayList<>();
                criteria = new Criteria();
                fields.add(siteIdField);
                FacilioModule extendedModule = module.getExtendModule();
                while(extendedModule != null) {
                    if (FieldUtil.isSiteIdFieldPresent(extendedModule)) {
                        fields.add(FieldFactory.getSiteIdField(extendedModule));
                    }
                    extendedModule = extendedModule.getExtendModule();
                }
            }
        }
        else {
            fields = Collections.singletonList(siteIdField);
        }
        return ScopeHandler.ScopeFieldsAndCriteria.of(fields, criteria);
    }

    private static String getAlternateLinkNameForMultiLookupHandling(String linkName, FacilioField field) {
        if(field instanceof MultiLookupField) {
            switch (linkName) {
                case "com.facilio.modules.SiteValueGenerator":
                    return "com.facilio.modules.ContainsSiteValueGenerator";
                case "com.facilio.modules.OrgUserValueGenerator":
                case "com.facilio.modules.CurrentUserValueGenerator" :
                case "com.facilio.modules.VendorValueGenerator" :
                case "com.facilio.modules.TenantValueGenerator" :
                case "com.facilio.modules.ClientValueGenerator" :
                    return "com.facilio.modules.ContainsUserValueGenerator";
            }
        }
        return linkName;
    }

    public static boolean isScopingAssociatedToUser(Long scopingId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(AccountConstants.getOrgUserAppsModule().getTableName())
                .select(AccountConstants.getOrgUserAppsFields())
                .andCondition(CriteriaAPI.getCondition("SCOPING_ID","scopingId", String.valueOf(scopingId), NumberOperators.EQUALS));
        List<Map<String, Object>> props = builder.get();
        return CollectionUtils.isEmpty(props);
    }

    public static void deleteCriteriaAssociatedWithUserScoping(Long scopingId) throws Exception {
        List<FacilioField> field = new ArrayList<>();
        field.add(FieldFactory.getField("criteriaId", "CRITERIA_ID", FieldType.NUMBER));
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getScopingConfigModule().getTableName())
                .select(field)
                .andCondition(CriteriaAPI.getCondition("SCOPING_ID","scopingId", String.valueOf(scopingId), NumberOperators.EQUALS));
        List<Map<String, Object>> props = builder.get();
        if(CollectionUtils.isNotEmpty(props)){
            List<Long> criteriaIds = props.stream().map(i->(Long)i.get("criteriaId")).collect(Collectors.toList());
            CriteriaAPI.batchDeleteCriteria(criteriaIds);
        }
    }

    public static String constructLinkName(String linkName, String displayName) throws Exception {
        UserScopeBean userScopeBean = (UserScopeBean) BeanFactory.lookup("UserScopeBean");
        List<ScopingContext> existingUserScopingList = userScopeBean.getUserScopingList(null,-1,-1);
        if (CollectionUtils.isEmpty(existingUserScopingList)) {
            if (StringUtils.isNotEmpty(linkName)) {
                return linkName;
            }
            if (StringUtils.isNotEmpty(displayName)) {
                return displayName.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
            }
        }
        if (CollectionUtils.isNotEmpty(existingUserScopingList)) {
            List<String> existingNames = existingUserScopingList.stream().map(ScopingContext::getLinkName).collect(Collectors.toList());
            String foundName = null;
            if (StringUtils.isNotEmpty(linkName)) {
                foundName = linkName;
            } else if (StringUtils.isNotEmpty(displayName)) {
                foundName = displayName.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
            }
            if (StringUtils.isEmpty(foundName)) {
                throw new IllegalArgumentException("Unable to construct link name for user scoping");
            }
            int i = 0;
            String constructedName = foundName;
            while (true) {
                if (existingNames.contains(constructedName)) {
                    constructedName = foundName + "_" + (++i);
                } else {
                    return constructedName;
                }
            }
        }
        throw new IllegalArgumentException("Unable to construct link name for user scoping");
    }
}