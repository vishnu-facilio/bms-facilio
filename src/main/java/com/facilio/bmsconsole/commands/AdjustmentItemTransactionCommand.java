package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.chain.FacilioContext;
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
import com.facilio.modules.fields.LookupField;
import com.facilio.bmsconsole.util.ItemsApi;

public class AdjustmentItemTransactionCommand extends FacilioCommand {
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule itemTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		List<FacilioField> itemTransactionsFields = modBean
				.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		FacilioModule purchasedItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
		List<FacilioField> purchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
		
		List<ItemTransactionsContext> itemTransactions = (List<ItemTransactionsContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		
		List<ItemTransactionsContext> itemTransactiosnToBeAdded = new ArrayList<>();
		long itemTypeId = -1;
		if (itemTransactions != null && !itemTransactions.isEmpty()) {
			for (ItemTransactionsContext itemTransaction : itemTransactions) {
				ItemContext item = ItemsApi.getItems(itemTransaction.getItem().getId());
				itemTypeId = item.getItemType().getId();
				ItemTypesContext itemType = ItemsApi.getItemTypes(itemTypeId);
				if (itemTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_DECREASE
						&& itemType.isRotating()) {
					throw new IllegalArgumentException("Not Applicable for Rotating Items!");
				}
				else if (itemTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_INCREASE
						&& itemType.isRotating()) {
					throw new IllegalArgumentException("Not Applicable for Rotating Items!");
				} 
				else if (itemTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_DECREASE
						&& item.getQuantity() < itemTransaction.getQuantity()) {
					throw new IllegalArgumentException("Invalid Adjustment Quantity!");
				}
				else if(itemTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_DECREASE
						&& !itemType.isRotating())
				{
					if(itemTransaction.getQuantity() <= item.getQuantity())
					{
						List<PurchasedItemContext> purchasedItem = new ArrayList<>();
	
						if (item.getCostTypeEnum() == null || item.getCostType() <= 0
								|| item.getCostTypeEnum() == CostType.FIFO) {
							purchasedItem = getPurchasedItemList(item.getId(), " asc", purchasedItemModule,
									purchasedItemFields);
						} else if (item.getCostTypeEnum() == CostType.LIFO) {
							purchasedItem = getPurchasedItemList(item.getId(), " desc", purchasedItemModule,
									purchasedItemFields);
						}
	
						if (purchasedItem != null && !purchasedItem.isEmpty()) {
							PurchasedItemContext pItem = purchasedItem.get(0);
							double requiredQuantity = -(itemTransaction.getQuantity());
							if (requiredQuantity + pItem.getCurrentQuantity() >= 0) {
								ItemTransactionsContext woItem = new ItemTransactionsContext();
								woItem = setWorkorderItemObj(pItem, itemTransaction.getQuantity(), item,
										itemTransaction, itemType);
								itemTransactiosnToBeAdded.add(woItem);
							} else {
								for (PurchasedItemContext purchaseitem : purchasedItem) {
									ItemTransactionsContext woItem = new ItemTransactionsContext();
									double quantityUsedForTheCost = 0;
									if (purchaseitem.getCurrentQuantity() + requiredQuantity >= 0) {
										quantityUsedForTheCost = requiredQuantity;
									} else {
										quantityUsedForTheCost = -(purchaseitem.getCurrentQuantity());
									}
									woItem = setWorkorderItemObj(purchaseitem, -(quantityUsedForTheCost), item,
											itemTransaction, itemType);
									requiredQuantity -= quantityUsedForTheCost;
									itemTransactiosnToBeAdded.add(woItem);
									if (requiredQuantity == 0) {
										break;
									}
								}
							}
						}
					}
					
				}
				else if(itemTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_INCREASE
						&& !itemType.isRotating())
				{
						PurchasedItemContext pi =itemTransaction.getPurchasedItem();
						pi.setItem(item);
						pi.setItemType(itemType);
						pi.setCostDate(System.currentTimeMillis());
						itemType.setLastPurchasedDate(pi.getCostDate());
						item.setLastPurchasedDate(pi.getCostDate());
						itemType.setLastPurchasedPrice(pi.getUnitcost());
						item.setLastPurchasedPrice(pi.getUnitcost());
						addPurchasedItem(purchasedItemModule,purchasedItemFields,pi);
						ItemTransactionsContext woItem = new ItemTransactionsContext();
						woItem = setWorkorderItemObj(itemTransaction.getPurchasedItem(), itemTransaction.getQuantity(), item,
								itemTransaction, itemType);
						itemTransactiosnToBeAdded.add(woItem);
				}

			}
			InsertRecordBuilder<ItemTransactionsContext> readingBuilder = new InsertRecordBuilder<ItemTransactionsContext>()
					.module(itemTransactionsModule).fields(itemTransactionsFields).addRecords(itemTransactiosnToBeAdded);
			readingBuilder.save();
			
			context.put(FacilioConstants.ContextNames.PARENT_ID, itemTransactions.get(0).getParentId());
			context.put(FacilioConstants.ContextNames.ITEM_ID, itemTransactions.get(0).getItem().getId());
			context.put(FacilioConstants.ContextNames.ITEM_IDS,
					Collections.singletonList(itemTransactions.get(0).getItem().getId()));
			context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTransactiosnToBeAdded);
			context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemTypeId);
			context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, Collections.singletonList(itemTypeId));
			context.put(FacilioConstants.ContextNames.TRANSACTION_STATE,
					itemTransactions.get(0).getTransactionStateEnum());
		}
		return false;
	}
	
	private ItemTransactionsContext setWorkorderItemObj(PurchasedItemContext purchasedItem, double quantity,
			ItemContext item, ItemTransactionsContext itemTransactions, ItemTypesContext itemTypes){
		ItemTransactionsContext woItem = new ItemTransactionsContext();
		woItem.setTransactionState(itemTransactions.getTransactionStateEnum());
		woItem.setIsReturnable(false);
		if (purchasedItem != null) {
			woItem.setPurchasedItem(purchasedItem);
		}
		woItem.setQuantity(quantity);
		woItem.setTransactionType(TransactionType.STOCK.getValue());
		woItem.setItem(item);
		woItem.setStoreRoom(item.getStoreRoom());
		woItem.setItemType(itemTypes);
		woItem.setSysModifiedTime(System.currentTimeMillis());
		woItem.setParentId(purchasedItem.getId());
		woItem.setParentTransactionId(itemTransactions.getParentTransactionId());
		woItem.setApprovedState(1);
		woItem.setRemainingQuantity(0);
		return woItem;
	}
	
	private void addPurchasedItem(FacilioModule module, List<FacilioField> fields, PurchasedItemContext parts)
			throws Exception {
		InsertRecordBuilder<PurchasedItemContext> readingBuilder = new InsertRecordBuilder<PurchasedItemContext>()
				.module(module).fields(fields).addRecord(parts);
		readingBuilder.save();
	}

	public static List<PurchasedItemContext> getPurchasedItemList(long id, String orderByType, FacilioModule module,
			List<FacilioField> fields) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<PurchasedItemContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(PurchasedItemContext.class)
				.andCondition(
						CriteriaAPI.getCondition(fieldMap.get("item"), String.valueOf(id), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("currentQuantity"), String.valueOf(0),
						NumberOperators.GREATER_THAN))
				.orderBy(fieldMap.get("costDate").getColumnName() + orderByType);

		List<PurchasedItemContext> purchasedItemlist = selectBuilder.get();

		if (purchasedItemlist != null && !purchasedItemlist.isEmpty()) {
			return purchasedItemlist;
		}
		return null;
	}
}
