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
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
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
	
	private ArrayListMultimap<String, String> groupedFields;
	
	@Override
	public boolean execute(Context context) throws Exception,ImportParseException {
		
		ArrayListMultimap<String, ReadingContext> groupedContext = ArrayListMultimap.create();
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		Long templateID = importProcessContext.getTemplateId();
		LOGGER.severe("templateID -- "+templateID);
		ImportTemplateAction importTemplateAction = new ImportTemplateAction();
		ImportTemplateContext importTemplateContext = importTemplateAction.fetchTemplate(templateID);
		LOGGER.severe("importTemplateContext getFieldMappingString-- "+importTemplateContext.getFieldMappingString());
		
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		InputStream is = fs.readFile(importProcessContext.getFileId());
		HashMap<String,String> fieldMapping = importTemplateContext.getFieldMapping();
		HashMap<String,String> uniqueMapping = importTemplateContext.getUniqueMapping();
		List<ReadingContext> readingContexts = new ArrayList<>();
		HashMap<Integer, HashMap<String,Object>> nullUniqueFields = new HashMap<>();
		HashMap<Integer, HashMap<String, Object>> nullResources = new HashMap<>();
		HashMap<Integer, HashMap<String, Object>> duplicateEntries = new HashMap<>();
		JSONObject templateMeta = importTemplateContext.getTemplateMetaJSON();
		ArrayList<String> ModulesPlusFields = new ArrayList(fieldMapping.keySet());
		JSONObject dateFormats = (JSONObject) templateMeta.get(ImportAPI.ImportProcessConstants.DATE_FORMATS);
		fieldMapParsing(fieldMapping);
		
		List<String> moduleNames = new ArrayList<>(groupedFields.keySet());
		HashMap<Integer, String> headerIndex = new HashMap<Integer, String>();
		Workbook workbook = WorkbookFactory.create(is);
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
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
							val = date1.getEpochSecond()*1000;
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
				
				// check for null uniqueMappingField
				if(uniqueMapping != null) {
					for(String key: uniqueMapping.keySet()) {
						String columnName = uniqueMapping.get(key);
						if(colVal.get(columnName) == null) {
							nullUniqueFields.put(row_no, colVal);
							break;
						}
						else {
							continue;
						}
					}
					LOGGER.severe("Unique fields null:" + nullUniqueFields.toString());
				}
				if(!(nullUniqueFields.isEmpty())) {
					if((nullUniqueFields.get(row_no)!= null)){
						continue;
					}
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
				
				JSONObject meta = importTemplateContext.getModuleJSON();
				Long parentId =null;
				if(!meta.isEmpty()) {
					parentId = (Long) meta.get(ImportAPI.ImportProcessConstants.PARENT_ID_FIELD);
				}
				if(!importTemplateContext.getUniqueMappingJSON().isEmpty()) {
					parentId = getAssetByUniqueness(colVal, importTemplateContext.getModuleMapping().get("subModule"), uniqueMapping);
				}
				if(parentId == null) {
					nullResources.put(row_no, colVal);
					continue;
				}
				
				for(String module : moduleNames) {
					
					List<String> fields  = new ArrayList(groupedFields.get(module));
					
					HashMap<String,Object> props = new HashMap<>();
					
					props.put(ImportAPI.ImportProcessConstants.PARENT_ID_FIELD, parentId);
	
					for(String field :fields){
						
						String key = null;
						if(field.equals("ttime")) {
							key = "sys"+"__"+field;
						}
						else {
							key = module+"__"+field;
						}
						
						Boolean isfilled = false;
						Object cellValue = colVal.get(fieldMapping.get(key));
						if(cellValue != null && !cellValue.equals("")) {
							FacilioField facilioField = null;
							try {
								facilioField = bean.getField(field, module);
							}catch (Exception e) {
								LOGGER.severe("FACILIO FIELD EXCEPTION" + e);
							}
							if(facilioField != null) {
								try {
									if(facilioField.getDataTypeEnum().equals(FieldType.DATE_TIME) || facilioField.getDataTypeEnum().equals(FieldType.DATE)) {
										if(!(cellValue instanceof Long)) {
											long millis;
											if(dateFormats.get(fieldMapping.get(key)).equals(ImportAPI.ImportProcessConstants.TIME_STAMP_STRING)) {
												millis = Long.parseLong(cellValue.toString());
											}
											else {
												Instant dateInstant = DateTimeUtil.getTimeInstant(dateFormats.get(fieldMapping.get(key)).toString(),cellValue.toString());
												millis = dateInstant.toEpochMilli();
											}
											props.put(field, millis);
											isfilled = true;
										} 
									}
									else if(facilioField.getDataTypeEnum().equals(FieldType.NUMBER) || facilioField.getDataTypeEnum().equals(FieldType.DECIMAL)) {
										String cellValueString = cellValue.toString();
										if(cellValueString.contains(",")) {
											cellValueString = cellValueString.replaceAll(",", "");
										}
										Double cellDoubleValue = Double.parseDouble(cellValueString);
										props.put(field, cellDoubleValue);
										isfilled = true;
									}
								} catch (Exception e) {
									LOGGER.severe("exception ---" + e);
									throw new ImportParseException(row_no, fieldMapping.get(key).toString(), e);
								}
							}
						}
						if(!isfilled) {
							props.put(field, cellValue);
						}
					}
					LOGGER.severe("props ---" + props);
					
					ReadingContext NonDuplicateReadingContext = FieldUtil.getAsBeanFromMap(props, ReadingContext.class);
					readingContexts.add(NonDuplicateReadingContext );
					groupedContext.put(module, NonDuplicateReadingContext );
					System.out.println(groupedContext);
				}
			}
		}
		context.put(ImportAPI.ImportProcessConstants.READINGS_LIST, readingContexts);
		context.put(ImportAPI.ImportProcessConstants.IMPORT_TEMPLATE_CONTEXT, importTemplateContext);
		context.put(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT, groupedContext);
		context.put(ImportAPI.ImportProcessConstants.GROUPED_FIELDS, groupedFields);
		context.put(ImportAPI.ImportProcessConstants.NULL_UNIQUE_FIELDS, nullUniqueFields);
		context.put(ImportAPI.ImportProcessConstants.NULL_RESOURCES, nullResources);
		return false;
	}
		
	
	public static Long getAssetByUniqueness(HashMap<String,Object> colVal, String module, HashMap<String,String> uniqueMapping) throws Exception {
		LOGGER.severe("colVal" + colVal.toString());
		LOGGER.severe("module" + module);
		LOGGER.severe("uniqueMapping" + uniqueMapping.toString());
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> assetFields = new ArrayList<>();
		
		FacilioModule assetModule = bean.getModule(module);
		SelectRecordsBuilder selectRecordBuilder = new SelectRecordsBuilder<>();
		List<FacilioField> facilioFields = bean.getAllFields(module);
		Class BeanClassName = FacilioConstants.ContextNames.getClassFromModuleName(module);
		selectRecordBuilder.table(assetModule.getTableName()).select(facilioFields).beanClass(BeanClassName).module(assetModule);
		
		for(String field : uniqueMapping.keySet()) {
			List<FacilioField> moduleFields = bean.getAllFields(module);
			FacilioField Field = null;
			for(FacilioField facilioField: moduleFields) {
				if(facilioField.getName().equals(field)) {
					assetFields.add(facilioField);
					Field = facilioField;
				}
			}
			// FacilioField Field = bean.getField(field, module);
			String columnName = Field.getColumnName();
			selectRecordBuilder.andCustomWhere(columnName+"= ?",  colVal.get(uniqueMapping.get(field)).toString());
		}
		
		List<? extends ModuleBaseWithCustomFields> props = selectRecordBuilder.get();
		LOGGER.severe("selectRecord" + selectRecordBuilder.toString());
		if(!props.isEmpty()) {
			Long Id = props.get(0).getId();
			LOGGER.severe("id -- " + Id);
			return Id;
		}
		else {
			return null;
		}
		
	}
	public void fieldMapParsing(HashMap<String, String> fieldMapping) throws Exception {

		LOGGER.severe("fieldMapping -- " + fieldMapping);
		groupedFields = ArrayListMultimap.create();
		new HashMap<>();
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
		LOGGER.severe("groupedFields -- " + groupedFields);
	}
}
