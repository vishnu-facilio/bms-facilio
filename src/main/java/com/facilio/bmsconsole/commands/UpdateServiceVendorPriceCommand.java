package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.context.ServiceVendorContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class UpdateServiceVendorPriceCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Set<Long> lineItemIds = (Set<Long>) context.get(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS_ID);
		   if (lineItemIds != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			FacilioModule serviceVendormodule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_VENDOR);
			for (long id : lineItemIds) {
				PurchaseOrderLineItemContext lineItem = getPoLineItem(id);
				PurchaseOrderContext po = PurchaseOrderAPI.getPoContext(lineItem.getPoId());
				if(lineItem.getInventoryTypeEnum() == InventoryType.SERVICE) {
					Map<String, Object> updateMap = new HashMap<>();
					FacilioField lastPriceField = modBean.getField("lastPrice", serviceVendormodule.getName());
					updateMap.put("lastPrice", lineItem.getUnitPrice());
					List<FacilioField> updatedfields = new ArrayList<FacilioField>();
					updatedfields.add(lastPriceField);
					UpdateRecordBuilder<ServiceVendorContext> updateBuilder = new UpdateRecordBuilder<ServiceVendorContext>()
									.module(serviceVendormodule)
									.fields(updatedfields)
									.andCondition(CriteriaAPI.getCondition("SERVICE","service", String.valueOf(lineItem.getService().getId()), NumberOperators.EQUALS))
									.andCondition(CriteriaAPI.getCondition("VENDOR","vendor", String.valueOf(po.getVendor().getId()), NumberOperators.EQUALS))
									;
				     updateBuilder.updateViaMap(updateMap);
				}
			}
			
		}
	
		
		return false;
	}
	
	private PurchaseOrderLineItemContext getPoLineItem(long lineItemId) throws Exception {
		  if (lineItemId <= 0) {
				throw new IllegalArgumentException("Invalid lineItem");
		  }
			
		  ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		  FacilioModule purchaseOrderLineItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);

		  SelectRecordsBuilder<PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<PurchaseOrderLineItemContext>()
					.module(purchaseOrderLineItemModule).select(modBean.getAllFields(purchaseOrderLineItemModule.getName())).beanClass(PurchaseOrderLineItemContext.class)
					.andCondition(CriteriaAPI.getIdCondition(lineItemId, purchaseOrderLineItemModule));
		  	
			
			List<PurchaseOrderLineItemContext> lItem = builder.get();
		    if(CollectionUtils.isNotEmpty(lItem)) {
		    	return lItem.get(0);
		    }
			return null;
		}
	
}
