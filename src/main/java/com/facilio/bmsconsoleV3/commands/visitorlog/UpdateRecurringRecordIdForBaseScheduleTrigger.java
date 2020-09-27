package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.RecurringInviteVisitorContextV3;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;

public class UpdateRecurringRecordIdForBaseScheduleTrigger extends FacilioCommand{
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
			
		String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<RecurringInviteVisitorContextV3> list = recordMap.get(moduleName);
        
        if(CollectionUtils.isNotEmpty(list) && moduleName.equals(FacilioConstants.ContextNames.RECURRING_INVITE_VISITOR)) {
            for(RecurringInviteVisitorContextV3 recurringInviteContext : list) {
        		if(recurringInviteContext != null && recurringInviteContext.getScheduleTrigger() != null && (recurringInviteContext.getScheduleTrigger().getId() != null && recurringInviteContext.getScheduleTrigger().getId() > 0)) {	
        			BaseScheduleContext scheduleTrigger = recurringInviteContext.getScheduleTrigger();
        			scheduleTrigger.setRecordId(recurringInviteContext.getId());
        			updateRecurringInviteScheduler(scheduleTrigger);      			
        		}
            }
        }
		
		return false;
	}
	
	
	public void updateRecurringInviteScheduler(BaseScheduleContext baseScheduleContext) throws Exception {
	   GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
	            .table(ModuleFactory.getBaseSchedulerModule().getTableName())
	            .fields(FieldFactory.getBaseSchedulerFields())
	            .andCondition(CriteriaAPI.getIdCondition(baseScheduleContext.getId(), ModuleFactory.getBaseSchedulerModule()));
	   update.update(FieldUtil.getAsProperties(baseScheduleContext));
	}
}
	