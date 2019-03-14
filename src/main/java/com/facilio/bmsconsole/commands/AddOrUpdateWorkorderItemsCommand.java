package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.WorkorderItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

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

		List<WorkorderItemContext> workorderitems = (List<WorkorderItemContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);
		List<WorkorderItemContext> workorderItemslist = new ArrayList<>();
		List<WorkorderItemContext> itemToBeAdded = new ArrayList<>();
		long itemTypesId = -1;
		if (workorderitems != null) {
			long parentId = workorderitems.get(0).getParentId();
			for (WorkorderItemContext workorderitem : workorderitems) {
				ItemContext item = getItem(workorderitem.getItem().getId());
				itemTypesId = item.getItemType().getId();
				ItemTypesContext itemType = getItemType(itemTypesId);
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
								if (itemType.individualTracking()) {
									wItem = setWorkorderItemObj(purchaseditem, 1, item, parentId);
								} else {
									wItem = setWorkorderItemObj(purchaseditem, workorderitem.getQuantity(), item, parentId);
								}
								updatePurchasedItem(purchaseditem);
								wItem.setId(workorderitem.getId());
								workorderItemslist.add(wItem);
								updateWorkorderItems(workorderItemsModule, workorderItemFields, wItem);
							}
						}
					}
				} else {
					if (item.getQuantity() < workorderitem.getQuantity()) {
						throw new IllegalArgumentException("Insufficient quantity in inventory!");
					} else {
						if (itemType.individualTracking()) {
							List<Long> PurchasedItemsIds = (List<Long>) context
									.get(FacilioConstants.ContextNames.PURCHASED_ITEM);
							List<PurchasedItemContext> purchasedItem = getPurchasedItemsListFromId(PurchasedItemsIds,
									purchasedItemModule, purchasedItemFields);
							if (purchasedItem != null) {
								for (PurchasedItemContext pItem : purchasedItem) {
									WorkorderItemContext woItem = new WorkorderItemContext();
									pItem.setIsUsed(true);
									woItem = setWorkorderItemObj(pItem, 1, item, parentId);
									updatePurchasedItem(pItem);
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
								if (workorderitem.getQuantity() <= pItem.getCurrentQuantity()) {
									WorkorderItemContext woItem = new WorkorderItemContext();
									woItem = setWorkorderItemObj(pItem, workorderitem.getQuantity(), item, parentId);
									workorderItemslist.add(woItem);
									itemToBeAdded.add(woItem);
								} else {
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
												parentId);
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

			if (itemToBeAdded != null && !itemToBeAdded.isEmpty()) {
				addWorkorderParts(workorderItemsModule, workorderItemFields, itemToBeAdded);
			}
			context.put(FacilioConstants.ContextNames.PARENT_ID, workorderitems.get(0).getParentId());
			context.put(FacilioConstants.ContextNames.ITEM_ID, workorderitems.get(0).getItem().getId());
			context.put(FacilioConstants.ContextNames.ITEM_IDS,
					Collections.singletonList(workorderitems.get(0).getItem().getId()));
			context.put(FacilioConstants.ContextNames.RECORD_LIST, workorderItemslist);
			context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 1);
			context.put(FacilioConstants.ContextNames.ITEM_TYPES_ID, itemTypesId);
		}
		return false;
	}

	private WorkorderItemContext setWorkorderItemObj(PurchasedItemContext purchasedItem, double quantity,
			ItemContext item, long parentId) {
		WorkorderItemContext woItem = new WorkorderItemContext();
		woItem.setTransactionType(TransactionType.WORKORDER);
		woItem.setTransactionState(TransactionState.ISSUE);
		woItem.setIsReturnable(false);
		woItem.setPurchasedItem(purchasedItem);
		woItem.setQuantity(quantity);
		woItem.setItem(item);
		woItem.setItemType(item.getItemType());
		woItem.setSysModifiedTime(System.currentTimeMillis());
		woItem.setParentId(parentId);
		double costOccured = 0;
		if (purchasedItem.getUnitcost() >= 0) {
			costOccured = purchasedItem.getUnitcost() * quantity;
		}
		woItem.setCost(costOccured);
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

		SelectRecordsBuilder<ItemContext> selectBuilder = new SelectRecordsBuilder<ItemContext>().select(fields)
				.table(module.getTableName()).moduleName(module.getName()).beanClass(ItemContext.class)
				.andCustomWhere(module.getTableName() + ".ID = ?", id);

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
