package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

public class DeleteCommissioningCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long id = (long) context.get(ContextNames.ID);
		CommissioningLogContext log = CommissioningApi.commissioniongDetails(id, false);
		if (log.getPublishedTime() != -1) {
			throw new IllegalArgumentException("Published log cannot be deleted");
		}
		
		FacilioModule module = ModuleFactory.getCommissioningLogModule();
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
			.table(module.getTableName())
			.andCondition(CriteriaAPI.getIdCondition(id, module));
		
		deleteRecordBuilder.delete();
		
		return false;
	}

}
