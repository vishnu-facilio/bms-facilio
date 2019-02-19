package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.InventryContext;
import com.facilio.bmsconsole.context.InventryContext.CostType;
import com.facilio.bmsconsole.context.ItemsContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.constants.FacilioConstants;

public class GetInventryDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			InventryContext inventry = (InventryContext) context.get(FacilioConstants.ContextNames.RECORD);
			if (inventry != null && inventry.getId() > 0) {
				if (inventry.getItem().getId() != -1) {
					Map<Long, ItemsContext> itemMap = ItemsApi.getItemsMap
							((inventry.getItem().getId()));
					inventry.setItem(itemMap.get(inventry.getItem().getId()));
				}
				
				if (inventry.getStoreRoom().getId() != -1) {
					Map<Long, StoreRoomContext> storeroomMap = StoreroomApi.getStoreRoomMap
							((inventry.getStoreRoom().getId()));
					inventry.setStoreRoom(storeroomMap.get(inventry.getStoreRoom().getId()));
				}
				inventry.setCostType(CostType.valueOf(inventry.getCostType()));
			}
			context.put(FacilioConstants.ContextNames.INVENTRY, inventry);
		}
		return false;
	}

}
