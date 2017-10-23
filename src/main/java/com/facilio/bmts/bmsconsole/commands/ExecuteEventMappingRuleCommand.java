package com.facilio.bmts.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmts.bmsconsole.context.EventContext;
import com.facilio.bmts.constants.BmtsConstants;

public class ExecuteEventMappingRuleCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		boolean ignoreEvent = (Boolean) context.get(BmtsConstants.IGNORE_EVENT);
		if(!ignoreEvent)
		{
			Map<String, Object> props = (Map<String, Object>) context.get(BmtsConstants.EVENT_PROPERTY);
			if((Boolean) props.get("hasMappingRule"))
			{
				EventContext event = (EventContext) context.get(BmtsConstants.EVENT);
			}
		}
		return false;
	}
}
