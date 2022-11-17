package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class ValidateInventoryRequestLineItemBeforeDeleteCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = (List<Long>) context.get("recordIds");
        if (CollectionUtils.isNotEmpty(recordIds)) {
           List<V3InventoryRequestLineItemContext> inventoryRequestLineItems = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS, recordIds, V3InventoryRequestLineItemContext.class);
           if(CollectionUtils.isNotEmpty(inventoryRequestLineItems)) {
               for(V3InventoryRequestLineItemContext inventoryRequestLineItem : inventoryRequestLineItems) {
                   if(inventoryRequestLineItem.getIsReserved()) {
                       throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot Delete Reserved Item");
                   }
               }
           }
        }
        return false;
    }
}
