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

import com.facilio.bmsconsole.context.CustomFilterContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;

public class ConstructCustomFiltersListCommand extends FacilioCommand  {
	

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<Long, Criteria> criteriaMap = (Map<Long, Criteria>) context.get(FacilioConstants.ContextNames.CRITERIA_MAP);
		Map<Long, JSONObject> filtersMap = (Map<Long, JSONObject>) context.get(FacilioConstants.ContextNames.FILTERS_MAP);
		List<CustomFilterContext> customFilters = (List<CustomFilterContext>) context.get(FacilioConstants.ContextNames.CUSTOM_FILTERS_LIST);
		if (customFilters != null && !customFilters.isEmpty()) {
			for (CustomFilterContext customFilter: customFilters) {
				if (customFilter.getCriteriaId() > 0 && criteriaMap.get(customFilter.getCriteriaId()) != null) {
					customFilter.setCriteria(criteriaMap.get(customFilter.getCriteriaId()));
				}
				if (customFilter.getCriteriaId() > 0 && filtersMap.get(customFilter.getCriteriaId()) != null) {
					customFilter.setFilters(filtersMap.get(customFilter.getCriteriaId()).toString());
				}
			}	
		}
		context.put(FacilioConstants.ContextNames.CUSTOM_FILTERS_LIST, customFilters);
		return false;
	}
	
}
