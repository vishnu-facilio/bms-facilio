package com.facilio.tasker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.facilio.tasker.config.SchedulerConf;
import com.facilio.tasker.executor.Executor;
import com.facilio.tasker.job.FacilioJob;

public class FacilioScheduler {
	
	private static Map<String, FacilioJob> jobsMap = null;
	
	public static void initScheduler() throws IOException, InterruptedException, JAXBException {
		
		jobsMap = getJobObjectsFromConf();
		System.out.println(jobsMap);
		
		Executor executor = new Executor("facilio", 15, jobsMap);
		executor.start();
	}
	
	private static Map<String, FacilioJob> getJobObjectsFromConf() throws JAXBException {
		//Get file from resources folder
		ClassLoader classLoader = FacilioScheduler.class.getClassLoader();
		File schedulerXml = new File(classLoader.getResource("conf/schedulerJobs.xml").getFile());
		
		JAXBContext jaxbContext = JAXBContext.newInstance(SchedulerConf.class);
		SchedulerConf schedulerConf = (SchedulerConf) jaxbContext.createUnmarshaller().unmarshal(schedulerXml);
		
		System.out.println(schedulerConf);
		
		Map<String, FacilioJob> jobsMap = new HashMap<String, FacilioJob>();
		for(SchedulerConf.Job jobConf : schedulerConf.getJobs()) {
			String name = jobConf.getName();
			String className = jobConf.getClassName();
			if(name != null && !name.isEmpty() && className != null && !className.isEmpty()) { 
				try {
					
					FacilioJob job = (FacilioJob) Class.forName(className).newInstance();
					jobsMap.put(name, job);
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
		
		return jobsMap;
	}
}
