package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.forms.FormField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Map;

public class UpdateFormFieldCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		FormField field = (FormField) context.get(FacilioConstants.ContextNames.FORM_FIELD);
		FacilioModule module = ModuleFactory.getFormFieldsModule();
		GenericUpdateRecordBuilder formUpdateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getFormFieldsFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(field.getId(), module));
		
		Map<String, Object> props = FieldUtil.getAsProperties(field);
		formUpdateBuilder.update(props);
		
		return false;
	}

}
