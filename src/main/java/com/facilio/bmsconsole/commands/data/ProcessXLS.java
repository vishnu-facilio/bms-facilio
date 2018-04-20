package com.facilio.bmsconsole.commands.data;

import java.io.File;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONArray;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportBuildingAction;
import com.facilio.bmsconsole.actions.ImportFloorAction;
import com.facilio.bmsconsole.actions.ImportMetaInfo;
import com.facilio.bmsconsole.actions.ImportSiteAction;
import com.facilio.bmsconsole.actions.ImportSpaceAction;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ProcessXLS implements Command {

	
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
	
	public static void processImport(ImportMetaInfo metainfo) throws Exception
	{
		System.out.println("All set for importing "+metainfo+" \n" + new Date(System.currentTimeMillis()));
		
		HashMap<String, String> fieldMapping = metainfo.getFieldMapping();			
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		List<ReadingContext> readingsList = new ArrayList<ReadingContext>();
		InputStream ins = fs.readFile(metainfo.getFileId());
		
		HashMap<Integer, String> colIndex = new HashMap<Integer, String>();
		
		Workbook workbook = WorkbookFactory.create(ins);
		
		for(int i=0;i<workbook.getNumberOfSheets();i++) {
			Sheet datatypeSheet = workbook.getSheetAt(i);

			Iterator<Row> itr = datatypeSheet.iterator();
			boolean heading=true;
			int row_no = 0;
			while (itr.hasNext()) {
				row_no++;
				System.out.println("row_no -- "+row_no);
				Row row = itr.next();
				if(row.getPhysicalNumberOfCells() <= 0) {
					break;
				}
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
				while (cellItr.hasNext()) {
					Cell cell = cellItr.next();

					String cellName = colIndex.get(cell.getColumnIndex());
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
					else {
						val = null;
					}
					colVal.put(cellName, val);

				}
				System.out.println("Finished loading data from file  "+row_no +" rows . "+metainfo+" \n" + new Date(System.currentTimeMillis()));
				
				System.out.println("colVal ---= "+colVal);

				HashMap <String, Object> props = new LinkedHashMap<String,Object>();
				
				if(metainfo.getModule().getName().equals(FacilioConstants.ContextNames.ASSET)) {
					
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
					 props.put("space", spaceId);
					 props.put("resourceType", ResourceType.ASSET.getValue());
					 
					colVal.remove("site");
					colVal.remove("building");
					colVal.remove("floor");
					colVal.remove("spaceName");
				}
				
				fieldMapping.forEach((key,value) -> 
				{
					Object cellValue=colVal.get(value);
					boolean isfilledByLookup = false;
					if(cellValue != null && !cellValue.toString().equals("")) {
						
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
				System.out.println("prpposss ----- "+props+"\n\n\n");
				ReadingContext reading = FieldUtil.getAsBeanFromMap(props, ReadingContext.class);
				reading.setParentId(metainfo.getAssetId());
				readingsList.add(reading);
				
				System.out.println("Finished commit data  for assetid ="+metainfo.getAssetId() +" \n"+ new Date(System.currentTimeMillis()));

			}
		}
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>()
				.moduleName(metainfo.getModule().getName())
				.fields(bean.getAllFields(metainfo.getModule().getName()))
				.addRecords(readingsList);
		readingBuilder.save();
		workbook.close();
	}

	public static List<Map<String, Object>> getLookupProps(LookupField lookupField,Object value) {
		
		try {
			System.out.println("getLookupProps -- "+lookupField.getColumnName() +" facilioField.getModule() - "+lookupField.getLookupModule().getTableName() +" with value -- "+value);
			
			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			ArrayList<FacilioField> fieldsList = new ArrayList<FacilioField>();
			
			fieldsList= (ArrayList<FacilioField>) bean.getAllFields(lookupField.getLookupModule().getName()).clone();
			fieldsList.add(FieldFactory.getIdField(lookupField.getLookupModule()));
			fieldsList.add(FieldFactory.getOrgIdField(lookupField.getLookupModule()));
			fieldsList.add(FieldFactory.getModuleIdField(lookupField.getLookupModule()));
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fieldsList)
					.table(lookupField.getLookupModule().getTableName())
					.andCustomWhere("LOWER(NAME) = ?", value.toString().toLowerCase())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(lookupField.getLookupModule()));
			
			List<Map<String, Object>> props = selectBuilder.get();
			
			if(props.isEmpty()) {
				
				HashMap <String, Object> insertProps = new LinkedHashMap<String,Object>();
				
				insertProps.put("name", value);
				insertProps.put("orgId", AccountUtil.getCurrentOrg().getId());
				insertProps.put("moduleId", lookupField.getLookupModule().getModuleId());
				
				GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
						.table(lookupField.getLookupModule().getTableName())
						.fields(fieldsList);
				
				
				insertRecordBuilder.addRecord(insertProps);
				
				insertRecordBuilder.save();
				System.out.println("insertProps --- "+insertProps);
				Long id = (Long) insertProps.get("id");
				
				if(id != null) {
					System.out.println("inserted with ID --"+id);
					props.add(insertProps);
				}
			}
			return props;			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
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
	
	
	
}










