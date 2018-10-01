package com.facilio.bmsconsole.commands;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportTemplateAction;
import com.facilio.bmsconsole.actions.ImportTemplateContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.google.common.collect.ArrayListMultimap;


public class DataParseForReadingsCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(DataParseForReadingsCommand.class.getName());
	
	private static ArrayListMultimap<String, String> groupedFields;
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		ArrayListMultimap<String, ReadingContext> groupedContext = ArrayListMultimap.create();
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		Long templateID = importProcessContext.getTemplateId();
		ImportTemplateAction importTemplateAction = new ImportTemplateAction();
		ImportTemplateContext importTemplateContext = importTemplateAction.fetchTemplate(templateID);
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		InputStream is = fs.readFile(importProcessContext.getFileId());
		HashMap<String,String> fieldMapping = importTemplateContext.getFieldMapping();
		HashMap<String,String> uniqueMapping = importTemplateContext.getUniqueMapping();
		List<ReadingContext> readingContexts = new ArrayList<>();
//		importProcessContext.setStatus(ImportProcessContext.ImportStatus.IN_PROGRESS.getValue());
//		ImportAPI.updateImportProcess(importProcessContext);
		fieldMapParsing(fieldMapping);
		
		List<String> moduleNames = new ArrayList<>(groupedFields.keySet());
		HashMap<Integer, String> headerIndex = new HashMap<Integer, String>();
		Workbook workbook = WorkbookFactory.create(is);
		
		for(int i=0;i<workbook.getNumberOfSheets();i++) {
			Sheet datatypeSheet = workbook.getSheetAt(i);

			Iterator<Row> rowItr = datatypeSheet.iterator();
			boolean heading=true;
			int row_no = 0;
			while (rowItr.hasNext()) {
				row_no++;
				LOGGER.severe("row_no -- "+row_no);
				Row row = rowItr.next();
				
				if(row.getPhysicalNumberOfCells() <= 0) {
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
					heading=false;
					continue;
				}
				
				HashMap<String, Object> colVal = new HashMap<>();

				Iterator<Cell> cellItr = row.cellIterator();
				while (cellItr.hasNext()) {
					Cell cell = cellItr.next();

					String cellName = headerIndex.get(cell.getColumnIndex());
					if (cellName == null) {
						continue;
					}

					Object val = 0.0;
					if (cell.getCellTypeEnum() == CellType.STRING) {

						val = cell.getStringCellValue();
					}
					else if (cell.getCellTypeEnum() == CellType.NUMERIC || cell.getCellTypeEnum() == CellType.FORMULA) {
						if(HSSFDateUtil.isCellDateFormatted(cell) && cell.getCellTypeEnum() == CellType.NUMERIC) {
							Date date = cell.getDateCellValue();
							 Instant date1 = date.toInstant();
							 val = (Long) date1.getEpochSecond()*1000;
						}
						else if(cell.getCellTypeEnum() == CellType.FORMULA) {
							val = cell.getNumericCellValue();
						}
						else {
							val = cell.getNumericCellValue();
						}
					}
					else if(cell.getCellTypeEnum() == CellType.BOOLEAN) {
						val = cell.getBooleanCellValue();
					}
					else {
						val = null;
					}
					colVal.put(cellName, val);

				}
				
				if(colVal.values() == null || colVal.values().isEmpty()) {
					break;
				}
				else {
					boolean isAllNull = true;
					for( Object value:colVal.values()) {
						if(value != null) {
							isAllNull = false;
							break;
						}
					}
					if(isAllNull) {
						break;
					}
				}
				LOGGER.severe("row -- "+row_no+" colVal --- "+colVal);
				
				
				
				for(String module : moduleNames) {
					List<String> fields  = new ArrayList(groupedFields.get(module));
					
					HashMap<String,Object> props = new HashMap<>();
					
					JSONObject meta = importTemplateContext.getModuleJSON();
					if(!meta.isEmpty()) {
						Long parentId =(Long) meta.get(ImportAPI.ImportProcessConstants.PARENT_ID_FIELD);
						if(parentId == null) {
							parentId = getAssetByUniqueness(colVal, importProcessContext.getModule().getName(), uniqueMapping);
							props.put(ImportAPI.ImportProcessConstants.PARENT_ID_FIELD, parentId);
						}
						else {
							props.put(ImportAPI.ImportProcessConstants.PARENT_ID_FIELD,parentId);
						}
					}
					
					fieldMapping.forEach((key,value) -> {
					Boolean isfilled = false;
					Object cellValue = colVal.get(value);
					String moduleAndField [] = key.split("__");
					String field = moduleAndField[(moduleAndField.length)-1];
					if(fields.contains(field)) {
						
					if(cellValue != null && !cellValue.equals("")) {
						FacilioField facilioField = null;
						try {
							ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
							facilioField = bean.getField(field, module);
						}catch (Exception e) {
							LOGGER.severe("FACILIO FIELD EXCEPTION" + e);
						}
						if(facilioField != null) {
						try {
							if(facilioField.getDataTypeEnum().equals(FieldType.DATE_TIME) || facilioField.getDataTypeEnum().equals(FieldType.DATE)) {
								if(!(cellValue instanceof Long)) {
									long millis = DateTimeUtil.getTime(cellValue.toString(), "dd-MM-yyyy HH:mm");
									if(!props.containsKey(field)) {
										props.put(field, millis);
									}
									isfilled = true;
								} 
							}
						} catch (Exception e) {
							LOGGER.severe("exception ---" + e.getMessage());
						}
						}
					}
					if(!isfilled) {
						if(!props.containsKey(field)) {
							props.put(field, cellValue);
						}
					}else {
						return;
					}
					}
				});
					LOGGER.severe("props ---" + props);
					ReadingContext readingContext = FieldUtil.getAsBeanFromMap(props, ReadingContext.class);
					readingContexts.add(readingContext);
					groupedContext.put(module, readingContext);
				}
			}
		}
		context.put(ImportAPI.ImportProcessConstants.READINGS_LIST, readingContexts);
		context.put(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT, groupedContext);
		context.put(ImportAPI.ImportProcessConstants.GROUPED_FIELDS, groupedFields);
		
		return false;
	}
	
	public static Long getAssetByUniqueness(HashMap<String,Object> colVal, String module, HashMap<String,String> uniqueMapping) throws Exception{
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> assetFields = new ArrayList<>();
		
		FacilioModule assetModule = bean.getModule(module);
		SelectRecordsBuilder selectRecordBuilder = new SelectRecordsBuilder<>();
		List<FacilioField> facilioFields = bean.getAllFields(module);
		Class BeanClassName = FacilioConstants.ContextNames.getClassFromModuleName(module);
		selectRecordBuilder.table(assetModule.getTableName()).select(facilioFields).beanClass(BeanClassName).module(assetModule);
		
		for(String field : uniqueMapping.keySet()) {
			FacilioField Field = bean.getField(field, module);
			assetFields.add(Field);
			String columnName = Field.getColumnName();
			selectRecordBuilder.andCustomWhere(columnName+"= ?", (String) colVal.get(uniqueMapping.get(field)));
		}
		
		List<? extends ModuleBaseWithCustomFields> props = selectRecordBuilder.get();
		Long Id = (Long) props.get(0).getId();
		return Id;
		
		
	}
	public void fieldMapParsing(HashMap<String, String> fieldMapping) throws Exception {

		groupedFields = ArrayListMultimap.create();
		HashMap<String, String> updatedFieldMapping = new HashMap<>();
		List<String> fieldMappingKeys = new ArrayList<>(fieldMapping.keySet());
		for (int i = 0; i < fieldMappingKeys.size(); i++) {
			String[] modulePlusFieldName = fieldMappingKeys.get(i).split("__");
			ArrayList<String> moduleNameList = new ArrayList(Arrays.asList(modulePlusFieldName)); 
			
			String fieldName = moduleNameList.get((moduleNameList.size())-1);
			moduleNameList.remove((moduleNameList.size()-1));
			String moduleName;
			if(moduleNameList.size() ==1) {
				moduleName = moduleNameList.get(0);
			}
			else {
			moduleName = String.join("_", moduleNameList);
			}
			groupedFields.put(moduleName, fieldName);
		}
		if(groupedFields.containsKey("sys")) {
			List<String> sys = new ArrayList(groupedFields.get("sys"));
			groupedFields.removeAll("sys");
			for(String module: groupedFields.keySet()) {
				for(int i=0;i< sys.size(); i++) {
					groupedFields.put(module, sys.get(i));
				}
			}
			
		}
	}
}
