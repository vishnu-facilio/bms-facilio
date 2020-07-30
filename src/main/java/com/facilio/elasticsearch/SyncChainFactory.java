package com.facilio.elasticsearch;

import com.facilio.chain.FacilioChain;
import com.facilio.elasticsearch.command.ConstructESSearchCommand;
import com.facilio.elasticsearch.command.RemoveSyncFromESCommand;
import com.facilio.elasticsearch.command.PushDataToESCommand;
import com.facilio.elasticsearch.job.SyncDataToESCommand;

public class SyncChainFactory {
    private static FacilioChain getTransactionChain() {
        return FacilioChain.getTransactionChain();
    }
    private static FacilioChain getReadChain() {
        return FacilioChain.getNonTransactionChain();
    }

    public static FacilioChain getInvokeSyncChain() {
        FacilioChain chain = getTransactionChain();
        chain.addCommand(new SyncDataToESCommand());
        chain.addCommand(new PushDataToESCommand());
        return chain;
    }

    public static FacilioChain getSearchChain() {
        FacilioChain chain = getReadChain();
        chain.addCommand(new ConstructESSearchCommand());
        return chain;
    }

    public static FacilioChain getRemoveSyncChain() {
        FacilioChain chain = getTransactionChain();
        chain.addCommand(new RemoveSyncFromESCommand());
        return chain;
    }
}
