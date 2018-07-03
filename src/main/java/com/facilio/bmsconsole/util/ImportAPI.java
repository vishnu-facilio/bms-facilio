package com.facilio.bmsconsole.util;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportBuildingAction;
import com.facilio.bmsconsole.actions.ImportFloorAction;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportProcessContext.ImportStatus;
import com.facilio.bmsconsole.actions.ImportSiteAction;
import com.facilio.bmsconsole.actions.ImportSpaceAction;
import com.facilio.bmsconsole.commands.data.ProcessXLS;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class ImportAPI {

	private static org.apache.log4j.Logger log = LogManager.getLogger(ImportAPI.class.getName());

	public static JSONArray getColumnHeadings(File excelfile) throws Exception
	{
		JSONArray columnheadings = new JSONArray();
		
        Workbook workbook = WorkbookFactory.create(excelfile);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        
        Iterator<Row> itr = datatypeSheet.iterator();
        while (itr.hasNext()) {
        	Row row = itr.next();
        	Iterator<Cell> cellItr = row.cellIterator();
        	while (cellItr.hasNext()) {
        		Cell cell = cellItr.next();
        		String cellValue = cell.getStringCellValue();
        		columnheadings.add(cellValue);
        	}
        	break;
        }
		workbook.close();
		
		return columnheadings;
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
				
				Long spaceId = getSpaceIDforAssets(props);
				
				props.put("space", spaceId);
				props.put("resourceType", ResourceType.ASSET.getValue());
				 
			}
			
			ReadingContext reading = FieldUtil.getAsBeanFromMap(props, ReadingContext.class);
			reading.setParentId(importProcessContext.getAssetId());
			readingsList.add(reading);
		}
		ProcessXLS.populateData(importProcessContext, readingsList);
	}
	
	public static Long getSpaceIDforAssets(HashMap<String, Object> colVal) throws Exception {

		
		String siteName = (String) colVal.get("site");
		String buildingName = (String) colVal.get("building");
		String floorName = (String) colVal.get("floor");
		String spaceName = (String) colVal.get("spaceName");
		
		
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
				 buildingId = buildingMeta.addBuilding(buildingName, siteId);
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
		    	floorId = floorMeta.addFloor(floorName, siteId, buildingId);
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
			 else {
				 List<SpaceContext> spaces = SpaceAPI.getAllSpaces();
				 HashMap<String, Long> spaceMap = new HashMap();
				 for (SpaceContext spaceContext : spaces)
				 {
					 spaceMap.put(spaceContext.getName().trim().toLowerCase(), spaceContext.getId());
				 }
				 if(spaceMap.containsKey(spaceName.trim().toLowerCase()))
				 {
					 spaceId = spaceMeta.getSpaceId(spaceName);
				 }
			}
			if(spaceId == null) {
				 if (floorName == null)
				 {
					 spaceId = spaceMeta.addSpace(spaceName, siteId, buildingId);
				 }
				 else
				 {
				 spaceId = spaceMeta.addSpace(spaceName, siteId, buildingId, floorId);
				 }
			}
		 }
		colVal.remove("site");
		colVal.remove("building");
		colVal.remove("floor");
		colVal.remove("spaceName");
		return spaceId;
	
	}
	
	public static List<Map<Integer,String>> parseRawString(String rawString,String rawStringType) throws Exception {
		
		if(rawStringType.equals("tsv")) {
			
			ICsvListReader listReader = null;
	        try {
	            Reader rawStringReaded = new StringReader(rawString);
                listReader = new CsvListReader(rawStringReaded, CsvPreference.TAB_PREFERENCE);
                
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
                System.out.println("parsedData --- "+parsedData);
                return parsedData;
	        }
	        finally {
                if( listReader != null ) {
                     listReader.close();
                }
	        }
	        
		}
		else if(rawStringType.equals("csv")) {
			ICsvListReader listReader = null;
	        try {
	            Reader rawStringReaded = new StringReader(rawString);
                listReader = new CsvListReader(rawStringReaded, CsvPreference.STANDARD_PREFERENCE);
                
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
                System.out.println("parsedData --- "+parsedData);
                return parsedData;
	        }
	        finally {
                if( listReader != null ) {
                     listReader.close();
                }
	        }
		}
		return null;
	}
	
	public static ImportProcessContext getImportProcessContext(Long id) throws Exception {
		
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
	
	public static JSONArray getFields(String module)
	{
		JSONArray fields = new JSONArray();
		
		try {
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fieldsList= bean.getAllFields(module);

			for(FacilioField field : fieldsList)
			{
				if(!ImportAPI.isRemovableFieldOnImport(field.getName()))
				{
					fields.add(field.getName());
				}
			}
			if(module.equals(FacilioConstants.ContextNames.ASSET) || module.equals(FacilioConstants.ContextNames.ENERGY_METER)) {
				
				fields.remove("space");
				fields.remove("localId");
				fields.remove("resourceType");
				if(module.equals(FacilioConstants.ContextNames.ENERGY_METER)) {
					fields.remove("purposeSpace");
				}
				
				fields.add("site");
				fields.add("building");
				fields.add("floor");
				fields.add("spaceName");
			}
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
		}
		return fields;
	}
	
	public static boolean isRemovableFieldOnImport(String name) {

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
}
