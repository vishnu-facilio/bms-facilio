package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;

public class DeleteFormSiteRelationCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getFormSiteRelationModule().getTableName())
				.andCustomWhere("FORM_ID = ?", form.getId());
				;
		 deleteBuilder.delete();
		return false;
	}

}
