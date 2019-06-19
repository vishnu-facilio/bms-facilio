package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.mv.context.MVProject;

public class MVProjectPageFactory {
	
	public static Page getMVProjectPage(MVProject project) {
		Page page = new Page();
		
		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);
		
		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		
		addECMWidget(tab1Sec1);
		addPrimaryDetailsWidget(tab1Sec1);
		addSavingsWidget(tab1Sec1);
		addEnergyWidget(tab1Sec1);
		addCarbonWidget(tab1Sec1);
		addBaselineVsActualWidget(tab1Sec1);
		addCostTrendWidget(tab1Sec1);
		addCumulativeSavingsWidget(tab1Sec1);
		
		Tab tab2 = page.new Tab("metrics");
		page.addTab(tab2);
		
		Section tab2Sec1 = page.new Section();
		tab1.addSection(tab2Sec1);
		
		addBaselineEquationWidget(tab2Sec1, false);
		addBaselineEquationWidget(tab2Sec1, true);
		addBaselineEquationListWidget(tab2Sec1);
		addStaticFactorTrendWidget(tab2Sec1);
		addAdjustmentWidget(tab2Sec1);
		
		
		return page;
	}
	
	private static void addECMWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 2);
		cardWidget.addToWidgetParams("type", CardType.ECM.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addPrimaryDetailsWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.PRIMARY_DETAILS_WIDGET);
		pageWidget.addToLayoutParams(section, 24, 6);
		section.addWidget(pageWidget);
	}
	
	private static void addSavingsWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 10, 5);
		cardWidget.addToWidgetParams("type", CardType.MV_SAVINGS.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addEnergyWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 10, 5);
		cardWidget.addToWidgetParams("type", CardType.MV_ENERGY.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addCarbonWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 10, 5);
		cardWidget.addToWidgetParams("type", CardType.CARBON_EMISSION.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addBaselineVsActualWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "baselineVsActual");
		cardWidget.addToLayoutParams(section, 24, 14);
		section.addWidget(cardWidget);
	}
	
	private static void addCostTrendWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "costTrend");
		cardWidget.addToLayoutParams(section, 24, 14);
		section.addWidget(cardWidget);
	}
	
	private static void addCumulativeSavingsWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "cumulativeSavings");
		cardWidget.addToLayoutParams(section, 24, 14);
		section.addWidget(cardWidget);
	}
	
	private static void addBaselineEquationWidget(Section section, boolean isPostRetrofit) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD, isPostRetrofit ? "baselineEquation" : "postRetrofitEquation");
		cardWidget.addToLayoutParams(section, 12, 5);
		cardWidget.addToWidgetParams("type", CardType.BASELINE_EQUATION.getName());
		cardWidget.addToWidgetParams("isPostRetrofit", isPostRetrofit);
		section.addWidget(cardWidget);
	}
	
	private static void addBaselineEquationListWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.LIST);
		cardWidget.addToLayoutParams(section, 24, 10);
		cardWidget.addToWidgetParams("type", CardType.BASELINE_EQUATION.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addStaticFactorTrendWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART);
		cardWidget.addToLayoutParams(section, 24, 14);
		cardWidget.addToWidgetParams("type", CardType.FAILURE_RATE.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addAdjustmentWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 12, 5);
		cardWidget.addToWidgetParams("type", CardType.MV_ADJUSTMENTS.getName());
		section.addWidget(cardWidget);
	}

}
