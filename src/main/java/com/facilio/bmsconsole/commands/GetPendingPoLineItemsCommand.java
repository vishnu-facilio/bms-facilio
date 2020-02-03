package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

;

public class GetPendingPoLineItemsCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
															        .andCustomWhere("(QUANTITY_RECEIVED < QUANTITY) OR (QUANTITY_RECEIVED IS NULL) ");
															        ;
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		List<LookupField>fetchLookup = Arrays.asList((LookupField) fieldsAsMap.get("toolType"),
																			(LookupField) fieldsAsMap.get("itemType"));
		
		builder.fetchLookups(fetchLookup);
		List<PurchaseOrderLineItemContext> pendingItems = builder.get();	
		context.put(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS, pendingItems);
		return false;
	}

}
