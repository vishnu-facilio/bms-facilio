package com.facilio.queue;

import com.facilio.aws.util.AwsUtil;

public class QueueFactory {

    private static boolean productionEnvironment = false;

    static {
        if("production".equals(AwsUtil.getConfig("environment"))) {
            productionEnvironment = true;
        }
    }

    private static FacilioQueue getInMemoryQueue(){
        return InMemoryQueueService.getInstance();
    }

    private static FacilioQueue getSqsQueue(){
        return SQSQueue.getInstance();
    }

    public static FacilioQueue getQueue() {
        if(productionEnvironment) {
            return getSqsQueue();
        } else {
            return getInMemoryQueue();
        }
    }

}

