package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

public class DeleteRelationshipCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long id = (long) context.get(FacilioConstants.ContextNames.ID);
		if (id > 0) {
			FacilioModule module = ModuleFactory.getRelationshipModule();
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder().table(module.getTableName())
					.andCondition(CriteriaAPI.getIdCondition(id, module));
			deleteBuilder.delete();
		}
		return false;
	}

}
