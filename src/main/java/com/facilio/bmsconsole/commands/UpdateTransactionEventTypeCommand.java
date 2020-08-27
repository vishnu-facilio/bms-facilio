package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class UpdateTransactionEventTypeCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);
        if (eventTypes == null) {
            eventTypes = new ArrayList<>();
            context.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, eventTypes);
        }
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        if (eventType != null && !eventTypes.contains(eventType)) {
            eventTypes.add(eventType);
        }

        if (!eventTypes.contains(EventType.TRANSACTION)) {
            eventTypes.add(EventType.TRANSACTION);
        }

        return false;
    }
}
