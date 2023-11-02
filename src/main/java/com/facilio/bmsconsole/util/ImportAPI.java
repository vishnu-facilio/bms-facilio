package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.*;
import com.facilio.bmsconsole.actions.ImportProcessContext.ImportStatus;
import com.facilio.bmsconsole.commands.ImportProcessLogContext;
import com.facilio.bmsconsole.commands.data.ProcessXLS;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.log4j.LogManager;
import org.apache.poi.ss.usermodel.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.logging.Logger;

import javax.validation.constraints.NotNull;

public class ImportAPI {

	private static org.apache.log4j.Logger log = LogManager.getLogger(ImportAPI.class.getName());
	private static Logger LOGGER  = Logger.getLogger(ImportAPI.class.getName());

	public static JSONArray getColumnHeadings(Workbook workbook) throws Exception
	{
		JSONArray columnheadings = new JSONArray();
		ArrayList<String> missingInSheet;
		HashMap<Integer, ArrayList<String>> missingColumns = new HashMap<Integer, ArrayList<String>>();
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
        return columnheadings;
	}
	
	public static ImportProcessContext getColumnHeadings(Workbook workbook, ImportProcessContext importProcessContext) throws Exception{
		JSONArray columnHeadings = getColumnHeadings(workbook);
		if(columnHeadings.size() == 1) {
			if(columnHeadings.get(0) instanceof java.util.HashMap<?,?>) {
				HashMap<Integer,ArrayList<String>> missingColumns = (HashMap<Integer, ArrayList<String>>) columnHeadings.get(0);
				if(!missingColumns.isEmpty()) {
		    		String warningMessage = constructWarning(missingColumns);
		    		if(importProcessContext.getImportJobMetaJson().isEmpty()) {
		    			JSONObject importMeta = new JSONObject();
		    			importMeta.put(ImportProcessConstants.IMPORT_WARNING, warningMessage);
		    		}
		    		else {
		    			JSONObject importMeta = importProcessContext.getImportJobMetaJson();
		    			importMeta.put(ImportProcessConstants.IMPORT_WARNING, warningMessage);
		    		}
		    	}
				importProcessContext.setColumnHeadingString(null);
			}
			else {
				importProcessContext.setColumnHeadingString(columnHeadings.toJSONString().replaceAll("\"", "\\\""));
			}	
		}
		else {
			importProcessContext.setColumnHeadingString(columnHeadings.toJSONString().replaceAll("\"", "\\\""));
		}
		return importProcessContext;
		
	}
	
	public static List<ImportProcessLogContext> setAssetName(ImportProcessContext importProcessContext, FacilioModule module, List<ImportProcessLogContext> logContexts) throws Exception{
		String nameField = importProcessContext.getFieldMapping().get(module.getName()+ "__name");
		for(ImportProcessLogContext logContext: logContexts) {
			String name= logContext.getRowContexts().get(0).getColVal().get(nameField).toString();
			logContext.setAssetName(name);
		}
		return logContexts;
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
	public static void addImportProcess(ImportProcessContext importProcessContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getImportProcessModule().getTableName())
				.fields(FieldFactory.getImportProcessFields());
		
		importProcessContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		Map<String, Object> props = FieldUtil.getAsProperties(importProcessContext);
		
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		importProcessContext.setId((Long) props.get("id"));
	}
	
	public static void updateImportProcess(ImportProcessContext importProcessContext) throws Exception {
		ImportProcessContext existingProcess = getImportProcessContext(importProcessContext.getId());
		if (existingProcess == null) {
			throw new IllegalArgumentException("Import Process doesn't exists");
		}

		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
		update.table(ModuleFactory.getImportProcessModule().getTableName());
		update.fields(FieldFactory.getImportProcessFields());
		update.andCustomWhere(ModuleFactory.getImportProcessModule().getTableName()+".ID = ?", importProcessContext.getId());
		
		Map<String, Object> props = FieldUtil.getAsProperties(importProcessContext);
		
		update.update(props);	
		
	}
	
	public static void updateImportProcess(ImportProcessContext importProcessContext,ImportStatus importStatus) throws Exception {
		
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
		update.table(ModuleFactory.getImportProcessModule().getTableName());
		update.fields(FieldFactory.getImportProcessFields());
		update.andCustomWhere(ModuleFactory.getImportProcessModule().getTableName()+".ID = ?", importProcessContext.getId());
		
		Map<String, Object> props = new HashMap<>();
		
		props.put("status", importStatus.getValue());
		
		update.update(props);	
		
	}
	
	public static List<Map<String,Object>> getValidatedRows(Long importProcessId) throws Exception{
		String validConditions = ImportProcessContext.ImportLogErrorStatus.RESOLVED.getStringValue() + "," + ImportProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getStringValue() + "," + ImportProcessContext.ImportLogErrorStatus.OTHER_ERRORS.getStringValue();
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getImportProcessLogModule().getTableName())
				.select(FieldFactory.getImportProcessLogFields())
				.andCondition(CriteriaAPI.getCondition("ERROR_RESOLVED", "error_resolved", validConditions, NumberOperators.EQUALS))
				.andCustomWhere("IMPORTID = ?", importProcessId);
		List<Map<String,Object>> result = selectRecordBuilder.get();
		return result;
	}
	
	public static void getFieldMapping(ImportProcessContext importProcessContext) throws Exception {
		
		if(importProcessContext.getColumnHeadingString() != null && importProcessContext.getModuleId() != null) {
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getImportProcessFields())
					.table(ModuleFactory.getImportProcessModule().getTableName());
			
			selectBuilder.andCustomWhere(ModuleFactory.getImportProcessModule().getTableName()+".COLUMN_HEADING = ?", importProcessContext.getColumnHeadingString());
			
			selectBuilder.andCustomWhere(ModuleFactory.getImportProcessModule().getTableName()+".MODULEID = ?", importProcessContext.getModuleId());
			
			selectBuilder.andCustomWhere(ModuleFactory.getImportProcessModule().getTableName()+".ORGID = ?", AccountUtil.getCurrentOrg().getId());
			
			selectBuilder.andCustomWhere(ModuleFactory.getImportProcessModule().getTableName()+".FIELD_MAPPING is not null");
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				
				String fieldMapping = (String) props.get(0).get("fieldMappingString");
				
				if(fieldMapping != null && !fieldMapping.trim().isEmpty()) {
					importProcessContext.setFieldMappingString(fieldMapping);
					return;
				}
			}
		}
		importProcessContext.populateFieldMapping();
	}
	
	public static void importPasteParsedData(List<Map<Integer,String>> parsedDatas, Map<Integer,String> columnMaping,ImportProcessContext importProcessContext) throws Exception {
		
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
				if(!isfilledByLookup) {
					props.put(value, cellValue);
				}
			});
			
			if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET)) {
				
				Long spaceId = getSpaceID(importProcessContext,props,null);
				
				props.put("space", spaceId);
				props.put("resourceType", ResourceType.ASSET.getValue());
				 
			}
			
			ReadingContext reading = FieldUtil.getAsBeanFromMap(props, ReadingContext.class);
			reading.setParentId(importProcessContext.getAssetId());
			readingsList.add(reading);
		}
		ProcessXLS.populateData(importProcessContext, readingsList);
	}
	public static JSONObject getFirstRow (Workbook workbook) throws Exception {
		JSONObject firstRow = new JSONObject();
		JSONArray columnHeadings = getColumnHeadings(workbook);
		Sheet datatypeSheet = workbook.getSheetAt(0);
		Row row = datatypeSheet.getRow(1);
		int lastCellNum = row.getLastCellNum();
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

		for (int i = 0; i < lastCellNum; i++) {
			Cell cell = row.getCell(i);
			if (columnHeadings.get(i) == null || columnHeadings.get(i) == "null") {
				continue;
			} else {
				if (cell == null) {
					firstRow.put(columnHeadings.get(i), null);
				}
				else if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
					throw new IllegalArgumentException("Unsupported Date/Time Formatted Field under column "
							+ columnHeadings.get(i) + " Kindly Use Plain text");
				} else {
					Object obj = 0.0;
					try {
						CellValue cellValue = evaluator.evaluate(cell);
						obj = getValueFromCell(cell, cellValue);
						firstRow.put(columnHeadings.get(i), obj);
					} catch (Exception e) {
						throw new IllegalArgumentException("Unable To Read Value under column " + columnHeadings.get(i));
					}
				}
			}
		}
//		workbook.close();
		return firstRow;
	}
	
	public static Object getValueFromCell(Cell cell, CellValue cellValue) throws Exception {
		
		Object val = 0.0;
		
		// Here we get CellValue after evaluating the formula So CellType FORMULA will never occur
		// todo add Date Time Format Handling 
		if (cellValue.getCellType() == CellType.BLANK) {
			val = null;
		}
		else if (cellValue.getCellType() == CellType.STRING) {
			if (cellValue.getStringValue().trim().length() == 0) {
				val = null;
			} else {
				val = cellValue.getStringValue().trim();
			}

		} else if (cellValue.getCellType() == CellType.NUMERIC) {
			val = cellValue.getNumberValue();
			
		} else if (cellValue.getCellType() == CellType.BOOLEAN) {
			val = cellValue.getBooleanValue();
		} else if (cellValue.getCellType() == CellType.ERROR) {
			throw new Exception("Error Evaulating Cell");
		} else {
			val = null;
		}
		
			
		return val;
	}
	
	public static Long getSpaceID(ImportProcessContext importProcessContext, HashMap<String, Object> colVal, HashMap<String,String> fieldMapping) throws Exception {

		String siteName,buildingName,floorName,spaceName;
		
		ArrayList<String> additionalSpaces = new ArrayList<>();
		String moduleName = importProcessContext.getModule().getName();
		siteName = (String) colVal.get(fieldMapping.get(moduleName + "__site"));
		buildingName = (String) colVal.get(fieldMapping.get(moduleName + "__building"));
		floorName = (String) colVal.get(fieldMapping.get(moduleName + "__floor"));
		spaceName = (String) colVal.get(fieldMapping.get(moduleName + "__spaceName"));
		
		if(spaceName != null) {
			for(int i =0; i<3; i++)
			{
				String temp = (String) colVal.get(fieldMapping.get(moduleName + "__subspace" + (i+1)));
				if(temp != null) {
					additionalSpaces.add(temp);
				}
				else {
					break;
				}
			}
		}
		
		ImportSiteAction siteMeta =new ImportSiteAction();
		ImportBuildingAction buildingMeta =new ImportBuildingAction();
		ImportFloorAction floorMeta =new ImportFloorAction();
		ImportSpaceAction spaceMeta =new ImportSpaceAction();
		
		Long siteId = null;
		Long buildingId = null;
		Long floorId = null;
		Long spaceId = null;
		 
		if(siteName != null && !siteName.equals("")) {
			 List<SiteContext> sites = SpaceAPI.getAllSites();
			 HashMap<String, Long> siteMap = new HashMap();
			 for(SiteContext siteContext : sites)	
			 {
				 siteMap.put(siteContext.getName().trim().toLowerCase(), siteContext.getId());
			 }
			 if(siteMap.containsKey(siteName.trim().toLowerCase()))
			 {
				siteId = siteMeta.getSiteId(siteName);
			 }
			 else
			 {
				 siteId = siteMeta.addSite(siteName);
			 }
			 if(siteId != null) {
				 spaceId = siteId;
			 }
		 }
		 
		 if(buildingName != null && !buildingName.equals("")) {
			 if(siteId != null) {
				 buildingId = buildingMeta.getBuildingId(siteId,buildingName);
			 }
			 else {
				 List<BuildingContext> buildings = SpaceAPI.getAllBuildings();
				 HashMap<String, Long> buildingMap = new HashMap();
				 for (BuildingContext buildingContext : buildings)
				 {
					 buildingMap.put(buildingContext.getName().trim().toLowerCase(), buildingContext.getId());
				 }
				 if(buildingMap.containsKey(buildingName.trim().toLowerCase()))
				 {
					 buildingId = buildingMeta.getBuildingId(buildingName);
				 }
			 }
			 if(buildingId == null)
			 {
				 buildingId = buildingMeta.addBuilding(buildingName, siteId, importProcessContext.getId());
			 }
			 
			 if(buildingId != null) {
				 spaceId = buildingId;
			 }
		 }
		 if(floorName != null && !floorName.equals("")) {
			if(buildingId != null) {
				floorId = floorMeta.getFloorId(buildingId,floorName);
			}
			else {
				
				 List<FloorContext> floors = SpaceAPI.getAllFloors();
				 HashMap<String, Long> floorMap = new HashMap();
				 for (FloorContext floorContext : floors)
				 {
					 floorMap.put(floorContext.getName().trim().toLowerCase(), floorContext.getId());
				 }
				 if(floorMap.containsKey(floorName.trim()))
				 {
					 floorId = floorMeta.getFloorId(floorName);
				 }
			}
		    if(floorId == null)
		    {
		    	floorId = floorMeta.addFloor(floorName, siteId, buildingId, importProcessContext.getId());
		    }
		    if(floorId != null) {
				 spaceId = floorId;
			 }
		 }

		 if(spaceName != null && !spaceName.equals("")) {
			 spaceId = null;
			 if(floorId != null) {
				 spaceId = spaceMeta.getSpaceId(floorId,spaceName);
			 }
			 else if (buildingId != null) {
				 spaceId = spaceMeta.getSpaceIdFromBuilding(buildingId,spaceName);
			 }
			 else if (siteId != null) {
				 spaceId = spaceMeta.getSpaceIdFromSite(siteId,spaceName);
			 }
			 
			if(spaceId == null) {
				 if (floorName == null)
				 {
					 spaceId = spaceMeta.addSpace(spaceName, siteId, buildingId, importProcessContext.getId());
				 }
				 else
				 {
				 spaceId = spaceMeta.addSpace(spaceName, siteId, buildingId, floorId);
				 }
			 }
		}
		 
		if(additionalSpaces.size()>0) {
			Long tempSpaceId;
			for(String additionalSpace : additionalSpaces) {
					tempSpaceId= SpaceAPI.getDependentSpaceId(additionalSpace, spaceId, additionalSpaces.indexOf(additionalSpace)+1);
					if(tempSpaceId != null) {
						spaceId = tempSpaceId;
					}
					else {
						spaceId = SpaceAPI.addDependentSpace(additionalSpace, spaceId,importProcessContext.getId());
					}
				}
		}
		return spaceId;
	
	}
	
	public static List<Map<Integer,String>> parseRawString(String rawString,String rawStringType) throws Exception {
		
		if(rawStringType.equals("tsv")) {
	        try(Reader rawStringReaded = new StringReader(rawString);
				ICsvListReader listReader = new CsvListReader(rawStringReaded, CsvPreference.TAB_PREFERENCE);) {
                
//	            listReader.getHeader(true);
                
                List<String> rowDatas;
    			List<Map<Integer,String>> parsedData = new ArrayList<>(); 
                while( (rowDatas = listReader.read()) != null ) {
                	
                	Map<Integer,String> posVsData = new HashMap<>();
                	for(int i=0;i<rowDatas.size();i++) {
                		
                		String data = rowDatas.get(i);
                		posVsData.put(i+1, data);
                	}
                	parsedData.add(posVsData);
                }
                return parsedData;
	        }
		}
		else if(rawStringType.equals("csv")) {
	        try(Reader rawStringReaded = new StringReader(rawString);
				ICsvListReader listReader = new CsvListReader(rawStringReaded, CsvPreference.STANDARD_PREFERENCE);) {
//	            listReader.getHeader(true);
                
                List<String> rowDatas;
    			List<Map<Integer,String>> parsedData = new ArrayList<>(); 
                while( (rowDatas = listReader.read()) != null ) {
                	
                	Map<Integer,String> posVsData = new HashMap<>();
                	for(int i=0;i<rowDatas.size();i++) {
                		
                		String data = rowDatas.get(i);
                		posVsData.put(i+1, data);
                	}
                	parsedData.add(posVsData);
                }
                return parsedData;
	        }
		}
		return null;
	}
	
	public static ImportProcessContext getImportProcessContext(Long id) throws Exception {
		if (id == null) {
			return null;
		}
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getImportProcessFields())
				.table(ModuleFactory.getImportProcessModule().getTableName())
				.andCustomWhere(ModuleFactory.getImportProcessModule().getTableName()+".ID = ?", id);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			ImportProcessContext importProcessContext = FieldUtil.getAsBeanFromMap(props.get(0), ImportProcessContext.class);
			return importProcessContext;
		}
		return null;
	}
	
	public static JSONArray getFields(String module, Integer importMode, Integer importSetting)
	{
		JSONArray fields = new JSONArray();
		
		try {
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule facilioModule =  bean.getModule(module);
			List<FacilioField> fieldsList = bean.getAllFields(module);
			Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldsList);
			
			if(facilioModule.getName().equals(FacilioConstants.ContextNames.TOOL)
					|| facilioModule.getName().equals(FacilioConstants.ContextNames.PURCHASED_TOOL)
					|| facilioModule.getName().equals(FacilioConstants.ContextNames.PURCHASED_ITEM)
					|| ImportFieldFactory.isFieldsFromFieldFactoryModule(facilioModule.getName())
					) {
				fields.addAll(ImportFieldFactory.getImportFieldNames(facilioModule.getName()));
			} else {
				List<String> removeFields = ImportFieldFactory.getFieldsTobeRemoved(facilioModule.getName());
				for (FacilioField field : fieldsList) {
					if(!removeFields.contains(field.getName())) {
						if (field.getDisplayType() == FacilioField.FieldDisplayType.ADDRESS) {
							fields.addAll(Arrays.asList(field.getName() + "_name", field.getName() + "_street", field.getName() + "_city", field.getName() + "_state", field.getName() + "_country", field.getName() + "_zip", field.getName() + "_lat", field.getName() + "_lng"));
						} else {
							fields.add(field.getName());
						}
					}
				}

				if (importSetting != null && (importSetting != ImportProcessContext.ImportSetting.INSERT.getValue() && importSetting != ImportProcessContext.ImportSetting.INSERT_SKIP.getValue())) {
					if (!fieldsMap.containsKey("localId") && !fieldsMap.containsKey("serialNumber")) {
						fields.add("id");
					}
				}
				fields.add("formId");

				if (AssetsAPI.isAssetsModule(facilioModule)) {

					fields.remove("space");
					fields.remove("resourceType");
					fields.remove("localId");
					if (importSetting != null && (importSetting != ImportProcessContext.ImportSetting.INSERT.getValue() && importSetting != ImportProcessContext.ImportSetting.INSERT_SKIP.getValue())) {
						fields.add("id");
					}

					if (importMode == null || importMode == 1) {
						if (module.equals(FacilioConstants.ContextNames.ENERGY_METER)) {
							fields.remove("purposeSpace");
						}
						fields.add("building");
						fields.add("floor");
						fields.add("spaceName");
						fields.add("space1");
						fields.add("space2");
						fields.add("space3");

					}
				} else if (facilioModule.getName().equals(FacilioConstants.ContextNames.WORK_ORDER)) {

					fields.remove("resource");
					fields.remove("site");
					fields.add("building");
					fields.add("floor");
					fields.add("spaceName");
					fields.add("space1");
					fields.add("space2");
					fields.add("space3");
					fields.add("asset");
				}
				else if (facilioModule.getName().equals(FacilioConstants.PM_V2.PM_V2_PLANNER)){
					fields.add("Times");
					fields.add("Runs Every");
					fields.add("Skip Every");
					fields.add("Execution days/dates/months");
					fields.add("Week Frequency");
					fields.add("Yearly Day Value");
					fields.add("Month Value");
					fields.add("Yearly Day of the Week Value");
					fields.add("End Date");
					fields.add("Start Date");
					fields.add("Season");
				}
		}
			if (!fields.contains("site") && !facilioModule.instanceOf(FacilioConstants.ContextNames.SITE) && FieldUtil.isSiteIdFieldPresent(facilioModule)
					&& AccountUtil.getCurrentSiteId() == -1) {
				fields.add("site");
			}
		}
		
		catch(Exception e) {
			log.info("Exception occurred ", e);
		}
		return fields;
	}
	
	public static JSONArray getIgnoreFields(String module, Integer importMode) throws Exception
	{
		JSONArray fields = new JSONArray();
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule facilioModule =  bean.getModule(module);
		
		if(facilioModule.getName().equals(FacilioConstants.ContextNames.ASSET) ||
				(facilioModule.getExtendModule() != null && facilioModule.getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))) {
			if(importMode == null || importMode == 1) {
				fields.add("category");
			}
		}
		return fields;
		
	}

	public static HashMap<String, FacilioField> getLocationFields(LookupField field) {
		HashMap<String, FacilioField> fieldsMap = new HashMap<>();
		fieldsMap.put(field.getName() + "_name", FieldFactory.getField("name", field.getDisplayName() + " - Name", "NAME", field.getModule(), FieldType.STRING));
		fieldsMap.put(field.getName() + "_street", FieldFactory.getField("street", field.getDisplayName() + " - Street", "STREET", field.getModule(), FieldType.STRING));
		fieldsMap.put(field.getName() + "_city", FieldFactory.getField("city", field.getDisplayName() + " - City", "CITY", field.getModule(), FieldType.STRING));
		fieldsMap.put(field.getName() + "_state", FieldFactory.getField("state", field.getDisplayName() + " - State", "STATE", field.getModule(), FieldType.STRING));
		fieldsMap.put(field.getName() + "_country", FieldFactory.getField("country", field.getDisplayName() + " - Country", "COUNTRY", field.getModule(), FieldType.STRING));
		fieldsMap.put(field.getName() + "_zip", FieldFactory.getField("zip", field.getDisplayName() + " - Zip", "ZIP", field.getModule(), FieldType.STRING));
		fieldsMap.put(field.getName() + "_lat", FieldFactory.getField("lat", field.getDisplayName() + " - LAT", "LAT", field.getModule(), FieldType.NUMBER));
		fieldsMap.put(field.getName() + "_lng", FieldFactory.getField("lng", field.getDisplayName() + " - LNG", "LNG", field.getModule(), FieldType.NUMBER));

		return fieldsMap;
	}
	
	public static ImportProcessContext updateTotalRows (ImportProcessContext importProcessContext) throws Exception {
		
		FacilioField idField = FieldFactory.getField("id", "ID", FieldType.NUMBER);
		String validConditions = ImportProcessContext.ImportLogErrorStatus.RESOLVED.getStringValue() + "," + ImportProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getStringValue();

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getImportProcessLogModule().getTableName())
				.select(new HashSet<>())
				.andCondition(CriteriaAPI.getCondition("IMPORTID", "importId", importProcessContext.getId().toString() ,NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("ERROR_RESOLVED", "error_resolved", validConditions, NumberOperators.EQUALS))
				.aggregate(CommonAggregateOperator.COUNT, idField);
		List<Map<String,Object>> result = selectRecordBuilder.get();
		if (!result.isEmpty()) {
			importProcessContext.setTotalRows((Long) result.get(0).get("id"));
		}
		return importProcessContext;
		
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
	
	public static String getKeyNameFromField(@NotNull FacilioField field) {
		return field.getModule().getName() +"__"+field.getName();
	}

	public static boolean canUpdateAssetBaseSpace(ImportProcessContext importProcessContext) throws Exception {
		JSONObject fieldMapping = importProcessContext.getFieldMappingJSON();
		if (fieldMapping != null) {
			if (AssetsAPI.isAssetsModule(importProcessContext.getModule())) {
					
				if(importProcessContext.getImportSetting().intValue() == ImportProcessContext.ImportSetting.UPDATE.getValue() || importProcessContext.getImportSetting().intValue() == ImportProcessContext.ImportSetting.UPDATE_NOT_NULL.getValue()) {
					if ((fieldMapping.containsKey("asset__floor") || fieldMapping.containsKey("asset__building") || fieldMapping.containsKey("asset__spaceName") || fieldMapping.containsKey("asset__space") || fieldMapping.containsKey("asset__space1") || fieldMapping.containsKey("asset__space2") || fieldMapping.containsKey("asset__space3"))) {
						return true;
					}
				}
				else {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean canUpdateResource(ImportProcessContext importProcessContext) throws Exception {
		JSONObject fieldMapping = importProcessContext.getFieldMappingJSON();
		String module = importProcessContext.getModule().getName();
		if (fieldMapping != null) {
			if (fieldMapping.containsKey(module + "__floor") || fieldMapping.containsKey(module + "__building") || fieldMapping.containsKey(module + "__spaceName") || fieldMapping.containsKey(module + "__space") || fieldMapping.containsKey(module + "__space1") || fieldMapping.containsKey(module + "__space2") || fieldMapping.containsKey(module + "__space3") || fieldMapping.containsKey(module + "__asset")) {
				return true;
			}
		}
		return false;
	}

	public static  boolean isInsertImport(ImportProcessContext importProcessContext) {
		if (importProcessContext.getImportSetting().intValue() == ImportProcessContext.ImportSetting.INSERT.getValue() || importProcessContext.getImportSetting().intValue() == ImportProcessContext.ImportSetting.INSERT_SKIP.getValue()) {
			return true;
		}
		return false;
	}

	public static boolean isResourceExtendedModule(FacilioModule module) {
		if (module.getName().equals(FacilioConstants.ContextNames.RESOURCE)) {
			return true;
		} else if (module.getExtendModule() != null) {
			return isResourceExtendedModule(module.getExtendModule());
		}
		return false;
	}
	public static boolean isSpaceModule (FacilioModule module) {
			return module.instanceOf(FacilioConstants.ContextNames.SPACE);
	}

	public static boolean isBaseSpaceExtendedModule(FacilioModule module) {
		return module.instanceOf(FacilioConstants.ContextNames.BASE_SPACE);
	}

	public static void setDefaultStateFlowAndStatus(ModuleBaseWithCustomFields record, FacilioModule module) throws Exception {
		StateFlowRuleContext defaultStateFlow = StateFlowRulesAPI.getDefaultStateFlow(module);
		if (defaultStateFlow != null) {
			record.setStateFlowId(defaultStateFlow.getId());
			if (defaultStateFlow.getDefaultStateId() > 0) {
				FacilioStatus status = StateFlowRulesAPI.getStateContext(defaultStateFlow.getDefaultStateId());
				record.setModuleState(status);
			}
		}
	}


	public static AssetContext getAssetFromName(String assetName, Long siteId) throws Exception {

		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule assetModule = bean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> assetFields = bean.getAllFields(FacilioConstants.ContextNames.ASSET);
		SelectRecordsBuilder<AssetContext> resourceBuilder = new SelectRecordsBuilder<AssetContext>()
				.select(assetFields)
				.module(bean.getModule(FacilioConstants.ContextNames.ASSET))
				.table(assetModule.getTableName())
				.beanClass(AssetContext.class)
				.andCondition(CriteriaAPI.getCondition("NAME", "name", assetName.replaceAll(",", StringOperators.DELIMITED_COMMA), StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(assetModule), String.valueOf(siteId), PickListOperators.IS));
		List<AssetContext> props = resourceBuilder.get();
		if(props != null && !props.isEmpty()) {
			return props.get(0);
		}
		return null;
	}


	
	public static class  ImportProcessConstants{
		public static final String IMPORT_PROCESS_CONTEXT = "importprocessContext";
		public static final String READINGS_LIST = "readingsList";
		public static final String READINGS_CONTEXT_LIST = "readingsContextList";
		public static final String FIELDS_MAPPING = "fieldMapping";
		public static final String NO_CATEGORY_DEFINED = "&&none$$";
		public static final String NAME_FIELD = "name";
		public static final String ID_FIELD = "id";
		public static final String CATEGORY_FROM_CONTEXT = "category";
		public static final String EMAIL_MESSAGE = "emailMessage";
		public static final String CATEGORY_BASED_ASSETS = "categoryBasedAsset";
		public static final String BULK_SETTING = "bulkSetting";
		public static final String MODULES_INFO = "modulesInfo";
		public static final String GROUPED_FIELDS = "groupedfields";
		public static final String GROUPED_ROW_CONTEXT = "groupedContext";
		public static final String GROUPED_READING_CONTEXT = "groupedContext"; // TODO: Remove after clearing in normal import
		public static final String SITE_ID_FIELD = "site";
		public static final String PARENT_ID_FIELD = "parentId";
		public static final String FLOOR_ID_FIELD = "floor";
		public static final String BUILDING_ID_FIELD = "building";
		public static final String SPACE_FIELD = "space";
		public static final String T_TIME ="ttime";
		public static final String ASSET_ID_FIELD = "asset";
		public static final String UPDATE_FIELDS = "updateBy";
		public static final String INSERT_FIELDS = "insertBy";
		public static final String SPACE_ID = "spaceId";	
		public static final String RESOURCE_TYPE = "resourceType";
		public static final String ASSET_CATEGORY = "assetCategory";
		public static final String UNIQUE_MAPPING = "uniqueMapping";
		public static final String FIELD_MAPPING = "fieldMapping";
		public static final String SYS_FIELD_SHOW ="save";
		public static final String NULL_UNIQUE_FIELDS = "nullUniqueFields";
		public static final String NULL_RESOURCES = "nullResources";
		public static final String DATE_FORMATS = "dateFormats";
		public static final String UNIT_FORMATS = "unitFormats";
		public static final String IMPORT_TEMPLATE_CONTEXT = "importTemplateContext";
		public static final String TIME_STAMP_STRING = "TimeStamp";
		public static final String IMPORT_WARNING= "warningMessage";
		public static final String IMPORT_ROW_CONTEXT = "importRowContext";
		public static final String HAS_DUPLICATE_ENTRIES ="hasDuplicateEntries";
		public static final String ROW_COUNT = "rowCount";
		public static final String NULL_COUNT = "nullCount";
		public static final String PARSING_ERROR="parsingError";
		public static final String PARSING_ERROR_MESSAGE="parsingErrorMessage";
		public static final String MODULE_STATIC_FIELDS = "moduleStaticFields";
		public static final String JOB_ID = "jobId";
		public static final String MODULE_META = "moduleMeta";
		public static final String CHOOSEN_MODULE = "choosenModule";
		public static final String IS_FROM_IMPORT = "isFromImport";
		public static final String ADDED_BASE_SPACE_IDS = "baseSpaceIds";
		public static final String REQUIRED_FIELDS = "requiredFields";
		public static final String UNIQUE_FUNCTION = "uniqueFunction";
		public static final String ROW_FUNCTION = "rowFunction";
		public static final String BEFORE_IMPORT_FUNCTION = "beforeImportFunction";
		public static final String AFTER_IMPORT_FUNCTION = "afterImportFunction";
		public static final String LOOKUP_MAIN_FIELD_MAP = "lookupMainFieldMap";

		public static final String INSERT_RECORDS = "insertRecords";
		public static final String UPDATE_RECORDS = "updateRecords";
		public static final String OLD_RECORDS = "oldRecords";
		public static final String PARSED_PROPS = "parsedProps";
	}
	
	
	
	
}
