package com.facilio.bmsconsole.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoRollUpUtil {

	public static final Map<String, List<String>> TABLES_WITH_COLUMN = Collections.unmodifiableMap(dateFieldModified());
	private static Map<String,List<String>> dateFieldModified()  {

		Map<String, List<String>> tablesContainsDateField = new HashMap<String, List<String>>();

		tablesContainsDateField.put("FacilioFile" ,Arrays.asList("UPLOADED_TIME"));
		tablesContainsDateField.put("Role" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Site" , Arrays.asList("DATE_CF1","DATE_CF2","DATE_CF3","DATE_CF4","DATE_CF5","DATETIME_CF1","DATETIME_CF2","DATETIME_CF3","DATETIME_CF4","DATETIME_CF5"));
		tablesContainsDateField.put("FacilioGroups" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("ResizedFile" , Arrays.asList("GENERATED_TIME","EXPIRY_TIME"));
		tablesContainsDateField.put("Workflow_Log" , Arrays.asList("EXECUTION_TIME"));
		tablesContainsDateField.put("BaseSpace_Photos" , Arrays.asList("TTIME"));
		tablesContainsDateField.put("Resources" , Arrays.asList("SYS_DELETED_TIME"));
		tablesContainsDateField.put("BaseSpace_Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("BaseSpace_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Building" , Arrays.asList("DATE_CF1","DATE_CF2","DATE_CF3","DATE_CF4","DATE_CF5","DATETIME_CF1","DATETIME_CF2","DATETIME_CF3","DATETIME_CF4","DATETIME_CF5"));
		tablesContainsDateField.put("Agent_Data" , Arrays.asList("LAST_MODIFIED_TIME","CREATED_TIME","LAST_DATA_RECEIVED_TIME","DELETED_TIME"));
		tablesContainsDateField.put("Agent_Log" , Arrays.asList("TIME"));
		tablesContainsDateField.put("Agent_Metrics" , Arrays.asList("LAST_UPDATED_TIME","CREATED_TIME"));
		tablesContainsDateField.put("Controller" , Arrays.asList("CREATED_TIME","LAST_MODIFIED_TIME","LAST_DATA_RECEIVED_TIME","DELETED_TIME"));
		tablesContainsDateField.put("Controller_Activity" , Arrays.asList("CREATED_TIME","RECORD_TIME"));
		tablesContainsDateField.put("Controller_Activity_Watcher" , Arrays.asList("CREATED_TIME","RECORD_TIME"));
		tablesContainsDateField.put("Publish_Data" , Arrays.asList("CREATED_TIME","ACKNOWLEDGE_TIME"));
		tablesContainsDateField.put("Publish_Message" , Arrays.asList("SENT_TIME","ACKNOWLEDGE_TIME"));
		tablesContainsDateField.put("Floor" , Arrays.asList("DATETIME_CF1","DATETIME_CF2","DATETIME_CF3","DATETIME_CF4","DATETIME_CF5"));
		tablesContainsDateField.put("Space" , Arrays.asList("DATETIME_CF1","DATETIME_CF2","DATETIME_CF3","DATETIME_CF4","DATETIME_CF5"));
		tablesContainsDateField.put("Store_room" , Arrays.asList("CREATED_TIME","MODIFIED_TIME","LAST_PURCHASED_DATE"));
		tablesContainsDateField.put("Item_Types_category" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Item_Type_Activity" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME"));
		tablesContainsDateField.put("Inventory_category" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Item_Types_status" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Item_Types" , Arrays.asList("CREATED_TIME","MODIFIED_TIME","LAST_PURCHASED_DATE","LAST_ISSUED_DATE"));
		tablesContainsDateField.put("Item_status" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Item" , Arrays.asList("CREATED_TIME","MODIFIED_TIME","LAST_PURCHASED_DATE"));
		tablesContainsDateField.put("Tool_types_status" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Tool_types" , Arrays.asList("CREATED_TIME","MODIFIED_TIME","LAST_PURCHASED_DATE","LAST_ISSUED_DATE"));
		tablesContainsDateField.put("Tool_status" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Tool" , Arrays.asList("CREATED_TIME","MODIFIED_TIME","LAST_PURCHASED_DATE"));
		tablesContainsDateField.put("Vendors" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Purchase_Orders" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","COMPLETED_TIME"));
		tablesContainsDateField.put("Assets" , Arrays.asList("PURCHASED_DATE","RETIRE_DATE","DATETIME_CF1","DATETIME_CF2","DATETIME_CF3","DATETIME_CF4","DATETIME_CF5"));
		tablesContainsDateField.put("Asset_Activity" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","TTIME"));
		tablesContainsDateField.put("Work_Order_Activity" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","TTIME"));
		tablesContainsDateField.put("Asset_Photos" , Arrays.asList("TTIME"));
		tablesContainsDateField.put("Asset_Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Asset_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Tenants" , Arrays.asList("CREATED_TIME","MODIFIED_TIME","IN_TIME","OUT_TIME"));
		tablesContainsDateField.put("Tickets" , Arrays.asList("DUE_DATE","SCHEDULED_START","ESTIMATED_START","ESTIMATED_END","ACTUAL_WORK_START","ACTUAL_WORK_END"));
		tablesContainsDateField.put("Ticket_Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Ticket_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("WorkOrderRequests" , Arrays.asList("DATETIME_CF1","DATETIME_CF2","DATETIME_CF3","DATETIME_CF4"));
		tablesContainsDateField.put("Tasks" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Task_Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("CustomModuleData" , Arrays.asList("DATETIME_CF1","DATETIME_CF2","DATETIME_CF3","DATETIME_CF4"));
		tablesContainsDateField.put("Custom_Activity" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","TTIME"));
		tablesContainsDateField.put("Site_Activity" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","TTIME"));
		tablesContainsDateField.put("Building_Activity" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","TTIME"));
		tablesContainsDateField.put("Floor_Activity" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","TTIME"));
		tablesContainsDateField.put("Space_Activity" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","TTIME"));
		tablesContainsDateField.put("Vendor_Activity" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","TTIME"));
		tablesContainsDateField.put("Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Formula_Field_Resource_Jobs" , Arrays.asList("START_TIME","END_TIME"));
		tablesContainsDateField.put("Workflow_Rule" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Preventive_Maintenance" , Arrays.asList("CREATION_TIME","LAST_MODIFIED_TIME","END_TIME"));
		tablesContainsDateField.put("Approval_Steps" , Arrays.asList("ACTION_TIME"));
		tablesContainsDateField.put("PM_Triggers" , Arrays.asList("START_TIME"));
		tablesContainsDateField.put("WorkOrders" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("WorkOrderTimeLog" , Arrays.asList("START_TIME","END_TIME"));
		tablesContainsDateField.put("User_Workhour_Readings" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("Ticket_Activity" , Arrays.asList("MODIFIED_TIME"));
		tablesContainsDateField.put("Notification" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Report_DateFilter" , Arrays.asList("START_TIME","END_TIME"));
		tablesContainsDateField.put("BaseLines" , Arrays.asList("START_TIME","END_TIME"));
		tablesContainsDateField.put("Report1_Folder" , Arrays.asList("MODIFIED_TIME"));
		tablesContainsDateField.put("Report_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Dashboard_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Points" , Arrays.asList("CREATED_TIME","MAPPED_TIME"));
		tablesContainsDateField.put("Unmodeled_Data" , Arrays.asList("TTIME"));
		tablesContainsDateField.put("Reading_Rule_Flaps" , Arrays.asList("FLAP_TIME"));
		tablesContainsDateField.put("Scheduled_Actions" , Arrays.asList("JOB_TIME"));
		tablesContainsDateField.put("Task_Section_Template_Triggers" , Arrays.asList("EXECUTE_IF_NOT_IN_TIME"));
		tablesContainsDateField.put("PM_Jobs" , Arrays.asList("NEXT_EXECUTION_TIME"));
		tablesContainsDateField.put("Reading_Data_Meta" , Arrays.asList("TTIME"));
		tablesContainsDateField.put("Tenant_Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Tenant_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Cost_Assets" , Arrays.asList("FIRST_BILL_TIME"));
		tablesContainsDateField.put("Cost_Readings" , Arrays.asList("ACTUAL_TTIME"));
		tablesContainsDateField.put("DeviceDetails" , Arrays.asList("LAST_UPDATED_TIME","LAST_ALERTED_TIME"));
		tablesContainsDateField.put("Workorder_cost" , Arrays.asList("TTIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Gate_Pass" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","SYS_DELETED_TIME"));
		tablesContainsDateField.put("Store_room_Notes" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Store_room_Attachments" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Store_Room_Photos" , Arrays.asList("TTIME"));
		tablesContainsDateField.put("Vendors_Notes" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Vendors_Attachments" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Item_Types_Notes" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Item_Types_Attachments" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Purchased_Item" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Item_Transactions" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Item_Activity" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Tool_types_category" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Purchased_Tool" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Tool_types_Notes" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Tool_types_Attachments" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Tool_transactions" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Workorder_tools" , Arrays.asList("ISSUE_TIME","RETURN_TIME"));
		tablesContainsDateField.put("Item_vendors" , Arrays.asList("DATE_ORDERED","CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Labour" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","SYS_DELETED_TIME"));
		tablesContainsDateField.put("Workorder_labour" , Arrays.asList("START_TIME","END_TIME"));
		tablesContainsDateField.put("Event" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Purchase_Orders" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","SYS_DELETED_TIME"));
		tablesContainsDateField.put("Purchase_Requests" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","SYS_DELETED_TIME"));
		tablesContainsDateField.put("Purchase_Request_Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Purchase_Request_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Purchase_Order_Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Purchase_Order_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Receivables" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","SYS_DELETED_TIME"));
		tablesContainsDateField.put("Receivables_Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Receivables_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Receipts" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","SYS_DELETED_TIME"));
		tablesContainsDateField.put("Contracts" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","SYS_DELETED_TIME"));
		tablesContainsDateField.put("Contracts_Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Contracts_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("PO_Line_Item_Serial_Numbers" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","SYS_DELETED_TIME"));
		tablesContainsDateField.put("Item_Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Item_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Tool_Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Tool_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Gate_Pass_Line_Items" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","SYS_DELETED_TIME"));
//		tablesContainsDateField.put("ConnectedApps" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME"));
		tablesContainsDateField.put("Connection" , Arrays.asList("ACCESS_EXPIRY_TIME","REFRESH_EXPIRY_TIME", "SYS_CREATED_TIME","SYS_MODIFIED_TIME"));
		tablesContainsDateField.put("Visitor_Logging", Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","SYS_DELETED_TIME","CHECKIN_TIME", "CHECKOUT_TIME", "LOG_GENERATED_UPTO", "EXPECTED_CHECKIN_TIME", "EXPECTED_CHECKOUT_TIME"));
		tablesContainsDateField.put("Visitor_Logging_Attachments", Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("VisitorLog_Triggers", Arrays.asList("START_TIME" , "END_TIME", "LOG_GENERATED_UPTO"));
		tablesContainsDateField.put("Visitor_Logging_Notes", Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Insurance", Arrays.asList("SYS_CREATED_TIME", "SYS_MODIFIED_TIME", "SYS_DELETED_TIME"));
		tablesContainsDateField.put("Insurance_Attachments", Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Insurance_Notes", Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("WorkPermit", Arrays.asList("SYS_CREATED_TIME", "SYS_MODIFIED_TIME", "SYS_DELETED_TIME"));
		tablesContainsDateField.put("WorkPermit_Attachments", Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("WorkPermit_Notes", Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Visitor", Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME","SYS_DELETED_TIME","LAST_VISITED_TIME"));
		tablesContainsDateField.put("MV_Project", Arrays.asList("START_TIME", "END_TIME", "REPORTING_START_TIME", "REPORTING_END_TIME"));
		tablesContainsDateField.put("MV_Baseline", Arrays.asList("START_TIME", "END_TIME"));
		tablesContainsDateField.put("MV_Adjustment", Arrays.asList("START_TIME", "END_TIME"));
			
		
		return tablesContainsDateField;
		
	}

	
}
