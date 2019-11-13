package com.facilio.bmsconsole.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext.AlarmType;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext.AssetState;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.context.FormulaFieldContext.ResourceType;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.context.reservation.ReservationContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.SystemEnumField;
import com.facilio.time.DateTimeUtil;

public class ViewFactory {

	private static Map<String, Map<String, FacilioView>> views = Collections.unmodifiableMap(initializeViews());
	private static Map<String, Map<String, List<String>>> groupViews = Collections
			.unmodifiableMap(initializeGroupViews()); // TODO remove
	private static Map<String, List<Map<String, Object>>> groupVsViews = Collections
			.unmodifiableMap(initializeGroupVsViews());

	public static FacilioView getView(FacilioModule module, String viewName, ModuleBean modBean) throws Exception {
		String moduleName;
		if (viewName.contains("approval_")) {
			moduleName = FacilioConstants.ContextNames.APPROVAL;
		} else {
			moduleName = module.getName();
		}
		return getView(moduleName, module, viewName, null);
	}
	
	public static FacilioView getView(String moduleName, String viewName) throws Exception {
		return getView(moduleName, null, viewName, null);
	}
	
	public static FacilioView getView(String moduleName, FacilioModule module, String viewName, ModuleBean modBean) throws Exception {
		FacilioView view = null;
		if (viewName != null) {
			
			view = getModuleViews(moduleName, module).get(viewName);
			if (view != null) {
				List<ViewField> columns = ColumnFactory.getColumns(moduleName, viewName);
				if (columns == null && module != null) {
					columns = new ArrayList<>();
					if (modBean == null) {
						modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					}
					List<FacilioField> allFields = modBean.getAllFields(moduleName);
					for (FacilioField field : allFields) {
							ViewField viewField = new ViewField(field.getName(), field.getDisplayName());
						    viewField.setField(field);
						    columns.add(viewField);	
				}
				}
				if (columns != null && module.getTypeEnum() == ModuleType.CUSTOM) {
					List<ViewField> fieldsToRemove = new ArrayList<>();
					for(ViewField column : columns) {
						if (column.getName().equals("stateFlowId")) {
							fieldsToRemove.add(column);
						}
						if (module.getStateFlowEnabled() != null && !module.getStateFlowEnabled() && column.getName().equals("moduleState")) {
							fieldsToRemove.add(column);
						}
					}
					columns.removeAll(fieldsToRemove);
				}

				view.setFields(columns);
				view.setDefault(true);
			}
		}
		return view;
	}

	public static Map<String, FacilioView> getModuleViews(String moduleName, FacilioModule module) throws Exception {
		Map<String, FacilioView> moduleViews = new LinkedHashMap<>();
		if (views.get(moduleName) != null) {
			views.get(moduleName).forEach((name, view) -> {
				moduleViews.put(name, new FacilioView(view));
			});
		}
		if (MapUtils.isEmpty(moduleViews)) {
			if (module != null && module.getTypeEnum() == ModuleType.CUSTOM) {
				// Views of custom module
				FacilioView allView = getCustomModuleAllView(module);
				moduleViews.put("all", allView);
			}
		}
		return moduleViews;
	}

	public static Map<String, List<String>> getGroupViews(String moduleName1) {
		Map<String, List<String>> moduleViews1 = new LinkedHashMap<>();
		if (groupViews.containsKey(moduleName1)) {
			moduleViews1.putAll(groupViews.get(moduleName1));
		}

		return moduleViews1;
	}

	public static List<Map<String, Object>> getGroupVsViews(String moduleName) {
		List<Map<String, Object>> moduleGroups = new ArrayList<>();
		if (groupVsViews.containsKey(moduleName)) {
			moduleGroups.addAll(groupVsViews.get(moduleName));
		}
		return moduleGroups;
	}

	private static Map<String, Map<String, FacilioView>> initializeViews() {
		Map<String, Map<String, FacilioView>> viewsMap = new HashMap<String, Map<String, FacilioView>>();
		Map<String, FacilioView> views = new LinkedHashMap<>();
		int order = 1;

		views.put("open", getOpenWorkorderRequests().setOrder(order++));
		views.put("all", getAllWorkRequests().setOrder(order++));
		views.put("rejected", getRejectedWorkorderRequests().setOrder(order++));
		views.put("myrequests", getMyWorkorderRequests().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST, views);

		order = 1;
		views = new LinkedHashMap<>();
//		views.put("approval_requested", getRequestedApproval().setOrder(order++));
//		views.put("approval_approved", getApprovedApproval().setOrder(order++));
//		views.put("approval_rejected", getRejectedApproval().setOrder(order++));
//		views.put("approval_all", getAllApproval().setOrder(order++));
		views.put("approval_myrequests", getMyRequestWorkorders().setOrder(order++));
		views.put("approval_requested", getRequestedStateApproval().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.APPROVAL, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("open", getAllOpenWorkOrders().setOrder(order++));
		views.put("overdue", getAllOverdueWorkOrders().setOrder(order++));
		views.put("duetoday", getAllDueTodayWorkOrders().setOrder(order++));
		views.put("planned", getOpenPlannedWorkOrders().setOrder(order++));
		views.put("unplanned", getOpenUnPlannedWorkOrders().setOrder(order++));
		views.put("unassigned", getUnassignedWorkorders().setOrder(order++));
		views.put("myopen", getMyOpenWorkOrders().setOrder(order++));
		views.put("myteamopen", getMyTeamOpenWorkOrders().setOrder(order++));
		views.put("myoverdue", getMyOverdueWorkOrders().setOrder(order++));
		views.put("myduetoday", getMyDueTodayWorkOrders().setOrder(order++));
		views.put("openfirealarms", getFireSafetyWOs().setOrder(order++));
		views.put("all", getAllWorkOrders().setOrder(order++));
		views.put("resolved", getAllResolvedWorkOrders().setOrder(order++));
		views.put("closed", getAllClosedWorkOrders().setOrder(order++));
		views.put("report", getReportView().setOrder(order++));
		views.put("myrequests", getMyRequestWorkorders().setOrder(order++));
		views.put("upcomingThisWeek", getUpcomingWorkOrdersThisWeek().setOrder(order++));
		views.put("upcomingNextWeek", getUpcomingWorkOrdersNextWeek().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WORK_ORDER, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("my", getMyTasks().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.TASK, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllAssetsView().setOrder(order++));
		views.put("energy", getAssets("Energy").setOrder(order++));
		views.put("hvac", getAssets("HVAC").setOrder(order++));
		views.put("active", getAssetsByState("Active").setOrder(order++));
		views.put("retired", getAssetsByState("Retired").setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ASSET, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllTenantsView().setOrder(order++));
		views.put("active", getActiveTenantsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.TENANT, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("today", getEvents("Today").setOrder(order++));
		views.put("yesterday", getEvents("Yesterday").setOrder(order++));
		views.put("thisweek", getEvents("ThisWeek").setOrder(order++));
		views.put("lastweek", getEvents("LastWeek").setOrder(order++));
		viewsMap.put("event", views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("active", getRulesByStatus("active", "Active", true).setOrder(order++));
		views.put("inactive", getRulesByStatus("inactive", "In Active", false).setOrder(order++));
		views.put("all", getAllRules());
		viewsMap.put("readingrule", views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("active",
				getSeverityAlarms("active", "Active", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++));
		views.put("unacknowledged", getUnacknowledgedAlarms().setOrder(order++));
		views.put("critical", getSeverityAlarms("critical", "Critical Alarms", "Critical", true).setOrder(order++));
		views.put("major", getSeverityAlarms("major", "Major Alarms", "Major", true).setOrder(order++));
		views.put("minor", getSeverityAlarms("minor", "Minor Alarms", "Minor", true).setOrder(order++));
		views.put("fire", getTypeAlarms("fire", "Fire Alarms", AlarmType.FIRE).setOrder(order++));
		views.put("energy", getTypeAlarms("energy", "Energy Alarms", AlarmType.ENERGY).setOrder(order++));
		views.put("hvac", getTypeAlarms("hvac", "HVAC Alarms", AlarmType.HVAC).setOrder(order++));
		views.put("cleared", getSeverityAlarms("cleared", "Cleared Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, true)
				.setOrder(order++));
		views.put("anomalies",
				getSourceTypeAlarms("anomalies", "Anomalies", SourceType.ANOMALY_ALARM).setOrder(order++));
		views.put("all", getAllAlarms().setOrder(order++));
		views.put("report", getReportView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ALARM, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("report", getReportView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ENERGY_DATA_READING, views);

		order = 1;
		views = new LinkedHashMap<>();
		FacilioView preventiveView = getStatusPreventiveWorkOrders("active", "Active", true).setOrder(order++);
		preventiveView.setModuleName(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		views.put(preventiveView.getName(), preventiveView);
		preventiveView = getTypePreventiveWorkOrders("preventive", "Preventive", "Preventive").setOrder(order++);
		preventiveView.setModuleName(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		views.put(preventiveView.getName(), preventiveView);
		preventiveView = getTypePreventiveWorkOrders("corrective", "Corrective", "Corrective").setOrder(order++);
		preventiveView.setModuleName(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		views.put(preventiveView.getName(), preventiveView);
		preventiveView = getTypePreventiveWorkOrders("rounds", "Rounds", "Rounds").setOrder(order++);
		preventiveView.setModuleName(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		views.put(preventiveView.getName(), preventiveView);
		preventiveView = getTypePreventiveWorkOrders("breakdown", "Breakdown", "Breakdown").setOrder(order++);
		preventiveView.setModuleName(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		views.put(preventiveView.getName(), preventiveView);
		preventiveView = getTypePreventiveWorkOrders("compliance", "Compliance", "Compliance").setOrder(order++);
		preventiveView.setModuleName(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		views.put(preventiveView.getName(), preventiveView);
		preventiveView = getAllPreventiveWorkOrders().setOrder(order++);
		preventiveView.setModuleName(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		views.put(preventiveView.getName(), preventiveView);
		preventiveView = getStatusPreventiveWorkOrders("inactive", "Inactive", false).setOrder(order++);
		preventiveView.setModuleName(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		views.put(preventiveView.getName(), preventiveView);
		viewsMap.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllInventory().setOrder(order++));
		views.put("stale", getStalePartsView().setOrder(order++));
		views.put("understocked", getUnderStockedPartsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.INVENTORY, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllStoreRooms().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.STORE_ROOM, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllItemTypes().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ITEM_TYPES, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllToolTypes().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.TOOL_TYPES, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllVendors().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.VENDORS, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllInventry().setOrder(order++));
		views.put("stale", getStalePartsView().setOrder(order++));
		views.put("understocked", getUnderStockedItemView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ITEM, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllTools().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.TOOL, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("pendingitem", getItemPendingApproval().setOrder(order++));
		views.put("allitem", getAllItemApproval().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ITEM_TRANSACTIONS, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("pendingtool", getToolPendingApproval().setOrder(order++));
		views.put("alltool", getAllToolApproval().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.TOOL_TRANSACTIONS, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("allpuritem", getAllPurchasedItem().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.PURCHASED_ITEM, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllGatePass().setOrder(order++));
		views.put("pending", getGatePassForStatus("pending", "Pending", 1).setOrder(order++));
		views.put("approved", getGatePassForStatus("approved", "Approved", 2).setOrder(order++));
		views.put("rejected", getGatePassForStatus("rejected", "Rejected", 4).setOrder(order++));
		views.put("pendingreturn", getPendingReturnGatePass("pendingreturn", "Pending Return", 2).setOrder(order++));
		views.put("overduereturn", getOverDueReturnGatePass("overduereturn", "Overdue Return", 2).setOrder(order++));
		
		viewsMap.put(FacilioConstants.ContextNames.GATE_PASS, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllPurchaseRequestView().setOrder(order++));
		views.put("open", getOpenPurchaseRequest().setOrder(order++));
		views.put("pending", getPurchaseRequestForStatus("pending", "Pending", 1).setOrder(order++));
		views.put("overdue", getOverDuePurchaseRequest().setOrder(order++));
		views.put("approved", getPurchaseRequestForStatus("approved", "Approved", 2).setOrder(order++));
		views.put("rejected", getPurchaseRequestForStatus("rejected", "Rejected", 3).setOrder(order++));
		views.put("completed", getPurchaseRequestForStatus("completed", "Completed", 4).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.PURCHASE_REQUEST, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllPurchaseOrderView().setOrder(order++));
		views.put("open", getOpenPurchaseOrder().setOrder(order++));
		views.put("pending", getPurchaseOrderForStatus("pending", "Pending", 1).setOrder(order++));
		views.put("overdue", getOverDuePurchaseOrder().setOrder(order++));
		views.put("approved", getPurchaseOrderForStatus("approved", "Approved", 2).setOrder(order++));
		views.put("ongoing", getOnGoingPurchaseOrder().setOrder(order++));
		views.put("rejected", getPurchaseOrderForStatus("rejected", "Rejected", 3).setOrder(order++));
		views.put("completed", getPurchaseOrderForStatus("completed", "Completed", 7).setOrder(order++));
		
		viewsMap.put(FacilioConstants.ContextNames.PURCHASE_ORDER, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllReceivableView().setOrder(order++));
		views.put("pending", getReceivableForStatus("pending", "Pending", 1).setOrder(order++));
		views.put("partial", getReceivableForStatus("partial", "Partially Received", 2).setOrder(order++));
		views.put("received", getReceivableForStatus("received", "Received", 3).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.RECEIVABLE, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("expiring", getExpiringContractView(ModuleFactory.getContractsModule(), -1).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.CONTRACTS, views);

		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllPurchaseContractView().setOrder(order++));
		views.put("expiring", getExpiringContractView(ModuleFactory.getContractsModule(), 1).setOrder(order++));
		
		viewsMap.put(FacilioConstants.ContextNames.PURCHASE_CONTRACTS, views);

	
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllLabourContractView().setOrder(order++));
		views.put("expiring", getExpiringContractView(ModuleFactory.getContractsModule(), 2).setOrder(order++));
		
		viewsMap.put(FacilioConstants.ContextNames.LABOUR_CONTRACTS, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllRentalLeaseContractView().setOrder(order++));
		views.put("expiring", getExpiringContractView(ModuleFactory.getContractsModule(), 4).setOrder(order++));
		
		viewsMap.put(FacilioConstants.ContextNames.RENTAL_LEASE_CONTRACTS, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllWarrantyContractView().setOrder(order++));
		views.put("expiring", getExpiringContractView(ModuleFactory.getContractsModule(), 3).setOrder(order++));
		
		viewsMap.put(FacilioConstants.ContextNames.WARRANTY_CONTRACTS, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllPoLineItemsSerialNumeberView().setOrder(order++));
		
		viewsMap.put(FacilioConstants.ContextNames.PO_LINE_ITEMS_SERIAL_NUMBERS, views);
		

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllShipmentView().setOrder(order++));
		views.put("staged", getShipmentForStatus("staged", "Staged", 2).setOrder(order++));
//		views.put("shipped", getShipmentForStatus("shipped", "Shipped", 3).setOrder(order++));
		views.put("received", getShipmentForStatus("received", "Received", 4).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.SHIPMENT, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllInventoryRequestView().setOrder(order++));
		views.put("pendingapproval", getInventoryRequestForStatus("pendingapproval", "Pending Approval", 1).setOrder(order++));
		views.put("pendingissue", getInventoryRequestForStatus("pendingissue", "Pending Issue", 2).setOrder(order++));
		views.put("rejected", getInventoryRequestForStatus("rejected", "Rejected", 3).setOrder(order++));
		views.put("issued", getInventoryRequestForStatus("issued", "Issued", 6).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.INVENTORY_REQUEST, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllAttendanceView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ATTENDANCE, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllAttendanceTransactionView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ATTENDANCE_TRANSACTIONS, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllShiftView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.SHIFT, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllBreakView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.BREAK, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllShiftRotationView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.SHIFT_ROTATION, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllBreakTransactionView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.BREAK_TRANSACTION, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("today", getBaseEvents(DateOperators.TODAY).setOrder(order++));
		views.put("yesterday", getBaseEvents(DateOperators.YESTERDAY).setOrder(order++));
		views.put("thisweek", getBaseEvents(DateOperators.CURRENT_WEEK).setOrder(order++));
		views.put("lastweek", getBaseEvents(DateOperators.LAST_WEEK).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.BASE_EVENT, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllReadingAlarmViews().setOrder(order++));
		views.put("active", getReadingAlarmSeverity("active", "Active", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++));
		views.put("unacknowledged", getReadingAlarmUnacknowledged().setOrder(order++));
		views.put("critical", getReadingAlarmSeverity("critical", "Critical Alarms", "Critical", true).setOrder(order++));
		views.put("major", getReadingAlarmSeverity("major", "Major Alarms", "Major", true).setOrder(order++));
		views.put("minor", getReadingAlarmSeverity("minor", "Minor Alarms", "Minor", true).setOrder(order++));
		views.put("cleared", getReadingAlarmSeverity("cleared", "Cleared Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, true)
				.setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.NEW_READING_ALARM, views);

		views.put("bmsAlarm", getBmsAlarm("bmsAlarm" , "All Alarms", true).setOrder(order++));
		views.put("bmsActive", getBmsAlarmSeverity("bmsActive", "Active Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++));
		views.put("unacknowledgedbmsalarm", getBmsAlarmUnacknowledged().setOrder(order++));
		views.put("bmsCritical", getBmsAlarmSeverity("bmsCritical", "Critical Alarms", "Critical", true).setOrder(order++));
		views.put("bmsMajor", getBmsAlarmSeverity("bmsMajor", "Major Alarms", "Major", true).setOrder(order++));
		views.put("bmsMinor", getBmsAlarmSeverity("bmsMinor", "Minor Alarms", "Minor", true).setOrder(order++));
		views.put("bmsCleared", getBmsAlarmSeverity("bmsCleared", "Cleared Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, true).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.BMS_ALARM, views);
		
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getOccurrenceViews().setOrder(order++));
		views.put("active", getAlarmOcccurrenceSeverity("active", "Active", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++));
		views.put("unacknowledged", getAlarmOccurrenceUnacknowledged().setOrder(order++));
		views.put("critical", getAlarmOcccurrenceSeverity("critical", "Critical Alarms", "Critical", true).setOrder(order++));
		views.put("major", getAlarmOcccurrenceSeverity("major", "Major Alarms", "Major", true).setOrder(order++));
		views.put("minor", getAlarmOcccurrenceSeverity("minor", "Minor Alarms", "Minor", true).setOrder(order++));
		views.put("cleared", getAlarmOcccurrenceSeverity("cleared", "Cleared Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, true)
				.setOrder(order++));
		views.put("bmsAlarm", getBmsAlarmOccurrence("bmsAlarm" , "All Bms Alarm", true).setOrder(order++));
		views.put("bmsActive", getBmsOccurrenceAlarmSeverity("bmsActive", "Bms Active Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++));
		views.put("unacknowledgedbmsalarm", getBmsAlarmOccurrenceUnacknowledged().setOrder(order++));
		views.put("bmsCritical", getBmsOccurrenceAlarmSeverity("bmsCritical", "Bms Critical Alarms", "Critical", true).setOrder(order++));
		views.put("bmsMajor", getBmsOccurrenceAlarmSeverity("bmsMajor", "Bms Major Alarms", "Major", true).setOrder(order++));
		views.put("bmsMinor", getBmsOccurrenceAlarmSeverity("bmsMinor", "Bms Minor Alarms", "Minor", true).setOrder(order++));
		views.put("bmsCleared", getBmsOccurrenceAlarmSeverity("bmsCleared", "Bms Cleared Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, true).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ALARM_OCCURRENCE, views);


		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllTermsAndConditionView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS, views);

	
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllServiceView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.SERVICE, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getMLAnomalyViews().setOrder(order++));
		views.put("activeAnomaly", getAnomalyAlarmSeverity("activeAnomaly", "Active", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++));
		views.put("criticalAnomaly", getAnomalyAlarmSeverity("criticalAnomaly", "Critical", "Critical", true).setOrder(order++));
		views.put("majorAnomaly", getAnomalyAlarmSeverity("majorAnomaly", "Major", "Major", true).setOrder(order++));
		views.put("minorAnomaly", getAnomalyAlarmSeverity("minorAnomaly", "Minor", "Minor", true).setOrder(order++));
		views.put("clearedAnomaly", getAnomalyAlarmSeverity("clearedAnomaly", "Cleared", FacilioConstants.Alarm.CLEAR_SEVERITY, true).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ML_ANOMALY_ALARM, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("nearing", getTodayReservationView().setOrder(order++));
		views.put("thisweek", getThisWeekReservationView().setOrder(order++));
		views.put("nextweek", getNextWeekReservationView().setOrder(order++));
		views.put("ongoing", getOnGoingReservationView().setOrder(order++));
		views.put("completed", getCompletedReservationView().setOrder(order++));
		views.put("all", getAllReservationView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.Reservation.RESERVATION, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllVisitorsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.VISITOR, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllVisitorLogsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.VISITOR_LOGGING, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllVisitorInvitesView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.VISITOR_INVITE, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllInsuranceView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.INSURANCE, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllWorkPermitView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WORKPERMIT, views);

		order = 1;
		views = new LinkedHashMap<>();
		Map<String, FacilioField> formulaFieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldFields());
		views.put("asset", getAssetKPIView("asset", formulaFieldMap).setOrder(order++));
		views.put("space", getSpaceKPIView("space", formulaFieldMap).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.FORMULA_FIELD, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllWatchListView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WATCHLIST, views);

		return viewsMap;
	}

	private static Map<String, Map<String, List<String>>> initializeGroupViews() {
		Map<String, Map<String, List<String>>> viewsMap1 = new HashMap<String, Map<String, List<String>>>();
		Map<String, List<String>> groupViews = new LinkedHashMap<String, List<String>>();
		ArrayList<String> open = new ArrayList<String>();
		open.add("open");
		open.add("overdue");
		open.add("duetoday");
		open.add("planned");
		open.add("unplanned");
		open.add("unassigned");

		ArrayList<String> myopen = new ArrayList<String>();
		myopen.add("myopen");
		myopen.add("myteamopen");
		myopen.add("myoverdue");
		myopen.add("myduetoday");

		ArrayList<String> resolved = new ArrayList<String>();
		resolved.add("resolved");

		ArrayList<String> closed = new ArrayList<String>();
		closed.add("closed");

		ArrayList<String> all = new ArrayList<String>();
		all.add("all");

		ArrayList<String> upcoming = new ArrayList<>();
		upcoming.add("upcomingThisWeek");
		upcoming.add("upcomingNextWeek");

		groupViews.put("OpenWorkOrders", open); // TODO change name to lower
												// case..also in client
		groupViews.put("MyopenWorkorders", myopen);
		groupViews.put("ResolvedWorkorders", resolved);
		groupViews.put("AllWorkorders", all);
		groupViews.put("closeWorkorder", closed);
		groupViews.put("upcomingWorkorder", upcoming);

		viewsMap1.put(FacilioConstants.ContextNames.WORK_ORDER, groupViews);
				
		return viewsMap1;

	}

	private static Map<String, List<Map<String, Object>>> initializeGroupVsViews() {
		Map<String, List<Map<String, Object>>> moduleVsGroup = new HashMap<String, List<Map<String, Object>>>();

		List<Map<String, Object>> groupVsViews = new ArrayList<>();
		Map<String, Object> groupDetails = new HashMap<>();

		ArrayList<String> open = new ArrayList<String>();
		open.add("open");
		open.add("overdue");
		open.add("duetoday");
		open.add("planned");
		open.add("unplanned");
		open.add("unassigned");

		groupDetails.put("name", "openworkorders");
		groupDetails.put("displayName", "Open Work Orders");
		groupDetails.put("views", open);
		groupVsViews.add(groupDetails);

		ArrayList<String> myopen = new ArrayList<String>();
		myopen.add("myopen");
		myopen.add("myteamopen");
		myopen.add("myoverdue");
		myopen.add("myduetoday");

		groupDetails = new HashMap<>();
		groupDetails.put("name", "myopenworkorders");
		groupDetails.put("displayName", "My Open Work Orders");
		groupDetails.put("views", myopen);
		groupVsViews.add(groupDetails);

		ArrayList<String> resolved = new ArrayList<String>();
		resolved.add("resolved");

		groupDetails = new HashMap<>();
		groupDetails.put("name", "resolvedworkorders");
		groupDetails.put("displayName", "Resolved Work Orders");
		groupDetails.put("views", resolved);
		groupVsViews.add(groupDetails);

		ArrayList<String> closed = new ArrayList<String>();
		closed.add("closed");

		groupDetails = new HashMap<>();
		groupDetails.put("name", "closedworkorders");
		groupDetails.put("displayName", "Closed Work Orders");
		groupDetails.put("views", closed);
		groupVsViews.add(groupDetails);

		ArrayList<String> all = new ArrayList<String>();
		all.add("all");

		groupDetails = new HashMap<>();
		groupDetails.put("name", "allworkorders");
		groupDetails.put("displayName", "All Work Orders");
		groupDetails.put("views", all);
		groupVsViews.add(groupDetails);
		
		
		ArrayList<String> upcoming = new ArrayList<>();
		upcoming.add("upcomingThisWeek");
		upcoming.add("upcomingNextWeek");
		
		groupDetails = new HashMap<>();
		groupDetails.put("name", "upcomingworkorders");
		groupDetails.put("displayName", "Upcoming Work Orders");
		groupDetails.put("views", upcoming);
		groupVsViews.add(groupDetails);
		
		
		groupDetails = new HashMap<>();
		groupDetails.put("name", "customworkorders");
		groupDetails.put("displayName", "Custom Work Orders");
		groupDetails.put("type", "custom");
		groupDetails.put("views", null);
		groupVsViews.add(groupDetails);

		moduleVsGroup.put(FacilioConstants.ContextNames.WORK_ORDER, groupVsViews);
		
		groupVsViews = new ArrayList<>();
		ArrayList<String> fddAlarms = new ArrayList<String>();
		fddAlarms.add("all");
		fddAlarms.add("active");
		fddAlarms.add("unacknowledged");
		fddAlarms.add("critical");
		fddAlarms.add("major");
		fddAlarms.add("minor");
		fddAlarms.add("cleared");
		
		groupDetails = new HashMap<>();
		groupDetails.put("name", "fddAlarmsViews");
		groupDetails.put("displayName", "FDD Alarms");
		groupDetails.put("views", fddAlarms);
		groupVsViews.add(groupDetails);
		
		ArrayList<String> bmsAlarms = new ArrayList<String>();
		bmsAlarms.add("bmsAlarm");
		bmsAlarms.add("bmsActive");
		bmsAlarms.add("unacknowledgedbmsalarm");
		bmsAlarms.add("bmsMajor");
		bmsAlarms.add("bmsMinor");
		bmsAlarms.add("bmsCritical");
		bmsAlarms.add("bmsCleared");
		
		
		groupDetails = new HashMap<>();
		groupDetails.put("name", "bmsAlarmsViews");
		groupDetails.put("displayName", "BMS Alarms");
		groupDetails.put("views", bmsAlarms);
		groupVsViews.add(groupDetails);
		
		groupDetails = new HashMap<>();
		groupDetails.put("name", "customalarms");
		groupDetails.put("displayName", "Custom Views");
		groupDetails.put("type", "custom");
		groupDetails.put("views", null);
		groupVsViews.add(groupDetails);
		
		moduleVsGroup.put(FacilioConstants.ContextNames.NEW_READING_ALARM, groupVsViews);
		
		groupVsViews = new ArrayList<>();
		ArrayList<String> asset = new ArrayList<String>();
		asset.add("all");
		asset.add("energy");
		asset.add("hvac");
		asset.add("active");
		asset.add("retired");
		
		groupDetails = new HashMap<>();
		groupDetails.put("name", "assetviews");
		groupDetails.put("displayName", "Asset");
		groupDetails.put("views", asset);
		groupVsViews.add(groupDetails);
		
		moduleVsGroup.put(FacilioConstants.ContextNames.ASSET, groupVsViews);

		return moduleVsGroup;

	}
	
	private static FacilioView getEvents(String category) {

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(EventConstants.EventModuleFactory.getEventModule());

		Condition dateCondition = new Condition();
		dateCondition.setField(createdTime);
		if (category.equals("Today")) {
			dateCondition.setOperator(DateOperators.TODAY);
		} else if (category.equals("Yesterday")) {
			dateCondition.setOperator(DateOperators.YESTERDAY);
		} else if (category.equals("ThisWeek")) {
			dateCondition.setOperator(DateOperators.CURRENT_WEEK);
		} else if (category.equals("LastWeek")) {
			dateCondition.setOperator(DateOperators.LAST_WEEK);
		}
		Criteria criteria = new Criteria();
		criteria.addAndCondition(dateCondition);

		FacilioView eventsView = new FacilioView();
		eventsView.setName("Created Time");
		eventsView.setDisplayName("Event Time");
		eventsView.setCriteria(criteria);

		return eventsView;
	}
	
	private static FacilioView getBaseEvents(DateOperators dateOperator) {

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getBaseEventModule());

		Condition dateCondition = new Condition();
		dateCondition.setField(createdTime);
		dateCondition.setOperator(dateOperator);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(dateCondition);

		FacilioView eventsView = new FacilioView();
		eventsView.setName(dateOperator.getOperator());
		eventsView.setDisplayName(dateOperator.getOperator());
		eventsView.setCriteria(criteria);

		return eventsView;
	}

	private static Criteria getAssetCategoryCriteria(String category) {
		FacilioField categoryType = new FacilioField();
		categoryType.setName("categoryType");
		categoryType.setColumnName("CATEGORY_TYPE");
		categoryType.setDataType(FieldType.NUMBER);
		categoryType.setModule(ModuleFactory.getAssetCategoryModule());

		Condition statusOpen = new Condition();
		statusOpen.setField(categoryType);
		statusOpen.setOperator(NumberOperators.EQUALS);
		if (category.equals("Energy")) {
			statusOpen.setValue(String.valueOf(AssetCategoryContext.AssetCategoryType.ENERGY.getIntVal()));
		} else if (category.equals("HVAC")) {
			statusOpen.setValue(String.valueOf(AssetCategoryContext.AssetCategoryType.HVAC.getIntVal()));
		}

		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusOpen);

		return criteria;
	}

	private static Condition getTenantStateCondition(String state) {
		FacilioModule tenantsModule = ModuleFactory.getTenantsModule();
		FacilioField statusField = new FacilioField();
		statusField.setName("status");
		statusField.setColumnName("STATUS");
		statusField.setDataType(FieldType.ENUM);
		statusField.setModule(tenantsModule);

		Condition status = new Condition();
		status.setField(statusField);
		status.setOperator(NumberOperators.EQUALS);
		if (state.equals("Active")) {
			status.setValue(String.valueOf(TenantContext.Status.ACTIVE.getValue()));
		} else if (state.equals("Retired")) {
			status.setValue(String.valueOf(TenantContext.Status.ACTIVE.getValue()));
		}

		return status;
	}
	private static Condition getAssetStateCondition(String state) {
		FacilioModule assetsModule = ModuleFactory.getAssetsModule();
		FacilioField statusField = new FacilioField();
		statusField.setName("state");
		statusField.setColumnName("STATE");
		statusField.setDataType(FieldType.NUMBER);
		statusField.setModule(assetsModule);

		Condition open = new Condition();
		open.setField(statusField);
		open.setOperator(NumberOperators.EQUALS);
		if (state.equals("Active")) {
			open.setValue(String.valueOf(AssetState.ACTIVE.getIntVal()));
		} else if (state.equals("Retired")) {
			open.setValue(String.valueOf(AssetState.RETIRED.getIntVal()));
		}

		return open;
	}

	private static Condition getAssetCategoryCondition(String category) {
		FacilioModule module = ModuleFactory.getAssetsModule();
		LookupField statusField = new LookupField();
		statusField.setName("category");
		statusField.setColumnName("CATEGORY");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getAssetCategoryModule());

		Condition open = new Condition();
		open.setField(statusField);
		open.setOperator(LookupOperator.LOOKUP);
		open.setCriteriaValue(getAssetCategoryCriteria(category));

		return open;
	}
	

	private static Criteria getAssetStatusCriteria(String status) {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("status");
		statusTypeField.setColumnName("STATUS");
		statusTypeField.setDataType(FieldType.STRING);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusActive = new Condition();
		statusActive.setField(statusTypeField);
		statusActive.setOperator(StringOperators.IS);
		statusActive.setValue(status);

		Criteria statusCriteria = new Criteria();
		statusCriteria.addAndCondition(statusActive);

		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(ModuleFactory.getAssetsModule());
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition ticketActive = new Condition();
		ticketActive.setField(statusField);
		ticketActive.setOperator(LookupOperator.LOOKUP);
		ticketActive.setCriteriaValue(statusCriteria);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(ticketActive);
		return criteria;
	}

	private static FacilioView getAssetsByState(String state) {

		FacilioView assetView = new FacilioView();
		Criteria criteria = getAssetStatusCriteria(state);
		if (state.equals("Active")) {
			assetView.setName("active");
			assetView.setDisplayName("Active Assets");
			assetView.setCriteria(criteria);
		} else if (state.equals("Retired")) {
			assetView.setName("retired");
			assetView.setDisplayName("Retired Assets");
			assetView.setCriteria(criteria);
		}

		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getAssetsModule());

		assetView.setSortFields(Arrays.asList(new SortField(localId, false)));

		return assetView;
	}

	private static FacilioView getAllRules() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkflowRuleModule());

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		allView.setSortFields(sortFields);
		return allView;
	}

	private static FacilioView getRulesByStatus(String name, String displayName, boolean status) {
		Condition statusCondition = getRulesStateCondition(status);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusCondition);
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkflowRuleModule());

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setCriteria(criteria);
		view.setSortFields(sortFields);

		return view;
	}

	private static Condition getRulesStateCondition(Boolean status) {
		List<FacilioField> rulesFields = FieldFactory.getWorkflowRuleFields();
		Map<String, FacilioField> fieldProps = FieldFactory.getAsMap(rulesFields);

		FacilioField statusField = fieldProps.get("status");

		Condition statusCondition = CriteriaAPI.getCondition(statusField, String.valueOf(status), BooleanOperators.IS);
		return statusCondition;
	}

	private static FacilioView getAllAssetsView() {
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Assets");
		allView.setSortFields(getSortFields(FacilioConstants.ContextNames.ASSET));
		return allView;
	}

	private static FacilioView getAllTenantsView() {
		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getTenantsModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Tenants");
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));
		return allView;
	}
	
	private static FacilioView getActiveTenantsView() {
		FacilioField localId = new FacilioField();
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getTenantStateCondition("Active"));
		
		
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getTenantsModule());

		FacilioView activeTenantsView = new FacilioView();
		activeTenantsView.setName("active");
		activeTenantsView.setDisplayName("Residing Tenants");
		activeTenantsView.setSortFields(Arrays.asList(new SortField(localId, false)));
		activeTenantsView.setCriteria(criteria);
		
		return activeTenantsView;
	}

	
	private static FacilioView getAssets(String category) {

		FacilioView assetView = new FacilioView();
		if (category.equals("Energy")) {
			Criteria criteria = new Criteria();
			criteria.addAndCondition(getAssetCategoryCondition(category));

			assetView.setName("energy");
			assetView.setDisplayName("Energy Assets");
			assetView.setCriteria(criteria);
		} else if (category.equals("HVAC")) {
			Criteria criteria = new Criteria();
			criteria.addAndCondition(getAssetCategoryCondition(category));

			assetView.setName("hvac");
			assetView.setDisplayName("HVAC Assets");
			assetView.setCriteria(criteria);
		}

		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getAssetsModule());

		assetView.setSortFields(Arrays.asList(new SortField(localId, false)));

		return assetView;
	}

	private static Criteria getNotOpenStatusCriteria() {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("typeCode");
		statusTypeField.setColumnName("STATUS_TYPE");
		statusTypeField.setDataType(FieldType.NUMBER);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusOpen = new Condition();
		statusOpen.setField(statusTypeField);
		statusOpen.setOperator(NumberOperators.NOT_EQUALS);
		statusOpen.setValue(String.valueOf(FacilioStatus.StatusType.OPEN.getIntVal()));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusOpen);

		return criteria;
	}

	public static Criteria getOpenStatusCriteria() {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("typeCode");
		statusTypeField.setColumnName("STATUS_TYPE");
		statusTypeField.setDataType(FieldType.NUMBER);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusOpen = new Condition();
		statusOpen.setField(statusTypeField);
		statusOpen.setOperator(NumberOperators.EQUALS);
		statusOpen.setValue(String.valueOf(FacilioStatus.StatusType.OPEN.getIntVal()));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusOpen);

		return criteria;
	}

	public static Criteria getPreOpenStatusCriteria() {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("typeCode");
		statusTypeField.setColumnName("STATUS_TYPE");
		statusTypeField.setDataType(FieldType.NUMBER);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusOpen = new Condition();
		statusOpen.setField(statusTypeField);
		statusOpen.setOperator(NumberOperators.EQUALS);
		statusOpen.setValue(String.valueOf(FacilioStatus.StatusType.PRE_OPEN.getIntVal()));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusOpen);

		return criteria;
	}

	public static Condition getPreOpenStatusCondition() {
		FacilioModule module = ModuleFactory.getTicketsModule();
		LookupField statusField = new LookupField();
		statusField.setName("status");
		statusField.setColumnName("STATUS_ID");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition preopen = new Condition();
		preopen.setField(statusField);
		preopen.setOperator(LookupOperator.LOOKUP);
		preopen.setCriteriaValue(getPreOpenStatusCriteria());

		return preopen;
	}

	public static Condition getOpenStatusCondition() {
		FacilioModule module = ModuleFactory.getTicketsModule();
		LookupField statusField = new LookupField();
		statusField.setName("status");
		statusField.setColumnName("STATUS_ID");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition open = new Condition();
		open.setField(statusField);
		open.setOperator(LookupOperator.LOOKUP);
		open.setCriteriaValue(getOpenStatusCriteria());

		return open;
	}

	public static Criteria getCloseStatusCriteria() {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("typeCode");
		statusTypeField.setColumnName("STATUS_TYPE");
		statusTypeField.setDataType(FieldType.NUMBER);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusClose = new Condition();
		statusClose.setField(statusTypeField);
		statusClose.setOperator(NumberOperators.EQUALS);
		statusClose.setValue(String.valueOf(FacilioStatus.StatusType.CLOSED.getIntVal()));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusClose);

		return criteria;
	}
	
	public static Criteria getStateCriteria(StatusType statusType) {
		FacilioModule module = ModuleFactory.getTicketsModule();
		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition status = new Condition();
		status.setField(statusField);
		status.setOperator(LookupOperator.LOOKUP);
		status.setCriteriaValue(getStateTypeCriteria(statusType));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(status);
		return criteria;
	}
	
	public static Criteria getStateTypeCriteria(StatusType statusType) {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("typeCode");
		statusTypeField.setColumnName("STATUS_TYPE");
		statusTypeField.setDataType(FieldType.NUMBER);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition status = new Condition();
		status.setField(statusTypeField);
		status.setOperator(NumberOperators.EQUALS);
		status.setValue(String.valueOf(statusType.getIntVal()));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(status);

		return criteria;
	}
	
	public static Criteria getRequestedStateCriteria(boolean isRequested) {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("requestedState");
		statusTypeField.setColumnName("REQUESTED_STATE");
		statusTypeField.setDataType(FieldType.BOOLEAN);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition status = new Condition();
		status.setField(statusTypeField);
		status.setOperator(BooleanOperators.IS);
		status.setValue(String.valueOf(isRequested));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(status);

		return criteria;
	}

	private static FacilioView getUnassignedWorkorders() {
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		Criteria unassignedWOCriteria = getUnAssignedCriteria();
		unassignedWOCriteria.addAndCondition(getOpenStatusCondition());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView unassignedWOView = new FacilioView();
		unassignedWOView.setName("unassigned");
		unassignedWOView.setDisplayName("Unassigned");
		unassignedWOView.setCriteria(unassignedWOCriteria);
		unassignedWOView.setSortFields(sortFields);

		return unassignedWOView;
	}

	private static Criteria getUnAssignedCriteria() {
		LookupField userField = new LookupField();
		userField.setName("assignedTo");
		userField.setColumnName("ASSIGNED_TO_ID");
		userField.setDataType(FieldType.LOOKUP);

		FacilioModule module = ModuleFactory.getTicketsModule();
		userField.setModule(module);
		userField.setSpecialType(FacilioConstants.ContextNames.USERS);

		Condition userFieldCondition = new Condition();
		userFieldCondition.setField(userField);
		userFieldCondition.setOperator(CommonOperators.IS_EMPTY);

		// LookupField groupField = (LookupField)
		// FieldFactory.getField("assignmentGroup", "ASSIGNMENT_GROUP_ID",
		// workOrdersModule, FieldType.LOOKUP);
		// groupField.setExtendedModule(ModuleFactory.getTicketsModule());
		// groupField.setSpecialType(FacilioConstants.ContextNames.GROUPS);
		//
		// Condition groupFieldCondition = new Condition();
		// groupFieldCondition.setField(groupField);
		// groupFieldCondition.setOperator(CommonOperators.IS_EMPTY);
		Criteria criteria = new Criteria();
		criteria.addAndCondition(userFieldCondition);
		// criteria.addAndCondition(groupFieldCondition);
		return criteria;
	}

	private static FacilioView getMyWorkorderRequests() {

		Criteria criteria = new Criteria();
		FacilioModule workOrderRequestsModule = ModuleFactory.getWorkOrderRequestsModule();
		criteria.addAndCondition(getMyRequestCondition());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrderRequestsModule);

		FacilioView myRequestsView = new FacilioView();
		myRequestsView.setName("myrequests");
		myRequestsView.setDisplayName("My Work Requests");
		myRequestsView.setCriteria(criteria);
		myRequestsView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return myRequestsView;
	}

	private static Condition getMyRequestCondition() {
		FacilioModule workOrderRequestsModule = ModuleFactory.getWorkOrdersModule();
		LookupField userField = new LookupField();
		userField.setName("requester");
		userField.setColumnName("REQUESTER_ID");
		userField.setDataType(FieldType.LOOKUP);
		userField.setModule(workOrderRequestsModule);
		userField.setSpecialType(FacilioConstants.ContextNames.REQUESTER);

		Condition myUserCondition = new Condition();
		myUserCondition.setField(userField);
		myUserCondition.setOperator(PickListOperators.IS);
		myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

		return myUserCondition;
	}

	private static FacilioView getOpenWorkorderRequests() {

		Criteria criteria = getWorkRequestStatusCriteria(WorkOrderRequestContext.RequestStatus.OPEN);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrderRequestsModule());

		FacilioView allRequestsView = new FacilioView();
		allRequestsView.setName("open");
		allRequestsView.setDisplayName("Work Requests");
		allRequestsView.setCriteria(criteria);
		allRequestsView.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		return allRequestsView;
	}

	private static FacilioView getRejectedWorkorderRequests() {

		Criteria criteria = getWorkRequestStatusCriteria(WorkOrderRequestContext.RequestStatus.REJECTED);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrderRequestsModule());

		FacilioView allRequestsView = new FacilioView();
		allRequestsView.setName("rejected");
		allRequestsView.setDisplayName("Rejected Work Requests");
		allRequestsView.setCriteria(criteria);
		allRequestsView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return allRequestsView;
	}

	public static Criteria getWorkRequestStatusCriteria(WorkOrderRequestContext.RequestStatus status) {

		FacilioField field = new FacilioField();
		field.setName("requestStatus");
		field.setColumnName("STATUS");
		field.setDataType(FieldType.NUMBER);
		FacilioModule workOrderRequestsModule = ModuleFactory.getWorkOrderRequestsModule();
		field.setModule(workOrderRequestsModule);

		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(NumberOperators.EQUALS);
		condition.setValue(String.valueOf(status.getIntVal()));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);

		return criteria;
	}

	private static FacilioView getUpcomingWorkOrdersNextWeek() {
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		FacilioField pm = new FacilioField();
		pm.setName("pm");
		pm.setDataType(FieldType.NUMBER);
		pm.setColumnName("PM_ID");
		pm.setModule(ModuleFactory.getWorkOrdersModule());

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getPreOpenStatusCondition());
		criteria.addAndCondition(CriteriaAPI.getCondition(pm, CommonOperators.IS_NOT_EMPTY));
		criteria.addAndCondition(CriteriaAPI.getCondition(createdTime, DateOperators.NEXT_WEEK));

		FacilioView preOpenTicketsView = new FacilioView();
		preOpenTicketsView.setName("upcomingNextWeek");
		preOpenTicketsView.setDisplayName("Upcoming Next Week");
		preOpenTicketsView.setCriteria(criteria);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		preOpenTicketsView.setSortFields(sortFields);

		return preOpenTicketsView;
	}

	private static FacilioView getUpcomingWorkOrdersThisWeek() {
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		FacilioField pm = new FacilioField();
		pm.setName("pm");
		pm.setDataType(FieldType.NUMBER);
		pm.setColumnName("PM_ID");
		pm.setModule(ModuleFactory.getWorkOrdersModule());

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getPreOpenStatusCondition());
		criteria.addAndCondition(CriteriaAPI.getCondition(pm, CommonOperators.IS_NOT_EMPTY));
		criteria.addAndCondition(CriteriaAPI.getCondition(createdTime, DateOperators.CURRENT_WEEK));

		FacilioView preOpenTicketsView = new FacilioView();
		preOpenTicketsView.setName("upcomingThisWeek");
		preOpenTicketsView.setDisplayName("Upcoming This Week");
		preOpenTicketsView.setCriteria(criteria);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		preOpenTicketsView.setSortFields(sortFields);

		return preOpenTicketsView;
	}

	private static FacilioView getAllOpenWorkOrders() {
		// All Open Tickets

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getOpenStatusCondition());

		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("open");
		openTicketsView.setDisplayName("Open");
		openTicketsView.setCriteria(criteria);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		openTicketsView.setSortFields(sortFields);

		return openTicketsView;
	}

	private static FacilioView getRejectedApproval() {

		Criteria criteria = getApprovalStateCriteria(ApprovalState.REJECTED);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		FacilioView rejectedApproval = new FacilioView();
		rejectedApproval.setName("rejected");
		rejectedApproval.setDisplayName("Rejected");
		rejectedApproval.setCriteria(criteria);
		rejectedApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return rejectedApproval;
	}

	private static FacilioView getApprovedApproval() {

		Criteria criteria = getApprovalStateCriteria(ApprovalState.APPROVED);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		FacilioView rejectedApproval = new FacilioView();
		rejectedApproval.setName("approved");
		rejectedApproval.setDisplayName("Approved");
		rejectedApproval.setCriteria(criteria);
		rejectedApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return rejectedApproval;
	}

	private static FacilioView getRequestedApproval() {

		Criteria criteria = getApprovalStateCriteria(ApprovalState.REQUESTED);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		FacilioView rejectedApproval = new FacilioView();
		rejectedApproval.setName("requested");
		rejectedApproval.setDisplayName("Pending Approval");
		rejectedApproval.setCriteria(criteria);
		rejectedApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return rejectedApproval;
	}
	
	public static FacilioView getRequestedStateApproval() {
		
		FacilioModule module = ModuleFactory.getTicketsModule();
		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition requested = new Condition();
		requested.setField(statusField);
		requested.setOperator(LookupOperator.LOOKUP);
		requested.setCriteriaValue(getRequestedStateCriteria(true));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(requested);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		FacilioView rejectedApproval = new FacilioView();
		rejectedApproval.setName("requested");
		rejectedApproval.setDisplayName("Pending Approval");
		rejectedApproval.setCriteria(criteria);
		rejectedApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return rejectedApproval;
	}

	private static FacilioView getAllApproval() {

		Criteria criteria = getAllApprovalStateCriteria();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		FacilioView rejectedApproval = new FacilioView();
		rejectedApproval.setName("all");
		rejectedApproval.setDisplayName("All Approval");
		rejectedApproval.setCriteria(criteria);
		rejectedApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return rejectedApproval;
	}

	public static Criteria getApprovalStateCriteria(ApprovalState status) {

		FacilioField field = new FacilioField();
		field.setName("approvalState");
		field.setColumnName("APPROVAL_STATE");
		field.setDataType(FieldType.NUMBER);
		FacilioModule approvalStateModule = ModuleFactory.getWorkOrdersModule();
		field.setModule(approvalStateModule);

		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(NumberOperators.EQUALS);
		condition.setValue(String.valueOf(status.getValue()));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);

		return criteria;
	}

	public static Criteria getAllApprovalStateCriteria() {
		FacilioField field = new FacilioField();
		field.setName("approvalState");
		field.setColumnName("APPROVAL_STATE");
		field.setDataType(FieldType.NUMBER);
		FacilioModule approvalStateModule = ModuleFactory.getWorkOrdersModule();
		field.setModule(approvalStateModule);

		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(NumberOperators.EQUALS);
		List<String> list = Arrays.asList(String.valueOf(ApprovalState.REQUESTED.getValue()),
				String.valueOf(ApprovalState.REJECTED.getValue()), String.valueOf(ApprovalState.APPROVED.getValue()));
		condition.setValue(String.join(", ", list));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);

		return criteria;
	}

	private static FacilioView getAllClosedWorkOrders() {

		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("closed");
		openTicketsView.setDisplayName("Closed Workorders");
		openTicketsView.setCriteria(getClosedTicketsCriteria());
		openTicketsView.setSortFields(sortFields);

		return openTicketsView;
	}

	public static Criteria getClosedTicketsCriteria () {
		FacilioModule module = ModuleFactory.getTicketsModule();
		LookupField statusField = new LookupField();
		statusField.setName("status");
		statusField.setColumnName("STATUS_ID");
		statusField.setDataType(FieldType.LOOKUP);

		statusField.setModule(module);
//		statusField.setExtendedModule(ModuleFactory.getTicketsModule());
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition ticketClose = new Condition();
		ticketClose.setField(statusField);
		ticketClose.setOperator(LookupOperator.LOOKUP);
		ticketClose.setCriteriaValue(getCloseStatusCriteria());

		Criteria criteria = new Criteria();
		criteria.addAndCondition(ticketClose);
		return criteria;
	}

	private static FacilioView getAllResolvedWorkOrders() {

		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView resolvedTicketsView = new FacilioView();
		resolvedTicketsView.setName("resolved");
		resolvedTicketsView.setDisplayName("Resolved");
		resolvedTicketsView.setCriteria(getTicketStatusCriteria("Resolved"));
		resolvedTicketsView.setSortFields(sortFields);

		return resolvedTicketsView;
	}

	private static Criteria getTicketStatusCriteria(String status) {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("status");
		statusTypeField.setColumnName("STATUS");
		statusTypeField.setDataType(FieldType.STRING);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusClose = new Condition();
		statusClose.setField(statusTypeField);
		statusClose.setOperator(StringOperators.IS);
		statusClose.setValue(status);

		Criteria statusCriteria = new Criteria();
		statusCriteria.addAndCondition(statusClose);

		LookupField statusField = new LookupField();
		statusField.setName("status");
		statusField.setColumnName("STATUS_ID");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(ModuleFactory.getTicketsModule());
//		statusField.setExtendedModule(ModuleFactory.getTicketsModule());
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition ticketClose = new Condition();
		ticketClose.setField(statusField);
		ticketClose.setOperator(LookupOperator.LOOKUP);
		ticketClose.setCriteriaValue(statusCriteria);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(ticketClose);
		return criteria;
	}

	private static FacilioView getMyOpenWorkOrders() {

		Criteria criteria = new Criteria();
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		criteria.addAndCondition(getOpenStatusCondition());
		criteria.addAndCondition(getMyUserCondition());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("myopen");
		openTicketsView.setDisplayName("My Work Orders");
		openTicketsView.setCriteria(criteria);
		openTicketsView.setSortFields(sortFields);

		return openTicketsView;
	}

	private static FacilioView getAllDueTodayWorkOrders() {
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();

		Criteria criteria = getDueTodayCriteria();
		criteria.addAndCondition(getOpenStatusCondition());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView overdueView = new FacilioView();
		overdueView.setName("duetoday");
		overdueView.setDisplayName("Due Today");
		overdueView.setCriteria(criteria);
		overdueView.setSortFields(sortFields);

		return overdueView;
	}

	private static Criteria getDueTodayCriteria() {
		FacilioModule ticketsModule = ModuleFactory.getTicketsModule();
		FacilioField dueField = new FacilioField();
		dueField.setName("dueDate");
		dueField.setColumnName("DUE_DATE");
		dueField.setDataType(FieldType.DATE_TIME);
		dueField.setModule(ticketsModule);

		Condition overdue = new Condition();
		overdue.setField(dueField);
		overdue.setOperator(DateOperators.TODAY);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(overdue);

		return criteria;
	}

	private static FacilioView getMyTeamOpenWorkOrders() {

		Criteria criteria = new Criteria();
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		criteria.addAndCondition(getOpenStatusCondition());
		criteria.addAndCondition(getMyTeamCondition());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("myteamopen");
		openTicketsView.setDisplayName("My Team Work Orders");
		openTicketsView.setCriteria(criteria);
		openTicketsView.setSortFields(sortFields);

		return openTicketsView;
	}

	private static FacilioView getOpenPlannedWorkOrders() {
		FacilioModule ticketsModule = ModuleFactory.getTicketsModule();
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		
		FacilioField sourceType = new FacilioField();
		sourceType.setName("sourceType");
		sourceType.setColumnName("SOURCE_TYPE");
		sourceType.setDataType(FieldType.NUMBER);
		sourceType.setModule(ticketsModule);

		Condition sourceTypeCondition = new Condition();
		sourceTypeCondition.setField(sourceType);
		sourceTypeCondition.setOperator(NumberOperators.EQUALS);
		sourceTypeCondition.setValue(String.valueOf(TicketContext.SourceType.PREVENTIVE_MAINTENANCE.getIntVal()));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(sourceTypeCondition);
		criteria.addAndCondition(getOpenStatusCondition());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView overdueView = new FacilioView();
		overdueView.setName("planned");
		overdueView.setDisplayName("Planned");
		overdueView.setCriteria(criteria);
		overdueView.setSortFields(sortFields);

		return overdueView;
	}

	private static FacilioView getOpenUnPlannedWorkOrders() {
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		FacilioModule ticketsModule = ModuleFactory.getTicketsModule();

		FacilioField sourceType = new FacilioField();
		sourceType.setName("sourceType");
		sourceType.setColumnName("SOURCE_TYPE");
		sourceType.setDataType(FieldType.NUMBER);
		sourceType.setModule(ticketsModule);
//		sourceType.setExtendedModule(ModuleFactory.getTicketsModule());

		Condition sourceTypeCondition = new Condition();
		sourceTypeCondition.setField(sourceType);
		sourceTypeCondition.setOperator(NumberOperators.NOT_EQUALS);
		sourceTypeCondition.setValue(String.valueOf(TicketContext.SourceType.PREVENTIVE_MAINTENANCE.getIntVal()));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(sourceTypeCondition);
		criteria.addAndCondition(getOpenStatusCondition());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView overdueView = new FacilioView();
		overdueView.setName("unplanned");
		overdueView.setDisplayName("Un Planned");
		overdueView.setCriteria(criteria);
		overdueView.setSortFields(sortFields);

		return overdueView;
	}

	private static FacilioView getAllOverdueWorkOrders() {

		FacilioView overdueView = new FacilioView();
		overdueView.setName("overdue");
		overdueView.setDisplayName("Overdue");
		Criteria criteria = getOverdueCriteria();
		criteria.addAndCondition(getOpenStatusCondition());
		overdueView.setCriteria(criteria);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		SortField sortField = new SortField(createdTime, false);
		overdueView.setSortFields(Arrays.asList(sortField));

		return overdueView;
	}

	private static FacilioView getMyDueTodayWorkOrders() {

		FacilioField dueField = new FacilioField();
		dueField.setName("dueDate");
		dueField.setColumnName("DUE_DATE");
		dueField.setDataType(FieldType.DATE_TIME);
		dueField.setModule(ModuleFactory.getTicketsModule());
//		dueField.setExtendedModule(ModuleFactory.getTicketsModule());

		Condition overdue = new Condition();
		overdue.setField(dueField);
		overdue.setOperator(DateOperators.TODAY);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(overdue);
		criteria.addAndCondition(getMyUserCondition());
		criteria.addAndCondition(getOpenStatusCondition());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView view = new FacilioView();
		view.setName("myduetoday");
		view.setDisplayName("My Due Today");
		view.setCriteria(criteria);
		view.setSortFields(sortFields);

		return view;
	}

	private static FacilioView getMyOverdueWorkOrders() {
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		Criteria criteria = getOverdueCriteria();
		criteria.addAndCondition(getOpenStatusCondition());
		criteria.addAndCondition(getMyUserCondition());

		FacilioView view = new FacilioView();
		view.setName("myoverdue");
		view.setDisplayName("My Overdue");
		view.setCriteria(criteria);
		view.setSortFields(sortFields);

		return view;
	}

	private static Criteria getOverdueCriteria() {
		FacilioField dueField = new FacilioField();
		dueField.setName("dueDate");
		dueField.setColumnName("DUE_DATE");
		dueField.setDataType(FieldType.DATE_TIME);
		dueField.setModule(ModuleFactory.getTicketsModule());

		Condition overdue = new Condition();
		overdue.setField(dueField);
		overdue.setOperator(DateOperators.TILL_NOW);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(overdue);

		return criteria;
	}

	private static FacilioView getMyWorkOrders() {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getMyUserCondition());

		FacilioView myWorkOrdersView = new FacilioView();
		myWorkOrdersView.setName("my");
		myWorkOrdersView.setDisplayName("My Work Orders");
		myWorkOrdersView.setCriteria(criteria);
		return myWorkOrdersView;
	}

	private static FacilioView getMyTasks() {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getMyUserCondition());

		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("mytasks");
		openTicketsView.setDisplayName("My Tasks");
		openTicketsView.setCriteria(criteria);
		return openTicketsView;
	}

	public static Condition getMyUserCondition() {
		LookupField userField = new LookupField();
		userField.setName("assignedTo");
		userField.setColumnName("ASSIGNED_TO_ID");
		userField.setDataType(FieldType.LOOKUP);
		userField.setModule(ModuleFactory.getTicketsModule());
		userField.setSpecialType(FacilioConstants.ContextNames.USERS);

		Condition myUserCondition = new Condition();
		myUserCondition.setField(userField);
		myUserCondition.setOperator(PickListOperators.IS);
		myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

		return myUserCondition;
	}

	private static Condition getMyTeamCondition() {
		LookupField groupField = new LookupField();
		groupField.setName("assignmentGroup");
		groupField.setColumnName("ASSIGNMENT_GROUP_ID");
		groupField.setDataType(FieldType.LOOKUP);
		groupField.setModule(ModuleFactory.getTicketsModule());
		groupField.setSpecialType(FacilioConstants.ContextNames.GROUPS);

		/*
		 * String groupIds = ""; try { List<Group> myGroups =
		 * AccountUtil.getGroupBean().getMyGroups(AccountUtil.getCurrentUser().
		 * getId()); if (myGroups != null) { for (int i=0; i< myGroups.size();
		 * i++) { if (i != 0) { groupIds += ","; } groupIds +=
		 * myGroups.get(i).getId(); } } } catch (Exception e) {
		 * e.printStackTrace(); } if (groupIds.equals("")) { groupIds = "-100";
		 * }
		 */

		Condition myTeamCondition = new Condition();
		myTeamCondition.setField(groupField);
		myTeamCondition.setOperator(PickListOperators.IS);
		myTeamCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER_GROUP);

		return myTeamCondition;
	}

	private static Criteria getFireSafetyCategoryCriteria() {
		FacilioField categoryNameField = new FacilioField();
		categoryNameField.setName("name");
		categoryNameField.setColumnName("NAME");
		categoryNameField.setDataType(FieldType.STRING);
		categoryNameField.setModule(ModuleFactory.getTicketCategoryModule());

		Condition fireSafety = new Condition();
		fireSafety.setField(categoryNameField);
		fireSafety.setOperator(StringOperators.IS);
		fireSafety.setValue("Fire Safety");

		Criteria criteria = new Criteria();
		criteria.addAndCondition(fireSafety);

		return criteria;
	}

	private static FacilioView getFireSafetyWOs() {
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		FacilioModule ticketsModule = ModuleFactory.getTicketsModule();

		LookupField categoryField = new LookupField();
		categoryField.setName("category");
		categoryField.setColumnName("CATEGORY_ID");
		categoryField.setDataType(FieldType.LOOKUP);
		categoryField.setModule(ticketsModule);
		categoryField.setLookupModule(ModuleFactory.getTicketCategoryModule());

		Condition fireSafetyCategory = new Condition();
		fireSafetyCategory.setField(categoryField);
		fireSafetyCategory.setOperator(LookupOperator.LOOKUP);
		fireSafetyCategory.setCriteriaValue(getFireSafetyCategoryCriteria());

		FacilioField sourceField = new FacilioField();
		sourceField.setName("sourceType");
		sourceField.setColumnName("SOURCE_TYPE");
		sourceField.setDataType(FieldType.NUMBER);
		sourceField.setModule(ticketsModule);

		Condition alarmSourceCondition = new Condition();
		alarmSourceCondition.setField(sourceField);
		alarmSourceCondition.setOperator(NumberOperators.EQUALS);
		alarmSourceCondition.setValue(String.valueOf(TicketContext.SourceType.ALARM.getIntVal()));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(fireSafetyCategory);
		criteria.addAndCondition(getOpenStatusCondition());
		criteria.addAndCondition(alarmSourceCondition);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView fireSafetyWOView = new FacilioView();
		fireSafetyWOView.setName("openfirealarms");
		fireSafetyWOView.setDisplayName("Fire Alarm Workorders");
		fireSafetyWOView.setCriteria(criteria);
		fireSafetyWOView.setSortFields(sortFields);

		return fireSafetyWOView;
	}

	private static FacilioView getSeverityAlarms(String name, String displayName, String severity, boolean equals) {

		Condition alarmCondition = getAlarmSeverityCondition(severity, equals);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(alarmCondition);
		criteria.andCriteria(getCommonAlarmCriteria());

		FacilioField modifiedTime = new FacilioField();
		modifiedTime.setColumnName("MODIFIED_TIME");
		modifiedTime.setName("modifiedTime");
		modifiedTime.setDataType(FieldType.DATE_TIME);
		modifiedTime.setModule(ModuleFactory.getAlarmsModule());

		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setCriteria(criteria);
		view.setSortFields(Arrays.asList(new SortField(modifiedTime, false)));

		return view;
	}

	
	public static Condition getAlarmSeverityCondition(String severity, boolean equals) {
		LookupField severityField = new LookupField();
		severityField.setName("severity");
		severityField.setColumnName("SEVERITY");
		severityField.setDataType(FieldType.LOOKUP);
		severityField.setModule(ModuleFactory.getAlarmsModule());
		severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());

		Condition alarmCondition = new Condition();
		alarmCondition.setField(severityField);
		alarmCondition.setOperator(LookupOperator.LOOKUP);
		alarmCondition.setCriteriaValue(getSeverityAlarmCriteria(severity, equals));

		return alarmCondition;
	}

	private static Criteria getSeverityAlarmCriteria(String severity, boolean equals) {
		FacilioField severityField = new FacilioField();
		severityField.setName("severity");
		severityField.setColumnName("SEVERITY");
		severityField.setDataType(FieldType.STRING);
		severityField.setModule(ModuleFactory.getAlarmSeverityModule());

		Condition activeAlarm = new Condition();
		activeAlarm.setField(severityField);
		if (equals) {
			activeAlarm.setOperator(StringOperators.IS);
		} else {
			activeAlarm.setOperator(StringOperators.ISN_T);
		}
		activeAlarm.setValue(severity);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(activeAlarm);

		return criteria;
	}

	private static FacilioView getSourceTypeAlarms(String name, String displayName,
			TicketContext.SourceType sourceType) {

		FacilioModule module = ModuleFactory.getAlarmsModule();

		Criteria criteria = getSourceTypeCriteria(sourceType, true);

		FacilioField modifiedTime = new FacilioField();
		modifiedTime.setColumnName("MODIFIED_TIME");
		modifiedTime.setName("modifiedTime");
		modifiedTime.setDataType(FieldType.DATE_TIME);
		modifiedTime.setModule(module);

		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setCriteria(criteria);
		view.setSortFields(Arrays.asList(new SortField(modifiedTime, false)));

		return view;
	}

	public static Criteria getCommonAlarmCriteria() {
		return getSourceTypeCriteria(SourceType.ANOMALY_ALARM, false);
	}

	private static Criteria getSourceTypeCriteria(TicketContext.SourceType sourceType, boolean isEqual) {
		FacilioModule module = ModuleFactory.getTicketsModule();

		FacilioField sourceField = new FacilioField();
		sourceField.setName("sourceType");
		sourceField.setColumnName("SOURCE_TYPE");
		sourceField.setDataType(FieldType.NUMBER);
		sourceField.setModule(module);

		Condition sourceCondition = new Condition();
		sourceCondition.setField(sourceField);
		sourceCondition.setOperator(isEqual ? NumberOperators.EQUALS : NumberOperators.NOT_EQUALS);
		sourceCondition.setValue(String.valueOf(sourceType.getIntVal()));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(sourceCondition);

		return criteria;
	}

	private static FacilioView getMyAlarms() {

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getMyUserCondition());

		FacilioView myAlarms = new FacilioView();
		myAlarms.setName("myalarms");
		myAlarms.setDisplayName("My Alarms");
		myAlarms.setCriteria(criteria);

		return myAlarms;
	}

	private static FacilioView getTypeAlarms(String name, String displayName, AlarmType type) {
		Condition condition = new Condition();
		condition.setColumnName("Alarms.ALARM_TYPE");
		condition.setFieldName("alarmType");
		condition.setOperator(NumberOperators.EQUALS);
		condition.setValue(String.valueOf(type.getIntVal()));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);
		criteria.andCriteria(getCommonAlarmCriteria());

		LookupField severityField = new LookupField();
		severityField.setName("severity");
		severityField.setColumnName("SEVERITY");
		severityField.setDataType(FieldType.LOOKUP);
		severityField.setModule(ModuleFactory.getAlarmsModule());
		severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());

		Condition activeAlarm = new Condition();
		activeAlarm.setField(severityField);
		activeAlarm.setOperator(LookupOperator.LOOKUP);
		activeAlarm.setCriteriaValue(getSeverityAlarmCriteria("Clear", false));

		criteria.addAndCondition(activeAlarm);

		FacilioField modifiedTime = new FacilioField();
		modifiedTime.setColumnName("MODIFIED_TIME");
		modifiedTime.setName("modifiedTime");
		modifiedTime.setDataType(FieldType.DATE_TIME);
		modifiedTime.setModule(ModuleFactory.getAlarmsModule());

		FacilioView typeAlarms = new FacilioView();
		typeAlarms.setName(name);
		typeAlarms.setDisplayName(displayName);
		typeAlarms.setCriteria(criteria);
		typeAlarms.setSortFields(Arrays.asList(new SortField(modifiedTime, false)));

		return typeAlarms;
	}

	private static FacilioView getUnacknowledgedAlarms() {
		Criteria criteria = getUnacknowledgedAlarmCriteria();

		FacilioField modifiedTime = new FacilioField();
		modifiedTime.setColumnName("MODIFIED_TIME");
		modifiedTime.setName("modifiedTime");
		modifiedTime.setDataType(FieldType.DATE_TIME);
		modifiedTime.setModule(ModuleFactory.getAlarmsModule());

		FacilioView typeAlarms = new FacilioView();
		typeAlarms.setName("unacknowledged");
		typeAlarms.setDisplayName("Unacknowledged");
		typeAlarms.setCriteria(criteria);
		typeAlarms.setSortFields(Arrays.asList(new SortField(modifiedTime, false)));

		return typeAlarms;
	}
	
	public static Criteria getUnacknowledgedAlarmCriteria() {
		Condition falseCondition = new Condition();
		falseCondition.setColumnName("Alarms.IS_ACKNOWLEDGED");
		falseCondition.setFieldName("isAcknowledged");
		falseCondition.setOperator(BooleanOperators.IS);
		falseCondition.setValue(String.valueOf(false));

		Condition emptyCondition = new Condition();
		emptyCondition.setColumnName("Alarms.IS_ACKNOWLEDGED");
		emptyCondition.setFieldName("isAcknowledged");
		emptyCondition.setOperator(CommonOperators.IS_EMPTY);

		Criteria criteria = new Criteria();
		criteria.addOrCondition(emptyCondition);
		criteria.addOrCondition(falseCondition);
		criteria.andCriteria(getCommonAlarmCriteria());

		LookupField severityField = new LookupField();
		severityField.setName("severity");
		severityField.setColumnName("SEVERITY");
		severityField.setDataType(FieldType.LOOKUP);
		severityField.setModule(ModuleFactory.getAlarmsModule());
		severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());

		Condition activeAlarm = new Condition();
		activeAlarm.setField(severityField);
		activeAlarm.setOperator(LookupOperator.LOOKUP);
		activeAlarm.setCriteriaValue(getSeverityAlarmCriteria("Clear", false));

		criteria.addAndCondition(activeAlarm);
		return criteria;
	}

	private static FacilioView getUnassignedAlarms() {

		LookupField userField = new LookupField();
		userField.setName("assignedTo");
		userField.setColumnName("ASSIGNED_TO_ID");
		userField.setDataType(FieldType.LOOKUP);
		userField.setModule(ModuleFactory.getTicketsModule());
		userField.setSpecialType(FacilioConstants.ContextNames.USERS);

		Condition userFieldCondition = new Condition();
		userFieldCondition.setField(userField);
		userFieldCondition.setOperator(CommonOperators.IS_EMPTY);

		Criteria unassignedWOCriteria = new Criteria();
		unassignedWOCriteria.addAndCondition(userFieldCondition);

		FacilioView unassignedWOView = new FacilioView();
		unassignedWOView.setName("unassigned");
		unassignedWOView.setDisplayName("Unassigned Alarms");
		unassignedWOView.setCriteria(unassignedWOCriteria);

		return unassignedWOView;
	}

	private static FacilioView getAllAlarms() {

		FacilioField modifiedTime = new FacilioField();
		modifiedTime.setColumnName("MODIFIED_TIME");
		modifiedTime.setName("modifiedTime");
		modifiedTime.setDataType(FieldType.DATE_TIME);
		modifiedTime.setModule(ModuleFactory.getAlarmsModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Alarms");
		allView.setSortFields(Arrays.asList(new SortField(modifiedTime, false)));

		return allView;
	}

	private static FacilioView getAllWorkOrders() {

		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Workorders");
		allView.setSortFields(sortFields);

		return allView;
	}

	private static FacilioView getAllWorkRequests() {

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrderRequestsModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Work Requests");
		allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		return allView;
	}

	private static FacilioView getAllPreventiveWorkOrders() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");

		return allView;
	}

	private static FacilioView getTypePreventiveWorkOrders(String name, String displayName, String type) {

		List<FacilioField> templateFields = FieldFactory.getWorkOrderTemplateFields();
		Map<String, FacilioField> fieldProps = FieldFactory.getAsMap(templateFields);
		LookupField typeIdField = (LookupField) fieldProps.get("typeId");

		FacilioModule typeModule = ModuleFactory.getTicketTypeModule();
		FacilioField nameField = FieldFactory.getField("name", "NAME", typeModule, FieldType.STRING);
		Condition nameCondition = CriteriaAPI.getCondition(nameField, type, StringOperators.IS);
		Criteria crit = new Criteria();
		crit.addAndCondition(nameCondition);

		Condition typeCondition = CriteriaAPI.getCondition(typeIdField, crit, LookupOperator.LOOKUP);
		Condition statusCondition = getPreventiveStatusCondition(true);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(typeCondition);
		criteria.addAndCondition(statusCondition);

		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setCriteria(criteria);

		return view;
	}

	private static FacilioView getStatusPreventiveWorkOrders(String name, String displayName, boolean status) {

		Condition statusCondition = getPreventiveStatusCondition(status);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusCondition);

		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setCriteria(criteria);

		return view;
	}

	public static Condition getPreventiveStatusCondition(boolean status) {
		List<FacilioField> preventiveFields = FieldFactory.getPreventiveMaintenanceFields();
		Map<String, FacilioField> fieldProps = FieldFactory.getAsMap(preventiveFields);
		FacilioField statusField = fieldProps.get("status");

		Condition statusCondition = CriteriaAPI.getCondition(statusField, String.valueOf(status), BooleanOperators.IS);

		return statusCondition;
	}

	// View for reports. Not shown in ui list
	private static FacilioView getReportView() {

		FacilioView reportView = new FacilioView();
		reportView.setName("report");
		reportView.setHidden(true);

		return reportView;
	}

	private static FacilioView getMyRequestWorkorders() {

		Criteria criteria = new Criteria();
		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
		criteria.addAndCondition(getMyRequestCondition());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView myAllWorkordersView = new FacilioView();
		myAllWorkordersView.setName("myall");
		myAllWorkordersView.setDisplayName("My Work Orders");
		myAllWorkordersView.setCriteria(criteria);
		myAllWorkordersView.setSortFields(sortFields);
		myAllWorkordersView.setHidden(true);

		return myAllWorkordersView;
	}

	public static Criteria getCriteriaForView(String viewName, FacilioModule module) {

		// For sub views, parent criteria can be taken from views. It is not
		// included here. eg: for closed view, closed criteria is not applied
		// for its subviews

		Criteria criteria = new Criteria();
		switch (viewName) {
		case "my":
			Condition myUser = getMyUserCondition();
			criteria.addAndCondition(myUser);
			break;

		case "myTeam":
			Condition myTeam = getMyTeamCondition();
			criteria.addAndCondition(myTeam);
			break;

		case "overdue":
			criteria = getOverdueCriteria();
			break;

		case "duetoday":
			criteria = getDueTodayCriteria();
			break;

		case "unassigned":
			criteria = getUnAssignedCriteria();
			break;

		case "rejected":
			criteria = getWorkRequestStatusCriteria(WorkOrderRequestContext.RequestStatus.REJECTED);
			break;

		case "open":
			if (module.getName().equals(FacilioConstants.ContextNames.WORK_ORDER_REQUEST)) {
				criteria = getWorkRequestStatusCriteria(WorkOrderRequestContext.RequestStatus.OPEN);
			}
			break;
		}
		return !criteria.isEmpty() ? criteria : null;
	}

	private static final Map<String, List<Map<String, Object>>> subViews = Collections.unmodifiableMap(initSubViews());

	private static Map<String, List<Map<String, Object>>> initSubViews() {
		Map<String, List<Map<String, Object>>> subViewsMap = new HashMap<String, List<Map<String, Object>>>();

		FacilioModule workorderModule = ModuleFactory.getWorkOrdersModule();

		// For sub views, parent criteria can be taken from views. It is not
		// included here. eg: for closed view, closed criteria is not applied
		// for its subviews

		// My Team SubViews
		List<Map<String, Object>> subViews = new ArrayList<>();
		// Map<String, Criteria> subViews = new LinkedHashMap<String,
		// Criteria>();
		Map<String, Object> subviewDetail = new HashMap<>();
		subviewDetail.put("name", "my");
		subviewDetail.put("displayName", "My Open");
		subviewDetail.put("criteria", getCriteriaForView("my", workorderModule));
		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "overdue");
		subviewDetail.put("displayName", "Overdue");
		subviewDetail.put("criteria", getCriteriaForView("overdue", workorderModule));
		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "unassigned");
		subviewDetail.put("displayName", "Unassigned");
		subviewDetail.put("criteria", getCriteriaForView("unassigned", workorderModule));
		subViews.add(subviewDetail);
		subViews.add(getAllSubViewDetail(workorderModule));

		subViewsMap.put("workorder-myteamopen", subViews);

		// My SubViews
		subViews = new ArrayList<>();

		subviewDetail = new HashMap<>();
		subViews.add(getAllSubViewDetail(workorderModule));
		subviewDetail.put("name", "overdue");
		subviewDetail.put("displayName", "Overdue");
		subviewDetail.put("criteria", getCriteriaForView("overdue", workorderModule));
		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "duetoday");
		subviewDetail.put("displayName", "Due Today");
		subviewDetail.put("criteria", getCriteriaForView("duetoday", workorderModule));
		subViews.add(subviewDetail);

		subViewsMap.put("workorder-myopen", subViews);

		// My SubViews
		subViews = new ArrayList<>();

		subviewDetail = new HashMap<>();
		subViews.add(getAllSubViewDetail(workorderModule));
		subviewDetail.put("name", "overdue");
		subviewDetail.put("displayName", "Overdue");
		subviewDetail.put("criteria", getCriteriaForView("overdue", workorderModule));
		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "duetoday");
		subviewDetail.put("displayName", "Due Today");
		subviewDetail.put("criteria", getCriteriaForView("duetoday", workorderModule));
		subViews.add(subviewDetail);

		subViewsMap.put("workorder-open", subViews);

		// Resolved
		subViews = new ArrayList<>();

		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "my");
		subviewDetail.put("displayName", "My Resolved");
		subviewDetail.put("criteria", getCriteriaForView("my", workorderModule));
		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "myteam");
		subviewDetail.put("displayName", "My Team Resolved");
		subviewDetail.put("criteria", getCriteriaForView("myteam", workorderModule));
		subViews.add(subviewDetail);
		subViews.add(getAllSubViewDetail(workorderModule));

		subViewsMap.put("workorder-resolved", subViews);

		// Closed
		subViews = new ArrayList<>();

		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "my");
		subviewDetail.put("displayName", "My Closed");
		subviewDetail.put("criteria", getCriteriaForView("my", workorderModule));
		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "myteam");
		subviewDetail.put("displayName", "My Team Closed");
		subviewDetail.put("criteria", getCriteriaForView("myteam", workorderModule));
		subViews.add(subviewDetail);
		subViews.add(getAllSubViewDetail(workorderModule));

		subViewsMap.put("workorder-closed", subViews);

		return subViewsMap;
	}

	public static List<Map<String, Object>> getSubViewsCriteria(String moduleName, String viewName) {
		String name = moduleName + "-" + viewName;
		if (!subViews.containsKey(name)) {
			return null;
		}
		return new ArrayList(subViews.get(name));
	}

	private static Map<String, Object> getAllSubViewDetail(FacilioModule module) {
		Map<String, Object> subviewDetail = new HashMap<>();
		subviewDetail.put("name", "all");
		subviewDetail.put("displayName", "All");
		subviewDetail.put("criteria", null);
		return subviewDetail;
	}

	private static FacilioView getAllInventory() {

		FacilioModule inventoryModule = ModuleFactory.getInventoryModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(inventoryModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Parts");
		allView.setSortFields(sortFields);

		return allView;
	}

	private static Criteria getStalePartsCriteria(FacilioModule module) {
		NumberField modifiedTime = new NumberField();
		modifiedTime.setName("modifiedTime");
		modifiedTime.setColumnName("MODIFIED_TIME");
		modifiedTime.setDataType(FieldType.NUMBER);
		modifiedTime.setModule(module);

		Long currTime = DateTimeUtil.getCurrenTime();
		Long twoMonthInMillis = 5184000000l;

		Condition staleParts = new Condition();
		staleParts.setField(modifiedTime);
		staleParts.setOperator(NumberOperators.LESS_THAN);
		staleParts.setValue(currTime - twoMonthInMillis + "");

		Criteria criteria = new Criteria();
		criteria.addAndCondition(staleParts);
		return criteria;
	}

	private static Criteria getUnderstockedPartCriteria(FacilioModule module) {
		FacilioField quantity = new NumberField();
		quantity.setName("quantity");
		quantity.setColumnName("QUANTITY");
		quantity.setDataType(FieldType.NUMBER);
		quantity.setModule(module);

		FacilioField minimumQuantity = new FacilioField();
		minimumQuantity.setName("minimumQuantity");
		minimumQuantity.setColumnName("MINIMUM_QUANTITY");
		minimumQuantity.setDataType(FieldType.NUMBER);
		minimumQuantity.setModule(module);

		Condition ticketClose = new Condition();
		ticketClose.setField(quantity);
		ticketClose.setOperator(NumberOperators.LESS_THAN);
		ticketClose.setValue("MINIMUM_QUANTITY");

		Criteria criteria = new Criteria();
		criteria.addAndCondition(ticketClose);
		return criteria;
	}
	
	private static Criteria getUnderstockedItemTypeCriteria(FacilioModule module) {
		FacilioField quantity = new NumberField();
		quantity.setName("currentQuantity");
		quantity.setColumnName("CURRENT_QUANTITY");
		quantity.setDataType(FieldType.DECIMAL);
		quantity.setModule(module);

		FacilioField minimumQuantity = new FacilioField();
		minimumQuantity.setName("minimumQuantity");
		minimumQuantity.setColumnName("MINIMUM_QUANTITY");
		minimumQuantity.setDataType(FieldType.NUMBER);
		minimumQuantity.setModule(module);

		Condition ticketClose = new Condition();
		ticketClose.setField(quantity);
		ticketClose.setOperator(NumberOperators.LESS_THAN);
		ticketClose.setValue("MINIMUM_QUANTITY");

		Criteria criteria = new Criteria();
		criteria.addAndCondition(ticketClose);
		return criteria;
	}
	private static Criteria getUnderstockedItemCriteria(FacilioModule module) {
		FacilioField quantity = new NumberField();
		quantity.setName("quantity");
		quantity.setColumnName("QUANTITY");
		quantity.setDataType(FieldType.DECIMAL);
		quantity.setModule(module);
		
		FacilioField minimumQuantity = new FacilioField();
		minimumQuantity.setName("minimumQuantity");
		minimumQuantity.setColumnName("MINIMUM_QUANTITY");
		minimumQuantity.setDataType(FieldType.DECIMAL);
		minimumQuantity.setModule(module);
		
		Condition ticketClose = new Condition();
		ticketClose.setField(quantity);
		ticketClose.setOperator(NumberOperators.LESS_THAN);
		ticketClose.setValue("MINIMUM_QUANTITY");
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(ticketClose);
		return criteria;
	}

	private static FacilioView getStalePartsView() {

		Criteria criteria = getStalePartsCriteria(ModuleFactory.getInventryModule());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getInventryModule());

		FacilioView staleParts = new FacilioView();
		staleParts.setName("stale");
		staleParts.setDisplayName("Stale");
		staleParts.setCriteria(criteria);
		staleParts.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return staleParts;
	}

	private static FacilioView getUnderStockedPartsView() {

		Criteria criteria = getUnderstockedPartCriteria(ModuleFactory.getInventoryModule());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getInventoryModule());

		FacilioView staleParts = new FacilioView();
		staleParts.setName("understocked");
		staleParts.setDisplayName("Under Stocked");
		staleParts.setCriteria(criteria);
		staleParts.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return staleParts;
	}
	
	private static FacilioView getUnderStockedItemTypeView() {

		Criteria criteria = getUnderstockedItemTypeCriteria(ModuleFactory.getItemTypesModule());

		FacilioModule itemsModule = ModuleFactory.getItemTypesModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(itemsModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView allView = new FacilioView();
		allView.setName("understocked");
		allView.setDisplayName("Understocked Items");
		allView.setSortFields(sortFields);
		
		allView.setCriteria(criteria);
		return allView;
	}
	private static FacilioView getUnderStockedItemView() {
		
		Criteria criteria = getUnderstockedItemCriteria(ModuleFactory.getInventryModule());
		
		FacilioModule itemsModule = ModuleFactory.getInventryModule();
		
		FacilioField createdTime = new LookupField();
		createdTime.setName("itemType");
		createdTime.setDataType(FieldType.LOOKUP);
		createdTime.setColumnName("ITEM_TYPES_ID");
		createdTime.setModule(ModuleFactory.getInventryModule());

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));
		
		FacilioView allView = new FacilioView();
		allView.setName("understocked");
		allView.setDisplayName("Understocked Items");
		allView.setSortFields(sortFields);
		
		allView.setCriteria(criteria);
		return allView;
	}
	
	private static FacilioView getAllStoreRooms() {

		FacilioModule storeRoomModule = ModuleFactory.getStoreRoomModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("name");
		createdTime.setDataType(FieldType.STRING);
		createdTime.setColumnName("NAME");
		createdTime.setModule(storeRoomModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Stores");
		allView.setSortFields(sortFields);

		return allView;
	}
	
	private static FacilioView getAllItemTypes() {

		FacilioModule itemsModule = ModuleFactory.getItemTypesModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("name");
		createdTime.setDataType(FieldType.STRING);
		createdTime.setColumnName("NAME");
		createdTime.setModule(itemsModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Item Types");
		allView.setSortFields(sortFields);

		return allView;
	}
	
	private static FacilioView getItemTypesForStatus(String viewName, String viewDisplayName, String status) {

		FacilioModule itemsModule = ModuleFactory.getItemTypesModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("name");
		createdTime.setDataType(FieldType.STRING);
		createdTime.setColumnName("NAME");
		createdTime.setModule(itemsModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

		Criteria criteria = getItemTypeStatusCriteria(itemsModule, status);
		
		FacilioView allView = new FacilioView();
		allView.setName(viewName);
		allView.setDisplayName(viewDisplayName);
		allView.setSortFields(sortFields);
		allView.setCriteria(criteria);

		return allView;
	}
	
	private static Criteria getItemTypeStatusCriteria(FacilioModule module, String status) {
		
		FacilioField itemStatusField = new FacilioField();
		itemStatusField.setName("name");
		itemStatusField.setColumnName("NAME");
		itemStatusField.setDataType(FieldType.STRING);
		itemStatusField.setModule(ModuleFactory.getItemTypeStatusModule());

		Condition statusCond = new Condition();
		statusCond.setField(itemStatusField);
		statusCond.setOperator(StringOperators.IS);
		statusCond.setValue(status);

		Criteria itemTypeStatusCriteria = new Criteria();
		itemTypeStatusCriteria.addAndCondition(statusCond);
		
		LookupField itemStatus = new LookupField();
		itemStatus.setName("status");
		itemStatus.setColumnName("STATUS");
		itemStatus.setDataType(FieldType.LOOKUP);
		itemStatus.setModule(module);
		itemStatus.setLookupModule(ModuleFactory.getItemTypeStatusModule());

		Condition statusFilter = new Condition();
		statusFilter.setField(itemStatus);
		statusFilter.setOperator(LookupOperator.LOOKUP);
		statusFilter.setCriteriaValue(itemTypeStatusCriteria);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusFilter);
		return criteria;
	}
	
	private static FacilioView getAllToolTypes() {

		FacilioModule itemsModule = ModuleFactory.getToolTypesModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("name");
		createdTime.setDataType(FieldType.STRING);
		createdTime.setColumnName("NAME");
		createdTime.setModule(itemsModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Tool Types");
		allView.setSortFields(sortFields);

		return allView;
	}
	
		private static FacilioView getAllVendors() {

		FacilioModule itemsModule = ModuleFactory.getVendorsModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(itemsModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Vendors");
		allView.setSortFields(sortFields);

		return allView;
	}
	
	private static FacilioView getAllInventry() {

		FacilioModule itemsModule = ModuleFactory.getInventryModule();

		FacilioField createdTime = new LookupField();
		createdTime.setName("itemType");
		createdTime.setDataType(FieldType.LOOKUP);
		createdTime.setColumnName("ITEM_TYPES_ID");
		createdTime.setModule(ModuleFactory.getInventryModule());

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Items");
		allView.setSortFields(sortFields);

		return allView;
	}
	
	private static FacilioView getAllTools() {

		FacilioModule toolmodule = ModuleFactory.getToolModule();

		FacilioField createdTime = new LookupField();
		createdTime.setName("toolType");
		createdTime.setDataType(FieldType.LOOKUP);
		createdTime.setColumnName("TOOL_TYPE_ID");
		createdTime.setModule(toolmodule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Tools");
		allView.setSortFields(sortFields);

		return allView;
	}
	
	public static List<SortField> getSortFields(String moduleName, FacilioModule...module) {
		List<SortField> fields = new ArrayList<>();
		switch (moduleName) {
		case FacilioConstants.ContextNames.ASSET:
			FacilioField localId = new FacilioField();
			localId.setName("localId");
			localId.setColumnName("LOCAL_ID");
			localId.setDataType(FieldType.NUMBER);
			localId.setModule(ModuleFactory.getAssetsModule());

			fields = Arrays.asList(new SortField(localId, false));
			break;
		default:
			if (module.length > 0) {
				FacilioField createdTime = new FacilioField();
				createdTime.setName("sysCreatedTime");
				createdTime.setDataType(FieldType.NUMBER);
				createdTime.setColumnName("CREATED_TIME");
				createdTime.setModule(module[0]);

				fields = Arrays.asList(new SortField(createdTime, false));
			}
			break;
		}
		return fields;
	}
	
	public static FacilioView getModuleView(FacilioModule childModule, String parentModuleName) {
		FacilioView moduleView = new FacilioView();
		moduleView.setName(childModule.getName());
		moduleView.setDisplayName("All " + childModule.getDisplayName() + "s");
		moduleView.setModuleId(childModule.getModuleId());
		moduleView.setModuleName(childModule.getName());
		moduleView.setDefault(true);
		moduleView.setOrder(1);
		
		moduleView.setFields(ColumnFactory.getColumns(parentModuleName, "default"));
		moduleView.setSortFields(getSortFields(parentModuleName));
		return moduleView;
	}
	
	public static Criteria getItemApprovalStateCriteria(ApprovalState status) {

		FacilioField field = new FacilioField();
		field.setName("approvedState");
		field.setColumnName("APPROVED_STATE");
		field.setDataType(FieldType.NUMBER);
		FacilioModule approvalStateModule = ModuleFactory.getItemTransactionsModule();
		field.setModule(approvalStateModule);

		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(NumberOperators.EQUALS);
		condition.setValue(String.valueOf(status.getValue()));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);

		return criteria;
	}
	
	private static FacilioView getItemPendingApproval() {

		Criteria criteria = getItemApprovalStateCriteria(ApprovalState.REQUESTED);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getItemTransactionsModule());

		FacilioView requestedItemApproval = new FacilioView();
		requestedItemApproval.setName("pendingitem");
		requestedItemApproval.setDisplayName("Pending Item Approvals");
		requestedItemApproval.setCriteria(criteria);
		requestedItemApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return requestedItemApproval;
	}
	
	private static FacilioView getAllItemApproval() {

		Criteria criteria = getAllItemApprovalStateCriteria();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getItemTransactionsModule());

		FacilioView rejectedApproval = new FacilioView();
		rejectedApproval.setName("allitem");
		rejectedApproval.setDisplayName("All Item Approvals");
		rejectedApproval.setCriteria(criteria);
		rejectedApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return rejectedApproval;
	}
	
	private static FacilioView getAllPurchasedItem() {
		FacilioModule purchasedItemModule = ModuleFactory.getPurchasedItemModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(purchasedItemModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

		FacilioView allView = new FacilioView();
		allView.setName("allpuritem");
		allView.setDisplayName("All Purchased Items");
		allView.setSortFields(sortFields);

		return allView;
	}
	
	private static FacilioView getAllGatePass() {
		FacilioModule purchasedItemModule = ModuleFactory.getGatePassModule();
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("issuedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("ISSUED_TIME");
		createdTime.setModule(purchasedItemModule);
		
		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));
		
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Gate Pass");
		allView.setSortFields(sortFields);
		
		return allView;
	}
	
	public static Criteria getAllToolApprovalStateCriteria() {
		FacilioField field = new FacilioField();
		field.setName("approvedState");
		field.setColumnName("APPROVED_STATE");
		field.setDataType(FieldType.NUMBER);
		FacilioModule approvalStateModule = ModuleFactory.getToolTransactionsModule();
		field.setModule(approvalStateModule);

		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(NumberOperators.EQUALS);
		List<String> list = Arrays.asList(String.valueOf(ApprovalState.REQUESTED.getValue()),
				String.valueOf(ApprovalState.REJECTED.getValue()), String.valueOf(ApprovalState.APPROVED.getValue()));
		condition.setValue(String.join(", ", list));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);

		return criteria;
	}
	
	
	public static Criteria getToolApprovalStateCriteria(ApprovalState status) {

		FacilioField field = new FacilioField();
		field.setName("approvedState");
		field.setColumnName("APPROVED_STATE");
		field.setDataType(FieldType.NUMBER);
		FacilioModule approvalStateModule = ModuleFactory.getToolTransactionsModule();
		field.setModule(approvalStateModule);

		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(NumberOperators.EQUALS);
		condition.setValue(String.valueOf(status.getValue()));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);

		return criteria;
	}
	
	private static FacilioView getToolPendingApproval() {

		Criteria criteria = getToolApprovalStateCriteria(ApprovalState.REQUESTED);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getToolTransactionsModule());

		FacilioView requestedItemApproval = new FacilioView();
		requestedItemApproval.setName("pendingtool");
		requestedItemApproval.setDisplayName("Pending Tool Approvals");
		requestedItemApproval.setCriteria(criteria);
		requestedItemApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return requestedItemApproval;
	}
	
	private static FacilioView getAllToolApproval() {

		Criteria criteria = getAllToolApprovalStateCriteria();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getToolTransactionsModule());

		FacilioView rejectedApproval = new FacilioView();
		rejectedApproval.setName("alltool");
		rejectedApproval.setDisplayName("All Tool Approvals");
		rejectedApproval.setCriteria(criteria);
		rejectedApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return rejectedApproval;
	}
	
	public static Criteria getAllItemApprovalStateCriteria() {
		FacilioField field = new FacilioField();
		field.setName("approvedState");
		field.setColumnName("APPROVED_STATE");
		field.setDataType(FieldType.NUMBER);
		FacilioModule approvalStateModule = ModuleFactory.getItemTransactionsModule();
		field.setModule(approvalStateModule);

		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(NumberOperators.EQUALS);
		List<String> list = Arrays.asList(String.valueOf(ApprovalState.REQUESTED.getValue()),
				String.valueOf(ApprovalState.REJECTED.getValue()), String.valueOf(ApprovalState.APPROVED.getValue()));
		condition.setValue(String.join(", ", list));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);

		return criteria;
	}
	
	private static FacilioView getAllPurchaseRequestView() {
		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getPurchaseRequestModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));
		return allView;
	}
	
	private static FacilioView getPurchaseRequestForStatus(String viewName, String viewDisplayName, int status) {
		FacilioModule prModule = ModuleFactory.getPurchaseRequestModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(prModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		Criteria criteria = getPurchaseRequestStatusCriteria(prModule, status);
		
		FacilioView statusView = new FacilioView();
		statusView.setName(viewName);
		statusView.setDisplayName(viewDisplayName);
		statusView.setSortFields(sortFields);
		statusView.setCriteria(criteria);

		return statusView;
	}
	
	private static Criteria getPurchaseRequestStatusCriteria(FacilioModule module, int status) {
		
		FacilioField prStatusField = new FacilioField();
		prStatusField.setName("status");
		prStatusField.setColumnName("STATUS");
		prStatusField.setDataType(FieldType.NUMBER);
		prStatusField.setModule(ModuleFactory.getPurchaseRequestModule());
		
		Condition statusCond = new Condition();
		statusCond.setField(prStatusField);
		statusCond.setOperator(NumberOperators.EQUALS);
		statusCond.setValue(String.valueOf(status));

		Criteria purchaseRequestStatusCriteria = new Criteria();
		purchaseRequestStatusCriteria.addAndCondition(statusCond);
		return purchaseRequestStatusCriteria;
	}
	
	private static FacilioView getOpenPurchaseRequest() {
		FacilioModule prModule = ModuleFactory.getPurchaseRequestModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(prModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		Criteria criteria = getOpenPurchaseRequestCriteria(prModule);
		
		FacilioView statusView = new FacilioView();
		statusView.setName("open");
		statusView.setDisplayName("Open");
		statusView.setSortFields(sortFields);
		statusView.setCriteria(criteria);

		return statusView;
	}
	
	private static Criteria getOpenPurchaseRequestCriteria(FacilioModule module) {
		
		FacilioField prStatusField = new FacilioField();
		prStatusField.setName("status");
		prStatusField.setColumnName("STATUS");
		prStatusField.setDataType(FieldType.NUMBER);
		prStatusField.setModule(ModuleFactory.getPurchaseRequestModule());
		
		Condition statusCond = new Condition();
		statusCond.setField(prStatusField);
		statusCond.setOperator(NumberOperators.LESS_THAN);
		statusCond.setValue(String.valueOf(3+""));

		Criteria purchaseRequestStatusCriteria = new Criteria();
		purchaseRequestStatusCriteria.addAndCondition(statusCond);
		return purchaseRequestStatusCriteria;
	}
	
	private static FacilioView getOverDuePurchaseRequest() {
		Criteria criteria = getOverDuePurchaseRequestCriteria(ModuleFactory.getPurchaseRequestModule());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(ModuleFactory.getPurchaseRequestModule());

		FacilioView overDueRequest = new FacilioView();
		overDueRequest.setName("overdue");
		overDueRequest.setDisplayName("Overdue");
		overDueRequest.setCriteria(criteria);
		overDueRequest.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return overDueRequest;
		
	}
	
	private static Criteria getOverDuePurchaseRequestCriteria (FacilioModule module) {
		NumberField requiredTime = new NumberField();
		requiredTime.setName("requiredTime");
		requiredTime.setColumnName("REQUIRED_TIME");
		requiredTime.setDataType(FieldType.NUMBER);
		requiredTime.setModule(module);

		Long currTime = DateTimeUtil.getCurrenTime();

		Condition overDueRequest = new Condition();
		overDueRequest.setField(requiredTime);
		overDueRequest.setOperator(NumberOperators.LESS_THAN);
		overDueRequest.setValue(currTime + "");
		
		FacilioField prStatusField = new FacilioField();
		prStatusField.setName("status");
		prStatusField.setColumnName("STATUS");
		prStatusField.setDataType(FieldType.NUMBER);
		prStatusField.setModule(ModuleFactory.getPurchaseRequestModule());
		
		Condition statusCond = new Condition();
		statusCond.setField(prStatusField);
		statusCond.setOperator(NumberOperators.LESS_THAN);
		statusCond.setValue(String.valueOf(3));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(overDueRequest);
		criteria.addAndCondition(statusCond);
		return criteria;
	}
	
	private static FacilioView getAllPurchaseOrderView() {
		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getPurchaseOrderModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));
		return allView;
	}
	
	private static FacilioView getPurchaseOrderForStatus(String viewName, String viewDisplayName, int status) {
		FacilioModule poModule = ModuleFactory.getPurchaseOrderModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(poModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		Criteria criteria = getPurchaseOrderStatusCriteria(poModule, status);
		
		FacilioView statusView = new FacilioView();
		statusView.setName(viewName);
		statusView.setDisplayName(viewDisplayName);
		statusView.setSortFields(sortFields);
		statusView.setCriteria(criteria);

		return statusView;
	}
	
	private static Criteria getPurchaseOrderStatusCriteria(FacilioModule module, int status) {
		
		FacilioField poStatusField = new FacilioField();
		poStatusField.setName("status");
		poStatusField.setColumnName("STATUS");
		poStatusField.setDataType(FieldType.NUMBER);
		poStatusField.setModule(ModuleFactory.getPurchaseOrderModule());
		
		Condition statusCond = new Condition();
		statusCond.setField(poStatusField);
		statusCond.setOperator(NumberOperators.EQUALS);
		statusCond.setValue(String.valueOf(status));

		Criteria purchaseOrderStatusCriteria = new Criteria();
		purchaseOrderStatusCriteria.addAndCondition(statusCond);
		return purchaseOrderStatusCriteria;
	}
	
	private static FacilioView getOpenPurchaseOrder() {
		FacilioModule poModule = ModuleFactory.getPurchaseOrderModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(poModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		Criteria criteria = getOpenPurchaseOrderCriteria(poModule);
		
		FacilioView statusView = new FacilioView();
		statusView.setName("open");
		statusView.setDisplayName("Open");
		statusView.setSortFields(sortFields);
		statusView.setCriteria(criteria);

		return statusView;
	}
	
	private static Criteria getOpenPurchaseOrderCriteria(FacilioModule module) {
		
		FacilioField poStatusField = new FacilioField();
		poStatusField.setName("status");
		poStatusField.setColumnName("STATUS");
		poStatusField.setDataType(FieldType.NUMBER);
		poStatusField.setModule(ModuleFactory.getPurchaseOrderModule());
		
		Condition statusCond = new Condition();
		statusCond.setField(poStatusField);
		statusCond.setOperator(NumberOperators.LESS_THAN);
		statusCond.setValue(String.valueOf(7+""));
		

		Condition rejCond = new Condition();
		rejCond.setField(poStatusField);
		rejCond.setOperator(NumberOperators.NOT_EQUALS);
		rejCond.setValue(String.valueOf(3+""));
		
		Criteria purchaseOrderStatusCriteria = new Criteria();
		purchaseOrderStatusCriteria.addAndCondition(statusCond);
		purchaseOrderStatusCriteria.addAndCondition(rejCond);
		return purchaseOrderStatusCriteria;
	}
	
	private static FacilioView getOverDuePurchaseOrder() {
		Criteria criteria = getOverDuePurchaseOrderCriteria(ModuleFactory.getPurchaseOrderModule());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(ModuleFactory.getPurchaseOrderModule());

		FacilioView overDueOrder = new FacilioView();
		overDueOrder.setName("overdue");
		overDueOrder.setDisplayName("Overdue");
		overDueOrder.setCriteria(criteria);
		overDueOrder.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return overDueOrder;
		
	}
	private static FacilioView getOnGoingPurchaseOrder() {
		Criteria criteria = getOngoingPurchaseOrderCriteria(ModuleFactory.getPurchaseOrderModule());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("orderedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("ORDERED_TIME");
		createdTime.setModule(ModuleFactory.getPurchaseOrderModule());

		FacilioView ongoingPurchaseOrder = new FacilioView();
		ongoingPurchaseOrder.setName("ongoing");
		ongoingPurchaseOrder.setDisplayName("Ongoing");
		ongoingPurchaseOrder.setCriteria(criteria);
		ongoingPurchaseOrder.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return ongoingPurchaseOrder;
		
	}
	
	private static Criteria getOverDuePurchaseOrderCriteria (FacilioModule module) {
		NumberField requiredTime = new NumberField();
		requiredTime.setName("requiredTime");
		requiredTime.setColumnName("REQUIRED_TIME");
		requiredTime.setDataType(FieldType.NUMBER);
		requiredTime.setModule(module);

		Long currTime = DateTimeUtil.getCurrenTime();

		Condition overDueOrder = new Condition();
		overDueOrder.setField(requiredTime);
		overDueOrder.setOperator(NumberOperators.LESS_THAN);
		overDueOrder.setValue(currTime + "");
		
		FacilioField poStatusField = new FacilioField();
		poStatusField.setName("status");
		poStatusField.setColumnName("STATUS");
		poStatusField.setDataType(FieldType.NUMBER);
		poStatusField.setModule(ModuleFactory.getPurchaseOrderModule());
		
		Condition statusCond = new Condition();
		statusCond.setField(poStatusField);
		statusCond.setOperator(NumberOperators.LESS_THAN);
		statusCond.setValue(String.valueOf(7+""));

		Condition rejCond = new Condition();
		rejCond.setField(poStatusField);
		rejCond.setOperator(NumberOperators.NOT_EQUALS);
		rejCond.setValue(String.valueOf(3+""));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(overDueOrder);
		criteria.addAndCondition(statusCond);
		criteria.addAndCondition(rejCond);
		return criteria;
	}

	
	private static Criteria getOngoingPurchaseOrderCriteria (FacilioModule module) {
		NumberField requestedTime = new NumberField();
		requestedTime.setName("orderedTime");
		requestedTime.setColumnName("ORDERED_TIME");
		requestedTime.setDataType(FieldType.NUMBER);
		requestedTime.setModule(module);

		Condition requestedTimeCond = new Condition();
		requestedTimeCond.setField(requestedTime);
		requestedTimeCond.setOperator(DateOperators.CURRENT_MONTH);
		
		FacilioField poStatusField = new FacilioField();
		poStatusField.setName("status");
		poStatusField.setColumnName("STATUS");
		poStatusField.setDataType(FieldType.NUMBER);
		poStatusField.setModule(ModuleFactory.getPurchaseOrderModule());
		
		Condition statusCond = new Condition();
		statusCond.setField(poStatusField);
		statusCond.setOperator(NumberOperators.EQUALS);
		statusCond.setValue(String.valueOf("2,5"));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(requestedTimeCond);
		criteria.addAndCondition(statusCond);
		return criteria;
	}


	private static FacilioView getAllReceivableView() {
		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getReceivableModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));
		return allView;
	}


	
	private static FacilioView getReceivableForStatus(String viewName, String viewDisplayName, int status) {
		FacilioModule receivableModule = ModuleFactory.getReceivableModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(receivableModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		Criteria criteria = getReceivableStatusCriteria(receivableModule, status);
		
		FacilioView statusView = new FacilioView();
		statusView.setName(viewName);
		statusView.setDisplayName(viewDisplayName);
		statusView.setSortFields(sortFields);
		statusView.setCriteria(criteria);

		return statusView;
	}
	
	private static Criteria getReceivableStatusCriteria(FacilioModule module, int status) {
		
		FacilioField receivableStatusField = new FacilioField();
		receivableStatusField.setName("status");
		receivableStatusField.setColumnName("STATUS");
		receivableStatusField.setDataType(FieldType.NUMBER);
		receivableStatusField.setModule(ModuleFactory.getReceivableModule());
		
		Condition statusCond = new Condition();
		statusCond.setField(receivableStatusField);
		statusCond.setOperator(NumberOperators.EQUALS);
		statusCond.setValue(String.valueOf(status));

		Criteria receivableStatusCriteria = new Criteria();
		receivableStatusCriteria.addAndCondition(statusCond);
		return receivableStatusCriteria;
	}
	
	private static Criteria getPurchaseContractStatusCriteria(FacilioModule module, int status) {
		
		FacilioField receivableStatusField = new FacilioField();
		receivableStatusField.setName("status");
		receivableStatusField.setColumnName("STATUS");
		receivableStatusField.setDataType(FieldType.NUMBER);
		receivableStatusField.setModule(module);
		
		Condition statusCond = new Condition();
		statusCond.setField(receivableStatusField);
		statusCond.setOperator(NumberOperators.EQUALS);
		statusCond.setValue(String.valueOf(status));

		Criteria receivableStatusCriteria = new Criteria();
		receivableStatusCriteria.addAndCondition(statusCond);
		return receivableStatusCriteria;
	}
	
	
	private static FacilioView getExpiringContractView(FacilioModule module, int type) {
		FacilioField endDateField = new FacilioField();
		endDateField.setName("endDate");
		endDateField.setColumnName("END_DATE");
		endDateField.setDataType(FieldType.DATE);
		endDateField.setModule(module);
		
		FacilioView allView = new FacilioView();
		allView.setName("expiring");
		allView.setDisplayName("Expiring This Month");
		allView.setCriteria(getExpiringContractListCriteria(module, type));
		allView.setSortFields(Arrays.asList(new SortField(endDateField, false)));
		return allView;
	}

	private static FacilioView getAllPurchaseContractView() {
		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getPurchaseContractModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		allView.setCriteria(getContractListCriteria());
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));
		return allView;
	}

	private static FacilioView getAllLabourContractView() {
		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getLabourContractModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		allView.setCriteria(getContractListCriteria());
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));
		return allView;
	}
	
	private static FacilioView getAllRentalLeaseContractView() {
		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getRentalLeaseContractModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		allView.setCriteria(getContractListCriteria());
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));
		return allView;
	}
	
	private static FacilioView getAllWarrantyContractView() {
		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getWarrantyContractModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		allView.setCriteria(getContractListCriteria());
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));
		return allView;
	}

	private static FacilioView getAllPoLineItemsSerialNumeberView() {
		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(ModuleFactory.getPoLineItemsSerialNumberModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Serial Numbers");
		allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return allView;
	}
	
	private static FacilioView getAllShipmentView() {
		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getShipmentModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));
		return allView;
	}
	
	
	private static FacilioView getShipmentForStatus(String viewName, String viewDisplayName, int status) {
		FacilioModule shipmentModule = ModuleFactory.getShipmentModule();

		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getShipmentModule());

		List<SortField> sortFields = Arrays.asList(new SortField(localId, false));

		Criteria criteria = getShipmentStatusCriteria(shipmentModule, status);
		
		FacilioView statusView = new FacilioView();
		statusView.setName(viewName);
		statusView.setDisplayName(viewDisplayName);
		statusView.setSortFields(sortFields);
		statusView.setCriteria(criteria);

		return statusView;
	}
	
	private static Criteria getShipmentStatusCriteria(FacilioModule module, int status) {
		
		FacilioField shipmentStatusField = new FacilioField();
		shipmentStatusField.setName("status");
		shipmentStatusField.setColumnName("STATUS");
		shipmentStatusField.setDataType(FieldType.NUMBER);
		shipmentStatusField.setModule(ModuleFactory.getShipmentModule());
		
		Condition statusCond = new Condition();
		statusCond.setField(shipmentStatusField);
		statusCond.setOperator(NumberOperators.EQUALS);
		statusCond.setValue(String.valueOf(status));

		Criteria shipmentStatusCriteria = new Criteria();
		shipmentStatusCriteria.addAndCondition(statusCond);
		return shipmentStatusCriteria;
	}
	

	private static FacilioView getAllInventoryRequestView() {
		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getInventoryRequestModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));
		return allView;
	}
	private static FacilioView getInventoryRequestForStatus(String viewName, String viewDisplayName, int status) {
		FacilioModule irModule = ModuleFactory.getInventoryRequestModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("localId");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("LOCAL_ID");
		createdTime.setModule(irModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		Criteria criteria = getInventoryRequestStatusCriteria(irModule, status);

		FacilioView statusView = new FacilioView();
		statusView.setName(viewName);
		statusView.setDisplayName(viewDisplayName);
		statusView.setSortFields(sortFields);
		statusView.setCriteria(criteria);
		List<ViewField> fields = new ArrayList<ViewField>();
		fields.add(new ViewField("id", "ID"));
		fields.add(new ViewField("name", "Name"));
		fields.add(new ViewField("requestedBy", "Requested By"));
		fields.add(new ViewField("requestedTime", "Requested Time"));
		fields.add(new ViewField("status", "Valid Till"));
		fields.add(new ViewField("totalCost", "Total Cost"));
		

		return statusView;
	}

	private static Criteria getInventoryRequestStatusCriteria(FacilioModule module, int status) {

		FacilioField irStatusField = new FacilioField();
		irStatusField.setName("status");
		irStatusField.setColumnName("STATUS");
		irStatusField.setDataType(FieldType.NUMBER);
		irStatusField.setModule(ModuleFactory.getInventoryRequestModule());

		Condition statusCond = new Condition();
		statusCond.setField(irStatusField);
		statusCond.setOperator(NumberOperators.EQUALS);
		statusCond.setValue(String.valueOf(status));

		Criteria inventoryRequestStatusCriteria = new Criteria();
		inventoryRequestStatusCriteria.addAndCondition(statusCond);
		return inventoryRequestStatusCriteria;
	}


	private static FacilioView getGatePassForStatus(String viewName, String viewDisplayName, int status) {
		FacilioModule gatePassModule = ModuleFactory.getGatePassModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(gatePassModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		Criteria criteria = getGatePassCriteria(gatePassModule, status);

		FacilioView statusView = new FacilioView();
		statusView.setName(viewName);
		statusView.setDisplayName(viewDisplayName);
		statusView.setSortFields(sortFields);
		statusView.setCriteria(criteria);

		return statusView;
	}

	private static Criteria getGatePassCriteria(FacilioModule module, int status) {

		FacilioField gpStatusField = new FacilioField();
		gpStatusField.setName("status");
		gpStatusField.setColumnName("STATUS");
		gpStatusField.setDataType(FieldType.NUMBER);
		gpStatusField.setModule(ModuleFactory.getGatePassModule());

		Condition statusCond = new Condition();
		statusCond.setField(gpStatusField);
		statusCond.setOperator(NumberOperators.EQUALS);
		statusCond.setValue(String.valueOf(status));

		Criteria gatePassStatusCriteria = new Criteria();
		gatePassStatusCriteria.addAndCondition(statusCond);
		return gatePassStatusCriteria;
	}
	
	private static FacilioView getPendingReturnGatePass(String viewName, String viewDisplayName, int status) {
		FacilioModule gatePassModule = ModuleFactory.getGatePassModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(gatePassModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		Criteria criteria = getPendingReturnGatePassCriteria(gatePassModule, status);

		FacilioView statusView = new FacilioView();
		statusView.setName(viewName);
		statusView.setDisplayName(viewDisplayName);
		statusView.setSortFields(sortFields);
		statusView.setCriteria(criteria);

		return statusView;
	}

	private static Criteria getPendingReturnGatePassCriteria(FacilioModule module, int status) {

		FacilioField gpStatusField = new FacilioField();
		gpStatusField.setName("status");
		gpStatusField.setColumnName("STATUS");
		gpStatusField.setModule(ModuleFactory.getGatePassModule());

		Condition statusCond = new Condition();
		statusCond.setField(gpStatusField);
		statusCond.setOperator(NumberOperators.EQUALS);
		statusCond.setValue(String.valueOf(status));
		
		FacilioField gpReturnableField = new FacilioField();
		gpReturnableField.setName("isReturnable");
		gpReturnableField.setColumnName("IS_RETURNABLE");
		gpReturnableField.setDataType(FieldType.BOOLEAN);
		gpReturnableField.setModule(ModuleFactory.getGatePassModule());

		Condition returnCond = new Condition();
		returnCond.setField(gpReturnableField);
		returnCond.setOperator(BooleanOperators.IS);
		returnCond.setValue(String.valueOf(true));

		Criteria gatePassStatusCriteria = new Criteria();
		gatePassStatusCriteria.addAndCondition(statusCond);
		gatePassStatusCriteria.addAndCondition(returnCond);
		return gatePassStatusCriteria;
	}
	
	private static FacilioView getOverDueReturnGatePass(String viewName, String viewDisplayName, int status) {
		FacilioModule gatePassModule = ModuleFactory.getGatePassModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(gatePassModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		Criteria criteria = getOverdueReturnGatePassCriteria(gatePassModule, status);

		FacilioView statusView = new FacilioView();
		statusView.setName(viewName);
		statusView.setDisplayName(viewDisplayName);
		statusView.setSortFields(sortFields);
		statusView.setCriteria(criteria);

		return statusView;
	}

	private static Criteria getOverdueReturnGatePassCriteria(FacilioModule module, int status) {

		FacilioField gpStatusField = new FacilioField();
		gpStatusField.setName("status");
		gpStatusField.setColumnName("STATUS");
		gpStatusField.setModule(ModuleFactory.getGatePassModule());

		Condition statusCond = new Condition();
		statusCond.setField(gpStatusField);
		statusCond.setOperator(NumberOperators.EQUALS);
		statusCond.setValue(String.valueOf(status));
		
		FacilioField gpReturnableField = new FacilioField();
		gpReturnableField.setName("isReturnable");
		gpReturnableField.setColumnName("IS_RETURNABLE");
		gpReturnableField.setDataType(FieldType.BOOLEAN);
		gpReturnableField.setModule(ModuleFactory.getGatePassModule());

		Condition returnCond = new Condition();
		returnCond.setField(gpReturnableField);
		returnCond.setOperator(BooleanOperators.IS);
		returnCond.setValue(String.valueOf(true));
		
		FacilioField gpReturnTimeField = new FacilioField();
		gpReturnTimeField.setName("returnTime");
		gpReturnTimeField.setColumnName("RETURN_TIME");
		gpReturnTimeField.setDataType(FieldType.NUMBER);
		gpReturnTimeField.setModule(ModuleFactory.getGatePassModule());

		Long currTime = DateTimeUtil.getCurrenTime();
		
		Condition overDue = new Condition();
		overDue.setField(gpReturnTimeField);
		overDue.setOperator(NumberOperators.LESS_THAN);
		overDue.setValue(currTime + "");

		Criteria gatePassStatusCriteria = new Criteria();
		gatePassStatusCriteria.addAndCondition(statusCond);
		gatePassStatusCriteria.addAndCondition(returnCond);
		gatePassStatusCriteria.addAndCondition(overDue);
		return gatePassStatusCriteria;
	}

	private static FacilioView getAllAttendanceView() {
		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(ModuleFactory.getAttendanceModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Attendance");
		allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return allView;
	}
	
	private static FacilioView getAllAttendanceTransactionView() {
		FacilioField createdTime = new FacilioField();
		createdTime.setName("transactionTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("TRANSACTION_TIME");
		createdTime.setModule(ModuleFactory.getAttendanceTransactionModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Attendance Transactions");
		allView.setSortFields(Arrays.asList(new SortField(createdTime, true)));
		return allView;
	}
	
	private static FacilioView getAllShiftView() {
		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		name.setModule(ModuleFactory.getShiftModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Shift(s)");
		allView.setSortFields(Arrays.asList(new SortField(name, false)));
		return allView;
	}
	
	private static FacilioView getAllBreakView() {
		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("name");
		name.setModule(ModuleFactory.getBreakModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Break");
		allView.setSortFields(Arrays.asList(new SortField(name, false)));
		return allView;
	}
	
	private static FacilioView getAllShiftRotationView() {
		FacilioField name = new FacilioField();
		name.setName("schedularName");
		name.setDataType(FieldType.STRING);
		name.setColumnName("SCHEDULAR_NAME");
		name.setModule(ModuleFactory.getShiftRotationModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Shift Rotation");
		return allView;
	}
	private static FacilioView getAllServiceView() {
		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("name");
		name.setModule(ModuleFactory.getServiceModule());
		
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Service");
		allView.setSortFields(Arrays.asList(new SortField(name, false)));
		return allView;
	}
	
	private static FacilioView getAllBreakTransactionView() {
		FacilioField createdTime = new FacilioField();
		createdTime.setName("startTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("START_TIME");
		createdTime.setModule(ModuleFactory.getBreakTransactionModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Break Transactions");
		allView.setSortFields(Arrays.asList(new SortField(createdTime, true)));
		return allView;
	}

	private static FacilioView getAllReadingAlarmViews() {
		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Alarms");
		allView.setModuleName("newreadingalarm");
		allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return allView;
	}
	private static FacilioView getOccurrenceViews() {
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getAlarmOccurenceModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Alarms");
		allView.setModuleName("newreadingalarm");
		allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return allView;
	}
	private static FacilioView getReadingAlarmSeverity(String name, String displayName, String severity, boolean equals) {

		Condition alarmCondition = getReadingAlarmSeverityCondition(severity, equals);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(alarmCondition);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());

		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setCriteria(criteria);
		view.setModuleName("newreadingalarm");
		view.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		return view;
	}
	private static FacilioView getAlarmOcccurrenceSeverity(String name, String displayName, String severity, boolean equals) {

		Condition alarmCondition = getOccurrenceSeverityCondition(severity, equals);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(alarmCondition);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getAlarmOccurenceModule());

		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setCriteria(criteria);
		view.setModuleName("newreadingalarm");
		view.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		return view;
	}
	public static Condition getOccurrenceSeverityCondition(String severity, boolean equals) {
		LookupField severityField = new LookupField();
		severityField.setName("severity");
		severityField.setColumnName("SEVERITY");
		severityField.setDataType(FieldType.LOOKUP);
		severityField.setModule(ModuleFactory.getAlarmOccurenceModule());
		severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());

		Condition alarmCondition = new Condition();
		alarmCondition.setField(severityField);
		alarmCondition.setOperator(LookupOperator.LOOKUP);
		alarmCondition.setCriteriaValue(getSeverityAlarmCriteria(severity, equals));

		return alarmCondition;
	}
	private static FacilioView getBmsAlarmSeverity(String name, String displayName, String severity, boolean equals) {

		Condition alarmCondition = getReadingAlarmSeverityCondition(severity, equals);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(alarmCondition);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());

		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setCriteria(criteria);
		view.setModuleName("bmsAlarm");
		view.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		return view;
	}
	
	private static FacilioView getBmsOccurrenceAlarmSeverity(String name, String displayName, String severity, boolean equals) {

		Condition alarmCondition = getOccurrenceSeverityCondition(severity, equals);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(alarmCondition);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getAlarmOccurenceModule());

		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setCriteria(criteria);
		view.setModuleName("bmsAlarm");
		view.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		return view;
	}
	private static FacilioView getBmsAlarm(String name, String displayName, boolean equals) {

		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());

		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setModuleName("bmsAlarm");
		view.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		return view;
	}
	private static FacilioView getBmsAlarmOccurrence(String name, String displayName, boolean equals) {

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getAlarmOccurenceModule());

		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setModuleName("bmsAlarm");
		view.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		return view;
	}
	public static Condition getBmsAlarmSeverityCondition(String severity, boolean equals) {
		LookupField severityField = new LookupField();
		severityField.setName("severity");
		severityField.setColumnName("SEVERITY");
		severityField.setDataType(FieldType.LOOKUP);
		severityField.setModule(ModuleFactory.getBmsAlarmModule());
		severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());

		Condition alarmCondition = new Condition();
		alarmCondition.setField(severityField);
		alarmCondition.setOperator(LookupOperator.LOOKUP);
		alarmCondition.setCriteriaValue(getSeverityAlarmCriteria(severity, equals));

		return alarmCondition;
	}
	public static Condition getReadingAlarmSeverityCondition(String severity, boolean equals) {
		LookupField severityField = new LookupField();
		severityField.setName("severity");
		severityField.setColumnName("SEVERITY");
		severityField.setDataType(FieldType.LOOKUP);
		severityField.setModule(ModuleFactory.getBaseAlarmModule());
		severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());

		Condition alarmCondition = new Condition();
		alarmCondition.setField(severityField);
		alarmCondition.setOperator(LookupOperator.LOOKUP);
		alarmCondition.setCriteriaValue(getSeverityAlarmCriteria(severity, equals));

		return alarmCondition;
	}
	private static FacilioView getReadingAlarmUnacknowledged() {
		Criteria criteria = getReadingAlarmUnacknowledgedCriteria();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());

		FacilioView typeAlarms = new FacilioView();
		typeAlarms.setName("unacknowledged");
		typeAlarms.setDisplayName("Unacknowledged");
		typeAlarms.setCriteria(criteria);
		typeAlarms.setModuleName("newreadingalarm");
		typeAlarms.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		return typeAlarms;
	}
	private static FacilioView getAlarmOccurrenceUnacknowledged() {
		Criteria criteria = getAlarmOccurrenceUnacknowledgedCriteria();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());

		FacilioView typeAlarms = new FacilioView();
		typeAlarms.setName("unacknowledged");
		typeAlarms.setDisplayName("Unacknowledged");
		typeAlarms.setCriteria(criteria);
		typeAlarms.setModuleName("newreadingalarm");
		typeAlarms.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		return typeAlarms;
	}
	
	private static FacilioView getBmsAlarmUnacknowledged() {
		Criteria criteria = getReadingAlarmUnacknowledgedCriteria();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBmsAlarmModule());

		FacilioView typeAlarms = new FacilioView();
		typeAlarms.setName("unacknowledgedbmsalarm");
		typeAlarms.setDisplayName("Unacknowledged");
		typeAlarms.setCriteria(criteria);
		typeAlarms.setModuleName("bmsAlarm");
		typeAlarms.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		return typeAlarms;
	}
	
	private static FacilioView getBmsAlarmOccurrenceUnacknowledged() {
		Criteria criteria = getAlarmOccurrenceUnacknowledgedCriteria();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getAlarmOccurenceModule());

		FacilioView typeAlarms = new FacilioView();
		typeAlarms.setName("unacknowledgedbmsalarm");
		typeAlarms.setDisplayName("Unacknowledged");
		typeAlarms.setCriteria(criteria);
		typeAlarms.setModuleName("bmsAlarm");
		typeAlarms.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		return typeAlarms;
	}
	public static Criteria getReadingAlarmUnacknowledgedCriteria() {
		Condition falseCondition = new Condition();
		falseCondition.setColumnName("ACKNOWLEDGED");
		falseCondition.setFieldName("acknowledged");
		falseCondition.setOperator(BooleanOperators.IS);
		falseCondition.setValue(String.valueOf(false));

		Condition emptyCondition = new Condition();
		emptyCondition.setColumnName("ACKNOWLEDGED");
		emptyCondition.setFieldName("acknowledged");
		emptyCondition.setOperator(CommonOperators.IS_EMPTY);

		Criteria criteria = new Criteria();
		criteria.addOrCondition(emptyCondition);
		criteria.addOrCondition(falseCondition);

		LookupField severityField = new LookupField();
		severityField.setName("severity");
		severityField.setColumnName("SEVERITY");
		severityField.setDataType(FieldType.LOOKUP);
		severityField.setModule(ModuleFactory.getBaseAlarmModule());
		severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());

		Condition activeAlarm = new Condition();
		activeAlarm.setField(severityField);
		activeAlarm.setOperator(LookupOperator.LOOKUP);
		activeAlarm.setCriteriaValue(getSeverityAlarmCriteria("Clear", false));

		criteria.addAndCondition(activeAlarm);
		return criteria;
	}
	
	
	public static Criteria getAlarmOccurrenceUnacknowledgedCriteria() {
		Condition falseCondition = new Condition();
		falseCondition.setColumnName("ACKNOWLEDGED");
		falseCondition.setFieldName("acknowledged");
		falseCondition.setOperator(BooleanOperators.IS);
		falseCondition.setValue(String.valueOf(false));

		Condition emptyCondition = new Condition();
		emptyCondition.setColumnName("ACKNOWLEDGED");
		emptyCondition.setFieldName("acknowledged");
		emptyCondition.setOperator(CommonOperators.IS_EMPTY);

		Criteria criteria = new Criteria();
		criteria.addOrCondition(emptyCondition);
		criteria.addOrCondition(falseCondition);

		LookupField severityField = new LookupField();
		severityField.setName("severity");
		severityField.setColumnName("SEVERITY");
		severityField.setDataType(FieldType.LOOKUP);
		severityField.setModule(ModuleFactory.getAlarmOccurenceModule());
		severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());

		Condition activeAlarm = new Condition();
		activeAlarm.setField(severityField);
		activeAlarm.setOperator(LookupOperator.LOOKUP);
		activeAlarm.setCriteriaValue(getSeverityAlarmCriteria("Clear", false));

		criteria.addAndCondition(activeAlarm);
		return criteria;
	}
	private static FacilioView getAllTermsAndConditionView() {
		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("name");
		name.setModule(ModuleFactory.getTermsAndConditionModule());
		
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All T&C(s)");
		allView.setSortFields(Arrays.asList(new SortField(name, false)));
		return allView;
	}

	private static Criteria getContractListCriteria() {

		FacilioField statusField = new FacilioField();
		statusField.setName("status");
		statusField.setColumnName("STATUS");
		statusField.setDataType(FieldType.NUMBER);
		FacilioModule contract = ModuleFactory.getContractsModule();
		statusField.setModule(contract);

		Condition statusCond = new Condition();
		statusCond.setField(statusField);
		statusCond.setOperator(NumberOperators.NOT_EQUALS);
		statusCond.setValue(String.valueOf(ContractsContext.Status.REVISED.getValue()));

		Criteria criteria = new Criteria ();
		criteria.addAndCondition(statusCond);

		return criteria;
	}
	
	private static Criteria getExpiringContractListCriteria(FacilioModule module, int type) {

		FacilioField statusField = new FacilioField();
		statusField.setName("status");
		statusField.setColumnName("STATUS");
		statusField.setDataType(FieldType.NUMBER);
		FacilioModule contract = module;
		statusField.setModule(contract);

		FacilioField endDateField = new FacilioField();
		endDateField.setName("endDate");
		endDateField.setColumnName("END_DATE");
		endDateField.setDataType(FieldType.DATE);
		endDateField.setModule(contract);

		Condition expiryCond = new Condition();
		expiryCond.setField(endDateField);
		expiryCond.setOperator(DateOperators.CURRENT_MONTH);
		
		Condition statusCond = new Condition();
		statusCond.setField(statusField);
		statusCond.setOperator(NumberOperators.EQUALS);
		statusCond.setValue(String.valueOf(ContractsContext.Status.APPROVED.getValue()));

		Criteria criteria = new Criteria ();
		criteria.addAndCondition(statusCond);
		criteria.addAndCondition(expiryCond);
		
		if(type > 0) {
			FacilioField typeField = new FacilioField();
			typeField.setName("contractType");
			typeField.setColumnName("CONTRACT_TYPE");
			typeField.setDataType(FieldType.ENUM);
			typeField.setModule(contract);
			
			Condition typeCond = new Condition();
			typeCond.setField(typeField);
			typeCond.setValue(String.valueOf(type));
			typeCond.setOperator(EnumOperators.IS);
			criteria.addAndCondition(typeCond);
			
		}

		return criteria;
	}

	private static FacilioView getCustomModuleAllView(FacilioModule moduleObj) throws Exception {
		FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", moduleObj);
		
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All " + moduleObj.getDisplayName());
		allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		allView.setDefault(true);
		
		return allView;
	}
	private static FacilioView getMLAnomalyViews() {
		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		return allView;
	}
	private static FacilioView getAllReservationView() {
		
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Reservations");
		return allView;
	}

	private static FacilioField getReservationStatusField() {
		SystemEnumField field = (SystemEnumField) FieldFactory.getField("status", "Reservations.STATUS", FieldType.SYSTEM_ENUM);
		field.setEnumName("ReservationStatus");
		return field;
	}

	private static FacilioField getReservationScheduledTimeField() {
		return FieldFactory.getField("scheduledStartTime","Reservations.SCHEDULED_START_TIME", FieldType.DATE_TIME);
	}

	private static FacilioView getScheduledReservationView() {
		FacilioView view = new FacilioView();

		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(getReservationStatusField(), String.valueOf(ReservationContext.ReservationStatus.SCHEDULED.getIndex()), EnumOperators.IS));
		view.setCriteria(criteria);

		view.setSortFields(Arrays.asList(new SortField(getReservationScheduledTimeField(), false)));
		return view;
	}

	private static FacilioView getTodayReservationView() {
		FacilioView view = getScheduledReservationView();
		view.setName("nearing");
		view.setDisplayName("Nearing");

		Criteria criteria = new Criteria();
		criteria.addOrCondition(CriteriaAPI.getCondition(getReservationScheduledTimeField(), DateOperators.TODAY));
		criteria.addOrCondition(CriteriaAPI.getCondition(getReservationScheduledTimeField(), DateOperators.YESTERDAY));
		criteria.addOrCondition(CriteriaAPI.getCondition(getReservationScheduledTimeField(), DateOperators.TOMORROW));

		view.getCriteria().andCriteria(criteria);
		return view;
	}

	private static FacilioView getThisWeekReservationView() {
		FacilioView view = getScheduledReservationView();
		view.setName("thisweek");
		view.setDisplayName("This Week");
		view.getCriteria().addAndCondition(CriteriaAPI.getCondition(getReservationScheduledTimeField(), DateOperators.CURRENT_WEEK));
		return view;
	}

	private static FacilioView getNextWeekReservationView() {
		FacilioView view = getScheduledReservationView();
		view.setName("nextweek");
		view.setDisplayName("Next Week");
		view.getCriteria().addAndCondition(CriteriaAPI.getCondition(getReservationScheduledTimeField(), DateOperators.NEXT_WEEK));
		return view;
	}

	private static FacilioView getOnGoingReservationView() {
		FacilioView view = getScheduledReservationView();
		view.setName("ongoing");
		view.setDisplayName("On Going");
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(getReservationStatusField(), String.valueOf(ReservationContext.ReservationStatus.ON_GOING.getIndex()), EnumOperators.IS));
		view.setCriteria(criteria);
		return view;
	}

	private static FacilioView getCompletedReservationView() {
		FacilioView view = getScheduledReservationView();
		view.setName("completed");
		view.setDisplayName("Completed");
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(getReservationStatusField(), String.valueOf(ReservationContext.ReservationStatus.FINISHED.getIndex()), EnumOperators.IS));
		view.setCriteria(criteria);
		return view;
	}
	private static FacilioView getAnomalyAlarmSeverity(String name, String displayName, String severity, boolean equals) {

		Condition alarmCondition = getReadingAlarmSeverityCondition(severity, equals);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(alarmCondition);

		FacilioField lastOccurredTime = new FacilioField();
		lastOccurredTime.setName("lastOccurredTime");	
		lastOccurredTime.setDataType(FieldType.DATE_TIME);
		lastOccurredTime.setColumnName("LAST_OCCURRED_TIME");
		lastOccurredTime.setModule(ModuleFactory.getBaseAlarmModule());

		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setCriteria(criteria);
		view.setModuleName("mlAnomalyAlarm");
		view.setSortFields(Arrays.asList(new SortField(lastOccurredTime, false)));

		return view;
	}
	
	public static Condition getPendingAssetMovementStateTypeCriteria() {
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("typeCode");
		statusTypeField.setColumnName("STATUS_TYPE");
		statusTypeField.setDataType(FieldType.NUMBER);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition status = new Condition();
		status.setField(statusTypeField);
		status.setOperator(NumberOperators.NOT_EQUALS);
		status.setValue(String.valueOf(StatusType.CLOSED.getIntVal())+ "," +String.valueOf(StatusType.REJECTED.getIntVal()));

		
		return status;
	}

	private static FacilioView getAllVisitorsView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Visitors");
		return allView;
	}

	private static FacilioView getAllVisitorLogsView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Visitor Logs");
		return allView;
	}

	private static FacilioView getAllVisitorInvitesView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Visitor Invites");
		return allView;
	}
	
	private static FacilioView getAllInsuranceView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Insurance");
		return allView;
	}
	
	private static FacilioView getAllWorkPermitView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Work Permit");
		return allView;
	}

	private static FacilioView getAssetKPIView(String name, Map<String, FacilioField> fieldMap) {
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getKPICondition(fieldMap));
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("resourceType"), String.valueOf(ResourceType.ASSET_CATEGORY.getValue()), NumberOperators.EQUALS));

		FacilioView assetKpisView = new FacilioView();
		assetKpisView.setName(name);
		assetKpisView.setDisplayName("Asset KPIs");
		assetKpisView.setCriteria(criteria);

		return assetKpisView;
	}
	
	private static FacilioView getSpaceKPIView(String name, Map<String, FacilioField> fieldMap) {
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getKPICondition(fieldMap));
		// Assuming all the asset kpis will have asset category
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("resourceType"), String.valueOf(ResourceType.ASSET_CATEGORY.getValue()), NumberOperators.NOT_EQUALS));

		FacilioView assetKpisView = new FacilioView();
		assetKpisView.setName(name);
		assetKpisView.setDisplayName("Space KPIs");
		assetKpisView.setCriteria(criteria);

		return assetKpisView;
	}
	
	private static Condition getKPICondition(Map<String, FacilioField> fieldMap) {
		return CriteriaAPI.getCondition(fieldMap.get("formulaFieldType"), String.valueOf(FormulaFieldType.ENPI.getValue()), NumberOperators.EQUALS);
	}

	private static FacilioView getAllWatchListView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Watch List");
		return allView;
	}

}
