package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.tenant.RateCardContext;
import com.facilio.bmsconsole.tenant.RateCardServiceContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.workflows.util.WorkflowUtil;

public class CalculateFormulaService implements Command {

	@Override
	public boolean execute(Context context) throws Exception {

		context.get(TenantsAPI.TENANT_CONTEXT);
		RateCardContext rateCard = (RateCardContext) context.get(TenantsAPI.RATECARD_CONTEXT);
		
		
		double utilitySumValue = (double) context.get(TenantsAPI.UTILITY_SUM_VALUE);
		Long startTime = (Long) context.get(TenantsAPI.START_TIME);
		Long endTime = (Long) context.get(TenantsAPI.END_TIME);
		
		List<RateCardServiceContext> formulaServices = rateCard.getServiceOfType(RateCardServiceContext.ServiceType.FORMULA.getValue(), RateCardServiceContext.ServiceType.TAX_FORMULA.getValue());
		
		double formulaSumValue = 0.0;
		if(formulaServices != null && !formulaServices.isEmpty()) {
			Map<Long,Double> formulaVsValue = new HashMap<>();
			List<Map<String, Object>> itemDetails = new ArrayList<>();
			
			for(RateCardServiceContext formulaService :formulaServices) {

				Map<String,Object> params = new HashMap<>();
				params.put("value", utilitySumValue);
				params.put("startTime", startTime);
				params.put("endTime", endTime);
				
				String workflowValueString = WorkflowUtil.getResult(formulaService.getWorkflowId(), params).toString();
				
				double workflowValue = Double.parseDouble(workflowValueString);
				
				if (formulaService.getServiceTypeEnum() == RateCardServiceContext.ServiceType.TAX_FORMULA) {
					context.put(TenantsAPI.TAX_VALUE, workflowValue);
				}
				else {
					Map<String, Object> item = new HashMap<>();
					formulaSumValue = formulaSumValue + workflowValue;
					
					formulaVsValue.put(formulaService.getId(), formulaSumValue);
					
					item.put("name", formulaService.getName());
					item.put("cost", workflowValue);
					itemDetails.add(item);
				}
			}
			
			context.put(TenantsAPI.FORMULA_VALUES, itemDetails);
		}
		context.put(TenantsAPI.FORMULA_SUM_VALUE, formulaSumValue);
		return false;
	}

}
