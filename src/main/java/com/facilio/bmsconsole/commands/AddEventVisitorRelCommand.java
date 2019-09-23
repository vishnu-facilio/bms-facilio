package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.EventVisitorRelContext;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorEventContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorEventsAPI;
import com.facilio.constants.FacilioConstants;

public class AddEventVisitorRelCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorContext> visitors = (List<VisitorContext>)context.get(FacilioConstants.ContextNames.VISITOR_EVENT_INVITEES);
		List<EventVisitorRelContext> eventVisitorRelList = new ArrayList<EventVisitorRelContext>();
		
		long eventId = (long)context.get(FacilioConstants.ContextNames.VISITOR_EVENT_ID);
		VisitorEventContext visitorEvent = VisitorEventsAPI.getVisitorEvent(eventId);
		
		if(CollectionUtils.isNotEmpty(visitors)) {
			List<VisitorLoggingContext> visitorLoggingList = new ArrayList<VisitorLoggingContext>();
			
			for(VisitorContext visitor : visitors) {
				EventVisitorRelContext eventVisitorRel = new EventVisitorRelContext();
				eventVisitorRel.setEventId(visitorEvent);
				eventVisitorRel.setVisitorId(visitor);
				eventVisitorRelList.add(eventVisitorRel);
				if(visitorEvent.isInviteeApprovalNeeded()) {
					eventVisitorRel.setIsApprovalNeeded(true);
				}
				else {
					VisitorLoggingContext visitorLogging = new VisitorLoggingContext();
					visitorLogging.setEvent(visitorEvent);
					visitorLogging.setEventHost(visitorEvent.getEventHost());
					visitorLogging.setVisitor(visitor);
					visitorLogging.setIsApprovalNeeded(visitorEvent.getIsApprovalNeeded());
					visitorLoggingList.add(visitorLogging);
				}
				
			}
		context.put(FacilioConstants.ContextNames.RECORD_LIST, eventVisitorRelList);	
		context.put(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS, visitorLoggingList);	
		
		}
		return false;
	}

}
