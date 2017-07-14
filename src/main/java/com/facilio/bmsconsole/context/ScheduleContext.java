package com.facilio.bmsconsole.context;

import java.text.ParseException;

import com.facilio.constants.FacilioConstants;

public class ScheduleContext {
	
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
				this.scheduledStart = FacilioConstants.HTML5_DATE_FORMAT.parse(scheduledStart).getTime();
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
				this.estimatedEnd = FacilioConstants.HTML5_DATE_FORMAT.parse(estimatedEnd).getTime();
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
				this.actualWorkStart = FacilioConstants.HTML5_DATE_FORMAT.parse(actualWorkStart).getTime();
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
				this.actualWorkEnd = FacilioConstants.HTML5_DATE_FORMAT.parse(actualWorkEnd).getTime();
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
