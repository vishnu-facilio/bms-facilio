package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

public class ReportAlarmContext {

	private long startTime;
	private long endTime;
	private int order = 0;
	List<ReadingAlarmContext> alarmContexts;
	
	public int addOrder() {
		order++;
		return order;
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
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public List<ReadingAlarmContext> getAlarmContexts() {
		return alarmContexts;
	}
	public void setAlarmContexts(List<ReadingAlarmContext> alarmContexts) {
		this.alarmContexts = alarmContexts;
	} 
	public void addAlarmContext(ReadingAlarmContext alarmContext) {
		this.alarmContexts = this.alarmContexts == null ? new ArrayList<>() : this.alarmContexts;
		
		this.alarmContexts.add(alarmContext);
	}
	
}
