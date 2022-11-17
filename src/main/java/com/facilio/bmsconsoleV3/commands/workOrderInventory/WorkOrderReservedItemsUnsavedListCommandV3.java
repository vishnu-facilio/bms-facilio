package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.bmsconsoleV3.context.reservation.InventoryReservationContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkOrderReservedItemsUnsavedListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> reservationIds = (List<Long>) context.get(FacilioConstants.ContextNames.INVENTORY_RESERVATION);
        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);

        if(!reservationIds.isEmpty() && workOrderId != null) {
            List<V3WorkorderItemContext> workorderItems = new ArrayList<>();
            List<InventoryReservationContext> inventoryReservations = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.INVENTORY_RESERVATION,reservationIds,InventoryReservationContext.class);
            for(InventoryReservationContext inventoryReservation : inventoryReservations){
                V3WorkorderItemContext workorderItem = new V3WorkorderItemContext();
                V3ItemContext item = V3ItemsApi.getItem(inventoryReservation.getItemType(), inventoryReservation.getStoreRoom());
                workorderItem.setWorkorder(inventoryReservation.getWorkOrder());
                //workorderItem.setWorkOrderPlannedItem(inventoryReservation.getWorkOrderPlannedItem());
                InventoryReservationContext reservation = new InventoryReservationContext();
                reservation.setId(inventoryReservation.getId());
                workorderItem.setInventoryReservation(reservation);
                workorderItem.setItem(item);
                workorderItem.setStoreRoom(item.getStoreRoom());
                workorderItem.setQuantity(inventoryReservation.getBalanceReservedQuantity());
                workorderItem.setUnitPrice(item.getLastPurchasedPrice());
                Double cost = null;
                if(item.getLastPurchasedPrice() != null && item.getLastPurchasedPrice() > 0) {
                    cost = workorderItem.getQuantity() * workorderItem.getUnitPrice();
                }
                workorderItem.setCost(cost);
                workorderItems.add(workorderItem);
            }
            Map<String, List> recordMap = new HashMap<>();
            recordMap.put(FacilioConstants.ContextNames.WORKORDER_ITEMS,workorderItems);
            context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
            context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.WORKORDER_ITEMS);
            context.put(FacilioConstants.ContextNames.WORKORDER_ITEMS,workorderItems);
        }
        return false;
    }
}
