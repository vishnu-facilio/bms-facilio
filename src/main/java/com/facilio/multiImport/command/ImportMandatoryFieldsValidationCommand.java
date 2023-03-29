package com.facilio.multiImport.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.multiImportExceptions.ImportMandatoryFieldsException;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

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


        boolean skipValidation = (boolean) context.get(FacilioConstants.ContextNames.SKIP_VALIDATION);
        if (skipValidation){
            return false;
        }

        try {
            validateMandatoryFields(importSheet, oldSheet.getModuleName());
        }catch (Exception e){
            if(e instanceof ImportMandatoryFieldsException){
                String clientMessage = ((ImportMandatoryFieldsException)e).getClientMessage();
                context.put("errorMessage",clientMessage);
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
        List<FacilioField> requiredFields = getRequiredFields(importSheet.getModuleName());

        if (CollectionUtils.isNotEmpty(requiredFields)) {
            for (FacilioField field : requiredFields) {
                if (!MultiImportApi.isFieldMappingPresent(importSheet,field)) {
                    missingColumns.add(field.getDisplayName());
                }
            }
        }
        if (missingColumns.size() > 0) {
           throw  new ImportMandatoryFieldsException(null, missingColumns, new Exception());
        }

    }
    private ArrayList<FacilioField> getRequiredFields(String moduleName) throws Exception {
        ArrayList<FacilioField> fields = new ArrayList<FacilioField>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = modBean.getAllFields(moduleName);
        for (FacilioField field : allFields) {
            if (field.isRequired()) {
                fields.add(field);
            }
        }
        return fields;
    }


}
