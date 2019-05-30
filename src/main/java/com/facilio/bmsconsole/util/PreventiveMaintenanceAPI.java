package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.PMJobsContext.PMJobsStatus;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.bmsconsole.context.PMTriggerContext.TriggerExectionSource;
import com.facilio.bmsconsole.context.PMTriggerContext.TriggerType;
import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.TaskTemplate;
import com.facilio.bmsconsole.templates.TaskTemplate.AttachmentRequiredEnum;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
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
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.time.DateTimeUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PreventiveMaintenanceAPI {
	
	private static final Logger LOGGER = Logger.getLogger(PreventiveMaintenanceAPI.class.getName());
	public static final int PM_CALCULATION_DAYS = 62;
	
	public static List<PMJobsContext> createPMJobs (PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime, long endTime) throws Exception { //Both in seconds
		return createPMJobs(pm, pmTrigger, startTime, endTime, true);
	}
	
	public static PMTriggerContext getDefaultTrigger(List<PMTriggerContext> triggers) {
		
		for(PMTriggerContext trigger :triggers) {
			if(trigger.getTriggerType() == TriggerType.DEFAULT.getVal()) {
				return trigger;
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
	public static Map<String, List<TaskContext>> getTaskMapForNewPMExecution(List<TaskSectionTemplate> sectiontemplates,Long woResourceId, Long triggerId) throws Exception {
		Map<String, List<TaskContext>> taskMap = new LinkedHashMap<>();
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
			Template sectionTemplate = TemplateAPI.getTemplate(sectiontemplate.getId());
			sectiontemplate = (TaskSectionTemplate)sectionTemplate;
			
			 List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(PMAssignmentType.valueOf(sectiontemplate.getAssignmentType()), woResourceId, sectiontemplate.getSpaceCategoryId(), sectiontemplate.getAssetCategoryId(),sectiontemplate.getResourceId(),sectiontemplate.getPmIncludeExcludeResourceContexts());
			 Map<String, Integer> dupSectionNameCount = new HashMap<>();
			 for(Long resourceId :resourceIds) {
				 if(resourceId == null || resourceId < 0) {
					 continue;
				 }
				 ResourceContext sectionResource = ResourceAPI.getResource(resourceId);
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
					 
					 List<Long> taskResourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(PMAssignmentType.valueOf(taskTemplate.getAssignmentType()), sectionResource.getId(), taskTemplate.getSpaceCategoryId(), taskTemplate.getAssetCategoryId(),taskTemplate.getResourceId(),taskTemplate.getPmIncludeExcludeResourceContexts());

					 applySectionSettingsIfApplicable(sectiontemplate,taskTemplate);
					 for(Long taskResourceId :taskResourceIds) {
					 	if (sectionAssignmentType == PMAssignmentType.ASSET_CATEGORY.getVal() || sectionAssignmentType == PMAssignmentType.SPACE_CATEGORY.getVal()) {
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

	public static Map<String, List<TaskContext>> getPreRequestMapForNewPMExecution(
			List<TaskSectionTemplate> sectiontemplates, Long woResourceId, Long triggerId) throws Exception {
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
					sectiontemplate.getResourceId(), sectiontemplate.getPmIncludeExcludeResourceContexts());
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
							taskTemplate.getResourceId(), taskTemplate.getPmIncludeExcludeResourceContexts());

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
	public static List<Long> getMultipleResourceToBeAddedFromPM(PMAssignmentType pmAssignmentType, Long resourceId,Long spaceCategoryID,Long assetCategoryID,Long currentAssetId, List<PMIncludeExcludeResourceContext> includeExcludeRess) throws Exception {
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
			return includedIds;
		}
		List<Long> resourceIds = new ArrayList<>();
		switch(pmAssignmentType) {
			case ALL_FLOORS:
				List<BaseSpaceContext> floors = SpaceAPI.getBuildingFloors(resourceId);
				for(BaseSpaceContext floor :floors) {
					resourceIds.add(floor.getId());
				}
				break;
			case ALL_SPACES:
				resourceIds = SpaceAPI.getSpaceIdListForBuilding(resourceId);
				break;
			case SPACE_CATEGORY:
				List<SpaceContext> spaces = SpaceAPI.getSpaceListOfCategory(resourceId, spaceCategoryID);
				for(SpaceContext space :spaces) {
					resourceIds.add(space.getId());
				}
				break;
			case ASSET_CATEGORY:
				List<AssetContext> assets = AssetsAPI.getAssetListOfCategory(assetCategoryID, resourceId);
				
				for(AssetContext asset :assets) {
					resourceIds.add(asset.getId());
				}
				break;
			case CURRENT_ASSET:
				resourceIds = Collections.singletonList(resourceId);
				break;
			case SPECIFIC_ASSET:
				resourceIds = Collections.singletonList(currentAssetId);
				break;
		default:
			break;
		}
		if(excludedIds != null) {
			resourceIds.removeAll(excludedIds);
		}
		return resourceIds;
 	}
	
	public static PMJobsContext getpmJob(PreventiveMaintenance pm,PMTriggerContext pmTrigger ,Long resourceId,Long nextExecutionTime, boolean addToDb) {
		PMJobsContext pmJob = new PMJobsContext();
		pmJob.setPmId(pm.getId());
		pmJob.setResourceId(resourceId);
		pmJob.setPmTriggerId(pmTrigger.getId());
		pmJob.setNextExecutionTime(nextExecutionTime);
		pmJob.setProjected(!addToDb);
		pmJob.setStatus(PMJobsStatus.ACTIVE);
		return pmJob;
	}
	public static List<PMJobsContext> createPMJobs (PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime, long endTime, boolean addToDb) throws Exception { //Both in seconds
		return createPMJobs(pm,pmTrigger,null,startTime,endTime,addToDb);
	}

	public static List<WorkOrderContext> createWOContextsFromPM(Context context, PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime, WorkorderTemplate woTemplate) throws Exception {
		long nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(startTime);
		int currentCount = pm.getCurrentExecutionCount();
		List<WorkOrderContext> wos = new ArrayList<>();
		long endTime = DateTimeUtil.getDayStartTime(pmTrigger.getFrequencyEnum().getMaxSchedulingDays(), true) - 1;
		long currentTime = System.currentTimeMillis();
		boolean isScheduled = false;
		while (nextExecutionTime <= endTime && (pm.getMaxCount() == -1 || currentCount < pm.getMaxCount()) && (pm.getEndTime() == -1 || nextExecutionTime <= pm.getEndTime())) {
			if ((nextExecutionTime * 1000) < currentTime) {
				LOGGER.log(Level.SEVERE, "Skipping : next: "+ nextExecutionTime * 1000 + " current: "+ currentTime);
				nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(nextExecutionTime);
				if (pmTrigger.getSchedule().getFrequencyTypeEnum() == FrequencyType.DO_NOT_REPEAT) {
					break;
				}
				continue;
			}

			WorkOrderContext wo = createWoContextFromPM(context, pm, pmTrigger, woTemplate, nextExecutionTime);

			wos.add(wo);

			nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(nextExecutionTime);
			currentCount++;
			if (pmTrigger.getSchedule().getFrequencyTypeEnum() == FrequencyType.DO_NOT_REPEAT) {
				break;
			}
			isScheduled = true;
		}
		if (!isScheduled && pmTrigger.getFrequencyEnum() != FacilioFrequency.ANNUALLY) {
			LOGGER.log(Level.SEVERE, "No Work orders generated for PM "+ pm.getId() + " PM Trigger ID: "+pmTrigger.getId());
			CommonCommandUtil.emailAlert("No Work orders generated for pm", "PM "+ pm.getId() + " PM Trigger ID: "+pmTrigger.getId());
		}
		return wos;
	}

	public static WorkOrderContext createWoContextFromPM(Context context, PreventiveMaintenance pm, PMTriggerContext pmTrigger, WorkorderTemplate woTemplate, long nextExecutionTime) throws Exception {
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
				taskMapForNewPmExecution = PreventiveMaintenanceAPI.getTaskMapForNewPMExecution(woTemplate.getSectionTemplates(), woTemplateResourceId, currentTriggerId);
			}
		} else {
			taskMapForNewPmExecution = woTemplate.getTasks();
		}

		if (AccountUtil.getCurrentOrg().getOrgId() == 92 && (pm.getId() == 15831 || pm.getId() == 16191)) {
			LOGGER.log(Level.SEVERE, "isNewPmType: "+ isNewPmType + "has sections: " + (woTemplate.getSectionTemplates() != null && !woTemplate.getSectionTemplates().isEmpty()) + "has tasks: "+ (woTemplate.getTasks() != null && !woTemplate.getTasks().isEmpty()));
		}

		if(taskMapForNewPmExecution != null) {
			taskMap = taskMapForNewPmExecution;
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
						woTemplate.getPreRequestSectionTemplates(), woTemplateResourceId, currentTriggerId);
			}
		} else {
			preRequestMapForNewPmExecution = woTemplate.getPreRequests();
		}

		if (AccountUtil.getCurrentOrg().getOrgId() == 92 && (pm.getId() == 15831 || pm.getId() == 16191)) {
			LOGGER.log(Level.SEVERE,
					"isNewPmType: " + isNewPmType + "has pre request sections: "
							+ (woTemplate.getPreRequestSectionTemplates() != null
									&& !woTemplate.getPreRequestSectionTemplates().isEmpty())
							+ "has pre requests: "
							+ (woTemplate.getPreRequests() != null && !woTemplate.getPreRequests().isEmpty()));
		}

		if (preRequestMapForNewPmExecution != null) {
			preRequestMap = preRequestMapForNewPmExecution;
		}
		wo.setSourceType(TicketContext.SourceType.PREVENTIVE_MAINTENANCE);
		wo.setPm(pm);
		context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
		context.put(FacilioConstants.ContextNames.REQUESTER, wo.getRequester());
		context.put(FacilioConstants.ContextNames.TASK_MAP, taskMap);
		context.put(FacilioConstants.ContextNames.PRE_REQUEST_MAP, preRequestMap);
		context.put(FacilioConstants.ContextNames.IS_PM_EXECUTION, true);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST, wo.getAttachments());

		//Temp fix. Have to be removed eventually
		PreventiveMaintenanceAPI.updateResourceDetails(wo, taskMap);
		Chain addWOChain = TransactionChainFactory.getAddPreOpenedWorkOrderChain();
		addWOChain.execute(context);

		return wo;
	}

	public static List<PMJobsContext> createPMJobs (PreventiveMaintenance pm, PMTriggerContext pmTrigger,Long resourceId, long startTime, long endTime, boolean addToDb) throws Exception { //Both in seconds
		long nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(startTime);
		int currentCount = pm.getCurrentExecutionCount();
		List<PMJobsContext> pmJobs = new ArrayList<>();
		while (nextExecutionTime <= endTime && (pm.getMaxCount() == -1 || currentCount < pm.getMaxCount()) && (pm.getEndTime() == -1 || nextExecutionTime <= pm.getEndTime())) {
			PMJobsContext pmJob = new PMJobsContext();
			pmJob.setPmId(pm.getId());
			pmJob.setPmTriggerId(pmTrigger.getId());
			pmJob.setNextExecutionTime(nextExecutionTime);
			pmJob.setProjected(!addToDb);
			pmJob.setStatus(PMJobsStatus.ACTIVE);
			if(resourceId != null) {
				pmJob.setResourceId(resourceId);
			}
			pmJobs.add(pmJob);
			nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(nextExecutionTime);
			currentCount++;
			if(pmTrigger.getSchedule().getFrequencyTypeEnum() == FrequencyType.DO_NOT_REPEAT) {
				break;
			}
		}
		if(addToDb) {
			addPMJobs(pmJobs);
		}
		return pmJobs;
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
	
	public static PMJobsContext createPMJobOnce(PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime) throws Exception {
		return createPMJobOnce(pm, pmTrigger, startTime, true);
	}
	
	public static PMJobsContext createPMJobOnce(PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime, boolean addToDb) throws Exception {
		long nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(startTime);
		int currentCount = pm.getCurrentExecutionCount();
		if((pm.getMaxCount() == -1 || currentCount < pm.getMaxCount()) && (pm.getEndTime() == -1 || nextExecutionTime <= pm.getEndTime())) {
			PMJobsContext pmJob = new PMJobsContext();
			pmJob.setPmId(pm.getId());
			pmJob.setPmTriggerId(pmTrigger.getId());
			pmJob.setNextExecutionTime(nextExecutionTime);
			pmJob.setStatus(PMJobsStatus.ACTIVE);
			pmJob.setProjected(!addToDb);
			
			if (addToDb) {
				addPMJob(pmJob);
			}
			return pmJob;
		}
		return null;
	}
	
	public static List<PMJobsContext> createNNumberOfPMJobs(PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime, long count) throws Exception {
		long nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(startTime);
		int currentCount = pm.getCurrentExecutionCount();
		int jobCount = 0;
		List<PMJobsContext> pmJobs = new ArrayList<>();
		while (jobCount < count && (pm.getMaxCount() == -1 || currentCount < pm.getMaxCount()) && (pm.getEndTime() == -1 || nextExecutionTime <= pm.getEndTime())) {
			PMJobsContext pmJob = new PMJobsContext();
			pmJob.setPmId(pm.getId());
			pmJob.setPmTriggerId(pmTrigger.getId());
			pmJob.setNextExecutionTime(nextExecutionTime);
			pmJob.setStatus(PMJobsStatus.ACTIVE);
			pmJobs.add(pmJob);
			nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(nextExecutionTime);
			currentCount++;
			jobCount++;
			if(pmTrigger.getSchedule().getFrequencyTypeEnum() == FrequencyType.DO_NOT_REPEAT) {
				break;
			}
		}
		addPMJobs(pmJobs);
		return pmJobs;
	}
	
	public static PMJobsContext updateAndGetPMJob(PMJobsContext pmJob) throws Exception {
		updatePMJob(pmJob);
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(pmJob.getId(), pmJobsModule));
		
		List<Map<String, Object>> jobProps = selectBuilder.get();
		if(jobProps != null && !jobProps.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(jobProps.get(0), PMJobsContext.class);
		}
		return null;
		
														
	}
	
	public static int updatePMJob(PMJobsContext pmJob) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.fields(fields)
															.table(pmJobsModule.getTableName())
															.andCondition(CriteriaAPI.getIdCondition(pmJob.getId(), pmJobsModule));
		return updateBuilder.update(FieldUtil.getAsProperties(pmJob));
	}
	
	public static void updatePMJobStatus(long id, PMJobsStatus status) throws Exception {
		PMJobsContext updatedJob = new PMJobsContext();
		updatedJob.setId(id);
		updatedJob.setStatus(status);
		updatePMJob(updatedJob);
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
				.limit(1);
		if (onlyActive) {
			selectRecordsBuilder.andCondition(CriteriaAPI.getCondition(isActive, String.valueOf(PMJobsStatus.IN_ACTIVE.getValue()), NumberOperators.NOT_EQUALS));
		}

		List<WorkOrderContext> workOrders = selectRecordsBuilder.get();
		if(workOrders != null && !workOrders.isEmpty()) {
			return workOrders.get(0);
		}
		return null;

	}
	
	public static PMJobsContext getNextPMJob(long pmTriggerId, long currentTime, boolean onlyActive) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		FacilioField pmTriggerField = fieldsMap.get("pmTriggerId");
		FacilioField nextExecutionField = fieldsMap.get("nextExecutionTime");
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getCondition(pmTriggerField, String.valueOf(pmTriggerId), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(nextExecutionField, String.valueOf(currentTime), NumberOperators.GREATER_THAN))
														.orderBy(nextExecutionField.getColumnName())
														.limit(1)
														;
		
		if (onlyActive) {
			FacilioField isActive = fieldsMap.get("status");
			selectBuilder.andCondition(CriteriaAPI.getCondition(isActive, String.valueOf(PMJobsStatus.IN_ACTIVE.getValue()), NumberOperators.NOT_EQUALS));
		}
		
		List<Map<String, Object>> jobProps = selectBuilder.get();
		if(jobProps != null && !jobProps.isEmpty()) {
			PMJobsContext pmJob = FieldUtil.getAsBeanFromMap(jobProps.get(0), PMJobsContext.class);
			return pmJob;
		}
		return null;
	}
	public static PMJobsContext getNextPMJob(long pmTriggerId, long currentTime,long resourceId, boolean onlyActive) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		FacilioField pmTriggerField = fieldsMap.get("pmTriggerId");
		FacilioField nextExecutionField = fieldsMap.get("nextExecutionTime");
		FacilioField resourceField = fieldsMap.get("resourceId");
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getCondition(pmTriggerField, String.valueOf(pmTriggerId), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(nextExecutionField, String.valueOf(currentTime), NumberOperators.GREATER_THAN))
														.andCondition(CriteriaAPI.getCondition(resourceField, String.valueOf(resourceId), NumberOperators.EQUALS))
														.limit(1)
														;
		
		if (onlyActive) {
			FacilioField isActive = fieldsMap.get("status");
			selectBuilder.andCondition(CriteriaAPI.getCondition(isActive, String.valueOf(PMJobsStatus.IN_ACTIVE.getValue()), NumberOperators.NOT_EQUALS));
		}
		
		List<Map<String, Object>> jobProps = selectBuilder.get();
		if(jobProps != null && !jobProps.isEmpty()) {
			PMJobsContext pmJob = FieldUtil.getAsBeanFromMap(jobProps.get(0), PMJobsContext.class);
			return pmJob;
		}
		return null;
	}
	
	public static PMJobsContext getLastPMJob(PMTriggerContext pmTrigger) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		FacilioField pmTriggerField = fieldsMap.get("pmTriggerId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getCondition(pmTriggerField, String.valueOf(pmTrigger.getId()), NumberOperators.EQUALS))
														.orderBy("NEXT_EXECUTION_TIME DESC")
														.limit(1)
														;
		
		List<Map<String, Object>> jobProps = selectBuilder.get();
		if(jobProps != null && !jobProps.isEmpty()) {
			PMJobsContext pmJob = FieldUtil.getAsBeanFromMap(jobProps.get(0), PMJobsContext.class);
			return pmJob;
		}
		return null;
	}
	
	public static List<PMJobsContext> getNextPMJobs(PMTriggerContext pmTrigger, long startTime, long endTime) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		FacilioField pmTriggerField = fieldsMap.get("pmTriggerId");
		FacilioField nextExecutionField = fieldsMap.get("nextExecutionTime");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getCondition(pmTriggerField, String.valueOf(pmTrigger.getId()), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(nextExecutionField, String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL))
														.andCondition(CriteriaAPI.getCondition(nextExecutionField, String.valueOf(endTime), NumberOperators.LESS_THAN))
														.orderBy("nextExecutionTime")
														;
		
		List<Map<String, Object>> jobProps = selectBuilder.get();
		if(jobProps != null && !jobProps.isEmpty()) {
			List<PMJobsContext> pmJobs = new ArrayList<>();
			for(Map<String, Object> jobProp : jobProps) {
				pmJobs.add(FieldUtil.getAsBeanFromMap(jobProp, PMJobsContext.class));
			}
			return pmJobs;
		}
		return null;
	}

	public static Map<Long, List<Map<String, Object>>> getPMScheduledWOsFromPMIds(long startTimeInSeconds, long endTimeInSeconds, Criteria filterCriteria) throws Exception {
		FacilioStatus facilioStatus = TicketAPI.getStatus("preopen");
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
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("scheduledStart"), String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("scheduledStart"), String.valueOf(endTime), NumberOperators.LESS_THAN))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(facilioStatus.getId()), NumberOperators.EQUALS))
				.andCustomWhere("WorkOrders.PM_ID IS NOT NULL")
				.orderBy("scheduledStart");
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

	public static Long getNextExecutionTime(long pmId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("workorder");
		FacilioStatus preopen = TicketAPI.getStatus("preopen");
		List<FacilioField> fields = modBean.getAllFields("workorder");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField minCreatedTime = FieldFactory.getField("minCreatedTime", "MIN(WorkOrders.CREATED_TIME)", FieldType.NUMBER);
		GenericSelectRecordBuilder selectRecordsBuilder = new GenericSelectRecordBuilder();
		selectRecordsBuilder.table(module.getTableName())
				.select(Arrays.asList(minCreatedTime))
				.innerJoin("Tickets")
				.on("Tickets.ID=WorkOrders.ID")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(preopen.getId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), String.valueOf(pmId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		List<Map<String, Object>> props = selectRecordsBuilder.get();
		if (props == null || props.isEmpty()) {
			return null;
		}

		if (props.get(0).get("minCreatedTime") == null) {
			return null;
		}

		return (Long) props.get(0).get("minCreatedTime");
	}

	public static void initScheduledWO(List<Long> orgs) throws Exception {
		for (long i : orgs) {
			try {
				AccountUtil.setCurrentAccount(i);
				if (AccountUtil.getCurrentOrg() == null || AccountUtil.getCurrentOrg().getOrgId() <= 0) {
					LOGGER.log(Level.SEVERE, "Org is missing");
					continue;
				}

				List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

				List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getAllActivePMs(null, Arrays.asList(fieldMap.get("id")));
				if (pms == null || pms.isEmpty()) {
					LOGGER.log(Level.SEVERE, "There are no PMS in the org " + AccountUtil.getCurrentOrg().getOrgId());
					continue;
				}

				LOGGER.log(Level.SEVERE, "Number of PMS to be deactivated: " + pms.size());

				List<Long> skipped = deactivateActivateAllPms(pms);

				LOGGER.log(Level.SEVERE, "skipped pms: " + StringUtils.join(skipped.toArray(), ", "));
			} finally {
				AccountUtil.cleanCurrentAccount();
			}
		}
	}

	private static List<Long> deactivateActivateAllPms(List<PreventiveMaintenance> pms) throws Exception {
		List<Long> skippedPms = new ArrayList<>();
		for (PreventiveMaintenance activePm: pms) {
			try {
				PreventiveMaintenance pm = new PreventiveMaintenance();
				pm.setStatus(false);

				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(activePm.getId()));
				context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);

				Chain migrationChain = TransactionChainFactory.getPMMigration(activePm.getId());
				migrationChain.execute(context);

				LOGGER.log(Level.SEVERE, "Migrated " + activePm.getId());
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Failed to migrate PM: " + activePm.getId(), e);
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
					LOGGER.log(Level.SEVERE, "Skipping activation: " + activePm.getId());
					continue;
				}
				PreventiveMaintenance pm = new PreventiveMaintenance();
				pm.setStatus(true);

				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(activePm.getId()));
				context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);

				Chain addTemplate = TransactionChainFactory.getChangeNewPreventiveMaintenanceStatusChainForMig();
				addTemplate.execute(context);

				LOGGER.log(Level.SEVERE, "Activated: " + activePm.getId());
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Failed to activate PM: " + activePm.getId(), e);
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
				pm.setStatus(false);
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Arrays.asList(activePm.getId()));
				context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);

				Chain addTemplate = TransactionChainFactory.getChangeNewPreventiveMaintenanceStatusChainForMig();
				addTemplate.execute(context);

				LOGGER.log(Level.SEVERE, "Deactivated: " + activePm.getId());
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Failed to deactivate PM: " + activePm.getId(), e);
				skipList.add(activePm.getId());
			}
		}
		return skipList;
	}


	public static Map<Long, List<Map<String, Object>>> getPMJobs(long startTime, long endTime, Criteria filterCriteria) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		FacilioField nextExecutionField = fieldsMap.get("nextExecutionTime");

		FacilioModule pmRPModule = ModuleFactory.getPMResourcePlannerModule();
		List<FacilioField> rpFields = FieldFactory.getPMResourcePlannerFields();
		Map<String, FacilioField> rpFieldsMap = FieldFactory.getAsMap(rpFields);
		FacilioField assignedToField = rpFieldsMap.get("assignedToId");
		fields.add(assignedToField);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.innerJoin("Preventive_Maintenance")
														.on("Preventive_Maintenance.ID=PM_Jobs.PM_ID")
														.innerJoin("Workorder_Template")
														.on("Workorder_Template.ID=Preventive_Maintenance.TEMPLATE_ID")
														.leftJoin(pmRPModule.getTableName())
														.on("PM_Resource_Planner.PM_ID=PM_Jobs.PM_ID AND PM_Resource_Planner.RESOURCE_ID=PM_Jobs.RESOURCE_ID")
														.andCondition(CriteriaAPI.getCondition(nextExecutionField, String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL))
														.andCondition(CriteriaAPI.getCondition(nextExecutionField, String.valueOf(endTime), NumberOperators.LESS_THAN))
                										.andCondition(CriteriaAPI.getCurrentOrgIdCondition(pmJobsModule))
														.orderBy("nextExecutionTime")
														;

		if (filterCriteria != null) {
			selectBuilder.andCriteria(filterCriteria);
		}

		List<Map<String, Object>> jobProps = selectBuilder.get();
		Map<Long, List<Map<String, Object>>> pmJobs = new HashMap<>();
		if(jobProps != null && !jobProps.isEmpty()) {
			for (Map<String, Object> prop : jobProps) {
				// List<PMJobsContext> pmJobList = new ArrayList<>();
				// PMJobsContext pmJob = FieldUtil.getAsBeanFromMap(prop, PMJobsContext.class);
				
				List<Map<String, Object>> pmJobList = pmJobs.get(prop.get("pmTriggerId"));
				if (pmJobList == null) {
					pmJobList = new ArrayList<>();
					pmJobs.put((Long) prop.get("pmTriggerId"), pmJobList);
				}
				pmJobList.add(prop);
			}
		}
		return pmJobs;
	}
	
	public static List<PMJobsContext> getPMJobs(List<Long> triggerIds, boolean hasOnlyNonEmptyTemlplates) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		FacilioField pmTriggerField = fieldsMap.get("pmTriggerId");
		FacilioField templateField = fieldsMap.get("templateId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getCondition(pmTriggerField,triggerIds, NumberOperators.EQUALS))
														;
		
		if (hasOnlyNonEmptyTemlplates) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(templateField,(String)null,CommonOperators.IS_NOT_EMPTY));
		}
		
		List<Map<String, Object>> jobProps = selectBuilder.get();
		List<PMJobsContext> pmJobs = new ArrayList<>();
		if(jobProps != null && !jobProps.isEmpty()) {
			for (Map<String, Object> prop : jobProps) {
				PMJobsContext pmJob = FieldUtil.getAsBeanFromMap(prop, PMJobsContext.class);
				pmJobs.add(pmJob);
			}
		}
		return pmJobs;
	}
	
	public static List<PMJobsContext> getPMJobs(List<Long> ids) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(ids, pmJobsModule))
														;
		List<Map<String, Object>> jobProps = selectBuilder.get();
		if(jobProps != null && !jobProps.isEmpty()) {
			return FieldUtil.getAsBeanListFromMapList(jobProps, PMJobsContext.class);
		}
		return null;
	}
	
	private static void addPMJob(PMJobsContext pmJob) throws SQLException, RuntimeException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		FacilioModule module = ModuleFactory.getPMJobsModule();
		Map<String, Object> props = FieldUtil.getAsProperties(pmJob);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.fields(FieldFactory.getPMJobFields())
														.table(module.getTableName())
														.addRecord(props);
		
		insertBuilder.save();
		pmJob.setId((long) props.get("id"));
	}
	
	public static void addPMJobs(List<PMJobsContext> pmJobs) throws SQLException, RuntimeException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		FacilioModule module = ModuleFactory.getPMJobsModule();
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(module.getTableName())
														.fields(FieldFactory.getPMJobFields())
														;
		List<Map<String, Object>> pmJobProps = new ArrayList<>();
		for (PMJobsContext pmJob : pmJobs) {
			pmJobProps.add(FieldUtil.getAsProperties(pmJob));
		}
		insertBuilder.addRecords(pmJobProps);
		insertBuilder.save();
		
		for(int itr = 0; itr < pmJobProps.size(); itr++) {
			Map<String, Object> pmJobProp = pmJobProps.get(itr);
			pmJobs.get(itr).setId((long) pmJobProp.get("id"));
		}
	}
	
	public static void schedulePMJob(PMJobsContext pmJob) throws Exception {
		FacilioTimer.scheduleOneTimeJob(pmJob.getId(), "PreventiveMaintenance", pmJob.getNextExecutionTime(), "priority");
		updatePMJobStatus(pmJob.getId(), PMJobsStatus.SCHEDULED);
	}
	
	public static void schedulePMJob(List<PMJobsContext> pmJobs) throws Exception {
		for(PMJobsContext pmJob :pmJobs) {
			FacilioTimer.scheduleOneTimeJob(pmJob.getId(), "PreventiveMaintenance", pmJob.getNextExecutionTime(), "priority");
			updatePMJobStatus(pmJob.getId(), PMJobsStatus.SCHEDULED);
		}
	}
	
	public static void reSchedulePMJob(PMJobsContext pmJob) throws Exception {
		FacilioTimer.deleteJob(pmJob.getId(), "PreventiveMaintenance");
		FacilioTimer.scheduleOneTimeJob(pmJob.getId(), "PreventiveMaintenance", pmJob.getNextExecutionTime(), "priority");
		updatePMJobStatus(pmJob.getId(), PMJobsStatus.SCHEDULED);
	}
	
	private static PMJobsContext deletePMJob(long pmTriggerId) throws Exception {
		FacilioModule pmJobsModule = ModuleFactory.getPMJobsModule();
		List<FacilioField> fields = FieldFactory.getPMJobFields();
		FacilioField pmTriggerField = FieldFactory.getAsMap(fields).get("pmTriggerId");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getCondition(pmTriggerField, String.valueOf(pmTriggerId), NumberOperators.EQUALS))
														;
		
		PMJobsContext pmJob = FieldUtil.getAsBeanFromMap(selectBuilder.get().get(0), PMJobsContext.class);
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(pmJobsModule.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(pmJob.getId(), pmJobsModule));
		deleteBuilder.delete();
		return pmJob;
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
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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

	public static List<PreventiveMaintenance> getAllPMs(Long orgid,boolean onlyActive) throws Exception {
		
		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														;
		if (onlyActive) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		List<Long> res = null;
		if(props != null && !props.isEmpty()) {
			
			res = new ArrayList<>();
			for(Map<String, Object> prop :props) {
				
				Long id = (Long) prop.get("id");
				res.add(id);
			}
			
			List<PreventiveMaintenance> pms = getPMs(res,null,null,null, null,true);
			
			return pms;
		}
		return null;
	}


	public static List<PreventiveMaintenance> getPMs(Collection<Long> ids, Criteria criteria, String searchQuery, JSONObject pagination, List<FacilioField> fields, Boolean...fetchDependencies) throws Exception {
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
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.orderBy("Preventive_Maintenance.CREATION_TIME DESC")
														;
		
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			
			int offset = ((page-1) * perPage);
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
			selectBuilder.andCriteria(criteria);
		}
		if (searchQuery!= null) {
			 selectBuilder.andCondition(CriteriaAPI.getCondition(pmSubjectField, searchQuery, StringOperators.CONTAINS));
		}
		
		boolean fetchDependency = false;
		boolean setTriggers = false;
		if (fetchDependencies != null && fetchDependencies.length > 0) {
			setTriggers = fetchDependencies[0];
			fetchDependency = fetchDependencies.length > 1 && fetchDependencies[1];
		}
		
		if (!fetchDependency) {
			fields.addAll(FieldFactory.getWorkOrderTemplateFields());
			FacilioModule woTemplateModule = ModuleFactory.getWorkOrderTemplateModule();
			selectBuilder.innerJoin(woTemplateModule.getTableName()).on(module.getTableName()+".TEMPLATE_ID = "+woTemplateModule.getTableName()+".ID");
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
				pm.setPmIncludeExcludeResourceContexts(TemplateAPI.getPMIncludeExcludeList(pm.getId(), null, null));
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
			for(PreventiveMaintenance pm : pms) {
				if (pm.getWoTemplate().getResourceIdVal() != -1) {
					pm.getWoTemplate().setResource(resourceMap.get(pm.getWoTemplate().getResourceIdVal()));
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
			for (Map<String, Object> triggerProp: props) {
				pmTriggerContexts.add(FieldUtil.getAsBeanFromMap(triggerProp, PMTriggerContext.class));
			}
		}
		return pmTriggerContexts;
	}
	
	public static void setPMInActive(long pmId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		PreventiveMaintenance updatePm = new PreventiveMaintenance();
		updatePm.setStatus(false);
		
		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(FieldFactory.getPreventiveMaintenanceFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
		if (pmIds == null || pmIds.isEmpty()) {
			return Collections.emptyMap();
		}
		FacilioModule module = ModuleFactory.getPMResourcePlannerModule();
		FacilioField pmIdField = FieldFactory.getField("pmId", "PM_ID", module, FieldType.LOOKUP);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getPMResourcePlannerFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(pmIdField, pmIds, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		Map<Long, List<PMResourcePlannerContext>> result = new HashMap<>();
		Map<Long, PMResourcePlannerContext> resourcePlannerContextMap = new HashMap<>();

		List<PMReminder> pmReminders = PreventiveMaintenanceAPI.getPMReminders(pmIds);

		Map<Long, PMReminder> reminderMap = new HashMap<>();

		if (pmReminders != null && !pmReminders.isEmpty()) {
			for (PMReminder rem: pmReminders) {
				reminderMap.put(rem.getId(), rem);
			}
		}

		List<Long> resourcePlannerIds = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop: props) {
				PMResourcePlannerContext pmResourcePlannerContext = FieldUtil.getAsBeanFromMap(prop, PMResourcePlannerContext.class);
				if(pmResourcePlannerContext.getResourceId() != null && pmResourcePlannerContext.getResourceId() > 0) {
					pmResourcePlannerContext.setResource(ResourceAPI.getResource(pmResourcePlannerContext.getResourceId()));
				}
				List<PMResourcePlannerReminderContext> resourcePlannerReminderContexts = PreventiveMaintenanceAPI.getPmResourcePlannerReminderContext(pmResourcePlannerContext.getId());
				if (resourcePlannerReminderContexts != null) {
					for (int i = 0; i < resourcePlannerReminderContexts.size(); i++) {
						PMReminder remContext = reminderMap.get(resourcePlannerReminderContexts.get(i).getReminderId());
						resourcePlannerReminderContexts.get(i).setReminderName(remContext.getName());
					}
					pmResourcePlannerContext.setPmResourcePlannerReminderContexts(resourcePlannerReminderContexts);
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

		if (!resourcePlannerIds.isEmpty()) {
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
	
	
	public static Map<Long,PMResourcePlannerContext> getPMResourcesPlanner(Long pmId) throws Exception {
		Map<Long, List<PMResourcePlannerContext>> pmMap = getPMResourcesPlanners(Arrays.asList(pmId));
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
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(pmIdField, pmIds, NumberOperators.EQUALS));
		
		return selectBuilder.get();
	}
	
	public static void schedulePrePMReminder(PMReminder reminder, long nextExecutionTime, long triggerId,long resourceId) throws Exception {
		if(reminder.getTypeEnum() == ReminderType.BEFORE_EXECUTION && nextExecutionTime != -1 && triggerId != -1) {
			long id = deleteOrAddPreviousBeforeRemindersRel(reminder.getId(), triggerId,resourceId);
			FacilioTimer.scheduleOneTimeJob(id, "PrePMReminder", nextExecutionTime-reminder.getDuration(), "facilio");
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
			FacilioTimer.scheduleOneTimeJob(addPMReminderToWORel(reminder.getId(), woId), "PostPMReminder", remindTime, "facilio");
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
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
							.andCondition(CriteriaAPI.getCurrentOrgIdCondition(reminderModule))
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
				List<Long> resourceIds = getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),baseSpaceId,pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts());
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
								triggerContexts = new ArrayList<>();
								triggerContexts.add(PreventiveMaintenanceAPI.getDefaultTrigger(pm.getTriggers()));
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
		WorkflowEventContext eventContext = new WorkflowEventContext();
		eventContext.setActivityType(EventType.SCHEDULED);
		eventContext.setModuleName("workorder");
		rule.setEvent(eventContext);
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

    public static void updateWorkOrderCreationStatus(List<Long> ids, boolean status) throws Exception {
		if (ids == null || ids.isEmpty()) {
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
                .andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("id"), ids, NumberOperators.EQUALS));
        updateRecordBuilder.update(props);
    }

	public static WorkOrderContext createWOContextsFromPMOnce(Context context, PreventiveMaintenance pm, PMTriggerContext trigger,  WorkorderTemplate woTemplate,  long startTime) throws Exception{
		long nextExecutionTime = trigger.getSchedule().nextExecutionTime(startTime);
		int currentCount = pm.getCurrentExecutionCount();
		long currentTime = System.currentTimeMillis() / 1000;
		while (nextExecutionTime < currentTime) {
			nextExecutionTime = trigger.getSchedule().nextExecutionTime(nextExecutionTime);
		}
		if((pm.getMaxCount() == -1 || currentCount < pm.getMaxCount()) && (pm.getEndTime() == -1 || nextExecutionTime <= pm.getEndTime())) {
			return createWoContextFromPM(context, pm, trigger, woTemplate, nextExecutionTime);
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
}
