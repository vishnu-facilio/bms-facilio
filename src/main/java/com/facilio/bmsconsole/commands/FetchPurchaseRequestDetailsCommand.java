package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.context.PurchaseRequestLineItemContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.modules.fields.FacilioField;;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FetchPurchaseRequestDetailsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		PurchaseRequestContext purchaseRequestContext = (PurchaseRequestContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (purchaseRequestContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String lineItemModuleName = FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS;
			List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			
			SelectRecordsBuilder<PurchaseRequestLineItemContext> builder = new SelectRecordsBuilder<PurchaseRequestLineItemContext>()
					.moduleName(lineItemModuleName)
					.select(fields)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
					.andCondition(CriteriaAPI.getCondition("PR_ID", "prid", String.valueOf(purchaseRequestContext.getId()), NumberOperators.EQUALS))
					.fetchLookups(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
					(LookupField) fieldsAsMap.get("toolType")));
		
			List<PurchaseRequestLineItemContext> list = builder.get();
			purchaseRequestContext.setLineItems(list);
		}
		return false;
	}

}
