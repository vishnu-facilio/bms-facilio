package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class SetTableNamesCommand extends FacilioCommand {

	private String moduleName, tableName;
	
	public SetTableNamesCommand() {}
	
	public SetTableNamesCommand(String moduleName, String tableName) {
		// TODO Auto-generated constructor stub
		this.moduleName = moduleName;
		this.tableName = tableName;
	}
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		if (moduleName == null && context.containsKey(FacilioConstants.ContextNames.MODULE_NAME)) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			setForModule(context, moduleName);
		}
		else {
			context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
			context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, tableName);
		}
		
		return false;
	}
	
	public static SetTableNamesCommand getForTicketStatus() {
		return new SetTableNamesCommand("ticketstatus", "TicketStatus");
	}
	
	public static SetTableNamesCommand getForServiceRequests() {
		return new SetTableNamesCommand("serviceRequest", "Service_Requests");
	}
	
	public static SetTableNamesCommand getForTicketPriority() {
		return new SetTableNamesCommand("ticketpriority", "TicketPriority");
	}
	
	public static SetTableNamesCommand getForTicketCategory() {
		return new SetTableNamesCommand("ticketcategory", "TicketCategory");
	}
	
	public static SetTableNamesCommand getForTicketType() {
		return new SetTableNamesCommand("tickettype", "TicketType");
	}
	
	public static SetTableNamesCommand getForAssetCategory() {
		return new SetTableNamesCommand("assetcategory", "AssetCategory");
	}
	
	public static SetTableNamesCommand getForAssetDepartment() {
		return new SetTableNamesCommand("assetdepartment", "AssetDepartment");
	}
	
	public static SetTableNamesCommand getForTenants() {
		return new SetTableNamesCommand("tenant", "Tenants");
	}
	
	public static SetTableNamesCommand getForAssetType() {
		return new SetTableNamesCommand("assettype", "AssetType");
	}
	
	public static SetTableNamesCommand getForAlarmSeverity() {
		return new SetTableNamesCommand("alarmseverity", "AlarmSeverity");
	}
	
	public static SetTableNamesCommand getForTicket() {
		return new SetTableNamesCommand("ticket", "Tickets");
	}
	
	public static SetTableNamesCommand getForWorkOrder() {
		return new SetTableNamesCommand("workorder", "WorkOrders");
	}
	
	public static SetTableNamesCommand getForWorkOrderRequest() {
		return new SetTableNamesCommand("workorderrequest", "WorkOrderRequests");
	}
	
	public static SetTableNamesCommand getForAlarm() {
		return new SetTableNamesCommand("alarm", "Alarms");
	}

	public static SetTableNamesCommand getForAlarmOccurrence() {
		return new SetTableNamesCommand("alarmoccurrence", "AlarmOccurrence");
	}
	
	public static SetTableNamesCommand getForReadingAlarm() {
		return new SetTableNamesCommand(FacilioConstants.ContextNames.READING_ALARM, "Reading_Alarms");
	}
	
	public static SetTableNamesCommand getForTask() {
		return new SetTableNamesCommand("task", "Tasks");
	}
	
	public static SetTableNamesCommand getForTaskStatus() {
		return new SetTableNamesCommand("taskstatus", "TaskStatus");
	}
	
	public static SetTableNamesCommand getForSite() {
		return new SetTableNamesCommand("site", "Site");
	}
	
	public static SetTableNamesCommand getForBuilding() {
		return new SetTableNamesCommand("building", "Building");
	}
	
	public static SetTableNamesCommand getForFloor() {
		return new SetTableNamesCommand("floor", "Floor");
	}
	
	public static SetTableNamesCommand getForSpace() {
		return new SetTableNamesCommand("space", "Space");
	}
	
	public static SetTableNamesCommand getForZone() {
		return new SetTableNamesCommand("zone", "Zone");
	}

	public static SetTableNamesCommand getForLocation(){
		return new SetTableNamesCommand("location","Locations");
	}
	
	public static SetTableNamesCommand getForSkill(){
		return new SetTableNamesCommand("skill","Skills");
	}
	
	public static SetTableNamesCommand getForAsset(){
		return new SetTableNamesCommand("asset","Assets");
	}
	
	public static SetTableNamesCommand getForEnergyMeter(){
		return new SetTableNamesCommand("energymeter","Energy_Meter");
	}
	public static SetTableNamesCommand getForEnergyMeterPurpose(){
		return new SetTableNamesCommand("energymeterpurpose","Energy_Meter_Purpose");
	}
	public static SetTableNamesCommand getForController(){
		return new SetTableNamesCommand("controller","Controller");
	}
	public static SetTableNamesCommand getForSpaceCategory(){
		return new SetTableNamesCommand("spacecategory","Space_Category");
	}
	public static SetTableNamesCommand getForTicketAttachment(){
		return new SetTableNamesCommand("ticketattachments","Ticket_Attachments");
	}
	
	public static SetTableNamesCommand getForInventory(){
		return new SetTableNamesCommand("inventory", "Inventory");
	}
	
	public static SetTableNamesCommand getForInventoryVendor(){
		return new SetTableNamesCommand("inventory_vendors", "Inventory_vendors");
	}
	
	public static SetTableNamesCommand getForInventoryCategory() {
		return new SetTableNamesCommand("inventoryCategory", "Inventory_category");
	}
	
	public static SetTableNamesCommand getForWorkOrderParts() {
		return new SetTableNamesCommand("workorderParts", "Workorder_Parts");
	}
	
	public static SetTableNamesCommand getForWorkOrderCosts() {
		return new SetTableNamesCommand("workorderCost", "Workorder_cost");
	}
	
	public static SetTableNamesCommand getForStoreRoom() {
		return new SetTableNamesCommand("storeRoom", "Store_room");
	}
	
	public static SetTableNamesCommand getForItemTypesCategory() {
		return new SetTableNamesCommand("itemTypesCategory", "Item_Types_category");
	}
	
	public static SetTableNamesCommand getForItemTypeStatus() {
		return new SetTableNamesCommand("itemTypesStatus", "Item_Types_status");
	}
	
	public static SetTableNamesCommand getForItemTypes() {
		return new SetTableNamesCommand("itemTypes", "Item_Types");
	}
		
	public static SetTableNamesCommand getForToolTypesCategory() {
		return new SetTableNamesCommand("toolTypesCategory", "Tool_types_category");
	}
	
	public static SetTableNamesCommand getForToolTypesStatus() {
		return new SetTableNamesCommand("toolTypesStatus", "Tool_types_status");
	}
	
	public static SetTableNamesCommand getForToolStatus() {
		return new SetTableNamesCommand("toolStatus", "Tool_status");
	}
	
	public static SetTableNamesCommand getForToolTypes() {
		return new SetTableNamesCommand("toolTypes", "Tool_types");
	}
	
	public static SetTableNamesCommand getForVendors() {
		return new SetTableNamesCommand("vendors", "Vendors");
	}
	
	public static SetTableNamesCommand getForItemStatus() {
		return new SetTableNamesCommand("itemStatus", "Item_status");
	}
	
	public static SetTableNamesCommand getForItem() {
		return new SetTableNamesCommand("item", "Item");
	}
	
	public static SetTableNamesCommand getForPurchasedItem() {
		return new SetTableNamesCommand("purchasedItem", "Purchased_Item");
	}
	
	public static SetTableNamesCommand getForWorkorderItems() {
		return new SetTableNamesCommand("workorderItem", "Workorder_items");
	}
	
	public static SetTableNamesCommand getForItemTransactions() {
		return new SetTableNamesCommand("itemTransactions", "Item_Transactions");
	}
	
	public static SetTableNamesCommand getForTool() {
		return new SetTableNamesCommand("tool", "Tool");
	}
	
	public static SetTableNamesCommand getForToolTranaction() {
		return new SetTableNamesCommand("toolTransactions", "Tool_transactions");
	}
	
	public static SetTableNamesCommand getForWorkorderTools() {
		return new SetTableNamesCommand("workorderTools", "Workorder_tools");
	}
	
	public static SetTableNamesCommand getForWorkorderLabour() {
		return new SetTableNamesCommand("workorderLabour", "Workorder_labour");
	}
	
	public static SetTableNamesCommand getForItemTypesVendors() {
		return new SetTableNamesCommand("itemVendors", "Item_vendors");
	}
	
	public static SetTableNamesCommand getForPurchasedTool() {
		return new SetTableNamesCommand("purchasedTool", "Purchased_Tool");
	}
	
	public static SetTableNamesCommand getForLabour() {
		return new SetTableNamesCommand("labour", "Labour");
	}
	
	public static SetTableNamesCommand getForPurchaseRequest() {
		return new SetTableNamesCommand("purchaserequest", "Purchase_Requests");
	}

	public static SetTableNamesCommand getForPurchaseRequestLineItem() {
		return new SetTableNamesCommand("purchaserequestlineitems", "PurchaseRequestLineItems");
	}

	public static SetTableNamesCommand getForPurchaseOrder() {
		return new SetTableNamesCommand("purchaseorder", "Purchase_Orders");
	}

	public static SetTableNamesCommand getForPurchaseOrderLineItem() {
		return new SetTableNamesCommand("purchaseorderlineitems", "PurchaseOrderLineItems");
	}

	public static SetTableNamesCommand getForReceipt() {
		return new SetTableNamesCommand("receipts", "Receipts");
	}

	public static SetTableNamesCommand getForReceivables() {
		return new SetTableNamesCommand("receivable", "Receivables");
	}
	public static SetTableNamesCommand getForContract() {
		return new SetTableNamesCommand("contracts", "Contracts");
	}
	
	public static SetTableNamesCommand getForPurchaseContract() {
		return new SetTableNamesCommand("purchasecontracts", "Purchase_Contracts");
	}
	
	public static SetTableNamesCommand getForPurchaseContractLineItem() {
		return new SetTableNamesCommand("purchasecontractlineitems", "PurchaseContractLineItems");
	}

	public static SetTableNamesCommand getForLabourContract() {
		return new SetTableNamesCommand("labourcontracts", "Labour_Contracts");
	}
	
	public static SetTableNamesCommand getForLabourContractLineItem() {
		return new SetTableNamesCommand("labourcontractlineitems", "LabourContractLineItems");
	}
	
	public static SetTableNamesCommand getForPoLineItemSerialNumber() {
		return new SetTableNamesCommand("poLineItemSerialNumbers", "PO_Line_Item_Serial_Numbers");
	}
	
	public static SetTableNamesCommand getForGatePass() {
		return new SetTableNamesCommand("gatePass", "Gate_Pass");
	}
	
	public static SetTableNamesCommand getForGatePassLineItems() {
		return new SetTableNamesCommand("gatePassLineItems", "Gate_Pass_Line_Items");
	}
	
	public static SetTableNamesCommand getForConnectedApps() {
		return new SetTableNamesCommand("connectedApps", "ConnectedApps");
	}
	
	public static SetTableNamesCommand getForInventoryRequest() {
		return new SetTableNamesCommand("inventoryrequest", "Inventory_Requests");
	}

	public static SetTableNamesCommand getForInventoryRequestLineItem() {
		return new SetTableNamesCommand("inventoryrequestlineitems", "InventoryRequestLineItems");
	}

	public static SetTableNamesCommand getForAttendance() {
		return new SetTableNamesCommand("attendance", "Attendance");
	}

	public static SetTableNamesCommand getForAttendanceTransaction() {
		return new SetTableNamesCommand("attendanceTransaction", "Attendance_Transactions");
	}

	public static SetTableNamesCommand getForBreak() {
		return new SetTableNamesCommand("break", "Break");
	}

	public static SetTableNamesCommand getForShift() {
		return new SetTableNamesCommand("shift", "Shift");
	}


	public static SetTableNamesCommand getForBreakTransaction() {
		return new SetTableNamesCommand("breakTransaction", "Break_Transaction");
	}

	public static SetTableNamesCommand getForServices() {
		return new SetTableNamesCommand("service", "Service");
	}
	
	public static SetTableNamesCommand getForWarrantyContract() {
		return new SetTableNamesCommand("warrantycontracts", "Warranty_Contracts");
	}
	
	public static SetTableNamesCommand getForWarrantyContractLineItems() {
		return new SetTableNamesCommand("warrantycontractlineitems", "WarrantyContractLineItems");
	}
	
	public static SetTableNamesCommand getForWorkorderService() {
		return new SetTableNamesCommand("workorderService", "Workorder_service");
	}

	public static SetTableNamesCommand getForRentalLeaseContract() {
		return new SetTableNamesCommand("rentalleasecontracts", "Rental_Lease_Contracts");
	}

	public static SetTableNamesCommand getForRentalLeaseContractLineItem() {
		return new SetTableNamesCommand("rentalleasecontractlineitems", "RentalLeaseContractLineItems");
	}

	public static SetTableNamesCommand getForToolTypeVendor() {
		return new SetTableNamesCommand("toolVendors", "Tool_vendors");
	}

	public static SetTableNamesCommand getForShipment() {
		return new SetTableNamesCommand("shipment", "Shipment");
	}

	public static SetTableNamesCommand getForShipmentLineItems() {
		return new SetTableNamesCommand("shipmentLineItem", "Shipment_line_item");
	}
	
	public static SetTableNamesCommand getForGraphics() {
		return new SetTableNamesCommand("graphics", "Graphics");
	}
	
	public static SetTableNamesCommand getForGraphicsFolder() {
		return new SetTableNamesCommand("graphicsFolder", "Graphics_Folder");
	}
	
	public static SetTableNamesCommand getForShiftRotation() {
		return new SetTableNamesCommand("shiftRotation", "Shift_Rotation");
	}

	public static SetTableNamesCommand getForTermsAndConditions() {
		return new SetTableNamesCommand("termsandconditions", "Terms_And_Conditions");
	}
	
	public static SetTableNamesCommand getForContractAssociatedAssets() {
		return new SetTableNamesCommand("contractassets", "Contracts_Associated_Assets");
	}
	
	public static SetTableNamesCommand getForAssetMovement() {
		return new SetTableNamesCommand("assetmovement", "Asset_Movement");
	}
	
	public static SetTableNamesCommand getForImportProcess() {
		return new SetTableNamesCommand("importProcess", "ImportProcess");
	}
	public static SetTableNamesCommand getForVisitor() {
		return new SetTableNamesCommand("visitor", "Visitor");
	}
	public static SetTableNamesCommand getForVisitorInvites() {
		return new SetTableNamesCommand("visitorinvite", "Visitor_Invites");
	}
	
	public static SetTableNamesCommand getForVisitorInviteRel() {
		return new SetTableNamesCommand("visitorinviterel", "Invite_Visitor_Rel");
	}
	
	public static SetTableNamesCommand getForVisitorLogging() {
		return new SetTableNamesCommand("visitorlogging", "Visitor_Logging");
	}
	public static SetTableNamesCommand getForVisitorType() {
		return new SetTableNamesCommand("visitorType", "VisitorType");
	}
	
	public static SetTableNamesCommand getForContacts() {
		return new SetTableNamesCommand("contact", "Contacts");
	}
	
	public static SetTableNamesCommand getForInsurance() {
		return new SetTableNamesCommand("insurance", "Insurance");
	}
	
	public static SetTableNamesCommand getForWatchList() {
		return new SetTableNamesCommand("watchlist", "WatchList");
	}
	
	public static SetTableNamesCommand getForWorkPermit() {
		return new SetTableNamesCommand("workpermit", "WorkPermit");
	}
	
	public static SetTableNamesCommand getForOccupants() {
		return new SetTableNamesCommand("occupant", "Occupants");
	}
	
	public static SetTableNamesCommand getForSafetyPlan() {
		return new SetTableNamesCommand("safetyPlan", "Safety_Plan");
	}
	
	public static SetTableNamesCommand getForHazard() {
		return new SetTableNamesCommand("hazard", "Hazard");
	}
	
	public static SetTableNamesCommand getForPrecaution() {
		return new SetTableNamesCommand("precaution", "Precaution");
	}
	
	public static SetTableNamesCommand getForSafetyPlanHazards() {
		return new SetTableNamesCommand("safetyPlanHazard", "SafetyPlan_Hazard");
	}
	
	public static SetTableNamesCommand getForHazardPrecaution() {
		return new SetTableNamesCommand("hazardPrecaution", "Hazard_Precautions");
	}
	
	public static SetTableNamesCommand getForWorkorderHazard() {
		return new SetTableNamesCommand("workorderHazard", "Workorder_Hazards");
	}
	
	public static SetTableNamesCommand getForAssetHazard() {
		return new SetTableNamesCommand("assetHazard", "Asset_Hazards");
	}
	
	public static SetTableNamesCommand getForClient() {
		return new SetTableNamesCommand("client", "Clients");
	}
	
	public static SetTableNamesCommand getForFloorPlan() {
		return new SetTableNamesCommand("floorPlan", "Floor_Plan");
	}
	public static SetTableNamesCommand getForPeople() {
		return new SetTableNamesCommand("people", "People");
	}
	
	public static SetTableNamesCommand getForTenantContact() {
		return new SetTableNamesCommand("tenantcontact", "Tenant_Contacts");
	}
	
	public static SetTableNamesCommand getForVendorContact() {
		return new SetTableNamesCommand("vendorcontact", "Vendor_Contacts");
	}
	
	public static SetTableNamesCommand getForEmployee() {
		return new SetTableNamesCommand("employee", "Employee");
	}
	
	public static SetTableNamesCommand getForClientContact() {
		return new SetTableNamesCommand("clientcontact", "Client_Contacts");
	}
	
	public static SetTableNamesCommand getForTenantUnitSpace() {
		return new SetTableNamesCommand("tenantunit", "Tenant_Unit_Space");
	}
	
	
	public static void setForModule (Context context, String moduleName) {
		//TODO handle all module and get from map
		String tableName = "";
		switch(moduleName) {
			case FacilioConstants.ContextNames.WORK_ORDER: 
				tableName = "WorkOrders";
				break;
			case FacilioConstants.ContextNames.ALARM:
				tableName = "Alarms";
				break;
		}
		context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, tableName);
	}
}
