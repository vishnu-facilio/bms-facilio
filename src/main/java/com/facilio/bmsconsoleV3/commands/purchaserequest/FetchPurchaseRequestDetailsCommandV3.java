package com.facilio.bmsconsoleV3.commands.purchaserequest;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestLineItemContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.CurrencyUtil;
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

					FacilioModule lineItemModule = modBean.getModule(lineItemModuleName);
					if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY) && CurrencyUtil.isMultiCurrencyEnabledModule(lineItemModule)) {
						fields.addAll(FieldFactory.getCurrencyPropsFields(lineItemModule));
					}

					SelectRecordsBuilder<V3PurchaseRequestLineItemContext> builder = new SelectRecordsBuilder<V3PurchaseRequestLineItemContext>()
							.moduleName(lineItemModuleName)
							.select(fields)
							.beanClass(V3PurchaseRequestLineItemContext.class)
							.andCondition(CriteriaAPI.getCondition("PR_ID", "purchaseRequest", String.valueOf(purchaseRequestContext.getId()), NumberOperators.EQUALS))
							.fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
									(LookupField) fieldsAsMap.get("toolType"), (LookupField) fieldsAsMap.get("service"), (LookupField) fieldsAsMap.get("tax")));
        		
        			List<V3PurchaseRequestLineItemContext> list = builder.get();
        			purchaseRequestContext.setLineItems(list);
					Map<String, Object> currencyInfo = CurrencyUtil.getCurrencyInfo();
					for(V3PurchaseRequestLineItemContext lineItem : list){
						CurrencyUtil.checkAndFillBaseCurrencyToRecord(lineItem, currencyInfo);
					}
        			purchaseRequestContext.setTermsAssociated(PurchaseOrderAPI.fetchAssociatedPrTerms(purchaseRequestContext.getId()));
					QuotationAPI.setTaxSplitUp(purchaseRequestContext, purchaseRequestContext.getLineItems(), context);
        		} 	
            }
        }
        
		return false;
	}
}
