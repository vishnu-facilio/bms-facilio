package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.constants.FacilioConstants;

public class AddNewVisitorsForInviteCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorContext> visitors = (List<VisitorContext>)context.get(FacilioConstants.ContextNames.INVITEES);
		List<VisitorContext> visiotrsToBeAdded = new ArrayList<VisitorContext>();
		if(CollectionUtils.isNotEmpty(visitors)) {
			for(VisitorContext visitor: visitors) {
				if(visitor.getId() <= 0) {
					visiotrsToBeAdded.add(visitor);
				}
			}
		}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, visiotrsToBeAdded);
		return false;
	}

}
