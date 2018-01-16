package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.TriggerType;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.WorkflowAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.PMTriggerContext;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddPreventiveMaintenanceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		PreventiveMaintenance pm = (PreventiveMaintenance) context
				.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		addDefaultProps(pm, context);
		Map<String, Object> pmProps = FieldUtil.getAsProperties(pm);

		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getPreventiveMaintenancetModule().getTableName())
				.fields(FieldFactory.getPreventiveMaintenanceFields()).addRecord(pmProps);

		builder.save();
		long id = (long) pmProps.get("id");
		pm.setId(id);

		addTriggersAndReadings(pm);
		schedulePM(pm, context);

		return false;
	}

	private static void schedulePM(PreventiveMaintenance pm, Context context) throws Exception {
		Map<Long, Long> nextExecutionTimes = new HashMap<>();
		for (PMTriggerContext trigger : pm.getTriggers()) {
			if (trigger.getSchedule() != null) {
				long startTime = getStartTimeInSecond(trigger.getStartTime());
				PMJobsContext pmJob = null;
				switch (pm.getTriggerTypeEnum()) {
				case ONLY_SCHEDULE_TRIGGER:
					List<PMJobsContext> pmJobs = PreventiveMaintenanceAPI.createNNumberOfPMJobs(pm, trigger, startTime, 2);
					if (!pmJobs.isEmpty()) {
						pmJob = pmJobs.get(0);
					}
					break;
				case FIXED:
				case FLOATING:
					pmJob = PreventiveMaintenanceAPI.createPMJobOnce(pm, trigger, startTime);
					break;
				}
				if (pmJob != null) {
					PreventiveMaintenanceAPI.schedulePMJob(pmJob);
					nextExecutionTimes.put(trigger.getId(), pmJob.getNextExecutionTime());
				}
			}
		}
		context.put(FacilioConstants.ContextNames.NEXT_EXECUTION_TIMES, nextExecutionTimes);
	}

	private static void addDefaultProps(PreventiveMaintenance pm, Context context) {
		WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		long templateId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);

		pm.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		pm.setTemplateId(templateId);

		pm.setCreatedById(AccountUtil.getCurrentUser().getId());
		pm.setCreatedTime(System.currentTimeMillis());
		pm.setStatus(true);

		if (workorder.getAsset() != null) {
			pm.setAssetId(workorder.getAsset().getId());
		}
		if (workorder.getSpace() != null) {
			pm.setSpaceId(workorder.getSpace().getId());
		}
		if (workorder.getAssignedTo() != null) {
			pm.setAssignedToid(workorder.getAssignedTo().getId());
		}
		if (workorder.getType() != null) {
			pm.setTypeId(workorder.getType().getId());
		}

		boolean isScheduleOnly = true;
		for (PMTriggerContext trigger : pm.getTriggers()) {
			if (trigger.getReadingRule() != null) {
				if (trigger.getSchedule() == null) {
					isScheduleOnly = false;
				} else {
					throw new IllegalArgumentException("Both schedule and Reading cannot be set for a trigger");
				}
			} else if (trigger.getSchedule() == null) {
				throw new IllegalArgumentException("Both schedule and Reading cannot be null for a trigger");
			}
		}
		if (isScheduleOnly) {
			pm.setTriggerType(TriggerType.ONLY_SCHEDULE_TRIGGER);
		}
	}

	private static long getStartTimeInSecond(long startTime) {
		long startTimeInSecond = startTime / 1000;
		startTimeInSecond = startTimeInSecond - 300; //for calculating next execution time

		return startTimeInSecond;
	}

	private void addTriggersAndReadings(PreventiveMaintenance pm) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getPMTriggersModule().getTableName()).fields(FieldFactory.getPMTriggerFields());

		for (PMTriggerContext trigger : pm.getTriggers()) {
			trigger.setPmId(pm.getId());
			trigger.setOrgId(AccountUtil.getCurrentOrg().getId());
			if (trigger.getReadingRule() != null) {
				long ruleId = WorkflowAPI.addWorkflowRule(trigger.getReadingRule());
				trigger.setReadingRuleId(ruleId);
			}
			insertBuilder.addRecord(FieldUtil.getAsProperties(trigger));
		}
		insertBuilder.save();

		List<Map<String, Object>> triggerProps = insertBuilder.getRecords();
		for (int i = 0; i < triggerProps.size(); i++) {
			Map<String, Object> triggerProp = triggerProps.get(i);
			pm.getTriggers().get(i).setId((long) triggerProp.get("id"));
		}
	}
}
