package com.facilio.multiImport.command;


import com.facilio.bmsconsole.imports.annotations.RowFunction;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.importFileReader.AbstractImportFileReader;
import com.facilio.multiImport.importFileReader.AbstractImportSheetReader;
import com.facilio.multiImport.importFileReader.ImportIterator;
import com.facilio.multiImport.multiImportExceptions.ImportParseException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ParseMultiImportFileCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(ParseMultiImportFileCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        ImportFileSheetsContext importSheet = (ImportFileSheetsContext) context.get(FacilioConstants.ContextNames.IMPORT_SHEET);
        Map<Long, AbstractImportFileReader> importFileIdVsImportReaderMap = (Map<Long, AbstractImportFileReader>)context.get(FacilioConstants.ContextNames.IMPORT_FILE_READERS_MAP);

        Long importFileId = importSheet.getImportFileId();
        Long importSheetId = importSheet.getId();
        int sheetIndex = importSheet.getSheetIndex();

        List<ImportRowContext> importRowContextList = new ArrayList<>();


        AbstractImportFileReader fileReader = importFileIdVsImportReaderMap.get(importFileId);
        AbstractImportSheetReader sheetReader = fileReader.getSheetReaderAt(sheetIndex);
        RowFunction rowFunction = (RowFunction) context.get(ImportAPI.ImportProcessConstants.ROW_FUNCTION);

        long processedRowCount = 0;                    //count parse error records count only
        boolean heading = true;

        ImportIterator<Map<String, Object>> rowItr = sheetReader.iterator();
        int rowNo = 0;
        while (rowItr.hasNext()) {
            rowNo++;

            if (heading) {
                heading = false;
                rowItr.next();
                continue;
            }

            ImportRowContext rowContext = new ImportRowContext();
            rowContext.setImportSheetId(importSheetId);
            rowContext.setRowNumber(rowNo);

            Map<String, Object> rowVal = null;
            try {
                rowVal = rowItr.next(); //parse and get a row value from cell based on cell type
            } catch (Exception e) {
                String errorMessage = null;
                if (e instanceof ImportParseException) {
                    errorMessage = ((ImportParseException) e).getClientMessage();
                } else {
                    errorMessage = e.getMessage();
                }
                rowContext.setErrorOccuredRow(true);
                rowContext.setErrorMessage(errorMessage);
            }

            if (rowContext.isErrorOccuredRow()) {   //if row is errorOccured while parsing ,mark it as as error occured row and save in DP
                processedRowCount++;
                importRowContextList.add(rowContext);
                continue;
            }

            if (isRowValueEmpty(rowVal)) {       // if it is empty row, continue the loop. We won't process further
                continue;
            }

            if (rowFunction != null) {
                rowFunction.apply(rowNo, rowVal, context);
            }


            JSONObject jsonObject = FieldUtil.getAsJSON(rowVal);
            rowContext.setRowContent(jsonObject.toJSONString());

            importRowContextList.add(rowContext);

        }

        importSheet.setProcessedRowCount(processedRowCount);
        context.put(FacilioConstants.ContextNames.IMPORT_ROW_CONTEXT_LIST, importRowContextList);

        return false;
    }

    private boolean isRowValueEmpty(Map<String, Object> rowVal) {
        if (MapUtils.isEmpty(rowVal)) {
            return true;
        }

        for (Object obj : rowVal.values()) {
            if (obj != null) {
                return false;
            }
        }
        return true;
    }
}
