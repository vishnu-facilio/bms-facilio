package com.facilio.bmsconsole.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.page.Page.Section;
import com.facilio.modules.FieldUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class PageWidget {
	private static final long serialVersionUID = 1L;
	
	public PageWidget () {}
	
	public PageWidget(WidgetType type) {
		this(type, null);
	}
	
	public PageWidget(WidgetType type, String name) {
		this.widgetType = type;
		this.name = name;
	}

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private String title;
	public String getTitle() { return  title; }
	public void setTitle (String title) { this.title = title; }

	private WidgetType widgetType;
	public WidgetType getWidgetTypeEnum() {
		return widgetType;
	}
	public int getWidgetType() {
		return widgetType != null ?  widgetType.getValue() : -1;
	}
	public void setWidgetType(int widgetType) {
		this.widgetType = WidgetType.valueOf(widgetType);
	}
	public void setWidgetType(WidgetType widgetType) {
		this.widgetType = widgetType;
	}
	
	@JsonIgnore
	public Map<String, Object> getWidgetTypeObj() throws Exception {
		return FieldUtil.getAsProperties(widgetType);
	}
	
	private long sectionId;
	public long getSectionId() {
		return sectionId;
	}
	public void setSectionId(long sectionId) {
		this.sectionId = sectionId;
	}
	
	private Section section;
	public Section getSection() {
		return section;
	}
	public void setSection(Section section) {
		this.section = section;
	}
	
	private JSONObject relatedList;
	
	public JSONObject getRelatedList() {
		return relatedList;
	}

	public void setRelatedList(JSONObject relatedList) {
		this.relatedList = relatedList;
	}

	
	private JSONObject widgetParams;
	public JSONObject getWidgetParams() {
		return widgetParams;
	}
	public void setWidgetParams(JSONObject widgetParams) {
		this.widgetParams = widgetParams;
	}
	public void setWidgetParams(String widgetParams) throws Exception {
		if(widgetParams != null) {
			JSONParser parser = new JSONParser();
			this.widgetParams = (JSONObject) parser.parse(widgetParams);
		}
	}
	
	public void addToWidgetParams (String key, Object value) {
		if (widgetParams == null) {
			widgetParams = new JSONObject();
		}
		widgetParams.put(key, value);
	}
	
	public void addCardType (CardType cardType) {
		addToWidgetParams("type", cardType.getName());
	}

	private JSONObject layoutParams;
	public JSONObject getLayoutParams() {
		return layoutParams;
	}
	public void setLayoutParams(JSONObject layoutParams) {
		this.layoutParams = layoutParams;
	}
	public void setLayoutParams(String layoutParams) throws Exception {
		if(layoutParams != null) {
			JSONParser parser = new JSONParser();
			this.layoutParams = (JSONObject) parser.parse(layoutParams);
		}
	}
	public void setXPoisition(int position) {
		addToLayoutParams("x", position);
	}
	public void setYPoisition(int position) {
		addToLayoutParams("y", position);
	}
	public void setWidth(int width) {
		addToLayoutParams("w", width);
	}
	public void setHeight(int height) {
		addToLayoutParams("h", height);
	}
	public void addToLayoutParams(Section section, int width, int height) {
		int x = section.getLatestX();
		int y = section.getLatestY();
		addToLayoutParams(section, x, y, width, height);
	}
	public void addToLayoutParams(Section section, int x, int y, int width, int height) {
		addToLayoutParams(x, y, width, height);
		x += width;
		if (x >= 24 || width >= 24) {
			y += height;	// Assuming the height will be same for everywidget
			x = 0;
		}
		section.setLatestXY(x, y);
	}
	public void addToLayoutParams(int xPosition, int yPosition, int width, int height) {
		setXPoisition(xPosition);
		setYPoisition(yPosition);
		setWidth(width);
		setHeight(height);
	}
	
	private void addToLayoutParams (String key, Object value) {
		if (layoutParams == null) {
			layoutParams = new JSONObject();
		}
		layoutParams.put(key, value);
	}
	
	private List<PageWidget> widgets;
	public List<PageWidget> getWidgets() {
		return widgets;
	}
	public void setWidgets(List<PageWidget> widgets) {
		this.widgets = widgets;
	}
	public void addToWidget (PageWidget widget) {
		if (widgets == null) {
			widgets = new ArrayList<PageWidget>();
		}
		widgets.add(widget);
	}

	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
	public enum WidgetType {
		DETAILS_WIDGET("detailsWidget"),
 		PRIMARY_DETAILS_WIDGET("primaryDetailsWidget"),
 		MV_DETAILS_WIDGET("mvSummaryWidget"),
 		SECONDARY_DETAILS_WIDGET("secondaryDetailsWidget"),
 		CARD("card"),
 		CHART("chart"),
 		LIST("list"),
 		COUNT("count"),
 		COMMENT("comment"),
 		ATTACHMENT("attachment"),
 		RELATED_LIST("relatedList"),
 		ACTIVITY("activity"),
 		GROUP("group"),
 		HISTORY("history"),
 		RULE_DETIALS_WIGET("ruleDetialsWidget"),
 		RULE_ASSOCIATED_WO("ruleAssociatedWo"),
 		RULE_RCA("ruleRCA"),
 		HISTORY_LOG("historyLog"),
 		GRAPHICS("graphics"),
 		ANOMALY_DETAILS_WIDGET("anomalyDetailsWidget"),
 		ANOMALIES_TREND_WIDGET("anomaliesTrendWidget"),
 		ANOMALY_SUB_METER_WIDGET("anomalySubMeterWidget"),
 		ANOMALY_RCA("anomalyRca"),
 		OCCURRENCE_HISTORY("occurrenceHistory"),
 		ANOMALY_METRICS("anomalyMetrics"),
 		FORMULA_LOG("formulaLog"),
		ALARM_DETAILS("alarmDetails"),
 		ALARM_REPORT("alarmReport"),
 		KPI_DETAILS_WIDGET("kpiDetailsWidget"),
		IMPACTS("impactsWidget"),
		ALARM_TIME_LINE("alarmTimeLine"),
		INSURANCE("insurance"),
		RECURRING_INFO("recurringInfo"),
		ACTIVITY_WIDGET("activityWidget"),
		ADDRESS("addressLocation"),
		RULE_ACTION_WIDGET("ruleActionWidget"),
		WORK_REQUEST_DETAILS_WIDGET("workRequestDetails"),
		PORTAL_ACTIVITY("portalActivity"),
		PORTAL_WORK_REQUEST_FORM_DETAILS("portalWorkrequestFormDetails")
 		;
		
		private String name;
		
		WidgetType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
 		public int getValue() {
			return ordinal() + 1;
		}
		
		public static WidgetType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
 	}
	
	public enum CardType {
		NEXT_PM("nextPm"),
		FAILURE_METRICS("failureMetrics"),
		WO_DETAILS("woDetails"),
		RECENTLY_CLOSED_PM("recentlyClosedPm"),
		ASSET_LIFE("assetLife"),
		TIME("time"),
		ALARM_INSIGHTS("alarmInsights"),
		LAST_DOWNTIME("lastDownTime"),
		OVERALL_DOWNTIME("overallDownTime"),
		FAILURE_RATE("failureRate"),
		AVG_TTR("avgTtr"),
		SET_COMMAND("setCommand"),
		RANK_RULE("rankRule"),
		RULE_ASSETS_ALARM("assetsAlarm"),
		RULE_ALARM_INSIGHT("ruleInsights"),
		RULE_WO(""),
		ECM("ecm"),
		MV_SAVINGS("savings"),
		MV_ENERGY("mvEnergy"),
		CARBON_EMISSION("carbonEmision"),
		BASELINE_EQUATION("baselineEquation"),
		MV_ADJUSTMENTS("mvAdjustments"),
		MV_BASELINE_ACTUAL("baselineVsActual"),
		MV_COST_TREND("costTrend"),
		MV_CUMULATIVE_SAVINGS("cumulativeSavings"),
		MV_PERCENTAGE_SAVINGS("mvPercentageSavings"),
		RULE_ASSOCIATED_WO("ruleAssociatedWo"),
		RULE_WO_DURATION("ruleWoDuration"),
		MAINTENACE_COST("maintenanceCost"),
 		PLANNED_MAINTENACE_COST("plannedMaintenanceCost"),
 		UNPLANNED_MAINTENACE_COST("unplannedMaintenanceCost"),
 		MAINTENANCE_COST_TREND("maintenanceCostTrend"),
 		NO_OF_ANOMALIES("noOfAnomalies"),
 		ML_ALARM_ENERGY_CDD("mlAlarmEnergyCdd"),
 		ENERGY_WASTAGE_DETAILS("energWastageDetails"),
 		ASSET_COST_DETAILS("assetCostDetails"),
 		COST_BREAKUP("costBreakup"),
 		DEPRECIATION_SCHEDULE("depreciationSchedule"),
 		DEPRECIATION_COST_TREND("depreciationCostTrend"),
 		ML_MTBA("mlMtba"),
 		ML_MTTC("mlMttc"),
 		ML_DEVIATION("mlDeviation"),
 		KPI_METERS_ASSOCIATED("metersAssociated"),
 		KPI_VIOLATIONS("kpiViolations"),
 		KPI_TARGET("kpiTarget"),
 		KPI_LATEST_VALUE("kpiLatestValue"),
 		KPI_TREND("kpiTrend"),
 		KPI_TABULAR("kpiTabular"),
		ALARM_DUARTION("alarmDuration"),
		ANALYTICS_CHART("analyticsChart"),
 		IMPACT_DETAILS("impactDetails")
 		;
		private String name;
		
		CardType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
 		public int getValue() {
			return ordinal() + 1;
		}
		
		public static CardType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
 	}
	
}
