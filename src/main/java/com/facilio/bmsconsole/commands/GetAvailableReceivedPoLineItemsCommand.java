package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.constants.FacilioConstants;

public class GetAvailableReceivedPoLineItemsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		int inventoryType = (Integer)context.get(FacilioConstants.ContextNames.INVENTORY_CATEGORY);
		List<PurchaseOrderLineItemContext> lineItemList = PurchaseOrderAPI.getPoReceivedLineItemList(inventoryType);
		context.put(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS, lineItemList);
		return false;
	}

}
