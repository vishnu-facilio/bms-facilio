package com.facilio.bmsconsole.page.factory;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVProjectWrapper;

public class MVProjectPageFactory {
	
	public static Page getMVProjectPage(MVProjectWrapper project) {
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
		tab2.addSection(tab2Sec1);
		
		addBaselineEquationWidget(tab2Sec1);
		addBaselineEquationListWidget(tab2Sec1);
		
		if (CollectionUtils.isNotEmpty(project.getAdjustments())) {
			addAdjustmentWidget(tab2Sec1, project.getAdjustments());
		}
		
		return page;
	}
	
	private static void addECMWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 2);
		cardWidget.addToWidgetParams("type", CardType.ECM.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addPrimaryDetailsWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.MV_DETAILS_WIDGET);
		pageWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(pageWidget);
	}
	
	private static void addSavingsWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 9, 4);
		cardWidget.addToWidgetParams("type", CardType.MV_SAVINGS.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addEnergyWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 9, 4);
		cardWidget.addToWidgetParams("type", CardType.MV_ENERGY.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addCarbonWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 6, 4);
		cardWidget.addToWidgetParams("type", CardType.CARBON_EMISSION.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addBaselineVsActualWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "baselineVsActual");
		cardWidget.addToLayoutParams(section, 24, 13);
		cardWidget.addToWidgetParams("type", "baselineVsActual");
		section.addWidget(cardWidget);
	}
	
	private static void addCostTrendWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "costTrend");
		cardWidget.addToLayoutParams(section, 24, 13);
		cardWidget.addToWidgetParams("type", "costTrend");
		section.addWidget(cardWidget);
	}
	
	private static void addCumulativeSavingsWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "cumulativeSavings");
		cardWidget.addToLayoutParams(section, 24, 13);
		cardWidget.addToWidgetParams("type", "cumulativeSavings");
		section.addWidget(cardWidget);
	}
	
	private static void addBaselineEquationWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD,  "baselineEquation");
		cardWidget.addToLayoutParams(section, 24, 6);
		cardWidget.addToWidgetParams("type", CardType.BASELINE_EQUATION.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addBaselineEquationListWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.LIST, CardType.BASELINE_EQUATION.getName());
		cardWidget.addToLayoutParams(section, 24, 10);
//		cardWidget.addToWidgetParams("type", CardType.BASELINE_EQUATION.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addAdjustmentWidget(Section section, List<MVAdjustment> adjustments) {
		int evenWidgetWidth = 0;
		for(int i = 0, size = adjustments.size(); i < size; i++) {
			MVAdjustment adjustment = adjustments.get(i);
			PageWidget cardWidget = new PageWidget(WidgetType.CARD);
			
			int width = 7;
			if (i % 2 == 0) {
				if (adjustment.getFormulaField() == null) {
					if ((i+1) == size || adjustments.get(i+1).getFormulaField() == null) {
						width = 5;
					}
				} 
				evenWidgetWidth = width;
			}
			else {
				width = evenWidgetWidth;
			}
			
			cardWidget.addToLayoutParams(section, 12, width);
			cardWidget.addToWidgetParams("type", CardType.MV_ADJUSTMENTS.getName());
			cardWidget.addToWidgetParams("adjustmentIndex", i);
			section.addWidget(cardWidget);
		}
	}

}
