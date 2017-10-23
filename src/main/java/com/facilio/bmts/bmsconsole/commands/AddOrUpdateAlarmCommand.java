package com.facilio.bmts.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmts.bmsconsole.context.EventContext;
import com.facilio.bmts.constants.BmtsConstants;

public class AddOrUpdateAlarmCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		boolean ignoreEvent = (Boolean) context.get(BmtsConstants.IGNORE_EVENT);
		if(!ignoreEvent)
		{
			EventContext event = (EventContext) context.get(BmtsConstants.EVENT);
		}
		return false;
	}
}
