package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;

public class DeletePMAndDependenciesCommand implements Command{
	
	private boolean isPMDelete;
	public DeletePMAndDependenciesCommand(boolean isDelete) {
		this.isPMDelete = isDelete;
	}

	@Override
	public boolean execute(Context context) throws Exception {
		
		List<Long> templateIds = new ArrayList<>();
		List<Long> ruleIds = new ArrayList<>();
		List<Long> pmIds = new ArrayList<>();
		List<Long> triggerPMIds = new ArrayList<>();
		
		PreventiveMaintenance newPm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		
		List<PreventiveMaintenance> oldPms = ((List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST));
		for(PreventiveMaintenance oldPm: oldPms) {
			pmIds.add(oldPm.getId());
			templateIds.add(oldPm.getTemplateId());
			
			if(oldPm.hasTriggers() && (isPMDelete || newPm.getTriggers() != null)) {
				List<Long> triggerIds = new ArrayList<>();
				oldPm.getTriggers().forEach(trigger -> {
					if(trigger.getReadingRuleId() != -1) {
						ruleIds.add(trigger.getReadingRuleId());
					}
					triggerIds.add(trigger.getId());
				});
				
				List<PMJobsContext> pmJobs = PreventiveMaintenanceAPI.getPMJobs(triggerIds,true);
				templateIds.addAll(pmJobs.stream().map(PMJobsContext::getTemplateId).collect(Collectors.toList()));
				
				triggerPMIds.add(oldPm.getId());
				
			}
		}
		
		if (!ruleIds.isEmpty()) {
			WorkflowRuleAPI.deleteWorkFlowRules(ruleIds);
		}
		
		deleteTriggers(triggerPMIds);
		
		List<PMReminder> newReminders = (List<PMReminder>) context.get(FacilioConstants.ContextNames.PM_REMINDERS);
		if(isPMDelete || (newPm != null && newPm.getId() != -1 && newReminders != null)) {
			
			List<PMReminder> reminders = PreventiveMaintenanceAPI.getPMReminders(pmIds);
			
			List<Long> actionIds = new ArrayList<>();
			actionIds.addAll(reminders.stream().map(PMReminder::getActionId).collect(Collectors.toList()));
			ActionAPI.deleteActions(actionIds);

		}
		
		if(isPMDelete) {
			List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			int count = deletePMs(recordIds);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, count);
		}
		
		TemplateAPI.deleteTemplates(templateIds);
		
		return false;
	}
	
	private void deleteTriggers(List<Long> triggerPMIds) throws Exception {
		if (!triggerPMIds.isEmpty()) {
			FacilioModule triggerModule = ModuleFactory.getPMTriggersModule();
			List<FacilioField> triggerFields = FieldFactory.getPMTriggerFields();
			FacilioField pmIdField = FieldFactory.getAsMap(triggerFields).get("pmId");
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(triggerModule.getTableName())
					.andCondition(CriteriaAPI.getCondition(pmIdField, triggerPMIds, NumberOperators.EQUALS));
			deleteBuilder.delete();
		}
	}
	
	private int deletePMs(List<Long> recordIds) throws Exception {
		int count = 0;
		if(recordIds != null && !recordIds.isEmpty()) {
			
			FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table("Preventive_Maintenance")
					.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
			
			count = builder.delete();

		}
		return count;
	}

}
