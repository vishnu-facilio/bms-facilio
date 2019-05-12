package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.ItemActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddItemCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ItemContext item_rec = (ItemContext) context.get(FacilioConstants.ContextNames.RECORD);
		long storeRoomId = (long) context.get(FacilioConstants.ContextNames.STORE_ROOM);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		Map<String, FacilioField> itemFieldMap = FieldFactory.getAsMap(itemFields);
		List<Long> itemTypesId = new ArrayList<>();
		Map<Long, Long> itemTypeVsItem = new HashMap<>();
		SelectRecordsBuilder<ItemContext> itemselectBuilder = new SelectRecordsBuilder<ItemContext>().select(itemFields)
				.table(itemModule.getTableName()).moduleName(itemModule.getName()).beanClass(ItemContext.class)
				.andCondition(CriteriaAPI.getCondition(itemFieldMap.get("storeRoom"), String.valueOf(storeRoomId),
						NumberOperators.EQUALS));

		List<ItemContext> items = itemselectBuilder.get();
		if (items != null && !items.isEmpty()) {
			for (ItemContext item : items) {
				itemTypesId.add(item.getItemType().getId());
				itemTypeVsItem.put(item.getItemType().getId(), item.getId());
			}
		}
		
		List<ItemContext> itemToBeAdded = new ArrayList<>();
		List<PurchasedItemContext> purchasedItems = new ArrayList<>();

		if (!itemTypesId.contains(item_rec.getItemType().getId())) {
			if(item_rec.getCostType()<=0) {
				item_rec.setCostType(CostType.FIFO);
			}
			itemToBeAdded.add(item_rec);
		} else {
			item_rec.setId(itemTypeVsItem.get(item_rec.getItemType().getId()));
			updateItem(itemModule, itemFields, item_rec);
		}

		if (itemToBeAdded != null && !itemToBeAdded.isEmpty()) {
			addItem(itemModule, itemFields, itemToBeAdded);
		}

		
		

		JSONObject info = new JSONObject();
		info.put("costTypeEnum", item_rec.getCostTypeEnum());
		info.put("itemTypeId", item_rec.getItemType().getId());
		info.put("storeroomId", item_rec.getStoreRoom().getId());
		List<Object> purchasedItemValues = new ArrayList<>();
		if (item_rec.getPurchasedItems() != null && !item_rec.getPurchasedItems().isEmpty()) {
			for (PurchasedItemContext pItem : item_rec.getPurchasedItems()) {
				pItem.setItem(item_rec);
				pItem.setItemType(item_rec.getItemType());
				purchasedItems.add(pItem);
				JSONObject purchase = new JSONObject();
				purchase.put("quantity", pItem.getQuantity());
				purchase.put("unitCost", pItem.getUnitcost());
				purchasedItemValues.add(purchase);
			}
			item_rec.setPurchasedItems(null);
			info.put("purchasedItemValues", purchasedItemValues);
		}
		JSONObject newinfo = new JSONObject();
		newinfo.put("addItem", info);
		CommonCommandUtil.addActivityToContext(item_rec.getId(), -1, ItemActivityType.ADD_ITEM, newinfo, (FacilioContext) context);


		context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItems);
		context.put(FacilioConstants.ContextNames.RECORD, item_rec);
		context.put(FacilioConstants.ContextNames.RECORD_ID, item_rec.getId());
		return false;
	}

	private void addItem(FacilioModule module, List<FacilioField> fields, List<ItemContext> parts) throws Exception {
		InsertRecordBuilder<ItemContext> readingBuilder = new InsertRecordBuilder<ItemContext>().module(module)
				.fields(fields).addRecords(parts);
		readingBuilder.save();
	}
	
	private void updateItem(FacilioModule module, List<FacilioField> fields, ItemContext item) throws Exception {
		UpdateRecordBuilder<ItemContext> readingBuilder = new UpdateRecordBuilder<ItemContext>().module(module)
				.fields(fields).andCondition(CriteriaAPI.getIdCondition(item.getId(), module));
		readingBuilder.update(item);
	}
}
