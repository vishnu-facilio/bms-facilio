package com.facilio.control;

import com.facilio.v3.context.V3Context;

public class ControlScheduleGroupedSlot extends V3Context {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ControlScheduleContext schedule;
	ControlGroupContext group;
	
	public ControlScheduleGroupedSlot () {
		
	}
	
	public ControlScheduleGroupedSlot(ControlScheduleSlot slot) {
		
		schedule = slot.getSchedule();
		group = slot.getGroup();
		if(slot.getStartTime() != null) {
			startTime = slot.getStartTime();
		}
		if(slot.getEndTime() != null) {
			endTime = slot.getEndTime();
		}
	}
	
	
	
	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public ControlScheduleContext getSchedule() {
		return schedule;
	}

	public void setSchedule(ControlScheduleContext schedule) {
		this.schedule = schedule;
	}

	public ControlGroupContext getGroup() {
		return group;
	}

	public void setGroup(ControlGroupContext group) {
		this.group = group;
	}
	
}