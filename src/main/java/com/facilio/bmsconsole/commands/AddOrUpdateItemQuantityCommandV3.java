package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.*;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

;

public class AddOrUpdateItemQuantityCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule purchaseItemsModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
		List<FacilioField> purchasedItemsFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
		Map<String, FacilioField> purchasedItemsFieldMap = FieldFactory.getAsMap(purchasedItemsFields);
		purchasedItemsFields.addAll(FieldFactory.getCurrencyPropsFields(purchaseItemsModule));

		CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
		Map<String, CurrencyContext> currencyMap = Constants.getCurrencyMap(context);

		FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> itemMultiCurrencyFields = CurrencyUtil.getMultiCurrencyFieldsFromFields(itemFields);
		CurrencyUtil.addMultiCurrencyFieldsToFields(itemFields, itemModule);
		// long inventoryId = (long)
		// context.get(FacilioConstants.ContextNames.INVENTORY_ID);
		List<Long> itemIds = (List<Long>) context.get(FacilioConstants.ContextNames.ITEM_IDS);
		if (itemIds != null && !itemIds.isEmpty()) {
			long itemTypesId = -1;
			List<Long> itemTypesIds = new ArrayList<>();
			if (itemIds != null && !itemIds.isEmpty()) {
                List<V3ItemContext> itemRecords = new ArrayList<V3ItemContext>();
                Map<Long, List<UpdateChangeSet>> changes = new HashMap<Long, List<UpdateChangeSet>>();
				for (long itemId : itemIds) {
					V3ItemContext itemContext = V3ItemsApi.getItems(itemId);
					if(!itemContext.getItemType().isRotating()) {
						SelectRecordsBuilder<V3PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<V3PurchasedItemContext>()
								.select(purchasedItemsFields).table(purchaseItemsModule.getTableName())
								.moduleName(purchaseItemsModule.getName()).beanClass(V3PurchasedItemContext.class)
								.andCondition(CriteriaAPI.getCondition(purchasedItemsFieldMap.get("item"),
										String.valueOf(itemId), PickListOperators.IS));

						List<V3PurchasedItemContext> purchasedItems = selectBuilder.get();
						double quantity = 0;
						long lastPurchasedDate = -1;
						double lastPurchasedPrice = -1;
						if (purchasedItems != null && !purchasedItems.isEmpty()) {
							for (V3PurchasedItemContext purchaseditem : purchasedItems) {
								quantity += purchaseditem.getCurrentQuantity();
							}
						}

						selectBuilder = new SelectRecordsBuilder<>(selectBuilder);
						selectBuilder.orderBy("COST_DATE DESC");
						List<V3PurchasedItemContext> pItems = selectBuilder.get();
						if (pItems != null && !pItems.isEmpty()) {
							V3PurchasedItemContext pitem = pItems.get(0);
							if(pitem.getCostDate()!=null){
								lastPurchasedDate = pitem.getCostDate();
							}
							if(pitem.getUnitcost()!=null){
								lastPurchasedPrice = pitem.getUnitcost();
								itemContext.setLastPurchasedPrice(lastPurchasedPrice);
							}
							Map<String, Object> itemAsProps = FieldUtil.getAsProperties(itemContext);
							itemAsProps.put("currencyCode", pitem.getCurrencyCode());
							List<String> patchFieldNames = new ArrayList<String>(){{
								add("lastPurchasedPrice");
							}};
							CurrencyUtil.checkAndUpdateCurrencyProps(itemAsProps, itemContext, baseCurrency, currencyMap, patchFieldNames, itemMultiCurrencyFields);
							itemContext = FieldUtil.getAsBeanFromMap(itemAsProps, V3ItemContext.class);
						}


						V3ItemContext item = new V3ItemContext();
						item = itemContext;
						itemTypesId = itemContext.getItemType().getId();
						item.setCurrentQuantity(quantity);
						item.setQuantity(quantity);
						item.setLastPurchasedDate(lastPurchasedDate);
						if( item.getMinimumQuantity()!=null && item.getQuantity() <= item.getMinimumQuantity()) {
							item.setIsUnderstocked(true);
						}
						else {
							item.setIsUnderstocked(false);
						}

						itemTypesIds.add(itemTypesId);
						itemFields.addAll(FieldFactory.getCurrencyPropsFields(itemModule));
						UpdateRecordBuilder<V3ItemContext> updateBuilder = new UpdateRecordBuilder<V3ItemContext>()
								.module(itemModule).fields(itemFields)
								.andCondition(CriteriaAPI.getIdCondition(item.getId(), itemModule));

						updateBuilder.withChangeSet(V3ItemContext.class);
						updateBuilder.update(item);
						Map<Long, List<UpdateChangeSet>> recordChanges = updateBuilder.getChangeSet();
						changes.put(itemContext.getId(), recordChanges.get(itemContext.getId()));
						itemRecords.add(item);
                        
						context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemTypesId);
					
					}
					else {
						double totalConsumed = getTotalQuantityConsumed(itemId, "item");
						itemContext.setQuantity(totalConsumed);
						itemContext.setCurrentQuantity(totalConsumed);
						UpdateRecordBuilder<V3ItemContext> updateBuilder = new UpdateRecordBuilder<V3ItemContext>()
								.module(itemModule).fields(modBean.getAllFields(itemModule.getName()))
								.andCondition(CriteriaAPI.getIdCondition(itemId, itemModule));
						updateBuilder.withChangeSet(V3ItemContext.class);
						if(itemContext.getMinimumQuantity()!=null && itemContext.getQuantity() <= itemContext.getMinimumQuantity()) {
							itemContext.setIsUnderstocked(true);
						}
						else {
							itemContext.setIsUnderstocked(false);
						}
						updateBuilder.update(itemContext);
						
						Map<Long, List<UpdateChangeSet>> recordChanges = updateBuilder.getChangeSet();
						itemRecords.add(itemContext);
						changes.put(itemContext.getId(), recordChanges.get(itemContext.getId()));

					}
				}

				notifyItemOutOfStock(itemRecords,changes);
				context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
				context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, itemTypesIds);

			}
	
		}
		return false;
	}

	private void notifyItemOutOfStock(List<V3ItemContext> itemRecords, Map<Long, List<UpdateChangeSet>> changes) throws Exception {
		Map<String, Map<Long,List<UpdateChangeSet>>> finalChangeMap = new HashMap<String, Map<Long,List<UpdateChangeSet>>>();
		finalChangeMap.put(FacilioConstants.ContextNames.ITEM, changes);
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ExecuteAllWorkflowsCommand(WorkflowRuleContext.RuleType.CUSTOM_STOREROOM_MINIMUM_QUANTITY_NOTIFICATION_RULE,
				WorkflowRuleContext.RuleType.CUSTOM_STOREROOM_OUT_OF_STOCK_NOTIFICATION_RULE));
		FacilioContext context = c.getContext();
		context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, finalChangeMap);
		context.put(FacilioConstants.ContextNames.RECORD_MAP, Collections.singletonMap(FacilioConstants.ContextNames.ITEM, itemRecords));
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);

		c.execute();
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
		fields.add(FieldFactory.getField("hardReserve", "sum(case WHEN TRANSACTION_STATE = 11 THEN QUANTITY ELSE 0 END)",
				FieldType.DECIMAL));

		builder.select(fields);

		builder.andCondition(CriteriaAPI.getCondition(consumableFieldMap.get(fieldName),
				String.valueOf(inventoryCostId), PickListOperators.IS));

		List<Map<String, Object>> rs = builder.get();
		if (rs != null && rs.size() > 0) {
			double addition = 0, issues = 0, returns = 0, hardReserve=0;
			addition = rs.get(0).get("addition") != null ? (double) rs.get(0).get("addition") : 0;
			issues = rs.get(0).get("issues") != null ? (double) rs.get(0).get("issues") : 0;
			returns = rs.get(0).get("returns") != null ? (double) rs.get(0).get("returns") : 0;
			hardReserve = rs.get(0).get("hardReserve") != null ? (double) rs.get(0).get("hardReserve") : 0;

			return ((addition + returns) - issues - hardReserve);
		}
		return 0d;
	}
}
