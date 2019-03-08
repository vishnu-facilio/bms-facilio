package com.facilio.agent;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;

public class AgentProcessorFactory implements IRecordProcessorFactory {

    private long orgId;
    private String orgDomainName;

    public AgentProcessorFactory(long orgId, String orgDomainName){
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
    }

    @Override
    public IRecordProcessor createProcessor() {
        return new AgentProcessor(orgId,orgDomainName);
    }
}
