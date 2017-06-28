package com.facilio.bmsconsole.context;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ScheduleContext {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
	
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
	public void setScheduledStart(String scheduledStart) {
 		if(scheduledStart != null && !scheduledStart.isEmpty()) {
 			try {
				this.scheduledStart = DATE_FORMAT.parse(scheduledStart).getTime()/1000;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
 	}
	public void setScheduledStartFromTimestamp(long scheduledStart) {
		this.scheduledStart = scheduledStart;
	}
	
	private long estimatedEnd = 0;
	public long getEstimatedEnd() {
		return estimatedEnd;
	}
	public void setEstimatedEnd(String estimatedEnd) {
 		if(estimatedEnd != null && !estimatedEnd.isEmpty()) {
 			try {
				this.estimatedEnd = DATE_FORMAT.parse(estimatedEnd).getTime()/1000;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
 	}
	public void setEstimatedEndFromTimestamp(long estimatedEnd) {
		this.estimatedEnd = estimatedEnd;
	}
	
	private long actualWorkStart = 0;
	public long getActualWorkStart() {
		return actualWorkStart;
	}
	public void setActualWorkStart(String actualWorkStart) {
 		if(actualWorkStart != null && !actualWorkStart.isEmpty()) {
 			try {
				this.actualWorkStart = DATE_FORMAT.parse(actualWorkStart).getTime()/1000;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
 	}
	public void setActualWorkStartFromTimestamp(long actualWorkStart) {
		this.actualWorkStart = actualWorkStart;
	}
	
	private long actualWorkEnd = 0;
	public long getActualWorkEnd() {
		return actualWorkEnd;
	}
	public void setActualWorkEnd(String actualWorkEnd) {
 		if(actualWorkEnd != null && !actualWorkEnd.isEmpty()) {
 			try {
				this.actualWorkEnd = DATE_FORMAT.parse(actualWorkEnd).getTime()/1000;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
 	}
	public void setActualWorkEndFromTimestamp(long actualWorkEnd) {
		this.actualWorkEnd = actualWorkEnd;
	}
}
