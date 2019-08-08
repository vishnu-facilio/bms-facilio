package com.facilio.bmsconsole.page.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingType;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.bmsconsole.page.Page.Tab;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.PageWidget.CardType;
import com.facilio.bmsconsole.page.PageWidget.WidgetType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

public class AssetPageFactory extends PageFactory {
	
	private static final Logger LOGGER = LogManager.getLogger(AssetPageFactory.class.getName());
	
	public static Page getAssetPage(AssetContext asset) throws Exception {
		Page page = new Page();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(asset.getModuleId());
		
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
		
		if (AccountUtil.isFeatureEnabled(FeatureLicense.CONTROL_ACTIONS)) {
			Section tab3Sec2 = page.new Section("commands");
			addCommandWidget(tab3Sec2, asset.getId());
			
			if (CollectionUtils.isNotEmpty(tab3Sec2.getWidgets())) {
				tab3.addSection(tab3Sec2);
			}
		}
		
		
		Tab tab4 = page.new Tab("performance");
		page.addTab(tab4);
		
		Section tab4Sec1 = page.new Section();
		tab4.addSection(tab4Sec1);
		
		addAssetLifeWidget(tab4Sec1);
		addAlarmInsightsWidget(tab4Sec1);
		addLastDownTimeWidget(tab4Sec1);
		addOverallDowntimeWidget(tab4Sec1);
		
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(ContextNames.ASSET_BREAKDOWN));
		
		Criteria breakdownCriteria = new Criteria();
		breakdownCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(asset.getId()), NumberOperators.EQUALS));
		
		addFailureRateWidget(tab4Sec1, breakdownCriteria);
		addAvgTtrWidget(tab4Sec1, breakdownCriteria);
		if (AccountUtil.getCurrentOrg().getOrgId() == 155){
		Tab tab7 = page.new Tab("cost");
		page.addTab(tab7);
		
		Section tab7Sec1 = page.new Section();
		tab7.addSection(tab7Sec1);
		addAssetCostDetailsWidget(tab7Sec1);
		
//		addMaintenanceCostWidget(tab7Sec1);
//		addPlannedMaintenanceCostWidget(tab7Sec1);
//		addUnplannedMaintenanceCostWidget(tab7Sec1);
		
        fieldMap = FieldFactory.getAsMap(modBean.getAllFields(ContextNames.WORK_ORDER));
		Criteria maintenanceCostCriteria = new Criteria();
		maintenanceCostCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(asset.getId()), NumberOperators.EQUALS));
		addCostBreakupWidget(tab7Sec1,maintenanceCostCriteria);
		addMaintenanceCostTrendWidget(tab7Sec1, maintenanceCostCriteria);
		
		addDepreciationScheduleWidget(tab7Sec1);
		
		 fieldMap = FieldFactory.getAsMap(modBean.getAllFields(ContextNames.WORK_ORDER));
			Criteria depreciationCostCriteria = new Criteria();
			depreciationCostCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), String.valueOf(asset.getId()), NumberOperators.EQUALS));
			addDepreciationCostTrendWidget(tab7Sec1, depreciationCostCriteria);
		}
		// if (AccountUtil.isFeatureEnabled(FeatureLicense.GRAPHICS)) {
		if ((AccountUtil.getCurrentOrg().getOrgId() == 210 && asset.isConnected() ) || (AccountUtil.getCurrentOrg().getOrgId() == 75 && module.getName().equals("fahu"))) {
			
			Tab tab6 = page.new Tab("graphics", "graphics");
			page.addTab(tab6);
			
			Section tab6Sec1 = page.new Section();
			tab6.addSection(tab6Sec1);
			
			addGraphicsWidget(tab6Sec1);
		}
		
		Tab tab5 = page.new Tab("history", "history");
		page.addTab(tab5);
		
		Section tab5Sec1 = page.new Section();
		tab5.addSection(tab5Sec1);
		
		addHistoryWidget(tab5Sec1);
		

		return page;
	}
	
	private static void addPrimaryDetailsWidget(Section section) {
		PageWidget pageWidget = new PageWidget(WidgetType.PRIMARY_DETAILS_WIDGET);
		pageWidget.addToLayoutParams(section, 24, 6);
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
		nextPmWidget.addToLayoutParams(section, 8, 7);
		nextPmWidget.addToWidgetParams("type", CardType.NEXT_PM.getName());
		section.addWidget(nextPmWidget);
	}
	
	private static void addWoDetailsWidget(Section section) {
		PageWidget woDetailsWidget = new PageWidget(WidgetType.CARD);
		woDetailsWidget.addToLayoutParams(section, 8, 7);
		woDetailsWidget.addToWidgetParams("type", CardType.WO_DETAILS.getName());
		section.addWidget(woDetailsWidget);
	}
	
	private static void addRecentlyClosedWidget(Section section) {
		PageWidget recentlyClosedWidget = new PageWidget(WidgetType.CARD);
		recentlyClosedWidget.addToLayoutParams(section, 8, 7);
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
	
	private static void addCommandWidget(Section section, long assetId) {
		
		List<ReadingDataMeta> writableReadings = null;
		try {
			writableReadings = ReadingsAPI.getReadingDataMetaList(Collections.singletonList(assetId), null, true, ReadingType.WRITE);
		
			if (CollectionUtils.isNotEmpty(writableReadings)) {
				for(ReadingDataMeta rdm : writableReadings) {
					
					ReadingsAPI.convertUnitForRdmData(rdm);
					
					PageWidget commandWidget = new PageWidget(WidgetType.CARD);
					commandWidget.addToLayoutParams(section, 6, 6);
					commandWidget.addToWidgetParams("type", CardType.SET_COMMAND.getName());
					
					JSONObject obj = new JSONObject();
					obj.put("field", rdm.getField());
					obj.put("value", rdm.getValue());
					try {
						obj.put("inputUnit", FieldUtil.getAsProperties(rdm.getUnitEnum()));
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						LOGGER.error("Error in converting unit", e);
					}
	
					commandWidget.addToWidgetParams("data", obj);
					section.addWidget(commandWidget);
				}
			}
			
		} catch (Exception e) {
			LOGGER.error("Error in fetching writable readings", e);
		}
		
	}
	
	private static void addAssetCostDetailsWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 6);
		cardWidget.addToWidgetParams("type", CardType.ASSET_COST_DETAILS.getName());
		section.addWidget(cardWidget);
	}
	private static void addCostBreakupWidget(Section section, Criteria criteria) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 14);
		cardWidget.addToWidgetParams("type", CardType.COST_BREAKUP.getName());
		addChartParams(cardWidget, "createdTime", "totalCost","plannedvsunplanned", criteria);
		section.addWidget(cardWidget);
	}
	
	private static void addDepreciationScheduleWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 8);
		cardWidget.addToWidgetParams("type", CardType.DEPRECIATION_SCHEDULE.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addMaintenanceCostTrendWidget(Section section, Criteria criteria) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "maintenanceCostTrend");
		cardWidget.addToLayoutParams(section, 24, 14);
		cardWidget.addToWidgetParams("type", CardType.MAINTENANCE_COST_TREND.getName());
		addChartParams(cardWidget, "createdTime", "totalCost","plannedvsunplanned", criteria);
		section.addWidget(cardWidget);
	}
	
	private static void addDepreciationCostTrendWidget(Section section, Criteria criteria) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "depreciationCostTrend");
		cardWidget.addToLayoutParams(section, 24, 14);
		cardWidget.addToWidgetParams("type", CardType.DEPRECIATION_COST_TREND.getName());
		addChartParams(cardWidget, "createdTime", "totalCost","plannedvsunplanned", criteria);
		section.addWidget(cardWidget);
	}
	
	private static void addAssetLifeWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 2);
		cardWidget.addToWidgetParams("type", CardType.ASSET_LIFE.getName());
		section.addWidget(cardWidget);
	}
	
	private static void addAlarmInsightsWidget(Section section) {
		PageWidget cardWidget = new PageWidget(WidgetType.CARD);
		cardWidget.addToLayoutParams(section, 24, 11);
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
	
	private static void addFailureRateWidget(Section section, Criteria breakdownCriteria) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "failureRate");
		cardWidget.addToLayoutParams(section, 12, 14);
		cardWidget.addToWidgetParams("type", CardType.FAILURE_RATE.getName());
		
		addChartParams(cardWidget, "fromtime", "timeBetweenFailure", breakdownCriteria);
		
		section.addWidget(cardWidget);
	}
	
	private static void addAvgTtrWidget(Section section, Criteria breakdownCriteria) {
		PageWidget cardWidget = new PageWidget(WidgetType.CHART, "avgTtr");
		cardWidget.addToLayoutParams(section, 12, 14);
		cardWidget.addToWidgetParams("type", CardType.AVG_TTR.getName());
		
		addChartParams(cardWidget, "fromtime", "duration", breakdownCriteria);
		
		section.addWidget(cardWidget);
	}
	
	
	private static void addGraphicsWidget(Section section) {
		PageWidget widget = new PageWidget(WidgetType.GRAPHICS);
		widget.addToLayoutParams(section, 24, 3);
		section.addWidget(widget);
	}
	
}
