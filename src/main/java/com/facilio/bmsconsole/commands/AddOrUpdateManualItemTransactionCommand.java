package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdateManualItemTransactionCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
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
		if (itemTransactions != null) {
			long parentId = itemTransactions.get(0).getParentId();
			for (ItemTransactionsContext itemTransaction : itemTransactions) {
				ItemContext item = getItem(itemTransaction.getItem().getId());
				itemTypeId = item.getItemType().getId();
				ItemTypesContext itemType = getItemType(itemTypeId);
				StoreRoomContext storeRoom = item.getStoreRoom();
				if (itemTransaction.getId() > 0) {
					SelectRecordsBuilder<ItemTransactionsContext> selectBuilder = new SelectRecordsBuilder<ItemTransactionsContext>()
							.select(itemTransactionsFields).table(itemTransactionsModule.getTableName())
							.moduleName(itemTransactionsModule.getName()).beanClass(ItemTransactionsContext.class)
							.andCondition(CriteriaAPI.getIdCondition(itemTransaction.getId(), itemTransactionsModule));
					List<ItemTransactionsContext> woIt = selectBuilder.get();
					if (woIt != null) {
						ItemTransactionsContext wItem = woIt.get(0);
						SelectRecordsBuilder<PurchasedItemContext> purchasedItemSelectBuilder = new SelectRecordsBuilder<PurchasedItemContext>()
								.select(purchasedItemFields).table(purchasedItemModule.getTableName())
								.moduleName(purchasedItemModule.getName()).beanClass(PurchasedItemContext.class)
								.andCondition(CriteriaAPI.getIdCondition(wItem.getPurchasedItem().getId(),
										purchasedItemModule));
						List<PurchasedItemContext> purchasedItemsList = purchasedItemSelectBuilder.get();
						if (purchasedItemsList != null && !purchasedItemsList.isEmpty()) {
							PurchasedItemContext purchaseditem = purchasedItemsList.get(0);
							double q = wItem.getQuantity();
							if (itemTransaction.getTransactionStateEnum() == TransactionState.ISSUE
									&& (q + purchaseditem.getCurrentQuantity() < itemTransaction.getQuantity())) {
								throw new IllegalArgumentException("Insufficient quantity in inventory!");
							} else {
								ApprovalState approvalState = ApprovalState.YET_TO_BE_REQUESTED;
								if (itemType.isApprovalNeeded() || storeRoom.isApprovalNeeded()) {
									approvalState = ApprovalState.REQUESTED;
								}
								if (itemType.individualTracking()) {
									wItem = setWorkorderItemObj(purchaseditem, 1, item, parentId, itemTransaction,
											itemType, approvalState);
								} else {
									wItem = setWorkorderItemObj(purchaseditem, itemTransaction.getQuantity(), item,
											parentId, itemTransaction, itemType, approvalState);
								}
								updatePurchasedItem(purchaseditem);
								wItem.setId(itemTransaction.getId());
								itemTransactionsList.add(wItem);
								updateWorkorderItems(itemTransactionsModule, itemTransactionsFields, wItem);
							}
						}
					}
				} else {
					if (itemTransaction.getTransactionStateEnum() == TransactionState.ISSUE
							&& item.getQuantity() < itemTransaction.getQuantity()) {
						throw new IllegalArgumentException("Insufficient quantity in inventory!");
					} else {
						ApprovalState approvalState = ApprovalState.YET_TO_BE_REQUESTED;
						if (itemType.isApprovalNeeded() || storeRoom.isApprovalNeeded()) {
							approvalState = ApprovalState.REQUESTED;
						}
						if (itemType.individualTracking()) {
							List<Long> PurchasedItemsIds = itemTransaction.getPurchasedItems();
							List<PurchasedItemContext> purchasedItem = getPurchasedItemsListFromId(PurchasedItemsIds,
									purchasedItemModule, purchasedItemFields);
							if (purchasedItem != null) {
								for (PurchasedItemContext pItem : purchasedItem) {
									if (itemTransaction.getTransactionStateEnum() == TransactionState.ISSUE
											&& pItem.isUsed()) {
										throw new IllegalArgumentException("Insufficient quantity in inventory!");
									}
									ItemTransactionsContext woItem = new ItemTransactionsContext();
									if (itemTransaction.getTransactionStateEnum() == TransactionState.ISSUE
											&& (itemType.isApprovalNeeded() || storeRoom.isApprovalNeeded())) {
										pItem.setIsUsed(false);
									} else {
										if ((itemTransaction.getApprovedStateEnum() == ApprovalState.APPROVED
												|| itemTransaction.getApprovedStateEnum() == ApprovalState.YET_TO_BE_REQUESTED)
												&& itemTransaction
														.getTransactionStateEnum() == TransactionState.RETURN) {
											pItem.setIsUsed(false);
										} else if (itemTransaction
												.getTransactionStateEnum() == TransactionState.ISSUE) {
											pItem.setIsUsed(true);
										}
									}
									woItem = setWorkorderItemObj(pItem, 1, item, parentId, itemTransaction, itemType,
											approvalState);
									updatePurchasedItem(pItem);
									itemTransactionsList.add(woItem);
									itemTransactiosnToBeAdded.add(woItem);
								}
							}
						} else {
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
								if (itemTransaction.getQuantity() <= pItem.getCurrentQuantity()) {
									ItemTransactionsContext woItem = new ItemTransactionsContext();
									woItem = setWorkorderItemObj(pItem, itemTransaction.getQuantity(), item, parentId,
											itemTransaction, itemType, approvalState);
									itemTransactionsList.add(woItem);
									itemTransactiosnToBeAdded.add(woItem);
								} else {
									double requiredQuantity = itemTransaction.getQuantity();
									for (PurchasedItemContext purchaseitem : purchasedItem) {
										ItemTransactionsContext woItem = new ItemTransactionsContext();
										double quantityUsedForTheCost = 0;
										if (requiredQuantity <= purchaseitem.getCurrentQuantity()) {
											quantityUsedForTheCost = requiredQuantity;
										} else {
											quantityUsedForTheCost = purchaseitem.getCurrentQuantity();
										}
										woItem = setWorkorderItemObj(purchaseitem, quantityUsedForTheCost, item,
												parentId, itemTransaction, itemType, approvalState);
										requiredQuantity -= quantityUsedForTheCost;
										itemTransactionsList.add(woItem);
										itemTransactiosnToBeAdded.add(woItem);
										if (requiredQuantity <= 0) {
											break;
										}
									}
								}
							}
						}
					}
				}

			}

			if (itemTransactiosnToBeAdded != null && !itemTransactiosnToBeAdded.isEmpty()) {
				addWorkorderParts(itemTransactionsModule, itemTransactionsFields, itemTransactiosnToBeAdded);
			}
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
			ApprovalState approvalState) {
		ItemTransactionsContext woItem = new ItemTransactionsContext();
		woItem.setTransactionType(TransactionType.MANUAL);
		woItem.setTransactionState(itemTransactions.getTransactionStateEnum());
		woItem.setIsReturnable(true);
		woItem.setPurchasedItem(purchasedItem);
		woItem.setQuantity(quantity);
		woItem.setItem(item);
		woItem.setItemType(itemTypes);
		woItem.setSysModifiedTime(System.currentTimeMillis());
		woItem.setParentId(parentId);
		woItem.setParentTransactionId(itemTransactions.getParentTransactionId());
		woItem.setApprovedState(approvalState);
		if (approvalState == ApprovalState.YET_TO_BE_REQUESTED) {
			if (itemTransactions.getTransactionStateEnum() == TransactionState.ISSUE) {
				woItem.setRemainingQuantity(quantity);
			}
		} else {
			woItem.setRemainingQuantity(0);
		}

		return woItem;
	}

	private void addWorkorderParts(FacilioModule module, List<FacilioField> fields, List<ItemTransactionsContext> parts)
			throws Exception {
		InsertRecordBuilder<ItemTransactionsContext> readingBuilder = new InsertRecordBuilder<ItemTransactionsContext>()
				.module(module).fields(fields).addRecords(parts);
		readingBuilder.save();
	}

	private void updateWorkorderItems(FacilioModule module, List<FacilioField> fields, ItemTransactionsContext item)
			throws Exception {

		UpdateRecordBuilder<ItemTransactionsContext> updateBuilder = new UpdateRecordBuilder<ItemTransactionsContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(item.getId(), module));
		updateBuilder.update(item);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

	}

	public static ItemContext getItem(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<LookupFieldMeta> lookUpfields = new ArrayList<>();
		lookUpfields.add(new LookupFieldMeta((LookupField) fieldMap.get("storeRoom")));
		SelectRecordsBuilder<ItemContext> selectBuilder = new SelectRecordsBuilder<ItemContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ItemContext.class)
				.andCustomWhere(module.getTableName() + ".ID = ?", id).fetchLookups(lookUpfields);

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

	private void updatePurchasedItem(PurchasedItemContext purchasedItem) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
		UpdateRecordBuilder<PurchasedItemContext> updateBuilder = new UpdateRecordBuilder<PurchasedItemContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(purchasedItem.getId(), module));
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

	public static List<PurchasedItemContext> getPurchasedItemsListFromId(List<Long> id, FacilioModule module,
			List<FacilioField> fields) throws Exception {
		SelectRecordsBuilder<PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<PurchasedItemContext>()
				.select(fields).table(module.getTableName()).moduleName(module.getName())
				.beanClass(PurchasedItemContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));

		List<PurchasedItemContext> purchasedItemlist = selectBuilder.get();

		if (purchasedItemlist != null && !purchasedItemlist.isEmpty()) {
			return purchasedItemlist;
		}
		return null;
	}
}
