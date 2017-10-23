package com.facilio.bmts.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmts.bmsconsole.context.EventContext;
import com.facilio.bmts.constants.BmtsConstants;

public class ExecuteEventRuleCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		Map<String, Object> props = (Map<String, Object>) context.get(BmtsConstants.EVENT_PROPERTY);
		if((Boolean) props.get("hasEventRule"))
		{
			EventContext event = (EventContext) context.get(BmtsConstants.EVENT);
			boolean ignoreEvent = false;
			if((Boolean) props.get("hasEventFilter"))
			{
				Criteria criteria = new Criteria();
				ignoreEvent = criteria.computePredicate().evaluate(event);
			}
			if(!ignoreEvent)
			{
				if((Boolean) props.get("hasTransformRule"))
				{
					//TODO apply transform
				}
				if((Boolean) props.get("hasThresholdRule"))
				{
					Criteria criteria = new Criteria();
					ignoreEvent = criteria.computePredicate().evaluate(event);
				}
			}
			context.put(BmtsConstants.IGNORE_EVENT, ignoreEvent);
		}
		return false;
	}
}
