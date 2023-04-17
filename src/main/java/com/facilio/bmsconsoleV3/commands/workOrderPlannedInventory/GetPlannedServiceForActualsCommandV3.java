package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderServiceContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedServicesContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetPlannedServiceForActualsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long plannedServiceId = (Long) context.get(FacilioConstants.ContextNames.WO_PLANNED_SERVICES);
        V3WorkOrderServiceContext workOrderService = new V3WorkOrderServiceContext();
        if(plannedServiceId != null) {
            WorkOrderPlannedServicesContext workOrderPlannedService = V3RecordAPI.getRecord(FacilioConstants.ContextNames.WO_PLANNED_SERVICES, plannedServiceId, WorkOrderPlannedServicesContext.class);
            if(workOrderPlannedService != null) {
                V3ServiceContext service = new V3ServiceContext();
                service.setId(workOrderPlannedService.getService().getId());
                workOrderService.setService(service);
            }
        }
        context.put(FacilioConstants.ContextNames.WO_SERVICE, workOrderService);
        return false;
    }
}
