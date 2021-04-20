package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.context.PMTriggerContext.TriggerExectionSource;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsoleV3.context.BaseVisitContextV3;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.RecurringInviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.V3VisitorContext;
import com.facilio.bmsconsoleV3.context.V3WatchListContext;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;

public class AddOrUpdateScheduleInRecurringVisitorCommandV3 extends FacilioCommand{
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
			
		String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<InviteVisitorContextV3> list = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(list) && moduleName.equals(FacilioConstants.ContextNames.RECURRING_INVITE_VISITOR)) {
            for(InviteVisitorContextV3 inviteVisitor : list) {
        		RecurringInviteVisitorContextV3 recurringInviteContext = (RecurringInviteVisitorContextV3)inviteVisitor;
        		if(recurringInviteContext != null && recurringInviteContext.getScheduleTrigger() != null) {
        			if((recurringInviteContext.getScheduleTrigger().getId() != null && recurringInviteContext.getScheduleTrigger().getId() > 0) || (recurringInviteContext.getScheduleId() != null && recurringInviteContext.getScheduleId() > 0)) {
        				V3VisitorManagementAPI.updateBaseScheduleContext(recurringInviteContext.getScheduleTrigger());
    					scheduleBaseSchedulerJob(recurringInviteContext, true);
    				}
    				else {
    					addRecurringInviteScheduler(recurringInviteContext);
    					scheduleBaseSchedulerJob(recurringInviteContext, false);
    				}
        		}
            }
        }
		
		return false;
	}
	
	private void addRecurringInviteScheduler(RecurringInviteVisitorContextV3 recurringInviteContext) throws Exception {
		
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getBaseSchedulerModule().getTableName())
                .fields(FieldFactory.getBaseSchedulerFields());
		
		BaseScheduleContext scheduleTrigger = recurringInviteContext.getScheduleTrigger();
		scheduleTrigger.setModuleId(modBean.getModule(FacilioConstants.ContextNames.RECURRING_INVITE_VISITOR).getModuleId());
		scheduleTrigger.setRecordId(recurringInviteContext.getId());
		scheduleTrigger.setScheduleType(ScheduleType.RECURRING_VISITOR_INVITE);
		
		Map<String, Object> props = FieldUtil.getAsProperties(scheduleTrigger);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		scheduleTrigger.setId((Long) props.get("id"));
		recurringInviteContext.setScheduleId(scheduleTrigger.getId());
		recurringInviteContext.setIsRecurring(true);	 
	}
	
	public void scheduleBaseSchedulerJob(RecurringInviteVisitorContextV3 recurringInviteContext, boolean isUpdate) throws Exception {
		int delay = 0;
		long scheduleId = recurringInviteContext.getScheduleTrigger().getId();
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioStatus status = TicketAPI.getStatus(modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR), "Upcoming");
//    	if(recurringInviteContext.getModuleState().getId() == status.getId()) {
		
			FacilioContext context = new FacilioContext();
			context.put("isUpdate", isUpdate);
			context.put("saveAsV3", false);
			context.put(FacilioConstants.ContextNames.BASE_SCHEDULE_ID, scheduleId);
			FacilioTimer.scheduleInstantJob("AddOneTimeBaseSchedulesJob",context);
			
//			if(isUpdate) {
//	        	BmsJobUtil.deleteJobWithProps(scheduleId, "BaseSchedulerJob");
//			}
//          BmsJobUtil.scheduleOneTimeJobWithProps(scheduleId, "BaseSchedulerJob", delay, "priority",  jObj);       
//    	}
	}
}
