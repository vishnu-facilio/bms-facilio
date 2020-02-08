package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.util.HazardsAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteHazardsFromAssociatedModulesCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(CollectionUtils.isNotEmpty(recordIds)) {
			HazardsAPI.deleteSafetyPlanHazard(recordIds);
			HazardsAPI.deleteWorkOrderHazard(recordIds);
		}
		return false;
	}

}
