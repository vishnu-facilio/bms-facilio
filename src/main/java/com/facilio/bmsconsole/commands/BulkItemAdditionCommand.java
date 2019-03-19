package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class BulkItemAdditionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ItemContext> itemsList = (List<ItemContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
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
		for (ItemContext item : itemsList) {
			if (!itemTypesId.contains(item.getItemType().getId())) {
				itemToBeAdded.add(item);
			} else {
				item.setId(itemTypeVsItem.get(item.getItemType().getId()));
			}
			purchasedItems.addAll(item.getPurchasedItems());
			item.setPurchasedItems(null);
		}

		if (itemToBeAdded != null && !itemToBeAdded.isEmpty()) {
			addItem(itemModule, itemFields, itemToBeAdded);
		}

		context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItems);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, itemsList);
		return false;
	}

	private void addItem(FacilioModule module, List<FacilioField> fields, List<ItemContext> parts) throws Exception {
		InsertRecordBuilder<ItemContext> readingBuilder = new InsertRecordBuilder<ItemContext>().module(module)
				.fields(fields).addRecords(parts);
		readingBuilder.save();
	}
}
