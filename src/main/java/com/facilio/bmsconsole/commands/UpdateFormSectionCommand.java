package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
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
		section.setFormId(formId);

		FacilioModule module = ModuleFactory.getFormSectionModule();
		List<FacilioField> fields = FieldFactory.getFormSectionFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		long systemTime = System.currentTimeMillis();
		User currentUser = AccountUtil.getCurrentUser();
		section.setSysModifiedTime(systemTime);
		if (currentUser != null) {
			section.setSysModifiedBy(AccountUtil.getCurrentUser().getPeopleId());
		}

		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(section.getId(), module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), String.valueOf(formId), NumberOperators.EQUALS))
				;
		
		Map<String, Object> prop = FieldUtil.getAsProperties(section);
		context.put(FacilioConstants.ContextNames.FORM_SECTION,prop);

		if (section.getSubFormValue() == null) {
			prop.put("subFormValueStr", "");
		}
		builder.update(prop);
		
		return false;
	}

}
