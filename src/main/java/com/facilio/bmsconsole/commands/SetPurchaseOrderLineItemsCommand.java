package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.constants.FacilioConstants;

public class SetPurchaseOrderLineItemsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PurchaseOrderContext> records = (List<PurchaseOrderContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (records != null && !records.isEmpty()) {
			for(PurchaseOrderContext po : records) {
				PurchaseOrderAPI.setLineItems(po);
			}
		}
	
		return false;
	}

}
