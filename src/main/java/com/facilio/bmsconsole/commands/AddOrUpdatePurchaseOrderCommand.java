package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.bmsconsole.context.ReceivableContext.Status;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

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
			purchaseOrderContext.setShipToAddress(LocationAPI.getLocation(purchaseOrderContext.getStoreRoom(), purchaseOrderContext.getShipToAddress(), "SHIP_TO_Location", true));
			purchaseOrderContext.setBillToAddress(LocationAPI.getLocation(purchaseOrderContext.getVendor(), purchaseOrderContext.getBillToAddress(), "BILL_TO_Location", false));
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
				receivableContext.setPoId(purchaseOrderContext);
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
}
