package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.constants.FacilioConstants;

public class AddOrUpdateKPICommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		KPIContext kpi = (KPIContext)context.get(FacilioConstants.ContextNames.KPI);
		if (kpi.getId() > 0) {
			
		}
		else {
			KPIUtil.addKPI(kpi);
		}
		
		return false;
	}

}
