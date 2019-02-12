package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.InventoryContext;
import com.facilio.constants.FacilioConstants;

public class UpdateInventoryCommand extends GenericUpdateModuleDataCommand{
	@Override
	public boolean execute(Context context) throws Exception {
		
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			InventoryContext inventoryContext = (InventoryContext) context.get(FacilioConstants.ContextNames.RECORD);
			inventoryContext.setModifiedTime(System.currentTimeMillis());
			super.execute(context);
			context.put(FacilioConstants.ContextNames.INVENTORY, inventoryContext);
		}
		return false;
	}
}
