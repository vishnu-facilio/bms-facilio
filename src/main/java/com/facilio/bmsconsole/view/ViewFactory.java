package com.facilio.bmsconsole.view;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.PickListAction;
import com.facilio.bmsconsole.actions.TicketStatusAction;
import com.facilio.bmsconsole.context.AlarmContext.AlarmType;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.AssetContext.AssetState;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.context.FormulaFieldContext.ResourceType;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.context.reservation.ReservationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.signup.jobPlan.AddJobPlanModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.fields.*;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;

public class  ViewFactory {
	   private static final Logger LOGGER = Logger.getLogger(ViewFactory.class.getName());

	private static Map<String, Map<String, FacilioView>> views = Collections.unmodifiableMap(initializeViews());
	private static Map<String, Map<String, List<String>>> groupViews = Collections
			.unmodifiableMap(initializeGroupViews()); // TODO remove
	private static Map<String, List<Map<String, Object>>> groupVsViews = Collections.unmodifiableMap(initializeGroupVsViews());


	public static FacilioView getView(FacilioModule module, String viewName, ModuleBean modBean) throws Exception {
		String moduleName;
		if (viewName.contains("approval_")) {
			moduleName = FacilioConstants.ContextNames.APPROVAL;
		} else if (viewName.contains("invite_")) {
			moduleName = FacilioConstants.ContextNames.VISITOR_INVITE;
		} else {
			moduleName = module.getName();
		}
		return getView(moduleName, module, viewName, null);
	}
	
	public static FacilioView getView(String moduleName, String viewName) throws Exception {
		return getView(moduleName, null, viewName, null);
	}
	
	public static FacilioView getView(String moduleName, FacilioModule module, String viewName, ModuleBean modBean) throws Exception {
		ApplicationContext applicationContext = getApplicationContext();
		LOGGER.info(String.format("ViewFactoryTracking - getView() - ModuleName - %s ViewName - %s AppId - %d AppName - %s", moduleName, viewName, applicationContext.getId(), applicationContext.getLinkName()));
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
				if (columns != null && module.isCustom()) {
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
			if (module != null && module.isCustom()) {
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
		views.put("approval_myrequests", getMyRequestWorkorders("myrequests").setOrder(order++));
		views.put("approval_requested", getRequestedStateApproval("requested", false).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.APPROVAL, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllTicketCategory().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.TICKET_CATEGORY, views);

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
		views.put("myrequests", getMyRequestWorkorders("myrequests").setOrder(order++));
		views.put("upcomingThisWeek", getUpcomingWorkOrdersThisWeek().setOrder(order++));
		views.put("upcomingNextWeek", getUpcomingWorkOrdersNextWeek().setOrder(order++));
		views.put("vendorWorkorder", getVendorWorkOrders().setOrder(order++));
		views.put("tenantWorkorder", getTenantWorkorders("tenantWorkorder").setOrder(order++));
		views.put("tenantOpen", getTenantOpenWorkOrders().setOrder(order++));
		views.put("tenantClosed", getTenantClosedWorkOrders().setOrder(order++));
		views.put("tenantAll", getTenantAllWorkOrders().setOrder(order++));

		views.put("vendorOpen", getVendorOpenWorkOrders().setOrder(order++));
		views.put("vendorClosed", getVendorClosedWorkOrders().setOrder(order++));

		//occupant portal views

		views.put("myAllWo", getMyAllRequestWorkorders().setOrder(order++));
		views.put("myOpenWo", getMyOpenRequestWorkorders().setOrder(order++));
		views.put("myClosedWo", getMyClosedRequestWorkorders().setOrder(order++));


		views.put("pendingapproval", getRequestedStateApproval("pendingapproval", true).setOrder(order++));
		views.put("myapproval", getMyRequestWorkorders("myapproval").setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WORK_ORDER, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("my", getMyTasks().setOrder(order++));
		views.put("all", getAllTaskView().setOrder(order++));
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
		views.put("energy", getAllEnergyMetersView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ENERGY_METER, views);


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
		views.put("active", getRulesByStatusForNewRule("active", "Active", true).setOrder(order++));
		views.put("inactive", getRulesByStatusForNewRule("inactive", "In Active", false).setOrder(order++));
		views.put("all", getAllNewReadingRules());
		viewsMap.put("newreadingrule", views);

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
		views.put("all", getAllInventoryCategory().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.INVENTORY_CATEGORY, views);

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
		views.put("all", getAllWorkOrderItems().setOrder(order++));
		views.put("details", getAllWorkOrderItemsDetailsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WORKORDER_ITEMS, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllWorkOrderTools().setOrder(order++));
		views.put("details",getAllWorkOrderToolsDetailsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WORKORDER_TOOLS,views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllWorkOrderService().setOrder(order++));
		views.put("details",getAllWorkOrderServiceDetailsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WO_SERVICE,views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllVendors().setOrder(order++));

		viewsMap.put(FacilioConstants.ContextNames.VENDORS, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllWorkOrderPlannedItems().setOrder(order++));
		views.put("details", getWorkOrderPlannedItemsDetails().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WO_PLANNED_ITEMS, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllWorkOrderPlannedTools().setOrder(order++));
		views.put("details", getWorkOrderPlannedToolsDetails().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WO_PLANNED_TOOLS, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllWorkOrderPlannedServices().setOrder(order++));
		views.put("details", getWorkOrderPlannedServicesDetails().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WO_PLANNED_SERVICES, views);

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
		/*
		views.put("open", getOpenPurchaseRequest().setOrder(order++));
		views.put("pending", getPurchaseRequestForStatus("pending", "Pending", 1).setOrder(order++));
		views.put("overdue", getOverDuePurchaseRequest().setOrder(order++));
		views.put("approved", getPurchaseRequestForStatus("approved", "Approved", 2).setOrder(order++));
		views.put("rejected", getPurchaseRequestForStatus("rejected", "Rejected", 3).setOrder(order++));
		views.put("completed", getPurchaseRequestForStatus("completed", "Completed", 4).setOrder(order++));
		 */
		viewsMap.put(FacilioConstants.ContextNames.PURCHASE_REQUEST, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllPurchaseOrderView().setOrder(order++));
		/*
		views.put("open", getOpenPurchaseOrder().setOrder(order++));
		views.put("pending", getPurchaseOrderForStatus("pending", "Pending", 1).setOrder(order++));
		views.put("overdue", getOverDuePurchaseOrder().setOrder(order++));
		views.put("approved", getPurchaseOrderForStatus("approved", "Approved", 2).setOrder(order++));
		views.put("ongoing", getOnGoingPurchaseOrder().setOrder(order++));
		views.put("rejected", getPurchaseOrderForStatus("rejected", "Rejected", 3).setOrder(order++));
		views.put("completed", getPurchaseOrderForStatus("completed", "Completed", 7).setOrder(order++));
		*/
		
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
		views.put("all", getAllContractView(ModuleFactory.getContractsModule()).setOrder(order++));
		views.put("expiring", getExpiringContractView(ModuleFactory.getContractsModule(), -1).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.CONTRACTS, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllDocumentsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.VENDOR_DOCUMENTS, views);

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
		views.put("issued", getInventoryRequestIssued("issued", "Issued" ,true).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.INVENTORY_REQUEST, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllAttendanceView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ATTENDANCE, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllInventoryRequestLineItemsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS, views);

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
		views.put("active", getReadingAlarmSeverity("active", "Active", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++));
		views.put("all", getAllReadingAlarmViews().setOrder(order++));
		views.put("unacknowledged", getReadingAlarmUnacknowledged().setOrder(order++));
		views.put("critical", getReadingAlarmSeverity("critical", "Critical Faults", "Critical", true).setOrder(order++));
		views.put("major", getReadingAlarmSeverity("major", "Major Faults", "Major", true).setOrder(order++));
		views.put("minor", getReadingAlarmSeverity("minor", "Minor Faults", "Minor", true).setOrder(order++));
		views.put("cleared", getReadingAlarmSeverity("cleared", "Cleared Faults", FacilioConstants.Alarm.CLEAR_SEVERITY, true)
				.setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.NEW_READING_ALARM, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("bmsActive", getBmsAlarmSeverity("bmsActive", "Active Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++));
		views.put("bmsAlarm", getBmsAlarm("bmsAlarm", "All Alarms", true).setOrder(order++));
		views.put("unacknowledgedbmsalarm", getBmsAlarmUnacknowledged().setOrder(order++));
		views.put("bmsCritical", getBmsAlarmSeverity("bmsCritical", "Critical Alarms", "Critical", true).setOrder(order++));
		views.put("bmsMajor", getBmsAlarmSeverity("bmsMajor", "Major Alarms", "Major", true).setOrder(order++));
		views.put("bmsMinor", getBmsAlarmSeverity("bmsMinor", "Minor Alarms", "Minor", true).setOrder(order++));
		views.put("bmsCleared", getBmsAlarmSeverity("bmsCleared", "Cleared Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, true).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.BMS_ALARM, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("sensorActive", getSensorAlarmSeverity("sensorActive", "Active Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++));
		views.put("sensorAlarm", getSensorAlarm("sensorAlarm", "All Alarms", true).setOrder(order++));
		views.put("unacknowledgedSensorAlarm", getSensorAlarmUnacknowledged().setOrder(order++));
		views.put("sensorCritical", getSensorAlarmSeverity("sensorCritical", "Critical Alarms", "Critical", true).setOrder(order++));
		views.put("sensorMajor", getSensorAlarmSeverity("sensorMajor", "Major Alarms", "Major", true).setOrder(order++));
		views.put("sensorMinor", getSensorAlarmSeverity("sensorMinor", "Minor Alarms", "Minor", true).setOrder(order++));
		views.put("sensorCleared", getSensorAlarmSeverity("sensorCleared", "Cleared Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, true).setOrder(order++));
		views.put("sensorMeter", getSensorMeterAlarm().setOrder(order++));
		views.put("sensorNonMeter", getSensorNonMeterAlarm().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("active", getOperationalAlarmSeverity("active", "Active", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++));
		views.put("all", getOperationalAlarmViews().setOrder(order++));
		views.put("cleared", getOperationalAlarmSeverity("cleared", "Cleared Alarms", FacilioConstants.Alarm.CLEAR_SEVERITY, true)
				.setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.OPERATION_ALARM, views);
		
		order = 1;
		views = new LinkedHashMap<>();
        views.put("agentAll", getAgentAlarmOccurrenceViews().setOrder(order++));
        viewsMap.put(FacilioConstants.ContextNames.AGENT_ALARM, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("mlmvaAlarms", getMultivariateAnomalyAlarmOccurrenceViews().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.MULTIVARIATE_ANOMALY_ALARM, views);

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
		views.put("todayvisits", getVisitorsCheckedInTodayView().setOrder(order++));
		views.put("current", getVisitorsCheckedInCurrentlyView().setOrder(order++));
		views.put("pending", getPendingVisitsView().setOrder(order++));
		views.put("upcoming", getUpcomingVisitsView().setOrder(order++));
		views.put("all", getAllVisitorLogsView().setOrder(order++));
		views.put("tenantAll", getTenantPortalAllVisitorLogsView().setOrder(order++));


		// views for vendor portal
		views.put("vendorActiveVisitors", getVendorUpcomingVisitorLogsView().setOrder(order++)); // 1
		views.put("vendorExpired", getVendorExpiredVisitorInvites().setOrder(order++)); // 4


		// views for tenant portal
		views.put("myExpired", getMyExpiredVisitorInvites().setOrder(order++)); // 3

		views.put("myActive", getMyActiveVisitorInvites().setOrder(order++));
		views.put("myPendingVisits", getMyPendingVisitsView().setOrder(order++));

		viewsMap.put(FacilioConstants.ContextNames.VISITOR_LOGGING, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("todayvisits", getVisitorsLogCheckedInTodayView().setOrder(order++));
		views.put("current", getVisitorsLogCheckedInCurrentlyView().setOrder(order++));
		views.put("pending", getPendingVisitLogsView().setOrder(order++));
		views.put("all", getAllVisitorCheckInLogsView().setOrder(order++));
		views.put("myPendingVisits", getMyPendingVisitsCheckInView().setOrder(order++));
		views.put("tenantAll", getTenantPortalAllVisitorCheckInLogsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.VISITOR_LOG, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("upcoming", getUpcomingInviteVisitsView().setOrder(order++));
		views.put("invite_today", getTodayInviteVisitorInvitesView().setOrder(order++));
		//views.put("invite_pending", getPendingInviteVisitorInvitesView().setOrder(order++));
		views.put("invite_all", getAllInviteVisitorInvitesView().setOrder(order++));
		views.put("invite_myInvites", getActiveInviteVisitorInvites().setOrder(order++));
		views.put("invite_myExpired", getExpiredInviteVisitorInvites().setOrder(order++));
		
		// views for vendor portal
		views.put("vendorActiveVisitors", getVendorUpcomingInviteVisitorLogsView().setOrder(order++)); // 1
		views.put("vendorExpired", getVendorExpiredInviteVisitorInvites().setOrder(order++)); // 4
		
		// views for tenant portal
		views.put("myExpired", getMyExpiredInviteVisitorInvites().setOrder(order++)); // 3
		views.put("myActive", getMyActiveInviteVisitorInvites().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.INVITE_VISITOR, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("invite_today", getTodayVisitorInvitesView().setOrder(order++));
		//views.put("invite_pending", getPendingVisitorInvitesView().setOrder(order++));
		views.put("invite_all", getAllVisitorInvitesView().setOrder(order++));
		views.put("invite_myInvites", getActiveVisitorInvites().setOrder(order++));
		views.put("invite_myExpired", getExpiredVisitorInvites().setOrder(order++)); // 3
		// views for vendor portal
		views.put("invite_vendorActiveVisitors", getVendorUpcomingVisitorLogsView().setOrder(order++)); // 1
		views.put("invite_vendorExpired", getVendorExpiredVisitorInvites().setOrder(order++)); // 4

		viewsMap.put(FacilioConstants.ContextNames.VISITOR_INVITE, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllInsuranceView().setOrder(order++));
		//views.put("vendor", getVendorInsuranceView().setOrder(order++));
		views.put("vendorActive", getVendorActiveInsuranceView().setOrder(order++));
		views.put("vendorExpired", getVendorExpiredInsuranceView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.INSURANCE, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllPlannedMaintenanceView().setOrder(order++));
		views.put("active", getActivePlannedMaintenanceView().setOrder(order++));
		views.put("inactive", getInActivePlannedMaintenanceView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.PLANNEDMAINTENANCE, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllWorkPermitView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT, views);

		order = 1;
		views = new LinkedHashMap<>();
		Map<String, FacilioField> formulaFieldMap = FieldFactory.getAsMap(FieldFactory.getFormulaFieldFields());
		views.put("asset", getAssetKPIView("asset", formulaFieldMap).setOrder(order++));
		views.put("space", getSpaceKPIView("space", formulaFieldMap).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.FORMULA_FIELD, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("blocked", getBlockedWatchListView().setOrder(order++));
		views.put("vip", getVipWatchListView().setOrder(order++));
		views.put("all", getAllWatchListView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WATCHLIST, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllContactView().setOrder(order++));
		views.put("tenant", getTenantContactView().setOrder(order++));
		views.put("vendor", getVendorContactView().setOrder(order++));
		views.put("employee", getEmployeeContactView().setOrder(order++));
		
		viewsMap.put(FacilioConstants.ContextNames.CONTACT, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllServiceRequests().setOrder(order++));
		views.put("myopenservicerequests", getMyServiceRequets().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.SERVICE_REQUEST, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllOccupantsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.OCCUPANT, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllSafetyPlansView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.SAFETY_PLAN, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllHazardModuleView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.HAZARD, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllPrecautionView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.PRECAUTION, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("associatedhazards", getAssociatedHazardPrecautionView().setOrder(order++));
		views.put("associatedprecautions", getAssociatedPrecautionView().setOrder(order++));
		views.put("all", getAllHazardPrecautionView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.HAZARD_PRECAUTION, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllClientView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.CLIENT, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllSafetyPlanHazardsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.SAFETYPLAN_HAZARD, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllAssetHazardsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.ASSET_HAZARD, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllWorkOrderHazardsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WORKORDER_HAZARD, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllWorkOrderHazardPrecautionsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WORKORDER_HAZARD_PRECAUTION, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllBaseSpaceHazardView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.BASESPACE_HAZARD, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllSites().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.SITE, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllBuildings().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.BUILDING, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllSpaces().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.SPACE, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllBasespaces().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.BASE_SPACE, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllFloors().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.FLOOR, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllHiddenTenantContacts().setOrder(order++));
		views.put("all-contacts", getAllTenantContacts().setOrder(order++));
		
		viewsMap.put(FacilioConstants.ContextNames.TENANT_CONTACT, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllFailureCodes().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.FAILURE_CODE, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllFailureClass().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.FAILURE_CLASS, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllHiddenVendorContacts().setOrder(order++));
		views.put("all-contacts", getAllVendorContacts().setOrder(order++));

		viewsMap.put(FacilioConstants.ContextNames.VENDOR_CONTACT, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllHiddenClientContacts().setOrder(order++));
		views.put("all-contacts", getAllClientContacts().setOrder(order++));

		viewsMap.put(FacilioConstants.ContextNames.CLIENT_CONTACT, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllHiddenEmployees().setOrder(order++));
		views.put("all-employees", getAllEmployees().setOrder(order++));

		viewsMap.put(FacilioConstants.ContextNames.EMPLOYEE, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllPeople().setOrder(order++));

		viewsMap.put(FacilioConstants.ContextNames.PEOPLE, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllTenantUnitSpace().setOrder(order++));
		views.put("details", getAllTenantUnitSpaceDetailsView().setOrder(order++));

		viewsMap.put(FacilioConstants.ContextNames.TENANT_UNIT_SPACE, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllTenantSpaces().setOrder(order++));

		viewsMap.put(FacilioConstants.ContextNames.TENANT_SPACES, views);

        order = 1;
        views = new LinkedHashMap<>();
        views.put("all", getAllQuotations().setOrder(order++));
        viewsMap.put(FacilioConstants.ContextNames.QUOTE, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllTaxes().setOrder(order++));
		views.put("active", getActiveTaxes().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.TAX, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllWorkOrderCostView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.WORKORDER_COST, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllAnnouncementView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllAudienceView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.Tenant.AUDIENCE, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllPeopleAnnouncementView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllNewsAndInformationView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllDealsAndOffersView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllNeighbourhoodView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllContactDirectoryView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllAdminDocumentsView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllBudgetView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.Budget.BUDGET, views);


		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllTransactionView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.TRANSACTION, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllTransferRequestView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.TRANSFER_REQUEST, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllTransferRequestShipmentView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllRequestForQuotationView().setOrder(order++));
		views.put("quotesReceived", getQuotesReceivedRfqView("quotesReceived", "Quotes Received" ,true).setOrder(order++));
		views.put("awarded", getAwardedRfqView("awarded", "Awarded" ,true).setOrder(order++));
		views.put("poCreated", getPoCreatedRfqView("poCreated", "PO Created" ,true).setOrder(order++));
		views.put("discarded", getDiscardedRfqView("discarded", "Discarded" ,true).setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllJobPlanView().setOrder(order++));
		views.put("active", getActiveJobPlanView().setOrder(order++));
		views.put("inactive", getInActiveJobPlanView().setOrder(order++));
		views.put("disabled",getDisabledJobPlanView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.JOB_PLAN, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllVendorQuotesView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.VENDOR_QUOTES, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllChartOfAccountView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.Budget.CHART_OF_ACCOUNT, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllAccountTypeView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.Budget.ACCOUNT_TYPE, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllControlScheduleView().setOrder(order++));
		viewsMap.put(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllControlScheduleExceptionTenantView().setOrder(order++));
		viewsMap.put(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllControlGroupView().setOrder(order++));
		viewsMap.put(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getTenantControlGroupView().setOrder(order++));
		viewsMap.put(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("upcoming", getUpcomingControlCommandView().setOrder(order++));
		views.put("history", getHistoryControlCommandView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllFacilityView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.FacilityBooking.FACILITY, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllFacilityBookingView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllAmenityView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.FacilityBooking.AMENITY, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllDepartmentView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.DEPARTMENT, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllMovesView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.MOVES, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllDeliveriesView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.DELIVERIES, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllDeliveryAreaView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.DELIVERY_AREA, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllLockersView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.LOCKERS, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllParkingStallView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.PARKING_STALL, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getAllDesksView().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.Floorplan.DESKS, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all",getAllInspectionTemplateViews().setOrder(order++));
		viewsMap.put(FacilioConstants.Inspection.INSPECTION_TEMPLATE, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all",getAllInspectionResponseViews().setOrder(order++));
		viewsMap.put(FacilioConstants.Inspection.INSPECTION_RESPONSE, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all",getAllInspectionCategoryViews().setOrder(order++));
		viewsMap.put(FacilioConstants.Inspection.INSPECTION_CATEGORY, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all",getAllInspectionPriorityViews().setOrder(order++));
		viewsMap.put(FacilioConstants.Inspection.INSPECTION_PRIORITY, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all",getAllInductionTemplateViews().setOrder(order++));
		viewsMap.put(FacilioConstants.Induction.INDUCTION_TEMPLATE, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all",getAllInductionResponseViews().setOrder(order++));
		viewsMap.put(FacilioConstants.Induction.INDUCTION_RESPONSE, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all",getAllSurveyTemplateViews().setOrder(order++));
		viewsMap.put(FacilioConstants.Survey.SURVEY_TEMPLATE, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all",getAllSurveyResponseViews().setOrder(order++));
		viewsMap.put(FacilioConstants.Survey.SURVEY_RESPONSE, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all",getAllEmailConversationThreadingViews().setOrder(order++));
		viewsMap.put(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME, views);
		
		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getWorkflowLog().setOrder(order++));
		viewsMap.put(FacilioConstants.Workflow.WORKFLOW_LOG, views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getMiscController().setOrder(order++));
		viewsMap.put("misccontroller", views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getBacnetController().setOrder(order++));
		viewsMap.put("bacnetipcontroller", views);

//		order = 1;
//		views = new LinkedHashMap<>();
//		views.put("all", getBacnetmstController().setOrder(order++));
//		viewsMap.put("bacnetmstpcontroller", views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getNiagaraController().setOrder(order++));
		viewsMap.put("niagaracontroller", views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getModbusTcpController().setOrder(order++));
		viewsMap.put("modbustcpcontroller", views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getModbusRtuontroller().setOrder(order++));
		viewsMap.put("modbusrtucontroller", views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getOpcUaController().setOrder(order++));
		viewsMap.put("opcuacontroller", views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getOpcXmldaController().setOrder(order++));
		viewsMap.put("opcxmldacontroller", views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getLonWorksController().setOrder(order++));
		viewsMap.put("lonworkscontroller", views);

//		order = 1;
//		views = new LinkedHashMap<>();
//		views.put("all", getKnxController().setOrder(order++));
//		viewsMap.put("knxcontroller", views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getCustomController().setOrder(order++));
		viewsMap.put("customcontroller", views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getRestController().setOrder(order++));
		viewsMap.put("restcontroller", views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getSystemController().setOrder(order++));
		viewsMap.put("systemController", views);

		order = 1;
		views = new LinkedHashMap<>();
		views.put("all", getRdmController().setOrder(order++));
		viewsMap.put("rdmcontroller", views);

		order =1;
		views = new LinkedHashMap<>();
		views.put("all",getAllSpaceBookingViews().setOrder(order++));
		viewsMap.put(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING,views);
		
		return viewsMap;
	}

	private static FacilioView getAllSurveyResponseViews() {
		
		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Survey_Responses.ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Surveys");
		allView.setModuleName(FacilioConstants.Survey.SURVEY_RESPONSE);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}

	private static FacilioView getAllSurveyTemplateViews() {
		
		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Survey_Templates.ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Survey Templates");
		allView.setModuleName(FacilioConstants.Survey.SURVEY_TEMPLATE);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}

	private static FacilioView getAllEmailConversationThreadingViews() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Email_Conversation_Threading.ID", FieldType.NUMBER), false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Email Conversation");
		allView.setModuleName(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
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
		groupDetails.put("moduleName", FacilioConstants.ContextNames.NEW_READING_ALARM);
		groupDetails.put("views", fddAlarms);
		groupVsViews.add(groupDetails);
		
		moduleVsGroup.put(FacilioConstants.ContextNames.NEW_READING_ALARM, groupVsViews);

		groupVsViews = new ArrayList<>();
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
		groupDetails.put("moduleName", FacilioConstants.ContextNames.BMS_ALARM);
		groupDetails.put("views", bmsAlarms);
		groupVsViews.add(groupDetails);
		
		moduleVsGroup.put(FacilioConstants.ContextNames.BMS_ALARM, groupVsViews);
		
		groupVsViews = new ArrayList<>();
		ArrayList<String> agentAlarms = new ArrayList<String>();
		agentAlarms.add("agentAll");
		groupDetails = new HashMap<>();
		groupDetails.put("name", "agentAlarmViews");
		groupDetails.put("displayName", "Agent Alarms");
		groupDetails.put("moduleName", FacilioConstants.ContextNames.AGENT_ALARM);
		groupDetails.put("views", agentAlarms);
		groupVsViews.add(groupDetails);
		
		moduleVsGroup.put(FacilioConstants.ContextNames.AGENT_ALARM, groupVsViews);
		
		groupVsViews = new ArrayList<>();
		ArrayList<String> sensorAlarms = new ArrayList<String>();
		sensorAlarms.add("sensorAlarm");
		sensorAlarms.add("sensorActive");
		sensorAlarms.add("unacknowledgedSensorAlarm");
		sensorAlarms.add("sensorMajor");
		sensorAlarms.add("sensorMinor");
		sensorAlarms.add("sensorCritical");
		sensorAlarms.add("sensorCleared");
		sensorAlarms.add("sensorMeter");
		sensorAlarms.add("sensorNonMeter");


		groupDetails = new HashMap<>();
		groupDetails.put("name", "sensorAlarmViews");
		groupDetails.put("displayName", "Sensor Alarms");
		groupDetails.put("moduleName", FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM);
		groupDetails.put("views", sensorAlarms);
		groupVsViews.add(groupDetails);
		
		moduleVsGroup.put(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM, groupVsViews);

		
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
	
	private static FacilioView getAllSites() {

		FacilioModule siteModule = ModuleFactory.getSiteModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Sites");
		allView.setSortFields(sortFields);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllHiddenTenantContacts() {

		FacilioModule tenantContactModule = ModuleFactory.getTenantContactModule();

		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(tenantContactModule);
		
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Tenant Contacts");
		allView.setModuleName(tenantContactModule.getName());
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));
		
		allView.setHidden(true);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllFailureCodes() {

		FacilioModule failureCodeModule = ModuleFactory.getFailureCodeModule();

		FacilioField id = new FacilioField();
		id.setName("id");
		id.setColumnName("ID");
		id.setDataType(FieldType.NUMBER);
		id.setModule(failureCodeModule);

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Failure Codes");
		allView.setModuleName(failureCodeModule.getName());
		allView.setSortFields(Arrays.asList(new SortField(id, false)));

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllFailureClass() {

		FacilioModule failureClassModule = ModuleFactory.getFailureClassModule();

		FacilioField id = new FacilioField();
		id.setName("id");
		id.setColumnName("ID");
		id.setDataType(FieldType.NUMBER);
		id.setModule(failureClassModule);

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Failure Class");
		allView.setModuleName(failureClassModule.getName());
		allView.setSortFields(Arrays.asList(new SortField(id, false)));

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	
	private static FacilioView getAllTenantContacts() {

		FacilioModule tenantContactModule = ModuleFactory.getTenantContactModule();

		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(tenantContactModule);
		
		
		FacilioView allView = new FacilioView();
		allView.setName("all-contacts");
		allView.setDisplayName("All Tenant Contacts");
		allView.setModuleName(tenantContactModule.getName());
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllHiddenVendorContacts() {

		FacilioModule vendorContactModule = ModuleFactory.getVendorContactModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Vendor Contacts");
		allView.setModuleName(vendorContactModule.getName());
		allView.setSortFields(sortFields);

		allView.setHidden(true);
		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		appDomains.add(AppDomain.AppDomainType.TENANT_PORTAL);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}

	private static FacilioView getAllVendorContacts() {

		FacilioModule vendorContactModule = ModuleFactory.getVendorContactModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

		FacilioView allView = new FacilioView();
		allView.setName("all-contacts");
		allView.setDisplayName("All Vendor Contacts");
		allView.setModuleName(vendorContactModule.getName());
		allView.setSortFields(sortFields);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllHiddenClientContacts() {

		FacilioModule clientContactModule = ModuleFactory.getClientContactModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Client Contacts");
		allView.setModuleName(clientContactModule.getName());
		allView.setSortFields(sortFields);

		allView.setHidden(true);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllClientContacts() {

		FacilioModule clientContactModule = ModuleFactory.getClientContactModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

		FacilioView allView = new FacilioView();
		allView.setName("all-contacts");
		allView.setDisplayName("All Client Contacts");
		allView.setModuleName(clientContactModule.getName());
		allView.setSortFields(sortFields);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}


	private static FacilioView getAllHiddenEmployees() {

		FacilioModule employeeModule = ModuleFactory.getEmployeeModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Employees");
		allView.setModuleName(employeeModule.getName());
		allView.setSortFields(sortFields);

		//allView.setHidden(true);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllEmployees() {

		FacilioModule employeeModule = ModuleFactory.getEmployeeModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

		FacilioView allView = new FacilioView();
		allView.setName("all-employees");
		allView.setDisplayName("All Employees");
		allView.setModuleName(employeeModule.getName());
		allView.setSortFields(sortFields);


		return allView;
	}

	private static FacilioView getAllPeople() {

		FacilioModule peopleModule = ModuleFactory.getPeopleModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("People");
		allView.setModuleName(peopleModule.getName());
		allView.setSortFields(sortFields);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		return allView;
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
		eventsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		eventsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("status");
		statusTypeField.setColumnName("STATUS");
		statusTypeField.setDataType(FieldType.STRING);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusCondition = new Condition();
		statusCondition.setField(statusTypeField);
		statusCondition.setOperator(StringOperators.IS);
		statusCondition.setValue(state);

		Criteria statusCriteria = new Criteria() ;
		statusCriteria.addAndCondition(statusCondition);

		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(ModuleFactory.getTenantsModule());
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition condition = new Condition();
		condition.setField(statusField);
		condition.setOperator(LookupOperator.LOOKUP);
		condition.setCriteriaValue(statusCriteria);

		return condition;
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
		assetView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllNewReadingRules() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getNewReadingRuleModule());

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		allView.setSortFields(sortFields);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	
	private static FacilioView getWorkflowLog() {

		FacilioView workflowLogView = new FacilioView();
		workflowLogView.setName("all");
		workflowLogView.setDisplayName(FacilioConstants.Workflow.WORKFLOW_LOG_VIEW);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		try {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.Workflow.WORKFLOW_LOG);
			createdTime.setModule(module);
		} catch (Exception e) {
			LOGGER.info(e);
		}

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		workflowLogView.setSortFields(sortFields);

		workflowLogView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return workflowLogView;
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
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return view;
	}

	private static FacilioView getRulesByStatusForNewRule(String name, String displayName, boolean status) {
		List<FacilioField> rulesFields = FieldFactory.getNewReadingRuleFields();
		FacilioField statusField = FieldFactory.getAsMap(rulesFields).get("status");

		Condition statusCondition = CriteriaAPI.getCondition(statusField, String.valueOf(status), BooleanOperators.IS);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(statusCondition);
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getNewReadingRuleModule());

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setCriteria(criteria);
		view.setSortFields(sortFields);
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	
	private static FacilioView getAllEnergyMetersView() {
		FacilioView allView = new FacilioView();
		allView.setName("energy");
		allView.setDisplayName("All Energy Meters");
		allView.setSortFields(getSortFields(FacilioConstants.ContextNames.ASSET));
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	
	private static FacilioView getAllVendorDocumentsView() {
		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getTenantsModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Vendor Documents");
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
		activeTenantsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		
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
		assetView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition preopen = CriteriaAPI.getCondition(statusField, CommonOperators.IS_EMPTY);
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
		unassignedWOView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		myRequestsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		allRequestsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		allRequestsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
	
	public static Condition getWorkPermitStatusCriteria(String status) {

		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("status");
		statusTypeField.setColumnName("STATUS");
		statusTypeField.setDataType(FieldType.STRING);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusCondition = new Condition();
		statusCondition.setField(statusTypeField);
		statusCondition.setOperator(StringOperators.IS);
		statusCondition.setValue(status);

		Criteria statusCriteria = new Criteria() ;
		statusCriteria.addAndCondition(statusCondition);

		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(ModuleFactory.getWorkPermitModule());
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition condition = new Condition();
		condition.setField(statusField);
		condition.setOperator(LookupOperator.LOOKUP);
		condition.setCriteriaValue(statusCriteria);

		return condition;
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
		preOpenTicketsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		preOpenTicketsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		openTicketsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return openTicketsView;
	}

	private static FacilioView getTenantOpenWorkOrders() {
		// All Open Tickets

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getOpenStatusCondition());

		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("tenantOpen");
		openTicketsView.setDisplayName("Open");
		openTicketsView.setCriteria(criteria);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		openTicketsView.setSortFields(sortFields);
		openTicketsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));

		return openTicketsView;
	}

	private static FacilioView getTenantClosedWorkOrders() {


		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("tenantClosed");
		openTicketsView.setDisplayName("Closed");
		openTicketsView.setCriteria(getClosedTicketsCriteria());


		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		openTicketsView.setSortFields(sortFields);
		openTicketsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));

		return openTicketsView;
	}

	private static FacilioView getTenantAllWorkOrders() {


		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("tenantAll");
		openTicketsView.setDisplayName("All");

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		openTicketsView.setSortFields(sortFields);
		openTicketsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));

		return openTicketsView;
	}

	private static FacilioView getVendorOpenWorkOrders() {
		// All Open Tickets

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getOpenStatusCondition());

		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("vendorOpen");
		openTicketsView.setDisplayName("Open");
		openTicketsView.setCriteria(criteria);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		openTicketsView.setSortFields(sortFields);
		openTicketsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.VENDOR_PORTAL)));

		return openTicketsView;
	}


	private static FacilioView getVendorClosedWorkOrders() {
		// All Closed Tickets

		FacilioView closedTicketsView = new FacilioView();
		closedTicketsView.setName("vendorClosed");
		closedTicketsView.setDisplayName("Closed");
		closedTicketsView.setCriteria(getClosedTicketsCriteria());


		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		closedTicketsView.setSortFields(sortFields);
		closedTicketsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.VENDOR_PORTAL)));

		return closedTicketsView;
	}


	public static FacilioView getRequestedStateApproval(String viewName, boolean isHidden) {
		
		FacilioModule module = ModuleFactory.getTicketsModule();
		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Criteria requestedStateCriteria = getRequestedStateCriteria(true);
		Condition requested = new Condition();
		requested.setField(statusField);
		requested.setOperator(LookupOperator.LOOKUP);
		requested.setCriteriaValue(requestedStateCriteria);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(requested);

		LookupField approvalStatusField = new LookupField();
		approvalStatusField.setName("approvalStatus");
		approvalStatusField.setColumnName("ARRPOVAL_STATE");
		approvalStatusField.setDataType(FieldType.LOOKUP);
		approvalStatusField.setModule(module);
		approvalStatusField.setLookupModule(ModuleFactory.getTicketStatusModule());
		Criteria c = new Criteria();
		c.addAndCondition(CriteriaAPI.getCondition(approvalStatusField, CommonOperators.IS_NOT_EMPTY));
		c.addAndCondition(CriteriaAPI.getCondition(approvalStatusField, requestedStateCriteria, LookupOperator.LOOKUP));
		criteria.orCriteria(c);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());

		FacilioView rejectedApproval = new FacilioView();
		rejectedApproval.setName(viewName);
		rejectedApproval.setDisplayName("Pending Approval");
		rejectedApproval.setCriteria(criteria);
		rejectedApproval.setHidden(isHidden);
		rejectedApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		rejectedApproval.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return rejectedApproval;
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

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		openTicketsView.setViewSharing(getSharingContext(appDomains));

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
		resolvedTicketsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		openTicketsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		overdueView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		openTicketsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		sourceTypeCondition.setValue(String.valueOf(TicketContext.SourceType.PREVENTIVE_MAINTENANCE.getIndex()));

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
		overdueView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		sourceTypeCondition.setValue(String.valueOf(TicketContext.SourceType.PREVENTIVE_MAINTENANCE.getIndex()));

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
		overdueView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		overdueView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		openTicketsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
	
	public static Condition getMyVendorsCondition() {
		LookupField userField = new LookupField();
		userField.setName("registeredBy");
		userField.setColumnName("REGISTERED_BY");
		userField.setDataType(FieldType.LOOKUP);
		userField.setModule(ModuleFactory.getVendorsModule());
		userField.setSpecialType(FacilioConstants.ContextNames.USERS);

		Condition myUserCondition = new Condition();
		myUserCondition.setField(userField);
		myUserCondition.setOperator(PickListOperators.IS);
		myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

		return myUserCondition;
	}
	
	public static Condition getMyVistorInvitesCondition() {
		LookupField userField = new LookupField();
		userField.setName("requestedBy");
		userField.setColumnName("REQUESTED_BY");
		userField.setDataType(FieldType.LOOKUP);
		userField.setModule(ModuleFactory.getVisitorLoggingModule());
		userField.setSpecialType(FacilioConstants.ContextNames.USERS);

		Condition myUserCondition = new Condition();
		myUserCondition.setField(userField);
		myUserCondition.setOperator(PickListOperators.IS);
		myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

		return myUserCondition;
	}
	
	public static Condition getMyBaseVisitorInvitesCondition() {
		LookupField userField = new LookupField();
		userField.setName("requestedBy");
		userField.setColumnName("REQUESTED_BY");
		userField.setDataType(FieldType.LOOKUP);
		userField.setModule(ModuleFactory.getBaseVisitorLogCheckInModule());
		userField.setSpecialType(FacilioConstants.ContextNames.USERS);

		Condition myUserCondition = new Condition();
		myUserCondition.setField(userField);
		myUserCondition.setOperator(PickListOperators.IS);
		myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

		return myUserCondition;
	}

	public static Condition getPreregisteredCondition() {
		BooleanField preregisteredField = new BooleanField();
		preregisteredField.setName("isPreregistered");
		preregisteredField.setColumnName("IS_PREREGISTERED");
		preregisteredField.setDataType(FieldType.BOOLEAN);
		preregisteredField.setModule(ModuleFactory.getVisitorLoggingModule());

		Condition inviteCondition = new Condition();
		inviteCondition.setField(preregisteredField);
		inviteCondition.setOperator(BooleanOperators.IS);
		inviteCondition.setValue("true");

		return inviteCondition;
	}
	
	public static Condition getMyWorkPermitsCondition() {
		LookupField userField = new LookupField();
		userField.setName("requestedBy");
		userField.setColumnName("REQUESTED_BY");
		userField.setDataType(FieldType.LOOKUP);
		userField.setModule(ModuleFactory.getWorkPermitModule());
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
		alarmSourceCondition.setValue(String.valueOf(TicketContext.SourceType.ALARM.getIndex()));

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
		fireSafetyWOView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		sourceCondition.setValue(String.valueOf(sourceType.getIndex()));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(sourceCondition);

		return criteria;
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
		typeAlarms.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
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
		typeAlarms.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return allView;
	}

	private static FacilioView getAllTicketCategory() {

		FacilioModule ticketCategoryMod = ModuleFactory.getTicketCategoryModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ticketCategoryMod);
		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioField nameField = new FacilioField();
		nameField.setName("name");
		nameField.setDataType(FieldType.STRING);
		nameField.setColumnName("NAME");
		nameField.setModule(ticketCategoryMod);
		ViewField nameViewFiled = new ViewField();
		nameViewFiled.setField(nameField);

		FacilioField descField = new FacilioField();
		descField.setName("description");
		descField.setDataType(FieldType.STRING);
		descField.setColumnName("DESCRIPTION");
		descField.setModule(ticketCategoryMod);
		ViewField descViewFiled = new ViewField();
		descViewFiled.setField(nameField);

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		allView.setSortFields(sortFields);
		allView.setFields(Arrays.asList(nameViewFiled, descViewFiled));

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	
	private static FacilioView getVendorWorkOrders() {

		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView allView = new FacilioView();
		allView.setName("vendorWorkorder");
		allView.setDisplayName("All Workorders");
		allView.setSortFields(sortFields);
		allView.setHidden(true);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.VENDOR_PORTAL)));

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

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return allView;
	}

	private static FacilioView getAllPreventiveWorkOrders() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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

		reportView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return reportView;
	}

	private static FacilioView getMyRequestWorkorders(String viewName) {

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
		myAllWorkordersView.setName(viewName);
		myAllWorkordersView.setDisplayName("My Work Orders");
		myAllWorkordersView.setCriteria(criteria);
		myAllWorkordersView.setSortFields(sortFields);
		myAllWorkordersView.setHidden(true);

		myAllWorkordersView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return myAllWorkordersView;
	}

	private static FacilioView getMyAllRequestWorkorders() {

		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView myAllWorkordersView = new FacilioView();
		myAllWorkordersView.setName("myAllWo");
		myAllWorkordersView.setDisplayName("All");
		myAllWorkordersView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.SERVICE_PORTAL);
		appDomains.add(AppDomain.AppDomainType.EMPLOYEE_PORTAL);

		myAllWorkordersView.setViewSharing(getSharingContext(appDomains));

		return myAllWorkordersView;
	}

	private static FacilioView getMyOpenRequestWorkorders() {

		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView myWorkordersView = new FacilioView();
		myWorkordersView.setName("myOpenWo");
		myWorkordersView.setDisplayName("Open");
		myWorkordersView.setSortFields(sortFields);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getOpenStatusCondition());
		myWorkordersView.setCriteria(criteria);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.SERVICE_PORTAL);
		appDomains.add(AppDomain.AppDomainType.EMPLOYEE_PORTAL);

		myWorkordersView.setViewSharing(getSharingContext(appDomains));

		return myWorkordersView;
	}

	private static FacilioView getMyClosedRequestWorkorders() {

		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView myWorkordersView = new FacilioView();
		myWorkordersView.setName("myClosedWo");
		myWorkordersView.setDisplayName("Closed");
		myWorkordersView.setSortFields(sortFields);

		myWorkordersView.setCriteria(getClosedTicketsCriteria());

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.SERVICE_PORTAL);
		appDomains.add(AppDomain.AppDomainType.EMPLOYEE_PORTAL);

		myWorkordersView.setViewSharing(getSharingContext(appDomains));

		return myWorkordersView;
	}

	private static FacilioView getTenantWorkorders(String viewName) {

		FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(workOrdersModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView allView = new FacilioView();
		allView.setName(viewName);
		allView.setDisplayName("Tenant Workorders");
		allView.setSortFields(sortFields);
		allView.setHidden(true);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));


		return allView;
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
		subviewDetail.put("viewSharing", getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "overdue");
		subviewDetail.put("displayName", "Overdue");
		subviewDetail.put("criteria", getCriteriaForView("overdue", workorderModule));
		subviewDetail.put("viewSharing", getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "unassigned");
		subviewDetail.put("displayName", "Unassigned");
		subviewDetail.put("criteria", getCriteriaForView("unassigned", workorderModule));
		subviewDetail.put("viewSharing", getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
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
		subviewDetail.put("viewSharing", getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "duetoday");
		subviewDetail.put("displayName", "Due Today");
		subviewDetail.put("criteria", getCriteriaForView("duetoday", workorderModule));
		subviewDetail.put("viewSharing", getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		subViews.add(subviewDetail);

		subViewsMap.put("workorder-myopen", subViews);

		// My SubViews
		subViews = new ArrayList<>();

		subviewDetail = new HashMap<>();
		subViews.add(getAllSubViewDetail(workorderModule));
		subviewDetail.put("name", "overdue");
		subviewDetail.put("displayName", "Overdue");
		subviewDetail.put("criteria", getCriteriaForView("overdue", workorderModule));
		subviewDetail.put("viewSharing", getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "duetoday");
		subviewDetail.put("displayName", "Due Today");
		subviewDetail.put("criteria", getCriteriaForView("duetoday", workorderModule));
		subviewDetail.put("viewSharing", getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		subViews.add(subviewDetail);

		subViewsMap.put("workorder-open", subViews);

		// Resolved
		subViews = new ArrayList<>();

		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "my");
		subviewDetail.put("displayName", "My Resolved");
		subviewDetail.put("criteria", getCriteriaForView("my", workorderModule));
		subviewDetail.put("viewSharing", getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "myteam");
		subviewDetail.put("displayName", "My Team Resolved");
		subviewDetail.put("criteria", getCriteriaForView("myteam", workorderModule));
		subviewDetail.put("viewSharing", getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		subViews.add(subviewDetail);
		subViews.add(getAllSubViewDetail(workorderModule));

		subViewsMap.put("workorder-resolved", subViews);

		// Closed
		subViews = new ArrayList<>();

		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "my");
		subviewDetail.put("displayName", "My Closed");
		subviewDetail.put("criteria", getCriteriaForView("my", workorderModule));
		subviewDetail.put("viewSharing", getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		subViews.add(subviewDetail);
		subviewDetail = new HashMap<>();
		subviewDetail.put("name", "myteam");
		subviewDetail.put("displayName", "My Team Closed");
		subviewDetail.put("criteria", getCriteriaForView("myteam", workorderModule));
		subviewDetail.put("viewSharing", getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		subViews.add(subviewDetail);
		subViews.add(getAllSubViewDetail(workorderModule));

		subViewsMap.put("workorder-closed", subViews);

		return subViewsMap;
	}

	public static List<Map<String, Object>> getSubViewsCriteria(String moduleName, String viewName) throws Exception {
		ApplicationContext applicationContext = getApplicationContext();
		LOGGER.info(String.format("ViewFactoryTracking - getSubViewsCriteria() - ModuleName - %s ViewName - %s AppId - %d AppName - %s", moduleName, viewName, applicationContext.getId(), applicationContext.getLinkName()));
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

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getInventryModule());

		FacilioView staleParts = new FacilioView();
		staleParts.setName("stale");
		staleParts.setDisplayName("Stale");
		staleParts.setCriteria(criteria);
		staleParts.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		staleParts.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return staleParts;
	}

	private static FacilioView getUnderStockedPartsView() {

		Criteria criteria = getUnderstockedPartCriteria(ModuleFactory.getInventoryModule());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getInventoryModule());

		FacilioView staleParts = new FacilioView();
		staleParts.setName("understocked");
		staleParts.setDisplayName("Under Stocked");
		staleParts.setCriteria(criteria);
		staleParts.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		staleParts.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	private static FacilioView getAllInventoryCategory() {

		FacilioModule inventoryCategoryModule = ModuleFactory.getInventoryCategoryModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("name");
		createdTime.setDataType(FieldType.STRING);
		createdTime.setColumnName("NAME");
		createdTime.setModule(inventoryCategoryModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Inventory Category");
		allView.setSortFields(sortFields);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return allView;
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

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllWorkOrderItems() {

		FacilioModule workOrderItemsModule = ModuleFactory.getWorkOrderItemsModule();
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Work Order Items");
		allView.setModuleName(workOrderItemsModule.getName());

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	private static FacilioView getAllWorkOrderItemsDetailsView() {
		FacilioModule workOrderItemsModule = ModuleFactory.getWorkOrderItemsModule();

		FacilioView detailsView = new FacilioView();
		detailsView.setName("details");
		detailsView.setDisplayName("Work Order Items Details");
		detailsView.setModuleName(workOrderItemsModule.getName());

		detailsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return detailsView;
	}

	private static FacilioView getAllWorkOrderTools() {

		FacilioModule workOrderToolsModule = ModuleFactory.getWorkOrderToolsModule();

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Work Order Tools");
		allView.setModuleName(workOrderToolsModule.getName());

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllWorkOrderToolsDetailsView() {
		FacilioModule workOrderToolsModule = ModuleFactory.getWorkOrderToolsModule();

		FacilioView detailsView = new FacilioView();
		detailsView.setName("details");
		detailsView.setDisplayName("Work Order Tools Details");
		detailsView.setModuleName(workOrderToolsModule.getName());

		detailsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return detailsView;
	}

	private static FacilioView getAllWorkOrderService() {

		FacilioModule workOrderServiceModule = ModuleFactory.getWorkOrderServiceModule();

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Work Order Service");
		allView.setModuleName(workOrderServiceModule.getName());

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllWorkOrderServiceDetailsView() {
		FacilioModule workOrderServiceModule = ModuleFactory.getWorkOrderServiceModule();

		FacilioView detailsView = new FacilioView();
		detailsView.setName("details");
		detailsView.setDisplayName("Work Order Service Details");
		detailsView.setModuleName(workOrderServiceModule.getName());

		detailsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return detailsView;
	}
	private static FacilioView getAllVendors() {

		FacilioModule itemsModule = ModuleFactory.getVendorsModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(itemsModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Vendors");
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		return allView;
	}
	private static FacilioView getAllWorkOrderPlannedItems() {

		FacilioModule plannedItemsModule = ModuleFactory.getWorkOrderPlannedItemsModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(plannedItemsModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All WorkOrder Planned Items");
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		return allView;
	}

	private static FacilioView getWorkOrderPlannedItemsDetails() {

		FacilioModule plannedItemsModule = ModuleFactory.getWorkOrderPlannedItemsModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(plannedItemsModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView allView = new FacilioView();
		allView.setName("details");
		allView.setDisplayName("WorkOrder Planned Item Details");
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		return allView;
	}
	private static FacilioView getAllWorkOrderPlannedTools() {

		FacilioModule plannedToolsModule = ModuleFactory.getWorkOrderPlannedToolsModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(plannedToolsModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All WorkOrder Planned Tools");
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		return allView;
	}
	private static FacilioView getWorkOrderPlannedToolsDetails() {

		FacilioModule plannedToolsModule = ModuleFactory.getWorkOrderPlannedToolsModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(plannedToolsModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView allView = new FacilioView();
		allView.setName("details");
		allView.setDisplayName("WorkOrder Planned Tool Details");
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		return allView;
	}
	private static FacilioView getAllWorkOrderPlannedServices() {

		FacilioModule plannedServicesModule = ModuleFactory.getWorkOrderPlannedServicesModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(plannedServicesModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All WorkOrder Planned Services");
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		return allView;
	}
	private static FacilioView getWorkOrderPlannedServicesDetails() {

		FacilioModule plannedServicesModule = ModuleFactory.getWorkOrderPlannedServicesModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(plannedServicesModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("WorkOrder Planned Service Details");
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		return allView;
	}
		public static Condition getMyNonInsuredVendorsCondition() {
			FacilioField hasInsuranceField = new LookupField();
			hasInsuranceField.setName("hasInsurance");
			hasInsuranceField.setColumnName("HAS_INSURANCE");
			hasInsuranceField.setDataType(FieldType.BOOLEAN);
			hasInsuranceField.setModule(ModuleFactory.getVendorsModule());

			Condition hasInsuranceCondition = new Condition();
			hasInsuranceCondition.setField(hasInsuranceField);
			hasInsuranceCondition.setOperator(BooleanOperators.IS);
			hasInsuranceCondition.setValue(String.valueOf(false));

			return hasInsuranceCondition;
		}
		
		private static FacilioView getMyRequestedVendors() {

			FacilioModule vendorModule = ModuleFactory.getVendorsModule();

			Criteria criteria = new Criteria();
			criteria.addAndCondition(getMyVendorsCondition());
			criteria.addAndCondition(getModuleTicketStatusCriteria("Requested", vendorModule));

			FacilioField createdTime = new FacilioField();
			createdTime.setName("sysCreatedTime");
			createdTime.setDataType(FieldType.NUMBER);
			createdTime.setColumnName("SYS_CREATED_TIME");
			createdTime.setModule(vendorModule);

			List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

			FacilioView myVendorView = new FacilioView();
			myVendorView.setName("myRequestedVendors");
			myVendorView.setDisplayName("Requested");
			myVendorView.setCriteria(criteria);
			myVendorView.setSortFields(sortFields);
			myVendorView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));
			myVendorView.setHidden(true);

			return myVendorView;
		}

	private static FacilioView getTenantRequestedVendors() {

		FacilioModule vendorModule = ModuleFactory.getVendorsModule();

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getModuleTicketStatusCriteria("Requested", vendorModule));

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(vendorModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView myVendorView = new FacilioView();
		myVendorView.setName("tenantRequestedVendors");
		myVendorView.setDisplayName("Requested");
		myVendorView.setCriteria(criteria);
		myVendorView.setSortFields(sortFields);
		myVendorView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));

		return myVendorView;
	}


	private static FacilioView getMyAllVendors() {

		FacilioModule vendorModule = ModuleFactory.getVendorsModule();

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getMyVendorsCondition());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(vendorModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView myVendorView = new FacilioView();
		myVendorView.setName("myAll");
		myVendorView.setDisplayName("All");
		myVendorView.setCriteria(criteria);
		myVendorView.setSortFields(sortFields);
		myVendorView.setHidden(true);

		myVendorView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));


		return myVendorView;
	}

	private static FacilioView getTenantAllVendors() {

		FacilioModule vendorModule = ModuleFactory.getVendorsModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("SYS_CREATED_TIME");
		createdTime.setModule(vendorModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView myVendorView = new FacilioView();
		myVendorView.setName("tenantAll");
		myVendorView.setDisplayName("All");
		myVendorView.setSortFields(sortFields);

		myVendorView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));


		return myVendorView;
	}




	private static FacilioView getActiveVisitorInvites() {
			
			Criteria criteria = new Criteria();
			//criteria.addAndCondition(getMyVistorInvitesCondition());
			criteria.addAndCondition(getActiveInvitesCondition());
			criteria.addAndCondition(getPreregisteredCondition());
		FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();


		FacilioField expCheckInTime = FieldFactory.getField("expectedCheckInTime", "EXPECTED_CHECKIN_TIME", inviteVisitorModule,FieldType.DATE_TIME);

		List<SortField> sortFields = Arrays.asList(new SortField(expCheckInTime, false));

			FacilioView myVisitorInvitesView = new FacilioView();
			myVisitorInvitesView.setName("invite_myInvites");
			myVisitorInvitesView.setDisplayName("Active");
			myVisitorInvitesView.setCriteria(criteria);
			myVisitorInvitesView.setSortFields(sortFields);

			myVisitorInvitesView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));


		return myVisitorInvitesView;
		}
	
	private static FacilioView getActiveInviteVisitorInvites() {
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getActiveInviteVisitorInvitesCondition());

		FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();
		FacilioField expCheckInTime = FieldFactory.getField("expectedCheckInTime", "EXPECTED_CHECKIN_TIME", inviteVisitorModule,FieldType.DATE_TIME);

		List<SortField> sortFields = Arrays.asList(new SortField(expCheckInTime, false));

		FacilioView myVisitorInvitesView = new FacilioView();
		myVisitorInvitesView.setName("invite_myInvites");
		myVisitorInvitesView.setDisplayName("Active");
		myVisitorInvitesView.setCriteria(criteria);
		myVisitorInvitesView.setSortFields(sortFields);

		myVisitorInvitesView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));


	return myVisitorInvitesView;
	}

	private static FacilioView getMyCurrentVisitorInvites() {

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getMyVistorInvitesCondition());

		FacilioField checkin = FieldFactory.getField("checkInTime","CHECKIN_TIME", FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(checkin, CommonOperators.IS_EMPTY));

		FacilioField expectedCheckin = FieldFactory.getField("expectedCheckInTime","EXPECTED_CHECKIN_TIME", FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(expectedCheckin, DateOperators.TILL_NOW));
		FacilioField expectedCheckout = FieldFactory.getField("expectedCheckOutTime","EXPECTED_CHECKOUT_TIME", FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(expectedCheckout, DateOperators.UPCOMING));


		List<SortField> sortFields = Arrays.asList(new SortField(expectedCheckin, true));

		FacilioView myVisitorInvitesView = new FacilioView();
		myVisitorInvitesView.setName("myCurrent");
		myVisitorInvitesView.setDisplayName("My Current Invites");
		myVisitorInvitesView.setCriteria(criteria);
		myVisitorInvitesView.setSortFields(sortFields);

		myVisitorInvitesView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));


		return myVisitorInvitesView;
	}
	
	private static FacilioView getExpiredInviteVisitorInvites() {

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getMyBaseVisitorInvitesCondition());
		criteria.addAndCondition(getExpiredInviteVisitsCondition());
		
		FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();
//		FacilioField checkin = FieldFactory.getField("checkInTime","CHECKIN_TIME", FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(checkin, CommonOperators.IS_EMPTY));
		FacilioField expectedCheckin = FieldFactory.getField("expectedCheckInTime","EXPECTED_CHECKIN_TIME",inviteVisitorModule, FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(expectedCheckin, DateOperators.TILL_NOW));

		List<SortField> sortFields = Arrays.asList(new SortField(expectedCheckin, true));

		FacilioView myVisitorInvitesView = new FacilioView();
		myVisitorInvitesView.setName("invite_myExpired");
		myVisitorInvitesView.setDisplayName("Expired");
		myVisitorInvitesView.setCriteria(criteria);
		myVisitorInvitesView.setSortFields(sortFields);

		myVisitorInvitesView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));


		return myVisitorInvitesView;
	}

	private static FacilioView getVendorCurrentVisitsView() {

		Criteria criteria = new Criteria();

		FacilioField checkin = FieldFactory.getField("checkInTime","CHECKIN_TIME", FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(checkin, CommonOperators.IS_EMPTY));

		FacilioField expectedCheckin = FieldFactory.getField("expectedCheckInTime","EXPECTED_CHECKIN_TIME", FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(expectedCheckin, DateOperators.TILL_NOW));
		FacilioField expectedCheckout = FieldFactory.getField("expectedCheckOutTime","EXPECTED_CHECKOUT_TIME", FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(expectedCheckout, DateOperators.UPCOMING));


		List<SortField> sortFields = Arrays.asList(new SortField(expectedCheckin, true));

		FacilioView myVisitorInvitesView = new FacilioView();
		myVisitorInvitesView.setName("vendorCurrentVisits");
		myVisitorInvitesView.setDisplayName("Vendor Current Invites");
		myVisitorInvitesView.setCriteria(criteria);
		myVisitorInvitesView.setSortFields(sortFields);

		myVisitorInvitesView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.VENDOR_PORTAL)));


		return myVisitorInvitesView;
	}
	
	private static FacilioView getMyActiveVisitorInvites() {

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getMyVistorInvitesCondition());
		criteria.addAndCondition(getActiveInvitesCondition());

		FacilioModule visitorInvitesModule = ModuleFactory.getVisitorLoggingModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("expectedCheckInTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("EXPECTED_CHECKIN_TIME");
		createdTime.setModule(visitorInvitesModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

		FacilioView myVisitorInvitesView = new FacilioView();
		myVisitorInvitesView.setName("myActive");
		myVisitorInvitesView.setDisplayName("Active");
		myVisitorInvitesView.setCriteria(criteria);
		myVisitorInvitesView.setSortFields(sortFields);
		myVisitorInvitesView.setHidden(true);

		myVisitorInvitesView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));

		return myVisitorInvitesView;
	}
	
	private static FacilioView getMyActiveInviteVisitorInvites() {

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getMyBaseVisitorInvitesCondition());
		criteria.addAndCondition(getActiveInviteVisitorInvitesCondition());

		FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("expectedCheckInTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("EXPECTED_CHECKIN_TIME");
		createdTime.setModule(inviteVisitorModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

		FacilioView myVisitorInvitesView = new FacilioView();
		myVisitorInvitesView.setName("myActive");
		myVisitorInvitesView.setDisplayName("Active");
		myVisitorInvitesView.setCriteria(criteria);
		myVisitorInvitesView.setSortFields(sortFields);
		myVisitorInvitesView.setHidden(true);

		myVisitorInvitesView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));

		return myVisitorInvitesView;
	}

	private static FacilioView getMyExpiredVisitorInvites() {

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getExpiredInvitesCondition());
		FacilioField expectedCheckin = FieldFactory.getField("expectedCheckInTime","EXPECTED_CHECKIN_TIME", FieldType.DATE_TIME);

		List<SortField> sortFields = Arrays.asList(new SortField(expectedCheckin, true));

		FacilioView myVisitorInvitesView = new FacilioView();
		myVisitorInvitesView.setName("myExpired");
		myVisitorInvitesView.setDisplayName("Expired");
		myVisitorInvitesView.setCriteria(criteria);
		myVisitorInvitesView.setSortFields(sortFields);
		myVisitorInvitesView.setHidden(true);

		myVisitorInvitesView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));


		return myVisitorInvitesView;
	}
	
	private static FacilioView getMyExpiredInviteVisitorInvites() {

		Criteria criteria = new Criteria();
	//ß	criteria.addAndCondition(getMyVistorInvitesCondition());
		criteria.addAndCondition(getExpiredInviteVisitsCondition());
//		FacilioField checkin = FieldFactory.getField("checkInTime","CHECKIN_TIME", FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(checkin, CommonOperators.IS_EMPTY));
		FacilioField expectedCheckin = FieldFactory.getField("expectedCheckInTime","EXPECTED_CHECKIN_TIME", FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(expectedCheckin, DateOperators.TILL_NOW));

		List<SortField> sortFields = Arrays.asList(new SortField(expectedCheckin, true));

		FacilioView myVisitorInvitesView = new FacilioView();
		myVisitorInvitesView.setName("myExpired");
		myVisitorInvitesView.setDisplayName("Expired");
		myVisitorInvitesView.setCriteria(criteria);
		myVisitorInvitesView.setSortFields(sortFields);
		myVisitorInvitesView.setHidden(true);

		myVisitorInvitesView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));


		return myVisitorInvitesView;
	}
	
	private static FacilioView getExpiredVisitorInvites() {

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getMyVistorInvitesCondition());
		criteria.addAndCondition(getExpiredInvitesCondition());
		FacilioField expectedCheckin = FieldFactory.getField("expectedCheckInTime","EXPECTED_CHECKIN_TIME", FieldType.DATE_TIME);
		criteria.addAndCondition(getPreregisteredCondition());

		List<SortField> sortFields = Arrays.asList(new SortField(expectedCheckin, true));

		FacilioView myVisitorInvitesView = new FacilioView();
		myVisitorInvitesView.setName("invite_myExpired");
		myVisitorInvitesView.setDisplayName("Expired");
		myVisitorInvitesView.setCriteria(criteria);
		myVisitorInvitesView.setSortFields(sortFields);

		myVisitorInvitesView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));


		return myVisitorInvitesView;
	}

	private static FacilioView getVendorExpiredVisitorInvites() {

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getExpiredInvitesCondition());
//		FacilioField checkin = FieldFactory.getField("checkInTime","CHECKIN_TIME", FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(checkin, CommonOperators.IS_EMPTY));
		FacilioField expectedCheckin = FieldFactory.getField("expectedCheckInTime","EXPECTED_CHECKIN_TIME", FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(expectedCheckin, DateOperators.TILL_NOW));

		List<SortField> sortFields = Arrays.asList(new SortField(expectedCheckin, true));

		FacilioView myVisitorInvitesView = new FacilioView();
		myVisitorInvitesView.setName("invite_vendorExpired");
		myVisitorInvitesView.setDisplayName("Expired");
		myVisitorInvitesView.setCriteria(criteria);
		myVisitorInvitesView.setSortFields(sortFields);

		myVisitorInvitesView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.VENDOR_PORTAL)));


		return myVisitorInvitesView;
	}
	
	private static FacilioView getVendorExpiredInviteVisitorInvites() {

		Criteria criteria = new Criteria();
		criteria.addAndCondition(getExpiredInviteVisitsCondition());
//		FacilioField checkin = FieldFactory.getField("checkInTime","CHECKIN_TIME", FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(checkin, CommonOperators.IS_EMPTY));
		FacilioField expectedCheckin = FieldFactory.getField("expectedCheckInTime","EXPECTED_CHECKIN_TIME", FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(expectedCheckin, DateOperators.TILL_NOW));

		List<SortField> sortFields = Arrays.asList(new SortField(expectedCheckin, true));

		FacilioView myVisitorInvitesView = new FacilioView();
		myVisitorInvitesView.setName("vendorExpired");
		myVisitorInvitesView.setDisplayName("Vendor Expired Invites");
		myVisitorInvitesView.setCriteria(criteria);
		myVisitorInvitesView.setSortFields(sortFields);

		myVisitorInvitesView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.VENDOR_PORTAL)));


		return myVisitorInvitesView;
	}
	
		private static FacilioView getMyAllVisitorInvites() {
			
			Criteria criteria = new Criteria();
			criteria.addAndCondition(getMyVistorInvitesCondition());
			criteria.addAndCondition(getPastVisitorLogStatusCriteria("Upcoming"));
			FacilioModule visitorInvitesModule = ModuleFactory.getVisitorLoggingModule();

			FacilioField createdTime = new FacilioField();
			createdTime.setName("sysCreatedTime");
			createdTime.setDataType(FieldType.DATE_TIME);
			createdTime.setColumnName("SYS_CREATED_TIME");
			createdTime.setModule(visitorInvitesModule);

			List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

			FacilioView myVisitorInvitesView = new FacilioView();
			myVisitorInvitesView.setName("myAll");
			myVisitorInvitesView.setDisplayName("My Visitor Invites");
			myVisitorInvitesView.setCriteria(criteria);
			myVisitorInvitesView.setSortFields(sortFields);

			myVisitorInvitesView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));

			return myVisitorInvitesView;
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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
	
	public static FacilioView getModuleView(FacilioModule childModule, String parentModuleName) throws Exception {
		FacilioView moduleView = new FacilioView();
		moduleView.setName(childModule.getName());
		moduleView.setDisplayName("All " + childModule.getDisplayName() + "s");
		moduleView.setModuleId(childModule.getModuleId());
		moduleView.setModuleName(childModule.getName());
		moduleView.setDefault(true);
		moduleView.setOrder(1);
		
		moduleView.setFields(ColumnFactory.getColumns(parentModuleName, "default"));
		moduleView.setSortFields(getSortFields(parentModuleName));

		ApplicationContext applicationContext = getApplicationContext();
		LOGGER.info(String.format("ViewFactoryTracking - getModuleView() - ChildModuleName - %s ParentModuleName - %s AppId - %d AppName - %s", childModule.getName(), parentModuleName, applicationContext.getId(), applicationContext.getLinkName()));

		return moduleView;
	}

	public static ApplicationContext getApplicationContext() throws Exception {
		ApplicationContext applicationContext = AccountUtil.getCurrentApp();
		if (applicationContext == null) {
			applicationContext = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
		}
		return applicationContext;
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
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getItemTransactionsModule());

		FacilioView requestedItemApproval = new FacilioView();
		requestedItemApproval.setName("pendingitem");
		requestedItemApproval.setDisplayName("Pending Item Approvals");
		requestedItemApproval.setCriteria(criteria);
//		requestedItemApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		requestedItemApproval.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return requestedItemApproval;
	}
	
	private static FacilioView getAllItemApproval() {

		Criteria criteria = getAllItemApprovalStateCriteria();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getItemTransactionsModule());

		FacilioView rejectedApproval = new FacilioView();
		rejectedApproval.setName("allitem");
		rejectedApproval.setDisplayName("All Item Approvals");
		rejectedApproval.setCriteria(criteria);
		rejectedApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		rejectedApproval.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		
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
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getToolTransactionsModule());

		FacilioView requestedItemApproval = new FacilioView();
		requestedItemApproval.setName("pendingtool");
		requestedItemApproval.setDisplayName("Pending Tool Approvals");
		requestedItemApproval.setCriteria(criteria);
		requestedItemApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		requestedItemApproval.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return requestedItemApproval;
	}
	
	private static FacilioView getAllToolApproval() {

		Criteria criteria = getAllToolApprovalStateCriteria();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("sysCreatedTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getToolTransactionsModule());

		FacilioView rejectedApproval = new FacilioView();
		rejectedApproval.setName("alltool");
		rejectedApproval.setDisplayName("All Tool Approvals");
		rejectedApproval.setCriteria(criteria);
		rejectedApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		rejectedApproval.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		localId.setName("id");
		localId.setColumnName("ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getPurchaseRequestModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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

		statusView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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

		statusView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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

		overDueRequest.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		localId.setName("id");
		localId.setColumnName("ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getPurchaseOrderModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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

		statusView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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

		statusView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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

		overDueOrder.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		ongoingPurchaseOrder.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		statusView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllContractView(FacilioModule module) {
		
		FacilioField parentIdField = new FacilioField();
		parentIdField.setName("parentId");
		parentIdField.setColumnName("PARENT_ID");
		parentIdField.setDataType(FieldType.NUMBER);
		parentIdField.setModule(module);
		
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		allView.setCriteria(getAllContractListCriteria());
		allView.setSortFields(Arrays.asList(new SortField(parentIdField, false)));
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	
	private static FacilioView getAllDocumentsView() {
		
		FacilioField sysCreatedtimeField = new FacilioField();
		sysCreatedtimeField.setName("sysCreatedTime");
		sysCreatedtimeField.setColumnName("SYS_CREATED_TIME");
		sysCreatedtimeField.setDataType(FieldType.DATE_TIME);
		sysCreatedtimeField.setModule(ModuleFactory.getVendorDocumentsModule());
		
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All");
		allView.setSortFields(Arrays.asList(new SortField(sysCreatedtimeField, false)));

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.TENANT_PORTAL);
		appDomains.add(AppDomain.AppDomainType.FACILIO);

		allView.setViewSharing(getSharingContext(appDomains));

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
		//allView.setCriteria(getContractListCriteria());
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		//allView.setCriteria(getContractListCriteria());
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		//allView.setCriteria(getContractListCriteria());
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		//allView.setCriteria(getContractListCriteria());
		allView.setSortFields(Arrays.asList(new SortField(localId, false)));

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
//		allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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

		statusView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllInventoryRequestLineItemsView() {
		FacilioModule invReqLineItems = ModuleFactory.getInventoryRequestLineItemsModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Inventory Request Line Items");
		allView.setModuleName(invReqLineItems.getName());
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));



		return allView;
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

		statusView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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

		statusView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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

		statusView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		allView.setSortFields(Arrays.asList(new SortField(name, true)));

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllReadingAlarmViews() {
		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());

		Criteria criteria = new Criteria();
		Condition tillDateAlarm = getOnlyTillDateAlarm();
		criteria.addAndCondition(tillDateAlarm);

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Faults");
		allView.setModuleName("newreadingalarm");
		allView.setCriteria(criteria);
		allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getOperationalAlarmViews() {
		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Alarms");
		allView.setModuleName(FacilioConstants.ContextNames.OPERATION_ALARM);
		allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAgentAlarmOccurrenceViews() {
		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());

		FacilioView allView = new FacilioView();
		allView.setName("agentAll");
		allView.setDisplayName("All Alarms");
		allView.setModuleName("agentAlarm");
		allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		allView.setDefault(true);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getMultivariateAnomalyAlarmOccurrenceViews() {
		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());

		FacilioView allView = new FacilioView();
		allView.setName("mlmvaAlarms");
		allView.setDisplayName("All Alarms");
		allView.setModuleName("multivariateanomalyalarm");
		allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		allView.setDefault(true);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	public static Condition getOnlyTillDateAlarm() {
		// To get alarm list till now as in demo account alarm for the whole week will be avaiable
		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());


		Condition alarmCondition = new Condition();
		alarmCondition.setField(createdTime);
		alarmCondition.setOperator(DateOperators.TILL_NOW);

		return alarmCondition;
	}

	private static FacilioView getReadingAlarmSeverity(String name, String displayName, String severity, boolean equals) {

		Condition alarmCondition = getReadingAlarmSeverityCondition(severity, equals);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(alarmCondition);
		Condition tillDateAlarm = getOnlyTillDateAlarm();
		criteria.addAndCondition(tillDateAlarm);

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
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return view;
	}

	private static FacilioView getOperationalAlarmSeverity(String name, String displayName, String severity, boolean equals) {

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
		view.setModuleName(FacilioConstants.ContextNames.OPERATION_ALARM);
		view.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		view.setModuleName("bmsalarm");
		view.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		view.setModuleName("bmsalarm");
		view.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		view.setModuleName("bmsalarm");
		view.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		view.setDefault(true);

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		view.setModuleName("bmsalarm");
		view.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return view;
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
		Condition tillDateAlarm = getOnlyTillDateAlarm();
		criteria.addAndCondition(tillDateAlarm);

		FacilioView typeAlarms = new FacilioView();
		typeAlarms.setName("unacknowledged");
		typeAlarms.setDisplayName("Unacknowledged");
		typeAlarms.setCriteria(criteria);
		typeAlarms.setModuleName("newreadingalarm");
		typeAlarms.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		typeAlarms.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		typeAlarms.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		typeAlarms.setModuleName("bmsalarm");
		typeAlarms.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		typeAlarms.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		typeAlarms.setModuleName("bmsalarm");
		typeAlarms.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		typeAlarms.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	public static Criteria getContractListCriteria() {

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

	private static Criteria getAllContractListCriteria() {

		FacilioField revisionNumberField = new FacilioField();
		revisionNumberField.setName("revisionNumber");
		revisionNumberField.setColumnName("REVISION_NUMBER");
		revisionNumberField.setDataType(FieldType.NUMBER);
		revisionNumberField.setModule(ModuleFactory.getContractsModule());

		Condition revisionNumberCond = new Condition();
		revisionNumberCond.setField(revisionNumberField);
		revisionNumberCond.setOperator(NumberOperators.EQUALS);
		revisionNumberCond.setValue("0");
		
		Criteria criteria = new Criteria ();
		criteria.addAndCondition(revisionNumberCond);
		
		return criteria;
	}

	public static FacilioView getCustomModuleAllView(FacilioModule moduleObj) throws Exception {
		FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", moduleObj);
		
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All " + moduleObj.getDisplayName());
		allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		allView.setDefault(true);

//		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	private static FacilioView getAllReservationView() {
		
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Reservations");
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return view;
	}

	private static FacilioView getThisWeekReservationView() {
		FacilioView view = getScheduledReservationView();
		view.setName("thisweek");
		view.setDisplayName("This Week");
		view.getCriteria().addAndCondition(CriteriaAPI.getCondition(getReservationScheduledTimeField(), DateOperators.CURRENT_WEEK));

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return view;
	}

	private static FacilioView getNextWeekReservationView() {
		FacilioView view = getScheduledReservationView();
		view.setName("nextweek");
		view.setDisplayName("Next Week");
		view.getCriteria().addAndCondition(CriteriaAPI.getCondition(getReservationScheduledTimeField(), DateOperators.NEXT_WEEK));
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return view;
	}

	private static FacilioView getOnGoingReservationView() {
		FacilioView view = getScheduledReservationView();
		view.setName("ongoing");
		view.setDisplayName("On Going");
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(getReservationStatusField(), String.valueOf(ReservationContext.ReservationStatus.ON_GOING.getIndex()), EnumOperators.IS));
		view.setCriteria(criteria);

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return view;
	}

	private static FacilioView getCompletedReservationView() {
		FacilioView view = getScheduledReservationView();
		view.setName("completed");
		view.setDisplayName("Completed");
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(getReservationStatusField(), String.valueOf(ReservationContext.ReservationStatus.FINISHED.getIndex()), EnumOperators.IS));
		view.setCriteria(criteria);

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


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

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllVisitorLogsView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Visits");
		
		FacilioModule visitorLogging = ModuleFactory.getVisitorLoggingModule();
		FacilioField createdTime = new FacilioField();
		createdTime.setName("checkInTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("CHECKIN_TIME");
		createdTime.setModule(visitorLogging);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	
	private static FacilioView getAllVisitorCheckInLogsView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Visits");
		
		FacilioModule visitorLogging = ModuleFactory.getVisitorLogCheckInModule();
		
		FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLogging,FieldType.DATE_TIME);
		List<SortField> sortFields = Arrays.asList(new SortField(checkInTime, false));
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getTenantPortalAllVisitorLogsView() {

		FacilioView allView = new FacilioView();
		allView.setName("tenantAll");
		allView.setDisplayName("My Visitors");

		FacilioModule visitorLogging = ModuleFactory.getVisitorLoggingModule();
		FacilioField createdTime = new FacilioField();
		createdTime.setName("checkInTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("CHECKIN_TIME");
		createdTime.setModule(visitorLogging);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		allView.setSortFields(sortFields);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));


		return allView;
	}
	
	private static FacilioView getTenantPortalAllVisitorCheckInLogsView() {

		FacilioView allView = new FacilioView();
		allView.setName("tenantAll");
		allView.setDisplayName("My Visitors");

		FacilioModule visitorLogModule = ModuleFactory.getVisitorLogCheckInModule();
		FacilioField createdTime = new FacilioField();
		createdTime.setName("checkInTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("CHECKIN_TIME");
		createdTime.setModule(visitorLogModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		allView.setSortFields(sortFields);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));

		return allView;
	}
	

	private static FacilioView getAllVendorVisitsView() {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("CHECKIN_TIME", "checkInTime", "-1", CommonOperators.IS_NOT_EMPTY));
		FacilioView allView = new FacilioView();
		allView.setName("vendorVisits");
		allView.setDisplayName("All Vendor Visits");
		allView.setCriteria(criteria);
		FacilioModule visitorLogging = ModuleFactory.getVisitorLoggingModule();
		FacilioField createdTime = new FacilioField();
		createdTime.setName("checkInTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("CHECKIN_TIME");
		createdTime.setModule(visitorLogging);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.VENDOR_PORTAL)));



		return allView;
	}

	private static FacilioView getVendorAllInvitesView() {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getPastVisitorLogStatusCriteria("Upcoming"));
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Invites");
		allView.setCriteria(criteria);
		
		FacilioModule visitorLogging = ModuleFactory.getVisitorLoggingModule();
		FacilioField createdTime = new FacilioField();
		createdTime.setName("expectedCheckInTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("EXPECTED_CHECKIN_TIME");
		createdTime.setModule(visitorLogging);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		allView.setSortFields(sortFields);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.VENDOR_PORTAL)));


		return allView;
	}
	
	private static FacilioView getVendorUpcomingVisitorLogsView() {
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getActiveInvitesCondition());
		FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLoggingModule();
//		FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLoggingModule,FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime, CommonOperators.IS_EMPTY));

		FacilioField createdTime = new FacilioField();
		createdTime.setName("expectedCheckInTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("EXPECTED_CHECKIN_TIME");
		createdTime.setModule(visitorLoggingModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));
		
		FacilioView allView = new FacilioView();
		allView.setName("invite_vendorActiveVisitors");
		allView.setDisplayName("Active");
		allView.setCriteria(criteria);
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.VENDOR_PORTAL)));

		return allView;
	}
	
	private static FacilioView getVendorUpcomingInviteVisitorLogsView() {
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getActiveInviteVisitorInvitesCondition());
		
		FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();
//		FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLoggingModule,FieldType.DATE_TIME);
//		criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime, CommonOperators.IS_EMPTY));

		FacilioField createdTime = new FacilioField();
		createdTime.setName("expectedCheckInTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("EXPECTED_CHECKIN_TIME");
		createdTime.setModule(inviteVisitorModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));
		
		FacilioView allView = new FacilioView();
		allView.setName("vendorActiveVisitors");
		allView.setDisplayName("All Visits");
		allView.setCriteria(criteria);
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.VENDOR_PORTAL)));

		return allView;
	}

	private static FacilioView getAllVisitorInvitesView() {

		FacilioView allView = new FacilioView();
		allView.setName("invite_all");
		allView.setDisplayName("All Invites");
		
		FacilioModule visitorLogModule = ModuleFactory.getVisitorLoggingModule();
		FacilioField expCheckInTime = FieldFactory.getField("expectedCheckInTime", "EXPECTED_CHECKIN_TIME", visitorLogModule,FieldType.DATE_TIME);
		
		allView.setSortFields(Arrays.asList(new SortField(expCheckInTime, true)));

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	
	private static FacilioView getAllInviteVisitorInvitesView() {

		FacilioView allView = new FacilioView();
		allView.setName("invite_all");
		allView.setDisplayName("All Invites");
		
		FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();
		FacilioField expCheckInTime = FieldFactory.getField("expectedCheckInTime", "EXPECTED_CHECKIN_TIME", inviteVisitorModule,FieldType.DATE_TIME);
		
		allView.setSortFields(Arrays.asList(new SortField(expCheckInTime, true)));

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllPlannedMaintenanceView() {
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Planned Maintenance");

		List<AppDomain.AppDomainType> appdomains = new ArrayList<>();
		appdomains.add(AppDomain.AppDomainType.FACILIO);

		FacilioModule plannedMaintenanceModule = ModuleFactory.getPlannedMaintenanceModule();
		FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", plannedMaintenanceModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(appdomains));
		return allView;
	}

	private static FacilioView getInActivePlannedMaintenanceView() {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getInActivePlannedMaintenanceCondition());
		FacilioModule plannedMaintenanceModule = ModuleFactory.getPlannedMaintenanceModule();
		FacilioField idField = FieldFactory.getIdField();
		idField.setModule(plannedMaintenanceModule);
		FacilioView allView = new FacilioView();
		allView.setName("inactive");
		allView.setDisplayName("Unpublished");
		allView.setCriteria(criteria);

		FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", plannedMaintenanceModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appdomains = new ArrayList<>();
		appdomains.add(AppDomain.AppDomainType.FACILIO);

		allView.setViewSharing(getSharingContext(appdomains));
		return allView;
	}

	private static FacilioView getActivePlannedMaintenanceView() {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getActivePlannedMaintenanceCondition());
		FacilioModule plannedMaintenanceModule = ModuleFactory.getPlannedMaintenanceModule();
		FacilioField idField = FieldFactory.getIdField();
		idField.setModule(plannedMaintenanceModule);
		FacilioView allView = new FacilioView();
		allView.setName("active");
		allView.setDisplayName("Published");
		allView.setCriteria(criteria);

		FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", plannedMaintenanceModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appdomains = new ArrayList<>();
		appdomains.add(AppDomain.AppDomainType.FACILIO);

		allView.setViewSharing(getSharingContext(appdomains));
		return allView;
	}

	public static Condition getInActivePlannedMaintenanceCondition() {
		FacilioModule module = ModuleFactory.getPlannedMaintenanceModule();
		FacilioField booleanField = new FacilioField();
		booleanField.setName("isActive");
		booleanField.setColumnName("IS_ACTIVE");
		booleanField.setDataType(FieldType.BOOLEAN);
		booleanField.setModule(module);

		Condition open = new Condition();
		open.setField(booleanField);
		open.setOperator(BooleanOperators.IS);
		open.setValue("false");

		return open;
	}

	public static Condition getActivePlannedMaintenanceCondition() {
		FacilioModule module = ModuleFactory.getPlannedMaintenanceModule();
		FacilioField booleanField = new FacilioField();
		booleanField.setName("isActive");
		booleanField.setColumnName("IS_ACTIVE");
		booleanField.setDataType(FieldType.BOOLEAN);
		booleanField.setModule(module);

		Condition open = new Condition();
		open.setField(booleanField);
		open.setOperator(BooleanOperators.IS);
		open.setValue("true");

		return open;
	}

	/* JobPlan View Declarations */
	private static FacilioView getActiveJobPlanView() {
		Criteria criteria = getActiveJobPlanCriteria();
		FacilioModule jobPlanModule = ModuleFactory.getJobPlanModule();
		FacilioField idField = FieldFactory.getIdField();
		idField.setModule(jobPlanModule);
		FacilioView allView = new FacilioView();
		allView.setName("active");
		allView.setDisplayName("Active");
		allView.setCriteria(criteria);
		return allView;
	}

	private static FacilioView getInActiveJobPlanView() {
		Criteria criteria = getInActiveJobPlanCriteria();
		FacilioModule jobPlanModule = ModuleFactory.getJobPlanModule();
		FacilioField idField = FieldFactory.getIdField();
		idField.setModule(jobPlanModule);
		FacilioView allView = new FacilioView();
		allView.setName("inactive");
		allView.setDisplayName("Inactive");
		allView.setCriteria(criteria);
		return allView;
	}

	private static FacilioView getDisabledJobPlanView() {
		Criteria criteria = getDisabledJobPlanCriteria();
		FacilioModule jobPlanModule = ModuleFactory.getJobPlanModule();
		FacilioField idField = FieldFactory.getIdField();
		idField.setModule(jobPlanModule);
		FacilioView allView = new FacilioView();
		allView.setName("disabled");
		allView.setDisplayName("Disabled");
		allView.setCriteria(criteria);
		allView.setAppLinkNames(AddJobPlanModule.jobPlanSupportedApps);
		return allView;
	}

	public static Criteria getActiveJobPlanCriteria() {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("JP_STATUS","jpStatus",String.valueOf(JobPlanContext.JPStatus.ACTIVE.getVal()),NumberOperators.EQUALS));
		criteria.setPattern("(1)");
		return criteria;
	}

	public static Criteria getInActiveJobPlanCriteria() {
		Criteria criteria = new Criteria();

		criteria.addAndCondition(CriteriaAPI.getCondition("JP_STATUS","jpStatus",String.valueOf(JobPlanContext.JPStatus.IN_ACTIVE.getVal()),NumberOperators.EQUALS));
		criteria.setPattern("(1)");

		return criteria;
	}
	public static Criteria getDisabledJobPlanCriteria(){
		Criteria criteria = new Criteria();

		criteria.addAndCondition(CriteriaAPI.getCondition("JP_STATUS","jpStatus",String.valueOf(JobPlanContext.JPStatus.DISABLED.getVal()),NumberOperators.EQUALS));
		criteria.setPattern("(1)");

		return criteria;
	}


	public static Condition getJobPlanBooleanCondition(String fieldName, String columnName, String conditionValue) {
		FacilioModule module = ModuleFactory.getJobPlanModule();
		FacilioField field = new FacilioField();
		field.setName(fieldName);
		field.setColumnName(columnName);
		field.setDataType(FieldType.BOOLEAN);
		field.setModule(module);

		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(BooleanOperators.IS);
		condition.setValue(conditionValue);

		return condition;
	}

	private static FacilioView getAllInsuranceView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Insurance");

		List<AppDomain.AppDomainType> appdomains = new ArrayList<>();
		appdomains.add(AppDomain.AppDomainType.FACILIO);
		appdomains.add(AppDomain.AppDomainType.TENANT_PORTAL);

		allView.setViewSharing(getSharingContext(appdomains));

		return allView;
	}
	
	private static FacilioView getVendorInsuranceView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Insurance");

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.VENDOR_PORTAL)));

		return allView;
	}
	
	private static Condition getExpiredInsuranceCondition(){
		
		FacilioField validTillField = new LookupField();
		validTillField.setName("validTill");
		validTillField.setColumnName("VALID_TILL");
		validTillField.setDataType(FieldType.DATE_TIME);
		validTillField.setModule(ModuleFactory.getInsuranceModule());

		Condition expiredCondition = new Condition();
		expiredCondition.setField(validTillField);
		expiredCondition.setOperator(DateOperators.TILL_NOW);
		return expiredCondition;
	}
	
	private static FacilioView getVendorActiveInsuranceView() {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getActiveInsurancesCondition());
		FacilioModule insuranceModule = ModuleFactory.getInsuranceModule();
		FacilioField idField = FieldFactory.getIdField();
		idField.setModule(insuranceModule);
		FacilioView allView = new FacilioView();
		allView.setName("vendorActive");
		allView.setDisplayName("Active");
		allView.setCriteria(criteria);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.VENDOR_PORTAL)));

		return allView;
	}
	private static FacilioView getVendorExpiredInsuranceView() {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(getExpiredInsurancesCondition());
		FacilioModule insuranceModule = ModuleFactory.getInsuranceModule();
		FacilioField validTill = FieldFactory.getField("validTill", "VALID_TILL", insuranceModule,FieldType.DATE_TIME);
		FacilioView allView = new FacilioView();
		allView.setName("vendorExpired");
		allView.setDisplayName("Expired");
		allView.setCriteria(criteria);
		allView.setSortFields(Arrays.asList(new SortField(validTill, false)));

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.VENDOR_PORTAL)));

		return allView;
	}
	
	
	private static FacilioView getAllWorkPermitView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Work Permit");

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		appDomains.add(AppDomain.AppDomainType.VENDOR_PORTAL);

		allView.setViewSharing(getSharingContext(appDomains));

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

		assetKpisView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

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

		assetKpisView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return assetKpisView;
	}
	
	private static Condition getKPICondition(Map<String, FacilioField> fieldMap) {
		return CriteriaAPI.getCondition(fieldMap.get("formulaFieldType"), String.valueOf(FormulaFieldType.ENPI.getValue()), NumberOperators.EQUALS);
	}

	private static FacilioView getAllWatchListView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Watchlist");

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getBlockedWatchListView() {
		FacilioView view = new FacilioView();
		view.setDisplayName("Blocked Watchlist");
		view.setName("blocked");
		FacilioField blockedField = FieldFactory.getField("isBlocked", "IS_BLOCKED", FieldType.BOOLEAN);
		Condition blockedCondition = CriteriaAPI.getCondition(blockedField, String.valueOf(true), BooleanOperators.IS);
		Criteria criteria = new Criteria();
		criteria.addAndCondition(blockedCondition);
		view.setCriteria(criteria);

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return view;
	}

	private static FacilioView getVipWatchListView() {
		FacilioView view = new FacilioView();
		view.setDisplayName("VIP Watchlist");
		view.setName("vip");
		FacilioField vipField = FieldFactory.getField("isVip", "IS_VIP", FieldType.BOOLEAN);
		Condition vipCondition = CriteriaAPI.getCondition(vipField, String.valueOf(true), BooleanOperators.IS);
		Criteria criteria = new Criteria();
		criteria.addAndCondition(vipCondition);
		view.setCriteria(criteria);

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return view;
	}
	
	private static FacilioView getPendingVisitLogsView() {

		FacilioView view = new FacilioView();
		view.setName("pending");
		view.setDisplayName("Pending Approval");
		Criteria criteria = new Criteria();
		FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLogCheckInModule();
		FacilioField hostApprovalField = FieldFactory.getField("approvalNeeded", "IS_APPROVAL_NEEDED", visitorLoggingModule,FieldType.BOOLEAN);
		criteria.addAndCondition(CriteriaAPI.getCondition(hostApprovalField, String.valueOf(true),BooleanOperators.IS));
		FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLoggingModule,FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime, CommonOperators.IS_EMPTY));
		view.setCriteria(criteria);

		List<SortField> sortFields = Arrays.asList(new SortField(checkInTime, false));
		view.setSortFields(sortFields);
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return view;
	}

	private static FacilioView getAllContactView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Contact");

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	
	private static FacilioView getTenantContactView() {

		FacilioView allView = new FacilioView();
		allView.setName("tenant");
		allView.setDisplayName("Tenant Contact");
		
		Criteria criteria = new Criteria();
		FacilioField contactType = FieldFactory.getField("contactType", "CONTACT_TYPE", ModuleFactory.getContactModule(),FieldType.SYSTEM_ENUM);
		criteria.addAndCondition(CriteriaAPI.getCondition(contactType, "1", PickListOperators.IS));
		allView.setCriteria(criteria);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	
	private static FacilioView getVendorContactView() {

		FacilioView allView = new FacilioView();
		allView.setName("vendor");
		allView.setDisplayName("Vendor Contact");
		Criteria criteria = new Criteria();
		FacilioField contactType = FieldFactory.getField("contactType", "CONTACT_TYPE", ModuleFactory.getContactModule(),FieldType.SYSTEM_ENUM);
		criteria.addAndCondition(CriteriaAPI.getCondition(contactType, "2", PickListOperators.IS));
		allView.setCriteria(criteria);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getEmployeeContactView() {

		FacilioView allView = new FacilioView();
		allView.setName("employee");
		allView.setDisplayName("Employee Contact");
		
		Criteria criteria = new Criteria();
		FacilioField contactType = FieldFactory.getField("contactType", "CONTACT_TYPE", ModuleFactory.getContactModule(),FieldType.SYSTEM_ENUM);
		criteria.addAndCondition(CriteriaAPI.getCondition(contactType, "3", PickListOperators.IS));
		allView.setCriteria(criteria);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getVisitorsCheckedInTodayView() {

		FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLoggingModule();
		FacilioView view = new FacilioView();
		view.setName("todayvisits");
		view.setDisplayName("Today Visits");
		Criteria criteria = new Criteria();
		FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLoggingModule,FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime, DateOperators.TODAY));
		view.setCriteria(criteria);
		view.setSortFields(Arrays.asList(new SortField(checkInTime, false)));
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return view;
	}
	
	private static FacilioView getVisitorsLogCheckedInTodayView() {

		FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLogCheckInModule();
		FacilioView view = new FacilioView();
		view.setName("todayvisits");
		view.setDisplayName("Today Visits");
		Criteria criteria = new Criteria();
		FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLoggingModule,FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime, DateOperators.TODAY));
		view.setCriteria(criteria);
		view.setSortFields(Arrays.asList(new SortField(checkInTime, false)));
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		return view;
	}


	private static FacilioView getTodayVisitorInvitesView() {

		FacilioModule inviteVisitorLogModule = ModuleFactory.getInviteVisitorLogModule();
		FacilioView view = new FacilioView();
		view.setName("invite_today");
		view.setDisplayName("Today Invites");
		Criteria criteria = new Criteria();
		FacilioField expCheckInTime = FieldFactory.getField("expectedCheckInTime", "EXPECTED_CHECKIN_TIME", inviteVisitorLogModule,FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(expCheckInTime, DateOperators.TODAY));
		view.setCriteria(criteria);
		
		List<SortField> sortFields = Arrays.asList(new SortField(expCheckInTime, false));

		view.setSortFields(sortFields);

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return view;
	}
	
	private static FacilioView getTodayInviteVisitorInvitesView() {

		FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();
		FacilioView view = new FacilioView();
		view.setName("invite_today");
		view.setDisplayName("Today Invites");
		Criteria criteria = new Criteria();
		FacilioField expCheckInTime = FieldFactory.getField("expectedCheckInTime", "EXPECTED_CHECKIN_TIME", inviteVisitorModule,FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(expCheckInTime, DateOperators.TODAY));
		view.setCriteria(criteria);
		
		List<SortField> sortFields = Arrays.asList(new SortField(expCheckInTime, false));

		view.setSortFields(sortFields);

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return view;
	}

	private static FacilioView  getVisitorsCheckedInCurrentlyView() {

		FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLoggingModule();
		FacilioView view = new FacilioView();
		view.setName("current");
		view.setDisplayName("Currently Checked-in");
		Criteria criteria = new Criteria();
		FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLoggingModule,FieldType.DATE_TIME);
		FacilioField checkOutTime = FieldFactory.getField("checkOutTime", "CHECKOUT_TIME", visitorLoggingModule,FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(checkOutTime, CommonOperators.IS_EMPTY));
		criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime, CommonOperators.IS_NOT_EMPTY));

		view.setCriteria(criteria);
		view.setSortFields(Arrays.asList(new SortField(checkInTime, false)));

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return view;
	}
	
	private static FacilioView getVisitorsLogCheckedInCurrentlyView() {

		FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLogCheckInModule();
		FacilioView view = new FacilioView();
		view.setName("current");
		view.setDisplayName("Currently Checked-in");
		Criteria criteria = new Criteria();
		FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLoggingModule,FieldType.DATE_TIME);
		FacilioField checkOutTime = FieldFactory.getField("checkOutTime", "CHECKOUT_TIME", visitorLoggingModule,FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(checkOutTime, CommonOperators.IS_EMPTY));
		criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime, CommonOperators.IS_NOT_EMPTY));

		view.setCriteria(criteria);
		view.setSortFields(Arrays.asList(new SortField(checkInTime, false)));

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return view;
	}

	private static FacilioView  getPendingVisitsView() {

		FacilioView view = new FacilioView();
		view.setName("pending");
		view.setDisplayName("Pending Approval");
		Criteria criteria = new Criteria();
		FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLoggingModule();
		FacilioField hostApprovalField = FieldFactory.getField("approvalNeeded", "IS_APPROVAL_NEEDED", visitorLoggingModule,FieldType.BOOLEAN);
		criteria.addAndCondition(CriteriaAPI.getCondition(hostApprovalField, String.valueOf(true),BooleanOperators.IS));
		FacilioField checkInTime = FieldFactory.getField("checkInTime", "CHECKIN_TIME", visitorLoggingModule,FieldType.DATE_TIME);
		criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime, CommonOperators.IS_EMPTY));
		view.setCriteria(criteria);
		
		List<SortField> sortFields = Arrays.asList(new SortField(checkInTime, false));
		view.setSortFields(sortFields);
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return view;
	}

	private static FacilioView  getMyPendingVisitsView() {
		
		Criteria criteria = new Criteria();		
		criteria.addAndCondition(getVisitorLogStatusCriteria("Requested"));
		FacilioView view = new FacilioView();
		view.setName("myPendingVisits");
		view.setDisplayName("My Approvals");
		view.setCriteria(criteria);

		FacilioModule visitorLogging = ModuleFactory.getVisitorLoggingModule();
		FacilioField createdTime = new FacilioField();
		createdTime.setName("checkInTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("CHECKIN_TIME");
		createdTime.setModule(visitorLogging);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		view.setSortFields(sortFields);
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));

		

		return view;
	}
	
	private static FacilioView  getMyPendingVisitsCheckInView() {
		
		Criteria criteria = new Criteria();		
		FacilioModule visitorLogModule = ModuleFactory.getVisitorLogCheckInModule();

		criteria.addAndCondition(getBaseVisitorLogStatusCriteria("Requested",visitorLogModule));
		FacilioView view = new FacilioView();
		view.setName("myPendingVisits");
		view.setDisplayName("My Approvals");
		view.setCriteria(criteria);

		FacilioField createdTime = new FacilioField();
		createdTime.setName("checkInTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("CHECKIN_TIME");
		createdTime.setModule(visitorLogModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
		view.setSortFields(sortFields);
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));

		return view;
	}
	
	private static FacilioView getUpcomingVisitsView() {

		FacilioView view = new FacilioView();
		view.setName("upcoming");
		view.setDisplayName("Upcoming Visits");
		Criteria criteria = new Criteria();
		FacilioModule visitorLoggingModule = ModuleFactory.getVisitorLoggingModule();
		
		FacilioField preRegisterField = FieldFactory.getField("isPreregistered", "IS_PREREGISTERED", visitorLoggingModule,FieldType.BOOLEAN);
		criteria.addAndCondition(CriteriaAPI.getCondition(preRegisterField, String.valueOf(true),BooleanOperators.IS));
		criteria.addAndCondition(getVisitorLogStatusCriteria("Upcoming"));
		view.setCriteria(criteria);
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("expectedCheckInTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("EXPECTED_CHECKIN_TIME");
		createdTime.setModule(visitorLoggingModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));
		view.setSortFields(sortFields);

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return view;
	}
	
	private static FacilioView getUpcomingInviteVisitsView() {

		FacilioView view = new FacilioView();
		view.setName("upcoming");
		view.setDisplayName("Upcoming Invites");
		Criteria criteria = new Criteria();
		FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();

		criteria.addAndCondition(getBaseVisitorLogStatusCriteria("Upcoming",inviteVisitorModule));
		view.setCriteria(criteria);
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("expectedCheckInTime");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("EXPECTED_CHECKIN_TIME");
		createdTime.setModule(inviteVisitorModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));
		view.setSortFields(sortFields);

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return view;
	}

	private static FacilioView  getPendingVisitorInvitesView() {

		FacilioView view = new FacilioView();
		view.setName("invite_pending");
		view.setDisplayName("Pending Approval");
		Criteria criteria = new Criteria();
		FacilioModule inviteVisitorLogModule = ModuleFactory.getInviteVisitorLogModule();

		FacilioField preRegisterField = FieldFactory.getField("isInviteApprovalNeeded", "IS_INVITE_APPROVAL_NEEDED", inviteVisitorLogModule,FieldType.BOOLEAN);
		criteria.addAndCondition(CriteriaAPI.getCondition(preRegisterField, String.valueOf(true),BooleanOperators.IS));
		FacilioField checkInTime = FieldFactory.getField("hasCheckedIn", "HAS_CHECKED_IN", inviteVisitorLogModule,FieldType.BOOLEAN);
		criteria.addAndCondition(CriteriaAPI.getCondition(checkInTime,String.valueOf(false), BooleanOperators.IS));
		criteria.addAndCondition(getVisitorLogStatusCriteria("InviteRequested"));
		
		FacilioField expCheckInTime = FieldFactory.getField("expectedCheckInTime", "EXPECTED_CHECKIN_TIME", inviteVisitorLogModule,FieldType.DATE_TIME);
		view.setSortFields(Arrays.asList(new SortField(expCheckInTime, false)));
		

		view.setCriteria(criteria);
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return view;
	}
	
	private static FacilioView getPendingInviteVisitorInvitesView() {

		FacilioView view = new FacilioView();
		view.setName("invite_pending");
		view.setDisplayName("Pending Approval");
		Criteria criteria = new Criteria();
		FacilioModule inviteVisitorModule = ModuleFactory.getInviteVisitorLogModule();

		FacilioField hasCheckedInField = FieldFactory.getField("hasCheckedIn", "HAS_CHECKED_IN", inviteVisitorModule,FieldType.BOOLEAN);
		Criteria subCriteria = new Criteria();
		subCriteria.addOrCondition(CriteriaAPI.getCondition(hasCheckedInField, String.valueOf(false),BooleanOperators.IS));
		subCriteria.addOrCondition(CriteriaAPI.getCondition(hasCheckedInField, CommonOperators.IS_EMPTY));
		criteria.andCriteria(subCriteria);
		criteria.addAndCondition(getBaseVisitorLogStatusCriteria("InviteRequested",inviteVisitorModule));
		
		FacilioField expCheckInTime = FieldFactory.getField("expectedCheckInTime", "EXPECTED_CHECKIN_TIME", inviteVisitorModule,FieldType.DATE_TIME);
		view.setSortFields(Arrays.asList(new SortField(expCheckInTime, false)));
		

		view.setCriteria(criteria);
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return view;
	}
	
	public static Condition getVisitorLogStatusCriteria(String status) {

		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("status");
		statusTypeField.setColumnName("STATUS");
		statusTypeField.setDataType(FieldType.STRING);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusCondition = new Condition();
		statusCondition.setField(statusTypeField);
		statusCondition.setOperator(StringOperators.IS);
		statusCondition.setValue(status);

		Criteria statusCriteria = new Criteria() ;
		statusCriteria.addAndCondition(statusCondition);

		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(ModuleFactory.getVisitorLogCheckInModule());
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition condition = new Condition();
		condition.setField(statusField);
		condition.setOperator(LookupOperator.LOOKUP);
		condition.setCriteriaValue(statusCriteria);

		return condition;
	}

	public static Condition getVisitorInviteStatusCriteria(String status) {

		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("status");
		statusTypeField.setColumnName("STATUS");
		statusTypeField.setDataType(FieldType.STRING);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusCondition = new Condition();
		statusCondition.setField(statusTypeField);
		statusCondition.setOperator(StringOperators.IS);
		statusCondition.setValue(status);

		Criteria statusCriteria = new Criteria() ;
		statusCriteria.addAndCondition(statusCondition);

		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(ModuleFactory.getInviteVisitorLogModule());
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition condition = new Condition();
		condition.setField(statusField);
		condition.setOperator(LookupOperator.LOOKUP);
		condition.setCriteriaValue(statusCriteria);

		return condition;
	}
	
	public static Condition getBaseVisitorLogStatusCriteria(String status, FacilioModule module) {

		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("status");
		statusTypeField.setColumnName("STATUS");
		statusTypeField.setDataType(FieldType.STRING);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusCondition = new Condition();
		statusCondition.setField(statusTypeField);
		statusCondition.setOperator(StringOperators.IS);
		statusCondition.setValue(status);

		Criteria statusCriteria = new Criteria() ;
		statusCriteria.addAndCondition(statusCondition);

		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition condition = new Condition();
		condition.setField(statusField);
		condition.setOperator(LookupOperator.LOOKUP);
		condition.setCriteriaValue(statusCriteria);

		return condition;
	}
	
	public static Condition getCheckInVisitorLogStatusCriteria(String status) {

		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("status");
		statusTypeField.setColumnName("STATUS");
		statusTypeField.setDataType(FieldType.STRING);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusCondition = new Condition();
		statusCondition.setField(statusTypeField);
		statusCondition.setOperator(StringOperators.IS);
		statusCondition.setValue(status);

		Criteria statusCriteria = new Criteria() ;
		statusCriteria.addAndCondition(statusCondition);

		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(ModuleFactory.getVisitorLoggingModule());
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition condition = new Condition();
		condition.setField(statusField);
		condition.setOperator(LookupOperator.LOOKUP);
		condition.setCriteriaValue(statusCriteria);

		return condition;
	}

	public static Condition getPastVisitorLogStatusCriteria(String status) {

		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("status");
		statusTypeField.setColumnName("STATUS");
		statusTypeField.setDataType(FieldType.STRING);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusCondition = new Condition();
		statusCondition.setField(statusTypeField);
		statusCondition.setOperator(StringOperators.ISN_T);
		statusCondition.setValue(status);

		Criteria statusCriteria = new Criteria() ;
		statusCriteria.addAndCondition(statusCondition);

		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(ModuleFactory.getVisitorLoggingModule());
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition condition = new Condition();
		condition.setField(statusField);
		condition.setOperator(LookupOperator.LOOKUP);
		condition.setCriteriaValue(statusCriteria);

		return condition;
	}
	
	public static Condition getMyServiceRequestCondition() {
		LookupField userField = new LookupField();
		userField.setName("assignedTo");
		userField.setColumnName("ASSIGNED_TO_ID");
		userField.setDataType(FieldType.LOOKUP);
		userField.setModule(ModuleFactory.getServiceRequestModule());
		userField.setSpecialType(FacilioConstants.ContextNames.USERS);

		Condition myUserCondition = new Condition();
		myUserCondition.setField(userField);
		myUserCondition.setOperator(PickListOperators.IS);
		myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

		return myUserCondition;
	}

	private static FacilioView getMyServiceRequets() {

		Criteria criteria = new Criteria();
		FacilioModule serviceRequestsModule = ModuleFactory.getServiceRequestModule();
//		criteria.addAndCondition(getOpenStatusCondition());
		criteria.addAndCondition(getMyServiceRequestCondition());

		FacilioField createdTime = new FacilioField();
		createdTime.setName("dueDate");
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("DUE_DATE");
		createdTime.setModule(serviceRequestsModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView openTicketsView = new FacilioView();
		openTicketsView.setName("myopenservicerequests");
		openTicketsView.setDisplayName("My Service Requests");
		openTicketsView.setCriteria(criteria);
		openTicketsView.setSortFields(sortFields);
		openTicketsView.setModuleName(FacilioConstants.ContextNames.SERVICE_REQUEST);

		openTicketsView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return openTicketsView;
	}
	
	private static FacilioView getAllOccupantsView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Occupants");

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllTaskView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Tasks");
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllServiceRequests() {

		FacilioModule serviceRequestsModule = ModuleFactory.getServiceRequestModule();

		FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", serviceRequestsModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Service Requests");
		allView.setSortFields(sortFields);
		allView.setModuleName(FacilioConstants.ContextNames.SERVICE_REQUEST);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return allView;
	}

	public static Condition getModuleTicketStatusCriteria(String status, FacilioModule module) {

		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("status");
		statusTypeField.setColumnName("STATUS");
		statusTypeField.setDataType(FieldType.STRING);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusCondition = new Condition();
		statusCondition.setField(statusTypeField);
		statusCondition.setOperator(StringOperators.IS);
		statusCondition.setValue(status);

		Criteria statusCriteria = new Criteria() ;
		statusCriteria.addAndCondition(statusCondition);

		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition condition = new Condition();
		condition.setField(statusField);
		condition.setOperator(LookupOperator.LOOKUP);
		condition.setCriteriaValue(statusCriteria);

		return condition;
	}
	
	public static Condition getActiveInvitesCondition() {
		FacilioModule module = ModuleFactory.getInviteVisitorLogModule();
		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition open = new Condition();
		open.setField(statusField);
		open.setOperator(LookupOperator.LOOKUP);
		open.setCriteriaValue(getOpenStatusCriteria());

		return open;
	}
	
	public static Condition getActiveInviteVisitorInvitesCondition() {
		FacilioModule module = ModuleFactory.getInviteVisitorLogModule();
		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition open = new Condition();
		open.setField(statusField);
		open.setOperator(LookupOperator.LOOKUP);
		open.setCriteriaValue(getOpenStatusCriteria());

		return open;
	}
	
	public static Condition getExpiredInvitesCondition() {
		FacilioModule module = ModuleFactory.getVisitorLoggingModule();
		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition open = new Condition();
		open.setField(statusField);
		open.setOperator(LookupOperator.LOOKUP);
		open.setCriteriaValue(getCloseStatusCriteria());

		return open;
	}
	
	public static Condition getExpiredInviteVisitsCondition() {
		FacilioModule module = ModuleFactory.getInviteVisitorLogModule();
		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition open = new Condition();
		open.setField(statusField);
		open.setOperator(LookupOperator.LOOKUP);
		open.setCriteriaValue(getCloseStatusCriteria());

		return open;
	}
	
	public static Condition getActiveInsurancesCondition() {
		FacilioModule module = ModuleFactory.getInsuranceModule();
		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition open = new Condition();
		open.setField(statusField);
		open.setOperator(LookupOperator.LOOKUP);
		open.setCriteriaValue(getOpenStatusCriteria());

		return open;
	}
	
	public static Condition getExpiredInsurancesCondition() {
		FacilioModule module = ModuleFactory.getInsuranceModule();
		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(module);
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition open = new Condition();
		open.setField(statusField);
		open.setOperator(LookupOperator.LOOKUP);
		open.setCriteriaValue(getCloseStatusCriteria());

		return open;
	}


	private static FacilioView getAllSafetyPlansView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("Safety Plans");
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return allView;
	}

	private static FacilioView getAllHazardModuleView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("Hazards");

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllPrecautionView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("Precautions");

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAssociatedHazardPrecautionView() {

		FacilioView allView = new FacilioView();
		allView.setName("associatedhazards");
		allView.setDisplayName("Associated Hazards");

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAssociatedPrecautionView() {

		FacilioView allView = new FacilioView();
		allView.setName("associatedprecautions");
		allView.setDisplayName("Associated Precautions");
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return allView;
	}

	private static FacilioView getAllSafetyPlanHazardsView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("Safety Plan Hazards");

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return allView;
	}

	private static FacilioView getAllAssetHazardsView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("Asset Hazards");

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllWorkOrderHazardsView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("Workorder Hazards");
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllWorkOrderHazardPrecautionsView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("Workorder Hazard Precautions");
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllBaseSpaceHazardView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("Space Hazards");
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllHazardPrecautionView() {

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Hazuard Precaution");
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return allView;
	}

	
	private static FacilioView getAllClientView() {
	
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("Clients");

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllBuildings() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Buildings");
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	private static FacilioView getAllSpaces() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Spaces");
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllBasespaces() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Basespaces");
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllFloors() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Floors");
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

	private static FacilioView getAllTenantUnitSpace() {

		FacilioModule tenantUnitSpaceModule = ModuleFactory.getTenantUnitSpaceModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Tenant Unit");
		allView.setModuleName(tenantUnitSpaceModule.getName());
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	
	private static FacilioView getAllTenantUnitSpaceDetailsView() {

		FacilioModule tenantUnitSpaceModule = ModuleFactory.getTenantUnitSpaceModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

		FacilioView allView = new FacilioView();
		allView.setName("details");
		allView.setDisplayName("All Tenant Units");
		allView.setModuleName(tenantUnitSpaceModule.getName());
		allView.setSortFields(sortFields);
		allView.setHidden(true);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}

    private static FacilioView getAllTenantSpaces() {

        FacilioModule tenantSpaceModule = ModuleFactory.getTenantSpacesModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tenant Spaces");
        allView.setModuleName(tenantSpaceModule.getName());
        allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
    }

    private static FacilioView getAllQuotations() {

        FacilioModule module = ModuleFactory.getQuotationModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("parentId", "PARENT_ID", FieldType.NUMBER), false));

		FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Quotes");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
    }

	private static FacilioView getAllTaxes() {

		FacilioModule module = ModuleFactory.getTaxModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Taxes");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return allView;
	}

	private static FacilioView getActiveTaxes() {

		FacilioModule module = ModuleFactory.getTaxModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView view = new FacilioView();
		view.setName("active");
		view.setDisplayName("Active Taxes");
		view.setModuleName(module.getName());
		view.setSortFields(sortFields);

		Criteria activeCriteria = new Criteria();
		activeCriteria.addAndCondition(CriteriaAPI.getCondition("IS_ACTIVE", "isActive",String.valueOf(true), BooleanOperators.IS));
		view.setCriteria(activeCriteria);
		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return view;
	}

	private static FacilioView getAllWorkOrderCostView() {

		FacilioModule module = ModuleFactory.getWorkorderCostModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Workorder Cost");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return allView;
	}

	private static FacilioView getAllAnnouncementView() {

		FacilioModule module = ModuleFactory.getAnnouncementModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Announcements");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return allView;
	}

	private static FacilioView getAllAudienceView() {

		FacilioModule module = ModuleFactory.getAudienceModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Audiences");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return allView;
	}

	private static FacilioView getAllPeopleAnnouncementView() {

		FacilioModule module = ModuleFactory.getPeopleAnnouncementModule();
		FacilioModule announcementModule = ModuleFactory.getAnnouncementModule();

		FacilioField sysCreatedTime = new FacilioField();
		sysCreatedTime.setName("sysCreatedTime");
		sysCreatedTime.setColumnName("SYS_CREATED_TIME");
		sysCreatedTime.setDataType(FieldType.DATE_TIME);
		sysCreatedTime.setModule(announcementModule);

		List<SortField> sortFields = Arrays.asList(new SortField(sysCreatedTime, false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Announcements");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));

		FacilioField isCancelledField = new FacilioField();
		isCancelledField.setName("isCancelled");
		isCancelledField.setColumnName("IS_CANCELLED");
		isCancelledField.setDataType(FieldType.BOOLEAN);
		isCancelledField.setModule(announcementModule);

		Condition condition = new Condition();
		condition.setField(isCancelledField);
		condition.setOperator(BooleanOperators.IS);
		condition.setValue("false");

		Criteria criteria = new Criteria();
		criteria.addOrCondition(condition);

		allView.setCriteria(criteria);

		return allView;
	}

	private static FacilioView getAllDealsAndOffersView() {

		FacilioModule module = ModuleFactory.getDealsAndOffersModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Deals and Offers");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		appDomains.add(AppDomain.AppDomainType.TENANT_PORTAL);
		allView.setViewSharing(getSharingContext(appDomains));


		return allView;
	}

	private static FacilioView getAllNeighbourhoodView() {

		FacilioModule module = ModuleFactory.getNeighbourhoodModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Neighbourhood");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		appDomains.add(AppDomain.AppDomainType.TENANT_PORTAL);
		allView.setViewSharing(getSharingContext(appDomains));


		return allView;
	}

	private static FacilioView getAllNewsAndInformationView() {

		FacilioModule module = ModuleFactory.getNewsAndInformationModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All News and Information");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		appDomains.add(AppDomain.AppDomainType.TENANT_PORTAL);
		allView.setViewSharing(getSharingContext(appDomains));


		return allView;
	}

	private static FacilioView getAllContactDirectoryView() {

		FacilioModule module = ModuleFactory.getContactDirectoryModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Contact Directory");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		appDomains.add(AppDomain.AppDomainType.TENANT_PORTAL);
		allView.setViewSharing(getSharingContext(appDomains));


		return allView;
	}

	private static FacilioView getAllAdminDocumentsView() {

		FacilioModule module = ModuleFactory.getAdminDocumentsModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Admin Documents");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		appDomains.add(AppDomain.AppDomainType.TENANT_PORTAL);
		allView.setViewSharing(getSharingContext(appDomains));


		return allView;
	}

	private static FacilioView getAllBudgetView() {

		FacilioModule module = ModuleFactory.getBudgetModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Budgets");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	private static FacilioView getAllRequestForQuotationView() {

		FacilioModule module = ModuleFactory.getRequestForQuotationModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Request For Quotations");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	private static FacilioView getAllTransactionView() {

		FacilioModule module = ModuleFactory.getTransactionModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}

	private static FacilioView getAllJobPlanView() {

		FacilioModule module = ModuleFactory.getJobPlanModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Job Plans");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	private static FacilioView getAllVendorQuotesView() {

		FacilioModule module = ModuleFactory.getVendorQuotesModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Vendor Quotes");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	private static FacilioView getAllTransferRequestView() {

		FacilioModule module = ModuleFactory.getTransferRequestModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Transfer Requests");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	private static FacilioView getAllTransferRequestShipmentView() {

		FacilioModule module = ModuleFactory.getTransferRequestShipmentModule();
		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Transfer Request Shipment");
		allView.setModuleName(module.getName());
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	private static FacilioView getAllChartOfAccountView() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Chart of Accounts");
		allView.setModuleName("chartofaccount");
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getAllAccountTypeView() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Account Types");
		allView.setModuleName("accounttype");
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}

	private static FacilioView getAllControlGroupView() {
		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Groups");
		allView.setModuleName(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getAllControlScheduleView() {
		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Schedules");
		allView.setModuleName(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getAllControlScheduleExceptionTenantView() {
		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Exceptions");
		allView.setModuleName(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getUpcomingControlCommandView() {
		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("executedTime", "EXECUTED_TIME", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("upcoming");
		allView.setDisplayName("Upcoming Commands");
		allView.setModuleName(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		allView.setSortFields(sortFields);
		
		Criteria criteria = new Criteria();
		
		List<Integer> upcomingStatusInt = new ArrayList<Integer>();
		
		upcomingStatusInt.add(ControlActionCommandContext.Status.SCHEDULED.getIntVal());
		upcomingStatusInt.add(ControlActionCommandContext.Status.SCHEDULED_WITH_NO_PERMISSION.getIntVal());
		
		criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", StringUtils.join(upcomingStatusInt, ","), NumberOperators.EQUALS));
		
		allView.setCriteria(criteria);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getHistoryControlCommandView() {
		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("executedTime", "EXECUTED_TIME", FieldType.NUMBER), false));

		FacilioView allView = new FacilioView();
		allView.setName("history");
		allView.setDisplayName("Command History");
		allView.setModuleName(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		allView.setSortFields(sortFields);
		
		List<Integer> historyStatusInt = new ArrayList<Integer>();
		
		historyStatusInt.add(ControlActionCommandContext.Status.SUCCESS.getIntVal());
		historyStatusInt.add(ControlActionCommandContext.Status.ERROR.getIntVal());
		historyStatusInt.add(ControlActionCommandContext.Status.SENT.getIntVal());
		
		Criteria criteria = new Criteria();
		
		criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", StringUtils.join(historyStatusInt, ","), NumberOperators.EQUALS));
		
		allView.setCriteria(criteria);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}

	private static FacilioView getTenantControlGroupView() {
		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Control_Group_V2_Tenant_Sharing.ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Groups");
		allView.setModuleName(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME);
		allView.setSortFields(sortFields);
		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.TENANT_PORTAL)));

		return allView;
	}

	public static Condition getNonRevisedModuleStatusQuotationCriteria() {

		FacilioField statusTypeField = new FacilioField();
		statusTypeField.setName("status");
		statusTypeField.setColumnName("STATUS");
		statusTypeField.setDataType(FieldType.STRING);
		statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

		Condition statusCondition = new Condition();
		statusCondition.setField(statusTypeField);
		statusCondition.setOperator(StringOperators.ISN_T);
		statusCondition.setValue("Revised");

		Criteria statusCriteria = new Criteria() ;
		statusCriteria.addAndCondition(statusCondition);

		LookupField statusField = new LookupField();
		statusField.setName("moduleState");
		statusField.setColumnName("MODULE_STATE");
		statusField.setDataType(FieldType.LOOKUP);
		statusField.setModule(ModuleFactory.getQuotationModule());
		statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

		Condition condition = new Condition();
		condition.setField(statusField);
		condition.setOperator(LookupOperator.LOOKUP);
		condition.setCriteriaValue(statusCriteria);

		return condition;
	}

	private static SharingContext<SingleSharingContext> getSharingContext(List<AppDomain.AppDomainType> appDomains) {
		SharingContext<SingleSharingContext> viewSharing = new SharingContext<SingleSharingContext>();
		if(CollectionUtils.isNotEmpty(appDomains)){
			for(AppDomain.AppDomainType appdomain : appDomains){
				SingleSharingContext appSharing = new SingleSharingContext();
				appSharing.setType(SingleSharingContext.SharingType.APP);
				appSharing.setAppType(appdomain);
				viewSharing.add(appSharing);
			}
		}
		return viewSharing;
	}
	
	private static FacilioView getSensorAlarmSeverity(String name, String displayName, String severity, boolean equals) {

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
		view.setModuleName("sensorrollupalarm");
		view.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return view;
	}
	
	private static FacilioView getSensorAlarm(String name, String displayName, boolean equals) {

		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());

		FacilioView view = new FacilioView();
		view.setName(name);
		view.setDisplayName(displayName);
		view.setModuleName("sensorrollupalarm");
		view.setSortFields(Arrays.asList(new SortField(createdTime, false)));
		view.setDefault(true);

		view.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return view;
	}
	
	private static FacilioView getSensorAlarmUnacknowledged() {
		Criteria criteria = getReadingAlarmUnacknowledgedCriteria();
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());

		FacilioView typeAlarms = new FacilioView();
		typeAlarms.setName("unacknowledgedSensorAlarm");
		typeAlarms.setDisplayName("Unacknowledged");
		typeAlarms.setCriteria(criteria);
		typeAlarms.setModuleName("sensorrollupalarm");
		typeAlarms.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		typeAlarms.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return typeAlarms;
	}
	
	private static FacilioView getSensorMeterAlarm() {
		Criteria criteria = getSensorMeterAlarmCriteria();
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());

		FacilioView typeAlarms = new FacilioView();
		typeAlarms.setName("sensorMeter");
		typeAlarms.setDisplayName("Meter Alarms");
		typeAlarms.setCriteria(criteria);
		typeAlarms.setModuleName("sensorrollupalarm");
		typeAlarms.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		typeAlarms.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return typeAlarms;
	}
	
	public static Criteria getSensorMeterAlarmCriteria() {
		FacilioModule module = ModuleFactory.getSensorRollUpAlarmModule();
		FacilioField field = new FacilioField();
		field.setName("readingFieldId");
		field.setColumnName("READING_FIELD_ID");
		field.setDataType(FieldType.NUMBER);
		field.setModule(module);

		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(CommonOperators.IS_EMPTY);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);
		return criteria;
		
	}
	
	private static FacilioView getSensorNonMeterAlarm() {
		Criteria criteria = getSensorNonMeterAlarmCriteria();
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("lastOccurredTime");	
		createdTime.setDataType(FieldType.DATE_TIME);
		createdTime.setColumnName("LAST_OCCURRED_TIME");
		createdTime.setModule(ModuleFactory.getBaseAlarmModule());

		FacilioView typeAlarms = new FacilioView();
		typeAlarms.setName("sensorNonMeter");
		typeAlarms.setDisplayName("Sensor Alarms");
		typeAlarms.setCriteria(criteria);
		typeAlarms.setModuleName("sensorrollupalarm");
		typeAlarms.setSortFields(Arrays.asList(new SortField(createdTime, false)));

		typeAlarms.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));


		return typeAlarms;
	}
	
	public static Criteria getSensorNonMeterAlarmCriteria() {
		FacilioModule module = ModuleFactory.getSensorRollUpAlarmModule();
		FacilioField field = new FacilioField();
		field.setName("readingFieldId");
		field.setColumnName("READING_FIELD_ID");
		field.setDataType(FieldType.NUMBER);
		field.setModule(module);

		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(CommonOperators.IS_NOT_EMPTY);

		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);
		return criteria;
		
		
	}

	private static FacilioView getAllFacilityView() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Facility");
		allView.setModuleName(FacilioConstants.ContextNames.FacilityBooking.FACILITY);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}

	private static FacilioView getAllFacilityBookingView() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Facility Booking");
		allView.setModuleName(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getAllAmenityView() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Amenity");
		allView.setModuleName(FacilioConstants.ContextNames.FacilityBooking.AMENITY);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}

	private static FacilioView getAllAmenitiesView() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));
		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Amenities");
		allView.setModuleName(FacilioConstants.ContextNames.FacilityBooking.AMENITY);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getAllDepartmentView() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Departments");
		allView.setModuleName(FacilioConstants.ContextNames.DEPARTMENT);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getAllMovesView() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Moves");
		allView.setModuleName(FacilioConstants.ContextNames.MOVES);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getAllDeliveriesView() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Deliveries");
		allView.setModuleName(FacilioConstants.ContextNames.DELIVERIES);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getAllDeliveryAreaView() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Delivery Areas");
		allView.setModuleName(FacilioConstants.ContextNames.DELIVERY_AREA);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getAllLockersView() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Lockers");
		allView.setModuleName(FacilioConstants.ContextNames.LOCKERS);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getAllParkingStallView() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Parking Stalls");
		allView.setModuleName(FacilioConstants.ContextNames.PARKING_STALL);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getAllDesksView() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Desks");
		allView.setModuleName(FacilioConstants.ContextNames.Floorplan.DESKS);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}

	private static FacilioView getAllInspectionTemplateViews() {
		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Inspection_Templates.ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Inspection Templates");
		allView.setModuleName(FacilioConstants.Inspection.INSPECTION_TEMPLATE);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getAllInspectionResponseViews() {
		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Inspection_Responses.ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Inspections");
		allView.setModuleName(FacilioConstants.Inspection.INSPECTION_RESPONSE);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	
	private static FacilioView getAllInductionTemplateViews() {
		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Induction_Templates.ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Induction Templates");
		allView.setModuleName(FacilioConstants.Induction.INDUCTION_TEMPLATE);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getAllInductionResponseViews() {
		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Induction_Responses.ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Inductions");
		allView.setModuleName(FacilioConstants.Induction.INDUCTION_RESPONSE);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getAllInspectionCategoryViews() {
		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Inspection Category");
		allView.setModuleName(FacilioConstants.Inspection.INSPECTION_CATEGORY);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}
	
	private static FacilioView getAllInspectionPriorityViews() {
		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Inspection Priority");
		allView.setModuleName(FacilioConstants.Inspection.INSPECTION_PRIORITY);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}

	private static FacilioView getAllSpaceBookingViews() {

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Space Booking");
		allView.setModuleName(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING);
		allView.setSortFields(sortFields);

		List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
		appDomains.add(AppDomain.AppDomainType.FACILIO);
		allView.setViewSharing(getSharingContext(appDomains));

		return allView;
	}

	private static FacilioView getInventoryRequestIssued(String viewName, String viewDisplayName, boolean isIssued) {
		FacilioModule irModule = ModuleFactory.getInventoryRequestModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("localId");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("LOCAL_ID");
		createdTime.setModule(irModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		Criteria criteria = getIRIssuedCondition(true);

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
		statusView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		return statusView;
	}

	private static Criteria getIRIssuedCondition(boolean isIssued) {

		FacilioField irStatusField = new FacilioField();
		irStatusField.setName("isIssued");
		irStatusField.setColumnName("IS_ISSUED");
		irStatusField.setDataType(FieldType.BOOLEAN);
		irStatusField.setModule(ModuleFactory.getInventoryRequestModule());

		Condition statusCond = new Condition();
		statusCond.setField(irStatusField);
		statusCond.setOperator(BooleanOperators.IS);
		statusCond.setValue(String.valueOf(isIssued));

		Criteria inventoryRequestStatusCriteria = new Criteria();
		inventoryRequestStatusCriteria.addAndCondition(statusCond);
		return inventoryRequestStatusCriteria;

	}
	private static FacilioView getQuotesReceivedRfqView(String viewName, String viewDisplayName, boolean quotesReceived) {
		FacilioModule rfqModule = ModuleFactory.getRequestForQuotationModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("localId");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("LOCAL_ID");
		createdTime.setModule(rfqModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		Criteria criteria = getQuotesReceivedRfqCondition(quotesReceived);
		FacilioView statusView = new FacilioView();
		statusView.setName(viewName);
		statusView.setDisplayName(viewDisplayName);
		statusView.setSortFields(sortFields);
		statusView.setCriteria(criteria);
		statusView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		return statusView;
	}

	private static Criteria getQuotesReceivedRfqCondition(boolean quotesReceived) {
		FacilioField quotesReceivedField = new FacilioField();
		quotesReceivedField.setName("isQuoteReceived");
		quotesReceivedField.setColumnName("QUOTE_RECEIVED");
		quotesReceivedField.setDataType(FieldType.BOOLEAN);
		quotesReceivedField.setModule(ModuleFactory.getRequestForQuotationModule());

		Condition quotesReceivedCondition = new Condition();
		quotesReceivedCondition.setField(quotesReceivedField);
		quotesReceivedCondition.setOperator(BooleanOperators.IS);
		quotesReceivedCondition.setValue(String.valueOf(quotesReceived));

		Criteria rfqStatusCriteria = new Criteria();
		rfqStatusCriteria.addAndCondition(quotesReceivedCondition);
		rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("RFQ_FINALIZED","isRfqFinalized",String.valueOf(true),BooleanOperators.IS));
		rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("AWARDED","isAwarded",String.valueOf(false),BooleanOperators.IS));
		rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("PO_CREATED","isPoCreated",String.valueOf(false),BooleanOperators.IS));
		rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("DISCARDED","isDiscarded",String.valueOf(false),BooleanOperators.IS));

		return rfqStatusCriteria;
	}

	private static FacilioView getAwardedRfqView(String viewName, String viewDisplayName, boolean awarded) {
		FacilioModule rfqModule = ModuleFactory.getRequestForQuotationModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("localId");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("LOCAL_ID");
		createdTime.setModule(rfqModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		Criteria criteria = getAwardedRfqCondition(awarded);
		FacilioView statusView = new FacilioView();
		statusView.setName(viewName);
		statusView.setDisplayName(viewDisplayName);
		statusView.setSortFields(sortFields);
		statusView.setCriteria(criteria);
		statusView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		return statusView;
	}

	private static Criteria getAwardedRfqCondition(boolean awarded) {
		FacilioField awardedField = new FacilioField();
		awardedField.setName("isAwarded");
		awardedField.setColumnName("AWARDED");
		awardedField.setDataType(FieldType.BOOLEAN);
		awardedField.setModule(ModuleFactory.getRequestForQuotationModule());

		Condition awardedCondition = new Condition();
		awardedCondition.setField(awardedField);
		awardedCondition.setOperator(BooleanOperators.IS);
		awardedCondition.setValue(String.valueOf(awarded));

		Criteria rfqStatusCriteria = new Criteria();
		rfqStatusCriteria.addAndCondition(awardedCondition);
		rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("RFQ_FINALIZED","isRfqFinalized",String.valueOf(true),BooleanOperators.IS));
		rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("QUOTE_RECEIVED","isQuoteReceived",String.valueOf(true),BooleanOperators.IS));
		rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("PO_CREATED","isPoCreated",String.valueOf(false),BooleanOperators.IS));
		rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("DISCARDED","isDiscarded",String.valueOf(false),BooleanOperators.IS));
		return rfqStatusCriteria;
	}
	private static FacilioView getPoCreatedRfqView(String viewName, String viewDisplayName, boolean poCreated) {
		FacilioModule rfqModule = ModuleFactory.getRequestForQuotationModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("localId");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("LOCAL_ID");
		createdTime.setModule(rfqModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		Criteria criteria = getPoCreatedRfqCondition(poCreated);
		FacilioView statusView = new FacilioView();
		statusView.setName(viewName);
		statusView.setDisplayName(viewDisplayName);
		statusView.setSortFields(sortFields);
		statusView.setCriteria(criteria);
		statusView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		return statusView;
	}

	private static Criteria getPoCreatedRfqCondition(boolean poCreated) {
		FacilioField poCreatedField = new FacilioField();
		poCreatedField.setName("isPoCreated");
		poCreatedField.setColumnName("PO_CREATED");
		poCreatedField.setDataType(FieldType.BOOLEAN);
		poCreatedField.setModule(ModuleFactory.getRequestForQuotationModule());

		Condition poCreatedCondition = new Condition();
		poCreatedCondition.setField(poCreatedField);
		poCreatedCondition.setOperator(BooleanOperators.IS);
		poCreatedCondition.setValue(String.valueOf(poCreated));

		Criteria rfqStatusCriteria = new Criteria();
		rfqStatusCriteria.addAndCondition(poCreatedCondition);
		rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("RFQ_FINALIZED","isRfqFinalized",String.valueOf(true),BooleanOperators.IS));
		rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("QUOTE_RECEIVED","isQuoteReceived",String.valueOf(true),BooleanOperators.IS));
		rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("AWARDED","isAwarded",String.valueOf(true),BooleanOperators.IS));
		rfqStatusCriteria.addAndCondition(CriteriaAPI.getCondition("DISCARDED","isDiscarded",String.valueOf(false),BooleanOperators.IS));

		return rfqStatusCriteria;
	}
	private static FacilioView getDiscardedRfqView(String viewName, String viewDisplayName, boolean discarded) {
		FacilioModule rfqModule = ModuleFactory.getRequestForQuotationModule();

		FacilioField createdTime = new FacilioField();
		createdTime.setName("localId");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("LOCAL_ID");
		createdTime.setModule(rfqModule);

		List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

		Criteria criteria = getDiscardedRfqCondition(discarded);
		FacilioView statusView = new FacilioView();
		statusView.setName(viewName);
		statusView.setDisplayName(viewDisplayName);
		statusView.setSortFields(sortFields);
		statusView.setCriteria(criteria);
		statusView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
		return statusView;
	}

	private static Criteria getDiscardedRfqCondition(boolean discarded) {
		FacilioField discardedField = new FacilioField();
		discardedField.setName("isDiscarded");
		discardedField.setColumnName("DISCARDED");
		discardedField.setDataType(FieldType.BOOLEAN);
		discardedField.setModule(ModuleFactory.getRequestForQuotationModule());

		Condition discardedCondition = new Condition();
		discardedCondition.setField(discardedField);
		discardedCondition.setOperator(BooleanOperators.IS);
		discardedCondition.setValue(String.valueOf(discarded));

		Criteria rfqStatusCriteria = new Criteria();
		rfqStatusCriteria.addAndCondition(discardedCondition);
		return rfqStatusCriteria;
	}
	private static FacilioView getMiscController() {

		FacilioModule miscControlerModule = ModuleFactory.getMisccontrolerModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Misc Controller");
		allView.setModuleName(miscControlerModule.getName());
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	private static FacilioView getBacnetController() {

		FacilioModule bacnetControllerModule = ModuleFactory.getBacnetipcontrollerModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All BACnet Controller");
		allView.setModuleName(bacnetControllerModule.getName());
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
//	private static FacilioView getBacnetmstController() {
//
//		FacilioModule bacnetipcontrollerModule = ModuleFactory.getBacnetmstControllerModule();
//
//		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));
//
//		FacilioView allView = new FacilioView();
//		allView.setName("all");
//		allView.setDisplayName("All BACnet Controller");
//		allView.setModuleName(bacnetipcontrollerModule.getName());
//		allView.setSortFields(sortFields);
//
//		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
//
//		return allView;
//	}
	private static FacilioView getNiagaraController() {

		FacilioModule niagaraControllerModule = ModuleFactory.getNiagaraControllerModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Niagara Controller");
		allView.setModuleName(niagaraControllerModule.getName());
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	private static FacilioView getModbusTcpController() {

		FacilioModule modbusTcpControllerModule = ModuleFactory.getModbusTcpControllerModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All ModbusTcp Controller");
		allView.setModuleName(modbusTcpControllerModule.getName());
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	private static FacilioView getModbusRtuontroller() {

		FacilioModule modbusRtuControllerModule = ModuleFactory.getModbusRtuControllerModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All ModbusRtu Controller");
		allView.setModuleName(modbusRtuControllerModule.getName());
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	private static FacilioView getOpcUaController() {

		FacilioModule opcUaControllerModule = ModuleFactory.getOpcUaControllerModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All OPC UA Controller");
		allView.setModuleName(opcUaControllerModule.getName());
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	private static FacilioView getOpcXmldaController() {

		FacilioModule opcXmlDaControllerModule = ModuleFactory.getOpcXmlDaControllerModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All OPC XML DA Controller");
		allView.setModuleName(opcXmlDaControllerModule.getName());
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	private static FacilioView getLonWorksController() {

		FacilioModule lonWorksontrollerModule = ModuleFactory.getLonWorksontrollerModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Lon Works Controller");
		allView.setModuleName(lonWorksontrollerModule.getName());
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
//	private static FacilioView getKnxController() {
//
//		FacilioModule bacnetipcontrollerModule = ModuleFactory.getKnxControllerModule();
//
//		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));
//
//		FacilioView allView = new FacilioView();
//		allView.setName("all");
//		allView.setDisplayName("All BACnet Controller");
//		allView.setModuleName(bacnetipcontrollerModule.getName());
//		allView.setSortFields(sortFields);
//
//		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));
//
//		return allView;
//	}
	private static FacilioView getCustomController() {

		FacilioModule customControllerModule = ModuleFactory.getCustomControllerModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Custom Controller");
		allView.setModuleName(customControllerModule.getName());
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	private static FacilioView getRestController() {

		FacilioModule restControllerModule = ModuleFactory.getRestControllerModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All Rest Controller");
		allView.setModuleName(restControllerModule.getName());
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	private static FacilioView getSystemController() {

		FacilioModule systemControllerModule = ModuleFactory.getSystemControllerModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All System Controller");
		allView.setModuleName(systemControllerModule.getName());
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
	private static FacilioView getRdmController() {

		FacilioModule rdmControllerModule = ModuleFactory.getRdmControllerModule();

		List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

		FacilioView allView = new FacilioView();
		allView.setName("all");
		allView.setDisplayName("All RDM Controller");
		allView.setModuleName(rdmControllerModule.getName());
		allView.setSortFields(sortFields);

		allView.setViewSharing(getSharingContext(Collections.singletonList(AppDomain.AppDomainType.FACILIO)));

		return allView;
	}
}
