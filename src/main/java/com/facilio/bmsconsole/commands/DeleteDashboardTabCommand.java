package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;

public class DeleteDashboardTabCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long dashboardTabId = (Long) context.get(FacilioConstants.ContextNames.DASHBOARD_TAB_ID);
		if(dashboardTabId != null) {
			
			GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
			
			deleteRecordBuilder.table(ModuleFactory.getDashboardTabModule().getTableName())
			.andCondition(CriteriaAPI.getIdCondition(dashboardTabId, ModuleFactory.getDashboardTabModule()));
			deleteRecordBuilder.delete();
		}
		return false;
	}

}