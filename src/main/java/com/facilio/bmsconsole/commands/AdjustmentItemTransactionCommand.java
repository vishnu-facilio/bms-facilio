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
		
		List<ItemTransactionsContext> itemTransactionsList = new ArrayList<>();
		List<ItemTransactionsContext> itemTransactiosnToBeAdded = new ArrayList<>();
		long itemTypeId = -1;
		ApprovalState approvalState = null;
		if (itemTransactions != null && !itemTransactions.isEmpty()) {
			for (ItemTransactionsContext itemTransaction : itemTransactions) {
				ItemContext item = getItem(itemTransaction.getItem().getId());
				itemTypeId = item.getItemType().getId();
				ItemTypesContext itemType = getItemType(itemTypeId);
				StoreRoomContext storeRoom = item.getStoreRoom();
				long parentId = itemTransaction.getParentId();
				if (itemTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_DECREASE
						&& itemType.isRotating()) {
					throw new IllegalArgumentException("Not Applicable for Rotating Items!");
				} 
				else if (itemTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_DECREASE
						&& item.getQuantity() < itemTransaction.getQuantity()) {
					throw new IllegalArgumentException("Insufficient quantity in inventory!");
				}
				else if(itemTransaction.getTransactionStateEnum() == TransactionState.ADJUSTMENT_DECREASE
						&& !itemType.isRotating())
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
							woItem = setWorkorderItemObj(pItem, itemTransaction.getQuantity(), item, parentId,
									itemTransaction, itemType, approvalState, context);
							itemTransactionsList.add(woItem);
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
										parentId, itemTransaction, itemType, approvalState, context);
								requiredQuantity -= quantityUsedForTheCost;
								itemTransactionsList.add(woItem);
								itemTransactiosnToBeAdded.add(woItem);
								if (requiredQuantity == 0) {
									break;
								}
							}
						}
					}
					
				}
			}
			InsertRecordBuilder<ItemTransactionsContext> readingBuilder = new InsertRecordBuilder<ItemTransactionsContext>()
					.module(itemTransactionsModule).fields(itemTransactionsFields).addRecords(itemTransactiosnToBeAdded);
			readingBuilder.save();
			
			context.put(FacilioConstants.ContextNames.PARENT_ID, itemTransactions.get(0).getParentId());
			context.put(FacilioConstants.ContextNames.ITEM_ID, itemTransactions.get(0).getItem().getId());
			context.put(FacilioConstants.ContextNames.ITEM_IDS,
					Collections.singletonList(itemTransactions.get(0).getItem().getId()));
			context.put(FacilioConstants.ContextNames.RECORD_LIST, itemTransactionsList);
			context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemTypeId);
			context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, Collections.singletonList(itemTypeId));
			context.put(FacilioConstants.ContextNames.TRANSACTION_STATE,
					itemTransactions.get(0).getTransactionStateEnum());
		}
		return false;
	}
	
	private ItemTransactionsContext setWorkorderItemObj(PurchasedItemContext purchasedItem, double quantity,
			ItemContext item, long parentId, ItemTransactionsContext itemTransactions, ItemTypesContext itemTypes,
			ApprovalState approvalState, Context context) throws Exception {
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
	
	
	
	public static ItemContext getItem(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<LookupField> lookUpfields = new ArrayList<>();
		lookUpfields.add((LookupField) fieldMap.get("storeRoom"));
		SelectRecordsBuilder<ItemContext> selectBuilder = new SelectRecordsBuilder<ItemContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ItemContext.class)
				.andCustomWhere(module.getTableName() + ".ID = ?", id).fetchSupplements(lookUpfields);

		List<ItemContext> inventories = selectBuilder.get();

		if (inventories != null && !inventories.isEmpty()) {
			return inventories.get(0);
		}
		return null;
	}

	public static ItemTypesContext getItemType(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule itemTypesModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
		List<FacilioField> itemTypesFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);

		SelectRecordsBuilder<ItemTypesContext> itemTypesselectBuilder = new SelectRecordsBuilder<ItemTypesContext>()
				.select(itemTypesFields).table(itemTypesModule.getTableName()).moduleName(itemTypesModule.getName())
				.beanClass(ItemTypesContext.class).andCondition(CriteriaAPI.getIdCondition(id, itemTypesModule));

		List<ItemTypesContext> itemTypes = itemTypesselectBuilder.get();
		if (itemTypes != null && !itemTypes.isEmpty()) {
			return itemTypes.get(0);
		}
		return null;
	}

	private void updatePurchasedItem(AssetContext purchasedItem) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		UpdateRecordBuilder<AssetContext> updateBuilder = new UpdateRecordBuilder<AssetContext>().module(module)
				.fields(fields).andCondition(CriteriaAPI.getIdCondition(purchasedItem.getId(), module));
		updateBuilder.update(purchasedItem);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

	}

	public static PurchasedItemContext getInventoryCost(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);

		SelectRecordsBuilder<PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<PurchasedItemContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(PurchasedItemContext.class).andCustomWhere(module.getTableName() + ".ITEM_ID = ?", id);

		List<PurchasedItemContext> inventoryCosts = selectBuilder.get();

		if (inventoryCosts != null && !inventoryCosts.isEmpty()) {
			return inventoryCosts.get(0);
		}
		return null;
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

	public static List<AssetContext> getPurchasedItemsListFromId(List<Long> id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(AssetContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));

		List<AssetContext> purchasedItemlist = selectBuilder.get();

		if (purchasedItemlist != null && !purchasedItemlist.isEmpty()) {
			return purchasedItemlist;
		}
		return null;
	}
}
