package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;

public class RulePageFactory extends PageFactory {
	public static Page getRulePage(AlarmRuleContext alarmRule) {
		return getDefaultRuleSummaryPage(alarmRule);
	}
	
	private static Page getDefaultRuleSummaryPage(AlarmRuleContext alarmRule) {
		Page page = new Page();

		Tab tab1 = page.new Tab("summary");
		page.addTab(tab1);

		Section tab1Sec1 = page.new Section();
		tab1.addSection(tab1Sec1);
		addRuleRankCard(tab1Sec1);
		addRuleDetailsWidget(tab1Sec1);

		Tab tab2 = page.new Tab("rule_insight");
		page.addTab(tab2);
		
		Section tab2Sec1 = page.new Section();
		tab2.addSection(tab2Sec1);
		
		Tab tab3 = page.new Tab("root_cause_impact");


		return page;
	}
	private static void addRuleDetailsWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.RULE_DETIALS_WIGET);
		pageWidget.addToLayoutParams(section, 24, 6);
		section.addWidget(pageWidget);
	}
	private static void addRuleRankCard(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 2);
		cardWidget.addToWidgetParams("type", CardType.RANK_RULE.getName());
		section.addWidget(cardWidget);
	}
	
	

}
