package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.BimImportProcessMappingContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportFieldValueMissingException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportMandatoryFieldsException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

public class GenericParseDataForImportCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(GenericParseDataForImportCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();

		HashMap<String, List<ImportRowContext>> groupedContext = new HashMap<String, List<ImportRowContext>>();
		
		FacilioModule bimModule = ModuleFactory.getBimImportProcessMappingModule();
		List<FacilioField> bimFields = FieldFactory.getBimImportProcessMappingFields();
		
		BimImportProcessMappingContext bimImport = BimAPI.getBimImportProcessMappingByImportProcessId(bimModule,bimFields,importProcessContext.getId());

		boolean isBim = (bimImport!=null);
		
		String moduleName = "";
		if(importProcessContext.getModule()!=null){
			moduleName = importProcessContext.getModule().getName();
		}else if(isBim){
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(importProcessContext.getImportJobMeta());
			moduleName = ((JSONObject)json.get("moduleInfo")).get("module").toString();
		}
		
		FileStore fs = FacilioFactory.getFileStore();
		InputStream is = fs.readFile(importProcessContext.getFileId());
		HashMap<Integer, String> headerIndex = new HashMap<Integer, String>();
		Workbook workbook = WorkbookFactory.create(is);

		int row_no = 0;
		ArrayList<String> missingColumns = new ArrayList<String>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		// For Asset, Workorder and Inventory Modules we are not checking based on this requiredFields.
		ArrayList<FacilioField> requiredFields = getRequiredFields(moduleName);

		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fieldsList = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldsList);
		if (ImportAPI.isInsertImport(importProcessContext)) {
			if (module!=null && module.isStateFlowEnabled() && !AssetsAPI.isAssetsModule(importProcessContext.getModule()) && !moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
				FacilioField moduleStateField = fieldsMap.get("moduleState");
				if (!fieldMapping.containsKey(moduleStateField.getModule().getName() + "__moduleState")) {
					missingColumns.add(moduleStateField.getDisplayName());
				}
			}
			if (module!=null && AssetsAPI.isAssetsModule(importProcessContext.getModule())) {
				if (!fieldMapping.containsKey("resource__name")) {
					missingColumns.add("Asset Name");
				}
				if (ImportAPI.isInsertImport(importProcessContext) && !fieldMapping.containsKey("asset__category")) {
					missingColumns.add("Category");
				}
			} else if (moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
				if (!fieldMapping.containsKey("ticket__subject")) {
					missingColumns.add("Subject");
				}
				if (!fieldMapping.containsKey("ticket__moduleState")) {
					missingColumns.add("Module State");
				}
			} else if (moduleName.equals(FacilioConstants.ContextNames.PURCHASED_ITEM)) {
				if (!fieldMapping.containsKey("itemTypes__name")) {
					missingColumns.add("Name");
				}
			} else if (moduleName.equals(FacilioConstants.ContextNames.PURCHASED_TOOL)) {
				if (!fieldMapping.containsKey("toolTypes__name")) {
					missingColumns.add("Name");
				}
			} else if (requiredFields.size() != 0) {

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
		
		String sheetName = (String) context.get(FacilioConstants.ContextNames.SHEET_NAME);
		
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			Sheet datatypeSheet = workbook.getSheetAt(i);
			
			if(sheetName == null || datatypeSheet.getSheetName().equalsIgnoreCase(sheetName)){
				Iterator<Row> rowItr = datatypeSheet.iterator();
				boolean heading = true;
				
				while (rowItr.hasNext()) {
					StringBuilder uniqueString = new StringBuilder();
					ImportRowContext rowContext = new ImportRowContext();
					row_no++;
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

					
					if(ImportAPI.isInsertImport(importProcessContext) && AssetsAPI.isAssetsModule(importProcessContext.getModule())) {
						String name = fieldMapping.get("resource__name");
						String category = fieldMapping.get("asset__category");
						ArrayList<String> columns = new ArrayList<>();
						if(!colVal.containsKey(name) || colVal.get(name) == null) {
							columns.add("Name");
						}
						if(ImportAPI.isInsertImport(importProcessContext) && (!colVal.containsKey(category) || colVal.get(category) == null)) {
							columns.add("Category");
						}
						if (CollectionUtils.isNotEmpty(columns)) {
							throw new ImportMandatoryFieldsException(row_no, columns, new Exception());
						}
						else {
							// For Asset Name is unique
							uniqueString.append(colVal.get(name));
						}
					}
					else if (ImportAPI.isInsertImport(importProcessContext) && moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
						String name = fieldMapping.get("ticket__subject");
						ArrayList<String> columns = new ArrayList<>();
						if (!colVal.containsKey(name) || (colVal.get(name) == null)) {
							columns.add("Subject");
						}
						if (CollectionUtils.isNotEmpty(columns)) {
							throw new ImportMandatoryFieldsException(row_no, columns, new Exception());
						}  else {
							uniqueString = getUniqueString(groupedContext, uniqueString);
						}
					} else if (ImportAPI.isInsertImport(importProcessContext) && moduleName.equals(FacilioConstants.ContextNames.PURCHASED_ITEM)) {
						String name = fieldMapping.get("itemTypes__name");
						ArrayList<String> columns = new ArrayList<>();
						if (!colVal.containsKey(name) || (colVal.get(name) == null)) {
							columns.add("Name");
						}
						if (CollectionUtils.isNotEmpty(columns)) {
							throw new ImportMandatoryFieldsException(row_no, columns, new Exception());
						} else {
							uniqueString = getUniqueString(groupedContext, uniqueString);
						}
					} else if (ImportAPI.isInsertImport(importProcessContext) && moduleName.equals(FacilioConstants.ContextNames.PURCHASED_TOOL)) {
						String name = fieldMapping.get("toolTypes__name");
						ArrayList<String> columns = new ArrayList<>();
						if (!colVal.containsKey(name) || (colVal.get(name) == null)) {
							columns.add("Name");
						}
						if (CollectionUtils.isNotEmpty(columns)) {
							throw new ImportMandatoryFieldsException(row_no, columns, new Exception());
						} else {
							uniqueString = getUniqueString(groupedContext, uniqueString);
						}
					} else {
						uniqueString = getUniqueString(groupedContext, uniqueString);
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
					if (!groupedContext.containsKey(uniqueString.toString())) {
						List<ImportRowContext> rowContexts = new ArrayList<ImportRowContext>();
						rowContexts.add(rowContext);
						groupedContext.put(uniqueString.toString(), rowContexts);
					} else {
						groupedContext.get(uniqueString.toString()).add(rowContext);
					}
				}
			}
		}
		
		context.put(ImportAPI.ImportProcessConstants.GROUPED_ROW_CONTEXT, groupedContext);
		context.put(ImportAPI.ImportProcessConstants.ROW_COUNT, row_no);
		workbook.close();
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
	
	private ArrayList<FacilioField> getRequiredFields(String moduleName) throws Exception{
		ArrayList<FacilioField> fields = new ArrayList<FacilioField>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField primaryField = modBean.getPrimaryField(moduleName);
		if(primaryField != null) {
			fields.add(primaryField);
		}
		switch (moduleName) {
			// fill pm required fields here 
		}
		return fields;
	}
		
}
