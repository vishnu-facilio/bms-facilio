package com.facilio.events.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.util.EventAPI;

public class UpdateEventCommand implements Command {
	
	private static final Logger LOGGER = LogManager.getLogger(UpdateEventCommand.class);

	@Override
	public boolean execute(Context context) throws Exception {
		EventContext event = (EventContext) context.get(EventConstants.EventContextNames.EVENT);
		event.setInternalState(EventInternalState.COMPLETED);
		EventAPI.updateEvent(event, AccountUtil.getCurrentOrg().getId());
		
		if (AccountUtil.getCurrentOrg().getId() == 88l) {
			StringBuilder builder = new StringBuilder("Updating event " + event.getId() + " with alarm id: " + event.getAlarmId())
					.append("\nTruncated Trace\n");
	
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			for (int i = 0; i < Math.min(10, trace.length); i++) {
				builder.append(trace[i])
					.append("\n");
			}
			LOGGER.info(builder.toString());
		}
		
		if (event.getAlarmId() != -1) {
			FacilioChain.addPostTransactionListObject(FacilioConstants.ContextNames.ALARM_ID, event.getAlarmId());
		}
		return false;
	}

}
