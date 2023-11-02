/**
 * 
 */
package com.facilio.bmsconsole.commands;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.poi.ss.usermodel.*;

import com.facilio.bmsconsole.actions.PointsProcessContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportMandatoryFieldsException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportFieldValueMissingException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.util.ImportPointsAPI;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.factory.FacilioFactory;

/**
 * @author facilio
 *
 */
public class PointsParseDataForImportCommand implements Command {

	/* (non-Javadoc)
	 * @see org.apache.commons.chain.Command#execute(org.apache.commons.chain.Context)
	 */
	private static final Logger LOGGER = Logger.getLogger(PointsParseDataForImportCommand.class.getName());
	private org.apache.log4j.Logger log = LogManager.getLogger(PointsParseDataForImportCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		PointsProcessContext importProcessContext = (PointsProcessContext) context.get(ImportPointsAPI.ImportPointsConstants.POINTS_PROCESS_CONTEXT);
		HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();
		
		HashMap<Integer, HashMap<String,Object>> nullUniqueFields = new HashMap<>();
		HashMap<Integer, HashMap<String, Object>> nullResources = new HashMap<>();
		HashMap<Integer, HashMap<String, Object>> duplicateEntries = new HashMap<>();
		HashMap<String, List<ImportRowContext>> groupedContext = new HashMap<String, List<ImportRowContext>>();
		ArrayList<String> requiredFields = getRequiredFields(importProcessContext.getModule().getName());
		FileStore fs = FacilioFactory.getFileStore();
		InputStream is = fs.readFile(importProcessContext.getFileId());
		HashMap<Integer, String> headerIndex = new HashMap<Integer, String>();
		Workbook workbook = WorkbookFactory.create(is);
		
		
		int row_no = 0;
	
		if(importProcessContext.getControllerId()== -1) {
			if(!fieldMapping.containsKey("device") ) {
				ArrayList<String> columns = new ArrayList<String>();
				if(!fieldMapping.containsKey("categoryId") &&!fieldMapping.containsKey("resourceId") && !fieldMapping.containsKey("fieldId")) {
					columns.add("Asset Category");
					columns.add("Asset");	
					columns.add("Reading Field");
				}
				else if(!fieldMapping.containsKey("categoryId")) {
					columns.add("Asset Category");
				}
				else if(!fieldMapping.containsKey("resourceId")){
					columns.add("Asset");
				}	
				else if(!fieldMapping.containsKey("fieldId")) {
					columns.add("Reading Field");
				}
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
				LOGGER.severe("row_no -- " + row_no);
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

				Iterator<Cell> cellItr = row.cellIterator();
				while (cellItr.hasNext()) {
					Cell cell = cellItr.next();

					String cellName = headerIndex.get(cell.getColumnIndex());
					if (cellName == null || cellName == "") {
						continue;
					}

					Object val = 0.0;
					try {
						if (cell.getCellType() == CellType.STRING) {

							val = cell.getStringCellValue().trim();
						} else if (cell.getCellType() == CellType.NUMERIC
								|| cell.getCellType() == CellType.FORMULA) {
							if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
								Date date = cell.getDateCellValue();
								Instant date1 = date.toInstant();
								val = date1.getEpochSecond()*1000;
							} 
							else if(cell.getCellType() == CellType.FORMULA) {
								val = cell.getStringCellValue();
							}
							else {
								val = cell.getNumericCellValue();
							}
						} else if (cell.getCellType() == CellType.BOOLEAN) {
							val = cell.getBooleanCellValue();
						} else {
							val = null;
						}
					}catch(Exception e) {
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
				
				
				
				if(importProcessContext.getColumnHeadings() != null) {
					String deviceName = fieldMapping.get("device");
					String instanceName = fieldMapping.get("instance");
					String assetCategoryName = fieldMapping.get("categoryId");
					String assetName = fieldMapping.get("resourceId");
					String ReadingFieldName = fieldMapping.get("fieldId");
					
					if(colVal.get(instanceName) == null || colVal.get(deviceName) == null || colVal.get(assetCategoryName) == null || colVal.get(assetName) == null || colVal.get(ReadingFieldName)== null) {
						ArrayList<String> columns = new ArrayList<String>();

						if(colVal.get(deviceName) == null ) {
							columns.add("Device Name");
						}
						if(colVal.get(instanceName) == null ) {
							columns.add("Instance Name");
						}
						if(colVal.get(assetCategoryName) == null ) {
							columns.add("Asset Category");
						}
						 if(colVal.get(assetName) == null ) {
							columns.add("Asset");
						}
						 if(colVal.get(ReadingFieldName)==null){
							columns.add("ReadingField");
						}
						
						throw new ImportMandatoryFieldsException(row_no,columns, new Exception());
					}
					else {
						uniqueString.append(colVal.get("name") );
					}
				}
				else if(requiredFields.size() != 0) {
					ArrayList<String> columns  = new ArrayList<String>();
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
						List<Integer> keys= new ArrayList(groupedContext.keySet());
						Integer lastKey = new Integer(0);
						if(keys.size() == 0) {
							lastKey = 1;
						}
						else {
							lastKey = keys.size() + 1;
						}
						
						uniqueString = uniqueString.append(lastKey.toString());
					}
				}
					
				if(importProcessContext.getImportSetting() != PointsProcessContext.ImportSetting.INSERT.getValue()) {
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
				
				LOGGER.severe("UNIQUE STRING!!!!!!!!!");
				LOGGER.severe(uniqueString.toString());
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
		
		context.put(ImportPointsAPI.ImportPointsConstants.GROUPED_ROW_CONTEXT, groupedContext);
		context.put(ImportPointsAPI.ImportPointsConstants.ROW_COUNT, row_no);
		workbook.close();
		LOGGER.severe(groupedContext.toString());
		LOGGER.severe("Data Parsing for Import " + importProcessContext.getId() + " is complete");
		
		return false;
	}
	
	private String getSettingString(PointsProcessContext importProcessContext) {
		if(importProcessContext.getImportSetting() == PointsProcessContext.ImportSetting.INSERT_SKIP.getValue()) {
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
