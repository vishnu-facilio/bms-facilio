package com.facilio.tasker.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="scheduler")
public class SchedulerJobConf {
	private List<Job> jobs;
	
	public SchedulerJobConf() {
		
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
		return String.valueOf(jobs.toString());
	}
	
	public final static class Job {
		private String name, className;
		
		public Job() {
			
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
