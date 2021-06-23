package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.CustomFilterContext;
import com.facilio.bmsconsole.util.FiltersAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;

public class CheckForCustomFilterAndGenerateCriteria extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long filterId = (Long) context.get(FacilioConstants.ContextNames.FILTER_IDS);
		
		if (filterId != null && filterId > 0) {			
			CustomFilterContext customFilter = FiltersAPI.getCustomFilter(filterId);

				if (customFilter.getCriteriaId() > 0) {
					Criteria customFilterCriteria = CriteriaAPI.getCriteria(customFilter.getCriteriaId());
					context.put(FacilioConstants.ContextNames.CUSTOM_FILTER_CRITERIA, customFilterCriteria);
				}
			
		}
		return false;
	}

}
