package com.facilio.plannedmaintenance;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import org.apache.commons.chain.Context;

import java.util.List;

public interface Executor {
    List<V3WorkOrderContext> execute(Context context, PlannedMaintenance plannedMaintenance, PMPlanner pmPlanner) throws Exception;
}
