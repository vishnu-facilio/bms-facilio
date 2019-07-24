package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ShipmentLineItemContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.util.StoreroomApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateShipmentCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ShipmentContext shipment = (ShipmentContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(shipment!=null && shipment.isShipmentTrackingEnabled()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.SHIPMENT_LINE_ITEM);
			
		
			if(shipment.getFromStore() == null) {
				throw new IllegalArgumentException("From Store cannot be null");
			}
			if(shipment.getToStore() == null) {
				throw new IllegalArgumentException("To Store cannot be null");
			}
			if(shipment.getTransferredBy() == null) {
				StoreRoomContext fromStore = StoreroomApi.getStoreRoom(shipment.getFromStore().getId());
				shipment.setTransferredBy(fromStore.getOwner() != null ? fromStore.getOwner() : AccountUtil.getCurrentUser());
			}
			if(shipment.getReceivedBy() == null) {
				StoreRoomContext toStore = StoreroomApi.getStoreRoom(shipment.getToStore().getId());
				shipment.setReceivedBy(toStore.getOwner() != null ? toStore.getOwner() : AccountUtil.getCurrentUser());
		    }
			if (shipment.getId() > 0) {
				updateRecord(shipment, module, fields);
				
				DeleteRecordBuilder<ShipmentLineItemContext> deleteBuilder = new DeleteRecordBuilder<ShipmentLineItemContext>()
						.module(lineModule)
						.andCondition(CriteriaAPI.getCondition("SHIPMENT_ID", "shipment", String.valueOf(shipment.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
			} else {
				
				shipment.setStatus(ShipmentContext.Status.NOT_STAGED);
				addRecord(true,Collections.singletonList(shipment), module, fields);
			}
			
			updateLineItems(shipment);
			addRecord(false,shipment.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
			
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
	
	private void addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
		if(isLocalIdNeeded) {
			insertRecordBuilder.withLocalId();
		}
		insertRecordBuilder.addRecords(list);
		insertRecordBuilder.save();
	}
	
	public void updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields) throws Exception {
		UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
		updateRecordBuilder.update(data);
	}
	


}
