package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetWorkOrderItemFromIssuedItemCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long itemTransactionId = (Long) context.get(FacilioConstants.ContextNames.ITEM_TRANSACTION_ID);
        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        V3WorkorderItemContext workOrderItem = new V3WorkorderItemContext();
        if(itemTransactionId != null && workOrderId != null) {
            V3ItemTransactionsContext itemTransaction = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ITEM_TRANSACTIONS, itemTransactionId, V3ItemTransactionsContext.class);
            V3WorkOrderContext workOrder = new V3WorkOrderContext();
            workOrder.setId(workOrderId);
            if(itemTransaction != null) {
                workOrderItem.setWorkorder(workOrder);
                workOrderItem.setItem(itemTransaction.getItem());
                workOrderItem.setStoreRoom(itemTransaction.getStoreRoom());
                workOrderItem.setQuantity(itemTransaction.getRemainingQuantity());
                workOrderItem.setRemainingQuantity(itemTransaction.getRemainingQuantity());
                workOrderItem.setBin(itemTransaction.getBin());
                if(workOrderItem.getRequestedLineItem()!=null){
                    workOrderItem.setRequestedLineItem(itemTransaction.getRequestedLineItem());
                }
            }
        }
        context.put(FacilioConstants.ContextNames.WORKORDER_ITEMS,workOrderItem);
        return false;
    }
}
