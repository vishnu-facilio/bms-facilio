package com.facilio.tasker.executor;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.aws.util.AwsUtil;
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
		if (Boolean.parseBoolean(AwsUtil.getConfig("instantJobServer")) && !isRunning) {
			executorThread = new Thread(this, "instantJobExecutor");
			isRunning = true;
			executorThread.start();
		}
	}
	
	public void stopExecutor() {
		isRunning = false;
	}

	private int getNoOfFreeThreads(){
		int freeCount = MAX_POOL_SIZE - THREAD_POOL_EXECUTOR.getActiveCount();
		LOGGER.debug("Instant jobs Free Count : "+freeCount);
		return freeCount;
	}

    public void run(){
    	LOGGER.info("Executor run status : "+isRunning);
    	while (isRunning) {
	        List<ObjectMessage> messageList = ObjectQueue.getObjects(InstantJobConf.getInstantJobQueue(), getNoOfFreeThreads());
	        if(messageList != null) {
	            for (ObjectMessage message : messageList) {
                    FacilioContext context = (FacilioContext) message.getSerializable();
                    if(context != null) {
						String jobName = (String) context.get(InstantJobConf.getJobNameKey());
						LOGGER.debug("Gonna Execute job : " + jobName);
						if (jobName != null) {
							InstantJobConf.Job instantJob = FacilioScheduler.getInstantJob(jobName);
							if (instantJob != null) {
								Class<? extends InstantJob> jobClass = instantJob.getClassObject();
								if (jobClass != null) {
									try {
										final InstantJob job = jobClass.newInstance();
										if(instantJob.getTransactionTimeout() != InstantJobConf.getDefaultTimeOut()) {
											ObjectQueue.changeVisibilityTimeout(InstantJobConf.getInstantJobQueue(), message.getReceiptHandle(), instantJob.getTransactionTimeout());
										}
										job.setReceiptHandle(message.getReceiptHandle());

										LOGGER.debug("Executing job : " + jobName);
										THREAD_POOL_EXECUTOR.execute(() -> job._execute(context, (instantJob.getTransactionTimeout()-5)*1000));
									} catch (InstantiationException | IllegalAccessException e) {
										LOGGER.info("Exception while executing job " + e);
									}
								}
							}
						}
					} else {
                    	ObjectQueue.deleteObject(InstantJobConf.getInstantJobQueue(), message.getReceiptHandle());
					}
	            }
	        }
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				LOGGER.info("Exception in sleep ", e);
			}
		}
    }
}