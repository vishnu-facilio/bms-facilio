package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class UpdateEventListForStateFlowCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        updateEventListForStateTransition(context);
        return false;
    }

    public static void updateEventListForStateTransition(Context context) {
        List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);
        if (eventTypes == null) {
            eventTypes = new ArrayList<>();
            context.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, eventTypes);
        }
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        if (eventType != null && !eventTypes.contains(eventType)) {
            eventTypes.add(eventType);
        }

        if (!eventTypes.contains(EventType.STATE_TRANSITION)) {
            eventTypes.add(EventType.STATE_TRANSITION);
        }
        if (!eventTypes.contains(EventType.APPROVAL)) {
            eventTypes.add(EventType.APPROVAL);
        }
    }
}
