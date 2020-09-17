package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.context.MultiVariateAnomalyAlarm;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.modules.FacilioModule;

public class MultiVariateAnomalyAlarmPageFactory extends PageFactory{
	
    public static Page getMultiVariateAnomalyAlarmPage(MultiVariateAnomalyAlarm alarms, FacilioModule module) throws Exception {
        return getDefaultMultiVariateAnomalyAlarmPage(alarms, module);
    }
    private static Page getDefaultMultiVariateAnomalyAlarmPage(MultiVariateAnomalyAlarm alarms, FacilioModule module) throws Exception {
    
    	Page page = new Page();

		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);
		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);

		addMlAnomalyCreatedTymWidget(tab1Sec1);
		addMlAnomalyDetailsWidget(tab1Sec1);
		addNoOfAnomalies(tab1Sec1);
		addMeanTimeBetweenAnomalyCard(tab1Sec1);
		addMeanTimeToClearCard(tab1Sec1);
		
		Tab tab2 = page.new Tab("occurrenceHistory", "occurrenceHistory");
		page.addTab(tab2);
		
		Section tab2Sec1 = page.new Section();
		tab2.addSection(tab2Sec1);
		
		addOccurrenceHistoryWidget(tab2Sec1);
		
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
		cardWidget.addCardType(CardType.NO_OF_ANOMALIES);
		section.addWidget(cardWidget);
	}
	private static void addMeanTimeBetweenAnomalyCard(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD, "mtba");
		cardWidget.addToLayoutParams(section, 8, 4);
		cardWidget.addCardType(CardType.ML_MTBA);
		section.addWidget(cardWidget);
	}
	private static void addMeanTimeToClearCard(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD, "mttc");
		cardWidget.addToLayoutParams(section, 8, 4);
		cardWidget.addCardType(CardType.ML_MTTC);
		section.addWidget(cardWidget);
	}
	private static void addOccurrenceHistoryWidget(Section section) {
		PageWidget occurrenceListWidget = new PageWidget(WidgetType.OCCURRENCE_HISTORY);
		section.addWidget(occurrenceListWidget);
	}
	
}