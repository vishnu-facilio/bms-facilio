package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;

public class MLAnomalyPageFactory extends PageFactory {

	public static Page getMLAlarmPage(BaseAlarmContext alarms) {
		return getDefaultAnomalySummaryPage(alarms);
	}
	
	private static Page getDefaultAnomalySummaryPage(BaseAlarmContext alarms) {
		Page page = new Page();

		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);
		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		addMlAnomalyCreatedTymWidget(tab1Sec1);
		addMlAnomalyDetailsWidget(tab1Sec1);
		addNoOfAnomalies(tab1Sec1);
		addConspumstionDetails(tab1Sec1);
		addEnergyWastage(tab1Sec1);
		addAnomaliesTrends(tab1Sec1);
		addSubMeterDetails(tab1Sec1);
		
		Tab tab3 = page.new Tab("anomalyRCA", "anomalyRCA");
		page.addTab(tab3);
		
		Section tab3Sec1 = page.new Section();
		tab3.addSection(tab3Sec1);
		
		addAnomalyRCAWidget(tab3Sec1);
		
		Tab tab2 = page.new Tab("occurrenceHistory", "occurrenceHistory");
		page.addTab(tab2);
		
		Section tab2Sec1 = page.new Section();
		tab2.addSection(tab2Sec1);
		
		addOccurrenceHistoryWidget(tab2Sec1);
//		Tab tab2 = page.new Tab("root_cause");
//		page.addTab(tab2);
//		
//		Section tab2Sec1 = page.new Section();
//		tab2.addSection(tab2Sec1);
//		
//		addAnomalyRCAWidget(tab2Sec1);
		

		return page;
	}
	private static void addMlAnomalyCreatedTymWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 2);
		cardWidget.addToWidgetParams("type", CardType.ASSET_LIFE.getName());
		section.addWidget(cardWidget);
	}
	private static void addMlAnomalyDetailsWidget (Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.ANOMALY_DETAILS_WIDGET);
		pageWidget.addToLayoutParams(section, 24, 4);
		section.addWidget(pageWidget);
	}
	private static void addNoOfAnomalies(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD, "noOfAnomalies");
		cardWidget.addToLayoutParams(section, 8, 4);
		cardWidget.addToWidgetParams("type", CardType.NO_OF_ANOMALIES.getName());
		section.addWidget(cardWidget);
	}
	private static void addConspumstionDetails(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD, "energyCDD");
		cardWidget.addToLayoutParams(section, 8, 4);
		cardWidget.addToWidgetParams("type", CardType.ML_ALARM_ENERGY_CDD.getName());
		section.addWidget(cardWidget);
	}
	private static void addEnergyWastage(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD, "energyWastageDetails");
		cardWidget.addToLayoutParams(section, 8, 4);
		cardWidget.addToWidgetParams("type", CardType.ENERGY_WASTAGE_DETAILS.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addAnomaliesTrends(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.ANOMALIES_TREND_WIDGET);
		pageWidget.addToLayoutParams(section, 24, 11);
		section.addWidget(pageWidget);
		
	}
	private static void addSubMeterDetails(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.ANOMALY_SUB_METER_WIDGET);
		pageWidget.addToLayoutParams(section, 24, 9);
		section.addWidget(pageWidget);
		
	}
	private static void addAnomalyRCAWidget(Section section) {
		PageWidget rcaWidget = new PageWidget(WidgetType.ANOMALY_RCA);
		section.addWidget(rcaWidget);
	}
	private static void addOccurrenceHistoryWidget(Section section) {
		PageWidget occurrenceListWidget = new PageWidget(WidgetType.OCCURRENCE_HISTORY);
		section.addWidget(occurrenceListWidget);
	}
}
