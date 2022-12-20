package com.facilio.plannedmaintenance;

import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.modules.FacilioStatus;
import com.facilio.time.DateTimeUtil;

import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;

public class ExecuteNowExecutor extends ExecutorBase {
    @Override
    protected FacilioStatus getStatus(Context context) {
        return null;
    }

    @Override
    protected List<Long> getNextExecutionTimes(Context context) {
        return Arrays.asList(System.currentTimeMillis());
    }
    
    @Override
	protected Long getComputedNextExecutionTime(Long nextExecutionTime, PlannedMaintenance plannedMaintenance) throws Exception {
		// TODO Auto-generated method stub
		return nextExecutionTime;
	}

	@Override
	protected Boolean canProceedWithCreatedTime(Long createdTime) throws Exception {
		// TODO Auto-generated method stub
		if(createdTime <= (DateTimeUtil.getCurrenTime() - 5 * 60 * 60)) {	// adding 5 minutes buffer time
    		return false;
    	}
		return true;
	}
}
