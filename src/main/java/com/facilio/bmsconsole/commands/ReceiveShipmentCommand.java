package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ShipmentLineItemContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.ShipmentAPI;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class ReceiveShipmentCommand implements Command{
	
	List<ShipmentLineItemContext> shipmentRotatingAssets = new ArrayList<ShipmentLineItemContext>();
	

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ShipmentContext shipment = (ShipmentContext) context
				.get(FacilioConstants.ContextNames.SHIPMENT);
		List<ItemContext> itemsTobeAdded = new ArrayList<>();
		List<ToolContext> toolsToBeAdded = new ArrayList<>();
		List<Long> newItemTypes = new ArrayList<Long>();
		List<Long> newToolTypes = new ArrayList<Long>();
		boolean containsIndividualTrackingItem = false;
		boolean containsIndividualTrackingTool = false;
		
		if (shipment != null) {
			context.put(FacilioConstants.ContextNames.STORE_ROOM, shipment.getToStore().getId());
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule shipmentModule = modBean.getModule(FacilioConstants.ContextNames.SHIPMENT);
			if ((shipment.isShipmentTrackingEnabled() && shipment.getStatusEnum() == ShipmentContext.Status.RECEIVED) || !shipment.isShipmentTrackingEnabled()) {
					List<ShipmentLineItemContext> lineItems = shipment.getLineItems();
					if (lineItems != null && !lineItems.isEmpty()) {
						for (ShipmentLineItemContext lineItem : lineItems) {
								if (lineItem.getInventoryTypeEnum() == InventoryType.ITEM) {
									ItemTypesContext itemtype = ItemsApi.getItemTypes(lineItem.getItemType().getId());
									if (itemtype.isRotating()) {
										containsIndividualTrackingItem = true;
									} else {
										containsIndividualTrackingItem = false;
									}
									ItemContext newItem = ShipmentAPI.createItem(shipment, lineItem, containsIndividualTrackingItem, shipmentRotatingAssets);
										if(!newItemTypes.contains(itemtype.getId())) {
											itemsTobeAdded.add(newItem);
											newItemTypes.add(newItem.getItemType().getId());
										}
								} else if (lineItem.getInventoryTypeEnum() == InventoryType.TOOL) {
									ToolTypesContext toolType = ToolsApi.getToolTypes(lineItem.getToolType().getId());
									if (toolType.isRotating()) {
										containsIndividualTrackingTool = true;
									} else {
										containsIndividualTrackingTool = false;
									}
									ToolContext newTool = ShipmentAPI.createTool(shipment, lineItem, containsIndividualTrackingTool, shipmentRotatingAssets);
									if(!newToolTypes.contains(toolType.getId())) {
										toolsToBeAdded.add(newTool);
										newToolTypes.add(newTool.getToolType().getId());
									}
								}
						}
					}
					
			}
		}
		
		context.put(FacilioConstants.ContextNames.ITEMS, itemsTobeAdded);
		context.put(FacilioConstants.ContextNames.TOOLS, toolsToBeAdded);
		context.put(FacilioConstants.ContextNames.SHIPMENT_LINE_ITEM, shipmentRotatingAssets);
		
		return false;
	}

	
	
}
