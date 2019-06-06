package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

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
				FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
				List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);

				for (long itemId : itemIds) {
					ItemContext itemContext = ItemsApi.getItems(itemId);
					if(!itemContext.getItemType().isRotating()) {
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


						ItemContext item = new ItemContext();
						item = itemContext;
						itemTypesId = itemContext.getItemType().getId();
						item.setQuantity(quantity);
						item.setLastPurchasedDate(lastPurchasedDate);
						item.setLastPurchasedPrice(lastPurchasedPrice);

						itemTypesIds.add(itemTypesId);
						UpdateRecordBuilder<ItemContext> updateBuilder = new UpdateRecordBuilder<ItemContext>()
								.module(itemModule).fields(modBean.getAllFields(itemModule.getName()))
								.andCondition(CriteriaAPI.getIdCondition(item.getId(), itemModule));

						updateBuilder.update(item);

						context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemTypesId);
					}
					else {
						double totalConsumed = getTotalQuantityConsumed(itemId, "item");
						itemContext.setQuantity(totalConsumed);
						UpdateRecordBuilder<ItemContext> updateBuilder = new UpdateRecordBuilder<ItemContext>()
								.module(itemModule).fields(modBean.getAllFields(itemModule.getName()))
								.andCondition(CriteriaAPI.getIdCondition(itemId, itemModule));
						updateBuilder.update(itemContext);
					}
				}
				context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, itemTypesIds);
			}
		}
		return false;
	}

	public static double getTotalQuantityConsumed(long inventoryCostId, String fieldName) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule consumableModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		List<FacilioField> consumableFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		Map<String, FacilioField> consumableFieldMap = FieldFactory.getAsMap(consumableFields);

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(consumableModule.getTableName())
				.andCustomWhere(consumableModule.getTableName() + ".ORGID = " + AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(consumableModule),
						String.valueOf(consumableModule.getModuleId()), NumberOperators.EQUALS))
				.andCustomWhere("APPROVED_STATE = ? OR APPROVED_STATE = ? ", 1, 3);
		// builder.andCondition(CriteriaAPI.getCondition(consumableFieldMap.get("approvedState"),
		// String.valueOf(1), NumberOperators.EQUALS))
		// .orCondition(CriteriaAPI.getCondition(consumableFieldMap.get("approvedState"),
		// String.valueOf(3), NumberOperators.EQUALS));

		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("addition", "sum(case WHEN TRANSACTION_STATE = 1 THEN QUANTITY ELSE 0 END)",
				FieldType.DECIMAL));
		fields.add(FieldFactory.getField("issues", "sum(case WHEN TRANSACTION_STATE = 2 THEN QUANTITY ELSE 0 END)",
				FieldType.DECIMAL));
		fields.add(FieldFactory.getField("returns", "sum(case WHEN TRANSACTION_STATE = 3 THEN QUANTITY ELSE 0 END)",
				FieldType.DECIMAL));
		builder.select(fields);

		builder.andCondition(CriteriaAPI.getCondition(consumableFieldMap.get(fieldName),
				String.valueOf(inventoryCostId), PickListOperators.IS));

		List<Map<String, Object>> rs = builder.get();
		if (rs != null && rs.size() > 0) {
			double addition = 0, issues = 0, returns = 0;
			addition = rs.get(0).get("addition") != null ? (double) rs.get(0).get("addition") : 0;
			issues = rs.get(0).get("issues") != null ? (double) rs.get(0).get("issues") : 0;
			returns = rs.get(0).get("returns") != null ? (double) rs.get(0).get("returns") : 0;
			return ((addition + returns) - issues);
		}
		return 0d;
	}
}
