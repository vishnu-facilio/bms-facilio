package com.facilio.plannedmaintenance;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;

public interface Executor {
    List<V3WorkOrderContext> execute(Context context) throws Exception;
}
