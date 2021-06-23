package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddDashboardTabCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		DashboardTabContext dashboardTab = (DashboardTabContext) context.get(FacilioConstants.ContextNames.DASHBOARD_TAB);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getDashboardTabModule().getTableName())
				.fields(FieldFactory.getDashboardTabFields());

		Map<String, Object> prop = FieldUtil.getAsProperties(dashboardTab);
		insertBuilder.addRecord(prop);
		insertBuilder.save();

		dashboardTab.setId((Long) prop.get("id"));
		context.put(FacilioConstants.ContextNames.DASHBOARD_TAB, dashboardTab);
		return false;
	}

}
