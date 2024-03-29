package com.facilio.plannedmaintenance;

import com.facilio.bmsconsole.context.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.log4j.Priority;

import java.util.*;

/**
 * ScheduleResourcePlannerExecutor is used to schedule the resources at Resource Planner level.
 * The endTime calculation is handled in this Executor so that it doesn't affect the workorders already created
 * and workorders created for the Resources through ScheduleResourcePlannerExecutor.
 */
@Log4j
public class ScheduleResourcePlannerExecutor extends ExecutorBase {
    @Override
    protected FacilioStatus getStatus(Context context) throws Exception {
        Map<FacilioStatus.StatusType, FacilioStatus> statusMap = (Map<FacilioStatus.StatusType, FacilioStatus>) context.get(FacilioConstants.ContextNames.STATUS_MAP);
        return statusMap.get(FacilioStatus.StatusType.PRE_OPEN);
    }

    @Override
    protected List<Long> getNextExecutionTimes(Context context) throws Exception {// return list of milliseconds.
        PMTriggerV2 trigger = (PMTriggerV2) context.get("trigger");
        long currentTime = System.currentTimeMillis();
        long startTime = trigger.getStartTime() > 0 ? trigger.getStartTime() > currentTime ? trigger.getStartTime() - 300 : currentTime : currentTime;

        PMPlanner pmPlanner = (PMPlanner) context.get(FacilioConstants.PM_V2.PM_V2_PLANNER);
        long plannerGeneratedUpto = pmPlanner.getGeneratedUpto();
        long endTime = calculateEndTime(trigger, startTime, plannerGeneratedUpto);

        List<Long> nextExecutionTimes = new ArrayList<>();

        List<DateRange> nextExecutionTimesRange = trigger.getScheduleInfo().getTimeIntervals(startTime, endTime);

        long maxNextExecutionCount = 1000;

        for (DateRange executionTime : nextExecutionTimesRange) {
            long times = executionTime.getEndTime() + 1;
            if (times < currentTime) {
                continue;
            }
            nextExecutionTimes.add(times);
            if (nextExecutionTimes.size() > maxNextExecutionCount) {
                throw new IllegalArgumentException("Only 1000 executions are allowed, this is to avoid OOMs and infinite looping.");
            }
        }
        return nextExecutionTimes;
    }

    /**
     * Helper function to get the ScheduleInfo object from @param trigger
     * @param trigger
     * @return
     * @throws Exception
     */

    /**
     * Helper method to set the endTime in @param triggerV2 based on 'planEndtime' and 'triggerEndtime'.
     * <p>
     * - 'planEndtime' is the time till which the trigger will be executed.
     * - 'triggerEndtime' is the time till which the trigger was configured
     * <p>
     * - Case 1: planEndtime and triggerEndtime are not configured -> we compute using triggertype
     * - Case 2: planEndtime is configured but triggerEndtime is not configured -> we choose planEndtime
     * - Case 3: trigger endTime is configured but not planEndtime -> we chose trigger end time
     * - Case 4: both are configured - > we chose earliest of the two
     *
     * @param triggerV2
     * @param cutOffTime           - Time from which schedule generation should be calculated.
     * @param plannerGeneratedUpto
     * @throws Exception
     */
    private long calculateEndTime(PMTriggerV2 triggerV2, long cutOffTime, long plannerGeneratedUpto) throws Exception {
        // planEndtime is the time till which the trigger will be executed
        // trigger end time is the time till which the trigger was configured

        // case 1: planEndtime and triggerEndtime are not configured -> we compute using triggertype
        // case 2: planEndtime is configured but triggerEndtime is not configured -> we choose planEndtime
        // case 3: trigger endTime is configured but not planEndtime -> we chose trigger end time
        // case 4: both are configured - > we chose earliest of the two
        /*
            CASE: This Executor is written keeping the Kitopi's use case, where asset is attached to the PM's planner when
                    asset is created. This is done so that we avoid the below cases by setting endTime that is very nearer.
             * If the current-execution's last next execution time is greater than last generated time in DB and ,
                if we update with current-execution's last next execution time, other resources' workorder generation might get affected (WOs would be missed to get scheduled).
             * If last generated time is not updated with current-execution's last next execution time, then duplicate records
                might get created for the resources was added by ScheduleResourcePlannerExecutor.
         */

        ScheduleInfo scheduleInfo = triggerV2.getScheduleInfo();

        long endTime = triggerV2.getPlanEndTime();
        if (triggerV2.getEndTime() <= 0) {
            endTime = computeEndtimeUsingTriggerType(scheduleInfo, cutOffTime); // endtime isn't assigned here, and again set at line:68, where endpoint is 0. -Now Fixed
        } else if (triggerV2.getEndTime() > 0) {
            endTime = triggerV2.getEndTime();
        }
        LOGGER.log(Priority.INFO, "calculatedEndTime = " + endTime + ", plannerGeneratedUpto = " + plannerGeneratedUpto);
        if (endTime > plannerGeneratedUpto) {
            endTime = plannerGeneratedUpto;
        }
        //endTime = Math.min(endTime, plannerGeneratedUpto);
        LOGGER.log(Priority.INFO, "EndTime from ScheduleResourcePlannerExecutor: " + endTime);

        return endTime;
    }

    /**
     * Helper method to set endTime based on @param scheduleInfo's FrequencyType.
     *
     * @param scheduleInfo
     * @param cutOffTime   - Time from which schedule generation should be calculated.
     * @return trigger's endtime in seconds
     */
    @Override
    protected Long getComputedNextExecutionTime(Long nextExecutionTime, PlannedMaintenance plannedMaintenance) throws Exception {
        // TODO Auto-generated method stub

        Long computedCreatedTime = nextExecutionTime - (plannedMaintenance.getLeadTime() * 1000);

        return computedCreatedTime;
    }

    @Override
    protected Boolean canProceedWithCreatedTime(Long createdTime) throws Exception {
        // TODO Auto-generated method stub
        if (createdTime < DateTimeUtil.getCurrenTime()) {
            return false;
        }
        return true;
    }

    @Override
    public void deletePreOpenworkOrder(long plannerId) throws Exception {
    }

    @Override
    protected long getLastExecutionTime(List<Long> nextExecutionTimes, long generatedUpto) {
        long lastExecutionTime = nextExecutionTimes.get(nextExecutionTimes.size() - 1);
        return Math.max(lastExecutionTime, generatedUpto);
    }
}