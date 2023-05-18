package com.facilio.plannedmaintenance;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections.list.AbstractLinkedList;
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
    protected List<Long> getNextExecutionTimes(Context context) throws Exception {// return list of milliseconds.
        PMTriggerV2 trigger = (PMTriggerV2) context.get("trigger");
        long currentTime = System.currentTimeMillis();
        long startTime = trigger.getStartTime() > 0 ? trigger.getStartTime() > currentTime ? trigger.getStartTime() - 300 : currentTime : currentTime;
        long endTime = calculateEndTime(trigger, startTime);
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

    /**
     * Helper function to get the ScheduleInfo object from @param trigger
     * @param trigger
     * @return
     * @throws Exception
     */

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
    private long calculateEndTime(PMTriggerV2 triggerV2, long cutOffTime) throws Exception {
        // planEndtime is the time till which the trigger will be executed
        // trigger end time is the time till which the trigger was configured

        // case 1: planEndtime and triggerEndtime are not configured -> we compute using triggertype
        // case 2: planEndtime is configured but triggerEndtime is not configured -> we choose planEndtime
        // case 3: trigger endTime is configured but not planEndtime -> we chose trigger end time
        // case 4: both are configured - > we chose earliest of the two

        ScheduleInfo scheduleInfo = triggerV2.getScheduleInfo();

        long endTime = triggerV2.getPlanEndTime();
        if (triggerV2.getEndTime() <= 0) {
            endTime = computeEndtimeUsingTriggerType(scheduleInfo, cutOffTime); // endtime isn't assigned here, and again set at line:68, where endpoint is 0. -Now Fixed
        }
        else if (triggerV2.getEndTime() > 0 ) {
            endTime = triggerV2.getEndTime();
        }
        return endTime;
    }

    /**
     * Helper method to set endTime based on @param scheduleInfo's FrequencyType.
     * @param scheduleInfo
     * @param cutOffTime - Time from which schedule generation should be calculated.
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
		if(createdTime < DateTimeUtil.getCurrenTime()) {
    		return false;
    	}
		return true;
	}
    @Override
    public  void deletePreOpenworkOrder(long plannerId) throws Exception {
        List<Long> plannerIdList = new ArrayList<>();
        plannerIdList.add(plannerId);
        PlannedMaintenanceAPI.deleteWorkOrdersFromPlannerId(plannerIdList);
    }
}