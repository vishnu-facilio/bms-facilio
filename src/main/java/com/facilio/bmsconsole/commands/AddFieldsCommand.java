package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddFieldsCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		//Have to be converted to batch insert
		List<FacilioModule> modules = CommonCommandUtil.getModulesWithFields(context);
		if(modules != null && !modules.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<Long> fieldIds = new ArrayList<>();
			List<List<ReadingRuleContext>> readingRules = new ArrayList<>();
			List<List<List<ActionContext>>> actionsList = new ArrayList<>();
			for (FacilioModule module : modules) {
				FacilioModule cloneMod = new FacilioModule(module);
				if(module != null && module.getFields() != null && !module.getFields().isEmpty()) {
					for(FacilioField field : module.getFields()) {
						field.setModule(cloneMod);
						constructFieldName(field, module);
						long fieldId = modBean.addField(field);
						field.setFieldId(fieldId);
						fieldIds.add(fieldId);
						if (field instanceof NumberField) {
							NumberField numberField = (NumberField) field;
							if (numberField.isCounterField()) {
								NumberField deltaField = FieldUtil.cloneBean(numberField, NumberField.class);
								deltaField.setCounterField(null);
								deltaField.setName(deltaField.getName()+"Delta");
								deltaField.setDisplayName(deltaField.getDisplayName()+" Delta");
								module.getFields().add(deltaField);
							}
						}
						
						//The following code has to be moved somewhere else. This is not needed here
						addSafeLimits(field, readingRules, actionsList);
					}
				}
			}
			context.put(FacilioConstants.ContextNames.READING_RULES_LIST, readingRules);
			context.put(FacilioConstants.ContextNames.ACTIONS_LIST, actionsList);
			context.put(FacilioConstants.ContextNames.MODULE_FIELD_IDS, fieldIds);
		}
		else {
			throw new IllegalArgumentException("No Fields to Add");
		}
		return false;
	}
	
	private void constructFieldName(FacilioField field, FacilioModule module) {
		if(field.getName() == null || field.getName().isEmpty()) {
			if(field.getDisplayName() != null && !field.getDisplayName().isEmpty()) {
				field.setName(field.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
			}
			else {
				throw new IllegalArgumentException("Invalid name for field of module : "+module.getName());
			}
		}
	}
	
	private void addSafeLimits(FacilioField field, List<List<ReadingRuleContext>> readingRules, List<List<List<ActionContext>>> actionsList) {
		List<ReadingRuleContext> rule = field.getReadingRules();
		List<List<ActionContext>> actions = new ArrayList<>();
		
		if (rule != null && !rule.isEmpty()) {
			rule.stream().forEach((r) -> {
				r.setReadingFieldId(field.getFieldId());
				r.getEvent().setModuleId(field.getModule().getModuleId());
				actions.add(r.getActions());
			});
			readingRules.add(rule);
			actionsList.add(actions);
		}
	}
}