package com.facilio.bmsconsole.context;

import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONArray;


public class HistoricalJobContext {
	long jobId = -1;
	String jobName;
	long startTime = -1;
	long endTime = -1;
	long subIntervalTime = -1;
	long executionEndTime = -1;
	Queue<HistoricalJobMLContext> mlList = new LinkedList<HistoricalJobMLContext>();
	JSONArray checkGamParent;
	long checkGamModuleid = -1;
	long checkratioModuleid1 = -1;
	
	public long getJobId() {
		return jobId;
	}
	public void setJobId(long jobId) {
		this.jobId = jobId;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public long getSubIntervalTime() {
		return subIntervalTime;
	}
	public void setSubIntervalTime(long subIntervalTime) {
		this.subIntervalTime = subIntervalTime;
	}
	public long getExecutionEndTime() {
		return executionEndTime;
	}
	public void setExecutionEndTime(long executionEndTime) {
		this.executionEndTime = executionEndTime;
	}
	
	public Queue<HistoricalJobMLContext> getMlList() {
		return mlList;
	}
	public void setMlList(Queue<HistoricalJobMLContext> mlList) {
		this.mlList = mlList;
	}

	public JSONArray getCheckGamParent() {
		return checkGamParent;
	}

	public void setCheckGamParent(JSONArray checkGamParent) {
		this.checkGamParent = checkGamParent;
	}

	public long getCheckGamModuleid() {
		return checkGamModuleid;
	}

	public void setCheckGamModuleid(long checkGamModuleid) {
		this.checkGamModuleid = checkGamModuleid;
	}

	public long getCheckratioModuleid1() {
		return checkratioModuleid1;
	}

	public void setCheckratioModuleid1(long checkratioModuleid1) {
		this.checkratioModuleid1 = checkratioModuleid1;
	}
	
}

