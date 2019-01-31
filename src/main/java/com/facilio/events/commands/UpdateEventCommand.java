package com.facilio.events.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.util.EventAPI;

public class UpdateEventCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		EventContext event = (EventContext) context.get(EventConstants.EventContextNames.EVENT);
		event.setInternalState(EventInternalState.COMPLETED);
		EventAPI.updateEvent(event, AccountUtil.getCurrentOrg().getId());
		
		if (event.getAlarmId() != -1) {
			FacilioChain.addPostTrasanction(FacilioConstants.ContextNames.ALARM_ID, event.getAlarmId());
		}
		return false;
	}

}
