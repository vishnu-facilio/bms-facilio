package com.facilio.queue;

public class QueueFactory {

//    private static FacilioQueue getInMemoryQueue(){
//        return InMemoryQueueService.getInstance();
//    }

    private static FacilioQueue getSqsQueue(){
        return SQSQueue.getInstance();
    }

    public static FacilioQueue getQueue() {
//        if(FacilioProperties.isProduction() && !FacilioProperties.isOnpremise()) {
            return getSqsQueue();
//        } else {
//            return getInMemoryQueue();
//        }
    }

}

