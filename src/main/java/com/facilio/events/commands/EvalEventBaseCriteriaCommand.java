package com.facilio.events.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventRule;

public class EvalEventBaseCriteriaCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
