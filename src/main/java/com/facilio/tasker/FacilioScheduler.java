package com.facilio.tasker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.aws.util.AwsUtil;
import com.facilio.tasker.config.ExecutorsConf;
import com.facilio.tasker.config.InstantJobConf;
import com.facilio.tasker.config.SchedulerJobConf;
import com.facilio.tasker.executor.Executor;

public class FacilioScheduler {
	
	private static final Map<String, SchedulerJobConf.Job> JOBS_MAP = new HashMap<>();
	private static final Map<String, InstantJobConf.Job> INSTANT_JOBS_MAP = new HashMap<>();

	private static final String INSTANT_JOB_FILE = "conf/instantJobs.xml";

	private static final Logger LOGGER = LogManager.getLogger(FacilioScheduler.class.getName());

	private static final List<Executor> executors = new ArrayList<>();
	public static void initScheduler() throws IOException, InterruptedException, JAXBException {
		
		getJobObjectsFromConf();
		LOGGER.info("Scheduler Jobs : ");
		LOGGER.info(JOBS_MAP);
		
		getInstanceJobFromConf();
		LOGGER.info("Instant Jobs : ");
		LOGGER.info(INSTANT_JOBS_MAP);
		
//		Executor executor = new Executor("facilio", 15, 600);
		if(AwsUtil.isScheduleServer()) {
			startExecutors();
		}
	}


	private static void getJobObjectsFromConf() throws JAXBException {
		//Get file from resources folder
		ClassLoader classLoader = FacilioScheduler.class.getClassLoader();
		String scheduleJobFile = "schedulerJobs";
		if(AwsUtil.getConfig("schedulejobfile") != null) {
			scheduleJobFile = AwsUtil.getConfig("schedulejobfile");
		}
		File schedulerXml = new File(classLoader.getResource("conf/" + scheduleJobFile + ".xml").getFile());
		
		JAXBContext jaxbContext = JAXBContext.newInstance(SchedulerJobConf.class);
		SchedulerJobConf schedulerConf = (SchedulerJobConf) jaxbContext.createUnmarshaller().unmarshal(schedulerXml);
		
		System.out.println(schedulerConf);
		
		if(schedulerConf.getJobs() != null) {
			for(SchedulerJobConf.Job jobConf : schedulerConf.getJobs()) {
				String name = jobConf.getName();
				if(name != null && !name.isEmpty() && jobConf.getClassObject() != null) {
					try {
						addJobToMap(name, jobConf);
					}
					catch(Exception e) {
						LOGGER.error("The folowing error occurred while parsing job name : "+name, e);
					}
				}
				else {
					LOGGER.error("Invalid job configuration : "+jobConf);
				}
			}
		}
	}

	private static void getInstanceJobFromConf() throws JAXBException {
		ClassLoader classLoader = FacilioScheduler.class.getClassLoader();
		File instantJobXml = new File(classLoader.getResource(INSTANT_JOB_FILE).getFile());

		JAXBContext jaxbContext = JAXBContext.newInstance(InstantJobConf.class);
		InstantJobConf instantJobConf = (InstantJobConf) jaxbContext.createUnmarshaller().unmarshal(instantJobXml);

		LOGGER.info(instantJobConf);

		if(instantJobConf.getJobs() != null) {
			for(InstantJobConf.Job jobConf : instantJobConf.getJobs()) {
				String name = jobConf.getName();
				if(name != null && !name.isEmpty() && jobConf.getClassObject() != null) {
					try {
						addInstantJobToMap(name, jobConf);
					}
					catch(Exception e) {
						LOGGER.error("The folowing error occurred while parsing job name : "+name, e);
					}
				}
				else {
					LOGGER.error("Invalid job configuration : "+jobConf);
				}
			}
		}
	}
	private static void startExecutors() throws JAXBException {
		ClassLoader classLoader = FacilioScheduler.class.getClassLoader();
		String executorFile = "executors";
		if(AwsUtil.getConfig("scheduleexecutorsfile") != null) {
			executorFile = AwsUtil.getConfig("scheduleexecutorsfile");
		}
		File executorsXml = new File(classLoader.getResource("conf/" + executorFile + ".xml").getFile());
		
		JAXBContext jaxbContext = JAXBContext.newInstance(ExecutorsConf.class);
		ExecutorsConf executorsConf = (ExecutorsConf) jaxbContext.createUnmarshaller().unmarshal(executorsXml);
		
		System.out.println(executorsConf);
		
		if(executorsConf.getExecutors() != null) {
			for(ExecutorsConf.ExecutorConf executorConf : executorsConf.getExecutors()) {
				if(executorConf.getName() != null && !executorConf.getName().isEmpty() && executorConf.getPeriod() != -1 && executorConf.getThreads() != -1) {
					if(executorConf.getMaxRetry() != -1) {
						executors.add(new Executor(executorConf.getName(), executorConf.getThreads(), executorConf.getPeriod(), executorConf.getMaxRetry()));
					}
					else {
						executors.add(new Executor(executorConf.getName(), executorConf.getThreads(), executorConf.getPeriod()));
					}
				}
			}
		}
	}
	
	public static void stopSchedulers() {
		for(Executor executor : executors) {
			executor.shutdown();
		}
	}

	private static void addJobToMap(String jobName, SchedulerJobConf.Job job){
		JOBS_MAP.put(jobName, job);
	}

	public static SchedulerJobConf.Job getSchedulerJob(String jobName){
		return JOBS_MAP.get(jobName);
	}

	private static void addInstantJobToMap(String jobName, InstantJobConf.Job job){
		INSTANT_JOBS_MAP.put(jobName, job);
	}

	public static InstantJobConf.Job getInstantJob(String jobName) {
		return INSTANT_JOBS_MAP.get(jobName);
	}
}
