package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.bmsconsoleV3.context.reservation.InventoryReservationContext;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetWorkOrderItemFromReservationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long reservationId = (Long) context.get(FacilioConstants.ContextNames.INVENTORY_RESERVATION);
        V3WorkorderItemContext workOrderItem = new V3WorkorderItemContext();
        if(reservationId != null) {
            InventoryReservationContext inventoryReservation = V3RecordAPI.getRecord(FacilioConstants.ContextNames.INVENTORY_RESERVATION, reservationId, InventoryReservationContext.class);
            if(inventoryReservation != null) {
                workOrderItem.setWorkorder(inventoryReservation.getWorkOrder());
                workOrderItem.setQuantity(inventoryReservation.getBalanceReservedQuantity());
                V3ItemContext item = V3ItemsApi.getItem(inventoryReservation.getItemType(), inventoryReservation.getStoreRoom());
                if(item!=null){
                    V3ItemContext itemRec = new V3ItemContext();
                    itemRec.setId(item.getId());
                    workOrderItem.setItem(itemRec);
                    workOrderItem.setStoreRoom(item.getStoreRoom());
                    if (item.getItemType() != null && item.getItemType().isRotating()) {
                        workOrderItem.setQuantity(1);
                        workOrderItem.setItemType(item.getItemType());
                    }
                }
            }
        }
        context.put(FacilioConstants.ContextNames.WORKORDER_ITEMS,workOrderItem);
        return false;
    }
}
