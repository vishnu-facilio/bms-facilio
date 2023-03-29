package com.facilio.multiImport.util;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fs.FileInfo;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.context.*;
import com.facilio.multiImport.enums.MultiImportSetting;
import com.facilio.multiImport.importFileReader.AbstractImportFileReader;
import com.facilio.multiImport.importFileReader.CSVFileReader;
import com.facilio.multiImport.importFileReader.XLFileReader;
import com.facilio.services.email.EmailClient;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

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

    public static ImportFileSheetsContext updateImportSheetStaus(ImportFileSheetsContext importSheet) throws Exception {

        long id = importSheet.getId();
        int status = importSheet.getStatus();
        long processedRowCount = importSheet.getProcessedRowCount();

        FacilioModule module = ModuleFactory.getImportFileSheetsModule();
        Map<String, FacilioField> updateFields = FieldFactory.getAsMap(FieldFactory.getImportFileSheetsFields());


        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .fields(Arrays.asList(updateFields.get("status"), updateFields.get("processedRowCount")))
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getImportFileSheetsModule()));

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("status", status);
        updateMap.put("processedRowCount", processedRowCount);
        updateRecordBuilder.update(updateMap);

        return importSheet;
    }

    public static void batchUpdateImportSheetBySheetId(List<ImportFileSheetsContext> importFileSheets, List<FacilioField> updateFields) throws Exception {

        if (CollectionUtils.isEmpty(updateFields)) {
            return;
        }

        List<FacilioField> fields = FieldFactory.getImportFileSheetsFields();
        Map<String, FacilioField> mapFields = FieldFactory.getAsMap(fields);
        Map<String, FacilioField> updateMapFields = FieldFactory.getAsMap(updateFields);
        Set<String> updateKeys = updateMapFields.keySet();

        List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdates = new ArrayList<>();
        for (ImportFileSheetsContext importFileSheet : importFileSheets) {
            long sheetId = importFileSheet.getId();

            GenericUpdateRecordBuilder.BatchUpdateByIdContext batchValue = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
            batchValue.setWhereId(sheetId);

            Map<String, Object> prop = FieldUtil.getAsProperties(importFileSheet, true);
            Map<String, Object> updateProp = new HashMap<>();

            for (String updateKey : updateKeys) {
                if (!mapFields.containsKey(updateKey) || !prop.containsKey(updateKey) || updateKey.equals("id")) {
                    continue;
                }
                updateProp.put(updateKey, prop.get(updateKey));
            }

            if (!updateProp.isEmpty()) {
                batchValue.setUpdateValue(updateProp);
                batchUpdates.add(batchValue);
            }

        }
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .fields(updateFields)
                .table(ModuleFactory.getImportFileSheetsModule().getTableName());
        updateBuilder.batchUpdateById(batchUpdates);
    }

    public static Set<Long> getSheetIdsByImportId(Long importId) throws Exception {
        HashSet<Long> sheetIds = new HashSet<>();

        FacilioModule importFileSheetModule = ModuleFactory.getImportFileSheetsModule();
        FacilioModule importFileModule = ModuleFactory.getImportFileModule();
        FacilioModule module = ModuleFactory.getImportFileSheetsModule();

        FacilioField importSheetIdField = FieldFactory.getIdField(module);


        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Arrays.asList(importSheetIdField))
                .table(importFileSheetModule.getTableName())
                .innerJoin(importFileModule.getTableName())
                .on("ImportFile.ID=ImportFileSheets.IMPORT_FILE_ID")
                .andCondition(CriteriaAPI.getCondition("ImportFile.IMPORT_ID", "importId", String.valueOf(importId), NumberOperators.EQUALS));
        List<Map<String, Object>> props = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            props.forEach(map -> {
                Long importSheetId = (Long) map.get("id");
                sheetIds.add(importSheetId);
            });

        }

        return sheetIds;
    }
    public static ImportFileSheetsContext getImportSheet(Long importId,Long sheetId) throws Exception {


        FacilioModule importFileSheetModule = ModuleFactory.getImportFileSheetsModule();
        FacilioModule importFileModule = ModuleFactory.getImportFileModule();

        FacilioField importSheetIdField = FieldFactory.getIdField(importFileSheetModule);
        FacilioField moduleIdField = FieldFactory.getNumberField("moduleId","MODULE_ID",importFileSheetModule);


        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Arrays.asList(importSheetIdField,moduleIdField))
                .table(importFileSheetModule.getTableName())
                .innerJoin(importFileModule.getTableName())
                .on("ImportFile.ID=ImportFileSheets.IMPORT_FILE_ID")
                .andCondition(CriteriaAPI.getCondition("ImportFile.IMPORT_ID", "importId", String.valueOf(importId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ImportFileSheets.ID","sheetId",sheetId.toString(),NumberOperators.EQUALS));
        List<Map<String, Object>> props = selectBuilder.get();

        if(CollectionUtils.isEmpty(props)){
            return null;
        }

        Map<String,Object> prop = props.get(0);

        ImportFileSheetsContext sheet = new ImportFileSheetsContext();
        long id = (long)prop.get("id");
        long moduleId = (long)prop.get("moduleId");
        sheet.setId(id);
        sheet.setModuleId(moduleId);
        return sheet;

    }
    public static List<ImportFileSheetsContext> getSheetsListByImportId(Long importId, boolean selectModuleMappedSheetsOnly) throws Exception {
        FacilioModule importFileSheetModule = ModuleFactory.getImportFileSheetsModule();
        FacilioModule importFileModule = ModuleFactory.getImportFileModule();
        FacilioModule importSheetFieldMappingModule = ModuleFactory.getImportSheetFieldMappingModule();

        List<FacilioField> importSheetFields = FieldFactory.getImportFileSheetsFields();

        List<FacilioField> fieldMappingFields = FieldFactory.getImportSheetFieldMappingFields();


        List<FacilioField> selectableFields = new ArrayList<>();
        selectableFields.addAll(importSheetFields);
        selectableFields.addAll(fieldMappingFields);

        selectableFields.removeIf(p -> {
            return p.getName().equals("id");
        });

        FacilioField sheetIdField = FieldFactory.getIdField(importFileSheetModule);
        sheetIdField.setName("sheetId");

        FacilioField fieldMappingIdField = FieldFactory.getIdField(importSheetFieldMappingModule);
        fieldMappingIdField.setName("fieldMappingId");

        selectableFields.add(sheetIdField);
        selectableFields.add(fieldMappingIdField);

        Long orgId = AccountUtil.getCurrentOrg().getOrgId();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(selectableFields)
                .table(importFileSheetModule.getTableName())
                .innerJoin(importFileModule.getTableName())
                .on("ImportFile.ORGID=ImportFileSheets.ORGID AND ImportFile.ORGID=" + orgId + " AND ImportFile.ID=ImportFileSheets.IMPORT_FILE_ID AND ImportFile.IMPORT_ID=" + importId)
                .leftJoin(importSheetFieldMappingModule.getTableName())
                .on("ImportFileSheets.ID=ImportSheetFieldMapping.IMPORT_SHEET_ID")
                .orderBy("EXECUTION_ORDER");

        if (selectModuleMappedSheetsOnly) {
            selectBuilder.andCustomWhere("ImportFileSheets.MODULE_ID IS NOT NULL");
        }
        List<Map<String, Object>> props = selectBuilder.get();

        HashMap<Long, ImportFileSheetsContext> sheetIdVsSheetsContext = new HashMap<>();
        for (Map<String, Object> prop : props) {
            long sheetId = (long) prop.get("sheetId");
            long fieldMappingId = (long) prop.getOrDefault("fieldMappingId", -1L);
            if (!sheetIdVsSheetsContext.containsKey(sheetId)) {
                ImportFileSheetsContext sheetContext = FieldUtil.getAsBeanFromMap(prop, ImportFileSheetsContext.class);//setSheetsContext
                sheetContext.setId(sheetId);
                sheetIdVsSheetsContext.put(sheetId, sheetContext);

                if (fieldMappingId == -1L) { //skip if fildMapping is empty
                    continue;
                }

                List<ImportFieldMappingContext> fieldMappingContextList = new ArrayList<>();
                ImportFieldMappingContext fieldMappingContext = FieldUtil.getAsBeanFromMap(prop, ImportFieldMappingContext.class);
                fieldMappingContext.setId(fieldMappingId);
                fieldMappingContextList.add(fieldMappingContext);
                sheetContext.setFieldMapping(fieldMappingContextList);

            } else {
                ImportFileSheetsContext sheetContext = sheetIdVsSheetsContext.get(sheetId);

                if (fieldMappingId == -1L) { //skip if fildMapping is empty
                    continue;
                }

                List<ImportFieldMappingContext> fieldMappingContextList = sheetContext.getFieldMapping();
                ImportFieldMappingContext fieldMappingContext = FieldUtil.getAsBeanFromMap(prop, ImportFieldMappingContext.class);
                fieldMappingContext.setId(fieldMappingId);
                fieldMappingContextList.add(fieldMappingContext);
            }
        }

        List<ImportFileSheetsContext> importSheetsList = new ArrayList<>();
        for (Map.Entry<Long, ImportFileSheetsContext> entry : sheetIdVsSheetsContext.entrySet()) {
            ImportFileSheetsContext sheet = entry.getValue();
            importSheetsList.add(sheet);
        }

        sortByExcutionOrder(importSheetsList);
        return importSheetsList;
    }

    public static List<ImportFileSheetsContext> getSortedImportSheetsFromAllFiles(List<ImportFileContext> importFiles) {
        Objects.requireNonNull(importFiles);
        List<ImportFileSheetsContext> importSheets = importFiles.stream().map(ImportFileContext::getImportFileSheetsContext)
                .collect(Collectors.toList())
                .stream()
                .flatMap(l -> l.stream())
                .collect(Collectors.toList());

        sortByExcutionOrder(importSheets);
        return importSheets;
    }

    private static void sortByExcutionOrder(List<ImportFileSheetsContext> importSheets) {
        if (CollectionUtils.isEmpty(importSheets)) {
            return;
        }
        Collections.sort(importSheets, (t1, t2) -> {
            if (t2.getExecutionOrder() == -1L) {
                return -1;
            } else if (t1.getExecutionOrder() == -1L) {
                return 1;
            }
            return (int) (t1.getExecutionOrder() - t2.getExecutionOrder());
        });
    }

    public static Map<Long, List<ImportFileContext>> getImportIdVsImportFilesMap(Set<Long> importIds) throws Exception {
        return getImportIdVsImportFilesMap(importIds, null);
    }

    public static Map<Long, List<ImportFileContext>> getImportIdVsImportFilesMap(Set<Long> importIds, List<FacilioField> selectableFields) throws Exception {
        if (CollectionUtils.isEmpty(importIds)) {
            return null;
        }

        FacilioModule importFileSheetModule = ModuleFactory.getImportFileSheetsModule();
        FacilioModule importFileModule = ModuleFactory.getImportFileModule();


        if (CollectionUtils.isEmpty(selectableFields)) {
            selectableFields = new ArrayList<>();

            List<FacilioField> importFileFields = FieldFactory.getImportFileFields();
            List<FacilioField> importFileSheetFields = FieldFactory.getImportFileSheetsFields();

            selectableFields.addAll(importFileFields);
            selectableFields.addAll(importFileSheetFields);
        }

        selectableFields.removeIf(p -> {
            return p.getName().equals("id");
        });

        FacilioField sheetIdField = FieldFactory.getIdField(importFileSheetModule);
        sheetIdField.setName("sheetId");

        FacilioField importFileIdField = FieldFactory.getIdField(importFileModule);
        importFileIdField.setName("importFileId");

        selectableFields.add(importFileIdField);
        selectableFields.add(sheetIdField);
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(selectableFields)
                .table(importFileModule.getTableName())
                .innerJoin(importFileSheetModule.getTableName())
                .on("ImportFile.ID=ImportFileSheets.IMPORT_FILE_ID")
                .andCondition(CriteriaAPI.getCondition("ImportFile.IMPORT_ID", "importId", StringUtils.join(importIds, ","), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        Map<Long, List<ImportFileContext>> importIdVsImportFiles = getImportIdVsImportFileListBeanFromMapList(props);
        return importIdVsImportFiles;

    }

    public static List<ImportFileContext> getImportFilesByImportId(Long importId) throws Exception {
        return getImportFilesByImportId(importId, false);
    }

    public static List<ImportFileContext> getImportFilesByImportId(Long importId, boolean selectModuleMappedSheetsOnly) throws Exception {

        FacilioModule importFileSheetModule = ModuleFactory.getImportFileSheetsModule();
        FacilioModule importFileModule = ModuleFactory.getImportFileModule();
        FacilioModule importSheetFieldMappingModule = ModuleFactory.getImportSheetFieldMappingModule();

        List<FacilioField> fieldMappingFields = FieldFactory.getImportSheetFieldMappingFields();
        List<FacilioField> importFileFields = FieldFactory.getImportFileFields();
        List<FacilioField> importFileSheetFields = FieldFactory.getImportFileSheetsFields();

        List<FacilioField> selectableFields = new ArrayList<>();
        selectableFields.addAll(importFileFields);
        selectableFields.addAll(importFileSheetFields);
        selectableFields.addAll(fieldMappingFields);
        selectableFields.removeIf(p -> {
            return p.getName().equals("id");
        });

        FacilioField sheetIdField = FieldFactory.getIdField(importFileSheetModule);
        sheetIdField.setName("sheetId");

        FacilioField importFileIdField = FieldFactory.getIdField(importFileModule);
        importFileIdField.setName("importFileId");

        FacilioField fieldMappingIdField = FieldFactory.getIdField(importSheetFieldMappingModule);
        fieldMappingIdField.setName("fieldMappingId");

        selectableFields.add(importFileIdField);
        selectableFields.add(sheetIdField);
        selectableFields.add(fieldMappingIdField);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(selectableFields)
                .table(importFileModule.getTableName())
                .innerJoin(importFileSheetModule.getTableName())
                .on("ImportFile.ORGID=ImportFileSheets.ORGID AND ImportFile.ORGID=" + AccountUtil.getCurrentOrg().getOrgId() + " AND ImportFile.ID=ImportFileSheets.IMPORT_FILE_ID AND ImportFile.IMPORT_ID=" + importId)
                .leftJoin(importSheetFieldMappingModule.getTableName())
                .on("ImportFileSheets.ID=ImportSheetFieldMapping.IMPORT_SHEET_ID")
                .andCondition(CriteriaAPI.getCondition("ImportFile.IMPORT_ID", "importId", String.valueOf(importId), NumberOperators.EQUALS))
                .orderBy("ImportFileSheets.EXECUTION_ORDER");

        if (selectModuleMappedSheetsOnly) {
            selectBuilder.andCustomWhere("ImportFileSheets.MODULE_ID IS NOT NULL");
        }

        List<Map<String, Object>> props = selectBuilder.get();
        Map<Long, List<ImportFileContext>> importIdVsImportFiles = getImportIdVsImportFileListBeanFromMapList(props);
        List<ImportFileContext> importFileContextList = importIdVsImportFiles.get(importId);
        return importFileContextList;
    }

    private static Map<Long, List<ImportFileContext>> getImportIdVsImportFileListBeanFromMapList(List<Map<String, Object>> props) {
        if (CollectionUtils.isEmpty(props)) {
            return Collections.EMPTY_MAP;
        }
        Map<Long, List<ImportFileContext>> importIdVsImportFileListMap = new HashMap<>();
        Map<Long, ImportFileContext> importFileIdvsImportFileMap = new HashMap<>();
        Map<Long, ImportFileSheetsContext> importSheetIdIdvsImportSheetMap = new HashMap<>();
        List<ImportFileContext> importFileList = new ArrayList<>();
        for (Map<String, Object> prop : props) {

            Long importFileId = (Long) prop.getOrDefault("importFileId", -1L);
            Long importSheetId = (Long) prop.getOrDefault("sheetId", -1l);
            Long importId = (Long) prop.getOrDefault("importId", -1L);
            Long fieldMappingId = (Long) prop.getOrDefault("fieldMappingId", -1L);

            if (!importIdVsImportFileListMap.containsKey(importId)) {
                List<ImportFileContext> importFileContextList = new ArrayList<>();
                importIdVsImportFileListMap.put(importId, importFileContextList);
            }

            if (!importFileIdvsImportFileMap.containsKey(importFileId)) {
                //Fill importFileContext info
                ImportFileContext importFileContext = FieldUtil.getAsBeanFromMap(prop, ImportFileContext.class);
                importFileContext.setId(importFileId);

                importFileIdvsImportFileMap.put(importFileId, importFileContext);
                importFileList.add(importFileContext);

                List<ImportFileSheetsContext> importFileSheets = new ArrayList<>();
                importFileContext.setImportFileSheetsContext(importFileSheets);

                List<ImportFileContext> importFileContextList = importIdVsImportFileListMap.get(importId);
                importFileContextList.add(importFileContext);

                importFileIdvsImportFileMap.put(importFileId, importFileContext);

            }
            if (!importSheetIdIdvsImportSheetMap.containsKey(importSheetId)) {
                //Fill sheet info
                ImportFileContext importFileContext = importFileIdvsImportFileMap.get(importFileId);
                List<ImportFileSheetsContext> importFileSheets = importFileContext.getImportFileSheetsContext();

                ImportFileSheetsContext importSheet = FieldUtil.getAsBeanFromMap(prop, ImportFileSheetsContext.class);
                importSheet.setId(importSheetId);

                List<ImportFieldMappingContext> fieldMappingContextList = new ArrayList<>();
                importSheet.setFieldMapping(fieldMappingContextList);

                importFileSheets.add(importSheet);

                importSheetIdIdvsImportSheetMap.put(importSheetId, importSheet);
            }


            if (fieldMappingId != -1L) { //skip if fildMapping is empty
                ImportFileSheetsContext importSheet = importSheetIdIdvsImportSheetMap.get(importSheetId);
                List<ImportFieldMappingContext> fieldMappingContextList = importSheet.getFieldMapping();

                ImportFieldMappingContext fieldMappingContext = FieldUtil.getAsBeanFromMap(prop, ImportFieldMappingContext.class);
                fieldMappingContext.setId(fieldMappingId);
                fieldMappingContextList.add(fieldMappingContext);

            }

        }

        return importIdVsImportFileListMap;

    }

    public static void addImportSheetsFieldMapping(List<ImportFieldMappingContext> importFieldMappingList) throws Exception {
        if (CollectionUtils.isEmpty(importFieldMappingList)) {
            return;
        }
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .fields(FieldFactory.getImportSheetFieldMappingFields())
                .table(ModuleFactory.getImportSheetFieldMappingModule().getTableName());
        List<Map<String, Object>> props = FieldUtil.getAsMapList(importFieldMappingList, ImportFieldMappingContext.class);
        insertRecordBuilder.addRecords(props);
        insertRecordBuilder.save();
    }

    public static void deleteImportSheetsFieldMapping(Collection<Long> sheetIds) throws Exception {
        if (CollectionUtils.isEmpty(sheetIds)) {
            return;
        }
        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getImportSheetFieldMappingModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("IMPORT_SHEET_ID", "importSheetId", StringUtils.join(sheetIds, ","), StringOperators.IS));

        deleteRecordBuilder.delete();
    }

    public static void updateImportDataDetails(ImportDataDetails importDataDetails) throws Exception {

        Long importId = importDataDetails.getId();
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .fields(FieldFactory.getImportDataDetailsFields())
                .table(ModuleFactory.getImportDataDetailsModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(importId, ModuleFactory.getImportDataDetailsModule()));
        updateRecordBuilder.update(FieldUtil.getAsProperties(importDataDetails));
    }

    public static void updateImportStatus(ImportDataDetails importDataDetails) throws Exception {
        Long importId = importDataDetails.getId();
        int status = importDataDetails.getStatus();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getImportDataDetailsFields());
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .fields(Arrays.asList(fieldMap.get("status")))
                .table(ModuleFactory.getImportDataDetailsModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(importId, ModuleFactory.getImportDataDetailsModule()));
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("status", status);
        updateRecordBuilder.update(updateMap);
    }

    public static boolean isValidJsonArray(String json) {
        try {
            JSONParser parser = new JSONParser();
            List<String> list = (List<String>) parser.parse(json);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static List<ImportDataDetails> getImportList(JSONObject pagination, String searchString, Criteria filterCriteria) throws Exception {
        List<FacilioField> fields = FieldFactory.getImportDataDetailsFields();
        Map<String, FacilioField> importDataDetailFields = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getImportDataDetailsModule().getTableName())
                .select(fields);

        if (StringUtils.isNotEmpty(searchString)) {
            addFileNameSearchSubQuery(selectBuilder, searchString);
        }
        if (filterCriteria != null && !filterCriteria.isEmpty()) {
            selectBuilder.andCriteria(filterCriteria);
        }
        int perPage = 50;
        int offset = 0;
        if (pagination != null) {
            int page = (int) pagination.get("page");
            perPage = (int) pagination.get("perPage");

            offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }
            selectBuilder.offset(offset);
            selectBuilder.limit(perPage);
        }
        StringBuilder orderBy = new StringBuilder().append(importDataDetailFields.get("id").getCompleteColumnName());
        selectBuilder.orderBy(orderBy +" DESC");

        List<ImportDataDetails> importDataDetailsList = null;
        List<Map<String, Object>> props = selectBuilder.get();

        if (CollectionUtils.isNotEmpty(props)) {

            importDataDetailsList = FieldUtil.getAsBeanListFromMapList(props, ImportDataDetails.class);

            Set<Long> importIds = new HashSet<>();

            for (ImportDataDetails importDataDetails : importDataDetailsList) {
                Long importId = importDataDetails.getId();
                importIds.add(importId);
            }

            fillImportFilesInfo(importDataDetailsList, importIds);

        }
        return CollectionUtils.isNotEmpty(importDataDetailsList) ? importDataDetailsList : Collections.emptyList();
    }

    private static void addFileNameSearchSubQuery(GenericSelectRecordBuilder selectBuilder, String searchString) throws Exception {
        StringBuilder subQuery = new StringBuilder();
        long orgId = AccountUtil.getCurrentOrg().getOrgId();

        subQuery.append("ImportDataDetails.ID in (SELECT distinct(ImportFile.IMPORT_ID) FROM ImportFile WHERE  ImportFile.ORGID=" + orgId + " ")
                .append("AND ")
                .append("ImportFile.FILE_NAME like ")
                .append("'%")
                .append(searchString)
                .append("%')");

        selectBuilder.andCustomWhere(subQuery.toString());
    }

    private static void fillImportFilesInfo(List<ImportDataDetails> importDataDetailsList, Set<Long> importIds) throws Exception {
        Map<Long, List<ImportFileContext>> importIdVsImportFileMap = MultiImportApi.getImportIdVsImportFilesMap(importIds, getSelectableFieldsForImportListView());
        for (ImportDataDetails importDataDetails : importDataDetailsList) {
            Long importId = importDataDetails.getId();
            List<ImportFileContext> importFileList = importIdVsImportFileMap.getOrDefault(importId, Collections.EMPTY_LIST);

            Long totalImportRecords = 0l;
            for (ImportFileContext importFile : importFileList) {
                Long totalRecords = 0L;
                for (ImportFileSheetsContext sheet : importFile.getImportFileSheetsContext()) {
                    Long rowCount = sheet.getRowCount();
                    if (rowCount != -1L) {
                        totalRecords += rowCount;
                    }
                }
                importFile.setTotalRecords(totalRecords);
                totalImportRecords += totalRecords;

            }
            MultiImportApi.setFileSize(importFileList);
            importDataDetails.setImportFiles(importFileList);
            importDataDetails.setTotalRecords(totalImportRecords);
        }
    }

    public static long getImportListCount(String searchString, Criteria filterCriteria) throws Exception {
        FacilioField idField = FieldFactory.getIdField(ModuleFactory.getImportDataDetailsModule());

        GenericSelectRecordBuilder countSelect = new GenericSelectRecordBuilder()
                .select(Arrays.asList())
                .table(ModuleFactory.getImportDataDetailsModule().getTableName());

        if (filterCriteria != null && !filterCriteria.isEmpty()) {
            countSelect.andCriteria(filterCriteria);
        }

        if (StringUtils.isNotEmpty(searchString)) {
            addFileNameSearchSubQuery(countSelect, searchString);
        }

        countSelect.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, idField);
        List<Map<String, Object>> props = countSelect.get();

        long count = 0;
        if (CollectionUtils.isNotEmpty(props)) {
            count = (long) props.get(0).get("id");
        }
        return count;
    }

    public static List<FacilioField> getSelectableFieldsForImportListView() {
        List<FacilioField> selectableFields = new ArrayList<>();

        List<FacilioField> importFileFields = FieldFactory.getImportFileFields();
        List<FacilioField> importFileSheetFields = FieldFactory.getImportFileSheetsFields();

        Map<String, FacilioField> importFileSheetFieldsMap = FieldFactory.getAsMap(importFileSheetFields);


        selectableFields.addAll(importFileFields);

        selectableFields.removeIf(p -> {
            return p.getName().equals("id");
        });

        FacilioField sheetIdField = FieldFactory.getIdField(ModuleFactory.getImportFileSheetsModule());
        sheetIdField.setName("importSheetId");

        FacilioField importFileIdField = FieldFactory.getIdField(ModuleFactory.getImportFileModule());
        importFileIdField.setName("importFileId");

        selectableFields.add(sheetIdField);
        selectableFields.add(importFileIdField);

        selectableFields.add(importFileSheetFieldsMap.get("name"));
        selectableFields.add(importFileSheetFieldsMap.get("rowCount"));

        return selectableFields;
    }

    public static void setFileSize(List<ImportFileContext> importFileContextList) throws Exception {
        if (CollectionUtils.isEmpty(importFileContextList)) {
            return;
        }
        for (ImportFileContext importFileContext : importFileContextList) {
            Long fileId = importFileContext.getFileId();
            FileStore fs = FacilioFactory.getFileStore();
            FileInfo fileInfo = fs.getFileInfo(fileId, true);
            Long fileSize = fileInfo.getFileSize();
            importFileContext.setFileSize(fileSize);
        }
    }

    public static boolean isInsertImportSheet(ImportFileSheetsContext importSheet) {
        if (MultiImportSetting.INSERT.getValue() == importSheet.getImportSetting() || MultiImportSetting.INSERT_SKIP.getValue() == importSheet.getImportSetting()) {
            return true;
        }
        return false;
    }

    public static void insertIntoMultiImportProcessLogTable(List<ImportRowContext> importRowContextList) throws Exception {
        if (CollectionUtils.isEmpty(importRowContextList)) {
            return;
        }

        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .fields(FieldFactory.getMultiImportProcessLogFields())
                .table(ModuleFactory.getMultiImportProcessLogModule().getTableName());

        List<Map<String, Object>> props = FieldUtil.getAsMapList(importRowContextList, ImportRowContext.class);
        insertRecordBuilder.addRecords(props);
        insertRecordBuilder.save();
    }

    public static List<ImportRowContext> getRowsByBatch(Long importSheetId, Long lastRowIdTaken, int chunkLimit) throws Exception {
        List<ImportRowContext> batchRecords = null;
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getMultiImportProcessLogFields())
                .table(ModuleFactory.getMultiImportProcessLogModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("IMPORT_SHEET_ID", "importSheetId", importSheetId.toString(), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("IS_ERROR_OCCURRED_ROW", "errorOccuredRow", "false", BooleanOperators.IS))
                .orderBy("ROW__NUMBER");

        if (lastRowIdTaken != 0) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition("ID", "id", lastRowIdTaken.toString(), NumberOperators.GREATER_THAN));
        }
        selectRecordBuilder.limit(chunkLimit);

        List<Map<String, Object>> result = selectRecordBuilder.get();
        batchRecords = FieldUtil.getAsBeanListFromMapList(result, ImportRowContext.class);

        return batchRecords;
    }

    public static Object getValueFromCell(Cell cell, CellValue cellValue) throws Exception {

        Object val = 0.0;

        // Here we get CellValue after evaluating the formula So CellType FORMULA will never occur

        if (cell.getCellType() == Cell.CELL_TYPE_BLANK || cellValue.getCellTypeEnum() == CellType.BLANK) {
            val = null;
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && HSSFDateUtil.isCellDateFormatted(cell)) {
            CellStyle style = cell.getCellStyle();
            Date date = cell.getDateCellValue();
            val = date.getTime();
        } else if (cellValue.getCellTypeEnum() == CellType.STRING) {
            if (cellValue.getStringValue().trim().length() == 0) {
                val = null;
            } else {
                val = cellValue.getStringValue().trim();
            }

        } else if (cellValue.getCellTypeEnum() == CellType.NUMERIC) {
            val = cellValue.getNumberValue();

        } else if (cellValue.getCellTypeEnum() == CellType.BOOLEAN) {
            val = cellValue.getBooleanValue();
        } else if (cell.getCellType() == Cell.CELL_TYPE_ERROR || cellValue.getCellTypeEnum() == CellType.ERROR) {
            throw new Exception("Error Evaulating Cell");
        } else {
            val = null;
        }

        return val;
    }

    public static List<Pair<Long, Map<String, Object>>> getErrorLessRecords(List<ImportRowContext> allRows) {
        List<Pair<Long, Map<String, Object>>> errorFreeRows = new ArrayList<>();

        for (ImportRowContext rowContext : allRows) {
            if (rowContext.isErrorOccuredRow()) {
                continue;
            }
            Long logId = rowContext.getId();
            Map<String, Object> errorFreeRow = rowContext.getProcessedRawRecordMap();
            Pair<Long, Map<String, Object>> pair = new ImmutablePair<>(logId, errorFreeRow);

            errorFreeRows.add(pair);
        }
        return errorFreeRows;
    }

    public static long getImportTotalRecordsCount(List<ImportFileContext> importFiles) {
        long totalRecords = 0;
        for (ImportFileContext importFile : importFiles) {
            List<ImportFileSheetsContext> importSheets = importFile.getImportFileSheetsContext();
            for (ImportFileSheetsContext importSheet : importSheets) {
                long rowCount = importSheet.getRowCount();
                totalRecords += rowCount;
            }
        }
        return totalRecords;
    }

    public static long getImportProcessedRecordsCount(List<ImportFileContext> importFiles) {
        long totalProcessdRecords = 0;
        for (ImportFileContext importFile : importFiles) {
            List<ImportFileSheetsContext> importSheets = importFile.getImportFileSheetsContext();
            for (ImportFileSheetsContext importSheet : importSheets) {
                long processedRowCountrowCount = importSheet.getProcessedRowCount();
                totalProcessdRecords += processedRowCountrowCount;
            }
        }
        return totalProcessdRecords;
    }

    public static long getMaximumRecordsCountInFiles(List<ImportFileContext> importFiles) {
        if (CollectionUtils.isEmpty(importFiles)) {
            return 0L;
        }

        return importFiles.stream().map(ImportFileContext::getImportFileSheetsContext)
                .collect(Collectors.toList()).stream().flatMap(List::stream).max(Comparator.comparingLong(ImportFileSheetsContext::getRowCount)).get().getRowCount();

    }

    public static boolean isFieldMappingPresent(ImportFileSheetsContext importSheet, FacilioField field) {
        Map<Long, String> fieldIdVsSheetColumnNameMap = importSheet.getFieldIdVsSheetColumnNameMap();
        Map<String, String> fieldNameVsSheetColumnNameMap = importSheet.getFieldNameVsSheetColumnNameMap();

        Long fieldId = field.getFieldId();
        String fieldName = field.getName();
        if (!fieldIdVsSheetColumnNameMap.containsKey(fieldId) && !fieldNameVsSheetColumnNameMap.containsKey(fieldName)) {
            // there is no mapping for this field. Don't do anything
            return false;
        }
        return true;
    }

    public static String getSheetColumnNameFromFacilioField(ImportFileSheetsContext importSheet, FacilioField field) {
        Map<Long, String> fieldIdVsSheetColumnNameMap = importSheet.getFieldIdVsSheetColumnNameMap();
        Map<String, String> fieldNameVsSheetColumnNameMap = importSheet.getFieldNameVsSheetColumnNameMap();


        Long fieldId = field.getFieldId();
        String fieldName = field.getName();

        String sheetColumnName = null;
        if (fieldIdVsSheetColumnNameMap != null && fieldIdVsSheetColumnNameMap.containsKey(fieldId)) {
            sheetColumnName = fieldIdVsSheetColumnNameMap.get(fieldId);
        } else if (fieldNameVsSheetColumnNameMap != null && fieldNameVsSheetColumnNameMap.containsKey(fieldName)) {
            sheetColumnName = fieldNameVsSheetColumnNameMap.get(fieldName);
        }
        return sheetColumnName;
    }

    public static String getFileType(ImportFileContext importFile) {
        String fileName = importFile.getFileName();
        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            return fileName.substring(index + 1).toUpperCase();
        }
        return "";
    }

    public static AbstractImportFileReader getImportFileReader(String fileType, InputStream is) throws Exception {
        return getImportFileReader(fileType, null, is);
    }

    public static AbstractImportFileReader getImportFileReader(String fileType, File file) throws Exception {
        return getImportFileReader(fileType, file, null);
    }

    public static AbstractImportFileReader getImportFileReader(String fileType, File file, InputStream is) throws Exception {
        if (fileType.equalsIgnoreCase("XLSX") || fileType.equalsIgnoreCase("XLS")) {
            if (file != null) {
                return new XLFileReader(file);
            } else if (is != null) {
                return new XLFileReader(is);
            }
        } else if (fileType.equalsIgnoreCase("CSV")) {
            if (file != null) {
                return new CSVFileReader(file);
            } else if (is != null) {
                return new CSVFileReader(is);
            }
        }
        return null;
    }
    public static void sendImportCompletedEmail(ImportDataDetails importDataDetails,User user){
        try{
            StringBuilder emailMessage = new StringBuilder();
            emailMessage.append("Your recent import has been successfully completed");
            EMailTemplate template = new EMailTemplate();
            template.setFrom(EmailClient.getFromEmail("alert"));
            template.setTo(user.getEmail());
            template.setMessage(emailMessage.toString());
            template.setSubject("Import of " + importDataDetails.getId());
            JSONObject emailJSON = template.getOriginalTemplate();
            emailJSON.put("mailType", "html");
            FacilioFactory.getEmailClient().sendEmail(emailJSON);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public static class ImportProcessConstants {
        public static final String UNIQUE_FUNCTION = "uniqueFunction";
        public static final String ROW_FUNCTION = "rowFunction";
        public static final String BEFORE_PROCESS_ROW_FUNCTION = "beforeProcessRowFunction";
        public static final String AFTER_PROCESS_ROW_FUNCTION = "afterProcessRowFunction";
        public static final String LOOKUP_UNIQUE_FIELDS_MAP = "lookupUniqueFieldsMap";
        public static final String INSERT_RECORDS = "insertRecords";
        public static final String UPDATE_RECORDS = "updateRecords";
        public static final String OLD_RECORDS = "oldRecords";
        public static final String PARSED_PROPS = "parsedProps";
        public static final String DATE_FORMATS = "dateFormat";
        public static final String TIME_STAMP_STRING = "timeStampString";
    }
}
