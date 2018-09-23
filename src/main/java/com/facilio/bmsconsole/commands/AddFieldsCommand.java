package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
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
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		
		if(fields != null && !fields.isEmpty()) {
			FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if(module == null) {
				String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
				module = modBean.getModule(moduleName);
			}
			
			if(module != null) {
				List<Long> fieldIds = new ArrayList<>();
				List<List<ReadingRuleContext>> readingRules = new ArrayList<>();
				List<List<List<ActionContext>>> actionsList = new ArrayList<>();
				for(FacilioField field : fields) {
					field.setModule(module);
					
					if(field.getName() == null || field.getName().isEmpty()) {
						if(field.getDisplayName() != null && !field.getDisplayName().isEmpty()) {
							field.setName(field.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
						}
						else {
							throw new IllegalArgumentException("Invalid name for field of module : "+module.getName());
						}
					}
					
					long fieldId = modBean.addField(field);
					field.setFieldId(fieldId);
					fieldIds.add(fieldId);
					
					if ((field.getDataTypeEnum() == FieldType.NUMBER || field.getDataTypeEnum() == FieldType.DECIMAL) && field instanceof NumberField) {
						NumberField numberField = (NumberField) field;
						if (numberField.isCounterField()) {
							NumberField deltaField = FieldUtil.cloneBean(numberField, NumberField.class);
							deltaField.setCounterField(null);
							deltaField.setName(deltaField.getName()+"Delta");
							deltaField.setDisplayName(deltaField.getDisplayName()+" Delta");
							fields.add(deltaField);
						}
					}
					
					//The following code has to be moved somewhere else
					List<ReadingRuleContext> rule = field.getReadingRules();
					List<List<ActionContext>> actions = new ArrayList<>();
					
					if (rule != null && !rule.isEmpty()) {
						rule.stream().forEach((r) -> {
							r.setReadingFieldId(fieldId);
							r.getEvent().setModuleId(field.getModule().getModuleId());
							actions.add(r.getActions());
						});
						readingRules.add(rule);
						actionsList.add(actions);
					}
				}
				context.put(FacilioConstants.ContextNames.READING_RULES_LIST, readingRules);
				context.put(FacilioConstants.ContextNames.ACTIONS_LIST, actionsList);
				context.put(FacilioConstants.ContextNames.MODULE_FIELD_IDS, fieldIds);
			}
		}
		else {
			throw new IllegalArgumentException("No Fields to Add");
		}
		return false;
	}	
}