package com.facilio.queue;

import com.facilio.aws.util.AwsUtil;

public class QueueFactory {

    private static FacilioQueue getInMemoryQueue(){
        return InMemoryQueueService.getInstance();
    }

    private static FacilioQueue getSqsQueue(){
        return SQSQueue.getInstance();
    }

    public static FacilioQueue getQueue() {
        if(AwsUtil.isProduction() && ! AwsUtil.disableCSP()) {
            return getSqsQueue();
        } else {
            return getInMemoryQueue();
        }
    }

}

