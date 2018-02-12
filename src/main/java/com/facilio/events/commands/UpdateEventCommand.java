package com.facilio.events.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.util.EventAPI;

public class UpdateEventCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventContext event = (EventContext) context.get(EventConstants.EventContextNames.EVENT);
		event.setInternalState(EventInternalState.COMPLETED);
		EventAPI.updateEvent(event, AccountUtil.getCurrentOrg().getId());
		return false;
	}

}
