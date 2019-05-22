package com.facilio.events.constants;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.util.EventAPI;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecuteEventRulesCommand implements Command {

	private static final boolean IS_CASCADING = false;
	private static final Logger logger = LogManager.getLogger(ExecuteEventRulesCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<EventContext> events = (List<EventContext>) context.get(EventConstants.EventContextNames.EVENT_LIST);
		List<EventRuleContext> eventRules = (List<EventRuleContext>) context.get(EventConstants.EventContextNames.EVENT_RULE_LIST);
		if (CollectionUtils.isNotEmpty(eventRules) && CollectionUtils.isNotEmpty(events)) {
			Map<String, Object> placeHolders = new HashMap<>();
			CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
			CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
//			CommonCommandUtil.appendModuleNameInKey(null, "event", FieldUtil.getAsProperties(event), placeHolders);

			for (int itr = 0; itr < events.size(); itr++) {
				EventContext event = events.get(itr);
				Map<String, Object> recordPlaceHolders = new HashMap<>(placeHolders);
				CommonCommandUtil.appendModuleNameInKey(null, "event", FieldUtil.getAsProperties(event), recordPlaceHolders);
				for (EventRuleContext eventRule : eventRules) {
					try {
						if (isRulePassed(event, eventRule, recordPlaceHolders)) {
							event = executeRule(event, eventRule, recordPlaceHolders);
							events.set(itr, event);
							if (!IS_CASCADING) {
								break;
							}
						}
					} catch (Exception e) {
						String errorMsg = "Error occurred during execution of event Rule : " + eventRule.getId();
//						CommonCommandUtil.emailException(errorMsg, e);
						logger.log(Level.ERROR, errorMsg, e);
						throw e;
					}
				}
			}
		}
		return false;
	}
	
	private boolean isRulePassed(EventContext event, EventRuleContext rule,Map<String, Object> rulePlaceHolders) throws Exception {
		boolean criteriaFlag = true;
		if(rule.getCriteria() != null) {
			criteriaFlag = rule.getCriteria().computePredicate(rulePlaceHolders).evaluate(event);
		}
		boolean workflowFlag = true;
		if (rule.getWorkflow() != null) {
			workflowFlag = WorkflowUtil.getWorkflowExpressionResultAsBoolean(rule.getWorkflow(), rulePlaceHolders);
		}
		return criteriaFlag && workflowFlag;
	}
	
	private EventContext executeRule(EventContext event, EventRuleContext rule, Map<String, Object> placeHolders) throws Exception {
		switch (rule.getSuccessActionEnum()) {
			case IGNORE:
				event.setEventState(EventState.IGNORED);
				break;
			case TRANSFORM:
				event = EventAPI.transformEvent(event, rule.getTransformTemplate(), placeHolders);
				break;
		}
		return event;
	}

}
