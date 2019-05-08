package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;

public class UpdateFormSectionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		FormSection section = (FormSection) context.get(FacilioConstants.ContextNames.FORM_SECTION);
		long formId = (long) context.get(FacilioConstants.ContextNames.FORM_ID);
		FacilioModule module = ModuleFactory.getFormSectionModule();
		List<FacilioField> fields = FieldFactory.getFormSectionFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(section.getId(), module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), String.valueOf(formId), NumberOperators.EQUALS))
				;
		
		Map<String, Object> props = FieldUtil.getAsProperties(section);
		builder.update(props);
		
		return false;
	}

}
