package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportFieldValueMissingException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportMandatoryFieldsException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class GenericParseDataForImportCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(GenericParseDataForImportCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();

		HashMap<String, List<ImportRowContext>> groupedContext = new HashMap<String, List<ImportRowContext>>();
		ArrayList<String> requiredFields = getRequiredFields(importProcessContext.getModule().getName());
		FileStore fs = FacilioFactory.getFileStore();
		InputStream is = fs.readFile(importProcessContext.getFileId());
		HashMap<Integer, String> headerIndex = new HashMap<Integer, String>();
		Workbook workbook = WorkbookFactory.create(is);

		int row_no = 0;

		Long siteId = importProcessContext.getSiteId();
		ArrayList<String> missingColumns = new ArrayList<String>();
		if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) ||
				(importProcessContext.getModule().getExtendModule()!= null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))) {
			if(ImportAPI.isInsertImport(importProcessContext) && (!fieldMapping.containsKey("resource__name") || (siteId == null))) {
				if(!fieldMapping.containsKey("resource__name") && (siteId == null) ) {
					missingColumns.add("Asset Name");
					missingColumns.add("Site");
				}
				else if(siteId == null) {
					missingColumns.add("Site");
				}
				else {
					missingColumns.add("Asset Name");
				}
				throw new ImportMandatoryFieldsException(null, missingColumns, new Exception());
			}
		} else if (importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.WORK_ORDER)) {
			if(!fieldMapping.containsKey("ticket__subject") || !fieldMapping.containsKey("workorder__site")) {
				if(!fieldMapping.containsKey("ticket__subject") && !fieldMapping.containsKey("workorder__site")) {
					missingColumns.add("Subject");
					missingColumns.add("Site");
				}
				else if(!fieldMapping.containsKey("workorder__site")) {
					missingColumns.add("Site");
				}
				else {
					missingColumns.add("Subject");
				}
				throw new ImportMandatoryFieldsException(null, missingColumns, new Exception());
			}
		}
		
		else if(requiredFields.size() != 0) {
			ArrayList<String> columns = new ArrayList<String>();
			
			for(String field: requiredFields) {
				if(!fieldMapping.containsKey(importProcessContext.getModule().getName() + "__" + field)) {
					columns.add(field);
			}
			}
				if(columns.size() != 0) {
					throw new ImportMandatoryFieldsException(null, columns, new Exception());
				}
		}
			
		
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			Sheet datatypeSheet = workbook.getSheetAt(i);

			Iterator<Row> rowItr = datatypeSheet.iterator();
			boolean heading = true;
			
			while (rowItr.hasNext()) {
				StringBuilder uniqueString = new StringBuilder();
				ImportRowContext rowContext = new ImportRowContext();
				row_no++;
				LOGGER.info("row_no -- " + row_no);
				Row row = rowItr.next();

				if (row.getPhysicalNumberOfCells() <= 0) {
					break;
				}
				if (heading) {
					Iterator<Cell> cellItr = row.cellIterator();
					int cellIndex = 0;
					while (cellItr.hasNext()) {
						Cell cell = cellItr.next();
						String cellValue = cell.getStringCellValue();
						headerIndex.put(cellIndex, cellValue);
						cellIndex++;
					}
					heading = false;
					continue;
				}

				HashMap<String, Object> colVal = new HashMap<>();
				FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
				Iterator<Cell> cellItr = row.cellIterator();
				while (cellItr.hasNext()) {
					Cell cell = cellItr.next();

					String cellName = headerIndex.get(cell.getColumnIndex());
					if (cellName == null || cellName == "") {
						continue;
					}
					Object val = 0.0;
					try {
						CellValue cellValue = evaluator.evaluate(cell);
						val = ImportAPI.getValueFromCell(cell, cellValue);
					} catch(Exception e) {
						throw new ImportParseException(row_no, cellName, e);
					}
					
					colVal.put(cellName, val);
				}
				
				if (colVal.values() == null || colVal.values().isEmpty()) {
					break;
				} else {
					boolean isAllNull = true;
					for (Object value : colVal.values()) {
						if (value != null) {
							isAllNull = false;
							break;
						}
					}
					if (isAllNull) {
						break;
					}
				}
				
				rowContext.setRowNumber(row_no);
				rowContext.setColVal(colVal);
				rowContext.setSheetNumber(i);
				
				
				
				
				if(ImportAPI.isInsertImport(importProcessContext) && importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) ||
						(importProcessContext.getModule().getExtendModule() != null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))) {
					String name = fieldMapping.get("resource__name");
					ArrayList<String> columns = new ArrayList<>();
					if(!colVal.containsKey(name) || colVal.get(name) == null) {
						columns.add("Name");
					}
					if (CollectionUtils.isNotEmpty(columns)) {
						throw new ImportMandatoryFieldsException(row_no, columns, new Exception());
					}
					else {
						uniqueString.append(colVal.get(name));
					}
				}
				else if (ImportAPI.isInsertImport(importProcessContext) && importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.WORK_ORDER)) {
					String name = fieldMapping.get("ticket__subject");
					String site = fieldMapping.get("workorder__site");
					ArrayList<String> columns = new ArrayList<>();
					if (!colVal.containsKey(name) || (colVal.get(name) == null)) {
						columns.add("Subject");
					}
					if (!colVal.containsKey(name) || colVal.get(site) == null) {
						columns.add("Site");
					}
					if (CollectionUtils.isNotEmpty(columns)) {
						throw new ImportMandatoryFieldsException(row_no, columns, new Exception());
					}
					else {
						uniqueString = getUniqueString(groupedContext, uniqueString);
					}
				}
				else if(requiredFields.size() != 0) {
					for(String field : requiredFields) {
						if(colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__" + field)) == null) {
							throw new ImportFieldValueMissingException(row_no, fieldMapping.get(importProcessContext.getModule().getName() + "__" + field), new Exception());
						}
						else {
							if(requiredFields.indexOf(field) != requiredFields.size() - 1) {
								uniqueString.append(colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__" + field)));
								uniqueString.append("__");
							}
							else {
								uniqueString.append(colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__" + field)));
							}
							
						}
					}
				}
				else {
					
					if(requiredFields.size() == 0) {
						uniqueString = getUniqueString(groupedContext, uniqueString);
					}
				}
					
				if(importProcessContext.getImportSetting() != ImportProcessContext.ImportSetting.INSERT.getValue()) {
					String settingArrayName = getSettingString(importProcessContext);
					ArrayList<String> fieldList = new ArrayList<String>();
					if(importProcessContext.getImportJobMetaJson() != null && 
							 !importProcessContext.getImportJobMetaJson().isEmpty() &&
							importProcessContext.getImportJobMetaJson().containsKey(settingArrayName)) {
						
						fieldList = (ArrayList<String>)importProcessContext.getImportJobMetaJson().get(settingArrayName);
					}
					

					for(String field : fieldList) {
						if(colVal.get(fieldMapping.get(field)) == null) {
							throw new ImportFieldValueMissingException(row_no, fieldMapping.get(field), new Exception());
						}
					}
					
				}

				LOGGER.info("UNIQUE STRING -- " + uniqueString.toString());
				if(!groupedContext.containsKey(uniqueString.toString())) {
					List<ImportRowContext> rowContexts = new ArrayList<ImportRowContext>();
					rowContexts.add(rowContext);
					groupedContext.put(uniqueString.toString(), rowContexts);
				}
				else {
					groupedContext.get(uniqueString.toString()).add(rowContext);
				}
			}
		}
		
		context.put(ImportAPI.ImportProcessConstants.GROUPED_ROW_CONTEXT, groupedContext);
		context.put(ImportAPI.ImportProcessConstants.ROW_COUNT, row_no);
		workbook.close();
		LOGGER.info(groupedContext.toString());
		LOGGER.info("Data Parsing for Import " + importProcessContext.getId() + " is complete");
		
		return false;
	}

	private StringBuilder getUniqueString(HashMap<String, List<ImportRowContext>> groupedContext, StringBuilder uniqueString) {
		List<Integer> keys= new ArrayList(groupedContext.keySet());
		Integer lastKey = 0;
		if(keys.size() == 0) {
			lastKey = 1;
		}
		else {
			lastKey = keys.size() + 1;
		}

		uniqueString = uniqueString.append(lastKey.toString());
		return uniqueString;
	}

	private String getSettingString(ImportProcessContext importProcessContext) {
		if(importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.INSERT_SKIP.getValue()) {
			return "insertBy";
		}
		else {
			return "updateBy";
		}
	}
	
	private ArrayList<String> getRequiredFields(String moduleName){
		ArrayList<String> fields = new ArrayList<String>();
		switch (moduleName) {
		}
		return fields;
	}
		
}
