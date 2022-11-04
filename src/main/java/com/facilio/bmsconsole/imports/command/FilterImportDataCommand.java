package com.facilio.bmsconsole.imports.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.PredicateUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FilterImportDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        Collection<Map<String, Object>> rawInputs = Constants.getBulkRawInput(context);

        Integer setting = importProcessContext.getImportSetting();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(importProcessContext.getModule().getName());
        List<FacilioField> allFields = modBean.getAllFields(module.getName());

        if (setting == ImportProcessContext.ImportSetting.INSERT.getValue()) {
            context.put(ImportAPI.ImportProcessConstants.INSERT_RECORDS, rawInputs);
        }
        else if (setting == ImportProcessContext.ImportSetting.INSERT_SKIP.getValue()) {
            List<Map<String, Object>> newRawInputs = new ArrayList<>();

            JSONObject meta = importProcessContext.getImportJobMetaJson();
            ArrayList<String> insertFields = (ArrayList<String>) meta.get(ImportAPI.ImportProcessConstants.INSERT_FIELDS);

            List<FacilioField> criteriaFields = getFieldList(module, insertFields);

            for (Map<String, Object> datum : rawInputs) {
                V3Context dbRecord = getDBRecord(module, allFields, criteriaFields, datum);
                if (dbRecord == null) { // add only when the record is not found in database.
                    newRawInputs.add(datum);
                }
            }

            context.put(ImportAPI.ImportProcessConstants.INSERT_RECORDS, newRawInputs);
        }
        else if (setting == ImportProcessContext.ImportSetting.UPDATE.getValue() ||
                setting == ImportProcessContext.ImportSetting.UPDATE_NOT_NULL.getValue()) {
            // get data and update
            JSONObject meta = importProcessContext.getImportJobMetaJson();
            ArrayList<String> updateFields = (ArrayList<String>) meta.get(ImportAPI.ImportProcessConstants.UPDATE_FIELDS);
            List<FacilioField> fieldList = getFieldList(module, updateFields);

            List<Map<String, Object>> newRawInputs = new ArrayList<>();
            List<V3Context> oldRecords = new ArrayList<>();
            for (Map<String, Object> datum : rawInputs) {
                V3Context dbRecord = getDBRecord(module, allFields, fieldList, datum);
                if (dbRecord == null || dbRecord.getId() <= 0) { // skip if the record is not found id db.
                    continue;
                }

                datum.put("id", dbRecord.getId());
                newRawInputs.add(datum);
                oldRecords.add(dbRecord);
            }

            if (setting == ImportProcessContext.ImportSetting.UPDATE_NOT_NULL.getValue()) {
                for (Map<String, Object> datum : newRawInputs) {
                    // remove all null value keys
                    CollectionUtils.filter(datum.values(), PredicateUtils.notNullPredicate());
                }
            }

            context.put(ImportAPI.ImportProcessConstants.UPDATE_RECORDS, newRawInputs);
            context.put(ImportAPI.ImportProcessConstants.OLD_RECORDS, oldRecords);
        }
        else if (setting == ImportProcessContext.ImportSetting.BOTH.getValue() ||
                setting == ImportProcessContext.ImportSetting.BOTH_NOT_NULL.getValue()) {
            // get data and check whether it is already there. if found, update otherwise insert
            // get data and update
            JSONObject meta = importProcessContext.getImportJobMetaJson();
            ArrayList<String> updateFields = (ArrayList<String>) meta.get(ImportAPI.ImportProcessConstants.UPDATE_FIELDS);
            List<FacilioField> fieldList = getFieldList(module, updateFields);

            List<Map<String, Object>> newCreateInputs = new ArrayList<>();
            List<Map<String, Object>> newUpdateInputs = new ArrayList<>();
            List<V3Context> oldRecords = new ArrayList<>();

            for (Map<String, Object> datum : rawInputs) {
                V3Context dbRecord = getDBRecord(module, allFields, fieldList, datum);
                if (dbRecord == null) {
                    newCreateInputs.add(datum);
                } else {
                    datum.put("id", dbRecord.getId());
                    newUpdateInputs.add(datum);
                    oldRecords.add(dbRecord);
                }
            }

            if (setting == ImportProcessContext.ImportSetting.BOTH_NOT_NULL.getValue()) {
                for (Map<String, Object> datum : newUpdateInputs) {
                    // remove all null value keys
                    CollectionUtils.filter(datum.values(), PredicateUtils.notNullPredicate());
                }
            }

            context.put(ImportAPI.ImportProcessConstants.INSERT_RECORDS, newCreateInputs);
            context.put(ImportAPI.ImportProcessConstants.UPDATE_RECORDS, newUpdateInputs);
            context.put(ImportAPI.ImportProcessConstants.OLD_RECORDS, oldRecords);
        }
        return false;
    }

    private List<FacilioField> getFieldList(FacilioModule module, ArrayList<String> fieldNames) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        List<FacilioField> fields = new ArrayList<>();
        for(String fieldName : fieldNames) {
            String modulePlusFields [] = fieldName.split("__");
            FacilioField name = fieldMap.get(modulePlusFields[1]); // modBean.getField(modulePlusFields[modulePlusFields.length - 1],Module.getName());
            fields.add(name);
        }
        fields.add(FieldFactory.getIdField(module));
        return fields;
    }

    private V3Context getDBRecord(FacilioModule module, List<FacilioField> allFields, List<FacilioField> criteriaFields, Map<String, Object> datum) throws Exception {
        SelectRecordsBuilder<V3Context> builder = new SelectRecordsBuilder<V3Context>()
                .module(module)
                .select(allFields)
                .beanClass(V3Context.class);

        List<SupplementRecord> supplements = new ArrayList<>();
        for (FacilioField field : allFields) {
            if (field.getDataTypeEnum().isRelRecordField()) {
                supplements.add((SupplementRecord) field);
            }
        }
        builder.fetchSupplements(supplements);

        Criteria criteria = buildCriteria(criteriaFields, datum);
        if (!criteria.isEmpty()) {
            builder.andCriteria(criteria);
        }
        V3Context record = builder.fetchFirst();

        return record;
    }

    private Criteria buildCriteria(List<FacilioField> fields, Map<String, Object> datum) {
        Criteria criteria = new Criteria();
        for (FacilioField field : fields) {
            Object value = datum.get(field.getName());
            switch (field.getDataTypeEnum()) {
                case STRING:
                    criteria.addAndCondition(CriteriaAPI.getCondition(field, String.valueOf(value), StringOperators.IS));
                    break;

                case NUMBER:
                case DECIMAL:
                case ENUM:
                case SYSTEM_ENUM:
                case DATE_TIME:
                case DATE:
                    criteria.addAndCondition(CriteriaAPI.getCondition(field, String.valueOf(value), NumberOperators.EQUALS));
                    break;

                case MULTI_ENUM:
                    if (value instanceof List && CollectionUtils.isNotEmpty((Collection<?>) value)) {
                        criteria.addAndCondition(CriteriaAPI.getCondition(field, (Collection) value, NumberOperators.EQUALS));
                    }
                    break;

                case BOOLEAN:
                    criteria.addAndCondition(CriteriaAPI.getCondition(field, String.valueOf(value), BooleanOperators.IS));
                    break;

                case LOOKUP:
                    if (value instanceof Map) {
                        long valueId = (long) ((Map) value).get("id");
                        criteria.addAndCondition(CriteriaAPI.getCondition(field, String.valueOf(valueId), NumberOperators.EQUALS));
                    }
                    break;

                case MULTI_LOOKUP:
                    if (value instanceof List) {
                        List<Long> ids = new ArrayList<>();
                        for (Object single : (List) value) {
                            ids.add((Long) ((Map) single).get("id"));
                        }
                        if (CollectionUtils.isNotEmpty(ids)) {
                            criteria.addAndCondition(CriteriaAPI.getCondition(field, ids, NumberOperators.EQUALS));
                        }
                    }
                    break;
            }
        }
        return criteria;
    }
}
