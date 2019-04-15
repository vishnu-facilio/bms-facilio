package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PMIncludeExcludeResourceContext;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class PreparePMForMultipleAsset implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		PMJobsContext pmJob = (PMJobsContext) context.get(FacilioConstants.ContextNames.PM_CURRENT_JOB);
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);

		if(pm != null && pm.getPmCreationTypeEnum() != null && pm.getPmCreationTypeEnum().equals(PreventiveMaintenance.PMCreationType.MULTIPLE) && pmJob.getResourceId() > 0) {
			PMIncludeExcludeResourceContext pmIncludeExcludeResourceContext = new PMIncludeExcludeResourceContext();
			pmIncludeExcludeResourceContext.setPmId(pm.getId());
			pmIncludeExcludeResourceContext.setIsInclude(true);
			pmIncludeExcludeResourceContext.setResourceId(pmJob.getResourceId());
			pm.setPmIncludeExcludeResourceContexts(Collections.singletonList(pmIncludeExcludeResourceContext));
		}
		return false;
	}

}
