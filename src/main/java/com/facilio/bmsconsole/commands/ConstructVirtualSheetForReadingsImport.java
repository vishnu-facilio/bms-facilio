package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportTemplateAction;
import com.facilio.bmsconsole.actions.ImportTemplateContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;


public class ConstructVirtualSheetForReadingsImport implements Command{

	
	private static final Logger LOGGER = Logger.getLogger(ConstructVirtualSheetForReadingsImport.class.getName());
	private HashMap<String, List<String>> groupedFields = new HashMap<String, List<String>>();

	@Override
	public boolean execute(Context context) throws Exception{
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
	
	LOGGER.severe("Virtual sheet construction complete");
	return false;
	
	}
	public void fieldMapParsing(HashMap<String, String> fieldMapping) throws Exception {
		LOGGER.severe("fieldMapping -- " + fieldMapping);
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
		LOGGER.severe("groupedFields -- " + groupedFields);

	}
}
