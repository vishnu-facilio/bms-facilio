package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;

public class CheckForblockedVisitorCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorLoggingContext> list = (List<VisitorLoggingContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(list)) {
			for(VisitorLoggingContext vLog : list) {
				VisitorContext visitor = VisitorManagementAPI.getVisitor(vLog.getVisitor().getId(), null);
				if(visitor.isBlocked()) {
					throw new IllegalArgumentException("This visitor is prevented from Checking In");
				}
			}
		}
		return false;
	}

}
