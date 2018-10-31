package com.facilio.bmsconsole.context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.tasker.ScheduleInfo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class PMTriggerContext {
	
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
		JSONParser parser = new JSONParser();
		this.schedule = FieldUtil.getAsBeanFromJson((JSONObject)parser.parse(jsonString), ScheduleInfo.class);
	}
	
	public String getScheduleMsg() {
		if(schedule != null) {
			return schedule.getDescription(startTime);
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

	private ReadingRuleContext readingRule;
	public ReadingRuleContext getReadingRule() {
		return readingRule;
	}
	public void setReadingRule(ReadingRuleContext readingRule) {
		this.readingRule = readingRule;
	}
	
	private long readingRuleId = -1;
	public long getReadingRuleId() {
		return readingRuleId;
	}
	public void setReadingRuleId(long readingRuleId) {
		this.readingRuleId = readingRuleId;
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
	
	TriggerType triggerType;
	
	Long assignedTo;
	
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
	
	List<PMTriggerResourceContext> pmTriggerResourceContexts;

	public List<PMTriggerResourceContext> getPmTriggerResourceContexts() {
		return pmTriggerResourceContexts;
	}
	public void setPmTriggerResourceContexts(List<PMTriggerResourceContext> pmTriggerResourceContexts) {
		this.pmTriggerResourceContexts = pmTriggerResourceContexts;
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
}
