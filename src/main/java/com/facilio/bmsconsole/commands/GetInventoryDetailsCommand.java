package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.InventoryContext;
import com.facilio.bmsconsole.context.InventoryVendorContext;
import com.facilio.bmsconsole.util.InventoryApi;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;

public class GetInventoryDetailsCommand extends GenericGetModuleDataDetailCommand{
	@Override
	public boolean execute(Context context) throws Exception {
		
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			super.execute(context);
			InventoryContext inventoryContext = (InventoryContext) context.get(FacilioConstants.ContextNames.RECORD);
			if (inventoryContext != null && inventoryContext.getId() > 0) {
				if (inventoryContext.getSpaceId() != -1) {
					Map<Long, BaseSpaceContext> spaceMap = SpaceAPI.getBaseSpaceMap(Collections.singleton(inventoryContext.getSpaceId()));
					inventoryContext.setSpace(spaceMap.get(inventoryContext.getSpaceId()));
				}
				if (inventoryContext.getVendor() != null && inventoryContext.getVendor().getId() > 0) {
					Map<Long, InventoryVendorContext> vendorsMap = InventoryApi.getInventoryVendorMap(Collections.singleton(inventoryContext.getVendor().getId()));
					inventoryContext.setVendor(vendorsMap.get(inventoryContext.getVendor().getId()));
				}
			}
			context.put(FacilioConstants.ContextNames.INVENTORY, inventoryContext);
		}
		return false;
	}
}
