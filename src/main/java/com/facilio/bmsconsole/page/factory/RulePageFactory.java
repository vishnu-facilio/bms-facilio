package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
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
		addAssetsNAlarmDetails(tab1Sec1);
		addAlarmInsight(tab1Sec1);
		addAssociatedWOCount(tab1Sec1);
		addAssociatedWO(tab1Sec1);
		

		Tab tab2 = page.new Tab("rule_insight");
		page.addTab(tab2);
		
		Section tab2Sec1 = page.new Section();
		tab2.addSection(tab2Sec1);
		
		addRuleInsight(tab2Sec1);
		
		
		Tab tab3 = page.new Tab("root_cause_impact", "root_cause_impact");
		page.addTab(tab3);
		
		Section tab3Sec1 = page.new Section();
		tab3.addSection(tab3Sec1);
		
		addRCAWidget(tab3Sec1);
	
		
		Tab tab4 = page.new Tab("history_log", "history_log");
		page.addTab(tab4);
		
		Section tab4Sec1 = page.new Section();
		tab4.addSection(tab4Sec1);
		
		addHistoryLogWidget(tab4Sec1);

//		Tab tab5 = page.new Tab("actions", "actions");
//		page.addTab(tab5);
//
//		Section tab5Sec1 = page.new Section();
//		tab4.addSection(tab5Sec1);
//
//		addActionsWidget(tab5Sec1);

		return page;
	}
	private static void addRuleDetailsWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.RULE_DETIALS_WIGET);
		pageWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(pageWidget);
	}
	private static void addRuleRankCard(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 2);
		cardWidget.addCardType(CardType.RANK_RULE);
		section.addWidget(cardWidget);
	}
	private static void addAssetsNAlarmDetails(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 8, 7);
		cardWidget.addCardType(CardType.RULE_ASSETS_ALARM);
		section.addWidget(cardWidget);
	}
	
	private static void addAlarmInsight(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 16, 7);
		cardWidget.addCardType(CardType.RULE_ALARM_INSIGHT);
		section.addWidget(cardWidget);
	}
	
	private static void addAssociatedWOCount(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 12, 5);
		cardWidget.addCardType(CardType.RULE_ASSOCIATED_WO);
		section.addWidget(cardWidget);
	}
	
	private static void addAssociatedWO(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 12, 5);
		cardWidget.addCardType(CardType.RULE_WO_DURATION);
		section.addWidget(cardWidget);
	}
	
	private static void addRuleInsight(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 11);
		cardWidget.addCardType(CardType.ALARM_INSIGHTS);
		section.addWidget(cardWidget);
	}
	
	private static void addRCAWidget(Section section) {
		PageWidget rcaWidget = new PageWidget(WidgetType.RULE_RCA);
		section.addWidget(rcaWidget);
	}
	private static void addHistoryLogWidget(Section section) {
		PageWidget historyLogWidget = new PageWidget(WidgetType.HISTORY_LOG);
		section.addWidget(historyLogWidget);
	}

	private static void addActionsWidget(Section section) {
		PageWidget actionWidget = new PageWidget(WidgetType.RULE_ACTION_WIDGET);
		section.addWidget(actionWidget);
	}
	

}
