package com.facilio.bmsconsole.commands;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportBuildingAction;
import com.facilio.bmsconsole.actions.ImportFloorAction;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.actions.ImportSiteAction;
import com.facilio.bmsconsole.actions.ImportSpaceAction;
import com.facilio.bmsconsole.commands.data.ProcessXLS;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.google.common.collect.ArrayListMultimap;

public class ProcessImportCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(ProcessImportCommand.class.getName());
	private static org.apache.log4j.Logger log = LogManager.getLogger(ProcessXLS.class.getName());
	private static ArrayListMultimap<String, String> groupedFields;
	private static ArrayListMultimap<String, Long> recordsList = ArrayListMultimap.create();

	@Override
	public boolean execute(Context c) throws Exception {

		ArrayListMultimap<String, ReadingContext> groupedContext = ArrayListMultimap.create();
		ImportProcessContext importProcessContext = (ImportProcessContext) c.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();
		fieldMapParsing(fieldMapping);
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		List<ReadingContext> readingsList = new ArrayList<ReadingContext>();
		InputStream ins = fs.readFile(importProcessContext.getFileId());
		ArrayList<String> modulesPlusFields = new ArrayList(fieldMapping.keySet());
		List<String> moduleNames = new ArrayList<>(groupedFields.keySet());
		Map<String, Long> lookupHolder;
		
		JSONObject importMeta = importProcessContext.getImportJobMetaJson();
		JSONObject dateFormats = (JSONObject) importMeta.get(ImportAPI.ImportProcessConstants.DATE_FORMATS);
//		importProcessContext.setStatus(ImportProcessContext.ImportStatus.IN_PROGRESS.getValue());
//		ImportAPI.updateImportProcess(importProcessContext);

		HashMap<Integer, String> headerIndex = new HashMap<Integer, String>();

		Workbook workbook = WorkbookFactory.create(ins);

		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			Sheet datatypeSheet = workbook.getSheetAt(i);

			Iterator<Row> rowItr = datatypeSheet.iterator();
			boolean heading = true;
			int row_no = 0;
			while (rowItr.hasNext()) {
				row_no++;
				LOGGER.severe("row_no -- " + row_no);
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
						headerIndex.put(cellIndex, cellValue);
						cellIndex++;
					}
					heading = false;
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

						val = cell.getStringCellValue().trim();
					} else if (cell.getCellTypeEnum() == CellType.NUMERIC
							|| cell.getCellTypeEnum() == CellType.FORMULA) {
						if (cell.getCellTypeEnum() == CellType.NUMERIC && HSSFDateUtil.isCellDateFormatted(cell)) {
							Date date = cell.getDateCellValue();
							Instant date1 = date.toInstant();
							val = date1.getEpochSecond()*1000;
						} 
						else if(cell.getCellTypeEnum() == CellType.FORMULA) {
							val = cell.getStringCellValue();
						}
						else {
							val = cell.getNumericCellValue();
						}
					} else if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
						val = cell.getBooleanCellValue();
					} else {
						val = null;
					}
					colVal.put(cellName, val);

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

				LOGGER.severe("row -- " + row_no + " colVal --- " + colVal);

				for (String module : moduleNames) {
					List<String> fields = new ArrayList(groupedFields.get(module));
					HashMap<String, Object> props = new LinkedHashMap<String, Object>();

					if (importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) || (importProcessContext.getModule().getExtendModule() != null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))) {
						Long spaceId = getSpaceID(importProcessContext, colVal, fieldMapping);
						props.put("purposeSpace", spaceId);
							
						props.put(ImportAPI.ImportProcessConstants.RESOURCE_TYPE, ResourceType.ASSET.getValue());
						
						lookupHolder = new HashMap<>();
						lookupHolder.put("id", spaceId);
						props.put(ImportAPI.ImportProcessConstants.SPACE_FIELD, lookupHolder);
						
						String siteName = (String) colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__site"));
						List<SiteContext> sites = SpaceAPI.getAllSites();
						for (SiteContext site : sites) {
							if (site.getName().trim().toLowerCase().equals(siteName.trim().toLowerCase())) {
								props.put("siteId", site.getId());
								break;
							}
						}

						colVal.remove(fieldMapping.get(importProcessContext.getModule().getName() + "__site"));
						colVal.remove(fieldMapping.get(importProcessContext.getModule().getName() + "__building"));
						colVal.remove(fieldMapping.get(importProcessContext.getModule().getName() + "__floor"));
						colVal.remove(fieldMapping.get(importProcessContext.getModule().getName() + "__spaceName"));
					}

					else if (importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.SPACE)) {
					
						
							String moduleName = importProcessContext.getModule().getName();
							ArrayList<String> spaceFields = new ArrayList<>();
							spaceFields.add(moduleName+"__site");
							spaceFields.add(moduleName+"__building");
							spaceFields.add(moduleName+"__floor");
							
							String [] Ids = {"site","building","floor"};
							
							for(int k =0 ;k<3 ;k++) {
								HashMap<String, Object> sendToSpaceID = new HashMap<>();
								for(int z=0 ;z<k+1;z++) {
								sendToSpaceID.put(fieldMapping.get(spaceFields.get(z)), colVal.get(fieldMapping.get(spaceFields.get(z))));
								}
								Long id = getSpaceID(importProcessContext, sendToSpaceID, fieldMapping);
								lookupHolder = new HashMap<>();
								lookupHolder.put("id", id);
								props.put(Ids[k],lookupHolder);
							}
							props.put("spaceType",BaseSpaceContext.SpaceType.SPACE.getIntVal());
							props.put("resourceType", ResourceType.SPACE.getValue());
				
					}

					if (importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.BUILDING)) {
					
						Long buildingId = getSpaceID(importProcessContext,colVal, fieldMapping);
						lookupHolder = new HashMap<>();
						lookupHolder.put("id", buildingId);
						props.put(ImportAPI.ImportProcessConstants.SITE_ID_FIELD,lookupHolder);
						props.put("resourceType", ResourceType.SPACE.getValue());
						props.put("spaceType", BaseSpaceContext.SpaceType.BUILDING.getIntVal());
						
						importProcessContext.getModule().getName();
					
					}

					else if (importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.FLOOR)) {
						
						Long floorId = getSpaceID(importProcessContext,colVal, fieldMapping);
						lookupHolder = new HashMap<>();
						lookupHolder.put("id", floorId);
						props.put(ImportAPI.ImportProcessConstants.BUILDING_ID_FIELD,lookupHolder);
						props.put("resourceType", ResourceType.SPACE.getValue());
						props.put("spaceType", BaseSpaceContext.SpaceType.FLOOR.getIntVal());
					
					
					} 
					else if (importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.SITE)) {
						
						props.put("resourceType", ResourceType.SPACE.getValue());
						props.put("spaceType",BaseSpaceContext.SpaceType.SITE.getIntVal());
					
					}
					LOGGER.severe("\n\n importProcessContext.getFacilioFieldMapping() --- "
							+ importProcessContext.getFacilioFieldMapping());

					LOGGER.severe("\n\n colVal --- " + colVal);

						for(int fieldIndex = 0; fieldIndex < modulesPlusFields.size(); fieldIndex++) {
						String key = modulesPlusFields.get(fieldIndex);
						String[] fieldAndModule = key.split("__");
						String field = fieldAndModule[(fieldAndModule.length) - 1];
						if (fields.contains(field)) {
							Object cellValue = colVal.get(fieldMapping.get(key));
							boolean isfilledByLookup = false;

							if (cellValue != null && !cellValue.toString().equals("")) {

								FacilioField facilioField = null;
								try {
									ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
									facilioField = bean.getField(field, module);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									log.info("Exception occurred ", e);
								}

								if (facilioField != null) {
									if (facilioField.getDataTypeEnum().equals(FieldType.LOOKUP)
											&& facilioField instanceof LookupField) {
										LookupField lookupField = (LookupField) facilioField;

										boolean isSkipSpecialLookup = false;

										try {
											if ((importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) || (importProcessContext.getModule().getExtendModule() != null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET)))
													&& lookupField.getName().equals("department")) {
												isSkipSpecialLookup = true;
											}
										} catch (Exception e) {
											e.printStackTrace();
										}

										if (facilioField.getDisplayType().equals(FacilioField.FieldDisplayType.LOOKUP_SIMPLE) || isSkipSpecialLookup) {
											List<Map<String, Object>> lookupPropsList = getLookupProps(lookupField,cellValue);
											if (lookupPropsList != null) {
												Map<String, Object> lookupProps = lookupPropsList.get(0);
												if (!props.containsKey(field)) {
													props.put(field, lookupProps);
												}
												isfilledByLookup = true;

											}
										} 
										else if (facilioField.getDisplayType().equals(FacilioField.FieldDisplayType.LOOKUP_POPUP)) {
											Map<String, Object> specialLookupList = getSpecialLookupProps(lookupField,cellValue);
											if (specialLookupList != null) {
												if (!props.containsKey(field)) {
													props.put(field, specialLookupList);
												}
												isfilledByLookup = true;
											}
										}
									}

									try {
										if (facilioField.getDataTypeEnum().equals(FieldType.DATE_TIME) || facilioField.getDataTypeEnum().equals(FieldType.DATE)) {
											if (!(cellValue instanceof Long)) {
												long millis;
												if(dateFormats.get(fieldMapping.get(key)).equals(ImportAPI.ImportProcessConstants.TIME_STAMP_STRING)) {
													millis = Long.parseLong(cellValue.toString());
												}
												else {
													Instant dateInstant = DateTimeUtil.getTimeInstant(dateFormats.get(fieldMapping.get(key)).toString(),cellValue.toString());
													millis = dateInstant.toEpochMilli();
												}
//												long millis = DateTimeUtil.getTime(cellValue.toString(),"dd/MM/yyyy HH:mm:ss");
												if (!props.containsKey(field)) {
													props.put(field, millis);
												}
												isfilledByLookup = true;
											}
										}
										else if(facilioField.getDataTypeEnum().equals(FieldType.NUMBER) || facilioField.getDataTypeEnum().equals(FieldType.DECIMAL)) {
											String cellValueString = cellValue.toString();
											if(cellValueString.contains(",")) {
												cellValueString = cellValueString.replaceAll(",", "");
											}
											Double cellDoubleValue = Double.parseDouble(cellValueString);
											if(!props.containsKey(field)) {
												props.put(field, cellDoubleValue);
											}
											isfilledByLookup = true;
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										throw new ImportParseException(row_no, fieldMapping.get(key), e);
									}
								}
							}

							if (!isfilledByLookup) {
								if (!props.containsKey(field)) {
									props.put(field, cellValue);
								}
							}
						} else {
							continue;
						}
					}

					LOGGER.severe("props -- " + props);
					ReadingContext reading = FieldUtil.getAsBeanFromMap(props, ReadingContext.class);
					readingsList.add(reading);
					groupedContext.put(module, reading);

				}
			}
		}
		c.put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
		c.put(ImportAPI.ImportProcessConstants.FIELDS_MAPPING, fieldMapping);
		c.put(ImportAPI.ImportProcessConstants.GROUPED_FIELDS, groupedFields);
		c.put(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT, groupedContext);
		c.put(ImportAPI.ImportProcessConstants.READINGS_LIST, readingsList);
		c.put(FacilioConstants.ContextNames.RECORD_LIST, recordsList);
		
		LOGGER.severe(groupedContext.toString());
		return false;
	}

	public static List<Map<String, Object>> getLookupProps(LookupField lookupField, Object value) {

		try {
			LOGGER.severe("getLookupProps -- " + lookupField.getColumnName() + " facilioField.getModule() - "
					+ lookupField.getLookupModule().getTableName() + " with value -- " + value);

			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			ArrayList<FacilioField> fieldsList = new ArrayList<FacilioField>();

			fieldsList = new ArrayList<>(bean.getAllFields(lookupField.getLookupModule().getName()));
			fieldsList.add(FieldFactory.getIdField(lookupField.getLookupModule()));
			fieldsList.add(FieldFactory.getOrgIdField(lookupField.getLookupModule()));
			fieldsList.add(FieldFactory.getModuleIdField(lookupField.getLookupModule()));

			String collumnName = "NAME";
			String fieldName = "name";
			if (lookupField.getModule().getName().equals(FacilioConstants.ContextNames.WORK_ORDER)) {
				if (lookupField.getName().equals("status")) {
					collumnName = "STATUS";
					fieldName = "status";
				} else if (lookupField.getName().equals("priority")) {
					collumnName = "PRIORITY";
					fieldName = "priority";
				}
			}
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fieldsList)
					.table(lookupField.getLookupModule().getTableName())
					.andCustomWhere("LOWER(" + collumnName + ") = ?", value.toString().toLowerCase())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(lookupField.getLookupModule()));

			List<Map<String, Object>> props = selectBuilder.get();

			if (props.isEmpty()) {

				HashMap<String, Object> insertProps = new LinkedHashMap<String, Object>();

				insertProps.put(fieldName, value);
				insertProps.put("orgId", AccountUtil.getCurrentOrg().getId());
				insertProps.put("moduleId", lookupField.getLookupModule().getModuleId());

				if (lookupField.getLookupModule().getName().equals(FacilioConstants.ContextNames.ASSET_CATEGORY)) {
					insertProps.put("type", AssetCategoryContext.AssetCategoryType.MISC.getIntVal());
				}
				GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
						.table(lookupField.getLookupModule().getTableName()).fields(fieldsList);

				insertRecordBuilder.addRecord(insertProps);

				insertRecordBuilder.save();
				LOGGER.severe("insertProps --- " + insertProps);
				Long id = (Long) insertProps.get("id");

				if (id != null) {
					LOGGER.severe("inserted with ID --" + id);
					props.add(insertProps);
				}
			}
			return props;
		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return null;
	}

	public static Map<String, Object> getSpecialLookupProps(LookupField lookupField, Object value) {

		try {

			switch (lookupField.getLookupModule().getName()) {
			case "workorder": {
				User user = AccountUtil.getUserBean().getFacilioUser(value.toString());
				Map<String, Object> prop = FieldUtil.getAsProperties(user);
				return prop;

			}
			case "asset": {
				long assetid = AssetsAPI.getAssetId(value.toString(), lookupField.getOrgId());
				AssetContext asset = AssetsAPI.getAssetInfo(assetid);

				Map<String, Object> prop1 = FieldUtil.getAsProperties(asset);
				return prop1;
			}
			case "site": {
				long siteId = new ImportSiteAction().getSiteId(value.toString());
				SiteContext site = SpaceAPI.getSiteSpace(siteId);
				Map<String, Object> prop2 = FieldUtil.getAsProperties(site);
				return prop2;
			}
			case "building": {
//				SpaceAPI.getBaseSpace(value);
//				User user = AccountUtil.getUserBean().getFacilioUser(value.toString());
//				Map<String, Object> prop = FieldUtil.getAsProperties(user);
//				return prop;
			}
			}

		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return null;
	}

	public void fieldMapParsing(HashMap<String, String> fieldMapping) throws Exception {

		groupedFields = ArrayListMultimap.create();
		new HashMap<>();
		List<String> fieldMappingKeys = new ArrayList<>(fieldMapping.keySet());
		for (int i = 0; i < fieldMappingKeys.size(); i++) {
			String[] modulePlusFieldName = fieldMappingKeys.get(i).split("__");
			ArrayList<String> moduleNameList = new ArrayList(Arrays.asList(modulePlusFieldName));

			String fieldName = moduleNameList.get((moduleNameList.size()) - 1);
			moduleNameList.remove((moduleNameList.size() - 1));
			String moduleName;
			if (moduleNameList.size() == 1) {
				moduleName = moduleNameList.get(0);
			} else {
				moduleName = String.join("_", moduleNameList);
			}
			groupedFields.put(moduleName, fieldName);
		}
		if (groupedFields.containsKey("sys")) {
			List<String> sys = new ArrayList(groupedFields.get("sys"));
			groupedFields.removeAll("sys");
			for (String module : groupedFields.keySet()) {
				for (int i = 0; i < sys.size(); i++) {
					groupedFields.put(module, sys.get(i));
				}
			}

		}
	}
	
	public static Long getSpaceID(ImportProcessContext importProcessContext, HashMap<String, Object> colVal, HashMap<String,String> fieldMapping) throws Exception {

		String siteName =null ,buildingName = null,floorName = null ,spaceName = null;
		
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
				 recordsList.put("site", siteId);
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
				 recordsList.put("building", buildingId);
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
		    	recordsList.put("floor", floorId);
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
					 spaceId = spaceMeta.addSpace(spaceName, siteId, buildingId);
				 }
				 else
				 {
				 spaceId = spaceMeta.addSpace(spaceName, siteId, buildingId, floorId);
				 }
				 recordsList.put("space", spaceId);
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
							spaceId = SpaceAPI.addDependentSpace(additionalSpace, spaceId);
							recordsList.put("space", spaceId);
						}
					}
			}
		return spaceId;
	
	}
}