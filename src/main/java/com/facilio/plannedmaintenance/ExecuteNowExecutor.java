package com.facilio.plannedmaintenance;

import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;

public class ExecuteNowExecutor extends ExecutorBase {
    @Override
    protected List<Long> getNextExecutionTimes(Context context) {
        return Arrays.asList(System.currentTimeMillis());
    }
}
