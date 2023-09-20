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
        ServicePMTriggerContext trigger = (ServicePMTriggerContext) context.get(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER);
        Long currentTime = System.currentTimeMillis();
        Long startTime = trigger.getStartTime()!=null && trigger.getStartTime() > 0 ? trigger.getStartTime() > currentTime ? trigger.getStartTime() - 300 : currentTime : currentTime;
        Long endTime = calculateEndTime(trigger, startTime);
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
    private long calculateEndTime(ServicePMTriggerContext trigger, Long cutOffTime) throws Exception {
        // planEndtime is the time till which the trigger will be executed
        // trigger end time is the time till which the trigger was configured

        // case 1: planEndtime and triggerEndtime are not configured -> we compute using triggertype
        // case 2: planEndtime is configured but triggerEndtime is not configured -> we choose planEndtime
        // case 3: trigger endTime is configured but not planEndtime -> we chose trigger end time
        // case 4: both are configured - > we chose earliest of the two

        ScheduleInfo scheduleInfo = trigger.getScheduleInfo();
        Long endTime = trigger.getEndTime()!=null ? trigger.getEndTime() : computeEndTimeUsingTriggerType(scheduleInfo, cutOffTime);
        return endTime;
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
