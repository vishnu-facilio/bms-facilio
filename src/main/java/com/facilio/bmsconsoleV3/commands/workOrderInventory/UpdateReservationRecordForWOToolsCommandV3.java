package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.bmsconsoleV3.context.V3TicketContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderToolsContext;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;


public class UpdateReservationRecordForWOToolsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3WorkorderToolsContext> workOrderTools = (List<V3WorkorderToolsContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);

        if(CollectionUtils.isNotEmpty(workOrderTools)){
            for(V3WorkorderToolsContext workOrderTool : workOrderTools){
                if(workOrderTool.getInventoryReservation()!=null && workOrderTool.getInventoryReservation().getId()>0){
                    V3TicketContext workOrder = workOrderTool.getWorkorder();
                    Long reservationId = workOrderTool.getInventoryReservation().getId();
                    V3InventoryUtil.UpdateReservationRecordForActuals(workOrder, reservationId, workOrderTool.getQuantity());
                }
            }
        }
        return false;
    }

}
