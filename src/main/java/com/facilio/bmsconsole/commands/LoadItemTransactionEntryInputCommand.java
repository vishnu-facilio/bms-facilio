package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.InventoryRequestContext;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;

public class LoadItemTransactionEntryInputCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		InventoryRequestContext request = (InventoryRequestContext)context.get(FacilioConstants.ContextNames.RECORD);

		if(request.getApprovalFlowId() <= 0) {
			List<ItemTransactionsContext> itemTransactions = new ArrayList<ItemTransactionsContext>();
			List<ToolTransactionContext> toolTransactions = new ArrayList<ToolTransactionContext>();
			
			for(InventoryRequestLineItemContext lineItem : request.getLineItems()) {
				if(lineItem.getInventoryType() == InventoryType.ITEM.getValue()) {
					itemTransactions.add(lineItem.contructManualItemTransactionContext(request.getRequestedFor()));
				}
				else if(lineItem.getInventoryType() == InventoryType.TOOL.getValue()) {
					toolTransactions.add(lineItem.contructManualToolTransactionContext(request.getRequestedFor()));
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
