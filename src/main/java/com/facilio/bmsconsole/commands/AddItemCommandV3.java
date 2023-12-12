package com.facilio.bmsconsole.commands;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.bmsconsoleV3.context.V3BinContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

public class AddItemCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = Constants.getModuleName(context);
		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
		List<V3ItemContext> itemRecords = recordMap.get(moduleName);

		if (CollectionUtils.isNotEmpty(itemRecords)) {
			List<V3PurchasedItemContext> purchasedItems = new ArrayList<>();
			Map<Long,V3BinContext> binMap = new HashMap<>();
			for (V3ItemContext item : itemRecords) {
				List<V3PurchasedItemContext> itemPurchasedItems = item.getPurchasedItems();
				Boolean isFirstBin = !V3ItemsApi.itemHasBin(item);

				if (itemPurchasedItems != null && !itemPurchasedItems.isEmpty()) {
					Set<String> binToAdd = new HashSet<>();
					Set<Long> existingBinIds = new HashSet<>();
					for (V3PurchasedItemContext pItem : itemPurchasedItems) {
						V3BinContext bin = pItem.getBin();
						if(bin != null){
							if( bin.getId() <= 0 && bin.getName() != null) {
								binToAdd.add(bin.getName());
							} else if(bin.getId() > 0) {
								existingBinIds.add(bin.getId());
							}
						}
					}
					V3ItemsApi.validateBin(existingBinIds,item);
					Map<String, Long> newlyAddedBins = new HashMap<>();
					if(!CollectionUtils.isEmpty(binToAdd)){
						newlyAddedBins = V3ItemsApi.quickAddBin(binToAdd, item);
					}
					for (V3PurchasedItemContext pItem : itemPurchasedItems) {
						V3BinContext bin = pItem.getBin();
						Boolean existingBin = bin != null && bin.getId() > 0;
						if(!existingBin) {
							if(bin != null && bin.getName() != null) {
								if(MapUtils.isNotEmpty(newlyAddedBins) && newlyAddedBins.containsKey(bin.getName())){
									Long id = newlyAddedBins.get(bin.getName());
									bin.setId(id);
								}
							} else {
								if (item.getDefaultBin() != null) {
									bin = item.getDefaultBin();
								}
								else {
									bin = V3ItemsApi.addVirtualBin(item);
								}
							}
						}
						pItem.setBin(bin);
						if(bin!= null && bin.getId() > 0){
							if((isFirstBin || item.getDefaultBin() == null) ){
								V3ItemsApi.makeBinDefault(item, bin);
								isFirstBin = false;
							}
							binMap.put(bin.getId(),bin);
						}
						pItem.setItem(item);
						pItem.setItemType(item.getItemType());
						purchasedItems.add(pItem);
					}
					item.setPurchasedItems(null);

				}
			}
			context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItems);
			context.put(FacilioConstants.ContextNames.BIN,binMap.values().stream().collect(Collectors.toList()));
			context.put(FacilioConstants.ContextNames.RECORD_LIST, itemRecords);
			context.put(FacilioConstants.ContextNames.STORE_ROOM,itemRecords.get(0).getStoreRoom().getId());
		}

		return false;
	}


}
