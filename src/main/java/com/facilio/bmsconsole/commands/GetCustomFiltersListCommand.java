package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.chain.Context;
import com.facilio.bmsconsole.context.CustomFilterContext;
import com.facilio.bmsconsole.util.FiltersAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;

public class GetCustomFiltersListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long viewId =  (Long) context.get(FacilioConstants.ContextNames.VIEWID);
		if (viewId > -1) {

				List<CustomFilterContext> customFilters = FiltersAPI.getCustomFiltersList(viewId);
				List<Long> criteriaIds = customFilters.stream().map(a -> a.getCriteriaId()).collect(Collectors.toList());
				Map<Long, Criteria> criteriaMap = CriteriaAPI.getCriteriaAsMap(criteriaIds);
				context.put(FacilioConstants.ContextNames.CRITERIA_MAP, criteriaMap);
				context.put(FacilioConstants.ContextNames.CUSTOM_FILTERS_LIST, customFilters);
		}
		return false;
	}

	

}
