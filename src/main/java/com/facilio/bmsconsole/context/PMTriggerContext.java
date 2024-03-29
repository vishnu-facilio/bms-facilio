package com.facilio.bmsconsole.context;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.modules.FieldUtil;
import com.facilio.taskengine.ScheduleInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class PMTriggerContext implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private long pmId = -1;
	public long getPmId() {
		return pmId;
	}
	public void setPmId(long pmId) {
		this.pmId = pmId;
	}

	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private ScheduleInfo schedule;
	public ScheduleInfo getSchedule() {
		return schedule;
	}
	public void setSchedule(ScheduleInfo schedule) {
		this.schedule = schedule;
	}
	
	public String getScheduleJson() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if(schedule != null) {
			return FieldUtil.getAsJSON(schedule).toJSONString();
		}
		return null;
	}
	public void setScheduleJson(String jsonString) throws JsonParseException, JsonMappingException, IOException, ParseException {
		if(jsonString != null) {
			JSONParser parser = new JSONParser();
			this.schedule = FieldUtil.getAsBeanFromJson((JSONObject)parser.parse(jsonString), ScheduleInfo.class);
		}
	}
	
	public String getScheduleMsg() {
		int triggerExecutionSource = getTriggerExecutionSource();
		TriggerExectionSource triggerExectionSourceEnum = TriggerExectionSource.valueOf(triggerExecutionSource);
		if (triggerExectionSourceEnum == null) {
			return null;
		}
		switch(triggerExectionSourceEnum) {
			case SCHEDULE:
				if(schedule != null) {
					return schedule.getDescription(startTime);
				}
				break;
			case USER:
				return "Manual";
			case CUSTOM:
				return "Custom";
		}
		return null;
	}
	
	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	private long lastGeneratedTime = -1;
	public long getLastGeneratedTime() {
		return lastGeneratedTime;
	}
	public void setLastGeneratedTime(long lastGeneratedTime) {
		this.lastGeneratedTime = lastGeneratedTime;
	}

	private ReadingRuleContext readingRule;
	public ReadingRuleContext getReadingRule() {
		return readingRule;
	}
	public void setReadingRule(ReadingRuleContext readingRule) {
		this.readingRule = readingRule;
	}
	
	private WorkflowRuleContext workFlowRule;
	public void setWorkFlowRule(WorkflowRuleContext workFlowRule) {
		this.workFlowRule = workFlowRule;
	}
	
	public WorkflowRuleContext getWorkFlowRule() {
		return this.workFlowRule;
	}
	
	private long ruleId = -1;
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long readingRuleId) {
		this.ruleId = readingRuleId;
	}
	
	private long templateId = -1;
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	
	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}
	
	private long startReading = -1;
	public long getStartReading() {
		return startReading;
	}
	public void setStartReading(long startReading) {
		this.startReading = startReading;
	}
	
	private long readingInterval = -1;
	public long getReadingInterval() {
		return readingInterval;
	}
	public void setReadingInterval(long readingInterval) {
		this.readingInterval = readingInterval;
	}
	
	private long endReading = -1;
	public long getEndReading() {
		return endReading;
	}
	public void setEndReading(long endReading) {
		this.endReading = endReading;
	}
	
	private TriggerType triggerType;
	private Long assignedTo;
	
	public int getTriggerType() {
		if(triggerType != null) {
			return triggerType.getVal();
		}
		return -1;
	}
	public void setTriggerType(int triggerType) {
		this.triggerType = TriggerType.valueOf(triggerType);
	}
	public Long getAssignedTo() {
		return assignedTo;
	}
	public void setAssignedTo(Long assignedTo) {
		this.assignedTo = assignedTo;
	}

	private SharingContext<SingleSharingContext> sharingContext = new SharingContext<>();

	public SharingContext<SingleSharingContext> getSharingContext() {
		return sharingContext;
	}

	public void setSharingContext(SharingContext<SingleSharingContext> sharingContext) {
		this.sharingContext = sharingContext;
	}

	private ExecuteOn executeOn;

	private long executionOffset = -1L;

	private long customModuleId = -1L;

	private long fieldId = -1L;

	private String customModuleName;

	private String dateFieldName;

	public ExecuteOn getExecuteOnEnum() {
		return this.executeOn;
	}

	public void setExecuteOn(int executeOn) { this.executeOn = ExecuteOn.valueOf(executeOn); }
	public int getExecuteOn() {
		if (executeOn != null) {
			return executeOn.getVal();
		}
		return -1;
	}

	public long getExecutionOffset() {
		return executionOffset;
	}

	public void setExecutionOffset(long executionOffset) {
		this.executionOffset = executionOffset;
	}

	public long getCustomModuleId() {
		return customModuleId;
	}

	public void setCustomModuleId(long customModuleId) {
		this.customModuleId = customModuleId;
	}

	public long getFieldId() {
		return fieldId;
	}

	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}

	public String getCustomModuleName() {
		return customModuleName;
	}

	public void setCustomModuleName(String customModuleName) {
		this.customModuleName = customModuleName;
	}

	public String getDateFieldName() {
		return dateFieldName;
	}

	public void setDateFieldName(String dateFieldName) {
		this.dateFieldName = dateFieldName;
	}

	public enum ExecuteOn {
		ON,
		AFTER,
		BEFORE;

		public int getVal() {
			return ordinal() + 1;
		}
		private static final ExecuteOn[] EXECUTE_ONS = ExecuteOn.values();
		public static ExecuteOn valueOf(int type) {
			if (type > 0 && type <= EXECUTE_ONS.length) {
				return EXECUTE_ONS[type - 1];
			}
			return null;
		}
	}
	
	public enum TriggerType {
		
		DEFAULT,
		CUSTOM,
		;
		
		public int getVal() {
			return ordinal() + 1;
		}
		private static final TriggerType[] PM_TRIGGER_TYPES = TriggerType.values();
		public static TriggerType valueOf(int type) {
			if (type > 0 && type <= PM_TRIGGER_TYPES.length) {
				return PM_TRIGGER_TYPES[type - 1];
			}
			return null;
		}
	}
	
	public enum TriggerExectionSource {
		SCHEDULE,
		READING,
		ALARMRULE,
		USER,
		CUSTOM
		;
		
		public int getVal() {
			return ordinal() + 1;
		}
		
		private static final TriggerExectionSource[] TRIGGER_EXECUTION_SOURCE = TriggerExectionSource.values();
		public static TriggerExectionSource valueOf(int type) {
			if (type > 0 && type <= TRIGGER_EXECUTION_SOURCE.length) {
				return TRIGGER_EXECUTION_SOURCE[type - 1];
			}
			return null;
		}
	}
	
	private TriggerExectionSource triggerExecutionSource;
	public void setTriggerExecutionSource(TriggerExectionSource triggerExecutionSource) {
		this.triggerExecutionSource = triggerExecutionSource;
	}
	
	public TriggerExectionSource getTriggerExecutionSourceEnum() {
		return this.triggerExecutionSource;
	}
	
	public void setTriggerExecutionSource(int triggerType) {
		this.triggerExecutionSource = TriggerExectionSource.valueOf(triggerType);
	}
	
	public int getTriggerExecutionSource() {
		if(triggerExecutionSource != null) {
			return triggerExecutionSource.getVal();
		}
		return -1;
	}
	
	private FacilioFrequency frequency;
	public int getFrequency() {
		if (frequency != null) {
			return frequency.getValue();
		}
		return -1;
	}
	public void setFrequency(int frequency) {
		this.frequency = FacilioFrequency.valueOf(frequency);
	}
	public void setFrequency(FacilioFrequency frequency) {
		this.frequency = frequency;
	}
	public FacilioFrequency getFrequencyEnum() {
		return frequency;
	}

	private Boolean custom;
	public Boolean getCustom() {
		return custom;
	}
	public void setCustom(Boolean custom) {
		this.custom = custom;
	}
	public boolean isCustom() {
		if (custom != null) {
			return custom.booleanValue();
		}
		return false;
	}
}
