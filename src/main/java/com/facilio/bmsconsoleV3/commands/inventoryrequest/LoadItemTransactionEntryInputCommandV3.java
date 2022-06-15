package com.facilio.bmsconsoleV3.commands.inventoryrequest;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.V3ToolTransactionContext;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;

public class LoadItemTransactionEntryInputCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        List<V3InventoryRequestContext> requests= (List<V3InventoryRequestContext>) context.get(FacilioConstants.ContextNames.INVENTORY_REQUEST);

        if (CollectionUtils.isNotEmpty(requests)){
            for (V3InventoryRequestContext request : requests) {
                    //V3InventoryRequestContext inv = (V3InventoryRequestContext) RecordAPI.getRecord(FacilioConstants.ContextNames.INVENTORY_REQUEST, request.getId());
                    if (request.getApprovalFlowId() <= 0) {
                        List<ItemTransactionsContext> itemTransactions = new ArrayList<ItemTransactionsContext>();
                        List<ToolTransactionContext> toolTransactions = new ArrayList<ToolTransactionContext>();

                        for (V3InventoryRequestLineItemContext lineItem : request.getInventoryrequestlineitems()) {
                            if (lineItem.getInventoryType() == InventoryType.ITEM.getValue()) {
                                itemTransactions.add(lineItem.contructManualItemTransactionContext(request.getRequestedFor()));
                            } else if (lineItem.getInventoryType() == InventoryType.TOOL.getValue()) {
                                toolTransactions.add(lineItem.contructManualToolTransactionContext(request.getRequestedFor()));
                            }
                        }
                        context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
                        context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTransactions);
                        context.put(FacilioConstants.ContextNames.TOOL_TRANSACTIONS, toolTransactions);

                        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ITEM_ACTIVITY);


                    } else {
                        throw new IllegalArgumentException("Only Approved request can be issued");
                    }
                }
            }

        return false;
    }


}
