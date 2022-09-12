package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.util.MultiImportApi;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ImportSheetMappingCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<ImportFileSheetsContext> importSheetList = (List<ImportFileSheetsContext>) context.get(FacilioConstants.ContextNames.IMPORT_SHEET_LIST);

        if (CollectionUtils.isEmpty(importSheetList)){
            throw new IllegalArgumentException("Import sheet cannot be empty");
        }

        List<Long> sheetId = new ArrayList<>();
        for(ImportFileSheetsContext sheet : importSheetList){
            sheetId.add(sheet.getId());
        }
        List<ImportFileSheetsContext> existingImportFileSheets = MultiImportApi.getImportSheets(sheetId);
        if (CollectionUtils.isEmpty(existingImportFileSheets)){
            throw new IllegalArgumentException("Existing Import sheet doesn't exist");
        }

        Map<Long, ImportFileSheetsContext> sheetIdVsModuleMap = importSheetList.stream()
                                                    .collect(Collectors.toMap(ImportFileSheetsContext::getId, Function.identity()));

            for (ImportFileSheetsContext existingSheet : existingImportFileSheets) {
                long id = existingSheet.getId();
                ImportFileSheetsContext sheet = sheetIdVsModuleMap.get(id);
                existingSheet.setModuleId(sheet.getModuleId());
            }


          Set<Long> moduleIds = new HashSet<>();


            long duplicateModuleEntry = existingImportFileSheets.stream()
                    .map(moduleId -> moduleId.getModuleId())
                    .filter(moduleId -> !moduleIds.add(moduleId))
                    .count();

            if (duplicateModuleEntry > 0) {
                throw new IllegalArgumentException("Module cannot be mapped twice");
            }

       for (ImportFileSheetsContext importFileSheet : existingImportFileSheets){
           MultiImportApi.updateImportSheetsWithModule(importFileSheet);
        }

        context.put(FacilioConstants.ContextNames.IMPORT_FILE_DETAILS, existingImportFileSheets);

        return false;
    }

}
