package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext.Status;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderLineItemContext;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.ItemTypesVendorsContext;
import com.facilio.bmsconsole.context.PoLineItemsSerialNumberContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypeVendorContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class PurchaseOrderCompleteCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		V3PurchaseOrderContext purchaseOrder = (V3PurchaseOrderContext) context.get(FacilioConstants.ContextNames.PURCHASE_ORDERS);
		Map<String, Object> map = FieldUtil.getAsProperties(purchaseOrder);
		PurchaseOrderContext po = FieldUtil.getAsBeanFromMap(map, PurchaseOrderContext.class);
		if (purchaseOrder != null) {
			List<V3PurchaseOrderLineItemContext> lineItems = (List<V3PurchaseOrderLineItemContext>) context
					.get(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
			List<ItemContext> itemsTobeAdded = new ArrayList<>();
			List<ToolContext> toolsToBeAdded = new ArrayList<>();
			List<ItemTypesVendorsContext> itemTypesVendors = new ArrayList<>();
			List<ToolTypeVendorContext> toolTypesVendors = new ArrayList<>();
			
			boolean containsIndividualTrackingItem = false;
			boolean containsIndividualTrackingTool = false;
			long storeRoomId = -1;
			long vendorId = -1;
			if (purchaseOrder != null) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule pomodule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);
					if(purchaseOrder.getStoreRoom() != null) {
						storeRoomId = purchaseOrder.getStoreRoom().getId();
					}
					vendorId = purchaseOrder.getVendor().getId();
					if (lineItems == null) {
						lineItems = getLineItemsForPO(purchaseOrder.getId());
					}

					if (lineItems != null && !lineItems.isEmpty()) {
						for (V3PurchaseOrderLineItemContext lineItem : lineItems) {
							if (lineItem.getQuantityReceived() != null && lineItem.getQuantityReceived() > 0) {
								if (lineItem.getInventoryTypeEnum() == InventoryType.ITEM) {
									itemTypesVendors.add(new ItemTypesVendorsContext(lineItem.getItemType(),
											po.getVendor(), lineItem.getCost(), purchaseOrder.getOrderedTime()));
									ItemTypesContext itemtype = getItemType(lineItem.getItemType().getId());
									if (itemtype.isRotating()) {
										containsIndividualTrackingItem = true;
										ItemContext item = ItemsApi.getItemsForTypeAndStore(purchaseOrder.getStoreRoom().getId(), lineItem.getItemType().getId());
										long lastPurchasedDate = ItemsApi.getLastPurchasedItemDateForItemId(item.getId());
										ItemContext update_item = new ItemContext();
										update_item.setId(item.getId());
										update_item.setLastPurchasedDate(lastPurchasedDate);
										update_item.setLastPurchasedPrice(ItemsApi.getLastPurchasedItemPriceForItemId(item.getId()));
										ItemsApi.updateLastPurchasedDateForItem(update_item);
										ItemsApi.updateLastPurchasedDetailsForItemType(itemtype.getId());
									} else {
										containsIndividualTrackingItem = false;
										itemsTobeAdded.add(createItem(po, lineItem, containsIndividualTrackingItem));
									}									
								} else if (lineItem.getInventoryTypeEnum() == InventoryType.TOOL) {
									toolTypesVendors.add(new ToolTypeVendorContext(lineItem.getToolType(),
											po.getVendor(), lineItem.getCost(), purchaseOrder.getOrderedTime()));
									ToolTypesContext toolType = getToolType(lineItem.getToolType().getId());
									if (toolType.isRotating()) {
										containsIndividualTrackingTool = true;
										ToolContext tool = ToolsApi.getToolsForTypeAndStore(purchaseOrder.getStoreRoom().getId(), toolType.getId());
										long lastPurchasedDate= ToolsApi.getLastPurchasedToolDateForToolId(tool.getId());	
										ToolContext update_tool = new ToolContext();
										update_tool.setId(tool.getId());
										update_tool.setLastPurchasedDate(lastPurchasedDate);
										ToolsApi.updateLastPurchasedDateForTool(update_tool);
										ToolsApi.updatelastPurchaseddetailsInToolType(toolType.getId());
									} else {
										containsIndividualTrackingTool = false;
										toolsToBeAdded.add(createTool(po, lineItem, containsIndividualTrackingTool));
									}								
								}
							}
						}
					}
				purchaseOrder.setStatus(Status.COMPLETED);
				purchaseOrder.setCompletedTime(System.currentTimeMillis());
					UpdateRecordBuilder<V3PurchaseOrderContext> updateBuilder = new UpdateRecordBuilder<V3PurchaseOrderContext>()
							.module(pomodule).fields(modBean.getAllFields(pomodule.getName()))
							.andCondition(CriteriaAPI.getIdCondition(purchaseOrder.getId(), pomodule));
					updateBuilder.update(purchaseOrder);
			}
			context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoomId);
			context.put(FacilioConstants.ContextNames.VENDOR_ID, vendorId);
			context.put(FacilioConstants.ContextNames.ITEM_VENDORS_LIST, itemTypesVendors);
			context.put(FacilioConstants.ContextNames.TOOL_VENDORS_LIST, toolTypesVendors);
			context.put(FacilioConstants.ContextNames.ITEMS, itemsTobeAdded);
			context.put(FacilioConstants.ContextNames.TOOLS, toolsToBeAdded);
		}
		return false;
	}

	private List<V3PurchaseOrderLineItemContext> getLineItemsForPO(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<V3PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<V3PurchaseOrderLineItemContext>()
				.module(module).select(fields)
				.andCondition(
						CriteriaAPI.getCondition(fieldsMap.get("poId"), String.valueOf(id), NumberOperators.EQUALS))
				.beanClass(V3PurchaseOrderLineItemContext.class);

		List<V3PurchaseOrderLineItemContext> lineItems = builder.get();
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

	private ItemContext createItem(PurchaseOrderContext po, V3PurchaseOrderLineItemContext lineItem,
			boolean isRotating) throws Exception {
		ItemContext item = new ItemContext();
		item.setStoreRoom(po.getStoreRoom());
		item.setItemType(lineItem.getItemType());
		item.setCostType(CostType.FIFO);
		if (isRotating) {
			List<String> serialNumbers = getLineItemSerialNumbers(lineItem.getId());
			if (serialNumbers.size() < lineItem.getQuantityReceived()) {
				throw new IllegalArgumentException("Please fill all the serial numbers of item");
			} else {
				List<PurchasedItemContext> purchasedItems = setPurchasedItemContext(serialNumbers,
						lineItem.getUnitPrice());
				item.setPurchasedItems(purchasedItems);
			}
		} else {
			PurchasedItemContext purchasedItem = new PurchasedItemContext();
			purchasedItem.setQuantity(lineItem.getQuantityReceived());
			purchasedItem.setUnitcost(lineItem.getUnitPrice());
			item.setPurchasedItems(Collections.singletonList(purchasedItem));
		}
		return item;
	}

	private ToolContext createTool(PurchaseOrderContext po, V3PurchaseOrderLineItemContext lineItem,
			boolean isRotating) throws Exception {
		ToolContext tool = new ToolContext();
		tool.setStoreRoom(po.getStoreRoom());
		tool.setToolType(lineItem.getToolType());
		tool.setQuantity(lineItem.getQuantityReceived());
		tool.setRate(lineItem.getCost());
		if (isRotating) {
			List<String> serialNumbers = getLineItemSerialNumbers(lineItem.getId());
			if (serialNumbers.size() < lineItem.getQuantityReceived()) {
				throw new IllegalArgumentException("Please fill all the serial numbers of tool");
			} else {
				List<PurchasedToolContext> purchasedTools = setPurchasedToolsContext(serialNumbers, lineItem.getCost());
				tool.setPurchasedTools(purchasedTools);
			}
		}
		return tool;
	}

	private List<String> getLineItemSerialNumbers(long lineitemId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PO_LINE_ITEMS_SERIAL_NUMBERS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PO_LINE_ITEMS_SERIAL_NUMBERS);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<PoLineItemsSerialNumberContext> builder = new SelectRecordsBuilder<PoLineItemsSerialNumberContext>()
				.module(module)
				.select(fields).andCondition(CriteriaAPI.getCondition(fieldsMap.get("lineItem"),
						String.valueOf(lineitemId), NumberOperators.EQUALS))
				.beanClass(PoLineItemsSerialNumberContext.class);

		List<PoLineItemsSerialNumberContext> polineitemsserialnumbers = builder.get();
		List<String> serialNumbers = new ArrayList<>();
		if (polineitemsserialnumbers != null && !polineitemsserialnumbers.isEmpty()) {
			for (PoLineItemsSerialNumberContext polineitemsserialnumber : polineitemsserialnumbers) {
				serialNumbers.add(polineitemsserialnumber.getSerialNumber());
			}
			return serialNumbers;
		}
		return null;
	}

	private List<PurchasedItemContext> setPurchasedItemContext(List<String> serialNumbers, double cost) {
		List<PurchasedItemContext> purchasedItems = new ArrayList<>();
		for (String serialNumber : serialNumbers) {
			PurchasedItemContext pItem = new PurchasedItemContext();
			pItem.setSerialNumber(serialNumber);
			pItem.setUnitcost(cost);
			purchasedItems.add(pItem);
		}
		return purchasedItems;
	}

	private List<PurchasedToolContext> setPurchasedToolsContext(List<String> serialNumbers, double cost) {
		List<PurchasedToolContext> purchasedTools = new ArrayList<>();
		for (String serialNumber : serialNumbers) {
			PurchasedToolContext pTool = new PurchasedToolContext();
			pTool.setSerialNumber(serialNumber);
			pTool.setRate(cost);
			purchasedTools.add(pTool);
		}
		return purchasedTools;
	}

}
