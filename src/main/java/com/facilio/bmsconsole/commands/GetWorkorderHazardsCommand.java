package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkorderHazardContext;
import com.facilio.bmsconsole.util.HazardsAPI;
import com.facilio.constants.FacilioConstants;

public class GetWorkorderHazardsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext workorder = (WorkOrderContext)context.get(FacilioConstants.ContextNames.RECORD);
		if(workorder != null && workorder.getId() > 0 && AccountUtil.isFeatureEnabled(FeatureLicense.SAFETY_PLAN)) {
			List<WorkorderHazardContext> workorderHazards = HazardsAPI.fetchWorkorderAssociatedHazards(workorder.getId());
			if(CollectionUtils.isNotEmpty(workorderHazards)) {
				List<Long> hazardIds = new ArrayList<Long>();
				for(WorkorderHazardContext sfh : workorderHazards) {
					hazardIds.add(sfh.getHazard().getId());
				}
				workorder.setHazardIds(hazardIds);
			}
		}
		return false;
	}

}
