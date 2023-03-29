package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class PredictSheetExecutionOrderCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);

        List<ImportFileSheetsContext> sheets = MultiImportApi.getSheetsListByImportId(importId, true);
        HashMap<Long, List<Long>> sheetIdVsLookUpModuleIds = loadLookUpModuleIds(sheets);

        List<ImportFileSheetsContext> orderedSheets = predict(sheets, sheetIdVsLookUpModuleIds);

        if (CollectionUtils.isEmpty(orderedSheets)) {
            context.put(FacilioConstants.ContextNames.IS_PREDICTABLE, false);
            context.put(FacilioConstants.ContextNames.IMPORT_SHEETS, sheets);
            return false;
        }

        List<FacilioField> fields = FieldFactory.getImportFileSheetsFields();
        Map<String, FacilioField> mapFields = FieldFactory.getAsMap(fields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(mapFields.get("executionOrder"));

        int excutionOrder = 1;
        for (ImportFileSheetsContext sheet : orderedSheets) {
            sheet.setExecutionOrder(excutionOrder++);
        }

        MultiImportApi.batchUpdateImportSheetBySheetId(orderedSheets, updateFields);
        context.put(FacilioConstants.ContextNames.IS_PREDICTABLE, true);
        return false;
    }

    private static List<ImportFileSheetsContext> predict(List<ImportFileSheetsContext> sheets, HashMap<Long, List<Long>> sheetIdVsLookUpModuleIds) {

        HashMap<Long, ImportFileSheetsContext> moduleIdVsSheetId = new HashMap<>();
        for (ImportFileSheetsContext sheet : sheets) {
            moduleIdVsSheetId.put(sheet.getModuleId(), sheet);
        }

        int n = sheets.size();

        int[][] relmatrix = new int[n][n];

        for (int i = 0; i < n; i++) {
            ImportFileSheetsContext sheet = sheets.get(i);
            long sheetId = sheet.getId();
            List<Long> lookUpModuleIds = sheetIdVsLookUpModuleIds.get(sheetId);

            if (CollectionUtils.isEmpty(lookUpModuleIds)) {
                continue;
            }
            for (int j = 0; j < n; j++) {
                ImportFileSheetsContext compareSheet = sheets.get(j);
                long compareModuleId = compareSheet.getModuleId();

                if (lookUpModuleIds.contains(compareModuleId)) {
                    relmatrix[i][j] = 1;
                }
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && relmatrix[i][j] == 1 && relmatrix[j][i] == 1) {
//                    System.out.println("loop detected sheets "+sheets.get(i).getId()+"<--->"+sheets.get(j).getId());
                    return null;  //abort excution prediction if loop detect
                }
            }
        }

        List<ImportFileSheetsContext> orderedSheets = new ArrayList<>();
        Set<Long> processedSheetIds = new HashSet<>();

        for (int i = 0; i < n; i++) {
            processSheet(sheets.get(i), orderedSheets, moduleIdVsSheetId, processedSheetIds, sheetIdVsLookUpModuleIds);
        }

        return orderedSheets;
    }

    private static void processSheet(ImportFileSheetsContext sheet, List<ImportFileSheetsContext> orderedSheets, HashMap<Long, ImportFileSheetsContext> moduleIdVsSheet, Set<Long> processedSheetIds, HashMap<Long, List<Long>> sheetIdVsLookUpModuleIds) {

        long sheetId = sheet.getId();

        if (processedSheetIds.contains(sheetId)) {
            return;
        }

        List<Long> lookUpModuleIds = sheetIdVsLookUpModuleIds.get(sheetId);

        if (CollectionUtils.isNotEmpty(lookUpModuleIds)) {
            for (Long lookModuleId : lookUpModuleIds) {
                ImportFileSheetsContext lookUpSheet = moduleIdVsSheet.get(lookModuleId);
                if (lookUpSheet == null) {
                    continue;
                }
                if (lookUpSheet.getId() == sheet.getId()) { //skip for same sheet lookup
                    continue;
                }
                processSheet(lookUpSheet, orderedSheets, moduleIdVsSheet, processedSheetIds, sheetIdVsLookUpModuleIds);
            }
        }
        if (!processedSheetIds.contains(sheet.getId())) {
            processedSheetIds.add(sheet.getId());
            orderedSheets.add(sheet);
        }
    }

    private HashMap<Long, List<Long>> loadLookUpModuleIds(List<ImportFileSheetsContext> sheets) throws Exception {
        HashMap<Long, List<Long>> sheetIdVsLookUpModuleIds = new HashMap<>();

        for (ImportFileSheetsContext sheet : sheets) {
            long sheetId = sheet.getId();
            String moduleName = sheet.getModuleName();
            List<FacilioField> fieldsList = Constants.getModBean().getAllFields(moduleName);
            List<Long> lookUpModuleIds = new ArrayList<>();
            for (FacilioField field : fieldsList) {
                if (field.getDataTypeEnum() == FieldType.LOOKUP || field.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
                    Long lookUpModuleId = ((BaseLookupField) field).getLookupModuleId();
                    String columnName = MultiImportApi.getSheetColumnNameFromFacilioField(sheet, field);
                    if (StringUtils.isNotEmpty(columnName)) {
                        lookUpModuleIds.add(lookUpModuleId);
                    }
                }
            }
            sheetIdVsLookUpModuleIds.put(sheetId, lookUpModuleIds);
        }
        return sheetIdVsLookUpModuleIds;
    }
}
