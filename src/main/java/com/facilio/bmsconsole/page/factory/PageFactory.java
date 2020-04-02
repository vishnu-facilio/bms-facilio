
package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.page.WidgetGroup.WidgetGroupType;
import com.facilio.bmsconsole.templates.DefaultTemplate;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.mv.context.MVProjectWrapper;
import org.json.simple.JSONObject;

import java.util.List;

public class PageFactory {


	public static Page getPage(FacilioModule module, Object record, boolean isApproval) throws Exception {
		if (isApproval) {
			return ApprovalPageFactory.getApprovalPage(module, (ModuleBaseWithCustomFields) record);
		}
		
		switch(module.getName()) {
			case ContextNames.ASSET:
				return AssetPageFactory.getAssetPage((AssetContext) record);
			case ContextNames.TENANT:
				return TenantPageFactory.getTenantPage((ModuleBaseWithCustomFields) record);
			case ContextNames.READING_RULE_MODULE:
				return RulePageFactory.getRulePage((AlarmRuleContext) record);
			case ContextNames.MV_PROJECT_MODULE:
				return MVProjectPageFactory.getMVProjectPage((MVProjectWrapper) record);
			case ContextNames.ML_ANOMALY_ALARM:
				return MLAnomalyPageFactory.getMLAlarmPage((BaseAlarmContext) record);
			case ContextNames.FORMULA_FIELD:
				return KPIPageFacory.getKpiPage((FormulaFieldContext) record);
			case ContextNames.READING_ALARM:
			case ContextNames.NEW_READING_ALARM:
				return  ReadingAlarmPageFactory.getReadingAlarmPage((ReadingAlarm) record);
			case ContextNames.AGENT_ALARM:
				return AgentAlarmPageFactory.getAgentAlarmPage((BaseAlarmContext) record);
			case ContextNames.WORKPERMIT:
				return WorkpermitPageFactory.getWorkPermitPage((WorkPermitContext) record);
			case ContextNames.VENDORS:
				return VendorPageFactory.getVendorPage((VendorContext) record);
			case ContextNames.WORK_ORDER:
				return TenantWorkRequestPageFactory.getWorkorderPage((WorkOrderContext) record);
			case ContextNames.VISITOR_LOGGING:
				return VisitorLoggingPageFactory.getVisitorLoggingPage((VisitorLoggingContext) record);
			case ContextNames.INSURANCE:
				return InsurancePageFactory.getInsurancePage((InsuranceContext) record);
			case ContextNames.SAFETY_PLAN:
				return SafetyPlanPageFactory.getSafetyPlanPage((SafetyPlanContext) record);
			case ContextNames.HAZARD:
				return SafetyPlanPageFactory.getHazardPage((HazardContext) record);
			case ContextNames.PRECAUTION:
				return SafetyPlanPageFactory.getPrecautionPage((PrecautionContext) record);
			case ContextNames.SITE:
				return SpaceManagementPageFactory.getSitePage((SiteContext) record);
			case ContextNames.CLIENT:
				return ClientPageFactory.getclientPage((ClientContext) record);
			case ContextNames.BUILDING:
				return SpaceManagementPageFactory.getBuildingPage((BuildingContext) record);
			case ContextNames.FLOOR:
				return SpaceManagementPageFactory.getFloorPage((FloorContext) record);
			case ContextNames.SPACE:
				return SpaceManagementPageFactory.getSpacePage((SpaceContext) record);
			case ContextNames.READING_TEMPLATE_MODULE:
				return TemplatePageFactory.getTemplatePage((DefaultTemplate) record);
			case ContextNames.TENANT_UNIT_SPACE:
				return TenantUnitSpacePageFactory.getTenantUnitSpacePage((TenantUnitSpaceContext) record);
				
		}
		if (module.getExtendModule() == null) {	// temp
			return CustomModulePageFactory.getCustomModulePage((ModuleBaseWithCustomFields) record);
		}
		return null;
	}

	protected static PageWidget addCommonSubModuleGroup(Section section) {

		PageWidget subModuleGroup = new PageWidget(WidgetType.GROUP);
		subModuleGroup.addToLayoutParams(section, 24, 8);
		subModuleGroup.addToWidgetParams("type", WidgetGroupType.TAB);
		section.addWidget(subModuleGroup);
		
		PageWidget notesWidget = new PageWidget();
		notesWidget.setWidgetType(WidgetType.COMMENT);
		subModuleGroup.addToWidget(notesWidget);
		
		PageWidget attachmentWidget = new PageWidget();
		attachmentWidget.setWidgetType(WidgetType.ATTACHMENT);
		subModuleGroup.addToWidget(attachmentWidget);

		return subModuleGroup;
	}
	
	protected static void addAssetReadingChart(Section section, long reportId, String title) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "assetReadingWidget");
		cardWidget.addToLayoutParams(section, 24, 14);
		cardWidget.setTitle(title);
		cardWidget.addToWidgetParams("reportId", reportId);
		cardWidget.addCardType(CardType.ASSET_READING_WIDGET);
		section.addWidget(cardWidget);
	}
	
	
	protected static void addHistoryWidget(Section section) {
		PageWidget historyWidget = new PageWidget(WidgetType.HISTORY);
		historyWidget.addToLayoutParams(section, 24, 3);
		section.addWidget(historyWidget);
	}

	protected static PageWidget getCountModuleWidget(String module) {
		PageWidget widget = new PageWidget();
		widget.setWidgetType(WidgetType.COUNT);
		widget.addToWidgetParams("module", module);
		return widget;
	}

	private static PageWidget getListModuleWidget(String module) {
		PageWidget widget = new PageWidget();
		widget.setWidgetType(WidgetType.LIST);
		widget.addToWidgetParams("module", module);
		return widget;
	}


	private static void addAssetPerformanceWidgets(Section section) {
		PageWidget cardWidget = new PageWidget();
		cardWidget.setWidgetType(WidgetType.CARD);

		cardWidget.addToLayoutParams(section, 8, 2);

		addReadingCard(cardWidget, "runHours", "chillerreading");

		section.addWidget(cardWidget);
	}

	private static void addReadingCard(PageWidget cardWidget, String fieldName, String moduleName) {
		addReadingCard(cardWidget, fieldName, moduleName, 3, 22, 20);
	}
	
	@SuppressWarnings("unchecked")
	protected static void addTimeWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 2);
		cardWidget.addCardType(CardType.TIME);
		section.addWidget(cardWidget);
	}
	
	protected static void addDetailsWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.DETAILS_WIDGET);
		pageWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(pageWidget);
	}
	
	protected static void addRelatedCountWidget(Section section, int yPos, List<String> modules) {
		PageWidget cardWidget = new PageWidget(WidgetType.RELATED_COUNT);
		cardWidget.addToLayoutParams(0, yPos, 16, 4);
		section.addWidget(cardWidget);
	}
	
	protected static void addSecondaryDetailWidget(Section section) {
		// col1Sec2.setName("overview");
		// col1Sec2.setDisplayName("common.page.overview");
		PageWidget detailsWidget = new PageWidget(WidgetType.SECONDARY_DETAILS_WIDGET);
		detailsWidget.addToLayoutParams(section, 24, 5);
		section.addWidget(detailsWidget);
	}
	
	private static void addReadingCard(PageWidget cardWidget, String fieldName, String moduleName, long aggregateFunc, long dateOperator, long xAggr) {
		cardWidget.addToWidgetParams("staticKey", "readingWithGraphCard");
		JSONObject paramsJson = new JSONObject();
		paramsJson.put("aggregateFunc", aggregateFunc);
		paramsJson.put("dateOperator", dateOperator);
		paramsJson.put("fieldName", fieldName);
		paramsJson.put("moduleName", moduleName);
		paramsJson.put("xAggr", xAggr);
		cardWidget.addToWidgetParams("paramsJson", paramsJson);
	}
	
	protected static void addChartParams(PageWidget widget, String xFieldName, String yFieldName, Criteria criteria) {
		addChartParams(widget, xFieldName, yFieldName, null, criteria);
	}
	
	protected static void addChartParams(String groupByFieldName,PageWidget widget, String xFieldName, String yFieldName , Criteria criteria) {
		addChartParams(widget, "line", DateAggregateOperator.MONTHANDYEAR, xFieldName, NumberAggregateOperator.SUM, yFieldName, groupByFieldName , DateOperators.CURRENT_YEAR, null, criteria);
	}
	
	protected static void addChartParams(PageWidget widget, String xFieldName,AggregateOperator xAggr, String yFieldName,AggregateOperator yAggr, Criteria criteria) {
		addChartParams(widget, "line", xAggr, xFieldName, yAggr, yFieldName, null , DateOperators.CURRENT_YEAR, null, criteria);
	}
	
	protected static void addChartParams(PageWidget widget, String xFieldName, String yFieldName,String groupByFieldName, Criteria criteria) {
		addChartParams(widget, "line", DateAggregateOperator.MONTHANDYEAR, xFieldName, NumberAggregateOperator.AVERAGE, yFieldName, groupByFieldName , DateOperators.CURRENT_YEAR, null, criteria);
	}
	
	@SuppressWarnings("unchecked")
	protected static void addChartParams(PageWidget widget, String chartType, AggregateOperator xAggr, String xFieldName, AggregateOperator yAggr, 
			String yFieldName,String groupByFieldName, DateOperators dateOperator, String dateOperatorValue, Criteria criteria) {
		JSONObject obj = new JSONObject();
		obj.put("chartType", chartType);
		
		JSONObject xField = new JSONObject();
		xField.put("aggr", xAggr.getValue());
		xField.put("fieldName", xFieldName);
		obj.put("xField", xField);
		
		JSONObject yField = new JSONObject();
		yField.put("aggr", yAggr.getValue());
		yField.put("fieldName", yFieldName);
		obj.put("yField", yField);
		
		JSONObject groupBy = new JSONObject();
		groupBy.put("fieldName", groupByFieldName);
		obj.put("groupBy", groupBy);
		
		obj.put("dateOperator", dateOperator.getOperatorId());
		obj.put("dateOperatorValue", dateOperatorValue);
		obj.put("criteria", criteria);
		
		widget.addToWidgetParams("chartParams", obj);
	}


}
