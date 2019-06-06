package com.facilio.events.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.events.context.EventRule;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class EventTransformCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventRule rule = (EventRule) context.get(EventConstants.EventContextNames.EVENT_RULE);
		if(rule != null) {
			EventContext event = (EventContext) context.get(EventConstants.EventContextNames.EVENT);
			if(event.getEventStateEnum() != EventState.IGNORED) {
				if(rule.getTransformCriteriaId() != -1)	{
					long orgId = AccountUtil.getCurrentOrg().getId();
					Criteria criteria = CriteriaAPI.getCriteria(orgId, rule.getTransformCriteriaId());
					boolean isMatched = criteria.computePredicate().evaluate(event);
					if(isMatched) {
						TemplateAPI.getTemplate(rule.getTransformAlertTemplateId());
					}
				}
				event.setInternalState(EventInternalState.TRANSFORMED);
				context.put(EventConstants.EventContextNames.EVENT, event);
			}
		}
		return false;
	}

}
