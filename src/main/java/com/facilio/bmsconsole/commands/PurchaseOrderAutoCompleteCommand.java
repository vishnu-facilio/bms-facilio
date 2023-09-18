package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderLineItemContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

;

public class PurchaseOrderAutoCompleteCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<V3PurchaseOrderContext> purchaseOrders = (List<V3PurchaseOrderContext>) context
				.get(FacilioConstants.ContextNames.PURCHASE_ORDERS);
		List<V3ItemContext> itemsTobeAdded = new ArrayList<>();
		List<V3ToolContext> toolsToBeAdded = new ArrayList<>();
		boolean containsIndividualTrackingItem = false;
		boolean containsIndividualTrackingTool = false;
		List<V3ItemTypesVendorsContext> itemTypesVendors = new ArrayList<>();
		List<V3ToolTypeVendorContext> toolTypesVendors = new ArrayList<>();

		CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
		Map<String, CurrencyContext> currencyMap = Constants.getCurrencyMap(context);

		long storeRoomId = -1;
		long vendorId = -1;
		if (purchaseOrders != null && !purchaseOrders.isEmpty()) {
			for (V3PurchaseOrderContext v3po : purchaseOrders) {
				Map<String, Object> map = FieldUtil.getAsProperties(v3po);
				V3PurchaseOrderContext po = FieldUtil.getAsBeanFromMap(map, V3PurchaseOrderContext.class);
				if (v3po.getReceivableStatus() == V3PurchaseOrderContext.ReceivableStatus.RECEIVED.getIndex()) {
					if (po.getStoreRoom() != null) {
						storeRoomId = po.getStoreRoom().getId();
					}
					vendorId = po.getVendor().getId();
					List<V3PurchaseOrderLineItemContext> lineItems = getLineItemsForPO(po.getId());
					if (lineItems != null && !lineItems.isEmpty()) {
						for (V3PurchaseOrderLineItemContext lineItem : lineItems) {
							if (lineItem.getInventoryTypeEnum() == InventoryType.ITEM) {
								itemTypesVendors.add(new V3ItemTypesVendorsContext(lineItem.getItemType(), po.getVendor(),
										lineItem.getCost(), po.getOrderedTime()));
								V3ItemTypesContext itemtype = getItemType(lineItem.getItemType().getId());
								if (itemtype.isRotating()) {
									containsIndividualTrackingItem = true;
									break;
								} else {
									itemsTobeAdded.add(createItem(po, lineItem, baseCurrency, currencyMap));
								}
							} else if (lineItem.getInventoryTypeEnum() == InventoryType.TOOL) {
								toolTypesVendors.add(new V3ToolTypeVendorContext(lineItem.getToolType(), po.getVendor(),
										lineItem.getCost(), po.getOrderedTime()));
								V3ToolTypesContext toolType = getToolType(lineItem.getToolType().getId());
								if (toolType.isRotating()) {
									containsIndividualTrackingTool = true;
									break;
								} else {
									toolsToBeAdded.add(createTool(po, lineItem, baseCurrency, currencyMap));
								}
							}
						}
					}
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

	private List<V3PurchaseOrderLineItemContext> getLineItemsForPO(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
		CurrencyUtil.addMultiCurrencyFieldsToFields(fields, module);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<V3PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<V3PurchaseOrderLineItemContext>()
				.module(module).select(fields)
				.andCondition(
						CriteriaAPI.getCondition(fieldsMap.get("purchaseOrder"), String.valueOf(id), NumberOperators.EQUALS))
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

	private V3ItemContext createItem(V3PurchaseOrderContext po, V3PurchaseOrderLineItemContext lineItem,
			CurrencyContext baseCurrency, Map<String, CurrencyContext> currencyMap) throws Exception {
		V3ItemContext item = new V3ItemContext();
		item.setStoreRoom(po.getStoreRoom());
		item.setItemType(lineItem.getItemType());

		ModuleBean modBean = Constants.getModBean();
		List<FacilioField> purchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
		List<FacilioField> purchasedItemMultiCurrencyFields = CurrencyUtil
				.getMultiCurrencyFieldsFromFields(purchasedItemFields);

		V3PurchasedItemContext purchasedItem = new V3PurchasedItemContext();
		purchasedItem.setQuantity(lineItem.getQuantity());
		purchasedItem.setUnitcost(lineItem.getUnitPrice());
		purchasedItem.setCurrencyCode(lineItem.getCurrencyCode());
		purchasedItem = (V3PurchasedItemContext) CurrencyUtil
				.addMultiCurrencyData(FacilioConstants.ContextNames.PURCHASED_ITEM,
						purchasedItemMultiCurrencyFields, Collections.singletonList(purchasedItem), V3PurchasedItemContext.class,
						baseCurrency, currencyMap)
				.get(0);
		item.setPurchasedItems(Collections.singletonList(purchasedItem));

		return item;
	}

	private V3ToolContext createTool(V3PurchaseOrderContext po, V3PurchaseOrderLineItemContext lineItem,
			CurrencyContext baseCurrency, Map<String, CurrencyContext> currencyMap) {
		V3ToolContext tool = new V3ToolContext();
		tool.setStoreRoom(po.getStoreRoom());
		tool.setToolType(lineItem.getToolType());
		tool.setQuantity(lineItem.getQuantity());
		V3PurchasedToolContext purchasedItem = new V3PurchasedToolContext();
		purchasedItem.setQuantity(lineItem.getQuantity());
		purchasedItem.setUnitPrice(lineItem.getUnitPrice());
		tool.setPurchasedTools(Collections.singletonList(purchasedItem));
		CurrencyUtil.setCurrencyCodeAndExchangeRateForWrite(tool, baseCurrency, currencyMap, lineItem.getCurrencyCode(),
				lineItem.getExchangeRate());

		return tool;
	}
}
