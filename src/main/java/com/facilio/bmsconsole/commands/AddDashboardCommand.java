package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;

public class AddDashboardCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		if(dashboard != null) {			
			List<FacilioField> fields = FieldFactory.getDashboardFields();
			InsertRecordBuilder<DashboardContext> dashboardBuilder = new InsertRecordBuilder<DashboardContext>()
																		.module(ModuleFactory.getDashboardModule())
																		.fields(fields)
																		.table(ModuleFactory.getDashboardModule().getTableName())
																		.addRecord(dashboard);
			
			dashboardBuilder.save();
			
			context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
		}
		
		return false;
	}

}