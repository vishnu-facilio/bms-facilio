package com.facilio.services.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.facilio.queue.source.MessageSource;

public class KinesisIRecordProcessorFactory implements IRecordProcessorFactory {

    private long orgId;
    private String orgDomainName;
    private MessageSource messageSource;

    public KinesisIRecordProcessorFactory(long orgId, String orgDomainName, MessageSource messageSource){
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        this.messageSource = messageSource;
    }

    @Override
    public IRecordProcessor createProcessor() {
        return new KinesisProcessor(orgId,orgDomainName, messageSource);
    }
}
