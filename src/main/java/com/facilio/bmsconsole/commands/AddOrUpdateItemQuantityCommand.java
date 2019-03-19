package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkorderPartsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateItemQuantityCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule inventoryCostModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
		List<FacilioField> inventoryCostFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
		Map<String, FacilioField> inventoryrCostsFieldMap = FieldFactory.getAsMap(inventoryCostFields);
		// long inventoryId = (long)
		// context.get(FacilioConstants.ContextNames.INVENTORY_ID);
		List<Long> itemIds = (List<Long>) context.get(FacilioConstants.ContextNames.ITEM_IDS);
		long itemTypesId = -1;
		List<Long> itemTypesIds = new ArrayList<>();

		if (itemIds != null && !itemIds.isEmpty()) {
			for (long inventoryId : itemIds) {
				SelectRecordsBuilder<PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<PurchasedItemContext>()
						.select(inventoryCostFields).table(inventoryCostModule.getTableName())
						.moduleName(inventoryCostModule.getName()).beanClass(PurchasedItemContext.class)
						.andCondition(CriteriaAPI.getCondition(inventoryrCostsFieldMap.get("item"),
								String.valueOf(inventoryId), PickListOperators.IS));

				List<PurchasedItemContext> purchasedItems = selectBuilder.get();
				int quantity = 0;
				if (purchasedItems != null && !purchasedItems.isEmpty()) {
					for (PurchasedItemContext invCost : purchasedItems) {
						quantity += invCost.getCurrentQuantity();
					}
				}
				FacilioModule inventoryModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
				List<FacilioField> inventoryFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
				Map<String, FacilioField> inventoryFieldMap = FieldFactory.getAsMap(inventoryFields);

				SelectRecordsBuilder<ItemContext> inventoryselectBuilder = new SelectRecordsBuilder<ItemContext>()
						.select(inventoryFields).table(inventoryModule.getTableName())
						.moduleName(inventoryModule.getName()).beanClass(ItemContext.class)
						.andCondition(CriteriaAPI.getIdCondition(inventoryId, inventoryModule));

				List<ItemContext> inventory = inventoryselectBuilder.get();
				ItemContext inven = new ItemContext();
				if (inventory != null && !inventory.isEmpty()) {
					inven = inventory.get(0);
					itemTypesId = inven.getItemType().getId();
					inven.setQuantity(quantity);
				}
				
				itemTypesIds.add(itemTypesId);
				UpdateRecordBuilder<ItemContext> updateBuilder = new UpdateRecordBuilder<ItemContext>()
						.module(inventoryModule).fields(modBean.getAllFields(inventoryModule.getName()))
						.andCondition(CriteriaAPI.getIdCondition(inven.getId(), inventoryModule));

				updateBuilder.update(inven);
				
				context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemTypesId);
			}
			context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, itemTypesIds);
		}

		return false;
	}

}
