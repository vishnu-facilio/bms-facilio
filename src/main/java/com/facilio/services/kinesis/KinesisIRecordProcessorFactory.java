package com.facilio.services.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;

public class KinesisIRecordProcessorFactory implements IRecordProcessorFactory {

    private long orgId;
    private String orgDomainName;

    public KinesisIRecordProcessorFactory(long orgId, String orgDomainName){
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
    }

    @Override
    public IRecordProcessor createProcessor() {
        return new KinesisProcessor(orgId,orgDomainName);
    }
}
