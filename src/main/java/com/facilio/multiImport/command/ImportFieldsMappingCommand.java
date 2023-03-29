package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFieldMappingContext;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.enums.ImportDataStatus;
import com.facilio.multiImport.enums.MultiImportSetting;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.util.FacilioUtil;
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

        List<ImportFieldMappingContext> importFieldMapping = importFileSheet.getFieldMapping();
        if (CollectionUtils.isNotEmpty(importFieldMapping)) {
            importFieldMapping.forEach(fieldMapping -> {
                fieldMapping.setImportSheetId(sheetId);
            });
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