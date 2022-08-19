package com.facilio.classifcation.chain;

import com.facilio.chain.FacilioChain;
import com.facilio.classifcation.command.AfterSaveClassificationCommand;
import com.facilio.classifcation.command.ResolveClassificationPathCommand;
import com.facilio.classifcation.command.ValidateListCommand;

public class ClassificationChain {

    public static FacilioChain getAfterSaveChain() {
        FacilioChain chain = FacilioChain.getTransactionChain();
        chain.addCommand(new AfterSaveClassificationCommand());
        return chain;
    }

    public static FacilioChain getAfterSummaryChain() {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new ResolveClassificationPathCommand());
        return chain;
    }

    public static FacilioChain getAfterListChain() {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new ResolveClassificationPathCommand());
        return chain;
    }

    public static FacilioChain getBeforeListChain() {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new ValidateListCommand());
        return chain;
    }
}
