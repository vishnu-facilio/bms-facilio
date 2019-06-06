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

public class StageShipmentCommand implements Command{

List<ShipmentLineItemContext> shipmentRotatingAssets = new ArrayList<ShipmentLineItemContext>();
	

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ShipmentContext shipment = (ShipmentContext) context
				.get(FacilioConstants.ContextNames.SHIPMENT);
		List<ItemContext> itemsTobeAdded = new ArrayList<>();
		List<ToolContext> toolsToBeAdded = new ArrayList<>();
		boolean containsIndividualTrackingItem = false;
		boolean containsIndividualTrackingTool = false;
		
		if (shipment != null) {
			context.put(FacilioConstants.ContextNames.STORE_ROOM, shipment.getToStore().getId());
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule shipmentModule = modBean.getModule(FacilioConstants.ContextNames.SHIPMENT);
				if ((shipment.isShipmentTrackingEnabled() && shipment.getStatusEnum() == ShipmentContext.Status.STAGED) || !shipment.isShipmentTrackingEnabled()) {
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
									itemsTobeAdded.add(ShipmentAPI.createItem(shipment, lineItem, containsIndividualTrackingItem, shipmentRotatingAssets));
								} else if (lineItem.getInventoryTypeEnum() == InventoryType.TOOL) {
									ToolTypesContext toolType = ToolsApi.getToolTypes(lineItem.getToolType().getId());
									if (toolType.isRotating()) {
										containsIndividualTrackingTool = true;
									} else {
										containsIndividualTrackingTool = false;
									}
									toolsToBeAdded.add(ShipmentAPI.createTool(shipment, lineItem, containsIndividualTrackingTool, shipmentRotatingAssets));
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
