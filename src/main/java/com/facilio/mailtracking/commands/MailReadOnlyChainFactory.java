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

    public static FacilioChain runHistoricChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new RunHistoricalMailCommand());
        return c;
    }

    public static FacilioChain pushToMailTemp() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new PushOutgoingMailToQueueCommand());
        return c;
    }
}
