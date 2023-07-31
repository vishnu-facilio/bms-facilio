package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.MultiCurrencyOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.util.DBConf;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.facilio.modules.FieldFactory.getNumberField;

public class RevertMultiCurrencyFieldMigrationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        String fieldName = (String) context.get(FacilioConstants.ContextNames.MODULE_FIELD_NAME);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String baseCurrencyValueColumnName = (String) context.get("baseCurrencyValueColumnName");
        boolean revert = (boolean) context.getOrDefault("revert", false);

        if (!revert) {
            return false;
        }

        FacilioField field = modBean.getField(fieldName, moduleName);
        FacilioModule module = modBean.getModule(moduleName);
//      revert dataType and displayType in Fields
        Map<String, Object> updateProps = new HashMap<>();
        updateProps.put("dataType", FieldType.DECIMAL.getTypeAsInt());
        updateProps.put("displayTypeInt", FacilioField.FieldDisplayType.DECIMAL.getIntValForDB());

        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(getNumberField("dataType", "DATA_TYPE", ModuleFactory.getFieldsModule()));
        updateFields.add(getNumberField("displayTypeInt", "DISPLAY_TYPE", ModuleFactory.getFieldsModule()));

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getFieldsModule().getTableName())
                .fields(updateFields)
                .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(field.getFieldId()), NumberOperators.EQUALS));
        updateRecordBuilder.update(updateProps);

//        revert display type in Form Fields
        updateProps = new HashMap<>();
        updateProps.put("displayType", FacilioField.FieldDisplayType.DECIMAL.getIntValForDB());

        updateFields = new ArrayList<>();
        List<FacilioField> formFieldsFields = FieldFactory.getFormFieldsFields();
        Map<String, FacilioField> formFieldsFieldsAsMap = FieldFactory.getAsMap(formFieldsFields);
        updateFields.add(formFieldsFieldsAsMap.get("displayType"));

        updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getFormFieldsModule().getTableName())
                .fields(updateFields)
                .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(field.getFieldId()), NumberOperators.EQUALS));
        updateRecordBuilder.update(updateProps);

//      delete Currency Fields Entry
        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getCurrencyFieldsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(field.getFieldId()), NumberOperators.EQUALS));
        deleteRecordBuilder.delete();

//      update Conditions table with proper columnname and computed where clause
        Map<Integer, Integer> multiCurrOpIdVsNumOperatorId = new HashMap<>();
        multiCurrOpIdVsNumOperatorId.put(MultiCurrencyOperator.EQUALS.getOperatorId(), NumberOperators.EQUALS.getOperatorId());
        multiCurrOpIdVsNumOperatorId.put(MultiCurrencyOperator.NOT_EQUALS.getOperatorId(), NumberOperators.NOT_EQUALS.getOperatorId());
        multiCurrOpIdVsNumOperatorId.put(MultiCurrencyOperator.LESS_THAN.getOperatorId(), NumberOperators.LESS_THAN.getOperatorId());
        multiCurrOpIdVsNumOperatorId.put(MultiCurrencyOperator.LESS_THAN_EQUALS.getOperatorId(), NumberOperators.LESS_THAN_EQUAL.getOperatorId());
        multiCurrOpIdVsNumOperatorId.put(MultiCurrencyOperator.GREATER_THAN.getOperatorId(), NumberOperators.GREATER_THAN.getOperatorId());
        multiCurrOpIdVsNumOperatorId.put(MultiCurrencyOperator.GREATER_THAN_EQUALS.getOperatorId(), NumberOperators.GREATER_THAN_EQUAL.getOperatorId());
        multiCurrOpIdVsNumOperatorId.put(MultiCurrencyOperator.BETWEEN.getOperatorId(), NumberOperators.BETWEEN.getOperatorId());
        multiCurrOpIdVsNumOperatorId.put(MultiCurrencyOperator.NOT_BETWEEN.getOperatorId(), NumberOperators.NOT_BETWEEN.getOperatorId());

        multiCurrOpIdVsNumOperatorId.put(CommonOperators.IS_EMPTY.getOperatorId(), CommonOperators.IS_EMPTY.getOperatorId());
        multiCurrOpIdVsNumOperatorId.put(CommonOperators.IS_NOT_EMPTY.getOperatorId(), CommonOperators.IS_NOT_EMPTY.getOperatorId());



        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getConditionFields())
                .table(ModuleFactory.getConditionsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("COLUMN_NAME", "columnName", String.valueOf(getBaseCurrencyValueColumnName(field, baseCurrencyValueColumnName)), StringOperators.IS));
        List<Map<String, Object>> maps = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(maps)) {
            List<Condition> conditionList = FieldUtil.getAsBeanListFromMapList(maps, Condition.class);
            Map<String, FacilioField> conditionFieldsAsMap = FieldFactory.getAsMap(FieldFactory.getConditionFields());
            List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();
            updateFields = new ArrayList<>();
            updateFields.add(conditionFieldsAsMap.get("columnName"));
            updateFields.add(conditionFieldsAsMap.get("operatorId"));
            updateFields.add(conditionFieldsAsMap.get("computedWhereClause"));

            for (Condition condition : conditionList) {
                condition.setColumnName(field.getCompleteColumnName());
                condition.setOperatorId(multiCurrOpIdVsNumOperatorId.get(condition.getOperatorId()));
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
}
