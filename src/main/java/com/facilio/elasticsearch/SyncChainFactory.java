package com.facilio.elasticsearch;

import com.facilio.chain.FacilioChain;
import com.facilio.elasticsearch.command.PushDataToESCommand;
import com.facilio.elasticsearch.job.SyncDataToESCommand;

public class SyncChainFactory {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain getSyncChain() {
        FacilioChain chain = getDefaultChain();
        chain.addCommand(new SyncDataToESCommand());
        chain.addCommand(new PushDataToESCommand());
        return chain;
    }
}
