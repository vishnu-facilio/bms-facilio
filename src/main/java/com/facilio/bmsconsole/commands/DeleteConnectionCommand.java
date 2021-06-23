package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;

public class DeleteConnectionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ConnectionContext connectionContext = (ConnectionContext) context.get(FacilioConstants.ContextNames.CONNECTION);
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getConnectionModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(connectionContext.getId(), ModuleFactory.getConnectionModule()));
		
		deleteRecordBuilder.delete();
		return false;
	}

}
