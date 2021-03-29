package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.BimImportProcessMappingContext;
import com.facilio.bmsconsole.context.PMIncludeExcludeResourceContext;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PMTriggerContext.TriggerExectionSource;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.TaskTemplate;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.BimAPI;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.ScheduleInfo;
import com.mysql.jdbc.StringUtils;

public class SwitchToAddResourceChain extends FacilioCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(SwitchToAddResourceChain.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
		HashMap<String, List<ReadingContext>>groupedContext = (HashMap<String, List<ReadingContext>>) context.get(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT);
		List<ReadingContext>readingsContext = new ArrayList<ReadingContext>();
		if(groupedContext != null) {
			for(List<ReadingContext> rContext: groupedContext.values()) {
				readingsContext.addAll(rContext);
			}
		}
		FacilioModule facilioModule = null;
		if(importProcessContext.getModule() !=null){
			String moduleName = importProcessContext.getModule().getName();
			facilioModule = bean.getModule(moduleName);
		}else{
			FacilioModule bimModule = ModuleFactory.getBimImportProcessMappingModule();
			List<FacilioField> bimFields = FieldFactory.getBimImportProcessMappingFields();
			
			BimImportProcessMappingContext bimImport = BimAPI.getBimImportProcessMappingByImportProcessId(bimModule,bimFields,importProcessContext.getId());
			
			boolean isBim = (bimImport!=null);
			if(isBim){
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(importProcessContext.getImportJobMeta());
				String moduleName = ((JSONObject)json.get("moduleInfo")).get("module").toString();
				if(moduleName.equals("zonespacerel")){
					facilioModule = ModuleFactory.getZoneRelModule(); 
				}
			}
		}
		
		if(facilioModule.getName().equals(FacilioConstants.ContextNames.ASSET) ||
				(facilioModule.getExtendModule() != null &&  facilioModule.getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET))
				) {

			if (!ImportAPI.isInsertImport(importProcessContext)) {
				FacilioChain c = TransactionChainFactory.getGenericImportChain();
				Long siteId = importProcessContext.getSiteId();
				c.getContext().put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
				c.getContext().put(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT, groupedContext);
				c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));
				c.getContext().put(FacilioConstants.ContextNames.SITE, siteId);
				c.execute();
			} else {
				FacilioChain bulkAssetImportChain = TransactionChainFactory.getBulkAssetImportChain();
				bulkAssetImportChain.getContext().put(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT, importProcessContext);
				bulkAssetImportChain.getContext().put(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT, groupedContext);
				bulkAssetImportChain.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));
				bulkAssetImportChain.execute();
			}
		}
		
		else if(facilioModule.getName().equals(FacilioConstants.ContextNames.SITE) 
				|| facilioModule.getName().equals(FacilioConstants.ContextNames.BUILDING) 
				|| facilioModule.getName().equals(FacilioConstants.ContextNames.FLOOR) 
				|| facilioModule.getName().equals(FacilioConstants.ContextNames.SPACE) 
				|| facilioModule.getExtendModule() != null && facilioModule.getExtendModule().getName().equals(FacilioConstants.ContextNames.BASE_SPACE)) {
			
				FacilioChain c = TransactionChainFactory.getGenericImportChain();
				c.execute(context);
		}
		
		else if(facilioModule.getName().equals(FacilioConstants.ContextNames.PURCHASED_ITEM)) {
			JSONObject importMeta = importProcessContext.getImportJobMetaJson();
			Long storeRoom;
			JSONObject moduleStaticFields = (JSONObject) importMeta.get(ImportAPI.ImportProcessConstants.MODULE_STATIC_FIELDS);
			if(moduleStaticFields != null && !moduleStaticFields.isEmpty()) {
				storeRoom = (Long)moduleStaticFields.get(FacilioConstants.ContextNames.STORE_ROOM);
				context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoom);
			}
			
			
			List<PurchasedItemContext> items = new ArrayList<PurchasedItemContext>();
			for(ReadingContext readingContext : readingsContext) {
				Map<String, Object> temp = readingContext.getData();
				items.add(FieldUtil.getAsBeanFromMap(temp, PurchasedItemContext.class));
				
			}
			
			FacilioChain c = TransactionChainFactory.getImportItemChain();
			context.put(FacilioConstants.ContextNames.RECORD_LIST, items);
			c.execute(context);
		}
		
		else if(facilioModule.getName().equals(FacilioConstants.ContextNames.PURCHASED_TOOL)) {
			JSONObject importMeta = importProcessContext.getImportJobMetaJson();
			Long storeRoom;
			JSONObject moduleStaticFields = (JSONObject) importMeta.get(ImportAPI.ImportProcessConstants.MODULE_STATIC_FIELDS);
			if(moduleStaticFields != null && !moduleStaticFields.isEmpty()) {
				storeRoom = (Long)moduleStaticFields.get(FacilioConstants.ContextNames.STORE_ROOM);
				context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoom);
			}
		
			List<PurchasedToolContext> tools = new ArrayList<PurchasedToolContext>();
			for(ReadingContext readingContext: readingsContext) {
				Map<String, Object> temp = readingContext.getData();
				tools.add(FieldUtil.getAsBeanFromMap(temp, PurchasedToolContext.class));
			}
			
			FacilioChain c = TransactionChainFactory.getImportToolChain();
			context.put(FacilioConstants.ContextNames.RECORD_LIST, tools);
			c.execute(context);
		} 
		else if(facilioModule.getName().equals(FacilioConstants.ContextNames.WORK_ORDER) && ImportAPI.isInsertImport(importProcessContext)) {
			
			context.put(ImportAPI.ImportProcessConstants.READINGS_CONTEXT_LIST, readingsContext);
			Long siteId = importProcessContext.getSiteId();
			context.put(FacilioConstants.ContextNames.SITE, siteId);
			FacilioChain c = TransactionChainFactory.getBulkWorkOrderImportChain();
			c.execute(context);

		}
		else if(facilioModule.getName().equals(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE)) {
			
			for(ReadingContext reading :readingsContext) {
				Map<String, Object> props = FieldUtil.getAsProperties(reading);
				
				props = removeNullNodes.apply(props);
				
				if(reading.getId() <= 0) {
					
					Long statusId = -1l;
					Long priorityId = -1l;
					Long categoryId = -1l;
					Long typeId = -1l;
					Long assignmentGroupId = -1l;
					Long assignedToId = -1l;
					Long tenantId = -1l;
					Long vendorId = -1l;
					Long safetyPlanId = -1l;
					Integer triggerFrequency = -1;
					Long baseSpaceId = -1l;
					Long assetCategoryId = null,spaceCategoryId = null;
					ResourceContext resource = null;
					
					if(props.get("statusId") != null) {
						statusId = (Long) ((Map<String,Object>)props.remove("statusId")).get("id");
					}
					if(props.get("priorityId") != null) {
						priorityId = (Long) ((Map<String,Object>)props.remove("priorityId")).get("id");
					}
					if(props.get("categoryId") != null) {
						categoryId = (Long) ((Map<String,Object>)props.remove("categoryId")).get("id");
					}
					if(props.get("typeId") != null) {
						typeId = (Long) ((Map<String,Object>)props.remove("typeId")).get("id");
					}
					if(props.get("assignmentGroupId") != null) {
						assignmentGroupId = (Long) ((Map<String,Object>)props.remove("assignmentGroupId")).get("id");
					}
					if(props.get("assignedToId") != null) {
						assignedToId = (Long) ((Map<String,Object>)props.remove("assignedToId")).get("ouid");
					}
					if(props.get("tenantId") != null) {
						tenantId = (Long) ((Map<String,Object>)props.remove("tenantId")).get("id");
					}
					if(props.get("vendorId") != null) {
						vendorId = (Long) ((Map<String,Object>)props.remove("vendorId")).get("id");
					}
					if(props.get("safetyPlanId") != null) {
						safetyPlanId = (Long) ((Map<String,Object>)props.remove("safetyPlanId")).get("id");
					}
					if(props.get("resourceName") != null) {
						resource = ProcessImportCommand.getResource((String)props.remove("resourceName"));
					}
					if(props.get("triggerFrequency") != null) {
						triggerFrequency = Integer.parseInt((String)props.get("triggerFrequency"));
					}
					if(props.get("baseSpaceId") != null) {
						baseSpaceId = (Long) ((Map<String,Object>)props.remove("baseSpaceId")).get("id");
					}
					if(props.get("assetCategoryId") != null) {
						assetCategoryId = (Long) ((Map<String,Object>)props.remove("assetCategoryId")).get("id");
					}
					if(props.get("spaceCategoryId") != null) {
						spaceCategoryId = (Long) ((Map<String,Object>)props.remove("spaceCategoryId")).get("id");
					}
					
					LOGGER.debug("wo props -> "+props);
					
					WorkorderTemplate woTemplate = FieldUtil.getAsBeanFromMap(props, WorkorderTemplate.class);
					PreventiveMaintenance pm = FieldUtil.getAsBeanFromMap(props, PreventiveMaintenance.class);
					if(props.get("triggerName") != null) {
						PMTriggerContext trigger = FieldUtil.getAsBeanFromMap(props, PMTriggerContext.class);
						
						trigger.setName((String) props.get("triggerName"));
						
						trigger.setFrequency(FacilioFrequency.valueOf(triggerFrequency));
						
						ScheduleInfo schedule = fillAndGetScheduleInfo(props,trigger.getFrequencyEnum());
						
						trigger.setSchedule(schedule);
						
						trigger.setTriggerExecutionSource(TriggerExectionSource.SCHEDULE);
						
						pm.addTriggers(trigger);
					}
					
					pm.setModifiedById(-1);
					
					pm.setBaseSpaceId(baseSpaceId);
					
					pm.setAssetCategoryId(assetCategoryId);
					pm.setSpaceCategoryId(spaceCategoryId);
					
					woTemplate.setStatusId(statusId);
					woTemplate.setCategoryId(categoryId);
					woTemplate.setPriorityId(priorityId);
					woTemplate.setTypeId(typeId);
					woTemplate.setAssignmentGroupId(assignmentGroupId);
					woTemplate.setAssignedToId(assignedToId);
					woTemplate.setTenantId(tenantId);
					woTemplate.setVendorId(vendorId);
					woTemplate.setSafetyPlanId(safetyPlanId);
					if(resource != null) {
						woTemplate.setResource(resource);
						woTemplate.setResourceId(resource.getId());
					}
					pm.setName(woTemplate.getSubject());
					pm.setTitle(woTemplate.getSubject());
					
					FacilioChain addPM = FacilioChainFactory.getAddNewPreventiveMaintenanceChain();
					
					FacilioContext contextNew = addPM.getContext();
					
					contextNew.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
					contextNew.put(FacilioConstants.ContextNames.WORK_ORDER, woTemplate.getWorkorder());
					contextNew.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Type.PM_WORKORDER);
					contextNew.put(FacilioConstants.ContextNames.SKIP_WO_CREATION,true);
			 		
					addPM.execute();
				}
			}
		}
		else if(facilioModule.getName().equals(ModuleFactory.getTaskTemplateModule().getName())) {
			
			Map<Long,List<TaskTemplate>> pmMap = new HashMap<Long, List<TaskTemplate>>();
			for(ReadingContext reading :readingsContext) {
				Map<String, Object> props = FieldUtil.getAsProperties(reading);
				
				props = removeNullNodes.apply(props);
				
				if(reading.getId() <= 0) {
					
					String options = null;
					String defaultValue = null;
					String trueVal = null,falseVal = null;
					Long pmId = null;
					JSONArray optionsList = new JSONArray();
					String sectionName = null;
					
					if(props.get("pmId") != null) {
						pmId = Double.valueOf(props.remove("pmId").toString()).longValue();
					}
					if(props.get("defaultValue") != null) {
						defaultValue = props.remove("defaultValue").toString();
					}
					if(props.get("trueVal") != null) {
						trueVal = props.remove("trueVal").toString();
					}
					if(props.get("falseVal") != null) {
						falseVal = props.remove("falseVal").toString();
					}
					if(props.get("options") != null) {
						options = props.remove("options").toString();
						String[] optionsSplit = options.split(",");
						
						for(int i=0;i<optionsSplit.length;i++) {
							optionsList.add(optionsSplit[i]);
						}
					}
					
					sectionName = (String) props.remove("sectionName");
					
					TaskTemplate taskTemplate = FieldUtil.getAsBeanFromMap(props, TaskTemplate.class);

					taskTemplate.addAdditionInfo("sectionName", sectionName);
					taskTemplate.setSiteId(-1l);
					if(defaultValue != null) {
						taskTemplate.addAdditionInfo("defaultValue", defaultValue);
					}
					if(optionsList != null && !optionsList.isEmpty()) {
						taskTemplate.addAdditionInfo("options", optionsList);
					}
					if(trueVal != null) {
						taskTemplate.addAdditionInfo("truevalue", trueVal);
					}
					if(falseVal != null) {
						taskTemplate.addAdditionInfo("falsevalue", falseVal);
					}
					List<TaskTemplate> tasks = pmMap.getOrDefault(pmId, new ArrayList<TaskTemplate>());
					tasks.add(taskTemplate);
					
					pmMap.put(pmId, tasks);
					
				}
			}
			
			for(Long pmId : pmMap.keySet()) {
				
				PreventiveMaintenance pm = PreventiveMaintenanceAPI.getPMsDetails(Collections.singleton(pmId)).get(0);
				
				List<TaskTemplate> taskTemplates = pmMap.get(pmId);
				
				Map<String,TaskSectionTemplate> sectionMap = new HashMap<String, TaskSectionTemplate>();
				Map<String,TaskSectionTemplate> preRequisiteSectionMap = new HashMap<String, TaskSectionTemplate>();
				int seq = 1;
				for(TaskTemplate taskTemplate : taskTemplates) {
					String sectionName = (String) taskTemplate.getAdditionInfo().get("sectionName");
					
					TaskSectionTemplate sectionTemplate = null;
					if(taskTemplate.isPreRequest()) {
						sectionTemplate = preRequisiteSectionMap.getOrDefault(sectionName, new TaskSectionTemplate());
					}
					else {
						sectionTemplate = sectionMap.getOrDefault(sectionName, new TaskSectionTemplate());
					}
					
					sectionTemplate.setName(sectionName);
					sectionTemplate.setType(Type.PM_TASK_SECTION);
					sectionTemplate.setAssignmentType(PMAssignmentType.CURRENT_ASSET.getVal());
					
					taskTemplate.setAssignmentType(PMAssignmentType.CURRENT_ASSET.getVal());
					taskTemplate.setType(Type.PM_TASK);
					taskTemplate.setSequence(seq++);
					sectionTemplate.addTaskTemplates(taskTemplate);
					
					if(taskTemplate.isPreRequest()) {
						sectionTemplate.setPreRequestSection(true);
						preRequisiteSectionMap.put(sectionName, sectionTemplate);
					}
					else {
						sectionMap.put(sectionName, sectionTemplate);
					}
				}
				
				FacilioChain updatePM = FacilioChainFactory.getUpdateNewPreventiveMaintenanceChain();
				
				FacilioContext newContext = updatePM.getContext();
				
				List<TaskSectionTemplate> sectionList = sectionMap.values().stream().collect(Collectors.toList());
				
				sectionList.addAll(preRequisiteSectionMap.values().stream().collect(Collectors.toList()));
				
				newContext.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(pmId));
				newContext.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
				newContext.put(FacilioConstants.ContextNames.WORK_ORDER, ((WorkorderTemplate)TemplateAPI.getTemplate(pm.getTemplateId())).getWorkorder());
				newContext.put(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES, sectionList);
				newContext.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Type.PM_WORKORDER);
				newContext.put(FacilioConstants.ContextNames.SKIP_WO_CREATION,true);
				
				updatePM.execute();
			}
		}
		else if(facilioModule.getName().equals(ModuleFactory.getPMIncludeExcludeResourceModule().getName())) {
			
			Map<Long,List<PMIncludeExcludeResourceContext>> pmMap = new HashMap<Long, List<PMIncludeExcludeResourceContext>>();
			
			for(ReadingContext reading :readingsContext) {
				Map<String, Object> props = FieldUtil.getAsProperties(reading);
				
				props = removeNullNodes.apply(props);
				
				long resourceId = (Long) ((Map<String,Object>)props.remove("resourceId")).get("id");
				
				PMIncludeExcludeResourceContext inclExcl = FieldUtil.getAsBeanFromMap(props, PMIncludeExcludeResourceContext.class);
				
				inclExcl.setResourceId(resourceId);

				inclExcl.setParentType(PMIncludeExcludeResourceContext.ParentType.PM.getVal());
				List<PMIncludeExcludeResourceContext> inclExclList = pmMap.getOrDefault(inclExcl.getPmId(), new ArrayList<PMIncludeExcludeResourceContext>());
				
				inclExclList.add(inclExcl);
				
				pmMap.put(inclExcl.getPmId(), inclExclList);
					
			}
			
			for(Long pmId : pmMap.keySet()) {
				
				PreventiveMaintenance pm = PreventiveMaintenanceAPI.getPMsDetails(Collections.singleton(pmId)).get(0);
				
				List<PMIncludeExcludeResourceContext> inclExcls = pmMap.get(pmId);
				
				pm.setPmIncludeExcludeResourceContexts(inclExcls);
				
				pm.setResourcePlanners(null);   			// resetting resource planner on incl and excl
				
				FacilioChain updatePM = FacilioChainFactory.getUpdateNewPreventiveMaintenanceChain();
				
				FacilioContext newContext = updatePM.getContext();
				
				WorkorderTemplate template = (WorkorderTemplate)TemplateAPI.getTemplate(pm.getTemplateId());
				
				newContext.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(pmId));
				newContext.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
				newContext.put(FacilioConstants.ContextNames.WORK_ORDER, template.getWorkorder());
				newContext.put(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES, template.getSectionTemplates());
				newContext.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Type.PM_WORKORDER);
				newContext.put(FacilioConstants.ContextNames.SKIP_WO_CREATION,true);
				
				updatePM.execute();
			}
		}
		else if(facilioModule.getName().equals(ModuleFactory.getPMTriggersModule().getName())) {
			
			Map<Long,List<PMTriggerContext>> pmMap = new HashMap<Long, List<PMTriggerContext>>();
			
			for(ReadingContext reading :readingsContext) {
				Map<String, Object> props = FieldUtil.getAsProperties(reading);
				
				props = removeNullNodes.apply(props);
				
				PMTriggerContext trigger = FieldUtil.getAsBeanFromMap(props, PMTriggerContext.class);
				
				trigger.setName((String) props.get("triggerName"));
				
				Integer triggerFrequency = Integer.parseInt((String)props.get("triggerFrequency"));
				
				trigger.setFrequency(FacilioFrequency.valueOf(triggerFrequency));
				
				ScheduleInfo schedule = fillAndGetScheduleInfo(props,trigger.getFrequencyEnum());
				
				trigger.setSchedule(schedule);
				
				trigger.setTriggerExecutionSource(TriggerExectionSource.SCHEDULE);
				
				List<PMTriggerContext> triggerList = pmMap.getOrDefault(trigger.getPmId(), new ArrayList<PMTriggerContext>());
				
				triggerList.add(trigger);
				
				pmMap.put(trigger.getPmId(), triggerList);
					
			}
			
			for(Long pmId : pmMap.keySet()) {
				
				PreventiveMaintenance pm = PreventiveMaintenanceAPI.getPMsDetails(Collections.singleton(pmId)).get(0);
				
				List<PMTriggerContext> triggers = pmMap.get(pmId);
				
				pm.setTriggers(triggers);
				
				pm.setResourcePlanners(null);
				
				FacilioChain updatePM = FacilioChainFactory.getUpdateNewPreventiveMaintenanceChain();
				
				FacilioContext newContext = updatePM.getContext();
				
				WorkorderTemplate template = (WorkorderTemplate)TemplateAPI.getTemplate(pm.getTemplateId());
				
				newContext.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(pmId));
				newContext.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
				newContext.put(FacilioConstants.ContextNames.WORK_ORDER, template.getWorkorder());
				newContext.put(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES, template.getSectionTemplates());
				newContext.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Type.PM_WORKORDER);
				newContext.put(FacilioConstants.ContextNames.SKIP_WO_CREATION,true);
				
				updatePM.execute();
			}
		}
		else if(facilioModule.getName().equals(ModuleFactory.getPMResourcePlannerModule().getName())) {
			
			Map<Long,List<PMResourcePlannerContext>> pmMap = new HashMap<Long, List<PMResourcePlannerContext>>();
			
			for(ReadingContext reading :readingsContext) {
				Map<String, Object> props = FieldUtil.getAsProperties(reading);
				
				props = removeNullNodes.apply(props);
				
				Long assignedToId = null;
				Long resourceId = null;
				if(props.get("assignedToId") != null) {
					assignedToId = (Long) ((Map<String,Object>)props.remove("assignedToId")).get("ouid");
				}				
				if(props.get("resourceId") != null) {
					resourceId = (Long) ((Map<String,Object>)props.remove("resourceId")).get("id");
				}
				PMResourcePlannerContext resourcePlanner = FieldUtil.getAsBeanFromMap(props, PMResourcePlannerContext.class);
				
				resourcePlanner.setAssignedToId(assignedToId);
				resourcePlanner.setResourceId(resourceId);
				
				String triggerNameString = (String) props.get("triggerNames");
				
				if(triggerNameString != null) {
					
					Criteria criteria = new Criteria();
					
					Map<String, FacilioField> triggerMap = FieldFactory.getAsMap(FieldFactory.getPMTriggerFields());
					
					criteria.addAndCondition(CriteriaAPI.getCondition(triggerMap.get("pmId"), resourcePlanner.getPmId()+"", NumberOperators.EQUALS));
					criteria.addAndCondition(CriteriaAPI.getCondition(triggerMap.get("name"), triggerNameString, StringOperators.IS));
					
					List<Map<String, Object>> triggerProps = PreventiveMaintenanceAPI.getPMTriggers(criteria);
					
					if(triggerProps != null && !triggerProps.isEmpty()) {
						List<PMTriggerContext> triggers = FieldUtil.getAsBeanListFromMapList(triggerProps,PMTriggerContext.class);
						resourcePlanner.setTriggerContexts(triggers);
					}
				}
				
				
				List<PMResourcePlannerContext> resourcePlanners = pmMap.getOrDefault(resourcePlanner.getPmId(), new ArrayList<PMResourcePlannerContext>());
				
				resourcePlanners.add(resourcePlanner);
				
				pmMap.put(resourcePlanner.getPmId(), resourcePlanners);
					
			}
			
			for(Long pmId : pmMap.keySet()) {
				
				PreventiveMaintenance pm = PreventiveMaintenanceAPI.getPMsDetails(Collections.singleton(pmId)).get(0);
				
				List<PMResourcePlannerContext> resourcePlanners = pmMap.get(pmId);
				
				pm.setResourcePlanners(resourcePlanners);
				
				FacilioChain updatePM = FacilioChainFactory.getUpdateNewPreventiveMaintenanceChain();
				
				FacilioContext newContext = updatePM.getContext();
				
				WorkorderTemplate template = (WorkorderTemplate)TemplateAPI.getTemplate(pm.getTemplateId());
				
				newContext.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(pmId));
				newContext.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
				newContext.put(FacilioConstants.ContextNames.WORK_ORDER, template.getWorkorder());
				newContext.put(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES, template.getSectionTemplates());
				newContext.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Type.PM_WORKORDER);
				newContext.put(FacilioConstants.ContextNames.SKIP_WO_CREATION,true);
				
				updatePM.execute();
			}
		}
		else {
			FacilioChain c = TransactionChainFactory.getGenericImportChain();
			c.execute(context);
		}
		
		return false;
	}
	
	private ScheduleInfo fillAndGetScheduleInfo(Map<String, Object> props, FacilioFrequency frequencyEnum) {
		
		ScheduleInfo schedule = new ScheduleInfo();
		
		String times = (String) props.get("triggerTimes");
		
		String dates = (String) props.get("triggerDates");
		String months = (String) props.get("triggerMonths");
		String days = (String) props.get("triggerDays");
		String weeks = (String) props.get("triggerWeeks");
		
		schedule.setTimes(splitByCommas.apply(times));
		
		switch(frequencyEnum) {
		case DAILY : {
			schedule.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
		}
		break;
		case WEEKLY : {
			schedule.setFrequencyType(ScheduleInfo.FrequencyType.WEEKLY);
			List<Integer> daysList = splitByCommasAndGetAsIntList.apply(days);
			schedule.setValues(daysList);
		}
		break;
		case MONTHLY : {
			if(dates != null) {
				List<Integer> dateList = splitByCommasAndGetAsIntList.apply(dates);
				schedule.setValues(dateList);
				schedule.setFrequencyType(ScheduleInfo.FrequencyType.MONTHLY_DAY);
			}
			else {
				List<Integer> dayList = splitByCommasAndGetAsIntList.apply(days);
				schedule.setValues(dayList);
				
				schedule.setWeekFrequency(stringToInt.apply(weeks));
				
				schedule.setFrequencyType(ScheduleInfo.FrequencyType.MONTHLY_WEEK);
			}
		}
		break;
		case QUARTERTLY : {
			if(dates != null) {
				List<Integer> dateList = splitByCommasAndGetAsIntList.apply(dates);
				schedule.setValues(dateList);
				
				schedule.setFrequencyType(ScheduleInfo.FrequencyType.QUARTERLY_DAY);
			}
			else {
				
				List<Integer> dayList = splitByCommasAndGetAsIntList.apply(days);
				schedule.setValues(dayList);
				
				schedule.setWeekFrequency(stringToInt.apply(weeks));
				
				schedule.setFrequencyType(ScheduleInfo.FrequencyType.QUARTERLY_WEEK);
			}
			schedule.setMonthValue(stringToInt.apply(months));
		}
		break;
		case HALF_YEARLY : {
			if(dates != null) {
				List<Integer> dateList = splitByCommasAndGetAsIntList.apply(dates);
				schedule.setValues(dateList);
				
				schedule.setFrequencyType(ScheduleInfo.FrequencyType.HALF_YEARLY_DAY);
			}
			else {
				
				List<Integer> dayList = splitByCommasAndGetAsIntList.apply(days);
				schedule.setValues(dayList);
				
				schedule.setWeekFrequency(stringToInt.apply(weeks));
				
				schedule.setFrequencyType(ScheduleInfo.FrequencyType.HALF_YEARLY_WEEK);
			}
			schedule.setMonthValue(stringToInt.apply(months));
		}
		break;
		case ANNUALLY : {
			if(months != null) {
				List<Integer> monthList = splitByCommasAndGetAsIntList.apply(months);
				schedule.setValues(monthList);
			}
			
			if(dates != null) {
				schedule.setYearlyDayValue(stringToInt.apply(dates));
				
				schedule.setFrequencyType(ScheduleInfo.FrequencyType.YEARLY);
			}
			else {
				
				List<Integer> dayList = splitByCommasAndGetAsIntList.apply(days);

				schedule.setYearlyDayOfWeekValues(dayList);
				
				schedule.setWeekFrequency(stringToInt.apply(weeks));
				
				schedule.setFrequencyType(ScheduleInfo.FrequencyType.YEARLY_WEEK);
			}
		}
		break;
		default:
			break;
		
		}
		
		return schedule;
	}

	Function<String, List<String>> splitByCommas = (name) -> StringUtils.split(name, ",", true);

	Function<String,Integer> stringToInt = (StringInt) -> Integer.parseInt(StringInt);
	
	Function<String, List<Integer>> splitByCommasAndGetAsIntList = (name) -> {
		
		List<Integer> returnList = new ArrayList<Integer>();
		List<String> stringList = StringUtils.split(name, ",", true);
		
		for(String s : stringList) {
			returnList.add(stringToInt.apply(s));
		}
		return returnList;
	};
	
	Function<Map<String, Object>,Map<String, Object>> removeNullNodes = (prop) -> {
		Map<String, Object> prop1 = new HashMap<String, Object>();
		for(String key : prop.keySet()) {
			if(prop.get(key) != null) {
				prop1.put(key, prop.get(key));
			}
		}
		return prop1;
	};
	
	}
