package com.facilio.mailtracking.commands;

import com.facilio.chain.FacilioChain;

public class MailReadOnlyChainFactory {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getNonTransactionChain();
    }

    public static FacilioChain getBeforeFetchMailRecipientListChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new OutgoingRecipientStatusFilterCommand());
        c.addCommand(new OutgoingRecipientLoadSupplementsCommand());
        return c;
    }

}
