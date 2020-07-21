package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.ViewFilterContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;

public class ConstructViewFiltersListCommand extends FacilioCommand  {
	

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<Long, Criteria> criteriaMap = (Map<Long, Criteria>) context.get(FacilioConstants.ContextNames.CRITERIA_MAP);
		Map<Long, JSONObject> filtersMap = (Map<Long, JSONObject>) context.get(FacilioConstants.ContextNames.FILTERS_MAP);
		List<ViewFilterContext> viewFilters = (List<ViewFilterContext>) context.get(FacilioConstants.ContextNames.VIEW_FILTERS_LIST);
		if (viewFilters != null && !viewFilters.isEmpty()) {
			for (ViewFilterContext viewFilter: viewFilters) {
				if (viewFilter.getCriteriaId() > 0 && criteriaMap.get(viewFilter.getCriteriaId()) != null) {
					viewFilter.setCriteria(criteriaMap.get(viewFilter.getCriteriaId()));
				}
				if (viewFilter.getCriteriaId() > 0 && filtersMap.get(viewFilter.getCriteriaId()) != null) {
					viewFilter.setFilters(filtersMap.get(viewFilter.getCriteriaId()).toString());
				}
			}	
		}
		context.put(FacilioConstants.ContextNames.VIEW_FILTERS_LIST, viewFilters);
		return false;
	}
	
}
