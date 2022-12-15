package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderServiceContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderToolsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkOrderServiceUnsavedListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> serviceIds = (List<Long>) context.get(FacilioConstants.ContextNames.SERVICE);
        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);

        if(serviceIds != null && workOrderId != null) {
            V3WorkOrderContext workOrder = new V3WorkOrderContext();
            workOrder.setId(workOrderId);
            List<V3ServiceContext> services = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.SERVICE,serviceIds,V3ServiceContext.class);
            List<V3WorkOrderServiceContext> workorderServices = new ArrayList<>();
            for(V3ServiceContext service : services){
                V3WorkOrderServiceContext workorderService = new V3WorkOrderServiceContext();
                workorderService.setService(service);
                workorderService.setUnitPrice(service.getBuyingPrice());
                workorderService.setQuantity(1.0);
                workorderService.setDuration(1.0);
                Double cost = V3InventoryUtil.getServiceCost(service, workorderService.getDuration(), workorderService.getQuantity());
                workorderService.setCost(cost);
                workorderServices.add(workorderService);
            }
            Map<String, List> recordMap = new HashMap<>();
            recordMap.put(FacilioConstants.ContextNames.WO_SERVICE,workorderServices);
            context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
            context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.WO_SERVICE);
            context.put(FacilioConstants.ContextNames.WO_SERVICE,workorderServices);
        }
        return false;
    }
}
