package com.facilio.backgroundactivity.factory;

import com.facilio.backgroundactivity.commands.AddBackgroundActivityCommand;
import com.facilio.chain.FacilioChain;

public class BackgroundActivityChainFactory {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain addBackgroundActivityChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddBackgroundActivityCommand());
        return c;
    }
}
