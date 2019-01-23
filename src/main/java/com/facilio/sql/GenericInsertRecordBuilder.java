package com.facilio.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.FileField;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.transaction.FacilioConnectionPool;

public class GenericInsertRecordBuilder implements InsertBuilderIfc<Map<String, Object>> {
	private static final Logger LOGGER = LogManager.getLogger(GenericInsertRecordBuilder.class.getName());

	private List<FacilioField> fields;
	private String tableName;
	private List<Map<String, Object>> values = new ArrayList<>();
	private String sql;
	private Connection conn = null;
	
	@Override
	public GenericInsertRecordBuilder table(String tableName) {
		this.tableName = tableName;
		return this;
	}
	
	@Override
	public GenericInsertRecordBuilder useExternalConnection (Connection conn) {
		this.conn = conn;
		return this;
	}

	@Override
	public GenericInsertRecordBuilder fields(List<FacilioField> fields) {
		this.fields = fields;
		return this;
	}

	@Override
	public GenericInsertRecordBuilder addRecord(Map<String, Object> value) {
		this.values.add(value);
		return this;
	}

	@Override
	public GenericInsertRecordBuilder addRecords(List<Map<String, Object>> values) {
		this.values.addAll(values);
		return this;
	}
	
	@Override
	public long insert (Map<String, Object> value) throws Exception {
		addRecord(value);
		save();
		Long id = (Long) value.get("id");
		if (id != null) {
			return id;
		}
		return -1; 
	}

	@Override
	public List<Map<String, Object>> getRecords() {
		return values;
	}

	private List<FileField> fileFields = new ArrayList<>();
	private void handleFileFields() {
		try {
			FieldUtil.addFiles(fileFields, values);
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Insertion failed while adding files", e);
			throw new RuntimeException("Insertion failed while adding files");
		}
	}
	
	private List<NumberField> numberFields = new ArrayList<>();
	
	@Override
	public void save() throws SQLException, RuntimeException {
		
		if(values.isEmpty()) {
			return;
		}
		
		checkForNull();
		splitFields();
		
		handleFileFields();
		FieldUtil.handleNumberFieldUnitConversion(numberFields, values);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		boolean isExternalConnection = true;
		try {
			if (conn == null) {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				isExternalConnection = false;
			}
			
			if ((AccountUtil.getCurrentOrg() != null && (AccountUtil.getCurrentOrg().getId() == 155 || AccountUtil.getCurrentOrg().getId() == 151 || AccountUtil.getCurrentOrg().getId() == 92)) && (tableName.equals("Ticket_Attachments") || tableName.equals("WorkOrders"))) {
				LOGGER.info("Connection in Insert Builder for "+tableName+" : "+conn);
			}
			
			sql = constructInsertStatement();
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			List<Long> ids = new ArrayList<>();
			int itr = 0;
			for(Map<String, Object> value : values) {
				pstmt.clearParameters();
				int paramIndex = 1;
				for(FacilioField field : fields) {
					if (isPrimaryField(field)) {
						continue;
					}
					try {
						FieldUtil.castOrParseValueAsPerType(pstmt, paramIndex++, field, value.get(field.getName()));
					}
					catch (Exception e) {
						LOGGER.error("Error in parsing field : "+field+" during insertion.", e);
						throw e;
					}
				}
				pstmt.addBatch();
				pstmt.executeUpdate();
				rs = pstmt.getGeneratedKeys();
				
				if(rs.next()) {
					long id = rs.getLong(1);
					if (id > 0) {
						ids.add(id);
						Map<String, Object> props = values.get(itr++);
						if(props != null) {
							props.put("id", id);
						}
					}
				}
			}
			
//			int[] executeBatch = pstmt.executeBatch();
			
			
			//System.out.println("Added records with IDs : "+ids);
		}
		catch(SQLException | RuntimeException e) {
			StringBuilder builder = new StringBuilder();
			builder.append("SQL Statement : ")
					.append(sql)
					.append("\nProps : \n")
					.append(values);
			CommonCommandUtil.emailException("GenericInsertRecord", "Insertion failed - ", e, builder.toString());
			LOGGER.log(Level.ERROR, "Insertion failed ", e);
			throw e;
		}
		finally {
			if (isExternalConnection) {
				DBUtil.closeAll(pstmt, rs);
			}
			else {
				DBUtil.closeAll(conn, pstmt, rs);
				conn = null;
			}
		}
	}
	
//	public static Map<String, String> pkFields = new HashMap();
//	static {
//		pkFields.put("organizations", "orgid");
//		pkFields.put("users", "userid");
//		pkFields.put("usersessions", "sessionid");
//		pkFields.put("user_mobile_setting", "user_mobile_setting_id");
//		pkFields.put("role", "role_id");
//		pkFields.put("files", "file_id");
//		pkFields.put("modules", "module_id");
//		pkFields.put("measurementtype", "typeid");
//		pkFields.put("measurementunit", "unitid");
//		pkFields.put("groups", "groupid");
//		pkFields.put("groupmembers", "memberid");
//		pkFields.put("fields", "fieldid");
//		
//		
//		pkFields.put("faciliousers", "id");
//		pkFields.put("permission", "id");
//		pkFields.put("resources", "id");
//		pkFields.put("locations", "id");
//		pkFields.put("global_facilioauthtoken", "id");
//		pkFields.put("org_units", "id");
//		pkFields.put("shift_readings", "id");
//		pkFields.put("businesshours", "id");
//		pkFields.put("singledaybusinesshours", "id");
//		pkFields.put("resizedfiles", "id");
//		pkFields.put("workflow", "id");
//		pkFields.put("workflow_field", "id");
//		pkFields.put("current_occupancy_reading", "id");
//		pkFields.put("assigned_occupancy_reading", "id");
//		pkFields.put("temperature_reading", "id");
//		pkFields.put("humidity_reading", "id");
//		pkFields.put("co2_reading", "id");
//		pkFields.put("set_point_reading", "id");
//		pkFields.put("weather_reading", "id");
//		pkFields.put("psychrometric_reading", "id");
//		pkFields.put("cdd_reading", "id");
//		pkFields.put("hdd_reading", "id");
//		pkFields.put("wdd_reading", "id");
//		pkFields.put("backspace_photos", "id");
//		pkFields.put("backspace_attachments", "id");
//		pkFields.put("backspace_notes", "id");
//		pkFields.put("forms", "id");
//		pkFields.put("form_fields", "id");
//		pkFields.put("controller", "id");
//		pkFields.put("controller_activity", "id");
//		pkFields.put("controller_activity_watcher", "id");
//		pkFields.put("publish_data", "id");
//		pkFields.put("publish_message", "id");
//		pkFields.put("space_category", "id");
//		pkFields.put("asset_types", "id");
//		pkFields.put("asset_categories", "id");
//		pkFields.put("asset_departments", "id");
//		pkFields.put("energy_meter_purpose", "id");
//		pkFields.put("chiller_readings", "id");
//		pkFields.put("chiller_condenser_readings", "id");
//		pkFields.put("chiller_primary_pump_readings", "id");
//		pkFields.put("chiller_secondary_pump_readings", "id");
//		pkFields.put("chiller_condenser_pump_readings", "id");
//		pkFields.put("ahu_readings", "id");
//		pkFields.put("cooling_tower_readings", "id");
//		pkFields.put("fhu_readings", "id");
//		pkFields.put("heat_pump_readings", "id");
//		pkFields.put("utility_pump_readings", "id");
//		pkFields.put("water_readings", "id");
//		pkFields.put("asset_photos", "id");
//		pkFields.put("asset_attachments", "id");
//		pkFields.put("asset_notes", "id");
//		pkFields.put("ticketstatus", "id");
//		pkFields.put("ticketstateflow", "id");
//		pkFields.put("ticketpriority", "id");
//		pkFields.put("ticketcategory", "id");
//		pkFields.put("tickettype", "id");
//		pkFields.put("tickets", "id");
//		pkFields.put("ticket_attachments", "id");
//		pkFields.put("ticket_notes", "id");
//		pkFields.put("workorderrequest_email", "id");
//		pkFields.put("task_section", "id");
//		pkFields.put("tasks", "id");
//		pkFields.put("task_attachments", "id");
//		pkFields.put("task_input_options", "id");
//		pkFields.put("userlocationcoverage", "id");
//		pkFields.put("grouplocationcoverage", "id");
//		pkFields.put("skills", "id");
//		pkFields.put("userskills", "id");
//		pkFields.put("groupskills", "id");
//		pkFields.put("service", "id");
//		pkFields.put("alarm_severity", "id");
//		pkFields.put("alarm_entity", "id");
//		pkFields.put("readings", "id");
//		pkFields.put("custommoduledata", "id");
//		pkFields.put("attachments", "id");
//		pkFields.put("notes", "id");
//		pkFields.put("energy_data", "id");
//		pkFields.put("criteria", "criteriaid");
//		pkFields.put("conditions", "conditionid");
//		pkFields.put("views", "id");
//		pkFields.put("view_sort_columns", "id");
//		pkFields.put("importprocess", "id");
//		pkFields.put("importtemplate", "id");
//		pkFields.put("expression", "id");
//		pkFields.put("formula_field", "id");
//		pkFields.put("formula_field_inclusions", "id");
//		pkFields.put("formula_field_resource_jobs", "id");
//		pkFields.put("workflow_event", "id");
//		pkFields.put("templates", "id");
//		pkFields.put("action", "id");
//		pkFields.put("workflow_rule", "id");
//		pkFields.put("workflow_field_change_fields", "id");
//		pkFields.put("workflow_rule_action", "workflow_rule_action_id");
//		pkFields.put("tenant", "id");
//		pkFields.put("pm_include_exclude_resource", "id");
//		pkFields.put("approvers", "id");
//		pkFields.put("approver_actions_rel", "id");
//		pkFields.put("approval_steps", "id");
//		pkFields.put("alarmfollowers", "id");
//		pkFields.put("user_workhour_readings", "id");
//		pkFields.put("pm_readings", "id");
//		pkFields.put("supportemails", "id");
//		pkFields.put("emailsettings", "id");
//		pkFields.put("portalinfo", "id");
//		pkFields.put("faciliorequestors", "id");
//		pkFields.put("ticket_activity", "id");
//		pkFields.put("connected_app", "id");
//		pkFields.put("tab_widget", "id");
//		pkFields.put("notification", "id");
//		pkFields.put("report_folder", "id");
//		pkFields.put("report_formula_field", "id");
//		pkFields.put("report_field", "id");
//		pkFields.put("report_entity", "id");
//		pkFields.put("report", "id");
//		pkFields.put("report_datefilter", "id");
//		pkFields.put("report_energymeter", "id");
//		pkFields.put("report_criteria", "id");
//		pkFields.put("report_threshold", "id");
//		pkFields.put("report_user_filter", "id");
//		pkFields.put("baselines", "id");
//		pkFields.put("benchmark", "id");
//		pkFields.put("report1_folder", "id");
//		pkFields.put("report1", "id");
//		pkFields.put("report_fields", "id");
//		pkFields.put("dashboard_folder", "id");
//		pkFields.put("dashboard", "id");
//		pkFields.put("space_filtered_dashboard_settings", "id");
//		pkFields.put("widget", "id");
//		pkFields.put("dashboard_vs_widget", "id");
//		pkFields.put("widget_vs_workflow", "id");
//		pkFields.put("screen", "id");
//		pkFields.put("screen_dashboard_rel", "id");
//		pkFields.put("remote_screens", "id");
//		pkFields.put("formula", "id");
//		pkFields.put("report_spacefilter", "id");
//		pkFields.put("unmodeled_instance", "id");
//		pkFields.put("unmodeled_data", "id");
//		pkFields.put("historical_vs_calculation", "id");
//		pkFields.put("instance_to_asset_mapping", "id");
//		pkFields.put("view_column", "id");
//		pkFields.put("license", "id");
//		pkFields.put("orginfo", "id");
//		pkFields.put("report_schedule_info", "id");
//		pkFields.put("report_schedule_info1", "id");
//		pkFields.put("calendar_color", "id");
//		pkFields.put("benchmark_units", "id");
//		pkFields.put("reading_rule_inclusions_exclusions", "id");
//		pkFields.put("reading_rule_alarm_meta", "id");
//		pkFields.put("reading_rule_flaps", "id");
//		pkFields.put("scheduled_rule_jobs", "id");
//		pkFields.put("scheduled_actions", "id");
//		pkFields.put("pm_triggers", "id");
//		pkFields.put("pm_jobs", "id");
//		pkFields.put("pm_reminders", "id");
//		pkFields.put("before_pm_reminder_trigger_rel", "id");
//		pkFields.put("after_pm_reminder_wo_rel", "id");
//		pkFields.put("pm_resource_planner", "id");
//		pkFields.put("pm_resource_planner_reminder", "id");
//		pkFields.put("report_columns", "id");
//		pkFields.put("marked_reading", "id");
//		pkFields.put("dashboard_sharing", "id");
//		pkFields.put("view_sharing", "id");
//		pkFields.put("derivations", "id");
//		pkFields.put("weather_stations", "id");
//		pkFields.put("tenants", "id");
//		pkFields.put("tenants_utility_mapping", "id");
//		pkFields.put("rate_card", "id");
//		pkFields.put("rate_card_services", "id");
//		pkFields.put("shift", "id");
//		pkFields.put("shift_user_rel", "id");
//		pkFields.put("costs", "id");
//		pkFields.put("cost_slabs", "id");
//		pkFields.put("additional_costs", "id");
//		pkFields.put("cost_assets", "id");
//		pkFields.put("cost_readings", "id");
//		pkFields.put("server_info", "id");
//		pkFields.put("devicedetails", "id");
//		pkFields.put("clientapp", "id");
//		pkFields.put("event_property", "id");
//		pkFields.put("event_rule", "id");
//		pkFields.put("event_rules", "id");
//		pkFields.put("event", "id");
//		pkFields.put("event_to_alarm_field_mapping", "id");
//		pkFields.put("source_to_resource_mapping", "id");
//		pkFields.put("reading_data_meta", "id");
//	}
	
	public static boolean isPrimaryField(FacilioField field) {
		return (field.getDataTypeEnum() == FieldType.ID && (field.getModule() == null || field.getModule().getExtendModule() == null));
	}
	
	private String constructInsertStatement() {
		Map<String, Object> map = values.get(0);
		
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(tableName);
		sql.append(" (");
		
		boolean isFirst = true;
		for(FacilioField field : fields) {
			if (isPrimaryField(field)) {
				continue;
			}
			if(isFirst) {
				isFirst = false;
			}
			else {
				sql.append(", ");
			}
			sql.append(field.getColumnName());
		}
		
		sql.append(") VALUES (");
		
		isFirst = true;
		for(FacilioField field : fields) {
			if (isPrimaryField(field)) {
				continue;
			}
			if(isFirst) {
				isFirst = false;
			}
			else {
				sql.append(", ");
			}
			sql.append("?");
		}
		
		sql.append(")");
		
		return sql.toString();
	}
	
	private void checkForNull() {
		
		if(tableName == null || tableName.isEmpty()) {
			throw new IllegalArgumentException("Table Name cannot be empty");
		}
		
		if(fields == null || fields.size() < 1) {
			throw new IllegalArgumentException("Fields cannot be null or empty");
		}
		
	}
	
	private void splitFields() {
		for (FacilioField field : fields) {
			if (field instanceof FileField) {
				if (fileFields == null) {
					fileFields = new ArrayList<>();
				}
				fileFields.add((FileField) field);
			}
			else if (field instanceof NumberField) {
				if (numberFields == null) {
					numberFields = new ArrayList<>();
				}
				numberFields.add((NumberField) field);
			}
		}
	}
}
