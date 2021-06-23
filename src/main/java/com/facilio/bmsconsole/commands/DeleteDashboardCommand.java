package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class DeleteDashboardCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long dashboardId = (Long) context.get(FacilioConstants.ContextNames.DASHBOARD_ID);
		if(dashboardId != null) {
			
			GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();
			select.table(ModuleFactory.getScreenDashboardRelModule().getTableName());
			select.select(FieldFactory.getScreenDashboardRelModuleFields());
			select.andCustomWhere("DASHBOARD_ID = ?", dashboardId);
			
			List<Map<String, Object>> props = select.get();
			
			if(props == null || props.isEmpty()) {
				DashboardUtil.deleteDashboard(dashboardId);
			}
			else {
				context.put(FacilioConstants.ContextNames.DASHBOARD_ERROR_MESSAGE, "Dashboard Used in Screen");
			}
		}
		return false;
	}

}
