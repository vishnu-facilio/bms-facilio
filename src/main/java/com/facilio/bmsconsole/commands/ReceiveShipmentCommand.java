package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ShipmentLineItemContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.ShipmentAPI;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
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
		Map<Long,ItemContext> itemMap = new HashMap<Long, ItemContext>();
		Map<Long,ToolContext> toolMap = new HashMap<Long, ToolContext>();
		
		List<ToolContext> toolsToBeAdded = new ArrayList<>();
		List<Long> newItemTypes = new ArrayList<Long>();
		List<Long> newToolTypes = new ArrayList<Long>();
		boolean containsIndividualTrackingItem = false;
		boolean containsIndividualTrackingTool = false;
		List<ToolTransactionContext> toolTransactions = new ArrayList<ToolTransactionContext>();
		
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
									if(containsIndividualTrackingItem) {
										newItem.setLastPurchasedDate(System.currentTimeMillis());
										newItem.setLastPurchasedPrice(lineItem.getUnitPrice());
										
										ItemsApi.updateLastPurchasedDateForItem(newItem);
										ItemsApi.updateLastPurchasedDetailsForItemType(newItem.getItemType().getId());
										
									}
									if(!newItemTypes.contains(itemtype.getId())) {
										itemMap.put(newItem.getItemType().getId(), newItem);
										newItemTypes.add(newItem.getItemType().getId());
									}
									else {
										ItemContext existingItem = itemMap.get(newItem.getItemType().getId());
										List<PurchasedItemContext> piList = new ArrayList<PurchasedItemContext>();
										piList.addAll(existingItem.getPurchasedItems());
										PurchasedItemContext pi = null;
										if(containsIndividualTrackingItem) {
											
											pi = ShipmentAPI.getPurchasedItem(0, lineItem.getUnitPrice());
										}
										else {
											pi = ShipmentAPI.getPurchasedItem(lineItem.getQuantity(), lineItem.getUnitPrice());
										}
										piList.add(pi);
										existingItem.setPurchasedItems(piList);
										
									}
								} else if (lineItem.getInventoryTypeEnum() == InventoryType.TOOL) {
									ToolTypesContext toolType = ToolsApi.getToolTypes(lineItem.getToolType().getId());
									ToolContext newTool = null;
									
									if (toolType.isRotating()) {
										containsIndividualTrackingTool = true;
									}
									else {
										containsIndividualTrackingTool = false;
									}
									newTool = ShipmentAPI.createTool(shipment, lineItem, containsIndividualTrackingTool, shipmentRotatingAssets);
									if (toolType.isRotating()) {
										containsIndividualTrackingTool = true;
										ToolTransactionContext transaction = new ToolTransactionContext();
										transaction.setTool(newTool);
										transaction.setQuantity(1);
										transaction.setParentId(newTool.getId());
										transaction.setIsReturnable(false);
										if(shipment == null) {
											transaction.setTransactionType(TransactionType.STOCK.getValue());
											transaction.setTransactionState(TransactionState.ADDITION.getValue());
										}
										else {
											transaction.setTransactionType(TransactionType.SHIPMENT_STOCK.getValue());
											transaction.setTransactionState(TransactionState.ADDITION.getValue());
											transaction.setShipment(shipment.getId());
										}
										transaction.setToolType(toolType);
										transaction.setApprovedState(ApprovalState.YET_TO_BE_REQUESTED);
										toolTransactions.add(transaction);

									} 
									if(!newToolTypes.contains(toolType.getId())) {
										toolMap.put(newTool.getToolType().getId(), newTool);
										newToolTypes.add(newTool.getToolType().getId());
										toolsToBeAdded.add(newTool);
									}
									else {
										ToolContext existingTool = toolMap.get(newTool.getToolType().getId());
										existingTool.setQuantity(existingTool.getQuantity() + newTool.getQuantity());
									}
								    	
								}
						}
					}
					
			}
		}
		
		context.put(FacilioConstants.ContextNames.ITEMS, new ArrayList<ItemContext>(itemMap.values()));
		context.put(FacilioConstants.ContextNames.TOOLS, toolsToBeAdded);
		context.put(FacilioConstants.ContextNames.SHIPMENT_LINE_ITEM, shipmentRotatingAssets);
		context.put(FacilioConstants.ContextNames.TOOL_TRANSACTION_LIST, toolTransactions);
			
		return false;
	}

	
	
}
