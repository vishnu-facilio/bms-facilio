package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFileContext;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.enums.ImportDataStatus;
import com.facilio.multiImport.importFileReader.*;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportFileUploadCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<File> files = (List<File>) context.get(FacilioConstants.ContextNames.FILE);
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);
        List<String> contentTypes = (List<String>) context.get(FacilioConstants.ContextNames.FILE_CONTENT_TYPE);

        if (CollectionUtils.isEmpty(files)) {
            throw new IllegalArgumentException("Imported file not found");
        }

        List<ImportFileContext> fileContextList = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            String fileName = file.getName();
            String contentType = contentTypes.get(i);
            FileStore fileStore = FacilioFactory.getFileStore();
            long fileId = fileStore.addFile(fileName, file, contentType);

            ImportFileContext importFileContext = new ImportFileContext();
            importFileContext.setFileName(fileName);
            importFileContext.setFileId(fileId);
            importFileContext.setImportId(importId);
            importFileContext.setFile(file);
            importFileContext.setFileSize(file.length());
            fileContextList.add(importFileContext);
        }
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder().table(ModuleFactory.getImportFileModule().getTableName()).fields(FieldFactory.getImportFileFields());

        List<Map<String, Object>> props = FieldUtil.getAsMapList(fileContextList, ImportFileContext.class);
        insertRecordBuilder.addRecords(props);
        insertRecordBuilder.save();


        //sheets entry
        for (int i = 0; i < fileContextList.size(); i++) {
            ImportFileContext fileContext = fileContextList.get(i);
            String fileType = MultiImportApi.getFileType(fileContext);
            fileContext.setId((Long) props.get(i).get("id"));
            long importFileId = fileContext.getId();

            File file = fileContext.getFile();

            AbstractImportFileReader reader = MultiImportApi.getImportFileReader(fileType, file);

            int numberOfSheets = reader.getNumberOfSheets();

            List<ImportFileSheetsContext> sheetList = new ArrayList<>(numberOfSheets);
            fileContext.setImportFileSheetsContext(sheetList);

            for (int j = 0; j < numberOfSheets; j++) {
                ImportFileSheetsContext sheet = null;
                AbstractImportSheetReader sheetReader = reader.getSheetReaderAt(j);
                ImportIterator<HashMap<String, Object>> iterator = sheetReader.iterator();
                List<HashMap<String, Object>> firstAndSecondRow = new ArrayList<>();
                String sheetName = sheetReader.getSheetName();
                int totalRowCount = sheetReader.getTotalRowCount();
                String columnHeadingString = sheetReader.getColumnHeadings();
                int rowNo = 0;
                while (iterator.hasNext()) {
                    rowNo++;
                    if (rowNo == 1) {
                        iterator.next();
                        continue;
                    }
                    if (rowNo == 4) {
                        break;
                    }
                    HashMap<String, Object> rowVal = null;
                    try {
                        rowVal = iterator.next();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    firstAndSecondRow.add(rowVal);
                }

                sheet = getImportFileSheets(sheetName, j, importFileId, totalRowCount, columnHeadingString, firstAndSecondRow);
                sheetList.add(sheet);
            }
            addImportFileSheets(sheetList);

            reader.close();

        }

        updateMultiImportDataDetails(importId);

        context.put(FacilioConstants.ContextNames.IMPORT_FILE_LIST, fileContextList);

        return false;
    }

    private void addImportFileSheets(List<ImportFileSheetsContext> sheets) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, SQLException {
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder().table(ModuleFactory.getImportFileSheetsModule().getTableName()).fields(FieldFactory.getImportFileSheetsFields());

        List<Map<String, Object>> props = FieldUtil.getAsMapList(sheets, ImportFileSheetsContext.class);
        insertRecordBuilder.addRecords(props);
        insertRecordBuilder.save();

        for (int i = 0; i < sheets.size(); i++) {
            ImportFileSheetsContext sheet = sheets.get(i);
            Map<String, Object> prop = props.get(i);
            long sheetId = (long) prop.get("id");
            sheet.setId(sheetId);
        }
    }

    private ImportFileSheetsContext getImportFileSheets(String sheetName, int sheetIndex, long importFileId, long rowCount, String columnHeadingString, List<HashMap<String, Object>> firstRowAndSecondString) throws SQLException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        ImportFileSheetsContext importFileSheetsContext = new ImportFileSheetsContext();
        importFileSheetsContext.setName(sheetName);
        importFileSheetsContext.setSheetIndex(sheetIndex);
        importFileSheetsContext.setImportFileId(importFileId);
        importFileSheetsContext.setRowCount(rowCount);
        importFileSheetsContext.setColumnHeadingString(columnHeadingString);

        if (CollectionUtils.isNotEmpty(firstRowAndSecondString)) {
            if (firstRowAndSecondString.size() >= 1 && firstRowAndSecondString.get(0)!=null) {
                HashMap<String, Object> firstRow = firstRowAndSecondString.get(0);
                JSONObject jsonObject = FieldUtil.getAsJSON(firstRow, false);
                String firstRowString = jsonObject.toJSONString();
                importFileSheetsContext.setFirstRowString(firstRowString);
            }
            if (firstRowAndSecondString.size() == 2 && firstRowAndSecondString.get(1)!=null) {
                HashMap<String, Object> secondRow = firstRowAndSecondString.get(1);
                JSONObject jsonObject = FieldUtil.getAsJSON(secondRow, false);
                String secondRowString = jsonObject.toJSONString();
                importFileSheetsContext.setSecondRowString(secondRowString);
            }
        }
        return importFileSheetsContext;
    }

    private static void updateMultiImportDataDetails(long importId) throws Exception {
        ImportDataDetails importDataDetails = new ImportDataDetails();
        importDataDetails.setId(importId);

        importDataDetails.setStatus(ImportDataStatus.UPLOAD_COMPLETED);
        MultiImportApi.updateImportDataDetails(importDataDetails);
    }

}
