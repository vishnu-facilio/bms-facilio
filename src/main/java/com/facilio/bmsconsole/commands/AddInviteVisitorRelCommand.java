package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.InviteVisitorRelContext;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;

public class AddInviteVisitorRelCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorContext> visitors = (List<VisitorContext>)context.get(FacilioConstants.ContextNames.INVITEES);
		List<InviteVisitorRelContext> eventVisitorRelList = new ArrayList<InviteVisitorRelContext>();
		
		long eventId = (long)context.get(FacilioConstants.ContextNames.VISITOR_INVITE_ID);
		VisitorInviteContext visitorEvent = VisitorManagementAPI.getVisitorEvent(eventId);
		
		if(CollectionUtils.isNotEmpty(visitors)) {
			for(VisitorContext visitor : visitors) {
				InviteVisitorRelContext eventVisitorRel = new InviteVisitorRelContext();
				eventVisitorRel.setInviteId(visitorEvent);
				eventVisitorRel.setVisitorId(visitor);
				eventVisitorRelList.add(eventVisitorRel);
				if(visitorEvent.isInviteeApprovalNeeded()) {
					eventVisitorRel.setIsApprovalNeeded(true);
				}
				else {
					eventVisitorRel.setIsApprovalNeeded(false);
				}
//				else {
//					VisitorLoggingContext visitorLogging = new VisitorLoggingContext();
//					visitorLogging.setInvite(visitorEvent);
//					visitorLogging.setHost(visitorEvent.getInviteHost());
//					visitorLogging.setVisitor(visitor);
//					visitorLogging.setIsApprovalNeeded(visitorEvent.getIsApprovalNeeded());
//					visitorLoggingList.add(visitorLogging);
//				}
				
			}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, eventVisitorRelList);	
	//	context.put(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, visitorLoggingList);	
		
		}
		return false;
	}

}
