package com.facilio.bmsconsole.imports.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportFieldValueMissingException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportMandatoryFieldsException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.imports.annotations.RowFunction;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

public class ParseImportFileCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(ParseImportFileCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        if (MapUtils.isEmpty(importProcessContext.getFieldMapping())) {
            throw new IllegalArgumentException("Field mapping not found");
        }

        HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();
        HashMap<String, List<ImportRowContext>> groupedContext = new HashMap<String, List<ImportRowContext>>();

        FileStore fs = FacilioFactory.getFileStore();
        InputStream is = fs.readFile(importProcessContext.getFileId());
        HashMap<Integer, String> headerIndex = new HashMap<Integer, String>();
        Workbook workbook = WorkbookFactory.create(is);

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ArrayList<FacilioField> requiredFields = (ArrayList<FacilioField>) context.get(ImportAPI.ImportProcessConstants.REQUIRED_FIELDS);
        if (CollectionUtils.isEmpty(requiredFields)) {
            requiredFields = getRequiredFields(moduleName);
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fieldsList = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldsList);

        if (ImportAPI.isInsertImport(importProcessContext)) {
            // Check whether there is a mapping for the required fields or not
            ArrayList<String> missingColumns = new ArrayList<String>();
            if (CollectionUtils.isNotEmpty(requiredFields)) {
                for (FacilioField field : requiredFields) {
                    if (!fieldMapping.containsKey(field.getModule().getName() + "__" + field.getName())) {
                        missingColumns.add(field.getDisplayName());
                    }
                }
            }
            if (missingColumns.size() > 0) {
                throw new ImportMandatoryFieldsException(null, missingColumns, new Exception());
            }
        }

        RowFunction uniqueFunction = (RowFunction) context.get(ImportAPI.ImportProcessConstants.UNIQUE_FUNCTION);
        if (uniqueFunction == null) {
            uniqueFunction = ParseImportFileCommand::getUniqueFunction;
        }
        RowFunction rowFunction = (RowFunction) context.get(ImportAPI.ImportProcessConstants.ROW_FUNCTION);

        String sheetName = (String) context.get(FacilioConstants.ContextNames.SHEET_NAME);
        int totalRowCount = 0;
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet datatypeSheet = workbook.getSheetAt(i);

            // if sheet name is not empty, and sheet name doesn't match
            if (StringUtils.isNotEmpty(sheetName) && !sheetName.equalsIgnoreCase(datatypeSheet.getSheetName())) {
                continue;
            }

            Iterator<Row> rowItr = datatypeSheet.iterator();
            boolean heading = true;

            int rowNo = 0;
            while (rowItr.hasNext()) {
                ImportRowContext rowContext = new ImportRowContext();
                rowNo++;
                Row row = rowItr.next();

                if (row.getPhysicalNumberOfCells() <= 0) {
                    break;
                }

                Iterator<Cell> cellItr = row.cellIterator();
                if (heading) { // get the heading info
                    int cellIndex = 0;
                    while (cellItr.hasNext()) {
                        Cell cell = cellItr.next();
                        String cellValue = cell.getStringCellValue();
                        headerIndex.put(cellIndex, cellValue);
                        cellIndex++;
                    }
                    heading = false;
                    continue;   // skip processing real data
                }

                HashMap<String, Object> rowVal = getRowVal(cellItr, headerIndex, workbook, rowNo);

                if (isRowValueEmpty(rowVal)) {
                    break;  // if it is empty cell, break the loop. We won't process further
                }

                rowContext.setRowNumber(rowNo);
                rowContext.setColVal(rowVal);
                rowContext.setSheetNumber(i);

                if (ImportAPI.isInsertImport(importProcessContext)) {
                    checkMandatoryFields(requiredFields, context, rowVal, rowNo);
                }

                Object uniqueStringObject = uniqueFunction.apply(rowNo, rowVal, context);
                String uniqueString;
                if (uniqueStringObject == null) {
                    LOGGER.info("Unique string is null -> fallback to default method");
                    uniqueString = getUniqueFunction(rowNo, rowVal, context);
                } else {
                    uniqueString = uniqueStringObject.toString();
                }

                if(importProcessContext.getImportSetting() != ImportProcessContext.ImportSetting.INSERT.getValue()) {
                    checkMandatoryUniqueFields(importProcessContext, rowVal, fieldMapping, rowNo);
                }

                if (rowFunction != null) {
                    rowFunction.apply(rowNo, rowVal, context);
                }

                if (!groupedContext.containsKey(uniqueString)) {
                    List<ImportRowContext> rowContexts = new ArrayList<>();
                    rowContexts.add(rowContext);
                    groupedContext.put(uniqueString, rowContexts);
                } else {
                    groupedContext.get(uniqueString).add(rowContext);
                }
            }

            totalRowCount += rowNo;
        }

        context.put(ImportAPI.ImportProcessConstants.GROUPED_ROW_CONTEXT, groupedContext);
        context.put(ImportAPI.ImportProcessConstants.ROW_COUNT, totalRowCount);
        workbook.close();

        return false;
    }

    private HashMap<String, Object> getRowVal(Iterator<Cell> cellItr, HashMap<Integer, String> headerIndex, Workbook workbook, int rowNo) throws ImportParseException {
        HashMap<String, Object> rowVal = new HashMap<>();
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        while (cellItr.hasNext()) {
            Cell cell = cellItr.next();

            String cellName = headerIndex.get(cell.getColumnIndex());
            if (StringUtils.isEmpty(cellName)) {
                continue;
            }

            Object val;
            try {
                CellValue cellValue = evaluator.evaluate(cell);
                val = ImportAPI.getValueFromCell(cell, cellValue);
            } catch(Exception e) {
                throw new ImportParseException(rowNo, cellName, e);
            }
            rowVal.put(cellName, val);
        }
        return rowVal;
    }

    /**
     * Check whether the uniqueField to match data is not empty. Throws error when any of these fields
     * for a particular row is null.
     *
     * @param importProcessContext
     * @param rowVal
     * @param fieldMapping
     * @param rowNo
     * @throws Exception
     */
    private void checkMandatoryUniqueFields(ImportProcessContext importProcessContext, HashMap<String, Object> rowVal, HashMap<String, String> fieldMapping, int rowNo) throws Exception {
        String settingArrayName = getSettingString(importProcessContext);
        List<String> fieldList = null;
        if(importProcessContext.getImportJobMetaJson() != null &&
                !importProcessContext.getImportJobMetaJson().isEmpty() &&
                importProcessContext.getImportJobMetaJson().containsKey(settingArrayName)) {

            fieldList = (List<String>) importProcessContext.getImportJobMetaJson().get(settingArrayName);
        }

        if (CollectionUtils.isNotEmpty(fieldList)) {
            for(String field : fieldList) {
                if(rowVal.get(fieldMapping.get(field)) == null) {
                    throw new ImportFieldValueMissingException(rowNo, fieldMapping.get(field), new Exception());
                }
            }
        }
    }

    /**
     * Check the value of the particular row is null.
     *
     * @param mandatoryFields
     * @param context
     * @param rowVal
     * @param rowNo
     * @throws ImportMandatoryFieldsException
     */
    private void checkMandatoryFields(List<FacilioField> mandatoryFields, Context context, HashMap<String, Object> rowVal, int rowNo) throws ImportMandatoryFieldsException {
        if (CollectionUtils.isNotEmpty(mandatoryFields)) {
            ArrayList<String> columns = new ArrayList<>();
            for (FacilioField field : mandatoryFields) {
                String fieldColumnName = field.getModule().getName() + "__" + field.getName();
                if (rowVal.containsKey(fieldColumnName) || rowVal.get(fieldColumnName) == null) {
                    columns.add(field.getDisplayName());
                }
            }

            if (CollectionUtils.isNotEmpty(columns)) {
                throw new ImportMandatoryFieldsException(rowNo, columns, new Exception());
            }
        }
    }

    private String getSettingString(ImportProcessContext importProcessContext) {
        if(importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.INSERT_SKIP.getValue()) {
            return "insertBy";
        }
        else {
            return "updateBy";
        }
    }

    private ArrayList<FacilioField> getRequiredFields(String moduleName) throws Exception{
        ArrayList<FacilioField> fields = new ArrayList<FacilioField>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = modBean.getAllFields(moduleName);
        for (FacilioField field : allFields) {
            if (field.isRequired()) {
                allFields.add(field);
            }
        }
        return fields;
    }

    private static String getUniqueFunction(Integer rowNumber, Map<String, Object> rowVal, Context context) {
        return null;
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
