package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;

public class LoadItemTransactionEntryInputCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		InventoryRequestContext request = (InventoryRequestContext)context.get(FacilioConstants.ContextNames.INVENTORY_REQUEST);
		if(request.getStatusEnum() == InventoryRequestContext.Status.APPROVED ) {
			List<ItemTransactionsContext> itemTransactions = new ArrayList<ItemTransactionsContext>();
			List<ToolTransactionContext> toolTransactions = new ArrayList<ToolTransactionContext>();
			
			for(InventoryRequestLineItemContext lineItem : request.getLineItems()) {
				if(lineItem.getInventoryType() == InventoryType.ITEM.getValue()) {
					itemTransactions.add(lineItem.contructManualItemTransactionContext(request.getRequestedBy()));
				}
				else if(lineItem.getInventoryType() == InventoryType.TOOL.getValue()) {
					toolTransactions.add(lineItem.contructManualToolTransactionContext(request.getRequestedBy()));
				}
			}
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
			context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTransactions);
			context.put(FacilioConstants.ContextNames.TOOL_TRANSACTIONS, toolTransactions);
			
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ITEM_ACTIVITY);
			
		}
		else {
			throw new IllegalArgumentException("Only Approved request can be issued");
		}
		return false;
	}

	
}
