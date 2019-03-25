package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext.Status;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class PurchaseOrderITCompleteCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<Long> purchaseOrdersIds = (List<Long>) context.get(FacilioConstants.ContextNames.PURCHASE_ORDERS);
		List<PurchaseOrderLineItemContext> lineItems = (List<PurchaseOrderLineItemContext>) context
				.get(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		List<ItemContext> itemsTobeAdded = new ArrayList<>();
		List<ToolContext> toolsToBeAdded = new ArrayList<>();
		boolean containsIndividualTrackingItem = false;
		boolean containsIndividualTrackingTool = false;
		long storeRoomId = -1;
		List<PurchaseOrderContext> purchaseOrders = getPurchaseOrderContext(purchaseOrdersIds);
		if (purchaseOrders != null && !purchaseOrders.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule pomodule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);
			for (PurchaseOrderContext po : purchaseOrders) {
				storeRoomId = po.getStoreRoom().getId();
					if (lineItems == null) {
						lineItems = getLineItemsForPO(po.getId());
					}

					if (lineItems != null && !lineItems.isEmpty()) {
						for (PurchaseOrderLineItemContext lineItem : lineItems) {
							if (lineItem.getInventoryTypeEnum() == InventoryType.ITEM) {
								ItemTypesContext itemtype = getItemType(lineItem.getItemType().getId());
								if (itemtype.individualTracking()) {
									containsIndividualTrackingItem = true;
								}
								else {
									containsIndividualTrackingItem = false;
								}
								 itemsTobeAdded.add(createItem(po, lineItem,
								 containsIndividualTrackingItem));
							} else if (lineItem.getInventoryTypeEnum() == InventoryType.TOOL) {
								ToolTypesContext toolType = getToolType(lineItem.getToolType().getId());
								if (toolType.individualTracking()) {
									containsIndividualTrackingTool = true;
								}
								else {
									containsIndividualTrackingTool = false;
								}
								toolsToBeAdded.add(createTool(po, lineItem, containsIndividualTrackingTool));
							}
						}
					}
					po.setStatus(Status.COMPLETED);
					
					UpdateRecordBuilder<PurchaseOrderContext> updateBuilder = new UpdateRecordBuilder<PurchaseOrderContext>()
							.module(pomodule).fields(modBean.getAllFields(pomodule.getName()))
							.andCondition(CriteriaAPI.getIdCondition(po.getId(), pomodule));
					updateBuilder.update(po);
			}
		}
		context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoomId);
		context.put(FacilioConstants.ContextNames.ITEMS, itemsTobeAdded);
		context.put(FacilioConstants.ContextNames.TOOLS, toolsToBeAdded);
		return false;
	}

	private List<PurchaseOrderLineItemContext> getLineItemsForPO(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<PurchaseOrderLineItemContext>()
				.module(module).select(fields)
				.andCondition(
						CriteriaAPI.getCondition(fieldsMap.get("poId"), String.valueOf(id), NumberOperators.EQUALS))
				.beanClass(PurchaseOrderLineItemContext.class);

		List<PurchaseOrderLineItemContext> lineItems = builder.get();
		if (lineItems != null && !lineItems.isEmpty()) {
			return lineItems;
		}
		return null;
	}

	private List<PurchaseOrderContext> getPurchaseOrderContext(List<Long> id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_ORDER);

		SelectRecordsBuilder<PurchaseOrderContext> builder = new SelectRecordsBuilder<PurchaseOrderContext>()
				.module(module).select(fields).andCondition(CriteriaAPI.getIdCondition(id, module))
				.beanClass(PurchaseOrderContext.class);

		List<PurchaseOrderContext> purchaseOrders = builder.get();
		if (purchaseOrders != null && !purchaseOrders.isEmpty()) {
			return purchaseOrders;
		}
		return null;
	}

	private ItemTypesContext getItemType(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);

		SelectRecordsBuilder<ItemTypesContext> builder = new SelectRecordsBuilder<ItemTypesContext>().module(module)
				.select(fields).andCondition(CriteriaAPI.getIdCondition(id, module)).beanClass(ItemTypesContext.class);

		List<ItemTypesContext> item = builder.get();
		if (item != null && !item.isEmpty()) {
			return item.get(0);
		}
		return null;
	}

	private ToolTypesContext getToolType(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);

		SelectRecordsBuilder<ToolTypesContext> builder = new SelectRecordsBuilder<ToolTypesContext>().module(module)
				.select(fields).andCondition(CriteriaAPI.getIdCondition(id, module)).beanClass(ToolTypesContext.class);

		List<ToolTypesContext> tool = builder.get();
		if (tool != null && !tool.isEmpty()) {
			return tool.get(0);
		}
		return null;
	}

	private ItemContext createItem(PurchaseOrderContext po, PurchaseOrderLineItemContext lineItem,
			boolean isIndividualTracking) {
		ItemContext item = new ItemContext();
		item.setStoreRoom(po.getStoreRoom());
		item.setItemType(lineItem.getItemType());
		item.setCostType(CostType.FIFO);
		if (isIndividualTracking) {
			item.setPurchasedItems(lineItem.getPurchasedItems());
		} else {
			PurchasedItemContext purchasedItem = new PurchasedItemContext();
			purchasedItem.setQuantity(lineItem.getQuantity());
			purchasedItem.setUnitcost(lineItem.getUnitPrice());
			item.setPurchasedItems(Collections.singletonList(purchasedItem));
		}
		return item;
	}

	private ToolContext createTool(PurchaseOrderContext po, PurchaseOrderLineItemContext lineItem,
			boolean isIndividualTracking) {
		ToolContext tool = new ToolContext();
		tool.setStoreRoom(po.getStoreRoom());
		tool.setToolType(lineItem.getToolType());
		tool.setQuantity(lineItem.getQuantity());
		tool.setRate(lineItem.getCost());
		if (isIndividualTracking) {
			tool.setPurchasedTools(lineItem.getPurchasedTools());
		}
		return tool;
	}
}
