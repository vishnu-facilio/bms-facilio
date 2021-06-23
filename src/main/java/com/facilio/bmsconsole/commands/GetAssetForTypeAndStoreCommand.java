package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;

public class GetAssetForTypeAndStoreCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Integer inventoryType = (Integer)context.get(FacilioConstants.ContextNames.INVENTORY_CATEGORY);
		Long storeId = (Long)context.get(FacilioConstants.ContextNames.STORE_ROOM_ID);
		
		if(inventoryType == 1) {
			Long itemTypeId = (Long)context.get(FacilioConstants.ContextNames.ITEM_TYPES_ID);
			context.put(FacilioConstants.ContextNames.ASSETS, AssetsAPI.getAssetForItemTypeAndStore(itemTypeId, storeId));	
		}
		else if(inventoryType == 2) {
			Long toolTypeId = (Long)context.get(FacilioConstants.ContextNames.TOOL_TYPES_ID);
			context.put(FacilioConstants.ContextNames.ASSETS, AssetsAPI.getAssetForToolTypeAndStore(toolTypeId, storeId));	
		}
		
		return false;
	}

}
