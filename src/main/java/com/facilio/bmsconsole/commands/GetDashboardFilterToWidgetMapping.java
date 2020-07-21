package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.cards.util.CardLayout;
import com.facilio.constants.FacilioConstants;

public class GetDashboardFilterToWidgetMapping extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// generate map of widgetId:FieldID for timeline filters

		DashboardFilterContext dashboardFilterContext = (DashboardFilterContext) context
				.get(FacilioConstants.ContextNames.DASHBOARD_FILTER);

		if (dashboardFilterContext != null && dashboardFilterContext.getIsTimelineFilterEnabled()) {

			DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);

			// handle only timeline filter for cards now
			List<DashboardWidgetContext> widgets = dashboard.getDashboardWidgets();

			JSONObject widgetTimeLineFilters = new JSONObject();

			for (DashboardWidgetContext widget : widgets) {

				//
				if (widget.getWidgetType() == DashboardWidgetContext.WidgetType.CARD) {

					WidgetCardContext newCardWidget = (WidgetCardContext) widget;
					System.out.println("card type is " + newCardWidget.getCardLayout());

					if (newCardWidget.getCardLayout().equals(CardLayout.READINGCARD_LAYOUT_1.getName())) {
						widgetTimeLineFilters.put(widget.getId(), "ttime");
					} else if (newCardWidget.getCardLayout().equals(CardLayout.ENERGYCOST_LAYOUT_1.getName())) {
						widgetTimeLineFilters.put(widget.getId(), "ttime");
					} else if (newCardWidget.getCardLayout().equals(CardLayout.KPICARD_LAYOUT_1.getName())) {
						JSONObject cardParams = newCardWidget.getCardParams();
//					
						String kpiType = (String) cardParams.get("kpiType");
						if (kpiType.equalsIgnoreCase("module")) {

							if (cardParams.get("dateRange") != null)								
							{
								JSONObject kpiObj = (JSONObject) cardParams.get("kpi");
								long kpiId = (long) kpiObj.get("kpiId");
								KPIContext kpi = KPIUtil.getKPI(kpiId);
								String dateFieldName = kpi.getDateField().getName();
								widgetTimeLineFilters.put(widget.getId(), dateFieldName);
							}
						}

					}

				}

			}
			context.put(FacilioConstants.ContextNames.DASHBOARD_WIDGET_TIMELINE_FILTER, widgetTimeLineFilters);
			dashboardFilterContext.setWidgetTimeLineFilters(widgetTimeLineFilters);
		}

		return false;
	}

}
