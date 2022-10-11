package com.facilio.multiImport.util;

import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.multiImport.context.ImportFileContext;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import org.apache.commons.collections4.CollectionUtils;

import java.sql.SQLException;
import java.util.*;

public class MultiImportApi {

    public static ImportDataDetails getImportData(Long id) throws Exception {
        if (id == null) {
            return null;
        }

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getImportDataDetailsFields())
                .table(ModuleFactory.getImportDataDetailsModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getImportDataDetailsModule()));

        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty()) {
            ImportDataDetails importDataDetails = FieldUtil.getAsBeanFromMap(props.get(0), ImportDataDetails.class);
            return importDataDetails;
        }
        return null;
    }

    public static List<Long> getImportFileIds(Long importId) throws Exception {
        if (importId == null) {
            return null;
        }

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getImportFileFields())
                .table(ModuleFactory.getImportFileModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("IMPORT_ID", "importId", String.valueOf(importId), NumberOperators.EQUALS));

        List<ImportFileContext> importFileList = FieldUtil.getAsBeanListFromMapList(selectRecordBuilder.get(), ImportFileContext.class);
        List<Long> importFileIds = new ArrayList<>();
        for (ImportFileContext importFile : importFileList) {
            importFileIds.add(importFile.getId());
        }
        return importFileIds;
    }

    public static List<ImportFileSheetsContext> getImportSheets(Set<Long> sheetIds) throws Exception {
        if (CollectionUtils.isEmpty(sheetIds)) {
            return null;
        }

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getImportFileSheetsFields())
                .table(ModuleFactory.getImportFileSheetsModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(sheetIds, (ModuleFactory.getImportFileSheetsModule())));

        List<ImportFileSheetsContext> importFileSheets = FieldUtil.getAsBeanListFromMapList(selectRecordBuilder.get(), ImportFileSheetsContext.class);
        return importFileSheets;
    }

    public static ImportFileSheetsContext updateImportSheetsWithModule(ImportFileSheetsContext importFileSheet) throws Exception {

        long id = importFileSheet.getId();

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .fields(FieldFactory.getImportFileSheetsFields())
                .table(ModuleFactory.getImportFileSheetsModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getImportFileSheetsModule()));

        updateRecordBuilder.update(FieldUtil.getAsProperties(importFileSheet));

        return importFileSheet;
    }

    public static void batchUpdateImportSheetWithModule(List<ImportFileSheetsContext> importFileSheets) throws SQLException {
        List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdates = new ArrayList<>();
        for (ImportFileSheetsContext importFileSheet : importFileSheets) {
            long sheetId = importFileSheet.getId();
            long moduleId = importFileSheet.getModuleId();
            GenericUpdateRecordBuilder.BatchUpdateByIdContext batchValue = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
            batchValue.setWhereId(sheetId);
            batchValue.addUpdateValue("moduleId", moduleId);
            batchUpdates.add(batchValue);
        }
        FacilioField moduleIdField=FieldFactory.getNumberField("moduleId","MODULE_ID",ModuleFactory.getImportFileSheetsModule());
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .fields(Arrays.asList(moduleIdField))
                .table(ModuleFactory.getImportFileSheetsModule().getTableName());
        updateBuilder.batchUpdateById(batchUpdates);
    }

    public static Set<Long> getSheetIdsByImportId(Long importId) throws Exception{
        HashSet<Long> sheetIds = new HashSet<>();

        FacilioModule importFileSheetModule = ModuleFactory.getImportFileSheetsModule();
        FacilioModule importFileModule = ModuleFactory.getImportFileModule();
        FacilioModule module = ModuleFactory.getImportFileSheetsModule();

        FacilioField importSheetIdField=FieldFactory.getIdField(module);


        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Arrays.asList(importSheetIdField))
                .table(importFileSheetModule.getTableName())
                .innerJoin(importFileModule.getTableName())
                .on("ImportFile.ID=ImportFileSheets.IMPORT_FILE_ID")
                .andCondition(CriteriaAPI.getCondition("ImportFile.IMPORT_ID", "importId", String.valueOf(importId), NumberOperators.EQUALS));

        List<Map<String,Object>> props=selectBuilder.get();
        if(CollectionUtils.isNotEmpty(props)){
            props.forEach(map->{
                Long importSheetId=(Long)map.get("id");
                sheetIds.add(importSheetId);
            });

        }

        return sheetIds;
    }
}
