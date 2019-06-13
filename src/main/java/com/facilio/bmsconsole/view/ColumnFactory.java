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
		columnMap.put("workorder-default", getDefaultViewColumns());
		columnMap.put("alarm-default", getDefaultAlarmColumns());
		columnMap.put("energy-default", getDefaultEnergyColumns());
		columnMap.put("asset-default", getDefaultAssetsColumns());
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
	
	private static List<ViewField> getDefaultViewColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
		columns.add(new ViewField("subject", "Subject"));
		columns.add(new ViewField("category", "Category"));
		columns.add(new ViewField("resource", "Space / Asset"));
		columns.add(new ViewField("assignedTo", "Assigned To"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("priority", "Priority"));
		columns.add(new ViewField("noOfNotes", "Comments"));
		columns.add(new ViewField("noOfTasks", "Tasks"));
		columns.add(new ViewField("modifiedTime", "Last Updated Time"));
		columns.add(new ViewField("actualWorkEnd", "Closed Time"));
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
		columns.add(new ViewField("contact", "Contact"));
		columns.add(new ViewField("inTime", "Lease Start Time"));
		columns.add(new ViewField("outTime", "Lease End Time"));
				
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
		columns.add(new ViewField("email", "E-Mail"));
		columns.add(new ViewField("phone", "Phone"));
		
		return columns;
	}
	
	private static List<ViewField> getDefaultItemColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
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
//		columns.add(new ViewField("name", "Purchase Request Name"));
		columns.add(new ViewField("description", "Description"));
//		columns.add(new ViewField("vendor", "Vendor"));
		columns.add(new ViewField("requestedTime", "Requested Date"));
		columns.add(new ViewField("requiredTime", "Required Date"));
		columns.add(new ViewField("status", "Status"));
		columns.add(new ViewField("totalCost", "Total Cost"));
				
		return columns;
	}
	
	private static List<ViewField> getDefaultPurchaseOrderColumns() {
		List<ViewField> columns = new ArrayList<ViewField>();
		
//		columns.add(new ViewField("localId", "Purchase Order Id"));
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
}
