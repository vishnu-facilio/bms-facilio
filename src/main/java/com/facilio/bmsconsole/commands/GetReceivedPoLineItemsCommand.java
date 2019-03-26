package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetReceivedPoLineItemsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long poId = (Long) context.get(FacilioConstants.ContextNames.PO_ID);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String lineItemModuleName = FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS;
		List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
		
		
		SelectRecordsBuilder<PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<PurchaseOrderLineItemContext>()
																	.moduleName(lineItemModuleName)
																	.select(fields)
																	.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
																	.andCondition(CriteriaAPI.getCondition("PO_ID", "poid", String.valueOf(poId), NumberOperators.EQUALS))
															        .andCustomWhere("QUANTITY_RECEIVED > 0");
															        ;
		List<PurchaseOrderLineItemContext> receivedItems = builder.get();	
		context.put(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS, receivedItems);
		return false;
	}

}
