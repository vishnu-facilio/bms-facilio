package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardContext.DashboardPublishStatus;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AddDashboardCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		if(dashboard != null) {			
			
			dashboard.setPublishStatus(DashboardPublishStatus.NONE.ordinal());
			dashboard.setCreatedByUserId(AccountUtil.getCurrentUser().getId());
			dashboard.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			
			List<FacilioField> fields = FieldFactory.getDashboardFields();
			
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
			
			if(DashboardUtil.getDashboard(temp,dashboard.getModuleId()) == null && (!DashboardUtil.RESERVED_DASHBOARD_LINK_NAME.contains(temp))) {
				dashboard.setLinkName(temp);
				break;
			}
			else {
				temp = linkName + i++;
			}
		}
		
	}

}