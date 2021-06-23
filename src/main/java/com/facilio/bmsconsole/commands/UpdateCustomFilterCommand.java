package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.CustomFilterContext;
import com.facilio.bmsconsole.util.FiltersAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;

public class UpdateCustomFilterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		CustomFilterContext customFilter = (CustomFilterContext) context.get(FacilioConstants.ContextNames.CUSTOM_FILTER);
//		Long filterId =  (Long) context.get(FacilioConstants.ContextNames.FILTER_ID);
//		Criteria criteria = new Criteria();
//		
//		if (filterId > 0) {
//			CustomFilterContext existingFilter = FiltersAPI.getCustomFilter(filterId);
//			Criteria existingCriteria = CriteriaAPI.getCriteria(existingFilter.getCriteriaId());
//			if (existingCriteria != null) {
//				Map<String, Condition> existingCriteriaCondition = existingCriteria.getConditions();
//				
//			}
//		}
//		if (filterCriteria != null) {
//			Map<String, Condition> filterCriteriaCondition = filterCriteria.getConditions();
//			
//		}
		
		customFilter = FiltersAPI.updateCustomFilter(filterCriteria, customFilter);
		
		context.put(FacilioConstants.ContextNames.CUSTOM_FILTER_CONTEXT, customFilter);
		
		return false;
	}

}
