package com.facilio.tasker.job;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.tasker.ScheduleInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class JobContext {
	
	private long jobId = -1;
	public long getJobId() {
		return jobId;
	}
	public void setJobId(long jobId) {
		this.jobId = jobId;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private String jobName;
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	private Boolean active;
	public boolean isActive() {
		if(active != null) {
			return active.booleanValue();
		}
		return false;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public Boolean getActive() {
		return active;
	}
	
	private int transactionTimeout = -1;
	public int getTransactionTimeout() {
		return transactionTimeout;
	}
	public void setTransactionTimeout(int transactionTimeout) {
		this.transactionTimeout = transactionTimeout;
	}

	private Boolean isPeriodic;
	public boolean isPeriodic() {
		if(isPeriodic != null) {
			return isPeriodic.booleanValue();
		}
		return false;
	}
	public void setIsPeriodic(boolean isPeriodic) {
		this.isPeriodic = isPeriodic;
	}
	public Boolean getIsPeriodic() {
		return isPeriodic;
	}
	
	private int period = -1;
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	
	public String getScheduleJson() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if(schedule != null) {
			return FieldUtil.getAsJSON(schedule).toJSONString();
		}
		return null;
	}
	public void setScheduleJson(String jsonString) throws JsonParseException, JsonMappingException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		this.schedule = FieldUtil.getAsBeanFromJson((JSONObject)parser.parse(jsonString), ScheduleInfo.class);
	}
	
	private ScheduleInfo schedule;
	public ScheduleInfo getSchedule() {
		return schedule;
	}
	public void setSchedule(ScheduleInfo schedule) {
		this.schedule = schedule;
	}

	private long executionTime = -1;
	public long getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}
	
	private long nextExecutionTime = -1;
	public long getNextExecutionTime() {
		return nextExecutionTime;
	}
	public void setNextExecutionTime(long nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
	}

	private String executorName;
	public String getExecutorName() {
		return executorName;
	}
	public void setExecutorName(String executorName) {
		this.executorName = executorName;
	}
	
	private long endExecutionTime = -1;
	public long getEndExecutionTime() {
		return endExecutionTime;
	}
	public void setEndExecutionTime(long endExecutionTime) {
		this.endExecutionTime = endExecutionTime;
	}
	
	private int maxExecution = -1;
	public int getMaxExecution() {
		return maxExecution;
	}
	public void setMaxExecution(int maxExecution) {
		this.maxExecution = maxExecution;
	}
	
	private int currentExecutionCount = 0;
	public int getCurrentExecutionCount() {
		return currentExecutionCount;
	}
	public void setCurrentExecutionCount(int currentExecutionCount) {
		this.currentExecutionCount = currentExecutionCount;
	}

	private long jobServerId = 0L;
	private int status = 1;
	private long jobStartTime = 0L;
	private int jobExecutionCount =0;

	public long getJobServerId() {
		return jobServerId;
	}

	public void setJobServerId(long jobServerId) {
		this.jobServerId = jobServerId;
	}


	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getJobStartTime() {
		return jobStartTime;
	}

	public void setJobStartTime(long jobStartTime) {
		this.jobStartTime = jobStartTime;
	}

	public int getJobExecutionCount() {
		return jobExecutionCount;
	}

	public void setJobExecutionCount(int jobExecutionCount) {
		this.jobExecutionCount = jobExecutionCount;
	}

	@Override
	public String toString() {
		return jobId+"::"+orgId+"::"+jobName+"::"+period+"::"+isPeriodic+"::"+executionTime;
	}


}
