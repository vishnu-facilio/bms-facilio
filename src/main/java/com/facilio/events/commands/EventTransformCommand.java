package com.facilio.events.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.events.context.EventRule;
import com.facilio.events.util.EventAPI;

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
						JSONTemplate template = (JSONTemplate) TemplateAPI.getTemplate(orgId, rule.getTransformAlertTemplateId());
						event = EventAPI.transformEvent(event, template);
					}
				}
				event.setInternalState(EventInternalState.TRANSFORMED);
				event.getMessageKey();
				context.put(EventConstants.EventContextNames.EVENT, event);
			}
		}
		return false;
	}

}
