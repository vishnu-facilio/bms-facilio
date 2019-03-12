package com.facilio.report.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.fw.BeanFactory;

public class ReportFactory {
	public static final int STATUS_CLOSD = 1;

	public static List<FacilioField> reportFields = new ArrayList<>();
	private static Map<String, FacilioField> fieldMap;
	
	static {
		ModuleBean modBean = null;
		try {
			modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			List<FacilioField> reportFields = new ArrayList<>();
			reportFields.add(getField("openvsclose", "Status Type", modBean.getModule("workorder"), " CASE WHEN STATUS_ID = ? THEN 'Closed' ELSE 'Open' END ", FieldType.NUMBER, STATUS_CLOSD));
			reportFields.add(getField("overdue_open", "Open Due Status", modBean.getModule("workorder"), " CASE WHEN DUE_DATE < " + System.currentTimeMillis() + " THEN 'Overdue' ELSE 'On Schedule' END ", FieldType.NUMBER));
			reportFields.add(getField("overdue_closed", "Closed Due Status", modBean.getModule("workorder"), " CASE WHEN DUE_DATE < ACTUAL_WORK_END THEN 'Overdue' ELSE 'Ontime' END ", FieldType.NUMBER));
			reportFields.add(getField("plannedvsunplanned", "Planned Type", modBean.getModule("workorder"), " CASE WHEN SOURCE_TYPE = 5 THEN 'Planned' ELSE 'Unplanned' END ", FieldType.NUMBER));
			
			reportFields.add(getField("firstresponsetime", "Response Time", modBean.getModule("workorder"), "Tickets.ACTUAL_WORK_START - WorkOrders.CREATED_TIME", FieldType.NUMBER));
			
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
	
	public static class ReportFacilioField extends FacilioField {		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int type;
		private Map<String, Object> config;
		private Map<String, Object> data = new HashMap<>();
		
		public ReportFacilioField(ReportFacilioField reportFacilioField) {
			super(reportFacilioField);
			this.type = reportFacilioField.type;
		}
		
		public ReportFacilioField(int type) {
			this.type = type;
		}

		@Override
		public String getCompleteColumnName() {
			processData();
			return super.getColumnName();
		}
		
		private void processData() {
			try {
				switch (type) {
				case STATUS_CLOSD:
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
					setColumnName(getColumnName().replace("?", arguments));
					break;
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
