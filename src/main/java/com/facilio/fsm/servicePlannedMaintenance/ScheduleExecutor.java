package com.facilio.fsm.servicePlannedMaintenance;

import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.fsm.context.ServicePMTriggerContext;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

import static com.facilio.fsm.util.ServicePlannedMaintenanceAPI.*;


public class ScheduleExecutor extends ExecutorBase {
    @Override
    protected List<Long> getNextExecutionTimes(Context context) throws Exception {
        ServicePlannedMaintenanceContext plannedMaintenance = (ServicePlannedMaintenanceContext) context.get(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        ServicePMTriggerContext trigger = (ServicePMTriggerContext) context.get(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER);
        Long currentTime = System.currentTimeMillis();
        Long startTime = trigger.getStartTime()!=null && trigger.getStartTime() > 0 ? trigger.getStartTime() > currentTime ? trigger.getStartTime() - 300 : currentTime : currentTime;
        Long endTime = calculateEndTime(trigger, startTime,plannedMaintenance.getPreviewPeriod());
        List<Long> nextExecutionTimes = new ArrayList<>();

        List<DateRange> nextExecutionTimesRange = trigger.getScheduleInfo().getTimeIntervals(startTime,endTime);

        long maxNextExecutionCount = 1000;

        for(DateRange executionTime : nextExecutionTimesRange){
            long times = executionTime.getEndTime()+1;
            if (times < currentTime ) {
                continue;
            }
            nextExecutionTimes.add(times);
            if (nextExecutionTimes.size() > maxNextExecutionCount) {
                throw new IllegalArgumentException("Only 1000 executions are allowed, this is to avoid OOMs and infinite looping.");
            }
        }
        return nextExecutionTimes;
    }
    private long calculateEndTime(ServicePMTriggerContext trigger, Long cutOffTime, Integer previewPeriod) throws Exception {
//        Long endTime = trigger.getEndTime()!=null ? trigger.getEndTime() : computeEndTimeUsingTriggerType(scheduleInfo, cutOffTime);
        if(trigger.getEndTime()!=null && previewPeriod==null){
            return trigger.getEndTime();
        } else if(previewPeriod!=null && trigger.getEndTime()==null){
         Long endTimeAfterPreviewPeriod = computeEndTimeUsingPreviewPeriod(cutOffTime,previewPeriod);
         return endTimeAfterPreviewPeriod;
        } else if(previewPeriod!=null && trigger.getEndTime()!=null){
            Long endTimeAfterPreviewPeriod = computeEndTimeUsingPreviewPeriod(cutOffTime,previewPeriod);
            return endTimeAfterPreviewPeriod < trigger.getEndTime() ? endTimeAfterPreviewPeriod : trigger.getEndTime();
        }else {
            ScheduleInfo scheduleInfo = trigger.getScheduleInfo();
            return computeEndTimeUsingTriggerType(scheduleInfo, cutOffTime);
        }
    }

    @Override
    protected ServiceOrderTicketStatusContext getStatus(Context context) throws Exception {
        return getServiceOrderStatus(FacilioConstants.ServiceOrder.UPCOMING);
    }

    @Override
    protected Long getComputedNextExecutionTime(Long nextExecutionTime, ServicePlannedMaintenanceContext plannedMaintenance) {
        Long computedCreatedTime = plannedMaintenance.getLeadTime()!=null ? nextExecutionTime - (plannedMaintenance.getLeadTime() * 1000) : nextExecutionTime;
        return computedCreatedTime;
    }

    @Override
    protected Boolean canProceedWithCreatedTime(Long createdTime){
        return !(createdTime < DateTimeUtil.getCurrenTime());
    }
    @Override
    public  void deletePreOpenServiceOrders(Long servicePMId) throws Exception {
        deleteServiceOrdersInUpcomingState(servicePMId);
    }
}
