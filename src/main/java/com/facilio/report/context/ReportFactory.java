package com.facilio.report.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.FieldOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReportFactory {
	
	public interface WorkOrder {
		String OPENVSCLOSE_COL = "openvsclose";
		String OVERDUE_OPEN_COL = "overdue_open";
		String OVERDUE_CLOSED_COL = "overdue_closed";
		String PLANNED_VS_UNPLANNED_COL = "plannedvsunplanned";
		String FIRST_RESPONSE_TIME_COL = "firstresponsetime";
		String ESTIMATED_DURATION_COL = "estimatedduration";
		
		
		int OPENVSCLOSE = 1;
		int OVERDUE_OPEN = 2;
		int OVERDUE_CLOSED = 3;
		int PLANNED_VS_UNPLANNED = 4;
		int FIRST_RESPONSE_TIME = 5;
		int ESTIMATED_DURATION = 6;
		
	}
	
	public interface Alarm {
		String FIRST_RESPONSE_TIME_COL = "al_firstresponsetime";
		String ALARM_DURATION_COL = "al_duration";
		String WO_ID = "al_wo_id";
		String NEW_FIRST_RESPONSE_TIME_COL = "new_al_firstresponsetime";
		String NEW_ALARM_DURATION_COL = "new_al_duration";
		
		int FIRST_RESPONSE_TIME = 8;
		int ALARM_DURATION = 7;
		int WORK_ORDER_ID = 9;
		int NEW_FIRST_RESPONSE_TIME = 10;
		int NEW_ALARM_DURATION = 11;
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
			ReportFacilioField openVsCloseField = (ReportFacilioField) getField(WorkOrder.OPENVSCLOSE_COL, "Status Type", ModuleFactory.getWorkOrdersModule(), " CASE WHEN STATUS_ID in (?) THEN 'Closed' ELSE 'Open' END ", FieldType.STRING, WorkOrder.OPENVSCLOSE);
			openVsCloseField.addGenericCondition("Open", CriteriaAPI.getCondition("STATUS_ID", "status", "?", NumberOperators.NOT_EQUALS));
			openVsCloseField.addGenericCondition("Closed", CriteriaAPI.getCondition("STATUS_ID", "status", "?", NumberOperators.EQUALS));
			reportFields.add(openVsCloseField);
			
			ReportFacilioField overdueOpenField = (ReportFacilioField) getField(WorkOrder.OVERDUE_OPEN_COL, "Open Due Status", ModuleFactory.getWorkOrdersModule(), " CASE WHEN DUE_DATE IS NOT NULL THEN CASE WHEN DUE_DATE < ? THEN 'Overdue' ELSE 'On Schedule' END END ", FieldType.STRING, WorkOrder.OVERDUE_OPEN);
			overdueOpenField.addGenericCondition("Overdue", CriteriaAPI.getCondition("DUE_DATE", "dueDate", "?", NumberOperators.LESS_THAN));
			overdueOpenField.addGenericCondition("On Schedule", CriteriaAPI.getCondition("DUE_DATE", "dueDate", "?", NumberOperators.GREATER_THAN_EQUAL));
			reportFields.add(overdueOpenField);
			
			ReportFacilioField overdueClosedField = (ReportFacilioField) getField(WorkOrder.OVERDUE_CLOSED_COL, "Closed Due Status", ModuleFactory.getWorkOrdersModule(), " CASE WHEN DUE_DATE IS NOT NULL AND ACTUAL_WORK_END IS NOT NULL THEN CASE WHEN DUE_DATE < ACTUAL_WORK_END THEN 'Overdue' ELSE 'Ontime' END END ", FieldType.STRING, WorkOrder.OVERDUE_CLOSED);
			overdueClosedField.addGenericCondition("Overdue", CriteriaAPI.getCondition("DUE_DATE", "dueDate", "actualWorkEnd", FieldOperator.LESS_THAN));
			overdueClosedField.addGenericCondition("Ontime", CriteriaAPI.getCondition("DUE_DATE", "dueDate", "actualWorkEnd", FieldOperator.GREATER_THAN_EQUAL));
			reportFields.add(overdueClosedField);
			
			ReportFacilioField plannedVsUnplannedField = (ReportFacilioField) getField(WorkOrder.PLANNED_VS_UNPLANNED_COL, "Planned Type", ModuleFactory.getWorkOrdersModule(), " CASE WHEN SOURCE_TYPE = 5 THEN 'Planned' ELSE 'Unplanned' END ", FieldType.STRING, WorkOrder.PLANNED_VS_UNPLANNED);
			plannedVsUnplannedField.addGenericCondition("Planned", CriteriaAPI.getCondition("SOURCE_TYPE", "sourceType", "5", NumberOperators.EQUALS));
			plannedVsUnplannedField.addGenericCondition("Unplanned", CriteriaAPI.getCondition("SOURCE_TYPE", "sourceType", "5", NumberOperators.NOT_EQUALS));
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
			reportFields.add(getField(Alarm.NEW_ALARM_DURATION_COL, "Alarm Duration", ModuleFactory.getAlarmOccurenceModule(), "(CASE WHEN AlarmOccurrence.CLEARED_TIME IS NOT NULL THEN AlarmOccurrence.CLEARED_TIME - AlarmOccurrence.CREATED_TIME ELSE ? - AlarmOccurrence.CREATED_TIME END) ", FieldType.NUMBER, Alarm.NEW_ALARM_DURATION));

			// Asset Breakdown fields
			reportFields.add(getField(AssetBreakDown.TIME_TO_REPAIR_COL, "Time to Repair", ModuleFactory.getAssetBreakdownModule(), "((TO_TIME - FROM_TIME) / 1000)", FieldType.NUMBER, AssetBreakDown.TIME_TO_REPAIR));
			
			fieldMap = FieldFactory.getAsMap(reportFields);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static FacilioField getField(String name, String displayName, FacilioModule module, String columnName, FieldType fieldType) {
		return getField(name, displayName, module, columnName, fieldType, -1);
	}
	
	private static FacilioField getField(String name, String displayName, FacilioModule module, String columnName, FieldType fieldType, int type) {
		ReportFacilioField f = new ReportFacilioField(type);
		f.setName(name);
		f.setDisplayName(displayName);
		f.setModule(module);
		f.setGenericColumnName(columnName);
		f.setDataType(fieldType);
		return f;
	}

	public static FacilioField getReportField(String fieldName) {
		return fieldMap.get(fieldName);
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
					List<FacilioStatus> list = TicketAPI.getStatusOfStatusType(StatusType.CLOSED);
					Map<String, Object> data = new HashMap<>();
					if (CollectionUtils.isNotEmpty(list)) {
						List<Long> statusIds = new ArrayList<>();
						for (FacilioStatus status : list) {
							statusIds.add(status.getId());
						}
						data.put("closed_status_id", StringUtils.join(statusIds, ","));
					}

					String arguments = String.valueOf(data.get("closed_status_id"));
					
					for (String key : genericConditions.keySet()) {
						Condition condition = genericConditions.get(key);
						Map<String, Object> conditionProperty = FieldUtil.getAsProperties(condition);
						Condition c = FieldUtil.getAsBeanFromMap(conditionProperty, Condition.class);
						String value = c.getValue();
						c.setValue(value.replace("?", arguments));
						conditions.put(key, c);
					}
					
					setColumnName(getGenericColumnName().replace("?", arguments));
					data.clear();
					break;
				}
					
				case WorkOrder.OVERDUE_OPEN:
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
				case Alarm.ALARM_DURATION:
				case Alarm.NEW_ALARM_DURATION:
				{
					String arguments = String.valueOf(System.currentTimeMillis());
					setColumnName(getGenericColumnName().replace("?", arguments));
					break;
				}
				
				default:
				{
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
