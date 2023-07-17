package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportPivotParamsContext;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.bmsconsole.context.WidgetCardContext.ScriptMode;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.cards.util.CardLayout;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.util.ReportUtil;
import org.json.simple.parser.JSONParser;

public class GetDbTimeLineFilterToWidgetMapping extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(GetDbTimeLineFilterToWidgetMapping.class.getName());

	public static Map<String, String> TIME_LINE_T_TIME_DATE_FIELD = Collections.singletonMap("dateField", "ttime");

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// generate map of widgetId:FieldID for timeline filters

		DashboardFilterContext dashboardFilterContext = (DashboardFilterContext) context
				.get(FacilioConstants.ContextNames.DASHBOARD_FILTER);

		if (dashboardFilterContext != null && dashboardFilterContext.getIsTimelineFilterEnabled() != null &&dashboardFilterContext.getIsTimelineFilterEnabled() == true) {

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



			Map<Long, Map<String, String>> widgetTimeLineFilters = new HashMap<>();

			for (DashboardWidgetContext widget : widgets) {
				try {
					Map<String, String> dateField;
					dateField=getDateFieldFromWidget(widget);
					if(dateField!=null)
					{
						widgetTimeLineFilters.put(widget.getId(), dateField);
					}
				}
				catch(Exception e)
				{
					LOGGER.log(Level.SEVERE, "Error occured trying to find date field in widget ,ID="+widget.getId(),e);
				}

				
			}
			context.put(FacilioConstants.ContextNames.DASHBOARD_WIDGET_TIMELINE_FILTER, widgetTimeLineFilters);
			dashboardFilterContext.setWidgetTimelineFilterMap((widgetTimeLineFilters));
		}

		return false;
	}

	  private Map<String, String> getDateFieldFromWidget(DashboardWidgetContext widget) throws Exception
	{
		  Map<String, String> dateField=null;
		  
		  JSONObject widgetSettings = widget.getWidgetSettings();
			boolean isFilterExclude = widgetSettings != null && widgetSettings.containsKey("excludeDbFilters") ? (boolean) widgetSettings.get("excludeDbFilters") : false;
			if (!isFilterExclude) {

				// for reports/charts , modular chart alone timeField differs or is optional ,
				// for analytics charts always ttime
				if (widget.getWidgetType() == DashboardWidgetContext.WidgetType.CHART) {


					WidgetChartContext chartWidget = (WidgetChartContext) widget;
					chartWidget.getNewReportId();
					ReportContext report = ReportUtil.getReport(chartWidget.getNewReportId(), true);

					if(report.getTypeEnum() == ReportType.PIVOT_REPORT){
						JSONParser parser = new JSONParser();
						ReportPivotParamsContext pivotparams = FieldUtil.getAsBeanFromJson(
								(JSONObject) parser.parse(report.getTabularState()), ReportPivotParamsContext.class);
						if(pivotparams.getDateOperator() > 0 && pivotparams.getDateFieldId() > 0)
						{
							ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
							FacilioField field = modBean.getField(pivotparams.getDateFieldId());
							if(field != null){
								dateField=new HashMap<>(Collections.singletonMap("dateField", field.getName()));
							}
						}
					}
					else if (report.getTypeEnum() == ReportType.WORKORDER_REPORT)// modular report
					{

						if (report.getDateOperator() > -1) {
							ReportDataPointContext dataPoint = report.getDataPoints() != null
									? report.getDataPoints().get(0)
									: null;

							if (dataPoint != null) {
								dateField=new HashMap<>(
										Collections.singletonMap("dateField", dataPoint.getDateFieldName()));
							}

						}
					} else {// reading,regression,readings with asset filters(report_template)
						dateField=new HashMap<>( TIME_LINE_T_TIME_DATE_FIELD);

					}

				}

				else if (widget.getWidgetType() == DashboardWidgetContext.WidgetType.CARD) {

					WidgetCardContext newCardWidget = (WidgetCardContext) widget;

					String cardLayout = newCardWidget.getCardLayout();
					
					if(newCardWidget.getScriptMode()==ScriptMode.CUSTOM_SCRIPT)
					{
						//cannot identify datefield for custom script cards, but still include widget in timeline map
						dateField=new HashMap<>( Collections.singletonMap("dateField",null));

					}	
					else if (newCardWidget.getCardLayout().equals(CardLayout.WEB_LAYOUT_1.getName()))
					{
						JSONObject cardParams = newCardWidget.getCardParams();
						if (cardParams != null) {
							String type = (String) cardParams.get("type");

							if ("conncetdapp".equals(type) || "connectedapp".equals(type)) {
								//cannot identify datefield for connected app cards, but still include widget in timeline map
								dateField=new HashMap<>( Collections.singletonMap("dateField",null));
							}
						}
					}
					else if (DashboardFilterUtil.T_TIME_ONLY_CARD_LAYOUTS.contains(cardLayout)) {

						dateField=new HashMap<>( TIME_LINE_T_TIME_DATE_FIELD);

					}

					else if (cardLayout.equals(CardLayout.KPICARD_LAYOUT_1.getName()) || cardLayout.equals(CardLayout.KPICARD_LAYOUT_2.getName())) {

						JSONObject cardParams = newCardWidget.getCardParams();//
						String kpiType = (String) cardParams.get("kpiType");

						if (kpiType.equalsIgnoreCase("module")) {
							// only for kpi with date period , include in timeline filter
							if (cardParams.get("dateRange") != null) {
								JSONObject kpiObj = (JSONObject) cardParams.get("kpi");
								long kpiId = (long) kpiObj.get("kpiId");
								KPIContext kpi = KPIUtil.getKPI(kpiId,false);
								if(kpi != null && kpi.getDateField() != null) {
									String dateFieldName = kpi.getDateField().getName();
									dateField = new HashMap<>(
											Collections.singletonMap("dateField", dateFieldName));
								}
							}
						} else if (kpiType.equalsIgnoreCase("reading")) {
							dateField=new HashMap<>( TIME_LINE_T_TIME_DATE_FIELD);
						}

					}

				}
				else if (widget.getWidgetType() == DashboardWidgetContext.WidgetType.LIST_VIEW)
				{
					String dateFieldName = (String) widgetSettings.get("dateField");
					if(dateFieldName!=null)
					{
						dateField=new HashMap<>(
								Collections.singletonMap("dateField", dateFieldName));
					}
				}
			}
		  
		  
		return dateField;
	}
}
