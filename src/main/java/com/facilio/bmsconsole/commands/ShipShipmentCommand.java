package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ShipmentLineItemContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ShipShipmentCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ShipmentContext> shipments = (List<ShipmentContext>) context
				.get(FacilioConstants.ContextNames.SHIPMENT);
		List<ItemContext> itemsTobeAdded = new ArrayList<>();
		List<ToolContext> toolsToBeAdded = new ArrayList<>();
		boolean containsIndividualTrackingItem = false;
		boolean containsIndividualTrackingTool = false;
		
		if (CollectionUtils.isNotEmpty(shipments)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule shipmentModule = modBean.getModule(FacilioConstants.ContextNames.SHIPMENT);
			for (ShipmentContext shipment : shipments) {
				if (shipment.getStatusEnum() == ShipmentContext.Status.SHIPPED) {
					List<ShipmentLineItemContext> lineItems = getLineItemsForShipment(shipment.getId());
					if (lineItems != null && !lineItems.isEmpty()) {
						for (ShipmentLineItemContext lineItem : lineItems) {
							if (lineItem.getInventoryTypeEnum() == InventoryType.ITEM) {
								ItemTypesContext itemtype = getItemType(lineItem.getItemType().getId());
								if (itemtype.isRotating()) {
									containsIndividualTrackingItem = true;
									break;
								} else {
									itemsTobeAdded.add(createItem(shipment, lineItem));
								}
							} else if (lineItem.getInventoryTypeEnum() == InventoryType.TOOL) {
								ToolTypesContext toolType = getToolType(lineItem.getToolType().getId());
								if (toolType.isRotating()) {
									containsIndividualTrackingTool = true;
									break;
								} else {
									toolsToBeAdded.add(createTool(shipment, lineItem));
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
		context.put(FacilioConstants.ContextNames.ITEMS, itemsTobeAdded);
		context.put(FacilioConstants.ContextNames.TOOLS, toolsToBeAdded);
	
		return false;
	}

	private List<ShipmentLineItemContext> getLineItemsForShipment(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SHIPMENT_LINE_ITEM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SHIPMENT_LINE_ITEM);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<ShipmentLineItemContext> builder = new SelectRecordsBuilder<ShipmentLineItemContext>()
				.module(module).select(fields)
				.andCondition(
						CriteriaAPI.getCondition(fieldsMap.get("shipment"), String.valueOf(id), NumberOperators.EQUALS))
				.beanClass(ShipmentLineItemContext.class);

		List<ShipmentLineItemContext> lineItems = builder.get();
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

	private ItemContext createItem(ShipmentContext shipment, ShipmentLineItemContext lineItem) {
		ItemContext item = new ItemContext();
		item.setStoreRoom(shipment.getToStore());
		item.setItemType(lineItem.getItemType());
		PurchasedItemContext purchasedItem = new PurchasedItemContext();
		purchasedItem.setQuantity(lineItem.getQuantity());
		purchasedItem.setUnitcost(lineItem.getUnitPrice());
		item.setPurchasedItems(Collections.singletonList(purchasedItem));

		return item;
	}

	private ToolContext createTool(ShipmentContext shipment, ShipmentLineItemContext lineItem) {
		ToolContext tool = new ToolContext();
		tool.setStoreRoom(shipment.getToStore());
		tool.setToolType(lineItem.getToolType());
		tool.setQuantity(lineItem.getQuantity());
		tool.setRate(lineItem.getRate());

		return tool;
	}
}
