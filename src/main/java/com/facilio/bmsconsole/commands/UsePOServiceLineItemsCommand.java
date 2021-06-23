package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.context.WorkOrderServiceContext;
import com.facilio.constants.FacilioConstants;

public class UsePOServiceLineItemsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PurchaseOrderLineItemContext> lineItems = (List<PurchaseOrderLineItemContext>) context.get(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		Integer inventoryType = (Integer) context.get(FacilioConstants.ContextNames.INVENTORY_CATEGORY);
		Long parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		
		List<WorkOrderServiceContext> woServiceList = new ArrayList<WorkOrderServiceContext>();
		
		for(PurchaseOrderLineItemContext lineItem : lineItems) {
			if(inventoryType == InventoryType.SERVICE.getValue())	{
				woServiceList.add(lineItem.constructWorkOrderServiceContext(parentId));
			}
		}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, woServiceList);
		return false;
	}

}
