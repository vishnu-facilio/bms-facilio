package com.facilio.events.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventRule;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class EvalEventBaseCriteriaCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<EventRule> eventRules = (List<EventRule>) context.get(EventConstants.EventContextNames.EVENT_RULE_LIST);
		if (eventRules != null && !eventRules.isEmpty()) {
			long orgId = AccountUtil.getCurrentOrg().getId();
			EventContext event = (EventContext) context.get(EventConstants.EventContextNames.EVENT);
			for(EventRule rule : eventRules) {
	            Criteria criteria = CriteriaAPI.getCriteria(orgId, rule.getBaseCriteriaId());
	            boolean isRuleMatched = criteria.computePredicate().evaluate(event);
	            if (isRuleMatched) {
	            	context.put(EventConstants.EventContextNames.EVENT_RULE, rule);
	            	event.setEventRuleId(rule.getEventRuleId());
	            	if (rule.isIgnoreEvent()) {
	            		event.setEventState(EventContext.EventState.IGNORED);
	            	}
	            	event.setInternalState(EventInternalState.FILTERED);
	            }
			}
			event.setInternalState(EventInternalState.FILTERED);
		}
		return false;
	}

}
