package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddDashboardCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		if(dashboard != null) {			
			List<FacilioField> fields = FieldFactory.getDashboardFields();
			
			// on add dashboard, we will set order as null so that it will go to the last in order
//			Integer lastDisplayOrder = DashboardUtil.getLastDashboardDisplayOrder(dashboard.getOrgId(), dashboard.getModuleId());
//			
//			dashboard.setDisplayOrder(lastDisplayOrder + 1);
			
			getDashboardLinkName(dashboard);
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getDashboardModule().getTableName())
					.fields(fields);

			Map<String, Object> props = FieldUtil.getAsProperties(dashboard);
			insertBuilder.addRecord(props);
			insertBuilder.save();

			dashboard.setId((Long) props.get("id"));
			context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
		}
		
		return false;
	}

	private void getDashboardLinkName(DashboardContext dashboard) throws Exception {
		
		String linkName = dashboard.getDashboardName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
		
		int i=1;
		String temp = linkName;
		while(true) {
			
			if(DashboardUtil.getDashboard(temp,dashboard.getModuleId()) == null) {
				dashboard.setLinkName(temp);
				break;
			}
			else {
				temp = linkName + i++;
			}
		}
		
	}

}