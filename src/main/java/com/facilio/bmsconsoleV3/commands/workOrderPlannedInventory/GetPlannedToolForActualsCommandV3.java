package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderToolsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedServicesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedToolsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetPlannedToolForActualsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long plannedToolId = (Long) context.get(FacilioConstants.ContextNames.TOOL);
        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        V3WorkorderToolsContext workorderTool = new V3WorkorderToolsContext();
        if(plannedToolId != null && plannedToolId > 0 && workOrderId != null && workOrderId > 0) {
            V3WorkOrderContext workOrder = new V3WorkOrderContext();
            workOrder.setId(workOrderId);
            WorkOrderPlannedToolsContext plannedTool = V3RecordAPI.getRecord(FacilioConstants.ContextNames.WO_PLANNED_TOOLS, plannedToolId, WorkOrderPlannedToolsContext.class);
            if(plannedTool != null){
                V3ToolContext tool = V3ToolsApi.getTool(plannedTool.getToolType().getId(), plannedTool.getStoreRoom().getId());
                if(tool != null) {
                    V3ToolContext toolRecord = new V3ToolContext();
                    toolRecord.setId(tool.getId());
                    workorderTool.setTool(toolRecord);
                    workorderTool.setWorkorder(workOrder);
                    workorderTool.setStoreRoom(tool.getStoreRoom());
                }
            }
        }
        context.put(FacilioConstants.ContextNames.WORKORDER_TOOLS,workorderTool);
        return false;
    }
}
