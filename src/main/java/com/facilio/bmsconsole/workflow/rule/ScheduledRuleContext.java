package com.facilio.bmsconsole.workflow.rule;

import java.time.LocalTime;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class ScheduledRuleContext extends WorkflowRuleContext {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	private long fetchDateVal(Object record) throws Exception {
		Long timeVal = (Long) ((ModuleBaseWithCustomFields) record).getDatum(dateField.getName());
		if (timeVal == null) {
			timeVal = (Long) PropertyUtils.getProperty(record, dateField.getName());
		}
		return timeVal == null ? -1 : timeVal;
	}
	
	@Override
	public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders,
			FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		long dateVal = fetchDateVal(record);
		return dateVal != -1;
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
