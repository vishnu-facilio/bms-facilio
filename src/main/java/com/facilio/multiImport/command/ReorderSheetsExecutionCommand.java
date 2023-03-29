package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ReorderSheetsExecutionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ImportFileSheetsContext> importSheetList = (List<ImportFileSheetsContext>) context.get(FacilioConstants.ContextNames.IMPORT_SHEET_LIST);
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);

        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(importSheetList), "Import sheet cannot be empty");

        Set<Long> sheetIds = new HashSet<>();
        int excutionOrder = 1;
        for (ImportFileSheetsContext sheet : importSheetList) {
            sheetIds.add(sheet.getId());
            sheet.setExecutionOrder(excutionOrder++);
        }
        validateImportSheetList(sheetIds, importId);


        List<FacilioField> fields = FieldFactory.getImportFileSheetsFields();
        Map<String, FacilioField> mapFields = FieldFactory.getAsMap(fields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(mapFields.get("executionOrder"));

        MultiImportApi.batchUpdateImportSheetBySheetId(importSheetList, updateFields);
        context.put(FacilioConstants.ContextNames.IMPORT_SHEETS, importSheetList);

        return false;
    }

    private void validateImportSheetList(Set<Long> payLoadSheetIds, Long importId) throws Exception {
        List<ImportFileSheetsContext> oldSheets = MultiImportApi.getSheetsListByImportId(importId, true);
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(oldSheets),"No sheets are avaliable");


        Set<Long> oldSheetIds = oldSheets.stream().map(ImportFileSheetsContext::getId).collect(Collectors.toSet());

        FacilioUtil.throwIllegalArgumentException(payLoadSheetIds.size() != oldSheetIds.size(), "Invalid Import Sheets");

        FacilioUtil.throwIllegalArgumentException(!oldSheetIds.containsAll(payLoadSheetIds), "Some Import Sheets are doesn't exists");
    }
}
