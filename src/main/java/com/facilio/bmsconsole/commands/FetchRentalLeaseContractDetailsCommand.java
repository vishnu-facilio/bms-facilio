package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.RentalLeaseContractContext;
import com.facilio.bmsconsole.context.RentalLeaseContractLineItemsContext;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class FetchRentalLeaseContractDetailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		RentalLeaseContractContext rentalLeaseContractContext = (RentalLeaseContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (rentalLeaseContractContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String lineItemModuleName = FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS_LINE_ITEMS;
			List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			
			SelectRecordsBuilder<RentalLeaseContractLineItemsContext> builder = new SelectRecordsBuilder<RentalLeaseContractLineItemsContext>()
					.moduleName(lineItemModuleName)
					.select(fields)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
					.andCondition(CriteriaAPI.getCondition("RENTAL_LEASE_CONTRACT", "rentalLeaseContractId", String.valueOf(rentalLeaseContractContext.getId()), NumberOperators.EQUALS))
					.fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
					(LookupField) fieldsAsMap.get("toolType")));
		
			List<RentalLeaseContractLineItemsContext> list = builder.get();
			rentalLeaseContractContext.setAssociatedAssets(ContractsAPI.fetchAssociatedAssets(rentalLeaseContractContext.getId()));
			rentalLeaseContractContext.setTermsAssociated(ContractsAPI.fetchAssociatedTerms(rentalLeaseContractContext.getId()));
			
			rentalLeaseContractContext.setLineItems(list);
		}
		return false;
	}

	
}
