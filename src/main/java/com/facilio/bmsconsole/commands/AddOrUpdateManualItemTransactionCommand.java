package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.context.ItemTransactionsContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.util.AssetsAPI;
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

;

public class AddOrUpdateManualItemTransactionCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule itemTransactionsModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		List<FacilioField> itemTransactionsFields = modBean
				.getAllFields(FacilioConstants.ContextNames.ITEM_TRANSACTIONS);
		ShipmentContext shipment = (ShipmentContext)context.get(FacilioConstants.ContextNames.SHIPMENT);


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
								approvalState = ApprovalState.YET_TO_BE_REQUESTED;
								if (itemTransaction.getRequestedLineItem() != null && itemTransaction.getRequestedLineItem().getId() > 0) {
									approvalState = ApprovalState.APPROVED;
								}
								if (itemType.isRotating()) {
									wItem = setWorkorderItemObj(purchaseditem, 1, item, parentId, itemTransaction,
											itemType, approvalState, wItem.getAsset(), shipment, context);
								} else {
									wItem = setWorkorderItemObj(purchaseditem, itemTransaction.getQuantity(), item,
											parentId, itemTransaction, itemType, approvalState, null, shipment, context);
								}
								//updatePurchasedItem(purchaseditem);
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
						approvalState = ApprovalState.YET_TO_BE_REQUESTED;
						if (itemTransaction.getRequestedLineItem() != null && itemTransaction.getRequestedLineItem().getId() > 0) {
							approvalState = ApprovalState.APPROVED;
						}
					
						if (itemType.isRotating()) {
							List<Long> assetIds = itemTransaction.getAssetIds();
							List<AssetContext> assets = getPurchasedItemsListFromId(assetIds);
							if (assets != null) {
								for (AssetContext asset : assets) {
									if (itemTransaction.getTransactionStateEnum() == TransactionState.ISSUE
											&& asset.isUsed()) {
										throw new IllegalArgumentException("Insufficient quantity in inventory!");
									}
									ItemTransactionsContext woItem = new ItemTransactionsContext();
									if (itemTransaction.getTransactionStateEnum() == TransactionState.RETURN) {
											asset.setIsUsed(false);
										} else if (itemTransaction
												.getTransactionStateEnum() == TransactionState.ISSUE) {
											asset.setIsUsed(true);
										}
								//	}
									woItem = setWorkorderItemObj(null, 1, item, parentId, itemTransaction, itemType,
											approvalState, asset, shipment, context);
									updatePurchasedItem(asset);
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
											itemTransaction, itemType, approvalState, null, shipment, context);
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
												parentId, itemTransaction, itemType, approvalState, null, shipment, context);
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
			ApprovalState approvalState, AssetContext asset, ShipmentContext shipment, Context context) throws Exception {
		ItemTransactionsContext woItem = new ItemTransactionsContext();
		if(itemTransactions.getRequestedLineItem() != null) {
			woItem.setRequestedLineItem(itemTransactions.getRequestedLineItem());
		}

		if(shipment == null) {
			woItem.setTransactionType(TransactionType.MANUAL.getValue());
		}
		else {
			woItem.setTransactionType(TransactionType.SHIPMENT_STOCK.getValue());
			woItem.setShipment(shipment.getId());
		}

		woItem.setTransactionState(itemTransactions.getTransactionStateEnum());
		woItem.setIsReturnable(true);
		if (purchasedItem != null) {
			woItem.setPurchasedItem(purchasedItem);
		}
		if (asset != null) {
			woItem.setAsset(asset);
		}
		woItem.setQuantity(quantity);
		if(itemTransactions.getResource() != null && itemTransactions.getResource().getResourceType() != ResourceType.USER.getValue()) {
			woItem.setTransactionCost(quantity* purchasedItem.getUnitcost());
		}
		woItem.setItem(item);
		woItem.setStoreRoom(item.getStoreRoom());
		woItem.setItemType(itemTypes);
		woItem.setSysModifiedTime(System.currentTimeMillis());
		woItem.setParentId(parentId);
		woItem.setParentTransactionId(itemTransactions.getParentTransactionId());
		woItem.setApprovedState(approvalState);
		if (itemTransactions.getTransactionStateEnum() == TransactionState.ISSUE) {
			if(itemTransactions.getTransactionType() == TransactionType.SHIPMENT_STOCK.getValue() || (itemTransactions.getTransactionType() == TransactionType.MANUAL.getValue() && (itemTransactions.getResource() == null || (itemTransactions.getResource() != null && itemTransactions.getResource().getResourceType() == ResourceType.USER.getValue()) ))) {
				woItem.setRemainingQuantity(quantity);
			} 
			else
			{
				woItem.setRemainingQuantity(0);
			}
		}

		if (itemTransactions.getTransactionStateEnum() == TransactionState.RETURN) {
			woItem.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
		}
		woItem.setIssuedTo(itemTransactions.getIssuedTo());
		JSONObject newinfo = new JSONObject();
		
		if (itemTypes.isRotating() && woItem.getTransactionStateEnum() == TransactionState.ISSUE) {
			
			asset.setLastIssuedToUser(woItem.getIssuedTo());
			if(woItem.getWorkorder() != null) {
				asset.setLastIssuedToWo(woItem.getWorkorder().getId());
			}
			asset.setLastIssuedTime(System.currentTimeMillis());
			AssetsAPI.updateAsset(asset, asset.getId());
			
			if(woItem.getTransactionTypeEnum() == TransactionType.MANUAL) {
				User user = AccountUtil.getUserBean().getUser(woItem.getParentId(), true);
				newinfo.put("issuedTo",user.getName());
					
			}
			CommonCommandUtil.addActivityToContext(asset.getId(), -1, AssetActivityType.ISSUE, newinfo,
					(FacilioContext) context);
					
		}
		else if(itemTypes.isRotating() && woItem.getTransactionStateEnum() == TransactionState.RETURN) {
			User user = new User();
			user.setId(-99);
			asset.setLastIssuedToUser(user);
			asset.setLastIssuedToWo(-99);
			asset.setLastIssuedTime(-99);
			AssetsAPI.updateAsset(asset, asset.getId());
			
			if(woItem.getTransactionTypeEnum() == TransactionType.MANUAL) {
				user = AccountUtil.getUserBean().getUser(woItem.getParentId(), true);
				newinfo.put("returnedBy", user.getName());
			}
			else if(woItem.getTransactionTypeEnum() == TransactionType.WORKORDER) {
				newinfo.put("returnedBy", "WO - #"+ woItem.getParentId());
			}
			CommonCommandUtil.addActivityToContext(asset.getId(), -1, AssetActivityType.RETURN, newinfo,
					(FacilioContext) context);
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
