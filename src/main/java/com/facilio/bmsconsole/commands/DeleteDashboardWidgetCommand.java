package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DeleteDashboardWidgetCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		if(dashboard != null) {			
			List<DashboardWidgetContext> removableWidgets = DashboardUtil.getDashboardWidgetsFormDashboardId(dashboard.getId());
			
			List<Long> removedWidgetIds = new ArrayList<Long>();
			
			for(DashboardWidgetContext removableWidget :removableWidgets) {
				removedWidgetIds.add(removableWidget.getId());
			}
			
			if(removedWidgetIds.size() > 0) {
				GenericDeleteRecordBuilder genericDeleteRecordBuilder = new GenericDeleteRecordBuilder();
				genericDeleteRecordBuilder.table(ModuleFactory.getWidgetModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(ModuleFactory.getWidgetModule().getTableName()+".ID", "ID", StringUtils.join(removedWidgetIds, ","),StringOperators.IS));
				
				genericDeleteRecordBuilder.delete();
			}
		}
		
		return false;
	}

}
