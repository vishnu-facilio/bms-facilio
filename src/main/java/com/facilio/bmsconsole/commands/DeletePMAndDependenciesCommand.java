package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.modules.FacilioStatus;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminderAction;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;


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
							for(PMReminderAction reminderAction : reminder.getReminderActions()) {
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
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SCHEDULED_WO)) {
			if (isPMDelete) {
				deleteScheduledWorkorders(context, recordIds);
			} else if (isStatusUpdate) {
				if (newPm != null && !newPm.isActive()) {
					deleteScheduledWorkorders(context, recordIds);
				}
			} else {
				deleteScheduledWorkorders(context, recordIds);
			}
			deleteMultiWoPMReminders(pmIds);
		}

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
			int count = deletePMs(recordIds);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, count);
		}
		
		TemplateAPI.deleteTemplates(templateIds);
		
		return false;
	}

	private List<Long> getScheduledWOIds(List<Long> pmIds) throws Exception {
		FacilioStatus status = TicketAPI.getStatus("preopen");
		if (status == null) {
			CommonCommandUtil.emailAlert("Org does not have pre-open state", "ORGID: "+ AccountUtil.getCurrentAccount().getOrg().getOrgId());
			throw new IllegalStateException("Org does not have pre-open state");
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
		selectRecordsBuilder.select(Arrays.asList(FieldFactory.getIdField(module)))
				.beanClass(WorkOrderContext.class)
				.module(module)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), pmIds, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), String.valueOf(status.getId()), NumberOperators.EQUALS));
		List<WorkOrderContext> wos = selectRecordsBuilder.get();
		if (wos != null && !wos.isEmpty()) {
			List<Long> res = new ArrayList<>();
			for (WorkOrderContext w : wos) {
				res.add(w.getId());
			}
			return res;
		}
		return Collections.emptyList();
	}

	private void deleteScheduledWorkorders(Context context, List<Long> pmIds) throws Exception {
		if (pmIds.isEmpty()) {
			return;
		}
		List<Long> ids = getScheduledWOIds(pmIds);
		if (!ids.isEmpty()) {
			Object peviousVal = context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
			Chain deleteScheduledWOChain = FacilioChainFactory.getDeleteScheduledWorkOrderChain();
			deleteScheduledWOChain.execute(context);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, peviousVal);
		}
	}

	private void deleteMultiWoPMReminders(List<Long> pmIds) throws Exception {
		if (pmIds.isEmpty()) {
			return;
		}

		FacilioModule module = ModuleFactory.getPMResourceScheduleRuleRelModule();
		Map<String, FacilioField> fieldMap =  FieldFactory.getAsMap(FieldFactory.getPMResourceScheduleRuleRelFields());

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
		selectRecordBuilder.select(Arrays.asList(fieldMap.get("scheduleRuleId")))
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmId"), pmIds, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		List<Map<String, Object>> props = selectRecordBuilder.get();
		List<Long> workFlowIds = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop: props) {
				if (prop.get("scheduleRuleId") != null) {
					workFlowIds.add((Long) prop.get("scheduleRuleId"));
				}
			}
		}

		if (!workFlowIds.isEmpty()) {
			WorkflowRuleAPI.deleteWorkFlowRules(workFlowIds);
		}

		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmId"), pmIds, NumberOperators.EQUALS));
		deleteRecordBuilder.delete();
	}
	
	private void deletePMReminders(List<Long> pmIds) throws Exception {
		if (pmIds.isEmpty()) {
			return;
		}

		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SCHEDULED_WO)) {
			deleteNewPMReminders(pmIds);
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

	private void deleteNewPMReminders(List<Long> pmIds) throws Exception {
		FacilioModule reminderModule = ModuleFactory.getPMReminderModule();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPMReminderFields());

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(Arrays.asList(fieldMap.get("scheduleRuleId")))
				.table(reminderModule.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(reminderModule))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmId"), pmIds, NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectRecordBuilder.get();
		List<Long> workFlowIds = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop: props) {
				if (prop.get("scheduleRuleId") != null) {
					workFlowIds.add((Long) prop.get("scheduleRuleId"));
				}
			}
		}

		if (!workFlowIds.isEmpty()) {
			WorkflowRuleAPI.deleteWorkFlowRules(workFlowIds);
		}

		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(reminderModule.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(reminderModule))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmId"), pmIds, NumberOperators.EQUALS));
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
