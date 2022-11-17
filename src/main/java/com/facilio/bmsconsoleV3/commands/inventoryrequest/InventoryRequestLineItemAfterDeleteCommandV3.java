package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class InventoryRequestLineItemAfterDeleteCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3InventoryRequestLineItemContext> inventoryRequestLineItems = (List<V3InventoryRequestLineItemContext>) context.get("deletedRecords");
        if(CollectionUtils.isNotEmpty(inventoryRequestLineItems)) {
            for(V3InventoryRequestLineItemContext inventoryRequestLineItem : inventoryRequestLineItems) {
                long invReqId = inventoryRequestLineItem.getInventoryRequestId().getId();
                if(invReqId > 0) {
                    V3InventoryUtil.updateInventoryRequestReservationStatus(invReqId);
                }
            }
        }
        return false;
    }
}
