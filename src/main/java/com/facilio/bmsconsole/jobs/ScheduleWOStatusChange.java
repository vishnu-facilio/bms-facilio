package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMTaskSectionTemplateTriggers;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.time.DateTimeUtil;

// creates a job - collects WOs for next 30 mins and create a job for every WOs that has to be executed
/*
   Check in Job Table
   This is 30 mins Job
 */
public class ScheduleWOStatusChange extends FacilioJob {
	private static final Logger LOGGER = LogManager.getLogger(ScheduleWOStatusChange.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception {
		try {
			
			long maxTime = jc.getNextExecutionTime()*1000; //Using next execution time because this can be independent of the period of the job
			
			if(AccountUtil.isFeatureEnabled(FeatureLicense.PM_PLANNER)) {
				FacilioChain chain = TransactionChainFactoryV3.moveWoInQueueForPreOpenToOpenChain();
				FacilioContext context = chain.getContext();
				context.put(FacilioConstants.ContextNames.END_TIME, maxTime);
			
				chain.execute();
			}

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

			SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
			selectRecordsBuilder.select(fields)
					.module(module)
					.beanClass(WorkOrderContext.class)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), CommonOperators.IS_EMPTY))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("jobStatus"), String.valueOf(PMJobsContext.PMJobsStatus.ACTIVE.getValue()), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(maxTime), NumberOperators.LESS_THAN))
					.andCustomWhere("WorkOrders.PM_ID IS NOT NULL")
					.skipModuleCriteria();

			List<Map<String, Object>> workOrderProps = selectRecordsBuilder.getAsProps();

			List<Long> workOrderIds = workOrderProps.stream()
					.map(map -> (Long) map.get("id"))
					.collect(Collectors.toList());
			LOGGER.info("execute() -> workOrderProps size: " + workOrderIds.size() + ". WorkOrder IDs = " + workOrderIds);

			List<WorkOrderContext> workOrderContexts = new ArrayList<>();

			for (Map<String, Object> workOrderProp : workOrderProps) {
				workOrderContexts.add(FieldUtil.getAsBeanFromMap(workOrderProp, WorkOrderContext.class));
			}

			handlePMV1Scheduling(workOrderContexts);// This is PM V1 version
		} catch (Exception e) {
			CommonCommandUtil.emailException("ScheduleWOStatusChange", ""+jc.getJobId(), e);
			LOGGER.error("PM Execution failed: ", e);
		}
	}

	private void handlePMV1Scheduling(List<WorkOrderContext> workOrderContexts) throws Exception {
		LOGGER.info("handlePMV1Scheduling():");
		if (CollectionUtils.isNotEmpty(workOrderContexts)) {
			LOGGER.info("Before modifyWorkflowBasedOnRule() call.");
			modifyWorkflowBasedOnRule(workOrderContexts);
			LOGGER.info("After modifyWorkflowBasedOnRule() call.");
			for (WorkOrderContext wo : workOrderContexts) {
				try {
					FacilioTimer.scheduleOneTimeJobWithTimestampInSec(wo.getId(), "OpenScheduledWO", wo.getCreatedTime() / 1000, "priority");
				} catch (Exception e) { //Delete job entry if any and try again
					CommonCommandUtil.emailException("ScheduleWOStatusChange", "handlePMV1Scheduling() | workOrder= " + wo, e);
					LOGGER.error("PM Execution failed in handlePMV1Scheduling():", e);
					FacilioTimer.deleteJob(wo.getId(), "OpenScheduledWO");
					FacilioTimer.scheduleOneTimeJobWithTimestampInSec(wo.getId(), "OpenScheduledWO", wo.getCreatedTime() / 1000, "priority");
				}
			}
			updateJobStatus(workOrderContexts);
		}
	}

	private void modifyWorkflowBasedOnRule(List<WorkOrderContext> wos) throws Exception {
		LOGGER.info("modifyWorkflowBasedOnRule():");
		if(wos == null) {
			return;
		}
		try {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			for(WorkOrderContext wo :wos) {
				if(wo.getTrigger() != null && wo.getPm() != null && wo.getPm().getId() > 0) {
					TicketAPI.loadRelatedModules(wo);

					List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getPMs(Collections.singletonList(wo.getPm().getId()), null, null, null, null, false);

					PreventiveMaintenance pm = pms.get(0);

					List<Map<String, Object>> props = PreventiveMaintenanceAPI.getTaskSectionTemplateTriggers(wo.getTrigger().getId());

					Map<Long, TaskSectionTemplate> sectionMap = TemplateAPI.getTaskSectionTemplatesFromWOTemplate(pm.getWoTemplate(), null);

					for (Map<String, Object> prop : props) {

						PMTaskSectionTemplateTriggers pmTaskSectionTemplateTriggers =FieldUtil.getAsBeanFromMap(prop, PMTaskSectionTemplateTriggers.class);

						if(pmTaskSectionTemplateTriggers.getExecuteIfNotInTime() <= 0) {
							continue;
						}
						Long executionTimeInSec = pmTaskSectionTemplateTriggers.getExecuteIfNotInTime();

						executionTimeInSec = DateTimeUtil.getCurrenTime() - (executionTimeInSec * 1000);

						long sectionTemplateId = pmTaskSectionTemplateTriggers.getSectionId();
						TaskSectionTemplate section = sectionMap.get(sectionTemplateId);
						TemplateAPI.getTasksFromSection(section);

						FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
						List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);

						SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();

						Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

						selectRecordsBuilder.select(fields).module(module).beanClass(WorkOrderContext.class)
								.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"),String.valueOf(executionTimeInSec), NumberOperators.GREATER_THAN_EQUAL))
								.andCondition(CriteriaAPI.getCondition(fieldMap.get("pm"),String.valueOf(pm.getId()), NumberOperators.EQUALS))
								.andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"),String.valueOf(wo.getResource().getId()), NumberOperators.EQUALS))
								.andCustomWhere("WorkOrders.PM_ID IS NOT NULL");


						List<WorkOrderContext> wosList = selectRecordsBuilder.get();

						List<Integer> uniqueIds = new ArrayList<>();
						for (TaskContext task : section.getTasks()) {
							uniqueIds.add(task.getUniqueId());
						}

						Map<Integer,List<TaskContext>> uniqueIdVsParentIdMap = getUniqueMapFromWO(wo);

						for(WorkOrderContext workOrderContext :wosList) {
							TicketAPI.loadRelatedModules(workOrderContext);
							Map<Integer,List<TaskContext>> uniqueIdVsParentIdMaptemp = getUniqueMapFromWO(workOrderContext);

							compareAndRemoveTask(uniqueIdVsParentIdMap,uniqueIdVsParentIdMaptemp,uniqueIds);
						}
					}
				}
			}
		}
		catch(Exception e) {
			CommonCommandUtil.emailException("ScheduleWOStatusChange rule based edit",  e.getMessage(), e);
			LOGGER.error("PM Execution failed: ", e);
		}

	}

	private void compareAndRemoveTask(Map<Integer, List<TaskContext>> child,Map<Integer, List<TaskContext>> parent, List<Integer> uniqueIds) throws Exception {
		LOGGER.info("compareAndRemoveTask():");
		List<TaskContext> deleteTasks = new ArrayList<>();
		for(Integer uniqueId :uniqueIds) {
			List<TaskContext> childTasks = child.get(uniqueId);
			List<TaskContext> parentTasks = parent.get(uniqueId);

			for(TaskContext childTask :childTasks) {
				for(TaskContext parentTask :parentTasks) {
					if(childTask.getResource() != null && parentTask.getResource() != null && childTask.getResource().getId() == parentTask.getResource().getId()) {
						deleteTasks.add(childTask);
					}
				}
			}
			childTasks.removeAll(deleteTasks);
		}
		if(!deleteTasks.isEmpty()) {
			List<Long> taskIDs = deleteTasks.stream().map(TaskContext::getId).collect(Collectors.toList());
			TicketAPI.deleteTasks(taskIDs);
		}
	}

	public Map<Integer,List<TaskContext>> getUniqueMapFromWO(WorkOrderContext wo) {
		LOGGER.info("getUniqueMapFromWO():");
    	Map<Integer,List<TaskContext>> uniqueIdVsParentIdMap = new HashMap<>();
    	if(wo.getTasks() != null) {
    		for(List<TaskContext> tasks :wo.getTasks().values()) {
    			for(TaskContext task :tasks) {
    				if(uniqueIdVsParentIdMap.containsKey(task.getUniqueId())) {
    					uniqueIdVsParentIdMap.get(task.getUniqueId()).add(task);
    				}
    				else {
    					List<TaskContext> tasksTemp = new ArrayList<>();
    					tasksTemp.add(task);
    					uniqueIdVsParentIdMap.put(task.getUniqueId(), tasksTemp);
    				}
    			}
    		}
    	}
    	return uniqueIdVsParentIdMap;
    }

	private void updateJobStatus(List<WorkOrderContext> wos) throws Exception {
		LOGGER.info("updateJobStatus():");
        if (wos == null || wos.isEmpty()) {
            return;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<Long> woIds = wos.stream().map(WorkOrderContext::getId).collect(Collectors.toList());
        WorkOrderContext wo = new WorkOrderContext();
        wo.setJobStatus(WorkOrderContext.JobsStatus.SCHEDULED);
        UpdateRecordBuilder<WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<>();
        updateRecordBuilder.fields(Arrays.asList(fieldMap.get("jobStatus")))
                .module(module)
                .andCondition(CriteriaAPI.getIdCondition(woIds, module))
                .update(wo);
    }
}
