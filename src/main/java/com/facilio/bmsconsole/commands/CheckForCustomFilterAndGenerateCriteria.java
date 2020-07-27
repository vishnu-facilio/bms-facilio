package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CustomFilterContext;
import com.facilio.bmsconsole.util.FiltersAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;

public class CheckForCustomFilterAndGenerateCriteria implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> filterIds = (List<Long>) context.get(FacilioConstants.ContextNames.FILTER_IDS);
		
		if (filterIds != null && filterIds.size() > 0) {
			Long filterId = filterIds.get(0);
			
			CustomFilterContext customFilter = FiltersAPI.getCustomFilter(filterId);

				if (customFilter.getCriteriaId() > 0) {
					Criteria customFilterCriteria = CriteriaAPI.getCriteria(customFilter.getCriteriaId());
					context.put(FacilioConstants.ContextNames.CUSTOM_FILTER_CRITERIA, customFilterCriteria);
				}
			
		}
		return false;
	}

}
