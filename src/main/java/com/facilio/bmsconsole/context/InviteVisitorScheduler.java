package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.RecurringInviteVisitorContextV3;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class InviteVisitorScheduler implements ScheduleTypeInterface {

	@Override
	public List<ModuleBaseWithCustomFields> createRecords(BaseScheduleContext baseScheduleContext) throws Exception {	
		
		List<ModuleBaseWithCustomFields> childRecords = new ArrayList<ModuleBaseWithCustomFields>();
		List<Map<String, Object>> parentRecordProps = baseScheduleContext.fetchParent();
		
		if (parentRecordProps != null && !parentRecordProps.isEmpty() && parentRecordProps.get(0) != null) {			
			RecurringInviteVisitorContextV3 parentVisitorInvite = FieldUtil.getAsBeanFromMap(parentRecordProps.get(0), RecurringInviteVisitorContextV3.class);
			if(parentVisitorInvite != null && parentVisitorInvite.getScheduleId() != null && baseScheduleContext.getScheduleInfo() != null) {
				long currentTime = System.currentTimeMillis();
				long startTime = parentVisitorInvite.getExpectedCheckInTime() > 0 ? parentVisitorInvite.getExpectedCheckInTime() : currentTime;
				
					
			}	
		}
		
		return childRecords;	
	}

	@Override
	public ScheduleType getSchedulerType() {
		return ScheduleType.RECURRING_VISITOR_INVITE;
	}

}
