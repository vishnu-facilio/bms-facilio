package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;

public class UpdateEventListForStateFlowCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if (workOrder != null) {
			addEventType(EventType.STATE_TRANSITION, context);
//			User assignedTo = workOrder.getAssignedTo();
//			Group assignmentGroup = workOrder.getAssignmentGroup();
//			if (assignedTo != null && assignedTo.getId() > 0) {
//				addEventType(EventType.ASSIGN_TICKET, context);
//			}
//			if (assignmentGroup != null && assignmentGroup.getId() > 0) {
//				addEventType(EventType.ASSIGN_TICKET, context);
//			}
		}
		return false;
	}

	private void addEventType(EventType assignTicket, Context context) {
		List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);
		if (eventTypes == null) {
			eventTypes = new ArrayList<>();
			context.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, eventTypes);
		}
		EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
		if (!eventTypes.contains(eventType)) {
			eventTypes.add(eventType);
		}
		
		if (!eventTypes.contains(assignTicket)) {
			eventTypes.add(assignTicket);
		}
	}

}
