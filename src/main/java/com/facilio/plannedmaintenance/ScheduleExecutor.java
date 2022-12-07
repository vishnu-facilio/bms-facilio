package com.facilio.plannedmaintenance;

import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.time.ZonedDateTime;
import java.util.*;

public class ScheduleExecutor extends ExecutorBase {
    @Override
    protected FacilioStatus getStatus(Context context) throws Exception {
        Map<FacilioStatus.StatusType, FacilioStatus> statusMap = (Map<FacilioStatus.StatusType, FacilioStatus>) context.get(FacilioConstants.ContextNames.STATUS_MAP);
        return statusMap.get(FacilioStatus.StatusType.PRE_OPEN);
    }

    @Override
    protected List<Long> getNextExecutionTimes(Context context) throws Exception {
        PMTriggerV2 trigger = (PMTriggerV2) context.get("trigger");
        long cutOffTime = (Long) context.get("cutOffTime");

        ScheduleInfo schedule = getScheduleInfo(trigger);
        // nextExecutionTime is the first next execution time based on trigger's StartTime.
        Pair<Long, Integer> nextExecutionTime = schedule.nextExecutionTime(Pair.of(trigger.getStartTime() / 1000, 0));
        List<Long> nextExecutionTimes = new ArrayList<>();
        calculateEndTime(trigger, cutOffTime);

        long maxNextExecutionCount = 1000;
        while (nextExecutionTime.getLeft() <= (trigger.getEndTime())) { // inconsistency, endpoint set via computeEndtimeUsingTriggerType() is in seconds so /1000 isn't required. -Now Fixed
            if (nextExecutionTime.getLeft() < cutOffTime / 1000) {
                nextExecutionTime = schedule.nextExecutionTime(nextExecutionTime);
                continue;
            }
            nextExecutionTimes.add(nextExecutionTime.getLeft());
            if (nextExecutionTimes.size() > maxNextExecutionCount) {
                throw new IllegalArgumentException("Only 1000 executions are allowed, this is to avoid OOMs and infinite looping.");
            }
            nextExecutionTime = schedule.nextExecutionTime(nextExecutionTime);
        }

        return nextExecutionTimes;
    }

    /**
     * Helper function to get the ScheduleInfo object from @param trigger
     * @param trigger
     * @return
     * @throws Exception
     */
    private ScheduleInfo getScheduleInfo(PMTriggerV2 trigger) throws Exception {
        String scheduleInfo = trigger.getSchedule();
        JSONParser parser = new JSONParser();
        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson((JSONObject) parser.parse(scheduleInfo), ScheduleInfo.class);
        return schedule;
    }

    /**
     * Helper method to set the endTime in @param triggerV2 based on 'planEndtime' and 'triggerEndtime'.
     *
     *  - 'planEndtime' is the time till which the trigger will be executed.
     *  - 'triggerEndtime' is the time till which the trigger was configured
     *
     *  - Case 1: planEndtime and triggerEndtime are not configured -> we compute using triggertype
     *  - Case 2: planEndtime is configured but triggerEndtime is not configured -> we choose planEndtime
     *  - Case 3: trigger endTime is configured but not planEndtime -> we chose trigger end time
     *  - Case 4: both are configured - > we chose earliest of the two
     * @param triggerV2
     * @param cutOffTime - Time from which schedule generation should be calculated.
     * @throws Exception
     */
    private void calculateEndTime(PMTriggerV2 triggerV2, long cutOffTime) throws Exception {
        // planEndtime is the time till which the trigger will be executed
        // trigger end time is the time till which the trigger was configured

        // case 1: planEndtime and triggerEndtime are not configured -> we compute using triggertype
        // case 2: planEndtime is configured but triggerEndtime is not configured -> we choose planEndtime
        // case 3: trigger endTime is configured but not planEndtime -> we chose trigger end time
        // case 4: both are configured - > we chose earliest of the two

        ScheduleInfo scheduleInfo = getScheduleInfo(triggerV2);

        long endTime = triggerV2.getPlanEndTime();
        if (triggerV2.getEndTime() <= 0 && triggerV2.getPlanEndTime() <= 0) {
            endTime = computeEndtimeUsingTriggerType(scheduleInfo, cutOffTime); // endtime isn't assigned here, and again set at line:68, where endpoint is 0. -Now Fixed
        } else if (triggerV2.getPlanEndTime() > 0 && triggerV2.getEndTime() <= 0) {
            endTime = triggerV2.getPlanEndTime();
        } else if (triggerV2.getEndTime() > 0 && triggerV2.getPlanEndTime() <= 0) {
            endTime = triggerV2.getEndTime();
        } else {
            endTime = Math.min(triggerV2.getPlanEndTime(), triggerV2.getEndTime());
        }
        triggerV2.setEndTime(endTime);
    }

    /**
     * Helper method to set endTime based on @param scheduleInfo's FrequencyType.
     * @param scheduleInfo
     * @param cutOffTime - Time from which schedule generation should be calculated.
     * @return trigger's endtime in seconds
     */
    private long computeEndtimeUsingTriggerType(ScheduleInfo scheduleInfo, long cutOffTime) {
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
        return zonedEnd.toEpochSecond();
    }

	@Override
	protected Long getComputedNextExecutionTime(Long nextExecutionTime, PlannedMaintenance plannedMaintenance) throws Exception {
		// TODO Auto-generated method stub
		
		Long computedCreatedTime = (nextExecutionTime - plannedMaintenance.getLeadTime()) * 1000;
		
		return computedCreatedTime;
	}
}