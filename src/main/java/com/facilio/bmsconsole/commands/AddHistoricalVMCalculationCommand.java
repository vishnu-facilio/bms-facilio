package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.constants.FacilioConstants;

public class AddHistoricalVMCalculationCommand implements Command {

	@Override
	public boolean execute (Context context) throws Exception {
		
		long startTime =  (long) context.get(FacilioConstants.ContextNames.STARTTIME);
		long endTime =  (long) context.get(FacilioConstants.ContextNames.ENDTIME);
		int interval = (int) context.get(FacilioConstants.ContextNames.INTERVAL);
		List<Long> vmList =  (List<Long>) context.get(FacilioConstants.ContextNames.VM_LIST);
		DeviceAPI.addVirtualMeterReadingsJob(startTime, endTime, interval, vmList);
		return false;
	}

}
