package com.facilio.events.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.events.context.EventContext;
import com.facilio.events.constants.EventConstants;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class EventAction extends ActionSupport {

	private List<EventContext> events;
	public List<EventContext> getEvents() {
		return events;
	}
	public void setEvents(List<EventContext> events) {
		this.events = events;
	}
	
	@SuppressWarnings("unchecked")
	public String eventList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		Chain alarmListChain = EventConstants.getEventListChain();
		alarmListChain.execute(context);
		
		setEvents((List<EventContext>) context.get(EventConstants.EVENT_LIST));
		return SUCCESS;
	}
}
