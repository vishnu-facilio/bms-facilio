package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.ItemActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.util.InventoryRequestAPI;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddOrUpdateWorkorderItemsCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderItemsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_ITEMS);
		List<FacilioField> workorderItemFields = modBean.getAllFields(FacilioConstants.ContextNames.WORKORDER_ITEMS);

		FacilioModule purchasedItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
		List<FacilioField> purchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
		
		FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> assetFields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);

		List<WorkorderItemContext> workorderitems = (List<WorkorderItemContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<WorkorderItemContext> workorderItemslist = new ArrayList<>();
		List<WorkorderItemContext> itemToBeAdded = new ArrayList<>();
		List<Object> woitemactivity = new ArrayList<>();

		long itemTypesId = -1;
		ApprovalState approvalState = null;
		if (workorderitems != null) {
			long parentId = workorderitems.get(0).getParentId();
			for (WorkorderItemContext workorderitem : workorderitems) {
				long parentTransactionId = workorderitem.getParentTransactionId();
				
				ItemContext item = getItem(workorderitem.getItem().getId());
				itemTypesId = item.getItemType().getId();
				ItemTypesContext itemType = getItemType(itemTypesId);
				StoreRoomContext storeRoom = item.getStoreRoom();
				WorkOrderContext wo = getWorkorderContext(parentId);
				if (workorderitem.getRequestedLineItem() != null && workorderitem.getRequestedLineItem().getId() > 0) {
					if(!InventoryRequestAPI.checkQuantityForWoItemNeedingApproval(itemType, workorderitem.getRequestedLineItem(), workorderitem.getQuantity())) {
						throw new IllegalArgumentException("Please check the quantity approved/issued in the request");
					}
				}
				else if(workorderitem.getParentTransactionId() > 0) {
					if(!InventoryRequestAPI.checkQuantityForWoItem(workorderitem.getParentTransactionId(), workorderitem.getQuantity())){
						throw new IllegalArgumentException("Please check the quantity issued");
					}
				}
				if (workorderitem.getId() > 0) {
					SelectRecordsBuilder<WorkorderItemContext> selectBuilder = new SelectRecordsBuilder<WorkorderItemContext>()
							.select(workorderItemFields).table(workorderItemsModule.getTableName())
							.moduleName(workorderItemsModule.getName()).beanClass(WorkorderItemContext.class)
							.andCondition(CriteriaAPI.getIdCondition(workorderitem.getId(), workorderItemsModule));
					List<WorkorderItemContext> woIt = selectBuilder.get();
					if (woIt != null) {
						WorkorderItemContext wItem = woIt.get(0);
						SelectRecordsBuilder<PurchasedItemContext> purchasedItemSelectBuilder = new SelectRecordsBuilder<PurchasedItemContext>()
								.select(purchasedItemFields).table(purchasedItemModule.getTableName())
								.moduleName(purchasedItemModule.getName()).beanClass(PurchasedItemContext.class)
								.andCondition(CriteriaAPI.getIdCondition(wItem.getPurchasedItem().getId(),
										purchasedItemModule));
						List<PurchasedItemContext> purchasedItemsList = purchasedItemSelectBuilder.get();
						if (purchasedItemsList != null && !purchasedItemsList.isEmpty()) {
							PurchasedItemContext purchaseditem = purchasedItemsList.get(0);
							double q = wItem.getQuantity();
							if ((q + purchaseditem.getCurrentQuantity() < workorderitem.getQuantity())) {
								throw new IllegalArgumentException("Insufficient quantity in inventory!");
							} else {
								approvalState = ApprovalState.YET_TO_BE_REQUESTED;
								if (workorderitem.getRequestedLineItem() != null && workorderitem.getRequestedLineItem().getId() > 0) {
									approvalState = ApprovalState.APPROVED;
								}
								JSONObject info = new JSONObject();
								info.put("itemid", workorderitem.getItem().getId());
								info.put("itemtype", itemType.getName());
								info.put("quantity", workorderitem.getQuantity());
								info.put("issuedToId", workorderitem.getParentId());
								if (itemType.isRotating()) {
									woitemactivity.add(info);
									wItem = setWorkorderItemObj(purchaseditem, 1, item, parentId, approvalState, wo, workorderitem.getAsset(), workorderitem.getRequestedLineItem(), parentTransactionId);

								} else {
									woitemactivity.add(info);
									wItem = setWorkorderItemObj(purchaseditem, workorderitem.getQuantity(), item,
											parentId, approvalState, wo, null, workorderitem.getRequestedLineItem(), parentTransactionId);
								}
								// updatePurchasedItem(purchaseditem);
								wItem.setId(workorderitem.getId());
								workorderItemslist.add(wItem);
								updateWorkorderItems(workorderItemsModule, workorderItemFields, wItem);
							}
						}
					}
				} else {
					if (workorderitem.getRequestedLineItem() == null && workorderitem.getParentTransactionId() <= 0 && item.getQuantity() < workorderitem.getQuantity()) {
						throw new IllegalArgumentException("Insufficient quantity in inventory!");
					} else {
						approvalState = ApprovalState.YET_TO_BE_REQUESTED;
						if (workorderitem.getRequestedLineItem() != null && workorderitem.getRequestedLineItem().getId() > 0) {
							approvalState = ApprovalState.APPROVED;
						}
						if (itemType.isRotating()) {
							List<Long> assetIds = workorderitem.getAssetIds();
							List<AssetContext> purchasedItem = getAssetsFromId(assetIds, assetModule, assetFields);
							if (purchasedItem != null) {
								for (AssetContext asset : purchasedItem) {
									if (workorderitem.getRequestedLineItem() == null && workorderitem.getParentTransactionId() <= 0 && asset.isUsed()) {
										throw new IllegalArgumentException("Insufficient quantity in inventory!");
									}
									JSONObject info = new JSONObject();
									info.put("itemid", workorderitem.getItem().getId());
									info.put("itemtype", itemType.getName());
									info.put("quantity", workorderitem.getQuantity());
									info.put("issuedToId", workorderitem.getParentId());
									info.put("serialno", asset.getSerialNumber());
									WorkorderItemContext woItem = new WorkorderItemContext();
									asset.setIsUsed(true);
									woitemactivity.add(info);
									woItem = setWorkorderItemObj(null, 1, item, parentId, approvalState, wo, asset, workorderitem.getRequestedLineItem(), parentTransactionId);
									updatePurchasedItem(asset);
									workorderItemslist.add(woItem);
									itemToBeAdded.add(woItem);
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
								JSONObject info = new JSONObject();
								info.put("itemid", workorderitem.getItem().getId());
								info.put("itemtype", itemType.getName());
								info.put("quantity", workorderitem.getQuantity());
								info.put("issuedToId", workorderitem.getParentId());
								if (workorderitem.getQuantity() <= pItem.getCurrentQuantity()) {
									WorkorderItemContext woItem = new WorkorderItemContext();
									woitemactivity.add(info);
									woItem = setWorkorderItemObj(pItem, workorderitem.getQuantity(), item, parentId,
											approvalState, wo, null, workorderitem.getRequestedLineItem(), parentTransactionId);
									workorderItemslist.add(woItem);
									itemToBeAdded.add(woItem);
								} else {
									woitemactivity.add(info);
									double requiredQuantity = workorderitem.getQuantity();
									for (PurchasedItemContext purchaseitem : purchasedItem) {
										WorkorderItemContext woItem = new WorkorderItemContext();
										double quantityUsedForTheCost = 0;
										if (requiredQuantity <= purchaseitem.getCurrentQuantity()) {
											quantityUsedForTheCost = requiredQuantity;
										} else {
											quantityUsedForTheCost = purchaseitem.getCurrentQuantity();
										}
										woItem = setWorkorderItemObj(purchaseitem, quantityUsedForTheCost, item,
												parentId, approvalState, wo, null, workorderitem.getRequestedLineItem(), parentTransactionId);
										requiredQuantity -= quantityUsedForTheCost;
										workorderItemslist.add(woItem);
										itemToBeAdded.add(woItem);
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
			JSONObject newinfo = new JSONObject();
			if (!(approvalState == ApprovalState.REQUESTED)) {
				newinfo.put("issued", woitemactivity);
				CommonCommandUtil.addActivityToContext(itemTypesId, -1, ItemActivityType.ISSUED, newinfo,
						(FacilioContext) context);
			} else if (approvalState == ApprovalState.REQUESTED) {
				newinfo.put("requested", woitemactivity);
				CommonCommandUtil.addActivityToContext(itemTypesId, -1, ItemActivityType.REQUESTED, newinfo,
						(FacilioContext) context);
			}

			if (itemToBeAdded != null && !itemToBeAdded.isEmpty()) {
				addWorkorderParts(workorderItemsModule, workorderItemFields, itemToBeAdded);
			}
			context.put(FacilioConstants.ContextNames.PARENT_ID, workorderitems.get(0).getParentId());
			context.put(FacilioConstants.ContextNames.PARENT_ID_LIST,
					Collections.singletonList(workorderitems.get(0).getParentId()));
			context.put(FacilioConstants.ContextNames.ITEM_ID, workorderitems.get(0).getItem().getId());
			context.put(FacilioConstants.ContextNames.ITEM_IDS,
					Collections.singletonList(workorderitems.get(0).getItem().getId()));
			context.put(FacilioConstants.ContextNames.WO_ITEMS_LIST, workorderItemslist);
			context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderItemslist);
			context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 1);
			context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemTypesId);
			context.put(FacilioConstants.ContextNames.ITEM_TYPES_IDS, Collections.singletonList(itemTypesId));
		}
		return false;
	}

	private WorkorderItemContext setWorkorderItemObj(PurchasedItemContext purchasedItem, double quantity,
			ItemContext item, long parentId, ApprovalState approvalState, WorkOrderContext wo, AssetContext asset, InventoryRequestLineItemContext lineItem, long parentTransactionId) throws Exception{
		WorkorderItemContext woItem = new WorkorderItemContext();
		woItem.setTransactionType(TransactionType.WORKORDER);
		woItem.setIsReturnable(false);
		double costOccured = 0;
		if (purchasedItem != null) {
			woItem.setPurchasedItem(purchasedItem);
			if (purchasedItem.getUnitcost() >= 0) {
				costOccured = purchasedItem.getUnitcost() * quantity;
			}
		}
		woItem.setQuantity(quantity);
		woItem.setTransactionState(TransactionState.USE);
		if(parentTransactionId != -1) {
			woItem.setParentTransactionId(parentTransactionId);
		}
		
		if(lineItem != null) {
			woItem.setRequestedLineItem(lineItem);
			woItem.setParentTransactionId(ItemsApi.getItemTransactionsForRequestedLineItem(lineItem.getId()).getId());
		}
		
		
		
		woItem.setItem(item);
		woItem.setItemType(item.getItemType());
		woItem.setSysModifiedTime(System.currentTimeMillis());
		woItem.setParentId(parentId);
		woItem.setApprovedState(approvalState);
		if (asset != null) {
			woItem.setAsset(asset);
			if(asset.getUnitPrice() >= 0) {
				costOccured = asset.getUnitPrice() * quantity;
			}
		}
		woItem.setRemainingQuantity(quantity);
		
		woItem.setCost(costOccured);
		if (wo != null) {
			woItem.setWorkorder(wo);
			if (wo.getAssignedTo() != null) {
				woItem.setIssuedTo(wo.getAssignedTo());
			}
		}
		return woItem;
	}

	private void addWorkorderParts(FacilioModule module, List<FacilioField> fields, List<WorkorderItemContext> parts)
			throws Exception {
		InsertRecordBuilder<WorkorderItemContext> readingBuilder = new InsertRecordBuilder<WorkorderItemContext>()
				.module(module).fields(fields).addRecords(parts);
		readingBuilder.save();
	}

	private void updateWorkorderItems(FacilioModule module, List<FacilioField> fields, WorkorderItemContext item)
			throws Exception {

		UpdateRecordBuilder<WorkorderItemContext> updateBuilder = new UpdateRecordBuilder<WorkorderItemContext>()
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

	private void updatePurchasedItem(AssetContext asset) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		UpdateRecordBuilder<AssetContext> updateBuilder = new UpdateRecordBuilder<AssetContext>().module(module)
				.fields(fields).andCondition(CriteriaAPI.getIdCondition(asset.getId(), module));
		updateBuilder.update(asset);

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

	public static List<AssetContext> getAssetsFromId(List<Long> id, FacilioModule module, List<FacilioField> fields)
			throws Exception {
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(AssetContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module));

		List<AssetContext> purchasedItemlist = selectBuilder.get();

		if (purchasedItemlist != null && !purchasedItemlist.isEmpty()) {
			return purchasedItemlist;
		}
		return null;
	}

	private WorkOrderContext getWorkorderContext(long woId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule workorderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> workorderFields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
		SelectRecordsBuilder<WorkOrderContext> workorderSetlectBuilder = new SelectRecordsBuilder<WorkOrderContext>()
				.select(workorderFields).table(workorderModule.getTableName()).moduleName(workorderModule.getName())
				.beanClass(WorkOrderContext.class).andCondition(CriteriaAPI.getIdCondition(woId, workorderModule));

		List<WorkOrderContext> workorderList = workorderSetlectBuilder.get();
		if (workorderList != null && !workorderList.isEmpty()) {
			return workorderList.get(0);
		}
		return null;
	}
	
	

}
