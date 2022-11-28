package com.facilio.plannedmaintenance;

import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.modules.FacilioStatus;
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
}
