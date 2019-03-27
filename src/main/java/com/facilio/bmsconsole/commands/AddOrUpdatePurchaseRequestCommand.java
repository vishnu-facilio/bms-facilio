package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext.Status;
import com.facilio.bmsconsole.context.PurchaseRequestLineItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdatePurchaseRequestCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		PurchaseRequestContext purchaseRequestContext = (PurchaseRequestContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (purchaseRequestContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS);
			
			if (CollectionUtils.isEmpty(purchaseRequestContext.getLineItems())) {
				throw new Exception("Line items cannot be empty");
			}

			if(purchaseRequestContext.getRequestedTime() == -1) {
				purchaseRequestContext.setRequestedTime(System.currentTimeMillis());
			}
			purchaseRequestContext.setShipToAddress(getLocation(purchaseRequestContext, purchaseRequestContext.getShipToAddress(), "SHIP_TO_Location"));
			purchaseRequestContext.setBillToAddress(getLocation(purchaseRequestContext, purchaseRequestContext.getBillToAddress(), "BILL_TO_Location"));
			if (purchaseRequestContext.getId() > 0) {
				updateRecord(purchaseRequestContext, module, fields);
				
				DeleteRecordBuilder<PurchaseRequestLineItemContext> deleteBuilder = new DeleteRecordBuilder<PurchaseRequestLineItemContext>()
						.module(lineModule)
						.andCondition(CriteriaAPI.getCondition("PR_ID", "prid", String.valueOf(purchaseRequestContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
			} else {
				purchaseRequestContext.setStatus(Status.REQUESTED);
				addRecord(Collections.singletonList(purchaseRequestContext), module, fields);
			}
			
			updateLineItems(purchaseRequestContext);
			addRecord(purchaseRequestContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
			
			context.put(FacilioConstants.ContextNames.RECORD, purchaseRequestContext);
		}
		return false;
	}
	
	private void setShipToLocation (PurchaseRequestContext purchaseRequestContext) throws Exception {
		LocationContext shipToLocation = new LocationContext();
		FacilioContext context = new FacilioContext();
		
		if(purchaseRequestContext.getShipToAddress() != null && purchaseRequestContext.getShipToAddress().getLat() != -1 && purchaseRequestContext.getShipToAddress().getLng() != -1) {
			shipToLocation.setName("SHIP_TO_Location");
			context.put(FacilioConstants.ContextNames.RECORD, purchaseRequestContext.getShipToAddress());
			if (shipToLocation.getId() > 0) {
				Chain editLocation = FacilioChainFactory.updateLocationChain();
				editLocation.execute(context);
				purchaseRequestContext.setShipToAddress(shipToLocation);
			}
			else {
				Chain addLocation = FacilioChainFactory.addLocationChain();
				addLocation.execute(context);
				long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				shipToLocation.setId(locationId);
			}
		}
		else {
			if(purchaseRequestContext.getStoreRoom() != null) {
				LocationContext storeRoomLocation = purchaseRequestContext.getStoreRoom().getLocation();
				shipToLocation.setName("SHIP_TO_Location");
				shipToLocation.setStreet(storeRoomLocation.getStreet());
				shipToLocation.setState(storeRoomLocation.getState());
				shipToLocation.setZip(storeRoomLocation.getZip());
				shipToLocation.setCountry(storeRoomLocation.getCountry());
				context.put(FacilioConstants.ContextNames.RECORD, shipToLocation);
				Chain addLocation = FacilioChainFactory.addLocationChain();
				addLocation.execute(context);
				long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				shipToLocation.setId(locationId);
		
				purchaseRequestContext.setShipToAddress(shipToLocation);
			}
		}
	}

	private void setBillToLocation (PurchaseRequestContext purchaseRequestContext) throws Exception {
		LocationContext billToLocation = new LocationContext();
		FacilioContext context = new FacilioContext();
		
		if(purchaseRequestContext.getBillToAddress() != null && purchaseRequestContext.getBillToAddress().getLat() != -1 && purchaseRequestContext.getBillToAddress().getLng() != -1) {
			billToLocation.setName("BILL_TO_Location");
			context.put(FacilioConstants.ContextNames.RECORD, purchaseRequestContext.getBillToAddress());
			if (billToLocation.getId() > 0) {
				Chain editLocation = FacilioChainFactory.updateLocationChain();
				editLocation.execute(context);
				purchaseRequestContext.setShipToAddress(billToLocation);
			}
			else {
				Chain addLocation = FacilioChainFactory.addLocationChain();
				addLocation.execute(context);
				long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				billToLocation.setId(locationId);
			}
		}
		else {
			if(purchaseRequestContext.getVendor() != null) {
				LocationContext vendorLocation = purchaseRequestContext.getVendor().getAddress();
				billToLocation.setName("BILL_TO_Location");
				billToLocation.setStreet(vendorLocation.getStreet());
				billToLocation.setState(vendorLocation.getState());
				billToLocation.setZip(vendorLocation.getZip());
				billToLocation.setCountry(vendorLocation.getCountry());
				context.put(FacilioConstants.ContextNames.RECORD, billToLocation);
				Chain addLocation = FacilioChainFactory.addLocationChain();
				addLocation.execute(context);
				long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				billToLocation.setId(locationId);
		
				purchaseRequestContext.setBillToAddress(billToLocation);
			}
		}
	}


	
	private void updateLineItems(PurchaseRequestContext purchaseRequestContext) {
		for (PurchaseRequestLineItemContext lineItemContext : purchaseRequestContext.getLineItems()) {
			lineItemContext.setPrid(purchaseRequestContext.getId());
			updateLineItemCost(lineItemContext);
		}
	}
	
	private void addRecord(List<? extends ModuleBaseWithCustomFields> list, FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>()
				.module(module)
				.fields(fields);
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
	
	private void updateLineItemCost(PurchaseRequestLineItemContext lineItemContext){
		if(lineItemContext.getUnitPrice() > 0) {
		  lineItemContext.setCost(lineItemContext.getUnitPrice() * lineItemContext.getQuantity()); 	
		}
		else {
			//need to check this.fetch is required to get the unit price of item/ tool
			lineItemContext.setCost(0);	
		}
	}
	
	private LocationContext getLocation (PurchaseRequestContext purchaseRequestContext, LocationContext locationContext, String locationName) throws Exception {
		LocationContext location;
		FacilioContext context = new FacilioContext();
		
		if(locationContext != null && locationContext.getLat() != -1 && locationContext.getLng() != -1) {
			location = locationContext;
			location.setName(locationName);
			context.put(FacilioConstants.ContextNames.RECORD, location);
			if (location.getId() > 0) {
				Chain editLocation = FacilioChainFactory.updateLocationChain();
				editLocation.execute(context);
			}
			else {
				Chain addLocation = FacilioChainFactory.addLocationChain();
				addLocation.execute(context);
				long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				location.setId(locationId);
			}
		}
		else {
			location = new LocationContext();
			if(purchaseRequestContext.getStoreRoom() != null) {
				LocationContext storeRoomLocation = purchaseRequestContext.getStoreRoom().getLocation();
				location.setName(locationName);
				location.setStreet(storeRoomLocation.getStreet());
				location.setState(storeRoomLocation.getState());
				location.setZip(storeRoomLocation.getZip());
				location.setCountry(storeRoomLocation.getCountry());
				context.put(FacilioConstants.ContextNames.RECORD, location);
				Chain addLocation = FacilioChainFactory.addLocationChain();
				addLocation.execute(context);
				long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
				location.setId(locationId);		
			}
		}
		return location;
	}


}
