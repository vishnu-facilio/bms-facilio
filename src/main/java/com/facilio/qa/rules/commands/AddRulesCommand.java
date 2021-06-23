package com.facilio.qa.rules.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AddRulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<QAndARule> rulesToBeAdded = (List<QAndARule>) context.get(Constants.Command.RULES_TO_BE_ADDED);
        if (CollectionUtils.isNotEmpty(rulesToBeAdded)) {
            QAndARuleType type = (QAndARuleType) context.get(Constants.Command.RULE_TYPE);
            Constants.getRuleBean().addRules(rulesToBeAdded, type);

            // Handle other additions if needed
        }

        return false;
    }
}
