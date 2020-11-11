package com.facilio.bmsconsoleV3.commands.purchaserequest;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestLineItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FetchPurchaseRequestDetailsCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PurchaseRequestContext> purchaseRequestContexts = recordMap.get(moduleName);
        if(purchaseRequestContexts != null && CollectionUtils.isNotEmpty(purchaseRequestContexts)) {
            for(V3PurchaseRequestContext purchaseRequestContext : purchaseRequestContexts) 
            { 
            	if (purchaseRequestContext != null && purchaseRequestContext.getId() > 0) {
        			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        			String lineItemModuleName = FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS;
        			List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
        			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        			
        			SelectRecordsBuilder<V3PurchaseRequestLineItemContext> builder = new SelectRecordsBuilder<V3PurchaseRequestLineItemContext>()
        					.moduleName(lineItemModuleName)
        					.select(fields)
        					.beanClass(V3PurchaseRequestLineItemContext.class)
        					.andCondition(CriteriaAPI.getCondition("PR_ID", "prid", String.valueOf(purchaseRequestContext.getId()), NumberOperators.EQUALS))
        					.fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
        					(LookupField) fieldsAsMap.get("toolType")));
        		
        			List<V3PurchaseRequestLineItemContext> list = builder.get();
        			purchaseRequestContext.setLineItems(list);
        		} 	
            }
        }
        
		return false;
	}
}
