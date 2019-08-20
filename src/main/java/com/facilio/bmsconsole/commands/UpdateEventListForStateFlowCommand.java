package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;

public class UpdateEventListForStateFlowCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);
		if (eventTypes == null) {
			eventTypes = new ArrayList<>();
			context.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, eventTypes);
		}
		EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
		if (!eventTypes.contains(eventType)) {
			eventTypes.add(eventType);
		}
		
		if (!eventTypes.contains(EventType.STATE_TRANSITION)) {
			eventTypes.add(EventType.STATE_TRANSITION);
		}
		return false;
	}

}
