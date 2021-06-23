package com.facilio.bmsconsole.commands;


import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;

public class GetAllAreaCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
	//	Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		Boolean fetchHierarchy = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_HIERARCHY, false);
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);

		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		
		Boolean withReadings = (Boolean) context.get(FacilioConstants.ContextNames.WITH_READINGS);
		
		context.put(FacilioConstants.ContextNames.BASE_SPACE_LIST, SpaceAPI.getAllBaseSpaces(filterCriteria, searchCriteria, orderBy, pagination, withReadings, fetchHierarchy));
		
		return false;
	}

}
