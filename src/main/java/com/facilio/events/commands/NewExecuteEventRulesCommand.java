package com.facilio.events.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BMSEventContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.NewEventAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventRuleContext;
import com.facilio.modules.FieldUtil;
import com.facilio.workflows.util.WorkflowUtil;

public class NewExecuteEventRulesCommand extends FacilioCommand {

    private static final boolean IS_CASCADING = false;
    private static final Logger logger = LogManager.getLogger(com.facilio.events.constants.ExecuteEventRulesCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<BaseEventContext> events = (List) context.get(EventConstants.EventContextNames.EVENT_LIST);
        List<EventRuleContext> eventRules = (List<EventRuleContext>) context.get(EventConstants.EventContextNames.EVENT_RULE_LIST);
        if (CollectionUtils.isNotEmpty(eventRules) && CollectionUtils.isNotEmpty(events)) {
            Map<String, Object> placeHolders = new HashMap<>();
            CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
            CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);

            for (int itr = 0; itr < events.size(); itr++) {
                BaseEventContext event = events.get(itr);
                if (!(event instanceof BMSEventContext)) {
                    continue;
                }

                BMSEventContext bmsEvent = (BMSEventContext) event;
                Map<String, Object> recordPlaceHolders = new HashMap<>(placeHolders);
                CommonCommandUtil.appendModuleNameInKey(null, "event", FieldUtil.getAsProperties(event), recordPlaceHolders);
                for (EventRuleContext eventRule : eventRules) {
                    try {
                        if (isRulePassed(bmsEvent, eventRule, recordPlaceHolders)) {
                            event = executeRule(bmsEvent, eventRule, recordPlaceHolders);
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

    private boolean isRulePassed(BMSEventContext event, EventRuleContext rule,Map<String, Object> rulePlaceHolders) throws Exception {
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

    private BMSEventContext executeRule(BMSEventContext event, EventRuleContext rule, Map<String, Object> placeHolders) throws Exception {
        switch (rule.getSuccessActionEnum()) {
            case IGNORE:
                event.setEventState(EventContext.EventState.IGNORED);
                break;
            case TRANSFORM:
                event = NewEventAPI.transformEvent(event, rule.getTransformTemplate(), placeHolders);
                break;
        }
        return event;
    }

}
