package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.AddActivitiesCommand;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.CorrectPMTriggerSelection;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.GetSpaceCategoriesCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.PMJobsContext.PMJobsStatus;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.bmsconsole.context.PMTriggerContext.TriggerExectionSource;
import com.facilio.bmsconsole.context.PMTriggerContext.TriggerType;
import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.context.WorkOrderContext.PreRequisiteStatus;
import com.facilio.bmsconsole.jobs.FailedPMNewScheduler;
import com.facilio.bmsconsole.jobs.PMNewScheduler;
import com.facilio.bmsconsole.templates.*;
import com.facilio.bmsconsole.templates.TaskTemplate.AttachmentRequiredEnum;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.jobplan.PMJobPlanContextV3;
import com.facilio.bmsconsoleV3.util.GlobalScopeUtil;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.ScheduleInfo.FrequencyType;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.var;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.bmsconsole.templates.Template.Type.PM_PRE_REQUEST_SECTION;

public class PreventiveMaintenanceAPI {
	
	private static final Logger LOGGER = Logger.getLogger(PreventiveMaintenanceAPI.class.getName());

	public static long getStartTime(PreventiveMaintenance pm, ScheduleActions action, PMTriggerContext trigger) {
		long startTime = -1;
		long triggerStartTime = getStartTimeInSecond(trigger.getStartTime());
		if (action == ScheduleActions.INIT) {
			startTime = triggerStartTime;
		} else if (action == ScheduleActions.GENERATION || action == ScheduleActions.NIGHTLY) {
			startTime = pm.getWoGeneratedUpto();
			if (triggerStartTime > startTime) {
			    startTime = triggerStartTime;
            }
		}
		return startTime;
	}
	
	public static void addJobPlanSectionsToWorkorderTemplate(PreventiveMaintenance pm,WorkorderTemplate workorderTemplate) {
		try {
			pm.setJobPlanList(getJobPlanV3FromPM(pm.getId()));
			
			if(CollectionUtils.isNotEmpty(pm.getJobPlanList())) {
				
				for(PMJobPlanContextV3 pmJobPlan : pm.getJobPlanList()) {
					workorderTemplate.setJobPlanTaskSections(pmJobPlan.prepareAndGetJobPlanSections());
				}
			}
		}
		catch(Exception e) {
			
			LOGGER.error("ERROR IN APPLYING JOB PLAN TO  PMID: " + pm.getId());
			CommonCommandUtil.emailException(PMNewScheduler.class.getName(), "ERROR IN APPLYING JOB PLAN TO  PMID: " + pm.getId(), e);
		}
	}
	
	private static List<PMJobPlanContext> getJobPlanFromPM(long pmId) throws Exception {
		
		FacilioModule module = ModuleFactory.getPMJobPlanModule();
		List<FacilioField> fields = FieldFactory.getPMJobPlanFields();
		Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("pmId"), pmId+"", NumberOperators.EQUALS))
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			
			List<PMJobPlanContext> pmJobPlans = new ArrayList<>();
			for(Map<String, Object> prop :props) {
				PMJobPlanContext pmJobPlan = FieldUtil.getAsBeanFromMap(prop, PMJobPlanContext.class);
				pmJobPlan.setJobPlanContext(JobPlanApi.getJobPlan(pmJobPlan.getJobPlanId()));
				pmJobPlan.setPmjobPlanTriggers(getJobPlanTriggers(pmJobPlan.getId()));
				pmJobPlans.add(pmJobPlan);
			}
			return pmJobPlans;
		}
		return null;
	}
	
	private static List<PmJobPlanTriggerContext> getJobPlanTriggers(long pmjobPlanId) throws Exception {
		
		FacilioModule module = ModuleFactory.getPMJobPlanTriggerModule();
		List<FacilioField> fields = FieldFactory.getPMJobPlanTriggerFields();
		Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("pmjobPlanId"), pmjobPlanId+"", NumberOperators.EQUALS))
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			
			List<PmJobPlanTriggerContext> pmJobPlanTriggers = new ArrayList<>();
			for(Map<String, Object> prop :props) {
				PmJobPlanTriggerContext pmJobPlanTrigger = FieldUtil.getAsBeanFromMap(prop, PmJobPlanTriggerContext.class);
				pmJobPlanTriggers.add(pmJobPlanTrigger);
			}
			return pmJobPlanTriggers;
		}
		return null;
	}
	
	public static void populateResourcePlanner(PreventiveMaintenance pm) throws Exception {
		Map<Long, PMResourcePlannerContext> resourcePlanners = getPMResourcesPlanner(pm.getId());
		populateResourcePlanner(pm, resourcePlanners);
	}

	public static void populateResourcePlanner(PreventiveMaintenance pm, Map<Long, PMResourcePlannerContext> resourcePlanners) throws Exception {
		Collection<PMResourcePlannerContext> resourcePlannerContexts = resourcePlanners.values();
		WorkorderTemplate workorderTemplate = pm.getWoTemplate();
		if (workorderTemplate == null) {
			workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(pm.getTemplateId());
		}
		for (PMResourcePlannerContext resourcePlanner :resourcePlannerContexts) {
			if (resourcePlanner.getTriggerContexts() == null || resourcePlanner.getTriggerContexts().isEmpty()) {
				boolean defaultAllTriggers = pm.getDefaultAllTriggers() != null && pm.getDefaultAllTriggers();
				resourcePlanner.setTriggerContexts(PreventiveMaintenanceAPI.getDefaultTrigger(defaultAllTriggers, pm.getTriggers()));
			}

			if (resourcePlanner.getAssignedToId() == null || resourcePlanner.getAssignedToId() == -1L) {
				resourcePlanner.setAssignedToId(workorderTemplate.getAssignedToId());
			}
		}

		Long baseSpaceId = pm.getBaseSpaceId();
		if (baseSpaceId == null || baseSpaceId < 0) {
			baseSpaceId = pm.getSiteId();
		}

		List<Long> resourceIds;
		if (pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTI_SITE) {
			if(pm.getAssignmentTypeEnum() == PMAssignmentType.SPACE_CATEGORY && pm.getSiteIds().size() == 1) {
				// When single site is selected we select 1 building.
				resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(), pm.getBaseSpaceId(), pm.getSpaceCategoryId(), pm.getAssetCategoryId(), null, pm.getPmIncludeExcludeResourceContexts(), true);
			}else {
				resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(), pm.getSiteIds(), pm.getSpaceCategoryId(), pm.getAssetCategoryId(), null, pm.getPmIncludeExcludeResourceContexts(), true);
			}
		} else {
			resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),baseSpaceId,pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts(), false);
		}
		for(Long resourceId :resourceIds) {					// construct resource planner for default cases
			if(!resourcePlanners.containsKey(resourceId)) {
				PMResourcePlannerContext pmResourcePlannerContext = new PMResourcePlannerContext();
				pmResourcePlannerContext.setResourceId(resourceId);
				if(pmResourcePlannerContext.getResourceId() != null && pmResourcePlannerContext.getResourceId() > 0) {
					pmResourcePlannerContext.setResource(ResourceAPI.getResource(pmResourcePlannerContext.getResourceId()));
				}

				boolean defaultAllTriggers = pm.getDefaultAllTriggers() != null && pm.getDefaultAllTriggers();
				pmResourcePlannerContext.setTriggerContexts(PreventiveMaintenanceAPI.getDefaultTrigger(defaultAllTriggers, pm.getTriggers()));

				pmResourcePlannerContext.setPmId(pm.getId());
				pmResourcePlannerContext.setAssignedToId(workorderTemplate.getAssignedToId());

				pmResourcePlannerContext.setPmResourcePlannerReminderContexts(Collections.emptyList());

				resourcePlanners.put(resourceId, pmResourcePlannerContext);
			}
		}

		if(resourcePlanners != null) {
			pm.setResourcePlanners(new ArrayList<>(resourcePlanners.values()));
		}
	}

	public static void addReading(TaskContext newTask, TaskContext oldTask, FacilioField field, Context context) throws Exception {
			FacilioModule readingModule = field.getModule();
			ReadingContext reading = new ReadingContext();
			reading.setId(oldTask.getReadingDataId());
			reading.addReading(field.getName(), newTask.getInputValue());
			reading.setTtime(newTask.getInputTime());
			long resourceId = oldTask.getResource().getId();
			reading.setParentId(resourceId);
			if (oldTask.getLastReading() == null) {
				ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(resourceId, field);
				newTask.setLastReading(meta.getValue() != null ? meta.getValue() : -1);
			}

			context.put(FacilioConstants.ContextNames.MODULE_NAME, readingModule.getName());
			context.put(FacilioConstants.ContextNames.READING, reading);
			context.put(FacilioConstants.ContextNames.RECORD, reading);
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
			context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, false);
	}

	public enum ScheduleActions {
		INIT,
		GENERATION,
		NIGHTLY;

		public int getVal() {
			return ordinal() + 1;
		}

		public static ScheduleActions getEnum(int val) {
			if (val > 0 && val <= values().length) {
				return values()[val - 1];
			}
			return null;
		}
	}

	public static void schedulePM(long pmId, ScheduleActions action, long endTime) throws Exception {
		LOGGER.log(Level.ERROR, "schedulePM(): pmId = " + pmId);
		JSONObject jobProp = new JSONObject();
		jobProp.put("endTime", endTime);
		jobProp.put("action", action.getVal());

		BmsJobUtil.deleteJobsWithProps(Collections.singletonList(pmId), "ScheduleNewPM");
		BmsJobUtil.scheduleOneTimeJobWithProps(pmId, "ScheduleNewPM", 1, "priority", jobProp);
	}

	public static List<PMTriggerContext> getDefaultTrigger(boolean isDefaultAllTriggers, List<PMTriggerContext> triggers) {
		if (isDefaultAllTriggers) {
			return triggers;
		} else {
			for(PMTriggerContext trigger :triggers) {
				if(trigger.getTriggerType() == TriggerType.DEFAULT.getVal()) {
					return Arrays.asList(trigger);
				}
			}
		}
		return null;
	}

	public static Map<Long, PMReminder> getReminderMap(List<PMReminder> reminders) {
		Map<Long, PMReminder> pmReminderMap = new HashMap<>();
		if(reminders != null) {
			for(PMReminder reminder :reminders) {
				pmReminderMap.put(reminder.getId(), reminder);
			}
		}
		return pmReminderMap;
	}
	public static void applySectionSettingsIfApplicable(TaskSectionTemplate sectiontemplate, TaskTemplate taskTemplate) throws Exception {
		
		if(taskTemplate.getAttachmentRequiredEnum() != null && taskTemplate.getAttachmentRequiredEnum().equals(AttachmentRequiredEnum.USE_PARENT)) {
			taskTemplate.setAttachmentRequired(sectiontemplate.getAttachmentRequired());
		}
		if(taskTemplate.getInputType() <= InputType.NONE.getVal() && sectiontemplate.getInputType() > InputType.NONE.getVal()) {
			taskTemplate.setInputType(sectiontemplate.getInputType());
			taskTemplate.setAdditionalInfoJsonStr(sectiontemplate.getAdditionalInfoJsonStr());
		}
	}

	public static Map<String, List<TaskContext>> getTaskMapForNewPMExecution(Context context, List<TaskSectionTemplate> sectiontemplates, Long woResourceId, Long triggerId, boolean isMultiSite) throws Exception {
		return getTaskMapForNewPMExecution(null, context, sectiontemplates, woResourceId, triggerId, isMultiSite);
	}
	public static Map<String, List<TaskContext>> getTaskMapForNewPMExecution(List<TaskSectionTemplate> sectiontemplates, Long woResourceId, Long triggerId, boolean isMultiSite) throws Exception {
		return getTaskMapForNewPMExecution(null, new FacilioContext(), sectiontemplates, woResourceId, triggerId, isMultiSite);
	}

	public static Map<String, List<TaskContext>> getTaskMapForNewPMExecution(WorkorderTemplate workorderTemplate, List<TaskSectionTemplate> sectiontemplates, Long woResourceId, Long triggerId, boolean isMultiSite) throws Exception {
		return getTaskMapForNewPMExecution(workorderTemplate, new FacilioContext(), sectiontemplates, woResourceId, triggerId, isMultiSite);
	}

	public static Map<String, List<TaskContext>> getTaskMapForNewPMExecution(WorkorderTemplate workorderTemplate, Context context, List<TaskSectionTemplate> sectiontemplates, Long woResourceId, Long triggerId, boolean isMultiSite) throws Exception {
		Map<String, List<TaskContext>> taskMap = new LinkedHashMap<>();
		List<String> sectionNameList = new ArrayList<>();
		int uniqueId = 1;
		int sequenceId = 1;
		for(TaskSectionTemplate sectiontemplate :sectiontemplates) {
			if (triggerId != null && triggerId > -1) {
				List<PMTriggerContext> triggerContexts = sectiontemplate.getPmTriggerContexts();
				if (triggerContexts != null && !triggerContexts.isEmpty()) {
					boolean found = false;
					for (int i = 0; i < triggerContexts.size(); i++) {
						if (triggerContexts.get(i).getId() == triggerId) {
							found = true;
							break;
						}
					}
					if (!found) {
						continue;
					}
				}
			}

			List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(PMAssignmentType.valueOf(sectiontemplate.getAssignmentType()), woResourceId, sectiontemplate.getSpaceCategoryId(), sectiontemplate.getAssetCategoryId(),sectiontemplate.getResourceId(),sectiontemplate.getPmIncludeExcludeResourceContexts(), isMultiSite);
			if (isMultiSite && CollectionUtils.isEmpty(resourceIds)) {
				//return taskMap; // returning taskMap here (when the section has no resources in scope), neglects the section next to the current section which are in scope.
				continue;
			}

			if (CollectionUtils.isEmpty(resourceIds)) {
				long woRId = woResourceId == null ? -1 : woResourceId;
				long spaceCategoryId = sectiontemplate.getSpaceCategoryId() == null ? -1 : sectiontemplate.getSpaceCategoryId();
				long assetCategoryId = sectiontemplate.getAssetCategoryId() == null ? -1 : sectiontemplate.getAssetCategoryId();
				long rId = sectiontemplate.getResourceId() == null ? -1 : sectiontemplate.getResourceId();

				LOGGER.log(Level.ERROR, "resource Ids in getTaskMapForNewPMExecution is empty " + Arrays.toString(resourceIds.toArray()) + " Assignment type " + sectiontemplate.getAssignmentType() + " woResourceId " + woRId  + " space categoryId " +  spaceCategoryId + " asset categoryId " + assetCategoryId + " resource id " + rId);
			}

			 Map<String, Integer> dupSectionNameCount = new HashMap<>();
			LOGGER.log(Level.ERROR,"getTaskMapForNewPMExecution(): Length of resourceIds: " + resourceIds.size()); // can be changed to info level
			 for(Long resourceId :resourceIds) {
				 if(resourceId == null || resourceId < 0) {
					 continue;
				 }
				 ResourceContext sectionResource = getResource(context, resourceId);

				 String sectionName = sectionResource.getName() + " - " +sectiontemplate.getName();

				 if (taskMap.containsKey(sectionName)) {
					 Integer count = dupSectionNameCount.get(sectionName);
					 if (count == null) {
					 	count = 0;
					 }
					 count = count + 1;
					 dupSectionNameCount.put(sectionName, count);
					 sectionName += sectionName + " - " + count;
				 }

				 int sectionAssignmentType = sectiontemplate.getAssignmentType();
				 List<TaskTemplate> taskTemplates = sectiontemplate.getTaskTemplates();
				 
				 List<TaskContext> tasks = new ArrayList<TaskContext>();
				 for(TaskTemplate taskTemplate :taskTemplates) {

					 List<Long> taskResourceIds = null;
				 	try {
						taskResourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(PMAssignmentType.valueOf(taskTemplate.getAssignmentType()), sectionResource.getId(), taskTemplate.getSpaceCategoryId(), taskTemplate.getAssetCategoryId(),taskTemplate.getResourceId(),taskTemplate.getPmIncludeExcludeResourceContexts(), isMultiSite);
					} catch (Exception ex) {
						LOGGER.log(Level.ERROR, "exception ocurred for task " + taskTemplate.getId() + " section " + taskTemplate.getSectionId());
						throw ex;
					}

					 applySectionSettingsIfApplicable(sectiontemplate,taskTemplate);
					 for(Long taskResourceId :taskResourceIds) {
					 	if (sectionAssignmentType == PMAssignmentType.ASSET_CATEGORY.getVal() || sectionAssignmentType == PMAssignmentType.SPACE_CATEGORY.getVal()) {
							if (ObjectUtils.compare(taskResourceId, resourceId) != 0) {
								continue;
							}
						}
						 ResourceContext taskResource = getResource(context, taskResourceId);

					 	TaskContext task = taskTemplate.getTask();
					 	task.setResource(taskResource);
					 	task.setSiteId(taskResource.getSiteId());
						 task.setUniqueId(uniqueId++);
						 task.setSequence(sequenceId++);
					 	tasks.add(task);
					 }
				 }
				 taskMap.put(sectionName, tasks);
				 sectionNameList.add(sectionName);
			 }
		}
		if(workorderTemplate != null) {
			workorderTemplate.setSectionNameList(sectionNameList);
		}
		if(sectionNameList != null){
			LOGGER.log(Level.ERROR,"getTaskMapForNewPMExecution(): Length of sectionNameList: " + sectionNameList.size()); // can be changed to info level
		}
		return taskMap;
	}

	public static Map<String, List<TaskContext>> getPreRequestMapForNewPMExecution(
			List<TaskSectionTemplate> sectiontemplates, Long woResourceId, Long triggerId, boolean isMultiSite) throws Exception {
		Map<String, List<TaskContext>> taskMap = new LinkedHashMap<>();
		for (TaskSectionTemplate sectiontemplate : sectiontemplates) {
			if (triggerId != null && triggerId > -1) {
				List<PMTriggerContext> triggerContexts = sectiontemplate.getPmTriggerContexts();
				if (triggerContexts != null && !triggerContexts.isEmpty()) {
					boolean found = false;
					for (int i = 0; i < triggerContexts.size(); i++) {
						if (triggerContexts.get(i).getId() == triggerId) {
							found = true;
							break;
						}
					}
					if (!found) {
						continue;
					}
				}
			}
			Template sectionTemplate = TemplateAPI.getTemplate(sectiontemplate.getId());
			sectiontemplate = (TaskSectionTemplate) sectionTemplate;
			List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(
					PMAssignmentType.valueOf(sectiontemplate.getAssignmentType()), woResourceId,
					sectiontemplate.getSpaceCategoryId(), sectiontemplate.getAssetCategoryId(),
					sectiontemplate.getResourceId(), sectiontemplate.getPmIncludeExcludeResourceContexts(), isMultiSite);
			Map<String, Integer> dupSectionNameCount = new HashMap<>();
			for (Long resourceId : resourceIds) {
				if (resourceId == null || resourceId < 0) {
					continue;
				}
				ResourceContext sectionResource = ResourceAPI.getResource(resourceId);
				String sectionName = sectiontemplate.getName();

				if (taskMap.containsKey(sectionName)) {
					Integer count = dupSectionNameCount.get(sectionName);
					if (count == null) {
						count = 0;
					}
					count = count + 1;
					dupSectionNameCount.put(sectionName, count);
					sectionName += sectionName + " - " + count;
				}

				int sectionAssignmentType = sectiontemplate.getAssignmentType();
				List<TaskTemplate> taskTemplates = sectiontemplate.getTaskTemplates();

				List<TaskContext> tasks = new ArrayList<TaskContext>();
				for (TaskTemplate taskTemplate : taskTemplates) {

					List<Long> taskResourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(
							PMAssignmentType.valueOf(taskTemplate.getAssignmentType()), sectionResource.getId(),
							taskTemplate.getSpaceCategoryId(), taskTemplate.getAssetCategoryId(),
							taskTemplate.getResourceId(), taskTemplate.getPmIncludeExcludeResourceContexts(), isMultiSite);

					applySectionSettingsIfApplicable(sectiontemplate, taskTemplate);
					for (Long taskResourceId : taskResourceIds) {
						if (sectionAssignmentType == PMAssignmentType.ASSET_CATEGORY.getVal()
								|| sectionAssignmentType == PMAssignmentType.SPACE_CATEGORY.getVal()) {
							if (ObjectUtils.compare(taskResourceId, resourceId) != 0) {
								continue;
							}
						}
						ResourceContext taskResource = ResourceAPI.getResource(taskResourceId);
						TaskContext task = taskTemplate.getTask();
						task.setResource(taskResource);
						tasks.add(task);
					}
				}
				taskMap.put(sectionName, tasks);
			}
		}
		return taskMap;
	}
	public static long getStartTimeInSecond(long startTime) {
		
		long startTimeInSecond = startTime / 1000;
		startTimeInSecond = startTimeInSecond - 300; //for calculating next execution time

		return startTimeInSecond;
	}

	public static List<Long> getMultipleResourceToBeAddedFromPM(PMAssignmentType pmAssignmentType, Long resourceId, Long spaceCategoryID, Long assetCategoryId, Long currentAssetId, List<PMIncludeExcludeResourceContext> includeExcludeResourceContexts, boolean isMultiSite) throws Exception {
		return getMultipleResourceToBeAddedFromPM(pmAssignmentType, Collections.singletonList(resourceId), spaceCategoryID, assetCategoryId, currentAssetId, includeExcludeResourceContexts, isMultiSite);
	}

	public static List<Long> getMultipleResourceToBeAddedFromPM(PMAssignmentType pmAssignmentType, List<Long> resourceIds,Long spaceCategoryID,Long assetCategoryID,Long currentAssetId, List<PMIncludeExcludeResourceContext> includeExcludeRess, boolean isMultiSite) throws Exception {
		List<Long> includedIds = null;
		List<Long> excludedIds = null;
		if(includeExcludeRess != null && !includeExcludeRess.isEmpty()) {
			for(PMIncludeExcludeResourceContext includeExcludeRes :includeExcludeRess) {
				if(includeExcludeRes.getIsInclude()) {
					includedIds = includedIds == null ? new ArrayList<>() : includedIds; 
					includedIds.add(includeExcludeRes.getResourceId());
				}
				else {
					excludedIds = excludedIds == null ? new ArrayList<>() : excludedIds;
					excludedIds.add(includeExcludeRes.getResourceId());
				}
			}
		}
		if(includedIds != null) {
			if(excludedIds != null) {
				includedIds.removeAll(excludedIds);
			}
			if (!isMultiSite) {
				return includedIds;
			}
		}
		List<Long> selectedResourceIds = new ArrayList<>();
		for (Long resourceId: resourceIds) {
			switch(pmAssignmentType) {
				case ALL_SITES:
					selectedResourceIds.addAll(Collections.singletonList(resourceId));
					break;
				case ALL_BUILDINGS:
					List<BaseSpaceContext> siteBuildingsWithFloors = SpaceAPI.getSiteBuildingsWithFloors(resourceId);
					for (BaseSpaceContext building: siteBuildingsWithFloors) {
						selectedResourceIds.add(building.getId());
					}
					break;
				case ALL_FLOORS:
					List<BaseSpaceContext> floors = SpaceAPI.getBuildingFloors(resourceId);
					for(BaseSpaceContext floor :floors) {
						selectedResourceIds.add(floor.getId());
					}
					break;
				case ALL_SPACES:
					selectedResourceIds.addAll(SpaceAPI.getSpaceIdListForBuilding(resourceId));
					break;
				case SPACE_CATEGORY:
					List<SpaceContext> spaces = SpaceAPI.getSpaceListOfCategory(resourceId, spaceCategoryID);
					for(SpaceContext space :spaces) {
						selectedResourceIds.add(space.getId());
					}
					break;
				case ASSET_CATEGORY:
					List<AssetContext> assets = AssetsAPI.getAssetListOfCategory(assetCategoryID, resourceId);

					for(AssetContext asset :assets) {
						selectedResourceIds.add(asset.getId());
					}
					break;
				case CURRENT_ASSET:
					selectedResourceIds.addAll(Collections.singletonList(resourceId));
					break;
				case SPECIFIC_ASSET:
					selectedResourceIds.addAll(Collections.singletonList(currentAssetId));
					break;
				default:
					break;
			}
		}

		if(excludedIds != null) {
			selectedResourceIds.removeAll(excludedIds);
		}

		// this is to avoid mixup accross sites, when multisite
		List<Long> commonresourceIds = new ArrayList<>();
		if (includedIds != null) {
			for (int i = 0; i < selectedResourceIds.size(); i++) {
				for (int j = 0; j < includedIds.size(); j++) {
					if (selectedResourceIds.get(i).equals(includedIds.get(j))) {
						commonresourceIds.add(selectedResourceIds.get(i));
					}
				}
			}
		} else {
			commonresourceIds = selectedResourceIds;
		}

		return commonresourceIds;
 	}


	public static long getEndTime(long startTime, List<PMTriggerContext> triggers) {
		Optional<PMTriggerContext> minTrigger = triggers.stream().filter(i -> i.getTriggerExecutionSourceEnum() == TriggerExectionSource.SCHEDULE).min(Comparator.comparingInt(PMTriggerContext::getFrequency));

		if(!minTrigger.isPresent()){
			LOGGER.log(Level.ERROR, "getEndTime(): minTrigger not present.");
		}else {
			StringBuilder builder = new StringBuilder("getEndTime(): minTrigger is present. ");
			builder.append("Trigger ID: ").append(minTrigger.get().getId());
			LOGGER.log(Level.ERROR, builder.toString());
		}

		int maxSchedulingDays = minTrigger.get().getFrequencyEnum().getMaxSchedulingDays();
		if (startTime == -1) {
			return DateTimeUtil.getDayStartTime(maxSchedulingDays, true) - 1;
		}

		return startTime + (maxSchedulingDays * 24 * 60 * 60);
	}

	public static boolean lessThenEndDate(Pair<Long, Integer> currentDate, PMTriggerContext trigger) {
		long endDate = trigger.getSchedule().getEndDate();
		if (endDate <= 0) return true;
		return (currentDate.getLeft() * 1000) <= trigger.getSchedule().getEndDate();
	}

	public static BulkWorkOrderContext createBulkWoContextsFromPM(Context context, PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime, long endTime, long minTime, WorkorderTemplate woTemplate) throws Exception {
		LOGGER.info("createBulkWoContextsFromPM()");
		Pair<Long, Integer> nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(Pair.of(startTime, 0));
		int currentCount = pm.getCurrentExecutionCount();
		long currentTime = System.currentTimeMillis();
		boolean isScheduled = false;
		List<Long> nextExecutionTimes = new ArrayList<>();
		LOGGER.log(Level.ERROR, "PM " + pm.getId() + " PM Trigger ID: " + pmTrigger.getId() + " next exec time " + nextExecutionTime.getLeft() + " end time " + endTime);
		while (nextExecutionTime.getLeft() <= endTime &&
				lessThenEndDate(nextExecutionTime, pmTrigger) &&
				(pm.getMaxCount() == -1 || currentCount < pm.getMaxCount()) &&
				(pm.getEndTime() == -1 || nextExecutionTime.getLeft() <= pm.getEndTime())) {

			if ((nextExecutionTime.getLeft() * 1000) < currentTime || nextExecutionTime.getLeft() < minTime) {
				nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(nextExecutionTime);
				if (pmTrigger.getSchedule().getFrequencyTypeEnum() == FrequencyType.DO_NOT_REPEAT) {
					break;
				}
				continue;
			}


			nextExecutionTimes.add(nextExecutionTime.getLeft());

			nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(nextExecutionTime);
			currentCount++;
			if (pmTrigger.getSchedule().getFrequencyTypeEnum() == FrequencyType.DO_NOT_REPEAT) {
				break;
			}
			isScheduled = true;
		}

		LOGGER.info("PM "+ pm.getId() + " PM Trigger ID: "+pmTrigger.getId() + " next exec times " + Arrays.toString(nextExecutionTimes.toArray()));

		if (!isScheduled && pmTrigger.getFrequencyEnum() != FacilioFrequency.ANNUALLY) {
			LOGGER.log(Level.WARN, "No Work orders generated for PM "+ pm.getId() + " PM Trigger ID: "+pmTrigger.getId());
			//CommonCommandUtil.emailAlert("No Work orders generated for pm", "PM "+ pm.getId() + " PM Trigger ID: "+pmTrigger.getId());
		}
		return createBulkContextFromPM(context, pm, pmTrigger, woTemplate, nextExecutionTimes);
	}

	public static long getNextExecutionTimesCountForPMMonitoring(PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime, long endTime, long minTime) {
		LOGGER.debug("getNextExecutionTimesForPMMonitoring()");
		Pair<Long, Integer> nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(Pair.of(startTime, 0));
		int currentCount = pm.getCurrentExecutionCount();
		long currentTime = System.currentTimeMillis();
		boolean isScheduled = false;
		List<Long> nextExecutionTimes = new ArrayList<>();
		LOGGER.info("PM " + pm.getId() + " PM Trigger ID: " + pmTrigger.getId() + " next exec time " + nextExecutionTime.getLeft() + " end time " + endTime);
		while (nextExecutionTime.getLeft() <= endTime &&
				lessThenEndDate(nextExecutionTime, pmTrigger) &&
				(pm.getMaxCount() == -1 || currentCount < pm.getMaxCount()) &&
				(pm.getEndTime() == -1 || nextExecutionTime.getLeft() <= pm.getEndTime())) {

			if ((nextExecutionTime.getLeft() * 1000) < currentTime || nextExecutionTime.getLeft() > pm.getWoGeneratedUpto() || nextExecutionTime.getLeft() < minTime) {
				nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(nextExecutionTime);
				if (pmTrigger.getSchedule().getFrequencyTypeEnum() == FrequencyType.DO_NOT_REPEAT) {
					break;
				}
				continue;
			}


			nextExecutionTimes.add(nextExecutionTime.getLeft());

			nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(nextExecutionTime);
			currentCount++;
			if (pmTrigger.getSchedule().getFrequencyTypeEnum() == FrequencyType.DO_NOT_REPEAT) {
				break;
			}
			isScheduled = true;
		}

		LOGGER.info("PM "+ pm.getId() + " PM Trigger ID: "+pmTrigger.getId() + " next exec times " + Arrays.toString(nextExecutionTimes.toArray()));

		if (!isScheduled && pmTrigger.getFrequencyEnum() != FacilioFrequency.ANNUALLY) {
			LOGGER.log(Level.WARN, "No nextExecutionTimes for PM "+ pm.getId() + " PM Trigger ID: "+pmTrigger.getId());
		}
		if(CollectionUtils.isNotEmpty(nextExecutionTimes)){
			return nextExecutionTimes.size();
		}
		return 0;
	}

	public static void incrementGenerationTime(long pmId, long generatedUpto) throws Exception {
		PreventiveMaintenance updatePm = new PreventiveMaintenance();
		updatePm.setWoGeneratedUpto(generatedUpto);

		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPreventiveMaintenanceFields());
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.fields(Collections.singletonList(fieldMap.get("woGeneratedUpto")))
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(pmId, module));

		updateBuilder.update(FieldUtil.getAsProperties(updatePm));
	}

	private static ResourceContext getResource(Context context, Long resourceId) throws Exception {
		Map<Long, ResourceContext> resourceMap = (Map<Long, ResourceContext>) context.get(FacilioConstants.ContextNames.RESOURCE_MAP);
		if (resourceMap == null) {
			resourceMap = new HashMap<>();
			context.put(FacilioConstants.ContextNames.RESOURCE_MAP, resourceMap);
		}
		ResourceContext resourceContext = resourceMap.get(resourceId);
		if (resourceContext == null) {
			resourceContext = ResourceAPI.getResource(resourceId);
			resourceMap.put(resourceId, resourceContext);
		}
		return resourceContext;
	}

	private static FacilioStatus getStatus(Context context, FacilioStatus.StatusType statusType) throws Exception {
		Map<FacilioStatus.StatusType, FacilioStatus> statusMap = (Map<FacilioStatus.StatusType, FacilioStatus>) context.get(FacilioConstants.ContextNames.STATUS_MAP);
		if (statusMap == null) {
			statusMap = new HashMap<>();
			context.put(FacilioConstants.ContextNames.STATUS_MAP, statusMap);
		}

		FacilioStatus result = statusMap.get(statusType);
		if (result == null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule workorderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
			List<FacilioStatus> statusOfStatusType = TicketAPI.getStatusOfStatusType(workorderModule, statusType);
			result = statusOfStatusType.get(0);
			statusMap.put(statusType, result);
		}

		return result;
	}

	public static BulkWorkOrderContext createBulkContextFromPM(Context context, PreventiveMaintenance pm, PMTriggerContext pmTrigger, WorkorderTemplate workorderTemplate, List<Long> nextExecutionTimes) throws Exception {
		FacilioStatus status = getStatus(context, FacilioStatus.StatusType.PRE_OPEN);
		BulkWorkOrderContext bulkWorkOrderContext = new BulkWorkOrderContext();
		for (long nextExecutionTime: nextExecutionTimes) {
			WorkorderTemplate clonedWoTemplate = FieldUtil.cloneBean(workorderTemplate, WorkorderTemplate.class);

			//PreventiveMaintenanceAPI.addJobPlanSectionsToWorkorderTemplate(pm, workorderTemplate);

			if(CollectionUtils.isNotEmpty(workorderTemplate.getJobPlanTaskSections())) {
				List<TaskSectionTemplate> sectionTemplates = new ArrayList<>();
				for (TaskSectionTemplate sectionTemplate: workorderTemplate.getJobPlanTaskSections()) {
					sectionTemplates.add(sectionTemplate);
					clonedWoTemplate.setTaskTemplates(sectionTemplate.getTaskTemplates());
				}
				clonedWoTemplate.setSectionTemplates(sectionTemplates);
			}

			if (workorderTemplate.getSectionTemplates() != null) {
				List<TaskSectionTemplate> sectionTemplates = new ArrayList<>();
				for (TaskSectionTemplate sectionTemplate: workorderTemplate.getSectionTemplates()) {
					TaskSectionTemplate template = FieldUtil.cloneBean(sectionTemplate, TaskSectionTemplate.class);
					sectionTemplates.add(template);
				}
				clonedWoTemplate.setSectionTemplates(sectionTemplates);
			}
			if(workorderTemplate.getTaskTemplates() != null) {
				for (TaskTemplate taskTemplate: workorderTemplate.getTaskTemplates()) {
					List<TaskTemplate> taskTemplates = new ArrayList<>();
					TaskTemplate template = FieldUtil.cloneBean(taskTemplate, TaskTemplate.class);
					taskTemplates.add(template);
					clonedWoTemplate.setTaskTemplates(taskTemplates);
				}
			}
			
			if (workorderTemplate.getPreRequestSectionTemplates() != null) {
				List<TaskSectionTemplate> sectionTemplates = new ArrayList<>();
				for (TaskSectionTemplate sectionTemplate: workorderTemplate.getPreRequestSectionTemplates()) {
					TaskSectionTemplate template = FieldUtil.cloneBean(sectionTemplate, TaskSectionTemplate.class);
					sectionTemplates.add(template);
				}
				clonedWoTemplate.setPreRequestSectionTemplates(sectionTemplates);
			}
			
			if(workorderTemplate.getPreRequestTemplates() != null) {
				for (TaskTemplate taskTemplate: workorderTemplate.getPreRequestTemplates()) {
					List<TaskTemplate> taskTemplates = new ArrayList<>();
					TaskTemplate template = FieldUtil.cloneBean(taskTemplate, TaskTemplate.class);
					taskTemplates.add(template);
					clonedWoTemplate.setPreRequestTemplates(taskTemplates);
				}
			}

			WorkOrderContext wo = clonedWoTemplate.getWorkorder();
			wo.setScheduledStart(nextExecutionTime * 1000);
			if (clonedWoTemplate.getResourceIdVal() > 0) {
				if (clonedWoTemplate.getResource() != null && clonedWoTemplate.getResource().getId() > 0) {
					wo.setResource(clonedWoTemplate.getResource());
				} else {
					ResourceContext resourceContext = getResource(context, clonedWoTemplate.getResourceIdVal());
					clonedWoTemplate.setResource(resourceContext);
				}
				wo.setSiteId(clonedWoTemplate.getResource().getSiteId());
			}
			int preRequisiteCount= clonedWoTemplate.getPreRequestSectionTemplates() != null ? clonedWoTemplate.getPreRequestSectionTemplates().size() : 0;
			wo.setPrerequisiteEnabled(preRequisiteCount != 0);
			if (wo.getPrerequisiteEnabled()) {
				wo.setPreRequestStatus(PreRequisiteStatus.NOT_STARTED.getValue());
			} else {
				wo.setPreRequestStatus(PreRequisiteStatus.COMPLETED.getValue());
			}			
			wo.setPm(pm);
			wo.setStatus(status);
			wo.setTrigger(pmTrigger);
			wo.setJobStatus(WorkOrderContext.JobsStatus.ACTIVE);
			wo.setSourceType(TicketContext.SourceType.PREVENTIVE_MAINTENANCE);
			if (wo.getSpace() != null && wo.getSpace().getId() != -1){
				wo.setResource(wo.getSpace());
			}

			Map<String, List<TaskContext>> taskMap = null;
			Map<String, List<TaskContext>> taskMapForNewPmExecution = null;	// should be handled in above if too

			boolean isNewPmType = false;

			if(clonedWoTemplate.getSectionTemplates() != null) {
				for(TaskSectionTemplate sectiontemplate : clonedWoTemplate.getSectionTemplates()) {// for new pm_Type section should be present and every section should have a AssignmentType
					if(sectiontemplate.getAssignmentType() < 0) {
						isNewPmType =  false;
						break;
					}
					else {
						isNewPmType = true;
					}
				}
			}

			if(isNewPmType) {
				Long woTemplateResourceId = wo.getResource() != null ? wo.getResource().getId() : -1;
				if(woTemplateResourceId > 0) {
					Long currentTriggerId = pmTrigger.getId();
					boolean isMultiSite = pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTI_SITE;
					taskMapForNewPmExecution = PreventiveMaintenanceAPI.getTaskMapForNewPMExecution(clonedWoTemplate, context, clonedWoTemplate.getSectionTemplates(), woTemplateResourceId, currentTriggerId, isMultiSite);
				}
			} else {
				taskMapForNewPmExecution = clonedWoTemplate.getTasks();
			}

			if(taskMapForNewPmExecution != null) {
				taskMap = taskMapForNewPmExecution;
				logIf(779L, "taskMap: " + taskMap);
			}

			if (taskMap != null && CollectionUtils.isNotEmpty(clonedWoTemplate.getSectionNameList())) {
				wo.setSectionNameList(clonedWoTemplate.getSectionNameList());
			}

			Map<String, List<TaskContext>> preRequestMap = null;
			Map<String, List<TaskContext>> preRequestMapForNewPmExecution = null;

			preRequestMapForNewPmExecution = clonedWoTemplate.getPreRequests();

			if (preRequestMapForNewPmExecution != null) {
				preRequestMap = preRequestMapForNewPmExecution;
			}

			PreventiveMaintenanceAPI.updateResourceDetails(wo, taskMap);

			if (taskMap == null || taskMap.isEmpty()) {
				LOGGER.log(Level.WARN, "task map is empty pm id " + wo.getPm().getId());
			}

			bulkWorkOrderContext.addContexts(wo, taskMap, preRequestMap, wo.getAttachments());
		}

		return bulkWorkOrderContext;
	}

	public static WorkOrderContext createWOContextFromPM(Context context, PreventiveMaintenance pm, PMTriggerContext pmTrigger, WorkorderTemplate woTemplate, long nextExecutionTime) throws Exception {
		Map<String, List<TaskContext>> taskMap = null;

		FacilioStatus status = TicketAPI.getStatus("preopen");
		WorkOrderContext wo = woTemplate.getWorkorder();
		wo.setScheduledStart(nextExecutionTime * 1000);
		if (woTemplate.getResourceIdVal() > 0) {
			if (woTemplate.getResource() != null && woTemplate.getResource().getId() > 0) {
				wo.setResource(woTemplate.getResource());
			} else {
				woTemplate.setResource(ResourceAPI.getResource(woTemplate.getResourceIdVal()));
			}
		}

		wo.setPm(pm);
		wo.setStatus(status);
		wo.setTrigger(pmTrigger);
		wo.setJobStatus(WorkOrderContext.JobsStatus.ACTIVE);

		Map<String, List<TaskContext>> taskMapForNewPmExecution = null;	// should be handled in above if too

		boolean isNewPmType = false;

		if(woTemplate.getSectionTemplates() != null) {
			for(TaskSectionTemplate sectiontemplate : woTemplate.getSectionTemplates()) {// for new pm_Type section should be present and every section should have a AssignmentType
				if(sectiontemplate.getAssignmentType() < 0) {
					isNewPmType =  false;
					break;
				}
				else {
					isNewPmType = true;
				}
			}
		}

		if(isNewPmType) {
			Long woTemplateResourceId = wo.getResource() != null ? wo.getResource().getId() : -1;
			if(woTemplateResourceId > 0) {
				Long currentTriggerId = pmTrigger.getId();
				taskMapForNewPmExecution = PreventiveMaintenanceAPI.getTaskMapForNewPMExecution(woTemplate, woTemplate.getSectionTemplates(), woTemplateResourceId, currentTriggerId, pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTI_SITE);
			}
		} else {
			taskMapForNewPmExecution = woTemplate.getTasks();
		}

		if (AccountUtil.getCurrentOrg().getOrgId() == 92 && (pm.getId() == 15831 || pm.getId() == 16191)) {
			LOGGER.log(Level.WARN, "isNewPmType: "+ isNewPmType + "has sections: " + (woTemplate.getSectionTemplates() != null && !woTemplate.getSectionTemplates().isEmpty()) + "has tasks: "+ (woTemplate.getTasks() != null && !woTemplate.getTasks().isEmpty()));
		}

		if(taskMapForNewPmExecution != null) {
			taskMap = taskMapForNewPmExecution;
		}
		if (taskMap != null && CollectionUtils.isNotEmpty(woTemplate.getSectionNameList())) {
			wo.setSectionNameList(woTemplate.getSectionNameList());
		}

		Map<String, List<TaskContext>> preRequestMap = null;
		Map<String, List<TaskContext>> preRequestMapForNewPmExecution = null;
		isNewPmType = false;

		if (woTemplate.getPreRequestSectionTemplates() != null) {
			for (TaskSectionTemplate sectiontemplate : woTemplate.getPreRequestSectionTemplates()) {
				if (sectiontemplate.getAssignmentType() < 0) {
					isNewPmType = false;
					break;
				} else {
					isNewPmType = true;
				}
			}
		}
		if (isNewPmType) {
			Long woTemplateResourceId = wo.getResource() != null ? wo.getResource().getId() : -1;
			if (woTemplateResourceId > 0) {
				Long currentTriggerId = pmTrigger.getId();
				preRequestMapForNewPmExecution = PreventiveMaintenanceAPI.getTaskMapForNewPMExecution(
						woTemplate.getPreRequestSectionTemplates(), woTemplateResourceId, currentTriggerId, pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTI_SITE);
			}
		} else {
			preRequestMapForNewPmExecution = woTemplate.getPreRequests();
		}

		if (preRequestMapForNewPmExecution != null) {
			preRequestMap = preRequestMapForNewPmExecution;
		}
		wo.setSourceType(TicketContext.SourceType.PREVENTIVE_MAINTENANCE);
		wo.setPm(pm);
		context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
		context.put(FacilioConstants.ContextNames.TASK_MAP, taskMap);
		context.put(FacilioConstants.ContextNames.PRE_REQUEST_MAP, preRequestMap);
		context.put(FacilioConstants.ContextNames.IS_PM_EXECUTION, true);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST, wo.getAttachments());

		//Temp fix. Have to be removed eventually
		PreventiveMaintenanceAPI.updateResourceDetails(wo, taskMap);
		FacilioChain addWOChain = TransactionChainFactory.getAddPreOpenedWorkOrderChain();
		addWOChain.execute(context);

		return wo;
	}


	public static List<Map<String, Object>> createProjectedPMJobs (PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime, long endTime) throws Exception { //Both in seconds
		startTime = pmTrigger.getSchedule().getFrequency() > 1 ? pmTrigger.getStartTime() / 1000 :  startTime;
		long nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(startTime);
		int currentCount = pm.getCurrentExecutionCount();
		List<Map<String, Object>> pmJobs = new ArrayList<>();
		while (nextExecutionTime <= endTime && (pm.getMaxCount() == -1 || currentCount < pm.getMaxCount()) && (pm.getEndTime() == -1 || nextExecutionTime <= pm.getEndTime())) {
			if (nextExecutionTime >= startTime) {
				Map<String, Object> pmJob = new HashMap<>();
				pmJob.put("pmId", pm.getId());
				pmJob.put("pmTriggerId", pmTrigger.getId());
				pmJob.put("nextExecutionTime", nextExecutionTime);
				pmJob.put("projected", true);
				pmJob.put("status", PMJobsStatus.ACTIVE);
				pmJobs.add(pmJob);
			}
			nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(nextExecutionTime);
			currentCount++;
			if(pmTrigger.getSchedule().getFrequencyTypeEnum() == FrequencyType.DO_NOT_REPEAT) {
				break;
			}
		}
		return pmJobs;
	}
	






	public static WorkOrderContext getNextPMWorkOrderContext(long pmTriggerID, long currentTime, boolean onlyActive) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField pmTriggerField = fieldMap.get("trigger");
		FacilioField nextExecutionField = fieldMap.get("createdTime");
		FacilioField isActive = fieldMap.get("jobStatus");
		SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
		selectRecordsBuilder.select(fields)
				.module(module)
				.beanClass(WorkOrderContext.class)
				.andCondition(CriteriaAPI.getCondition(pmTriggerField, String.valueOf(pmTriggerID), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(nextExecutionField, String.valueOf(currentTime * 1000), NumberOperators.GREATER_THAN))
				.andCondition(CriteriaAPI.getCondition(isActive, String.valueOf(WorkOrderContext.JobsStatus.COMPLETED.getValue()), NumberOperators.NOT_EQUALS))
				.andCondition(CriteriaAPI.getCondition(isActive, CommonOperators.IS_NOT_EMPTY))
				.orderBy(nextExecutionField.getColumnName())
				.limit(1)
				.skipModuleCriteria();
		if (onlyActive) {
			selectRecordsBuilder.andCondition(CriteriaAPI.getCondition(isActive, String.valueOf(PMJobsStatus.IN_ACTIVE.getValue()), NumberOperators.NOT_EQUALS));
		}

		List<WorkOrderContext> workOrders = selectRecordsBuilder.get();
		if(workOrders != null && !workOrders.isEmpty()) {
			return workOrders.get(0);
		}
		return null;

	}

	public static void setPreWorkOrderInactive(PreventiveMaintenance pm,WorkOrderContext wo) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		Boolean markIgnored = pm.getMarkIgnoredWo();

		LOGGER.log(Level.INFO, "Log 1 PM ID "+ pm.getId() + " mark Ignored: "+pm.getMarkIgnoredWo() );

		if( markIgnored != null && markIgnored ) {

			PreventiveMaintenance newPm = wo.getPm();
			String pmId = Long.toString(newPm.getId());
			ResourceContext resource = wo.getResource();
			LOGGER.log(Level.INFO, "Log 2 PM ID "+ newPm.getId() + " Resource: "+wo.getResource() );

			//Criteria jobStatusCriteria = new Criteria();
			//jobStatusCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("jobStatus"), 3+"", NumberOperators.EQUALS));
			//jobStatusCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("jobStatus"),CommonOperators.IS_EMPTY));

			SelectRecordsBuilder<WorkOrderContext> selectNewRecordsBuilder = new SelectRecordsBuilder<>();
			selectNewRecordsBuilder.select(fields)
					.module(module)
					.beanClass(WorkOrderContext.class)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), pmId, NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), Long.toString(resource.getId()), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), Long.toString(wo.getId()), NumberOperators.NOT_EQUALS))
					.orderBy("CREATED_TIME desc")
					.limit(1);

			List<WorkOrderContext> workOrderNewContexts = selectNewRecordsBuilder.get();

			if( !(workOrderNewContexts == null || workOrderNewContexts.isEmpty()) ) {
				WorkOrderContext wc = workOrderNewContexts.get(0);
				long actualStartTime = wc.getActualWorkStart();
				if(actualStartTime == -1 ) {
					FacilioChain chain = FacilioChain.getTransactionChain();
					FacilioContext context = chain.getContext();
					context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
					FacilioStatus preWoStatus = TicketAPI.getStatus(module, "Skipped");

					chain.addCommand(new FacilioCommand() {
						@Override
						public boolean executeCommand(Context context) throws Exception {
							StateFlowRulesAPI.updateState(wc, module, preWoStatus, false, context);
							return false;
						}
					});
					chain.addCommand(new AddActivitiesCommand());
					chain.execute();
				}
			}
		}
	}

	public static Map<Long, List<Map<String, Object>>> getPMScheduledWOsFromPMIds(long startTimeInSeconds, long endTimeInSeconds, Criteria filterCriteria) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		long startTime = startTimeInSeconds * 1000;
		long endTime = endTimeInSeconds * 1000;
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<>();
		builder.module(module)
				.beanClass(WorkOrderContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(endTime), NumberOperators.LESS_THAN))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), CommonOperators.IS_EMPTY))
				.andCustomWhere("WorkOrders.PM_ID IS NOT NULL")
				.orderBy("createdTime")
				.skipModuleCriteria();
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		}
		List<WorkOrderContext> workorders = builder.get();
		Map<Long, List<Map<String, Object>>> pmWos = new HashMap<>();
		if (workorders != null && !workorders.isEmpty()) {
			for (WorkOrderContext wo : workorders) {
				Map<String, Object> prop = new HashMap<>();
				prop.put("id", wo.getId());
				prop.put("nextExecutionTime", wo.getScheduledStart());
				prop.put("orgId", wo.getOrgId());
				prop.put("pmId", wo.getPm().getId());
				prop.put("woSubject",wo.getSubject());
				if (wo.getResource() != null && wo.getResource().getId() > 0) {
					prop.put("resourceId", wo.getResource().getId());
				}
				if (wo.getAssignmentGroup() != null) {
					prop.put("assignmentGroupId", wo.getAssignmentGroup().getId());
				}
				if (wo.getAssignedTo() != null) {
					prop.put("assignedToId", wo.getAssignedTo().getId());
				}
				if (wo.getTrigger() != null) {
					prop.put("pmTriggerId", wo.getTrigger().getId());
					
					List<Map<String, Object>> woList = pmWos.get(wo.getTrigger().getId());
					if (woList == null) {
						woList = new ArrayList<>();
						pmWos.put(wo.getTrigger().getId(), woList);
					}
					woList.add(prop);
				}
			}
		}
		return pmWos;
	}

	public static long getNextExecutionTimeForWorkOrder(long workOrderId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("workorder");
		List<FacilioField> fields = modBean.getAllFields("workorder");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField minCreatedTime = FieldFactory.getField("minCreatedTime", "MIN(WorkOrders.CREATED_TIME)", FieldType.NUMBER);

		SelectRecordsBuilder<WorkOrderContext> workOrderBuilder = new SelectRecordsBuilder<>();
		workOrderBuilder.module(module)
				.beanClass(WorkOrderContext.class)
				.select(Arrays.asList(fieldMap.get("pm"), fieldMap.get("createdTime"), fieldMap.get("resource")))
				.andCondition(CriteriaAPI.getIdCondition(workOrderId, module))
				.skipModuleCriteria();
		List<WorkOrderContext> workOrders = workOrderBuilder.get();

		if (CollectionUtils.isEmpty(workOrders)) {
			return -1L;
		}

		WorkOrderContext workOrderContext = workOrders.get(0);
		if (workOrderContext.getPm() == null) {
			return -1L;
		}

		long pmId = workOrderContext.getPm().getId();
		long previousTime = workOrderContext.getCreatedTime();
		long resourceId = workOrderContext.getResource().getId();
		long triggerId = -1L;

		if (workOrderContext.getTrigger() != null) {
			triggerId = workOrderContext.getTrigger().getId();
		}

		SelectRecordsBuilder woSelectBuilder = new SelectRecordsBuilder();
		woSelectBuilder.module(module)
				.select(Arrays.asList(minCreatedTime))
				.setAggregation()
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), String.valueOf(pmId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(previousTime), NumberOperators.GREATER_THAN))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(resourceId), NumberOperators.EQUALS));

		if (triggerId != -1) {
			woSelectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("trigger"), String.valueOf(triggerId), NumberOperators.EQUALS));
		}

		List<Map<String, Object>> props = woSelectBuilder.getAsProps();
		if (props == null || props.isEmpty()) {
			return -1L;
		}

		if (props.get(0).get("minCreatedTime") == null) {
			return -1L;
		}

		return (Long) props.get(0).get("minCreatedTime");
	}

	public static Long getNextExecutionTime(long pmId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("workorder");
		FacilioStatus preopen = TicketAPI.getStatus("preopen");
		List<FacilioField> fields = modBean.getAllFields("workorder");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField minCreatedTime = FieldFactory.getField("minCreatedTime", "MIN(WorkOrders.CREATED_TIME)", FieldType.NUMBER);
		SelectRecordsBuilder woSelectBuilder = new SelectRecordsBuilder();
		woSelectBuilder.module(module)
				.select(Arrays.asList(minCreatedTime))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(preopen.getId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), String.valueOf(pmId), NumberOperators.EQUALS))
				.skipModuleCriteria()
				.setAggregation()
				;
		List<Map<String, Object>> props = woSelectBuilder.getAsProps();
		if (props == null || props.isEmpty()) {
			return null;
		}

		if (props.get(0).get("minCreatedTime") == null) {
			return null;
		}

		return (Long) props.get(0).get("minCreatedTime");
	}

	private static List<Long> deactivateActivateAllPms(List<PreventiveMaintenance> pms) throws Exception {
		List<Long> skippedPms = new ArrayList<>();
		for (PreventiveMaintenance activePm: pms) {
			try {
				PreventiveMaintenance pm = new PreventiveMaintenance();
				pm.setStatus(PMStatus.INACTIVE);

				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(activePm.getId()));
				context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);

				FacilioChain migrationChain = TransactionChainFactory.getPMMigration(activePm.getId());
				migrationChain.execute(context);

				// LOGGER.log(Level.WARN, "Migrated " + activePm.getId());
			} catch (Exception e) {
				// LOGGER.log(Level.WARN, "Failed to migrate PM: " + activePm.getId(), e);
				skippedPms.add(activePm.getId());
			}
		}
		return skippedPms;
	}

	private static Set<Long> activateAllPms(List<PreventiveMaintenance> pms, Set<Long> skipList) throws Exception {
		Set<Long> activateSkipList = new HashSet<>();
		for (PreventiveMaintenance activePm: pms) {
			try {
				if (skipList.contains(activePm.getId())) {
					LOGGER.log(Level.WARN, "Skipping activation: " + activePm.getId());
					continue;
				}
				PreventiveMaintenance pm = new PreventiveMaintenance();
				pm.setStatus(PMStatus.ACTIVE);

				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(activePm.getId()));
				context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);

				FacilioChain addTemplate = TransactionChainFactory.getChangeNewPreventiveMaintenanceStatusChainForMig();
				addTemplate.execute(context);

				// LOGGER.log(Level.WARN, "Activated: " + activePm.getId());
			} catch (Exception e) {
				LOGGER.log(Level.WARN, "Failed to activate PM: " + activePm.getId(), e);
				activateSkipList.add(activePm.getId());
			}
		}
		return activateSkipList;
	}

	private static Set<Long> deactivateAllPms(List<PreventiveMaintenance> pms) {
		Set<Long> skipList = new HashSet<>();
		for (PreventiveMaintenance activePm: pms) {
			try {
				PreventiveMaintenance pm = new PreventiveMaintenance();
				pm.setStatus(PMStatus.INACTIVE);
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(activePm.getId()));
				context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);

				FacilioChain addTemplate = TransactionChainFactory.getChangeNewPreventiveMaintenanceStatusChainForMig();
				addTemplate.execute(context);

				// LOGGER.log(Level.WARN, "Deactivated: " + activePm.getId());
			} catch (Exception e) {
				LOGGER.log(Level.WARN, "Failed to deactivate PM: " + activePm.getId(), e);
				skipList.add(activePm.getId());
			}
		}
		return skipList;
	}


	public static PreventiveMaintenance getActivePM(long id, boolean fetchChildren) throws Exception {
		return getPM(id, true, fetchChildren);
	}
	
	public static PreventiveMaintenance getPM(long id, boolean fetchChildren) throws Exception {
		return getPM(id, false, fetchChildren);
	}
	
	private static PreventiveMaintenance getPM(long id, boolean onlyActive, boolean fetchChildren) throws Exception {
		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module))
														;
		
		if (onlyActive) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			PreventiveMaintenance pm = FieldUtil.getAsBeanFromMap(props.get(0), PreventiveMaintenance.class);
			if (fetchChildren) {
				pm.setPmIncludeExcludeResourceContexts(TemplateAPI.getPMIncludeExcludeList(pm.getId(), null, null));
			}
			return pm;
		}
		return null;
	}
	
	public static List<PreventiveMaintenance> getPM(Criteria criteria) throws Exception {
		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCriteria(criteria)
														;
		
		selectBuilder.andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanListFromMapList(props, PreventiveMaintenance.class);
		}
		return null;
	}
	public static List<PreventiveMaintenance> getAllActivePMs(Criteria filterCriteria) throws Exception {
		return getActivePMs(null, filterCriteria, null);
	}

	public static List<PreventiveMaintenance> getAllActivePMs(Criteria filterCriteria, List<FacilioField> fields) throws Exception {
		return getActivePMs(null, filterCriteria, fields);
	}

	public static List<PreventiveMaintenance> getActivePMs(List<Long> ids, Criteria filterCriteria, List<FacilioField> requiredfields) throws Exception {
		ModuleFactory.getPreventiveMaintenanceModule();
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(fields);
		
		if(filterCriteria == null) {
			filterCriteria = new Criteria();
		}
		
		filterCriteria.addAndCondition(CriteriaAPI.getCondition(pmFieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS));
		
		return getPMs(ids, filterCriteria,null, null, requiredfields);
	}
	
	public static List<PreventiveMaintenance> getPMsDetails(Collection<Long> ids) throws Exception {
		return getPMs(ids, null, null, null, null,true, true);
	}

	public static List<PreventiveMaintenance> getAllPMs(Long orgId, boolean onlyActive) throws Exception {
		
		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														;
		if (onlyActive) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		List<Long> res = null;
		if(props != null && !props.isEmpty()) {
			res = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				Long id = (Long) prop.get("id");
				res.add(id);
			}
			List<PreventiveMaintenance> pms = getPMs(res, null, null, null, null, true);
			return pms;
		}
		return null;
	}

	/*
	 * A PM can have multiple triggers; there is a one-many relationship between Preventive_Maintenance & PM_Triggers
	 * table; earlier PM used to have a single trigger and FREQUENCY_TYPE column used to have the type ID - this
	 *  is not used anymore;
	 *
	 * To preserve existing behavior The generated criteria is preserved and only FREQUENCY_TYPE condition is
	 * replaced with PM_ID equals from matching data from PM_Triggers table; */
	private static Criteria UpdateFrequencyFilterCriteria(Criteria criteria) throws Exception {
		Criteria newCriteria = new Criteria();
		// 'haveFrequencyCriteria' is a flag to keep a track, if criteria has a frequency field
		boolean haveFrequencyCriteria = false;
		for (Map.Entry<String, Condition> cond : criteria.getConditions().entrySet()) {
			if (cond.getValue() != null && cond.getValue().getFieldName().equals("frequency")) {
				haveFrequencyCriteria = true;
				break;
			}
		}

		if(haveFrequencyCriteria) {
			Optional<String> frequencyCriteriaKey = Optional.empty();
			Optional<String> frequencyCriteriaValue = Optional.empty();
			for (Map.Entry<String, Condition> cond : criteria.getConditions().entrySet()) {
				if (cond.getValue() != null &&  cond.getValue().getFieldName().equals("frequency")) {
					frequencyCriteriaKey = Optional.of(cond.getKey());
					frequencyCriteriaValue = Optional.of(cond.getValue().getValue());
				} else {
					// need to add a check if the condition has OR, or NOT conditions.
					newCriteria.addAndCondition(cond.getValue());
				}
			}

			if (frequencyCriteriaKey.isPresent()) {
				FacilioField pmIDField = FieldFactory.getField("PM_ID", "PM_ID", FieldType.NUMBER);
				FacilioField frequencyField = FieldFactory.getField("FREQUENCY", "FREQUENCY", FieldType.NUMBER);

				GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
						.select(Collections.singletonList(pmIDField))
						.table(ModuleFactory.getPMTriggersModule().getTableName())
						.andCondition(CriteriaAPI.getCondition(frequencyField, frequencyCriteriaValue.get(), NumberOperators.EQUALS));

				List<Map<String, Object>> resultSet = selectBuilder.get();
				List<Long> pmsWithGivenFreq = new ArrayList<>();
				resultSet.forEach(record -> pmsWithGivenFreq.add(FacilioUtil.parseLong(record.get("PM_ID"))));
				newCriteria.addAndCondition(CriteriaAPI.getIdCondition(pmsWithGivenFreq, ModuleFactory.getPreventiveMaintenanceModule()));
			}
			return newCriteria;
		}else{
			return criteria;
		}
	}

	public static List<PreventiveMaintenance> getPMs(Collection<Long> ids, Criteria criteria, String searchQuery, JSONObject pagination, List<FacilioField> fields, Boolean... fetchDependencies) throws Exception {
		return getPMs(ids, criteria, searchQuery, pagination, fields, null, fetchDependencies);
	}

	public static List<PreventiveMaintenance> getPMs(Collection<Long> ids, Criteria criteria, String searchQuery, JSONObject pagination, List<FacilioField> fields, String siteFilterValues, Boolean... fetchDependencies) throws Exception {

			FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		if (fields == null || fields.isEmpty()) {
			fields = FieldFactory.getPreventiveMaintenanceFields();
		} else {
			fields = new ArrayList<>(fields);
		}



		FacilioField pmSubjectField = FieldFactory.getAsMap(fields).get("title");
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				//.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.orderBy("Preventive_Maintenance.CREATION_TIME DESC");

		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");

			int offset = ((page - 1) * perPage);
			if (offset < 0) {
				offset = 0;
			}

			selectBuilder.offset(offset);
			selectBuilder.limit(perPage);
		}
		
		if(ids != null && !ids.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getIdCondition(ids, module));
		}
		
		if(criteria != null && !criteria.isEmpty()) {
			criteria = UpdateFrequencyFilterCriteria(criteria);
			selectBuilder.andCriteria(criteria);
		}
		if (searchQuery!= null) {
			 selectBuilder.andCondition(CriteriaAPI.getCondition(pmSubjectField, searchQuery, StringOperators.CONTAINS));
		}
		
		boolean fetchDependency = false;
		boolean setTriggers = false;
		boolean setPmCategoryDescription = false;
		if (fetchDependencies != null && fetchDependencies.length > 0) {
			setTriggers = fetchDependencies[0];
			fetchDependency = fetchDependencies.length > 1 && fetchDependencies[1];
			setPmCategoryDescription = fetchDependencies.length > 2 && fetchDependencies[2];
		}
		
		if (!fetchDependency) {
			fields.addAll(FieldFactory.getWorkOrderTemplateFields());
			FacilioModule woTemplateModule = ModuleFactory.getWorkOrderTemplateModule();
			selectBuilder.innerJoin(woTemplateModule.getTableName()).on(module.getTableName()+".TEMPLATE_ID = "+woTemplateModule.getTableName()+".ID");
		}

		// Add accessible spaces criteria for Multisite PMs
		User user = AccountUtil.getCurrentUser();
		// Add global site switch value check in criteria
		List<Long> globalSwitchSiteValues = GlobalScopeUtil.getGlobalSwitchSiteValues();
		List<Long> accessibleSpaceIds = null;

		if(user != null) {
			List<Long> accessibleSpace = user.getAccessibleSpace();
			if(accessibleSpace !=null && accessibleSpace.size() > 0) {
				accessibleSpaceIds = accessibleSpace;
			}
		}
		StringBuilder builder = new StringBuilder();

		if(CollectionUtils.isNotEmpty(accessibleSpaceIds) && CollectionUtils.isNotEmpty(globalSwitchSiteValues)){
			builder.append(" ( ").append(getPMSiteIDQuery(accessibleSpaceIds)).append(" ) ")
					.append(" AND ")
					.append(" ( ").append(getPMSiteIDQueryForGlobalSiteSwitch(globalSwitchSiteValues)).append(" ) ");
			//selectBuilder.andCustomWhere(getPMSiteIDQuery(accessibleSpaceIds));
			//selectBuilder.andCustomWhere(getPMSiteIDQueryForGlobalSiteSwitch(globalSwitchSiteValues));
		} else if ((CollectionUtils.isNotEmpty(globalSwitchSiteValues) && CollectionUtils.isEmpty(accessibleSpaceIds))) {
			builder.append(" ( ").append(getPMSiteIDQueryForGlobalSiteSwitch(globalSwitchSiteValues)).append(" ) ");
			//selectBuilder.andCustomWhere(getPMSiteIDQueryForGlobalSiteSwitch(globalSwitchSiteValues));
		}else if ((CollectionUtils.isNotEmpty(accessibleSpaceIds) && CollectionUtils.isEmpty(globalSwitchSiteValues))) {
			builder.append(" ( ").append(getPMSiteIDQuery(accessibleSpaceIds)).append(" ) ");
			//selectBuilder.andCustomWhere(getPMSiteIDQuery(accessibleSpaceIds));
		}

		//Adding condition to show the single site PMs
		long singleSiteSiteID = -1L;

		if(CollectionUtils.isNotEmpty(globalSwitchSiteValues) && globalSwitchSiteValues.size() == 1){
			long globalSwitchSiteValue = globalSwitchSiteValues.get(0);
			if(CollectionUtils.isNotEmpty(accessibleSpaceIds)){
				for (Long id: accessibleSpaceIds){
					if(globalSwitchSiteValue == id){
						singleSiteSiteID = globalSwitchSiteValue;
						break;
					}
				}
			}else {
				singleSiteSiteID = globalSwitchSiteValues.get(0);
			}
		}

		if(singleSiteSiteID > 0){
			//selectBuilder.orCondition(CriteriaAPI.getCondition("Preventive_Maintenance.SITE_ID", "siteId", singleSiteSiteID+"", NumberOperators.EQUALS));
			StringBuilder builder1 = new StringBuilder();
			builder1.append(" ( ").append(builder).append(" OR ").append("Preventive_Maintenance.SITE_ID = ").append(singleSiteSiteID).append(" ) ");
			builder = builder1;
			//selectBuilder.orCustomWhere("Preventive_Maintenance.SITE_ID = ?", Collections.singletonList(singleSiteSiteID));
		}
		/*
			Handling for siteId filter.
			This is just a workaround, as we can't remove the siteId condition from criteria,
			since siteId condition takes in WorkOrderTemplate.SITE_ID.
		 */
		if (StringUtils.isNotEmpty(siteFilterValues)) {
			StringBuilder builder1 = new StringBuilder();
			List<Long> siteFilterValueIDs = new ArrayList<>();
			List<String> siteFilterValueStringIDs = Arrays.stream(siteFilterValues.split(",")).collect(Collectors.toList());
			for (String id: siteFilterValueStringIDs){
				siteFilterValueIDs.add(Long.parseLong(id));
			}

			if(!builder.toString().isEmpty()) {
				builder1.append(" ( ").append(builder).append(" AND ").append(getPMSiteIDQueryForGlobalSiteSwitch(siteFilterValueIDs)).append(" ) ");
			}else{
				builder1.append(" ( ").append(builder).append(getPMSiteIDQueryForGlobalSiteSwitch(siteFilterValueIDs)).append(" ) ");
			}
			builder = builder1;
//			selectBuilder.andCustomWhere("( Preventive_Maintenance.ID IN ( select PM_ID from PM_Sites p1 where SITE_ID in ("
//					+ siteFilterValues
//					+ ") group by PM_ID having count(PM_ID) = (select COUNT(PM_ID) from PM_Sites p2 where p1.PM_ID = p2.PM_ID group by PM_ID)))");
		}

		if(!builder.toString().isEmpty()) {
			selectBuilder.andCustomWhere(builder.toString());
		}

		List<Map<String, Object>> pmProps = selectBuilder.get();
		
		List<Long> resourceIds = new ArrayList<>();
		if(pmProps != null && !pmProps.isEmpty()) {
			List<PreventiveMaintenance> pms = new ArrayList<>();
			
			for(Map<String, Object> prop : pmProps) {
				PreventiveMaintenance pm = FieldUtil.getAsBeanFromMap(prop, PreventiveMaintenance.class);
				WorkorderTemplate template;
				if (fetchDependency) {
					template = (WorkorderTemplate) TemplateAPI.getTemplate(pm.getTemplateId());
				}
				else {
					template = FieldUtil.getAsBeanFromMap(prop, WorkorderTemplate.class);
					template.setId(pm.getTemplateId());
				}
				pm.setWoTemplate(template);
				pms.add(pm);
			}
			
			Map<Long, List<PMTriggerContext>> pmTriggers = null;
			if (setTriggers) {
				pmTriggers = PreventiveMaintenanceAPI.getPMTriggers(pms);
			}
			
			for(PreventiveMaintenance pm : pms) {
				if (pm.getWoTemplate().getResourceIdVal() != -1) {
					resourceIds.add(pm.getWoTemplate().getResourceIdVal());
				}
				
				if (pmTriggers != null) {
					pm.setTriggers(pmTriggers.get(pm.getId()));
					if (pm.getTriggers() == null) {
						continue;
					}
					for (PMTriggerContext trigger : pm.getTriggers()) {
						if (trigger.getRuleId() != -1) {
							if (trigger.getTriggerExecutionSourceEnum() == TriggerExectionSource.ALARMRULE) {
								WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(trigger.getRuleId());
								trigger.setWorkFlowRule(rule);
							}
							else if (trigger.getTriggerExecutionSourceEnum() == TriggerExectionSource.READING) {
								ReadingRuleContext rule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(trigger.getRuleId());
								trigger.setReadingFieldId(rule.getReadingFieldId());
								trigger.setReadingInterval(rule.getInterval());
								trigger.setStartReading(rule.getStartValue());
								trigger.setReadingRule(rule);
							}
						}
					}
				}
			}
			Map<Long, ResourceContext> resourceMap = ResourceAPI.getExtendedResourcesAsMapFromIds(resourceIds, true);
			Map<Long, List<Long>> pmSitesMap = new HashMap<>();
			if (pms != null && !pms.isEmpty()) {
				pmSitesMap = PreventiveMaintenanceAPI.getPMSitesMap(pms.stream().map(i -> i.getId()).collect(Collectors.toList()));
			}
			for(PreventiveMaintenance pm : pms) {
				if (pm.getWoTemplate().getResourceIdVal() != -1) {
					pm.getWoTemplate().setResource(resourceMap.get(pm.getWoTemplate().getResourceIdVal()));
				}

				pm.setSiteIds(pmSitesMap.get(pm.getId()));

				// set up data for additional [SITE] column
				if (pm.getSiteIds() != null && pm.getSiteIds().size() > 0) {
					List<SiteContext> siteContextList = SpaceAPI.getSites(pm.getSiteIds());
					pm.setSiteObjects(siteContextList);
				}else if(pm.getSiteId() > 0){
					List<SiteContext> siteContextList = SpaceAPI.getSites(Collections.singletonList(pm.getSiteId()));
					pm.setSiteObjects(siteContextList);
				}

				// set up data for additional [category] column
				if(pm.getWoTemplate() != null && pm.getWoTemplate().getCategoryId() > -1L){
					long categoryID = pm.getWoTemplate().getCategoryId();
					TicketCategoryContext categoryContext = TicketAPI.getCategory(pm.getOrgId(), categoryID);
					pm.setTicketCategory(categoryContext);
				}

				// set up data for additional [vendor name] column
				if(pm.getWoTemplate() != null && pm.getWoTemplate().getVendorId() > -1L){
					long vendorId = pm.getWoTemplate().getVendorId();
					VendorContext vendorContext = (VendorContext) RecordAPI.getRecord(ContextNames.VENDORS, vendorId);
					pm.setVendor(vendorContext);
				}

				// set up data for additional [next execution time] column
				Long nextExecution = PreventiveMaintenanceAPI.getNextExecutionTime(pm.getId());
				if (nextExecution != null) {
					pm.setNextExecutionTime(nextExecution);
				}

				// set up data for additional [last triggered time] column
				if(pm.getWorkorders() == null || pm.getWorkorders().size() == 0){
					Long lastTriggeredTime = getLastTriggeredTime(pm);
					if(lastTriggeredTime > 0){
						pm.setLastTriggeredTime(lastTriggeredTime);
					}
				}
			}
			if (fetchDependency) {
				Map<Long, List<PMReminder>> reminders = PreventiveMaintenanceAPI.getPMRemindersAsMap(ids);
				if (reminders != null) {
					for (PreventiveMaintenance pm : pms) {
						long pmId = pm.getId();
						if (reminders.containsKey(pmId)) {
							List<PMReminder> rms = reminders.get(pmId);
							if (rms != null) {
								pm.setReminders(rms);
								pm.setReminderMap(new HashMap<>());
								for (int l = 0; l < rms.size(); l++) {
									pm.getReminderMap().put(rms.get(l).getName(), rms.get(l));
								}
							}
						}
					}
				}
				
				for(PreventiveMaintenance pm : pms) {
					pm.setPmIncludeExcludeResourceContexts(TemplateAPI.getPMIncludeExcludeList(pm.getId(), null, null));
				}
				
				Map<Long, List<PMResourcePlannerContext>> resourcePlanners = getPMResourcesPlanners(ids);
				Map<Long, Map<Long, List<PMResourcePlannerReminderContext>>> rpReminders = getPmResourcePlannerReminderContexts(ids);
				if (resourcePlanners != null && !resourcePlanners.isEmpty()) {
					for (PreventiveMaintenance pm : pms) {
						long pmId = pm.getId();
						if (resourcePlanners.containsKey(pmId)) {
							List<PMResourcePlannerContext> rps = resourcePlanners.get(pmId);
							if (rps != null) {
								pm.setResourcePlanners(rps);
								for (int k = 0; k < rps.size(); k++) {
									PMResourcePlannerContext rp = rps.get(k);
									if (rpReminders.get(pmId) != null && rpReminders.get(pmId).get(rp.getId()) != null) {
										rp.setPmResourcePlannerReminderContexts(rpReminders.get(pmId).get(rp.getId()));
									}
								}
							}
							
						}
					}
				}
			}

			if (setPmCategoryDescription) {
				List<BuildingContext> allBuildings = SpaceAPI.getAllBuildings();
				Map<Long, BuildingContext> buildingMap = new HashMap<>();
				for (BuildingContext buildingContext: allBuildings) {
					long id = buildingContext.getId();
					buildingMap.put(id, buildingContext);
				}
				Map<Long, SpaceCategoryContext> allSpaceCateg = getAllSpaceCateg();
				Map<Long, AssetCategoryContext> allAssetCateg = getAllAssetCateg();

				var triggerMap = PreventiveMaintenanceAPI.getPMTriggers(pms);

				for(PreventiveMaintenance pm : pms) {
					PreventiveMaintenance.PMCreationType pmCreationTypeEnum = pm.getPmCreationTypeEnum();
					var assignmentType = pm.getAssignmentTypeEnum();
					Long baseSpaceId = pm.getBaseSpaceId();
					BuildingContext buildingContext = buildingMap.get(baseSpaceId);
					var buildingName = buildingContext != null ? buildingContext.getName(): null;

					var triggers = triggerMap.get(pm.getId());
					if (CollectionUtils.isNotEmpty(triggers)) {
						if (triggers.size() == 1) {
							var trigger = triggers.get(0);
							switch (trigger.getTriggerExecutionSourceEnum()) {
								case SCHEDULE:
									ScheduleInfo schedule = trigger.getSchedule();
									if (schedule != null) {
										var frequencyType = trigger.getFrequencyEnum();
										if (frequencyType != null) {
											pm.setPmTriggerDescription(frequencyType.getName());
											pm.setFrequency(frequencyType);
										} else {
											pm.setPmTriggerDescription("---");
										}
									} else {
										pm.setPmTriggerDescription("---");
									}
									break;
								case CUSTOM:
									pm.setPmTriggerDescription("Custom");
									break;
								case ALARMRULE:
									pm.setPmTriggerDescription("Alarm Rule");
									break;
								case USER:
									pm.setPmTriggerDescription("Manual");
									break;
								case READING:
									pm.setPmTriggerDescription("Reading");
									break;
							}
						} else {
							pm.setPmTriggerDescription("Multiple Triggers");
						}
					} else {
						pm.setPmTriggerDescription("---");
					}

					if (pmCreationTypeEnum == PreventiveMaintenance.PMCreationType.SINGLE) {
						WorkorderTemplate woTemplate = pm.getWoTemplate();
						ResourceContext resource = woTemplate.getResource();
						if(resource != null) {
							pm.setPmCategoryDescription(resource.getName());
						}
					} else {
						switch(assignmentType) {
							case ALL_FLOORS:
								if (buildingName != null) {
									pm.setPmCategoryDescription(buildingName + " - " + "Floors");
								} else {
									pm.setPmCategoryDescription("All Floors");
								}
								break;
							case SPACE_CATEGORY:
								SpaceCategoryContext spaceCategoryContext = allSpaceCateg.get(pm.getSpaceCategoryId());
								if (buildingName != null) {
									if (spaceCategoryContext != null) {
										pm.setPmCategoryDescription(buildingName + " - " + spaceCategoryContext.getName());
									} else {
										pm.setPmCategoryDescription("---");
									}
								} else {
									if (spaceCategoryContext != null) {
										pm.setPmCategoryDescription(spaceCategoryContext.getName());
									} else {
										pm.setPmCategoryDescription("---");
									}
								}
								break;
							case ASSET_CATEGORY:
								AssetCategoryContext assetCategoryContext = allAssetCateg.get(pm.getAssetCategoryId());
								if (buildingName != null) {
									if (assetCategoryContext != null) {
										pm.setPmCategoryDescription(buildingName + " - " + assetCategoryContext.getName());
									} else {
										pm.setPmCategoryDescription("---");
									}
								} else {
									if (assetCategoryContext != null) {
										pm.setPmCategoryDescription(assetCategoryContext.getName());
									} else {
										pm.setPmCategoryDescription("---");
									}
								}
								break;
							case ALL_BUILDINGS:
								List<PMIncludeExcludeResourceContext> pmIncludeExcludeList = TemplateAPI.getPMIncludeExcludeList(pm.getId(), null, null);
								if (CollectionUtils.isNotEmpty(pmIncludeExcludeList)) {
									if (pmIncludeExcludeList.size() == 1) {
										var resourceId = pmIncludeExcludeList.get(0).getResourceId();
										ResourceContext resource = ResourceAPI.getResource(resourceId);
										if(resource != null) {
											pm.setPmCategoryDescription(resource.getName());
										}else{
											pm.setPmCategoryDescription("1 - Building(s)");
										}
									} else {
										pm.setPmCategoryDescription(pmIncludeExcludeList.size() + " - Building(s)");
									}
								} else {
									pm.setPmCategoryDescription("---");
								}
								break;
							case ALL_SITES:
								List<Long> siteIds = pm.getSiteIds();
								int count = siteIds != null ? siteIds.size(): 0;
								if (count > 0) {
									if (count == 1) {
										ResourceContext resource = ResourceAPI.getResource(siteIds.get(0));
										if(resource != null) {
											pm.setPmCategoryDescription(resource.getName());
										}else{
											pm.setPmCategoryDescription("1 - Site(s)");
										}
									} else {
										pm.setPmCategoryDescription(count + " - " + "Site(s)");
									}
								} else {
									pm.setPmCategoryDescription("---");
								}
								break;
						}
					}
				}
			}

			return pms;
		}
		return null;
	}

	public static List<PMTriggerContext> getPMTriggersByTriggerIds(Collection<Long> triggerIds) throws Exception {
		FacilioModule module = ModuleFactory.getPMTriggersModule();
		FacilioField idField = FieldFactory.getIdField(module);

		Criteria idCriteria = new Criteria();
		idCriteria.addAndCondition(CriteriaAPI.getCondition(idField, triggerIds, NumberOperators.EQUALS));

		List<Map<String, Object>> props = getPMTriggers(idCriteria);
		List<PMTriggerContext> pmTriggerContexts = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			pmTriggerContexts = FieldUtil.getAsBeanListFromMapList(props, PMTriggerContext.class);
		}
		return pmTriggerContexts;
	}

	private static String getPMSiteIDQuery(List<Long> siteIds){
		return "( Preventive_Maintenance.ID IN ( select PM_ID from PM_Sites p1 where SITE_ID in ("
				+ StringUtils.join(siteIds, ",")
				+") group by PM_ID having count(PM_ID) = (select COUNT(PM_ID) from PM_Sites p2 where p1.PM_ID = p2.PM_ID group by PM_ID)))";
	}

	private static String getPMSiteIDQueryForGlobalSiteSwitch(List<Long> siteIds){
		return "( Preventive_Maintenance.ID IN ( select PM_ID from PM_Sites p1 where SITE_ID in ("
				+ StringUtils.join(siteIds, ",")
				+") group by PM_ID having count(PM_ID) <= (select COUNT(PM_ID) from PM_Sites p2 where p1.PM_ID = p2.PM_ID group by PM_ID)))";
	}

	public static void setPMInActive(long pmId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		PreventiveMaintenance updatePm = new PreventiveMaintenance();
		updatePm.setStatus(PMStatus.INACTIVE);
		
		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(FieldFactory.getPreventiveMaintenanceFields())
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(pmId, module))
														;
		
		updateBuilder.update(FieldUtil.getAsProperties(updatePm));
	}

	public static Map<Long, List<PMTriggerContext>> getPMTriggers(Collection<Long> pmIds) throws Exception {
		String pmIdString = pmIds.stream()
				.map(i -> String.valueOf(i))
				.collect(Collectors.joining(", "));
		return getPMTriggers(pmIdString);
	}

	public static List<Map<String, Object>> getPMTriggers(Criteria filterCriteria) throws Exception {
		FacilioModule module = ModuleFactory.getPMTriggersModule();
		List<FacilioField> fields = FieldFactory.getPMTriggerFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCriteria(filterCriteria);
		return selectBuilder.get();
	}

	private static Map<Long, List<PMTriggerContext>> getPMTriggers(String pmIds) throws Exception {
		List<FacilioField> fields = FieldFactory.getPMTriggerFields();
		FacilioField pmIdField = FieldFactory.getAsMap(fields).get("pmId");

		Criteria idCriteria = new Criteria();
		idCriteria.addAndCondition(CriteriaAPI.getCondition(pmIdField, pmIds, NumberOperators.EQUALS));

		List<Map<String, Object>> triggerProps = getPMTriggers(idCriteria);
		Map<Long, List<PMTriggerContext>> pmTriggers = new HashMap<>();
		for(Map<String, Object> triggerProp : triggerProps) {
			PMTriggerContext trigger = FieldUtil.getAsBeanFromMap(triggerProp, PMTriggerContext.class);

			if (trigger.getRuleId() != -1) {
				if (trigger.getTriggerExecutionSourceEnum() == TriggerExectionSource.ALARMRULE) {
					WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(trigger.getRuleId());
					trigger.setWorkFlowRule(rule);
				}
				else if (trigger.getTriggerExecutionSourceEnum() == TriggerExectionSource.READING) {
					ReadingRuleContext rule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(trigger.getRuleId());
					trigger.setReadingFieldId(rule.getReadingFieldId());
					trigger.setReadingInterval(rule.getInterval());
					trigger.setStartReading(rule.getStartValue());
					trigger.setReadingRule(rule);
				}
			}

			if (trigger.getTriggerExecutionSourceEnum() == TriggerExectionSource.USER) {
				SharingContext<SingleSharingContext> singleSharingContext = SharingAPI.getSharing(trigger.getId(), ModuleFactory.getPMExecSharingModule(), SingleSharingContext.class);
				trigger.setSharingContext(singleSharingContext);
			}

			List<PMTriggerContext> triggerList = pmTriggers.get(trigger.getPmId());
			if(triggerList == null) {
				triggerList = new ArrayList<>();
				pmTriggers.put(trigger.getPmId(), triggerList);
			}
			triggerList.add(trigger);
		}

		return pmTriggers;
	}


	public static Map<Long, List<PMTriggerContext>> getPMTriggers(List<PreventiveMaintenance> pms) throws Exception {
		String pmIds = pms.stream()
				.map(pm -> String.valueOf(pm.getId()))
				.collect(Collectors.joining(", "));
		return getPMTriggers(pmIds);
	}

	public static Map<Long, List<PMResourcePlannerContext>> getPMResourcesPlanners(Collection<Long> pmIds) throws Exception {
		return getPMResourcesPlanners(pmIds, false);
	}
	
	public static Map<Long, List<PMResourcePlannerContext>> getPMResourcesPlanners(Collection<Long> pmIds, boolean onlyName) throws Exception {
		if (pmIds == null || pmIds.isEmpty()) {
			return Collections.emptyMap();
		}
		FacilioModule module = ModuleFactory.getPMResourcePlannerModule();
		FacilioField pmIdField = FieldFactory.getField("pmId", "PM_ID", module, FieldType.LOOKUP);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getPMResourcePlannerFields())
				.table(module.getTableName())
				.innerJoin("Resources")
				.on("Resources.ID = PM_Resource_Planner.RESOURCE_ID  AND (Resources.SYS_DELETED IS NULL OR Resources.SYS_DELETED = 0)")
				.andCondition(CriteriaAPI.getCondition(pmIdField, pmIds, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		Map<Long, List<PMResourcePlannerContext>> result = new HashMap<>();
		Map<Long, PMResourcePlannerContext> resourcePlannerContextMap = new HashMap<>();

		List<Long> resourcePlannerIds = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop: props) {
				PMResourcePlannerContext pmResourcePlannerContext = FieldUtil.getAsBeanFromMap(prop, PMResourcePlannerContext.class);
				if(pmResourcePlannerContext.getResourceId() != null && pmResourcePlannerContext.getResourceId() > 0) {
					ResourceContext resource = ResourceAPI.getResource(pmResourcePlannerContext.getResourceId());
					if (onlyName) {
						pmResourcePlannerContext.setResourceName(resource.getName());
					} else {
						pmResourcePlannerContext.setResource(resource);
					}
				}

				if (!onlyName) {
					List<PMReminder> pmReminders = PreventiveMaintenanceAPI.getPMReminders(pmIds);

					Map<Long, PMReminder> reminderMap = new HashMap<>();

					if (pmReminders != null && !pmReminders.isEmpty()) {
						for (PMReminder rem: pmReminders) {
							reminderMap.put(rem.getId(), rem);
						}
					}
					List<PMResourcePlannerReminderContext> resourcePlannerReminderContexts = PreventiveMaintenanceAPI.getPmResourcePlannerReminderContext(pmResourcePlannerContext.getId());
					if (resourcePlannerReminderContexts != null) {
						for (int i = 0; i < resourcePlannerReminderContexts.size(); i++) {
							PMReminder remContext = reminderMap.get(resourcePlannerReminderContexts.get(i).getReminderId());
							resourcePlannerReminderContexts.get(i).setReminderName(remContext.getName());
						}
						pmResourcePlannerContext.setPmResourcePlannerReminderContexts(resourcePlannerReminderContexts);
					}
				}

				long pmId = (long) prop.get("pmId");
				if (!result.containsKey(pmId)) {
					result.put(pmId, new ArrayList<>());
				}
				resourcePlannerIds.add(pmResourcePlannerContext.getId());
				resourcePlannerContextMap.put(pmResourcePlannerContext.getId(), pmResourcePlannerContext);
				result.get(pmId).add(pmResourcePlannerContext);
			}
		}

		if (!resourcePlannerIds.isEmpty() && !onlyName) {
			FacilioField field = FieldFactory.getField("resourcePlannerId", "PM_RESOURCE_PLANNER_ID", ModuleFactory.getPMResourcePlannerTriggersModule(), FieldType.LOOKUP);
			List<FacilioField> fields = new ArrayList<>(FieldFactory.getPMResourcePlannerTriggersFields());
			fields.addAll(FieldFactory.getPMTriggerFields());
			GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getPMResourcePlannerTriggersModule().getTableName())
				.innerJoin(ModuleFactory.getPMTriggersModule().getTableName())
					.on("PM_Triggers.ID = PM_Resource_Planner_Triggers.TRIGGER_ID")
				.andCondition(CriteriaAPI.getCondition(field, resourcePlannerIds, NumberOperators.EQUALS));

			List<Map<String, Object>> relProps = selectRecordBuilder.get();
			if (relProps != null && !relProps.isEmpty()) {
				for (Map<String, Object> relProp: relProps) {
					Long resourcePlannerId = (Long) relProp.get("resourcePlannerId");
					PMResourcePlannerContext resourcePlannerContext = resourcePlannerContextMap.get(resourcePlannerId);
					if (resourcePlannerContext.getTriggerContexts() == null) {
						resourcePlannerContext.setTriggerContexts(new ArrayList<>());
					}
					resourcePlannerContext.getTriggerContexts().add(FieldUtil.getAsBeanFromMap(relProp, PMTriggerContext.class));
				}
			}
		}

		return result;
	}

	public static Map<Long, PMResourcePlannerContext> getPMResourcesPlanner(Long pmId) throws Exception {
		return getPMResourcesPlanner(pmId, false);
	}
	
	public static Map<Long,PMResourcePlannerContext> getPMResourcesPlanner(Long pmId, boolean onlyName) throws Exception {
		Map<Long, List<PMResourcePlannerContext>> pmMap = getPMResourcesPlanners(Arrays.asList(pmId), onlyName);
		List<PMResourcePlannerContext> resourcePlannerContexts = null;
		if (pmMap != null) {
			resourcePlannerContexts = pmMap.get(pmId);
		}

		Map<Long,PMResourcePlannerContext> result = new HashMap<>();
		if (resourcePlannerContexts == null || resourcePlannerContexts.isEmpty()) {
			return result;
		}

		for (PMResourcePlannerContext rp: resourcePlannerContexts) {
			result.put(rp.getResourceId(), rp);
		}

		return result;
	}
	
	public static List<PMTriggerContext> getPMTriggers(PreventiveMaintenance pm) throws Exception {
		Map<Long, List<PMTriggerContext>> pmTriggerMap = getPMTriggers(Collections.singletonList(pm));
		return pmTriggerMap.get(pm.getId());
	}
	
	public static Map<String, List<TaskContext>> getTaskMapFromJson(JSONObject json) throws JsonParseException, JsonMappingException, IOException {
		Map<String, List> tasksMap = FieldUtil.getAsBeanFromJson(json, Map.class);
		Map<String, List<TaskContext>> tasks = new HashMap<>();
		ObjectMapper mapper = FieldUtil.getMapper(TaskContext.class);
		for (Map.Entry<String, List> entry : tasksMap.entrySet()) {
			tasks.put(entry.getKey(), mapper.readValue(JSONArray.toJSONString(entry.getValue()), mapper.getTypeFactory().constructCollectionType(List.class, TaskContext.class)));
		}
		return tasks;
	}
	
	public static void updateResourceDetails(WorkOrderContext wo, Map<String, List<TaskContext>> taskMap) throws Exception {
		List<Long> oldAssetIds = new ArrayList<>();
		checkAndUpdateSpaceResource(wo, oldAssetIds);
		if(taskMap != null && !taskMap.isEmpty()) {
			for (List<TaskContext> tasks : taskMap.values()) {
				for (TaskContext task : tasks) {
					checkAndUpdateSpaceResource(task, oldAssetIds);
				}
			}
		}
		
		if(!oldAssetIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
			
			List<FacilioField> fields = new ArrayList<>();
			fields.add(FieldFactory.getIdField(module));
			FacilioField oldId = FieldFactory.getField("oldId", "OLD_ID_TEMP", module, FieldType.NUMBER);
			fields.add(oldId);
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table(module.getTableName())
															.andCondition(CriteriaAPI.getCondition(oldId, oldAssetIds, NumberOperators.EQUALS))
															;
			List<Map<String, Object>> props = selectBuilder.get();
			Map<Long, Long> oldIdToNewId = new HashMap<>();
			for(Map<String, Object> prop : props) {
				oldIdToNewId.put((Long)prop.get("oldId"), (Long)prop.get("id"));
			}
			
			updateAssetResource(wo, oldIdToNewId);
			if(taskMap != null && !taskMap.isEmpty()) {
				for (List<TaskContext> tasks : taskMap.values()) {
					for (TaskContext task : tasks) {
						updateAssetResource(task, oldIdToNewId);
					}
				}
			}
		}
	}
	
	private static void updateAssetResource(TicketContext ticket, Map<Long, Long> oldIdToNewId) {
		if(ticket.getAsset() != null && ticket.getAsset().getId() != -1) {
			AssetContext asset = ticket.getAsset();
			asset.setId(oldIdToNewId.get(asset.getId()));
			ticket.setResource(asset);
		}
	}
	
	private static void updateAssetResource(TaskContext task, Map<Long, Long> oldIdToNewId) {
		if(task.getAsset() != null && task.getAsset().getId() != -1) {
			AssetContext asset = task.getAsset();
			asset.setId(oldIdToNewId.get(asset.getId()));
			task.setResource(asset);
		}
	}
	
	private static void checkAndUpdateSpaceResource(TaskContext task, List<Long> oldAssetIds) {
		if(task.getAsset() != null && task.getAsset().getId() != -1) {
			oldAssetIds.add(task.getAsset().getId());
		}
		else if (task.getSpace() != null && task.getSpace().getId() != -1){
			task.setResource(task.getSpace());
		}
	}
	
	private static void checkAndUpdateSpaceResource(TicketContext ticket, List<Long> oldAssetIds) {
		if(ticket.getAsset() != null && ticket.getAsset().getId() != -1) {
			oldAssetIds.add(ticket.getAsset().getId());
		}
		else if (ticket.getSpace() != null && ticket.getSpace().getId() != -1){
			ticket.setResource(ticket.getSpace());
		}
	}
	
	public static List<PMReminder> getPMReminders(Long pmId) throws Exception {
		List<Long> pmids = new ArrayList<>();
		pmids.add(pmId);
		return getPMReminders(pmids);
	}

	public static List<PMReminder> getPMReminders(Collection<Long> pmIds) throws Exception {
		List<Map<String, Object>> reminderProps = fetchPMReminders(pmIds);
		if(reminderProps != null && !reminderProps.isEmpty()) {
			List<PMReminder> reminders = new ArrayList<>();
			List<Long> reminderIds = new ArrayList<>();
			for(Map<String, Object> prop : reminderProps) {
				PMReminder reminder = FieldUtil.getAsBeanFromMap(prop, PMReminder.class);
				reminders.add(reminder);
				reminderIds.add(reminder.getId());
			}
			Multimap<Long, PMReminderAction> pmReminderActionMap =  getPMReminderActions(reminderIds);
			
			for(PMReminder reminder : reminders) {
				reminder.setReminderActions(new ArrayList<>(pmReminderActionMap.get(reminder.getId())));
				for (PMReminderAction reminderAction:  reminder.getReminderActions()) {
					if (reminderAction.getAction().getDefaultTemplateId() > -1) {
						reminderAction.getAction().setTemplate(TemplateAPI.getDefaultTemplate(DefaultTemplate.DefaultTemplateType.ACTION, reminderAction.getAction().getDefaultTemplateId()));
					}
				}
			}
			return reminders;
		}
		return null;
	}
	
	public static Multimap<Long, PMReminderAction> getPMReminderActions(List<Long> reminderIds) throws Exception {
		
		FacilioModule module = ModuleFactory.getPMReminderActionModule();
		List<FacilioField> fields = FieldFactory.getPMReminderActionFields();
		Map<String, FacilioField> fieldProps = FieldFactory.getAsMap(fields);
		FacilioField reminderIdField = fieldProps.get("reminderId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(reminderIdField, reminderIds, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		Multimap<Long, PMReminderAction> pmReminderActionMap = ArrayListMultimap.create();
		List<Long> actionIds = new ArrayList<>();
		for(Map<String, Object> prop :props) {
			PMReminderAction reminderAction = FieldUtil.getAsBeanFromMap(prop, PMReminderAction.class);
			if(reminderAction.getActionId() > 0) {
				actionIds.add(reminderAction.getActionId());
			}
			pmReminderActionMap.put(reminderAction.getReminderId(), reminderAction);
		}
		Map<Long, ActionContext> actionMap = actionIds.isEmpty() ? null : ActionAPI.getActionsAsMap(actionIds);
		for (PMReminderAction pmReminderAction : pmReminderActionMap.values()) {
			if(pmReminderAction.getActionId() > 0) {
				pmReminderAction.setAction(actionMap.get(pmReminderAction.getActionId()));
			}
		}
		
		return pmReminderActionMap;
	}

	public static List<Long> getPMSites(long pmId) throws Exception {
		Map<Long, List<Long>> pmSitesMap = getPMSitesMap(Collections.singletonList(pmId));
		if (pmSitesMap == null || pmSitesMap.isEmpty()) {
			return Collections.emptyList();
		}

		return pmSitesMap.get(pmId);
	}

	public static Map<Long, List<Long>> getPMSitesMap(Collection<Long> pmIds) throws Exception {
		List<FacilioField> pmSitesFields = FieldFactory.getPMSitesFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmSitesFields);

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
		selectRecordBuilder.select(pmSitesFields)
				.table(ModuleFactory.getPMSites().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmId"), pmIds, NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectRecordBuilder.get();

		if (CollectionUtils.isEmpty(props)) {
			return Collections.EMPTY_MAP;
		}

		Map<Long, List<Long>> pmIdVsSiteIds = new HashMap<>();
		for (Map<String, Object> prop: props) {
			long pmId = (long) prop.get("pmId");
			List<Long> siteIds = pmIdVsSiteIds.get(pmId);
			if (siteIds == null) {
				pmIdVsSiteIds.put(pmId, new ArrayList<>());
			}
			pmIdVsSiteIds.get(pmId).add((long) prop.get("siteId"));
		}

		return pmIdVsSiteIds;
	}
	
	public static Map<Long,List<PMReminder>> getPMRemindersAsMap(Collection<Long> pmIds) throws Exception {
		
		List<PMReminder> pmReminders = getPMReminders(pmIds);
		
		if(pmReminders != null && !pmReminders.isEmpty()) {
			Map<Long,List<PMReminder>> reminderMap = new HashMap<>();
			for(PMReminder pmReminder : pmReminders) {
				Long pmId = pmReminder.getPmId();
				if (!reminderMap.containsKey(pmId)) {
					reminderMap.put(pmId, new ArrayList<>());
				}
				List<PMReminder> reminders = reminderMap.get(pmId);
				reminders.add(pmReminder);
			}
			return reminderMap;
		}
		return null;
	}

	private static List<Map<String, Object>> fetchPMReminders (Collection<Long> pmIds) throws Exception {
		FacilioModule module = ModuleFactory.getPMReminderModule();
		List<FacilioField> fields = FieldFactory.getPMReminderFields();
		Map<String, FacilioField> fieldProps = FieldFactory.getAsMap(fields);
		FacilioField pmIdField = fieldProps.get("pmId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(pmIdField, pmIds, NumberOperators.EQUALS));
		
		return selectBuilder.get();
	}
	
	public static void schedulePrePMReminder(PMReminder reminder, long nextExecutionTime, long triggerId,long resourceId) throws Exception {
		if(reminder.getTypeEnum() == ReminderType.BEFORE_EXECUTION && nextExecutionTime != -1 && triggerId != -1) {
			long id = deleteOrAddPreviousBeforeRemindersRel(reminder.getId(), triggerId,resourceId);
			FacilioTimer.scheduleOneTimeJobWithTimestampInSec(id, "PrePMReminder", nextExecutionTime-reminder.getDuration(), "facilio");
		}
		else {
			throw new IllegalArgumentException("Invalid parameters for scheduling Before PMReminder job"+reminder.getId());
		}
	}
	
	private static long deleteOrAddPreviousBeforeRemindersRel(long pmReminderId, long triggerId,long resourceId) throws Exception {
		List<FacilioField> fields = FieldFactory.getBeforePMRemindersTriggerRelFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioModule module = ModuleFactory.getBeforePMRemindersTriggerRelModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmReminderId"), String.valueOf(pmReminderId), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmTriggerId"), String.valueOf(triggerId), NumberOperators.EQUALS))
														;
		if(resourceId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), String.valueOf(resourceId), NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			Map<String, Object> relProp = props.get(0);
			long id = (long) relProp.get("id");
			FacilioTimer.deleteJob(id, "PrePMReminder");
			return id;
		}
		else {
			Map<String, Object> relProp = new HashMap<>();
			relProp.put("pmReminderId", pmReminderId);
			relProp.put("pmTriggerId", triggerId);
			if(resourceId > 0) {
				relProp.put("resourceId", resourceId);
			}
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(module.getTableName())
															.fields(fields)
															.addRecord(relProp);
			
			insertBuilder.save();
			return (long) relProp.get("id");
		}
	}
	
	public static void schedulePostPMReminder(PMReminder reminder, long remindTime, long woId) throws SQLException, RuntimeException, Exception {
		if((reminder.getTypeEnum() == ReminderType.AFTER_EXECUTION || reminder.getTypeEnum() == ReminderType.BEFORE_DUE || reminder.getTypeEnum() == ReminderType.AFTER_DUE) && remindTime != -1 && woId != -1) {
			FacilioTimer.scheduleOneTimeJobWithTimestampInSec(addPMReminderToWORel(reminder.getId(), woId), "PostPMReminder", remindTime, "facilio");
		}
		else {
			throw new IllegalArgumentException("Invalid parameters for scheduling After PMReminder job"+reminder.getId());
		}
	}
	
	private static long addPMReminderToWORel(long pmReminderId, long woId) throws SQLException, RuntimeException {
		Map<String, Object> props = new HashMap<>();
		props.put("pmReminderId", pmReminderId);
		props.put("woId", woId);
		
		GenericInsertRecordBuilder recordBuilder = new GenericInsertRecordBuilder()
														.fields(FieldFactory.getAfterPMReminderWORelFields())
														.table(ModuleFactory.getAfterPMRemindersWORelModule().getTableName())
														.addRecord(props);
		
		recordBuilder.save();
		return (long) props.get("id");
	}
	
	public static PMResourcePlannerContext getPMResourcePlanner(long pmId , long resourceId) throws Exception {
		
		if(pmId > 0 && resourceId > 0) {
			
			FacilioModule module = ModuleFactory.getPMResourcePlannerModule();
			List<FacilioField> fields = FieldFactory.getPMResourcePlannerFields();
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(module.getTableName())
					.andCustomWhere(module.getTableName()+".PM_ID = ?", pmId)
					.andCustomWhere(module.getTableName()+".RESOURCE_ID = ?", resourceId);
			
			List<Map<String, Object>> props = builder.get();
			
			if(props != null && !props.isEmpty()) {
				PMResourcePlannerContext pmResourceContext = FieldUtil.getAsBeanFromMap(props.get(0), PMResourcePlannerContext.class);
				pmResourceContext.setPmResourcePlannerReminderContexts(getPmResourcePlannerReminderContext(pmResourceContext.getId()));
				return pmResourceContext;
			}
		}
		
		return null;
	}
	
	public static Map<Long, Map<Long, List<PMResourcePlannerReminderContext>>> getPmResourcePlannerReminderContexts(Collection<Long> pmIds) throws Exception {
		if (pmIds == null || pmIds.isEmpty()) {
			return Collections.emptyMap();
		}
		
		FacilioModule module = ModuleFactory.getPMResourcePlannerReminderModule();
		List<FacilioField> fields = FieldFactory.getPMResourcePlannerReminderFields();
		
		FacilioField pmIdField = FieldFactory.getField("pmId", "PM_ID", module, FieldType.LOOKUP);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(pmIdField, pmIds, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = builder.get();
		
		Map<Long, Map<Long, List<PMResourcePlannerReminderContext>>> result = new HashMap<>();
		
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				PMResourcePlannerReminderContext pmTriggerResourceRemContext = FieldUtil.getAsBeanFromMap(prop, PMResourcePlannerReminderContext.class);
				if (!result.containsKey(pmTriggerResourceRemContext.getPmId())) {
					result.put(pmTriggerResourceRemContext.getPmId(), new HashMap<>());
				}
				
				if (!result.get(pmTriggerResourceRemContext.getPmId()).containsKey(pmTriggerResourceRemContext.getResourcePlannerId())) {
					result.get(pmTriggerResourceRemContext.getPmId()).put(pmTriggerResourceRemContext.getResourcePlannerId(), new ArrayList<>());
				}
				
				result.get(pmTriggerResourceRemContext.getPmId()).get(pmTriggerResourceRemContext.getResourcePlannerId()).add(pmTriggerResourceRemContext);
			}
		}
		
		return result;
	}
	
	public static List<PMResourcePlannerReminderContext> getPmResourcePlannerReminderContext(long pmTriggerResourceId) throws Exception {
		
		if(pmTriggerResourceId > 0) {
			
			FacilioModule module = ModuleFactory.getPMResourcePlannerReminderModule();
			List<FacilioField> fields = FieldFactory.getPMResourcePlannerReminderFields();
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(module.getTableName())
					.andCustomWhere(module.getTableName()+".PM_RESOURCE_PLANNER_ID = ?", pmTriggerResourceId);
			
			List<Map<String, Object>> props = builder.get();
			
			List<PMResourcePlannerReminderContext> pmResourcePlannerReminderContexts = new ArrayList<>();
			if(props != null && !props.isEmpty()) {
				for(Map<String, Object> prop :props) {
					
					PMResourcePlannerReminderContext pmTriggerResourceRemContext = FieldUtil.getAsBeanFromMap(prop, PMResourcePlannerReminderContext.class);
					pmResourcePlannerReminderContexts.add(pmTriggerResourceRemContext);
				}
				return pmResourcePlannerReminderContexts;
			}
		}
		
		return null;
	}
	
	public static long getPMCount(List<FacilioField> conditionFields, List<Condition> conditions) throws Exception {
		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		FacilioModule woTemplateModule = ModuleFactory.getWorkOrderTemplateModule();
		FacilioField countField = FieldFactory.getField("count", "COUNT(*)", FieldType.NUMBER);
		List<FacilioField> fields = new ArrayList<>();
		fields.add(countField);
		fields.addAll(FieldFactory.getWorkOrderTemplateFields());
		if (conditionFields != null) {
			fields.addAll(conditionFields);
		}
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
												.select(fields)
												.table(module.getTableName())
												.innerJoin(woTemplateModule.getTableName())
												.on(module.getTableName()+".TEMPLATE_ID = "+woTemplateModule.getTableName()+".ID");
		
		if (conditions != null && !conditions.isEmpty()) {
			conditions.forEach(condition -> builder.andCondition(condition));
		}
		List<Map<String, Object>> rs = builder.get();
		if (rs == null || rs.isEmpty()) {
			return 0;
		}
		else {
			return ((Number) rs.get(0).get("count")).longValue();
		}
	}
	
	public static void updateTaskTemplateFromTaskContext(Map<TaskContext, TaskTemplate> taskvsTemplateMap) throws Exception {
		if(taskvsTemplateMap != null && !taskvsTemplateMap.isEmpty()) {
			for(TaskContext task :taskvsTemplateMap.keySet()) {
				taskvsTemplateMap.get(task).setTask(task);
			}
		}
	}

	public static void schedulePostReminder(List<PreventiveMaintenance> pms, long resourceId, Map<Long, WorkOrderContext> pmToWo, long currentExecutionTime) throws Exception {
		FacilioModule module = ModuleFactory.getPMReminderModule();
		for(PreventiveMaintenance pm : pms) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.table(module.getTableName())
															.select(FieldFactory.getPMReminderFields())
//															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCustomWhere("PM_ID = ?", pm.getId())
															.andCustomWhere("REMINDER_TYPE != ?", ReminderType.BEFORE_EXECUTION.getValue())
															;

			List<Map<String, Object>> reminderProps = selectBuilder.get();
			if(reminderProps != null && !reminderProps.isEmpty()) {

				List<PMReminder> reminders = FieldUtil.getAsBeanListFromMapList(reminderProps, PMReminder.class);

				List<PMReminder> remindersToBeExecuted = new ArrayList<>();
				if(pm.getPmCreationTypeEnum().equals(PreventiveMaintenance.PMCreationType.MULTIPLE)) {

					Map<Long, PMReminder> pmReminderMap = getReminderMap(reminders);

					PMResourcePlannerContext planner = getPMResourcePlanner(pm.getId(), resourceId);
					if(planner != null && planner.getPmResourcePlannerReminderContexts() != null && !planner.getPmResourcePlannerReminderContexts().isEmpty()) {
						for(PMResourcePlannerReminderContext pmResPlannerRem :planner.getPmResourcePlannerReminderContexts()) {
							PMReminder rem = pmReminderMap.get(pmResPlannerRem.getReminderId());		// reminder might also have before execution type.
							if(rem != null) {
								remindersToBeExecuted.add(rem);
							}
						}
					}
					else {
						remindersToBeExecuted.add(reminders.get(0));
					}
				}
				else {
					remindersToBeExecuted.addAll(reminders);
				}

				WorkOrderContext wo = pmToWo.get(pm.getId());
				for(PMReminder reminder : remindersToBeExecuted) {
					switch(reminder.getTypeEnum()) {
						case BEFORE_EXECUTION:
							throw new RuntimeException("This is not supposed to happen");
						case AFTER_EXECUTION:
							if(wo != null) {
								schedulePostPMReminder(reminder, (currentExecutionTime + reminder.getDuration()), wo.getId());
							}
							break;
						case BEFORE_DUE:
							if(wo != null && wo.getDueDate() != -1) {
								schedulePostPMReminder(reminder, ((wo.getDueDate()/1000) - reminder.getDuration()), wo.getId());
							}
							break;
						case AFTER_DUE:
							if(wo != null && wo.getDueDate() != -1) {
								schedulePostPMReminder(reminder, ((wo.getDueDate()/1000) + reminder.getDuration()), wo.getId());
							}
							break;
					}
				}
			}
		}
	}

	public static void scheduleReminders(Map<String, List<WorkOrderContext>> resourceTriggerWoMap, List<PreventiveMaintenance> pms) throws Exception {
		if (pms == null || pms.isEmpty()) {
		   return;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields("workorder");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField createdTimeField = fieldMap.get("createdTime");
		FacilioField dueDateField = fieldMap.get("dueDate");

		for (PreventiveMaintenance pm : pms) {
			List<PMReminder> reminders = pm.getReminders();
			if (reminders == null || reminders.isEmpty()) {
				continue;
			}
			if (pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.SINGLE) {
				FacilioModule reminderModule = ModuleFactory.getPMReminderModule();
				List<FacilioField> reminderFields = FieldFactory.getPMReminderFields();
				Map<String, FacilioField> reminderFieldsMap = FieldFactory.getAsMap(reminderFields);
				for (PMReminder reminder: reminders) {
					long ruleId = -1;
					Criteria criteria = new Criteria();
					criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), String.valueOf(pm.getId()), PickListOperators.IS));
					if (reminder.getTypeEnum() == ReminderType.BEFORE_EXECUTION) {
						ruleId = addScheduleRule(createdTimeField, pm, reminder, criteria, WorkflowRuleContext.ScheduledRuleType.BEFORE);
					} else if (reminder.getTypeEnum() == ReminderType.AFTER_EXECUTION) {
						ruleId = addScheduleRule(createdTimeField, pm, reminder, criteria, WorkflowRuleContext.ScheduledRuleType.AFTER);
					} else if (reminder.getTypeEnum() == ReminderType.BEFORE_DUE) {
						ruleId = addScheduleRule(dueDateField, pm, reminder, criteria, WorkflowRuleContext.ScheduledRuleType.BEFORE);
					} else if (reminder.getTypeEnum() == ReminderType.AFTER_DUE) {
						ruleId = addScheduleRule(dueDateField, pm, reminder, criteria, WorkflowRuleContext.ScheduledRuleType.AFTER);
					}

					if (ruleId <= 0) {
						continue;
					}

					Map<String, Object> props = new HashMap<>();
					props.put("scheduleRuleId", ruleId);
					GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder();
					updateRecordBuilder.table(reminderModule.getTableName())
							.fields(Arrays.asList(reminderFieldsMap.get("scheduleRuleId")))
//							.andCondition(CriteriaAPI.getCurrentOrgIdCondition(reminderModule))
							.andCondition(CriteriaAPI.getIdCondition(reminder.getId(), reminderModule))
							.andCondition(CriteriaAPI.getCondition(reminderFieldsMap.get("pmId"), String.valueOf(pm.getId()), NumberOperators.EQUALS));
					updateRecordBuilder.update(props);
				}
			} else {
				pm.setPmIncludeExcludeResourceContexts(TemplateAPI.getPMIncludeExcludeList(pm.getId(), null, null));
				Map<Long, PMResourcePlannerContext> resourcePlanners = getPMResourcesPlanner(pm.getId());
				Long baseSpaceId = pm.getBaseSpaceId();
				if (baseSpaceId == null || baseSpaceId < 0) {
					baseSpaceId = pm.getSiteId();
				}
				List<Long> resourceIds;
				if (pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTI_SITE) {
					List<Long> pmSites = PreventiveMaintenanceAPI.getPMSites(pm.getId());
					resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(), pmSites, pm.getSpaceCategoryId(), pm.getAssetCategoryId(), null, pm.getPmIncludeExcludeResourceContexts(), true);
				} else {
					resourceIds = getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),baseSpaceId,pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts(), false);
				}
				if (resourceIds != null && !resourceIds.isEmpty()) {
					for(Long resourceId :resourceIds) {					// construct resource planner for default cases
						if(!resourcePlanners.containsKey(resourceId)) {
							PMResourcePlannerContext pmResourcePlannerContext = new PMResourcePlannerContext();
							pmResourcePlannerContext.setResourceId(resourceId);
							if(pmResourcePlannerContext.getResourceId() != null && pmResourcePlannerContext.getResourceId() > 0) {
								pmResourcePlannerContext.setResource(ResourceAPI.getResource(pmResourcePlannerContext.getResourceId()));
							}
							pmResourcePlannerContext.setPmId(pm.getId());
							pmResourcePlannerContext.setAssignedToId(-1l);
							pmResourcePlannerContext.setTriggerContexts(new ArrayList<>());
							pmResourcePlannerContext.setPmResourcePlannerReminderContexts(Collections.emptyList());

							resourcePlanners.put(resourceId, pmResourcePlannerContext);
						}
					}
				}

				if(resourcePlanners != null) {
					FacilioModule module = ModuleFactory.getPMResourceScheduleRuleRelModule();
					List<FacilioField> relFields = FieldFactory.getPMResourceScheduleRuleRelFields();
					GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder();
					builder.fields(relFields)
							.table(module.getTableName());
					pm.setResourcePlanners(new ArrayList<>(resourcePlanners.values()));
					boolean hasEntry = false;
					List<PMResourcePlannerContext> resourcePlannerContexts = pm.getResourcePlanners();

					if (resourcePlannerContexts != null && !resourcePlannerContexts.isEmpty()) {
						for (int i = 0; i < resourcePlannerContexts.size(); i++) {
							List<PMTriggerContext> triggerContexts = resourcePlannerContexts.get(i).getTriggerContexts();
							if (triggerContexts == null || triggerContexts.isEmpty()) {
								triggerContexts = PreventiveMaintenanceAPI.getDefaultTrigger(pm.getDefaultAllTriggers() != null && pm.getDefaultAllTriggers(), pm.getTriggers());
							}
							resourcePlannerContexts.get(i).setTriggerContexts(triggerContexts);
							//NO entry in resource planner table
							if (resourcePlannerContexts.get(i).getId() == null || resourcePlannerContexts.get(i).getId() <= 0) {
								PMResourcePlannerReminderContext reminderContext = new PMResourcePlannerReminderContext();
								reminderContext.setReminderName(reminders.get(0).getName());
								reminderContext.setReminderId(reminders.get(0).getId());
								resourcePlannerContexts.get(i).setPmResourcePlannerReminderContexts(Arrays.asList(reminderContext));
							} else {
								List<PMResourcePlannerReminderContext> rpReminderContexts = PreventiveMaintenanceAPI.getPmResourcePlannerReminderContext(resourcePlannerContexts.get(i).getId());
								resourcePlannerContexts.get(i).setPmResourcePlannerReminderContexts(rpReminderContexts);
							}
						}
					} else {
						continue;
					}

					for (PMResourcePlannerContext resourcePlannerContext : resourcePlannerContexts) {
						if (resourcePlannerContext.getTriggerContexts() != null && !resourcePlannerContext.getTriggerContexts().isEmpty()) {
							List<PMResourcePlannerReminderContext> rpReminderContexts = resourcePlannerContext.getPmResourcePlannerReminderContexts();
							Set<Long> reminderIds = new HashSet<>();
							if (rpReminderContexts != null && !rpReminderContexts.isEmpty()) {
								rpReminderContexts.stream().forEach(i -> reminderIds.add(i.getReminderId()));
							}

							for (PMTriggerContext pmTriggerContext: resourcePlannerContext.getTriggerContexts()) {
								List<WorkOrderContext> ws = resourceTriggerWoMap.get("" + resourcePlannerContext.getResourceId() + pmTriggerContext.getId());
								if (ws != null && !ws.isEmpty()) {
									for (PMReminder reminder: reminders) {
										if (!reminderIds.contains(reminder.getId())) {
											continue;
										}
										Criteria criteria = new Criteria();
										criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), String.valueOf(pm.getId()),PickListOperators.IS));
										criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(resourcePlannerContext.getResourceId()), PickListOperators.IS));
										long ruleId = -1;
										if (reminder.getTypeEnum() == ReminderType.BEFORE_EXECUTION) {
											ruleId = addScheduleRule(createdTimeField, pm, reminder, criteria, WorkflowRuleContext.ScheduledRuleType.BEFORE);
										} else if (reminder.getTypeEnum() == ReminderType.AFTER_EXECUTION) {
											ruleId = addScheduleRule(createdTimeField, pm, reminder, criteria, WorkflowRuleContext.ScheduledRuleType.AFTER);
										} else if (reminder.getTypeEnum() == ReminderType.BEFORE_DUE) {
											ruleId = addScheduleRule(dueDateField, pm, reminder, criteria, WorkflowRuleContext.ScheduledRuleType.BEFORE);
										} else if (reminder.getTypeEnum() == ReminderType.AFTER_DUE) {
											ruleId = addScheduleRule(dueDateField, pm, reminder, criteria, WorkflowRuleContext.ScheduledRuleType.AFTER);
										}

										if (ruleId <= 0) {
											continue;
										}

										Map<String, Object> props = new HashMap<>();
										props.put("scheduleRuleId", ruleId);
										props.put("pmId", pm.getId());
										props.put("resourceId", resourcePlannerContext.getResourceId());
										builder.addRecord(props);
										hasEntry = true;
									}
								}
							}
						}
					}
					if (hasEntry) {
						builder.save();
					}
				}
			}
		}
	}

	private static long addScheduleRule(FacilioField dateField, PreventiveMaintenance pm, PMReminder reminder, Criteria criteria, WorkflowRuleContext.ScheduledRuleType scheduledRuleType) throws Exception {
		WorkflowRuleContext rule = new WorkflowRuleContext();
		rule.setDateFieldId(dateField.getFieldId());
		rule.setModuleName("workorder");
		rule.setActivityType(EventType.SCHEDULED);
		rule.setInterval(reminder.getDuration());
		rule.setCriteria(criteria);
		rule.setRuleType(WorkflowRuleContext.RuleType.PM_NOTIFICATION_RULE);
		rule.setName(reminder.getName()+"_"+System.currentTimeMillis());
		rule.setScheduleType(scheduledRuleType);
		rule.setSiteId(pm.getSiteId());
		long ruleId = WorkflowRuleAPI.addWorkflowRule(rule);
		List<ActionContext> actions = getActionListFromReminder(reminder);
		ActionAPI.addWorkflowRuleActionRel(ruleId, actions);
		return ruleId;
	}

	private static List<ActionContext> getActionListFromReminder(PMReminder pmReminder) throws Exception {
		List<ActionContext> actions = new ArrayList<>();
		for(PMReminderAction reminderAction : pmReminder.getReminderActions()) {
			ActionContext action = ActionAPI.getAction(reminderAction.getActionId());
			actions.add(action);
		}
		return actions;
	}

	public static Criteria getPMExcludeCriteria() {
		Criteria cr = new Criteria();
		Map<String, FacilioField> pmFieldMap = FieldFactory.getAsMap(FieldFactory.getPreventiveMaintenanceFields());
		cr.addAndCondition(CriteriaAPI.getCondition(pmFieldMap.get("status"), "3", NumberOperators.NOT_EQUALS));
		return cr;
	}

	public static Criteria getUserTriggerCriteria() throws Exception {
		Criteria criteria = new Criteria();
		Map<String, FacilioField> pmFields = FieldFactory.getAsMap(FieldFactory.getPreventiveMaintenanceFields());
		criteria.addAndCondition(CriteriaAPI.getCondition(pmFields.get("isUserTriggerPresent"), "true" , BooleanOperators.IS));
		List<PreventiveMaintenance> userTriggerPMs = PreventiveMaintenanceAPI.getPMs(null, criteria, null, null, null, true);
		if (CollectionUtils.isEmpty(userTriggerPMs)) {
			return null;
		}

		List<Long> pmIds = new ArrayList<>();

		for (PreventiveMaintenance pm: userTriggerPMs) {
			if (CollectionUtils.isEmpty(pm.getTriggers())) {
				continue;
			}

			List<SharingContext<SingleSharingContext>> sharingContexts = pm.getTriggers().stream().filter(i -> i.getTriggerExecutionSourceEnum() == TriggerExectionSource.USER).map(PMTriggerContext::getSharingContext).collect(Collectors.toList());

			for (SharingContext sharingContext: sharingContexts) {
				if (sharingContext.isAllowed()) {
					pmIds.add(pm.getId());
					break;
				}
			}
		}

		if (pmIds.isEmpty()) {
			return null;
		}

		Criteria pmCriteria = new Criteria();
		pmCriteria.addAndCondition(CriteriaAPI.getIdCondition(pmIds, ModuleFactory.getPreventiveMaintenanceModule()));
		return pmCriteria;
	}

	public static void updateWorkOrderCreationStatus(Connection conn, List<Long> ids, int status) throws Exception {
		LOGGER.log(Level.ERROR, "updateWorkOrderCreationStatus(): Status = " + status + ", " + " ids = " + ids);
		if (ids == null || ids.isEmpty()) {
			LOGGER.log(Level.ERROR, "updateWorkOrderCreationStatus(): IDs are empty.");
			return;
		}
		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		Map<String, Object> props = new HashMap<>();
		props.put("woGenerationStatus", status);
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder();
		updateRecordBuilder.fields(Arrays.asList(fieldMap.get("woGenerationStatus")))
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("id"), ids, NumberOperators.EQUALS));
		if (conn != null) {
			updateRecordBuilder.useExternalConnection(conn);
		}
		updateRecordBuilder.update(props);
	}

    public static void updateWorkOrderCreationStatus(List<Long> ids, int status) throws Exception {
		updateWorkOrderCreationStatus(null, ids, status);
    }

	public static WorkOrderContext createWOContextsFromPMOnce(Context context, PreventiveMaintenance pm, PMTriggerContext trigger,  WorkorderTemplate woTemplate,  long startTime) throws Exception{
		long nextExecutionTime = trigger.getSchedule().nextExecutionTime(startTime);
		int currentCount = pm.getCurrentExecutionCount();
		long currentTime = System.currentTimeMillis() / 1000;
		while (nextExecutionTime < currentTime) {
			nextExecutionTime = trigger.getSchedule().nextExecutionTime(nextExecutionTime);
		}
		if((pm.getMaxCount() == -1 || currentCount < pm.getMaxCount()) && (pm.getEndTime() == -1 || nextExecutionTime <= pm.getEndTime())) {
			return createWOContextFromPM(context, pm, trigger, woTemplate, nextExecutionTime);
		}
		return null;
	}
	
	public static List<Map<String, Object>> getTaskSectionTemplateTriggers(long triggerId) throws Exception {
		FacilioModule sectionTriggerModule = ModuleFactory.getTaskSectionTemplateTriggersModule();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(sectionTriggerModule.getTableName())
				.select(FieldFactory.getTaskSectionTemplateTriggersFields())
				.andCustomWhere("PM_TRIGGER_ID = ?", triggerId)
				.andCustomWhere("EXECUTE_IF_NOT_IN_TIME IS NOT NULL");
		
		return builder.get();
	}

    public static void addTaskSectionTrigger(PreventiveMaintenance pm, List<TaskSectionTemplate> sectionTemplates) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
        FacilioModule module = ModuleFactory.getTaskSectionTemplateTriggersModule();

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(module.getTableName())
                .fields(FieldFactory.getTaskSectionTemplateTriggersFields());


        List<Map<String, Object>> props = new ArrayList<>();

        if(sectionTemplates != null) {
            for (int i = 0; i < sectionTemplates.size(); i++) {
                List<PMTaskSectionTemplateTriggers> pmTaskSectionTemplateTriggers = sectionTemplates.get(i).getPmTaskSectionTemplateTriggers();
                if (pmTaskSectionTemplateTriggers == null) {
                    continue;
                }
                for (int k = 0; k < pmTaskSectionTemplateTriggers.size(); k++) {
                    PMTaskSectionTemplateTriggers pmTaskSectionTemplateTrigger = pmTaskSectionTemplateTriggers.get(k);
                    PMTriggerContext trig = pm.getTriggerMap().get(pmTaskSectionTemplateTriggers.get(k).getTriggerName());
                    if (trig == null) {
                    	throw new IllegalArgumentException("Trigger associated with section does not exist.");
                    }
                    pmTaskSectionTemplateTrigger.setTriggerId(trig.getId());
                    pmTaskSectionTemplateTrigger.setSectionId(sectionTemplates.get(i).getId());
                    props.add(FieldUtil.getAsProperties(pmTaskSectionTemplateTrigger));
                }
            }
        }


        if (!props.isEmpty()) {
            builder.addRecords(props);
            builder.save();
        }
    }


	public static void migrateNewPMMigration (List<Long> orgs) throws Exception {
		LOGGER.log(Level.WARN, "Starting PM Trigger migration");
		for (long i : orgs) {
			try {
				LOGGER.log(Level.WARN, "org id " + i);
				AccountUtil.setCurrentAccount(i);
				if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
					LOGGER.log(Level.WARN, "org is missing");
					continue;
				}

				List<FacilioField> triggerFields = FieldFactory.getPMTriggerFields();
				Map<String, FacilioField> triggerFieldMap = FieldFactory.getAsMap(triggerFields);

				Criteria triggerCri = new Criteria();
				triggerCri.addAndCondition(CriteriaAPI.getCondition(triggerFieldMap.get("frequency"), Arrays.asList(3L, 4L, 5L), NumberOperators.EQUALS));
				List<Map<String, Object>> triggers = getPMTriggers(triggerCri);

				for (Map<String, Object> trigger: triggers) {
					PMTriggerContext pmt = FieldUtil.getAsBeanFromMap(trigger, PMTriggerContext.class);
					ScheduleInfo sinfo = pmt.getSchedule();
					long startTime = pmt.getStartTime();

					LOGGER.log(Level.WARN, "Migrating PM: " + pmt.getPmId() + " Trigger: " + pmt.getId() + " Frequency Type: " + sinfo.getFrequencyTypeEnum());
					try {
						ZonedDateTime zonedStartTime = DateTimeUtil.getDateTime(startTime, false).with(LocalTime.of(0, 0)).withDayOfMonth(1);
						ZonedDateTime calculated = DateTimeUtil.getDateTime(sinfo.nextExecutionTime(zonedStartTime.toEpochSecond()), true);
						ZonedDateTime prev = DateTimeUtil.getDateTime(startTime, false);

						long newStartTime;
						if (calculated.getMonthValue() == prev.getMonthValue()
								&& calculated.getDayOfMonth() == prev.getDayOfMonth()
								&& calculated.getYear() == prev.getYear()
								&& calculated.getHour() == prev.getHour()
								&& calculated.getMinute() == prev.getMinute()) {
							newStartTime = zonedStartTime.toInstant().toEpochMilli();
						} else {
							ZonedDateTime nextTime = DateTimeUtil.getDateTime(sinfo.nextExecutionTime(startTime/1000), true);
							newStartTime = nextTime.with(LocalTime.of(0, 0)).withDayOfMonth(1).toInstant().toEpochMilli();
						}

						HashMap<String, Object> update = transformScheduleInfo(pmt, newStartTime);

						if (!update.isEmpty()) {
							updatePMTriggers(pmt, update);
						}
					} catch (Exception e) {
						LOGGER.log(Level.WARN, "===> Execption Migrating PM: " + pmt.getPmId() + " Trigger: " + pmt.getId() + " Frequency Type: " + sinfo.getFrequencyTypeEnum(), e);
					}
				}
			} catch (Exception e) {
				LOGGER.log(Level.WARN, "Exception migrating org " + i, e);
				throw e;
			} finally {
				AccountUtil.cleanCurrentAccount();
			}
		}
		LOGGER.log(Level.WARN, "Completing migration");
	}

	private static HashMap<String, Object> transformScheduleInfo(PMTriggerContext pmt, long newStartTime) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		ScheduleInfo sinfo = pmt.getSchedule();
		HashMap<String, Object> update = new HashMap<>();
		if (pmt.getFrequencyEnum() == FacilioFrequency.MONTHLY) { // verify this assumption
			update.put("startTime", newStartTime);
		} else if (pmt.getFrequencyEnum() == FacilioFrequency.QUARTERTLY) {
			update.put("startTime", newStartTime);

			ZonedDateTime zonedNewStartTime = DateTimeUtil.getDateTime(newStartTime, false);
			int month = zonedNewStartTime.getMonthValue();

			int offsetMonth = ((month - 1) % 3) + 1;
			ScheduleInfo s = new ScheduleInfo();

			FrequencyType currentFreq = sinfo.getFrequencyTypeEnum();
			int frequency = sinfo.getFrequency();

			if ((currentFreq != FrequencyType.MONTHLY_DAY && currentFreq != FrequencyType.MONTHLY_WEEK) || frequency != 3)  {
				throw new IllegalStateException("should not be " + currentFreq + " frequency " + frequency);
			}

			FrequencyType newFrequencyType = currentFreq == FrequencyType.MONTHLY_DAY ? FrequencyType.QUARTERLY_DAY: FrequencyType.QUARTERLY_WEEK;

			s.setFrequencyType(newFrequencyType);
			s.setMonthValue(offsetMonth);
			s.setTimeObjects(sinfo.getTimeObjects());
			s.setValues(sinfo.getValues());

			if (newFrequencyType == FrequencyType.QUARTERLY_WEEK) {
				s.setWeekFrequency(sinfo.getWeekFrequency());
			}
			update.put("scheduleJson", FieldUtil.getAsJSON(s).toJSONString());
		} else if (pmt.getFrequencyEnum() == FacilioFrequency.HALF_YEARLY) {
			update.put("startTime", newStartTime);

			ZonedDateTime zonedNewStartTime = DateTimeUtil.getDateTime(newStartTime, false);
			int month = zonedNewStartTime.getMonthValue();

			int offsetMonth = ((month - 1) % 6) + 1;
			ScheduleInfo s = new ScheduleInfo();

			FrequencyType currentFreq = sinfo.getFrequencyTypeEnum();
			int frequency = sinfo.getFrequency();

			if ((currentFreq != FrequencyType.MONTHLY_DAY && currentFreq != FrequencyType.MONTHLY_WEEK) || frequency != 6)  {
				throw new IllegalStateException("should not be " + currentFreq + " frequency " + frequency);
			}

			FrequencyType newFrequencyType = currentFreq == FrequencyType.MONTHLY_DAY ? FrequencyType.HALF_YEARLY_DAY: FrequencyType.HALF_YEARLY_WEEK;

			s.setFrequencyType(newFrequencyType);
			s.setMonthValue(offsetMonth);
			s.setTimeObjects(sinfo.getTimeObjects());
			s.setValues(sinfo.getValues());

			if (newFrequencyType == FrequencyType.HALF_YEARLY_WEEK) {
				s.setWeekFrequency(sinfo.getWeekFrequency());
			}
			update.put("scheduleJson", FieldUtil.getAsJSON(s).toJSONString());
		}
		return update;
	}

	private static void updatePMTriggers(PMTriggerContext pmt, HashMap<String, Object> update) throws SQLException {
		List<FacilioField> fields = FieldFactory.getPMTriggerFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioModule module = ModuleFactory.getPMTriggersModule();
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder();
		builder = builder.fields(Arrays.asList(fieldMap.get("startTime"), fieldMap.get("scheduleJson")))
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(pmt.getId(), module))
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(),module));
		builder.update(update);
	}


	private static Set<Long> getInactivePMs() throws Exception  {
		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("status"), "0", NumberOperators.EQUALS))
				;
		List<Map<String, Object>> ret = selectBuilder.get();

		Set<Long> result = new HashSet<>();
		if (ret == null) {
			return result;
		}

		for (Map<String, Object> r : ret) {
			result.add((long) r.get("id"));
		}

		return result;
	}

	public static void activateDeactivatePMs(Long orgId, List<Long> pmIds) throws Exception {
		try {
			AccountUtil.setCurrentAccount(orgId);
			if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
				LOGGER.log(Level.WARN, "Org is missing");
				return;
			}
			for (long pmId: pmIds) {
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(pmId));
				PreventiveMaintenance pm = new PreventiveMaintenance();
				pm.setStatus(PMStatus.INACTIVE);
				context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
				FacilioChain addTemplate = TransactionChainFactory.getChangeNewPreventiveMaintenanceStatusChain();
				addTemplate.execute(context);

				pm = new PreventiveMaintenance();
				pm.setStatus(PMStatus.ACTIVE);
				context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(pmId));
				context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
				addTemplate = TransactionChainFactory.getChangeNewPreventiveMaintenanceStatusChain();
				addTemplate.execute(context);
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARN, "Exception verifying org " + orgId, e);
			throw e;
		} finally {
			AccountUtil.cleanCurrentAccount();
			LOGGER.log(Level.WARN, "Migration completed orgId: " + orgId);
		}
	}


	public static void verifyNewPMMigration (List<Long> orgs) throws Exception {
		LOGGER.log(Level.WARN, "Verifying orgs");
		for (long i : orgs) {
			try {
				AccountUtil.setCurrentAccount(i);
				if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
					LOGGER.log(Level.WARN, "Org is missing");
					continue;
				}

				List<FacilioField> triggerFields = FieldFactory.getPMTriggerFields();

				Map<String, FacilioField> triggerFieldMap = FieldFactory.getAsMap(triggerFields);

				Criteria migrationCriteria = new Criteria();
				migrationCriteria.addAndCondition(CriteriaAPI.getCondition(triggerFieldMap.get("frequency"), Arrays.asList(3L, 4L, 5L), NumberOperators.EQUALS));
				List<Map<String, Object>> triggersToBeMigrated = getPMTriggers(migrationCriteria);

				Set<Long> inactivePms = getInactivePMs();
				long currentTime = System.currentTimeMillis();
				LOGGER.log(Level.WARN, "Verifying orgs");
				for (Map<String, Object> trigger: triggersToBeMigrated) {
					PMTriggerContext pmt = FieldUtil.getAsBeanFromMap(trigger, PMTriggerContext.class);

					if (inactivePms.contains(pmt.getPmId())) {
						continue;
					}

					ScheduleInfo sinfo = pmt.getSchedule();
					long startTime = pmt.getStartTime();


					LOGGER.log(Level.WARN, "PM ID " + pmt.getPmId());
					LOGGER.log(Level.WARN, "TRIGGER ID " + pmt.getId());
					LOGGER.log(Level.WARN, "Frequency type "+ sinfo.getFrequencyTypeEnum());

					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

					FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
					List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
					Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

					ZonedDateTime zonedStartTime = DateTimeUtil.getDateTime(startTime, false).with(LocalTime.of(0, 0)).withDayOfMonth(1);
					ZonedDateTime calculated = DateTimeUtil.getDateTime(sinfo.nextExecutionTime(zonedStartTime.toEpochSecond()), true);
					ZonedDateTime prev = DateTimeUtil.getDateTime(startTime, false);
					long newStartTime;
					long checkTime;
					boolean isSame = false;
					if (calculated.getMonthValue() == prev.getMonthValue()
							&& calculated.getDayOfMonth() == prev.getDayOfMonth()
							&& calculated.getYear() == prev.getYear()
							&& calculated.getMinute() == prev.getMinute()
							&& calculated.getHour() == prev.getHour()) {
						isSame = true;
						checkTime = calculated.toInstant().toEpochMilli()/1000;
					} else {
						ZonedDateTime nextTime = DateTimeUtil.getDateTime(sinfo.nextExecutionTime(startTime/1000), true);
						newStartTime = nextTime.with(LocalTime.of(0, 0)).withDayOfMonth(1).toInstant().toEpochMilli();
						checkTime = sinfo.nextExecutionTime(newStartTime/1000);
					}

					HashMap<String, Object> transformed = transformScheduleInfo(pmt, checkTime * 1000);
					ScheduleInfo transformedInfo;
					if (pmt.getFrequencyEnum() != FacilioFrequency.MONTHLY) {
						JSONParser parser = new JSONParser();
						transformedInfo = FieldUtil.getAsBeanFromJson((JSONObject)parser.parse((String) transformed.get("scheduleJson")), ScheduleInfo.class);
					} else {
						transformedInfo = pmt.getSchedule();
					}
					while((checkTime * 1000) < currentTime) {
						checkTime = transformedInfo.nextExecutionTime(checkTime);
					}

					checkTime = checkTime * 1000;

					SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
					selectRecordsBuilder.module(module)
							.beanClass(WorkOrderContext.class)
							.select(Arrays.asList(fieldMap.get("createdTime")))
							.andCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), Arrays.asList(pmt.getPmId()),NumberOperators.EQUALS))
							.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), Arrays.asList(checkTime), NumberOperators.EQUALS))
							.limit(1)
							.skipModuleCriteria();
					List<WorkOrderContext> res = selectRecordsBuilder.get();

					if (res == null || res.isEmpty()) {

						LOGGER.log(Level.WARN, "===> No work order found for PM ID: " + pmt.getPmId() + " Trigger Id: " + pmt.getId() + " isSame: " + isSame + " checkTime: " + checkTime);
					}
				}
			} catch (Exception e) {
				LOGGER.log(Level.WARN, "Exception verifying org " + i, e);
				throw e;
			} finally {
				AccountUtil.cleanCurrentAccount();
				LOGGER.log(Level.WARN, "Migration completed orgId: " + i);
			}
		}
	}

	public static void logIf(long orgId, String message) {
		if (AccountUtil.getCurrentOrg().getOrgId() == orgId) {
			LOGGER.log(Level.WARN, message);
		}
	}

    private static List<Long> getScheduledWOIds(List<Long> pmIds) throws Exception {
        FacilioStatus status = TicketAPI.getStatus("preopen");
        if (status == null) {
            CommonCommandUtil.emailAlert("Org does not have pre-open state", "ORGID: "+ AccountUtil.getCurrentAccount().getOrg().getOrgId());
            throw new IllegalStateException("Org does not have pre-open state");
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder.select(Arrays.asList(FieldFactory.getIdField(module)))
                .beanClass(WorkOrderContext.class)
                .module(module)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), pmIds, NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(status.getId()), NumberOperators.EQUALS));
        List<WorkOrderContext> wos = selectRecordsBuilder.get();
        if (wos != null && !wos.isEmpty()) {
            List<Long> res = new ArrayList<>();
            for (WorkOrderContext w : wos) {
                res.add(w.getId());
            }
            return res;
        }
        return Collections.emptyList();
    }

	public static void deleteScheduledWorkorders(Connection conn, List<Long> pmIds) throws Exception {
		if (pmIds.isEmpty()) {
			return;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderModule = modBean.getModule("workorder");
		List<FacilioField> woFields = modBean.getAllFields(workorderModule.getName());
		Map<String, FacilioField> woFieldMap = FieldFactory.getAsMap(woFields);

		DeleteRecordBuilder deleteRecordBuilder = new DeleteRecordBuilder();
		deleteRecordBuilder.module(workorderModule);
		deleteRecordBuilder.andCondition(CriteriaAPI.getCondition(woFieldMap.get("pm"), pmIds, NumberOperators.EQUALS));
		deleteRecordBuilder.andCondition(CriteriaAPI.getCondition(woFieldMap.get("jobStatus"), 1+"", NumberOperators.EQUALS));
		if (conn != null) {
			deleteRecordBuilder.useExternalConnection(conn);
		}
		deleteRecordBuilder.skipModuleCriteria();
		deleteRecordBuilder.markAsDelete();
	}

    public static void deleteScheduledWorkorders(List<Long> pmIds) throws Exception {
		deleteScheduledWorkorders(null, pmIds);
    }

	public static void deleteMultiWoPMReminders(List<Long> pmIds) throws Exception {
		if (pmIds.isEmpty()) {
			return;
		}
		LOGGER.log(Level.ERROR, "deleteMultiWoPMReminders: " + pmIds);

		FacilioModule module = ModuleFactory.getPMResourceScheduleRuleRelModule();
		Map<String, FacilioField> fieldMap =  FieldFactory.getAsMap(FieldFactory.getPMResourceScheduleRuleRelFields());

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
		selectRecordBuilder.select(Arrays.asList(fieldMap.get("scheduleRuleId")))
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmId"), pmIds, NumberOperators.EQUALS))
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
				;
		List<Map<String, Object>> props = selectRecordBuilder.get();
		List<Long> workFlowIds = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop: props) {
				if (prop.get("scheduleRuleId") != null) {
					workFlowIds.add((Long) prop.get("scheduleRuleId"));
				}
			}
		}

		if (!workFlowIds.isEmpty()) {
			WorkflowRuleAPI.deleteWorkFlowRules(workFlowIds);
		}

		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmId"), pmIds, NumberOperators.EQUALS));
		deleteRecordBuilder.delete();
	}

	public static void deleteTriggers(List<Long> triggerPMIds) throws Exception {
		if (!triggerPMIds.isEmpty()) {
			LOGGER.log(Level.ERROR, "deleteTriggers: " + triggerPMIds);
			FacilioModule triggerModule = ModuleFactory.getPMTriggersModule();
			List<FacilioField> triggerFields = FieldFactory.getPMTriggerFields();
			FacilioField pmIdField = FieldFactory.getAsMap(triggerFields).get("pmId");
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(triggerModule.getTableName())
					.andCondition(CriteriaAPI.getCondition(pmIdField, triggerPMIds, NumberOperators.EQUALS));
			deleteBuilder.delete();
		}
	}

	public static void deletePmResourcePlanner(List<Long> pmids) throws Exception {
		if (pmids !=  null && !pmids.isEmpty()) {
			LOGGER.log(Level.ERROR, "deletePmResourcePlanner: " + pmids);
			FacilioModule module = ModuleFactory.getPMResourcePlannerModule();
			List<FacilioField> fields = FieldFactory.getPMResourcePlannerFields();
			FacilioField pmIdField = FieldFactory.getAsMap(fields).get("pmId");
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getCondition(pmIdField, StringUtils.join(pmids, ","), NumberOperators.EQUALS));
			deleteBuilder.delete();
		}
	}

	public static void deletePmIncludeExclude(List<Long> pmids) throws Exception {
		if (pmids !=  null && !pmids.isEmpty()) {
			LOGGER.log(Level.ERROR, "deletePmIncludeExclude: " + pmids);
			FacilioModule module = ModuleFactory.getPMIncludeExcludeResourceModule();
			List<FacilioField> fields = FieldFactory.getPMIncludeExcludeResourceFields();
			FacilioField pmIdField = FieldFactory.getAsMap(fields).get("pmId");
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getCondition(pmIdField,StringUtils.join(pmids, ","), NumberOperators.EQUALS));
			deleteBuilder.delete();
		}
	}

	public static void deletePMReminders(List<Long> pmIds) throws Exception {
		if (pmIds.isEmpty()) {
			return;
		}
		deleteNewPMReminders(pmIds);
	}

	private static void deleteNewPMReminders(List<Long> pmIds) throws Exception {
		LOGGER.log(Level.ERROR, "deleteNewPMReminders: " + pmIds);
		FacilioModule reminderModule = ModuleFactory.getPMReminderModule();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPMReminderFields());

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(Arrays.asList(fieldMap.get("scheduleRuleId")))
				.table(reminderModule.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(reminderModule))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmId"), pmIds, NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectRecordBuilder.get();
		List<Long> workFlowIds = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop: props) {
				if (prop.get("scheduleRuleId") != null) {
					workFlowIds.add((Long) prop.get("scheduleRuleId"));
				}
			}
		}

		if (!workFlowIds.isEmpty()) {
			WorkflowRuleAPI.deleteWorkFlowRules(workFlowIds);
		}

		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(reminderModule.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(reminderModule))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmId"), pmIds, NumberOperators.EQUALS));
		deleteBuilder.delete();
	}

	public static int deletePMs(List<Long> recordIds) throws Exception {
		int count = 0;
		if(recordIds != null && !recordIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
			DeleteRecordBuilder<ResourceContext> deleteBuilder = new DeleteRecordBuilder<ResourceContext>()
				.module(module)
				.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
			count = deleteBuilder.delete();
		}
		return count;
	}

	private static int markPM(List<Long> recordIds, PMStatus status) throws Exception {
		int count = 0;

		if(recordIds != null && !recordIds.isEmpty()) {
			List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
			FacilioModule pmModule = ModuleFactory.getPreventiveMaintenanceModule();
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

			Map<String, Object> updateMap = new HashMap<>();
			updateMap.put("status", status.getValue());

			GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder();
			updateRecordBuilder.table("Preventive_Maintenance");
			updateRecordBuilder.fields(Arrays.asList(fieldMap.get("status")));
			updateRecordBuilder.andCondition(CriteriaAPI.getIdCondition(recordIds, pmModule));
			count = updateRecordBuilder.update(updateMap);
		}

		return count;
	}

	public static int markAsScheduling(List<Long> recordIds) throws Exception {
		return markPM(recordIds, PMStatus.SCHEDULING);
	}

	public static int markAsDelete(List<Long> recordIds) throws Exception {
		return markPM(recordIds, PMStatus.DELETED);
	}

	public static Criteria getWorkOrderExcludeCriteria() throws Exception {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPreventiveMaintenanceFields());
		Criteria pmCriteria = new Criteria();
		pmCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), PMStatus.DELETED.getValue()+"", NumberOperators.EQUALS));
		pmCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("woGenerationStatus"), "1", NumberOperators.EQUALS));
		List<PreventiveMaintenance> pms = getPMs(null, pmCriteria, null, null, Arrays.asList(fieldMap.get("id")), false);
		if (pms == null || pms.isEmpty()) {
			return null;
		}

		List<Long> pmIds = pms.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toList());

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> woFieldMap = FieldFactory.getAsMap(modBean.getAllFields("workorder"));

		Criteria woCriteria = new Criteria();
		woCriteria.addAndCondition(CriteriaAPI.getCondition(woFieldMap.get("pm"), pmIds, NumberOperators.NOT_EQUALS));

		return woCriteria;
	}


	public static boolean canOpenWorkOrder(PreventiveMaintenance pm) {
		return pm.getStatusEnum() == PMStatus.ACTIVE && !pm.isWoGenerating();
	}

	public static void migrateScheduleGeneration(List<Long> orgs) throws Exception {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPreventiveMaintenanceFields());
		Criteria statusCriteria = new Criteria();
		statusCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), 1+"", NumberOperators.EQUALS));
		statusCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("triggerType"), String.valueOf(PreventiveMaintenance.TriggerType.ONLY_SCHEDULE_TRIGGER.getVal()), NumberOperators.EQUALS));
		for (long i : orgs) {
			try {
				AccountUtil.setCurrentAccount(i);
				if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
					LOGGER.log(Level.WARN, "Org is missing");
					continue;
				}
				LOGGER.log(Level.WARN, "migrating org " + i);
				List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getActivePMs(null, statusCriteria, null);
				if (pms == null || pms.isEmpty()) {
					LOGGER.log(Level.WARN, "no Pms in this org");
					continue;
				}

				List<Long> pmIds = pms.stream().map(PreventiveMaintenance::getId).collect(Collectors.toList());

				Map<Long, PreventiveMaintenance> pmMap = FieldUtil.getAsMap(pms);

				Map<Long, List<PMTriggerContext>> pmTriggers = PreventiveMaintenanceAPI.getPMTriggers(pmIds);

				Map<Long, Long> endTimeMap = new HashMap<>();

				for (long pmId: pmIds) {
					long endTime = getEndtimeForPM(pmId, pmMap, pmTriggers);
					endTimeMap.put(pmId, endTime);
				}

				Set<Map.Entry<Long, Long>> endTimes = endTimeMap.entrySet().stream().filter(m -> m.getValue() > 0).collect(Collectors.toSet());
				adjustToEndtime(endTimes);

				LOGGER.log(Level.WARN, "pm valid end time map " + Arrays.toString(endTimeMap.entrySet().stream().filter(m -> m.getValue() > 0).collect(Collectors.toSet()).toArray()));
				LOGGER.log(Level.WARN, "pm map size " + endTimeMap.keySet().size());
				LOGGER.log(Level.WARN, "pm map no end time " + Arrays.toString(endTimeMap.entrySet().stream().filter(m -> m.getValue() < 0).collect(Collectors.toSet()).toArray()));
				LOGGER.log(Level.WARN, "pm map no trigger " + Arrays.toString(endTimeMap.entrySet().stream().filter(m -> m.getValue() == -1).collect(Collectors.toSet()).toArray()));
				LOGGER.log(Level.WARN, "pm map not used " + Arrays.toString(endTimeMap.entrySet().stream().filter(m -> m.getValue() == -2).collect(Collectors.toSet()).toArray()));
				LOGGER.log(Level.WARN, "pm map no workorder " + Arrays.toString(endTimeMap.entrySet().stream().filter(m -> m.getValue() == -3).collect(Collectors.toSet()).toArray()));
			} catch (Exception e) {
				LOGGER.log(Level.WARN, "Exception migrating org " + i, e);
				throw e;
			} finally {
				AccountUtil.cleanCurrentAccount();
				LOGGER.log(Level.WARN, "Migration completed orgId: " + i);
			}
		}
	}

	private static void adjustToEndtime(Set<Map.Entry<Long, Long>> endTimes) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderModule = modBean.getModule("workorder");
		List<FacilioField> woFields = modBean.getAllFields(workorderModule.getName());
		Map<String, FacilioField> woFieldMap = FieldFactory.getAsMap(woFields);

		for (Map.Entry<Long, Long> pair: endTimes) {
			DeleteRecordBuilder deleteRecordBuilder = new DeleteRecordBuilder();
			deleteRecordBuilder.module(workorderModule);
			deleteRecordBuilder.andCondition(CriteriaAPI.getCondition(woFieldMap.get("createdTime"), (pair.getValue() * 1000)+"", NumberOperators.GREATER_THAN));
			deleteRecordBuilder.andCondition(CriteriaAPI.getCondition(woFieldMap.get("pm"), pair.getKey()+"", NumberOperators.EQUALS));
			deleteRecordBuilder.andCondition(CriteriaAPI.getCondition(woFieldMap.get("jobStatus"), 1+"", NumberOperators.EQUALS));
			deleteRecordBuilder.markAsDelete();

			PreventiveMaintenanceAPI.incrementGenerationTime(pair.getKey(), pair.getValue());
		}
	}

	private static long getEndtimeForPM(long pmId, Map<Long, PreventiveMaintenance> pmMap, Map<Long, List<PMTriggerContext>> pmTriggers) throws Exception {
		PreventiveMaintenance preventiveMaintenance = pmMap.get(pmId);
		List<PMTriggerContext> pmTriggerContexts = pmTriggers.get(pmId);

		if (CollectionUtils.isEmpty(pmTriggerContexts)) {
			return -1;
		}

		Set<Long> usedTriggers;

		if (preventiveMaintenance.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.SINGLE) {
			usedTriggers = CollectionUtils.emptyIfNull(pmTriggerContexts).stream().map(PMTriggerContext::getId).collect(Collectors.toSet());
		} else {
			populateResourcePlanner(pmMap.get(pmId));
			List<PMResourcePlannerContext> resourcePlannerContexts = pmMap.get(pmId).getResourcePlanners();
			usedTriggers = CollectionUtils.emptyIfNull(resourcePlannerContexts).stream()
					.map(PMResourcePlannerContext::getTriggerContexts)
					.map(i -> {
						if (i == null || i.isEmpty()) {
							return Collections.singletonList(pmTriggerContexts.get(0));
						}
						return i;
					})
					.flatMap(Collection::stream)
					.map(PMTriggerContext::getId)
					.collect(Collectors.toSet());
		}

		Optional<PMTriggerContext> min =  CollectionUtils.emptyIfNull(pmTriggerContexts).stream().filter(i -> usedTriggers.contains(i.getId())).min(Comparator.comparingInt(PMTriggerContext::getFrequency));

		if (!min.isPresent()) {
			return -2;
		}

		long minTriggerId = min.get().getId();

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

		List<FacilioField> woFields = modBean.getAllFields(workorderModule.getName());
		Map<String, FacilioField> woFieldMap = FieldFactory.getAsMap(woFields);

		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
				.moduleName("workorder")
				.beanClass(WorkOrderContext.class)
				.select(woFields)
				.andCondition(CriteriaAPI.getCondition(woFieldMap.get("trigger"), minTriggerId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(woFieldMap.get("jobStatus"), 1+"", NumberOperators.EQUALS))
				.orderBy("CREATED_TIME desc")
				.limit(1)
				.skipModuleCriteria();

		List<WorkOrderContext> workOrderContexts = builder.get();

		if (CollectionUtils.isEmpty(workOrderContexts)) {
			return -3;
		}

		return (workOrderContexts.get(0).getCreatedTime()/1000) + (2 * 60);
	}


	public static List<Long> getOrg() {
		List<Long> result = new ArrayList<>();
		for (long i = 1; i <= 250; i++) {
			result.add(i);
		}
		return result;
	}


	public static void removeDuplicates() throws Exception {
		AccountUtil.setCurrentAccount(176L);
		if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
			LOGGER.log(Level.WARN, "Org is missing");
			return;
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> woFields = modBean.getAllFields(workorderModule.getName());
		Map<String, FacilioField> woFieldMap = FieldFactory.getAsMap(woFields);

		FacilioField count = FieldFactory.getField("woCount", "COUNT(WorkOrders.ID)", FieldType.NUMBER);
		FacilioField min =  FieldFactory.getField("minCount", "MIN(WorkOrders.ID)", FieldType.NUMBER);

		List<FacilioField> selectFields = Arrays.asList(count, min, woFieldMap.get("pm"), woFieldMap.get("resource"), woFieldMap.get("createdTime"));

		SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
		selectRecordsBuilder
				.beanClass(WorkOrderContext.class)
				.module(workorderModule)
				.select(selectFields)
				//.andCondition(CriteriaAPI.getCondition(woFieldMap.get("createdTime"), System.currentTimeMillis() +"", NumberOperators.GREATER_THAN))
				.groupBy("Tickets.RESOURCE_ID, WorkOrders.CREATED_TIME, WorkOrders.PM_ID")
				.having("woCount > 1");

		List<Map<String, Object>> props = selectRecordsBuilder.getAsProps();

		for (Map<String, Object> wo: props) {
			LOGGER.log(Level.ERROR, "Vals minCount: " + wo.get("minCount") + " resource " + ((Map) wo.get("resource")).get("id") + " pm " + ((Map) wo.get("pm")).get("id") + " created time " + wo.get("createdTime"));
			List<Map<String, Object>> workOrders = getAffectedWos(wo);
			if (CollectionUtils.isEmpty(workOrders)) {
				continue;
			}
			List<Long> woIdsToBeDeleted;
			if (isAllSubmitted(workOrders)) {
				woIdsToBeDeleted = skipOneSubmittedIds(workOrders);
			} else {
				woIdsToBeDeleted = getSubmittedIds(workOrders);
			}

//			DeleteRecordBuilder<WorkOrderContext> deleteRecordBuilder = new DeleteRecordBuilder<>();
//			deleteRecordBuilder.module(workorderModule)
//					.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(workorderModule), woIdsToBeDeleted , NumberOperators.EQUALS))
//					.markAsDelete();

			LOGGER.log(Level.ERROR, "Ids to be deleted " + Arrays.toString(woIdsToBeDeleted.toArray()));
		}
	}

	private static List<Long> getSubmittedIds(List<Map<String, Object>> workOrders) throws Exception {
		FacilioStatus submitted = TicketAPI.getStatus("Submitted");
		List<Long> ids = workOrders.stream().filter(i -> ((Map) i.get("status")).get("id").equals(submitted.getId())).map(i -> (Long) i.get("id")).collect(Collectors.toList());
		return ids;
	}

	private static List<Long> skipOneSubmittedIds(List<Map<String, Object>> workOrders) {
		List<Long> ids = workOrders.stream().map(i -> (Long) i.get("id")).collect(Collectors.toList());
		ids.remove(0);
		return ids;
	}

	private static boolean isAllSubmitted(List<Map<String, Object>> workOrders) throws Exception {
		FacilioStatus submitted = TicketAPI.getStatus("Submitted");
		for (Map<String, Object> workOrder: workOrders) {
			if (!((Map) workOrder.get("status")).get("id").equals(submitted.getId())) {
				return false;
			}
		}
		return true;
	}

	private static List<Map<String, Object>> getAffectedWos(Map<String, Object> wo) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> woFields = modBean.getAllFields(workorderModule.getName());
		Map<String, FacilioField> woFieldMap = FieldFactory.getAsMap(woFields);

		SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
		selectRecordsBuilder.module(workorderModule)
				.select(Arrays.asList(FieldFactory.getIdField(workorderModule), woFieldMap.get("moduleState")))
				.andCondition(CriteriaAPI.getCondition(woFieldMap.get("resource"), ((Map) wo.get("resource")).get("id") + "", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(woFieldMap.get("pm"), ((Map) wo.get("pm")).get("id") + "", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(woFieldMap.get("createdTime"), wo.get("createdTime") + "", NumberOperators.EQUALS));
		return selectRecordsBuilder.getAsProps();
	}

//	public static void updateResource() throws Exception {
//		AccountUtil.setCurrentAccount(146L);
//		if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
//			LOGGER.log(Level.WARN, "Org is missing");
//			return;
//		}
//
//		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//		FacilioModule taskModule = modBean.getModule(FacilioConstants.ContextNames.TASK);
//		List<FacilioField> taskFields = modBean.getAllFields(taskModule.getName());
//		Map<String, FacilioField> taskFieldMap = FieldFactory.getAsMap(taskFields);
//
//		List<PreventiveMaintenance> allPMs = PreventiveMaintenanceAPI.getAllPMs(1L, false);
//		Set<Long> resourceIsMissing = new HashSet<>();
//		Set<Long> invalidResource = new HashSet<>();
//		Set<Long> unusualSectionName = new HashSet<>();
//		Set<Long> taskMapIsEmpty = new HashSet<>();
//		Set<Long> uniqueIdIsMissing = new HashSet<>();
//
//		for (PreventiveMaintenance pm: allPMs) {
//			LOGGER.log(Level.WARN, "executing for pm " + pm.getId());
//			Map<String, Map<String, Long>> pmLookup = new HashMap<>();
//			WorkorderTemplate woTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(pm.getTemplateId());
//			List<TaskSectionTemplate> sectionTemplates = woTemplate.getSectionTemplates();
//			for (TaskSectionTemplate sectionTemplate: sectionTemplates) {
//				Map<String,Long> taskMap = new HashMap<>();
//				pmLookup.put(sectionTemplate.getName(), taskMap);
//				List<TaskTemplate> taskTemplates = sectionTemplate.getTaskTemplates();
//				for(TaskTemplate taskTemplate: taskTemplates) {
//					taskMap.put(taskTemplate.getName(), (Long) taskTemplate.getAdditionInfo().get("uniqueId"));
//				}
//			}
//			List<WorkOrderContext> workOrders = WorkOrderAPI.getWorkOrderFromPMId(pm.getId());
//			for (WorkOrderContext workOrderContext: workOrders) {
//				LOGGER.log(Level.WARN, "executing for wo " + workOrderContext.getId());
//				List<TaskContext> tasks = TicketAPI.getRelatedTasks(workOrderContext.getId());
//				for (TaskContext task : tasks) {
//					long sectionId = task.getSectionId();
//					TaskSectionContext taskSection = TicketAPI.getTaskSection(sectionId);
//					if (task.getResource() == null) {
//						resourceIsMissing.add(pm.getId());
//						LOGGER.log(Level.ERROR, "resource is missing " + task.getSubject());
//						continue;
//					}
//				}
//			}
//	}


	public static boolean isAllTasksAssignmentTypeIs5(List<TaskSectionTemplate> sectionTemplates) {

		if (CollectionUtils.isEmpty(sectionTemplates)) {
			return true;
		}

		for (TaskSectionTemplate sectionTemplate: sectionTemplates) {
			if (sectionTemplate.getTypeEnum() == PM_PRE_REQUEST_SECTION) {
				continue;
			}
			if (PMAssignmentType.valueOf(sectionTemplate.getAssignmentType()) != PMAssignmentType.CURRENT_ASSET) {
				return false;
			}
			List<TaskTemplate> taskTemplates = sectionTemplate.getTaskTemplates();
			for (TaskTemplate taskTemplate: taskTemplates) {
				if (PMAssignmentType.valueOf(taskTemplate.getAssignmentType()) != PMAssignmentType.CURRENT_ASSET) {
					return false;
				}
			}
		}
		return true;
	}

	public static void populateUniqueId(long accountId, boolean doMigration) throws Exception {

			AccountUtil.setCurrentAccount(accountId);
			if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
				LOGGER.log(Level.WARN, "Org is missing");
				return;
			}

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule taskModule = modBean.getModule(FacilioConstants.ContextNames.TASK);
			List<FacilioField> taskFields = modBean.getAllFields(taskModule.getName());
			Map<String, FacilioField> taskFieldMap = FieldFactory.getAsMap(taskFields);

			List<PreventiveMaintenance> allPMs = PreventiveMaintenanceAPI.getAllPMs(accountId, false);
			Set<Long> resourceIsMissing = new HashSet<>();
			Set<Long> invalidResource = new HashSet<>();
			Set<Long> unusualSectionName = new HashSet<>();
			Set<Long> taskMapIsEmpty = new HashSet<>();
			Set<Long> uniqueIdIsMissing = new HashSet<>();
			Set<Long> additionInfoMissing = new HashSet<>();
			List<Long> skipList = Arrays.asList();
			Set<Long> skip = new HashSet<>(skipList);
			FacilioStatus status = TicketAPI.getStatus("preopen");

			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

			for (PreventiveMaintenance pm: allPMs) {
				if (skip.contains(pm.getId())) {
					LOGGER.log(Level.WARN, "skipping " + pm.getId());
					continue;
				}
				LOGGER.log(Level.WARN, "executing for pm " + pm.getId());

				Map<String, Map<String, Long>> pmLookup = new HashMap<>();
				WorkorderTemplate woTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(pm.getTemplateId());
				List<TaskSectionTemplate> sectionTemplates = woTemplate.getSectionTemplates();

				boolean allTasksAssignmentTypeIs5 = isAllTasksAssignmentTypeIs5(sectionTemplates);

				for (TaskSectionTemplate sectionTemplate: sectionTemplates) {
					if (sectionTemplate.getTypeEnum() == PM_PRE_REQUEST_SECTION) {
						continue;
					}
					Map<String,Long> taskMap = new HashMap<>();
					pmLookup.put(sectionTemplate.getName(), taskMap);
					List<TaskTemplate> taskTemplates = sectionTemplate.getTaskTemplates();
					for(TaskTemplate taskTemplate: taskTemplates) {
						if (taskTemplate.getAdditionInfo() == null || taskTemplate.getAdditionInfo().isEmpty()) {
							LOGGER.log(Level.WARN, "additioninfo missing for pm " + pm.getId());
							additionInfoMissing.add(pm.getId());
							continue;
						}

						if (taskTemplate.getAdditionInfo().get("uniqueId") == null) {
							LOGGER.log(Level.WARN, "uniqueId missing for pm " + pm.getId());
							continue;
						}

						taskMap.put(taskTemplate.getName(), (Long) taskTemplate.getAdditionInfo().get("uniqueId"));
					}
				}
				Criteria timeCriteria = new Criteria();
				timeCriteria.addAndCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", "1547887790000", NumberOperators.GREATER_THAN));
				timeCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(status.getId()), NumberOperators.NOT_EQUALS));
				List<WorkOrderContext> workOrders = WorkOrderAPI.getWorkOrdersFromPM(pm.getId(), timeCriteria, 30);

				Criteria preOpen = new Criteria();
				preOpen.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(status.getId()), NumberOperators.EQUALS));
				preOpen.addAndCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", "1547887790000", NumberOperators.GREATER_THAN));

				List<WorkOrderContext> preOpens = WorkOrderAPI.getWorkOrdersFromPM(pm.getId(), preOpen, -1);
				workOrders.addAll(preOpens);

				for (WorkOrderContext workOrderContext: workOrders) {
					LOGGER.log(Level.WARN, "executing for wo " + workOrderContext.getId());
					List<TaskContext> tasks = TicketAPI.getRelatedTasks(Collections.singletonList(workOrderContext.getId()), true, true);
					Map<String, Object> nullMap = new HashMap<>();
					nullMap.put("uniqueId", -99L);
					if (doMigration) {
						UpdateRecordBuilder<TaskContext> nullUpdateRecordBuilder = new UpdateRecordBuilder<>();
						nullUpdateRecordBuilder.moduleName("task")
							.fields(Collections.singletonList(taskFieldMap.get("uniqueId")))
							.andCondition(CriteriaAPI.getCondition(taskFieldMap.get("parentTicketId"), workOrderContext.getId()+"", NumberOperators.EQUALS))
							.updateViaMap(nullMap);
					}

					for (TaskContext task: tasks) {
						if (task.isPreRequest()) {
							continue;
						}
						long sectionId = task.getSectionId();
						TaskSectionContext taskSection = TicketAPI.getTaskSection(sectionId);
						Map<String, Long> taskMap = null;
						ResourceContext resource = null;
						if (task.getResource() == null) {
							Set<Map.Entry<String, Map<String, Long>>> entries = pmLookup.entrySet();
							if (entries.size() == 1) {
								for (Map.Entry<String, Map<String, Long>> entry: entries) {
									taskMap = entry.getValue();
								}
							} else if (allTasksAssignmentTypeIs5) {
								if (workOrderContext.getResource() == null) {
									LOGGER.log(Level.ERROR, "workorder context resource is empty woid: " + workOrderContext.getId());
									continue;
								}
								LOGGER.log(Level.ERROR, "resource id fetched from workorder context " + workOrderContext.getResource().getId());
								resource = ResourceAPI.getResource(workOrderContext.getResource().getId());
							} else {
								resourceIsMissing.add(pm.getId());
								LOGGER.log(Level.ERROR, "resource is missing " + task.getSubject());
								continue;
							}
						} else {
							resource = ResourceAPI.getResource(task.getResource().getId());
						}

						if (taskMap == null) {
							if (resource == null) {
								Set<Map.Entry<String, Map<String, Long>>> entries = pmLookup.entrySet();
								if (entries.size() == 1) {
									for (Map.Entry<String, Map<String, Long>> entry: entries) {
										taskMap = entry.getValue();
									}
								} else {
									invalidResource.add(pm.getId());
									LOGGER.log(Level.ERROR, "invalid resource " + task.getResource().getId());
									continue;
								}
							}
						}

						if (taskMap == null) {
							String resourceName = resource.getName();
							String sectionName;
							try {
								sectionName = taskSection.getName().substring((resourceName + " - ").length());
								taskMap = pmLookup.get(sectionName);
							} catch (Exception e) {
								try {
									Set<Map.Entry<String, Map<String, Long>>> entries = pmLookup.entrySet();
									if (entries.size() == 1) {
										for (Map.Entry<String, Map<String, Long>> entry: entries) {
											taskMap = entry.getValue();
										}
									} else {
										unusualSectionName.add(pm.getId());
										LOGGER.log(Level.ERROR, "unusual section name " + taskSection.getName());
										continue;
									}
								} catch (Exception e2) {
									unusualSectionName.add(pm.getId());
									LOGGER.log(Level.ERROR, "unusual section name " + taskSection.getName());
									continue;
								}
							}
						}

						if (taskMap == null) {
							taskMapIsEmpty.add(pm.getId());
							LOGGER.log(Level.ERROR, "task map is empty for section name " + taskSection.getName());
							continue;
						}

						Long uniqueId = taskMap.get(task.getSubject());
						if (uniqueId == null) {
							uniqueIdIsMissing.add(pm.getId());
							LOGGER.log(Level.ERROR, "unique id is missing for " + task.getId());
						} else {
							LOGGER.log(Level.ERROR, "task id " + task.getId() + " uniqueId " + uniqueId);
							if (doMigration) {
								Map<String, Object> updateMap = new HashMap<>();
								updateMap.put("uniqueId", uniqueId);
								UpdateRecordBuilder<TaskContext> updateRecordBuilder = new UpdateRecordBuilder<>();
								updateRecordBuilder.moduleName("task")
										.fields(Collections.singletonList(taskFieldMap.get("uniqueId")))
										.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(taskModule), task.getId()+"", NumberOperators.EQUALS))
										.updateViaMap(updateMap);
							}
						}
					}
				}
			}
			LOGGER.log(Level.WARN, "resourceIsMissing " + Arrays.toString(resourceIsMissing.toArray()));
			LOGGER.log(Level.WARN, "invalidResource " + Arrays.toString(invalidResource.toArray()));
			LOGGER.log(Level.WARN, "unusualSectionName " + Arrays.toString(unusualSectionName.toArray()));
			LOGGER.log(Level.WARN, "taskMapIsEmpty " + Arrays.toString(taskMapIsEmpty.toArray()));
			LOGGER.log(Level.WARN, "uniqueIdIsMissing " + Arrays.toString(uniqueIdIsMissing.toArray()));
			LOGGER.log(Level.WARN, "additionInfoMissing " + Arrays.toString(additionInfoMissing.toArray()));
			LOGGER.log(Level.WARN, "uniqueIdMissing " + Arrays.toString(uniqueIdIsMissing.toArray()));
	}

	public static void verifyScheduleGeneration(List<Long> orgs) throws Exception {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPreventiveMaintenanceFields());
		Criteria statusCriteria = new Criteria();
		statusCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("status"), 1+"", NumberOperators.EQUALS));
		statusCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("triggerType"), String.valueOf(PreventiveMaintenance.TriggerType.ONLY_SCHEDULE_TRIGGER.getVal()), NumberOperators.EQUALS));
		for (long i : orgs) {
			try {
				AccountUtil.setCurrentAccount(i);
				if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
					LOGGER.log(Level.WARN, "Org is missing");
					continue;
				}

				LOGGER.log(Level.WARN, "Verifying org " + i);
				List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getActivePMs(null, statusCriteria, null);
				if (pms == null || pms.isEmpty()) {
					LOGGER.log(Level.WARN, "no Pms in this org");
					continue;
				}

				List<Long> pmIds = pms.stream().map(PreventiveMaintenance::getId).collect(Collectors.toList());

				Map<Long, PreventiveMaintenance> pmMap = FieldUtil.getAsMap(pms);
				Map<Long, Boolean> successMap = new HashMap<>();
				for (long pmId: pmIds) {
					PreventiveMaintenance preventiveMaintenance = pmMap.get(pmId);
					if (preventiveMaintenance.getWoGeneratedUpto() == -1) {
						continue;
					}
					boolean isSuccess = verifyEndTime(preventiveMaintenance.getId(), preventiveMaintenance.getWoGeneratedUpto());
					successMap.put(pmId, isSuccess);
				}
				LOGGER.log(Level.WARN, "PMs with wrong end time: " + Arrays.toString(successMap.entrySet().stream().filter(k -> !k.getValue()).distinct().toArray()));
			} catch (Exception e) {
				LOGGER.log(Level.WARN, "Exception verifying org " + i, e);
				throw e;
			} finally {
				AccountUtil.cleanCurrentAccount();
				LOGGER.log(Level.WARN, "Migration completed orgId: " + i);
			}
		}
	}

	private static boolean verifyEndTime(long pmId, long endTime) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

		List<FacilioField> woFields = modBean.getAllFields(workorderModule.getName());
		Map<String, FacilioField> woFieldMap = FieldFactory.getAsMap(woFields);

		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
				.moduleName("workorder")
				.beanClass(WorkOrderContext.class)
				.select(woFields)
				.andCondition(CriteriaAPI.getCondition(woFieldMap.get("pm"), pmId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(woFieldMap.get("jobStatus"), 1+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(woFieldMap.get("createdTime"), (endTime * 1000)+"", NumberOperators.GREATER_THAN_EQUAL))
				.orderBy("CREATED_TIME desc")
				.limit(1)
				.skipModuleCriteria();;

		List<WorkOrderContext> workOrderContexts = builder.get();

		return CollectionUtils.isEmpty(workOrderContexts);
	}

//	public static void migrateJobs(long orgid) throws Exception {
//		Connection conn = null;
//		PreparedStatement getPstmt = null;
//		ResultSet rs = null;
//
//		List<JobContext> jcs = new ArrayList<>();
//
//		try {
//			conn = FacilioConnectionPool.INSTANCE.getConnection();
//			getPstmt = conn.prepareStatement("SELECT * FROM Jobs WHERE IS_ACTIVE = 1 AND IS_PERIODIC = 1 AND EXECUTION_ERROR_COUNT < 5 AND ORGID = "+orgid);
//
//
//			rs = getPstmt.executeQuery();
//			while(rs.next()) {
//				jcs.add(getJobFromRS(rs));
//			}
//		}
//		catch(SQLException e) {
//			throw e;
//		}
//		finally {
//			DBUtil.closeAll(conn, getPstmt, rs);
//		}
//
//		for (JobContext jc: jcs) {
//			long lastExecutionTime = jc.getJobStartTime();
//			if (lastExecutionTime == 0) {
//				lastExecutionTime = DateTimeUtil.getCurrenTime();
//			}
//			lastExecutionTime = lastExecutionTime / 1000;
//
//			long nextExecutionTime = -1;
//			if (jc.getSchedule() != null) {
//				nextExecutionTime = jc.getSchedule().nextExecutionTime(jc.getJobStartTime() / 1000);
//			}
//			else if (jc.getPeriod() > 0) {
//				nextExecutionTime = jc.getPeriod() + lastExecutionTime;
//			}
//
//			if (nextExecutionTime > -1) {
//				JobStore.updateNextExecutionTimeAndCount(jc.getOrgId(), jc.getJobId(), jc.getJobName(), nextExecutionTime, jc.getCurrentExecutionCount());
//			}
//		}
//
//	}


	private static long getMinWorkOrder(long pmId, long triggerId) throws Exception {
		SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		selectRecordsBuilder.select(fields)
				.module(module)
				.beanClass(WorkOrderContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(1571702400000L), NumberOperators.LESS_THAN))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("trigger"), String.valueOf(triggerId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), String.valueOf(pmId), NumberOperators.EQUALS))
				.orderBy("WorkOrders.CREATED_TIME DESC")
				.limit(1);
		List<WorkOrderContext> workOrderContexts = selectRecordsBuilder.get();

		if (CollectionUtils.isEmpty(workOrderContexts)) {
			return -1L;
		}

		return workOrderContexts.get(0).getCreatedTime();
	}

	private static boolean isWorkorderExists(long pmId, long time) throws Exception {
		SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		selectRecordsBuilder.select(fields)
				.module(module)
				.beanClass(WorkOrderContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(time * 1000), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), String.valueOf(pmId), NumberOperators.EQUALS));
		List<WorkOrderContext> workOrderContexts = selectRecordsBuilder.get();

		return !CollectionUtils.isEmpty(workOrderContexts);
	}

	public static void findMissingExecutions(long orgId, boolean doMigration) throws Exception {
		AccountUtil.setCurrentAccount(orgId);
		if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
			LOGGER.log(Level.WARN, "Org is missing");
			return;
		}

		long currentTime = System.currentTimeMillis();

		List<PreventiveMaintenance> allActivePMs = PreventiveMaintenanceAPI.getAllActivePMs(null);
		for (PreventiveMaintenance pm: allActivePMs) {
			LOGGER.log(Level.WARN, "Executing pm: " + pm.getId());
			Map<Long, List<PMTriggerContext>> pmTriggers = PreventiveMaintenanceAPI.getPMTriggers(Collections.singletonList(pm.getId()));
			List<PMTriggerContext> pmTriggerContexts = pmTriggers.get(pm.getId());
			long start = -1;
			boolean found = false;
			if (CollectionUtils.isEmpty(pmTriggerContexts)) {
				continue;
			}
			for (PMTriggerContext pmt: pmTriggerContexts) {
				if (pmt.getTriggerExecutionSourceEnum() != TriggerExectionSource.SCHEDULE) {
					continue;
				}

				if (pmt.getSchedule().getFrequencyType() < 1) {
					continue;
				}

				long minWorkOrder = PreventiveMaintenanceAPI.getMinWorkOrder(pm.getId(), pmt.getId());
				if (minWorkOrder == -1) {
					//LOGGER.log(Level.WARN, "missing min pm: "+ pm.getId() + " trigger: " + pmt.getId());
					continue;
				}

				//LOGGER.log(Level.WARN, "mintime pm: "+ pm.getId() + " trigger: " + pmt.getId() + " time: " + minWorkOrder);

				long startTime = pmt.getStartTime() / 1000;

				Pair<Long, Integer> nextExecutionTime = pmt.getSchedule().nextExecutionTime(Pair.of(startTime, 0));
				//LOGGER.log(Level.WARN, "first exec pm: "+ pm.getId() + " trigger: " + pmt.getId() + " time: " + nextExecutionTime.getLeft() + " current time: " + currentTime);
				int count = 0;
				long previous = -1;
				while (nextExecutionTime.getLeft() <= pm.getWoGeneratedUpto()) {
					//LOGGER.log(Level.WARN, "next exec pm: "+ pm.getId() + " trigger: " + pmt.getId() + " time: " + nextExecutionTime.getLeft() + " current time: " + currentTime);
					if (count > 500) {
						//LOGGER.log(Level.WARN, "exceeded 500 pm: " + pm.getId() + " trigger: " + pmt.getId());
						break;
					}
					if ((nextExecutionTime.getLeft() * 1000) < minWorkOrder) {
						nextExecutionTime = pmt.getSchedule().nextExecutionTime(nextExecutionTime);
						continue;
					}

					boolean workorderExists = PreventiveMaintenanceAPI.isWorkorderExists(pm.getId(), nextExecutionTime.getLeft());
					if (!workorderExists) {
						LOGGER.log(Level.WARN, "missing work order pm: "+ pm.getId() + " trigger: " + pmt.getId() + " time " + nextExecutionTime.getLeft());
						found = true;
						break;
					} else {
						//LOGGER.log(Level.WARN, "work order exist pm: "+ pm.getId() + " trigger: " + pmt.getId() + " time " + nextExecutionTime.getLeft());
					}
					previous = nextExecutionTime.getLeft();
					nextExecutionTime = pmt.getSchedule().nextExecutionTime(nextExecutionTime);
					count++;
				}
				if(found) {
					start = previous;
					break;
				}
			}

			if (!(start > 0)) {
				continue;
			}

			JobContext jc = new JobContext();
			jc.setJobId(orgId);
			long end = start + (24 * 60 * 60);
			FailedPMNewScheduler.execute(jc, pm.getId(), start, end, doMigration);
		}
	}

	public static void findMissedTriggerSelection(long orgID, boolean doMig, long pmId) throws Exception {

		AccountUtil.setCurrentAccount(orgID);
		if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
			LOGGER.log(Level.WARN, "org is missing");
			return;
		}

		CorrectPMTriggerSelection correctPMTriggerSelection = new CorrectPMTriggerSelection();

		SelectRecordsBuilder<WorkOrderContext> endTimeSelect = new SelectRecordsBuilder<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		endTimeSelect.select(fields)
				.module(module)
				.beanClass(WorkOrderContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), String.valueOf(pmId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("jobStatus"), String.valueOf(3), NumberOperators.EQUALS))
				.orderBy("WorkOrders.CREATED_TIME DESC")
				.limit(1);
		List<WorkOrderContext> endWorkOrderContexts = endTimeSelect.get();

		if (CollectionUtils.isEmpty(endWorkOrderContexts)) {
			LOGGER.log(Level.ERROR, "no end workorder");
			return;
		}

		long endTime = endWorkOrderContexts.get(0).getCreatedTime();

		SelectRecordsBuilder<WorkOrderContext> startTimeSelect = new SelectRecordsBuilder<>();
		startTimeSelect.select(fields)
				.module(module)
				.beanClass(WorkOrderContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), String.valueOf(pmId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("jobStatus"), String.valueOf(3), NumberOperators.EQUALS))
				.orderBy("WorkOrders.CREATED_TIME ASC")
				.limit(1);
		List<WorkOrderContext> startWorkOrderContexts = startTimeSelect.get();

		if (CollectionUtils.isEmpty(startWorkOrderContexts)) {
			LOGGER.log(Level.ERROR, "no start workorder");
			return;
		}

		long startTime = startWorkOrderContexts.get(0).getCreatedTime();

		correctPMTriggerSelection.setStartTime((startTime/1000) - 300);
		correctPMTriggerSelection.setEndTime((endTime/1000) + 300);
		correctPMTriggerSelection.setDoMig(doMig);

		JobContext jc = new JobContext();
		jc.setJobId(pmId);
		correctPMTriggerSelection.execute(jc);
	}

	private static JobContext getJobFromRS(ResultSet rs) throws SQLException, JsonParseException, JsonMappingException, IOException, ParseException {
		JobContext jc = new JobContext();

		jc.setJobId(rs.getLong("JOBID"));

		if(rs.getObject("ORGID") != null) {
			jc.setOrgId(rs.getLong("ORGID"));
		}

		jc.setJobName(rs.getString("JOBNAME"));

		if (rs.getObject("TIMEZONE") != null) {
			jc.setTimezone(rs.getString("TIMEZONE"));
		}

		jc.setActive(rs.getBoolean("IS_ACTIVE"));

		if(rs.getObject("TRANSACTION_TIMEOUT") != null) {
			jc.setTransactionTimeout(rs.getInt("TRANSACTION_TIMEOUT"));
		}

		jc.setIsPeriodic(rs.getBoolean("IS_PERIODIC"));

		if(rs.getObject("PERIOD") != null) {
			jc.setPeriod(rs.getInt("PERIOD"));
		}

		if(rs.getObject("SCHEDULE_INFO") != null) {
			jc.setScheduleJson(rs.getString("SCHEDULE_INFO"));
		}

		jc.setExecutionTime(rs.getLong("NEXT_EXECUTION_TIME"));

		jc.setExecutorName(rs.getString("EXECUTOR_NAME"));

		if(rs.getObject("END_EXECUTION_TIME") != null) {
			jc.setEndExecutionTime(rs.getLong("END_EXECUTION_TIME"));
		}

		if(rs.getObject("MAX_EXECUTION") != null) {
			jc.setMaxExecution(rs.getInt("MAX_EXECUTION"));
		}

		if(rs.getObject("CURRENT_EXECUTION_COUNT") != null) {
			jc.setCurrentExecutionCount(rs.getInt("CURRENT_EXECUTION_COUNT"));
		}

		if(rs.getObject("STATUS") != null) {
			jc.setStatus(rs.getInt("STATUS"));
		}

		if(rs.getObject("JOB_SERVER_ID") != null) {
			jc.setJobServerId(rs.getLong("JOB_SERVER_ID"));
		}

		if(rs.getObject("CURRENT_EXECUTION_TIME") != null) {
			jc.setJobStartTime(rs.getLong("CURRENT_EXECUTION_TIME"));
		}

		if(rs.getObject("EXECUTION_ERROR_COUNT") != null) {
			jc.setJobExecutionCount(rs.getInt("EXECUTION_ERROR_COUNT"));
		}

		if (rs.getObject("LOGGER_LEVEL") != null) {
			jc.setLoggerLevel(rs.getInt("LOGGER_LEVEL"));
		}

		return jc;
	}


	public static void importPM(long pmId, long fromOrg, long toOrg) throws Exception {
		AccountUtil.setCurrentAccount(fromOrg);
		if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
			LOGGER.log(Level.WARN, "org is missing");
			return;
		}

		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, pmId);

		FacilioChain pmSummary = FacilioChainFactory.getNewPreventiveMaintenanceSummaryChain();
		pmSummary.execute(context);

		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		WorkOrderContext wo = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		Map<Long, List<TaskContext>> taskMap = (Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
		Map<Long, List<TaskContext>> preReqMap = (Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_MAP);
		List<TaskContext> taskList = (List<TaskContext>) context.get(FacilioConstants.ContextNames.TASK_LIST);
		List<TaskContext> preReqList = (List<TaskContext>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_LIST);
		List<PrerequisiteApproversTemplate> prerequisiteApproversTemplateList = (List<PrerequisiteApproversTemplate>) context.get(FacilioConstants.ContextNames.PREREQUISITE_APPROVER_TEMPLATES);
		List<TaskSectionTemplate> sectionTemplatesList = (List<TaskSectionTemplate>) context.get(FacilioConstants.ContextNames.TASK_SECTIONS);
		List<TaskSectionTemplate> preReqSectionTemplatesList = (List<TaskSectionTemplate>) context.get(FacilioConstants.ContextNames.PRE_REQUEST_SECTIONS);
		List<PMReminder> pmReminderList = (List<PMReminder>) context.get(FacilioConstants.ContextNames.PM_REMINDERS);
		long typeId = wo.getType().getId();
		wo.setType(TicketAPI.getType(fromOrg, typeId));
		if (wo.getCategory() != null && wo.getCategory().getId() != -1L) {
			wo.setCategory(TicketAPI.getCategory(fromOrg, wo.getCategory().getId()));
		}

		if (wo.getPriority() != null && wo.getPriority().getId() != -1L) {
			wo.setPriority(TicketAPI.getPriority(fromOrg, wo.getPriority().getId()));
		}
		AssetCategoryContext assetCateg = null;
		if (pm.getAssetCategoryId() != null && pm.getAssetCategoryId() != -1) {
			assetCateg = getAssetCateg(pm.getAssetCategoryId());
		}

		SpaceCategoryContext spaceCategoryContext = null;
		if (pm.getSpaceCategoryId() != null && pm.getSpaceCategoryId() != -1) {
			spaceCategoryContext = getSpaceCateg(pm.getSpaceCategoryId());
		}

		Long baseSpaceId = pm.getBaseSpaceId();
		ResourceContext resource = null;
		if (baseSpaceId != null) {
			 resource = ResourceAPI.getResource(baseSpaceId);
		}

		List<PMResourcePlannerContext> resourcePlanners = pm.getResourcePlanners();
		List<PMResourcePlannerContext> newResourcePlanners = new ArrayList<>();

		if (resourcePlanners != null) {
			for (PMResourcePlannerContext resourcePlannerContext: resourcePlanners) {
				ResourceContext rpResource = ResourceAPI.getResource(resourcePlannerContext.getResourceId());

				ResourceContext newResource = getNewResource(rpResource.getName(), fromOrg, toOrg);

				if (newResource == null) {
					return;
				}

				AccountUtil.setCurrentAccount(fromOrg);
				if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
					LOGGER.log(Level.WARN, "org is missing");
					return;
				}

				PMResourcePlannerContext newResourcePlanner = new PMResourcePlannerContext();
				newResourcePlanners.add(newResourcePlanner);

				newResourcePlanner.setResourceId(newResource.getId());
				List<PMResourcePlannerReminderContext> pmResourcePlannerReminderContexts = resourcePlannerContext.getPmResourcePlannerReminderContexts();
				List<PMResourcePlannerReminderContext> newPmResourcePlannerReminderContexts = new ArrayList<>();
				newResourcePlanner.setPmResourcePlannerReminderContexts(newPmResourcePlannerReminderContexts);

				if (pmResourcePlannerReminderContexts != null) {
					for (PMResourcePlannerReminderContext pmResourcePlannerReminderContext: pmResourcePlannerReminderContexts) {
						PMResourcePlannerReminderContext newPmResourcePlannerReminderContext = new PMResourcePlannerReminderContext();
						newPmResourcePlannerReminderContext.setReminderName(pmResourcePlannerReminderContext.getReminderName());
						newPmResourcePlannerReminderContexts.add(newPmResourcePlannerReminderContext);
					}
				}

				List<PMTriggerContext> triggerContexts = resourcePlannerContext.getTriggerContexts();
				List<PMTriggerContext> newTriggerContexts = new ArrayList<>();
				newResourcePlanner.setTriggerContexts(newTriggerContexts);

				for (PMTriggerContext trigger: triggerContexts) {
					PMTriggerContext newtrigger = new PMTriggerContext();
					newtrigger.setName(trigger.getName());
					newTriggerContexts.add(newtrigger);
				}
			}
		}


		List<PMIncludeExcludeResourceContext> includeExcludeResourceContexts = pm.getPmIncludeExcludeResourceContexts();
		List<PMIncludeExcludeResourceContext> newIncludeExcludeResourceContexts = new ArrayList<>();

		if (includeExcludeResourceContexts != null) {
			for (PMIncludeExcludeResourceContext includeExcludeResourceContext: includeExcludeResourceContexts) {
				PMIncludeExcludeResourceContext newIncludeExcludeResourceContext = new PMIncludeExcludeResourceContext();
				ResourceContext resource1 = ResourceAPI.getResource(includeExcludeResourceContext.getResourceId());

				ResourceContext newResource = getNewResource(resource1.getName(), fromOrg, toOrg);
				if (newResource == null) {
					return;
				}

				AccountUtil.setCurrentAccount(fromOrg);
				if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
					LOGGER.log(Level.WARN, "org is missing");
					return;
				}

				newIncludeExcludeResourceContext.setResourceId(newResource.getId());
				newIncludeExcludeResourceContext.setIsInclude(includeExcludeResourceContext.getIsInclude());
				newIncludeExcludeResourceContext.setParentType(includeExcludeResourceContext.getParentType());

				newIncludeExcludeResourceContexts.add(newIncludeExcludeResourceContext);
			}
		}


		List<PMTriggerContext> newPmTriggerContexts = new ArrayList<>();
		if (pm.getTriggers() != null) {
			for (PMTriggerContext pmTriggerContext: pm.getTriggers()) {
				PMTriggerContext newPmTrigger = new PMTriggerContext();
				newPmTrigger.setName(pmTriggerContext.getName());
				newPmTrigger.setTriggerType(pmTriggerContext.getTriggerType());
				newPmTrigger.setStartTime(pmTriggerContext.getStartTime());
				newPmTrigger.setSchedule(pmTriggerContext.getSchedule());
				newPmTrigger.setEndTime(pmTriggerContext.getEndTime());
				newPmTrigger.setStartReading(pmTriggerContext.getStartReading());
				newPmTrigger.setEndReading(pmTriggerContext.getEndReading());
				newPmTrigger.setReadingInterval(pmTriggerContext.getReadingInterval());
				newPmTrigger.setFrequency(pmTriggerContext.getFrequency());
				newPmTrigger.setTriggerExecutionSource(pmTriggerContext.getTriggerExecutionSource());

				newPmTriggerContexts.add(newPmTrigger);
			}
		}

		ResourceContext siteResource = ResourceAPI.getResource(wo.getSiteId());

		AccountUtil.setCurrentAccount(toOrg);
		if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
			LOGGER.log(Level.WARN, "org is missing");
			return;
		}

		FacilioContext addContext = new FacilioContext();

		wo.setRequester(null);
		if(pmReminderList != null) {
			pm.setReminders(pmReminderList);
		}

		ResourceContext newResource = null;
		if (resource != null) {
			newResource = getNewResource(resource.getName(), fromOrg, toOrg);
		}

		ResourceContext newSiteResource = getNewResource(siteResource.getName(), fromOrg, toOrg);
		ResourceContext woResource = null;
		if (wo.getResource() != null && wo.getResource().getId() != -1L) { woResource = getNewResource(wo.getResource().getName(), fromOrg, toOrg); }

		TicketCategoryContext ticketCategoryContext = null;
		if (wo.getCategory() != null) {
			ticketCategoryContext = TicketAPI.getCategory(toOrg, wo.getCategory().getName());
		}

		TicketPriorityContext ticketPriorityContext = null;
		if(wo.getPriority() != null) {
			ticketPriorityContext = TicketAPI.getPriority(toOrg, wo.getPriority().getPriority());
		}

		long newAssetCategoryId = -1L;
		long newSpaceCategoryId = -1L;

		if (assetCateg != null) {
			newAssetCategoryId = AssetsAPI.getCategoryByDisplayName(assetCateg.getName()).getId();
		}

		if (spaceCategoryContext != null) {
			newSpaceCategoryId = getSpaceCateg(spaceCategoryContext.getName()).getId();
		}

		addContext.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, constructPM(pm, newResource != null ? newResource.getId(): -1L, newSiteResource.getSiteId(), newResourcePlanners, newIncludeExcludeResourceContexts, newPmTriggerContexts, newAssetCategoryId, newSpaceCategoryId));
		addContext.put(FacilioConstants.ContextNames.WORK_ORDER, constructWO(wo, TicketAPI.getType(toOrg, wo.getType().getName()),ticketCategoryContext, ticketPriorityContext, woResource, newSiteResource.getSiteId()));
		//addContext.put(FacilioConstants.ContextNames.PREREQUISITE_APPROVER_TEMPLATES, prerequisiteApproversTemplateList);
		addContext.put(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES, constructTaskSectionTemplates(sectionTemplatesList, fromOrg, toOrg));
		addContext.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Template.Type.PM_WORKORDER);

//		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, this.attachedFiles);
//		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, this.attachedFilesFileName);
//		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, this.attachedFilesContentType);
//		context.put(FacilioConstants.ContextNames.ATTACHMENT_TYPE, this.attachmentType);

		FacilioChain addTemplate = FacilioChainFactory.getAddNewPreventiveMaintenanceChain();
		addTemplate.execute(addContext);
	}

	private static SpaceCategoryContext getSpaceCateg(String name) throws Exception {
		Context context = new FacilioContext();
		GetSpaceCategoriesCommand getSpaceCategoriesCommand = new GetSpaceCategoriesCommand();
		getSpaceCategoriesCommand.executeCommand(context);
		List<SpaceCategoryContext> spaceCategories = (List<SpaceCategoryContext>) context.get(FacilioConstants.ContextNames.SPACECATEGORIESLIST);
		return spaceCategories.stream().filter(i -> i.getName() == name).collect(Collectors.toList()).get(0);
	}

	private static SpaceCategoryContext getSpaceCateg(long id) throws Exception {
		Context context = new FacilioContext();
		GetSpaceCategoriesCommand getSpaceCategoriesCommand = new GetSpaceCategoriesCommand();
		getSpaceCategoriesCommand.executeCommand(context);
		List<SpaceCategoryContext> spaceCategories = (List<SpaceCategoryContext>) context.get(FacilioConstants.ContextNames.SPACECATEGORIESLIST);
		return spaceCategories.stream().filter(i -> i.getId() == id).collect(Collectors.toList()).get(0);
	}

	private static Map<Long, SpaceCategoryContext> getAllSpaceCateg() throws Exception {
		Context context = new FacilioContext();
		GetSpaceCategoriesCommand getSpaceCategoriesCommand = new GetSpaceCategoriesCommand();
		getSpaceCategoriesCommand.executeCommand(context);
		List<SpaceCategoryContext> spaceCategories = (List<SpaceCategoryContext>) context.get(FacilioConstants.ContextNames.SPACECATEGORIESLIST);
		if (CollectionUtils.isEmpty(spaceCategories)) {
			return Collections.EMPTY_MAP;
		}
		Map<Long, SpaceCategoryContext> map = new HashMap<>();
		for (SpaceCategoryContext spaceCategoryContext: spaceCategories) {
			map.put(spaceCategoryContext.getId(), spaceCategoryContext);
		}
		return map;
	}

	private static AssetCategoryContext getAssetCateg(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		SelectRecordsBuilder<AssetCategoryContext> selectBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
				.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY))
				.moduleName(FacilioConstants.ContextNames.ASSET_CATEGORY)
				.beanClass(AssetCategoryContext.class)
				.andCustomWhere("ID = ?", id);

		List<AssetCategoryContext> categories = selectBuilder.get();

		if(categories != null && !categories.isEmpty()) {
			return categories.get(0);
		}
		return null;
	}

	private static Map<Long, AssetCategoryContext> getAllAssetCateg() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		SelectRecordsBuilder<AssetCategoryContext> selectBuilder = new SelectRecordsBuilder<AssetCategoryContext>()
				.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_CATEGORY))
				.moduleName(FacilioConstants.ContextNames.ASSET_CATEGORY)
				.beanClass(AssetCategoryContext.class);

		List<AssetCategoryContext> categories = selectBuilder.get();
		if (CollectionUtils.isEmpty(categories)) {
			return Collections.EMPTY_MAP;
		}

		Map<Long, AssetCategoryContext> map = new HashMap<>();
		for (AssetCategoryContext assetCategoryContext: categories) {
			map.put(assetCategoryContext.getId(), assetCategoryContext);
		}

		return map;
	}

	private static ResourceContext getNewResource(String name, long fromOrg, long toOrg) throws Exception {
		AccountUtil.setCurrentAccount(toOrg);
		if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
			LOGGER.log(Level.WARN, "org is missing");
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> allFields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
		Map<String, FacilioField> asMap = FieldFactory.getAsMap(allFields);

		Criteria cr = new Criteria();
		cr.addAndCondition(CriteriaAPI.getCondition(asMap.get("name"), name, StringOperators.IS));

		SelectRecordsBuilder<ResourceContext> resourceBuilder = new SelectRecordsBuilder<ResourceContext>()
				.select(allFields).module(module)
				.beanClass(ResourceContext.class).andCriteria(cr);

		List<ResourceContext> resources = resourceBuilder.get();
		if (resources != null && !resources.isEmpty()) {
			return resources.get(0);
		}
		return null;
	}


	private static WorkOrderContext constructWO(WorkOrderContext oldWo, TicketTypeContext type, TicketCategoryContext ticketCategoryContext, TicketPriorityContext priorityContext, ResourceContext newResource, long newSiteId) {
		WorkOrderContext newWO = new WorkOrderContext();
		newWO.setType(type);
		newWO.setCategory(ticketCategoryContext);
		newWO.setPriority(priorityContext);
		newWO.setSubject(oldWo.getSubject());
		newWO.setDescription(oldWo.getDescription());
		newWO.setDuration(oldWo.getDuration());
		newWO.setEstimatedWorkDuration(oldWo.getEstimatedWorkDuration());
		newWO.setIsSignatureRequired(oldWo.getIsSignatureRequired());
		newWO.setQrEnabled(oldWo.getQrEnabled());
		newWO.setSiteId(newSiteId);
		newWO.setPhotoMandatory(oldWo.getPhotoMandatory());
		newWO.setResource(newResource);
		return newWO;
	}

	private static PreventiveMaintenance constructPM(PreventiveMaintenance oldPM, Long newBaseSpaceId, long newSiteId, List<PMResourcePlannerContext> newResourcePlanners, List<PMIncludeExcludeResourceContext> newIncludeExcludeResourceContexts, List<PMTriggerContext> newPmTriggerContexts, long newAssetCategoryId, long newSpaceCategoryId) {
		PreventiveMaintenance newPm = new PreventiveMaintenance();
		newPm.setTitle(oldPM.getTitle());
		newPm.setPreventOnNoTask(oldPM.getPreventOnNoTask());
		newPm.setPmCreationType(oldPM.getPmCreationType());
		newPm.setBaseSpaceId(newBaseSpaceId);
		newPm.setAssignmentType(oldPM.getAssignmentType());
		newPm.setSiteId(newSiteId);
		newPm.setResourcePlanners(newResourcePlanners);
		newPm.setAssetCategoryId(newAssetCategoryId);
		newPm.setSpaceCategoryId(newSpaceCategoryId);
		newPm.setPmIncludeExcludeResourceContexts(newIncludeExcludeResourceContexts);
		newPm.setTriggers(newPmTriggerContexts);

		return newPm;
	}

	private static List<TaskSectionTemplate> constructTaskSectionTemplates(List<TaskSectionTemplate> oldTaskSectionTemplates, long fromOrg, long toOrg) throws Exception {
		AccountUtil.setCurrentAccount(toOrg);
		if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
			LOGGER.log(Level.WARN, "org is missing");
			return null;
		}
		List<TaskSectionTemplate> newTaskSectionTemplates = new ArrayList<>();
		for (TaskSectionTemplate taskSectionTemplate: oldTaskSectionTemplates) {
			TaskSectionTemplate newTaskSectionTemplate = new TaskSectionTemplate();
			newTaskSectionTemplate.setName(taskSectionTemplate.getName());
			newTaskSectionTemplate.setInputType(taskSectionTemplate.getInputType());
			newTaskSectionTemplate.setAttachmentRequired(taskSectionTemplate.getAttachmentRequired());
			newTaskSectionTemplate.setAdditionInfo(taskSectionTemplate.getAdditionInfo());
			newTaskSectionTemplate.setAssignmentType(taskSectionTemplate.getAssignmentType());
			Long oldAssetCategoryId = taskSectionTemplate.getAssetCategoryId();

			if (oldAssetCategoryId != null && oldAssetCategoryId != -1) {
				AccountUtil.setCurrentAccount(fromOrg);
				if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
					LOGGER.log(Level.WARN, "org is missing");
					return null;
				}

				AssetCategoryContext oldAssetCateg = getAssetCateg(oldAssetCategoryId);

				AccountUtil.setCurrentAccount(toOrg);
				if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
					LOGGER.log(Level.WARN, "org is missing");
					return null;
				}

				AssetCategoryContext newAssetCateg = AssetsAPI.getCategoryByDisplayName(oldAssetCateg.getName());

				newTaskSectionTemplate.setAssetCategoryId(newAssetCateg.getId());
			}

			Long oldSpaceCategoryId = taskSectionTemplate.getSpaceCategoryId();

			if (oldSpaceCategoryId != null && oldSpaceCategoryId != -1) {
				AccountUtil.setCurrentAccount(fromOrg);
				if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
					LOGGER.log(Level.WARN, "org is missing");
					return null;
				}

				SpaceCategoryContext oldSpaceCateg = getSpaceCateg(oldSpaceCategoryId);

				AccountUtil.setCurrentAccount(toOrg);
				if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
					LOGGER.log(Level.WARN, "org is missing");
					return null;
				}

				SpaceCategoryContext newSpaceCateg = getSpaceCateg(oldSpaceCateg.getName());
				newTaskSectionTemplate.setSpaceCategoryId(newSpaceCateg.getId());
			}

			List<TaskTemplate> taskTemplates = new ArrayList<>();
			newTaskSectionTemplate.setTaskTemplates(taskTemplates);

			newTaskSectionTemplates.add(newTaskSectionTemplate);

			for (TaskTemplate taskTemplate: taskSectionTemplate.getTaskTemplates()) {
				TaskTemplate newTaskTemplate = new TaskTemplate();
				newTaskTemplate.setName(taskTemplate.getName());
				newTaskTemplate.setDescription(taskTemplate.getDescription());
				newTaskTemplate.setAttachmentRequired(taskTemplate.getAttachmentRequired());
				newTaskTemplate.setSequence(taskTemplate.getSequence());
				newTaskTemplate.setAssignmentType(taskTemplate.getAssignmentType());
				newTaskTemplate.setInputType(taskTemplate.getInputType());
				newTaskTemplate.setAdditionInfo(taskTemplate.getAdditionInfo());

				if (newTaskTemplate.getAdditionInfo() != null) {
					newTaskTemplate.getAdditionInfo().remove("readingField");
				}

				if (taskTemplate.getReadingFieldId() != -1 && taskTemplate.getInputType() == 2) {
					AccountUtil.setCurrentAccount(fromOrg);
					if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
						LOGGER.log(Level.WARN, "org is missing");
						return null;
					}

					ModuleBean oldModBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioField oldReadingField = oldModBean.getField(taskTemplate.getReadingFieldId());
					long moduleId = oldReadingField.getModuleId();
					FacilioModule oldModule = oldModBean.getModule(moduleId);

					AccountUtil.setCurrentAccount(toOrg);
					if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
						LOGGER.log(Level.WARN, "org is missing");
						return null;
					}

					ModuleBean newModBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioField newReadingField = newModBean.getField(oldReadingField.getName(), oldModule.getName());

					newTaskTemplate.setReadingFieldId(newReadingField.getId());
				}

				taskTemplates.add(newTaskTemplate);
			}
		}
		return newTaskSectionTemplates;
	}


	public static void deleteJobPlansForPm(Long pmId) throws Exception {

		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		Map<String, FacilioField> fieldMap =  FieldFactory.getAsMap(FieldFactory.getPreventiveMaintenanceFields());

		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmId"), String.valueOf(pmId), NumberOperators.EQUALS));
		deleteRecordBuilder.delete();

	}

	public static void addJobPlansForPm(PreventiveMaintenance pm) throws Exception {
		List<Map<String, Object>> paramsMap = new ArrayList<>();
		List<String> triggerNames = new ArrayList<>();
		for(PMJobPlanContextV3 pmJp : pm.getJobPlanList()) {
			if(CollectionUtils.isNotEmpty(pmJp.getJpPmTriggers())){
				for(PMTriggerContext pmTrigger : pmJp.getJpPmTriggers()) {
					if (!triggerNames.contains(pmTrigger.getName())){
						triggerNames.add(pmTrigger.getName());
					}
					else {
						throw new IllegalArgumentException("Please associate different triggers for every job plan");
					}
				}
			}
			Map<String, Object> relProp = FieldUtil.getAsProperties(pmJp);
			relProp.put("pmId", pm.getId());
			paramsMap.add(relProp);

		}

		FacilioModule module = ModuleFactory.getPMJobPlanV3Module();
		Map<String, FacilioField> fieldMap =  FieldFactory.getAsMap(FieldFactory.getPMJobPlanV3Fields());

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getPMJobPlanV3Fields())
				;
		insertBuilder.addRecords(paramsMap);

		insertBuilder.save();
	}

	public static List<PMJobPlanContextV3> getJobPlanV3FromPM(long pmId) throws Exception {

		FacilioModule module = ModuleFactory.getPMJobPlanV3Module();
		List<FacilioField> fields = FieldFactory.getPMJobPlanV3Fields();
		Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(fields);


		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("pmId"), String.valueOf(pmId), NumberOperators.EQUALS))
				;

		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {

			List<PMJobPlanContextV3> pmJobPlans = new ArrayList<>();
			for(Map<String, Object> prop :props) {
				PMJobPlanContextV3 pmJobPlan = FieldUtil.getAsBeanFromMap(prop, PMJobPlanContextV3.class);
				pmJobPlan.setJobPlanContext(JobPlanAPI.getJobPlan(pmJobPlan.getJobPlanId()));
				pmJobPlans.add(pmJobPlan);
			}
			return pmJobPlans;
		}
		return null;
	}
	
	public static List<String> getTemplateFields() {
		return LookupSpecialTypeUtil.getAllFields(ContextNames.WORK_ORDER_TEMPLATE)
				.stream().map(field -> field.getName()).collect(Collectors.toList());
	}
	
	public static String getPmModule(List<String> templateFields, String fieldName) {
		if (templateFields.contains(fieldName)) {
			// check if the field is ID and return PREVENTIVE_MAINTENANCE
			if(fieldName.equals("id")){
				return FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE;
			}
			return FacilioConstants.ContextNames.WORK_ORDER_TEMPLATE;
		}
		return FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE;
	}
	public static List<PMResourcePlannerContext> getPMForResources(List<Long> resourceIds) throws Exception {
		if (CollectionUtils.isNotEmpty(resourceIds)) {
			FacilioModule module = ModuleFactory.getPMResourcePlannerModule();
			Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getPMResourcePlannerFields());
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getPMResourcePlannerFields())
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldsMap.get("resourceId"), resourceIds, NumberOperators.EQUALS));
			List<Map<String, Object>> props = selectBuilder.get();
			if(CollectionUtils.isNotEmpty(props)){
				return FieldUtil.getAsBeanListFromMapList(props, PMResourcePlannerContext.class);
			}
		}
		return null;
	}

	public static Long getLastTriggeredTime(PreventiveMaintenance pm) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioField pmField = modBean.getField("pm", FacilioConstants.ContextNames.WORK_ORDER);
		FacilioField createdTimeField = modBean.getField("createdTime", FacilioConstants.ContextNames.WORK_ORDER);

		SelectRecordsBuilder<WorkOrderContext> selectBuilder = new SelectRecordsBuilder<WorkOrderContext>()
				.beanClass(WorkOrderContext.class)
				.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
				.select(Collections.singletonList(createdTimeField))
				.andCondition(CriteriaAPI.getCondition(pmField, String.valueOf(pm.getId()), PickListOperators.IS))
				.orderBy("WorkOrders.CREATED_TIME DESC")
				.limit(1);

		List<WorkOrderContext> wos = selectBuilder.get();
		if(wos != null && wos.size() > 0)
			return wos.get(0).getCreatedTime();
		return -1L;
	}
}
