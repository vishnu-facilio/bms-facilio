package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.WorkflowAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.PMTriggerContext;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.tasker.FacilioTimer;

public class AddPreventiveMaintenanceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		long templateId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		pm.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		pm.setTemplateId(templateId);
		
		pm.setCreatedById(AccountUtil.getCurrentUser().getId());
		pm.setCreatedTime(System.currentTimeMillis());
		pm.setStatus(true);
		
		if(workorder.getAsset() != null) {
			pm.setAssetId(workorder.getAsset().getId());
		}
		if(workorder.getSpace() != null) {
			pm.setSpaceId(workorder.getSpace().getId());
		}
		if(workorder.getAssignedTo() != null) {
			pm.setAssignedToid(workorder.getAssignedTo().getId());
		}
		if(workorder.getType() != null) {
			pm.setTypeId(workorder.getType().getId());
		}
		
		Map<String, Object> pmProps = FieldUtil.getAsProperties(pm);
		
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
													.table(ModuleFactory.getPreventiveMaintenancetModule().getTableName())
													.fields(FieldFactory.getPreventiveMaintenanceFields())
													.addRecord(pmProps);
		
		builder.save();
		long id = (long) pmProps.get("id");
		pm.setId(id);
		
		addTriggersAndReadings(pm.getId(), pm.getTriggers());
		
		for (PMTriggerContext trigger : pm.getTriggers()) {
			if(trigger.getSchedule() != null) {
				long startTime = getStartTimeInSecond(trigger.getStartTime());
				PreventiveMaintenanceAPI.schedulePM(trigger, startTime, PreventiveMaintenanceAPI.PM_CALCULATION_DAYS*3600);
			}
		}
		return false;
	}
	
	private static long getStartTimeInSecond(long startTime) {
		long startTimeInSecond = startTime/1000;
		startTimeInSecond = startTimeInSecond - 300; //for calculating next execution time
		
		return startTimeInSecond;
	}
	
	private void addTriggersAndReadings(long pmId, List<PMTriggerContext> triggers) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getPMTriggersModule().getTableName())
														.fields(FieldFactory.getPMTriggerFields());
		
		for (PMTriggerContext trigger : triggers) {
			trigger.setPmId(pmId);
			if (trigger.getReadingRule() != null) {
				long ruleId = WorkflowAPI.addWorkflowRule(trigger.getReadingRule());
				trigger.setReadingRuleId(ruleId);
			}
			insertBuilder.addRecord(FieldUtil.getAsProperties(trigger));
		}
		insertBuilder.save();
		
		List<Map<String, Object>> triggerProps = insertBuilder.getRecords();
		for(int i = 0; i<triggerProps.size(); i++) {
			Map<String, Object> triggerProp = triggerProps.get(i); 
			triggers.get(i).setId((long) triggerProp.get("id"));
		}
	}
}
