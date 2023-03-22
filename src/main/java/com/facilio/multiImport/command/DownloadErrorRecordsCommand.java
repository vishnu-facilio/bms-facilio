package com.facilio.multiImport.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.multiImport.context.*;
import com.facilio.multiImport.importFileWriter.AbstractFileWriter;
import com.facilio.multiImport.importFileWriter.AbstractSheetWriter;
import com.facilio.multiImport.importFileWriter.XLFileWriter;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DownloadErrorRecordsCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(DownloadErrorRecordsCommand.class.getName());
    private static Integer BATCH_SIZE = 5000;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);

        ImportDataDetails importDataDetails = (ImportDataDetails) context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS);

        List<ImportFileContext> importFiles = importDataDetails.getImportFiles();

        String fileName = importId.toString() + "-ImportErrorFile.xlsx";
        String filePath = getFilePath(fileName);
        AbstractFileWriter fileWriter = new XLFileWriter();



        List<ImportFileSheetsContext> importSheets = MultiImportApi.getSortedImportSheetsFromAllFiles(importFiles);

        for (ImportFileSheetsContext sheetContext : importSheets) {
            Long sheetId = sheetContext.getId();
            String sheetName = sheetContext.getName();

            Map<Integer, String> headerMap = getHeaderMap(sheetContext);
            Map<Integer, String> columnIndexVsDateFormat = getColumnIndexVsDateFormat(sheetContext);
            Map<Integer,Short> columnIndexVsColour = getColumnIndexVsColour(headerMap);
            AbstractSheetWriter sheetWriter = fileWriter.createSheet(sheetName)
                    .setHeaderColurs(columnIndexVsColour)
                    .createHeader(headerMap)
                    .setDateFormatColumnIndex(columnIndexVsDateFormat);

            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getMultiImportProcessLogFields())
                    .table(ModuleFactory.getMultiImportProcessLogModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("IMPORT_SHEET_ID", "importSheetId", sheetId.toString(), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("IS_ERROR_OCCURRED_ROW", "errorOccuredRow", "true", BooleanOperators.IS));


            GenericSelectRecordBuilder.GenericBatchResult batchResult = selectRecordBuilder.getInBatches("ROW__NUMBER", BATCH_SIZE);

            while (batchResult.hasNext()) {

                List<Map<String, Object>> result = batchResult.get();
                List<ImportRowContext> batchRows = FieldUtil.getAsBeanListFromMapList(result, ImportRowContext.class);
                List<Map<String, Object>> dataList = getDataListToWriteInFile(batchRows);

                sheetWriter.writeData(dataList);

            }

        }

        fileWriter.write(filePath);
        File file = new File(filePath);
        FileStore fs = FacilioFactory.getFileStore();
        long fileId = fs.addFile(file.getName(), file, "application/xlsx");

        file.delete();
        importDataDetails.setErrorFileId(fileId);
        MultiImportApi.updateImportDataDetails(importDataDetails);
        return false;
    }

    private Map<Integer,Short> getColumnIndexVsColour(Map<Integer, String> headerMap){
        Map<Integer,Short> columnIndexVsColour = new HashMap<>();

        for(int i=0 ; i<headerMap.size() ; i++){
            String columnName = headerMap.get(i);
            if(columnName.equals("ROW NUMBER") || columnName.equals("ROW STATUS") || columnName.equals("ERROR MESSAGE")){
                columnIndexVsColour.put(i, IndexedColors.RED.getIndex());
            }else{
                columnIndexVsColour.put(i,IndexedColors.BLACK.getIndex());
            }
        }
        return columnIndexVsColour;
    }
    private Map<Integer, String> getColumnIndexVsDateFormat(ImportFileSheetsContext sheet) {
        Map<Integer, String> columnIndexVsDateFormat = new HashMap<>();
        Map<String, ImportFieldMappingContext> columnNameVsFiledMappingContext = sheet.getSheetColumnNameVsFieldMapping();
        if(columnNameVsFiledMappingContext == null){
            return columnIndexVsDateFormat;
        }
        int i = 1;
        for (Map.Entry<String, ImportFieldMappingContext> entry : columnNameVsFiledMappingContext.entrySet()) {
            ImportFieldMappingContext fieldMappingContext = entry.getValue();
            String dateFormat = fieldMappingContext.getDateFormat();

            if (StringUtils.isNotEmpty(dateFormat)) {
                columnIndexVsDateFormat.put(i, dateFormat);
            }

            i++;
        }

        return columnIndexVsDateFormat;
    }

    private Map<Integer, String> getHeaderMap(ImportFileSheetsContext sheet) {
        Map<Integer, String> mappedHeaderMap = new HashMap<>();
        Map<String, ImportFieldMappingContext> columnNameVsFiledMappingContext = sheet.getSheetColumnNameVsFieldMapping();
         if(columnNameVsFiledMappingContext == null){
             return mappedHeaderMap;
         }
        mappedHeaderMap.put(0, "ROW NUMBER");
        int i = 1;
        for (Map.Entry<String, ImportFieldMappingContext> entry : columnNameVsFiledMappingContext.entrySet()) {
            String columnName = entry.getKey();
            mappedHeaderMap.put(i, columnName);
            i++;
        }
        mappedHeaderMap.put(i++, "ROW STATUS");
        mappedHeaderMap.put(i, "ERROR MESSAGE");
        return mappedHeaderMap;
    }

    private List<Map<String, Object>> getDataListToWriteInFile(List<ImportRowContext> batchRows) {

        List<Map<String, Object>> dataList = new LinkedList<>();

        for (ImportRowContext importRowContext : batchRows) {
            Map<String, Object> rawRecordMap = importRowContext.getRawRecordMap();
            rawRecordMap.put("ROW NUMBER", importRowContext.getRowNumber());
            rawRecordMap.put("ROW STATUS", importRowContext.getRowStatus());
            rawRecordMap.put("ERROR MESSAGE", importRowContext.getErrorMessage());
            dataList.add(rawRecordMap);
        }

        return dataList;
    }

    public static File getTempFolder(String rootFolderName) {
        ClassLoader classLoader = DownloadErrorRecordsCommand.class.getClassLoader();
        String path = classLoader.getResource("").getFile() + File.separator + "facilio-import-error-files" + File.separator + AccountUtil.getCurrentOrg().getOrgId();
        if (rootFolderName != null) {
            path += File.separator + rootFolderName;
        }

        File file = new File(path);
        if (!(file.exists() && file.isDirectory())) {
            file.mkdirs();
        }
        return file;
    }
    private static String getFilePath(String fileName) {

        String  path = getTempFolder(null).getPath();

        return path + File.separator + fileName ;
    }
}
