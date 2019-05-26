package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ShipmentLineItemContext;
import com.facilio.constants.FacilioConstants;

public class UpdateShipmentRecordForDirecttransferCommand  implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ShipmentContext shipment = (ShipmentContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(shipment!=null) {
			if(shipment.getFromStore() == null) {
				throw new IllegalArgumentException("From Store cannot be null");
			}
			if(shipment.getToStore() == null) {
				throw new IllegalArgumentException("To Store cannot be null");
			}
			if(shipment.getTransferredBy() == null) {
				shipment.setTransferredBy(AccountUtil.getCurrentUser());
			}
			updateLineItems(shipment);
			
			context.put(FacilioConstants.ContextNames.RECORD, shipment);
		}
		return false;
	}
	
		private void updateLineItems(ShipmentContext shipment) {
		     List<ShipmentLineItemContext> rotatingItems = new ArrayList<ShipmentLineItemContext>(); 
	         
		        for (ShipmentLineItemContext lineItemContext : shipment.getLineItems()) { 
		            lineItemContext.setShipment(shipment.getId()); 
		            if(lineItemContext.getAssetIds() != null && lineItemContext.getAssetIds().size() > 0) { 
		                AssetContext lineItemAsset = new AssetContext(); 
		                lineItemAsset.setId(lineItemContext.getAssetIds().get(0)); 
		                lineItemContext.setAsset(lineItemAsset); 
		                lineItemContext.setQuantity(1); 
		                for(int i=1; i<lineItemContext.getAssetIds().size(); i++) { 
		                     ShipmentLineItemContext lineItem = new ShipmentLineItemContext(); 
		                     AssetContext asset = new AssetContext(); 
		                     asset.setId(lineItemContext.getAssetIds().get(i)); 
		                     lineItem.setAsset(asset); 
		                     lineItem.setUnitPrice(lineItemContext.getUnitPrice());
		                     lineItem.setRate(lineItemContext.getRate());
		                     lineItem.setInventoryType(lineItemContext.getInventoryType()); 
		                     if(lineItemContext.getInventoryType() == InventoryType.ITEM.getValue()) { 
		                         lineItem.setItemType(lineItemContext.getItemType()); 
		                     } 
		                     else if(lineItemContext.getInventoryType() == InventoryType.TOOL.getValue()) { 
		                         lineItem.setToolType(lineItemContext.getToolType()); 
		                     } 
		                      
		                     lineItem.setQuantity(lineItemContext.getQuantity()); 
		                     lineItem.setShipment(shipment.getId()); 
		                     rotatingItems.add(lineItem);  
		                } 
		            } 
		             
		        } 
		        shipment.getLineItems().addAll(rotatingItems); 
		    
	}
	
	
}
