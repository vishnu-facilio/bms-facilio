package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.reservation.InventoryReservationContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class CreateReservationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<WorkOrderPlannedItemsContext> workOrderPlannedItems = (List<WorkOrderPlannedItemsContext>) context.get(FacilioConstants.ContextNames.WO_PLANNED_ITEMS);

        if(CollectionUtils.isNotEmpty(workOrderPlannedItems)){
            for(WorkOrderPlannedItemsContext workOrderPlannedItem : workOrderPlannedItems){
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_RESERVATION);
                InventoryReservationContext reservation = getReservationRecord(workOrderPlannedItem);
                V3Util.createRecord(module, FacilioUtil.getAsMap(FieldUtil.getAsJSON(reservation)),null,null);
            }
        }
        return false;
    }
    public InventoryReservationContext getReservationRecord(WorkOrderPlannedItemsContext workOrderPlannedItem){
        InventoryReservationContext reservation = new InventoryReservationContext();
        reservation.setReservationSource(InventoryReservationContext.ReservationSource.WO_PLANS.getIndex());
        reservation.setReservationType(workOrderPlannedItem.getReservationType());
        reservation.setReservationStatus(InventoryReservationContext.InventoryReservationStatus.NOT_ISSUED.getIndex());
        reservation.setStoreRoom(workOrderPlannedItem.getStoreRoom());
        reservation.setWorkOrder(workOrderPlannedItem.getWorkOrder());
        reservation.setItemType(workOrderPlannedItem.getItemType());
        reservation.setReservedQuantity(workOrderPlannedItem.getQuantity());
        reservation.setBalanceReservedQuantity(workOrderPlannedItem.getQuantity());
        reservation.setWorkOrderPlannedItem(workOrderPlannedItem);

        return reservation;
    }
}