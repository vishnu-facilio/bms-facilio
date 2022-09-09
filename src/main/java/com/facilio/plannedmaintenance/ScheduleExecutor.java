package com.facilio.plannedmaintenance;

import com.facilio.bmsconsole.context.PMTriggerV2;
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

        String scheduleInfo = trigger.getSchedule();
        JSONParser parser = new JSONParser();
        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson((JSONObject) parser.parse(scheduleInfo), ScheduleInfo.class);
        Pair<Long, Integer> nextExecutionTime = schedule.nextExecutionTime(Pair.of(trigger.getStartTime() / 1000, 0));

        List<Long> nextExecutionTimes = new ArrayList<>();
        calculateEndTime(trigger, cutOffTime);

        while (nextExecutionTime.getLeft() <= (trigger.getEndTime())) { // inconsistency, endpoint set via computeEndtimeUsingTriggerType() is in seconds so /1000 isn't required. -Now Fixed
            if (nextExecutionTime.getLeft() < cutOffTime / 1000) {
                nextExecutionTime = schedule.nextExecutionTime(nextExecutionTime);
                continue;
            }
            nextExecutionTimes.add(nextExecutionTime.getLeft());
            nextExecutionTime = schedule.nextExecutionTime(nextExecutionTime);
        }

        return nextExecutionTimes;
    }

    private void calculateEndTime(PMTriggerV2 triggerV2, long cutOffTime) {
        // planEndtime is the time till which the trigger will be executed
        // trigger end time is the time till which the trigger was configured

        // case 1: planEndtime and triggerEndtime are not configured -> we compute using triggertype
        // case 2: planEndtime is configured but triggerEndtime is not configured -> we choose planEndtime
        // case 3: trigger endTime is configured but not planEndtime -> we chose trigger end time
        // case 4: both are configured - > we chose earliest of the two

        long endTime = triggerV2.getPlanEndTime();
        if (triggerV2.getEndTime() <= 0 && triggerV2.getPlanEndTime() <= 0) {
            endTime = computeEndtimeUsingTriggerType(triggerV2, cutOffTime); // endtime isn't assigned here, and again set at line:68, where endpoint is 0. -Now Fixed
        } else if (triggerV2.getPlanEndTime() > 0 && triggerV2.getEndTime() <= 0) {
            endTime = triggerV2.getPlanEndTime();
        } else if (triggerV2.getEndTime() > 0 && triggerV2.getPlanEndTime() <= 0) {
            endTime = triggerV2.getEndTime();
        } else {
            endTime = Math.min(triggerV2.getPlanEndTime(), triggerV2.getEndTime());
        }
        triggerV2.setEndTime(endTime);
    }

    private long computeEndtimeUsingTriggerType(PMTriggerV2 triggerV2, long cutOffTime) {
        ZonedDateTime dateTime = DateTimeUtil.getDateTime(cutOffTime, false);
        ZonedDateTime zonedEnd = dateTime.plusDays(15);
        if (triggerV2.getFrequencyEnum() == PMTriggerV2.PMTriggerFrequency.DAILY) {
            zonedEnd = dateTime.plusDays(15);
        } else if (triggerV2.getFrequencyEnum() == PMTriggerV2.PMTriggerFrequency.WEEKLY) {
            zonedEnd = dateTime.plusWeeks(15);
        } else if (triggerV2.getFrequencyEnum() == PMTriggerV2.PMTriggerFrequency.MONTHLY) {
            zonedEnd = dateTime.plusMonths(15);
        } else if (triggerV2.getFrequencyEnum() == PMTriggerV2.PMTriggerFrequency.QUARTERLY) {
            zonedEnd = dateTime.plusYears(5);
        } else if (triggerV2.getFrequencyEnum() == PMTriggerV2.PMTriggerFrequency.HALF_YEARLY) {
            zonedEnd = dateTime.plusYears(5);
        } else if (triggerV2.getFrequencyEnum() == PMTriggerV2.PMTriggerFrequency.ANNUALLY) {
            zonedEnd = dateTime.plusYears(5);
        }
        return zonedEnd.toEpochSecond();
    }
}