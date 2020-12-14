package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsole.context.ServiceVendorContext;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderLineItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class UpdateServiceVendorPriceCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Set<Long> lineItemIds = (Set<Long>) context.get(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS_ID);
		   if (lineItemIds != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

			FacilioModule serviceVendormodule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_VENDOR);
			for (long id : lineItemIds) {
				V3PurchaseOrderLineItemContext lineItem = getPoLineItem(id);
				V3PurchaseOrderContext po = PurchaseOrderAPI.getPoContext(lineItem.getPurchaseOrder().getId());
				if(lineItem.getInventoryTypeEnum() == InventoryType.SERVICE) {
					Map<String, Object> updateMap = new HashMap<>();
					FacilioField lastPriceField = modBean.getField("lastPrice", serviceVendormodule.getName());
					updateMap.put("lastPrice", lineItem.getUnitPrice());
					List<FacilioField> updatedfields = new ArrayList<FacilioField>();
					updatedfields.add(lastPriceField);
					UpdateRecordBuilder<ServiceVendorContext> updateBuilder = new UpdateRecordBuilder<ServiceVendorContext>()
									.module(serviceVendormodule)
									.fields(updatedfields)
									.andCondition(CriteriaAPI.getCondition("SERVICE_ID","serviceId", String.valueOf(lineItem.getService().getId()), NumberOperators.EQUALS))
									.andCondition(CriteriaAPI.getCondition("VENDOR_ID","vendor", String.valueOf(po.getVendor().getId()), NumberOperators.EQUALS))
									;
				     updateBuilder.updateViaMap(updateMap);
				}
			}
			
		}
	
		
		return false;
	}
	
	private V3PurchaseOrderLineItemContext getPoLineItem(long lineItemId) throws Exception {
		  if (lineItemId <= 0) {
				throw new IllegalArgumentException("Invalid lineItem");
		  }
			
		  ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		  FacilioModule purchaseOrderLineItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);

		  SelectRecordsBuilder<V3PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<V3PurchaseOrderLineItemContext>()
					.module(purchaseOrderLineItemModule).select(modBean.getAllFields(purchaseOrderLineItemModule.getName())).beanClass(V3PurchaseOrderLineItemContext.class)
					.andCondition(CriteriaAPI.getIdCondition(lineItemId, purchaseOrderLineItemModule));
		  	
			
			List<V3PurchaseOrderLineItemContext> lItem = builder.get();
		    if(CollectionUtils.isNotEmpty(lItem)) {
		    	return lItem.get(0);
		    }
			return null;
		}
	
}
