package com.facilio.bmsconsole.page.factory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.context.NewReadingRuleContext;

import java.util.List;
import java.util.Map;

public class RulePageFactory extends PageFactory {
	public static Page getRulePage(AlarmRuleContext alarmRule) throws Exception {
		return getDefaultRuleSummaryPage(alarmRule);
	}
	public static Page getNewRulePage(AlarmRuleContext alarmRule) throws Exception {
		return getDefaultRuleSummaryPage(alarmRule);
	}
	private static Page getDefaultRuleSummaryPage(AlarmRuleContext alarmRule) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

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

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.READING_EVENT));
		Criteria criteria = new Criteria();
		Long ruleId;
		if(alarmRule.getPreRequsite() == null) { //TODO:SPK - For new reading rule, prerequiste is null, should be change this design
			ruleId = alarmRule.getAlarmTriggerRule().getId();
		} else {
			ruleId = alarmRule.getPreRequsite().getId();
		}
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("rule"), String.valueOf(ruleId), NumberOperators.EQUALS));
//		addImpactDetails(tab2Sec1,criteria);


		// if (alarmRule.alarmTriggerRule.actions.get(0).template.getOriginalTemplate().containsKey("impact")) {
		if (alarmRule.getAlarmTriggerRule() != null && alarmRule.getAlarmTriggerRule().getActions() != null) {
			{
				List<ActionContext> actions = alarmRule.getAlarmTriggerRule().getActions();
				for (ActionContext action: actions) {
					if(action.template!=null) {
						if (action.template.getOriginalTemplate() != null && action.template.getOriginalTemplate().containsKey("impact")) {
							Tab tab5 = page.new Tab("rule_impact");
							page.addTab(tab5);
							Section tab5Sec1 = page.new Section();
							tab5.addSection(tab5Sec1);
							addImpactWidget(tab5Sec1, criteria);
							break;
						}
					}
				}
			}
		}

	
		
		Tab tab4 = page.new Tab("history_log", "history_log");
		page.addTab(tab4);
		
		Section tab4Sec1 = page.new Section();
		tab4.addSection(tab4Sec1);
		
		addHistoryLogWidget(tab4Sec1);

		Tab tab5 = page.new Tab("actions", "actions");
		page.addTab(tab5);

		Section tab5Sec1 = page.new Section();
		tab4.addSection(tab5Sec1);

		addActionsWidget(tab5Sec1);

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
	private static void addImpactWidget(Section section, Criteria criteria) {
		PageWidget alarmDetails = new PageWidget(WidgetType.CHART, "impactDetails");
		alarmDetails.addToLayoutParams(section, 24, 12);
		alarmDetails.addCardType(PageWidget.CardType.IMPACT_DETAILS);
		addChartParams(alarmDetails, null, BmsAggregateOperators.DateAggregateOperator.FULLDATE, "createdTime",null, BmsAggregateOperators.NumberAggregateOperator.SUM,  "cost" ,null, "resource", DateOperators.CURRENT_MONTH,"30" ,criteria);
		section.addWidget(alarmDetails);
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
