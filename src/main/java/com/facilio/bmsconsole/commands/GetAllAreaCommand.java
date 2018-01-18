package com.facilio.bmsconsole.commands;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;

public class GetAllAreaCommand implements Command{

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
	//	Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		
		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		
		context.put(FacilioConstants.ContextNames.BASE_SPACE_LIST, SpaceAPI.getAllBaseSpaces(filterCriteria, searchCriteria, orderBy));
		
		return false;
	}

}
