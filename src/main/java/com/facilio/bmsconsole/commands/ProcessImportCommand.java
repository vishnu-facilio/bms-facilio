package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.*;
import com.facilio.bmsconsole.actions.ImportProcessContext.ImportSetting;
import com.facilio.bmsconsole.commands.data.ProcessXLS;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

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
		Map<String, Long> lookupHolder;
		String module = importProcessContext.getModule().getName().toString();
		
		JSONObject importMeta = importProcessContext.getImportJobMetaJson();
		JSONObject dateFormats = (JSONObject) importMeta.get(ImportAPI.ImportProcessConstants.DATE_FORMATS);

		HashMap<Integer, String> headerIndex = new HashMap<Integer, String>();
		List<Map<String, Object>> allRows = ImportAPI.getValidatedRows(importProcessContext.getId());

		for(Map<String, Object> row: allRows) {
			ImportProcessLogContext rowLogContext = FieldUtil.getAsBeanFromMap(row, ImportProcessLogContext.class);
			ImportRowContext rowContext = new ImportRowContext();
			
			if(rowLogContext.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getValue()){
				rowContext = rowLogContext.getRowContexts().get(0);
			}
			else if(rowLogContext.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.RESOLVED.getValue()) {
				rowContext = rowLogContext.getCorrectedRow();
			}	
			
			int row_no = rowContext.getRowNumber();
			HashMap<String, Object> colVal = rowContext.getColVal();
			
				LOGGER.severe("row -- " + row_no + " colVal --- " + colVal);

					HashMap<String, Object> props = new LinkedHashMap<String, Object>();

					if (importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) || (importProcessContext.getModule().getExtendModule() != null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))) {
						Long spaceId = getSpaceID(importProcessContext, colVal, fieldMapping);
						if(spaceId != null) {
							props.put("purposeSpace", spaceId);
							
							lookupHolder = new HashMap<>();
							lookupHolder.put("id", spaceId);
							props.put(ImportAPI.ImportProcessConstants.SPACE_FIELD, lookupHolder);
						}
						
						props.put(ImportAPI.ImportProcessConstants.RESOURCE_TYPE, ResourceType.ASSET.getValue());
						
						String siteName = (String) colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__site"));
						
						if(!(importProcessContext.getImportSetting() == ImportSetting.UPDATE.getValue() || importProcessContext.getImportSetting() == ImportSetting.UPDATE_NOT_NULL.getValue())) {
							List<SiteContext> sites = SpaceAPI.getAllSites();
							for (SiteContext site : sites) {
								if (site.getName().trim().toLowerCase().equals(siteName.trim().toLowerCase())) {
									props.put("siteId", site.getId());
									break;
								}
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

					else if (importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.BUILDING)) {
					
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

//						for(int fieldIndex = 0; fieldIndex < modulesPlusFields.size(); fieldIndex++) {
//						String key = modulesPlusFields.get(fieldIndex);
//						String[] fieldAndModule = key.split("__");
//						String field = fieldAndModule[(fieldAndModule.length) - 1];
//						String fieldModule = fieldAndModule[0];
						
						List<FacilioField> fields = new ArrayList<FacilioField>();
						try {
							ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
							fields = bean.getAllFields(importProcessContext.getModule().getName());
						}catch(Exception e) {
							LOGGER.severe("Cannot get fields for module" + e.getMessage());
						}
						
						for(FacilioField facilioField: fields)
						{
							String key = facilioField.getModule().getName() + "__" + facilioField.getName();
							Object cellValue = colVal.get(fieldMapping.get(key));
							boolean isfilledByLookup = false;

							if (cellValue != null && !cellValue.toString().equals("") && facilioField.getDataType() != FieldType.LOOKUP.getTypeAsInt()) {
//								try {
//									ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//									facilioField = bean.getField(field, fieldModule);
//								} catch (Exception e) {
//									// TODO Auto-generated catch block
//									log.info("Exception occurred ", e);
//								}
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
												if (!props.containsKey(facilioField.getName())) {
													props.put(facilioField.getName(), millis);
												}
												isfilledByLookup = true;
											}
										}
										else if(facilioField.getDataType() == FieldType.ENUM.getTypeAsInt()) {
											EnumField enumField = (EnumField) facilioField;
											String enumString = (String)colVal.get(fieldMapping.get(key));
											int enumIndex = enumField.getIndex(enumString.toLowerCase());
											
											if(!props.containsKey(facilioField.getName())) {
												props.put(facilioField.getName(), enumIndex);
											}
										}
										else if(facilioField.getDataTypeEnum().equals(FieldType.NUMBER) || facilioField.getDataTypeEnum().equals(FieldType.DECIMAL)) {
											String cellValueString = cellValue.toString();
											if(cellValueString.contains(",")) {
												cellValueString = cellValueString.replaceAll(",", "");
											}
											
											Double cellDoubleValue = Double.parseDouble(cellValueString);
											if(!props.containsKey(facilioField.getName())) {
												props.put(facilioField.getName(), cellDoubleValue);
											}
											isfilledByLookup = true;
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										throw new ImportParseException(row_no, fieldMapping.get(key), e);
									}
							}
							else if (facilioField.getDataTypeEnum().equals(FieldType.LOOKUP) && facilioField instanceof LookupField) {
								LookupField lookupField = (LookupField) facilioField;

								boolean isSkipSpecialLookup = false;

								try {
									if ((importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) || (importProcessContext.getModule().getExtendModule() != null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))) && lookupField.getName().equals("department")) {
										isSkipSpecialLookup = true;
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

								if (facilioField.getDisplayType().equals(FacilioField.FieldDisplayType.LOOKUP_SIMPLE) || isSkipSpecialLookup) {
									List<Map<String, Object>> lookupPropsList = getLookupProps(lookupField,colVal, fieldMapping.get(key), importProcessContext);
									if (lookupPropsList != null) {
										Map<String, Object> lookupProps = lookupPropsList.get(0);
										if (!props.containsKey(facilioField.getName())) {
											props.put(facilioField.getName(), lookupProps);
										}
										isfilledByLookup = true;

									}
								} 
								else if (facilioField.getDisplayType().equals(FacilioField.FieldDisplayType.LOOKUP_POPUP)) {
									Map<String, Object> specialLookupList = getSpecialLookupProps(lookupField,cellValue);
									if (specialLookupList != null) {
										if (!props.containsKey(facilioField.getName())) {
											props.put(facilioField.getName(), specialLookupList);
										}
										isfilledByLookup = true;
									}
								}
							}

							if (!isfilledByLookup) {
								if (!props.containsKey(facilioField.getName())) {
									props.put(facilioField.getName(), cellValue);
								}
							}
					}

					LOGGER.severe("props -- " + props);
					ReadingContext reading = FieldUtil.getAsBeanFromMap(props, ReadingContext.class);
					readingsList.add(reading);
					groupedContext.put(module, reading);	
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

	public static List<Map<String, Object>> getLookupProps(LookupField lookupField, HashMap<String, Object> colVal, String key, ImportProcessContext importProcessContext) {

		try {

			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if(isNewLookupFormat(importProcessContext)) {
				List<Map<String, Object>> lookupFieldList = new ArrayList<Map<String,Object>>();
				List<FacilioField> lookupModuleFields = bean.getAllFields(lookupField.getLookupModule().getName());
				HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();
				Map<String, Object> lookupFieldMap = new HashMap<String, Object>();
				for(FacilioField field : lookupModuleFields) {
					
					if(fieldMapping.containsKey(field.getModule().getName() + "__" + field.getName())) {
						if(field.getDataType() == FieldType.ENUM.getTypeAsInt()) {
							EnumField enumField = (EnumField) field;
							String enumString = (String)colVal.get(fieldMapping.get(field.getModule().getName() + "__" + field.getName()));
							int enumIndex = enumField.getIndex(enumString.toLowerCase());
							lookupFieldMap.put(field.getName(),enumIndex);
						}
						else if(field.getDataType() == FieldType.LOOKUP.getTypeAsInt()) {
							// special handling for tool and items. Their types have a second lookup
							if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.PURCHASED_ITEM) 
									|| importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.PURCHASED_TOOL)) {
								Map<String, Object> secondLookup = new HashMap<String, Object>();
								secondLookup.put("name", (String)colVal.get(fieldMapping.get(field.getModule().getName() + "__" + field.getName())));
								lookupFieldMap.put(field.getName(), secondLookup);
							}
						}
						else {
							lookupFieldMap.put(field.getName(), colVal.get(fieldMapping.get(field.getModule().getName() + "__" + field.getName())));
						}
						
					}
				}
				lookupFieldList.add(lookupFieldMap);
				return lookupFieldList;
			}
			else {
				Object value = colVal.get(key);
				LOGGER.severe("getLookupProps -- " + lookupField.getColumnName() + " facilioField.getModule() - "
						+ lookupField.getLookupModule().getTableName() + " with value -- " + value);

				
				ArrayList<FacilioField> fieldsList = new ArrayList<FacilioField>();

				fieldsList = new ArrayList<>(bean.getAllFields(lookupField.getLookupModule().getName()));
				fieldsList.add(FieldFactory.getIdField(lookupField.getLookupModule()));
				/*fieldsList.add(FieldFactory.getOrgIdField(lookupField.getLookupModule()));*/
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
						FacilioModule module = new FacilioModule();
						String name = value.toString();
						
						ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						FacilioModule assetModule = modBean.getModule("asset");
						
						module.setName(name.toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
						module.setDisplayName(name);
						module.setTableName("AssetCustomModuleData");
						module.setType(ModuleType.BASE_ENTITY);
						module.setExtendModule(assetModule);
						
						FacilioContext context = new FacilioContext();
						context.put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
						
						Chain addCategory = FacilioChain.getTransactionChain();
						TransactionChainFactory.commonAddModuleChain(addCategory);
						
						addCategory.execute(context);
						
						FacilioModule AssetModuleId = (FacilioModule) ((List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST)).get(0);
						insertProps.put("type", AssetCategoryContext.AssetCategoryType.MISC.getIntVal());
						insertProps.put("assetModuleID", AssetModuleId.getModuleId());
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
			}
			
			
		} catch (Exception e) {
			LOGGER.severe("Exception occurred at lookup field");
			LOGGER.severe(e.toString());
		}
		return null;
	}

	public static boolean isNewLookupFormat(ImportProcessContext importProcessContext) throws Exception{
		boolean isNewFormat = false;
		List<String> moduleList = new ArrayList<String>();
		
		moduleList.add(FacilioConstants.ContextNames.PURCHASED_ITEM);
		moduleList.add(FacilioConstants.ContextNames.PURCHASED_TOOL);
		
		if(moduleList.contains(importProcessContext.getModule().getName())) {
			isNewFormat = true;
		}
		return isNewFormat;
		
	}
	public static Map<String, Object> getSpecialLookupProps(LookupField lookupField, Object value) {

		try {
			String moduleName;
			if(lookupField.getLookupModule() == null && lookupField.getSpecialType() != null) {
				moduleName = lookupField.getSpecialType();
			}
			else {
				moduleName = lookupField.getLookupModule().getName();
			}
			switch (moduleName) {
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
			case "resource": {
				ResourceContext resource = getResource(value.toString());
				Map<String, Object> prop2 = FieldUtil.getAsProperties(resource);
				return prop2;
			}
			case "users": {
				User user = AccountUtil.getUserBean().getUserFromEmail(value.toString());
				if(user == null) {
					user = new User();
					user.setEmail(value.toString());
					AccountUtil.getUserBean().addRequester(AccountUtil.getCurrentOrg().getId(), user);
				}
				Map<String, Object> prop = FieldUtil.getAsProperties(user);
				return prop;
			}
			case "groups": {
				Group group = AccountUtil.getGroupBean().getGroup(value.toString());
				Map<String, Object> prop = FieldUtil.getAsProperties(group);
				return prop;
			}
			}

		} catch (Exception e) {
			LOGGER.severe("Exception occured for special lookup");
			LOGGER.severe(e.toString());
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
	
	public static ResourceContext getResource(String name) throws Exception {
		boolean fetchDeleted = false;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		SelectRecordsBuilder<ResourceContext> resourceBuilder = new SelectRecordsBuilder<ResourceContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE))
																		.module(module)
																		.beanClass(ResourceContext.class)
																		.andCustomWhere("NAME = ?",name)
																		;
		if (fetchDeleted) {
			resourceBuilder.fetchDeleted();
		}
		
		List<ResourceContext> resources = resourceBuilder.get();
		if(resources != null && !resources.isEmpty()) {
			return resources.get(0);
		}
		return null;									
	}
}