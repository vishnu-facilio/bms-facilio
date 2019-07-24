package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class EnableMobileDashboardCommand  extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		
		if (dashboard != null && dashboard.isMobileEnabled()) {
			// generate mobile layout only when mobile mode enabled for dashboard
			
			Long dashboardId = dashboard.getId();
			
			List<DashboardWidgetContext> widgets = DashboardUtil.getDashboardWidgetsFormDashboardId(dashboardId);
			if (widgets != null && widgets.size() > 0) {
				
				int mLayoutPosition = 1;
				for (DashboardWidgetContext widget : widgets) {
					
					if (isWidgetMobileSupported(widget)) {
						
						int mLayoutWidth = 12;
						int mLayoutHeight = widget.getLayoutHeight();
						
						if (widget.getLayoutWidth() <= 6) {
							mLayoutWidth = 12;
						}
						else {
							mLayoutWidth = 24;
						}
						
						
						widget.setmLayoutWidth(mLayoutWidth);
						widget.setmLayoutHeight(mLayoutHeight);
						widget.setmLayoutPosition(mLayoutPosition);
					
						GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
								.table(ModuleFactory.getWidgetModule().getTableName())
								.fields(FieldFactory.getWidgetFields())
								.andCustomWhere(ModuleFactory.getWidgetModule().getTableName()+".DASHBOARD_ID = ? AND " + ModuleFactory.getWidgetModule().getTableName()+".ID = ?", dashboardId, widget.getId());
						
						Map<String, Object> updateProps = FieldUtil.getAsProperties(widget);
						
						updateBuilder.update(updateProps);
						
						mLayoutPosition++;
					}
				}
			}
		}
		
		return false;
	}
	
	private boolean isWidgetMobileSupported(DashboardWidgetContext widget) {
		
		if (DashboardWidgetContext.WidgetType.STATIC == widget.getWidgetType()) {
			return true;
		}
		else if (DashboardWidgetContext.WidgetType.CHART == widget.getWidgetType()) {
			WidgetChartContext chart = (WidgetChartContext) widget;
			if (chart != null && chart.getNewReportId() != null && chart.getNewReportId() > 0) {
				return true;
			}
		}
		return false;
	}
}
