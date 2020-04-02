package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.OperationAlarmApi;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class OperationAlarmCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(OperationAlarmCommand.class.getName());

	public boolean executeCommand(Context context) throws Exception {
    	long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
    	long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		List<Long> resourceList = (List<Long>) context.getOrDefault(FacilioConstants.ContextNames.RESOURCE_LIST, null);
		Boolean isHistorical = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_HISTORICAL, false);
		OperationAlarmApi.processOutOfCoverage(startTime, endTime, resourceList, context);
	    return false;
	}
}