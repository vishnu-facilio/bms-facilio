package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;

public class KPIPageFacory extends PageFactory {
	
	public static Page getKpiPage(FormulaFieldContext formulaField) {
		Page page = new Page();
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);
		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		addTimeWidget(tab1Sec1);
		addKpiDetailsWidget(tab1Sec1);
		addMetersWidget(tab1Sec1);
		addViolationsWidget(tab1Sec1);
		if (formulaField.getMatchedResourcesIds().size() == 1) {
			addTargetWidget(tab1Sec1);
		}
		else {
			addLatestValueWidget(tab1Sec1);
		}
		
		
		Tab tab2 = page.new Tab("historicalTrends");
		page.addTab(tab2);
		Section tab2Sec1 = page.new Section();
		tab2.addSection(tab2Sec1);
		
		addTrendsWidget(tab2Sec1);
		addTabularWidget(tab2Sec1);
		
		Tab tab3 = page.new Tab("kpiLog");
		page.addTab(tab3);
		Section tab3Sec1 = page.new Section();
		tab3.addSection(tab3Sec1);
		
		addLogWidget(tab3Sec1);
		
		return page;
		
	}
	
	private static void addKpiDetailsWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.KPI_DETAILS_WIDGET);
		pageWidget.addToLayoutParams(section, 24, 6);
		section.addWidget(pageWidget);
	}
	
	private static void addMetersWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 8, 4);
		cardWidget.addCardType(CardType.KPI_METERS_ASSOCIATED);
		section.addWidget(cardWidget);
	}
	
	private static void addViolationsWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 16, 4);
		cardWidget.addCardType(CardType.KPI_VIOLATIONS);
		section.addWidget(cardWidget);
	}
	
	private static void addTargetWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 10);
		cardWidget.addCardType(CardType.KPI_TARGET);
		section.addWidget(cardWidget);
	}
	
	private static void addLatestValueWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 10);
		cardWidget.addCardType(CardType.KPI_LATEST_VALUE);
		section.addWidget(cardWidget);
	}
	
	private static void addTrendsWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART);
		cardWidget.addToLayoutParams(section, 24, 12);
		cardWidget.addCardType(CardType.KPI_TREND);
		
//		addChartParams(cardWidget, "readingFieldId", null, null);
		
		section.addWidget(cardWidget);
	}
	
	private static void addTabularWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 10);
		cardWidget.addCardType(CardType.KPI_TABULAR);
		section.addWidget(cardWidget);
	}
	
	private static void addLogWidget(Section section) {
		PageWidget widget = new PageWidget(WidgetType.FORMULA_LOG);
		section.addWidget(widget);
	}
	
}
