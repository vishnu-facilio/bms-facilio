package com.facilio.multiImport.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFieldMappingContext;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.enums.MultiImportSetting;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FilterMultiImportDataCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(FilterMultiImportDataCommand.class.getName());

    ImportDataDetails importDataDetails = null;
    Long importId = null;
    ImportFileSheetsContext importSheet = null;
    List<FacilioField> requiredFields = null;
    String moduleName = null;
    Class beanClass = null;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("FilterMultiImportDataCommand started time:"+System.currentTimeMillis());
        List<ImportRowContext> allRows = ImportConstants.getRowContextList(context);

        importDataDetails = ImportConstants.getImportDataDetails(context);
        importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);
        importSheet = (ImportFileSheetsContext) context.get(FacilioConstants.ContextNames.IMPORT_SHEET);
        beanClass = (Class) context.get(Constants.BEAN_CLASS);

        Long importSheetId = importSheet.getId();
        moduleName = importSheet.getModuleName();
        ModuleBean modBean = Constants.getModBean();
        MultiImportSetting setting = importSheet.getImportSettingEnum();
        FacilioModule module = importSheet.getModule();
        requiredFields = (List<FacilioField>) context.get(ImportAPI.ImportProcessConstants.REQUIRED_FIELDS);
        if (CollectionUtils.isEmpty(requiredFields)) {
            requiredFields = MultiImportApi.getRequiredFields(moduleName);
        }

        Map<Long, ImportRowContext> logIdVsRowContext = allRows.stream().collect(Collectors.toMap(ImportRowContext::getId, Function.identity()));
        ImportConstants.setLogIdVsRowContextMap(context, logIdVsRowContext);
        List<Pair<Long, Map<String, Object>>> rawInputs = MultiImportApi.getErrorLessRecords(allRows);

        if (setting == MultiImportSetting.INSERT) {
            markTheRowStatus(logIdVsRowContext, rawInputs, ImportRowContext.RowStatus.ADDED);
            ImportConstants.setInsertRecords(context, rawInputs);
        }

        else if (setting == MultiImportSetting.INSERT_SKIP) {
            List<Pair<Long, Map<String, Object>>> newRawInputs = new ArrayList<>();

            List<String> insertBySheetColumNames = importSheet.getInsertByFieldsList();

            List<FacilioField> criteriaFields = getFieldsBySheetColumnNames(importSheet,insertBySheetColumNames,context);

            List<FacilioField> dbSelectableFields = getDBSelectableFields(setting,context);

            for (Pair<Long, Map<String, Object>> datum : rawInputs) {
                long logId = datum.getLeft();
                Map<String,Object> record = datum.getRight();
                ImportRowContext importRowContext = logIdVsRowContext.get(logId);
                ModuleBaseWithCustomFields dbRecord = getDBRecord(module, dbSelectableFields, criteriaFields,record);
                if (dbRecord == null) { // add only when the record is not found in database.
                    newRawInputs.add(datum);
                    importRowContext.setRowStatus(ImportRowContext.RowStatus.ADDED);
                }
                else{
                   importRowContext.setRowStatus(ImportRowContext.RowStatus.SKIPPED);
                }
                int skipRecordsCount = rawInputs.size()- newRawInputs.size();
                context.put(ImportConstants.SKIP_RECORDS_COUNT,skipRecordsCount);
            }

            ImportConstants.setInsertRecords(context, newRawInputs);
        }
       else if (setting == MultiImportSetting.UPDATE ||
                setting == MultiImportSetting.UPDATE_NOT_NULL) {
            // get data and update

            List<String> updateBySheetColumNames = importSheet.getUpdateByFieldsList();

            List<FacilioField> criteriaFields = getFieldsBySheetColumnNames(importSheet,updateBySheetColumNames,context);

            List<Pair<Long, Map<String, Object>>> newRawInputs = new ArrayList<>();

            List<FacilioField> dbSelectableFields = getDBSelectableFields(setting,context);

            Map<Long, ModuleBaseWithCustomFields> oldRecords = new LinkedHashMap<>();
            for (Pair<Long, Map<String, Object>> datum : rawInputs) {
                long logId = datum.getLeft();
                Map<String,Object> record = datum.getRight();
                ImportRowContext importRowContext = logIdVsRowContext.get(logId);
                ModuleBaseWithCustomFields dbRecord = getDBRecord(module, dbSelectableFields, criteriaFields,record); //only select id field from dp for old record
                if (dbRecord == null) {// Mark error if the record is not found id db.
                    importRowContext.setErrorOccurredRow(true);
                    importRowContext.setErrorMessage("Record is not found to update");
                    continue;
                }

                record.put("id",dbRecord.getId());
                newRawInputs.add(datum);
                importRowContext.setRowStatus(ImportRowContext.RowStatus.UPDATED);

                oldRecords.put(logId,dbRecord);

                if(setting == MultiImportSetting.UPDATE_NOT_NULL){
                    CollectionUtils.filter(record.values(), PredicateUtils.notNullPredicate());
                }

            }

            List<FacilioField> patchFields = getPatchFields(importSheet,context);
            context.put(Constants.PATCH_FIELDS,patchFields);
            ImportConstants.setUpdateRecords(context,newRawInputs);
            ImportConstants.setOldRecordsMap(context,oldRecords);

        }
        else if (setting == MultiImportSetting.BOTH ||
                setting == MultiImportSetting.BOTH_NOT_NULL) {
            // get data and check whether it is already there. if found, update otherwise insert
            // get data and update

            List<String> updateBySheetColumNames = importSheet.getUpdateByFieldsList();

            List<FacilioField> criteriaFields = getFieldsBySheetColumnNames(importSheet,updateBySheetColumNames,context);

            List<Pair<Long, Map<String, Object>>> updateInputs = new ArrayList<>();
            List<Pair<Long, Map<String, Object>>> newCreateInputs = new ArrayList<>();
            Map<Long,ModuleBaseWithCustomFields> oldRecords = new LinkedHashMap<>();

            List<FacilioField> dbSelectableFields = getDBSelectableFields(setting,context);

            for (Pair<Long, Map<String, Object>> datum : rawInputs) {
                long logId = datum.getLeft();
                Map<String,Object> record = datum.getRight();
                ImportRowContext importRowContext = logIdVsRowContext.get(logId);
                ModuleBaseWithCustomFields dbRecord = getDBRecord(module, dbSelectableFields, criteriaFields,record);
                if (dbRecord == null) { // if not found in dp, add the record
                    if(validateRow(importRowContext)){ // check mandatory fields value
                        newCreateInputs.add(datum);
                        importRowContext.setRowStatus(ImportRowContext.RowStatus.ADDED);
                    }
                }else{
                    record.put("id",dbRecord.getId());
                    updateInputs.add(datum);
                    importRowContext.setRowStatus(ImportRowContext.RowStatus.UPDATED);

                    oldRecords.put(logId,dbRecord);
                    if(setting == MultiImportSetting.BOTH_NOT_NULL){
                        CollectionUtils.filter(record.values(), PredicateUtils.notNullPredicate());
                    }
                }
            }

            List<FacilioField> patchFields = getPatchFields(importSheet,context);
            context.put(Constants.PATCH_FIELDS,patchFields);

            ImportConstants.setInsertRecords(context, newCreateInputs);
            ImportConstants.setUpdateRecords(context,updateInputs);
            ImportConstants.setOldRecordsMap(context,oldRecords);

        }

        LOGGER.info("FilterMultiImportDataCommand completed time:"+System.currentTimeMillis());

        return false;
    }

    private static List<FacilioField> getFieldsBySheetColumnNames(ImportFileSheetsContext importSheet,List<String> sheetColumnNames,Context context) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        String moduleName = importSheet.getModuleName();
        Map<String, ImportFieldMappingContext> sheetColumnNameVsFieldMapping =importSheet.getSheetColumnNameVsFieldMapping();

        List<FacilioField>  allFields = MultiImportApi.getImportFields(context,moduleName);

        Map<Long, FacilioField> fieldIdVsFacilioFieldMap = FieldFactory.getAsIdMap(allFields);
        Map<String, FacilioField> fieldNameVsFacilioFieldMap = FieldFactory.getAsMap(allFields);

        for (String sheetColumnName : sheetColumnNames) {
            ImportFieldMappingContext fieldMappingContext = sheetColumnNameVsFieldMapping.get(sheetColumnName);

            long fieldId = fieldMappingContext.getFieldId();
            String fieldName = fieldMappingContext.getFieldName();

            if(fieldId != -1L && fieldIdVsFacilioFieldMap.get(fieldId) != null) {
                fields.add(fieldIdVsFacilioFieldMap.get(fieldId));
            }
            else if (StringUtils.isNotEmpty(fieldName) && fieldNameVsFacilioFieldMap.get(fieldName)!=null) {
                fields.add(fieldNameVsFacilioFieldMap.get(fieldName));
            }
        }
        return fields;
    }
    private static List<FacilioField> getPatchFields(ImportFileSheetsContext importSheet,Context context) throws Exception {
        List<String> sheetColumnNames = new ArrayList<>(importSheet.getSheetColumnNameVsFieldMapping().keySet());
        return getFieldsBySheetColumnNames(importSheet,sheetColumnNames,context);
    }

    private ModuleBaseWithCustomFields getDBRecord(FacilioModule module, List<FacilioField> selectableFields, List<FacilioField> criteriaFields, Map<String, Object> datum) throws Exception {
        SelectRecordsBuilder<V3Context> builder = new SelectRecordsBuilder<V3Context>()
                .module(module)
                .select(selectableFields)
                .beanClass(beanClass)
                .limit(1);
        List<SupplementRecord> supplements = new ArrayList<>();
        for (FacilioField field : selectableFields) {
            if (field.getDataTypeEnum().isRelRecordField()) {
                supplements.add((SupplementRecord) field);
            }
        }
        builder.fetchSupplements(supplements);

        Criteria criteria = buildCriteria(criteriaFields, datum);
        if (!criteria.isEmpty()) {
            builder.andCriteria(criteria);
        }
        ModuleBaseWithCustomFields record = builder.fetchFirst();

        return record;
    }

    private Criteria buildCriteria(List<FacilioField> fields, Map<String, Object> datum) {
        Criteria criteria = new Criteria();
        for (FacilioField field : fields) {
            Object value = datum.get(field.getName());
            switch (field.getDataTypeEnum()) {
                case STRING:
                    String strValue = value.toString().replace(",", StringOperators.DELIMITED_COMMA);
                    criteria.addAndCondition(CriteriaAPI.getCondition(field, strValue, StringOperators.IS));
                    break;

                case ID:
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

    private void markTheRowStatus(Map<Long, ImportRowContext> logIdVsRowContext, List<Pair<Long, Map<String, Object>>> rawInputs, ImportRowContext.RowStatus status) {
        for (Pair<Long, Map<String, Object>> pair : rawInputs) {
            Long logId = pair.getLeft();
            ImportRowContext rowContext = logIdVsRowContext.get(logId);
            rowContext.setRowStatus(status);
        }
    }
    private  boolean validateRow(ImportRowContext rowContext){
        MultiImportApi.checkMandatoryFieldsValueExistsOrNot(requiredFields,importSheet,rowContext);
        if(rowContext.isErrorOccurredRow()){
            return false;
        }
        return true;
    }
    private List<FacilioField> getDBSelectableFields(MultiImportSetting setting,Context context) throws Exception {
        List<FacilioField> dbSelectableFields = new ArrayList<>();

        switch (setting){
            case INSERT_SKIP:
                dbSelectableFields.add(FieldFactory.getIdField(importSheet.getModule()));
                return dbSelectableFields;
            case UPDATE:
            case UPDATE_NOT_NULL:
            case BOTH:
            case BOTH_NOT_NULL:
               /* List<FacilioField> patchFields = getPatchFields(importSheet,context);
                Map<String,FacilioField> patchFieldMap = FieldFactory.getAsMap(patchFields);
                if(!patchFieldMap.containsKey("id")){
                    dbSelectableFields.add(FieldFactory.getIdField(importSheet.getModule()));
                }*/
                dbSelectableFields.add(FieldFactory.getIdField(importSheet.getModule()));
                return dbSelectableFields;
        }
        return dbSelectableFields;
    }
}
