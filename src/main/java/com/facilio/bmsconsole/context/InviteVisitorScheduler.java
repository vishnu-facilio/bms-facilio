package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;
import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class InviteVisitorScheduler implements ScheduleTypeInterface {

	@Override
	public List<ModuleBaseWithCustomFields> createRecords(BaseScheduleContext baseScheduleContext) throws Exception {		
		ModuleBaseWithCustomFields parentRecord = baseScheduleContext.fetchParent();
		InviteVisitorContextV3 parentVisitorInvite = (InviteVisitorContextV3) parentRecord;
		
		List<ModuleBaseWithCustomFields> childRecords = new ArrayList<ModuleBaseWithCustomFields>();
		return childRecords;
		
	}

	@Override
	public ScheduleType getSchedulerType() {
		return ScheduleType.RECURRING_VISITOR_INVITE;
	}

}
