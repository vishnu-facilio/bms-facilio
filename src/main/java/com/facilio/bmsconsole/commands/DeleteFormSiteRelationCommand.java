package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class DeleteFormSiteRelationCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormSiteRelationFields());
		long formId = -1;
		if (form != null && form.getSiteIds() != null) {
			formId = form.getId();
		}
		else if (form == null) {
			formId = (long) context.get(FacilioConstants.ContextNames.FORM_ID);
		}
		if (formId > 0) {	
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(ModuleFactory.getFormSiteRelationModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), String.valueOf(formId), NumberOperators.EQUALS))
					;
			 deleteBuilder.delete();
		}
		return false;
	}

}
