package com.facilio.events.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.text.StrSubstitutor;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ExpressionContext;
import com.facilio.bmsconsole.criteria.FacilioExpressionParser;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.util.ExpressionAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.events.context.EventRule;
import com.facilio.events.util.EventAPI;

public class EventCoRelationCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventRule rule = (EventRule) context.get(EventConstants.EventContextNames.EVENT_RULE);
		if(rule != null) {
			EventContext event = (EventContext) context.get(EventConstants.EventContextNames.EVENT);
			if(event.getEventStateEnum() != EventState.IGNORED) {
				long expressionId = rule.getCoRelExpressionId();
				if (expressionId != -1) {
					ExpressionContext expression = ExpressionAPI.getExpressionContext(expressionId);
					String expressionStr = StrSubstitutor.replace(expression.getExpressionString(), FieldUtil.getAsProperties(event));
					if(FacilioExpressionParser.isSingleValueReturnTypeExpression(expressionStr)) {
						FacilioExpressionParser parser = new FacilioExpressionParser(expressionStr);
						double result = (double) parser.getResult();
						if(result == 1) {
							switch(rule.getColRelActionEnum()) {
								case IGNORE:
									event.setEventState(EventState.IGNORED);
									break;
								case TRANSFORM:
									long coRelTransformTemplateId = rule.getCoRelTransformTemplateId();
									JSONTemplate template = (JSONTemplate) TemplateAPI.getTemplate(AccountUtil.getCurrentOrg().getId(), coRelTransformTemplateId);
									event = EventAPI.transformEvent(event, template);
									break;
							}
						}
					}
				}
				event.setInternalState(EventInternalState.CO_RELATION_DONE);
				context.put(EventConstants.EventContextNames.EVENT, event);
			}
		}
		
		return false;
	}

}
