package com.facilio.classification.chain;

import com.facilio.chain.FacilioChain;
import com.facilio.classification.command.*;

public class ClassificationChain {

    public static FacilioChain getAfterSaveChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new AfterSaveClassificationCommand());
        return chain;
    }

    public static FacilioChain getAfterSummaryChain() {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new GetClassificationAppliedModulesCommand());
        chain.addCommand(new GetClassificationAssociatedAttributesCommand());
        chain.addCommand(new ResolveClassificationPathCommand());
        return chain;
    }

    public static FacilioChain getAfterListChain() {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new ResolveClassificationPathCommand());
        chain.addCommand(new GetHasChildCommand());
        return chain;
    }

    public static FacilioChain getBeforeListChain() {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new ValidateListCommand());
        return chain;
    }

    public static FacilioChain getBeforeUpdateChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new BeforeUpdateClassificationCommand());
        return chain;
    }
}
