package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.ItemContext.CostType;
import com.facilio.bmsconsole.context.PurchaseOrderContext.Status;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PurchaseOrderCompleteCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<Long> purchaseOrdersIds = (List<Long>) context.get(FacilioConstants.ContextNames.PURCHASE_ORDERS);
		if (purchaseOrdersIds != null && !purchaseOrdersIds.isEmpty()) {
			List<PurchaseOrderLineItemContext> lineItems = (List<PurchaseOrderLineItemContext>) context
					.get(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
			List<ItemContext> itemsTobeAdded = new ArrayList<>();
			List<ToolContext> toolsToBeAdded = new ArrayList<>();
			List<ItemTypesVendorsContext> itemTypesVendors = new ArrayList<>();
			boolean containsIndividualTrackingItem = false;
			boolean containsIndividualTrackingTool = false;
			long storeRoomId = -1;
			long vendorId = -1;
			List<PurchaseOrderContext> purchaseOrders = getPurchaseOrderContext(purchaseOrdersIds);
			if (purchaseOrders != null && !purchaseOrders.isEmpty()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule pomodule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);
				for (PurchaseOrderContext po : purchaseOrders) {
					storeRoomId = po.getStoreRoom().getId();
					vendorId = po.getVendor().getId();
					if (lineItems == null) {
						lineItems = getLineItemsForPO(po.getId());
					}

					if (lineItems != null && !lineItems.isEmpty()) {
						for (PurchaseOrderLineItemContext lineItem : lineItems) {
							if (lineItem.getQuantityReceived() > 0) {
								if (lineItem.getInventoryTypeEnum() == InventoryType.ITEM) {
									itemTypesVendors.add(new ItemTypesVendorsContext(lineItem.getItemType(),
											po.getVendor(), lineItem.getCost(), po.getOrderedTime()));
									ItemTypesContext itemtype = getItemType(lineItem.getItemType().getId());
									if (itemtype.isRotating()) {
										containsIndividualTrackingItem = true;
										ItemContext item = ItemsApi.getItemsForTypeAndStore(po.getStoreRoom().getId(), lineItem.getItemType().getId());
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
									ToolTypesContext toolType = getToolType(lineItem.getToolType().getId());
									if (toolType.isRotating()) {
										containsIndividualTrackingTool = true;
										ToolContext tool = ToolsApi.getToolsForTypeAndStore(po.getStoreRoom().getId(), toolType.getId());
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
					po.setStatus(Status.COMPLETED);
					po.setCompletedTime(System.currentTimeMillis());
					UpdateRecordBuilder<PurchaseOrderContext> updateBuilder = new UpdateRecordBuilder<PurchaseOrderContext>()
							.module(pomodule).fields(modBean.getAllFields(pomodule.getName()))
							.andCondition(CriteriaAPI.getIdCondition(po.getId(), pomodule));
					updateBuilder.update(po);
				}
			}
			context.put(FacilioConstants.ContextNames.STORE_ROOM, storeRoomId);
			context.put(FacilioConstants.ContextNames.VENDOR_ID, vendorId);
			context.put(FacilioConstants.ContextNames.ITEM_VENDORS_LIST, itemTypesVendors);
			context.put(FacilioConstants.ContextNames.ITEMS, itemsTobeAdded);
			context.put(FacilioConstants.ContextNames.TOOLS, toolsToBeAdded);
		}
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

	private ToolContext createTool(PurchaseOrderContext po, PurchaseOrderLineItemContext lineItem,
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
