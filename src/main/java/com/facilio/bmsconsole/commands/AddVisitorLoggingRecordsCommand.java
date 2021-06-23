package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.constants.FacilioConstants;

public class AddVisitorLoggingRecordsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorLoggingContext> visitorLoggingList = (List<VisitorLoggingContext>)context.get(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS);
		if(CollectionUtils.isNotEmpty(visitorLoggingList)) {
			context.put(FacilioConstants.ContextNames.RECORD_LIST, visitorLoggingList);
		}
		return false;
	}

}
