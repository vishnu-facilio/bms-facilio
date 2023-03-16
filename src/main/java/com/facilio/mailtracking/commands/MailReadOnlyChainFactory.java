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

    public static FacilioChain getAfterFetchMailLoggerSummaryChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new MaskConfidentialUrlCommand());
        c.addCommand(new FetchMailAttachmentsCommand());
        return c;
    }

    public static FacilioChain getApiStatsChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new OutgoingMailStatsCommand());
        return c;
    }

    public static FacilioChain prepareOutgoingMailChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new HandleUserDelegationAndIsActiveCommand());
        return c;
    }
}
