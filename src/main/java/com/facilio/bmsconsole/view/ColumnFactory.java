package com.facilio.bmsconsole.view;

import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ColumnFactory {
	
	private static Map<String, List<ViewField>> columns = Collections.unmodifiableMap(initColumns());
	public static List<ViewField> getColumns (String moduleName, String viewName) {
		String name = moduleName + "-" +viewName;
		if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			name = moduleName;
		}
		if (!columns.containsKey(name)) {
			name = moduleName + "-default";
		}
		if (columns.containsKey(name)) {
			return new ArrayList(columns.get(name));
		}
		return null;
	}
	
	private static Map<String, List<ViewField>> initColumns() {
		Map<String, List<ViewField>> columnMap = new HashMap<>();
		columnMap.put("workorder-myopen", getMyWorkorderColumns());
		
//		columnMap.put("alarm-active", getDefaultAlarmColumns());
		
//		columnMap.put("asset-energy", getDefaultAssetsColumns());
		
		// For getting default columns for a module
		columnMap.put("approval-default", getWoApprovalDefaultViewColumns());
		columnMap.put("workorder-default", getWorkorderDefaultViewColumns());
		columnMap.put("workorder-pendingapproval", getWorkorderApprovalColumns());
		columnMap.put("workorder-tenantWorkorder", getTenantWorkorderColumns());
		columnMap.put("workorder-tenantOpen", getTenantWorkorderColumns());
		columnMap.put("workorder-tenantClosed", getTenantWorkorderColumns());
		columnMap.put("workorder-tenantAll", getTenantWorkorderColumns());
		columnMap.put("workorder-vendorWorkorder", getVendorWorkorderColumns());
		columnMap.put("alarm-default", getDefaultAlarmColumns());
		columnMap.put("energy-default", getDefaultEnergyColumns());
		columnMap.put("asset-default", getDefaultAssetsColumns());
		columnMap.put("energymeter-default", getDefaultAssetsColumns());
		columnMap.put("tenant-default", getDefaultTenantsColumns());
		columnMap.put("inventory-default", getDefaultInventoryColumns());
		columnMap.put("storeRoom-default", getDefaultStoreRoomColumns());
		columnMap.put("itemTypes-default", getDefaultItemTypesColumns());
		columnMap.put("toolTypes-default", getDefaultToolTypesColumns());
		columnMap.put("vendors-default", getDefaultVendorsColumns());
		columnMap.put("item-default", getDefaultItemColumns());
		columnMap.put("tool-default", getDefaultToolColumns());
		columnMap.put("stockedTools-default", getDefaultStockedToolsColumns());
		columnMap.put("purchaserequest-default", getDefaultPurchaseRequestColumns());
		columnMap.put("purchaseorder-default", getDefaultPurchaseOrderColumns());
		columnMap.put("receivable-default", getDefaultReceivableColumns());
		columnMap.put("purchasecontracts-default", getDefaultContractColumns());
		columnMap.put("labourcontracts-default", getDefaultContractColumns());
		columnMap.put("poLineItemSerialNumbers-default", getDefaultPoLineItemsSerialNumberColumns());
		columnMap.put("toolTransactions-default", getDefaultToolTransactionsColumns());
		columnMap.put("itemTransactions-default", getDefaultItemTransactionsColumns());
		columnMap.put("purchasedItem-default", getDefaultPurchasedItemColumns());
		columnMap.put("gatePass-default", getDefaultGatePassColumns());
		columnMap.put("shipment-default", getDefaultShipmentColumns());
		columnMap.put("attendance-default", getDefaultAttendanceColumns());
		columnMap.put("attendanceTransaction-default", getDefaultAttendanceTransactionColumns());
		columnMap.put("shift-default", getDefaultShiftColumns());
		columnMap.put("break-default", getDefaultBreakColumns());
		columnMap.put("shiftRotation-default", getDefaultShiftRotationColumns());
		// reading alarm
		columnMap.put("newreadingalarm-default", getDefaultReadingAlarmColumns());
		columnMap.put("newreadingalarm-active", getDefaultReadingAlarmColumns());
		columnMap.put("newreadingalarm-unacknowledged", getDefaultReadingAlarmColumns());
		columnMap.put("newreadingalarm-critical", getDefaultReadingAlarmColumns());
		columnMap.put("newreadingalarm-major", getDefaultReadingAlarmColumns());
		columnMap.put("newreadingalarm-minor", getDefaultReadingAlarmColumns());
		columnMap.put("newreadingalarm-cleared", getDefaultReadingAlarmColumns());

		//bmsAlarm
		columnMap.put("bmsalarm-default", getDefaultBmsAlarmColumns());
		columnMap.put("bmsalarm-bmsAlarm",  getDefaultBmsAlarmColumns());
		columnMap.put("bmsalarm-active",  getDefaultBmsAlarmColumns());
		columnMap.put("bmsalarm-unacknowledged",  getDefaultBmsAlarmColumns());
		columnMap.put("bmsalarm-critical",  getDefaultBmsAlarmColumns());
		columnMap.put("bmsalarm-major",  getDefaultBmsAlarmColumns());
		columnMap.put("bmsalarm-minor",  getDefaultBmsAlarmColumns());
		columnMap.put("bmsalarm-cleared",  getDefaultBmsAlarmColumns());

		// agent Alarm

		columnMap.put("agentAlarm-default", getDefaultAgentAlarmColumns());

		// operational Alarm
		columnMap.put("operationalarm-default", getDefaultOperationalAlarmColumns());


		columnMap.put("mlAnomalyAlarm-default", getDefaultMlAnomalyAlarmColumns());
		columnMap.put("service-default", getDefaultServiceColumns());
		columnMap.put("contracts-default", getDefaultContractColumns());
		columnMap.put("contracts-all", getAllContractColumns());
		columnMap.put("vendorDocuments-all", getAllVendorDocumentsColumns());

		columnMap.put("rentalleasecontracts-default", getDefaultRentalLeaseContractColumns());
		columnMap.put("warrantycontracts-default", getDefaultWarrantyContractColumns());
		columnMap.put("termsandconditions-default", getDefaultTermsAndConditionColumns());
		columnMap.put("reservation-default", getDefaultReservationColumns());
		columnMap.put("inventoryrequest-pendingapproval", getDefaultInventoryRequestColumns());
		columnMap.put("visitor-default", getDefaultVisitorColumns());
		columnMap.put("visitorinvite-default", getDefaultVisitorInvitesColumns());
		columnMap.put("visitorinvite-invite_myInvites", getTenantPortalVisitorInvitesColumns());
		columnMap.put("visitorinvite-invite_myExpired", getTenantPortalVisitorInvitesColumns());

		// visitor logging
		columnMap.put("visitorlogging-default", getDefaultVisitorLoggingColumns());
		// vl tenant portal
		columnMap.put("visitorlogging-myActive", getMyVisitorInvitesColumns());
		columnMap.put("visitorlogging-myCurrent", getMyVisitorInvitesColumns());
		columnMap.put("visitorlogging-myExpired", getMyExpiredVisitorInvitesColumns());
		// vl vendor portal
		columnMap.put("visitorlogging-vendorActiveVisitors", getVendorUpcomingVisitorInvitesColumns());
		columnMap.put("visitorlogging-vendorVisits", getVendorAllVisitorInvitesColumns());
		columnMap.put("visitorlogging-vendorCurrentVisits", getVendorVisitorInvitesColumns());
		columnMap.put("visitorlogging-vendorExpired", getVendorVisitorInvitesColumns());

		columnMap.put("visitorlogging-invite_all", getDefaultVisitorInvitesColumns());
		columnMap.put("visitorlogging-myInvites", getMyVisitorInvitesColumns());
		columnMap.put("visitorlogging-myAll", getMyVisitorInvitesColumns());
		columnMap.put("visitorlogging-myPendingVisits", getmyVisitsColumns());
		columnMap.put("visitorlogging-all", getmyVisitsColumns());
		columnMap.put("visitorlogging-tenantAll", getTenantPortalAllVisitsColumns());


		columnMap.put("insurance-default", getDefaultInsuranceColumns());
		columnMap.put("insurance-vendor", getVendorInsuranceColumns());
		columnMap.put("insurance-vendorActive", getVendorInsuranceColumns());
		columnMap.put("insurance-vendorExpired", getVendorExpiredInsuranceColumns());
		columnMap.put("watchlist-default", getDefaultWatchListColumns());
		columnMap.put("workpermit-default", getDefaultWorkPermitColumns());
		columnMap.put("workpermit-hidden-all", getHiddenAllWorkPermitColumns());
		columnMap.put("workpermit-myWorkpermits", getMyWorkPermitColumns());
		columnMap.put("workpermit-myActive", getMyWorkPermitColumns());
		columnMap.put("workpermit-myRequested", getMyActiveWorkPermitColumns());
		columnMap.put("workpermit-myExpired", getMyActiveWorkPermitColumns());
		columnMap.put("workpermit-vendorWorkpermits", getVendorWorkPermitColumns());
		columnMap.put("workpermit-vendorActiveWorkpermits", getVendorWorkPermitColumns());
		columnMap.put("workpermit-vendorExpiredWorkpermits", getVendorWorkPermitColumns());
		columnMap.put("vendors-myVendors", getMyVendorsColumns());
		columnMap.put("vendors-myNonInsuredVendors", getMyVendorsColumns());
		columnMap.put("contact-default", getDefaultContactColumns());
		columnMap.put("occupant-default", getDefaultOccupantColumns());
		columnMap.put("workpermit-requested", getRequestedWorkPermitColumns());
		columnMap.put("contact-tenant", getTenantContactColumns());
		columnMap.put("contact-vendor", getVendorContactColumns());
		columnMap.put("contact-employee", getEmployeeContactColumns());
		columnMap.put("tenantcontact-default", getTenantContactv2Columns());
		columnMap.put("tenantcontact-all", getHiddenTenantContactv2Columns());

		columnMap.put("vendorcontact-default", getVendorContactv2Columns());
		columnMap.put("vendorcontact-all", getHiddenVendorContactv2Columns());

		columnMap.put("clientcontact-default", getClientContactColumns());
		columnMap.put("clientcontact-all", getHiddenClientContactColumns());

		columnMap.put("employee-default", getEmployeeColumns());
		
		columnMap.put("serviceRequest-default", getDefaultServiceRequestColumns());
		columnMap.put("task-all", getAllTasksColumns());
		// portal vendors
		columnMap.put("vendors-myRequestedVendors", getPortalVendorsColumns());
		columnMap.put("vendors-myRegisteredVendors", getPortalVendorsColumns());
		columnMap.put("vendors-myApprovedVendors", getPortalVendorsColumns());
		columnMap.put("vendors-myInactiveVendors", getPortalVendorsColumns());
		columnMap.put("vendors-myAll", getPortalVendorsColumns());

		// safety plan submodules default columns
		columnMap.put("safetyPlanHazard-all", getSafetyPlanHazardColumns());
		columnMap.put("assetHazard-all", getSafetyPlanHazardColumns());
		columnMap.put("workorderHazard-all", getSafetyPlanHazardColumns());
		columnMap.put("hazardPrecaution-all", getAllHazardPrecautionsColumns());
		columnMap.put("hazardPrecaution-associatedprecautions", getHazardPrecautionsColumns());
		columnMap.put("hazardPrecaution-associatedhazards", getAssociatedHazardPrecautionsColumns());

		// site,building,space columns
		columnMap.put("site-default", getDefaultSiteViewColumns());
		columnMap.put("building-default", getDefaultBuildingViewColumns());
		columnMap.put("space-default", getDefaultSpaceViewColumns());
		columnMap.put("floor-default", getDefaultFloorViewColumns());
		columnMap.put("tenantunit-default",getDefaultTenantUnitSpaceColumns());
		columnMap.put("tenantspaces-default",getDefaultTenantSpacesColumns());
		
		columnMap.put("people-default",getDefaultPeopleColumns());
		columnMap.put("client-default", getDefaultClientColumns());

		columnMap.put("quote-default", getDefaultQuotationColumns());
		columnMap.put("quoteterms-default", getDefaultQuotationTermsColumns());

		// Default report columns
		columnMap.put("workorder-report", getWorkOrderReportColumns());
		columnMap.put("alarm-report", getAlarmReportColumns());
		columnMap.put("energydata-report", getDefaultEnergyColumns());

		// Special types
		columnMap.put("preventivemaintenance-default", getPreventiveMaintenanceColumns());
		
		return columnMap;
	}
	
	private static final Map<String, List<SortField>> defaultSortFields = Collections.unmodifiableMap(initDefaultSortFields());
	
	private static Map<String, List<SortField>> initDefaultSortFields() {
		Map<String, List<SortField>> defaultMap = new HashMap<>();
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(ModuleFactory.getWorkOrdersModule());
		
		FacilioField modifiedTime = new FacilioField();
		modifiedTime.setColumnName("MODIFIED_TIME");
		modifiedTime.setName("modifiedTime");
		modifiedTime.setDataType(FieldType.DATE_TIME);
		modifiedTime.setModule(ModuleFactory.getAlarmsModule());

		FacilioField localId = new FacilioField();
		localId.setName("localId");
		localId.setColumnName("LOCAL_ID");
		localId.setDataType(FieldType.NUMBER);
		localId.setModule(ModuleFactory.getAssetsModule());
		
		FacilioField woReqCreatedTime = new FacilioField();
		woReqCreatedTime.setName("createdTime");
		woReqCreatedTime.setDataType(FieldType.NUMBER);
		woReqCreatedTime.setColumnName("CREATED_TIME");
		woReqCreatedTime.setModule(ModuleFactory.getWorkOrderRequestsModule());
		
		defaultMap.put("workorder", Arrays.asList(new SortField(createdTime, false)));
		defaultMap.put("alarm", Arrays.asList(new SortField(modifiedTime, false)));
		defaultMap.put("asset", Arrays.asList(new SortField(localId, false)));
		defaultMap.put("workorderrequest", Arrays.asList(new SortField(woReqCreatedTime, false)));
		
		return defaultMap;
	}
	
	public static List<SortField> getDefaultSortField(String moduleName) {
		return defaultSortFields.get(moduleName);
	}
	
	private static List<ViewField> getDefaultSiteViewColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("name", "Site Name"));
		columns.add(new ViewField("siteType", "Site Type"));
		columns.add(new ViewField("managedBy", "Mananged By"));
		columns.add(new ViewField("grossFloorArea", "Floor Area"));
		columns.add(new ViewField("area", "Total Area"));
		
		return columns;
	}
	
	
	private static List<ViewField> getWorkorderDefaultViewColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("subject", "Subject"));
		columns.add(new ViewField("category", "Category"));
		columns.add(new ViewField("resource", "Space / Asset"));
		columns.add(new ViewField("assignedTo", "Team / Staff"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("priority", "Priority"));
		columns.add(new ViewField("noOfNotes", "Comments"));
		columns.add(new ViewField("noOfTasks", "Tasks"));
		columns.add(new ViewField("modifiedTime", "Last Updated Time"));
		columns.add(new ViewField("actualWorkEnd", "Closed Time"));
		return columns;
	}
	
	private static List<ViewField> getWorkorderApprovalColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("serialNumber", "ID"));
		columns.add(new ViewField("subject", "Subject"));
		columns.add(new ViewField("resource", "Space / Asset"));
		columns.add(new ViewField("requester", "Requester"));
		columns.add(new ViewField("siteId", "Site"));
		columns.add(new ViewField("category", "Category"));
		columns.add(new ViewField("urgency", "Urgency"));
		columns.add(new ViewField("createdTime", "Created Time"));
		return columns;
	}

	private static List<ViewField> getWoApprovalDefaultViewColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("serialNumber", "ID"));
		columns.add(new ViewField("subject", "Subject"));
		columns.add(new ViewField("requester", "Requester"));
		columns.add(new ViewField("priority", "Priority"));
		columns.add(new ViewField("createdTime", "Created Time"));
		return columns;
	}
	
	private static List<ViewField> getVendorWorkorderColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("subject", "Subject"));
//		columns.add(new ViewField("site", "Site"));
		columns.add(new ViewField("resource", "Space / Asset"));
		columns.add(new ViewField("category", "Category"));
		columns.add(new ViewField("priority", "Priority"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("createdTime", "Created Time"));
		columns.add(new ViewField("actualWorkEnd", "Resolved Time"));
		columns.add(new ViewField("dueDate", "Due Date"));
		return columns;
	}	
	
	private static List<ViewField> getTenantWorkorderColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("subject", "Subject"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("urgency", "Urgency"));
		columns.add(new ViewField("assignedTo", "Assigned To"));
		columns.add(new ViewField("vendor", "Vendor"));
		columns.add(new ViewField("createdTime", "Created Time"));
		columns.add(new ViewField("actualWorkEnd", "Resolved Time"));
		return columns;
	}	
	
	private static List<ViewField> getMyWorkorderColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("subject", "Subject"));
		columns.add(new ViewField("category", "Category"));
		columns.add(new ViewField("resource", "Space / Asset"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("priority", "Priority"));
		columns.add(new ViewField("noOfNotes", "Comments"));
		columns.add(new ViewField("noOfTasks", "Tasks"));
		
		return columns;
	}
	
	private static List<ViewField> getDefaultAlarmColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("severity", "Severity"));
		columns.add(new ViewField("subject", "Message"));
		columns.add(new ViewField("alarmType", "Category"));
		columns.add(new ViewField("source", "Source"));
		columns.add(new ViewField("condition", "Condition"));
		columns.add(new ViewField("resource", "Asset"));
		columns.add(new ViewField("modifiedTime", "Last Reported"));
		columns.add(new ViewField("acknowledgedBy", "Acknowledged"));
		
		return columns;
	}
	
	private static List<ViewField> getDefaultEnergyColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("ttime", "Timestamp"));
		columns.add(new ViewField("totalEnergyConsumptionDelta", "Total Energy Consumption Delta"));
		
		return columns;
	}
	
	private static List<ViewField> getDefaultAssetsColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("type", "Type"));
		columns.add(new ViewField("category", "Category"));
		columns.add(new ViewField("department", "Department"));
//		columns.add(new ViewField("movable", "Movable"));		// TODO needs to handle parent field
		
		return columns;
	}
	
	private static List<ViewField> getDefaultTenantsColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("name", "Tenant Name"));
		columns.add(new ViewField("description", "Description"));
		columns.add(new ViewField("primaryContactName", "Primary Contact Name"));
		columns.add(new ViewField("primaryContactEmail", "Primary Contact Email"));
		columns.add(new ViewField("inTime", "Lease Start Date"));
		columns.add(new ViewField("outTime", "Lease End Date"));
				
		return columns;
	}
	
	private static List<ViewField> getWorkOrderReportColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("subject", "Subject"));
		columns.add(new ViewField("resource", "Space / Asset"));
		columns.add(new ViewField("assignedTo", "Assigned To"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("priority", "Priority"));
		
		return columns;
	}
	
	private static List<ViewField> getAlarmReportColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("subject", "Message"));
		columns.add(new ViewField("severity", "Severity"));
		columns.add(new ViewField("source", "Source"));
		columns.add(new ViewField("condition", "Condition"));
		columns.add(new ViewField("acknowledgedBy", "Acknowledged By"));
		
		return columns;
	}
	
	private static List<ViewField> getDefaultInventoryColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("name", "Part Name"));
		columns.add(new ViewField("category", "Category"));
		columns.add(new ViewField("vendor", "Vendor"));
//		columns.add(new ViewField("movable", "Movable"));	
		return columns;
}// TODO needs to handle parent field
	private static List<ViewField> getPreventiveMaintenanceColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("title", "Message"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("createdById", "createdById"));
		columns.add(new ViewField("createdTime", "createdTime"));
		columns.add(new ViewField("lastModifiedTime", "lastModifiedTime"));
		
		return columns;
	}
	
	private static List<ViewField> getDefaultStoreRoomColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("description", "Description"));
		columns.add(new ViewField("location", "Location"));
		columns.add(new ViewField("owner", "Owner"));
		
		return columns;
	}
	
	private static List<ViewField> getDefaultItemTypesColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("description", "Description"));
		columns.add(new ViewField("category", "Category"));
		columns.add(new ViewField("currentQuantity", "Current Quantity"));
		columns.add(new ViewField("lastPurchasedPrice", "Last Purchased Price"));
		columns.add(new ViewField("lastPurchasedDate", "Last Purchased Date"));
		
		return columns;
	}

	private static List<ViewField> getDefaultToolTypesColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("description", "Description"));
		columns.add(new ViewField("category", "Category"));
		columns.add(new ViewField("currentQuantity", "Current Quantity"));
		columns.add(new ViewField("lastPurchasedDate", "Last Purchased Date"));
		columns.add(new ViewField("lastIssuedDate", "Last Issued Date"));
		
		return columns;
	}
	
	private static List<ViewField> getDefaultVendorsColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("website", "Website"));
		columns.add(new ViewField("primaryContactName", "Primary Contact Name"));
		columns.add(new ViewField("primaryContactEmail", "Primary Contact E-Mail"));
		columns.add(new ViewField("primaryContactPhone", "Primary Contact Phone"));
		columns.add(new ViewField("registeredBy", "Registered By"));
		
		return columns;
	}

	private static List<ViewField> getPortalVendorsColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();

		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("primaryContactName", "Contact Name"));
		columns.add(new ViewField("primaryContactEmail", "Contact E-Mail"));
		columns.add(new ViewField("primaryContactPhone", "Contact Phone"));
		columns.add(new ViewField("website", "Website"));
		columns.add(new ViewField("moduleState", "Status"));
		return columns;
	}
	
	private static List<ViewField> getMyVendorsColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("primaryContactName", "Primary Contact"));
		columns.add(new ViewField("primaryContactEmail", "Contact E-Mail"));
		columns.add(new ViewField("primaryContactPhone", "Contact Phone"));
		columns.add(new ViewField("website", "Website"));
		columns.add(new ViewField("moduleState", "Status"));
		return columns;
	}
	
	
	private static List<ViewField> getDefaultItemColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		
		columns.add(new ViewField("itemType", "Item"));
		columns.add(new ViewField("storeRoom", "Storeroom"));
		columns.add(new ViewField("quantity", "Current Balance"));
		columns.add(new ViewField("minimumQuantity", "Mininum Quantity"));
//		columns.add(new ViewField("costType", "Cost Type"));
		columns.add(new ViewField("lastPurchasedDate", "Last Purchased Date"));
		columns.add(new ViewField("lastPurchasedPrice", "Last Purchased Price"));
		
		return columns;
	}
	private static List<ViewField> getDefaultToolColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("storeRoom", "Storeroom"));
		columns.add(new ViewField("quantity", "Quantity"));
		columns.add(new ViewField("currentQuantity", "Current Balance"));
		columns.add(new ViewField("lastPurchasedDate", "Last Purchased Date"));
		
		return columns;
	}
	
	private static List<ViewField> getDefaultStockedToolsColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("tool", "Tool"));
		columns.add(new ViewField("storeRoom", "Store Room"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("quantity", "Quantity"));
		
		return columns;
	}
	
	private static List<ViewField> getDefaultToolTransactionsColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
//		columns.add(new ViewField("toolType", "Tool Type"));
//		columns.add(new ViewField("tool", "Tool"));
		columns.add(new ViewField("quantity", "Quantity"));
		columns.add(new ViewField("sysCreatedTime", "Requested Time"));
//		columns.add(new ViewField("issuedTo", "Issued To"));
		return columns;
	}
	
	private static List<ViewField> getDefaultItemTransactionsColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
//		columns.add(new ViewField("itemType", "Item Type"));
		columns.add(new ViewField("quantity", "Quantity"));
		columns.add(new ViewField("sysCreatedTime", "Requested Time"));
//		columns.add(new ViewField("issuedTo", "Issued To"));
		return columns;
	}
	
	private static List<ViewField> getDefaultPurchasedItemColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("quantity", "Quantity"));
		columns.add(new ViewField("currentQuantity", "Current Quantity"));
		columns.add(new ViewField("serialNumber", "Serial Number"));
		columns.add(new ViewField("isUsed", "Is Used"));
		columns.add(new ViewField("unitcost", "Unit Cost"));
		return columns;
	}
	
	private static List<ViewField> getDefaultGatePassColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("issuedTo", "Issued To"));
		columns.add(new ViewField("issuedToPhoneNumber", "Phone Number"));
		columns.add(new ViewField("fromStoreRoom", "From Storeroom"));
		columns.add(new ViewField("toStoreRoom", "To Storeroom"));
		columns.add(new ViewField("issuedTime", "Issued Time"));
		columns.add(new ViewField("returnTime", "Return Time"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("issuedBy", "Issued By"));
		columns.add(new ViewField("isReturnable", "Is Returnable"));
		columns.add(new ViewField("gatePassType", "Gate Pass Type"));
		columns.add(new ViewField("vehicleNo", "Vehicle Number"));
		return columns;
	}
	
	private static List<ViewField> getDefaultPurchaseRequestColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
//		columns.add(new ViewField("localId", "Purchase Request Id"));
		columns.add(new ViewField("name", "Purchase Request Name"));
	//	columns.add(new ViewField("description", "Description"));
//		columns.add(new ViewField("vendor", "Vendor"));
		columns.add(new ViewField("requestedTime", "Requested Date"));
		columns.add(new ViewField("requiredTime", "Required Date"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("totalCost", "Total Cost"));
				
		return columns;
	}
	
	private static List<ViewField> getDefaultPurchaseOrderColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("localId", "Purchase Order Id"));
//		columns.add(new ViewField("name", "Purchase Order Name"));
//		columns.add(new ViewField("description", "Description"));
		columns.add(new ViewField("vendor", "Vendor"));
		columns.add(new ViewField("storeRoom", "Storeroom"));
		columns.add(new ViewField("orderedTime", "Ordered Date"));
		columns.add(new ViewField("requiredTime", "Required Date"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("totalCost", "Total Cost"));
		return columns;
	}
	
	
	private static List<ViewField> getDefaultReceivableColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
//		columns.add(new ViewField("localId", "Receivable Id"));
//		columns.add(new ViewField("poId", "Purchase Order ID"));
		columns.add(new ViewField("status", "Status"));
//		columns.add(new ViewField("sysCreatedTime", "Created Time"));
		return columns;
	}
	private static List<ViewField> getDefaultContractColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
	//	columns.add(new ViewField("parentId", "ID"));
		columns.add(new ViewField("vendor", "Vendor"));
		columns.add(new ViewField("contractType", "Type"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("fromDate", "Valid From"));
		columns.add(new ViewField("endDate", "Valid Till"));
		columns.add(new ViewField("renewalDate", "Renewal Date"));
		columns.add(new ViewField("totalCost", "Total Cost"));

		return columns;
	}
	
	private static List<ViewField> getAllContractColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("contractType", "Type"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("fromDate", "Valid From"));
		columns.add(new ViewField("endDate", "Valid Till"));
		columns.add(new ViewField("renewalDate", "Renewal Date"));
		columns.add(new ViewField("totalCost", "Total Cost"));

		return columns;
	}
	
	private static List<ViewField> getAllVendorDocumentsColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("documentName", "Document Name"));
		columns.add(new ViewField("documentType", "Type"));
		columns.add(new ViewField("document", "Document"));
		
		return columns;
	}

	private static List<ViewField> getDefaultRentalLeaseContractColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("rentalLeaseContractType", "Type"));
		columns.add(new ViewField("vendor", "Vendor"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("fromDate", "Valid From"));
		columns.add(new ViewField("endDate", "Valid Till"));
		columns.add(new ViewField("renewalDate", "Renewal Date"));
		columns.add(new ViewField("totalCost", "Total Cost"));

		return columns;
	}

	private static List<ViewField> getDefaultWarrantyContractColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("vendor", "Vendor"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("fromDate", "Valid From"));
		columns.add(new ViewField("endDate", "Valid Till"));
		columns.add(new ViewField("renewalDate", "Renewal Date"));
		columns.add(new ViewField("totalCost", "Total Cost"));

		return columns;
	}
	
	private static List<ViewField> getDefaultPoLineItemsSerialNumberColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("serialNumber", "Serial Number"));
	
		return columns;
	}
	
	private static List<ViewField> getDefaultShipmentColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("fromStore", "From Store"));
		columns.add(new ViewField("toStore", "To Store"));
		columns.add(new ViewField("transferredBy", "Transferred By"));
		columns.add(new ViewField("receivedBy", "Received By"));
		columns.add(new ViewField("status", "Status"));
		return columns;
	}
	private static List<ViewField> getDefaultAttendanceColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
//		columns.add(new ViewField("user", "User"));
		columns.add(new ViewField("checkInTime", "Check-in Time"));
		columns.add(new ViewField("checkOutTime", "Check-out Time"));
		columns.add(new ViewField("workingHours", "Working Hours"));
		columns.add(new ViewField("day", "Day"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("shift", "Shift"));
		columns.add(new ViewField("lastCheckInTime", "Last Check-in Time"));
		columns.add(new ViewField("totalPaidBreakHrs", "Total Paid Break Hours"));
		columns.add(new ViewField("totalUnpaidBreakHrs", "Total Unpaid Break Hours"));
//		columns.add(new ViewField("lastBreakStartTime", "Last Break Start Time"));
		return columns;
	}
	
	private static List<ViewField> getDefaultAttendanceTransactionColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("attendance", "Attendance"));
		columns.add(new ViewField("transactionType", "Transaction Type"));
		columns.add(new ViewField("sourceType", "Source Type"));
		columns.add(new ViewField("location", "Location"));
		columns.add(new ViewField("ipAddress", "IP Address"));
		columns.add(new ViewField("terminal", "Terminal"));
		columns.add(new ViewField("transactionTime", "Transaction Time"));
		return columns;
	}
	
	private static List<ViewField> getDefaultShiftColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("startTime", "Start Time"));
		columns.add(new ViewField("endTime", "End Time"));
		columns.add(new ViewField("defaultShift", "Is Default"));
		return columns;
	}
	
	private static List<ViewField> getDefaultBreakColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("breakTime", "Break Time"));
		columns.add(new ViewField("breakType", "Break Type"));
		return columns;
	}

	private static List<ViewField> getDefaultServiceColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();

		columns.add(new ViewField("description", "Description"));
		columns.add(new ViewField("duration", "Duration"));
		columns.add(new ViewField("status", "Status"));
		return columns;
	}

	private static List<ViewField> getDefaultTermsAndConditionColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("termType", "Term Type"));
		columns.add(new ViewField("shortDesc", "Short Description"));
		columns.add(new ViewField("longDesc", "Long Description"));
//		columns.add(new ViewField("isEditable", "Editable"));
		columns.add(new ViewField("defaultOnPo", "Default On PO"));
		return columns;
	}

	private static List<ViewField> getDefaultShiftRotationColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();

		columns.add(new ViewField("schedularFrequency", "Schedular Frequency"));
		columns.add(new ViewField("timeOfSchedule", "Time Of Schedule"));
		columns.add(new ViewField("schedularDay", "Schedular Day"));
		return columns;
	}

	private static List<ViewField> getDefaultReadingAlarmColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("severity", "Severity"));
		columns.add(new ViewField("subject", "Message"));
		columns.add(new ViewField("resource", "Space / Asset"));
		columns.add(new ViewField("lastOccurredTime", "Last Reported"));
		columns.add(new ViewField("acknowledgedBy", "Acknowledged By"));
		columns.add(new ViewField("acknowledged", "Acknowledged"));
		columns.add(new ViewField("noOfOccurrences", "Occurrences"));
		columns.add(new ViewField("lastCreatedTime", "Last Occurred Time"));
		columns.add(new ViewField("readingalarmcategory", "Category" ));
		columns.add(new ViewField("faultType", "Fault Type"));
		// columns.add(new ViewField("rule", "Rule"));
		return columns;
	}
	
	private static List<ViewField> getDefaultReservationColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("space", "Space"));
		columns.add(new ViewField("scheduledStartTime", "Scheduled Start Time"));
		columns.add(new ViewField("scheduledEndTime", "Scheduled End Time"));
		columns.add(new ViewField("noOfAttendees", "No. Of Attendees"));
//		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("reservedFor", "Reserved For"));
		return columns;
	}
	
	private static List<ViewField> getDefaultInventoryRequestColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("localId", "ID"));
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("requestedTime", "Requested Time"));
		columns.add(new ViewField("requiredTime", "Required Time"));
		columns.add(new ViewField("status", "Status"));
		return columns;
	}
	
	private static List<ViewField> getDefaultBmsAlarmColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("severity", "Severity"));
		columns.add(new ViewField("subject", "Message"));
		columns.add(new ViewField("resource", "Space / Asset"));
		columns.add(new ViewField("source", "Source"));
		columns.add(new ViewField("condition", "Condition"));
		columns.add(new ViewField("lastOccurredTime", "Last Reported Time"));
		columns.add(new ViewField("acknowledgedBy", "Acknowledged By"));
		columns.add(new ViewField("acknowledged", "Acknowledged"));
		columns.add(new ViewField("noOfOccurrences", "Occurrences"));
		columns.add(new ViewField("lastCreatedTime", "Last Occurred Time"));
		return columns;
	}

	private static List<ViewField> getDefaultAgentAlarmColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("severity", "Severity"));
		columns.add(new ViewField("subject", "Message"));
		columns.add(new ViewField("agent", "Agent"));
		columns.add(new ViewField("lastOccurredTime", "Last Reported Time"));
		columns.add(new ViewField("acknowledgedBy", "Acknowledged By"));
		columns.add(new ViewField("acknowledged", "Acknowledged"));
		columns.add(new ViewField("noOfOccurrences", "Occurrences"));
		columns.add(new ViewField("lastCreatedTime", "Last Occurred Time"));
		return columns;
	}


	private static List<ViewField> getDefaultOperationalAlarmColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("severity", "Severity"));
		columns.add(new ViewField("subject", "Message"));
		columns.add(new ViewField("resource", "Asset"));
		columns.add(new ViewField("lastOccurredTime", "Last Reported Time"));
		columns.add(new ViewField("lastCreatedTime", "Last Occurred Time"));
		columns.add(new ViewField("noOfOccurrences", "Occurrences"));
		return columns;
	}
	
	private static List<ViewField> getDefaultMlAnomalyAlarmColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("severity", "Severity"));
		columns.add(new ViewField("subject", "Message"));
		columns.add(new ViewField("resource", "Asset"));
		columns.add(new ViewField("source", "Source"));
		columns.add(new ViewField("condition", "Condition"));
		columns.add(new ViewField("lastOccurredTime", "Last Reported Time"));
		columns.add(new ViewField("acknowledgedBy", "Acknowledged By"));
		columns.add(new ViewField("acknowledged", "Acknowledged"));
		columns.add(new ViewField("noOfOccurrences", "Occurrences"));
		columns.add(new ViewField("lastCreatedTime", "Last Occurred Time"));
		return columns;
	}

	public static List<ViewField> getDefaultVisitorColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("email", "Email"));
		columns.add(new ViewField("phone", "Phone"));
		columns.add(new ViewField("visitorType", "Type"));
		columns.add(new ViewField("lastVisitedTime", "Last Visited Time"));
		columns.add(new ViewField("lastVisitedHost", "Last Visited Host"));
		return columns;
	}

	public static List<ViewField> getDefaultVisitorLoggingColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("visitorName", "Name"));
		columns.add(new ViewField("visitorPhone", "Phone"));
		columns.add(new ViewField("visitorEmail", "Email"));
		columns.add(new ViewField("visitorType", "Type"));
		columns.add(new ViewField("checkInTime", "Check-In Time"));
		columns.add(new ViewField("checkOutTime", "Check-Out Time"));
		columns.add(new ViewField("host", "Host"));
		columns.add(new ViewField("purposeOfVisit", "Purpose Of Visit"));
		return columns;
	}


	public static List<ViewField> getDefaultVisitorInvitesColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("visitorName", "Name"));
		columns.add(new ViewField("visitorPhone", "Phone"));
		columns.add(new ViewField("visitorEmail", "Email"));
		columns.add(new ViewField("visitorType", "Type"));
		columns.add(new ViewField("host", "Host"));
		columns.add(new ViewField("moduleState", "Status"));
		columns.add(new ViewField("expectedCheckInTime", "Expected Check-in Time"));
		columns.add(new ViewField("expectedCheckOutTime", "Expected Check-out Time"));
		columns.add(new ViewField("purposeOfVisit", "Purpose Of Visit"));
		return columns;
	}

	public static List<ViewField> getTenantPortalVisitorInvitesColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("visitorName", "Name"));
		columns.add(new ViewField("visitorPhone", "Phone"));
		columns.add(new ViewField("visitorEmail", "Email"));
		columns.add(new ViewField("visitorType", "Type"));
		columns.add(new ViewField("host", "Host"));
		columns.add(new ViewField("moduleState", "Status"));
		columns.add(new ViewField("expectedCheckInTime", "Expected Check-in Time"));
		columns.add(new ViewField("expectedCheckOutTime", "Expected Check-out Time"));
		columns.add(new ViewField("purposeOfVisit", "Purpose Of Visit"));
		columns.add(new ViewField("requestedBy", "Requested By"));

		return columns;
	}
	
	public static List<ViewField> getMyVisitorInvitesColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("visitorName", "Name"));
		columns.add(new ViewField("vendor", "Vendor"));
		columns.add(new ViewField("host", "Host"));
		columns.add(new ViewField("visitorType", "Type"));
		columns.add(new ViewField("moduleState", "Status"));
		columns.add(new ViewField("expectedCheckInTime", "Expected Check-in"));
		columns.add(new ViewField("expectedCheckOutTime", "Expected Check-out"));
		columns.add(new ViewField("purposeOfVisit", "Purpose Of Visit"));
		columns.add(new ViewField("visitedSpace", "Visiting Space"));
		columns.add(new ViewField("requestedBy", "Requested By"));
	
		return columns;
	}
	
	public static List<ViewField> getMyExpiredVisitorInvitesColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("visitorName", "Name"));
		columns.add(new ViewField("vendor", "Vendor"));
		columns.add(new ViewField("host", "Host"));
		columns.add(new ViewField("visitorType", "Type"));
		columns.add(new ViewField("expectedCheckInTime", "Expected Check-in"));
		columns.add(new ViewField("expectedCheckOutTime", "Expected Check-out"));
		columns.add(new ViewField("purposeOfVisit", "Purpose Of Visit"));
		columns.add(new ViewField("visitedSpace", "Visiting Space"));
		columns.add(new ViewField("requestedBy", "Requested By"));
	
		return columns;
	}
	
	public static List<ViewField> getmyVisitsColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("visitorName", "Name"));
		columns.add(new ViewField("host", "Host"));
		columns.add(new ViewField("visitorType", "Type"));
		columns.add(new ViewField("checkInTime", "Check-in Time"));
		columns.add(new ViewField("checkOutTime", "Check-out Time"));
		columns.add(new ViewField("purposeOfVisit", "Purpose Of Visit"));
		columns.add(new ViewField("visitedSpace", "Visiting Space"));
		columns.add(new ViewField("requestedBy", "Requested By"));
//		columns.add(new ViewField("moduleState", "Status"));
	
	
		return columns;
	}

	public static List<ViewField> getTenantPortalAllVisitsColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("visitorName", "Name"));
		columns.add(new ViewField("host", "Host"));
		columns.add(new ViewField("visitorType", "Type"));
		columns.add(new ViewField("checkInTime", "Check-in Time"));
		columns.add(new ViewField("checkOutTime", "Check-out Time"));
		columns.add(new ViewField("purposeOfVisit", "Purpose Of Visit"));
		columns.add(new ViewField("visitedSpace", "Visiting Space"));
		columns.add(new ViewField("requestedBy", "Requested By"));
		columns.add(new ViewField("moduleState", "Module Status"));


		return columns;
	}
	
	public static List<ViewField> getVendorVisitsInvitesColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("visitorName", "Name"));
		columns.add(new ViewField("visitorPhone", "Phone"));
		columns.add(new ViewField("visitorEmail", "Email"));
		columns.add(new ViewField("visitorType", "Type"));
		columns.add(new ViewField("host", "Host"));
		columns.add(new ViewField("visitedSpace", "Visiting Space"));
		columns.add(new ViewField("requestedBy", "Requested By"));
		columns.add(new ViewField("tenant", "Tenant"));
		columns.add(new ViewField("moduleState", "Status"));
		columns.add(new ViewField("checkInTime", "Check-in Time"));
		columns.add(new ViewField("checkOutTime", "Check-out Time"));
		columns.add(new ViewField("purposeOfVisit", "Purpose Of Visit"));
		
		return columns;
	}
	
	public static List<ViewField> getVendorVisitorInvitesColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("visitorName", "Name"));
		columns.add(new ViewField("host", "Host"));
		columns.add(new ViewField("tenant", "Tenant"));
		columns.add(new ViewField("visitorType", "Type"));
		columns.add(new ViewField("purposeOfVisit", "Purpose Of Visit"));
		columns.add(new ViewField("visitedSpace", "Visiting Space"));

		return columns;
	}
	
	public static List<ViewField> getAllVisitsColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("visitorName", "Name"));
		columns.add(new ViewField("visitorPhone", "Phone"));
		columns.add(new ViewField("visitorEmail", "Email"));
		columns.add(new ViewField("visitorType", "Type"));
		columns.add(new ViewField("moduleState", "Status"));
		columns.add(new ViewField("checkInTime", "Check-in Time"));
		columns.add(new ViewField("checkOutTime", "Check-out Time"));
		columns.add(new ViewField("host", "Host"));
		columns.add(new ViewField("purposeOfVisit", "Purpose Of Visit"));
		
		return columns;
	}

	public static List<ViewField> getVendorUpcomingVisitorInvitesColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("visitorName", "Name"));
		columns.add(new ViewField("host", "Host"));
		columns.add(new ViewField("tenant", "Tenant"));
		columns.add(new ViewField("visitorType", "Type"));
		columns.add(new ViewField("expectedCheckInTime", "Expected Check-in Time"));
		columns.add(new ViewField("expectedCheckOutTime", "Expected Check-out Time"));
		columns.add(new ViewField("purposeOfVisit", "Purpose Of Visit"));
		columns.add(new ViewField("visitedSpace", "Visiting Space"));
		columns.add(new ViewField("moduleState", "Status"));

		return columns;
	}
	public static List<ViewField> getVendorAllVisitorInvitesColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("visitorName", "Name"));
		columns.add(new ViewField("host", "Host"));
		columns.add(new ViewField("tenant", "Tenant"));
		columns.add(new ViewField("visitorType", "Type"));
		columns.add(new ViewField("expectedCheckInTime", "Expected Check-in Time"));
		columns.add(new ViewField("expectedCheckOutTime", "Expected Check-out Time"));
		columns.add(new ViewField("purposeOfVisit", "Purpose Of Visit"));
		columns.add(new ViewField("visitedSpace", "Visiting Space"));
		columns.add(new ViewField("checkInTime", "Check-in Time"));
		columns.add(new ViewField("checkOutTime", "Check-out Time"));
		columns.add(new ViewField("moduleState", "Status"));

		return columns;
	}

	public static List<ViewField> getDefaultInsuranceColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("companyName", "Company Name"));
		columns.add(new ViewField("validFrom", "Valid From"));
		columns.add(new ViewField("validTill", "Valid Till"));
		columns.add(new ViewField("insurance", "Insurance"));
		return columns;
	}
	
	public static List<ViewField> getVendorInsuranceColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("companyName", "Company Name"));
		columns.add(new ViewField("validFrom", "Valid From"));
		columns.add(new ViewField("validTill", "Valid Till"));
		columns.add(new ViewField("insurance", "Insurance"));
		columns.add(new ViewField("moduleState", "Status"));
		return columns;
	}
	public static List<ViewField> getVendorExpiredInsuranceColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("companyName", "Company Name"));
		columns.add(new ViewField("validFrom", "Valid From"));
		columns.add(new ViewField("validTill", "Valid Till"));
		columns.add(new ViewField("insurance", "Insurance"));
		return columns;
	}
	
	public static List<ViewField> getDefaultWorkPermitColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Subject"));
		columns.add(new ViewField("vendor", "Vendor"));
		columns.add(new ViewField("people", "Contact"));
		columns.add(new ViewField("space", "Location"));
		columns.add(new ViewField("expectedStartTime", "Valid From"));
		columns.add(new ViewField("expectedEndTime", "Valid To"));
		columns.add(new ViewField("workPermitType", "Permit Type"));
		columns.add(new ViewField("requestedBy", "Requested By"));
		columns.add(new ViewField("moduleState", "Status"));
		return columns;
	}
	public static List<ViewField> getHiddenAllWorkPermitColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("expectedStartTime", "Valid From"));
		columns.add(new ViewField("expectedEndTime", "Valid Till"));
		columns.add(new ViewField("workType", "Work Type"));
		columns.add(new ViewField("requestedBy", "Requested By"));
		columns.add(new ViewField("moduleState", "Status"));
		return columns;
	}
	
	public static List<ViewField> getMyWorkPermitColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("vendor", "Vendor"));
		columns.add(new ViewField("vendorContact", "Permit Holder"));
		columns.add(new ViewField("requestedBy", "Requested By"));
		columns.add(new ViewField("workType", "Work Type"));
		columns.add(new ViewField("expectedStartTime", "Valid From"));
		columns.add(new ViewField("expectedEndTime", "Valid To"));
		columns.add(new ViewField("moduleState", "Status"));

		return columns;
	}

	public static List<ViewField> getMyActiveWorkPermitColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("vendor", "Vendor"));
		columns.add(new ViewField("vendorContact", "Permit Holder"));
		columns.add(new ViewField("requestedBy", "Requested By"));
		columns.add(new ViewField("workType", "Work Type"));
		columns.add(new ViewField("expectedStartTime", "Valid From"));
		columns.add(new ViewField("expectedEndTime", "Valid To"));

		return columns;
	}
	
	public static List<ViewField> getRequestedWorkPermitColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("requestedBy", "Requested By"));
		columns.add(new ViewField("expectedStartTime", "Valid From"));
		columns.add(new ViewField("expectedEndTime", "Valid Till"));
		columns.add(new ViewField("workType", "Work Type"));
		columns.add(new ViewField("issuedToUser", "Permit Holder"));
		
		return columns;
	}
	
	public static List<ViewField> getVendorWorkPermitColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Permit Name"));
		columns.add(new ViewField("vendorContact", "Permit Holder"));
		columns.add(new ViewField("requestedBy", "Requested By"));
		columns.add(new ViewField("tenant", "Tenant"));
		columns.add(new ViewField("workType", "Work Type"));
		columns.add(new ViewField("expectedStartTime", "Valid From"));
		columns.add(new ViewField("expectedEndTime", "Valid To"));
		columns.add(new ViewField("moduleState", "Status"));
		
		return columns;
	}

	public static List<ViewField> getDefaultWatchListColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("phone", "PHONE"));
		columns.add(new ViewField("email", "Email"));
		columns.add(new ViewField("isBlocked", "Blocked Entry"));
		columns.add(new ViewField("isVip", "VIP"));
		return columns;
	}

	public static List<ViewField> getDefaultContactColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("phone", "PHONE"));
		columns.add(new ViewField("EMAIL", "Email"));
		columns.add(new ViewField("contactType", "Contact Type"));
		//columns.add(new ViewField("isPortalAccessNeeded", "Portal Access"));
		columns.add(new ViewField("isPrimaryContact", "Primary Contact"));
		return columns;
	}
	
	public static List<ViewField> getTenantContactColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("phone", "PHONE"));
		columns.add(new ViewField("EMAIL", "Email"));
		columns.add(new ViewField("tenant", "Tenant"));
		//columns.add(new ViewField("isPortalAccessNeeded", "Portal Access"));
		columns.add(new ViewField("isPrimaryContact", "Primary Contact"));
		return columns;
	}
	
	public static List<ViewField> getTenantContactv2Columns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("phone", "Phone"));
		columns.add(new ViewField("email", "Email"));
		columns.add(new ViewField("tenant", "Tenant"));
		//columns.add(new ViewField("isPortalAccessNeeded", "Portal Access"));
		columns.add(new ViewField("isPrimaryContact", "Primary Contact"));
		return columns;
	}
	
	public static List<ViewField> getHiddenTenantContactv2Columns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("phone", "Phone"));
		columns.add(new ViewField("email", "Email"));
		columns.add(new ViewField("isTenantPortalAccess", "Tenant Portal Access"));
		columns.add(new ViewField("isOccupantPortalAccess", "Occupant Portal Access"));
		
		return columns;
	}

	public static List<ViewField> getVendorContactv2Columns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("phone", "Phone"));
		columns.add(new ViewField("email", "Email"));
		columns.add(new ViewField("vendor", "Vendor"));
		//columns.add(new ViewField("isPortalAccessNeeded", "Portal Access"));
		columns.add(new ViewField("isPrimaryContact", "Primary Contact"));
		return columns;
	}

	public static List<ViewField> getHiddenVendorContactv2Columns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("phone", "Phone"));
		columns.add(new ViewField("email", "Email"));
		columns.add(new ViewField("isVendorPortalAccess", "Vendor Portal Access"));

		return columns;
	}

	public static List<ViewField> getClientContactColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("phone", "Phone"));
		columns.add(new ViewField("email", "Email"));
		columns.add(new ViewField("client", "Client"));
		//columns.add(new ViewField("isClientPortalAccess", "Client Portal Access"));
		return columns;
	}

	public static List<ViewField> getHiddenClientContactColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("phone", "Phone"));
		columns.add(new ViewField("email", "Email"));
		//columns.add(new ViewField("isClientPortalAccess", "Client Portal Access"));

		return columns;
	}

	public static List<ViewField> getEmployeeColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("phone", "Phone"));
		columns.add(new ViewField("email", "Email"));
		columns.add(new ViewField("isOccupantPortalAccess", "Occupant Portal Access"));
		columns.add(new ViewField("isAppAccess", "Application Access"));
		columns.add(new ViewField("isLabour", "Is Labour"));
		columns.add(new ViewField("isAssignable", "Is Assignable"));

		return columns;
	}
	
	public static List<ViewField> getVendorContactColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("phone", "PHONE"));
		columns.add(new ViewField("EMAIL", "Email"));
		columns.add(new ViewField("vendor", "Vendor"));
		//columns.add(new ViewField("isPortalAccessNeeded", "Portal Access"));
		columns.add(new ViewField("isPrimaryContact", "Primary Contact"));
		return columns;
	}
	
	public static List<ViewField> getEmployeeContactColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("phone", "PHONE"));
		columns.add(new ViewField("EMAIL", "Email"));
	//	columns.add(new ViewField("isPortalAccessNeeded", "Portal Access"));
		columns.add(new ViewField("isPrimaryContact", "Primary Contact"));
		return columns;
	}
	
	
	public static List<ViewField> getDefaultServiceRequestColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("subject", "Subject"));
		columns.add(new ViewField("assignedTo", "Team / Staff"));
		columns.add(new ViewField("urgency", "Urgency"));
		columns.add(new ViewField("moduleState", "Status"));
		columns.add(new ViewField("classification", "Classification"));
		return columns;
	}

	public static List<ViewField> getAllTasksColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();

		columns.add(new ViewField("subject", "Subject"));
		columns.add(new ViewField("resource", "Space / Asset"));
		columns.add(new ViewField("inputValue", "Input Value"));
		columns.add(new ViewField("inputTime", "Input Time"));
		columns.add(new ViewField("inputType", "Input Type"));
		columns.add(new ViewField("statusNew", "Status"));
		columns.add(new ViewField("createdTime", "Created Time"));
		columns.add(new ViewField("sectionId", "Section"));
		columns.add(new ViewField("sequence", "Sequence"));
		columns.add(new ViewField("sequence", "Sequence"));
		columns.add(new ViewField("uniqueId", "Unique Id"));

		return columns;
	}

	public static List<ViewField> getDefaultOccupantColumns () {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("phone", "Phone"));
		columns.add(new ViewField("email", "Email"));
		columns.add(new ViewField("occupantType", "Occupant Type"));
		columns.add(new ViewField("tenant", "Tenant"));
		columns.add(new ViewField("isPortalAccessNeeded", "Is Portal Access Needed"));
		columns.add(new ViewField("locatedSpace", "Located Space"));
		columns.add(new ViewField("requester", "Requester"));
		return columns;
	}

	public static  List<ViewField> getSafetyPlanHazardColumns() {
		List<ViewField> columns = new ArrayList<>();
		columns.add(new ViewField("hazard", "Hazard"));
		columns.add(new ViewField("description", "Description", "hazard"));
		columns.add(new ViewField("type", "Type", "hazard"));
		return columns;
	}

	public static  List<ViewField> getAssociatedHazardPrecautionsColumns() {
		List<ViewField> columns = new ArrayList<>();
		columns.add(new ViewField("hazard", "Hazard"));
		columns.add(new ViewField("description", "Description", "hazard"));
		columns.add(new ViewField("type", "Type", "hazard"));
		return columns;
	}

	public static  List<ViewField> getHazardPrecautionsColumns() {
		List<ViewField> columns = new ArrayList<>();
		columns.add(new ViewField("precaution", "Precaution"));
		columns.add(new ViewField("description", "Description", "precaution"));
		return columns;
	}

	public static  List<ViewField> getAllHazardPrecautionsColumns() {
		List<ViewField> columns = new ArrayList<>();
		columns.add(new ViewField("precaution", "Precaution"));
		columns.add(new ViewField("description", "Description", "precaution"));
		columns.add(new ViewField("hazard", "Hazard"));
		return columns;
	}
	private static List<ViewField> getDefaultClientColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("description", "Description"));
		columns.add(new ViewField("primaryContactName", "Primary Contact Name"));
		columns.add(new ViewField("primaryContactEmail", "Primary Contact Email"));
		
		return columns;
	}

	private static List<ViewField> getDefaultBuildingViewColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();

		columns.add(new ViewField("name", "Building Name"));
		columns.add(new ViewField("site", "Site"));
		columns.add(new ViewField("managedBy", "Mananged By"));
		columns.add(new ViewField("area", "Total Area"));

		return columns;
	}

	private static List<ViewField> getDefaultSpaceViewColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();

		columns.add(new ViewField("name", "Space Name"));
		columns.add(new ViewField("spaceCategory", "Category"));
		columns.add(new ViewField("area", "Total Area"));

		return columns;
	}

	private static List<ViewField> getDefaultFloorViewColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();

		columns.add(new ViewField("name", "Floor Name"));
		columns.add(new ViewField("maxOccupancy", "Max Occupancy"));
		columns.add(new ViewField("area", "Area"));

		return columns;
	}

	private static List<ViewField> getDefaultTenantUnitSpaceColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();

		columns.add(new ViewField("name", "Units"));
		columns.add(new ViewField("building", "Building"));
		columns.add(new ViewField("site", "Site"));
		columns.add(new ViewField("tenant", "Tenant"));
		columns.add(new ViewField("area", "Total Area"));
		columns.add(new ViewField("isOccupied", "Occupancy Status"));


		return columns;
	}
	
	private static List<ViewField> getDefaultPeopleColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();

		columns.add(new ViewField("name", "Name"));
		columns.add(new ViewField("email", "Email"));
		columns.add(new ViewField("phone", "Phone"));
		columns.add(new ViewField("peopleType", "People Type"));
	

		return columns;
	}
	
	private static List<ViewField> getDefaultTenantSpacesColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		columns.add(new ViewField("tenant", "Tenant"));
		columns.add(new ViewField("primaryContactName", "Primary Contact Name", "tenant"));
		columns.add(new ViewField("primaryContactPhone", "Primary Contact Phone", "tenant"));
		columns.add(new ViewField("inTime", "Lease From", "tenant"));
		columns.add(new ViewField("outTime", "Lease Till", "tenant"));
		

		return columns;
	}

	private static List<ViewField> getDefaultQuotationColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();

		//primary
		columns.add(new ViewField("subject", "Subject"));
		columns.add(new ViewField("description", "Description"));
		columns.add(new ViewField("billDate", "Bill Date"));
		columns.add(new ViewField("expiryDate", "Expiry Date"));

		//cost
		columns.add(new ViewField("totalCost", "Total Cost"));
		columns.add(new ViewField("subTotal", "Sub Total"));
		columns.add(new ViewField("totalTaxAmount", "Total Tax Amount"));
		columns.add(new ViewField("discountAmount", "Discount Amount"));
		columns.add(new ViewField("shippingCharges", "Shipping Charges"));
		columns.add(new ViewField("adjustmentsCost", "Adjustments Cost"));
		columns.add(new ViewField("miscellaneousCharges", "Miscellaneous Charges"));

		columns.add(new ViewField("tenant", "Tenant"));
		columns.add(new ViewField("workorder", "Workorder"));
		columns.add(new ViewField("moduleState", "Status"));


		return columns;
	}

	private static List<ViewField> getDefaultQuotationTermsColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();

		columns.add(new ViewField("name", "Name", "terms"));
		columns.add(new ViewField("shortDesc", "Short Description", "terms"));
		columns.add(new ViewField("longDesc", "Long Description", "terms"));

		return columns;
	}
}
