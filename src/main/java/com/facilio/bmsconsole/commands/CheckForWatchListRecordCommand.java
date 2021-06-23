package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.context.WatchListContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;

public class CheckForWatchListRecordCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorLoggingContext> list = (List<VisitorLoggingContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(list)) {
			for(VisitorLoggingContext vLog : list) {
				VisitorContext visitor = vLog.getVisitor();
				if(visitor != null) {
					if(visitor.getId() > 0) {
						visitor = VisitorManagementAPI.getVisitor(visitor.getId(), null);
					}
					WatchListContext watchListRecord = VisitorManagementAPI.getBlockedWatchListRecordForPhoneNumber(visitor.getPhone(), visitor.getEmail());
					if(watchListRecord != null) {
						if(watchListRecord.isVip()) {
							vLog.setIsVip(true);
						}
						else if(watchListRecord.isBlocked()) {
							vLog.setIsBlocked(true);
						}
					}
				}
			}
		}
		return false;
	}

}
