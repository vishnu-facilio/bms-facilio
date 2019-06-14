package com.facilio.bmsconsole.commands;

import com.chargebee.internal.StringJoiner;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.context.WorkorderToolsContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class UseInventoryRequestLineItemsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<InventoryRequestLineItemContext> lineItems = (List<InventoryRequestLineItemContext>) context.get(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
		Integer inventoryType = (Integer) context.get(FacilioConstants.ContextNames.INVENTORY_CATEGORY);
		
		List<WorkorderItemContext> woItemList = new ArrayList<WorkorderItemContext>();
		List<WorkorderToolsContext> woToolList = new ArrayList<WorkorderToolsContext>();
		StringJoiner lineItemIds = new StringJoiner(",");
		
		for(InventoryRequestLineItemContext lineItem : lineItems) {
			if(inventoryType == InventoryType.ITEM.getValue())	{
				woItemList.add(lineItem.constructWorkOrderItemContext());
			}
			else if(inventoryType == InventoryType.TOOL.getValue()) {
				woToolList.add(lineItem.constructWorkOrderToolContext());
			}
			if(lineItem.getParentId() != -1) {
				lineItemIds.add(String.valueOf(lineItem.getId()));
			}
		}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, inventoryType == InventoryType.ITEM.getValue() ? woItemList : inventoryType == InventoryType.TOOL.getValue() ? woToolList : null);
		
		return false;
	}
	
	
}
