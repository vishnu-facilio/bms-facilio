package com.facilio.plannedmaintenance;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.V3TicketContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.util.V3Util;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class ExecutorBase implements Executor {
    @Override
    public List<V3WorkOrderContext> execute(Context context) throws Exception {
    	
    	PlannedMaintenance plannedMaintenance = (PlannedMaintenance) context.get(FacilioConstants.PM_V2.PM_V2_MODULE_NAME);
    	PMPlanner pmPlanner = (PMPlanner) context.get(FacilioConstants.PM_V2.PM_V2_PLANNER);
		
        List<Long> nextExecutionTimes = getNextExecutionTimes(context);
        FacilioStatus status = getStatus(context);
        List<V3WorkOrderContext> result = new ArrayList<>();

        PMTriggerV2 trigger = (PMTriggerV2) context.get("trigger");
        
        if(CollectionUtils.isNotEmpty(nextExecutionTimes)) {
        	
        	context.put(FacilioConstants.ContextNames.LAST_EXECUTION_TIME, nextExecutionTimes.get(nextExecutionTimes.size()-1));
        	
        	for (long nextExecutionTime: nextExecutionTimes) {	// in seconds
            	
            	Long computedCreatedTime = getComputedNextExecutionTime(nextExecutionTime, plannedMaintenance);
            	
            	if(!canProceedWithCreatedTime(computedCreatedTime)) {
            		continue;
            	}
            	
                for (PMResourcePlanner pmResourcePlanner: pmPlanner.getResourcePlanners()) {
                    //Cloning workorder
                    Map<String, Object> asProperties = FieldUtil.getAsProperties(plannedMaintenance);
                    V3WorkOrderContext workOrderCopy = FieldUtil.getAsBeanFromMap(asProperties, V3WorkOrderContext.class);

                    if (pmPlanner.getPreReqJobPlan() != null) {
                        workOrderCopy.setPrerequisiteEnabled(true);
                        workOrderCopy.setPreRequestStatus(WorkOrderContext.PreRequisiteStatus.NOT_STARTED.getValue());
                    } else {
                        workOrderCopy.setPreRequestStatus(WorkOrderContext.PreRequisiteStatus.COMPLETED.getValue());
                    }

                    ResourceContext resourceContext = new ResourceContext();
                    resourceContext.setId(pmResourcePlanner.getResource().getId());
                    resourceContext.setSiteId(pmResourcePlanner.getResource().getSiteId());
                    workOrderCopy.setScheduledStart(nextExecutionTime);
                    workOrderCopy.setResource(resourceContext);
                    workOrderCopy.setPmV2(pmResourcePlanner.getPmId());
                    workOrderCopy.setSiteId(resourceContext.getSiteId());
                    if (trigger != null) {
                        workOrderCopy.setPmTriggerV2(trigger.getId());
                    }
                    workOrderCopy.setPmPlanner(pmPlanner.getId());
                    workOrderCopy.setPmResourcePlanner(pmResourcePlanner.getId());
                    workOrderCopy.setJobPlan(pmResourcePlanner.getJobPlan());
                    workOrderCopy.setAdhocJobPlan(pmPlanner.getAdhocJobPlan());
                    workOrderCopy.setPrerequisiteJobPlan(pmPlanner.getPreReqJobPlan());
                    workOrderCopy.setCreatedTime(computedCreatedTime);
                    workOrderCopy.setModifiedTime(computedCreatedTime);

                    workOrderCopy.setJobStatus(V3WorkOrderContext.JobsStatus.ACTIVE.getValue());
                    workOrderCopy.setSourceType(V3TicketContext.SourceType.PREVENTIVE_MAINTENANCE.getIntVal());
                    workOrderCopy.setStatus(status);
                    workOrderCopy.setModuleState(null);
                    workOrderCopy.setStateFlowId(-1);

                    // Set Duration, DueDate, Estimated End
                    workOrderCopy.setDuration(plannedMaintenance.getDueDuration());
                    if(workOrderCopy.getDuration() != null) {
                        workOrderCopy.setDueDate(workOrderCopy.getScheduledStart()+(workOrderCopy.getDuration()*1000));
                    }
                    workOrderCopy.setEstimatedEnd(workOrderCopy.getDueDate());

                    workOrderCopy.setCreatedBy(AccountUtil.getCurrentUser());
                    workOrderCopy.setAssignedTo(pmResourcePlanner.getAssignedTo());

                    result.add(workOrderCopy);
                }
            }
        }

        return result;
    }
    
    protected abstract Boolean canProceedWithCreatedTime(Long createdTime) throws Exception;

    protected abstract FacilioStatus getStatus(Context context) throws Exception;
    
    protected abstract List<Long> getNextExecutionTimes(Context context) throws Exception;
    
    protected abstract Long getComputedNextExecutionTime(Long nextExecutionTime, PlannedMaintenance plannedMaintenance) throws Exception;
    
    
    public long computeEndtimeUsingTriggerType(ScheduleInfo scheduleInfo, long cutOffTime) {	// returning milliseconds
        ZonedDateTime dateTime = DateTimeUtil.getDateTime(cutOffTime, false);
        ZonedDateTime zonedEnd = dateTime.plusDays(15);
        if (scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.DAILY) {
            zonedEnd = dateTime.plusDays(15);
        } else if (scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.WEEKLY) {
            zonedEnd = dateTime.plusWeeks(15);
        } else if (scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.MONTHLY_DAY
                || scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.MONTHLY_WEEK) {
            zonedEnd = dateTime.plusMonths(15);
        } else if (scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.QUARTERLY_WEEK
                || scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.QUARTERLY_DAY) {
            zonedEnd = dateTime.plusYears(5);
        } else if (scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.HALF_YEARLY_DAY
                || scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.HALF_YEARLY_WEEK) {
            zonedEnd = dateTime.plusYears(5);
        } else if (scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.YEARLY
                || scheduleInfo.getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.YEARLY_WEEK) {
            zonedEnd = dateTime.plusYears(5);
        }
        return zonedEnd.toEpochSecond()*1000;
    }

	public void deletePreOpenworkOrder(long plannerId) throws Exception {
		
		// do nothing in general
	}
    
}
