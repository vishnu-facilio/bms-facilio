package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class GetAssetForTypeAndStoreCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
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
