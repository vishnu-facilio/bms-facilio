package com.facilio.mailtracking.commands;

import com.facilio.chain.FacilioChain;

public class MailTransactionChainFactory {

    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain pushOutgoingMailToQueue() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new InsertOutgoingMailLoggerCommand());
        c.addCommand(new PushOutgoingMailToQueueCommand());
        return c;
    }

    public static FacilioChain trackOutgoingMailChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateGlobalMapperIdCommand());
        c.addCommand(new InsertOutgoingRecipientsCommand());
        c.addCommand(new SendMailCommand());
        c.addCommand(new UpdateMailMessageIdCommand());
        c.addCommand(new UpdateRecipientStatusCommand());
        c.addCommand(new TriggerMailHandlerCommmand());
        return c;
    }

}
