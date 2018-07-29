package com.facilio.queue;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.tasker.FacilioScheduler;
import com.facilio.tasker.config.InstantJobConf;
import com.facilio.tasker.job.InstantJob;

public class InstantJobExecutor implements Runnable {

    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE = 300000L;
    private static final int QUEUE_SIZE = 100;

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<>(QUEUE_SIZE));

    public void run(){
        List<QueueMessage> messageList = ObjectQueue.getObjects("instantJob");
        if(messageList != null) {
            for (QueueMessage message : messageList) {
                while (THREAD_POOL_EXECUTOR.getQueue().size() < 15) {
                    FacilioContext context = (FacilioContext) message.getObject();
                    String jobName = (String) context.get("JOB");
                    if (jobName != null) {
                        InstantJobConf.Job instantJob = FacilioScheduler.getInstantJob(jobName);
                        if (instantJob != null) {
                            Class<? extends InstantJob> jobClass = instantJob.getClassObject();
                            if (jobClass != null) {
                                try {
                                    final InstantJob job = jobClass.newInstance();
                                    THREAD_POOL_EXECUTOR.execute(() -> job._execute(context));
                                } catch (InstantiationException | IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}