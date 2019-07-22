package com.facilio.bmsconsole.util;

import java.util.*;

public class DemoRollUpUtil {

	public static final Map<String, List<String>> TABLES_WITH_COLUMN = Collections.unmodifiableMap(dateFieldModified());
	private static Map<String,List<String>> dateFieldModified()  {

		Map<String, List<String>> tablesContainsDateField = new HashMap<String, List<String>>();

		tablesContainsDateField.put("Organizations" , Arrays.asList("CREATED_TIME"));
//		tablesContainsDateField.put("UserSessions" , Arrays.asList("START_TIME"));
//		tablesContainsDateField.put("User_Mobile_Setting" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("FacilioFile" ,Arrays.asList("UPLOADED_TIME"));
		tablesContainsDateField.put("Role" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("ORG_Users" , Arrays.asList("INVITEDTIME"));
		tablesContainsDateField.put("Site" , Arrays.asList("DATE_CF1","DATE_CF2","DATE_CF3","DATE_CF4","DATE_CF5","DATETIME_CF1","DATETIME_CF2","DATETIME_CF3","DATETIME_CF4","DATETIME_CF5"));
		tablesContainsDateField.put("System_Jobs" , Arrays.asList("LAST_EXECUTION_TIME"));
		tablesContainsDateField.put("Groups" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Shift_Readings" , Arrays.asList("ACTUAL_TTIME"));//day,month
		tablesContainsDateField.put("SingleDayBusinessHours" , Arrays.asList("START_TIME","END_TIME"));
		tablesContainsDateField.put("ResizedFile" , Arrays.asList("GENERATED_TIME","EXPIRY_TIME"));
		tablesContainsDateField.put("Workflow_Log" , Arrays.asList("EXECUTION_TIME"));
		tablesContainsDateField.put("Current_Occupancy_Reading" , Arrays.asList("ACTUAL_TTIME"));//day
		tablesContainsDateField.put("Assigned_Occupancy_Reading" , Arrays.asList("ACTUAL_TTIME"));
		tablesContainsDateField.put("Temperature_Reading" , Arrays.asList("ACTUAL_TTIME"));
		tablesContainsDateField.put("Humidity_Reading" , Arrays.asList("ACTUAL_TTIME"));
		tablesContainsDateField.put("CO2_Reading" , Arrays.asList("ACTUAL_TTIME"));
		tablesContainsDateField.put("Set_Point_Reading" , Arrays.asList("ACTUAL_TTIME"));
		tablesContainsDateField.put("Weather_Reading" , Arrays.asList("ACTUAL_TTIME"));
		tablesContainsDateField.put("Weather_Reading_Daily" , Arrays.asList("ACTUAL_TTIME"));
		tablesContainsDateField.put("Weather_Reading_Hourly_Forecast" , Arrays.asList("ACTUAL_TTIME"));
		tablesContainsDateField.put("Weather_Reading_Daily_Forecast" , Arrays.asList("ACTUAL_TTIME"));
		tablesContainsDateField.put("Psychrometric_Reading" , Arrays.asList("ACTUAL_TTIME"));
		tablesContainsDateField.put("CDD_Reading" , Arrays.asList("ACTUAL_TTIME"));
		tablesContainsDateField.put("HDD_Reading" , Arrays.asList("ACTUAL_TTIME"));
		tablesContainsDateField.put("WDD_Reading" , Arrays.asList("ACTUAL_TTIME"));
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
		tablesContainsDateField.put("Chiller_Readings" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("Chiller_Condenser_Readings" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("Chiller_Primary_Pump_Readings" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("Chiller_Secondary_Pump_Readings" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("Chiller_Condenser_Pump_Readings" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("AHU_Readings" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("Cooling_Tower_Readings" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("FCU_Readings" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("Heat_Pump_Readings" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("Utility_Bill_Readings" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("Water_Readings" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("Asset_Photos" , Arrays.asList("TTIME"));
		tablesContainsDateField.put("Asset_Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Asset_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Tenants" , Arrays.asList("CREATED_TIME","MODIFIED_TIME","IN_TIME","OUT_TIME"));//
		tablesContainsDateField.put("Tickets" , Arrays.asList("CREATED_BY","DUE_DATE"));
		tablesContainsDateField.put("Ticket_Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Ticket_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("WorkOrderRequests" , Arrays.asList("DATETIME_CF1","DATETIME_CF2","DATETIME_CF3","DATETIME_CF4"));
		tablesContainsDateField.put("Tasks" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Task_Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Readings" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("Readings_2" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("Predicted_Readings" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("CustomModuleData" , Arrays.asList("DATETIME_CF1","DATETIME_CF2","DATETIME_CF3","DATETIME_CF4"));
		tablesContainsDateField.put("Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Energy_Data" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("ImportProcessLog" , Arrays.asList("TTIME"));
		tablesContainsDateField.put("Formula_Field_Resource_Jobs" , Arrays.asList("START_TIME","END_TIME"));
		tablesContainsDateField.put("Workflow_Rule" , Arrays.asList("JOB_TIME","CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("Preventive_Maintenance" , Arrays.asList("CREATION_TIME","LAST_MODIFIED_TIME","END_TIME"));
		tablesContainsDateField.put("Approval_Steps" , Arrays.asList("ACTION_TIME"));
		tablesContainsDateField.put("PM_Triggers" , Arrays.asList("START_TIME"));
		tablesContainsDateField.put("WorkOrders" , Arrays.asList("CREATED_TIME","MODIFIED_TIME"));
		tablesContainsDateField.put("WorkOrderTimeLog" , Arrays.asList("START_TIME","END_TIME"));
		tablesContainsDateField.put("Alarms" , Arrays.asList("CREATED_TIME","MODIFIED_TIME","CLEARED_TIME","DATETIME_CF1","DATETIME_CF2","DATETIME_CF3","DATETIME_CF4"));
		tablesContainsDateField.put("User_Workhour_Readings" , Arrays.asList("ACTUAL_TTIME","TTIME"));
		tablesContainsDateField.put("PM_Readings" , Arrays.asList("ACTUAL_TTIME"));
		tablesContainsDateField.put("Ticket_Activity" , Arrays.asList("MODIFIED_TIME"));
		tablesContainsDateField.put("Notification" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Report_DateFilter" , Arrays.asList("START_TIME","END_TIME"));
		tablesContainsDateField.put("BaseLines" , Arrays.asList("START_TIME","END_TIME"));
		tablesContainsDateField.put("Report1_Folder" , Arrays.asList("MODIFIED_TIME"));
		tablesContainsDateField.put("Report_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Dashboard_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Unmodeled_Instance" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Points" , Arrays.asList("CREATED_TIME","MAPPED_TIME"));
		tablesContainsDateField.put("Unmodeled_Data" , Arrays.asList("TTIME"));
		tablesContainsDateField.put("Historical_VM_Calculation" , Arrays.asList("START_TIME","END_TIME"));
		tablesContainsDateField.put("Instance_To_Asset_Mapping" , Arrays.asList("MAPPED_TIME"));
//		tablesContainsDateField.put("License" , Arrays.asList("EXPIRY_DATE"));
		tablesContainsDateField.put("Reading_Alarms" , Arrays.asList("START_TIME","END_TIME"));
		tablesContainsDateField.put("ML_Alarm_Occurrences" , Arrays.asList("TTIME"));
		tablesContainsDateField.put("Reading_Rule_Flaps" , Arrays.asList("FLAP_TIME"));
		tablesContainsDateField.put("Scheduled_Actions" , Arrays.asList("JOB_TIME"));
		tablesContainsDateField.put("Task_Section_Template_Triggers" , Arrays.asList("EXECUTE_IF_NOT_IN_TIME"));
		tablesContainsDateField.put("PM_Jobs" , Arrays.asList("NEXT_EXECUTION_TIME"));
		tablesContainsDateField.put("Reading_Data_Meta" , Arrays.asList("TTIME"));
		tablesContainsDateField.put("Marked_Reading" , Arrays.asList("TTIME"));
		tablesContainsDateField.put("Tenant_Attachments" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Tenant_Notes" , Arrays.asList("CREATED_TIME"));
		tablesContainsDateField.put("Cost_Assets" , Arrays.asList("FIRST_BILL_TIME"));
		tablesContainsDateField.put("Cost_Readings" , Arrays.asList("ACTUAL_TTIME"));
//		tablesContainsDateField.put("server_info" , Arrays.asList("pingtime"));
		tablesContainsDateField.put("Sync_Errors" , Arrays.asList("CREATED_TIME","LAST_SYNC_TIME"));
		tablesContainsDateField.put("DeviceDetails" , Arrays.asList("LAST_UPDATED_TIME","LAST_ALERTED_TIME"));
//		tablesContainsDateField.put("ClientApp" , Arrays.asList("updatedTime"));
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
		tablesContainsDateField.put("Notification_Logger" , Arrays.asList("CREATED_TIME"));
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
		tablesContainsDateField.put("StateFlowTransition" , Arrays.asList("SCHEDULE_TIME"));
		tablesContainsDateField.put("ConnectedApps" , Arrays.asList("SYS_CREATED_TIME","SYS_MODIFIED_TIME"));
		tablesContainsDateField.put("Connection" , Arrays.asList("EXPIRY_TIME"));
		tablesContainsDateField.put("ML_Readings" , Arrays.asList("ACTUAL_TTIME"));
		tablesContainsDateField.put("ML_Log_Readings" , Arrays.asList("ACTUAL_TTIME"));

		return tablesContainsDateField;
		
	}

	
}
