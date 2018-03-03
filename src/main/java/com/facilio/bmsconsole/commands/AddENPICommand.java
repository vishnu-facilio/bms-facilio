package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.EnergyPerformanceIndicatorContext;
import com.facilio.bmsconsole.util.EnergyPerformanceIndicatiorAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;

public class AddENPICommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EnergyPerformanceIndicatorContext enpi = (EnergyPerformanceIndicatorContext) context.get(FacilioConstants.ContextNames.ENPI);
		
		if (enpi == null) {
			throw new IllegalArgumentException("ENPI cannot be null during addition");
		}
		long enpiId = EnergyPerformanceIndicatiorAPI.addENPI(enpi);
		
		FacilioTimer.scheduleOneTimeJob(enpiId, "HistoricalENPICalculatior", 60, "priority");
		
		return false;
	}
}
