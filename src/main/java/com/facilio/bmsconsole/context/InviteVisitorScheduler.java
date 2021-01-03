package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.bmsconsole.jobs.BaseSchedulerJob;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.RecurringInviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;

public class InviteVisitorScheduler implements ScheduleTypeInterface {

	private static final Logger LOGGER = Logger.getLogger(InviteVisitorScheduler.class.getName());
	
	@Override
	public List<? extends ModuleBaseWithCustomFields> createRecords(BaseScheduleContext baseScheduleContext, boolean isUpdate, List<Map<String, Object>> parentRecordProps, boolean isManualOrScheduleTrigger) throws Exception {	
				
		if (parentRecordProps != null && !parentRecordProps.isEmpty() && parentRecordProps.get(0) != null) {			
			RecurringInviteVisitorContextV3 parentVisitorInvite = FieldUtil.getAsBeanFromMap(parentRecordProps.get(0), RecurringInviteVisitorContextV3.class);
			if(parentVisitorInvite != null && parentVisitorInvite.getScheduleId() != null && baseScheduleContext.getScheduleInfo() != null && parentVisitorInvite.getScheduleId() == baseScheduleContext.getId()) {
				parentVisitorInvite.setScheduleTrigger(baseScheduleContext);
				long currentTime = System.currentTimeMillis();
				long startTime = parentVisitorInvite.getExpectedCheckInTime() > 0 ? parentVisitorInvite.getExpectedCheckInTime() : currentTime;
				
				if(isUpdate) {
					deleteUpcomingChildInvites(parentVisitorInvite.getId(), startTime);
				}
				return scheduleVisitorInvites(parentVisitorInvite, startTime/1000, isManualOrScheduleTrigger);			
			}	
		}
		return null;
	}

	@Override
	public ScheduleType getSchedulerType() {
		return ScheduleType.RECURRING_VISITOR_INVITE;
	}
	
	private List<InviteVisitorContextV3> scheduleVisitorInvites(RecurringInviteVisitorContextV3 parentVisitorInvite, Long startTime, boolean isManualOrScheduleTrigger) throws Exception {
		LOGGER.log(Level.FINE, "Generating visitorInvites for visitorInvites: " + parentVisitorInvite.getId());
		long endTime = 0;
		if (parentVisitorInvite.getScheduleTrigger() != null) {
			BaseScheduleContext scheduleTrigger = parentVisitorInvite.getScheduleTrigger();
			endTime = V3VisitorManagementAPI.getScheduleEndTime(-1, parentVisitorInvite.getScheduleTrigger());

			if(isManualOrScheduleTrigger) {
				startTime = scheduleTrigger.getScheduleInfo().nextExecutionTime(startTime); // Calculating start time & skipping for first date because parent invite will act as childInvite for first date
				parentVisitorInvite.setExpectedCheckInTime(startTime * 1000);
				parentVisitorInvite.setExpectedCheckOutTime((startTime * 1000) + (parentVisitorInvite.getExpectedVisitDuration() != null ? parentVisitorInvite.getExpectedVisitDuration() : 0));
				parentVisitorInvite.setParentInviteId(parentVisitorInvite.getId());

				startTime = scheduleTrigger.getScheduleInfo().nextExecutionTime(startTime);
			}
			else {
				long lastGeneratedTime = scheduleTrigger.getGeneratedUptoTime()/1000;	
				startTime = (lastGeneratedTime < endTime) ? scheduleTrigger.getScheduleInfo().nextExecutionTime(lastGeneratedTime) : null;		
			}
				
			List<InviteVisitorContextV3> childInvites = V3VisitorManagementAPI.getChildInviteSchedules(startTime, endTime, scheduleTrigger, parentVisitorInvite);
			V3VisitorManagementAPI.updateGeneratedUptoInParentInviteAndTrigger(scheduleTrigger, parentVisitorInvite);
			return childInvites;
		}
		return null;
	}
	
	public static void deleteUpcomingChildInvites(long parentInviteId, long currentTime) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.INVITE_VISITOR));
        DeleteRecordBuilder<RecurringInviteVisitorContextV3> deleteBuilder = new DeleteRecordBuilder<RecurringInviteVisitorContextV3>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("parentInviteId"), String.valueOf(parentInviteId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("expectedCheckInTime"), String.valueOf(currentTime), DateOperators.IS_AFTER));

        deleteBuilder.markAsDelete();
    }

}
