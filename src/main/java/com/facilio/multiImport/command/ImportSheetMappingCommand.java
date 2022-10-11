package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ImportSheetMappingCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<ImportFileSheetsContext> importSheetList = (List<ImportFileSheetsContext>) context.get(FacilioConstants.ContextNames.IMPORT_SHEET_LIST);
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);

        FacilioUtil.throwIllegalArgumentException(importId==-1L,"Import Id is Empty");
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(importSheetList),"Import sheet cannot be empty");

        Set<Long> sheetIds = new HashSet<>();
        for (ImportFileSheetsContext sheet : importSheetList) {
            sheetIds.add(sheet.getId());
        }

        validateImportSheetList(sheetIds,importId);

        Set<Long> moduleIds = new HashSet<>();
        long duplicateModuleEntry = importSheetList.stream()
                .map(moduleId -> moduleId.getModuleId())
                .filter(moduleId -> !moduleIds.add(moduleId))
                .count();

        if (duplicateModuleEntry > 0) {
            throw new IllegalArgumentException("Module cannot be mapped twice");
        }

        MultiImportApi.batchUpdateImportSheetWithModule(importSheetList);
        context.put(FacilioConstants.ContextNames.IMPORT_FILE_DETAILS, importSheetList);

        return false;
    }

    private void validateImportSheetList(Set<Long> sheetIds,Long importId) throws Exception{
        Set<Long> dbSheetIds=MultiImportApi.getSheetIdsByImportId(importId);

        FacilioUtil.throwIllegalArgumentException(sheetIds.size()!=dbSheetIds.size(),"Invalid Import Sheets");

        dbSheetIds.removeAll(sheetIds);
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isNotEmpty(dbSheetIds),"Some Import Sheets are doesn't exists");
    }
}
