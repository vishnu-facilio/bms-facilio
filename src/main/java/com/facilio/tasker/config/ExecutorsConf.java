package com.facilio.tasker.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

@XmlRootElement(name="executors")
public class ExecutorsConf {
	
	public ExecutorsConf() {
		// TODO Auto-generated constructor stub
	}
	
	private Set<ExecutorConf> executors;
	
	@XmlElement(name="executor")
	public Set<ExecutorConf> getExecutors() {
		return executors;
	}
	public void setExecutors(Set<ExecutorConf> executors) {
		this.executors = executors;
	}
	
	@Override
	public String toString() {
		return String.valueOf(executors);
	}
	
	public final static class ExecutorConf {
		public ExecutorConf() {
			// TODO Auto-generated constructor stub
		}
		
		private String name;
		
		@XmlAttribute(name="name")
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		private int period = -1;
		
		@XmlAttribute(name="period")
		public int getPeriod() {
			return period;
		}
		public void setPeriod(int period) {
			this.period = period;
		}
		
		private int threads = -1;
		
		@XmlAttribute(name="threads") 
		public int getThreads() {
			return threads;
		}
		public void setThreads(int threads) {
			this.threads = threads;
		}
		
		private int maxRetry = -1;
		@XmlAttribute(name="maxRetry") 
		public int getMaxRetry() {
			return maxRetry;
		}
		public void setMaxRetry(int maxRetry) {
			this.maxRetry = maxRetry;
		}
		@Override
		public String toString() {
			return "(name, period, threads, maxRetry)=("+name+", "+period+", "+threads+", "+maxRetry+")";
		}
		
		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			if (name != null) {
				return name.hashCode();
			}
			else {
				return 0;
			}
		}
		
		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			if(this == obj) {
				return true;
			}
			if(obj instanceof ExecutorConf) {
				String anotherName = ((ExecutorConf) obj).getName();
				if(name != null) {
					return name.equals(anotherName);
				}
				else {
					return name == anotherName;
				}
			}
			return false;
		}
	}

}
