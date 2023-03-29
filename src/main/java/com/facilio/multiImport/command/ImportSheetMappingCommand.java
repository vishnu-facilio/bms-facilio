package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.enums.ImportDataStatus;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ImportSheetMappingCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<ImportFileSheetsContext> payLoadImportSheetList = (List<ImportFileSheetsContext>) context.get(FacilioConstants.ContextNames.IMPORT_SHEET_LIST);
        ImportDataDetails importDataDetails = (ImportDataDetails) context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS);
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);

        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(payLoadImportSheetList), "Import sheet cannot be empty");

        List<ImportFileSheetsContext> oldRecord = MultiImportApi.getSheetsListByImportId(importId, false);
        if (CollectionUtils.isEmpty(oldRecord)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Few records doesn't have data");
        }
        Map<Long, ImportFileSheetsContext> oldRecordMap = oldRecord.stream().collect(Collectors.toMap(ImportFileSheetsContext::getId, Function.identity()));


        validateImportSheets(payLoadImportSheetList, oldRecordMap);
        cleanImportFieldMappingForModuleUnselectedSheets(payLoadImportSheetList, oldRecordMap);
        setPayLoadModuleIdInOldRecord(payLoadImportSheetList, oldRecordMap);
        validateModuleMapping(oldRecordMap);

        List<FacilioField> fields = FieldFactory.getImportFileSheetsFields();
        Map<String, FacilioField> mapFields = FieldFactory.getAsMap(fields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(mapFields.get("moduleId"));

        MultiImportApi.batchUpdateImportSheetBySheetId(oldRecord, updateFields);

        importDataDetails.setStatus(ImportDataStatus.MODULE_MAPPED);

        MultiImportApi.updateImportStatus(importDataDetails);
        context.put(FacilioConstants.ContextNames.IMPORT_SHEETS, oldRecord);

        return false;
    }

    private void setPayLoadModuleIdInOldRecord(List<ImportFileSheetsContext> payLoadImportSheets, Map<Long, ImportFileSheetsContext> oldRecordMap) {
        for (ImportFileSheetsContext payLoadSheet : payLoadImportSheets) {
            long sheetId = payLoadSheet.getId();
            long moduleId = payLoadSheet.getModuleId();

            ImportFileSheetsContext dbSheet = oldRecordMap.get(sheetId);
            dbSheet.setModuleId(moduleId);
        }
    }

    private void validateImportSheets(List<ImportFileSheetsContext> importSheets, Map<Long, ImportFileSheetsContext> oldRecordMap) {
        Set<Long> dbSheetIds = oldRecordMap.keySet();
        Set<Long> payLoadSheetIds = importSheets.stream().map(ImportFileSheetsContext::getId).collect(Collectors.toSet());
        FacilioUtil.throwIllegalArgumentException(!(dbSheetIds.containsAll(payLoadSheetIds)), "Some Import Sheets are doesn't exists.");
    }

    private void validateModuleMapping(Map<Long, ImportFileSheetsContext> dbSheetIdvsSheetContextMap) throws Exception {
        Collection<ImportFileSheetsContext> dbSheets = dbSheetIdvsSheetContextMap.values();

        Set<Long> sheetIdsWithModuleMapping = new HashSet<>();
        Set<Long> moduleIds = new HashSet<>();
        boolean hasDublicateModuleEntry = false;
        for (ImportFileSheetsContext sheet : dbSheets) {
            if (sheet.getModuleId() != -1L) {
                sheetIdsWithModuleMapping.add(sheet.getId());
                hasDublicateModuleEntry = !moduleIds.add(sheet.getModuleId());
                FacilioUtil.throwIllegalArgumentException(hasDublicateModuleEntry, "Module cannot be mapped twice");
            }
        }

        FacilioUtil.throwIllegalArgumentException(sheetIdsWithModuleMapping.size() > 5, "For a single multi import, more than five sheet mapping are not permitted.");
    }

    private void cleanImportFieldMappingForModuleUnselectedSheets(List<ImportFileSheetsContext> importSheets, Map<Long, ImportFileSheetsContext> oldRecordMap) throws Exception {
        Set<Long> moduleMappingUnselectedSheetIds = new HashSet<>();
        for (ImportFileSheetsContext payLoadSheet : importSheets) {
            Long sheetId = payLoadSheet.getId();
            ImportFileSheetsContext oldSheet = oldRecordMap.get(sheetId);

            if (isValidToDeleteFieldMappingData(payLoadSheet, oldSheet)) {
                moduleMappingUnselectedSheetIds.add(sheetId);
            }
        }
        MultiImportApi.deleteImportSheetsFieldMapping(moduleMappingUnselectedSheetIds);
    }

    private boolean isValidToDeleteFieldMappingData(ImportFileSheetsContext payLoadSheet, ImportFileSheetsContext oldSheet) {
        long payLoadModuleId = payLoadSheet.getModuleId();
        long oldModuleId = oldSheet.getModuleId();

        if (oldSheet.isHasFieldMappingDependencies() && (payLoadModuleId == -1L || payLoadModuleId!=oldModuleId )) {
            return true;
        }
        return false;
    }
}
