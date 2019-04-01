package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class EditFormCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		FacilioForm oldForm = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		FacilioForm editedForm = (FacilioForm) context.get(FacilioConstants.ContextNames.EDITED_FORM);
		editedForm.setId(oldForm.getId());
		Map<String, Object> props = FieldUtil.getAsProperties(editedForm);
		FacilioModule formModule = ModuleFactory.getFormModule();
		
		GenericUpdateRecordBuilder formUpdateBuilder = new GenericUpdateRecordBuilder()
				.table(formModule.getTableName())
				.fields(FieldFactory.getFormFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(formModule))
				.andCondition(CriteriaAPI.getIdCondition(oldForm.getId(), formModule));
		
		int records = formUpdateBuilder.update(props);
		
		if (records == 0) {
			throw new IllegalStateException();
		}
		
		FacilioModule formFieldModule = ModuleFactory.getFormFieldsModule();
		GenericDeleteRecordBuilder fieldDeleteBuilder = new GenericDeleteRecordBuilder()
			.table(formFieldModule.getTableName())
			.andCondition(CriteriaAPI.getCurrentOrgIdCondition(formFieldModule))
			.andCondition(CriteriaAPI.getCondition("FORMID", "formId", String.valueOf(oldForm.getId()), NumberOperators.EQUALS));
		
		fieldDeleteBuilder.delete();
		
		List<Map<String, Object>> fieldProps = new ArrayList<>();
		
		int i = 1;
		long orgId = AccountUtil.getCurrentOrg().getId();
		for (FormField f: editedForm.getFields()) {
			f.setFormId(oldForm.getId());
			f.setOrgId(orgId);
			f.setSequenceNumber(i);
			Map<String, Object> prop = FieldUtil.getAsProperties(f);
			if (prop.get("required") == null) {
				prop.put("required", false);
			}
			fieldProps.add(prop);
			++i;
		}
		
		GenericInsertRecordBuilder fieldInsertBuilder = new GenericInsertRecordBuilder()
				.table(formFieldModule.getTableName())
				.fields(FieldFactory.getFormFieldsFields())
				.addRecords(fieldProps);
		
		fieldInsertBuilder.save();
		
		return false;
	}

}
