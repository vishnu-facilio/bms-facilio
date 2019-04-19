package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportAssetMandatoryFieldsException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportFieldValueMissingException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

public class DataParseForImportCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(DataParseForImportCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();
		
		HashMap<Integer, HashMap<String,Object>> nullUniqueFields = new HashMap<>();
		HashMap<Integer, HashMap<String, Object>> nullResources = new HashMap<>();
		HashMap<Integer, HashMap<String, Object>> duplicateEntries = new HashMap<>();
		HashMap<String, List<ImportRowContext>> groupedContext = new HashMap<String, List<ImportRowContext>>();
		ArrayList<String> requiredFields = getRequiredFields(importProcessContext.getModule().getName());
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		InputStream is = fs.readFile(importProcessContext.getFileId());
		HashMap<Integer, String> headerIndex = new HashMap<Integer, String>();
		Workbook workbook = WorkbookFactory.create(is);
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		int row_no = 0;
	
		if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) ||
				(importProcessContext.getModule().getExtendModule()!= null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))) {
			if(!fieldMapping.containsKey("resource__name") || !fieldMapping.containsKey(importProcessContext.getModule().getName() + "__site")) {
				ArrayList<String> columns = new ArrayList<String>();
				if(!fieldMapping.containsKey("resource__name") &&!fieldMapping.containsKey(importProcessContext.getModule().getName() + "__site") ) {
					columns.add("Asset Name");
					columns.add("Site");	
				}
				else if(!fieldMapping.containsKey(importProcessContext.getModule().getName() + "__site")) {
					columns.add("Site");
				}
				else {
					columns.add("Asset Name");
				}	
				throw new ImportAssetMandatoryFieldsException(null, columns, new Exception());
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
					throw new ImportAssetMandatoryFieldsException(null, columns, new Exception());
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
						if (cell.getCellTypeEnum() == CellType.STRING) {

							val = cell.getStringCellValue().trim();
						} else if (cell.getCellTypeEnum() == CellType.NUMERIC
								|| cell.getCellTypeEnum() == CellType.FORMULA) {
							if (cell.getCellTypeEnum() == CellType.NUMERIC && HSSFDateUtil.isCellDateFormatted(cell)) {
								Date date = cell.getDateCellValue();
								Instant date1 = date.toInstant();
								val = date1.getEpochSecond()*1000;
							} 
							else if(cell.getCellTypeEnum() == CellType.FORMULA) {
								val = cell.getStringCellValue();
							}
							else {
								val = cell.getNumericCellValue();
							}
						} else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
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
				
				
				
				
				if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) ||
						(importProcessContext.getModule().getExtendModule() != null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))) {
					String name = fieldMapping.get("resource__name");
					String site = fieldMapping.get(importProcessContext.getModule().getName() + "__site");
					
					if(colVal.get(name) == null || colVal.get(site) == null) {
						ArrayList<String> columns = new ArrayList<String>();
						if((colVal.get(name) == null || !colVal.containsKey(name)) && (colVal.get(site) == null || !colVal.containsKey(site))) {
							columns.add("Name");
							columns.add("Site");	
						}
						else if(colVal.get(site) == null || !colVal.containsKey(site)) {
							columns.add("Site");
						}
						else {
							columns.add("Name");
						}
						
						throw new ImportAssetMandatoryFieldsException(row_no,columns, new Exception());
					}
					else {
						uniqueString.append(colVal.get(name) + "__" + colVal.get(site));
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
		
		context.put(ImportAPI.ImportProcessConstants.GROUPED_ROW_CONTEXT, groupedContext);
		context.put(ImportAPI.ImportProcessConstants.ROW_COUNT, row_no);
		
		LOGGER.severe(groupedContext.toString());
		LOGGER.severe("Data Parsing for Import " + importProcessContext.getId() + " is complete");
		
		return false;
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
