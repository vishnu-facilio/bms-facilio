package com.facilio.tasker.executor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.queue.FacilioObjectQueue;
import com.facilio.queue.ObjectMessage;
import com.facilio.tasker.FacilioScheduler;
import com.facilio.tasker.config.InstantJobConf;
import com.facilio.tasker.job.InstantJob;
import com.facilio.tasker.job.JobTimeOutInfo;

public enum FacilioInstantJobExecutor implements Runnable {
	INSTANCE;

	private static final Logger LOGGER = LogManager.getLogger(FacilioInstantJobExecutor.class.getName());

	private static final int CORE_POOL_SIZE = 10;
	private static final int MAX_POOL_SIZE = 10;
	private static final long KEEP_ALIVE = 300000L;
	private static final int QUEUE_SIZE = 100;
	private static final int JOB_TIMEOUT_BUFFER = 5;
	private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
			KEEP_ALIVE, TimeUnit.SECONDS, new LinkedBlockingQueue<>(QUEUE_SIZE));

	private static final ConcurrentMap<String, JobTimeOutInfo> JOB_MONITOR_MAP = new ConcurrentHashMap<>();

	private boolean isRunning = false;
	private Thread executorThread = null;

	private FacilioInstantJobExecutor() {

	}

	public void startExecutor() {
		if (Boolean.parseBoolean(FacilioProperties.getConfig("instantJobServer")) && !isRunning) {
			executorThread = new Thread(this, "instantJobExecutor");
			isRunning = true;
			executorThread.start();
		}
	}

	public void stopExecutor() {
		isRunning = false;
	}

	private int getNoOfFreeThreads() {
		int freeCount = MAX_POOL_SIZE - THREAD_POOL_EXECUTOR.getActiveCount();
		LOGGER.info("Instant jobs Free Count : " + freeCount);
		return freeCount;
	}

	public void run() {
		LOGGER.info("Executor run status : " + isRunning);
		while (isRunning) {
			try {
				handleTimeOut();
				List<ObjectMessage> messageList = FacilioObjectQueue.getObjects(InstantJobConf.getInstantJobQueue(),
						getNoOfFreeThreads());
				if (messageList != null) {
					for (ObjectMessage message : messageList) {
						FacilioContext context = (FacilioContext) message.getSerializable();
						if (context != null) {
							String jobName = (String) context.get(InstantJobConf.getJobNameKey());
							LOGGER.info("Gonna Execute job : " + jobName);
							if (jobName != null) {
								InstantJobConf.Job instantJob = FacilioScheduler.getInstantJob(jobName);
								if (instantJob != null) {
									Class<? extends InstantJob> jobClass = instantJob.getClassObject();
									if (jobClass != null) {
										try {
											final InstantJob job = jobClass.newInstance();
											if (instantJob.getTransactionTimeout() != InstantJobConf
													.getDefaultTimeOut()) {
												try {
													FacilioObjectQueue.changeVisibilityTimeout(
															InstantJobConf.getInstantJobQueue(),
															message.getId(),
															instantJob.getTransactionTimeout());
												} catch (Exception e) {
													LOGGER.info(
															"Ignoring job " + jobName + " since it's not available");
													continue;
												}
											}
											String receiptHandle = message.getId();
											job.setReceiptHandle(receiptHandle);
											LOGGER.info("receiptHandle is  "+receiptHandle);
											LOGGER.info("Executing job : " + jobName);
											Future f = THREAD_POOL_EXECUTOR.submit(() -> job._execute(context,
													(instantJob.getTransactionTimeout() - JOB_TIMEOUT_BUFFER) * 1000));
											JOB_MONITOR_MAP.put(receiptHandle, new JobTimeOutInfo(
													System.currentTimeMillis(),
													(instantJob.getTransactionTimeout() + JOB_TIMEOUT_BUFFER) * 1000, f,
													job));
										} catch (Exception e) {
											LOGGER.info("Exception Occured in executing job "+jobName, e);
//											LOGGER.error(e.getMessage() + " Exception while executing job " + jobName);
										}
									}
								}
							}
						} else {
							FacilioObjectQueue.deleteObject(InstantJobConf.getInstantJobQueue(), message.getId());
						}
					}
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					LOGGER.info("Exception in sleep ", e);
				}
			} catch (Exception e) {
				LOGGER.info("Exception in Facilio instant job queue executor " + e);
				CommonCommandUtil.emailException(FacilioInstantJobExecutor.class.getSimpleName(),
						"Exception in instant job executor", e);
			}
		}
		LOGGER.info("Executor running is false and so stopping executor thread");
	}

	public void jobEnd(String receiptHandle) {
		try {
			FacilioObjectQueue.deleteObject(InstantJobConf.getInstantJobQueue(), receiptHandle);
		} catch (Exception e) {
			LOGGER.info("Exception occurred in FacilioInstant Job Qeueu :  "+e);
		}
		JOB_MONITOR_MAP.remove(receiptHandle);
	}

	private void handleTimeOut() {
		Iterator<Map.Entry<String, JobTimeOutInfo>> itr = JOB_MONITOR_MAP.entrySet().iterator();
		long currentTime = System.currentTimeMillis();
		while (itr.hasNext()) {
			JobTimeOutInfo info = itr.next().getValue();
			if (currentTime >= (info.getExecutionTime() + info.getTimeOut())) {
				if (info.getFuture().cancel(true)) {
					info.getInstantJob().handleTimeOut();
					itr.remove();
				}
			}
		}
	}
}
