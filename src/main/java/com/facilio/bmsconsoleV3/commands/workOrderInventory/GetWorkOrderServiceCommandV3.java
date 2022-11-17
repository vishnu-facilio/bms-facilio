package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderServiceContext;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetWorkOrderServiceCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long serviceId = (Long) context.get(FacilioConstants.ContextNames.SERVICE);
        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        V3WorkOrderServiceContext workorderService = new V3WorkOrderServiceContext();
        if(serviceId != null && workOrderId != null) {
            V3WorkOrderContext workOrder = new V3WorkOrderContext();
            workOrder.setId(workOrderId);
            V3ServiceContext service = V3RecordAPI.getRecord(FacilioConstants.ContextNames.SERVICE, serviceId, V3ServiceContext.class);
            if(service != null) {
                workorderService.setService(service);
                workorderService.setUnitPrice(service.getBuyingPrice());
            }
        }
        context.put(FacilioConstants.ContextNames.WO_SERVICE,workorderService);
        return false;
    }
}
