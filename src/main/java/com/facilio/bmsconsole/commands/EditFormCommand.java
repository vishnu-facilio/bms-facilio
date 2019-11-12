package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class EditFormCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		FacilioForm editedForm = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		Map<String, Object> props = FieldUtil.getAsProperties(editedForm);
		FacilioModule formModule = ModuleFactory.getFormModule();
		
		GenericUpdateRecordBuilder formUpdateBuilder = new GenericUpdateRecordBuilder()
				.table(formModule.getTableName())
				.fields(FieldFactory.getFormFields())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(formModule))
				.andCondition(CriteriaAPI.getIdCondition(editedForm.getId(), formModule));
		
		formUpdateBuilder.update(props);
		
		if (CollectionUtils.isNotEmpty(editedForm.getSections())) {
			FormsAPI.initFormFields(editedForm);
		}
		
		return false;
	}

}
