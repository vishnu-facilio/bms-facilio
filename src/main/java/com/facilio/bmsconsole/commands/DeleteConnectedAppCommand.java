package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;

public class DeleteConnectedAppCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long connectedAppId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if (connectedAppId > 0) {
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(ModuleFactory.getConnectedAppsModule().getTableName())
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getConnectedAppsModule()))
					.andCondition(CriteriaAPI.getIdCondition(connectedAppId, ModuleFactory.getConnectedAppsModule()));
			
			builder.delete();
		}
		
		return false;
	}

}
