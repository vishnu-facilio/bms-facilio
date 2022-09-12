package com.facilio.multiImport.util;

import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.multiImport.context.ImportFileContext;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultiImportApi {

    public static ImportDataDetails getImportData(Long id) throws Exception {
        if (id == null) {
            return null;
        }

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getImportDataDetailsFields())
                .table(ModuleFactory.getImportDataDetailsModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getImportDataDetailsModule()));

        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty()) {
            ImportDataDetails importDataDetails = FieldUtil.getAsBeanFromMap(props.get(0), ImportDataDetails.class);
            return importDataDetails;
        }
        return null;
    }

    public static List<Long> getImportFileIds(Long importId) throws Exception{
        if (importId == null){
            return null;
        }

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getImportFileFields())
                .table(ModuleFactory.getImportFileModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("IMPORT_ID","importId", String.valueOf(importId),NumberOperators.EQUALS));

        List<ImportFileContext> importFileList = FieldUtil.getAsBeanListFromMapList(selectRecordBuilder.get(), ImportFileContext.class);
        List<Long> importFileIds = new ArrayList<>();
        for (ImportFileContext importFile : importFileList){
            importFileIds.add(importFile.getId());
        }
        return importFileIds;
    }

    public static List<ImportFileSheetsContext> getImportSheets(List<Long> sheetIds) throws Exception{
        if (CollectionUtils.isEmpty(sheetIds)){
            return null;
        }

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getImportFileSheetsFields())
                .table(ModuleFactory.getImportFileSheetsModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(sheetIds,(ModuleFactory.getImportFileSheetsModule())));

        List<ImportFileSheetsContext> importFileSheets = FieldUtil.getAsBeanListFromMapList(selectRecordBuilder.get(),ImportFileSheetsContext.class);
        return importFileSheets;
    }

    public static ImportFileSheetsContext updateImportSheetsWithModule(ImportFileSheetsContext importFileSheet) throws Exception{

        long id = importFileSheet.getId();

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .fields(FieldFactory.getImportFileSheetsFields())
                .table(ModuleFactory.getImportFileSheetsModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id,ModuleFactory.getImportFileSheetsModule()));

        updateRecordBuilder.update(FieldUtil.getAsProperties(importFileSheet));

        return importFileSheet;
    }
}
