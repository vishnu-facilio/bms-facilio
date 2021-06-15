package com.facilio.qa.rules.commands;

import com.facilio.bmsconsole.commands.filters.FetchOperatorsForFiltersCommand;
import com.facilio.chain.FacilioChain;
import com.facilio.qa.command.FetchPageAndQuestionsCommand;

public class QAndARuleReadOnlyChainFactory {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getNonTransactionChain();
    }

    public static FacilioChain questionOperatorsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SetDataTypeFromQuestionTypes());
        c.addCommand(new FetchOperatorsForFiltersCommand());
        c.addCommand(new MapOperatorsToQuestionTypes());
        return c;
    }

    public static FacilioChain fetchRulesChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchPageAndQuestionsCommand());
        c.addCommand(new FetchAndSerializeRulesOfAPage());

        return c;
    }
}
