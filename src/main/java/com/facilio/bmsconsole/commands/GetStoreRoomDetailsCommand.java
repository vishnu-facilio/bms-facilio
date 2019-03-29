package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.Map;

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
