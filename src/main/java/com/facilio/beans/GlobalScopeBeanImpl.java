package com.facilio.beans;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class GlobalScopeBeanImpl implements GlobalScopeBean {
    @Override
    public List<GlobalScopeVariableContext> getAllScopeVariable() throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalScopeVariableModule().getTableName())
                .select(FieldFactory.getGlobalScopeVariableFields());
        List<Map<String, Object>> props = selectRecordBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<GlobalScopeVariableContext> scopeVariableList = FieldUtil.getAsBeanListFromMapList(props, GlobalScopeVariableContext.class);
            for (GlobalScopeVariableContext scopeVariable : scopeVariableList) {
                scopeVariable.setScopeVariableModulesFieldsList(getScopeVariableModulesFields(scopeVariable.getId()));
            }
            return scopeVariableList;
        }
        return new ArrayList<>();
    }

    @Override
    public Map<String, Pair<GlobalScopeVariableContext,ValueGeneratorContext>> getAllScopeVariableAndValueGen(Long appId) throws Exception {
        List<FacilioField> fieldList = FieldFactory.getValueGeneratorFields();
        fieldList.add(FieldFactory.getField("scopeVariableLinkName", "LINK_NAME", ModuleFactory.getGlobalScopeVariableModule(), FieldType.STRING));
        fieldList.add(FieldFactory.getField("status", "STATUS", ModuleFactory.getGlobalScopeVariableModule(), FieldType.BOOLEAN));
        fieldList.add(FieldFactory.getField("scopeVariableId", "ID", ModuleFactory.getGlobalScopeVariableModule(), FieldType.NUMBER));
        fieldList.add(FieldFactory.getField("appId", "APP_ID", ModuleFactory.getGlobalScopeVariableModule(), FieldType.NUMBER));

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getGlobalScopeVariableModule().getTableName())
                .select(fieldList)
                .innerJoin(ModuleFactory.getValueGeneratorModule().getTableName())
                .on("Value_Generators.ID = Global_Scope_Variable.VALUE_GENERATOR_ID");
        selectRecordBuilder.andCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(true), BooleanOperators.IS));
        selectRecordBuilder.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(appId), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectRecordBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            Map<String, Pair<GlobalScopeVariableContext,ValueGeneratorContext>> scopeVsValGen = new HashMap<>();
            for(Map<String,Object> prop : props){
                ValueGeneratorContext valGenContext = FieldUtil.getAsBeanFromMap(prop,ValueGeneratorContext.class);
                GlobalScopeVariableContext scopeVariableContext = new GlobalScopeVariableContext();
                scopeVariableContext.setLinkName(String.valueOf(prop.get("scopeVariableLinkName")));
                scopeVariableContext.setScopeVariableModulesFieldsList(getScopeVariableModulesFields(Long.parseLong(String.valueOf(prop.get("scopeVariableId")))));
                scopeVsValGen.put(String.valueOf(prop.get("scopeVariableLinkName")),Pair.of(scopeVariableContext,valGenContext));
            }
            return scopeVsValGen;
        }
        return null;
    }

    @Override
    public List<ScopeVariableModulesFields> getScopeVariableModulesFields(Long id) throws Exception {
        if (id != null) {
            Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getGlobalScopeVariableModulesFieldsFields());
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getGlobalScopeVariableModulesFieldsModule().getTableName())
                    .select(FieldFactory.getGlobalScopeVariableModulesFieldsFields())
                    .andCondition(CriteriaAPI.getCondition(fieldsMap.get("scopeVariableId"),Collections.singletonList(id),NumberOperators.EQUALS));
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
                .andCondition(CriteriaAPI.getCondition("LINK_NAME","linkName",linkName,StringOperators.IS));
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
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getGlobalScopeVariableModule()));
        Map<String, Object> props = selectRecordBuilder.fetchFirst();
        if (props != null && !props.isEmpty()) {
            GlobalScopeVariableContext scopeVariable = FieldUtil.getAsBeanFromMap(props, GlobalScopeVariableContext.class);
            return scopeVariable;
        }
        return null;
    }

    @Override
    public Long addScopeVariable(GlobalScopeVariableContext scopeVariable) throws Exception {
        if (scopeVariable != null) {
            scopeVariable.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
            scopeVariable.setSysCreatedTime(System.currentTimeMillis());
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
        if(scopeVariable != null){
            scopeVariable.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
            scopeVariable.setSysModifiedTime(System.currentTimeMillis());
            Map<String,Object> prop = FieldUtil.getAsProperties(scopeVariable);
            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getGlobalScopeVariableModule().getTableName())
                    .fields(FieldFactory.getGlobalScopeVariableFields())
                    .andCondition(CriteriaAPI.getIdCondition(scopeVariable.getId(),ModuleFactory.getGlobalScopeVariableModule()));
            updateBuilder.update(prop);
            if(prop != null){
                if(prop.containsKey("id")){
                    return (long) prop.get("id");
                }
            }
        }
        return null;
    }

    @Override
    public void addScopeVariableModulesFields(List<ScopeVariableModulesFields> scopeVariableModuleFields) throws Exception {
        if(CollectionUtils.isNotEmpty(scopeVariableModuleFields)){
            List<Map<String,Object>> props = FieldUtil.getAsMapList(scopeVariableModuleFields,ScopeVariableModulesFields.class);
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getGlobalScopeVariableModulesFieldsModule().getTableName())
                    .fields(FieldFactory.getGlobalScopeVariableModulesFieldsFields());
            insertBuilder.addRecords(props);
            insertBuilder.save();
        }
    }

    @Override
    public void deleteScopeVariableModulesFieldsByScopeVariableId(Long scopeVariableId) throws Exception {
        if(scopeVariableId != null){
            Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getGlobalScopeVariableModulesFieldsFields());
            GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getGlobalScopeVariableModulesFieldsModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition(fieldsMap.get("scopeVariableId"), Collections.singletonList(scopeVariableId), NumberOperators.EQUALS));
            deleteBuilder.delete();
        }
    }
}
