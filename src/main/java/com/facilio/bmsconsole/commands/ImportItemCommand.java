package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryCategoryContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.util.InventoryCategoryApi;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportItemCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<PurchasedItemContext> purchasedItemList = (List<PurchasedItemContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		long storeRoomId = (long) context.get(FacilioConstants.ContextNames.STORE_ROOM);
		if (purchasedItemList != null && !purchasedItemList.isEmpty() && storeRoomId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			Map<String, Long> itemNameVsIdMap = ItemsApi.getAllItemTypes();
			Map<String, Long> categoryNameVsIdMap = InventoryCategoryApi.getAllInventoryCategories();
			List<ItemContext> itemsList = new ArrayList<>();
			Map<String, Long> itemNameVsIndexMap = new HashMap<>();
			Long itemIndexCounter;
			itemIndexCounter = (long) 0;
			for (PurchasedItemContext purchasedItem : purchasedItemList) {
				ItemTypesContext itemType = new ItemTypesContext();
				ItemContext item = new ItemContext();
				if (itemNameVsIdMap != null && itemNameVsIdMap.containsKey(purchasedItem.getItemType().getName())) {
					itemType.setId(itemNameVsIdMap.get(purchasedItem.getItemType().getName()));
				} else {
					itemType = purchasedItem.getItemType();
					if (purchasedItem.getQuantity() > 0 && (purchasedItem.getSerialNumber() == null || purchasedItem.getSerialNumber().equalsIgnoreCase("null"))) {
						itemType.setIsRotating(false);
						itemType.setIsConsumable(true);
					}
					else if(purchasedItem.getSerialNumber() != null && !purchasedItem.getSerialNumber().equalsIgnoreCase("null")) {
						itemType.setIsRotating(true);
					}
					InventoryCategoryContext category = new InventoryCategoryContext();
					if (itemType.getCategory() != null) {
						if (categoryNameVsIdMap != null
								&& categoryNameVsIdMap.containsKey(itemType.getCategory().getName())) {
							category.setId(categoryNameVsIdMap.get(itemType.getCategory().getName()));
							itemType.setCategory(category);
						} else {
							category.setName(itemType.getCategory().getName());
							category.setDisplayName(itemType.getCategory().getName());
							category.setId(InventoryCategoryApi.insertInventoryCategory(category));
							if (categoryNameVsIdMap == null) {
								categoryNameVsIdMap = new HashMap<String, Long>();
							}
							categoryNameVsIdMap.put(category.getName(), category.getId());
						}
						itemType.setCategory(category);
					}
					long insertItemTypeId = insertItemType(modBean, itemType);
					if(itemNameVsIdMap == null) {
						itemNameVsIdMap = new HashMap<String, Long>();
					}
					itemNameVsIdMap.put(itemType.getName(), insertItemTypeId);
					itemType.setId(insertItemTypeId);
				}
				item.setItemType(itemType);
				item.setStoreRoom(StoreroomApi.getStoreRoom(storeRoomId));
				if (purchasedItem.getItem() != null && purchasedItem.getItem().getMinimumQuantity() > 0) {
					item.setMinimumQuantity(purchasedItem.getItem().getMinimumQuantity());
				}
				if (purchasedItem.getItem() != null && purchasedItem.getItem().getData() != null) {
					item.setData(purchasedItem.getItem().getData());
				}

				if (purchasedItem.getQuantity() > 0) {
					List<PurchasedItemContext> purItem = new ArrayList<>();
					purItem.add(purchasedItem);
					item.setPurchasedItems(purItem);
				}
				if (itemNameVsIndexMap.containsKey(purchasedItem.getItemType().getName())) {
					int itemIndex = itemNameVsIndexMap.get(purchasedItem.getItemType().getName()).intValue();
					ItemContext exisItem = itemsList.get(itemIndex);
					List<PurchasedItemContext> curPurchasedItem = exisItem.getPurchasedItems();
					if (purchasedItem.getQuantity() > 0) {
						if (curPurchasedItem == null) {
							curPurchasedItem = new ArrayList<>();
							curPurchasedItem.add(purchasedItem);
						} else {
							curPurchasedItem.add(purchasedItem);
						}
					}
					exisItem.setPurchasedItems(curPurchasedItem);
					itemsList.set(itemIndex, exisItem);
				} else {
					itemsList.add(item);
					itemNameVsIndexMap.put(purchasedItem.getItemType().getName(), itemIndexCounter);
					itemIndexCounter++;
				}
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
