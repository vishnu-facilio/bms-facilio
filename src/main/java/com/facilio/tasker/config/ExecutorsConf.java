package com.facilio.tasker.config;

import com.facilio.util.FacilioUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
		return new StringBuilder()
				.append(executors)
				.append(",\n Global ")
				.append(globalParams)
				.toString();
	}

	private Params globalParams;
	@XmlElement(name="global")
	public Params getGlobalParams() {
		return globalParams;
	}
	public void setGlobalParams(Params globalParams) {
		this.globalParams = globalParams;
	}

	public static class Params {

		private List<Long> include;
		@XmlElement(name="include")
		public void setInclude(String include) {
			this.include = parseOrgIds(include);
		}
		public List<Long> getIncludedOrgs() {
			return include;
		}

		private List<Long> exclude;
		@XmlElement(name="exclude")
		public void setExclude(String exclude) {
			this.exclude = parseOrgIds(exclude);
		}
		public List<Long> getExcludedOrgs() {
			return exclude;
		}

		private List<Long> parseOrgIds(String str) {
			if (StringUtils.isEmpty(str)) {
				return null;
			}
			String[] orgs = FacilioUtil.splitByComma(str);
			return Arrays.stream(orgs)
					.map(Long::parseLong)
					.collect(Collectors.toList());
		}

		@Override
		public String toString() {
			return new StringBuilder("Params{")
					.append("include=").append(include)
					.append(", exclude=").append(exclude)
					.append('}')
					.toString();
		}
	}
	
	public final static class ExecutorConf extends Params {
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
			return new StringBuilder("ExecutorConf{")
					.append("name='").append(name).append('\'')
					.append(", period=").append(period)
					.append(", threads=").append(threads)
					.append(", maxRetry=").append(maxRetry)
					.append("include=").append(getIncludedOrgs())
					.append(", exclude=").append(getExcludedOrgs())
					.append('}')
					.toString();
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
