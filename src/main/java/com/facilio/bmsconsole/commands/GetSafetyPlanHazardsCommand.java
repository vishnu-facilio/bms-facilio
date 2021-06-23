package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.SafetyPlanContext;
import com.facilio.bmsconsole.context.SafetyPlanHazardContext;
import com.facilio.bmsconsole.util.HazardsAPI;
import com.facilio.constants.FacilioConstants;

import java.util.ArrayList;
import java.util.List;

public class GetSafetyPlanHazardsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		SafetyPlanContext safetyPlan = (SafetyPlanContext)context.get(FacilioConstants.ContextNames.RECORD);
		if(safetyPlan != null) {
			List<SafetyPlanHazardContext> safetyPlanHazards = HazardsAPI.fetchAssociatedHazards(safetyPlan.getId());
			if(CollectionUtils.isNotEmpty(safetyPlanHazards)) {
				List<Long> hazardIds = new ArrayList<Long>();
				for(SafetyPlanHazardContext sfh : safetyPlanHazards) {
					hazardIds.add(sfh.getHazard().getId());
				}
				safetyPlan.setHazardIds(hazardIds);
			}
		}
		return false;
	}

}
