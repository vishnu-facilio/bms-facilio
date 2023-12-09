package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderLineItemContext;
import com.facilio.bmsconsoleV3.enums.CostType;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.modules.*;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class PurchaseOrderCompleteCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		V3PurchaseOrderContext purchaseOrder = (V3PurchaseOrderContext) context
				.get(FacilioConstants.ContextNames.PURCHASE_ORDERS);
		if (purchaseOrder != null) {
			List<V3PurchaseOrderLineItemContext> lineItems = (List<V3PurchaseOrderLineItemContext>) context
					.get(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
			List<V3ItemContext> itemsTobeAdded = new ArrayList<>();
			List<V3ToolContext> toolsToBeAdded = new ArrayList<>();
			List<V3ItemTypesVendorsContext> itemTypesVendors = new ArrayList<>();
			List<V3ToolTypeVendorContext> toolTypesVendors = new ArrayList<>();

			CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
			Map<String, CurrencyContext> currencyMap = Constants.getCurrencyMap(context);

			boolean containsIndividualTrackingItem = false;
			boolean containsIndividualTrackingTool = false;
			long storeRoomId = -1;
			long vendorId = -1;
			if (purchaseOrder != null) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule pomodule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);
				if (purchaseOrder.getStoreRoom() != null) {
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
								itemTypesVendors.add(new V3ItemTypesVendorsContext(lineItem.getItemType(),
										purchaseOrder.getVendor(), lineItem.getCost(), purchaseOrder.getOrderedTime()));
								V3ItemTypesContext itemtype = getItemType(lineItem.getItemType().getId());
								if (itemtype.isRotating()) {
									containsIndividualTrackingItem = true;
									V3ItemContext item = V3ItemsApi.getItemsForTypeAndStore(purchaseOrder.getStoreRoom().getId(),
											lineItem.getItemType().getId());
									Long lastPurchasedDate = V3ItemsApi.getLastPurchasedItemDateForItemId(item.getId());
									V3ItemContext update_item = new V3ItemContext();
									update_item.setId(item.getId());
									update_item.setLastPurchasedDate(lastPurchasedDate);
									update_item.setLastPurchasedPrice(V3ItemsApi.getLastPurchasedItemPriceForItemId(item.getId()));
									V3ItemsApi.updateLastPurchasedDateForItem(update_item);
									V3ItemsApi.updateLastPurchasedDetailsForItemType(itemtype.getId(), baseCurrency, currencyMap);
								} else {
									containsIndividualTrackingItem = false;
									itemsTobeAdded.add(
											createItem(purchaseOrder, lineItem, containsIndividualTrackingItem, baseCurrency, currencyMap));
								}
							} else if (lineItem.getInventoryTypeEnum() == InventoryType.TOOL) {
								toolTypesVendors.add(new V3ToolTypeVendorContext(lineItem.getToolType(),
										purchaseOrder.getVendor(), lineItem.getCost(), purchaseOrder.getOrderedTime()));
								V3ToolTypesContext toolType = getToolType(lineItem.getToolType().getId());
								if (toolType.isRotating()) {
									containsIndividualTrackingTool = true;
									V3ToolContext tool = V3ToolsApi.getToolsForTypeAndStore(purchaseOrder.getStoreRoom().getId(),
											toolType.getId());
									long lastPurchasedDate = V3ToolsApi.getLastPurchasedToolDateForToolId(tool.getId());
									V3ToolContext update_tool = new V3ToolContext();
									update_tool.setId(tool.getId());
									update_tool.setLastPurchasedDate(lastPurchasedDate);
									update_tool.setLastPurchasedPrice(V3ToolsApi.getLastPurchasedToolPriceForToolId(tool.getId()));
									V3ToolsApi.updateLastPurchasedDateForTool(update_tool);
									V3ToolsApi.updatelastPurchaseddetailsInToolType(toolType.getId());
								} else {
									containsIndividualTrackingTool = false;
									toolsToBeAdded.add(
											createTool(purchaseOrder, lineItem, containsIndividualTrackingTool, baseCurrency, currencyMap));
								}
							}
						}
					}
				}
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

	private V3ItemTypesContext getItemType(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM_TYPES);

		SelectRecordsBuilder<V3ItemTypesContext> builder = new SelectRecordsBuilder<V3ItemTypesContext>().module(module)
				.select(fields).andCondition(CriteriaAPI.getIdCondition(id, module)).beanClass(V3ItemTypesContext.class);

		List<V3ItemTypesContext> item = builder.get();
		if (item != null && !item.isEmpty()) {
			return item.get(0);
		}
		return null;
	}

	private V3ToolTypesContext getToolType(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);

		SelectRecordsBuilder<V3ToolTypesContext> builder = new SelectRecordsBuilder<V3ToolTypesContext>().module(module)
				.select(fields).andCondition(CriteriaAPI.getIdCondition(id, module)).beanClass(V3ToolTypesContext.class);

		List<V3ToolTypesContext> tool = builder.get();
		if (tool != null && !tool.isEmpty()) {
			return tool.get(0);
		}
		return null;
	}

	private V3ItemContext createItem(V3PurchaseOrderContext purchaseOrder, V3PurchaseOrderLineItemContext lineItem,
			boolean isRotating, CurrencyContext baseCurrency, Map<String, CurrencyContext> currencyMap) throws Exception {
		V3ItemContext item = new V3ItemContext();
		item.setStoreRoom(purchaseOrder.getStoreRoom());
		item.setItemType(lineItem.getItemType());
		if(lineItem.getItemType()!=null){
			V3ItemTypesContext itemType = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ITEM_TYPES,lineItem.getItemType().getId(),V3ItemTypesContext.class);
				if(itemType.getCostType()!=null){
					item.setCostType(itemType.getCostType());
				}else{
					item.setCostType(CostType.FIFO.getIndex());
				}
		}
		ModuleBean modBean = Constants.getModBean();
		List<FacilioField> purchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
		List<FacilioField> purchasedItemMultiCurrencyFields = CurrencyUtil
				.getMultiCurrencyFieldsFromFields(purchasedItemFields);

		if (isRotating) {
			List<String> serialNumbers = getLineItemSerialNumbers(lineItem.getId());
			if (serialNumbers.size() < lineItem.getQuantityReceived()) {
				throw new IllegalArgumentException("Please fill all the serial numbers of item");
			} else {
				List<V3PurchasedItemContext> purchasedItems = setPurchasedItemContext(serialNumbers,
						lineItem.getUnitPrice());
				item.setPurchasedItems(purchasedItems);
			}
		} else {
			V3PurchasedItemContext purchasedItem = new V3PurchasedItemContext();
			purchasedItem.setQuantity(lineItem.getQuantityReceived());
			purchasedItem.setUnitcost(lineItem.getUnitPrice());
			purchasedItem.setCurrencyCode(lineItem.getCurrencyCode());
			purchasedItem = (V3PurchasedItemContext) CurrencyUtil
					.addMultiCurrencyData(FacilioConstants.ContextNames.PURCHASED_ITEM,
							purchasedItemMultiCurrencyFields, Collections.singletonList(purchasedItem), V3PurchasedItemContext.class,
							baseCurrency, currencyMap)
					.get(0);
			item.setPurchasedItems(Collections.singletonList(purchasedItem));
		}
		return item;
	}

	private V3ToolContext createTool(V3PurchaseOrderContext purchaseOrder, V3PurchaseOrderLineItemContext lineItem,
			boolean isRotating, CurrencyContext baseCurrency, Map<String, CurrencyContext> currencyMap) throws Exception {
		V3ToolContext tool = new V3ToolContext();
		tool.setStoreRoom(purchaseOrder.getStoreRoom());
		tool.setToolType(lineItem.getToolType());
		tool.setCostType(CostType.FIFO.getIndex());
		tool.setQuantity(lineItem.getQuantityReceived());
		if (isRotating) {
			List<String> serialNumbers = getLineItemSerialNumbers(lineItem.getId());
			if (serialNumbers.size() < lineItem.getQuantityReceived()) {
				throw new IllegalArgumentException("Please fill all the serial numbers of tool");
			} else {
				List<V3PurchasedToolContext> purchasedTools = setPurchasedToolsContext(serialNumbers, lineItem.getUnitPrice());
				tool.setPurchasedTools(purchasedTools);
			}
		} else {
			V3PurchasedToolContext purchasedTool = new V3PurchasedToolContext();
			purchasedTool.setQuantity(lineItem.getQuantityReceived());
			purchasedTool.setUnitPrice(lineItem.getUnitPrice());
			tool.setPurchasedTools(Collections.singletonList(purchasedTool));
		}
		CurrencyUtil.setCurrencyCodeAndExchangeRateForWrite(tool, baseCurrency, currencyMap, lineItem.getCurrencyCode(),
				lineItem.getExchangeRate());
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

	private List<V3PurchasedItemContext> setPurchasedItemContext(List<String> serialNumbers, double cost) {
		List<V3PurchasedItemContext> purchasedItems = new ArrayList<>();
		for (String serialNumber : serialNumbers) {
			V3PurchasedItemContext pItem = new V3PurchasedItemContext();
			pItem.setSerialNumber(serialNumber);
			pItem.setUnitcost(cost);
			purchasedItems.add(pItem);
		}
		return purchasedItems;
	}

	private List<V3PurchasedToolContext> setPurchasedToolsContext(List<String> serialNumbers, double cost) {
		List<V3PurchasedToolContext> purchasedTools = new ArrayList<>();
		for (String serialNumber : serialNumbers) {
			V3PurchasedToolContext pTool = new V3PurchasedToolContext();
			pTool.setSerialNumber(serialNumber);
			pTool.setUnitPrice(cost);
			purchasedTools.add(pTool);
		}
		return purchasedTools;
	}
}
