package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.context.ReceivableContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
					.andCondition(CriteriaAPI.getCondition("PO_ID", "poId", String.valueOf(purchaseOrderContext.getId()), NumberOperators.EQUALS))
			        ;
		
			List<ReceivableContext> receivableList = recordsBuilder.get();
			if (receivableList.size() != 1) {
				throw new Exception("Receivable are invalid");
			}
			purchaseOrderContext.setReceivableContext(receivableList.get(0));
			
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			
			SelectRecordsBuilder<PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<PurchaseOrderLineItemContext>()
					.moduleName(lineItemModuleName)
					.select(fields)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
					.andCondition(CriteriaAPI.getCondition("PO_ID", "poid", String.valueOf(purchaseOrderContext.getId()), NumberOperators.EQUALS))
			        .fetchLookups(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
					(LookupField) fieldsAsMap.get("toolType")))
			        ;
			List<PurchaseOrderLineItemContext> list = builder.get();
			purchaseOrderContext.setLineItems(list);
					
		}
		return false;
	}

}
