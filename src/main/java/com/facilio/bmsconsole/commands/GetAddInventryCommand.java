package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.InventoryContext;
import com.facilio.constants.FacilioConstants;

public class GetAddInventryCommand extends GenericAddModuleDataCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		if (context.get(FacilioConstants.ContextNames.RECORD) != null) {
			InventoryContext inventoryContext = (InventoryContext) context.get(FacilioConstants.ContextNames.RECORD);
			inventoryContext.setModifiedTime(System.currentTimeMillis());
			super.executeCommand(context);
			context.put(FacilioConstants.ContextNames.ITEM, inventoryContext);
		}
		return false;
	}
}
