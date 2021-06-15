package com.facilio.qa.rules.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class UpdateRulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<QAndARule> rulesToBeUpdated = (List<QAndARule>) context.get(Constants.Command.RULES_TO_BE_UPDATED);
        if (CollectionUtils.isNotEmpty(rulesToBeUpdated)) {
            QAndARuleType type = (QAndARuleType) context.get(Constants.Command.RULE_TYPE);
            int updated = Constants.getRuleBean().updateRules(rulesToBeUpdated, type);

            // Handle other updates if needed
        }

        return false;
    }
}
