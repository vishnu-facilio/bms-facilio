package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.ItemTypesVendorsContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext.Status;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypeVendorContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

;

public class PurchaseOrderAutoCompleteCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<V3PurchaseOrderContext> purchaseOrders = (List<V3PurchaseOrderContext>) context
				.get(FacilioConstants.ContextNames.PURCHASE_ORDERS);
		List<ItemContext> itemsTobeAdded = new ArrayList<>();
		List<ToolContext> toolsToBeAdded = new ArrayList<>();
		boolean containsIndividualTrackingItem = false;
		boolean containsIndividualTrackingTool = false;
		List<ItemTypesVendorsContext> itemTypesVendors = new ArrayList<>();	
		List<ToolTypeVendorContext> toolTypesVendors = new ArrayList<>();

		long storeRoomId = -1;
		long vendorId =-1;
		if (purchaseOrders != null && !purchaseOrders.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule pomodule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);
			for (V3PurchaseOrderContext v3po : purchaseOrders) {
				Map<String, Object> map = FieldUtil.getAsProperties(v3po);
				PurchaseOrderContext po = FieldUtil.getAsBeanFromMap(map, PurchaseOrderContext.class);
				if (po.getStatusEnum() == Status.RECEIVED) {
					storeRoomId = po.getStoreRoom().getId();
					vendorId= po.getVendor().getId();
					List<PurchaseOrderLineItemContext> lineItems = getLineItemsForPO(po.getId());
					if (lineItems != null && !lineItems.isEmpty()) {
						for (PurchaseOrderLineItemContext lineItem : lineItems) {
							if (lineItem.getInventoryTypeEnum() == InventoryType.ITEM) {
								itemTypesVendors.add(new ItemTypesVendorsContext(lineItem.getItemType(), po.getVendor(), lineItem.getCost(), po.getOrderedTime()));
								ItemTypesContext itemtype = getItemType(lineItem.getItemType().getId());
								if (itemtype.isRotating()) {
									containsIndividualTrackingItem = true;
									break;
								} else {
									itemsTobeAdded.add(createItem(po, lineItem));
								}
							} else if (lineItem.getInventoryTypeEnum() == InventoryType.TOOL) {
								toolTypesVendors.add(new ToolTypeVendorContext(lineItem.getToolType(), po.getVendor(), lineItem.getCost(), po.getOrderedTime()));
								ToolTypesContext toolType = getToolType(lineItem.getToolType().getId());
								if (toolType.isRotating()) {
									containsIndividualTrackingTool = true;
									break;
								} else {
									toolsToBeAdded.add(createTool(po, lineItem));
								}
							}
						}
					}
				}
				if (!containsIndividualTrackingItem && !containsIndividualTrackingTool) {
					po.setStatus(Status.COMPLETED);
					po.setCompletedTime(System.currentTimeMillis());
					UpdateRecordBuilder<PurchaseOrderContext> updateBuilder = new UpdateRecordBuilder<PurchaseOrderContext>()
							.module(pomodule).fields(modBean.getAllFields(pomodule.getName()))
							.andCondition(CriteriaAPI.getIdCondition(po.getId(), pomodule));
					updateBuilder.update(po);
				}
			}
			if (containsIndividualTrackingItem || containsIndividualTrackingTool) {
				itemsTobeAdded = null;
				toolsToBeAdded = null;
			}
		}
		context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoomId);
		context.put(FacilioConstants.ContextNames.VENDOR_ID, vendorId);
		context.put(FacilioConstants.ContextNames.ITEM_VENDORS_LIST, itemTypesVendors);
		context.put(FacilioConstants.ContextNames.TOOL_VENDORS_LIST, toolTypesVendors);

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

	private ItemContext createItem(PurchaseOrderContext po, PurchaseOrderLineItemContext lineItem) {
		ItemContext item = new ItemContext();
		item.setStoreRoom(po.getStoreRoom());
		item.setItemType(lineItem.getItemType());
		PurchasedItemContext purchasedItem = new PurchasedItemContext();
		purchasedItem.setQuantity(lineItem.getQuantity());
		purchasedItem.setUnitcost(lineItem.getUnitPrice());
		item.setPurchasedItems(Collections.singletonList(purchasedItem));

		return item;
	}

	private ToolContext createTool(PurchaseOrderContext po, PurchaseOrderLineItemContext lineItem) {
		ToolContext tool = new ToolContext();
		tool.setStoreRoom(po.getStoreRoom());
		tool.setToolType(lineItem.getToolType());
		tool.setQuantity(lineItem.getQuantity());
		tool.setRate(lineItem.getCost());

		return tool;
	}
}
