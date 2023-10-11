package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.bmsconsoleV3.context.V3TicketContext;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class UpdateReservationRecordForWOItemsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3WorkorderItemContext> workOrderItems = (List<V3WorkorderItemContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);

        if(CollectionUtils.isNotEmpty(workOrderItems)){
            for(V3WorkorderItemContext workOrderItem : workOrderItems){
                if(workOrderItem.getInventoryReservation()!=null && workOrderItem.getInventoryReservation().getId()>0){
                    Long reservationId = workOrderItem.getInventoryReservation().getId();
                    V3TicketContext workOrder = workOrderItem.getWorkorder();
                    V3InventoryUtil.UpdateReservationRecordForActuals(workOrder, reservationId, workOrderItem.getQuantity());
                }
            }
        }
        return false;
    }
}
