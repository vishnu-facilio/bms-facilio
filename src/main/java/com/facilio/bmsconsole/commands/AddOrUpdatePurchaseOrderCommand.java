package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.bmsconsole.context.ReceivableContext.Status;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

;

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
			
			if (purchaseOrderContext.getId() <= 0 && CollectionUtils.isEmpty(purchaseOrderContext.getLineItems())) {
				throw new Exception("Line items cannot be empty");
			}
			
			if (purchaseOrderContext.getVendor() == null) {
				throw new Exception("Vendor cannot be empty");
			}
			checkForStoreRoom(purchaseOrderContext, purchaseOrderContext.getLineItems());
			// setting current user to requestedBy
			if(purchaseOrderContext.getRequestedBy() == null) {
				purchaseOrderContext.setRequestedBy(AccountUtil.getCurrentUser());
			}
			
			            
			purchaseOrderContext.setShipToAddress(LocationAPI.getPoPrLocation(purchaseOrderContext.getStoreRoom(), purchaseOrderContext.getShipToAddress(), "SHIP_TO_Location", true));
			purchaseOrderContext.setBillToAddress(LocationAPI.getPoPrLocation(purchaseOrderContext.getVendor(), purchaseOrderContext.getBillToAddress(), "BILL_TO_Location", false));
			if (purchaseOrderContext.getId() > 0) {
				RecordAPI.updateRecord(purchaseOrderContext, module, fields);
				if(purchaseOrderContext.getLineItems() != null) {
					DeleteRecordBuilder<PurchaseOrderLineItemContext> deleteBuilder = new DeleteRecordBuilder<PurchaseOrderLineItemContext>()
							.module(lineModule)
							.andCondition(CriteriaAPI.getCondition("PO_ID", "poid", String.valueOf(purchaseOrderContext.getId()), NumberOperators.EQUALS));
					deleteBuilder.delete();
					updateLineItems(purchaseOrderContext);
					RecordAPI.addRecord(false,purchaseOrderContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
			
				}
			} else {
				
				if(purchaseOrderContext.getOrderedTime() == -1) {
					purchaseOrderContext.setOrderedTime(System.currentTimeMillis());
				}
				purchaseOrderContext.setStatus(PurchaseOrderContext.Status.REQUESTED);
				RecordAPI.addRecord(true,Collections.singletonList(purchaseOrderContext), module, fields);
				FacilioModule receivableModule = modBean.getModule(FacilioConstants.ContextNames.RECEIVABLE);
				ReceivableContext receivableContext = new ReceivableContext();
				receivableContext.setPoId(purchaseOrderContext);
				receivableContext.setStatus(Status.YET_TO_RECEIVE);
				RecordAPI.addRecord(true,Collections.singletonList(receivableContext), receivableModule, modBean.getAllFields(receivableModule.getName()));
				updateLineItems(purchaseOrderContext);
				RecordAPI.addRecord(false,purchaseOrderContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
		
			}
			
			
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
	

	private void updateLineItemCost(PurchaseOrderLineItemContext lineItemContext){
		if(lineItemContext.getUnitPrice() > 0) {
		  lineItemContext.setCost(lineItemContext.getUnitPrice() * lineItemContext.getQuantity()); 	
		}
		else {
			//need to check this.fetch is required to get the unit price of item/ tool
			lineItemContext.setCost(0);	
		}
	}
	
	private void checkForStoreRoom(PurchaseOrderContext po, List<PurchaseOrderLineItemContext> lineItems) throws Exception{
		if(CollectionUtils.isNotEmpty(lineItems)) {
			for(PurchaseOrderLineItemContext lineItem : lineItems) {
				if((lineItem.getInventoryType() == InventoryType.ITEM.getValue() || lineItem.getInventoryType() == InventoryType.TOOL.getValue()) && po.getStoreRoom() == null) {
					throw new IllegalArgumentException("Storeroom cannot be null for pos with items and tools");
				}
			}
		}
	}
}
