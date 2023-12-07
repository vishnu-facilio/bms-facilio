package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;

public class AddItemCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = Constants.getModuleName(context);
		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
		List<V3ItemContext> itemRecords = recordMap.get(moduleName);
		if (itemRecords != null && !itemRecords.isEmpty()) {
			List<V3PurchasedItemContext> purchasedItems = new ArrayList<>();
			for (V3ItemContext item : itemRecords) {
				if (item.getPurchasedItems() != null && !item.getPurchasedItems().isEmpty()) {
					for (V3PurchasedItemContext pItem : item.getPurchasedItems()) {
						pItem.setItem(item);
						pItem.setItemType(item.getItemType());
						purchasedItems.add(pItem);
					}
					item.setPurchasedItems(null);
				}
			}
			context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItems);
			context.put(FacilioConstants.ContextNames.RECORD_LIST, itemRecords);
			context.put(FacilioConstants.ContextNames.STORE_ROOM,itemRecords.get(0).getStoreRoom().getId());
		}

		return false;
	}
}
