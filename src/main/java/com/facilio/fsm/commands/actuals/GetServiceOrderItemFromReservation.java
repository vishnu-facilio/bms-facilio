package com.facilio.fsm.commands.actuals;

import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceInventoryReservationContext;
import com.facilio.fsm.context.ServiceOrderItemsContext;
import org.apache.commons.chain.Context;

public class GetServiceOrderItemFromReservation extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long reservationId = (Long) context.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION);
        ServiceOrderItemsContext serviceOrderItem = new ServiceOrderItemsContext();
        if(reservationId != null) {
            ServiceInventoryReservationContext inventoryReservation = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_INVENTORY_RESERVATION, reservationId, ServiceInventoryReservationContext.class);
            if(inventoryReservation != null) {
                serviceOrderItem.setQuantity(inventoryReservation.getBalanceReservedQuantity());
                V3ItemContext item = V3ItemsApi.getItem(inventoryReservation.getItemType(), inventoryReservation.getStoreRoom());
                if(item!=null){
                    V3ItemContext itemRec = new V3ItemContext();
                    itemRec.setId(item.getId());
                    serviceOrderItem.setItem(itemRec);
                    serviceOrderItem.setStoreRoom(item.getStoreRoom());
                    if (item.getItemType() != null && item.getItemType().isRotating()) {
                        serviceOrderItem.setQuantity(1d);
                        serviceOrderItem.setItemType(item.getItemType());
                    }
                }
            }
        }
        context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ITEMS,serviceOrderItem);
        return false;
    }
}
