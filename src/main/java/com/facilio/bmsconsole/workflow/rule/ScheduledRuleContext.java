package com.facilio.bmsconsole.workflow.rule;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.FacilioTimer;

public class ScheduledRuleContext extends WorkflowRuleContext {
	private long dateFieldId = -1;
	public long getDateFieldId() {
		return dateFieldId;
	}
	public void setDateFieldId(long dateFieldId) {
		this.dateFieldId = dateFieldId;
	}
	
	private FacilioField dateField;
	public FacilioField getDateField() {
		return dateField;
	}
	public void setDateField(FacilioField dateField) {
		this.dateField = dateField;
	}
	
	private ScheduledRuleType scheduleType;
	public ScheduledRuleType getScheduleTypeEnum() {
		return scheduleType;
	}
	public void setScheduleType(ScheduledRuleType scheduleType) {
		this.scheduleType = scheduleType;
	}
	public int getScheduleType() {
		if (scheduleType != null) {
			return scheduleType.getValue();
		}
		return -1;
	}
	public void setScheduleType(int scheduleType) {
		this.scheduleType = ScheduledRuleType.valueOf (scheduleType);
	}

	private long interval = -1; //In Seconds
	public long getInterval() {
		return interval;
	}
	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	private LocalTime time;
	public LocalTime getTimeObj() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	public String getTime() {
		return time == null? null : time.toString();
	}
	public void setTime(String time) {
		this.time = LocalTime.parse(time);
	}
	
	@Override
	public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders,
			FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		Long dateVal = (Long) PropertyUtils.getProperty(record, dateField.getName());
		return dateVal != null && dateVal != -1;
	}
	
	@Override
	public void executeWorkflowActions(Object record, Context context, Map<String, Object> placeHolders)
			throws Exception {
		// TODO Auto-generated method stub
		long timeVal = (long) PropertyUtils.getProperty(record, dateField.getName());
		long id = ((ModuleBaseWithCustomFields) record).getId();
		switch (scheduleType) {
			case BEFORE:
				long adjustedTime = getAdjustedTime(timeVal - (interval * 1000));
				if (adjustedTime >= System.currentTimeMillis()) {
					scheduleJob(adjustedTime, id);
				}
				break;
			case ON:
				scheduleJob(timeVal, id);
				break;
			case AFTER:
				scheduleJob(getAdjustedTime(timeVal + (interval * 1000)), id);
				break;
		}
	}
	
	public void actualWorkflowActionExecution (Object record, Context context, Map<String, Object> placeHolders) throws Exception {
		super.executeWorkflowActions(record, context, placeHolders);
	}
	
	private long getAdjustedTime(long timeVal) {
		if (this.time != null) {
			ZonedDateTime zdt = DateTimeUtil.getDateTime(timeVal);
			zdt = zdt.with(time);
			return zdt.toInstant().toEpochMilli();
		}
		return timeVal;
	}
	
	private void scheduleJob (long time, long recordId) throws Exception {
		FacilioModule module = ModuleFactory.getScheduledRuleJobModule();
		List<FacilioField> fields = FieldFactory.getScheduledRuleJobFields();
		Map<String, Object> scheduledJob = getScheduledJob(module, fields, recordId);
		if (scheduledJob == null) {
			long id = addScheduledJob(module, fields, recordId, time);
			FacilioTimer.scheduleOneTimeJob(id, "ScheduledRuleExecution", time/1000, "facilio");
		}
		else if ((long)scheduledJob.get("scheduledTime") != time) {
			long id = (long) scheduledJob.get("id");
			updateScheduledJob(module, fields, id, time);
			FacilioTimer.deleteJob(id, "ScheduledRuleExecution");
			FacilioTimer.scheduleOneTimeJob(id, "ScheduledRuleExecution", time/1000, "facilio");
		}
	}
	
	private long addScheduledJob(FacilioModule module, List<FacilioField> fields, long recordId, long time) throws Exception {
		Map<String, Object> prop = new HashMap<>();
		prop.put("ruleId", getId());
		prop.put("orgId", AccountUtil.getCurrentOrg().getId());
		prop.put("recordId", recordId);
		prop.put("scheduledTime", time);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.fields(fields)
														.table(module.getTableName())
														;
		return insertBuilder.insert(prop);
	}
	
	private void updateScheduledJob (FacilioModule module, List<FacilioField> fields, long id, long time) throws SQLException {
		Map<String, Object> prop = new HashMap<>();
		prop.put("scheduledTime", time);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(id, module))
														;
		
		updateBuilder.update(prop);
	}
	
	private Map<String, Object> getScheduledJob(FacilioModule module, List<FacilioField> fields, long recordId) throws Exception {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), String.valueOf(getId()), PickListOperators.IS))
														.andCondition(CriteriaAPI.getCondition(fieldMap.get("recordId"), String.valueOf(recordId), PickListOperators.IS))
														;
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return props.get(0);
		}
		return null;
	}

	public static enum ScheduledRuleType {
		BEFORE,
		ON,
		AFTER
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static ScheduledRuleType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
}
