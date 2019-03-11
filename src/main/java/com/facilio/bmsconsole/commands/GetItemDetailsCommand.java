package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.constants.FacilioConstants;

public class GetItemDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			ItemContext inventry = (ItemContext) context.get(FacilioConstants.ContextNames.RECORD);
			if (inventry != null && inventry.getId() > 0) {
				if (inventry.getItemType().getId() != -1) {
					Map<Long, ItemTypesContext> itemMap = ItemsApi.getItemsMap
							((inventry.getItemType().getId()));
					inventry.setItemType(itemMap.get(inventry.getItemType().getId()));
				}
				
				if (inventry.getStoreRoom().getId() != -1) {
					Map<Long, StoreRoomContext> storeroomMap = StoreroomApi.getStoreRoomMap
							((inventry.getStoreRoom().getId()));
					inventry.setStoreRoom(storeroomMap.get(inventry.getStoreRoom().getId()));
				}
				inventry.setCostType(CostType.valueOf(inventry.getCostType()));
			}
			context.put(FacilioConstants.ContextNames.ITEM, inventry);
		}
		return false;
	}

}
