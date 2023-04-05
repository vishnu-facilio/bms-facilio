package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.*;
import com.facilio.bmsconsole.actions.ImportProcessContext.ImportSetting;
import com.facilio.command.FacilioCommand;
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
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
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

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
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

			if (AssetsAPI.isAssetsModule(importProcessContext.getModule())) {
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

				if(!isBim){
					if(siteId != null && siteId > 0) {
						props.put("siteId", siteId);
					}
					else if (fieldMapping.get(importProcessContext.getModule().getName() + "__site") != null && colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__site")) != null) {
						String siteName = (String) colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__site"));
						SiteContext site = SpaceAPI.getSite(siteName);
						props.put("siteId", site.getId());
					}
					
				}else{
					props.put("siteId", Long.parseLong(props.get("site").toString()));
					props.remove("site");
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

			else if (importProcessContext.getModule() != null && ImportAPI.isSpaceModule(importProcessContext.getModule())) {

				if(!isBim){
					ArrayList<String> spaceFields = new ArrayList<>();
					spaceFields.add(module + "__site");
					spaceFields.add(module+"__building");
					spaceFields.add(module+"__floor");
					spaceFields.add(module + "__space1");
					spaceFields.add(module + "__space2");
					spaceFields.add(module + "__space3");
					spaceFields.add(module + "__space4");
					String name = (String) colVal.get(fieldMapping.get("resource__name"));

					String [] Ids = {"site","building","floor", "space1", "space2", "space3", "space4"};

					for(int k =0 ;k<Ids.length ;k++) {
						HashMap<String, Object> sendToSpaceID = new HashMap<>();
						for(int z=0 ;z<k+1;z++) {
							if(colVal.get(fieldMapping.get(spaceFields.get(z))) != null && colVal.get(fieldMapping.get(spaceFields.get(z))) != "") {
								sendToSpaceID.put(fieldMapping.get(spaceFields.get(z)), colVal.get(fieldMapping.get(spaceFields.get(z))));
							}
						}
						if(!sendToSpaceID.containsKey(fieldMapping.get(spaceFields.get(k)))) {
							continue;
						}
						Long id = getSpaceID(importProcessContext, sendToSpaceID, fieldMapping, row_no,true);
						lookupHolder = new HashMap<>();
						lookupHolder.put("id", id);
						if (Ids[k].equals(FacilioConstants.ContextNames.SITE)) {
							props.put(FacilioConstants.ContextNames.SITE_ID, id);
						}
						props.put(Ids[k], lookupHolder);
					}

					if (!module.equals(FacilioConstants.ContextNames.SPACE)) {
						// Filling space modules Default stateflow and status for tenant unit module (remove this if tenant unit has its own stateflow and states)
						if (ImportAPI.isInsertImport(importProcessContext) && module.equals(FacilioConstants.ContextNames.TENANT_UNIT_SPACE)) {
							FacilioModule spaceModule = modBean.getModule(FacilioConstants.ContextNames.SPACE);
							StateFlowRuleContext defaultStateFlow = StateFlowRulesAPI.getDefaultStateFlow(spaceModule);
							if (defaultStateFlow != null) {
								props.put(FacilioConstants.ContextNames.STATE_FLOW_ID, defaultStateFlow.getId());
								lookupHolder = new HashMap<>();
								lookupHolder.put("id", defaultStateFlow.getDefaultStateId());
								props.put(FacilioConstants.ContextNames.MODULE_STATE, lookupHolder);
							}
						}
						SpaceCategoryContext spaceCategory = SpaceAPI.getSpaceCategoryFromModule(importProcessContext.getModuleId());
						if (spaceCategory != null) {
							props.put(FacilioConstants.ContextNames.SPACE_CATEGORY_FIELD, spaceCategory);
							SpaceContext space = SpaceAPI.getSpaceFromHierarchy(props, name);
							if (space != null && !(space.getSpaceCategory() != null && space.getSpaceCategory().getId() == spaceCategory.getId())) {
								SpaceAPI.addSpaceExtentedModuleEntry(space, module);
							}
						}
					}
				}
				else{
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
				Long siteId;
				if(!isBim){
					siteId = getSpaceID(importProcessContext,colVal, fieldMapping, row_no, true);
				}else{
					siteId = Long.parseLong(props.get("site").toString());
				}
				if (siteId != null) {
					lookupHolder = new HashMap<>();
					lookupHolder.put("id", siteId);
					props.put("siteId", siteId);
					props.put(ImportAPI.ImportProcessConstants.SITE_ID_FIELD, lookupHolder);
				}
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
				
				lookupHolder = new HashMap<>();
				lookupHolder.put("id", Long.parseLong(props.get("siteId").toString()));
				props.put("site",lookupHolder);

				if(!isBim){
					Long floorId = getSpaceID(importProcessContext,colVal, fieldMapping,row_no, true);
					lookupHolder = new HashMap<>();
					lookupHolder.put("id", floorId);
					props.put(ImportAPI.ImportProcessConstants.BUILDING_ID_FIELD,lookupHolder);
					props.put("buildingId", floorId);
										
				}else{
					props.put("siteId", Long.parseLong(props.get("site").toString()));					
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

			} else if(isBim && module.equals("zonespacerel")){

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

			} else if(isBim && importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.CONTACT)){
				String nameStr = fieldMapping.get(FacilioConstants.ContextNames.CONTACT+"__"+"name");
				String[] colNames= nameStr.split("&&");
				StringBuilder name = new StringBuilder();
				for (String colName : colNames) {
					if (colVal != null && colVal.get(colName) != null && !colVal.get(colName).toString().equals("n/a")) {
						name.append(colVal.get(colName)).append(" ");
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

			} 
			else if (importProcessContext.getModule()!=null && importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.TASK)) {
				String sectionName = (String) colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__sectionId"));
				Object parentTicketId = colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__parentTicketId"));
				if (StringUtils.isNotEmpty(sectionName) && parentTicketId != null && StringUtils.isNotEmpty(parentTicketId.toString())) {
					Criteria criteria = new Criteria();
					criteria.addAndCondition(CriteriaAPI.getCondition("PARENT_TICKET_ID", "parentTicketId" , parentTicketId.toString(), NumberOperators.EQUALS));
					criteria.addAndCondition(CriteriaAPI.getCondition("NAME","name", sectionName.trim().replace(",", StringOperators.DELIMITED_COMMA), StringOperators.IS));
					List<TaskSectionContext> taskSection = TicketAPI.getTaskSections(criteria);
					if (CollectionUtils.isNotEmpty(taskSection) && taskSection.size() > 0) {
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
				FacilioModule moduleObj = modBean.getModule(module);
				if(isBim && module.equals("zonespacerel")){
					moduleObj = ModuleFactory.getZoneRelModule();
				}

				if (fieldMapping.containsKey(module + "__formId") && colVal.get(fieldMapping.get(module + "__formId")) != null) {
					props.put("formId", colVal.get(fieldMapping.get(module + "__formId")));
				}

				if (moduleObj != null && moduleObj.isStateFlowEnabled()) {
					StateFlowRuleContext defaultStateFlow = StateFlowRulesAPI.getDefaultStateFlow(moduleObj);
					if (defaultStateFlow != null) {
						props.put(FacilioConstants.ContextNames.STATE_FLOW_ID, defaultStateFlow.getId());
					}
				}
				else if (AssetsAPI.isAssetsModule(importProcessContext.getModule())) {
					StateFlowRuleContext stateFlow = StateFlowRulesAPI.getDefaultStateFlow(modBean.getModule(FacilioConstants.ContextNames.ASSET));
					if (stateFlow != null) {
						props.put(FacilioConstants.ContextNames.STATE_FLOW_ID, stateFlow.getId());
						if (ImportAPI.isInsertImport(importProcessContext)) {
							if (!fieldMapping.containsKey("asset__moduleState")) {
								lookupHolder = new HashMap<>();
								lookupHolder.put("id", stateFlow.getDefaultStateId());
								props.put(FacilioConstants.ContextNames.MODULE_STATE, lookupHolder);
							}
						}
					}
				} 
				else if (module.equals(FacilioConstants.ContextNames.WORK_ORDER) && ImportAPI.isInsertImport(importProcessContext)) {
					props.put(FacilioConstants.ContextNames.STATE_FLOW_ID, StateFlowRulesAPI.getDefaultStateFlow(moduleObj).getId());
				}
				if (colVal.containsKey(fieldMapping.get(module + "__site")))
				{
					String siteName = (String) colVal.get(fieldMapping.get(module + "__site"));
					if (!(importProcessContext.getImportSetting() == ImportSetting.UPDATE.getValue() || importProcessContext.getImportSetting() == ImportSetting.UPDATE_NOT_NULL.getValue())) {
						if (!props.containsKey("siteId") && siteName != null) {
							SiteContext site = SpaceAPI.getSite(siteName.trim());
							if(site != null) {
								props.put("siteId", site.getId());
							}
						}
					}
				}
			}
			List<FacilioField> fields;
			try {
				ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				if(isBim && module.equals("zonespacerel")){
					fields = FieldFactory.getZoneRelFields();
				}
				else if(module.equals(ModuleFactory.getPreventiveMaintenanceModule().getName())){
					fields = ImportFieldFactory.getPMImportFields();
				}
				else if(module.equals(ModuleFactory.getTaskTemplateModule().getName())){
					fields = ImportFieldFactory.getPMTaskImportFields();
				}
				else if(module.equals(ModuleFactory.getTaskSectionTemplateModule().getName())){
					fields = ImportFieldFactory.getPMTaskSectionImportFields();
				}
				else if(module.equals(ModuleFactory.getPMTriggersModule().getName())){
					fields = ImportFieldFactory.getPMTriggerFields();
				}
				else if(module.equals(ModuleFactory.getPMResourcePlannerModule().getName())){
					fields = ImportFieldFactory.getPMResourcePlannerFields();
				}
				else if(module.equals(ModuleFactory.getPMReminderModule().getName())){
					fields = ImportFieldFactory.getPMReminderFields();
				}
				else{
					fields = bean.getAllFields(module);
				}
			}
			catch(Exception e) {
				LOGGER.severe("Cannot get fields for module" + e.getMessage());
				throw e;
			}

			for (FacilioField facilioField : fields) {
				String key = facilioField.getModule().getName() + "__" + facilioField.getName();
				Object cellValue = colVal.get(fieldMapping.get(key));
				boolean isfilledByLookup = false;

				if (cellValue != null && !cellValue.toString().equals("") && (!isBim || !cellValue.toString().equals("n/a"))) {
					try {
						if (facilioField.getDataTypeEnum().equals(FieldType.DATE_TIME) || facilioField.getDataTypeEnum().equals(FieldType.DATE)) {
							if (!(cellValue instanceof Long)) {
								long millis;
								if (dateFormats.get(fieldMapping.get(key)).equals(ImportAPI.ImportProcessConstants.TIME_STAMP_STRING)) {
									millis = Long.parseLong(cellValue.toString());
								} else {
									Instant dateInstant = DateTimeUtil.getTimeInstant(dateFormats.get(fieldMapping.get(key)).toString(), cellValue.toString());
									millis = dateInstant.toEpochMilli();
								}
								if (!props.containsKey(facilioField.getName())) {
									props.put(facilioField.getName(), millis);
								}
								isfilledByLookup = true;
							}
						} else if (facilioField.getDataType() == FieldType.ENUM.getTypeAsInt()) {
							EnumField enumField = (EnumField) facilioField;
							String enumString = (String) colVal.get(fieldMapping.get(key));
							int enumIndex = enumField.getIndex(enumString);

							if (!props.containsKey(facilioField.getName())) {
								props.put(facilioField.getName(), enumIndex);
							}
							isfilledByLookup = true;
						} else if (facilioField.getDataType() == FieldType.MULTI_ENUM.getTypeAsInt()) {
							MultiEnumField multiEnumField = (MultiEnumField) facilioField;
							String enumString = (String) colVal.get(fieldMapping.get(key));
							ArrayList enumIndices = new ArrayList();
							if (StringUtils.isNotEmpty(enumString)) {
								for (String string : FacilioUtil.splitByComma(enumString)) {
									int enumIndex = multiEnumField.getIndex(string);
									if (enumIndex > 0) {
										enumIndices.add(enumIndex);
									}
								}
							}
							if (!props.containsKey(facilioField.getName())) {
								props.put(facilioField.getName(), enumIndices);
							}
							isfilledByLookup = true;
						} else if (facilioField.getDataType() == FieldType.MULTI_LOOKUP.getTypeAsInt()) {
							String value = (String) colVal.get(fieldMapping.get(key));
							List<Map<String,Object>> lookupRecords = new ArrayList<>();
							if (StringUtils.isNotEmpty(value)) {
								lookupRecords = getRecordsForMultiLookupField((MultiLookupField)facilioField, value);
							}
							if (CollectionUtils.isNotEmpty(lookupRecords)) {
								props.put(facilioField.getName(), lookupRecords);
							}
							isfilledByLookup = true;
						} else if (facilioField.getName().equals(FacilioConstants.ContextNames.SITE_ID)) {
							String cellValueString = cellValue.toString();
							if (StringUtils.isNotEmpty(cellValueString)) {
								// cellValueString should have Site names in the following format for multi-site - Site 1,Site 2,Site 3
								String[] siteNames = cellValueString.split(",");
								if (siteNames.length > 1) {
									// handling for multi site import
									List<Long> siteIds = new ArrayList<>();
									for (String site : siteNames) {
										if (StringUtils.isNotEmpty(site)) {
											SiteContext siteContext = SpaceAPI.getSite(site.trim());
											if (siteContext.getId() > 0) {
												siteIds.add(siteContext.getId());
											}
										}
									}
									props.put("siteIds", siteIds);
									props.put(facilioField.getName(), -1); // adding -1 to siteId field
								} else {
									SiteContext site = SpaceAPI.getSite(cellValueString);
									if (site != null && site.getId() > 0) {
										props.put(facilioField.getName(), site.getId());
									}
								}
							}
							isfilledByLookup = true;
						} else if (facilioField.getDataTypeEnum().equals(FieldType.NUMBER) || facilioField.getDataTypeEnum().equals(FieldType.DECIMAL)) {
							String cellValueString = cellValue.toString();
							if (cellValueString.contains(",")) {
								cellValueString = cellValueString.replaceAll(",", "");
							}

							Double cellDoubleValue = Double.parseDouble(cellValueString);
							if (!props.containsKey(facilioField.getName())) {
								props.put(facilioField.getName(), cellDoubleValue);
							}
							isfilledByLookup = true;
						}
					} catch (Exception e) {

						LOGGER.severe("Process Import Exception -- Row No --" + row_no + " Fields Mapping --" + fieldMapping.get(key));
						throw new ImportParseException(row_no, fieldMapping.get(key), e);
					}
				}

				if (facilioField.getDataTypeEnum().equals(FieldType.LOOKUP) && facilioField instanceof LookupField && (!isBim || (cellValue != null && !cellValue.toString().equals("n/a")))) {
					LookupField lookupField = (LookupField) facilioField;

					if (lookupField.getDisplayType() == null) {
						continue;
					}
					// Auto Add Category for Asset extended modules
					if (lookupField.getLookupModule() != null && lookupField.getLookupModule().getName().equals(FacilioConstants.ContextNames.ASSET_CATEGORY) && importProcessContext.getModule().getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET) && fieldMapping.get(key) == null) {
						ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						String assetCategoryModuleName = lookupField.getLookupModule().getName();
						List<FacilioField> lookupModuleFields = bean.getAllFields(assetCategoryModuleName);
						FacilioModule assetCategoryModule = bean.getModule(assetCategoryModuleName);

						Map<String, FacilioField> lookupModuleFieldMap = FieldFactory.getAsMap(lookupModuleFields);
						SelectRecordsBuilder<AssetCategoryContext> builder = new SelectRecordsBuilder<AssetCategoryContext>()
								.beanClass(AssetCategoryContext.class)
								.module(assetCategoryModule)
								.select(bean.getAllFields(assetCategoryModuleName))
								.andCondition(CriteriaAPI.getCondition(lookupModuleFieldMap.get("assetModuleID"), importProcessContext.getModule().getModuleId() + "", NumberOperators.EQUALS));

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

					if (isNewLookupFormat || fieldMapping.get(key) != null) {

						boolean isSkipSpecialLookup = false;

						try {
							if (AssetsAPI.isAssetsModule(importProcessContext.getModule()) && lookupField.getName().equals("department")) {
								isSkipSpecialLookup = true;
							} else if ((importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.WORK_ORDER) || importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.TASK)) && lookupField.getName().equals("resource")) {
								isSkipSpecialLookup = true;
							}
						} catch (Exception e) {
							throw e;
						}

						if (facilioField.getDisplayType().equals(FacilioField.FieldDisplayType.LOOKUP_SIMPLE) || isSkipSpecialLookup) {
							List<Map<String, Object>> lookupPropsList;
							try {
								lookupPropsList = getLookupProps(lookupField, colVal, fieldMapping.get(key), importProcessContext);
							} catch (Exception e) {
								LOGGER.severe("Process Import Lookup Exception -- Row No --" + row_no + " Fields Mapping --" + fieldMapping.get(key));
								if (colVal.get(fieldMapping.get(key)) == null) {
									throw new ImportFieldValueMissingException(row_no, fieldMapping.get(key), e);
								} else if (e.getMessage() != null && e.getMessage().equals("Value not found")) {
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
						} else if (facilioField.getDisplayType().equals(FacilioField.FieldDisplayType.LOOKUP_POPUP) && (fieldMapping.get(key) != null)) {
							// TODO remove this handle based on data type
							Map<String, Object> specialLookupList;
							try {
								specialLookupList = getSpecialLookupProps(lookupField, colVal, fieldMapping.get(key), importProcessContext);
							} catch (Exception e) {
								if (colVal.get(fieldMapping.get(key)) == null) {
									LOGGER.severe("Process Import Special Lookup Exception -- Row No --" + row_no + " Fields Mapping --" + fieldMapping.get(key));
									throw new ImportFieldValueMissingException(row_no, fieldMapping.get(key), e);
								} else if (e.getMessage() != null && e.getMessage().equals("Value not found")) {
									throw new ImportLookupModuleValueNotFoundException(colVal.get(fieldMapping.get(key)).toString(), row_no, fieldMapping.get(key), e);
								} else {
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
					} else if (facilioField.getDisplayType() != null && facilioField.getDisplayType().equals(FacilioField.FieldDisplayType.ADDRESS) && isLocationFieldMapped(fieldMapping, facilioField, importProcessContext.getModule().getName())) {
						LocationContext location = new LocationContext();
						if (fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_name") != null) {
							location.setName((String) colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_name")));
						}
						if (fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_street") != null) {
							location.setStreet((String) colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_street")));
						}
						if (fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_city") != null) {
							location.setCity((String) colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_city")));
						}
						if (fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_state") != null) {
							location.setState((String) colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_state")));
						}
						if (fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_country") != null) {
							location.setCountry((String) colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_country")));
						}
						if (fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_zip") != null) {
							location.setZip((String) colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_zip")));
						}
						if (fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_lat") != null) {
							location.setLat((double) colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_lat")));
						}
						if (fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_lng") != null) {
							location.setLng((double) colVal.get(fieldMapping.get(importProcessContext.getModule().getName() + "__" + facilioField.getName() + "_lng")));
						}
						if (isLocationContextNotEmpty(location)) {
							if (location.getLng() == -1) {
								location.setLng(1.1);
							}
							if (location.getLat() == -1) {
								location.setLat(1.1);
							}
							if (location.getName() == null) {
								location.setName("Import" + importProcessContext.getId());
							}
							location.setId(LocationAPI.addLocationV2(location));
							props.put(facilioField.getName(), location);
						}
					}
				}

				if (!isfilledByLookup && (!isBim || (cellValue != null && !cellValue.toString().equals("n/a")))) {
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

		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		try {

			String lookupModuleName = lookupField.getLookupModule().getName();
			if(isNewLookupFormat(importProcessContext)) {
				List<Map<String, Object>> lookupFieldList = new ArrayList<Map<String,Object>>();
				List<FacilioField> lookupModuleFields = bean.getAllFields(lookupModuleName);
				HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();
				Map<String, Object> lookupFieldMap = new HashMap<String, Object>();
				for(FacilioField field : lookupModuleFields) {
					if(fieldMapping.containsKey(field.getModule().getName() + "__" + field.getName())) {
						if(field.getDataType() == FieldType.LOOKUP.getTypeAsInt()) {
							// special handling for tool and items. Their types have a second lookup
							if(importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.PURCHASED_ITEM)
									|| importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.PURCHASED_TOOL)) {
								Map<String, Object> secondLookup = new HashMap<String, Object>();
								secondLookup.put("name", colVal.get(field.getModule().getName() + "__" + field.getName()));
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
						throw new Exception("Field value missing under column " + importProcessContext.getFieldMapping().get(key) + ".");
					}
				}

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

				FacilioModule lookupModule = bean.getModule(lookupModuleName);

				SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder =  new SelectRecordsBuilder<>()
						.module(lookupModule)
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
					List<String> lookupModuleList = Arrays.asList("ticketstatus");
					if (lookupModuleList.contains(lookupField.getName()) || lookupField.getModule().getTypeEnum() == FacilioModule.ModuleType.BASE_ENTITY || lookupField.getModule().getTypeEnum() == FacilioModule.ModuleType.CUSTOM) {
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
	public static Map<String, Object> getSpecialLookupProps(LookupField lookupField, HashMap<String,Object> colVal, String key, ImportProcessContext importProcessContext) throws Exception{

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
				case "asset": {
					long assetid = AssetsAPI.getAssetId(value.toString(), lookupField.getOrgId());
					AssetContext asset = AssetsAPI.getAssetInfo(assetid);

					return FieldUtil.getAsProperties(asset);
				}
				case "site": {
					Long siteId = new ImportSiteAction().getSiteId(value.toString());
					if (siteId != null) {
						SiteContext site = SpaceAPI.getSiteSpace(siteId);
						return FieldUtil.getAsProperties(site);
					} else {
						throw new Exception("Value not found");
					}
				}
				case "resource": {
					ResourceContext resource = getResource(value.toString());
					return FieldUtil.getAsProperties(resource);
				}
				case "users": {
					long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
					AppDomain appDomainObj = ApplicationApi.getAppDomainForApplication(appId);
					User user = AccountUtil.getUserBean().getUser(value.toString(), appDomainObj != null ? appDomainObj.getIdentifier() : null);
					if (user == null) {
						throw new Exception("Value not found");
					}
					return FieldUtil.getAsProperties(user);
				}
				case "location": {
					if (value != null) {
						String[] locationSplit = value.toString().split(",");
						double lat = Double.parseDouble(locationSplit[0]);
						double lon = Double.parseDouble(locationSplit[locationSplit.length - 1]);

						LocationContext existingLocation = LocationAPI.getLocation((long) lat, (long) lon, AccountUtil.getCurrentOrg().getOrgId());

						if (existingLocation == null) {
							HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();
							String resourceNameKey = fieldMapping.get("resource__name");
							String resourceName;
							LocationContext newLocation = new LocationContext();
							newLocation.setName("Import" + importProcessContext.getId());
							if(resourceNameKey!=null){
								if(colVal.containsKey(resourceNameKey)){
									resourceName = (String) colVal.get(resourceNameKey);
									newLocation.setName(resourceName+"_Location");
								}
							}
							newLocation.setLat(lat);
							newLocation.setLng(lon);
							newLocation.setOrgId(AccountUtil.getCurrentOrg().getOrgId());

							long locationId = LocationAPI.addLocation(newLocation);
							newLocation.setId(locationId);
							return FieldUtil.getAsProperties(newLocation);
						} else {
							return FieldUtil.getAsProperties(existingLocation);
						}
					}
				}
				case "groups": {
					Group group = AccountUtil.getGroupBean().getGroup(value.toString());
					if (group == null && lookupField.getName().equals("assignmentGroup")) {
						throw new Exception("Value not found");
					}
					return FieldUtil.getAsProperties(group);
				}
				case "preventivemaintenance": {
					if (value != null) {
						PreventiveMaintenance pm = PreventiveMaintenanceAPI.getPM((long) Double.parseDouble(value.toString()), false);
						if (pm != null) {
							return FieldUtil.getAsProperties(pm);
						}
					}
					return null;
				}
				default : {
					
					List<Map<String, Object>> lookupPropsList = getLookupProps(lookupField, colVal, key, importProcessContext);
					if(CollectionUtils.isNotEmpty(lookupPropsList)) {
						return lookupPropsList.get(0);
					}
				}
			}

		} catch (Exception e) {
			LOGGER.severe("Exception occurred for special lookup: " + e.toString());
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
				for (String sy : sys) {
					groupedFields.put(module, sy);
				}
			}

		}
	}

	public static Long getResourceId(ImportProcessContext importProcessContext, HashMap<String, Object> colVal, HashMap<String, String> fieldMapping, Long siteId, int row_no) throws Exception {
		String moduleName = importProcessContext.getModule().getName();
		String assetName = (String) colVal.get(fieldMapping.get(moduleName + "__asset"));
		if (StringUtils.isNotEmpty(assetName)) {
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
		siteName = (String) colVal.get(fieldMapping.get(moduleName + "__site"));
		buildingName = (String) colVal.get(fieldMapping.get(moduleName + "__building"));
		floorName = (String) colVal.get(fieldMapping.get(moduleName + "__floor"));
		spaceName = (String) colVal.get(fieldMapping.get(moduleName + "__spaceName"));
		int start = 1;
		boolean isSpaceImport = ImportAPI.isBaseSpaceExtendedModule(importProcessContext.getModule());
		if (spaceName != null || isSpaceImport) {
			if (isSpaceImport) {
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

		ImportBuildingAction buildingMeta =new ImportBuildingAction();
		ImportFloorAction floorMeta =new ImportFloorAction();
		ImportSpaceAction spaceMeta =new ImportSpaceAction();

		Long siteId = null;
		Long buildingId = null;
		Long floorId = null;
		Long spaceId = null;

		if(siteName != null && !siteName.equals("")) {
			
			 SiteContext site = SpaceAPI.getSite(siteName.trim());
			 
			 if(site != null)
			 {
				siteId = site.getId();
			 } 
			 else {
				 throw new ImportLookupModuleValueNotFoundException(siteName, row_no, fieldMapping.get(moduleName + "__site"), new Exception());
			 }
			if (siteId != null) {
				spaceId = siteId;
			}
		 } else if (importProcessContext.getModule().getName().equals(FacilioConstants.ContextNames.WORK_ORDER) || AssetsAPI.isAssetsModule(importProcessContext.getModule())) {
			siteId = importProcessContext.getSiteId();
			spaceId = siteId;
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
				int level = (int)additionalSpace.get("level");
				if (isSpaceImport) {
					level--;
				}
				tempSpaceId = SpaceAPI.getDependentSpaceId((String) additionalSpace.get("name"), spaceId, level);
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
		
		return getResource(name, null);
	}
	public static ResourceContext getResource(String name,Long siteId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		SelectRecordsBuilder<ResourceContext> resourceBuilder = new SelectRecordsBuilder<ResourceContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE))
																		.module(module)
																		.beanClass(ResourceContext.class)
																		.andCustomWhere("NAME = ?", name.replace(",", StringOperators.DELIMITED_COMMA))
																		;
		if(siteId != null && siteId > 0) {
			resourceBuilder.andCustomWhere("SITE_ID = ?", siteId);
		}
		List<ResourceContext> resources = resourceBuilder.get();
		if(resources != null && !resources.isEmpty()) {
			return resources.get(0);
		}
		return null;
	}

	public static boolean isLocationFieldMapped(HashMap<String, String> fieldMapping, FacilioField field, String moduleName) {
		return (fieldMapping.containsKey(moduleName + "__" + field.getName() + "_name")) || fieldMapping.containsKey(moduleName + "__" + field.getName() + "_street") || fieldMapping.containsKey(moduleName + "__" + field.getName() + "_city") || fieldMapping.containsKey(moduleName + "__" + field.getName() + "_state") || fieldMapping.containsKey(moduleName + "__" + field.getName() + "_country") || fieldMapping.containsKey(moduleName + "__" + field.getName() + "_zip") || fieldMapping.containsKey(moduleName + "__" + field.getName() + "_lat") || fieldMapping.containsKey(moduleName + "__" + field.getName() + "_lng");
	}

	public static boolean isLocationContextNotEmpty(LocationContext location) {
		return location.getName() != null || location.getStreet() != null || location.getCity() != null || location.getCountry() != null || location.getState() != null || location.getZip() != null || location.getLat() != -1 || location.getLng() != -1;
	}

	public static List<Map<String, Object>> getRecordsForMultiLookupField (MultiLookupField lookupField, String value) throws Exception {

		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String lookupModuleName = lookupField.getLookupModule().getName();
		ArrayList<FacilioField> fieldsList = new ArrayList();
		fieldsList.add(FieldFactory.getIdField(lookupField.getLookupModule()));
		fieldsList.add(FieldFactory.getModuleIdField(lookupField.getLookupModule()));
		FacilioField primaryField = bean.getPrimaryField(lookupModuleName);
		fieldsList.add(primaryField);

		FacilioModule lookupModule = bean.getModule(lookupModuleName);

		SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder =  new SelectRecordsBuilder<>()
				.module(lookupModule)
				.select(fieldsList);

		selectBuilder.andCondition(CriteriaAPI.getCondition(primaryField, value.toLowerCase().trim(), StringOperators.IS));

		List<Map<String, Object>> props = selectBuilder.getAsProps();

		return props;

	}

}