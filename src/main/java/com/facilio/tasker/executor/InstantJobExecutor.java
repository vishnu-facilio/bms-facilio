package com.facilio.tasker.executor;

import com.facilio.aws.util.AwsUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.queue.ObjectMessage;
import com.facilio.queue.ObjectQueue;
import com.facilio.tasker.FacilioScheduler;
import com.facilio.tasker.config.InstantJobConf;
import com.facilio.tasker.job.InstantJob;
import com.facilio.tasker.job.JobTimeOutInfo;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public enum InstantJobExecutor implements Runnable {
	INSTANCE;
	
	private static final Logger LOGGER = LogManager.getLogger(InstantJobExecutor.class.getName());
	
	private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE = 300000L;
    private static final int QUEUE_SIZE = 100;
    private static final int JOB_TIMEOUT_BUFFER = 5;
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<>(QUEUE_SIZE));

    private static final ConcurrentMap<String, JobTimeOutInfo> JOB_MONITOR_MAP = new ConcurrentHashMap<>();

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
    		handleTimeOut();
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
										String receiptHandle = message.getReceiptHandle();
										job.setReceiptHandle(receiptHandle);

										LOGGER.debug("Executing job : " + jobName);
										Future f = THREAD_POOL_EXECUTOR.submit(() -> job._execute(context, (instantJob.getTransactionTimeout()-JOB_TIMEOUT_BUFFER)*1000));
										JOB_MONITOR_MAP.put(receiptHandle, new JobTimeOutInfo(System.currentTimeMillis(), (instantJob.getTransactionTimeout() + JOB_TIMEOUT_BUFFER)*1000, f, job));
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

    public void jobEnd(String receiptHandle) {
		ObjectQueue.deleteObject(InstantJobConf.getInstantJobQueue(), receiptHandle);
		JOB_MONITOR_MAP.remove(receiptHandle);
	}

	private void handleTimeOut() {
		Iterator<Map.Entry<String, JobTimeOutInfo>> itr = JOB_MONITOR_MAP.entrySet().iterator();
		long currentTime = System.currentTimeMillis();
		while (itr.hasNext()) {
			JobTimeOutInfo info = itr.next().getValue();
			if (currentTime >= (info.getExecutionTime()+info.getTimeOut())) {
				if (info.getFuture().cancel(true)) {
					info.getInstantJob().handleTimeOut();
					itr.remove();
				}
			}
		}
	}
}