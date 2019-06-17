package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WarrantyContractContext;
import com.facilio.bmsconsole.context.WarrantyContractLineItemContext;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class FetchWarrantyContractDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WarrantyContractContext serviceContractContext = (WarrantyContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (serviceContractContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String lineItemModuleName = FacilioConstants.ContextNames.WARRANTY_CONTRACTS_LINE_ITEMS;
			List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			
			SelectRecordsBuilder<WarrantyContractLineItemContext> builder = new SelectRecordsBuilder<WarrantyContractLineItemContext>()
					.moduleName(lineItemModuleName)
					.select(fields)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
					.andCondition(CriteriaAPI.getCondition("WARRANTY_CONTRACT", "warrantyContractId", String.valueOf(serviceContractContext.getId()), NumberOperators.EQUALS))
					.fetchLookups(Arrays.asList((LookupField) fieldsAsMap.get("service")));
		
			List<WarrantyContractLineItemContext> list = builder.get();
			serviceContractContext.setAssetIds(ContractsAPI.fetchAssociatedAssets(serviceContractContext.getId()));
			serviceContractContext.setLineItems(list);
		}
		return false;
	}

}
