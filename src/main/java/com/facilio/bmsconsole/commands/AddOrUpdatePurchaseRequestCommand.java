package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext.Status;
import com.facilio.bmsconsole.context.PurchaseRequestLineItemContext;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;



public class AddOrUpdatePurchaseRequestCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		PurchaseRequestContext purchaseRequestContext = (PurchaseRequestContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (purchaseRequestContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS);
			
			if (purchaseRequestContext.getId() <= 0 && CollectionUtils.isEmpty(purchaseRequestContext.getLineItems())) {
				throw new Exception("Line items cannot be empty");
			}
			// setting current user to requestedBy
			if(purchaseRequestContext.getRequestedBy() == null) {
		 	  purchaseRequestContext.setRequestedBy(AccountUtil.getCurrentUser());
			}
			purchaseRequestContext.setShipToAddress(LocationAPI.getPoPrLocation(purchaseRequestContext.getStoreRoom(), purchaseRequestContext.getShipToAddress(), "SHIP_TO_Location", true));
            purchaseRequestContext.setBillToAddress(LocationAPI.getPoPrLocation(purchaseRequestContext.getVendor(), purchaseRequestContext.getBillToAddress(), "BILL_TO_Location", false));
            if (purchaseRequestContext.getId() > 0) {
				RecordAPI.updateRecord(purchaseRequestContext, module, fields);
				if(purchaseRequestContext.getLineItems() != null) {
					DeleteRecordBuilder<PurchaseRequestLineItemContext> deleteBuilder = new DeleteRecordBuilder<PurchaseRequestLineItemContext>()
							.module(lineModule)
							.andCondition(CriteriaAPI.getCondition("PR_ID", "prid", String.valueOf(purchaseRequestContext.getId()), NumberOperators.EQUALS));
					deleteBuilder.delete();
					updateLineItems(purchaseRequestContext);
					RecordAPI.addRecord(false, purchaseRequestContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
				
				}
			} else {
				if(purchaseRequestContext.getRequestedTime() == -1) {
					purchaseRequestContext.setRequestedTime(System.currentTimeMillis());
				}
				
				purchaseRequestContext.setStatus(Status.REQUESTED);
				RecordAPI.addRecord(true, Collections.singletonList(purchaseRequestContext), module, fields);
				updateLineItems(purchaseRequestContext);
				RecordAPI.addRecord(false, purchaseRequestContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
			}
			
			
			context.put(FacilioConstants.ContextNames.RECORD, purchaseRequestContext);
		}
		return false;
	}
	
		private void updateLineItems(PurchaseRequestContext purchaseRequestContext) {
		for (PurchaseRequestLineItemContext lineItemContext : purchaseRequestContext.getLineItems()) {
			lineItemContext.setPrid(purchaseRequestContext.getId());
			updateLineItemCost(lineItemContext);
		}
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
			private LocationContext getLocation (LocationContext locationContext, String locationName,long defaultLocationId) throws Exception {
			LocationContext location;
			FacilioContext context = new FacilioContext();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				location = locationContext;
				location.setName(locationName);
				context.put(FacilioConstants.ContextNames.RECORD, location);
				if (location.getId() > 0) {
					Chain editLocation = FacilioChainFactory.updateLocationChain();
					editLocation.execute(context);
				}
				else {
					if(location.isEmpty()) {
						if(defaultLocationId != -1) {
							FacilioModule module = modBean.getModule("location");
							List<FacilioField> fields = modBean.getAllFields(module.getName());
							
							SelectRecordsBuilder<LocationContext> builder = new SelectRecordsBuilder<LocationContext>()
																			.module(module)
																			.beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
																			.select(fields)
																			.andCondition(CriteriaAPI.getIdCondition(defaultLocationId, module))
																			;
			                List<LocationContext> locations = builder.get();
			                LocationContext locationObj =locations.get(0);
			                location.setName(locationName);
							location.setStreet(locationObj.getStreet());
							location.setState(locationObj.getState());
							location.setLat(1.1);
							location.setLng(1.1);
							location.setZip(locationObj.getZip());
							location.setCountry(locationObj.getCountry());
						}
						else {
							location.setName("SHIP_TO_location");
		                	location.setLat(1.1);
		    				location.setLng(1.1);
		    			}
						
						Chain addLocation = FacilioChainFactory.addLocationChain();
						addLocation.execute(context);
						long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
						location.setId(locationId);
			
					}
				}
				
				return location;
			}
				

}
