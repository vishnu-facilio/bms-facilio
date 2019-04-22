package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;

public class DeleteConnectedAppCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		long connectedAppId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if (connectedAppId > 0) {
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(ModuleFactory.getConnectedAppsModule().getTableName())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getConnectedAppsModule()))
					.andCondition(CriteriaAPI.getIdCondition(connectedAppId, ModuleFactory.getConnectedAppsModule()));
			
			builder.delete();
		}
		
		return false;
	}

}
