package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PMTaskSectionTemplateTriggers;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTaskSectionTriggersCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		if (pm == null || pm.getTriggerMap() == null) {
			return false;
		}
		
		List<TaskSectionTemplate> sectionTemplates =  (List<TaskSectionTemplate>) context.get(FacilioConstants.ContextNames.TASK_SECTION_TEMPLATES);
		if (sectionTemplates == null) {
			return false;
		}
		
		FacilioModule module = ModuleFactory.getTaskSectionTemplateTriggersModule();
		
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getTaskSectionTemplateTriggersFields());
				
		
		List<Map<String, Object>> props = new ArrayList<>();
		
		boolean isNewFlow = true;
		if(isNewFlow) {
			for (int i = 0; i < sectionTemplates.size(); i++) {
				List<PMTaskSectionTemplateTriggers> pmTaskSectionTemplateTriggers = sectionTemplates.get(i).getPmTaskSectionTemplateTriggers();
				if (pmTaskSectionTemplateTriggers == null) {
					continue;
				}
				for (int k = 0; k < pmTaskSectionTemplateTriggers.size(); k++) {
					
					PMTaskSectionTemplateTriggers pmTaskSectionTemplateTrigger = pmTaskSectionTemplateTriggers.get(k);
					PMTriggerContext trig = pm.getTriggerMap().get(pmTaskSectionTemplateTriggers.get(k).getTriggerName());
					if (trig == null) {
						throw new IllegalArgumentException("Trigger associated with section does not exist.");
					}
					pmTaskSectionTemplateTrigger.setTriggerId(trig.getId());
					pmTaskSectionTemplateTrigger.setSectionId(sectionTemplates.get(i).getId());
					props.add(FieldUtil.getAsProperties(pmTaskSectionTemplateTrigger));
				}
			}
		}
		else {
			for (int i = 0; i < sectionTemplates.size(); i++) {
				List<PMTriggerContext> triggerContexts = sectionTemplates.get(i).getPmTriggerContexts();
				if (triggerContexts == null) {
					continue;
				}
				for (int k = 0; k < triggerContexts.size(); k++) {
					Map<String, Object> prop = new HashMap<>();
					prop.put("sectionId", sectionTemplates.get(i).getId());
					PMTriggerContext trig = pm.getTriggerMap().get(triggerContexts.get(k).getName());
					if (trig == null) {
						throw new IllegalArgumentException("Trigger associated with section does not exist.");
					}
					prop.put("triggerId", trig.getId());
					props.add(prop);
				}
			}
		}
		
		if (!props.isEmpty()) {
			builder.addRecords(props);
			builder.save();
		}
		
		return false;
	}

}
