package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class UpdateInventoryRequestReservationStatusCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3InventoryRequestLineItemContext> inventoryRequestLineItems = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(inventoryRequestLineItems)) {
            for (V3InventoryRequestLineItemContext inventoryRequestLineItem : inventoryRequestLineItems) {
                long invReqId = inventoryRequestLineItem.getInventoryRequestId().getId();
                if(invReqId > 0) {
                    V3InventoryUtil.updateInventoryRequestReservationStatus(invReqId);
                }
            }
        }
        return false;
    }
}
