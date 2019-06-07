package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.constants.FacilioConstants;

public class AssetPageFactory extends PageFactory {
	
	public static Page getAssetPage(AssetContext asset) {
		// TODO do the check based on status or category
		return getConnectedAssetPage();
	}

	private static Page getConnectedAssetPage() {
		Page page = new Page();

		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		addPrimaryDetailsWidget(tab1Sec1);
		addWoCountWidget(tab1Sec1);
		addAlarmCountWidget(tab1Sec1);
		addFailureMetricWidget(tab1Sec1);
		addSecondaryDetailsWidget(tab1Sec1);
		addCommonSubModuleGroup(tab1Sec1);

		
		Tab tab2 = page.new Tab("maintenance");
		page.addTab(tab2);
		
		Section tab2Sec1 = page.new Section();
		tab2.addSection(tab2Sec1);
		
		addNextPmWidget(tab2Sec1);
		addWoDetailsWidget(tab2Sec1);
		addRecentlyClosedWidget(tab2Sec1);
		
		Section tab2Sec2 = page.new Section("plannedWorkorder");
		tab2.addSection(tab2Sec2);
		addPlannedWoWidget(tab2Sec2);
		
		Section tab2Sec3 = page.new Section("unplannedWorkorder");
		tab2.addSection(tab2Sec3);
		addUnPlannedWoWidget(tab2Sec3);


		Tab tab3 = page.new Tab("readings");
		page.addTab(tab3);
		
		Section tab3Sec1 = page.new Section();
		tab3.addSection(tab3Sec1);
		
		addReadingWidget(tab3Sec1);
		
		Section tab3Sec2 = page.new Section("commands");
		tab3.addSection(tab3Sec2);
		
		addCommandWidget(tab3Sec2);
		
		
		Tab tab4 = page.new Tab("performance");
		page.addTab(tab4);
		
		Section tab4Sec1 = page.new Section();
		tab4.addSection(tab4Sec1);
		
		addAssetLifeWidget(tab4Sec1);
		addAlarmInsightsWidget(tab4Sec1);
		addLastDownTimeWidget(tab4Sec1);
		addOverallDowntimeWidget(tab4Sec1);
		addFailureRateWidget(tab4Sec1);
		addAvgTtrWidget(tab4Sec1);
		
		
		Tab tab5 = page.new Tab("history");
		page.addTab(tab5);
		
		Section tab5Sec1 = page.new Section();
		tab5.addSection(tab5Sec1);
		
		addHistoryWidget(tab5Sec1);

		return page;
	}
	
	private static void addPrimaryDetailsWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.PRIMARY_DETAILS_WIDGET);
		pageWidget.addToLayoutParams(section, 24, 7);
		pageWidget.addToWidgetParams("showOperatingHours", true);
		section.addWidget(pageWidget);
	}
	
	private static void addWoCountWidget(Section section) {
		PageWidget workorderWidget = getCountModuleWidget(FacilioConstants.ContextNames.WORK_ORDER);
		workorderWidget.addToLayoutParams(section, 7, 8);
		section.addWidget(workorderWidget);
	}
	
	private static void addAlarmCountWidget(Section section) {
		PageWidget alarmWidget = getCountModuleWidget(FacilioConstants.ContextNames.ALARM);
		alarmWidget.addToLayoutParams(section, 7, 8);
		section.addWidget(alarmWidget);
	}
	
	private static void addFailureMetricWidget(Section section) {
		PageWidget fddWidget = new PageWidget(WidgetType.CARD);
		fddWidget.addToLayoutParams(section, 10, 8);
		fddWidget.addToWidgetParams("type", CardType.FAILURE_METRICS.getName());
		section.addWidget(fddWidget);
	}
	
	private static void addSecondaryDetailsWidget(Section section) {
		// col1Sec2.setName("overview");
		// col1Sec2.setDisplayName("common.page.overview");
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(detailsWidget);
	}
	
	private static void addNextPmWidget(Section section) {
		PageWidget nextPmWidget = new PageWidget(WidgetType.CARD);
		nextPmWidget.addToLayoutParams(section, 8, 9);
		nextPmWidget.addToWidgetParams("type", CardType.NEXT_PM.getName());
		section.addWidget(nextPmWidget);
	}
	
	private static void addWoDetailsWidget(Section section) {
		PageWidget woDetailsWidget = new PageWidget(WidgetType.CARD);
		woDetailsWidget.addToLayoutParams(section, 8, 9);
		woDetailsWidget.addToWidgetParams("type", CardType.WO_DETAILS.getName());
		section.addWidget(woDetailsWidget);
	}
	
	private static void addRecentlyClosedWidget(Section section) {
		PageWidget recentlyClosedWidget = new PageWidget(WidgetType.CARD);
		recentlyClosedWidget.addToLayoutParams(section, 8, 9);
		recentlyClosedWidget.addToWidgetParams("type", CardType.RECENTLY_CLOSED_PM.getName());
		section.addWidget(recentlyClosedWidget);
	}
	
	private static void addPlannedWoWidget(Section section) {
		PageWidget plannedWidget = new PageWidget(WidgetType.LIST, "plannedWorkorder");
		plannedWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(plannedWidget);
	}
	
	private static void addUnPlannedWoWidget(Section section) {
		PageWidget unPlannedWidget = new PageWidget(WidgetType.LIST, "unPlannedWorkorder");
		unPlannedWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(unPlannedWidget);
	}
	
	private static void addReadingWidget(Section section) {
		PageWidget readingsWidget = new PageWidget(WidgetType.LIST, "readings");
		readingsWidget.addToLayoutParams(section, 24, 10);
		section.addWidget(readingsWidget);
	}
	
	private static void addCommandWidget(Section section) {
		PageWidget commandWidget = new PageWidget(WidgetType.CARD);
		commandWidget.addToLayoutParams(section, 24, 24);
		commandWidget.addToWidgetParams("type", CardType.SET_COMMAND.getName());
		section.addWidget(commandWidget);
	}
	
	private static void addAssetLifeWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 2);
		cardWidget.addToWidgetParams("type", CardType.ASSET_LIFE.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addAlarmInsightsWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 10);
		cardWidget.addToWidgetParams("type", CardType.ALARM_INSIGHTS.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addLastDownTimeWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD, "lastReportedDownTime");
		cardWidget.addToLayoutParams(section, 12, 5);
		cardWidget.addToWidgetParams("type", CardType.LAST_DOWNTIME.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addOverallDowntimeWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD, "overallDownTime");
		cardWidget.addToLayoutParams(section, 12, 5);
		cardWidget.addToWidgetParams("type", CardType.OVERALL_DOWNTIME.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addFailureRateWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART);
		cardWidget.addToLayoutParams(section, 12, 11);
		cardWidget.addToWidgetParams("type", CardType.FAILURE_RATE.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addAvgTtrWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART);
		cardWidget.addToLayoutParams(section, 12, 11);
		cardWidget.addToWidgetParams("type", CardType.AVG_TTR.getName());
		section.addWidget(cardWidget);
	}
	
	
}
