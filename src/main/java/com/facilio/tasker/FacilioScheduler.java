package com.facilio.tasker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.facilio.tasker.config.ExecutorsConf;
import com.facilio.tasker.config.SchedulerJobConf;
import com.facilio.tasker.executor.Executor;
import com.facilio.tasker.job.FacilioJob;

public class FacilioScheduler {
	
	public static final Map<String, Class<? extends FacilioJob>> JOBS_MAP = new HashMap<>();
	
	private static final List<Executor> executors = new ArrayList<>();
	public static void initScheduler() throws IOException, InterruptedException, JAXBException {
		
		getJobObjectsFromConf();
		System.out.println(JOBS_MAP);
		
//		Executor executor = new Executor("facilio", 15, 600);
		startExecutors();
	}
	
	private static void getJobObjectsFromConf() throws JAXBException {
		//Get file from resources folder
		ClassLoader classLoader = FacilioScheduler.class.getClassLoader();
		File schedulerXml = new File(classLoader.getResource("conf/schedulerJobs.xml").getFile());
		
		JAXBContext jaxbContext = JAXBContext.newInstance(SchedulerJobConf.class);
		SchedulerJobConf schedulerConf = (SchedulerJobConf) jaxbContext.createUnmarshaller().unmarshal(schedulerXml);
		
		System.out.println(schedulerConf);
		
		if(schedulerConf.getJobs() != null) {
			for(SchedulerJobConf.Job jobConf : schedulerConf.getJobs()) {
				String name = jobConf.getName();
				String className = jobConf.getClassName();
				if(name != null && !name.isEmpty() && className != null && !className.isEmpty()) { 
					try {
						
						JOBS_MAP.put(name, (Class<? extends FacilioJob>) Class.forName(className));
					}
					catch(Exception e) {
						System.err.println("The folowing error occurred while parsing job name : "+name);
						e.printStackTrace();
					}
				}
				else {
					System.err.println("Invalid job configuration : "+jobConf);
				}
			}
		}
	}
	
	private static void startExecutors() throws JAXBException {
		ClassLoader classLoader = FacilioScheduler.class.getClassLoader();
		File executorsXml = new File(classLoader.getResource("conf/executors.xml").getFile());
		
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
}
