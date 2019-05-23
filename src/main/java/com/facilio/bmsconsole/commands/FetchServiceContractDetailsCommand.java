package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ServiceContractContext;
import com.facilio.bmsconsole.context.ServiceContractLineItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class FetchServiceContractDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ServiceContractContext serviceContractContext = (ServiceContractContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (serviceContractContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			String lineItemModuleName = FacilioConstants.ContextNames.SERVICE_CONTRACTS_LINE_ITEMS;
			List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			
			SelectRecordsBuilder<ServiceContractLineItemContext> builder = new SelectRecordsBuilder<ServiceContractLineItemContext>()
					.moduleName(lineItemModuleName)
					.select(fields)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lineItemModuleName))
					.andCondition(CriteriaAPI.getCondition("SERVICE_CONTRACT", "serviceContractId", String.valueOf(serviceContractContext.getId()), NumberOperators.EQUALS))
					.fetchLookups(Arrays.asList((LookupField) fieldsAsMap.get("service")));
		
			List<ServiceContractLineItemContext> list = builder.get();
			serviceContractContext.setLineItems(list);
		}
		return false;
	}

}
