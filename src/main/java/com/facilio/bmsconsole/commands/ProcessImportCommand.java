package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.*;
import com.facilio.bmsconsole.actions.ImportProcessContext.ImportSetting;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportFieldValueMissingException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportLookupModuleValueNotFoundException;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.modules.*;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.time.DateTimeUtil;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

public class ProcessImportCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(ProcessImportCommand.class.getName());
	private static ArrayListMultimap<String, String> groupedFields;
	private static ArrayListMultimap<String, Long> recordsList;

	@Override
	public boolean executeCommand(Context c) throws Exception {

		HashMap<String, List<ReadingContext>> groupedContext = new HashMap<String, List<ReadingContext>>();
		recordsList = ArrayListMultimap.create();

		ImportProcessContext importProcessContext = (ImportProcessContext) c.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();

		FacilioModule bimModule = ModuleFactory.getBimImportProcessMappingModule();
		List<FacilioField> bimFields = FieldFactory.getBimImportProcessMappingFields();
		String module = "";
		BimImportProcessMappingContext bimImport = BimAPI.getBimImportProcessMappingByImportProcessId(bimModule,bimFields,importProcessContext.getId());
		HashMap<String, Object> bimDefaultValuesMap = new LinkedHashMap();
		boolean isBim = (bimImport!=null);
		if(isBim){
			if(importProcessContext.getModule() == null){
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(importProcessContext.getImportJobMeta());
				module = ((JSONObject)json.get("moduleInfo")).get("module").toString();
			}else{
				bimDefaultValuesMap = BimAPI.getBimDefaultValues(bimImport.getBimId(),importProcessContext.getModule().getName());
			}

		}
		if(importProcessContext.getModule()!=null){
			module = importProcessContext.getModule().getName();
		}

		fieldMapParsing(fieldMapping);
		Map<String, Long> lookupHolder;

		JSONObject importMeta = importProcessContext.getImportJobMetaJson();
		JSONObject dateFormats = (JSONObject) importMeta.get(ImportAPI.ImportProcessConstants.DATE_FORMATS);

		List<Map<String, Object>> allRows = ImportAPI.getValidatedRows(importProcessContext.getId());

		boolean isNewLookupFormat = isNewLookupFormat(importProcessContext);
		for(Map<String, Object> row: allRows) {
			ImportProcessLogContext rowLogContext = FieldUtil.getAsBeanFromMap(row, ImportProcessLogContext.class);
			ImportRowContext rowContext;

			if(rowLogContext.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getValue()){
				rowContext = rowLogContext.getRowContexts().get(0);
			}
			else if(rowLogContext.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.RESOLVED.getValue()) {
				rowContext = rowLogContext.getCorrectedRow();
			} else {
				continue;
			}

			int row_no = rowContext.getRowNumber();
			HashMap<String, Object> colVal = rowContext.getColVal();

				LOGGER.info("row -- " + row_no + " colVal --- " + colVal);

					HashMap<String, Object> props = new LinkedHashMap<String, Object>();

					if(!bimDefaultValuesMap.isEmpty()){
						props.putAll(bimDefaultValuesMap);
					}

					if (importProcessContext.getModule() != null && ImportAPI.isResourceExtendedModule(importProcessContext.getModule())) {
						// Setting Source Type and Source Id here for Resource extended Module
						props.put(FacilioConstants.ContextNames.SOURCE_TYPE, SourceType.IMPORT.getIndex());
						props.put(FacilioConstants.ContextNames.SOURCE_ID, importProcessContext.getId());
					}

					if ((importProcessContext.getModule()!=null && importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET)) || (importProcessContext.getModule()!=null && importProcessContext.getModule().getExtendModule() != null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))) {
						if(ImportAPI.canUpdateAssetBaseSpace(importProcessContext)) {
							Long spaceId = getSpaceID(importProcessContext, colVal, fieldMapping, row_no, true);
							props.put("purposeSpace", spaceId);

							lookupHolder = new HashMap<>();
							lookupHolder.put("id", spaceId);
							props.put("purposeSpace", lookupHolder);
							props.put(ImportAPI.ImportProcessConstants.SPACE_FIELD, lookupHolder);
						}

						props.put(ImportAPI.ImportProcessConstants.RESOURCE_TYPE, ResourceType.ASSET.getValue());

						Long siteId = importProcessContext.getSiteId();

						if(!(importProcessContext.getImportSetting() == ImportSetting.UPDATE.getValue() || importProcessContext.getImportSetting() == ImportSetting.UPDATE_NOT_NULL.getValue())) {
							if(!isBim){
								props.put("siteId", siteId);
							}else{
								props.put("siteId", Long.parseLong(props.get("site").toString()));
								props.remove("site");
							}
						}
						colVal.remove(fieldMapping.get(importProcessContext.getModule().getName() + "__site"));
						colVal.remove(fieldMapping.get(importProcessContext.getModule().getName() + "__building"));
						colVal.remove(fieldMapping.get(importProcessContext.getModule().getName() + "__floor"));
						colVal.remove(fieldMapping.get(importProcessContext.getModule().getName() + "__spaceName"));

						if (!ImportAPI.isInsertImport(importProcessContext)) {
							if (fieldMapping.containsKey(importProcessContext.getModule().getName() + "__id") && colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__id")) != null) {
								Object idValue = colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__id"));

								if (idValue != null && StringUtils.isNotEmpty(idValue.toString())) {
									props.put("id", (long) Double.parseDouble(idValue.toString()));
								}
							}
						}
					}

					else if (module.equals(FacilioConstants.ContextNames.SPACE) || module.equals(FacilioConstants.ContextNames.TENANT_UNIT_SPACE)) {

						if(!isBim){
							String moduleName = module;
							ArrayList<String> spaceFields = new ArrayList<>();
							spaceFields.add("basespace__site");
							spaceFields.add(moduleName+"__building");
							spaceFields.add(moduleName+"__floor");
							spaceFields.add(moduleName + "__space1");
							spaceFields.add(moduleName + "__space2");
							spaceFields.add(moduleName + "__space3");
							spaceFields.add(moduleName + "__space4");
							String name = (String) colVal.get(fieldMapping.get("resource__name"));

							String [] Ids = {"site","building","floor", "space1", "space2", "space3", "space4"};
							SpaceCategoryContext tenantUnitSpaceCategory = SpaceAPI.getSpaceCategoryContext("Tenant Unit");

							for(int k =0 ;k<Ids.length ;k++) {
								HashMap<String, Object> sendToSpaceID = new HashMap<>();
								for(int z=0 ;z<k+1;z++) {
									if(colVal.get(fieldMapping.get(spaceFields.get(z))) != null && colVal.get(fieldMapping.get(spaceFields.get(z))) != "") {
										sendToSpaceID.put(fieldMapping.get(spaceFields.get(z)), colVal.get(fieldMapping.get(spaceFields.get(z))));
									}
									else {
										continue;
									}
								}
								if(!sendToSpaceID.containsKey(fieldMapping.get(spaceFields.get(k)))) {
									continue;
								}
								Long id = getSpaceID(importProcessContext, sendToSpaceID, fieldMapping, row_no,true);
								if(Ids[k].contains("space")) {
									props.put(Ids[k], id);
								}
								else {
									lookupHolder = new HashMap<>();
									lookupHolder.put("id", id);
									if(Ids[k].equals(FacilioConstants.ContextNames.SITE)) {
										props.put(FacilioConstants.ContextNames.SITE_ID, id);
									}
									props.put(Ids[k],lookupHolder);
								}
							}

							if (importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.TENANT_UNIT_SPACE)) {
								props.put(FacilioConstants.ContextNames.SPACE_CATEGORY_FIELD, tenantUnitSpaceCategory);
								SpaceContext space = SpaceAPI.getSpaceFromHierarchy(props, name);
								if (space != null && !(space.getSpaceCategory() != null  && space.getSpaceCategory().getId() == tenantUnitSpaceCategory.getId())) {
									SpaceAPI.addTenantUnitExtentedModuleEntry(space);
								}
							}
						}else{
							Long siteId = Long.parseLong(props.get("site").toString());
							ResourceContext site = SpaceAPI.getSiteSpace(siteId);
							fieldMapping.put("space__site", "SiteName");
							colVal.put("SiteName", site.getName());

							Long buildingId = Long.parseLong(props.get("building").toString());
							ResourceContext building = SpaceAPI.getBuildingSpace(buildingId);
							fieldMapping.put("space__building", "BuildingName");
							colVal.put("BuildingName", building.getName());

							Long floorId = getSpaceID(importProcessContext,colVal, fieldMapping, row_no, true);
							props.put("floor", floorId);
							props.put("siteId", Long.parseLong(props.get("site").toString()));
						}
							props.put("spaceType",BaseSpaceContext.SpaceType.SPACE.getIntVal());
							props.put("resourceType", ResourceType.SPACE.getValue());

					}

					else if (module.equals(FacilioConstants.ContextNames.BUILDING)) {
						long buildingId = -1;
						if(!isBim){
							buildingId = getSpaceID(importProcessContext,colVal, fieldMapping, row_no, true);
						}else{
							buildingId = Long.parseLong(props.get("site").toString());
						}

						lookupHolder = new HashMap<>();
						lookupHolder.put("id", buildingId);
						props.put("siteId", buildingId);
						props.put(ImportAPI.ImportProcessConstants.SITE_ID_FIELD,lookupHolder);
						props.put("resourceType", ResourceType.SPACE.getValue());
						props.put("spaceType", BaseSpaceContext.SpaceType.BUILDING.getIntVal());

					}

					else if (module.equals(FacilioConstants.ContextNames.FLOOR)) {
						HashMap<String, Object> col= new HashMap<String, Object>();
						col.put(
								fieldMapping.get(module + "__site"),
								colVal.get(fieldMapping.get(module+ "__site")));

						Long siteId = getSpaceID(importProcessContext, col, fieldMapping, row_no, true);
						props.put("siteId", siteId);

						if(!isBim){
							Long floorId = getSpaceID(importProcessContext,colVal, fieldMapping,row_no, true);
							lookupHolder = new HashMap<>();
							lookupHolder.put("id", floorId);
							props.put(ImportAPI.ImportProcessConstants.BUILDING_ID_FIELD,lookupHolder);
							props.put("buildingId", floorId);
						}else{
							props.put("siteId", Long.parseLong(props.get("site").toString()));
							lookupHolder = new HashMap<>();
							lookupHolder.put("id", Long.parseLong(props.get("siteId").toString()));
							props.put("site",lookupHolder);

							props.put("buildingId", Long.parseLong(props.get("building").toString()));
							lookupHolder = new HashMap<>();
							lookupHolder.put("id", Long.parseLong(props.get("building").toString()));
							props.put(ImportAPI.ImportProcessConstants.BUILDING_ID_FIELD,lookupHolder);
						}

						props.put("resourceType", ResourceType.SPACE.getValue());
						props.put("spaceType", BaseSpaceContext.SpaceType.FLOOR.getIntVal());


					}
					else if (module.equals(FacilioConstants.ContextNames.SITE)) {

						props.put("resourceType", ResourceType.SPACE.getValue());
						props.put("spaceType",BaseSpaceContext.SpaceType.SITE.getIntVal());

					}
					else if(module.equals(FacilioConstants.ContextNames.ZONE)){
						props.remove("site");
						props.put("resourceType", ResourceType.SPACE.getValue());
						props.put("spaceType",BaseSpaceContext.SpaceType.ZONE.getIntVal());
					}else if(isBim && module.equals("zonespacerel")){

						if(colVal!=null &&  colVal.get(fieldMapping.get("resource"+"__"+"name")) !=null && !colVal.get(fieldMapping.get("resource"+"__"+"name")).toString().equals("n/a")){
							String zoneName= colVal.get(fieldMapping.get("resource"+"__"+"name")).toString();
							ZoneContext zone = SpaceAPI.getZone(zoneName);
							props.put("zoneId", zone.getId());

							String spaceName= colVal.get(fieldMapping.get(module+"__"+"space")).toString();
							HashMap<String, Object> defaultValueMap =BimAPI.getBimDefaultValues(bimImport.getBimId(),FacilioConstants.ContextNames.ZONE);
							Long siteId = Long.parseLong(defaultValueMap.get("site").toString());
							List<BaseSpaceContext> baseSpaces = SpaceAPI.getSiteChildren(siteId);
							BaseSpaceContext space = baseSpaces.stream().filter(s->s.getName().equals(spaceName)).findFirst().get();

							props.put("basespaceId", space.getId());
						}

					}else if(isBim && importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.CONTACT)){
						String nameStr = fieldMapping.get(FacilioConstants.ContextNames.CONTACT+"__"+"name");
						String[] colNames= nameStr.split("&&");
						StringBuffer name = new StringBuffer();
						for(int i=0;i<colNames.length;i++){
							if(colVal !=null && colVal.get(colNames[i])!=null && !colVal.get(colNames[i]).toString().equals("n/a")){
								name.append(colVal.get(colNames[i])).append(" ");
							}
						}
						props.put("name", name.toString());
					}
					else if (importProcessContext.getModule()!=null && importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.WORK_ORDER)) {

						Long siteId = importProcessContext.getSiteId();
						if (ImportAPI.canUpdateResource(importProcessContext)) {
							Long resourceId = getResourceId(importProcessContext, colVal, fieldMapping, siteId, row_no);
							lookupHolder = new HashMap<>();
							lookupHolder.put("id", resourceId);
							props.put("resource", lookupHolder);
						}
						if(!(importProcessContext.getImportSetting() == ImportSetting.UPDATE.getValue() || importProcessContext.getImportSetting() == ImportSetting.UPDATE_NOT_NULL.getValue())) {
							props.put("siteId", siteId);
						}
						colVal.remove(fieldMapping.get(importProcessContext.getModule().getName() + "__site"));
						colVal.remove(fieldMapping.get(importProcessContext.getModule().getName() + "__building"));
						colVal.remove(fieldMapping.get(importProcessContext.getModule().getName() + "__floor"));
						colVal.remove(fieldMapping.get(importProcessContext.getModule().getName() + "__spaceName"));
						colVal.remove(fieldMapping.get(importProcessContext.getModule().getName() + "__asset"));

					} else if (importProcessContext.getModule()!=null && importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.TASK)) {
						String sectionName = (String) colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__sectionId"));
						Object parentTicketId = colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__parentTicketId"));
						if (sectionName != null && StringUtils.isNotEmpty(sectionName) && parentTicketId != null && StringUtils.isNotEmpty(parentTicketId.toString())) {
							Criteria criteria = new Criteria();
							criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_TICKET_ID", "parentTicketId" , parentTicketId.toString(), NumberOperators.EQUALS));
							criteria.addAndCondition(CriteriaAPI.getCondition("NAME","name", sectionName.trim().replace(",", StringOperators.DELIMITED_COMMA), StringOperators.IS));
							List<TaskSectionContext> taskSection = TicketAPI.getTaskSections(criteria);
							if (taskSection != null && CollectionUtils.isNotEmpty(taskSection) && taskSection.size() > 0) {
								props.put("sectionId", taskSection.get(0).getId());
							} else {
								GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
										.table(ModuleFactory.getTaskSectionModule().getTableName())
										.fields(FieldFactory.getTaskSectionFields())
										;
								TaskSectionContext section = new TaskSectionContext();
								section.setParentTicketId((long) Double.parseDouble(parentTicketId.toString()));
								section.setName(sectionName);
								section.setPreRequest(Boolean.FALSE);
								Map<Long, TaskSectionContext> taskSections = TicketAPI.getRelatedTaskSections((long) Double.parseDouble(parentTicketId.toString()));
								if (taskSections != null && taskSections.size() > 0) {
									section.setSequenceNumber(taskSections.size() + 1);
								} else {
									section.setSequenceNumber(1);
								}
								long sectionId = insertBuilder.insert(FieldUtil.getAsProperties(section));
								props.put("sectionId", sectionId);
							}
						}
						colVal.remove(fieldMapping.get(importProcessContext.getModule().getName() + "__sectionId"));
					}

					if (module != null) {
						ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						FacilioModule moduleObj = modBean.getModule(module);
						if(isBim && module.equals("zonespacerel")){
							moduleObj = ModuleFactory.getZoneRelModule();
						}

						if (moduleObj!=null && moduleObj.isStateFlowEnabled()) {
							props.put(FacilioConstants.ContextNames.STATE_FLOW_ID, StateFlowRulesAPI.getDefaultStateFlow(moduleObj).getId());
						} else if (ImportAPI.isAssetBaseModule(importProcessContext.getModule())) {
							StateFlowRuleContext stateFlow = StateFlowRulesAPI.getDefaultStateFlow(modBean.getModule(FacilioConstants.ContextNames.ASSET));
							if (stateFlow != null) {
								props.put(FacilioConstants.ContextNames.STATE_FLOW_ID, stateFlow.getId());
								if (ImportAPI.isInsertImport(importProcessContext)) {
									if (!fieldMapping.containsKey("asset__moduleState")) {
										lookupHolder = new HashMap<>();
										lookupHolder.put("id", stateFlow.getDefaultStateId());
										props.put("moduleState", lookupHolder);
									}
								}
							}
						} else if (module.equals(FacilioConstants.ContextNames.WORK_ORDER) && ImportAPI.isInsertImport(importProcessContext)) {
							props.put(FacilioConstants.ContextNames.STATE_FLOW_ID, StateFlowRulesAPI.getDefaultStateFlow(moduleObj).getId());
						}
						if (colVal.containsKey(fieldMapping.get(module + "__site")))
						{
							String siteName = (String) colVal.get(fieldMapping.get(module + "__site"));
							if (!(importProcessContext.getImportSetting() == ImportSetting.UPDATE.getValue() || importProcessContext.getImportSetting() == ImportSetting.UPDATE_NOT_NULL.getValue())) {
								List<SiteContext> sites = SpaceAPI.getAllSites();
								for (SiteContext site : sites) {
									if (!props.containsKey("siteId") && site.getName().trim().toLowerCase().equals(siteName.trim().toLowerCase())) {
										props.put("siteId", site.getId());
										break;
									}
								}
							}
						}
					}
						List<FacilioField> fields;
						try {
							ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
							String name = module;
							if(isBim && module.equals("zonespacerel")){
								fields = FieldFactory.getZoneRelFields();
							}else{
								fields = bean.getAllFields(name);
							}
						}catch(Exception e) {
							LOGGER.severe("Cannot get fields for module" + e.getMessage());
							throw e;
						}

						for(FacilioField facilioField: fields)
						{
							String key = facilioField.getModule().getName() + "__" + facilioField.getName();
							Object cellValue = colVal.get(fieldMapping.get(key));
							boolean isfilledByLookup = false;

							if(cellValue != null && !cellValue.toString().equals("") && (!isBim || !cellValue.toString().equals("n/a"))) {
								if (facilioField.getDataType() != FieldType.LOOKUP.getTypeAsInt()) {
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
												if (!props.containsKey(facilioField.getName())) {
													props.put(facilioField.getName(), millis);
												}
												isfilledByLookup = true;
											}
										}
										else if(facilioField.getDataType() == FieldType.ENUM.getTypeAsInt()) {
											EnumField enumField = (EnumField) facilioField;
											String enumString = (String)colVal.get(fieldMapping.get(key));
											int enumIndex = enumField.getIndex(enumString);

											if(!props.containsKey(facilioField.getName())) {
												props.put(facilioField.getName(), enumIndex);
											}
										}
										else if (facilioField.getName().equals(FacilioConstants.ContextNames.SITE_ID)) {
											String cellValueString = cellValue.toString();
											if (StringUtils.isNotEmpty(cellValueString)) {
												SiteContext site = SpaceAPI.getSite(cellValueString);
												if (site != null && site.getId() > 0) {
													props.put(facilioField.getName(), site.getId());
												}
											}
											isfilledByLookup = true;
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

										e.printStackTrace();
										LOGGER.severe("Process Import Exception -- Row No --" + row_no + " Fields Mapping --" + fieldMapping.get(key));
										throw new ImportParseException(row_no, fieldMapping.get(key), e);
									}
							}
							}

							if (facilioField.getDataTypeEnum().equals(FieldType.LOOKUP) && facilioField instanceof LookupField && (!isBim || (cellValue!=null && !cellValue.toString().equals("n/a")))) {
								LookupField lookupField = (LookupField) facilioField;

								if (lookupField.getLookupModule().getName().equals(FacilioConstants.ContextNames.ASSET_CATEGORY) && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET) && fieldMapping.get(facilioField.getModule().getName() + "__" + facilioField.getName()) == null) {
									ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
									String assetCategoryModuleName = lookupField.getLookupModule().getName();
									List<FacilioField> lookupModuleFields = bean.getAllFields(assetCategoryModuleName);
									FacilioModule assetCategoryModule = bean.getModule(assetCategoryModuleName);

									Map<String, FacilioField> lookupModuleFieldMap = FieldFactory.getAsMap(lookupModuleFields);
									SelectRecordsBuilder<AssetCategoryContext> builder = new SelectRecordsBuilder<AssetCategoryContext>()
											.beanClass(AssetCategoryContext.class)
											.module(assetCategoryModule)
											.select(bean.getAllFields(assetCategoryModuleName))
											.andCondition(CriteriaAPI.getCondition(lookupModuleFieldMap.get("assetModuleID"), importProcessContext.getModule().getModuleId() + "", NumberOperators.EQUALS));;

									List<AssetCategoryContext> categoryPropList = builder.get();
									if (categoryPropList.isEmpty()) {
										throw new Exception("Asset Category " + importProcessContext.getModule().getName() + " not Found.");
									}
									AssetCategoryContext categoryProp = categoryPropList.get(0);

									if (!props.containsKey(facilioField.getName())) {
										props.put(facilioField.getName(), categoryProp);
										groupedFields.put(importProcessContext.getModule().getExtendModule().getName(), lookupField.getName());
									}
									isfilledByLookup = true;
								}

								if (isNewLookupFormat || fieldMapping.get(facilioField.getModule().getName() + "__" + facilioField.getName()) != null) {

								boolean isSkipSpecialLookup = false;

								try {
									if ((importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) || (importProcessContext.getModule().getExtendModule() != null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))) && lookupField.getName().equals("department")) {
										isSkipSpecialLookup = true;
									} else if ((importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.WORK_ORDER) || importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.TASK)) && lookupField.getName().equals("resource")) {
										isSkipSpecialLookup = true;
									}
								} catch (Exception e) {
									e.printStackTrace();
									throw e;
								}

								if (facilioField.getDisplayType().equals(FacilioField.FieldDisplayType.LOOKUP_SIMPLE) || isSkipSpecialLookup) {
									List<Map<String, Object>> lookupPropsList;
									try {
										lookupPropsList = getLookupProps(lookupField,colVal, fieldMapping.get(key), importProcessContext);
									}catch(Exception e) {
										LOGGER.severe("Process Import Lookup Exception -- Row No --" + row_no + " Fields Mapping --" + fieldMapping.get(key));
										if(colVal.get(fieldMapping.get(key)) == null) {
											throw new ImportFieldValueMissingException(row_no, fieldMapping.get(key), e);
										}
										else if (e.getMessage().equals("Value not found")) {
											throw new ImportLookupModuleValueNotFoundException(colVal.get(fieldMapping.get(key)).toString(), row_no, fieldMapping.get(key), e);
										} else {
											throw e;
										}

									}

									if (lookupPropsList != null) {
										Map<String, Object> lookupProps = lookupPropsList.get(0);
										if (!props.containsKey(facilioField.getName())) {
											props.put(facilioField.getName(), lookupProps);
										}
										isfilledByLookup = true;

									}
								}
								else if (facilioField.getDisplayType().equals(FacilioField.FieldDisplayType.LOOKUP_POPUP) && (fieldMapping.get(facilioField.getModule().getName() + "__" + facilioField.getName()) != null)) {
									Map<String, Object> specialLookupList;
									try {
										specialLookupList = getSpecialLookupProps(lookupField,colVal, importProcessContext);
									}catch(Exception e) {
										if(colVal.get(fieldMapping.get(key)) == null) {
											LOGGER.severe("Process Import Special Lookup Exception -- Row No --" + row_no + " Fields Mapping --" + fieldMapping.get(key));
											throw new ImportFieldValueMissingException(row_no, fieldMapping.get(key), e);
										} else if (e.getMessage().equals("Value not found")) {
											throw new ImportLookupModuleValueNotFoundException(colVal.get(fieldMapping.get(key)).toString(), row_no, fieldMapping.get(key), e);
										}
										else {
											throw e;
										}
									}

									if (specialLookupList != null) {
										if (!props.containsKey(facilioField.getName())) {
											props.put(facilioField.getName(), specialLookupList);
										}
										isfilledByLookup = true;
									}
								}
								}
							}

								if (!isfilledByLookup && (!isBim || (cellValue!=null && !cellValue.toString().equals("n/a")))) {
									if (!props.containsKey(facilioField.getName())) {
										props.put(facilioField.getName(), cellValue);
									}
								}
					}

				ReadingContext reading = FieldUtil.getAsBeanFromMap(props, ReadingContext.class);
				if(groupedContext.containsKey(module)) {
					List<ReadingContext> existingList = groupedContext.get(module);
					existingList.add(reading);
				}
				else {
					ArrayList<ReadingContext> tempList = new ArrayList<ReadingContext>();
					tempList.add(reading);
					groupedContext.put(module, tempList);
				}
			}

		c.put(ImportAPI.ImportProcessConstants.GROUPED_FIELDS, groupedFields);
		c.put(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT, groupedContext);
		c.put(FacilioConstants.ContextNames.RECORD_LIST, recordsList);

		return false;
	}

	public static List<Map<String, Object>> getLookupProps(LookupField lookupField, HashMap<String, Object> colVal, String key, ImportProcessContext importProcessContext) throws Exception{

		try {

			ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String lookupModuleName = lookupField.getLookupModule().getName();
			if(isNewLookupFormat(importProcessContext)) {
				List<Map<String, Object>> lookupFieldList = new ArrayList<Map<String,Object>>();
				List<FacilioField> lookupModuleFields = bean.getAllFields(lookupModuleName);
				HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();
				Map<String, Object> lookupFieldMap = new HashMap<String, Object>();
				for(FacilioField field : lookupModuleFields) {

					if(fieldMapping.containsKey(field.getModule().getName() + "__" + field.getName())) {
						if(field.getDataType() == FieldType.ENUM.getTypeAsInt()) {
							EnumField enumField = (EnumField) field;
							String enumString = (String)colVal.get(fieldMapping.get(field.getModule().getName() + "__" + field.getName()));
							int enumIndex = enumField.getIndex(enumString);
							lookupFieldMap.put(field.getName(),enumIndex);
						}
						else if(field.getDataType() == FieldType.LOOKUP.getTypeAsInt()) {
							// special handling for tool and items. Their types have a second lookup
							if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.PURCHASED_ITEM)
									|| importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.PURCHASED_TOOL)) {
								Map<String, Object> secondLookup = new HashMap<String, Object>();
								secondLookup.put("name", colVal.get(fieldMapping.get(field.getModule().getName() + "__" + field.getName())));
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

				if(value == null || value.toString().isEmpty()) {
					if (!lookupField.isRequired()) {
						return null;
					} else {
						throw new Exception("Field value missing under column " + importProcessContext.getFieldMapping().get(lookupField.getModule().getName()+ "__" + lookupField.getName()) + ".");
					}
				}

				LOGGER.info("getLookupProps -- " + lookupField.getColumnName() + " facilioField.getModule() - "
						+ lookupField.getLookupModule().getTableName() + " with value -- " + value);


				ArrayList<FacilioField> fieldsList;

				fieldsList = new ArrayList<>(bean.getAllFields(lookupModuleName));
				fieldsList.add(FieldFactory.getIdField(lookupField.getLookupModule()));
				fieldsList.add(FieldFactory.getModuleIdField(lookupField.getLookupModule()));
				String columnName,fieldName;

				if (FieldFactory.getAsMap(bean.getAllFields(lookupModuleName)).get("displayName") != null) {
					columnName = "DISPLAY_NAME";
					fieldName = "displayName";
				} else {
					columnName = "NAME";
					fieldName = "name";
				}

				SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder =  new SelectRecordsBuilder<>()
						.module(lookupField.getLookupModule())
						.select(fieldsList);
				if (lookupField.getName().equals("moduleState")) {
					selectBuilder.andCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", String.valueOf(importProcessContext.getModule().getModuleId()), NumberOperators.EQUALS));
				}

				selectBuilder.andCondition(CriteriaAPI.getCondition(columnName, fieldName, value.toString().toLowerCase().trim().replace(",", StringOperators.DELIMITED_COMMA), StringOperators.IS));

				List<Map<String, Object>> props = selectBuilder.getAsProps();

				if (props.isEmpty()) {

					HashMap<String, Object> insertProps = new LinkedHashMap<String, Object>();
					if (FieldFactory.getAsMap(bean.getAllFields(lookupModuleName)).get("displayName") != null) {
						insertProps.put(fieldName, value);
					}
					if (FieldFactory.getAsMap(bean.getAllFields(lookupModuleName)).get("name") != null) {
						insertProps.put("name", value);
					}

					insertProps.put("orgId", AccountUtil.getCurrentOrg().getId());
					insertProps.put("moduleId", lookupField.getLookupModule().getModuleId());

					if (lookupModuleName.equals(FacilioConstants.ContextNames.ASSET_CATEGORY)) {
						throw new Exception("Value not found");
					} else if (lookupField.getModule().getName().equals(FacilioConstants.ContextNames.WORK_ORDER) || lookupField.getModule().getName().equals(FacilioConstants.ContextNames.TICKET)) {
						if (lookupField.getName().equals("moduleState") || lookupField.getName().equals("ticketstatus")  ||  lookupField.getName().equals("resource") || lookupField.getName().equals("priority")) {
							throw new Exception("Value not found");
						}
					} else if (lookupField.getModule().getName().equals(FacilioConstants.ContextNames.TASK)) {
						if (lookupField.getName().equals("resource")) {
							throw new Exception("Value not found");
						}
					}
					List<String> lookupModuleList = Arrays.asList("tenant", "ticketstatus", "vendors", "site");
					if (lookupModuleList.contains(lookupField.getName())) {
						throw new Exception("Value not found");
					}

					GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
							.table(lookupField.getLookupModule().getTableName()).fields(fieldsList);

					insertRecordBuilder.addRecord(insertProps);

					insertRecordBuilder.save();
					LOGGER.info("insertProps --- " + insertProps);
					Long id = (Long) insertProps.get("id");

					if (id != null) {
						LOGGER.info("inserted with ID --" + id);
						props.add(insertProps);
					}
				}
				return props;
			}


		} catch (Exception e) {
			LOGGER.severe("Exception occurred at lookup field");
			LOGGER.severe(e.toString());
			throw e;
		}
	}

	public static boolean isNewLookupFormat(ImportProcessContext importProcessContext) throws Exception{
		boolean isNewFormat = false;
		List<String> moduleList = new ArrayList<String>();

		moduleList.add(FacilioConstants.ContextNames.PURCHASED_ITEM);
		moduleList.add(FacilioConstants.ContextNames.PURCHASED_TOOL);

		if(importProcessContext.getModule()!=null && moduleList.contains(importProcessContext.getModule().getName())) {
			isNewFormat = true;
		}
		return isNewFormat;

	}
	public static Map<String, Object> getSpecialLookupProps(LookupField lookupField, HashMap<String,Object> colVal, ImportProcessContext importProcessContext) throws Exception{

		Object value = colVal.get(importProcessContext.getFieldMapping().get(lookupField.getModule().getName()+ "__" + lookupField.getName()));


		try {
			if(value == null || value.toString().isEmpty()) {
				if (!lookupField.isRequired()) {
					return null;
				} else {
					throw new Exception("Field value missing under column " + importProcessContext.getFieldMapping().get(lookupField.getModule().getName()+ "__" + lookupField.getName()) + ".");
				}
			}
			String moduleName;
			if(lookupField.getLookupModule() == null && lookupField.getSpecialType() != null) {
				moduleName = lookupField.getSpecialType();
			}
			else {
				moduleName = lookupField.getLookupModule().getName();
			}
			switch (moduleName) {
			case "workorder": {
				AppDomain appDomain = IAMAppUtil.getAppDomain(AccountUtil.getDefaultAppDomain());
				User user = AccountUtil.getUserBean().getUser(value.toString(), appDomain != null ? appDomain.getIdentifier() : null);
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
				Long siteId = new ImportSiteAction().getSiteId(value.toString());
				if (siteId != null) {
					SiteContext site = SpaceAPI.getSiteSpace(siteId);
					Map<String, Object> prop2 = FieldUtil.getAsProperties(site);
					return prop2;
				} else {
					throw new Exception("Value not found");
				}
			}
			case "resource": {
				ResourceContext resource = getResource(value.toString());
				Map<String, Object> prop2 = FieldUtil.getAsProperties(resource);
				return prop2;
			}
			case "users": {
				long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
				AppDomain appDomainObj = ApplicationApi.getAppDomainForApplication(appId);
			    User user = AccountUtil.getUserBean().getUser(value.toString(), appDomainObj != null ? appDomainObj.getIdentifier() : null);
				if(user == null) {
					if (lookupField.getName().equals("assignedTo") || lookupField.getName().equals("createdBy")) {
						throw new Exception("Value not found");
					}
					user = new User();
					user.setEmail(value.toString());
					user.setApplicationId(appId);
					user.setAppDomain(appDomainObj);

					AccountUtil.getUserBean().inviteRequester(AccountUtil.getCurrentOrg().getId(), user, false, false,appDomainObj.getIdentifier(), true, false);
				}
				Map<String, Object> prop = FieldUtil.getAsProperties(user);
				return prop;
			}
			case "location":{
				if(value != null) {
					String [] locationSplit = value.toString().split(",");

					double lat = Double.parseDouble(locationSplit[0]);
					double lon = Double.parseDouble(locationSplit[locationSplit.length - 1]);

					LocationContext existingLocation = LocationAPI.getLocation((long)lat, (long)lon, AccountUtil.getCurrentOrg().getOrgId());

					if(existingLocation == null) {
						LocationContext newLocation = new LocationContext();
						newLocation.setName("Import" + importProcessContext.getId());
						newLocation.setLat(lat);
						newLocation.setLng(lon);
						newLocation.setOrgId(AccountUtil.getCurrentOrg().getOrgId());

						// newLocation.setSiteId((long)siteObj.get("id"));
						LocationAPI.addLocation(newLocation);
						return FieldUtil.getAsProperties(newLocation);
					}
					else {
						return FieldUtil.getAsProperties(existingLocation);
					}
				}
			}
			case "groups": {
				Group group = AccountUtil.getGroupBean().getGroup(value.toString());
				if (group == null && lookupField.getName().equals("assignmentGroup")) {
					throw new Exception("Value not found");
				}
				Map<String, Object> prop = FieldUtil.getAsProperties(group);
				return prop;
			}
			}

		} catch (Exception e) {
			LOGGER.severe("Exception occurred for special lookup");
			LOGGER.severe(e.toString());
			throw e;
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

	public static Long getResourceId(ImportProcessContext importProcessContext, HashMap<String, Object> colVal, HashMap<String, String> fieldMapping, Long siteId, int row_no) throws Exception {
		String moduleName = importProcessContext.getModule().getName();
		String assetName = (String) colVal.get(fieldMapping.get(moduleName + "__asset"));
		if (assetName != null && StringUtils.isNotEmpty(assetName)) {
			AssetContext asset = ImportAPI.getAssetFromName(assetName, siteId);
			if (asset != null) {
				return asset.getId();
			} else {
				throw new ImportLookupModuleValueNotFoundException(colVal.get(fieldMapping.get(moduleName + "__asset")).toString(), row_no, fieldMapping.get(moduleName + "__asset"), new Exception());
			}
		} else {
			return getSpaceID(importProcessContext, colVal, fieldMapping, row_no, false);
		}
	}

	public static Long getSpaceID(ImportProcessContext importProcessContext, HashMap<String, Object> colVal, HashMap<String,String> fieldMapping, int row_no ,boolean canAddNew) throws Exception {

		String siteName,buildingName,floorName,spaceName;

		ArrayList<JSONObject> additionalSpaces = new ArrayList<>();
		String moduleName = importProcessContext.getModule().getName();
		if (moduleName.equals(FacilioConstants.ContextNames.TENANT_UNIT_SPACE) || moduleName.equals(FacilioConstants.ContextNames.SPACE)) {
			siteName = (String) colVal.get(fieldMapping.get("basespace" + "__site"));
		} else {
			siteName = (String) colVal.get(fieldMapping.get(moduleName + "__site"));
		}
		buildingName = (String) colVal.get(fieldMapping.get(moduleName + "__building"));
		floorName = (String) colVal.get(fieldMapping.get(moduleName + "__floor"));
		spaceName = (String) colVal.get(fieldMapping.get(moduleName + "__spaceName"));
		int start = 1;

		if (spaceName != null ||
				importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.BASE_SPACE)
				|| importProcessContext.getModule().getExtendModule() != null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.BASE_SPACE)
		) {
			if (importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.BASE_SPACE)
					|| importProcessContext.getModule().getExtendModule() != null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.BASE_SPACE)) {

				spaceName = (String) colVal.get(fieldMapping.get(moduleName + "__space1"));
				start = 2;
			}
			for (int i = start; i <= 4; i++) {
				String temp = (String) colVal.get(fieldMapping.get(moduleName + "__space" + (i)));
				if (temp != null && temp != "") {
					JSONObject additionalSpace = new JSONObject();
					additionalSpace.put("level", i);
					additionalSpace.put("name", temp);
					additionalSpaces.add(additionalSpace);
				} else {
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
				siteId = siteMap.get(siteName.trim().toLowerCase());
			 } else {
				 throw new ImportLookupModuleValueNotFoundException(siteName, row_no, fieldMapping.get(moduleName + "__site"), new Exception());
			 }
			 if(siteId != null) {
				 spaceId =
						 siteId;
			 }
		 } else if (importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.WORK_ORDER) || importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) || (importProcessContext.getModule().getExtendModule() != null && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))) {
			siteId = importProcessContext.getSiteId();
			if(siteId != null) {
				spaceId = siteId;
			 }
		 }

		 if(buildingName != null && !buildingName.equals("")) {
			 if(siteId != null) {
				 buildingId = buildingMeta.getBuildingId(siteId, buildingName);
			 }
			 else {
				 List<BuildingContext> buildings = SpaceAPI.getAllBuildings(siteId);
				 HashMap<String, Long> buildingMap = new HashMap();
				 for (BuildingContext buildingContext : buildings)
				 {
					 buildingMap.put(buildingContext.getName().trim().toLowerCase(), buildingContext.getId());
				 }
				 if(buildingMap.containsKey(buildingName.trim().toLowerCase()))
				 {
					 buildingId = buildingMap.get(buildingName.trim().toLowerCase());
				 }
			 }
			 if (buildingId == null) {
				 if (canAddNew) {
					 buildingId = buildingMeta.addBuilding(buildingName, siteId, importProcessContext.getId());
					 recordsList.put("building", buildingId);
				 } else {
					 throw new ImportLookupModuleValueNotFoundException(buildingName, row_no, fieldMapping.get(moduleName + "__building"), new Exception());
				 }
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
				throw new Exception(floorName + " can be added only under a building");
			}
		    if(floorId == null)
		    {
		    	if (canAddNew) {
					floorId = floorMeta.addFloor(floorName, siteId, buildingId, importProcessContext.getId());
					recordsList.put("floor", floorId);
				} else {
					throw new ImportLookupModuleValueNotFoundException(floorName, row_no, fieldMapping.get(moduleName + "__floor"), new Exception());
				}
		    }
		    if(floorId != null) {
				 spaceId = floorId;
			 }
		 }

		if (spaceName != null && !spaceName.equals("")) {
			spaceId = null;
			if (floorId != null) {
				spaceId = spaceMeta.getSpaceId(floorId, spaceName);
			} else if (buildingId != null) {
				spaceId = spaceMeta.getSpaceIdFromBuilding(buildingId, spaceName);
			} else if (siteId != null) {
				spaceId = spaceMeta.getSpaceIdFromSite(siteId, spaceName);
			}
			if (spaceId == null) {
				if (canAddNew) {
					if (floorName == null) {
						spaceId = spaceMeta.addSpace(spaceName, siteId, buildingId, importProcessContext.getId());
					} else {
						spaceId = spaceMeta.addSpace(spaceName, siteId, buildingId, floorId, importProcessContext.getId());
					}
					recordsList.put("space", spaceId);
				} else {
					throw new ImportLookupModuleValueNotFoundException(spaceName, row_no, fieldMapping.get(moduleName + "__spaceName"), new Exception());
				}
			}
		}
		if (additionalSpaces.size() > 0) {
			Long tempSpaceId;
			for (JSONObject additionalSpace : additionalSpaces) {
				tempSpaceId = SpaceAPI.getDependentSpaceId((String) additionalSpace.get("name"), spaceId, (int)additionalSpace.get("level"));
				if (tempSpaceId != null) {
					spaceId = tempSpaceId;
				} else if (canAddNew) {
					spaceId = SpaceAPI.addDependentSpace((String) additionalSpace.get("name"), spaceId, importProcessContext.getId());
					recordsList.put("space", spaceId);
				} else {
					throw new ImportLookupModuleValueNotFoundException((String) additionalSpace.get("name"), row_no, fieldMapping.get(moduleName + "__space" + additionalSpace.get("level")), new Exception());
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
																		.andCustomWhere("NAME = ?", name.replace(",", StringOperators.DELIMITED_COMMA))
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