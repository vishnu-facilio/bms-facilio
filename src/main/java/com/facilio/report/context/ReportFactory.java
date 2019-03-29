package com.facilio.report.context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.FieldOperator;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.fw.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportFactory {
	
	public interface WorkOrder {
		String OPENVSCLOSE_COL = "openvsclose";
		String OVERDUE_OPEN_COL = "overdue_open";
		String OVERDUE_CLOSED_COL = "overdue_closed";
		String PLANNED_VS_UNPLANNED_COL = "plannedvsunplanned";
		String FIRST_RESPONSE_TIME_COL = "firstresponsetime";
		
		int OPENVSCLOSE = 1;
		int OVERDUE_OPEN = 2;
		int OVERDUE_CLOSED = 3;
		int PLANNED_VS_UNPLANNED = 4;
		int FIRST_RESPONSE_TIME = 5;
	}
	
	public interface Alarm {
		String FIRST_RESPONSE_TIME_COL = "al_firstresponsetime";
		String ALARM_DURATION_COL = "al_duration";
		
		int FIRST_RESPONSE_TIME = 6;
		int ALARM_DURATION = 7;
	}

	public static List<FacilioField> reportFields = new ArrayList<>();
	private static Map<String, FacilioField> fieldMap;
	
	static {
		try {
			// workorder fields
			List<FacilioField> reportFields = new ArrayList<>();
			ReportFacilioField openVsCloseField = (ReportFacilioField) getField(WorkOrder.OPENVSCLOSE_COL, "Status Type", ModuleFactory.getWorkOrdersModule(), " CASE WHEN STATUS_ID = ? THEN 'Closed' ELSE 'Open' END ", FieldType.NUMBER, WorkOrder.OPENVSCLOSE);
			openVsCloseField.addCondition("Open", CriteriaAPI.getCondition("STATUS_ID", "status", "?", NumberOperators.NOT_EQUALS));
			openVsCloseField.addCondition("Closed", CriteriaAPI.getCondition("STATUS_ID", "status", "?", NumberOperators.EQUALS));
			reportFields.add(openVsCloseField);
			
			ReportFacilioField overdueOpenField = (ReportFacilioField) getField(WorkOrder.OVERDUE_OPEN_COL, "Open Due Status", ModuleFactory.getWorkOrdersModule(), " CASE WHEN DUE_DATE IS NOT NULL THEN CASE WHEN DUE_DATE < ? THEN 'Overdue' ELSE 'On Schedule' END END ", FieldType.NUMBER, WorkOrder.OVERDUE_OPEN);
			overdueOpenField.addCondition("Overdue", CriteriaAPI.getCondition("DUE_DATE", "dueDate", "?", NumberOperators.LESS_THAN));
			overdueOpenField.addCondition("On Schedule", CriteriaAPI.getCondition("DUE_DATE", "dueDate", "?", NumberOperators.GREATER_THAN_EQUAL));
			reportFields.add(overdueOpenField);
			
			ReportFacilioField overdueClosedField = (ReportFacilioField) getField(WorkOrder.OVERDUE_CLOSED_COL, "Closed Due Status", ModuleFactory.getWorkOrdersModule(), " CASE WHEN DUE_DATE IS NOT NULL AND ACTUAL_WORK_END IS NOT NULL THEN CASE WHEN DUE_DATE < ACTUAL_WORK_END THEN 'Overdue' ELSE 'Ontime' END END ", FieldType.NUMBER, WorkOrder.OVERDUE_CLOSED);
			overdueClosedField.addCondition("Overdue", CriteriaAPI.getCondition("DUE_DATE", "dueDate", "actualWorkEnd", FieldOperator.LESS_THAN));
			overdueClosedField.addCondition("Ontime", CriteriaAPI.getCondition("DUE_DATE", "dueDate", "actualWorkEnd", FieldOperator.GREATER_THAN_EQUAL));
			reportFields.add(overdueClosedField);
			
			ReportFacilioField plannedVsUnplannedField = (ReportFacilioField) getField(WorkOrder.PLANNED_VS_UNPLANNED_COL, "Planned Type", ModuleFactory.getWorkOrdersModule(), " CASE WHEN SOURCE_TYPE = 5 THEN 'Planned' ELSE 'Unplanned' END ", FieldType.NUMBER, WorkOrder.PLANNED_VS_UNPLANNED);
			plannedVsUnplannedField.addCondition("Planned", CriteriaAPI.getCondition("SOURCE_TYPE", "sourceType", "5", NumberOperators.EQUALS));
			plannedVsUnplannedField.addCondition("Unplanned", CriteriaAPI.getCondition("SOURCE_TYPE", "sourceType", "5", NumberOperators.NOT_EQUALS));
			reportFields.add(plannedVsUnplannedField);
			
			reportFields.add(getField(WorkOrder.FIRST_RESPONSE_TIME_COL, "Response Time", ModuleFactory.getWorkOrdersModule(), "Tickets.ACTUAL_WORK_START - WorkOrders.CREATED_TIME", FieldType.NUMBER, WorkOrder.FIRST_RESPONSE_TIME));
			
			// alarm fields
			reportFields.add(getField(Alarm.FIRST_RESPONSE_TIME_COL, "Response Time", ModuleFactory.getAlarmsModule(), " (Alarms.ACKNOWLEDGED_TIME - Alarms.CREATED_TIME) ", FieldType.NUMBER, Alarm.FIRST_RESPONSE_TIME));
			reportFields.add(getField(Alarm.ALARM_DURATION_COL, "Alarm Duration", ModuleFactory.getAlarmsModule(), "(CASE WHEN Alarms.CLEARED_TIME IS NOT NULL THEN Alarms.CLEARED_TIME - Alarms.CREATED_TIME ELSE ? - Alarms.CREATED_TIME END) ", FieldType.NUMBER, Alarm.ALARM_DURATION));
			
			
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
		f.setColumnName(columnName);
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
		private Map<String, Object> config;
		private Map<String, Object> data = new HashMap<>();
		private Map<String, Condition> conditions = new HashMap<>();
		
		public ReportFacilioField(ReportFacilioField reportFacilioField) {
			super(reportFacilioField);
			this.type = reportFacilioField.type;
		}
		
		public ReportFacilioField(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
		
		public void addCondition(String name, Condition condition) {
			conditions.put(name, condition);
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
					if (!data.containsKey("closed_status_id")) {
						ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						SelectRecordsBuilder<TicketStatusContext> builder = new SelectRecordsBuilder<TicketStatusContext>()
								.module(modBean.getModule("ticketstatus"))
								.select(modBean.getAllFields("ticketstatus"))
								.beanClass(TicketStatusContext.class)
								.andCondition(CriteriaAPI.getCondition("STATUS_TYPE", "statusType", "2", NumberOperators.EQUALS));
						List<TicketStatusContext> list = builder.get();
						if (CollectionUtils.isNotEmpty(list)) {
							TicketStatusContext ticketStatusContext = list.get(0);
							data.put("closed_status_id", ticketStatusContext.getId());
						}
					}
					String arguments = String.valueOf((Long) data.get("closed_status_id"));
					
					for (Condition c : conditions.values()) {
						String value = c.getValue();
						c.setValue(value.replace("?", arguments));
					}
					
					setColumnName(getColumnName().replace("?", arguments));
					break;
				}
					
				case WorkOrder.OVERDUE_OPEN:
				{
					String arguments = String.valueOf(System.currentTimeMillis());
					
					for (Condition c : conditions.values()) {
						String value = c.getValue();
						c.setValue(value.replace("?", arguments));
					}
					
					setColumnName(getColumnName().replace("?", arguments));
					break;
				}
				case Alarm.ALARM_DURATION:
					String arguments = String.valueOf(System.currentTimeMillis());
					setColumnName(getColumnName().replace("?", arguments));
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
