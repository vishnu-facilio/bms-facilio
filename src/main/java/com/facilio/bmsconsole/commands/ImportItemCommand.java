package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ImportItemCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<PurchasedItemContext> purchasedItemList = (List<PurchasedItemContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		long storeRoomId = (long) context.get(FacilioConstants.ContextNames.STORE_ROOM);
		if (purchasedItemList != null && !purchasedItemList.isEmpty() && storeRoomId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			Map<String, Long> itemNameVsIdMap = ItemsApi.getAllItemTypes();
			List<ItemContext> itemsList = new ArrayList<>();
			for (PurchasedItemContext purchasedItem : purchasedItemList) {
				ItemTypesContext itemType = new ItemTypesContext();
				ItemContext item = new ItemContext();
				if (itemNameVsIdMap.containsKey(purchasedItem.getItemType().getName())) {
					itemType.setId(itemNameVsIdMap.get(purchasedItem.getItemType().getName()));
				} else {
					itemType = purchasedItem.getItemType();
					if (purchasedItem.getQuantity() > 0 && (purchasedItem.getSerialNumber() == null || purchasedItem.getSerialNumber().equalsIgnoreCase("null"))) {
						itemType.setIsRotating(false);
					}
					else if(purchasedItem.getSerialNumber() != null && !purchasedItem.getSerialNumber().equalsIgnoreCase("null")) {
						itemType.setIsRotating(true);
					}
					itemType.setId(insertItemType(modBean, itemType));
				}
				item.setItemType(itemType);
				item.setStoreRoom(StoreroomApi.getStoreRoom(storeRoomId));
				item.setPurchasedItems(Collections.singletonList(purchasedItem));
				itemsList.add(item);
			}
			context.put(FacilioConstants.ContextNames.ITEMS, itemsList);
			context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoomId);
		}
		return false;
	}

	private long insertItemType(ModuleBean modBean, ItemTypesContext itemType) throws Exception {
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);
		InsertRecordBuilder<ItemTypesContext> insertRecordBuilder = new InsertRecordBuilder<ItemTypesContext>()
				.module(module).fields(fields);
		long id = insertRecordBuilder.insert(itemType);
		return id;
	}

}
