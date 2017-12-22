package com.facilio.events.tasker.tasks;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;

public class EventProcessorFactory implements IRecordProcessorFactory {
    private long orgId;

    EventProcessorFactory(long orgId){
        this.orgId = orgId;
    }

    @Override
    public IRecordProcessor createProcessor() {
        return new EventProcessor(orgId);
    }
}
