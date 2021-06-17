package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.PreventiveMaintenance.TriggerType;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.TaskTemplate;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class FailedPMNewScheduler {

	private static final Logger LOGGER = LogManager.getLogger(FailedPMNewScheduler.class.getName());

	public static void execute(JobContext jc, long pmId, long statTime, long endTime, boolean doMigration) throws Exception {
		// TODO Auto-generated method stub
		try {
			FacilioModule pmTriggerModule = ModuleFactory.getPMTriggersModule();
			FacilioModule pmModule = ModuleFactory.getPreventiveMaintenanceModule();
			List<FacilioField> fields = FieldFactory.getPMTriggerFields();
			List<FacilioField> pmFields = FieldFactory.getPreventiveMaintenanceFields();
			Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(pmFields);
			fields.addAll(pmFields);
			
			GenericSelectRecordBuilder pmTriggerBuilder = new GenericSelectRecordBuilder()
																.select(fields)
																.table(pmTriggerModule.getTableName())
																.innerJoin(pmModule.getTableName())
																.on(pmTriggerModule.getTableName()+".PM_ID = "+pmModule.getTableName()+".ID")
//																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(pmTriggerModule))
																.andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("triggerType"), String.valueOf(TriggerType.ONLY_SCHEDULE_TRIGGER.getVal()), NumberOperators.EQUALS))
																.andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS))
																.andCondition(CriteriaAPI.getIdCondition(pmId, pmModule))
																;
			
			List<Map<String, Object>> triggerProps = pmTriggerBuilder.get();
			Map<Long,PMTriggerContext> triggerMap = new HashMap<>();
			
			if(triggerProps != null && !triggerProps.isEmpty()) {
				List<PMTriggerContext> triggers = new ArrayList<>();
				Map<Long, PreventiveMaintenance> pms = new HashMap<>();
				StringBuilder ids = new StringBuilder();
				triggerProps.forEach(triggerProp -> {
					PMTriggerContext trigger = FieldUtil.getAsBeanFromMap(triggerProp, PMTriggerContext.class);
					triggers.add(trigger);
					triggerMap.put(trigger.getId(), trigger);
					ids.append(", ")
						.append(trigger.getId());
					
					if(pms.get(trigger.getPmId()) == null) {
						PreventiveMaintenance pm = FieldUtil.getAsBeanFromMap(triggerProp, PreventiveMaintenance.class);
						pm.setId(trigger.getPmId());
						pms.put(pm.getId(), pm);
					}
				});
				
				List<PreventiveMaintenance> pmList = groupPmAndTriggers(pms,triggers);
				List<BulkWorkOrderContext> bulkWorkOrderContexts = new ArrayList<>();
				if (pmList != null && !pmList.isEmpty()) {
					PreventiveMaintenanceAPI.logIf(92L,"No of pms " + pmList.size());
				}

				for(PreventiveMaintenance pm : pmList) {
					try{
						// PreventiveMaintenanceAPI.logIf(92L,"pm: " + i + " Executing pm: "  + pm.getId());
						List<BulkWorkOrderContext> bulkWo = createPMJobs(pm, triggerMap, statTime, endTime);
						if (!bulkWo.isEmpty()) {
							bulkWorkOrderContexts.addAll(bulkWo);
						}
					} catch (Exception e) {
						LOGGER.error("Exception occurred in PM Scheduler Job ID - "+jc.getJobId(), e);
						CommonCommandUtil.emailException("PMScheduler", "Exception occurred in generating Schedule - orgId: "+jc.getJobId() + " pmId " + pm.getId(), e);
					}
				}

				if (doMigration) {
					if (!bulkWorkOrderContexts.isEmpty()) {
						FacilioContext context = new FacilioContext();
						BulkWorkOrderContext bulkWorkOrderContext = new BulkWorkOrderContext(bulkWorkOrderContexts);
						PreventiveMaintenanceAPI.logIf(92L,"No  of work orders to save " + bulkWorkOrderContext.getWorkOrderContexts().size());
						context.put(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT, bulkWorkOrderContext);
						context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);

						FacilioChain addWOChain = TransactionChainFactory.getTempAddPreOpenedWorkOrderChain();
						addWOChain.execute(context);
					}
				}
			}
		} catch (Exception e) {
			FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
			LOGGER.error("Exception occurred in PM Scheduler Job ID - "+jc.getJobId(), e);
			CommonCommandUtil.emailException("PMScheduler", "Exception occurred in PM Scheduler Job - "+jc.getJobId(), e);
		}
	}
		
	private static List<BulkWorkOrderContext> createPMJobs(PreventiveMaintenance pm, Map<Long,PMTriggerContext> triggerMap, long start, long end) throws Exception {
	    List<BulkWorkOrderContext> bulkWorkOrderContexts = new ArrayList<>();
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RESOURCE_MAP, new HashMap<Long, ResourceContext>());
		context.put(FacilioConstants.ContextNames.STATUS_MAP, new HashMap<FacilioStatus.StatusType, FacilioStatus>());
		if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTIPLE) {
			long templateId = pm.getTemplateId();
			WorkorderTemplate workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
			if (workorderTemplate != null) {
				Long baseSpaceId = pm.getBaseSpaceId();
				if (baseSpaceId == null || baseSpaceId < 0) {
					baseSpaceId = pm.getSiteId();
				}
				//TODO
				List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),baseSpaceId,pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts(), false);
				//TODO
				Map<Long, PMResourcePlannerContext> pmResourcePlanner = PreventiveMaintenanceAPI.getPMResourcesPlanner(pm.getId());
				//TODO
				List<ResourceContext> resourceObjs = ResourceAPI.getResources(resourceIds, false); // ?

				Map<Long, ResourceContext> resourceMap = new HashMap<>();
				if(resourceObjs != null && !resourceObjs.isEmpty()) {
					for (ResourceContext resource : resourceObjs) {
						resourceMap.put(resource.getId(), resource);
					}
				}

				for(Long resourceId :resourceIds) {
					List<PMTriggerContext> triggers = getResourceTriggers(triggerMap, workorderTemplate, pmResourcePlanner, resourceId);
					if(triggers == null) {
						triggers = PreventiveMaintenanceAPI.getDefaultTrigger(pm.getDefaultAllTriggers() != null && pm.getDefaultAllTriggers(), pm.getTriggers());
					}

					if (resourceMap.get(resourceId) != null) {
						workorderTemplate.setResourceId(resourceId);
						workorderTemplate.setResource(resourceMap.get(resourceId));
					} else {
						LOGGER.error("work order not generated PMID: " + pm.getId() + "ResourceId: " + resourceId);
						CommonCommandUtil.emailAlert("work order not generated", "PMID: " + pm.getId() + "ResourceId: " + resourceId);
					}

					bulkWorkOrderContexts.addAll(generateBulkWoContext(pm, context, workorderTemplate, triggers, start, end));
				}
			}
		}
		else {
			long templateId = pm.getTemplateId();
			WorkorderTemplate workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
			bulkWorkOrderContexts.addAll(generateBulkWoContext(pm, context, workorderTemplate, pm.getTriggers(), start, end));
		}

		return bulkWorkOrderContexts;
	}

	private static List<PMTriggerContext> getResourceTriggers(Map<Long, PMTriggerContext> triggerMap, WorkorderTemplate workorderTemplate, Map<Long, PMResourcePlannerContext> pmResourcePlanner, Long resourceId) {
		if(pmResourcePlanner.get(resourceId) == null) {
			return null;
		}
		PMResourcePlannerContext currentResourcePlanner = pmResourcePlanner.get(resourceId);
		List<PMTriggerContext> trigs = new ArrayList<>();
		if (currentResourcePlanner.getTriggerContexts() != null) {
			for (PMTriggerContext t: currentResourcePlanner.getTriggerContexts()) {
				if (triggerMap.get(t.getId()) != null) {
					trigs.add(triggerMap.get(t.getId()));
				}
			}
		}
		if (currentResourcePlanner.getAssignedToId() != null && currentResourcePlanner.getAssignedToId() > 0 ) {
			workorderTemplate.setAssignedToId(currentResourcePlanner.getAssignedToId());
		}
		currentResourcePlanner.setTriggerContexts(trigs);
		return trigs;
	}

	private static List<BulkWorkOrderContext> generateBulkWoContext(PreventiveMaintenance pm, FacilioContext context, WorkorderTemplate workorderTemplate, List<PMTriggerContext> triggers, long start, long end) throws Exception {
		List<BulkWorkOrderContext> bulkWorkOrderContexts = new ArrayList<>();
		for (PMTriggerContext trigger: triggers) {
			if (trigger.getSchedule() != null) {
				BulkWorkOrderContext bulkWoContextsFromTrigger = createBulkWoContextsFromPM(context, pm, trigger, start, end, workorderTemplate);
				bulkWorkOrderContexts.add(bulkWoContextsFromTrigger);
			}
		}
		return bulkWorkOrderContexts;
	}

	public static BulkWorkOrderContext createBulkWoContextsFromPM(Context context, PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime, long endTime, WorkorderTemplate woTemplate) throws Exception {
		Pair<Long, Integer> nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(Pair.of(startTime, 0));
		int currentCount = pm.getCurrentExecutionCount();
		long currentTime = System.currentTimeMillis();
		boolean isScheduled = false;
		List<Long> nextExecutionTimes = new ArrayList<>();
		LOGGER.log(Level.ERROR, "PM "+ pm.getId() + " PM Trigger ID: "+pmTrigger.getId() + " next exec time " + nextExecutionTime.getLeft() + " end time " + endTime);
		while (nextExecutionTime.getLeft() <= endTime && (pm.getMaxCount() == -1 || currentCount < pm.getMaxCount()) && (pm.getEndTime() == -1 || nextExecutionTime.getLeft() <= pm.getEndTime())) {
			nextExecutionTimes.add(nextExecutionTime.getLeft());

			nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(nextExecutionTime);
			currentCount++;
			if (pmTrigger.getSchedule().getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.DO_NOT_REPEAT) {
				break;
			}
			isScheduled = true;
		}

		LOGGER.log(Level.ERROR, "PM "+ pm.getId() + " PM Trigger ID: "+pmTrigger.getId() + " next exec times " + Arrays.toString(nextExecutionTimes.toArray()));
		
		return createBulkContextFromPM(context, pm, pmTrigger, woTemplate, nextExecutionTimes);
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

	public static BulkWorkOrderContext createBulkContextFromPM(Context context, PreventiveMaintenance pm, PMTriggerContext pmTrigger, WorkorderTemplate workorderTemplate, List<Long> nextExecutionTimes) throws Exception {
		FacilioStatus status = getStatus(context, FacilioStatus.StatusType.PRE_OPEN);
		BulkWorkOrderContext bulkWorkOrderContext = new BulkWorkOrderContext();
		for (long nextExecutionTime: nextExecutionTimes) {
			WorkorderTemplate clonedWoTemplate = FieldUtil.cloneBean(workorderTemplate, WorkorderTemplate.class);

			if (workorderTemplate.getSectionTemplates() != null) {
				List<TaskSectionTemplate> sectionTemplates = new ArrayList<>();
				for (TaskSectionTemplate sectionTemplate: workorderTemplate.getSectionTemplates()) {
					TaskSectionTemplate template = FieldUtil.cloneBean(sectionTemplate, TaskSectionTemplate.class);
					sectionTemplates.add(template);
				}
				clonedWoTemplate.setSectionTemplates(sectionTemplates);
			}

			for (TaskTemplate taskTemplate: workorderTemplate.getTaskTemplates()) {
				List<TaskTemplate> taskTemplates = new ArrayList<>();
				TaskTemplate template = FieldUtil.cloneBean(taskTemplate, TaskTemplate.class);
				taskTemplates.add(template);
				clonedWoTemplate.setTaskTemplates(taskTemplates);
			}

			if (workorderTemplate.getPreRequestSectionTemplates() != null) {
				List<TaskSectionTemplate> sectionTemplates = new ArrayList<>();
				for (TaskSectionTemplate sectionTemplate: workorderTemplate.getPreRequestSectionTemplates()) {
					TaskSectionTemplate template = FieldUtil.cloneBean(sectionTemplate, TaskSectionTemplate.class);
					sectionTemplates.add(template);
				}
				clonedWoTemplate.setPreRequestSectionTemplates(sectionTemplates);
			}

			for (TaskTemplate taskTemplate: workorderTemplate.getPreRequestTemplates()) {
				List<TaskTemplate> taskTemplates = new ArrayList<>();
				TaskTemplate template = FieldUtil.cloneBean(taskTemplate, TaskTemplate.class);
				taskTemplates.add(template);
				clonedWoTemplate.setPreRequestTemplates(taskTemplates);
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
			}
			int preRequisiteCount= clonedWoTemplate.getPreRequestSectionTemplates() != null ? clonedWoTemplate.getPreRequestSectionTemplates().size() : 0;
			wo.setPrerequisiteEnabled(preRequisiteCount != 0);
			if (wo.getPrerequisiteEnabled()) {
				wo.setPreRequestStatus(WorkOrderContext.PreRequisiteStatus.NOT_STARTED.getValue());
			} else {
				wo.setPreRequestStatus(WorkOrderContext.PreRequisiteStatus.COMPLETED.getValue());
			}
			wo.setPm(pm);
			wo.setStatus(status);
			wo.setTrigger(pmTrigger);
			wo.setJobStatus(WorkOrderContext.JobsStatus.ACTIVE);
			wo.setSourceType(TicketContext.SourceType.PREVENTIVE_MAINTENANCE);
			wo.setPm(pm);
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
					taskMapForNewPmExecution = PreventiveMaintenanceAPI.getTaskMapForNewPMExecution(context, clonedWoTemplate.getSectionTemplates(), woTemplateResourceId, currentTriggerId, false);
				}
			} else {
				taskMapForNewPmExecution = clonedWoTemplate.getTasks();
			}

			if(taskMapForNewPmExecution != null) {
				taskMap = taskMapForNewPmExecution;
			}

			Map<String, List<TaskContext>> preRequestMap = null;
			Map<String, List<TaskContext>> preRequestMapForNewPmExecution = null;
			isNewPmType = false;

			if (clonedWoTemplate.getPreRequestSectionTemplates() != null) {
				for (TaskSectionTemplate sectiontemplate : clonedWoTemplate.getPreRequestSectionTemplates()) {
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
					preRequestMapForNewPmExecution = PreventiveMaintenanceAPI.getTaskMapForNewPMExecution(context, clonedWoTemplate.getPreRequestSectionTemplates(), woTemplateResourceId, currentTriggerId, false);
				}
			} else {
				preRequestMapForNewPmExecution = clonedWoTemplate.getPreRequests();
			}

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

	private static long getEndTime(PreventiveMaintenance pm) {
		return pm.getWoGeneratedUpto() + (24 * 60 * 60);
	}

	private static List<PreventiveMaintenance> groupPmAndTriggers(Map<Long, PreventiveMaintenance> pms, List<PMTriggerContext> triggers) throws Exception {
		List<PreventiveMaintenance> pmList = new ArrayList<>();
		if(pms != null) {
			for( Map.Entry<Long, PreventiveMaintenance> map : pms.entrySet()) {
				
				PreventiveMaintenance pm = map.getValue();
				pm.setPmIncludeExcludeResourceContexts(TemplateAPI.getPMIncludeExcludeList(pm.getId(), null, null));
				
				for(PMTriggerContext trigger :triggers) {
					if(trigger.getPmId() == pm.getId()) {
						pm.addTriggers(trigger); 
					} 
				}
				
				pmList.add(pm);
			}
		}
		return pmList;
	}

	private Map<Long, Long> getMaxExecutionTimes(String triggerIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> woFields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
		Map<String, FacilioField> woFieldMap = FieldFactory.getAsMap(woFields);
		FacilioField maxField = FieldFactory.getField("maxScheduledStart", "MAX(SCHEDULED_START)", FieldType.NUMBER);
		FacilioField triggerIdField = woFieldMap.get("trigger");
		List<FacilioField> fields = new ArrayList<>();
		fields.add(maxField);
		fields.add(triggerIdField);
		FacilioField statusField = woFieldMap.get("status");
		FacilioField jobStatusField = woFieldMap.get("jobStatus");
		FacilioStatus status = TicketAPI.getStatus("preopen");
		FacilioModule ticketModule = ModuleFactory.getTicketsModule();

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.innerJoin(ticketModule.getTableName())
														.on(ticketModule.getTableName()+ ".ID=" + module.getTableName() + ".ID")
														.andCondition(CriteriaAPI.getCondition(triggerIdField, triggerIds, NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(statusField, String.valueOf(status.getId()), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(jobStatusField, String.valueOf(PMJobsContext.PMJobsStatus.ACTIVE.getValue()), NumberOperators.EQUALS))
														.groupBy(triggerIdField.getCompleteColumnName())
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		return props.stream().collect(Collectors.toMap(prop -> (Long) prop.get(triggerIdField.getName()), prop -> (Long) prop.get(maxField.getName())));
	}
}
