package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.InventoryContext;
import com.facilio.bmsconsole.context.InventoryVendorContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.util.InventoryApi;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;

public class GetStoreRoomDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			StoreRoomContext storeRoom = (StoreRoomContext) context.get(FacilioConstants.ContextNames.RECORD);
			if (storeRoom != null && storeRoom.getId() > 0) {
				if (storeRoom.getLocationId() != -1) {
					Map<Long, LocationContext> spaceMap = LocationAPI.getLocationMap(Collections.singleton(storeRoom.getLocationId()));
					storeRoom.setLocation(spaceMap.get(storeRoom.getLocationId()));
				}
			}
			context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoom);
		}
		return false;
	}

}
