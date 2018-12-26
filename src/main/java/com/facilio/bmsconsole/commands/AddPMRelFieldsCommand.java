package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PMResourcePlannerReminderContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddPMRelFieldsCommand implements Command {
	
	private boolean isBulkUpdate = false;
	
	public AddPMRelFieldsCommand() {}
	
	public AddPMRelFieldsCommand(boolean isBulkUpdate) {
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
		
		for(PreventiveMaintenance pm:pms) {
			if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTIPLE) {
				TemplateAPI.addIncludeExcludePropsForPM(pm);
				prepareAndAddResourcePlanner(pm);
			}
		}
		return false;
	}
	private void prepareAndAddResourcePlanner(PreventiveMaintenance pm) throws Exception {
		
		
		if(pm.getResourcePlanners() != null) {
			
			for(PMResourcePlannerContext resourcePlanner :pm.getResourcePlanners()) {
				if(resourcePlanner.getTriggerName() != null) {
					PMTriggerContext trigger = pm.getTriggerMap().get(resourcePlanner.getTriggerName());
					resourcePlanner.setTriggerId(trigger.getId());
					resourcePlanner.setPmId(pm.getId());
				}
				Map<String, Object> prop = FieldUtil.getAsProperties(resourcePlanner);
				GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder();
				insert.table(ModuleFactory.getPMResourcePlannerModule().getTableName());
				insert.fields(FieldFactory.getPMResourcePlannerFields());
				insert.insert(prop);
				resourcePlanner.setId((Long)prop.get("id"));
				
				
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
