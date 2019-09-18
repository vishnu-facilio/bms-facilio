package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

public class DeleteRelatedAssetsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long id = (long) context.get(FacilioConstants.ContextNames.ID);
		if (id > 0) {
			
			FacilioModule module = ModuleFactory.getRelatedAssetsModule();
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder().table(module.getTableName())
					.andCondition(CriteriaAPI.getIdCondition(id, module));
			deleteBuilder.delete();
		}
		return false;
	}

}
