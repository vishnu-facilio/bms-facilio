package com.facilio.events.tasker.tasks;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;

public class EventProcessorFactory implements IRecordProcessorFactory {
    private long orgId;
    private String orgDomainName;

    public EventProcessorFactory(long orgId, String orgDomainName){
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
    }

    @Override
    public IRecordProcessor createProcessor() {
        return new EventProcessor(orgId, orgDomainName);
    }
}
