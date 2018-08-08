package com.facilio.tasker.executor;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.queue.ObjectMessage;
import com.facilio.queue.ObjectQueue;
import com.facilio.tasker.FacilioScheduler;
import com.facilio.tasker.config.InstantJobConf;
import com.facilio.tasker.job.InstantJob;

public enum InstantJobExecutor implements Runnable {
	INSTANCE;
	
	private static final Logger LOGGER = LogManager.getLogger(InstantJobExecutor.class.getName());
	
	private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE = 300000L;
    private static final int QUEUE_SIZE = 100;
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<>(QUEUE_SIZE));
	
	private boolean isRunning = false;
	private Thread executorThread = null;
	private InstantJobExecutor() {
		
	}
	
	public void startExecutor() {
		if (!isRunning) {
			executorThread = new Thread(this, "instantJobExecutor");
			isRunning = true;
			executorThread.start();
		}
	}
	
	public void stopExecutor() {
		isRunning = false;
	}
	
    public void run(){
    	LOGGER.info("Executor run status : "+isRunning);
    	while (isRunning) {
	        List<ObjectMessage> messageList = ObjectQueue.getObjects(InstantJobConf.getInstantJobQueue(), 10);
	        if(messageList != null) {
	            for (ObjectMessage message : messageList) {
	                while (THREAD_POOL_EXECUTOR.getQueue().size() < 15) {
	                    FacilioContext context = (FacilioContext) message.getSerializable();
	                    String jobName = (String) context.get(InstantJobConf.getJobNameKey());
	                    LOGGER.info("Gonna Execute job : "+jobName);
	                    if (jobName != null) {
	                        InstantJobConf.Job instantJob = FacilioScheduler.getInstantJob(jobName);
	                        if (instantJob != null) {
	                            Class<? extends InstantJob> jobClass = instantJob.getClassObject();
	                            if (jobClass != null) {
	                                try {
	                                    final InstantJob job = jobClass.newInstance();
	                                    job.setReceiptHandle(message.getReceiptHandle());
	                                    LOGGER.info("Executing job : "+jobName);
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
}