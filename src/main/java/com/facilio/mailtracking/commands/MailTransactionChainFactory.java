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

    public static FacilioChain outgoingMailPreChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateGlobalMapperIdCommand());
        c.addCommand(new InsertOutgoingMailAttachmentsCommand());
        c.addCommand(new InsertOutgoingRecipientsCommand());
        return c;
    }

    public static FacilioChain sendOutgoingMailChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SendMailWithTrackingCommand());
        c.addCommand(outgoingMailPostChain());
        return c;
    }

    private static FacilioChain outgoingMailPostChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateRecipientStatusCommand());
        c.addCommand(new MaskConfidentialUrlCommand());
        c.addCommand(new UpdateMailMessageIdCommand());
        return c;
    }

    public static FacilioChain triggerMailHandlerChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new TriggerMailHandlerCommmand());
        return c;
    }

    public static FacilioChain getNoTrackingChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SendMailWithoutTrackingCommand());
        c.addCommand(outgoingMailPostChain());
        return c;
    }

}
