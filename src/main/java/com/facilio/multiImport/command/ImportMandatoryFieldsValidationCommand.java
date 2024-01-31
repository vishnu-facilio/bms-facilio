package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.config.ImportConfig;
import com.facilio.multiImport.config.ImportHandler;
import com.facilio.multiImport.context.ImportFieldMappingContext;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.context.LookupIdentifierEnum;
import com.facilio.multiImport.multiImportExceptions.ImportMandatoryFieldsException;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.multiImport.util.MultiImportChainUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImportMandatoryFieldsValidationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);
        ImportFileSheetsContext importSheet = (ImportFileSheetsContext) context.get(FacilioConstants.ContextNames.IMPORT_SHEET);

        if (importSheet == null) {
            throw new IllegalArgumentException("importSheet can not be empty");
        }

        long sheetId = importSheet.getId();
        FacilioUtil.throwIllegalArgumentException(sheetId == -1L, "Sheet ID can not be empty");
        ImportFileSheetsContext oldSheet = MultiImportApi.getImportSheet(importId,sheetId);
        FacilioUtil.throwIllegalArgumentException(oldSheet == null, "Sheet ID doesn't exists");

        validateMandatoryUniqueFields(importSheet);
        boolean skipValidation = (boolean) context.get(FacilioConstants.ContextNames.SKIP_VALIDATION);
        if (skipValidation){
            return false;
        }

        try {
            validateMandatoryFields(importSheet, oldSheet.getModuleName());
        }catch (Exception e){
            if(e instanceof ImportMandatoryFieldsException){
                String clientMessage = ((ImportMandatoryFieldsException)e).getClientMessage();
                throw new IllegalArgumentException(clientMessage);
            }
            throw e;
        }

        return false;
    }
    private void validateMandatoryFields(ImportFileSheetsContext importSheet,String moduleName) throws Exception {
        if(!MultiImportApi.isInsertImportSheet(importSheet)){  //validate only for insert import
            return;
        }

            // Check whether there is a mapping for the required fields or not
        ArrayList<String> missingColumns = new ArrayList<String>();

        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(moduleName),"Module name cannot be empty for field mapping");
        List<FacilioField> requiredFields = MultiImportApi.getRequiredFields(importSheet.getModuleName());

        if (CollectionUtils.isNotEmpty(requiredFields)) {
            for (FacilioField field : requiredFields) {
                if (!MultiImportApi.isFieldMappingPresent(importSheet,field)) {
                    missingColumns.add(field.getDisplayName());
                }
            }
        }
        if (missingColumns.size() > 0) {
           throw  new ImportMandatoryFieldsException(missingColumns, new Exception());
        }

    }
    private void validateMandatoryUniqueFields(ImportFileSheetsContext importSheet) throws Exception{
        Map<String,String> fieldNameVsSheetColumnName = importSheet.getFieldNameVsSheetColumnNameMap();
        if(MapUtils.isEmpty(fieldNameVsSheetColumnName)){
            return;
        }

        ImportConfig importConfig= MultiImportChainUtil.getMultiImportConfig(importSheet.getModuleName());
        Map<String, List<String>> lookupUniqueFieldsMap = null;
        if (importConfig!=null){
            ImportHandler importHandler = importConfig.getImportHandler();
            if(importHandler!=null){
                lookupUniqueFieldsMap = importHandler.getLookupUniqueFieldsMap();
            }
        }
        if(MapUtils.isEmpty(lookupUniqueFieldsMap)){
            return;
        }

        for (String lookupFieldName:lookupUniqueFieldsMap.keySet()){
            if(!fieldNameVsSheetColumnName.containsKey(lookupFieldName)){
                continue;
            }

            String sheetColumnName = fieldNameVsSheetColumnName.get(lookupFieldName);
            ImportFieldMappingContext fieldMappingContext = importSheet.getSheetColumnNameVsFieldMapping().get(sheetColumnName);
            LookupIdentifierEnum lookupIdentifier = fieldMappingContext.getLookupIdentifierEnum();

            if(lookupIdentifier == LookupIdentifierEnum.PRIMARY_FIELD){
                if (MapUtils.isNotEmpty(lookupUniqueFieldsMap) && lookupUniqueFieldsMap.containsKey(lookupFieldName)) {
                    List<String> fieldNames = lookupUniqueFieldsMap.get(lookupFieldName);
                    for (String fieldName : fieldNames) {
                        if(!fieldName.startsWith("*")){
                            String uniqueFieldSheetColumnName = fieldNameVsSheetColumnName.get(fieldName);
                            if(uniqueFieldSheetColumnName == null && fieldName.equals("site")){
                                uniqueFieldSheetColumnName = fieldNameVsSheetColumnName.get("siteId");
                            }
                           FacilioUtil.throwIllegalArgumentException(uniqueFieldSheetColumnName==null,"Mandatory unique field:"+fieldName +" is not mapped");
                        }
                    }
                }
            }
        }


    }

}
