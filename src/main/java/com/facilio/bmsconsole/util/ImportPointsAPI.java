/**
 * 
 */
package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.PointsProcessContext;
import com.facilio.bmsconsole.commands.data.ProcessXLS;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import io.jsonwebtoken.lang.Collections;

/**
 * @author facilio
 *
 */
public class ImportPointsAPI {

	private static org.apache.log4j.Logger log = LogManager.getLogger(ImportAPI.class.getName());
	private static Logger LOGGER  = Logger.getLogger(ImportAPI.class.getName());

	public static JSONArray getColumnHeadings(Workbook workbook) throws Exception
	{
		HashMap<String,String> headingsInFirstSheet = new HashMap<String, String>();
		JSONArray columnheadings = new JSONArray();
		ArrayList<String> missingInSheet;
		HashMap<Integer, ArrayList<String>> missingColumns = new HashMap<Integer, ArrayList<String>>();
		//        Workbook workbook = WorkbookFactory.create(excelfile);
		if(workbook.getNumberOfSheets() > 1) {
			for(int i =0; i< workbook.getNumberOfSheets();i++) {
				Sheet dataSheet =workbook.getSheetAt(i);
				Row row = dataSheet.getRow(0);
				Iterator ctr = row.cellIterator();
				missingInSheet = new ArrayList<String>();
				while(ctr.hasNext()) {
					Cell cell = (Cell)ctr.next();
					if(cell.getCellType() == CellType.BLANK) {
						if(i == 0) {
							columnheadings.add(null);
						}
					}
					else {
						if(i!=0) {
							if(columnheadings.contains(cell.getStringCellValue())) {
								continue;
							}
							else {
								missingInSheet.add(cell.getStringCellValue());
							}
						}
						else {
							String cellValue = cell.getStringCellValue();
						}
					}
				}
				missingColumns.put(i, missingInSheet);
			}

			columnheadings.add(missingColumns);
		}
		else {
			Sheet datatypeSheet = workbook.getSheetAt(0);

			Iterator<Row> itr = datatypeSheet.iterator();
			while (itr.hasNext()) {
				Row row = itr.next();
				Iterator<Cell> cellItr = row.cellIterator();
				while (cellItr.hasNext()) {
					Cell cell = cellItr.next();
					if(cell.getCellType() == CellType.BLANK) {
						columnheadings.add(null);
					}
					else {
						String cellValue = cell.getStringCellValue();
						columnheadings.add(cellValue);
					}
				}
				break;
			}
		}
		//        workbook.close();
		return columnheadings;
	}

	public static PointsProcessContext getColumnHeadings(Workbook workbook, PointsProcessContext pointsProcessContext) throws Exception{
		JSONArray columnHeadings = getColumnHeadings(workbook);
		if(columnHeadings.size() == 1) {
			if(columnHeadings.get(0) instanceof java.util.HashMap<?,?>) {
				HashMap<Integer,ArrayList<String>> missingColumns = (HashMap<Integer, ArrayList<String>>) columnHeadings.get(0);
				if(!missingColumns.isEmpty()) {
					String warningMessage = constructWarning(missingColumns);
					if(pointsProcessContext.getImportJobMetaJson().isEmpty()) {
						JSONObject importMeta = new JSONObject();
						importMeta.put(ImportPointsConstants.IMPORT_WARNING, warningMessage);
					}
					else {
						JSONObject importMeta = pointsProcessContext.getImportJobMetaJson();
						importMeta.put(ImportPointsConstants.IMPORT_WARNING, warningMessage);
					}
				}
				pointsProcessContext.setColumnHeadingString(null);
			}
			else {
				pointsProcessContext.setColumnHeadingString(columnHeadings.toJSONString().replaceAll("\"", "\\\""));
			}	
		}
		else {
			pointsProcessContext.setColumnHeadingString(columnHeadings.toJSONString().replaceAll("\"", "\\\""));
		}
		return pointsProcessContext;

	}



	public static String constructWarning(HashMap<Integer, ArrayList<String>> missingColumns) {
		StringBuilder newWarning= new StringBuilder();
		newWarning.append("Error! Missing columns");
		newWarning.append(System.getProperty("line.separator"));
		ArrayList<Integer> sheetIndexes = new ArrayList(missingColumns.values());
		for(Integer sheet: sheetIndexes) {
			ArrayList<String> columns = missingColumns.get(sheet);
			for(String column: columns) {
				newWarning.append(column + ' ');
			}
			newWarning.append("at sheet " + sheet);
			newWarning.append(System.getProperty("line.separator"));
		}
		LOGGER.severe("Import Warning:" + newWarning.toString());
		return newWarning.toString();

	}
	public static void addImportProcess(PointsProcessContext importProcessContext) throws Exception {

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getImportProcessModule().getTableName())
				.fields(FieldFactory.getImportProcessFields());

		importProcessContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		Map<String, Object> props = FieldUtil.getAsProperties(importProcessContext);

		insertBuilder.addRecord(props);
		insertBuilder.save();

		importProcessContext.setId((Long) props.get("id"));
	}

	public static void updateImportProcess(PointsProcessContext importProcessContext) throws Exception {

		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
		update.table(ModuleFactory.getImportPointsModule().getTableName());
		update.fields(FieldFactory.getImportPointsFields());
		update.andCustomWhere(ModuleFactory.getImportPointsModule().getTableName()+".ID = ?", importProcessContext.getId());

		Map<String, Object> props = FieldUtil.getAsProperties(importProcessContext);

		update.update(props);	

	}

	//	public static void updateImportProcess(PointsProcessContext importProcessContext,ImportStatus importStatus) throws Exception {
	//		
	//		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
	//		update.table(ModuleFactory.getImportProcessModule().getTableName());
	//		update.fields(FieldFactory.	());
	//		update.andCustomWhere(ModuleFactory.getImportProcessModule().getTableName()+".ID = ?", importProcessContext.getId());
	//		
	//		Map<String, Object> props = new HashMap<>();
	//		
	//		props.put("status", importStatus.getValue());
	//		
	//		update.update(props);	
	//		
	//	}


	public static List<Map<String,Object>> getValidatedRows(Long importProcessId) throws Exception{
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getImportProcessLogModule().getTableName())
				.select(FieldFactory.getImportProcessLogFields())
				.andCustomWhere("IMPORTID = ?", importProcessId);
		List<Map<String,Object>> result = selectRecordBuilder.get();
		return result;
	}

	public static HashMap<String, String> getFieldMapping() throws Exception{
		HashMap<String, String> fieldMapping = new HashMap<String, String>();
		for(FacilioField field : getImportPointsFieldsAsList()) {
			fieldMapping.put(field.getName(), "-1");
		}
		return fieldMapping;
	}

	public static List<FacilioField> getImportPointsFieldsAsList() throws Exception{
		List<FacilioField> fields = new ArrayList<FacilioField>();
		fields = FieldFactory.getPointsFields();
		List<FacilioField> selectedFields = new ArrayList<FacilioField>();
		selectedFields = fields.stream().filter(f -> f.getName().equals("device") || f.getName().equals("instance") || f.getName().equals("categoryId") || f.getName().equals("resourceId") || f.getName().equals("fieldId") || f.getName().equals("unit")).collect(Collectors.toList());

		LOGGER.severe(selectedFields.toString());
		return selectedFields;
	}
	public static void getFieldMapping(PointsProcessContext importProcessContext) throws Exception {

		if(importProcessContext.getColumnHeadingString() != null) {

			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getImportPointsFields())
					.table(ModuleFactory.getImportPointsModule().getTableName());

			selectBuilder.andCustomWhere(ModuleFactory.getImportPointsModule().getTableName()+".COLUMN_HEADING = ?", importProcessContext.getColumnHeadingString());

			selectBuilder.andCustomWhere(ModuleFactory.getImportPointsModule().getTableName()+".ORGID = ?", AccountUtil.getCurrentOrg().getId());

			selectBuilder.andCustomWhere(ModuleFactory.getImportPointsModule().getTableName()+".FIELD_MAPPING is not null");

			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {

				String fieldMapping = (String) props.get(0).get("fieldMappingString");

				if(fieldMapping != null && !fieldMapping.trim().isEmpty()) {
					importProcessContext.setFieldMappingString(fieldMapping);
					return;
				}
			}
		}
		importProcessContext.populatePointsFieldMapping();
	}




	public static void importPasteParsedData(List<Map<Integer,String>> parsedDatas, Map<Integer,String> columnMaping,PointsProcessContext importProcessContext) throws Exception {

		List<ReadingContext> readingsList = new ArrayList<ReadingContext>();

		for(Map<Integer, String> parsedData:parsedDatas) {

			HashMap <String, Object> props = new LinkedHashMap<String,Object>();
			columnMaping.forEach((key,value) ->
			{
				Object cellValue = parsedData.get(key);
				boolean isfilledByLookup = false;

				if(cellValue != null && !cellValue.toString().equals("")) {

					Map<String, FacilioField> fieldMapping = null;
					try {
						fieldMapping = importProcessContext.getFacilioFieldMapping();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						log.info("Exception occurred ", e);
					}
					if (!Collections.isEmpty(fieldMapping)) {
						FacilioField facilioField = fieldMapping.get(value);

						if(facilioField != null && facilioField.getDataTypeEnum().equals(FieldType.LOOKUP)) {
							LookupField lookupField = (LookupField) facilioField;
							List<Map<String, Object>> lookupPropsList = ProcessXLS.getLookupProps(lookupField,cellValue);
							if(lookupPropsList != null) {
								Map<String, Object> lookupProps = lookupPropsList.get(0);
								props.put(value, lookupProps);
								isfilledByLookup = true;
							}
						}
					}
				}
				if(!isfilledByLookup) {
					props.put(value, cellValue);
				}
			});


			ReadingContext reading = FieldUtil.getAsBeanFromMap(props, ReadingContext.class);
			reading.setParentId(importProcessContext.getAssetId());
			readingsList.add(reading);
		}
		ProcessXLS.populateData(importProcessContext, readingsList);
	}
	public static List<Map<String,Object>> getFirstRow (Workbook workbook)throws Exception {
		List<Map<String, Object>> listVal = new ArrayList<>();
		JSONArray columnHeadings = getColumnHeadings(workbook);
		//		Workbook workbook = WorkbookFactory.create(excelfile);
		Sheet datatypeSheet = workbook.getSheetAt(0);
		Row row = datatypeSheet.getRow(1);
		int lastCellNum = row.getLastCellNum();

		for(int rowCount=1 ;rowCount<5000;rowCount++) {
				row = datatypeSheet.getRow(rowCount);
				if(row == null) {
					continue;
				}
				Map<String,Object> list = new HashMap<String, Object>();
				for(int i =0; i< lastCellNum; i++) {
				Cell cell = row.getCell(i);
				if(columnHeadings.get(i) == null || columnHeadings.get(i) == "null") {
					continue;
				}
				else {
					if(cell == null || (cell.getCellType() == CellType.BLANK)) {
						continue;
					}
					else {
						CellType type = cell.getCellType();
						
						 if(type == CellType.STRING)
						{
							String cellValue = cell.getStringCellValue();
							list.put(String.valueOf(columnHeadings.get(i)),cellValue);
						}else {
	        				Double cellValue = cell.getNumericCellValue();
	        				list.put(String.valueOf(columnHeadings.get(i)),cellValue);
	        			}
					}
				}
			}
				if(!list.isEmpty()) {
					listVal.add(list);
				}
				
		}
		//		workbook.close();
		return listVal;
	}


	public static PointsProcessContext getImportProcessContext(Long id) throws Exception {

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getImportPointsFields())
				.table(ModuleFactory.getImportPointsModule().getTableName())
				.andCustomWhere(ModuleFactory.getImportPointsModule().getTableName()+".ID = ?", id);

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			PointsProcessContext importProcessContext = FieldUtil.getAsBeanFromMap(props.get(0), PointsProcessContext.class);
			return importProcessContext;
		}
		return null;
	}

	public static void addImportPoints(PointsProcessContext importProcessContext) throws Exception {

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getImportPointsModule().getTableName())
				.fields(FieldFactory.getImportPointsFields());

		importProcessContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		Map<String, Object> props = FieldUtil.getAsProperties(importProcessContext);

		insertBuilder.addRecord(props);
		insertBuilder.save();

		importProcessContext.setId((Long) props.get("id"));
	}

	public static boolean isRemovableFieldImport(String name) {

		switch (name){ 

		case "parentId":
		case "date":
		case "month":
		case "week":
		case "day":
		case "hour":{
			return true;
		}
		}
		return false;
	}

	public static void updateImportPoints(PointsProcessContext importProcessContext) throws Exception {

		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
		update.table(ModuleFactory.getImportPointsModule().getTableName());
		update.fields(FieldFactory.getImportPointsFields());
		update.andCustomWhere(ModuleFactory.getImportPointsModule().getTableName()+".ID = ?", importProcessContext.getId());

		Map<String, Object> props = FieldUtil.getAsProperties(importProcessContext);

		update.update(props);	

	}

	public static HashMap<String,String> getFieldMap(String jsonString) throws Exception
	{
		HashMap <String,String> fieldMap = new LinkedHashMap<String,String>();
		JSONParser parser = new JSONParser();
		JSONObject json=(JSONObject)parser.parse(jsonString);
		Iterator keys = json.keySet().iterator();
		while(keys.hasNext())
		{
			String key =(String) keys.next();
			String values = (String) json.get(key);
			fieldMap.put(key, values);
		}
		return fieldMap;
	}


	public static class  ImportPointsConstants{
		public static final String POINTS_PROCESS_CONTEXT = "pointsProcessContext";
		public static final String READINGS_LIST = "readingsList";
		public static final String FIELDS_MAPPING = "fieldMapping";
		public static final String NO_CATEGORY_DEFINED = "&&none$$";
		public static final String NAME_FIELD = "name";
		public static final String ID_FIELD = "id";
		public static final String CATEGORY_FROM_CONTEXT = "category";
		public static final String CATEGORY_BASED_ASSETS = "categoryBasedAsset";
		public static final String T_TIME ="ttime";
		public static final String ASSET_ID_FIELD = "asset";
		public static final String UPDATE_FIELDS = "updateBy";
		public static final String INSERT_FIELDS = "insertBy";
		public static final String ASSET_CATEGORY = "assetCategory";
		public static final String UNIQUE_MAPPING = "uniqueMapping";
		public static final String NULL_UNIQUE_FIELDS = "nullUniqueFields";
		public static final String NULL_RESOURCES = "nullResources";
		public static final String IMPORT_WARNING= "warningMessage";
		public static final String IMPORT_ROW_CONTEXT = "importRowContext";
		public static final String HAS_DUPLICATE_ENTRIES ="hasDuplicateEntries";
		public static final String ROW_COUNT = "rowCount";
		public static final String NULL_COUNT = "nullCount";
		public static final String PARSING_ERROR="parsingError";
		public static final String PARSING_ERROR_MESSAGE="parsingErrorMessage";
		public static final String GROUPED_ROW_CONTEXT="groupRowContext";
	}





}
