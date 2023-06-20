package com.facilio.multiImport.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.*;

public class ImportInItCommand extends FacilioCommand {
    Map<String,Object> batchCollectMap = new HashMap<>();
    Set<String> batchCollectFieldNames = null;
    List<ImportRowContext> batchRows = null;

    ImportFileSheetsContext importSheet = null;

    Long importSheetId = null;
    Long lastRowIdTaken = null;
    int chunkLimit = 0;
    String moduleName = null;
    @Override
    public boolean executeCommand(Context context) throws Exception {
        init(context);
        loadBatchCollectMap();
        context.put(MultiImportApi.ImportProcessConstants.BATCH_ROWS,batchRows);
        context.put(MultiImportApi.ImportProcessConstants.BATCH_COLLECT_MAP,batchCollectMap);
        return false;
    }
    private void init(Context context) throws Exception {
        importSheet = (ImportFileSheetsContext) context.get(FacilioConstants.ContextNames.IMPORT_SHEET);
        moduleName = importSheet.getModuleName();
        importSheetId = importSheet.getId();
        lastRowIdTaken = importSheet.getLastRowIdTaken();
        chunkLimit = (Integer)context.get(FacilioConstants.ContextNames.CHUNK_LIMIT);
        batchRows = MultiImportApi.getRowsByBatch(importSheetId, lastRowIdTaken, chunkLimit);

        batchCollectFieldNames = (Set<String>) context.get(MultiImportApi.ImportProcessConstants.BATCH_COLLECT_FIELD_NAMES);
        addDefaultCollectFieldNames();
        removeUnMappedFields();
    }
    private void loadBatchCollectMap() throws Exception {

        if(CollectionUtils.isEmpty(batchCollectFieldNames)){
            return;
        }

        Map<String,FacilioField> fieldMap = getBatchCollectFieldMap();

        for (ImportRowContext importRowContext : batchRows) {
            Map<String, Object> rowVal = importRowContext.getRawRecordMap();
            for(String fieldName:batchCollectFieldNames){
                FacilioField field = fieldMap.get(fieldName);
                if(field == null || !MultiImportApi.isFieldMappingPresent(importSheet,field)){
                    continue;
                }
                String sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(importSheet, field);
                Object cellValue = rowVal.get(sheetColumnName);
                if (isEmpty(cellValue)) {
                    continue;
                }
                String stringValue = cellValue.toString();
                FieldType fieldType = getFieldType(field);
                switch (fieldType){
                    case NUMBER:{
                        Set<Long> numberSet =(Set<Long>)batchCollectMap.get(fieldName);
                        if(numberSet == null){
                            numberSet = new HashSet<>();
                            batchCollectMap.put(fieldName,numberSet);
                        }
                        if(NumberUtils.isNumber(stringValue)){
                            Long longValue = (long)Double.parseDouble(stringValue);
                            numberSet.add(longValue);
                        }
                    }
                    break;
                    case DECIMAL:{
                        Set<Double> decimalSet =(Set<Double>)batchCollectMap.get(fieldName);
                        if(decimalSet == null){
                            decimalSet = new HashSet<>();
                            batchCollectMap.put(fieldName,decimalSet);
                        }
                        if(NumberUtils.isNumber(stringValue)){
                            Double longValue = Double.parseDouble(stringValue);
                            decimalSet.add(longValue);
                        }
                    }
                    break;
                    default:{
                        Set<String> stringSet =(Set<String>)batchCollectMap.get(fieldName);
                        if(stringSet == null){
                            stringSet = new HashSet<>();
                            batchCollectMap.put(fieldName,stringSet);
                        }
                        stringSet.add(stringValue);
                    }
                }

            }

        }
    }
    private void removeUnMappedFields() throws Exception {
        if(CollectionUtils.isEmpty(batchCollectFieldNames)){
            return;
        }
        Map<String,FacilioField> fieldMap = getBatchCollectFieldMap();
        batchCollectFieldNames.removeIf((fieldName)-> {
            FacilioField field = fieldMap.get(fieldName);
            if(field == null || !MultiImportApi.isFieldMappingPresent(importSheet,field)){
                return true;
            }
            return false;
        });
    }
    private void addDefaultCollectFieldNames() throws Exception {
        if(CollectionUtils.isEmpty(batchCollectFieldNames)){
            batchCollectFieldNames = new HashSet<>();
        }
        if(isSiteIdFieldPresent()){
            batchCollectFieldNames.add("siteId");
        }
        if(isStateFlowIdFieldPresent()){
            batchCollectFieldNames.add("stateFlowId");
        }
    }
    private boolean isEmpty(Object cellValue) {
        if (cellValue == null || cellValue.toString().equals("") || (cellValue.toString().equals("n/a"))) {
            return true;
        }
        return false;
    }
    private  Map<String,FacilioField> getBatchCollectFieldMap() throws Exception{
        ModuleBean modBean = Constants.getModBean();
        Map<String,FacilioField> fieldMap = new HashMap<>();
        Map<String,FacilioField> allFieldsMap = FieldFactory.getAsMap(MultiImportApi.getFields(moduleName));
        for(String fieldName:batchCollectFieldNames){
            FacilioField field = allFieldsMap.get(fieldName);
            fieldMap.put(fieldName,field);
        }
        return fieldMap;
    }
    public boolean isStateFlowIdFieldPresent() throws Exception {
        FacilioField siteIdField = Constants.getModBean().getField("stateFlowId", moduleName);

        if (siteIdField == null) {
            return false;
        }

        String siteSheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(importSheet, siteIdField);

        if (siteSheetColumnName == null) {
            return false;
        }

        return true;
    }
    public boolean isSiteIdFieldPresent() throws Exception {
        FacilioField siteIdField = Constants.getModBean().getField("siteId", moduleName);

        if (siteIdField == null) {
            return false;
        }

        String siteSheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(importSheet, siteIdField);

        if (siteSheetColumnName == null) {
            return false;
        }

        return true;
    }
    private static FieldType getFieldType(FacilioField field){
        String fieldName = field.getName();
        if(fieldName.equals("siteId")){
            return FieldType.STRING;
        }
        return field.getDataTypeEnum();
    }
}
