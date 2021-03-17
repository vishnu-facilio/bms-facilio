package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PMResourcePlannerReminderContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections4.CollectionUtils;

public class AddPMRelFieldsCommand extends FacilioCommand {
	
	private boolean isBulkUpdate = false;
	
	public AddPMRelFieldsCommand() {}
	
	public AddPMRelFieldsCommand(boolean isBulkUpdate) {
		this.isBulkUpdate = isBulkUpdate;
	}

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
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
		
		for(PreventiveMaintenance pm:pms) {
			if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTIPLE || pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTI_SITE) {
				TemplateAPI.addIncludeExcludePropsForPM(pm);
				prepareAndAddResourcePlanner(context, pm);
			}
			if(AccountUtil.getCurrentOrg().getOrgId() == 173l) {
				addPmJobPlans(pm);
			}
		}
		return false;
	}

	private void addPmJobPlans(PreventiveMaintenance pm) throws Exception {
		if(CollectionUtils.isNotEmpty(pm.getJobPlanList())) {
			PreventiveMaintenanceAPI.deleteJobPlansForPm(pm.getId());
			PreventiveMaintenanceAPI.addJobPlansForPm(pm);
		}
	}

	private void prepareAndAddResourcePlanner(Context context, PreventiveMaintenance pm) throws Exception {
		PreventiveMaintenance pm1 = PreventiveMaintenanceAPI.getPM(pm.getId(), false);
		pm.setDefaultAllTriggers(pm1.getDefaultAllTriggers());
		PreventiveMaintenanceAPI.populateResourcePlanner(pm, pm.getResourcePlannersMap());
		if(pm.getResourcePlanners() != null) {
			for(PMResourcePlannerContext resourcePlanner :pm.getResourcePlanners()) {
				if(resourcePlanner.getTriggerContexts() != null) {
					List<PMTriggerContext> trigs = new ArrayList<>();
					for (PMTriggerContext t: resourcePlanner.getTriggerContexts()) {
						PMTriggerContext trigger = pm.getTriggerMap().get(t.getName());
						trigs.add(trigger);
					}
					resourcePlanner.setTriggerContexts(trigs);
					resourcePlanner.setPmId(pm.getId());
				}
				Map<String, Object> prop = FieldUtil.getAsProperties(resourcePlanner);
				GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder();
				insert.table(ModuleFactory.getPMResourcePlannerModule().getTableName());
				insert.fields(FieldFactory.getPMResourcePlannerFields());
				insert.insert(prop);
				resourcePlanner.setId((Long)prop.get("id"));

				List<Map<String, Object>> relProps = new ArrayList<>();
				if (resourcePlanner.getTriggerContexts() != null) {
					for (PMTriggerContext t: resourcePlanner.getTriggerContexts()) {
						Map<String, Object> p = new HashMap<>();
						p.put("orgId", AccountUtil.getCurrentAccount().getOrg().getId());
						p.put("triggerId", t.getId());
						p.put("resourcePlannerId", resourcePlanner.getId());
						relProps.add(p);
					}
				}

				if (!relProps.isEmpty()) {
					GenericInsertRecordBuilder insertTrigResourceRel = new GenericInsertRecordBuilder()
							.table(ModuleFactory.getPMResourcePlannerTriggersModule().getTableName())
							.fields(FieldFactory.getPMResourcePlannerTriggersFields());

					insertTrigResourceRel.addRecords(relProps);
					insertTrigResourceRel.save();
				}
				
				if(resourcePlanner.getPmResourcePlannerReminderContexts() != null) {
					for(PMResourcePlannerReminderContext pmResourcePlannerReminderContext : resourcePlanner.getPmResourcePlannerReminderContexts()) {
						if(pmResourcePlannerReminderContext.getReminderName() != null) {
							PMReminder reminder = pm.getReminderMap().get(pmResourcePlannerReminderContext.getReminderName());
							pmResourcePlannerReminderContext.setReminderId(reminder.getId());
							pmResourcePlannerReminderContext.setPmId(pm.getId());
							pmResourcePlannerReminderContext.setResourcePlannerId(resourcePlanner.getId());
							Map<String, Object> prop1 = FieldUtil.getAsProperties(pmResourcePlannerReminderContext);
							
							GenericInsertRecordBuilder insert1 = new GenericInsertRecordBuilder();
							insert1.table(ModuleFactory.getPMResourcePlannerReminderModule().getTableName());
							insert1.fields(FieldFactory.getPMResourcePlannerReminderFields());
							insert1.insert(prop1);
							
							pmResourcePlannerReminderContext.setId((Long)prop1.get("id"));
						}
					}
				}
			}
		}
	}
}
