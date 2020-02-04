package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LabourContractContext;
import com.facilio.bmsconsole.context.LabourContractLineItemContext;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class FetchLabourContractDetailsCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		LabourContractContext labourContractContext = (LabourContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (labourContractContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String lineItemModuleName = FacilioConstants.ContextNames.LABOUR_CONTRACTS_LINE_ITEMS;
			List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			
			SelectRecordsBuilder<LabourContractLineItemContext> builder = new SelectRecordsBuilder<LabourContractLineItemContext>()
					.moduleName(lineItemModuleName)
					.select(fields)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
					.andCondition(CriteriaAPI.getCondition("LABOUR_CONTRACT", "labourContractId", String.valueOf(labourContractContext.getId()), NumberOperators.EQUALS))
					.fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("labour")));
		
			List<LabourContractLineItemContext> list = builder.get();
			labourContractContext.setTermsAssociated(ContractsAPI.fetchAssociatedTerms(labourContractContext.getId()));
			labourContractContext.setLineItems(list);
		}
		return false;
	}
}
