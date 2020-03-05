package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DbShradingScript {
	private static final String SOURCE_URL = "jdbc:mysql//localhost:3306/bmslocal";
	private static final String SOURCE_USERNAME = "root", SOURCE_PASSWORD = "";

	private static final String TARGET_URL = "jdbc:mysql//localhost:3306/bms_new_test";
	private static final String TARGET_USERNAME = "root", TARGET_PASSWORD = "";

	private static final long ORGID = 210;

	private static int LIMIT = 50000;
	private static HashMap<String, String> tableVsPrimaryField = new LinkedHashMap();

	private static Connection getConn1() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(SOURCE_URL, SOURCE_USERNAME, SOURCE_PASSWORD);
		return con;
	}

	private static Connection getConn2() throws ClassNotFoundException, SQLException {
		Class.forName("com.amazon.redshift.jdbc.Driver");
		Connection con = DriverManager.getConnection(TARGET_URL, TARGET_USERNAME, TARGET_PASSWORD);
		con.setAutoCommit(false);
		return con;
	}

	private static void init() {
		//	tableVsPrimaryField.put("Role", "ROLE_ID");
		//	tableVsPrimaryField.put("FacilioFile", "FILE_ID");
		//	tableVsPrimaryField.put("Modules", "MODULEID");
		//	tableVsPrimaryField.put("Permission", "ID");
		//	tableVsPrimaryField.put("BusinessHours", "ID");
		//	tableVsPrimaryField.put("Forms", "ID");
		//	tableVsPrimaryField.put("SingleDayBusinessHours", "ID");
		//	tableVsPrimaryField.put("Resources", "ID");
		//	tableVsPrimaryField.put("ORG_Users", "ORG_USERID");
		//	tableVsPrimaryField.put("Locations", "ID");
		//	tableVsPrimaryField.put("BaseSpace", "ID");
		//	tableVsPrimaryField.put("Site", "ID");
		//	tableVsPrimaryField.put("Org_Units", "ID");
//		tableVsPrimaryField.put("UtilityProviders", "UTILITYID");
		//	tableVsPrimaryField.put("Groups", "GROUPID");
		//	tableVsPrimaryField.put("GroupMembers", "MEMBERID");
		//	tableVsPrimaryField.put("SubModulesRel", "PARENT_MODULE_ID, CHILD_MODULE_ID");
		//	tableVsPrimaryField.put("Fields", "FIELDID");
		//	tableVsPrimaryField.put("LookupFields", "FIELDID");
		//	tableVsPrimaryField.put("NumberFields", "FIELDID");
		//	tableVsPrimaryField.put("BooleanFields", "FIELDID");
		//	tableVsPrimaryField.put("EnumFieldValues", "ID");
		//	tableVsPrimaryField.put("SystemEnumFields", "FIELDID");
		//	tableVsPrimaryField.put("FileFields", "FIELDID");
		//	tableVsPrimaryField.put("Shift_Readings", "ID");
		//	tableVsPrimaryField.put("ResizedFile", "ID");
		//	tableVsPrimaryField.put("Common_Job_Props", "JOBID, JOBNAME");
		//	tableVsPrimaryField.put("Workflow_Namespace", "ID");
		//	tableVsPrimaryField.put("Workflow", "ID");
		//	tableVsPrimaryField.put("Workflow_User_Function", "ID");
		//	tableVsPrimaryField.put("Workflow_Field", "ID");
		//	tableVsPrimaryField.put("Workflow_Log", "ID");
		//	tableVsPrimaryField.put("Scheduled_Workflow", "ID");
		//	tableVsPrimaryField.put("Accessible_Space", "ORG_USER_ID, BS_ID");
		//	tableVsPrimaryField.put("Resource_Readings", "RESOURCE_ID,READING_ID");
		//	tableVsPrimaryField.put("BaseSpace_Photos", "ID");
		//	tableVsPrimaryField.put("BaseSpace_Attachments", "ID");
		//	tableVsPrimaryField.put("BaseSpace_Notes", "ID");
		//	tableVsPrimaryField.put("Form_Section", "ID");
		//	tableVsPrimaryField.put("Form_Fields", "ID");
		//	tableVsPrimaryField.put("Building", "ID");
		//	tableVsPrimaryField.put("Agent_Data", "ID");
		//	tableVsPrimaryField.put("Agent_Log", "ID");
		//	tableVsPrimaryField.put("Agent_Metrics", "ID");
		//	tableVsPrimaryField.put("Agent_Message", "ID");
		//	tableVsPrimaryField.put("Agent_Integration", "ID");
		//	tableVsPrimaryField.put("Controller", "ID");
		//	tableVsPrimaryField.put("Controller_Activity", "ID");
		//	tableVsPrimaryField.put("Controller_Activity_Records", "ID");
		//	tableVsPrimaryField.put("Controller_Activity_Watcher", "ID");
		//	tableVsPrimaryField.put("Controller_Building_Rel", "ORGID");
		//	tableVsPrimaryField.put("Publish_Data", "ID");
		//	tableVsPrimaryField.put("Publish_Message", "ID");
		//	tableVsPrimaryField.put("Floor", "ID");
		//	tableVsPrimaryField.put("Space_Category", "ID");
		//	tableVsPrimaryField.put("Space_Category_Readings", "PARENT_CATEGORY_ID");
		//	tableVsPrimaryField.put("Space", "ID");
		//	tableVsPrimaryField.put("Zone", "ID");
		//	tableVsPrimaryField.put("Zone_Space", "ZONE_ID, BASE_SPACE_ID");
		//	tableVsPrimaryField.put("Store_room", "ID");
		//	tableVsPrimaryField.put("Item_Types_category", "ID");
		//	tableVsPrimaryField.put("Item_Type_Activity", "ID");
		//	tableVsPrimaryField.put("Inventory_category", "ID");
		//	tableVsPrimaryField.put("Item_Types_status", "ID");
		//	tableVsPrimaryField.put("Item_Types", "ID");
		//	tableVsPrimaryField.put("Item_status", "ID");
		//	tableVsPrimaryField.put("Item", "ID");
		//	tableVsPrimaryField.put("Tool_types_status", "ID");
		//	tableVsPrimaryField.put("Tool_types", "ID");
		//	tableVsPrimaryField.put("Tool_status", "ID");
		//	tableVsPrimaryField.put("Tool", "ID");
		//	tableVsPrimaryField.put("TicketStatus", "ID");
		//	tableVsPrimaryField.put("Vendors", "ID");
		//	tableVsPrimaryField.put("Contracts", "ID");
		//	tableVsPrimaryField.put("Purchase_Orders", "ID");
		//	tableVsPrimaryField.put("Asset_Types", "ID");
		//	tableVsPrimaryField.put("Asset_Categories", "ID");
		//	tableVsPrimaryField.put("Asset_Category_Readings", "PARENT_CATEGORY_ID, READING_MODULE_ID");
		//	tableVsPrimaryField.put("Asset_Departments", "ID");
		//	tableVsPrimaryField.put("Assets", "ID");
		//	tableVsPrimaryField.put("Relationship", "ID");
		//	tableVsPrimaryField.put("Related_Assets", "ID");
		//	tableVsPrimaryField.put("Asset_Activity", "ID");
		//	tableVsPrimaryField.put("Asset_Breakdown", "ID");
		//	tableVsPrimaryField.put("Asset_BD_SourceDetails", "ID");
		//	tableVsPrimaryField.put("Agent", "ID");
		//	tableVsPrimaryField.put("Field_Device", "ID");
		//	tableVsPrimaryField.put("Controllers", "ID");
		//	tableVsPrimaryField.put("Misc_Controller", "ID");
		//	tableVsPrimaryField.put("Custom_Controller", "ID");
		//	tableVsPrimaryField.put("Rest_Controller", "ID");
		//	tableVsPrimaryField.put("BACnet_IP_Controller", "ID");
		//	tableVsPrimaryField.put("Niagara_Controller", "ID");
		//	tableVsPrimaryField.put("LonWorks_Controller", "ID");
		//	tableVsPrimaryField.put("Modbus_Rtu_Controller", "ID");
		//	tableVsPrimaryField.put("Modbus_Tcp_Controller", "ID");
		//	tableVsPrimaryField.put("OpcXMLDA_Controller", "ID");
		//	tableVsPrimaryField.put("OpcUA_Controller", "ID");
		//	tableVsPrimaryField.put("Iot_Data", "ID");
		//	tableVsPrimaryField.put("Iot_Message", "ID");
		//	tableVsPrimaryField.put("AssetCustomModuleData", "ID");
		//	tableVsPrimaryField.put("Work_Order_Activity", "ID");
		//	tableVsPrimaryField.put("Energy_Meter_Purpose", "ID");
		//	tableVsPrimaryField.put("Energy_Meter", "ID");
		//	tableVsPrimaryField.put("Controller_Asset", "ID");
		//	tableVsPrimaryField.put("Virtual_Energy_Meter_Rel", "VIRTUAL_METER_ID, CHILD_METER_ID");
		//	tableVsPrimaryField.put("Hvac", "ID");
		//	tableVsPrimaryField.put("Chiller", "ID");
		//	tableVsPrimaryField.put("Chiller_Plant_Manager", "ID");
		//	tableVsPrimaryField.put("Chiller_Primary_Pump", "ID");
		//
		//	tableVsPrimaryField.put("Chiller_Secondary_Pump", "ID");
		//	tableVsPrimaryField.put("Chiller_Condenser_Pump", "ID");
		//	tableVsPrimaryField.put("AHU", "ID");
		//	tableVsPrimaryField.put("FAHU", "ID");
		//	tableVsPrimaryField.put("Cooling_Tower", "ID");
		//	tableVsPrimaryField.put("FCU", "ID");
		//	tableVsPrimaryField.put("Heat_Pump", "ID");
		//	tableVsPrimaryField.put("Utility_Meters", "ID");
		//	tableVsPrimaryField.put("Water_Meter", "ID");
		//	tableVsPrimaryField.put("Asset_Photos", "ID");
		//	tableVsPrimaryField.put("Asset_Attachments", "ID");
		//	tableVsPrimaryField.put("Asset_Notes", "ID");
		//	tableVsPrimaryField.put("TicketStatus", "ID");
		//	tableVsPrimaryField.put("TicketStateFlow", "ACTIVITY_ID");
		//	tableVsPrimaryField.put("Tenants", "ID");
		//	tableVsPrimaryField.put("Contacts", "ID");
		//	tableVsPrimaryField.put("Contacts_Attachments", "ID");
		//	tableVsPrimaryField.put("Contacts_Notes", "ID");
		//	tableVsPrimaryField.put("TicketPriority", "ID");
		//	tableVsPrimaryField.put("TicketCategory", "ID");
		//	tableVsPrimaryField.put("TicketType", "ID");
		//	tableVsPrimaryField.put("Tickets", "ID");
		//	tableVsPrimaryField.put("Ticket_Attachments", "ID");
		//	tableVsPrimaryField.put("Ticket_Notes", "ID");
		//	tableVsPrimaryField.put("WorkOrderRequests", "ID");
		// tableVsPrimaryField.put("WorkOrderRequest_EMail", "ID");
		//	tableVsPrimaryField.put("UserLocationCoverage", "USER_LOCATION_ID");
		//	tableVsPrimaryField.put("GroupLocationCoverage", "GROUP_LOCATION_ID");
		//	tableVsPrimaryField.put("Skills", "ID");
		//	tableVsPrimaryField.put("UserSkills", "USER_SKILL_ID");
		//	tableVsPrimaryField.put("GroupSkills", "GROUP_SKILL_ID");
		//	tableVsPrimaryField.put("Alarm_Severity", "ID");
		//	tableVsPrimaryField.put("Alarm_Entity", "ENTITY_ID");
		//	tableVsPrimaryField.put("CustomModuleData", "ID");
		//	tableVsPrimaryField.put("CMD_Notes", "ID");
		//	tableVsPrimaryField.put("CMD_Attachments", "ID");
		//	tableVsPrimaryField.put("Attachments", "ID");
		//	tableVsPrimaryField.put("Notes", "ID");
		//	tableVsPrimaryField.put("Criteria", "CRITERIAID");
		//	tableVsPrimaryField.put("Conditions", "CONDITIONID");
		//	tableVsPrimaryField.put("Form_Rule", "ID");
		//	tableVsPrimaryField.put("Form_Rule_Action", "ID");
		//	tableVsPrimaryField.put("Views", "ID");
		//	tableVsPrimaryField.put("View_Sort_Columns", "ID");
		//	tableVsPrimaryField.put("ImportProcess", "ID");
		//	tableVsPrimaryField.put("ImportTemplate", "ID");
//		tableVsPrimaryField.put("ImportProcessLog", "ID");
		//	tableVsPrimaryField.put("Expression", "ID");
		//	tableVsPrimaryField.put("KPI_Category", "ID");
		//	tableVsPrimaryField.put("Formula_Field", "ID");
		//	tableVsPrimaryField.put("Formula_Field_Inclusions", "ID");
		//	tableVsPrimaryField.put("Formula_Field_Resource_Jobs", "ID");
		//	tableVsPrimaryField.put("Job_Plan", "ID");
		//	tableVsPrimaryField.put("Templates", "ID");
		//	tableVsPrimaryField.put("EMail_Templates", "ID");
		//	tableVsPrimaryField.put("Workflow_Template", "ID");
		//	tableVsPrimaryField.put("Control_Groups", "ID");
		//	tableVsPrimaryField.put("Control_Action_Template", "ID");
		//	tableVsPrimaryField.put("SMS_Templates", "ID");
		//	tableVsPrimaryField.put("Whatsapp_Message_Template", "ID");
		//	tableVsPrimaryField.put("Call_Template", "ID");
		//	tableVsPrimaryField.put("Assignment_Templates", "ID");
		//	tableVsPrimaryField.put("SLA_Templates", "ID");
		//	tableVsPrimaryField.put("Push_Notification_Templates", "ID");
		// tableVsPrimaryField.put("Web_Notification_Templates", "ID");
		//	tableVsPrimaryField.put("JSON_Template", "ID");
		//	tableVsPrimaryField.put("Form_Template", "ID");
		//	tableVsPrimaryField.put("Workorder_Template", "ID");
		//	tableVsPrimaryField.put("Task_Section_Template", "ID");
		//	tableVsPrimaryField.put("Prerequisite_Approvers_Template", "ID");
		//	tableVsPrimaryField.put("Task_Template", "ID");
		//	tableVsPrimaryField.put("Action", "ID");
		//	tableVsPrimaryField.put("Workflow_Event", "ID");
		//	tableVsPrimaryField.put("Workflow_Rule", "ID");
		//	tableVsPrimaryField.put("Workflow_Field_Change_Fields", "ID");
		//	tableVsPrimaryField.put("Workflow_Rule_Action", "WORKFLOW_RULE_ACTION_ID");
		//	tableVsPrimaryField.put("Excel_Templates", "ID");
		// tableVsPrimaryField.put("Tenant", "ID");
		// tableVsPrimaryField.put("TenantZones", "TENANT_ID,ZONE_ID");
		// tableVsPrimaryField.put("TenantMeters", "TENANT_ID,METER_ID");
		//	tableVsPrimaryField.put("Preventive_Maintenance", "ID");
		//	tableVsPrimaryField.put("PM_Include_Exclude_Resource", "ID");
		//	tableVsPrimaryField.put("Approval_Rules", "ID");
		//	tableVsPrimaryField.put("Approvers", "ID");
		//	tableVsPrimaryField.put("Approver_Actions_Rel", "ID");
		//	tableVsPrimaryField.put("Approval_Steps", "ID");
		//	tableVsPrimaryField.put("PM_Triggers", "ID");
		//	tableVsPrimaryField.put("WorkOrders", "ID");
		//	tableVsPrimaryField.put("Related_Workorders", "SOURCE_WO,TARGET_WO");
		//	tableVsPrimaryField.put("Prerequisite_Photos", "ID");
		//	tableVsPrimaryField.put("Prerequisite_Approvers", "ID");
		//	tableVsPrimaryField.put("WorkOrderTimeLog", "ID");

		// tableVsPrimaryField.put("AlarmFollowers", "ID");
		//	tableVsPrimaryField.put("User_Workhour_Readings", "ID");
		//	tableVsPrimaryField.put("SupportEmails", "ID");
		//	tableVsPrimaryField.put("EmailSettings", "ID");
		//	tableVsPrimaryField.put("PortalInfo", "PORTALID");
		//	tableVsPrimaryField.put("Ticket_Activity", "ID");
		//	tableVsPrimaryField.put("Connected_App", "CONNECTED_APP_ID");
		//	tableVsPrimaryField.put("Tab_Widget", "TAB_WIDGET_ID");
		//	tableVsPrimaryField.put("Notification", "ID");
		//	tableVsPrimaryField.put("Report_Folder", "ID");
		//	tableVsPrimaryField.put("Report_Formula_Field", "ID");
		//	tableVsPrimaryField.put("Report_Field", "ID");
		//	tableVsPrimaryField.put("Report_Entity", "ID");
		//	tableVsPrimaryField.put("Report", "ID");
		//	tableVsPrimaryField.put("Report_DateFilter", "ID");
		//	tableVsPrimaryField.put("Report_EnergyMeter", "ID");
		//	tableVsPrimaryField.put("Report_Criteria", "ID");
		//	tableVsPrimaryField.put("Report_Threshold", "ID");
		//	tableVsPrimaryField.put("Report_User_Filter", "ID");
		//	tableVsPrimaryField.put("BaseLines", "ID");
		//	tableVsPrimaryField.put("Benchmark", "ID");
		//	tableVsPrimaryField.put("Report1_Folder", "ID");
		//	tableVsPrimaryField.put("Report1", "ID");
		//	tableVsPrimaryField.put("Report_Notes", "ID");
		//	tableVsPrimaryField.put("Report_Fields", "ID");
		//	tableVsPrimaryField.put("Dashboard_Folder", "ID");
		//	tableVsPrimaryField.put("Dashboard", "ID");
		//	tableVsPrimaryField.put("Dashboard_Tab", "ID");
		//	tableVsPrimaryField.put("Dashboard_Notes", "ID");
		//	tableVsPrimaryField.put("Space_Filtered_Dashboard_Settings", "ID");
		//	tableVsPrimaryField.put("Widget", "ID");
		//	tableVsPrimaryField.put("Widget_Chart", "ID");
		//	tableVsPrimaryField.put("Widget_List_View", "ID");
		//	tableVsPrimaryField.put("Widget_Static", "ID");
		//	tableVsPrimaryField.put("Widget_Vs_Workflow", "ID");
		//	tableVsPrimaryField.put("Widget_Web", "ID");
		//	tableVsPrimaryField.put("Widget_Card", "ID");
		//	tableVsPrimaryField.put("Screen_Dashboard_Rel", "ID");
		//	tableVsPrimaryField.put("Formula", "ID");
		//	tableVsPrimaryField.put("Report_SpaceFilter", "ID");
		//	tableVsPrimaryField.put("Unmodeled_Instance", "ID");
		//	tableVsPrimaryField.put("Points", "ID");
		//	tableVsPrimaryField.put("Point", "ID");
		//	tableVsPrimaryField.put("Misc_Point", "ID");
		//	tableVsPrimaryField.put("BACnet_IP_Point", "ID");
		//	tableVsPrimaryField.put("Niagara_Point", "ID");
		//	tableVsPrimaryField.put("OPC_XML_DA_Point", "ID");
		//	tableVsPrimaryField.put("Modbus_Tcp_Point", "ID");
		//	tableVsPrimaryField.put("Modbus_Rtu_Point", "ID");
		//	tableVsPrimaryField.put("OPC_UA_Point", "ID");
		//	tableVsPrimaryField.put("Historical_Logger", "ID");
		//	tableVsPrimaryField.put("Workflow_Rule_Historical_Logger", "ID");
		//	tableVsPrimaryField.put("Formula_Field_Historical_Logger", "ID");
		//	tableVsPrimaryField.put("Unmodeled_Data", "ID");
		//	tableVsPrimaryField.put("Instance_To_Asset_Mapping", "ID");
		//	tableVsPrimaryField.put("View_Column", "ID");
		//	tableVsPrimaryField.put("License", "ID");
		//	tableVsPrimaryField.put("FeatureLicense", "ORGID");
		//	tableVsPrimaryField.put("OrgInfo", "ID");
		//	tableVsPrimaryField.put("View_Schedule_Info", "ID");
		//	tableVsPrimaryField.put("Report_Schedule_Info", "ID");
		//	tableVsPrimaryField.put("Report_Schedule_Info1", "ID");
		//	tableVsPrimaryField.put("Calendar_Color", "ID");
		//	tableVsPrimaryField.put("Benchmark_Units", "ID");
		//	tableVsPrimaryField.put("Report_Benchmark_Rel", "REPORT_ID");
		//	tableVsPrimaryField.put("Reading_Rule", "ID");
		//	tableVsPrimaryField.put("Reading_Alarm_Rule", "ID");
		//	tableVsPrimaryField.put("Reading_Rule_Inclusions_Exclusions", "ID");

		//	tableVsPrimaryField.put("SLA_Rule", "ID");
		//	tableVsPrimaryField.put("Reading_Rule_Flaps", "ID");
		//	tableVsPrimaryField.put("Reading_Rule_Metrics", "ID");
		//	tableVsPrimaryField.put("Scheduled_Actions", "ID");
		//	tableVsPrimaryField.put("Task_Section_Template_Triggers", "SECTION_ID, PM_TRIGGER_ID");
		//	tableVsPrimaryField.put("PM_Jobs", "ID");
		//	tableVsPrimaryField.put("PM_Reminders", "ID");
		//	tableVsPrimaryField.put("PM_Reminder_Action", "ID");
		//	tableVsPrimaryField.put("Before_PM_Reminder_Trigger_Rel", "ID");
		//	tableVsPrimaryField.put("After_PM_Reminder_WO_Rel", "ID");
		//	tableVsPrimaryField.put("PM_Resource_Planner", "ID");
		//	tableVsPrimaryField.put("PM_Resource_Planner_Reminder", "ID");
		//	tableVsPrimaryField.put("PM_Resource_Planner_Triggers", "PM_RESOURCE_PLANNER_ID, TRIGGER_ID");
		//	tableVsPrimaryField.put("PM_Resource_Schedule_Rule_Rel", "PM_ID, RESOURCE_ID, SCHEDULE_RULE_ID");
		//	tableVsPrimaryField.put("Report_BaseLine_Rel", "BASE_LINE_ID, REPORT_ID");
		//	tableVsPrimaryField.put("Report_Columns", "ID");
		//	tableVsPrimaryField.put("Reading_Data_Meta", "ID");
		//
		//	tableVsPrimaryField.put("Reading_Input_Values", "ORGID,RDMID,IDX");
		//	tableVsPrimaryField.put("Dashboard_Sharing", "ID");
		//	tableVsPrimaryField.put("View_Sharing", "ID");
		//	tableVsPrimaryField.put("Pm_Exec_Sharing", "ID");
		//	tableVsPrimaryField.put("Report_Folder_Sharing", "ID");
		//	tableVsPrimaryField.put("Derivations", "ID");
		//	tableVsPrimaryField.put("Tenant_Users", "TENANTID,ORG_USERID");
		//	tableVsPrimaryField.put("Tenants_Utility_Mapping", "ID");
		//	tableVsPrimaryField.put("Tenant_Attachments", "ID");
		//	tableVsPrimaryField.put("Tenant_Notes", "ID");
		//	tableVsPrimaryField.put("Rate_Card", "ID");
		//	tableVsPrimaryField.put("Rate_Card_Services", "ID");
		//	tableVsPrimaryField.put("Module_Local_ID", "MODULE_NAME,ORGID");
		//	tableVsPrimaryField.put("Shift", "ID");
		//	tableVsPrimaryField.put("Shift_User_Rel", "ID");
		//	tableVsPrimaryField.put("Break", "ID");
		//	tableVsPrimaryField.put("Shift_Break_Rel", "ID");
		//	tableVsPrimaryField.put("Costs", "ID");
		//	tableVsPrimaryField.put("Cost_Slabs", "ID");
		//	tableVsPrimaryField.put("Additional_Costs", "ID");
		//	tableVsPrimaryField.put("Cost_Assets", "ID");
		// tableVsPrimaryField.put("Sync_Errors", "ORGID"); // check the primary key
		//	tableVsPrimaryField.put("DeviceDetails", "ID");
		// tableVsPrimaryField.put("ClientApp", "id");
		// tableVsPrimaryField.put("MobileDetails", "ID");
		//	tableVsPrimaryField.put("Workorder_cost", "ID");
		//	tableVsPrimaryField.put("Gate_Pass", "ID");
		//	tableVsPrimaryField.put("Gate_Pass_Notes", "ID");
		//	tableVsPrimaryField.put("Gate_Pass_Attachments", "ID");
		//	tableVsPrimaryField.put("Store_room_Notes", "ID");
		//	tableVsPrimaryField.put("Store_room_Attachments", "ID");
		//	tableVsPrimaryField.put("Store_Room_Photos", "ID");
		//	tableVsPrimaryField.put("Vendors_Notes", "ID");
		//	tableVsPrimaryField.put("Vendors_Attachments", "ID");
		//	tableVsPrimaryField.put("Item_Types_Notes", "ID");
		//	tableVsPrimaryField.put("Item_Types_Attachments", "ID");
		//	tableVsPrimaryField.put("Purchased_Item", "ID");
		//	tableVsPrimaryField.put("Inventory_Requests", "ID");
		//	tableVsPrimaryField.put("Inventory_Request_Attachments", "ID");
		//	tableVsPrimaryField.put("Inventory_Request_Notes", "ID");
		//	tableVsPrimaryField.put("InventoryRequestLineItems", "ID");
		//	tableVsPrimaryField.put("Shipment", "ID");
		//	tableVsPrimaryField.put("Shipment_line_item", "ID");
		//	tableVsPrimaryField.put("Shipment_Notes", "ID");
		//	tableVsPrimaryField.put("Shipment_Attachments", "ID");
		//	tableVsPrimaryField.put("Item_Transactions", "ID");
		//	tableVsPrimaryField.put("Workorder_items", "ID");
		//	tableVsPrimaryField.put("Item_Activity", "ID");
		//	tableVsPrimaryField.put("Tool_types_category", "ID");
		//	tableVsPrimaryField.put("Purchased_Tool", "ID");
		//	tableVsPrimaryField.put("Tool_types_Notes", "ID");
		//	tableVsPrimaryField.put("Tool_types_Attachments", "ID");
		//	tableVsPrimaryField.put("Tool_transactions", "ID");
		//	tableVsPrimaryField.put("Workorder_tools", "ID");
		//	tableVsPrimaryField.put("Item_vendors", "ID");
		//	tableVsPrimaryField.put("Tool_vendors", "ID");
		//	tableVsPrimaryField.put("Labour", "ID");
		//	tableVsPrimaryField.put("Workorder_labour", "ID");
		// tableVsPrimaryField.put("Event_Property", "ID");
		// tableVsPrimaryField.put("Event_Rule", "EVENT_RULE_ID");
		// tableVsPrimaryField.put("Event_Rules", "ID");
		// tableVsPrimaryField.put("Event", "ID");
		// tableVsPrimaryField.put("Event_To_Alarm_Field_Mapping", "EVENT_TO_ALARM_FIELD_MAPPING_ID");
		//	tableVsPrimaryField.put("Source_To_Resource_Mapping", "ID");
		//	tableVsPrimaryField.put("Notification_Logger", "ID");
		//	tableVsPrimaryField.put("Purchase_Requests", "ID");
		//	tableVsPrimaryField.put("Purchase_Request_Attachments", "ID");
		//	tableVsPrimaryField.put("Purchase_Request_Notes", "ID");
		//	tableVsPrimaryField.put("Service", "ID");
		//	tableVsPrimaryField.put("PurchaseRequestLineItems", "ID");
		//	tableVsPrimaryField.put("Purchase_Order_Attachments", "ID");
		//	tableVsPrimaryField.put("Purchase_Order_Notes", "ID");
		//	tableVsPrimaryField.put("PurchaseOrderLineItems", "ID");
		// tableVsPrimaryField.put("PurchaseRequest_PurchaseOrder", "PO_ID, PR_ID");
		//	tableVsPrimaryField.put("Receivables", "ID");
		//	tableVsPrimaryField.put("Receivables_Attachments", "ID");
		//	tableVsPrimaryField.put("Receivables_Notes", "ID");
		//	tableVsPrimaryField.put("Receipts", "ID");
		//	tableVsPrimaryField.put("Contracts_Attachments", "ID");
		//	tableVsPrimaryField.put("Contracts_Notes", "ID");
		//	tableVsPrimaryField.put("Purchase_Contracts", "ID");
		//	tableVsPrimaryField.put("PurchaseContractLineItems", "ID");
		//	tableVsPrimaryField.put("Labour_Contracts", "ID");
		//	tableVsPrimaryField.put("LabourContractLineItems", "ID");
		//	tableVsPrimaryField.put("Storeroom_Sites", "ID");
		//	tableVsPrimaryField.put("PO_Line_Item_Serial_Numbers", "ID");
		//	tableVsPrimaryField.put("Item_Attachments", "ID");
		//	tableVsPrimaryField.put("Item_Notes", "ID");
		//	tableVsPrimaryField.put("Tool_Attachments", "ID");
		//	tableVsPrimaryField.put("Tool_Notes", "ID");
		//	tableVsPrimaryField.put("Gate_Pass_Line_Items", "ID");
		//	tableVsPrimaryField.put("Workflow_RCA_Mapping", "RULE_ID,RCA_RULE_ID");
		//	tableVsPrimaryField.put("AlarmWorkflow_Rule", "ID");
		//	tableVsPrimaryField.put("StateFlow", "ID");
		//	tableVsPrimaryField.put("StateFlowTransition", "ID");
		//	tableVsPrimaryField.put("StateFlowScheduler", "ID");
		//	tableVsPrimaryField.put("Workflow_Validation", "ID");
		//	tableVsPrimaryField.put("CustomButton", "ID");
		//	tableVsPrimaryField.put("ConnectedApps", "ID");
		//	tableVsPrimaryField.put("Connection", "ID");
		//	tableVsPrimaryField.put("Connection_Api", "ID");
		//	tableVsPrimaryField.put("Connection_Params", "ID");
		//	tableVsPrimaryField.put("ML", "ID");
		//	tableVsPrimaryField.put("ML_Variables", "ID");
		//	tableVsPrimaryField.put("ML_Model_Variables", "ID");
		//	tableVsPrimaryField.put("ML_Asset_Variables", "ID");
		//	tableVsPrimaryField.put("ML_Criteria_Variables", "ID");
		//	tableVsPrimaryField.put("Rule_Templates_Rel", "ID");
		//	tableVsPrimaryField.put("Shipped_Asset_Rel", "ID");
		//	tableVsPrimaryField.put("PM_Planner_Settings", "ID");
		//	tableVsPrimaryField.put("Attendance", "ID");
		//	tableVsPrimaryField.put("Attendance_Transactions", "ID");
		//	tableVsPrimaryField.put("Graphics_Folder", "ID");
		//	tableVsPrimaryField.put("Graphics", "ID");
		//	tableVsPrimaryField.put("Widget_Graphics", "ID");
		//	tableVsPrimaryField.put("MV_Project", "ID");
		//	tableVsPrimaryField.put("MV_Baseline", "ID");
		//	tableVsPrimaryField.put("MV_Adjustment", "ID");
		//	tableVsPrimaryField.put("Shift_Rotation", "ID");
		//	tableVsPrimaryField.put("Shift_Rotation_Details", "ID");
		//	tableVsPrimaryField.put("Shift_Rotation_Applicable_For", "ID");
		//	tableVsPrimaryField.put("Store_Notification_config", "ID");
		//	tableVsPrimaryField.put("Break_Transaction", "ID");
		//	tableVsPrimaryField.put("Warranty_Contracts", "ID");
		//	tableVsPrimaryField.put("Contracts_Associated_Assets", "ID");
		//	tableVsPrimaryField.put("WarrantyContractLineItems", "ID");
		//	tableVsPrimaryField.put("Workorder_service", "ID");
		//	tableVsPrimaryField.put("Service_vendors", "ID");
		//	tableVsPrimaryField.put("Rental_Lease_Contracts", "ID");
		//	tableVsPrimaryField.put("RentalLeaseContractLineItems", "ID");
		//	tableVsPrimaryField.put("Terms_And_Conditions", "ID");
		//	tableVsPrimaryField.put("Terms_And_Conditions_Attachments", "ID");
		//	tableVsPrimaryField.put("Terms_And_Conditions_Notes", "ID");
		//	tableVsPrimaryField.put("Contracts_Associated_Terms", "ID");
		//	tableVsPrimaryField.put("Digest_Configuration", "ID");
		//	tableVsPrimaryField.put("Digest_Configuration_Users", "ID");
		//	tableVsPrimaryField.put("Preference_Rules", "ID");
		//	tableVsPrimaryField.put("Reading_Tools", "ID");
		//	tableVsPrimaryField.put("Control_Group_Spaces", "ID");
		//	tableVsPrimaryField.put("Control_Group_Include_Exclude_Resource", "ID");
		//	tableVsPrimaryField.put("Control_Action_Command", "ID");
		//	tableVsPrimaryField.put("Reservations", "ID");
		//	tableVsPrimaryField.put("Reservation_Internal_Attendees", "ID");
		//	tableVsPrimaryField.put("Reservation_External_Attendees", "ID");
		//	tableVsPrimaryField.put("Reservation_Attachments", "ID");
		//	tableVsPrimaryField.put("Reservation_Notes", "ID");
		//	tableVsPrimaryField.put("Asset_Movement", "ID");
		//	tableVsPrimaryField.put("PO_Associated_Terms", "ID");
		//	tableVsPrimaryField.put("Devices", "ID");
		//	tableVsPrimaryField.put("Digital_Log_Book", "ID");
		//	tableVsPrimaryField.put("FacilioQueue", "ID");
		//	tableVsPrimaryField.put("VisitorType", "ID");
		//	tableVsPrimaryField.put("Visitor", "ID");
		//	tableVsPrimaryField.put("Visitor_Attachments", "ID");
		//	tableVsPrimaryField.put("Visitor_Notes", "ID");
		//	tableVsPrimaryField.put("Visitor_Invites", "ID");
		//	tableVsPrimaryField.put("Invite_Visitor_Rel", "ID");
		//	tableVsPrimaryField.put("Visitor_Logging", "ID");
		//	tableVsPrimaryField.put("VisitorLog_Triggers", "ID");
		//	tableVsPrimaryField.put("Visitor_Logging_Attachments", "ID");
		//	tableVsPrimaryField.put("Visitor_Logging_Notes", "ID");
		//	tableVsPrimaryField.put("VisitorSettings", "VISITOR_TYPE_ID");
		//	tableVsPrimaryField.put("Service_Catalog_Group", "ID");
		//	tableVsPrimaryField.put("Service_Catalog", "ID");
		//	tableVsPrimaryField.put("Insurance", "ID");
		//	tableVsPrimaryField.put("Insurance_Attachments", "ID");
		//	tableVsPrimaryField.put("Insurance_Notes", "ID");
		//	tableVsPrimaryField.put("WatchList", "ID");
		//	tableVsPrimaryField.put("WorkPermit", "ID");
		//	tableVsPrimaryField.put("WorkPermit_Attachments", "ID");
		//	tableVsPrimaryField.put("WorkPermit_Notes", "ID");
		//	tableVsPrimaryField.put("Printers", "ID");
		//	tableVsPrimaryField.put("Devices_Visitor_Kiosk", "ID");
		//	tableVsPrimaryField.put("Devices_Feedback_Kiosk", "ID");
		//	tableVsPrimaryField.put("Device_Catalog_Mapping", "ID");

		//	tableVsPrimaryField.put("Reset_Counter_Meta", "ID");
		//	tableVsPrimaryField.put("Task_Section", "ID");
		//	tableVsPrimaryField.put("Tasks", "ID");
		//	tableVsPrimaryField.put("Task_Attachments", "ID");
		//	tableVsPrimaryField.put("Task_Input_Options", "ID");

		//	tableVsPrimaryField.put("BaseAlarm", "ID");
		//	tableVsPrimaryField.put("BaseAlarm_Notes", "ID");
		//	tableVsPrimaryField.put("AlarmOccurrence", "ID");
		//	tableVsPrimaryField.put("ReadingAlarmCategory", "ID");
		//	tableVsPrimaryField.put("BaseEvent", "ID");
		//	tableVsPrimaryField.put("Alarms", "ID");

		//	tableVsPrimaryField.put("ReadingAlarmCategory", "ID");
		////	tableVsPrimaryField.put("ReadingEvent", "ID");
		//	tableVsPrimaryField.put("ReadingAlarmOccurrence", "ID");
		//	tableVsPrimaryField.put("ReadingAlarm", "ID");
		//	tableVsPrimaryField.put("BMSEvent", "ID");
		//	tableVsPrimaryField.put("BMSAlarm", "ID");
		//	tableVsPrimaryField.put("Reading_Alarms", "ID");
		//	tableVsPrimaryField.put("ML", "ID");
/*		tableVsPrimaryField.put("ML_Anomaly_Event", "ID");
		tableVsPrimaryField.put("MLAlarmOccurence", "ID");
		tableVsPrimaryField.put("ML_Anomaly_Alarm", "ID");
		tableVsPrimaryField.put("RCA_Event", "ID");
		tableVsPrimaryField.put("RCA_Alarm", "ID");
		tableVsPrimaryField.put("ReadingRCAEvent", "ID");
		tableVsPrimaryField.put("ReadingRCAAlarm", "ID");
		tableVsPrimaryField.put("ML_Alarms", "ID");
		tableVsPrimaryField.put("ML_Alarm_Occurrences", "ID");
		tableVsPrimaryField.put("ReadingViolationEvent", "ID");
		tableVsPrimaryField.put("ReadingViolationAlarm", "ID");
		tableVsPrimaryField.put("ReadingViolationOccurrence", "ID");
		tableVsPrimaryField.put("Reading_Rule_Alarm_Meta", "ID");
		tableVsPrimaryField.put("Alarm_Activity", "ID");*/

		tableVsPrimaryField.put("Energy_Data", "ID");/*
		tableVsPrimaryField.put("Weather_Reading", "ID");
		tableVsPrimaryField.put("Weather_Reading_Daily", "ID");
		tableVsPrimaryField.put("Weather_Reading_Hourly_Forecast", "ID");
		tableVsPrimaryField.put("Weather_Reading_Daily_Forecast", "ID");
		tableVsPrimaryField.put("Psychrometric_Reading", "ID");
		tableVsPrimaryField.put("CDD_Reading", "ID");
		tableVsPrimaryField.put("HDD_Reading", "ID");
		tableVsPrimaryField.put("WDD_Reading", "ID");
		tableVsPrimaryField.put("Temperature_Reading", "ID");
		tableVsPrimaryField.put("Humidity_Reading", "ID");
		tableVsPrimaryField.put("CO2_Reading", "ID");
		tableVsPrimaryField.put("Chiller_Readings", "ID");
		tableVsPrimaryField.put("Chiller_Readings2", "ID");
		tableVsPrimaryField.put("Chiller_Plant_Manager_Readings", "ID");
		tableVsPrimaryField.put("Chiller_Condenser_Readings", "ID");
		tableVsPrimaryField.put("Chiller_Primary_Pump_Readings", "ID");
		tableVsPrimaryField.put("Chiller_Secondary_Pump_Readings", "ID");
		tableVsPrimaryField.put("Current_Occupancy_Reading", "ID");
		tableVsPrimaryField.put("Assigned_Occupancy_Reading", "ID");
		tableVsPrimaryField.put("Set_Point_Reading", "ID");
		tableVsPrimaryField.put("Chiller_Condenser_Pump_Readings", "ID");
		tableVsPrimaryField.put("AHU_Readings", "ID");
		tableVsPrimaryField.put("FAHU_Readings", "ID");
		tableVsPrimaryField.put("FCU_Readings", "ID");
		tableVsPrimaryField.put("Cooling_Tower_Readings", "ID");
		tableVsPrimaryField.put("Heat_Pump_Readings", "ID");
		tableVsPrimaryField.put("Utility_Bill_Readings", "ID");
		tableVsPrimaryField.put("Water_Readings", "ID");
		tableVsPrimaryField.put("Readings", "ID");
		tableVsPrimaryField.put("Readings_2", "ID");
		tableVsPrimaryField.put("Formula_Readings", "ID");
		tableVsPrimaryField.put("Controller_Readings", "ID");
		tableVsPrimaryField.put("Predicted_Readings", "ID");
		tableVsPrimaryField.put("PM_Readings", "ID");
		tableVsPrimaryField.put("Marked_Reading", "ID");
		tableVsPrimaryField.put("Cost_Readings", "ID");
		tableVsPrimaryField.put("ML_Readings", "ID");
		tableVsPrimaryField.put("ML_Log_Readings", "ID");
		tableVsPrimaryField.put("MV_Readings", "ID");
		tableVsPrimaryField.put("MV_Adjustment_Readings", "ID");*/

	}

	private static String getInsertStatement(String tableName, String[] columns) {
		// insert into bms_new_test.modules select * from bmslocal.modules;
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(tableName).append(" ");
		sql.append("(");
		for (int i = 0; i < columns.length; i++) {
			sql.append(columns[i]);
			if (i < columns.length-1) {
				sql.append(",");
			}
		}
		sql.append(")");
		sql.append(" values(");
		for (int i = 1; i <= columns.length; i++) {
			if (i < columns.length) {
				sql.append("?,");
			} else {
				sql.append("?");
			}
		}
		sql.append(")");
		sql.append(";").toString();
		System.out.println("Select statement : " + sql);
		return sql.toString();
	}

	private static String getSelectStatement(String tableName, String fieldName, long offset) {
		String sql = new StringBuilder("SELECT * FROM ").append(tableName).append(" WHERE ORGID = ").append(ORGID)
				.append(" ORDER BY ").append(fieldName).append(" LIMIT ").append(LIMIT).append(" OFFSET ")
				.append(offset).append(";").toString();
		System.out.println("Select statement : " + sql);
		return sql;
	}

	private static String getCountStatement(String tableName, String fieldName) {
		String sql = new StringBuilder("SELECT COUNT(*) AS total FROM ").append(tableName).append(" WHERE ORGID = ")
				.append(ORGID).append(";").toString();
		System.out.println("Select statement : " + sql);
		return sql;
	}

	private static void executeBatch(int count, PreparedStatement pstmt, long id) throws SQLException {
		pstmt.executeBatch();
		System.out.println("Updated batch upto ID : " + id);
	}

	public static void main(String args[]) {
		Connection con2 = null;
		try (Connection con1 = getConn1();) {
			con2 = getConn2();
			init();
			for (Map.Entry<String, String> entry : tableVsPrimaryField.entrySet()) {
				int totalCount = 0;
				while (true) {
					try (PreparedStatement pstmt1 = con1
							.prepareStatement(getSelectStatement(entry.getKey(), entry.getValue(), totalCount));
						 ResultSet rs = pstmt1.executeQuery()) {
						long id = -1;
						int count = 0;
						ResultSetMetaData rdm = rs.getMetaData();
						int columnCount = rdm.getColumnCount();
						String[] columns = new String[columnCount];
						for (int i = 0; i < columnCount; i++) {
							columns[i] = rdm.getColumnName(i + 1);
						}
						try (PreparedStatement pstmt2 = con2
								.prepareStatement(getInsertStatement(entry.getKey(), columns));) {
							int currentBatch = 0;
							while (rs.next()) {
								id = rs.getLong(1);
								count++;
								for (int i = 1; i <= columnCount; i++) {
									pstmt2.setObject(i, rs.getObject(i));
								}
								pstmt2.addBatch();
								currentBatch++;
							}
							if (currentBatch > 0) {
								executeBatch(count, pstmt2, id);
								con2.commit();
								System.out.println("Migration done for table: " + entry.getKey());
							}
							else {
								System.out.println("Migration done for table: " + entry.getKey());
								break;
							}
						} catch (Exception e) {
							throw e;
						}
						totalCount += LIMIT;
					} catch (Exception e) {
						e.printStackTrace();
						try {
							if (con2 != null) {
								con2.rollback();
							}
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						throw e;
					}
				}
			}
			//	con2.commit();
			System.out.println("Completed Migration");
		} catch (Exception e) {
			System.out.println("Error occurred : ");
			//	try {
			//		if (con2 != null) {
			//			con2.rollback();
			//	}
			//} catch (SQLException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			//}
			e.printStackTrace();
		} finally {
			if (con2 != null) {
				try {
					con2.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
