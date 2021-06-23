package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchaseContractContext;
import com.facilio.bmsconsole.context.PurchaseContractLineItemContext;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class FetchPurchaseContractDetailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		PurchaseContractContext purchaseContractContext = (PurchaseContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (purchaseContractContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String lineItemModuleName = FacilioConstants.ContextNames.PURCHASE_CONTRACTS_LINE_ITEMS;
			List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			
			SelectRecordsBuilder<PurchaseContractLineItemContext> builder = new SelectRecordsBuilder<PurchaseContractLineItemContext>()
					.moduleName(lineItemModuleName)
					.select(fields)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
					.andCondition(CriteriaAPI.getCondition("PURCHASE_CONTRACT", "purchaseContractId", String.valueOf(purchaseContractContext.getId()), NumberOperators.EQUALS))
					.fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
					(LookupField) fieldsAsMap.get("toolType")));
		
			List<PurchaseContractLineItemContext> list = builder.get();
			purchaseContractContext.setTermsAssociated(ContractsAPI.fetchAssociatedTerms(purchaseContractContext.getId()));
			
			purchaseContractContext.setLineItems(list);
		}
		return false;
	}

}
