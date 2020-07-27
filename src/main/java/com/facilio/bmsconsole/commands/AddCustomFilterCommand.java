package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.CustomFilterContext;
import com.facilio.bmsconsole.util.FiltersAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;


public class AddCustomFilterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		CustomFilterContext customFilter = (CustomFilterContext) context.get(FacilioConstants.ContextNames.CUSTOM_FILTER);
		
		if (customFilter != null && (filterCriteria != null || customFilter.getCriteria() != null)) {
			
			CustomFilterContext newCustomFilter = FiltersAPI.addCustomFilter(filterCriteria, customFilter);
			
			context.put(FacilioConstants.ContextNames.CUSTOM_FILTER_CONTEXT, newCustomFilter);
		}
		return false;

	}

}
