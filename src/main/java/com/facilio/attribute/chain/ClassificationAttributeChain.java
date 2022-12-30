package com.facilio.attribute.chain;

import com.facilio.attribute.command.ClassificationAttributeListFilterCommand;
import com.facilio.chain.FacilioChain;


public class ClassificationAttributeChain {
    public static FacilioChain getBeforeListChain() {
        FacilioChain chain = FacilioChain.getNonTransactionChain();
        chain.addCommand(new ClassificationAttributeListFilterCommand());
        return chain;
    }
}
