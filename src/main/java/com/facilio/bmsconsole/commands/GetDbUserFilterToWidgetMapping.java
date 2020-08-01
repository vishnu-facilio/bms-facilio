package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.context.DashboardUserFilterContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext.WidgetType;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.cards.util.CardLayout;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.util.ReportUtil;

public class GetDbUserFilterToWidgetMapping extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
			Map<Long, List<Long>> userFilterToWidgetListMap=new HashMap<>();
			
		DashboardFilterContext dbFilter=(DashboardFilterContext) context.get(FacilioConstants.ContextNames.DASHBOARD_FILTER);
		if(dbFilter!=null&&dbFilter.getDashboardUserFilters()!=null) {
			
			List<DashboardUserFilterContext> userFilters=dbFilter.getDashboardUserFilters(); 
			//for each  filter , check which widgets have the same module as the filter.
			 for (DashboardUserFilterContext filter : userFilters) {
				 List<Long> widgetIdList=new ArrayList<Long>();
				 
				
				 
				 DashboardContext dashboard=(DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
				 
				 DashboardTabContext dashboardTab=(DashboardTabContext) context.get(FacilioConstants.ContextNames.DASHBOARD_TAB);
				 
				 List<DashboardWidgetContext> widgets=new ArrayList<>();
				 if(dashboard!=null)
				 {
					 	widgets=dashboard.getDashboardWidgets();
				 }
				 else if(dashboardTab!=null)
				 {
					 widgets=dashboardTab.getDashboardWidgets();
				 }
				 else {
					 throw new Exception("NO dashboard or dashboard tab");
				 }
				 
				 
				 if(widgets!=null)
				 {
					 
					 for (DashboardWidgetContext widget : widgets) {
					
						 long moduleId=getModuleIdFromWidget(widget);
						 	if(moduleId==filter.getField().getModuleId())
						 	{
						 		widgetIdList.add(moduleId);
						 		
						 	}
					}
				 }
				 
				 
				 userFilterToWidgetListMap.put(filter.getId(),widgetIdList);
				 dbFilter.setFilterWidgetMapping(userFilterToWidgetListMap);
			}
			
			 
			
			
		}

		return false;
	}

	public static long getModuleIdFromWidget(DashboardWidgetContext widget) throws Exception
	{
		//only modular reports and Module KPI cards and lists  have modules associated with them 
		long moduleId=-1L;
		
		 if(widget.getWidgetType().equals(WidgetType.CHART))
		 {
			 WidgetChartContext widgetChart=(WidgetChartContext)widget;
			 
			 
			 ReportContext report=ReportUtil.getReport(widgetChart.getNewReportId(), true);
			 
			 				 
			 if(report.getTypeEnum()==ReportType.WORKORDER_REPORT)
			 {				 
				 moduleId=report.getModuleId();
			 }
			 				 				 
		 }
		 else if(widget.getWidgetType().equals(WidgetType.CARD))
		 {
			 WidgetCardContext newCardWidget = (WidgetCardContext) widget;
			 if (newCardWidget.getCardLayout().equals(CardLayout.KPICARD_LAYOUT_1.getName())) {
					JSONObject cardParams = newCardWidget.getCardParams();
//				
					String kpiType = (String) cardParams.get("kpiType");
					if (kpiType.equalsIgnoreCase("module")) {

						
							JSONObject kpiObj = (JSONObject) cardParams.get("kpi");
							long kpiId = (long) kpiObj.get("kpiId");
							KPIContext kpi = KPIUtil.getKPI(kpiId);
							moduleId=kpi.getModuleId();
						
					}

				}
		 }
		 
		 
		 return moduleId;
		 
	}
}
