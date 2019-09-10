package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetNextWorkOrder extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        WorkOrderContext workOrderContext = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        PreventiveMaintenance pm = workOrderContext.getPm();
        if (pm == null) {
            return false;
        }

        Long nextExecutionTime = PreventiveMaintenanceAPI.getNextExecutionTime(pm.getId());

        pm.setNextExecutionTime(nextExecutionTime);
        return false;
    }
}
