package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import java.util.List;


import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.context.WatchListContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;

public class GetLogsForWatchListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WatchListContext watchList = (WatchListContext)context.get(FacilioConstants.ContextNames.RECORD);
		if(watchList != null) {
			VisitorContext visitor = VisitorManagementAPI.getVisitor(-1, watchList.getPhone());
			if(visitor != null) {
				List<VisitorLoggingContext> visitorLoggingList = VisitorManagementAPI.getAllVisitorLogging(visitor.getId());
				watchList.setVisitorLogs(visitorLoggingList);
			}
		}
		return false;
	}

}
