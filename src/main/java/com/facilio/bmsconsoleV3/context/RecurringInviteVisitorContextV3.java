package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.BaseScheduleContext;

public class RecurringInviteVisitorContextV3 extends InviteVisitorContextV3{

	private static final long serialVersionUID = 1L;

	private BaseScheduleContext scheduleTrigger;

	public BaseScheduleContext getScheduleTrigger() {
		return scheduleTrigger;
	}

	public void setScheduleTrigger(BaseScheduleContext scheduleTrigger) {
		this.scheduleTrigger = scheduleTrigger;
	}
	
	private Long scheduleId;
	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}
	
}
