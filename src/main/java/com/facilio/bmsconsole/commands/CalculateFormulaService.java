package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.tenant.RateCardContext;
import com.facilio.bmsconsole.tenant.RateCardServiceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.workflows.util.WorkflowUtil;

public class CalculateFormulaService implements Command {

	@Override
	public boolean execute(Context context) throws Exception {

		TenantContext tenant = (TenantContext) context.get(TenantsAPI.TENANT_CONTEXT);
		RateCardContext rateCard = (RateCardContext) context.get(TenantsAPI.RATECARD_CONTEXT);
		
		
		double utilitySumValue = (double) context.get(TenantsAPI.UTILITY_SUM_VALUE);
		
		
		List<RateCardServiceContext> formulaServices = rateCard.getServiceOfType(RateCardServiceContext.ServiceType.FORMULA.getValue());
		
		double formulaSumValue = 0.0;
		if(formulaServices != null && !formulaServices.isEmpty()) {
			
			Map<Long,Double> formulaVsValue = new HashMap<>();
			
			for(RateCardServiceContext formulaService :formulaServices) {

				Map<String,Object> params = new HashMap<>();
				params.put("value", utilitySumValue);
				
				String workflowValueString = WorkflowUtil.getResult(formulaService.getWorkflowId(), params).toString();
				
				double workflowValue = Double.parseDouble(workflowValueString);
				
				formulaSumValue = formulaSumValue + workflowValue;
				
				formulaVsValue.put(formulaService.getId(), formulaSumValue);
			}
			
			context.put(TenantsAPI.FORMULA_VALUES, formulaVsValue);
		}
		context.put(TenantsAPI.FORMULA_SUM_VALUE, formulaSumValue);
		return false;
	}

}
