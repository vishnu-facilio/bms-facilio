package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class GetV2EventDetailCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long eventId = (Long) context.get(EventConstants.EventContextNames.EVENT_ID);
        if (eventId != null) {
            BaseEventContext event = NewEventAPI.getEvent(eventId);
            context.put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(event));
        }
        return false;
    }
}
