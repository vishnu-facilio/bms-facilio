package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMTaskSectionTemplateTriggers;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.modules.FacilioStatus;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ScheduleWOStatusChange extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(ScheduleWOStatusChange.class.getName());
    @Override
    public void execute(JobContext jc) throws Exception {
        try {
            if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SCHEDULED_WO)) {
                return;
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            FacilioStatus status = TicketAPI.getStatus("preopen");
            long maxTime = System.currentTimeMillis()+(30*60*1000);

            SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
            selectRecordsBuilder.select(fields)
                    .module(module)
                    .beanClass(WorkOrderContext.class)
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(status.getId()), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("jobStatus"), String.valueOf(PMJobsContext.PMJobsStatus.ACTIVE.getValue()), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("scheduledStart"), String.valueOf(maxTime), NumberOperators.LESS_THAN_EQUAL))
                    .andCustomWhere("WorkOrders.PM_ID IS NOT NULL");
            List<WorkOrderContext> wos = selectRecordsBuilder.get();

            if (wos == null || wos.isEmpty()) {
                return;
            }
            
            modifyWorkflowBasedOnRule(wos);

            for (WorkOrderContext wo : wos) {
//            	TicketAPI.loadRelatedModules(wo);
//            	if(wo.getTasks() == null || wo.getTasks().isEmpty()) {
//            		continue;
//            	}
                FacilioTimer.scheduleOneTimeJob(wo.getId(), "OpenScheduledWO", wo.getScheduledStart()/1000, "priority");
            }

            updateJobStatus(wos);
        } catch (Exception e) {
            CommonCommandUtil.emailException("ScheduleWOStatusChange", ""+jc.getJobId(), e);
            LOGGER.error("PM Execution failed: ", e);
            throw e;
        }
    }

    private void modifyWorkflowBasedOnRule(List<WorkOrderContext> wos) throws Exception {
    	
    	if(wos == null) {
    		return;
    	}
    	try {
    		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    		
        	for(WorkOrderContext wo :wos) {
        		if(wo.getTrigger() != null && wo.getPm() != null && wo.getPm().getId() > 0) {
        			long woSiteId = wo.getSiteId();
        			TicketAPI.loadRelatedModules(wo);
        			
        			List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getPMs(Collections.singletonList(wo.getPm().getId()), null, null, null, null, false);
        			
        			PreventiveMaintenance pm = pms.get(0);
        			long pmSiteId = pm.getSiteId();
        			
        			List<Map<String, Object>> props = PreventiveMaintenanceAPI.getTaskSectionTemplateTriggers(wo.getTrigger().getId());
        			
        			Map<Long, TaskSectionTemplate> sectionMap = TemplateAPI.getTaskSectionTemplatesFromWOTemplate(pm.getWoTemplate(), null);
        			
    				for (Map<String, Object> prop : props) {
    					
    					PMTaskSectionTemplateTriggers pmTaskSectionTemplateTriggers =FieldUtil.getAsBeanFromMap(prop, PMTaskSectionTemplateTriggers.class);
    					
    					if(pmTaskSectionTemplateTriggers.getExecuteIfNotInTime() <= 0) {
    						continue;
    					}
    					Long executionTimeInSec = (Long) pmTaskSectionTemplateTriggers.getExecuteIfNotInTime();
    					
    					executionTimeInSec = DateTimeUtil.getCurrenTime() - (executionTimeInSec * 1000);

    					long sectionTemplateId = pmTaskSectionTemplateTriggers.getSectionId();
    					TaskSectionTemplate section = sectionMap.get(sectionTemplateId);
    					TemplateAPI.getTasksFromSection(section);

    					FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
    					List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);

    					SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
    					
    		            FacilioStatus status = TicketAPI.getStatus("preopen");
    		            
    		            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
    					
    					selectRecordsBuilder.select(fields).module(module).beanClass(WorkOrderContext.class)
    							 .andCondition(CriteriaAPI.getCondition(fieldMap.get("status"),String.valueOf(status.getId()), NumberOperators.NOT_EQUALS))
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
    				long newSiteId = wo.getSiteId();
    	            if (newSiteId != woSiteId || newSiteId != pmSiteId) {
    	            		StringBuilder builder = new StringBuilder();
    	            		builder.append("woId: ").append(wo.getId())
    	            		.append("\nInitial SiteId: ").append(woSiteId)
    	            		.append("\nNew SiteId: ").append(newSiteId)
    	            		.append("\nPm SiteId: ").append(pmSiteId);
    	            		CommonCommandUtil.emailException("ScheduleWOStatusChange", "Workorder site different", builder.toString());
    	                LOGGER.info("Workorder site different. " + builder.toString());
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
