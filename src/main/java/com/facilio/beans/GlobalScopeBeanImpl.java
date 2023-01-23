package com.facilio.beans;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.exception.CircularDependencyException;
import com.facilio.bmsconsole.exception.DependencyException;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.bmsconsoleV3.util.GlobalScopeUtil;
import com.facilio.datastructure.dag.DAG;
import com.facilio.datastructure.dag.DAGCache;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class GlobalScopeBeanImpl implements GlobalScopeBean {
    @Override
    public List<GlobalScopeVariableContext> getAllScopeVariable() throws Exception {
        return getAllScopeVariable(null);
    }

    public List<GlobalScopeVariableContext> getSwitchVariable() throws Exception {
        if(AccountUtil.getCurrentApp() != null) {
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getGlobalScopeVariableModule().getTableName())
                    .select(FieldFactory.getGlobalScopeVariableFields())
                    .andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS))
                    .andCondition(CriteriaAPI.getCondition("SHOW_SWITCH", "showSwitch", String.valueOf(Boolean.TRUE), BooleanOperators.IS))
                    .andCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(Boolean.TRUE), BooleanOperators.IS))
                    .andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(AccountUtil.getCurrentApp().getId()), NumberOperators.EQUALS));
            List<Map<String, Object>> props = selectRecordBuilder.get();
            if (CollectionUtils.isNotEmpty(props)) {
                List<GlobalScopeVariableContext> switchVariableList = FieldUtil.getAsBeanListFromMapList(props, GlobalScopeVariableContext.class);
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                for (GlobalScopeVariableContext switchVariable : switchVariableList) {
                    if (switchVariable.getApplicableModuleId() != null) {

                        FacilioModule module = modBean.getModule(switchVariable.getApplicableModuleId());
                        if(module != null ) {
                            switchVariable.setApplicableModuleName(module.getName());
                        }
                    }
                }
                return switchVariableList;
            }
        }
            return new ArrayList<>();

    }

    @Override
    public Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> getAllScopeVariableAndValueGen(Long appId) throws Exception {
        return getAllScopeVariableAndValueGen(appId, false,false);
    }

    public Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> getAllScopeVariableAndValueGen(Long appId, boolean skipDependenacyCheck, boolean includeInactive) throws Exception {
        List<FacilioField> fieldList = FieldFactory.getValueGeneratorFields();
        fieldList.add(FieldFactory.getField("scopeVariableLinkName", "LINK_NAME", ModuleFactory.getGlobalScopeVariableModule(), FieldType.STRING));
        fieldList.add(FieldFactory.getField("status", "STATUS", ModuleFactory.getGlobalScopeVariableModule(), FieldType.BOOLEAN));
        fieldList.add(FieldFactory.getField("scopeVariableId", "ID", ModuleFactory.getGlobalScopeVariableModule(), FieldType.NUMBER));
        fieldList.add(FieldFactory.getField("appId", "APP_ID", ModuleFactory.getGlobalScopeVariableModule(), FieldType.NUMBER));
        fieldList.add(FieldFactory.getField("applicableModuleId", "APPLICABLE_MODULE_ID", ModuleFactory.getGlobalScopeVariableModule(), FieldType.NUMBER));
        fieldList.add(FieldFactory.getField("applicableModuleName", "APPLICABLE_MODULE_NAME", ModuleFactory.getGlobalScopeVariableModule(), FieldType.STRING));
        fieldList.add(FieldFactory.getField("sysDeleted", "SYS_DELETED", ModuleFactory.getGlobalScopeVariableModule(), FieldType.BOOLEAN));
        fieldList.add(FieldFactory.getField("type", "TYPE", ModuleFactory.getGlobalScopeVariableModule(), FieldType.NUMBER));

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalScopeVariableModule().getTableName())
                .select(fieldList)
                .leftJoin(ModuleFactory.getValueGeneratorModule().getTableName())
                .on("Value_Generators.ID = Global_Scope_Variable.VALUE_GENERATOR_ID");
        if(!includeInactive) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(true), BooleanOperators.IS));
        }
        selectRecordBuilder.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(appId), NumberOperators.EQUALS));
        selectRecordBuilder.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS));
        selectRecordBuilder.orderBy("Global_Scope_Variable.ID");

        List<Map<String, Object>> props = selectRecordBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> scopeVsValGen = new LinkedHashMap<>();
            for (Map<String, Object> prop : props) {
                ValueGeneratorContext valGenContext = FieldUtil.getAsBeanFromMap(prop, ValueGeneratorContext.class);
                GlobalScopeVariableContext scopeVariableContext = new GlobalScopeVariableContext();
                scopeVariableContext.setLinkName(String.valueOf(prop.get("scopeVariableLinkName")));
                scopeVariableContext.setType((int) prop.get("type"));
                if (prop.containsKey("applicableModuleId")) {
                    scopeVariableContext.setApplicableModuleId(Long.valueOf(String.valueOf(prop.get("applicableModuleId"))));
                }
                if (prop.containsKey("applicableModuleName")) {
                    scopeVariableContext.setApplicableModuleName(String.valueOf(prop.get("applicableModuleName")));
                }
                scopeVariableContext.setScopeVariableModulesFieldsList(getScopeVariableModulesFields(Long.parseLong(String.valueOf(prop.get("scopeVariableId")))));
                scopeVsValGen.put(String.valueOf(prop.get("scopeVariableLinkName")), Pair.of(scopeVariableContext, valGenContext));
            }
            if (skipDependenacyCheck) {
                return scopeVsValGen;
            }
            return getOrderedScopeVariableMap(scopeVsValGen);
        }
        return null;
    }

    public List<GlobalScopeVariableContext> getAllScopeVariable(Long appId) throws Exception {
        return getAllScopeVariable(appId, -1, -1, null, false);
    }

    public List<GlobalScopeVariableContext> getAllScopeVariable(Long appId, boolean fetchDeleted) throws Exception {
        return getAllScopeVariable(appId, -1, -1, null, fetchDeleted);
    }

    @Override
    public List<GlobalScopeVariableContext> getAllScopeVariable(Long appId, int page, int perPage, String searchQuery, boolean fetchDeleted) throws Exception {
        List<FacilioField> fieldList = FieldFactory.getGlobalScopeVariableFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldList);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalScopeVariableModule().getTableName())
                .select(FieldFactory.getGlobalScopeVariableFields());
        if (!fetchDeleted) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS));
        }
        if (appId != null) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(appId), NumberOperators.EQUALS));
        }
        if (page > -1 && perPage > -1) {
            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }
            selectRecordBuilder.offset(offset);
            selectRecordBuilder.limit(perPage);
        }
        if (StringUtils.isNotEmpty(searchQuery)) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("displayName"), searchQuery, StringOperators.CONTAINS));
        }
        List<Map<String, Object>> props = selectRecordBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<GlobalScopeVariableContext> scopeVariableList = FieldUtil.getAsBeanListFromMapList(props, GlobalScopeVariableContext.class);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            ValueGeneratorBean valGenBean = (ValueGeneratorBean) BeanFactory.lookup("ValueGeneratorBean");
            for (GlobalScopeVariableContext scopeVariable : scopeVariableList) {
                if (scopeVariable.getValueGeneratorId() != null) {
                    ValueGeneratorContext valGen = valGenBean.getValueGenerator(scopeVariable.getValueGeneratorId());
                    if (valGen != null) {
                        scopeVariable.setValueGeneratorDisplayName(valGen.getDisplayName());
                    }
                }
                FacilioModule module = null;
                if (scopeVariable.getApplicableModuleName() != null) {
                    module = modBean.getModule(scopeVariable.getApplicableModuleName());
                } else if (scopeVariable.getApplicableModuleId() != null) {
                    module = modBean.getModule(scopeVariable.getApplicableModuleId());
                }
                if (module != null) {
                    scopeVariable.setApplicableModuleDisplayName(module.getDisplayName());
                }
                scopeVariable.setScopeVariableModulesFieldsList(getScopeVariableModulesFields(scopeVariable.getId()));
            }
            return scopeVariableList;
        }
        return new ArrayList<>();
    }

    @Override
    public long getScopeVariableCount(Long appId, String searchQuery) throws Exception {
        List<FacilioField> fieldList = FieldFactory.getGlobalScopeVariableFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldList);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalScopeVariableModule().getTableName())
                .select(FieldFactory.getCountField())
                .andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS));
        if (appId != null) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(appId), NumberOperators.EQUALS));
        }
        if (StringUtils.isNotEmpty(searchQuery)) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("displayName"), searchQuery, StringOperators.CONTAINS));
        }

        Map<String, Object> props = selectRecordBuilder.fetchFirst();
        long count = MapUtils.isNotEmpty(props) ? (long) props.get("count") : 0;
        return count;
    }

    @Override
    public List<ScopeVariableModulesFields> getScopeVariableModulesFields(Long id) throws Exception {
        if (id != null) {
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getGlobalScopeVariableModulesFieldsFields());
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getGlobalScopeVariableModulesFieldsModule().getTableName())
                    .select(FieldFactory.getGlobalScopeVariableModulesFieldsFields())
                    .andCondition(CriteriaAPI.getCondition(fieldsMap.get("scopeVariableId"), Collections.singletonList(id), NumberOperators.EQUALS));
            List<Map<String, Object>> props = selectRecordBuilder.get();
            if (CollectionUtils.isNotEmpty(props)) {
                List<ScopeVariableModulesFields> scopeVariableList = FieldUtil.getAsBeanListFromMapList(props, ScopeVariableModulesFields.class);
                return scopeVariableList;
            }
        }
        return new ArrayList<>();
    }

    @Override
    public GlobalScopeVariableContext getScopeVariable(String linkName) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalScopeVariableModule().getTableName())
                .select(FieldFactory.getGlobalScopeVariableFields())
                .andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", linkName, StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS));

        List<Map<String, Object>> props = selectRecordBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<GlobalScopeVariableContext> scopeVariableList = FieldUtil.getAsBeanListFromMapList(props, GlobalScopeVariableContext.class);
            return scopeVariableList.get(0);
        }
        return null;
    }

    @Override
    public GlobalScopeVariableContext getScopeVariable(Long id) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalScopeVariableModule().getTableName())
                .select(FieldFactory.getGlobalScopeVariableFields())
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getGlobalScopeVariableModule()))
                .andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS));
        Map<String, Object> props = selectRecordBuilder.fetchFirst();
        if (props != null && !props.isEmpty()) {
            GlobalScopeVariableContext scopeVariable = FieldUtil.getAsBeanFromMap(props, GlobalScopeVariableContext.class);
            if (scopeVariable != null && scopeVariable.getApplicableModuleId() != null) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                scopeVariable.setApplicableModuleName(modBean.getModule(scopeVariable.getApplicableModuleId()).getName());
            }
            scopeVariable.setScopeVariableModulesFieldsList(getScopeVariableModulesFields(id));
            return scopeVariable;
        }
        return null;
    }

    @Override
    public Long addScopeVariable(GlobalScopeVariableContext scopeVariable) throws Exception {
        if (scopeVariable != null) {
            if (scopeVariable.getAppId() == null) {
                throw new IllegalArgumentException("App Id cannot be null");
            }
            scopeVariable.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
            scopeVariable.setSysCreatedTime(System.currentTimeMillis());
            scopeVariable.setLinkName(constructLinkName(scopeVariable.getLinkName(), scopeVariable.getDisplayName(), scopeVariable.getAppId()));
            validateScopeVariable(scopeVariable);
            checkDependency(scopeVariable);
            Map<String, Object> prop = FieldUtil.getAsProperties(scopeVariable);
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getGlobalScopeVariableModule().getTableName())
                    .fields(FieldFactory.getGlobalScopeVariableFields());
            insertBuilder.addRecord(prop);
            insertBuilder.save();
            if (prop != null) {
                if (prop.containsKey("id")) {
                    return (long) prop.get("id");
                }
            }
        }
        return null;
    }

    @Override
    public Long updateScopeVariable(GlobalScopeVariableContext scopeVariable) throws Exception {
        if (scopeVariable != null) {
            scopeVariable.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
            scopeVariable.setSysModifiedTime(System.currentTimeMillis());
            Map<String, Object> prop = FieldUtil.getAsProperties(scopeVariable, true);
            if (scopeVariable.getLinkName() == null) {
                prop.remove("linkName");
            }
            prop.remove("showSwitch");
            validateScopeVariable(scopeVariable);
            GlobalScopeVariableContext existingScopeVariable = getScopeVariable(scopeVariable.getId());
            existingScopeVariable.setScopeVariableModulesFieldsList(scopeVariable.getScopeVariableModulesFieldsList());
            checkDependency(existingScopeVariable);
            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getGlobalScopeVariableModule().getTableName())
                    .fields(FieldFactory.getGlobalScopeVariableFields())
                    .andCondition(CriteriaAPI.getIdCondition(scopeVariable.getId(), ModuleFactory.getGlobalScopeVariableModule()))
                    .ignoreSplNullHandling()
                    .andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS));
            updateBuilder.update(prop);
            if (prop != null) {
                if (prop.containsKey("id")) {
                    return (long) prop.get("id");
                }
            }
        }
        return null;
    }

    @Override
    public void addScopeVariableModulesFields(List<ScopeVariableModulesFields> scopeVariableModuleFields) throws Exception {
        if (CollectionUtils.isNotEmpty(scopeVariableModuleFields)) {
            List<Map<String, Object>> props = FieldUtil.getAsMapList(scopeVariableModuleFields, ScopeVariableModulesFields.class);
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getGlobalScopeVariableModulesFieldsModule().getTableName())
                    .fields(FieldFactory.getGlobalScopeVariableModulesFieldsFields());
            insertBuilder.addRecords(props);
            insertBuilder.save();
        }
    }

    @Override
    public void deleteScopeVariableModulesFieldsByScopeVariableId(Long scopeVariableId) throws Exception {
        if (scopeVariableId != null) {
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getGlobalScopeVariableModulesFieldsFields());
            GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getGlobalScopeVariableModulesFieldsModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition(fieldsMap.get("scopeVariableId"), Collections.singletonList(scopeVariableId), NumberOperators.EQUALS));
            deleteBuilder.delete();
        }
    }

    @Override
    public void deleteScopeVariable(Long id,Long appId) throws Exception {
        GlobalScopeVariableContext globalScopeVariable = new GlobalScopeVariableContext();
        globalScopeVariable.setSysDeletedBy(AccountUtil.getCurrentUser().getId());
        globalScopeVariable.setSysDeletedTime(System.currentTimeMillis());
        globalScopeVariable.setDeleted(true);
        Map<String, Object> prop = FieldUtil.getAsProperties(globalScopeVariable);
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getGlobalScopeVariableModule().getTableName())
                .fields(FieldFactory.getGlobalScopeVariableFields())
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getGlobalScopeVariableModule()));
        updateBuilder.update(prop);
    }

    private String constructLinkName(String linkName, String displayName, Long appId) throws Exception {
        List<GlobalScopeVariableContext> existingVariableList = getAllScopeVariable(appId, true);
        if (CollectionUtils.isEmpty(existingVariableList)) {
            if (StringUtils.isNotEmpty(linkName)) {
                return linkName;
            }
            if (StringUtils.isNotEmpty(displayName)) {
                return displayName.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
            }
        }
        if (CollectionUtils.isNotEmpty(existingVariableList)) {
            List<String> existingNames = existingVariableList.stream().map(GlobalScopeVariableContext::getLinkName).collect(Collectors.toList());
            String foundName = null;
            if (StringUtils.isNotEmpty(linkName)) {
                foundName = linkName;
            } else if (StringUtils.isNotEmpty(displayName)) {
                foundName = displayName.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
            }
            if (StringUtils.isEmpty(foundName)) {
                throw new IllegalArgumentException("Unable to construct link name for global scope variable");
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
        throw new IllegalArgumentException("Unable to construct link name for global scope variable");
    }

    public void setStatus(Long appId, Long scopeVariableId, boolean status) throws Exception {
        if (scopeVariableId != null) {
            GlobalScopeVariableContext scopeVariable = new GlobalScopeVariableContext();
            scopeVariable.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
            scopeVariable.setSysModifiedTime(System.currentTimeMillis());
            scopeVariable.setStatus(status);
            Map<String, Object> prop = FieldUtil.getAsProperties(scopeVariable, true);
            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getGlobalScopeVariableModule().getTableName())
                    .fields(FieldFactory.getGlobalScopeVariableFields())
                    .andCondition(CriteriaAPI.getIdCondition(scopeVariableId, ModuleFactory.getGlobalScopeVariableModule()));
            updateBuilder.update(prop);
        }
    }

    public void setSwitchStatus(Long appId, Long scopeVariableId, boolean status) throws Exception {
        if (scopeVariableId != null) {
            GlobalScopeVariableContext scopeVariable = new GlobalScopeVariableContext();
            scopeVariable.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
            scopeVariable.setSysModifiedTime(System.currentTimeMillis());
            scopeVariable.setShowSwitch(status);
            Map<String, Object> prop = FieldUtil.getAsProperties(scopeVariable, true);
            GenericUpdateRecordBuilder enableBuilder = getGlobalScopeVariableUpdateBuilder();
            enableBuilder.andCondition(CriteriaAPI.getIdCondition(scopeVariableId, ModuleFactory.getGlobalScopeVariableModule()));
            enableBuilder.update(prop);
            if (status) {
                GlobalScopeVariableContext globalScopeVariable = getScopeVariable(scopeVariableId);
                if (globalScopeVariable != null) {
                    GlobalScopeVariableContext updateContext = new GlobalScopeVariableContext();
                    updateContext.setShowSwitch(false);
                    GenericUpdateRecordBuilder disableBuilder = getGlobalScopeVariableUpdateBuilder();
                    disableBuilder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(scopeVariableId), NumberOperators.NOT_EQUALS));
                    disableBuilder.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(globalScopeVariable.getAppId()), NumberOperators.EQUALS));
                    Map<String, Object> updateProp = FieldUtil.getAsProperties(updateContext, true);
                    disableBuilder.update(updateProp);
                }
            }
        }
    }

    private GenericUpdateRecordBuilder getGlobalScopeVariableUpdateBuilder() {
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getGlobalScopeVariableModule().getTableName())
                .fields(FieldFactory.getGlobalScopeVariableFields());
        return updateBuilder;
    }

    private void validateScopeVariable(GlobalScopeVariableContext scopeVariable) throws Exception {
        if (scopeVariable != null) {
            if(scopeVariable.getAppId() == null) {
                throw new IllegalArgumentException("App id cannot be null");
            }
            Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getGlobalScopeVariableFields());
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getGlobalScopeVariableModule().getTableName())
                    .select(FieldFactory.getGlobalScopeVariableFields())
                    .andCondition(CriteriaAPI.getCondition(fieldsMap.get("deleted"), String.valueOf(Boolean.FALSE), BooleanOperators.IS))
                    .andCondition(CriteriaAPI.getCondition(fieldsMap.get("appId"), String.valueOf(scopeVariable.getAppId()),NumberOperators.EQUALS));

            if(scopeVariable.getApplicableModuleName() == null && scopeVariable.getApplicableModuleId() == null){
                throw new IllegalArgumentException("Module cannot be null");
            }
            Criteria criteria = new Criteria();
            if(scopeVariable.getApplicableModuleName() != null){
                criteria.addOrCondition(CriteriaAPI.getCondition(fieldsMap.get("applicableModuleName"),scopeVariable.getApplicableModuleName(),StringOperators.IS));
            }
            if(scopeVariable.getApplicableModuleId() != null){
                criteria.addOrCondition(CriteriaAPI.getCondition(fieldsMap.get("applicableModuleId"),String.valueOf(scopeVariable.getApplicableModuleId()),StringOperators.IS));
            }
            if(scopeVariable.getId() != -1L){
                selectRecordBuilder.andCondition(CriteriaAPI.getCondition("ID","id",String.valueOf(scopeVariable.getId()),NumberOperators.NOT_EQUALS));
            }
            selectRecordBuilder.andCriteria(criteria);
            List<Map<String, Object>> props = selectRecordBuilder.get();
            if(CollectionUtils.isNotEmpty(props)){
                throw new IllegalArgumentException("Scope variable with same module already exists");
            }
        }
    }

    private void checkDependency(GlobalScopeVariableContext scopeVariable) throws Exception {
        Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> existingScopeVariables = getAllScopeVariableAndValueGen(scopeVariable.getAppId(), true,true);
        checkDependency(scopeVariable,existingScopeVariables);
    }
    private void checkDependency(GlobalScopeVariableContext currentScopeVariable, Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> scopeVariables) throws RESTException {
        try{
            if(MapUtils.isEmpty(scopeVariables)){
                scopeVariables = new HashMap<>();
            }
            scopeVariables.put(currentScopeVariable.getLinkName(),Pair.of(currentScopeVariable,null));
            Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> orderedScopeVariables = new LinkedHashMap<>();
            if (MapUtils.isNotEmpty(scopeVariables)) {
                for (Map.Entry<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> scopeVariable : scopeVariables.entrySet()) {
                    if (!orderedScopeVariables.containsKey(scopeVariable.getKey())) {
                        String linkname = findDependentScopeVariable(currentScopeVariable, scopeVariables, orderedScopeVariables, new ArrayList<>(), 0);
                        orderedScopeVariables.put(linkname, scopeVariables.get(linkname));
                    }
                }
                if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SCOPE_SUBQUERY)) {
                    GlobalScopeUtil.constructAndGetGraph(scopeVariables.values().stream().map(Pair::getLeft).collect(Collectors.toList()), 3, true);
                }
            }
        } catch (CircularDependencyException e){
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> getOrderedScopeVariableMap(Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> scopeVariables) throws Exception {
        Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> orderedScopeVariables = new LinkedHashMap<>();
        if (MapUtils.isNotEmpty(scopeVariables)) {
            for (Map.Entry<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> scopeVariable : scopeVariables.entrySet()) {
                if (!orderedScopeVariables.containsKey(scopeVariable.getKey())) {
                    String linkname = findDependentScopeVariable(scopeVariable.getValue().getLeft(), scopeVariables, orderedScopeVariables, new ArrayList<>(), 0);
                    orderedScopeVariables.put(linkname, scopeVariables.get(linkname));
                }
            }
        }
        return orderedScopeVariables;
    }

    public String findDependentScopeVariable(GlobalScopeVariableContext currentScopeVariable,Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> allScopeVariables,Map<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> orderedScopeVariables,List<String> visitedScopes,int i) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(visitedScopes.contains(currentScopeVariable.getLinkName()) || i > allScopeVariables.size()) {
            throw new CircularDependencyException("Circular dependency detected");
        }
        Long currentVariableModuleId = currentScopeVariable.getApplicableModuleId();
        if(currentVariableModuleId != null) {
            for(Map.Entry<String, Pair<GlobalScopeVariableContext, ValueGeneratorContext>> iter : allScopeVariables.entrySet()){
                if(iter.getValue() != null && iter.getValue().getLeft() != null) {
                    GlobalScopeVariableContext iterScopeVariable = iter.getValue().getLeft();
                    if(!orderedScopeVariables.containsKey(iterScopeVariable.getLinkName())){
                        List<ScopeVariableModulesFields> modulesFieldsList = iterScopeVariable.getScopeVariableModulesFieldsList();
                        if (CollectionUtils.isNotEmpty(modulesFieldsList)) {
                            for (ScopeVariableModulesFields moduleField : modulesFieldsList) {
                                if (iterScopeVariable.getApplicableModuleId() != null || StringUtils.isNotEmpty(iterScopeVariable.getApplicableModuleName())) {
                                    List<Long> extendedModuleIds = new ArrayList<>();
                                    FacilioModule module;
                                    if(iterScopeVariable.getApplicableModuleId() != null) {
                                        module = modBean.getModule(iterScopeVariable.getApplicableModuleId());
                                        extendedModuleIds = module.getExtendedModuleIds();
                                    }
                                    if (CollectionUtils.isNotEmpty(extendedModuleIds) && extendedModuleIds.contains(moduleField.getModuleId())) {
                                        throw new CircularDependencyException("Circular dependency detected");
                                    }
                                    if (moduleField.getModuleId() != null && currentVariableModuleId != null) {
                                        module = modBean.getModule(moduleField.getModuleId());
                                        extendedModuleIds = module.getExtendedModuleIds();
                                        if (extendedModuleIds.contains(currentVariableModuleId)) {
                                            currentVariableModuleId = iterScopeVariable.getApplicableModuleId();
                                            visitedScopes.add(currentScopeVariable.getLinkName());
                                            String linkname = findDependentScopeVariable(iter.getValue().getLeft(), allScopeVariables, orderedScopeVariables, visitedScopes, ++i);
                                            orderedScopeVariables.put(linkname, allScopeVariables.get(linkname));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return currentScopeVariable.getLinkName();
    }

    public DAGCache getGlobalScopeGraph(Long appId) throws Exception {
        DAG dag = GlobalScopeUtil.constructAndGetGraph(appId,4);
        DAGCache dagCache = new DAGCache(dag);
        return dagCache;
    }
}
