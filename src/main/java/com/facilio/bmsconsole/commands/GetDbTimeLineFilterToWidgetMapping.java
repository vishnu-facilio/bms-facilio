package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.cards.util.CardLayout;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.util.ReportUtil;




public class GetDbTimeLineFilterToWidgetMapping extends FacilioCommand {
	
	public static  Map<String,String> TIME_LINE_T_TIME_DATE_FIELD=Collections.singletonMap("dateField", "ttime");
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// generate map of widgetId:FieldID for timeline filters

		DashboardFilterContext dashboardFilterContext = (DashboardFilterContext) context
				.get(FacilioConstants.ContextNames.DASHBOARD_FILTER);

		if (dashboardFilterContext != null && dashboardFilterContext.getIsTimelineFilterEnabled()!=null) {

			DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);

 
			List<DashboardWidgetContext> widgets = dashboard.getDashboardWidgets();


			Map<Long, Map<String,String>> widgetTimeLineFilters=new HashMap<>();

			for (DashboardWidgetContext widget : widgets) {

				//for reports/charts , modular chart  alone timeField differs or is optional , for analytics charts always ttime
				if(widget.getWidgetType() == DashboardWidgetContext.WidgetType.CHART)
				{
					
					long widgetId=widget.getId();
					
					WidgetChartContext chartWidget = (WidgetChartContext) widget;
					chartWidget.getNewReportId();
					ReportContext report=ReportUtil.getReport(chartWidget.getNewReportId(), true);
					
					if(report.getTypeEnum()==ReportType.WORKORDER_REPORT)//modular report
					{
						
						
						if(report.getDateOperator()>-1)
						{							
							 ReportDataPointContext  dataPoint=report.getDataPoints()!=null?report.getDataPoints().get(0):null;
	
							 if(dataPoint!=null)
							 {
								 widgetTimeLineFilters.put(widgetId,Collections.singletonMap("dateField",dataPoint.getDateFieldName()));
							 }
							
						}
					}
					else {//reading,regression,readings with  asset filters(report_template)
						 widgetTimeLineFilters.put(widgetId,TIME_LINE_T_TIME_DATE_FIELD);
						
					}
					
										
				}
				
				
				if (widget.getWidgetType() == DashboardWidgetContext.WidgetType.CARD) {
					
					
					

					WidgetCardContext newCardWidget = (WidgetCardContext) widget;
					
					String cardLayout=newCardWidget.getCardLayout();
					
					if(DashboardFilterUtil.T_TIME_ONLY_CARD_LAYOUTS.contains(cardLayout)) {
						
						widgetTimeLineFilters.put(widget.getId(),TIME_LINE_T_TIME_DATE_FIELD);

					}
					

					 else if (cardLayout.equals(CardLayout.KPICARD_LAYOUT_1.getName())) {
						 
						JSONObject cardParams = newCardWidget.getCardParams();//					
						String kpiType = (String) cardParams.get("kpiType");
						
						if (kpiType.equalsIgnoreCase("module")) {
							//only for kpi with date period , include in timeline filter
							if (cardParams.get("dateRange") != null)								
							{
								JSONObject kpiObj = (JSONObject) cardParams.get("kpi");
								long kpiId = (long) kpiObj.get("kpiId");
								KPIContext kpi = KPIUtil.getKPI(kpiId);
								String dateFieldName = kpi.getDateField().getName();
								widgetTimeLineFilters.put(widget.getId(),Collections.singletonMap("dateField", dateFieldName));
							}
						}
						else if(kpiType.equalsIgnoreCase("reading"))
						{
							widgetTimeLineFilters.put(widget.getId(),TIME_LINE_T_TIME_DATE_FIELD);
						}

					}

				}

			}
			context.put(FacilioConstants.ContextNames.DASHBOARD_WIDGET_TIMELINE_FILTER, widgetTimeLineFilters);
			dashboardFilterContext.setWidgetTimelineFilterMap((widgetTimeLineFilters));
		}

		return false;
	}

}
