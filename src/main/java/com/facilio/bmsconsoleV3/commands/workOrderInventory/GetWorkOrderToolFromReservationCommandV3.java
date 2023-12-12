package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderToolsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.bmsconsoleV3.context.reservation.InventoryReservationContext;
import com.facilio.bmsconsoleV3.util.V3InventoryRequestAPI;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class GetWorkOrderToolFromReservationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long reservationId = (Long) context.get(FacilioConstants.ContextNames.INVENTORY_RESERVATION);
        V3WorkorderToolsContext workOrderTool = new V3WorkorderToolsContext();
        if(reservationId != null) {
            SupplementRecord workorderField = (SupplementRecord) Constants.getModBean().getField("workOrder", FacilioConstants.ContextNames.INVENTORY_RESERVATION);
            InventoryReservationContext inventoryReservation = V3RecordAPI.getRecord(FacilioConstants.ContextNames.INVENTORY_RESERVATION, reservationId, InventoryReservationContext.class, Collections.singletonList(workorderField));
            if(inventoryReservation != null) {
                workOrderTool.setQuantity(inventoryReservation.getBalanceReservedQuantity());
                V3WorkOrderContext workorder = inventoryReservation.getWorkOrder();
                workOrderTool.setWorkorder(workorder);
                User issueTo = V3InventoryRequestAPI.getUserToIssueFromReservation(inventoryReservation);
                if(issueTo == null && workorder != null) {
                    issueTo = workorder.getAssignedTo();
                }
                workOrderTool.setIssuedTo(issueTo);
                V3ToolContext tool = V3ToolsApi.getTool(inventoryReservation.getToolType(), inventoryReservation.getStoreRoom());
                if(tool!=null){
                    V3ToolContext toolRec = new V3ToolContext();
                    toolRec.setId(tool.getId());
                    workOrderTool.setTool(toolRec);
                    workOrderTool.setStoreRoom(tool.getStoreRoom());
                    workOrderTool.setBin(tool.getDefaultBin());
                    if (tool.getToolType() != null && tool.getToolType().isRotating()) {
                        workOrderTool.setQuantity(1.0);
                        workOrderTool.setToolType(tool.getToolType());
                    }
                }
            }
        }
        context.put(FacilioConstants.ContextNames.WORKORDER_TOOLS,workOrderTool);
        return false;
    }
}
