package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportTemplateAction;
import com.facilio.bmsconsole.actions.ImportTemplateContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportTimeColumnParseException;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.json.simple.JSONObject;

import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

public class ParseDataForReadingLogsCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(ParseDataForReadingLogsCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		LOGGER.severe("---DataParseForLogCommand start-----");
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		Long templateID = importProcessContext.getTemplateId();
		LOGGER.severe("templateID -- "+templateID);
		ImportTemplateAction importTemplateAction = new ImportTemplateAction();
		ImportTemplateContext importTemplateContext = importTemplateAction.fetchTemplate(templateID);
		
		
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		InputStream is = fs.readFile(importProcessContext.getFileId());
		HashMap<String,String> fieldMapping = importTemplateContext.getFieldMapping();
		HashMap<String,String> uniqueMapping = importTemplateContext.getUniqueMapping();

		HashMap<String, List<ImportRowContext>> groupedContext = new HashMap<String, List<ImportRowContext>>();
		
		JSONObject templateMeta = importTemplateContext.getTemplateMetaJSON();
		ArrayList<String> ModulesPlusFields = new ArrayList(fieldMapping.keySet());
		JSONObject dateFormats = (JSONObject) templateMeta.get(ImportAPI.ImportProcessConstants.DATE_FORMATS);
		
		HashMap<Integer, String> headerIndex = new HashMap<Integer, String>();
		Workbook workbook = WorkbookFactory.create(is);
		
		int row_no = 0;
		
		for(int i=0;i<workbook.getNumberOfSheets();i++) {
			Sheet datatypeSheet = workbook.getSheetAt(i);
			Iterator<Row> rowItr = datatypeSheet.iterator();
			boolean heading=true;
			while (rowItr.hasNext()) {
				row_no++;
				ImportRowContext rowContext = new ImportRowContext();
				rowContext.setSheetNumber(i);
				rowContext.setRowNumber(row_no);
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
						if(cellValue.equals("") || cellValue.equals(null)) {
							continue;
						}
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
					
					try{
						if(cell.getCellType() == Cell.CELL_TYPE_BLANK) {
							val = null;
						}
						else if (cell.getCellTypeEnum() == CellType.STRING) {
							if( cell.getStringCellValue().trim().length() == 0) {
								val = null;
							}
							else {
								val = cell.getStringCellValue().trim();
							}
							
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
						
					}catch(Exception e) {
						throw new ImportParseException(rowContext.getRowNumber(), cellName, e);
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
				
				// check for null uniqueMappingField
				if(uniqueMapping != null) {
					for(String key: uniqueMapping.keySet()) {
						String columnName = uniqueMapping.get(key);
						if(colVal.get(columnName) == null) {
							rowContext.setError_code(ImportProcessContext.ImportRowErrorCode.NULL_UNIQUE_FIELDS.getValue());
							break;
						}
						else {
							continue;
						}
					}
				}
				
//				if(rowContext.getError_code() != null && rowContext.getError_code() == ImportProcessContext.ImportRowErrorCode.NULL_UNIQUE_FIELDS.getValue()) {
//					continue;
//				}
				
				
				
				rowContext.setColVal(colVal);
				LOGGER.severe("row -- "+row_no+" colVal --- "+colVal);
				
				JSONObject meta = importTemplateContext.getModuleJSON();
				Long parentId =null;
				if(!meta.isEmpty() && uniqueMapping == null) {
					parentId = (Long) meta.get(ImportAPI.ImportProcessConstants.PARENT_ID_FIELD);
				}
				
				if(!importTemplateContext.getUniqueMappingJSON().isEmpty())
				{
					if(rowContext.getError_code() == null)
					{
						parentId = getAssetByUniqueness(colVal, importTemplateContext.getModuleMapping().get("subModule"), uniqueMapping);
					}
				}
				
				if(parentId == null) {
					rowContext.setError_code(ImportProcessContext.ImportRowErrorCode.NULL_RESOURCES.getValue());
					//nullResources.put(row_no, colVal);
//					continue;
				}
				else {
					rowContext.setParentId(parentId);
					rowContext.setError_code(ImportProcessContext.ImportRowErrorCode.NO_ERRORS.getValue());
				}
				
				long millis;
				try {
					if(dateFormats.get(fieldMapping.get("sys__ttime")).equals(ImportAPI.ImportProcessConstants.TIME_STAMP_STRING)) {
						String ttime = colVal.get(fieldMapping.get("sys__ttime")).toString();
						millis = Long.parseLong(ttime);
					}
					else {
						String ttime = colVal.get(fieldMapping.get("sys__ttime")).toString();
						Instant dateInstant = DateTimeUtil.getTimeInstant(dateFormats.get(fieldMapping.get("sys__ttime")).toString(),ttime);
						millis = dateInstant.toEpochMilli();
				}
				}catch(Exception e) {
					throw new ImportTimeColumnParseException(rowContext.getRowNumber(), fieldMapping.get("sys__ttime"), e);
				}
				
				rowContext.setTtime(millis);
				StringBuilder uniqueString = new StringBuilder();
				if(parentId == null || parentId < 0) {
					uniqueString.append("-1");
				}
				else {
					uniqueString.append(parentId.toString());
				}
				uniqueString.append("__");
				uniqueString.append(String.valueOf(rowContext.getTtime()));
				if(!groupedContext.containsKey(uniqueString.toString())) {
					List<ImportRowContext> rowContexts = new ArrayList<ImportRowContext>();
					rowContexts.add(rowContext);
					groupedContext.put(uniqueString.toString(), rowContexts);
				}
				else
				{
					groupedContext.get(uniqueString.toString()).add(rowContext);
				}
			}
		}
		LOGGER.severe(groupedContext.toString());
		context.put(ImportAPI.ImportProcessConstants.GROUPED_ROW_CONTEXT, groupedContext);
		context.put(ImportAPI.ImportProcessConstants.ROW_COUNT, row_no);

		LOGGER.severe("---DataParseForLogCommand End-----");
		return false;
	}
	
//	private static boolean hasDuplicates(HashMap<String, List<ImportRowContext>> groupedContext) {
//		for(String uniqueString : groupedContext.keySet()) {
//			String[] split = uniqueString.split("__");
//			if(split[0] != "-1")
//			if(groupedContext.get(uniqueString).size() > 1) {
//				return true;
//			}
//		}
//		return false;
//	}
	public static Long getAssetByUniqueness(HashMap<String,Object> colVal, String module, HashMap<String,String> uniqueMapping) throws Exception {
		LOGGER.severe("getAssetByUniqueness");
//		LOGGER.severe("colVal" + colVal.toString());
//		LOGGER.severe("module" + module);
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
		// LOGGER.severe("selectRecord" + selectRecordBuilder.toString());
		if(!props.isEmpty()) {
			Long Id = props.get(0).getId();
			LOGGER.severe("id -- " + Id);
			return Id;
		}
		else {
			return null;
		}
		
	}

}
