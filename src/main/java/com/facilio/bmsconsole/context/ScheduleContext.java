package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.commands.FacilioContext;

public class ScheduleContext extends FacilioContext {
	
	private long scheduleId;
	public long getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(long scheduleId) {
		this.scheduleId = scheduleId;
	}
	
	private long orgId;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long scheduledStart = 0;
	public long getScheduledStart() {
		return scheduledStart;
	}
	public void setScheduledStart(long scheduledStart) {
		this.scheduledStart = scheduledStart;
	}
	
	private long estimatedEnd = 0;
	public long getEstimatedEnd() {
		return estimatedEnd;
	}
	public void setEstimatedEnd(long estimatedEnd) {
		this.estimatedEnd = estimatedEnd;
	}
	
	private long actualWorkStart = 0;
	public long getActualWorkStart() {
		return actualWorkStart;
	}
	public void setActualWorkStart(long actualWorkStart) {
		this.actualWorkStart = actualWorkStart;
	}
	
	private long actualWorkEnd = 0;
	public long getActualWorkEnd() {
		return actualWorkEnd;
	}
	public void setActualWorkEnd(long actualWorkEnd) {
		this.actualWorkEnd = actualWorkEnd;
	}
}
