package com.facilio.bmsconsole.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.relation.context.RelationRequestContext;
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

	private RelationRequestContext relation;
	public void setRelation(RelationRequestContext relation) {
		this.relation = relation;
	}
	public RelationRequestContext getRelation() {
		return relation;
	}

	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
	public enum WidgetType {
		DETAILS_WIDGET("detailsWidget"),
		PRIMARY_DETAILS_WIDGET("primaryDetailsWidget"),
		SPARE_PARTS("spareParts"),
		WHERE_USED("whereUsed"),
		MV_DETAILS_WIDGET("mvSummaryWidget"),
		SECONDARY_DETAILS_WIDGET("secondaryDetailsWidget"),
		WORKORDER_SECONDARY_DETAILS_WIDGET("workOrderSecondaryDetailsWidget"),
		CUSTOM_SECONDARY_DETAILS_WIDGET("customSecondaryDetailsWidget"),
		TENANT_SPECIAL_WIDGET("tenantSpecialWidget"),
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
		BMS_ALARM_DETAILS("bmsAlarmDetails"),
		SENSOR_ALARM_DETAIL("sensorAlarmDetail"),
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
		PORTAL_WORK_REQUEST_FORM_DETAILS("portalWorkrequestFormDetails"),
		TENANT_PORTAL_VISITS_DETAILS("tenantPortalVisitsDetails"),
		SPACE_WIDGET("spaceWidget"),
		PORTAL_INSURANCE("portalInsurance"),
		PORTAL_WORKPERMIT("portalWorkPermit"),
		RELATED_COUNT("relatedCount"),
		SITE_LIST_WIDGET("siteListWidget"),
		FIXED_DETAILS_WIDGET("fixedDetailsWidget"),
		AGENT_DETAILS_WIDGET("agentDetailsWidget"),
		FLOOR_MAP("floorMap"),
		SPACE_PRIMARY_READINGS("spacePrimaryReadings"),
		TEMPLATE_SUMMARY_DETAILS("templateSummaryDetails"),
		TEMPALTE_CONDITIONS("templateConditions"),
		TEMPLATE_ALARM_DETAILS("templateAlarmDetails"),

		APPROVERS("approvers"),
		APPROVAL_FIELDS_WIDGET("approvalFields"),
		QUOTATION_PREVIEW("quotationPreview"),
		WORKPERMIT_PREVIEW("workPermitPreview"),
		BILL_LIST("billList"),
		BILL_ALERT_DETAILS("billAlertDetails"),
		INVOICE_DETAILS("invoiceDetails"),
		INVOICE_DETAILS_2("invoiceDetails2"),
		SUPPLIER_ACTIVE_PAYMENTS("supplierActivePayment"),
		SUPPLIER_TRANSACTIONS("supplierTransaction"),
		ARREARS("arrears"),
		UNDER_REVIEW("underReview"),
		DETAILS_TABLE("detailstable"),
		DETAILS_TAB("detailsTab"),



		SUPPLIER_CANCELLED_PAYMENTS("supplierCancelledPayments"),
		SUPPLIER_CARDS("supplierCards"),
		SUPPLIER_CARDS2("supplierCards2"),
		SUPPLIER_ELECTRYCITY("supplierElectrycity"),
		SUPPLIER_WATER("supplierWater"),
		SUPPLIER_SEAWAGE("supplierSeawage"),
		SUPPLIER_DETAILS("supplierDetails"),
		TARIFF_DETAILS("tariffDetails"),
		TARIFF_INFORMATION("tariffInformation"),

		UTILITY_DETAILS("utilityDetails"),
		UTILITY_CONNECTIONS("utilityConnections"),
		UTILITY_ACTIVITY_PAYMENT("utilityActivePayments"),
		UTILITY_CANCEL_PAYMENT("utilityCancelPayments"),
		UTILITY_TRANSACTIONS("utilityTransactions"),
		UTILITY_ALL_ACTIVITIES("utilityAllActivities"),

		BILL_SITE_DETAILS("billSiteDetails"),

		INVENTORY_REQUEST_CARD1("inventoryrequestcard1"),
		INVENTORY_REQUEST_CARD2("inventoryrequestcard2"),
		INVENTORY_REQUEST_CARD3("inventoryrequestcard3"),
		INVENTORY_REQUEST_LINE_ITEMS("inventoryrequestlineitems"),

		SERVICE_VENDORS("servicevendors"),



		CONNNECTED_APP("connectedApp"),

		VISITS_PRIMARY_FIELDS("visitsPrimaryFields"),
		INVITES_PRIMARY_FIELDS("invitesPrimaryFields"),

		// Community Features Widgets
		PUBLISH_TO_INFO("publishToInfo"),
		ANNOUNCEMENT_SECONDARY_DETAILS_WIDGET("announcementSecondaryDetailsWidget"),
		ATTACHMENTS_PREVIEW("attachmentsPreview"),
		RICH_TEXT_PREVIEW("richTextPreview"),
		NEIGHBOURHOOD_DETAILS_WIDGET("neighbourhoodDetailsWidget"),
		DEALS_DETAILS_WIDGET("dealsDetailsWidget"),
		NEIGHBOURHOOD_DEALS("neighbourhoodDeals"),
		NEWS_AND_INFORMATION("newsAndInformationWidget"),
		AUDIENCE_INFO("publishedAudienceInfo"),

		// Budget Widgets
		TOTAL_BUDGET("totalBudget"),
		ACTUAL_BUDGET_AMOUNT("actualBudgetAmount"),
		REMAINING_BUDGET("remainingBudget"),
		BUDGET_PRIMARY_DETAILS("budgetPrimaryDetails"),
		BUDGET_SPLIT_UP("budgetSplitUp"),
		BUDGET_NET_INCOME("budgetNetIncome"),

		// Purchase Module Widgets
		PR_PREVIEW("prPreview"),
		PO_PREVIEW("poPreview"),
		RFQ_PREVIEW("rfqPreview"),
		VENDOR_QUOTES_LINE_ITEMS("vendorQuotesLineItems"),

		// Facility Module Widgets
		FACILITY_PHOTOS("facilityPhotos"),
		FACILITY_FEATURES("facilityFeatures"),
		FACILITY_SLOT_INFORMATION("facilitySlotInformation"),
		FACILITY_SPECIAL_AVAILABILITY("facilitySpecialAvailability"),
		BOOKING_INFO("bookingInfo"),
		BOOKING_SLOT_INFORMATION("bookingSlotInformation"),
		BOOKING_INTERNAL_ATTENDEES("bookingInternalAttendees"),
		BOOKING_EXTERNAL_ATTENDEES("bookingExternalAttendees"),

		INSPECTION_TEMPLATE_inspectionDetails("inspectionDetails"),
		INSPECTION_TEMPLATE_inspectionPageDetails("inspectionPageDetails"),
		INSPECTION_TEMPLATE_inspectionTriggers("inspectionTriggers"),
		INSPECTION_TEMPLATE_inspectionInsights("inspectionInsights"),
		INSPECTION_TEMPLATE_inspectionQuestions("inspectionQuestions"),
		INSPECTION_TEMPLATE_history("inspectionHistory"),

		INSPECTION_RESPONSE_WIDGET("inspectionResponseWidget"),

		INDUCTION_TEMPLATE_inductionDetails("inductionDetails"),
		INDUCTION_TEMPLATE_inductionPageDetails("inductionPageDetails"),
		INDUCTION_TEMPLATE_inductionTriggers("inductionTriggers"),
		INDUCTION_TEMPLATE_inductionInsights("inductionInsights"),
		INDUCTION_TEMPLATE_inductionQuestions("inductionQuestions"),
		INDUCTION_TEMPLATE_history("inductionHistory"),

		INDUCTION_RESPONSE_WIDGET("inductionResponseWidget"),

		Q_AND_A_SECONDARY_DETAILS_WIDGET("qandASecondaryDetailsWidget"),

		// Service Request Module Widgets
		SR_EMAIL_THREAD("srEmailThread"),
		SR_DETAILS_WIDGET("srDetailsWidget"),

		COMBINED_RELATED_LIST("combinedRelatedListWidget"),
		BOOKINGS_RELATED_LIST("bookingRelatedListWidget"),

		// Workorder Widgets
		WORKORDER_DESCRIPTION("workorderDescription"),
		WORKORDER_ATTACHMENTS("workorderAttachments"),
		WORKORDER_COMMENTS("workorderComments"),
		TASKS_COMPLETED("tasksCompleted"),
		SCHEDULED_DURATION("scheduledDuration"),
		ACTUAL_DURATION("actualDuration"),
		WORK_DURATION("workDuration"),
		SLA_REMAINING_TIME("slaRemainingTime"),
		RESOURCE("resource"),
		RESPONSIBILITY("responsibility"),
		WORKORDER_DETAILS("workorderDetails"),
		RELATED_RECORDS("relatedRecords"),
		WORKORDER_PROGRESS("workorderProgress"),
		TASKS("tasks"),
		WORKORDER_HISTORY("workorderHistory"),
		WORKORDER_SIDEBAR("workorderSidebar"),
		HAZARDS("hazards"),
		WORKORDER_SAFETY_PLAN("workorderSafetyPlan"),
		PRECAUTIONS("precautions"),
		PREREQUISITES("prerequisites"),
		TASKS_MONOLITH("taskMonolith"),
		ITEMS_AND_LABOR("itemsAndLabor"),
		PLANS("plans"),
		PLANS_COST("plansCost"),
		QUOTATION("quotation"),
		INVENTORY_ITEMS("inventoryItems"),
		INVENTORY_SERVICES("inventoryServices"),
		INVENTORY_LABOR("inventoryLabor"),
		INVENTORY_TOOLS("inventoryTools"),
		INVENTORY_OVERALL_COST("inventoryOverallCost"),
		TENANT("tenant"),
		TOTAL_COST("totalCost"),
		TRANSFER_REQUEST_LINE_ITEMS("transferrequestlineitems"),
		DESCRIPTION_CARD("descriptioncard"),
		SURVEY_TEMPLATE_surveyDetails("surveyDetails"),
		SURVEY_TEMPLATE_surveyPageDetails("surveyPageDetails"),
		SURVEY_TEMPLATE_surveyTriggers("surveyTriggers"),
		SURVEY_TEMPLATE_surveyInsights("surveyInsights"),
		SURVEY_TEMPLATE_surveyQuestions("surveyQuestions"),
		SURVEY_TEMPLATE_history("surveyHistory"),

		SURVEY_RESPONSE_WIDGET("surveyResponseWidget"),
		TRANSFER_REQUEST_SHIPMENT_RECEIVABLES("transferrequestshipmentreceivables"),
		STATE_TRANSITION_TIME_LOG("statetransitiontimelog"),

		STORE_ROOM_CARD1("storeroomcard1"),
		STORE_ROOM_CARD2("storeroomcard2"),
		STORE_ROOM_CARD3("storeroomcard3"),

		SERVING_SITES_WIDGET("servingsites"),
		ITEMS_TOOLS_WIDGET("itemstools"),
		STOREROOM_PHOTOS("storeroomphotos"),
		INVENTORY_REQ_RELATED_LIST("inventoryreqrelatedlist"),
		PO_RELATED_LIST("porelatedlist"),
		STORE_ROOM("storeroom"),
		TRANSACTIONS("transactions"),
		PURCHASE_ORDER("purchaseorder"),
		INVENTORY_CARD("inventorycard"),
		TENANT_PRIMARY_CONTACT("tenantprimarycontact"),
		TENANT_WORKORDERS("tenantworkorders"),
		TENANT_BOOKINGS("tenantbookings"),
		TENANT_DETAIL_CONTACT("tenantdetailcontact"),
		TENANT_DESCRIPTION("tenantdescription"),
		ASSOCIATED_TERMS("associatedterms"),
		TENANT_DETAIL_OVERVIEW("tenantdetailoverview"),
		FAILURE_CLASS_DETAILS("failureclassdetails"),
		FAILURE_REPORT("failurereports"),
		WORK_ASSET("workAsset"),
		SAFETYPLAY_HAZARD("safetyPlanHazards"),
		WORKORDER_HAZARD_PRECAUTIONS("workorderHazardPrecautions"),
		FAILURE_CLASS_ASSOCIATED_PROBELMS("failureclassassociatedproblems"),
		PURCHASED_ITEMS("purchaseditems"),
		ITEM_TRANSACTIONS("transactionsitem"),
		TOOL_TRANSACTIONS("transactionstool"),
		ACTUALS_COST("actualsCost"),
		ACTUALS("actuals"),
		WORK_ORDER_ITEMS("workorderitems"),
		WORK_ORDER_TOOLS("workordertools"),
		WORK_ORDER_SERVICE("workorderservice"),
		JOB_PLAN_ITEMS("jobplanitems"),
		JOB_PLAN_TOOLS("jobplantools"),
		JOB_PLAN_SERVICE("jobplanservice"),
		TENANT_UNIT_OVERVIEW("tenantunitoverview"),
		TENANT_UNIT_SPECIAL_WIDGET("tenantunitspecialwidget"),
		TENANT_UNIT_TENANT("tenantunittenant"),
		TENANT_UNIT_WORKORDER("tenantunitworkorder"),
		TENANT_UNIT_LOCATION("tenantunitlocation"),
		TENANT_UNIT_PHOTO("tenantunitphoto"),
		RECEIVABLE_RECEIPTS("receivablereceipts"),

		RELATIONSHIP_WIDGET("relationshipwidget"),
		JOBPLAN_FIELDS_WIDGET("jobPlanFieldDetails"),
		JOBPLAN_TASKS_WIDGET("jobPlanTasks"),

		PLANNED_INVENTORY_OVERALL_COST("plannedInventoryOverallCost"),
		PLANNED_INVENTORY_ITEMS("plannedInventoryItems"),
		PLANNED_INVENTORY_SERVICES("plannedInventoryServices"),
		PLANNED_INVENTORY_LABOR("plannedInventoryLabor"),
		PLANNED_INVENTORY_TOOLS("plannedInventoryTools"),
		BUDGET_TRANSACTIONS("budgetTransactions"),

		PLANNED_MAINTENANCE_DETAILS("preventiveMaintenanceDetails"),
		PM_PLANNER_DETAILS("pmPlannerDetails"),
		PM_EXECUTED_WORKORDERS_DETAILS("pmExecutedWorkOrdersDetails"),
		PM_SCHEDULER_DETAILS("pmSchedulerDetails"),

		SPACE_BOOKING_FULL_SUMMARY("spaceBookingFullSummary"),
		SPACE_BOOKING_CARD_DETAILS("spaceBookingCardDetails"),
		SPACE_BOOKING_SECONDARY_DETAILS("spaceBookingSecondaryDetails"),
		SUMMARY_FIELDS_WIDGET("summaryFieldsWidget"),
		CLASSIFICATION("classification"),
		NEW_RELATED_LIST("newRelatedList"),
		MULTIRESOURCE("multiResource"),
		JOBPLAN_PLANNER("jobPlanPlanner"),
		ALARM_SECONDARY_DETAILS("alarmSecondaryDetails"),
		NEW_RELATED_COUNT("newRelatedCount"),
		WEATHER_CHART("weatherChart"),
		WEATHER_TABLE("weatherTable");

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
		SPARE_PARTS("spareParts"),
		TIME("time"),
		ALARM_INSIGHTS("alarmInsights"),
		LAST_DOWNTIME("lastDownTime"),
		OVERALL_DOWNTIME("overallDownTime"),
		FAILURE_RATE("failureRate"),
		AVG_TTR("avgTtr"),
		SET_COMMAND("setCommand"),
		MAINTENACE_COST("maintenanceCost"),
		PLANNED_MAINTENACE_COST("plannedMaintenanceCost"),
		UNPLANNED_MAINTENACE_COST("unplannedMaintenanceCost"),
		MAINTENANCE_COST_TREND("maintenanceCostTrend"),
		ASSET_COST_DETAILS("assetCostDetails"),
		COST_BREAKUP("costBreakup"),
		DEPRECIATION_SCHEDULE("depreciationSchedule"),
		DEPRECIATION_COST_TREND("depreciationCostTrend"),
		OPERATION_ALARM_REPORT("operationAlarm"),

		RANK_RULE("rankRule"),
		RULE_ASSETS_ALARM("assetsAlarm"),
		RULE_ALARM_INSIGHT("ruleInsights"),
		RULE_WO(""),
		RULE_ASSOCIATED_WO("ruleAssociatedWo"),
		RULE_WO_DURATION("ruleWoDuration"),
		NO_OF_ANOMALIES("noOfAnomalies"),
		ML_ALARM_ENERGY_CDD("mlAlarmEnergyCdd"),
		ENERGY_WASTAGE_DETAILS("energWastageDetails"),
		ML_MTBA("mlMtba"),
		ML_MTTC("mlMttc"),
		ML_DEVIATION("mlDeviation"),
		ALARM_DUARTION("alarmDuration"),
		ANALYTICS_CHART("analyticsChart"),
		IMPACT_DETAILS("impactDetails"),
		OUT_OF_SCHEDULE("outOfSchedule"),
		DEFAULT_COMPARISION_CARD("defaultComparison"),

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

		KPI_METERS_ASSOCIATED("metersAssociated"),
		KPI_VIOLATIONS("kpiViolations"),
		KPI_TARGET("kpiTarget"),
		KPI_LATEST_VALUE("kpiLatestValue"),
		KPI_TREND("kpiTrend"),
		KPI_TABULAR("kpiTabular"),

		WEATHER("weather"),
		ENERGY("energy"),
		OPERATING_HOURS("operatingHours"),
		BUILDING("building"),
		SPACE("space"),
		OCCUPANCY("occupancy"),
		ASSET_READING_WIDGET("assetReadingWidget"),
		NEW_OPERATING_HOURS("newOperatingHours"),
		CURRENT_WEATHER("currentWeather"),
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
