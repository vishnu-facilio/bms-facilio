package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ShipmentLineItemContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;

public class LoadTransactionInputForShipmentCommand implements Command{

	@Override 
    public boolean execute(Context context) throws Exception { 
        // TODO Auto-generated method stub 
        ShipmentContext shipment = (ShipmentContext)context.get(FacilioConstants.ContextNames.RECORD); 
        if((shipment.isShipmentTrackingEnabled() && shipment.getStatusEnum() == ShipmentContext.Status.STAGED) || !shipment.isShipmentTrackingEnabled() ) { 
            List<ItemTransactionsContext> itemTransactions = new ArrayList<ItemTransactionsContext>(); 
            List<ToolTransactionContext> toolTransactions = new ArrayList<ToolTransactionContext>(); 
             
            for(ShipmentLineItemContext lineItem : shipment.getLineItems()) { 
                if(lineItem.getInventoryType() == InventoryType.ITEM.getValue()) { 
                    itemTransactions.add(lineItem.contructManualItemTransactionContext(shipment)); 
                } 
                else if(lineItem.getInventoryType() == InventoryType.TOOL.getValue()) { 
                    toolTransactions.add(lineItem.contructManualToolTransactionContext(shipment)); 
                } 
            } 
            context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE); 
            context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTransactions); 
            context.put(FacilioConstants.ContextNames.TOOL_TRANSACTIONS, toolTransactions); 
             
            context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ITEM_ACTIVITY); 
             
        } 
        return false; 
    } 

}
