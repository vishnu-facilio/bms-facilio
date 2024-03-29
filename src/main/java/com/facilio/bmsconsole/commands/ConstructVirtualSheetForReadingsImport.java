package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportTemplateAction;
import com.facilio.bmsconsole.actions.ImportTemplateContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportTimeColumnParseException;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;


public class ConstructVirtualSheetForReadingsImport extends FacilioCommand {

	
	private static final Logger LOGGER = Logger.getLogger(ConstructVirtualSheetForReadingsImport.class.getName());
	private HashMap<String, List<String>> groupedFields = new HashMap<String, List<String>>();

	@Override
	public boolean executeCommand(Context context) throws Exception{
	ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
	Long templateID = importProcessContext.getTemplateId();
	
	LOGGER.severe("templateID -- "+templateID);
	ImportTemplateAction importTemplateAction = new ImportTemplateAction();
	ImportTemplateContext importTemplateContext = importTemplateAction.fetchTemplate(templateID);

	int nullFields = 0;

	HashMap<String, String> fieldMapping = importTemplateContext.getFieldMapping();
	HashMap<String, String> uniqueMapping = importTemplateContext.getUniqueMapping();
	HashMap<String, HashMap<String, ReadingContext>> groupedContext = new HashMap<String, HashMap<String,ReadingContext>>();

	List<Map<String,Object>> allRows = ImportAPI.getValidatedRows(importProcessContext.getId());
	fieldMapParsing(fieldMapping);
	List<String> moduleNames = new ArrayList<>(groupedFields.keySet());
	JSONObject templateMeta = importTemplateContext.getTemplateMetaJSON();
	JSONObject dateFormats = (JSONObject) templateMeta.get(ImportAPI.ImportProcessConstants.DATE_FORMATS);
	JSONObject unitFormats = (JSONObject) templateMeta.get(ImportAPI.ImportProcessConstants.UNIT_FORMATS);

	ModuleBean bean = (ModuleBean)BeanFactory.lookup("ModuleBean");

	LOGGER.severe("Starting virtual sheet reconstruction");

	for(Map<String, Object> row: allRows) {
		ImportProcessLogContext rowLogContext = FieldUtil.getAsBeanFromMap(row, ImportProcessLogContext.class);
		ImportRowContext rowContext = new ImportRowContext();

		if(rowLogContext.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getValue()) {
				rowContext = rowLogContext.getRowContexts().get(0);
		}

		else if(rowLogContext.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.OTHER_ERRORS.getValue()) {
			nullFields = nullFields + rowLogContext.getRowContexts().size();
			continue;
		}

		else {
			rowContext = rowLogContext.getCorrectedRow();
			}
		// LOGGER.severe("---RowContext----" + rowContext.toString());


	HashMap<String,Object> colVal = rowContext.getColVal();
	for(String module : moduleNames) {
		List<String> fields  = new ArrayList(groupedFields.get(module));
		HashMap<String,Object> props = new HashMap<>();
		props.put(ImportAPI.ImportProcessConstants.PARENT_ID_FIELD, rowContext.getParentId());
		props.put(FacilioConstants.ContextNames.SOURCE_TYPE, SourceType.IMPORT.getIndex());
		props.put(FacilioConstants.ContextNames.SOURCE_ID, importProcessContext.getId());
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
								
								if (unitFormats!=null && unitFormats.containsKey(key) && unitFormats.get(key) != null) {
										ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(rowContext.getParentId(), facilioField);
										Double convertedInputReading;										
										String reading_unitTypeID = unitFormats.get(key).toString();
										if(rdm != null && rdm.getUnitEnum() != null) {
											Unit rdmUnit = rdm.getUnitEnum();
											convertedInputReading = UnitsUtil.convert(cellValueString, Unit.valueOf(Integer.parseInt(reading_unitTypeID)), rdmUnit);
											props.put(field, convertedInputReading);
										} else {
											convertedInputReading = UnitsUtil.convertToSiUnit(cellValueString, Unit.valueOf(Integer.parseInt(reading_unitTypeID)));
											props.put(field, convertedInputReading);
										}
								}else {
									Double cellDoubleValue = Double.parseDouble(cellValueString);
									props.put(field, cellDoubleValue);
								}								
								isfilled = true;
								} else if (facilioField.getDataType() == FieldType.ENUM.getTypeAsInt()) {
								EnumField enumField = (EnumField) facilioField;
								String cellValueString = cellValue.toString();
								if (StringUtils.isNotEmpty(cellValueString)) {
									int enumIndex = enumField.getIndex(cellValueString.trim());
									props.put(field, enumIndex);
								}
								isfilled = true;
							}
						} catch (Exception e) {
								LOGGER.severe("Exception Row Number --" + rowContext.getRowNumber() + "Field Mapping ---" + fieldMapping.get(key));
								LOGGER.severe("exception ---" + e);
								throw new ImportParseException(rowContext.getRowNumber(), fieldMapping.get(key).toString(), e);
								}
						}
					}
				if(!isfilled) {
					props.put(field, cellValue);
					}
				}
			
			// LOGGER.severe("props ---" + props);
			ReadingContext readingContext = FieldUtil.getAsBeanFromMap(props,ReadingContext.class);
			StringBuilder uniqueId = new StringBuilder();
			uniqueId.append(rowContext.getParentId()).append(rowContext.getTtime());
			
			if(!groupedContext.containsKey(module)) {
				HashMap<String, ReadingContext> rows = new HashMap<String, ReadingContext>();
				rows.put(uniqueId.toString(),readingContext);
				groupedContext.put(module, rows);
				}
			else {
				groupedContext.get(module).put(uniqueId.toString(), readingContext);
				}
			
			}
	}
	context.put(ImportAPI.ImportProcessConstants.GROUPED_ROW_CONTEXT, groupedContext);
	context.put(ImportAPI.ImportProcessConstants.GROUPED_FIELDS, groupedFields);
	context.put(ImportAPI.ImportProcessConstants.IMPORT_TEMPLATE_CONTEXT, importTemplateContext);
	context.put(ImportAPI.ImportProcessConstants.NULL_COUNT, nullFields);
	
	LOGGER.info("Virtual sheet construction complete");
	return false;
	
	}
	public void fieldMapParsing(HashMap<String, String> fieldMapping) throws Exception {
		LOGGER.info("fieldMapping -- " + fieldMapping);
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
			if(groupedFields.isEmpty()) {
				List<String> fields = new ArrayList<String>();
				fields.add(fieldName);
				groupedFields.put(moduleName, fields);
				}
			else if(groupedFields.containsKey(moduleName)) {
				groupedFields.get(moduleName).add(fieldName);
				}
			else {
				List<String> fields = new ArrayList<String>();
				fields.add(fieldName);
				groupedFields.put(moduleName, fields);
				}
			}
		if(groupedFields.containsKey("sys")) {
			List<String> sys = new ArrayList(groupedFields.get("sys"));
			groupedFields.remove("sys");
			for(String module: groupedFields.keySet()) {
				for(int i=0;i< sys.size(); i++) {
					groupedFields.get(module).add(sys.get(i));
					}
				}
			}
		LOGGER.info("groupedFields -- " + groupedFields);

	}
}
