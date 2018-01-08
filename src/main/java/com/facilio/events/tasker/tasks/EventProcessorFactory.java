package com.facilio.events.tasker.tasks;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;

public class EventProcessorFactory implements IRecordProcessorFactory {
    private long orgId;
    private String orgName;

    public EventProcessorFactory(long orgId, String orgName){
        this.orgId = orgId;
        this.orgName = orgName;
    }

    @Override
    public IRecordProcessor createProcessor() {
        return new EventProcessor(orgId, orgName);
    }
}
