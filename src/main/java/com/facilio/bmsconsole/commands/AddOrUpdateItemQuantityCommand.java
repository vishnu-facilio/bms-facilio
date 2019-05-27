package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

;

public class AddOrUpdateItemQuantityCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule purchaseItemsModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
		List<FacilioField> purchasedItemsFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
		Map<String, FacilioField> purchasedItemsFieldMap = FieldFactory.getAsMap(purchasedItemsFields);
		// long inventoryId = (long)
		// context.get(FacilioConstants.ContextNames.INVENTORY_ID);
		List<Long> itemIds = (List<Long>) context.get(FacilioConstants.ContextNames.ITEM_IDS);
		if (itemIds != null && !itemIds.isEmpty()) {
			long itemTypesId = -1;
			List<Long> itemTypesIds = new ArrayList<>();

			if (itemIds != null && !itemIds.isEmpty()) {
				for (long itemId : itemIds) {
					SelectRecordsBuilder<PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<PurchasedItemContext>()
							.select(purchasedItemsFields).table(purchaseItemsModule.getTableName())
							.moduleName(purchaseItemsModule.getName()).beanClass(PurchasedItemContext.class)
							.andCondition(CriteriaAPI.getCondition(purchasedItemsFieldMap.get("item"),
									String.valueOf(itemId), PickListOperators.IS));

					List<PurchasedItemContext> purchasedItems = selectBuilder.get();
					int quantity = 0;
					long lastPurchasedDate = -1;
					double lastPurchasedPrice = -1;
					if (purchasedItems != null && !purchasedItems.isEmpty()) {
						for (PurchasedItemContext purchaseditem : purchasedItems) {
							quantity += purchaseditem.getCurrentQuantity();
						}
					}

					selectBuilder.orderBy("COST_DATE DESC");
					List<PurchasedItemContext> pItems = selectBuilder.get();
					if (pItems != null && !pItems.isEmpty()) {
						PurchasedItemContext pitem = pItems.get(0);
						lastPurchasedDate = pitem.getCostDate();
						lastPurchasedPrice = pitem.getUnitcost();
					}

					FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
					List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);

					SelectRecordsBuilder<ItemContext> inventoryselectBuilder = new SelectRecordsBuilder<ItemContext>()
							.select(itemFields).table(itemModule.getTableName()).moduleName(itemModule.getName())
							.beanClass(ItemContext.class).andCondition(CriteriaAPI.getIdCondition(itemId, itemModule));

					List<ItemContext> items = inventoryselectBuilder.get();
					ItemContext item = new ItemContext();
					if (items != null && !items.isEmpty()) {
						item = items.get(0);
						itemTypesId = item.getItemType().getId();
						item.setQuantity(quantity);
						item.setLastPurchasedDate(lastPurchasedDate);
						item.setLastPurchasedPrice(lastPurchasedPrice);
					}

					itemTypesIds.add(itemTypesId);
					UpdateRecordBuilder<ItemContext> updateBuilder = new UpdateRecordBuilder<ItemContext>()
							.module(itemModule).fields(modBean.getAllFields(itemModule.getName()))
							.andCondition(CriteriaAPI.getIdCondition(item.getId(), itemModule));

					updateBuilder.update(item);

					context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemTypesId);
				}
				context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, itemTypesIds);
			}
		}
		return false;
	}

}
