package com.facilio.qa.rules.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.qa.rules.pojo.RuleCondition;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class DeleteConditionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<RuleCondition> conditionsToBeDeleted = (List<RuleCondition>) context.get(Constants.Command.CONDITIONS_TO_BE_DELETED);
        if (CollectionUtils.isNotEmpty(conditionsToBeDeleted)) {
            QAndARuleType type = (QAndARuleType) context.get(Constants.Command.RULE_TYPE);
            int deletedConditions = Constants.getRuleBean().deleteConditions(conditionsToBeDeleted, type);

            // Handle other deletions if needed
        }

        return false;
    }
}
