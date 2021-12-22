package com.facilio.qa.rules.commands;

import com.facilio.chain.FacilioChain;
import com.facilio.qa.command.FetchPageAndQuestionsCommand;

public class QAndARuleTransactionChainFactory {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain addRules() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchPageAndQuestionsCommand());
        c.addCommand(new DeserializeRulesCommand());
        c.addCommand(new SplitRulesToAddOrUpdateRules());
        c.addCommand(new DeleteQandARuleActions());
        c.addCommand(new DeleteConditionsCommand());
        c.addCommand(new UpdateRulesCommand());
        c.addCommand(new AddRulesCommand());
        c.addCommand(new AddConditionsCommand());
        c.addCommand(new AddRuleActionCommand());

        return c;
    }
}
