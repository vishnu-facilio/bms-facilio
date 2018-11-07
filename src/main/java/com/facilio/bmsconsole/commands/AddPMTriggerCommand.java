package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddPMTriggerCommand implements Command {
	
	private boolean isBulkUpdate = false;
	
	public AddPMTriggerCommand() {}
	
	public AddPMTriggerCommand(boolean isBulkUpdate) {
		this.isBulkUpdate = isBulkUpdate;
	}

	@Override
	public boolean execute(Context context) throws Exception {
		List<PreventiveMaintenance> pms;
		if (isBulkUpdate) {
			pms = (List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
		}
		else {
			pms = Collections.singletonList((PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE));
		}
		
		if (pms == null) {
			return false;
		}
		
		for(PreventiveMaintenance pm: pms) {
			if (pm.getTriggers() != null && pm.isActive()) {
				addTriggersAndReadings(pm);
			}
		}
		return false;
	}
	
	private void addTriggersAndReadings(PreventiveMaintenance pm) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getPMTriggersModule().getTableName()).fields(FieldFactory.getPMTriggerFields());
		
		for (PMTriggerContext trigger : pm.getTriggers()) {
			trigger.setPmId(pm.getId());
			trigger.setOrgId(AccountUtil.getCurrentOrg().getId());
			if (trigger.getReadingRule() != null) {
				
				trigger.getReadingRule().setName("PM_" + pm.getId());
				long ruleId = WorkflowRuleAPI.addWorkflowRule(trigger.getReadingRule());
				trigger.setReadingRuleId(ruleId);
				
				ActionContext action = new ActionContext();
				action.setActionType(ActionType.EXECUTE_PM);
				action.setStatus(true);
				
				List<ActionContext> actions = Collections.singletonList(action);
				ActionAPI.addActions(actions);
				ActionAPI.addWorkflowRuleActionRel(ruleId, actions);
			}
			else if (trigger.getStartTime() < System.currentTimeMillis()) {
				trigger.setStartTime(System.currentTimeMillis());
			}
			insertBuilder.addRecord(FieldUtil.getAsProperties(trigger));
		}
		insertBuilder.save();

		List<Map<String, Object>> triggerProps = insertBuilder.getRecords();
		Map<String,PMTriggerContext> triggerMap = new HashMap<>();
		for (int i = 0; i < triggerProps.size(); i++) {
			Map<String, Object> triggerProp = triggerProps.get(i);
			pm.getTriggers().get(i).setId((long) triggerProp.get("id"));
			triggerMap.put(pm.getTriggers().get(i).getName(), pm.getTriggers().get(i));
		}
		pm.setTriggerMap(triggerMap);
	}

}
