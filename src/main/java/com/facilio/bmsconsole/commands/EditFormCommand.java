package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;

public class EditFormCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		FacilioForm editedForm = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		Map<String, Object> props = FieldUtil.getAsProperties(editedForm);
		FacilioModule formModule = ModuleFactory.getFormModule();
		
		GenericUpdateRecordBuilder formUpdateBuilder = new GenericUpdateRecordBuilder()
				.table(formModule.getTableName())
				.fields(FieldFactory.getFormFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(formModule))
				.andCondition(CriteriaAPI.getIdCondition(editedForm.getId(), formModule));
		
		formUpdateBuilder.update(props);
		
		return false;
	}

}
