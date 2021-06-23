package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class UpdateFormSectionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		FormSection section = (FormSection) context.get(FacilioConstants.ContextNames.FORM_SECTION);
		long formId = (long) context.get(FacilioConstants.ContextNames.FORM_ID);
		FacilioModule module = ModuleFactory.getFormSectionModule();
		List<FacilioField> fields = FieldFactory.getFormSectionFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(section.getId(), module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), String.valueOf(formId), NumberOperators.EQUALS))
				;
		
		Map<String, Object> prop = FieldUtil.getAsProperties(section);
		if (section.getSubFormValue() == null) {
			prop.put("subFormValueStr", "");
		}
		builder.update(prop);
		
		return false;
	}

}
