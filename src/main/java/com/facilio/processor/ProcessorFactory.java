package com.facilio.processor;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;

public class ProcessorFactory implements IRecordProcessorFactory {

    private long orgId;
    private String orgDomainName;

    public ProcessorFactory(long orgId, String orgDomainName){
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
    }

    @Override
    public IRecordProcessor createProcessor() {
        return new Processor(orgId,orgDomainName);
    }
}
