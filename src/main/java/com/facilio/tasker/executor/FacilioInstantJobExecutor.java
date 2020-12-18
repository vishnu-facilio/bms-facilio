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

import com.facilio.tasker.FacilioInstantJobScheduler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.queue.FacilioObjectQueue;
import com.facilio.queue.FacilioQueueException;
import com.facilio.queue.ObjectMessage;
import com.facilio.tasker.config.InstantJobConf;
import com.facilio.tasker.job.InstantJob;
import com.facilio.tasker.job.JobTimeOutInfo;

public class FacilioInstantJobExecutor implements Runnable {
//	INSTANCE ("default", "FacilioInstantJobQueue", "FacilioInstantJobQueue_Data", -1 , -1);

	private static final Logger LOGGER = LogManager.getLogger(FacilioInstantJobExecutor.class.getName());

	private static final int DEFAULT_MAX_THREADS = 10;
	private static final long KEEP_ALIVE = 300000L;
	private static final int QUEUE_SIZE = 100;
	private static final int JOB_TIMEOUT_BUFFER = 5;

	private boolean isRunning = false;
	private FacilioObjectQueue objectQueue = null;
	private ThreadPoolExecutor threadPoolExecutor = null;
	private ConcurrentMap<String, JobTimeOutInfo> jobMonitorMap = new ConcurrentHashMap<>();
	private int maxThreads;
	private String name;
	private String tableName;
	private int dataRetention;
	private int pollingFrequency;

	public String getName() {
		return name;
	}


	public FacilioInstantJobExecutor(String name, String tableName, int maxThreads, int queueSize, int dataRetention, int pollingFrequency, List<Long> include, List<Long> exclude) { //pollingFrequency can have maximum of 30s and so int
		this.name = name;
		objectQueue = new FacilioObjectQueue(tableName, include, exclude);
		this.maxThreads = maxThreads > 0 ? maxThreads : DEFAULT_MAX_THREADS;
		this.tableName = tableName;
		this.dataRetention = dataRetention;
		this.pollingFrequency = pollingFrequency;
		threadPoolExecutor = new ThreadPoolExecutor(this.maxThreads, //core pool size
													this.maxThreads, //max pool size
													KEEP_ALIVE,
													TimeUnit.SECONDS,
													new LinkedBlockingQueue<>(queueSize > 0 ? queueSize : QUEUE_SIZE));
	}

	public void startExecutor() {
		if (!isRunning) {
			isRunning = true;
			new Thread(this, "facilioInstantJobExecutor").start();
		}
	}

	public void stopExecutor() {
		isRunning = false;
	}

	private int getNoOfFreeThreads() {
		int freeCount = maxThreads - threadPoolExecutor.getActiveCount();
		LOGGER.debug("Instant jobs Free Count : " + freeCount);
		return freeCount;
	}

	public void run() {
		LOGGER.debug("Executor run status : " + isRunning);
		while (isRunning) {
			try {
				handleTimeOut();
				if(getNoOfFreeThreads() == 0) {
					continue;
				}
				List<ObjectMessage> messageList = objectQueue.getObjects(
						getNoOfFreeThreads());
				if (messageList != null) {
					for (ObjectMessage message : messageList) {
						FacilioContext context = (FacilioContext) message.getSerializable();
						if (context != null) {
							String jobName = (String) context.get(InstantJobConf.getJobNameKey());
							LOGGER.debug("Gonna Execute job : " + jobName);
							if (jobName != null) {
								InstantJobConf.Job instantJob = FacilioInstantJobScheduler.getInstantJob(jobName);
								if (instantJob != null) {
									Class<? extends InstantJob> jobClass = instantJob.getClassObject();
									if (jobClass != null) {
										try {
											final InstantJob job = jobClass.newInstance();
											if (instantJob.getTransactionTimeout() != InstantJobConf
													.getDefaultTimeOut()) {
												try {
													objectQueue.changeVisibilityTimeout(
															
															message.getId(),
															(int) TimeUnit.SECONDS.toMinutes(instantJob.getTransactionTimeout()));
												} catch (Exception e) {
													LOGGER.info(
															"Ignoring job " + jobName + " since it's not available");
													continue;
												}
											}
											String receiptHandle = message.getId();
											job.setMessageId(receiptHandle);
											job.setExecutor(this);
											LOGGER.debug("Executing job : " + jobName);
											Future f = threadPoolExecutor.submit(() -> job._execute(context,
													(instantJob.getTransactionTimeout() - JOB_TIMEOUT_BUFFER) * 1000));
											jobMonitorMap.put(receiptHandle, new JobTimeOutInfo(
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
							objectQueue.deleteObject( message.getId());
						}
					}
				}
				try {
					Thread.sleep(pollingFrequency);
				} catch (InterruptedException e) {
					LOGGER.info("Exception in pollingFrequencySleep ", e);
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
			objectQueue.deleteObject( receiptHandle);
		} catch (Exception e) {
			LOGGER.info("Exception occurred in FacilioInstant Job Qeueu :  "+e);
		}
		jobMonitorMap.remove(receiptHandle);
	}

	private void handleTimeOut() {
		Iterator<Map.Entry<String, JobTimeOutInfo>> itr = jobMonitorMap.entrySet().iterator();
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

	public void addJob (String jobName, FacilioContext context) throws Exception {
		if (!objectQueue.sendMessage(context)) {
			throw new IllegalArgumentException("Unable to add instant job to queue");
		}
	}
	
	public void deleteInstantJobQueueTable() throws Exception {
		long deletionTillDate = (System.currentTimeMillis() - (dataRetention * 24 * 60 * 60 * 1000)); 

		try{
            objectQueue.delete(deletionTillDate);
        }catch(Exception e){
        	CommonCommandUtil.emailException("FacilioExecutorInstantJobDeletion", "ExecutorDeletionJob Failed  -- "+tableName+ " with tillDate --" +deletionTillDate, e);
        	LOGGER.info("Exception occurred in FacilioExecutorInstantJobDeletion :  "+tableName+ " with tillDate --" +deletionTillDate,e);
        }
    }
}
