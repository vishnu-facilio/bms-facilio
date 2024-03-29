package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

;

public class BulkItemAdditionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ItemContext> itemsList = (List<ItemContext>) context.get(FacilioConstants.ContextNames.ITEMS);
		if (itemsList != null && !itemsList.isEmpty()) {
			long storeRoomId = (long) context.get(FacilioConstants.ContextNames.STORE_ROOM);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
			List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
			Map<String, FacilioField> itemFieldMap = FieldFactory.getAsMap(itemFields);
			List<Long> itemTypesId = new ArrayList<>();
			Map<Long, Long> itemTypeVsItem = new HashMap<>();
			SelectRecordsBuilder<ItemContext> itemselectBuilder = new SelectRecordsBuilder<ItemContext>()
					.select(itemFields).table(itemModule.getTableName()).moduleName(itemModule.getName())
					.beanClass(ItemContext.class).andCondition(CriteriaAPI.getCondition(itemFieldMap.get("storeRoom"),
							String.valueOf(storeRoomId), NumberOperators.EQUALS));

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
					if(item.getCostType()<=0) {
						item.setCostType(CostType.FIFO);
					}
					itemToBeAdded.add(item);
				} else {
					item.setId(itemTypeVsItem.get(item.getItemType().getId()));
					updateItem(itemModule, itemFields, item);
				}
			}

			if (itemToBeAdded != null && !itemToBeAdded.isEmpty()) {
				addItem(itemModule, itemFields, itemToBeAdded);
			}

			for (ItemContext item : itemsList) {
				if (item.getPurchasedItems() != null && !item.getPurchasedItems().isEmpty()) {
					for (PurchasedItemContext pItem : item.getPurchasedItems()) {
						pItem.setItem(item);
						pItem.setItemType(item.getItemType());
						purchasedItems.add(pItem);
					}
					item.setPurchasedItems(null);
				}
			}
			context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, purchasedItems);
			context.put(FacilioConstants.ContextNames.RECORD_LIST, itemsList);
		}
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
