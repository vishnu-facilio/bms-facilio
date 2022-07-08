package com.facilio.plannedmaintenance;

import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.modules.FieldUtil;
import com.facilio.taskengine.ScheduleInfo;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class ScheduleExecutor extends ExecutorBase {
    @Override
    protected List<Long> getNextExecutionTimes(Context context) throws Exception {
        PMTriggerV2 trigger = (PMTriggerV2) context.get("trigger");
        long cutOffTime = (Long) context.get("cutOffTime");
        int maxCount = (int) context.getOrDefault("maxCount", 0);

        if (maxCount <= 0) {
            throw new IllegalArgumentException("Invalid count.");
        }

        String scheduleInfo = trigger.getSchedule();
        JSONParser parser = new JSONParser();
        ScheduleInfo schedule = FieldUtil.getAsBeanFromJson((JSONObject)parser.parse(scheduleInfo), ScheduleInfo.class);;

        Pair<Long, Integer> nextExecutionTime = schedule.nextExecutionTime(Pair.of(trigger.getStartTime()/1000, 0));

        List<Long> nextExecutionTimes = new ArrayList<>();

        int count = 0;
        while (true) {
            if (trigger.getEndTime() > 0) {
                if (nextExecutionTime.getLeft() > schedule.getEndDate()/1000) {
                    break;
                }
            }

            if (nextExecutionTime.getLeft() < cutOffTime/1000) {
                nextExecutionTime = schedule.nextExecutionTime(nextExecutionTime);
                continue;
            }

            if (count > maxCount) {
                break;
            }

            nextExecutionTimes.add(nextExecutionTime.getLeft());
            nextExecutionTime = schedule.nextExecutionTime(nextExecutionTime);
            count++;
        }

        return nextExecutionTimes;
    }
}