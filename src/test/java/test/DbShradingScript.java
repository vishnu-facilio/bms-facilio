package test;

import java.sql.*;
import java.util.*;

public class DbShradingScript {
	private static final String SOURCE_URL = "localhost:3306/bmslocal";
	private static final String SOURCE_USERNAME = "root", SOURCE_PASSWORD = "";

	private static final String TARGET_URL = "localhost:3306/bmslocal";
	private static final String TARGET_USERNAME = "root", TARGET_PASSWORD = "";

	private static final long ORGID = 339;

	private static int LIMIT = 50000;
	private static List<String> tables = init();

	private static Connection getConn1() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://"+SOURCE_URL, SOURCE_USERNAME, SOURCE_PASSWORD);
		return con;
	}

	private static Connection getConn2() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://"+TARGET_URL, TARGET_USERNAME, TARGET_PASSWORD);
		con.setAutoCommit(false);
		return con;
	}

	private static String getPKs (Connection conn, String tableName) throws SQLException {
		StringJoiner builder = new StringJoiner(",");
		DatabaseMetaData meta=conn.getMetaData();
		try (ResultSet rs = meta.getPrimaryKeys(conn.getCatalog(), conn.getSchema(), tableName);) {
			while (rs.next()) {
				builder.add(rs.getString("COLUMN_NAME"));
			}
		}
		return builder.toString();
	}

	private static List<String> init() {
		return Arrays.asList(
				"Role",
				"FacilioFile",
				"Modules",
				"Permission",
				"BusinessHours",
				"Forms",
				"SingleDayBusinessHours",
				"Resources",
				"BaseSpace",
				"ORG_Users",
				"People",
				"Global_FacilioAuthToken",
				"Global_Domain_Index",
				"Locations",
				"TicketStatus",
				"Clients",
				"Clients_Notes",
				"Clients_Attachments",
				"Site",
				"Org_Units",
				"UtilityProviders",
				"FacilioGroups",
				"FacilioGroupMembers",
				"ORGCache",
				"SubModulesRel",
				"Fields",
				"LookupFields",
				"MultiLookupFields",
				"MultiEnumFields",
				"NumberFields",
				"BooleanFields",
				"EnumFieldValues",
				"SystemEnumFields",
				"FileFields",
				"Shift_Readings",
				"ResizedFile",
				"Common_Job_Props",
				"Workflow_Namespace",
				"Workflow",
				"Workflow_User_Function",
				"Workflow_Field",
				"Workflow_Log",
				"Scheduled_Workflow",
				"Accessible_Space",
				"Resource_Readings",
				"Current_Occupancy_Reading",
				"Assigned_Occupancy_Reading",
				"Temperature_Reading",
				"Humidity_Reading",
				"CO2_Reading",
				"Set_Point_Reading",
				"Weather_Reading",
				"Weather_Reading_Daily",
				"Weather_Reading_Hourly_Forecast",
				"Weather_Reading_Daily_Forecast",
				"Psychrometric_Reading",
				"CDD_Reading",
				"HDD_Reading",
				"WDD_Reading",
				"BaseSpace_Photos",
				"BaseSpace_Attachments",
				"BaseSpace_Notes",
				"Form_Section",
				"Form_Fields",
				"Building",
				"Agent_Data",
				"Agent_Log",
				"Agent_Metrics",
				"Agent_Message",
				"Agent_Integration",
				"Controller",
				"Controller_Activity",
				"Controller_Activity_Records",
				"Controller_Activity_Watcher",
				"Controller_Building_Rel",
				"Publish_Data",
				"Publish_Message",
				"Floor",
				"Space_Category",
				"Space_Category_Readings",
				"Space",
				"Zone",
				"Zone_Space",
				"Store_room",
				"Item_Types_category",
				"Item_Type_Activity",
				"Inventory_category",
				"Item_Types_status",
				"Item_Types",
				"Item_status",
				"Item",
				"Tool_types_status",
				"Tool_types",
				"Tool_status",
				"Tool",
				"Vendors",
				"Contracts",
				"Purchase_Orders",
				"Asset_Types",
				"Asset_Categories",
				"RecommendedRule",
				"Asset_Category_Readings",
				"Asset_Departments",
				"Assets",
				"Relationship",
				"Related_Assets",
				"Asset_Activity",
				"AssetDepreciation",
				"Asset_Depreication_Rel",
				"Asset_Depreciation_Calculation",
				"Agent",
				"Asset_Breakdown",
				"Asset_BD_SourceDetails",
				"Agent_V2_Metrics",
				"Field_Device",
				"Controllers",
				"Misc_Controller",
				"Custom_Controller",
				"Rest_Controller",
				"BACnet_IP_Controller",
				"Niagara_Controller",
				"LonWorks_Controller",
				"Rtu_Network",
				"Modbus_Rtu_Controller",
				"Modbus_Tcp_Controller",
				"OpcXMLDA_Controller",
				"OpcUA_Controller",
				"Iot_Data",
				"Iot_Message",
				"Modbus_Import",
				"Agent_V2_Log",
				"Agent_Thread_Dump",
				"AssetCustomModuleData",
				"Work_Order_Activity",
				"Energy_Meter_Purpose",
				"Energy_Meter",
				"Controller_Asset",
				"Virtual_Energy_Meter_Rel",
				"Hvac",
				"Chiller",
				"Chiller_Readings",
				"Chiller_Readings2",
				"Chiller_Plant_Manager",
				"Chiller_Plant_Manager_Readings",
				"Chiller_Condenser_Readings",
				"Chiller_Primary_Pump",
				"Chiller_Primary_Pump_Readings",
				"Chiller_Secondary_Pump",
				"Chiller_Secondary_Pump_Readings",
				"Chiller_Condenser_Pump",
				"Chiller_Condenser_Pump_Readings",
				"AHU",
				"AHU_Readings_General",
				"AHU_Readings_Supply_Air",
				"AHU_Readings_Exhaust_Air",
				"AHU_Readings_Mix_Air",
				"FAHU",
				"FAHU_Readings_General",
				"FAHU_Readings_Supply_Air",
				"FAHU_Readings_Return_Air",
				"Cooling_Tower",
				"Cooling_Tower_Readings",
				"FCU",
				"FCU_Readings_2",
				"Heat_Pump",
				"Heat_Pump_Readings",
				"Utility_Meters",
				"Utility_Bill_Readings",
				"Water_Meter",
				"Water_Readings",
				"Asset_Photos",
				"Asset_Attachments",
				"Site_Attachments",
				"Asset_Notes",
				"TicketStateFlow",
				"Tenants",
				"Tenant_Unit_Space",
				"Contacts",
				"Contacts_Attachments",
				"Contacts_Notes",
				"ServiceRequestPriority",
				"Service_Requests",
				"TicketPriority",
				"TicketCategory",
				"TicketType",
				"Tickets",
				"Ticket_Attachments",
				"Ticket_Notes",
				"WorkOrderRequests",
				"WorkOrderRequest_EMail",
				"Task_Section",
				"Tasks",
				"Task_Attachments",
				"Task_Input_Options",
				"UserLocationCoverage",
				"GroupLocationCoverage",
				"Skills",
				"UserSkills",
				"GroupSkills",
				"Alarm_Severity",
				"Alarm_Entity",
				"Readings",
				"Readings_2",
				"Readings_3",
				"Formula_Readings",
				"Aggregated_Readings",
				"Controller_Readings",
				"Predicted_Readings",
				"CustomModuleData",
				"Custom_Activity",
				"Custom_Rel_Records",
				"Custom_Multi_Enum_Values",
				"CMD_Notes",
				"CMD_Attachments",
				"Attachments",
				"Notes",
				"Energy_Data_2",
				"Criteria",
				"Conditions",
				"RollUpFields",
				"Form_Rule",
				"Form_Rule_Action",
				"Form_Rule_Action_Field",
				"Views",
				"View_Sort_Columns",
				"ImportProcess",
				"ImportTemplate",
				"ImportProcessLog",
				"Expression",
				"KPI_Category",
				"Formula_Field",
				"Formula_Field_Inclusions",
				"Formula_Field_Resource_Jobs",
				"Module_KPIs",
				"Job_Plan",
				"Templates",
				"EMail_Templates",
				"Workflow_Template",
				"Control_Groups",
				"Control_Action_Template",
				"SMS_Templates",
				"Whatsapp_Message_Template",
				"Call_Template",
				"Assignment_Templates",
				"SLA_Templates",
				"Push_Notification_Templates",
				"JSON_Template",
				"Form_Template",
				"Safety_Plan",
				"Workorder_Template",
				"Task_Section_Template",
				"Prerequisite_Approvers_Template",
				"Task_Template",
				"Action",
				"Workflow_Rule",
				"Workflow_Field_Change_Fields",
				"Workflow_Rule_Action",
				"Excel_Templates",
				"Excel_PlaceHolders",
				"Tenant_spaces",
				"Preventive_Maintenance",
				"PM_Include_Exclude_Resource",
				"Approval_Rules",
				"Approvers",
				"Approver_Actions_Rel",
				"Approval_Steps",
				"PM_Triggers",
				"WorkOrders",
				"Related_Workorders",
				"Prerequisite_Photos",
				"Prerequisite_Approvers",
				"WorkOrderTimeLog",
				"BaseAlarm",
				"BaseAlarm_Notes",
				"AlarmOccurrence",
				"ReadingAlarmCategory",
				"BaseEvent",
				"Alarms",
				"AlarmFollowers",
				"User_Workhour_Readings",
				"PM_Readings",
				"SupportEmails",
				"EmailSettings",
				"PortalInfo",
				"Ticket_Activity",
				"Connected_App",
				"Tab_Widget",
				"Notification",
				"Report_Folder",
				"Report_Formula_Field",
				"Report_Field",
				"Report_Entity",
				"Report",
				"Report_DateFilter",
				"Report_EnergyMeter",
				"Report_Criteria",
				"Report_Threshold",
				"Report_User_Filter",
				"BaseLines",
				"Benchmark",
				"Report1_Folder",
				"Report1",
				"Report_Notes",
				"Report_Fields",
				"Dashboard_Folder",
				"Dashboard",
				"Dashboard_Tab",
				"Dashboard_Notes",
				"Space_Filtered_Dashboard_Settings",
				"Widget",
				"Widget_Chart",
				"Widget_List_View",
				"Widget_Static",
				"Widget_Vs_Workflow",
				"Widget_Web",
				"Widget_Card",
				"Screen_Dashboard_Rel",
				"Formula",
				"Report_SpaceFilter",
				"Unmodeled_Instance",
				"Points",
				"Point",
				"Misc_Point",
				"BACnet_IP_Point",
				"Niagara_Point",
				"OPC_XML_DA_Point",
				"Modbus_Tcp_Point",
				"Modbus_Rtu_Point",
				"OPC_UA_Point",
				"Historical_Logger",
				"Workflow_Rule_Historical_Logger",
				"Formula_Field_Historical_Logger",
				"Workflow_Rule_Logger",
				"Workflow_Rule_Resource_Logger",
				"Workflow_Rule_Historical_Logs",
				"Unmodeled_Data",
				"Instance_To_Asset_Mapping",
				"View_Column",
				"License",
				"FeatureLicense",
				"OrgInfo",
				"View_Schedule_Info",
				"Report_Schedule_Info",
				"Report_Schedule_Info1",
				"Calendar_Color",
				"Benchmark_Units",
				"Report_Benchmark_Rel",
				"Reading_Rule",
				"Reading_Alarm_Rule",
				"Reading_Rule_Inclusions_Exclusions",
				"ReadingEvent",
				"ReadingAlarmOccurrence",
				"ReadingAlarm",
				"BMSEvent",
				"BMSAlarm",
				"Reading_Alarms",
				"ML",
				"ML_Anomaly_Event",
				"MLAlarmOccurence",
				"ML_Anomaly_Alarm",
				"PreEvent",
				"PreAlarmOccurrence",
				"PreAlarm",
				"RCA_Event",
				"RCA_Alarm",
				"ReadingRCAEvent",
				"ReadingRCAAlarm",
				"ML_Alarms",
				"ML_Alarm_Occurrences",
				"ReadingViolationEvent",
				"ReadingViolationAlarm",
				"ReadingViolationOccurrence",
				"Reading_Rule_Alarm_Meta",
				"SLA_Rule",
				"Reading_Rule_Flaps",
				"Reading_Rule_Metrics",
				"Scheduled_Actions",
				"Task_Section_Template_Triggers",
				"PM_Jobs",
				"PM_Reminders",
				"PM_Reminder_Action",
				"Before_PM_Reminder_Trigger_Rel",
				"After_PM_Reminder_WO_Rel",
				"PM_Resource_Planner",
				"PM_Resource_Planner_Reminder",
				"PM_Resource_Planner_Triggers",
				"PM_Resource_Schedule_Rule_Rel",
				"Report_BaseLine_Rel",
				"Report_Columns",
				"Reading_Data_Meta",
				"Reset_Counter_Meta",
				"Reading_Input_Values",
				"Marked_Reading",
				"Dashboard_Sharing",
				"View_Sharing",
				"Pm_Exec_Sharing",
				"Report_Folder_Sharing",
				"Derivations",
				"Tenant_Users",
				"Tenants_Utility_Mapping",
				"Tenant_Attachments",
				"Tenant_Notes",
				"Rate_Card",
				"Rate_Card_Services",
				"Module_Local_ID",
				"Shift",
				"Shift_User_Rel",
				"Break",
				"Shift_Break_Rel",
				"Costs",
				"Cost_Slabs",
				"Additional_Costs",
				"Cost_Assets",
				"Cost_Readings",
				"Sync_Errors",
				"DeviceDetails",
				"Workorder_cost",
				"Gate_Pass",
				"Gate_Pass_Notes",
				"Gate_Pass_Attachments",
				"Store_room_Notes",
				"Store_room_Attachments",
				"Store_Room_Photos",
				"Vendors_Notes",
				"Vendors_Attachments",
				"Item_Types_Notes",
				"Item_Types_Attachments",
				"Purchased_Item",
				"StateFlow",
				"Inventory_Requests",
				"Inventory_Request_Attachments",
				"Inventory_Request_Notes",
				"InventoryRequestLineItems",
				"Shipment",
				"Shipment_line_item",
				"Shipment_Notes",
				"Shipment_Attachments",
				"Item_Transactions",
				"Workorder_items",
				"Item_Activity",
				"Tool_types_category",
				"Purchased_Tool",
				"Tool_types_Notes",
				"Tool_types_Attachments",
				"Tool_transactions",
				"Workorder_tools",
				"Item_vendors",
				"Tool_vendors",
				"Labour",
				"Workorder_labour",
				"Event_Property",
				"Event_Rule",
				"Event_Rules",
				"Event",
				"Event_To_Alarm_Field_Mapping",
				"Source_To_Resource_Mapping",
				"Notification_Logger",
				"Purchase_Requests",
				"Purchase_Request_Attachments",
				"Purchase_Request_Notes",
				"Service",
				"PurchaseRequestLineItems",
				"Purchase_Order_Attachments",
				"Purchase_Order_Notes",
				"PurchaseOrderLineItems",
				"Receivables",
				"Receivables_Attachments",
				"Receivables_Notes",
				"Receipts",
				"Contracts_Attachments",
				"Contracts_Notes",
				"Purchase_Contracts",
				"PurchaseContractLineItems",
				"Labour_Contracts",
				"LabourContractLineItems",
				"Storeroom_Sites",
				"PO_Line_Item_Serial_Numbers",
				"Item_Attachments",
				"Item_Notes",
				"Tool_Attachments",
				"Tool_Notes",
				"Gate_Pass_Line_Items",
				"Workflow_RCA_Mapping",
				"AlarmWorkflow_Rule",
				"StateFlowTransition",
				"ParallelStateTransitionStatus",
				"StateFlowScheduler",
				"Workflow_Validation",
				"CustomButton",
				"SLA_Entity",
				"SLA_Workflow_Commitment_Duration",
				"SLA_Workflow_Escalation",
				"SLA_Workflow_Escalation_Action",
				"ConnectedApps",
				"ConnectedApps_SAML",
				"ConnectedApp_Widgets",
				"Variables",
				"Connection",
				"ConnectedApp_Connectors",
				"Connection_Api",
				"Connection_Params",
				"ML",
				"ML_Variables",
				"ML_Model_Variables",
				"ML_Asset_Variables",
				"ML_Criteria_Variables",
				"ML_Readings",
				"ML_Log_Readings",
				"Rule_Templates_Rel",
				"Shipped_Asset_Rel",
				"PM_Planner_Settings",
				"Attendance",
				"Attendance_Transactions",
				"Graphics_Folder",
				"Graphics",
				"Widget_Graphics",
				"MV_Project",
				"MV_Baseline",
				"MV_Adjustment",
				"Shift_Rotation",
				"Shift_Rotation_Details",
				"Shift_Rotation_Applicable_For",
				"Store_Notification_config",
				"Break_Transaction",
				"Warranty_Contracts",
				"Contracts_Associated_Assets",
				"WarrantyContractLineItems",
				"Workorder_service",
				"Service_vendors",
				"Rental_Lease_Contracts",
				"RentalLeaseContractLineItems",
				"Terms_And_Conditions",
				"Terms_And_Conditions_Attachments",
				"Terms_And_Conditions_Notes",
				"Contracts_Associated_Terms",
				"Digest_Configuration",
				"Digest_Configuration_Users",
				"Preferences",
				"Preference_Rules",
				"Reading_Tools",
				"Control_Group_Spaces",
				"Control_Group_Include_Exclude_Resource",
				"Control_Action_Command",
				"Reservations",
				"Reservation_Internal_Attendees",
				"Reservation_External_Attendees",
				"Reservation_Attachments",
				"Reservation_Notes",
				"Asset_Movement",
				"PO_Associated_Terms",
				"Devices",
				"Digital_Log_Book",
				"FacilioQueue",
				"VisitorType",
				"Visitor",
				"FaceCollections",
				"Visitor_Faces",
				"Visitor_Attachments",
				"Visitor_Notes",
				"Visitor_Invites",
				"Invite_Visitor_Rel",
				"Visitor_Logging",
				"VisitorLog_Triggers",
				"Visitor_Logging_Attachments",
				"Visitor_Logging_Notes",
				"VisitorSettings",
				"Service_Catalog_Group",
				"Service_Catalog",
				"Insurance",
				"Insurance_Attachments",
				"Insurance_Notes",
				"WatchList",
				"WorkPermit",
				"WorkPermit_Attachments",
				"WorkPermit_Notes",
				"MV_Readings",
				"Alarm_Activity",
				"Form_Site_Relation",
				"MV_Adjustment_Readings",
				"Printers",
				"Devices_Visitor_Kiosk",
				"Occupants",
				"Occupants_Attachments",
				"Occupants_Notes",
				"Scoping",
				"Application",
				"WebTab_Group",
				"WebTab",
				"NewPermission",
				"TABID_MODULEID_APPID_MAPPING",
				"Service_Requests_Notes",
				"Service_Requests_Attachments",
				"Service_Requests_Activity",
				"CB_Model",
				"CB_Model_Versions",
				"CB_Intent",
				"CB_Intent_Action",
				"CB_Intent_Invoke_Sample",
				"CB_Intent_Params",
				"CB_Intent_Child",
				"CB_Session",
				"CB_Session_Conversation",
				"CB_Session_Params",
				"Agent_Alarm",
				"Agent_Event",
				"Agent_AlarmOccurrence",
				"AgentAlarm_Controller_Rel",
				"AgentEvent_Controller_Rel",
				"Vendor_Documents",
				"Hazard",
				"Safety_Plan_Attachments",
				"Safety_Plan_Notes",
				"Hazard_Attachments",
				"Hazard_Notes",
				"Precaution",
				"Precaution_Attachments",
				"Precaution_Notes",
				"SafetyPlan_Hazard",
				"Hazard_Precautions",
				"Workorder_Hazards",
				"Asset_Hazards",
				"DEMO_ROLLUP_TOOL",
				"Feedback_Type",
				"Feedback_Type_Catalog_Mapping",
				"Feedback_Kiosk",
				"Rating",
				"PM_Job_Plan",
				"PM_Job_Plan_Triggers",
				"Controllable_Asset_Category",
				"Controllable_Point",
				"Floor_Plan",
				"Floorplan_Objects",
				"Control_Point",
				"Smart_Control_Kiosk",
				"CommissioningLog",
				"CommissioningLogController",
				"Bim_Integration_Logs",
				"Bim_Import_Process_Mapping",
				"Bim_Default_Values",
				"People_Attachments",
				"People_Notes",
				"Vendor_Contacts",
				"Tenant_Contacts",
				"Employee",
				"Client_Contacts",
				"ORG_User_Apps",
				"Operation_Alarm",
				"Operation_Event",
				"Operation_Alarm_Occurrence",
				"Operation_Alarm_Historical_Logs",
				"Energy_Star_Customer",
				"Energy_Star_Property",
				"Energy_Star_Property_Use",
				"Energy_Star_Meter",
				"Scoping_Config"
		);
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
		StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ").append(tableName).append(" WHERE ORGID = ").append(ORGID);
				if (fieldName != null && !fieldName.isEmpty()) {
					sqlBuilder.append(" ORDER BY ").append(fieldName);
				}
				sqlBuilder.append(" LIMIT ").append(LIMIT).append(" OFFSET ")
				.append(offset).append(";");
		String sql = sqlBuilder.toString();
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
			for (String tableName : tables) {
				int totalCount = 0;
				while (true) {
					String pk = getPKs(con1, tableName);
					try (PreparedStatement pstmt1 = con1
							.prepareStatement(getSelectStatement(tableName, pk, totalCount));
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
								.prepareStatement(getInsertStatement(tableName, columns));) {
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
								System.out.println("Migration done for table: " + tableName);
							}
							else {
								System.out.println("Migration completed for table: " + tableName);
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
