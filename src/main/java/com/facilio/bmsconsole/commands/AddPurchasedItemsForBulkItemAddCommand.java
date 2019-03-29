package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.*;

public class AddPurchasedItemsForBulkItemAddCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		Map<String, FacilioField> itemFieldMap = FieldFactory.getAsMap(itemFields);

		FacilioModule purchasedItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
		List<FacilioField> purchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
		// Map<String, FacilioField> workorderCostsFieldMap =
		// FieldFactory.getAsMap(inventoryCostFields);
		long itemTypesId = -1;
		List<Long> itemTypesIds = new ArrayList<>();
		List<Long> itemIds = new ArrayList<>();
		Set<Long> uniqueItemIds = new HashSet<Long>();
		Set<Long> uniqueItemTypesIds = new HashSet<Long>();
		List<PurchasedItemContext> purchasedItem = (List<PurchasedItemContext>) context
				.get(FacilioConstants.ContextNames.PURCHASED_ITEM);

		FacilioModule itemTypesModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
		List<FacilioField> itemTypesFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);
		Map<String, FacilioField> itemTypesFieldMap = FieldFactory.getAsMap(itemTypesFields);

		List<ItemContext> items = (List<ItemContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<PurchasedItemContext> pcToBeAdded = new ArrayList<>();
		List<PurchasedItemContext> purchaseItemsList = new ArrayList<>();
		if (purchasedItem != null && !purchasedItem.isEmpty()) {
			for (PurchasedItemContext pi : purchasedItem) {
				SelectRecordsBuilder<ItemTypesContext> itemTypesselectBuilder = new SelectRecordsBuilder<ItemTypesContext>()
						.select(itemTypesFields).table(itemTypesModule.getTableName()).moduleName(itemTypesModule.getName())
						.beanClass(ItemTypesContext.class)
						.andCondition(CriteriaAPI.getIdCondition(pi.getItemType().getId(), itemTypesModule));

				List<ItemTypesContext> itemTypes = itemTypesselectBuilder.get();
				ItemTypesContext itemType = null;
				if (itemTypes != null && !itemTypes.isEmpty()) {
					itemType = itemTypes.get(0);
				}
				uniqueItemIds.add(pi.getItem().getId());
				uniqueItemTypesIds.add(pi.getItemType().getId());
				if (itemType.individualTracking()) {
					if (pi.getQuantity() > 0) {
						throw new IllegalArgumentException("Quantity cannot be set when individual Tracking is enabled");
					}
					pi.setQuantity(1);
					pi.setCurrentQuantity(1);
				} else {
					if (pi.getQuantity() <= 0) {
						throw new IllegalArgumentException("Quantity cannot be null");
					}
					double quantity = pi.getQuantity();
					pi.setCurrentQuantity(quantity);
				}
				pi.setCostDate(System.currentTimeMillis());
				if (pi.getId() <= 0) {
					// Insert
					purchaseItemsList.add(pi);
					pcToBeAdded.add(pi);
				} else {
					purchaseItemsList.add(pi);
					updateInventorycost(purchasedItemModule, purchasedItemFields, pi);
				}

			}

			if (pcToBeAdded != null && !pcToBeAdded.isEmpty()) {
				addInventorycost(purchasedItemModule, purchasedItemFields, pcToBeAdded);
			}
		}
		itemIds.addAll(uniqueItemIds);
		itemTypesIds.addAll(uniqueItemTypesIds);
		context.put(FacilioConstants.ContextNames.PURCHASED_ITEM, purchaseItemsList);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, purchaseItemsList);
		context.put(FacilioConstants.ContextNames.ITEM_IDS, itemIds);
		context.put(FacilioConstants.ContextNames.TRANSACTION_TYPE, TransactionType.STOCK);
		context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemTypesId);
		context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, itemTypesIds);

		return false;
	}

	private void addInventorycost(FacilioModule module, List<FacilioField> fields, List<PurchasedItemContext> parts)
			throws Exception {
		InsertRecordBuilder<PurchasedItemContext> readingBuilder = new InsertRecordBuilder<PurchasedItemContext>()
				.module(module).fields(fields).addRecords(parts);
		readingBuilder.save();
	}

	private void updateInventorycost(FacilioModule module, List<FacilioField> fields, PurchasedItemContext part)
			throws Exception {

		UpdateRecordBuilder<PurchasedItemContext> updateBuilder = new UpdateRecordBuilder<PurchasedItemContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(part.getId(), module));
		updateBuilder.update(part);

		System.err.println(Thread.currentThread().getName() + "Exiting updateCosts in  AddorUpdateCommand#######  ");

	}

}
