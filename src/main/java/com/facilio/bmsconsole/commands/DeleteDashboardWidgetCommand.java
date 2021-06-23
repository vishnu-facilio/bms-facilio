package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.ModuleFactory;

public class DeleteDashboardWidgetCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		if(dashboard != null) {			
			List<DashboardWidgetContext> removableWidgets = DashboardUtil.getDashboardWidgetsFormDashboardIdOrTabId(dashboard.getId(),null);
			
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
