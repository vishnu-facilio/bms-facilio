package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;


public class GetNextWorkOrder extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long workOrderId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);

        long nextExecutionTime = PreventiveMaintenanceAPI.getNextExecutionTimeForWorkOrder(workOrderId);

        context.put(FacilioConstants.ContextNames.RESULT, nextExecutionTime);
        return false;
    }
}
