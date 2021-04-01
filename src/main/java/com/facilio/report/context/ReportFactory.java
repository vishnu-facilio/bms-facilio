package com.facilio.report.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.FieldOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportFactory.ReportFacilioField;
import com.facilio.report.context.ReportFactory.WorkOrder;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReportFactory {
	
	public interface WorkOrder {
		String OPENVSCLOSE_COL = "openvsclose";
		String OVERDUE_OPEN_COL = "overdue_open";
		String OVERDUE_CLOSED_COL = "overdue_closed";
		String PLANNED_VS_UNPLANNED_COL = "plannedvsunplanned";
		String FIRST_RESPONSE_TIME_COL = "firstresponsetime";
		String ESTIMATED_DURATION_COL = "estimatedduration";
		String TOTAL_SCORE_PERCENTAGE_COL = "totalscorepercentage";
		String RESPONSE_SLA_COL = "response_sla";
		String RESOLUTION_DUE_COL="resolutionduestatus";
		String ACCEPTANCE_DUE_COL="acceptanceduestatus";
		
		
		int OPENVSCLOSE = 1;
		int OVERDUE_OPEN = 2;
		int OVERDUE_CLOSED = 3;
		int PLANNED_VS_UNPLANNED = 4;
		int FIRST_RESPONSE_TIME = 5;
		int ESTIMATED_DURATION = 6;
		int TOTAL_SCORE_PERCENTAGE = 13;
		int RESPONSE_SLA = 14;
		int RESOLUTION_DUE=15;
		int ACCEPTANCE_DUE=16;
	}
	
	public interface Alarm {
		String FIRST_RESPONSE_TIME_COL = "al_firstresponsetime";
		String ALARM_DURATION_COL = "al_duration";
		String WO_ID = "al_wo_id";
		String NEW_FIRST_RESPONSE_TIME_COL = "new_al_firstresponsetime";
		String NEW_ALARM_DURATION_COL = "new_al_duration";
		String NEW_ALARM_DISTINCT_RESOURCE_COL = "new_al_distinct_resource";
		
		int FIRST_RESPONSE_TIME = 8;
		int ALARM_DURATION = 7;
		int WORK_ORDER_ID = 9;
		int NEW_FIRST_RESPONSE_TIME = 10;
		int NEW_ALARM_DURATION = 11;
		int NEW_ALARM_DISTINCT_RESOURCE = 12;
	}

	// integer between 21-25
	public interface AssetBreakDown {
		String TIME_TO_REPAIR_COL = "asset_breakdown_time_to_repair";

		int TIME_TO_REPAIR = 21;
	}

	public static List<FacilioField> reportFields = new ArrayList<>();
	private static Map<String, FacilioField> fieldMap;
	
	static {
		try {
			// workorder fields
			List<FacilioField> reportFields = new ArrayList<>();
			ReportFacilioField openVsCloseField = (ReportFacilioField) getField(WorkOrder.OPENVSCLOSE_COL, "Status Type", ModuleFactory.getWorkOrdersModule(), " CASE WHEN Tickets.MODULE_STATE in (?) THEN 'Closed' ELSE CASE WHEN Tickets.MODULE_STATE in (@) THEN 'Skipped' ELSE CASE WHEN Tickets.MODULE_STATE in (*) THEN 'Open' END END END", FieldType.STRING, WorkOrder.OPENVSCLOSE);
			openVsCloseField.addGenericCondition("Open", CriteriaAPI.getCondition("MODULE_STATE", "moduleState", "*", PickListOperators.IS));
			openVsCloseField.addGenericCondition("Closed", CriteriaAPI.getCondition("MODULE_STATE", "moduleState", "?", PickListOperators.IS));
			openVsCloseField.addGenericCondition("Skipped", CriteriaAPI.getCondition("MODULE_STATE", "moduleState", "@", PickListOperators.IS));
			reportFields.add(openVsCloseField);
			
			ReportFacilioField overdueOpenField = (ReportFacilioField) getField(WorkOrder.OVERDUE_OPEN_COL, "Open Due Status", ModuleFactory.getWorkOrdersModule(), " CASE WHEN Tickets.DUE_DATE IS NOT NULL THEN CASE WHEN Tickets.DUE_DATE < ? THEN 'Overdue' ELSE 'On Schedule' END END ", FieldType.STRING, WorkOrder.OVERDUE_OPEN);
			overdueOpenField.addGenericCondition("Overdue", CriteriaAPI.getCondition("DUE_DATE", "dueDate", "?", NumberOperators.LESS_THAN));
			overdueOpenField.addGenericCondition("On Schedule", CriteriaAPI.getCondition("DUE_DATE", "dueDate", "?", NumberOperators.GREATER_THAN_EQUAL));
			reportFields.add(overdueOpenField);
			
			ReportFacilioField overdueClosedField = (ReportFacilioField) getField(WorkOrder.OVERDUE_CLOSED_COL, "Closed Due Status", ModuleFactory.getWorkOrdersModule(), " CASE WHEN Tickets.DUE_DATE IS NOT NULL AND Tickets.ACTUAL_WORK_END IS NOT NULL THEN CASE WHEN Tickets.DUE_DATE < Tickets.ACTUAL_WORK_END THEN 'Overdue' ELSE 'Ontime' END END ", FieldType.STRING, WorkOrder.OVERDUE_CLOSED);
			overdueClosedField.addGenericCondition("Overdue", CriteriaAPI.getCondition("DUE_DATE", "dueDate", "actualWorkEnd", FieldOperator.LESS_THAN));
			overdueClosedField.addGenericCondition("Ontime", CriteriaAPI.getCondition("DUE_DATE", "dueDate", "actualWorkEnd", FieldOperator.GREATER_THAN_EQUAL));
			reportFields.add(overdueClosedField);
			
			ReportFacilioField responseSlaField = (ReportFacilioField) getField(WorkOrder.RESPONSE_SLA_COL, "Response SLA", ModuleFactory.getWorkOrdersModule(), " CASE WHEN Tickets.SCHEDULED_START IS NOT NULL AND Tickets.ACTUAL_WORK_START IS NOT NULL THEN CASE WHEN Tickets.ACTUAL_WORK_START > Tickets.SCHEDULED_START THEN 'Delayed' ELSE 'Ontime' END END ", FieldType.STRING, WorkOrder.RESPONSE_SLA);
			responseSlaField.addGenericCondition("Delayed", CriteriaAPI.getCondition("ACTUAL_WORK_START", "actualWorkStart", "scheduledStart", FieldOperator.GREATER_THAN));
			responseSlaField.addGenericCondition("Ontime", CriteriaAPI.getCondition("ACTUAL_WORK_START", "actualWorkStart", "scheduledStart", FieldOperator.LESS_THAN_EQUAL));
			reportFields.add(responseSlaField);
			
			ReportFacilioField plannedVsUnplannedField = (ReportFacilioField) getField(WorkOrder.PLANNED_VS_UNPLANNED_COL, "Planned Type", ModuleFactory.getWorkOrdersModule(), " CASE WHEN Tickets.SOURCE_TYPE = 5 THEN 'Planned' ELSE 'Unplanned' END ", FieldType.STRING, WorkOrder.PLANNED_VS_UNPLANNED);
			plannedVsUnplannedField.addGenericCondition("Planned", CriteriaAPI.getCondition("SOURCE_TYPE", "sourceType", "5", EnumOperators.IS));
			plannedVsUnplannedField.addGenericCondition("Unplanned", CriteriaAPI.getCondition("SOURCE_TYPE", "sourceType", "5", EnumOperators.ISN_T));
			reportFields.add(plannedVsUnplannedField);
			
			ReportFacilioField estimatedDurationField = (ReportFacilioField) getField(WorkOrder.ESTIMATED_DURATION_COL, "Estimated Duration", ModuleFactory.getWorkOrdersModule(), " CASE WHEN Tickets.DUE_DATE IS NOT NULL THEN Tickets.DUE_DATE - WorkOrders.CREATED_TIME ELSE 0 END",FieldType.NUMBER, WorkOrder.ESTIMATED_DURATION);
			reportFields.add(estimatedDurationField);
			
			reportFields.add(getField(WorkOrder.FIRST_RESPONSE_TIME_COL, "Response Time", ModuleFactory.getWorkOrdersModule(), "Tickets.ACTUAL_WORK_START - WorkOrders.CREATED_TIME", FieldType.NUMBER, WorkOrder.FIRST_RESPONSE_TIME));
			
			// alarm fields
			reportFields.add(getField(Alarm.FIRST_RESPONSE_TIME_COL, "Response Time", ModuleFactory.getAlarmsModule(), " (Alarms.ACKNOWLEDGED_TIME - Alarms.CREATED_TIME) ", FieldType.NUMBER, Alarm.FIRST_RESPONSE_TIME));
			reportFields.add(getField(Alarm.ALARM_DURATION_COL, "Alarm Duration", ModuleFactory.getAlarmsModule(), "(CASE WHEN Alarms.CLEARED_TIME IS NOT NULL THEN Alarms.CLEARED_TIME - Alarms.CREATED_TIME ELSE ? - Alarms.CREATED_TIME END) ", FieldType.NUMBER, Alarm.ALARM_DURATION));
			
			ReportFacilioField wo_id = (ReportFacilioField) getField(Alarm.WO_ID, "Is Workorder created", ModuleFactory.getAlarmOccurenceModule(), "CASE WHEN AlarmOccurrence.WOID IS NULL THEN 'False' ELSE 'True' END", FieldType.STRING, Alarm.WORK_ORDER_ID);
			wo_id.addGenericCondition("False", CriteriaAPI.getCondition("WOID", "woid", null, CommonOperators.IS_EMPTY));
			wo_id.addGenericCondition("True", CriteriaAPI.getCondition("WOID", "woid", null, CommonOperators.IS_NOT_EMPTY));
			reportFields.add(wo_id);

			// alarm occurrence fields
			reportFields.add(getField(Alarm.NEW_FIRST_RESPONSE_TIME_COL, "Response Time", ModuleFactory.getAlarmOccurenceModule(), " (AlarmOccurrence.ACKNOWLEDGED_TIME - AlarmOccurrence.CREATED_TIME) ", FieldType.NUMBER, Alarm.NEW_FIRST_RESPONSE_TIME));
			reportFields.add(getField(Alarm.NEW_ALARM_DISTINCT_RESOURCE_COL, "Distinct Assets", ModuleFactory.getAlarmOccurenceModule(), " DISTINCT(RESOURCE_ID) ", FieldType.NUMBER, Alarm.NEW_ALARM_DISTINCT_RESOURCE));
			reportFields.add(getField(Alarm.NEW_ALARM_DURATION_COL, "Alarm Duration", ModuleFactory.getAlarmOccurenceModule(), "(CASE WHEN AlarmOccurrence.CLEARED_TIME IS NOT NULL THEN AlarmOccurrence.CLEARED_TIME - AlarmOccurrence.CREATED_TIME ELSE ? - AlarmOccurrence.CREATED_TIME END) ", FieldType.NUMBER, Alarm.NEW_ALARM_DURATION));

			// Asset Breakdown fields
			reportFields.add(getField(AssetBreakDown.TIME_TO_REPAIR_COL, "Time to Repair", ModuleFactory.getAssetBreakdownModule(), "((TO_TIME - FROM_TIME) / 1000)", FieldType.NUMBER, AssetBreakDown.TIME_TO_REPAIR));
			
			
			fieldMap = FieldFactory.getAsMap(reportFields);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static FacilioField getField(String name, String displayName, FacilioModule module, String columnName, FieldType fieldType) {
		return getField(name, displayName, module, columnName, fieldType, -1);
	}
	
	public static FacilioField getField(String name, String displayName, FacilioModule module, String columnName, FieldType fieldType, int type) {
		ReportFacilioField f = new ReportFacilioField(type);
		f.setName(name);
		f.setDisplayName(displayName);
		f.setModule(module);
		f.setGenericColumnName(columnName);
		f.setDataType(fieldType);
		return f;
	}

	public static FacilioField getReportField(String fieldName) {
		FacilioField facilioField = fieldMap.get(fieldName);
		if (facilioField != null) {
			return facilioField;
		}
		switch (fieldName) {
		case "totalscorepercentage":
			if (FacilioProperties.isProduction() && AccountUtil.getCurrentOrg().getOrgId() == 210) {
				return (ReportFacilioField) ReportFactory.getField(WorkOrder.TOTAL_SCORE_PERCENTAGE_COL, "Total Score In Percentage", ModuleFactory.getWorkOrdersModule(), " CASE WHEN WorkOrders.NUMBER_CF9 IS NOT NULL AND WorkOrders.NUMBER_CF13 IS NOT NULL THEN WorkOrders.NUMBER_CF9 / WorkOrders.NUMBER_CF13 * 100 ELSE 0 END",FieldType.NUMBER, WorkOrder.TOTAL_SCORE_PERCENTAGE);
			}
			break;
		case "resolutionduestatus":
			if (FacilioProperties.isProduction() && AccountUtil.getCurrentOrg().getOrgId() == 274) {
				ReportFacilioField ResolutionDueStatusField = (ReportFacilioField) ReportFactory.getField(WorkOrder.RESOLUTION_DUE_COL, "Resolution Due Status", ModuleFactory.getWorkOrdersModule(), " CASE WHEN WorkOrders.DATETIME_CF4 IS NOT NULL AND WorkOrders.DATETIME_CF3 IS NOT NULL THEN CASE WHEN WorkOrders.DATETIME_CF4 < WorkOrders.DATETIME_CF3 THEN 'Overdue' ELSE 'Ontime' END END ",FieldType.STRING, WorkOrder.RESOLUTION_DUE);
				ResolutionDueStatusField.addGenericCondition("Overdue", CriteriaAPI.getCondition("DATETIME_CF4", "datetime_3", "WorkOrders.DATETIME_CF3", NumberOperators.LESS_THAN));
				ResolutionDueStatusField.addGenericCondition("Ontime", CriteriaAPI.getCondition("DATETIME_CF4", "datetime_3", "WorkOrders.DATETIME_CF3", NumberOperators.GREATER_THAN_EQUAL));
				return ResolutionDueStatusField;
			}
			break;
		case "acceptanceduestatus":
			if (FacilioProperties.isProduction() && AccountUtil.getCurrentOrg().getOrgId() == 274) {
				ReportFacilioField AcceptanceDueStatusField = (ReportFacilioField) ReportFactory.getField(WorkOrder.ACCEPTANCE_DUE_COL, "Acceptance Due Status", ModuleFactory.getWorkOrdersModule(), " CASE WHEN WorkOrders.DATETIME_CF6 IS NOT NULL AND WorkOrders.DATETIME_CF5 IS NOT NULL THEN CASE WHEN WorkOrders.DATETIME_CF6 < WorkOrders.DATETIME_CF5 THEN 'Overdue' ELSE 'Ontime' END END ",FieldType.STRING, WorkOrder.ACCEPTANCE_DUE);
				AcceptanceDueStatusField.addGenericCondition("Overdue", CriteriaAPI.getCondition("DATETIME_CF6", "datetime_5", "WorkOrders.DATETIME_CF5", NumberOperators.LESS_THAN));
				AcceptanceDueStatusField.addGenericCondition("Ontime", CriteriaAPI.getCondition("DATETIME_CF6", "datetime_5", "WorkOrders.DATETIME_CF5", NumberOperators.GREATER_THAN_EQUAL));
				return AcceptanceDueStatusField;
			}
			break;
		}
		return null;
	}
	
	public static class ModuleType {
		private String displayName;
		private int type;
		
		public ModuleType(String displayName, int type) {
			this.displayName = displayName;
			this.type = type;
		}

		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
	}
	
	public static class ReportFacilioField extends FacilioField {		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int type;
		private String genericColumnName;
		@JsonIgnore
		private Map<String, Condition> genericConditions = new HashMap<>();
		private Map<String, Condition> conditions = new HashMap<>();
		
		public ReportFacilioField(ReportFacilioField reportFacilioField) {
			super(reportFacilioField);
			this.genericColumnName = reportFacilioField.genericColumnName;
			this.genericConditions = reportFacilioField.genericConditions;
			this.type = reportFacilioField.type;
		}
		
		public ReportFacilioField(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
		
		public void setGenericColumnName(String genericColumnName) {
			this.genericColumnName = genericColumnName;
		}
		public String getGenericColumnName() {
			return genericColumnName;
		}
		
		@JSON(serialize=false)
		public Map<String, Condition> getGenericConditions() {
			return genericConditions;
		}
		
		public void addGenericCondition(String name, Condition condition) {
			genericConditions.put(name, condition);
		}

		@Override
		public String getCompleteColumnName() {
			processData();
			return super.getColumnName();
		}
		
		public Map<String, Condition> getConditions() {
			return conditions;
		}

		private void processData() {
			try {
				switch (type) {
				case WorkOrder.OPENVSCLOSE:
				{
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					
					List<FacilioStatus> closedlist = TicketAPI.getStatusOfStatusType(StatusType.CLOSED);
					Map<String, Object> closeddata = new HashMap<>();
					if (CollectionUtils.isNotEmpty(closedlist)) {
						List<Long> statusIds = new ArrayList<>();
						for (FacilioStatus status : closedlist) {
							statusIds.add(status.getId());
						}
						closeddata.put("closed_status_id", StringUtils.join(statusIds, ","));
					}

					String closedarguments = String.valueOf(closeddata.get("closed_status_id"));
					
					List<FacilioStatus> openlist = TicketAPI.getStatusOfStatusType(StatusType.OPEN);
					Map<String, Object> opendata = new HashMap<>();
					if (CollectionUtils.isNotEmpty(openlist)) {
						List<Long> statusIds = new ArrayList<>();
						for (FacilioStatus status : openlist) {
							statusIds.add(status.getId());
						}
						opendata.put("open_status_id", StringUtils.join(statusIds, ","));
					}

					String openarguments = String.valueOf(opendata.get("open_status_id"));
					
					List<FacilioStatus> skippedlist = TicketAPI.getStatusOfStatusType(StatusType.SKIPPED);
					Map<String, Object> skippeddata = new HashMap<>();
					if (CollectionUtils.isNotEmpty(skippedlist)) {
						List<Long> statusIds = new ArrayList<>();
						for (FacilioStatus status : skippedlist) {
							statusIds.add(status.getId());
						}
						skippeddata.put("skipped_status_id", StringUtils.join(statusIds, ","));
					}

					String skippedarguments = String.valueOf(skippeddata.get("skipped_status_id"));
					
					for (String key : genericConditions.keySet()) {
						Condition condition = genericConditions.get(key);
						Map<String, Object> conditionProperty = FieldUtil.getAsProperties(condition);
						Condition c = FieldUtil.getAsBeanFromMap(conditionProperty, Condition.class);
						String value = c.getValue();
						value = value.replace("?", closedarguments);
						value = value.replace("*", openarguments);
						value = value.replaceAll("@", skippedarguments);
						c.setValue(value);
						conditions.put(key, c);
					}
					
					String columnName = getGenericColumnName();
					columnName = columnName.replace("?", closedarguments);
					columnName = columnName.replace("*", openarguments);
					columnName = columnName.replace("@", skippedarguments);
					setColumnName(columnName);
					closeddata.clear();
					opendata.clear();
					skippeddata.clear();
					break;
				}
					
				case WorkOrder.OVERDUE_OPEN:
				case Alarm.ALARM_DURATION:
				case Alarm.NEW_ALARM_DURATION:
				{
					String arguments = String.valueOf(System.currentTimeMillis());
					
					for (String key : getGenericConditions().keySet()) {
						Condition condition = genericConditions.get(key);
						Map<String, Object> conditionProperty = FieldUtil.getAsProperties(condition);
						Condition c = FieldUtil.getAsBeanFromMap(conditionProperty, Condition.class);
						String value = c.getValue();
						c.setValue(value.replace("?", arguments));
						conditions.put(key, c);
					}
					
					setColumnName(getGenericColumnName().replace("?", arguments));
					break;
				}
				
				default:
				{
					conditions = genericConditions;
					setColumnName(getGenericColumnName());
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public FacilioField clone() {
			return new ReportFacilioField(this);
		}
	}
}
