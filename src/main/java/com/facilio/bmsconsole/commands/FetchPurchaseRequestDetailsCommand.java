package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.context.PurchaseRequestLineItemContext;
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

public class FetchPurchaseRequestDetailsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		PurchaseRequestContext purchaseRequestContext = (PurchaseRequestContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (purchaseRequestContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String lineItemModuleName = FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS;
			List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
			
			SelectRecordsBuilder<PurchaseRequestLineItemContext> builder = new SelectRecordsBuilder<PurchaseRequestLineItemContext>()
					.moduleName(lineItemModuleName)
					.select(fields)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
					.andCondition(CriteriaAPI.getCondition("PR_ID", "prid", String.valueOf(purchaseRequestContext.getId()), NumberOperators.EQUALS));
			List<PurchaseRequestLineItemContext> list = builder.get();
			purchaseRequestContext.setLineItems(list);
			if(purchaseRequestContext.getVendor().getId() > 0) {
				
				String vendorModuleName = FacilioConstants.ContextNames.VENDORS;
				FacilioModule vendorModule = modBean.getModule(vendorModuleName);
						
				List<FacilioField> vendorFields = modBean.getAllFields(vendorModuleName);
				Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(vendorFields);
				
				SelectRecordsBuilder<VendorContext> vendorBuilder = new SelectRecordsBuilder<VendorContext>()
						.moduleName(vendorModuleName)
						.select(vendorFields)
						.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(vendorModuleName))
						.andCondition(CriteriaAPI.getIdCondition(purchaseRequestContext.getVendor().getId(), vendorModule))
						.fetchLookups(Arrays.asList(new LookupFieldMeta((LookupField) fieldsAsMap.get("address"))))
						;
				List<VendorContext> vendorlist = vendorBuilder.get();
				if(!CollectionUtils.isEmpty(vendorlist))  {
					purchaseRequestContext.setVendor(vendorlist.get(0));
				}
			}
			if(purchaseRequestContext.getStoreRoom().getId() > 0) {
				String storeRoomModuleName = FacilioConstants.ContextNames.STORE_ROOM;
				FacilioModule storeRoomModule = modBean.getModule(storeRoomModuleName);
						
				List<FacilioField> storeRoomFields = modBean.getAllFields(storeRoomModuleName);
				
				SelectRecordsBuilder<StoreRoomContext> storeRoomBuilder = new SelectRecordsBuilder<StoreRoomContext>()
						.moduleName(storeRoomModuleName)
						.select(storeRoomFields)
						.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(storeRoomModuleName))
						.andCondition(CriteriaAPI.getIdCondition(purchaseRequestContext.getStoreRoom().getId(), storeRoomModule));
				List<StoreRoomContext> storeRoomlist = storeRoomBuilder.get();
				if(!CollectionUtils.isEmpty(storeRoomlist))  {
					purchaseRequestContext.setStoreRoom(storeRoomlist.get(0));
				}
			}
			
		}
		return false;
	}

}
