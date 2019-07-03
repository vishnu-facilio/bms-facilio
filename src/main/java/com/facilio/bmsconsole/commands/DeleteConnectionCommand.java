package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class DeleteConnectionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		ConnectionContext connectionContext = (ConnectionContext) context.get(FacilioConstants.ContextNames.CONNECTION);
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getConnectionModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(connectionContext.getId(), ModuleFactory.getConnectionModule()));
		
		deleteRecordBuilder.delete();
		return false;
	}

}
