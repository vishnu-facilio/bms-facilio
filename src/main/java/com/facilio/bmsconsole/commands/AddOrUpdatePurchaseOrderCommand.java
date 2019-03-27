package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.context.ReceivableContext.Status;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddOrUpdatePurchaseOrderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		PurchaseOrderContext purchaseOrderContext = (PurchaseOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (purchaseOrderContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			
			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
			
			if (CollectionUtils.isEmpty(purchaseOrderContext.getLineItems())) {
				throw new Exception("Line items cannot be empty");
			}
			
//			if (purchaseOrderContext.getVendor() == null) {
//				throw new Exception("Vendor cannot be empty");
//			}
//			
//			if (purchaseOrderContext.getStoreRoom() == null) {
//				throw new Exception("StoreRoom cannot be empty");
//			}
            purchaseOrderContext.setShipToAddress(getStoreRoomLocation(purchaseOrderContext, purchaseOrderContext.getShipToAddress(), "SHIP_TO_Location"));
            purchaseOrderContext.setBillToAddress(getVendorLocation(purchaseOrderContext, purchaseOrderContext.getBillToAddress(), "BILL_TO_Location"));
			
			if (purchaseOrderContext.getId() > 0) {
				if(purchaseOrderContext.getStatusEnum() == PurchaseOrderContext.Status.APPROVED) {
					if(purchaseOrderContext.getVendor() == null || (purchaseOrderContext.getVendor()!=null && purchaseOrderContext.getVendor().getId() == -1)) {
						throw new IllegalArgumentException("Vendor cannot be null for approved Purchase Order");
					}
					if(purchaseOrderContext.getStoreRoom() == null || (purchaseOrderContext.getStoreRoom()!=null && purchaseOrderContext.getStoreRoom().getId() == -1)) {
						throw new IllegalArgumentException("Storeroom cannot be null for approved Purchase Order");
					}
				}
				updateRecord(purchaseOrderContext, module, fields);
				
				DeleteRecordBuilder<PurchaseOrderLineItemContext> deleteBuilder = new DeleteRecordBuilder<PurchaseOrderLineItemContext>()
						.module(lineModule)
						.andCondition(CriteriaAPI.getCondition("PO_ID", "poid", String.valueOf(purchaseOrderContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
			} else {
				
				if(purchaseOrderContext.getOrderedTime() == -1) {
					purchaseOrderContext.setOrderedTime(System.currentTimeMillis());
				}
				purchaseOrderContext.setStatus(PurchaseOrderContext.Status.REQUESTED);
				addRecord(Collections.singletonList(purchaseOrderContext), module, fields);
				FacilioModule receivableModule = modBean.getModule(FacilioConstants.ContextNames.RECEIVABLE);
				ReceivableContext receivableContext = new ReceivableContext();
				receivableContext.setPoId(purchaseOrderContext.getId());
				receivableContext.setStatus(Status.YET_TO_RECEIVE);
				addRecord(Collections.singletonList(receivableContext), receivableModule, modBean.getAllFields(receivableModule.getName()));
			}
			
			updateLineItems(purchaseOrderContext);
			addRecord(purchaseOrderContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
			
			context.put(FacilioConstants.ContextNames.RECORD, purchaseOrderContext);
		}
		return false;
	}

	private void updateLineItems(PurchaseOrderContext purchaseOrderContext) {
		for (PurchaseOrderLineItemContext lineItemContext : purchaseOrderContext.getLineItems()) {
			lineItemContext.setPoId(purchaseOrderContext.getId());
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
	private void updateLineItemCost(PurchaseOrderLineItemContext lineItemContext){
		if(lineItemContext.getUnitPrice() > 0) {
		  lineItemContext.setCost(lineItemContext.getUnitPrice() * lineItemContext.getQuantity()); 	
		}
		else {
			//need to check this.fetch is required to get the unit price of item/ tool
			lineItemContext.setCost(0);	
		}
	}

	private LocationContext getStoreRoomLocation (PurchaseOrderContext purchaseOrderContext, LocationContext locationContext, String locationName) throws Exception {
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
			if(purchaseOrderContext.getStoreRoom() != null) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule("storeRoom");
				
				Long storeRoomId = purchaseOrderContext.getStoreRoom().getId();
				List<FacilioField> fields = modBean.getAllFields(module.getName());
				Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
				
				SelectRecordsBuilder<StoreRoomContext> builder = new SelectRecordsBuilder<StoreRoomContext>()
																.module(module)
																.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
																.select(fields)
																.fetchLookup(new LookupFieldMeta((LookupField) fieldsAsMap.get("location")))
																.andCondition(CriteriaAPI.getIdCondition(storeRoomId, module))
																;
                List<StoreRoomContext> storeRooms = builder.get();
                if(CollectionUtils.isEmpty(storeRooms)) {
                	
                	return location;
                }
				LocationContext storeRoomLocation =storeRooms.get(0).getLocation();
				location.setName(locationName);
				location.setStreet(storeRoomLocation.getStreet());
				location.setState(storeRoomLocation.getState());
				location.setLat(1.1);
				location.setLng(1.1);
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


	private LocationContext getVendorLocation (PurchaseOrderContext purchaseOrderContext, LocationContext locationContext, String locationName) throws Exception {
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
			if(purchaseOrderContext.getVendor() != null) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule("vendors");
				
				Long vendorId = purchaseOrderContext.getVendor().getId();
				List<FacilioField> fields = modBean.getAllFields(module.getName());
				Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
				
				SelectRecordsBuilder<VendorContext> builder = new SelectRecordsBuilder<VendorContext>()
																.module(module)
																.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
																.select(fields)
																.fetchLookup(new LookupFieldMeta((LookupField) fieldsAsMap.get("address")))
																.andCondition(CriteriaAPI.getIdCondition(vendorId, module))
																;
                List<VendorContext> vendors = builder.get();
                if(CollectionUtils.isEmpty(vendors)) {
                	
                	return location;
                }
				LocationContext vendorLocation = vendors.get(0).getAddress();
				location.setName(locationName);
				location.setStreet(vendorLocation.getStreet());
				location.setState(vendorLocation.getState());
				location.setZip(vendorLocation.getZip());
				location.setCountry(vendorLocation.getCountry());
				location.setLat(1.1);
				location.setLng(1.1);
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
