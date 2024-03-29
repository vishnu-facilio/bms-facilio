package com.facilio.bmsconsole.commands;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportTemplateAction;
import com.facilio.bmsconsole.actions.ImportTemplateContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportTimeColumnParseException;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class ParseDataForReadingLogsCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(ParseDataForReadingLogsCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		LOGGER.info("---DataParseForLogCommand start-----");
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		Long templateID = importProcessContext.getTemplateId();
		LOGGER.info("templateID -- "+templateID);
		ImportTemplateAction importTemplateAction = new ImportTemplateAction();
		ImportTemplateContext importTemplateContext = importTemplateAction.fetchTemplate(templateID);
		
		
		FileStore fs = FacilioFactory.getFileStore();

		HashMap<String,String> fieldMapping = importTemplateContext.getFieldMapping();
		HashMap<String,String> uniqueMapping = importTemplateContext.getUniqueMapping();

		HashMap<String, List<ImportRowContext>> groupedContext = new HashMap<String, List<ImportRowContext>>();
		
		JSONObject templateMeta = importTemplateContext.getTemplateMetaJSON();
		JSONObject dateFormats = (JSONObject) templateMeta.get(ImportAPI.ImportProcessConstants.DATE_FORMATS);
		
		HashMap<Integer, String> headerIndex = new HashMap<Integer, String>();

		try (InputStream is = fs.readFile(importProcessContext.getFileId());
			 Workbook workbook = WorkbookFactory.create(is);) {
			int row_no = 0;

			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet datatypeSheet = workbook.getSheetAt(i);
				Iterator<Row> rowItr = datatypeSheet.iterator();
				boolean heading = true;
				while (rowItr.hasNext()) {
					row_no++;
					ImportRowContext rowContext = new ImportRowContext();
					rowContext.setSheetNumber(i);
					rowContext.setRowNumber(row_no);
					LOGGER.log(Level.FINE, "row_no -- " + row_no);
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
							if (cellValue.equals("") || cellValue.equals(null)) {
								continue;
							}
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
//						CellType type = cellValue.getCellTypeEnum();

						String cellName = headerIndex.get(cell.getColumnIndex());
						if (cellName == null) {
							continue;
						}

						Object val = 0.0;

						try {
							CellValue cellValue = evaluator.evaluate(cell);
							val = ImportAPI.getValueFromCell(cell, cellValue);
							
							colVal.put(cellName, val);

						} catch (Exception e) {
							throw new ImportParseException(rowContext.getRowNumber(), cellName, e);
						}
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

					// check for null uniqueMappingField
					if (uniqueMapping != null) {
						for (String key : uniqueMapping.keySet()) {
							String columnName = uniqueMapping.get(key);
							if (colVal.get(columnName) == null) {
								rowContext.setError_code(ImportProcessContext.ImportRowErrorCode.NULL_UNIQUE_FIELDS.getValue());
								break;
							} else {
								continue;
							}
						}
					}

//				if(rowContext.getError_code() != null && rowContext.getError_code() == ImportProcessContext.ImportRowErrorCode.NULL_UNIQUE_FIELDS.getValue()) {
//					continue;
//				}


					rowContext.setColVal(colVal);
					LOGGER.log(Level.FINE, "row -- " + row_no + " colVal --- " + colVal);

					JSONObject meta = importTemplateContext.getModuleJSON();
					Long parentId = null;
					if (!meta.isEmpty() && uniqueMapping == null) {
						parentId = (Long) meta.get(ImportAPI.ImportProcessConstants.PARENT_ID_FIELD);
					}

					if (!importTemplateContext.getUniqueMappingJSON().isEmpty()) {
						if (rowContext.getError_code() == null) {
							parentId = getAssetByUniqueness(colVal, importTemplateContext.getModuleMapping().get("subModule"), uniqueMapping);
						}
					}

					if (parentId == null) {
						rowContext.setError_code(ImportProcessContext.ImportRowErrorCode.NULL_RESOURCES.getValue());
						//nullResources.put(row_no, colVal);
//					continue;
					} else {
						rowContext.setParentId(parentId);
						rowContext.setError_code(ImportProcessContext.ImportRowErrorCode.NO_ERRORS.getValue());
					}

					long millis;
					try {
						if (dateFormats.get(fieldMapping.get("sys__ttime")).equals(ImportAPI.ImportProcessConstants.TIME_STAMP_STRING)) {
							String ttime = colVal.get(fieldMapping.get("sys__ttime")).toString();
							millis = Long.parseLong(ttime);
						} else {
							String ttime = colVal.get(fieldMapping.get("sys__ttime")).toString();
							Instant dateInstant = DateTimeUtil.getTimeInstant(dateFormats.get(fieldMapping.get("sys__ttime")).toString(), ttime);
							millis = dateInstant.toEpochMilli();
						}
					} catch (Exception e) {
						throw new ImportTimeColumnParseException(rowContext.getRowNumber(), fieldMapping.get("sys__ttime"), e);
					}

					rowContext.setTtime(millis);
					StringBuilder uniqueString = new StringBuilder();
					if (parentId == null || parentId < 0) {
						uniqueString.append("-1");
					} else {
						uniqueString.append(parentId.toString());
					}
					uniqueString.append("__");
					uniqueString.append(String.valueOf(rowContext.getTtime()));
					if (!groupedContext.containsKey(uniqueString.toString())) {
						List<ImportRowContext> rowContexts = new ArrayList<ImportRowContext>();
						rowContexts.add(rowContext);
						groupedContext.put(uniqueString.toString(), rowContexts);
					} else {
						groupedContext.get(uniqueString.toString()).add(rowContext);
					}
				}
			}
			context.put(ImportAPI.ImportProcessConstants.GROUPED_ROW_CONTEXT, groupedContext);
			context.put(ImportAPI.ImportProcessConstants.ROW_COUNT, row_no);
			LOGGER.info("---DataParseForLogCommand End-----");
			return false;
		}
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
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> assetFields = new ArrayList<>();
		
		FacilioModule assetModule = bean.getModule(module);
		SelectRecordsBuilder selectRecordBuilder = new SelectRecordsBuilder<>();
		List<FacilioField> facilioFields = bean.getAllFields(module);
		Class BeanClassName = FacilioConstants.ContextNames.getClassFromModule(assetModule);
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
