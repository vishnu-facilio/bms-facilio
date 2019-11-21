package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;

public class QrScanVisitorLogCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long recordId = (Long)context.getOrDefault(FacilioConstants.ContextNames.RECORD_ID, -1);
		if(recordId > 0) {
			VisitorLoggingContext vLog = VisitorManagementAPI.getVisitorLoggingTriggers(recordId, false);
			if(vLog != null) {
				if(!vLog.isRecurring()) {
					long currentTime = System.currentTimeMillis();
					if(currentTime < vLog.getExpectedCheckInTime() || currentTime > vLog.getExpectedCheckOutTime()) {
						throw new IllegalArgumentException("Invalid checkin time");
					}
					context.put(FacilioConstants.ContextNames.VISITOR_LOGGING, vLog);
				}
				else {
					VisitorLoggingContext validChildLog = VisitorManagementAPI.getValidChildLogForToday(recordId);
					if(validChildLog == null) {
						throw new IllegalArgumentException("No valid invite log found for the day");
					}
					context.put(FacilioConstants.ContextNames.VISITOR_LOGGING, validChildLog);
				}
			}
			
		}
		return false;
	}

}
