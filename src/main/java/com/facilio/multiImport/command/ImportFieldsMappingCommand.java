package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFieldMappingContext;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.enums.FieldTypeImportRowProcessor;
import com.facilio.multiImport.enums.ImportDataStatus;
import com.facilio.multiImport.enums.ImportFieldMappingType;
import com.facilio.multiImport.enums.MultiImportSetting;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class ImportFieldsMappingCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ImportDataDetails importDataDetails = (ImportDataDetails) context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS);
        ImportFileSheetsContext importFileSheet = (ImportFileSheetsContext) context.get(FacilioConstants.ContextNames.IMPORT_SHEET);
        boolean skipValidation = (boolean) context.get(FacilioConstants.ContextNames.SKIP_VALIDATION);

        long sheetId = importFileSheet.getId();

        validateImportSettings(importFileSheet);
        validateGroupedFields(importFileSheet);

        List<ImportFieldMappingContext> importFieldMapping = importFileSheet.getFieldMapping();
        if (CollectionUtils.isNotEmpty(importFieldMapping)) {
            importFieldMapping.forEach(fieldMapping -> {
                fieldMapping.setImportSheetId(sheetId);
            });
            setImportFieldMappingType(importFieldMapping,importFileSheet.getModuleName());
        }

        MultiImportApi.deleteImportSheetsFieldMapping(Arrays.asList(sheetId));
        MultiImportApi.addImportSheetsFieldMapping(importFieldMapping);
        MultiImportApi.batchUpdateImportSheetBySheetId(Collections.singletonList(importFileSheet), getUpdateFields());

        if(!skipValidation){
            importDataDetails.setStatus(ImportDataStatus.FIELD_MAPPED);
            MultiImportApi.updateImportStatus(importDataDetails);
        }
        context.put(FacilioConstants.ContextNames.IMPORT_SHEET, importFileSheet);
        return false;
    }

    private void validateImportSettings(ImportFileSheetsContext importSheet) {
        Integer importSetting = importSheet.getImportSetting();
        FacilioUtil.throwIllegalArgumentException(importSetting == null, "ImportSetting can not be empty");
        if (MultiImportSetting.INSERT.getValue() == importSetting) {
            return;
        }

        if (MultiImportSetting.INSERT_SKIP.getValue() == importSetting) {
            String insertBy = importSheet.getInsertBy();
            FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(insertBy), "insertBy cannot be empty for insert with skip import");
            if (insertBy != null) {
                FacilioUtil.throwIllegalArgumentException(!MultiImportApi.isValidJsonArray(insertBy), "insertBy is a not valid JSON Array String");
            }
        } else {
            String updateBy = importSheet.getUpdateBy();
            FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(updateBy), "updateBy cannot be empty for update import");

            if (updateBy != null) {
                FacilioUtil.throwIllegalArgumentException(!MultiImportApi.isValidJsonArray(updateBy), "updateBy is a not valid JSON Array String");
            }

        }

    }
    private void validateGroupedFields(ImportFileSheetsContext importSheet) throws Exception{
        List<ImportFieldMappingContext> importFieldMapping = importSheet.getFieldMapping();
        String moduleName = importSheet.getModuleName();
        Map<Long,FacilioField> fieldMap = FieldFactory.getAsIdMap(Constants.getModBean().getAllFields(moduleName));
        for(ImportFieldMappingContext fieldMappingContext : importFieldMapping){
            long fieldId = fieldMappingContext.getFieldId();
            if(fieldId!=-1 && fieldMap.containsKey(fieldId)){
                FacilioField field = fieldMap.get(fieldId);
                FieldTypeImportRowProcessor rowProcessor = FieldTypeImportRowProcessor.getFieldTypeImportRowProcessor(field.getDataTypeEnum());
                if(rowProcessor.isGroupedFieldType()){
                   String groupedFieldName =  fieldMappingContext.getFieldName();
                   if(!groupedFieldName.contains(".")){
                       throw new IllegalArgumentException("Grouped field name '"+ groupedFieldName +"' should contain dot followed by parent field name in prefix");
                   }
                }
            }
        }
    }
    private void setImportFieldMappingType(List<ImportFieldMappingContext> importFieldMapping,String moduleName) throws Exception{
        Map<Long,FacilioField> fieldMap = FieldFactory.getAsIdMap(Constants.getModBean().getAllFields(moduleName));
        for(ImportFieldMappingContext fieldMappingContext : importFieldMapping){

            long fieldId = fieldMappingContext.getFieldId();
            String fieldName = fieldMappingContext.getFieldName();
            long relMappingId = fieldMappingContext.getRelMappingId();
            long parentLookupFieldId = fieldMappingContext.getParentLookupFieldId();

            if(relMappingId!=-1l){
                fieldMappingContext.setType(ImportFieldMappingType.RELATIONSHIP);
            }
            else if(fieldId!=-1l && parentLookupFieldId!=-1l){
                fieldMappingContext.setType(ImportFieldMappingType.ONE_LEVEL);
            }else if(fieldId!=-1l && !fieldMap.containsKey(fieldId)){
                fieldMappingContext.setType(ImportFieldMappingType.UNIQUE_FIELD);
            }
            else if(fieldId!=-1l){
                FacilioField field = fieldMap.get(fieldId);
                FieldTypeImportRowProcessor rowProcessor = FieldTypeImportRowProcessor.getFieldTypeImportRowProcessor(field.getDataTypeEnum());
                if(rowProcessor.isGroupedFieldType()){
                    fieldMappingContext.setType(ImportFieldMappingType.GROUPED_FIELD);
                }else {
                    fieldMappingContext.setType(ImportFieldMappingType.NORMAL);
                }
            }
            else if(StringUtils.isNotEmpty(fieldName)){
                fieldMappingContext.setType(ImportFieldMappingType.NORMAL);
            }else {
                throw new IllegalArgumentException("Invalid import field mapping type");
            }
        }
    }

    private static List<FacilioField> getUpdateFields() {
        List<FacilioField> fields = FieldFactory.getImportFileSheetsFields();
        Map<String, FacilioField> mapFields = FieldFactory.getAsMap(fields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(mapFields.get("importSetting"));
        updateFields.add(mapFields.get("insertBy"));
        updateFields.add(mapFields.get("updateBy"));
        return updateFields;
    }
}