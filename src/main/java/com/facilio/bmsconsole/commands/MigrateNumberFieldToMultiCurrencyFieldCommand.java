package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.cache.CacheUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.MultiCurrencyOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.util.DBConf;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.modules.*;
import com.facilio.modules.fields.MultiCurrencyField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.*;

import static com.facilio.modules.FieldFactory.getNumberField;

public class MigrateNumberFieldToMultiCurrencyFieldCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        String fieldName = (String) context.get(FacilioConstants.ContextNames.MODULE_FIELD_NAME);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String baseCurrencyValueColumnName = (String) context.get("baseCurrencyValueColumnName");
        boolean revert = (boolean) context.getOrDefault("revert", false);

        if (revert) {
            return false;
        }

        MultiCurrencyField field = new MultiCurrencyField(modBean.getField(fieldName, moduleName), baseCurrencyValueColumnName);
        FacilioModule module = modBean.getModule(moduleName);
        FieldUtil.dropFieldFromCache(AccountUtil.getCurrentOrg().getOrgId(), field);

        FacilioCache cache = LRUCache.getFieldNameCache();
        String key = CacheUtil.FIELD_NAME_KEY(AccountUtil.getCurrentOrg().getOrgId(), fieldName, moduleName);
        cache.put(key, field);

//      to update datatype and display type in Fields
        Map<String, Object> updateProps = new HashMap<>();
        updateProps.put("dataType", FieldType.MULTI_CURRENCY_FIELD.getTypeAsInt());
        updateProps.put("displayTypeInt", FacilioField.FieldDisplayType.MULTI_CURRENCY.getIntValForDB());

        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(getNumberField("dataType", "DATA_TYPE", ModuleFactory.getFieldsModule()));
        updateFields.add(getNumberField("displayTypeInt", "DISPLAY_TYPE", ModuleFactory.getFieldsModule()));

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getFieldsModule().getTableName())
                .fields(updateFields)
                .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(field.getFieldId()), NumberOperators.EQUALS));
        updateRecordBuilder.update(updateProps);

//        to update display type in Form Fields
        updateProps = new HashMap<>();
        updateProps.put("displayType", FacilioField.FieldDisplayType.MULTI_CURRENCY.getIntValForDB());

        updateFields = new ArrayList<>();
        List<FacilioField> formFieldsFields = FieldFactory.getFormFieldsFields();
        Map<String, FacilioField> formFieldsFieldsAsMap = FieldFactory.getAsMap(formFieldsFields);
        updateFields.add(formFieldsFieldsAsMap.get("displayType"));

        updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getFormFieldsModule().getTableName())
                .fields(updateFields)
                .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(field.getFieldId()), NumberOperators.EQUALS));
        updateRecordBuilder.update(updateProps);

//      insert fieldId and BaseCurremcyValueColumnName in CurrencyFields Table
        Map<String, Object> insertProps = new HashMap<>();
        insertProps.put("fieldId", field.getFieldId());
        insertProps.put("baseCurrencyValueColumnName", baseCurrencyValueColumnName);

        List<FacilioField> currencyFieldFields = FieldFactory.getCurrencyFieldFields();

        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .fields(currencyFieldFields)
                .table(ModuleFactory.getCurrencyFieldsModule().getTableName());
        insertRecordBuilder.addRecord(insertProps);
        insertRecordBuilder.save();

        GenericSelectRecordBuilder selectRecordBuilder;

//      select existing Currency Field records
        int offset = 0;
        int limit = 5000;
        int iterationCount = 0;

        List<FacilioField> selectFields = new ArrayList<>();
        selectFields.add(field);
        selectFields.add(FieldFactory.getIdField(module));

        FacilioField baseCurrencyValueField = getBaseCurrencyFieldsForModule(module, field, baseCurrencyValueColumnName);

        List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();
        updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(Collections.singletonList(baseCurrencyValueField));

        selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(selectFields)
                .table(module.getTableName())
                .offset(offset)
                .limit(limit);

        List<Map<String, Object>> selectRecordBuilderProps = selectRecordBuilder.get();
        while (!selectRecordBuilderProps.isEmpty()) {
            for (Map<String, Object> prop : selectRecordBuilderProps) {
                GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
                updateVal.addWhereValue(FieldFactory.getIdField(module).getName(), prop.get(FieldFactory.getIdField(module).getName()));
                updateVal.addUpdateValue(baseCurrencyValueField.getName(), prop.get(field.getName()));
                batchUpdateList.add(updateVal);
            }

            updateRecordBuilder.batchUpdate(Collections.singletonList(FieldFactory.getIdField(module)), batchUpdateList);

            if (selectRecordBuilderProps.size() < limit) {
                break;
            }

            iterationCount++;
            offset = iterationCount * limit;
            selectRecordBuilder.offset(offset);
            selectRecordBuilderProps = selectRecordBuilder.get();
        }


//      update Conditions table with proper column name and computed where clause
        Map<Integer, Integer> numOperatorIdVsMultiCurrOpId = new HashMap<>();
        numOperatorIdVsMultiCurrOpId.put(NumberOperators.EQUALS.getOperatorId(), MultiCurrencyOperator.EQUALS.getOperatorId());
        numOperatorIdVsMultiCurrOpId.put(NumberOperators.NOT_EQUALS.getOperatorId(), MultiCurrencyOperator.NOT_EQUALS.getOperatorId());
        numOperatorIdVsMultiCurrOpId.put(NumberOperators.LESS_THAN.getOperatorId(), MultiCurrencyOperator.LESS_THAN.getOperatorId());
        numOperatorIdVsMultiCurrOpId.put(NumberOperators.LESS_THAN_EQUAL.getOperatorId(), MultiCurrencyOperator.LESS_THAN_EQUALS.getOperatorId());
        numOperatorIdVsMultiCurrOpId.put(NumberOperators.GREATER_THAN.getOperatorId(), MultiCurrencyOperator.GREATER_THAN.getOperatorId());
        numOperatorIdVsMultiCurrOpId.put(NumberOperators.GREATER_THAN_EQUAL.getOperatorId(), MultiCurrencyOperator.GREATER_THAN_EQUALS.getOperatorId());
        numOperatorIdVsMultiCurrOpId.put(NumberOperators.BETWEEN.getOperatorId(), MultiCurrencyOperator.BETWEEN.getOperatorId());
        numOperatorIdVsMultiCurrOpId.put(NumberOperators.NOT_BETWEEN.getOperatorId(), MultiCurrencyOperator.NOT_BETWEEN.getOperatorId());


        selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getConditionFields())
                .table(ModuleFactory.getConditionsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("COLUMN_NAME", "columnName", String.valueOf(field.getCompleteColumnName()), StringOperators.IS));
        List<Map<String, Object>> conditions = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(conditions)) {
            List<Condition> conditionList = FieldUtil.getAsBeanListFromMapList(conditions, Condition.class);
            Map<String, FacilioField> conditionFieldsAsMap = FieldFactory.getAsMap(FieldFactory.getConditionFields());
            updateFields = new ArrayList<>();
            updateFields.add(conditionFieldsAsMap.get("columnName"));
            updateFields.add(conditionFieldsAsMap.get("operatorId"));
            updateFields.add(conditionFieldsAsMap.get("computedWhereClause"));
            batchUpdateList = new ArrayList<>();
            for (Condition condition : conditionList) {
                condition.setColumnName(getBaseCurrencyValueColumnName(field, baseCurrencyValueColumnName));
                condition.setOperatorId(numOperatorIdVsMultiCurrOpId.get(condition.getOperatorId()));
                condition.setOperator(DBConf.getInstance().getOperator(condition.getOperatorId()));
                condition.setComputedWhereClause(null);
                condition.computeAndGetWhereClause();

                GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();

                updateVal.addWhereValue(conditionFieldsAsMap.get("conditionId").getName(), condition.getConditionId());
                updateVal.addUpdateValue(conditionFieldsAsMap.get("columnName").getName(), condition.getColumnName());
                updateVal.addUpdateValue(conditionFieldsAsMap.get("operatorId").getName(), condition.getOperatorId());
                updateVal.addUpdateValue(conditionFieldsAsMap.get("computedWhereClause").getName(), condition.getComputedWhereClause());

                batchUpdateList.add(updateVal);
            }
            updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .fields(updateFields)
                    .table(ModuleFactory.getConditionsModule().getTableName());
            updateRecordBuilder.batchUpdate(Collections.singletonList(conditionFieldsAsMap.get("conditionId")), batchUpdateList);
        }
        return false;
    }
    private String getBaseCurrencyValueColumnName(FacilioField multiCurrencyField, String baseCurrencyColumnName) {
        String tableName = multiCurrencyField.getTableName();
        String alias = multiCurrencyField.getTableAlias();

        if (StringUtils.isNotEmpty(alias)) {
            return alias + "." + baseCurrencyColumnName;
        } else if (StringUtils.isNotEmpty(tableName)) {
            return tableName + "." + baseCurrencyColumnName;
        } else {
            return baseCurrencyColumnName;
        }
    }

    public  FacilioField getBaseCurrencyFieldsForModule(FacilioModule module, FacilioField field, String baseCurrencyValueColumnName) {
        String BASE_CURRENCY_FIELD_NAME = "##{0}##"+"baseCurrencyValue";
        String baseCurrencyFieldName = MessageFormat.format(BASE_CURRENCY_FIELD_NAME, field.getName());
        return FieldFactory.getField(baseCurrencyFieldName, baseCurrencyValueColumnName, module, FieldType.DECIMAL);
    }
}
