package com.facilio.qa.rules.commands;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.pojo.ActionRuleCondition;
import com.facilio.qa.rules.pojo.QAndARuleType;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.sql.SQLException;
import java.util.*;

@Log4j
public class AddRuleActionCommand extends FacilioCommand {

    private static final String Q_AND_A_RULE = "Q_AND_A_RULE_";

    @Override
    public boolean executeCommand(Context context) throws Exception {

        QAndARuleType type = (QAndARuleType) context.get(Constants.Command.RULE_TYPE);

        if (!type.isConditionBasedActions()) {
            return false;
        }

        List<ActionRuleCondition> ruleConditions = (List<ActionRuleCondition>) context.get(Constants.Command.ACTIONS_TO_BE_ADDED);
        if (CollectionUtils.isNotEmpty(ruleConditions)) {
            for (ActionRuleCondition actionRuleCondition : ruleConditions) {
                List<ActionContext> actions = actionRuleCondition.getActions();
                if (CollectionUtils.isNotEmpty(actions)) {
                    addRuleActionRel(actionRuleCondition.getRuleId(), actionRuleCondition.getId(),ActionAPI.addQandARuleActions(validateBeforeAddActions(actions), Q_AND_A_RULE + actionRuleCondition.getId() + actionRuleCondition.getRuleId()));
                }
            }
        }
        return false;
    }

    private List<ActionContext> validateBeforeAddActions(List<ActionContext> actions) {
        List<ActionContext> actionContexts = new ArrayList<>();
        for (ActionContext action : actions) {
            ActionContext actionContext = new ActionContext();
            actionContext.setActionType(action.getActionType());
            actionContext.setTemplateJson(action.getTemplateJson());
            actionContexts.add(actionContext);
        }
        return actionContexts;
    }

    private void addRuleActionRel(Long ruleId, Long conditionId, List<ActionContext> actions) throws SQLException {

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(Constants.ModuleFactory.evalRuleActionRelModule().getTableName())
                .fields(Constants.FieldFactory.evalRuleActionRelFields());

        for (ActionContext action : actions) {

            Map<String, Object> props = new HashMap<>();
            props.put("ruleId", ruleId);
            props.put("conditionId",conditionId);
            props.put("actionId", action.getId());

            insertBuilder.addRecord(props);
            insertBuilder.save();
        }
    }
}
