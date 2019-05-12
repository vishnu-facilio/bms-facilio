package com.facilio.bmsconsole.commands.data;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.*;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.json.simple.JSONArray;

import java.io.File;
import java.io.InputStream;
import java.sql.Date;
import java.util.*;

public class ProcessSpaceXLS implements Command {

	private static Logger log = LogManager.getLogger(ProcessSpaceXLS.class.getName());

	public boolean execute(Context context) throws Exception {
		
		
		return false;

	}
	
	
	public static boolean isRowEmpty(Row row) {
		if (row == null) {
	        return true;
	    }
	    if (row.getLastCellNum() <= 0) {
	        return true;
	    }
	    for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
	        Cell cell = row.getCell(cellNum);
	        if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && StringUtils.isNotBlank(cell.toString())) {
	            return false;
	        }
	    }
	    return true;
	} 
	
	public static void processImport(ImportProcessContext importProcessContext) throws Exception
	{
		System.out.println("All set for importing "+importProcessContext+" \n" + new Date(System.currentTimeMillis()));
//		System.out.println("-----------> checking 6----------->");
		HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();			
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		new ArrayList<ReadingContext>();
		InputStream ins = fs.readFile(importProcessContext.getFileId());
		
		HashMap<Integer, String> colIndex = new HashMap<Integer, String>();
		
		Workbook workbook = WorkbookFactory.create(ins);
		
		for(int i=0;i<workbook.getNumberOfSheets();i++) {
			Sheet datatypeSheet = workbook.getSheetAt(i);

			Iterator<Row> itr = datatypeSheet.iterator();
			boolean heading=true;
			int row_no = 0;
			while (itr.hasNext()) {
				row_no++;
				Row row = itr.next();
				if (heading) {
					// column heading
					
					Iterator<Cell> cellItr = row.cellIterator();
					int cellIndex = 0;
					while (cellItr.hasNext()) {
						Cell cell = cellItr.next();
						String cellValue = cell.getStringCellValue();
						colIndex.put(cellIndex, cellValue);
						cellIndex++;
					}
					heading=false;
					continue;
				}
				HashMap<String, Object> colVal = new HashMap<>();

				Iterator<Cell> cellItr = row.cellIterator();
				int cellIndex = 0;
				while (cellItr.hasNext()) {
					Cell cell = cellItr.next();

					String cellName = colIndex.get(cellIndex);
					if (cellName == null) {
						continue;
					}

					Object val = 0.0;
					if (cell.getCellTypeEnum() == CellType.STRING) {

						val = cell.getStringCellValue();
					}
					else if (cell.getCellTypeEnum() == CellType.NUMERIC || cell.getCellTypeEnum() == CellType.FORMULA) {

						val = cell.getNumericCellValue();
					}
					else if(cell.getCellTypeEnum() == CellType.BOOLEAN) {
						val = cell.getBooleanCellValue();
					}
					colVal.put(cellName.trim().toLowerCase(), val);

					cellIndex++;
					
					
					
				}
				ImportSiteAction siteMeta =new ImportSiteAction();
				ImportBuildingAction buildingMeta =new ImportBuildingAction();
				ImportFloorAction floorMeta =new ImportFloorAction();
				ImportSpaceAction spaceMeta =new ImportSpaceAction();
				
				
				 String siteName = (String) colVal.get(fieldMapping.get("Site Name").toString().toLowerCase());
				 String buildingName = (String) colVal.get(fieldMapping.get("Building Name").toString().toLowerCase());
				 String floorName = (String) colVal.get(fieldMapping.get("Floor").toString().toLowerCase());
				 String spaceName = (String) colVal.get(fieldMapping.get("Space Name").toString().toLowerCase());
				Long siteId = null;
				Long buildingId = null;
				Long floorId = null;
				Long spaceId = null;
				 
				System.out.println("Finished loading data from file  "+row_no +" rows . "+importProcessContext+" \n" + new Date(System.currentTimeMillis()));
				 colVal.entrySet().iterator();
				  
//				 System.out.println("------------Colval---------------");
				 
//				 System.out.println("<----------- SiteName------------->"+ siteName);
//				 System.out.println("<----------- BuildingName------------->"+ buildingName);
//				 System.out.println("<----------- FloorName------------->"+ floorName);
//				 System.out.println("<----------- SpaceName------------->"+ spaceName);
				
				 
				 // Site Creation
				 try{
					 
				 List<SiteContext> sites = SpaceAPI.getAllSites();
				 HashMap<String, Long> siteMap = new HashMap();
				 for(SiteContext siteContext : sites)	
				 {
					 siteMap.put(siteContext.getName().trim().toLowerCase(), siteContext.getId());
				 }
				 if(siteMap.containsKey(siteName.trim().toLowerCase()))
				 {
					
					siteId = siteMeta.getSiteId(siteName);
					System.out.println("----------> The site already available : "+siteId);
				 }
				 else
				 {
					 siteId = siteMeta.addSite(siteName);
					 System.out.println("------------------->The created site id: "+siteId);
				 }
				 
				 
				 
				//  Building creation
				
				 List<BuildingContext> buildings = SpaceAPI.getAllBuildings();
				 HashMap<String, Long> buildingMap = new HashMap();
				 for (BuildingContext buildingContext : buildings)
				 {
					 buildingMap.put(buildingContext.getName().trim().toLowerCase(), buildingContext.getId());
					 
				 }
				 if(buildingMap.containsKey(buildingName.trim().toLowerCase()))
				 {
					 buildingId = buildingMeta.getBuildingId(buildingName);
					 System.out.println("----------> The building already available : "+buildingId);
				 }
				 else
				 {
					 buildingId = buildingMeta.addBuilding(buildingName, siteId);
					 
				 }
				
//				 System.out.println("--------------------- checking floorname ---------------->"+floorName);
				 
				try 
				{
					
				 List<FloorContext> floors = SpaceAPI.getAllFloors();
				 HashMap<String, Long> floorMap = new HashMap();
				 for (FloorContext floorContext : floors)
				 {
					 floorMap.put(floorContext.getName().trim().toLowerCase(), floorContext.getId());
					 
				 }
				 if(floorMap.containsKey(floorName.trim().toLowerCase()))
				 {
					 floorId = floorMeta.getFloorId(floorName);
					 System.out.println("----------> The floor already available : "+floorId);
				 }
				 else
				 {
					 floorId = floorMeta.addFloor(floorName, siteId, buildingId);
				 }
				 
				}
				catch(Exception e)
				{
					log.info("Exception occurred ", e);
				}
				
				
				
				 List<SpaceContext> spaces = SpaceAPI.getAllSpaces();
				 HashMap<String, Long> spaceMap = new HashMap();
				 for (SpaceContext spaceContext : spaces)
				 {
					 spaceMap.put(spaceContext.getName().trim().toLowerCase(), spaceContext.getId());
					 
				 }
				 if(spaceMap.containsKey(spaceName.trim().toLowerCase()))
				 {
					 spaceId = spaceMeta.getSpaceId(spaceName);
					 System.out.println("----------> The space already available : "+spaceId);
				 }
				 
				 System.out.println("Floor id is "+floorId);
					 
				 	if (floorName == null)
					 {
						 spaceId = spaceMeta.addSpace(spaceName, siteId, buildingId);
					 }
					 else
					 {
					 spaceId = spaceMeta.addSpace(spaceName, siteId, buildingId, floorId);
				 
					 }
				 
				}
				catch (Exception e)
				{
					log.info("Exception occurred ", e);
				}
				 // Long idh = siteMeta.addSite(siteName);
				 
				 // System.out.println(idh);
				 // Long buildingId = buildingMeta.addBuilding(buildingName, siteId);
				 // Long floorId = floorMeta.addFloor(SiteName, Building);
				 // Long spaceId = spaceMeta.addSpace(spaceName, buildingId, siteId);
				 
				  
//				 while (it.hasNext()) {
//				    
//				        Map.Entry pair = (Map.Entry)it.next();
//				        List<String> ins = new ArrayList<String>();
//				        System.out.println(pair.getKey());
//				       if(pair.getKey().toString().trim().toLowerCase().equals("space")) {
//				    	   
//				    	   System.out.println("<----------- Siteval------------->");
//				    	   System.out.println(pair.getKey() + " = " + pair.getValue());
//				    	  
//				    	 
//				  	   System.out.println(siteHash);
//				    	   
//				       }
//				       
//				    }
//				 
				// System.out.println("List of Sites: "+siteHash);
				    
				/*HashMap <String, Object> props = new LinkedHashMap<String,Object>();
				fieldMapping.forEach((key,value) -> 
				{
					Object cellValue=colVal.get(value);
					boolean isfilledByLookup = false;
					if(cellValue != null) {
						FacilioField facilioField = metainfo.getFacilioFieldMapping(metainfo.getModule().getName()).get(key);
						if(facilioField.getDataTypeEnum().equals(FieldType.LOOKUP)) {
							LookupField lookupField = (LookupField) facilioField;
							List<Map<String, Object>> lookupPropsList = getLookupProps(lookupField,cellValue);
							if(lookupPropsList != null) {
								Map<String, Object> lookupProps = lookupPropsList.get(0);
								props.put(key, lookupProps);
								isfilledByLookup = true;
							}
						}
					}
					if(!isfilledByLookup) {
						props.put(key, cellValue);
					}
				});
				System.out.println("Loading  ReadingContext   . "+metainfo + new Date(System.currentTimeMillis()));

				ReadingContext reading = FieldUtil.getAsBeanFromMap(props, ReadingContext.class);
				reading.setParentId(metainfo.getAssetId());
				readingsList.add(reading);
				*/
				System.out.println("Finished importing xls");

			}
		}
/*		System.out.println("metainfo.getModule().getName()) is "+metainfo.getModule().getName());
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (metainfo.getModule().getName() == "space")
		{
			System.out.println("Inserting space xls");	
			
		}
		else
		{
			System.out.println("Inserting xls");
		InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>()
				.moduleName(metainfo.getModule().getName())
				.fields(bean.getAllFields(metainfo.getModule().getName()))
				.addRecords(readingsList);
		readingBuilder.save();
		}*/
		workbook.close();
	}

	public static List<Map<String, Object>> getLookupProps(LookupField lookupField, Object value) {
		
		try {
			System.out.println("getLookupProps -- "+lookupField.getColumnName() +" facilioField.getModule() - "+lookupField.getLookupModule().getTableName() +" with value -- "+value);
			System.out.println("<--------------- checking 8 ----------------->");
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fieldsList= new ArrayList<>(bean.getAllFields(lookupField.getLookupModule().getName()));
			fieldsList.add(FieldFactory.getIdField());
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fieldsList)
					.table(lookupField.getLookupModule().getTableName())
					.andCustomWhere("LOWER(NAME) = ?", value.toString().toLowerCase());
			
			List<Map<String, Object>> props = selectBuilder.get();
			
			if(props.isEmpty()) {
				
				HashMap <String, Object> insertProps = new LinkedHashMap<String,Object>();
				
				insertProps.put("name", value);
				
				ReadingContext reading = FieldUtil.getAsBeanFromMap(insertProps, ReadingContext.class);
				
				InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>()
						.moduleName(lookupField.getLookupModule().getName())
						.fields(bean.getAllFields(lookupField.getLookupModule().getName()));
				
				Long id = readingBuilder.insert(reading);
				if(id != null) {
					System.out.println("inserted with ID --"+id);
					Map<String, Object> propMap = new HashMap<>();
					propMap.put("id", id);
					propMap.put("name", value);
					props.add(propMap);
				}
			}
			return props;			
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
		}
		return null;
	}
	public static JSONArray getColumnHeadings(File excelfile) throws Exception
	{
		JSONArray columnheadings = new JSONArray();
		
        Workbook workbook = WorkbookFactory.create(excelfile);
        Sheet datatypeSheet = workbook.getSheetAt(0);
      System.out.println("<---------checking 5---------->");  
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
	
	
	
}