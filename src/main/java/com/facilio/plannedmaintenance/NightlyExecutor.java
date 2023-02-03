package com.facilio.plannedmaintenance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.time.DateRange;

public class NightlyExecutor extends ExecutorBase {

	@Override
	protected FacilioStatus getStatus(Context context) throws Exception {
		Map<FacilioStatus.StatusType, FacilioStatus> statusMap = (Map<FacilioStatus.StatusType, FacilioStatus>) context.get(FacilioConstants.ContextNames.STATUS_MAP);
		return statusMap.get(FacilioStatus.StatusType.PRE_OPEN);
	}

	@Override
	protected List<Long> getNextExecutionTimes(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		PMTriggerV2 trigger = (PMTriggerV2) context.get("trigger");
        long cutOffTime = (Long) context.get("cutOffTime");
        
        PMPlanner pmPlanner = (PMPlanner) context.get(FacilioConstants.PM_V2.PM_V2_PLANNER);
        
        long generatedUpto = pmPlanner.getGeneratedUpto();
        
        if(generatedUpto > 0) {
        	
        	long endDate = computeEndtimeUsingTriggerType(trigger.getScheduleInfo(), cutOffTime);
        	trigger.setEndTime(endDate);
            
            if(generatedUpto < endDate) {
            	List<DateRange> times = trigger.getScheduleInfo().getTimeIntervals(generatedUpto, endDate);
            	
            	List<Long> nextExecutionTimes = new ArrayList<>();
            	for(DateRange time : times) {
            		
            		long createdtime = time.getEndTime()+1;
            		nextExecutionTimes.add(createdtime);
            	}
            	return nextExecutionTimes;
            }
        }
		
		return null;
	}

	@Override
	protected Long getComputedNextExecutionTime(Long nextExecutionTime, PlannedMaintenance plannedMaintenance)
			throws Exception {
		// TODO Auto-generated method stub
		Long computedCreatedTime = nextExecutionTime - (plannedMaintenance.getLeadTime() * 1000);
		
		return computedCreatedTime;
	}

	@Override
	protected Boolean canProceedWithCreatedTime(Long createdTime) throws Exception {			// nightly should process all including old ones
		// TODO Auto-generated method stub
		return true;
	}

}
