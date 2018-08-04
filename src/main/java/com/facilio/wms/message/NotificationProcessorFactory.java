package com.facilio.wms.message;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;

public class NotificationProcessorFactory implements IRecordProcessorFactory {

    public IRecordProcessor createProcessor() {
        return new NotificationProcessor();
    }
}
