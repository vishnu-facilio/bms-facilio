package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderToolsContext;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetWorkOrderToolFromIssuedToolCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long toolTransactionId = (Long) context.get(FacilioConstants.ContextNames.TOOL_TRANSACTION_ID);
        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        V3WorkorderToolsContext workOrderTool = new V3WorkorderToolsContext();
        if(toolTransactionId != null && workOrderId != null) {
            V3ToolTransactionContext toolTransaction = V3RecordAPI.getRecord(FacilioConstants.ContextNames.TOOL_TRANSACTIONS, toolTransactionId, V3ToolTransactionContext.class);
            V3WorkOrderContext workOrder = new V3WorkOrderContext();
            workOrder.setId(workOrderId);
            if(toolTransaction != null) {
                workOrderTool.setWorkorder(workOrder);
                workOrderTool.setTool(toolTransaction.getTool());
                workOrderTool.setStoreRoom(toolTransaction.getStoreRoom());
                workOrderTool.setQuantity(toolTransaction.getRemainingQuantity());
                workOrderTool.setRemainingQuantity(toolTransaction.getRemainingQuantity());
                workOrderTool.setIssuedTo(toolTransaction.getIssuedTo());
                if(workOrderTool.getRequestedLineItem()!=null){
                    workOrderTool.setRequestedLineItem(toolTransaction.getRequestedLineItem());
                }
            }
        }
        context.put(FacilioConstants.ContextNames.WORKORDER_TOOLS,workOrderTool);
        return false;
    }
}
