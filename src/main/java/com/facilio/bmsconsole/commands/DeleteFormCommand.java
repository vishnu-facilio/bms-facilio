package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;

public class DeleteFormCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long formId = (long) context.get(FacilioConstants.ContextNames.FORM_ID);
		FacilioModule module = ModuleFactory.getFormModule();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(formId, module));
		int count = builder.delete();
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, count);
		
		return false;
	}

}
