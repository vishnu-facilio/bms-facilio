package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;

public class GetVisitorDetailAndLogCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String phoneNumber = (String)context.getOrDefault(FacilioConstants.ContextNames.PHONE_NUMBER, null);
		long currentTime = System.currentTimeMillis();
		if(StringUtils.isNotEmpty(phoneNumber)) {
			VisitorContext visitor = VisitorManagementAPI.getVisitor(-1, phoneNumber);
			if(visitor != null) {
				VisitorLoggingContext vLog = VisitorManagementAPI.getValidChildLogForToday(-1, currentTime, true, visitor.getId());
				context.put(FacilioConstants.ContextNames.VISITOR, visitor);
				context.put(FacilioConstants.ContextNames.VISITOR_LOGGING, vLog);
			}
				
		}
		return false;
	}

}
