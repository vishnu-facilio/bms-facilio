package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class InviteVisitorScheduler implements ScheduleTypeInterface {

	@Override
	public void createRecords(BaseScheduleContext baseScheduleContext) throws Exception {
		
		ModuleBaseWithCustomFields parentRecord = baseScheduleContext.fetchParent();
		InviteVisitorContextV3 parentVisitorInvite = (InviteVisitorContextV3) parentRecord;
		
	}

	@Override
	public ScheduleType getSchedulerType() {
		return ScheduleType.RECURRING_VISITOR_INVITE;
	}

}
