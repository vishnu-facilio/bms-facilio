package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
				storeRoom.setSites(getSitesList(storeRoom.getId()));
			}
			context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoom);
		}
		return false;
	}
	
	public List<Long> getSitesList (long storeRoomId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getSitesForStoreRoomFields())
                .table(ModuleFactory.getSitesForStoreRoomModule().getTableName())
                .andCustomWhere("STORE_ROOM_ID = ?", storeRoomId);

        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty()) {
            List<Long> bsids = new ArrayList<>();
            for(Map<String, Object> prop : props) {
                bsids.add((Long) prop.get("siteId"));
            }
            return bsids;
        }
        return null;

	}

}
