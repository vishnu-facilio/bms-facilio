package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class FetchPurchaseOrderDetailsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		PurchaseOrderContext purchaseOrderContext = (PurchaseOrderContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (purchaseOrderContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String lineItemModuleName = FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS;
			List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
			
			FacilioModule receivableModule = modBean.getModule(FacilioConstants.ContextNames.RECEIVABLE);
			List<FacilioField> receivableFields = modBean.getAllFields(receivableModule.getName());
			SelectRecordsBuilder<ReceivableContext> recordsBuilder = new SelectRecordsBuilder<ReceivableContext>()
					.module(receivableModule)
					.beanClass(ReceivableContext.class)
					.select(receivableFields)
					.andCondition(CriteriaAPI.getCondition("PO_ID", "poId", String.valueOf(purchaseOrderContext.getId()), NumberOperators.EQUALS));
			List<ReceivableContext> receivableList = recordsBuilder.get();
			if (receivableList.size() != 1) {
				throw new Exception("Receivable are invalid");
			}
			purchaseOrderContext.setReceivableContext(receivableList.get(0));
			
			SelectRecordsBuilder<PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<PurchaseOrderLineItemContext>()
					.moduleName(lineItemModuleName)
					.select(fields)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
					.andCondition(CriteriaAPI.getCondition("PO_ID", "poid", String.valueOf(purchaseOrderContext.getId()), NumberOperators.EQUALS));
			List<PurchaseOrderLineItemContext> list = builder.get();
			purchaseOrderContext.setLineItems(list);
			
			if(purchaseOrderContext.getVendor().getId() > 0) {
				String vendorModuleName = FacilioConstants.ContextNames.VENDORS;
				FacilioModule vendorModule = modBean.getModule(vendorModuleName);
						
				List<FacilioField> vendorFields = modBean.getAllFields(vendorModuleName);
				Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(vendorFields);
				
				SelectRecordsBuilder<VendorContext> vendorBuilder = new SelectRecordsBuilder<VendorContext>()
						.moduleName(vendorModuleName)
						.select(vendorFields)
						.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(vendorModuleName))
						.andCondition(CriteriaAPI.getIdCondition(purchaseOrderContext.getVendor().getId(), vendorModule))
						.fetchLookups(Arrays.asList(new LookupFieldMeta((LookupField) fieldsAsMap.get("address"))))
						;

				List<VendorContext> vendorlist = vendorBuilder.get();
				if(!CollectionUtils.isEmpty(vendorlist))  {
					purchaseOrderContext.setVendor(vendorlist.get(0));
				}
			}
			
			if(purchaseOrderContext.getStoreRoom().getId() > 0) {
				
				String storeRoomModuleName = FacilioConstants.ContextNames.STORE_ROOM;
				FacilioModule storeRoomModule = modBean.getModule(storeRoomModuleName);
						
				List<FacilioField> storeRoomFields = modBean.getAllFields(storeRoomModuleName);
				
				SelectRecordsBuilder<StoreRoomContext> storeRoomBuilder = new SelectRecordsBuilder<StoreRoomContext>()
						.moduleName(storeRoomModuleName)
						.select(storeRoomFields)
						.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(storeRoomModuleName))
						.andCondition(CriteriaAPI.getIdCondition(purchaseOrderContext.getStoreRoom().getId(), storeRoomModule));
				List<StoreRoomContext> storeRoomlist = storeRoomBuilder.get();
				if(!CollectionUtils.isEmpty(storeRoomlist))  {
					purchaseOrderContext.setStoreRoom(storeRoomlist.get(0));
				}
			}
		
		}
		return false;
	}

}
