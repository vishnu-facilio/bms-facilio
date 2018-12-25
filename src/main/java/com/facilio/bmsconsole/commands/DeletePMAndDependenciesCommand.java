package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminderAction;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;

public class DeletePMAndDependenciesCommand implements Command{
	
	private boolean isPMDelete;
	private boolean isStatusUpdate = false;
	public DeletePMAndDependenciesCommand(boolean isDelete, boolean... isStatusUpdate) {
		this.isPMDelete = isDelete;
		if (isStatusUpdate != null && isStatusUpdate.length > 0) {
			this.isStatusUpdate = isStatusUpdate[0];
		}
	}

	@Override
	public boolean execute(Context context) throws Exception {
		
		List<Long> templateIds = new ArrayList<>();
		List<Long> ruleIds = new ArrayList<>();
		List<Long> pmIds = new ArrayList<>();
		List<Long> triggerPMIds = new ArrayList<>();
		
		PreventiveMaintenance newPm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		boolean deleteOnStatusUpdate = isStatusUpdate && newPm != null && newPm.isActive();
		
		List<PreventiveMaintenance> oldPms = ((List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST));
		List<Long> actionIds = new ArrayList<>();
		
		if (oldPms != null) {
			for(PreventiveMaintenance oldPm: oldPms) {
				pmIds.add(oldPm.getId());
				if (!isStatusUpdate) {
					templateIds.add(oldPm.getTemplateId());
				}
				
				if(oldPm.hasTriggers() && oldPm.getTriggers() != null && (isPMDelete || newPm.getTriggers() != null || deleteOnStatusUpdate)) {
					List<Long> triggerIds = new ArrayList<>();
					oldPm.getTriggers().forEach(trigger -> {
						if(trigger.getRuleId() != -1) {
							ruleIds.add(trigger.getRuleId());
						}
						triggerIds.add(trigger.getId());
					});
					
					List<PMJobsContext> pmJobs = PreventiveMaintenanceAPI.getPMJobs(triggerIds,true);
					templateIds.addAll(pmJobs.stream().map(PMJobsContext::getTemplateId).collect(Collectors.toList()));
					
					triggerPMIds.add(oldPm.getId());
					
					List<PMReminder> reminders = oldPm.getReminders();
					if (reminders != null) {
						for(PMReminder reminder :reminders) {
							for( PMReminderAction reminderAction : reminder.getReminderActions()) {
								actionIds.add(reminderAction.getActionId());
							}
							reminder.setReminderActions(null);			// temp fix, should handle differently for custom template case
						}
					}
					
				}
			}
		}
		
		if (!ruleIds.isEmpty()) {
			WorkflowRuleAPI.deleteWorkFlowRules(ruleIds);
		}
		
		deletePmResourcePlanner(pmIds);
		deletePmIncludeExclude(pmIds);
		deleteTriggers(triggerPMIds);
		ActionAPI.deleteActions(actionIds);
		deletePMReminders(pmIds);
		
//		if(isPMDelete || deleteOnStatusUpdate || (newPm != null && newPm.getId() != -1 && newPm.getReminders() != null)) {
//			
//			List<PMReminder> reminders = PreventiveMaintenanceAPI.getPMReminders(pmIds);
//			if (reminders != null) {
//				List<Long> actionIds = new ArrayList<>();
//				for(PMReminder reminder :reminders) {
//					for( PMReminderAction reminderAction : reminder.getReminderActions()) {
//						actionIds.add(reminderAction.getActionId());
//					}
//					reminder.setReminderActions(null);			// temp fix, should handle differently for custom template case
//				}
//				ActionAPI.deleteActions(actionIds);
//				deletePMReminders(pmIds);
//			}
//		}
		
		if(isPMDelete) {
			List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			int count = deletePMs(recordIds);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, count);
		}
		
		TemplateAPI.deleteTemplates(templateIds);
		
		return false;
	}
	
	private void deletePMReminders(List<Long> pmIds) throws Exception {
		if (pmIds.isEmpty()) {
			return;
		}
		FacilioModule reminderModule = ModuleFactory.getPMReminderModule();
		FacilioField pmIdField = new FacilioField();
		pmIdField.setName("pmId");
		pmIdField.setDataType(FieldType.NUMBER);
		pmIdField.setColumnName("PM_ID");
		pmIdField.setModule(reminderModule);
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(reminderModule.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(reminderModule))
				.andCondition(CriteriaAPI.getCondition(pmIdField, pmIds, NumberOperators.EQUALS));
		deleteBuilder.delete();
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
	
	private void deletePmResourcePlanner(List<Long> pmids) throws Exception {
		if (pmids !=  null && !pmids.isEmpty()) {
			FacilioModule module = ModuleFactory.getPMResourcePlannerModule();
			List<FacilioField> fields = FieldFactory.getPMResourcePlannerFields();
			FacilioField pmIdField = FieldFactory.getAsMap(fields).get("pmId");
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getCondition(pmIdField,StringUtils.join(pmids, ","), NumberOperators.EQUALS));
			deleteBuilder.delete();
		}
	}
	
	private void deletePmIncludeExclude(List<Long> pmids) throws Exception {
		if (pmids !=  null && !pmids.isEmpty()) {
			FacilioModule module = ModuleFactory.getPMIncludeExcludeResourceModule();
			List<FacilioField> fields = FieldFactory.getPMIncludeExcludeResourceFields();
			FacilioField pmIdField = FieldFactory.getAsMap(fields).get("pmId");
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getCondition(pmIdField,StringUtils.join(pmids, ","), NumberOperators.EQUALS));
			deleteBuilder.delete();
		}
	}
	
	private int deletePMs(List<Long> recordIds) throws Exception {
		//Deleting via Delete Cascading
		
		int count = 0;
		if(recordIds != null && !recordIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
			DeleteRecordBuilder<ResourceContext> deleteBuilder = new DeleteRecordBuilder<ResourceContext>()
																		.module(module)
																		.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
			count = deleteBuilder.delete();
		}
		return count;
	}

}
