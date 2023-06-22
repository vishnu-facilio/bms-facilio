package com.facilio.fsm.commands;

import com.facilio.bmsconsole.commands.GenerateCriteriaFromFilterCommand;
import com.facilio.bmsconsole.commands.GenerateSearchConditionCommand;
import com.facilio.chain.FacilioChain;

public class FSMReadOnlyChainFactory {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getNonTransactionChain();
    }

    public static FacilioChain fetchPeopleListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new GenerateCriteriaFromFilterCommand());
        c.addCommand(new GenerateSearchConditionCommand());
        c.addCommand(new fetchDispatcherResourcesCommand());
        return c;
    }

    public static FacilioChain fetchEventsListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new fetchDispatcherEventsCommand());
        return c;
    }
}
