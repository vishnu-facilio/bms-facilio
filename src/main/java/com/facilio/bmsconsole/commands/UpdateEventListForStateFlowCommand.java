package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;

public class UpdateEventListForStateFlowCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		WorkOrderContext workOrder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if (workOrder != null) {
			User assignedTo = workOrder.getAssignedTo();
			Group assignmentGroup = workOrder.getAssignmentGroup();
			if (assignedTo != null && assignedTo.getId() > 0) {
				addEventType(EventType.ASSIGN_TICKET, context);
			}
			if (assignmentGroup != null && assignmentGroup.getId() > 0) {
				addEventType(EventType.ASSIGN_TICKET, context);
			}
		}
		return false;
	}

	private void addEventType(EventType assignTicket, Context context) {
		List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);
		if (eventTypes == null) {
			eventTypes = new ArrayList<>();
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
