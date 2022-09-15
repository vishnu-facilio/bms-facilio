package com.facilio.multiImport.command;

import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFileContext;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

public class ImportFileUploadCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<File> uploadedFiles = (List<File>) context.get(FacilioConstants.ContextNames.FILE);
        Long importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);
        String contentType = (String) context.get(FacilioConstants.ContextNames.FILE_CONTENT_TYPE);

        if (CollectionUtils.isEmpty(uploadedFiles)){
            throw new IllegalArgumentException("Imported file not found");
        }

        ImportDataDetails importDataDetails = MultiImportApi.getImportData(importId);
        if (importDataDetails == null) {
            throw new Exception("Import doesn't exists");
        }

        List<ImportFileContext> fileContextList = new ArrayList<>();
        for (File file : uploadedFiles) {
            String fileName = file.getName();
            FileStore fileStore = FacilioFactory.getFileStore();
            long fileId = fileStore.addFile(fileName, file, contentType);

            ImportFileContext importFileContext = new ImportFileContext();
            importFileContext.setFileName(fileName);
            importFileContext.setFileId(fileId);
            importFileContext.setImportId(importId);
            importFileContext.setFile(file);
            fileContextList.add(importFileContext);
        }
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getImportFileModule().getTableName())
                .fields(FieldFactory.getImportFileFields());

        List<Map<String,Object>> props = FieldUtil.getAsMapList(fileContextList,ImportFileContext.class);
        insertRecordBuilder.addRecords(props);
        insertRecordBuilder.save();

        for (int i=0;i< fileContextList.size();i++) {
            fileContextList.get(i).setId((Long) props.get(i).get("id"));
            File file = fileContextList.get(i).getFile();
            long importFileId = fileContextList.get(i).getId();
            Workbook workbook = WorkbookFactory.create(file);
            ImportFileSheetsContext importFileSheets;
            for(int j=0;j< workbook.getNumberOfSheets();j++){
                String sheetName = workbook.getSheetName(j);
                Sheet sheets = workbook.getSheetAt(j);
                Iterator<Row> rowItr = sheets.iterator();
                long rowCount = 0;
                while (rowItr.hasNext()){
                    rowCount++;
                    Row row = rowItr.next();
                    if (row.getPhysicalNumberOfCells() <= 0) {
                        break;
                    }
                }
                importFileSheets = addImportFileSheets(sheetName,importFileId,rowCount);
                fileContextList.get(j).setImportFileSheetsContext(importFileSheets);
            }

        }

        context.put(FacilioConstants.ContextNames.IMPORT_FILE_DETAILS, fileContextList);

        return false;
    }

    private ImportFileSheetsContext addImportFileSheets(String sheetName, long importFileId, long rowCount) throws SQLException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        ImportFileSheetsContext importFileSheetsContext = new ImportFileSheetsContext();
        importFileSheetsContext.setName(sheetName);
        importFileSheetsContext.setImportFileId(importFileId);
        importFileSheetsContext.setRowCount(rowCount);

        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getImportFileSheetsModule().getTableName())
                .fields(FieldFactory.getImportFileSheetsFields());

        Map<String,Object> props = FieldUtil.getAsProperties(importFileSheetsContext);
        insertRecordBuilder.addRecord(props);
        insertRecordBuilder.save();

        importFileSheetsContext.setId((long) props.get("id"));

        return importFileSheetsContext;
    }
}
