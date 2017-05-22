package com.facilio.tasker.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="scheduler")
public class SchedulerConf {
	private List<Job> jobs;
	
	public SchedulerConf() {
		
	}
	
	public SchedulerConf(List<Job> jobs) {
		this.jobs = jobs;
	}
	
	@XmlElement(name="job")
	public List<Job> getJobs() {
		return jobs;
	}
	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}
	
	@Override
	public String toString() {
		return jobs.toString();
	}
	
	public static void main(String[] args) {
//		File schedulerXml = new File(classLoader.getResource("conf/scheduler.xml").getFile());
//		
//		JAXBContext jaxbContext = JAXBContext.newInstance(SchedulerConf.class);
//		SchedulerConf schedulerConf = (SchedulerConf) jaxbContext.createUnmarshaller().unmarshal(schedulerXml);
//		
//		System.out.println(schedulerConf);
	}
	
	public final static class Job {
		private String name, className;
		
		public Job() {
			
		}
		
		public Job(String name, String className) {
			this.name = name;
			this.className = className;
		}
		
		@XmlAttribute(name="name")
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		@XmlAttribute(name="classname")
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		
		
		@Override
		public String toString() {
			return "(name, classname)=("+name+","+className+")";
		}
	}
}
