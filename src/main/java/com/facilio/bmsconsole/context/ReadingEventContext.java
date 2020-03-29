package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

public class ReadingEventContext extends BaseEventContext {
	private static final long serialVersionUID = 1L;

	@Override
	public String constructMessageKey() {
		if (getResource() != null && getRule() != null) {
			return rule.getId() + "_" + getResource().getId();
		}
		return null;
	}
	
	@Override
	public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
		if (add && baseAlarm == null) {
			baseAlarm = new ReadingAlarm();
		}
		super.updateAlarmContext(baseAlarm, add);
		ReadingAlarm readingAlarm = (ReadingAlarm) baseAlarm;

		if (readingAlarmCategory == null) {
			if (getResource() != null) {
				readingAlarmCategory = NewAlarmAPI.getReadingAlarmCategory(getResource().getId());
			}
		}
		readingAlarm.setReadingAlarmCategory(readingAlarmCategory);

		readingAlarm.setRule(rule);
		readingAlarm.setSubRule(subRule);
		if (readingFieldId != -1) {
			readingAlarm.setReadingFieldId(readingFieldId);
		}
		return baseAlarm;
	}

	@Override
	public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
		if (add && alarmOccurrence == null) {
			alarmOccurrence = new ReadingAlarmOccurrenceContext();
		}

		ReadingAlarmOccurrenceContext readingOccurrence = (ReadingAlarmOccurrenceContext) alarmOccurrence;
		if (readingAlarmCategory == null) {
			if (getResource() != null) {
				readingAlarmCategory = NewAlarmAPI.getReadingAlarmCategory(getResource().getId());
			}
		}
		readingOccurrence.setReadingAlarmCategory(readingAlarmCategory);

		readingOccurrence.setRule(rule);
		readingOccurrence.setSubRule(subRule);
		if (readingFieldId != -1) {
			readingOccurrence.setReadingFieldId(readingFieldId);
		}
		createRuleRollUpEvent(this, context);
		createAssetRollUpEvent(this, context);
		return super.updateAlarmOccurrenceContext(alarmOccurrence, context, add);
	}

	private ReadingAlarmCategoryContext readingAlarmCategory;
	public ReadingAlarmCategoryContext getReadingAlarmCategory() {
		return readingAlarmCategory;
	}
	public void setReadingAlarmCategory(ReadingAlarmCategoryContext readingAlarmCategory) {
		this.readingAlarmCategory = readingAlarmCategory;
	}

	private ReadingRuleContext rule;
	public ReadingRuleContext getRule() {
		return rule;
	}
	public void setRule(ReadingRuleContext rule) {
		this.rule = rule;
	}

	private ReadingRuleContext subRule;
	public ReadingRuleContext getSubRule() {
		return subRule;
	}
	public void setSubRule(ReadingRuleContext subRule) {
		this.subRule = subRule;
	}

	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}

	@Override
	@JsonSerialize
	public Type getEventTypeEnum() {
		return Type.READING_ALARM;
	}

	@JsonIgnore
	@JSON(deserialize = false)
	public void setRuleId(long ruleId) {
		if (ruleId > 0) {
			ReadingRuleContext ruleContext = new ReadingRuleContext();
			ruleContext.setId(ruleId);
			setRule(ruleContext);
		}
	}

	@JsonIgnore
	@JSON(deserialize = false)
	public void setSubRuleId(long subRuleId) {
		if (subRuleId > 0) {
			ReadingRuleContext ruleContext = new ReadingRuleContext();
			ruleContext.setId(subRuleId);
			setSubRule(ruleContext);
		}
	}
	@Override
	public void setBaseAlarm(BaseAlarmContext baseAlarm) {
		super.setBaseAlarm(baseAlarm);
		if (assetRollUpEvent != null) {
			baseAlarm.addAdditionalEvent(assetRollUpEvent);
		}
		if (ruleRollUpEvent != null) {
			baseAlarm.addAdditionalEvent(ruleRollUpEvent);
		}
	}
	public void createRuleRollUpEvent(ReadingEventContext readingEvent, Context context) throws  Exception {
		this.ruleRollUpEvent = constructRuleRollUpEvent(readingEvent);

	}

	public void createAssetRollUpEvent(ReadingEventContext readingEvent, Context context) throws  Exception {
		this.assetRollUpEvent = constructAssetRollUpEvent(readingEvent);
	}


	private AssetRollUpEvent constructAssetRollUpEvent (ReadingEventContext readingEvent) throws Exception {

		AssetRollUpEvent assetEvent = new AssetRollUpEvent();
		assetEvent.setMessage(readingEvent.getMessage());
		assetEvent.setCreatedTime(readingEvent.getCreatedTime());
		assetEvent.setResource(readingEvent.getResource());
		assetEvent.setDescription(readingEvent.getDescription());
		assetEvent.setSiteId(readingEvent.getSiteId());
		assetEvent.setDescription(readingEvent.getDescription());
		assetEvent.setReadingFieldId(readingEvent.getReadingFieldId());
		AlarmSeverityContext alarmSeverity = AlarmAPI.getAlarmSeverity(readingEvent.getSeverity().getId());
		assetEvent.setSeverity(alarmSeverity);
		assetEvent.setRule(readingEvent.getRule());
		assetEvent.setSubRule(readingEvent.getSubRule());
		return assetEvent;

	}

	public AssetRollUpEvent constructClearAssetRollUpEvent (ReadingEventContext readingEvent) throws Exception {
		AssetRollUpEvent assetClearEvent = new AssetRollUpEvent();
		assetClearEvent.setResource(readingEvent.getResource());
		assetClearEvent.setReadingFieldId(readingEvent.getReadingFieldId());
		assetClearEvent.setRule(readingEvent.getRule());
		assetClearEvent.setSubRule(readingEvent.getSubRule());
		assetClearEvent.setEventMessage(readingEvent.getMessage());
		assetClearEvent.setComment("System auto cleared Alarm because associated rule executed clear condition for the associated resource");
		assetClearEvent.setCreatedTime(readingEvent.getCreatedTime());
		assetClearEvent.setAutoClear(true);
		assetClearEvent.setSiteId(readingEvent.getSiteId());
		assetClearEvent.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
		return assetClearEvent;
	}


	private RuleRollUpEvent constructRuleRollUpEvent (ReadingEventContext readingEvent) throws Exception {

		RuleRollUpEvent ruleEvent = new RuleRollUpEvent();
		ruleEvent.setMessage(readingEvent.getMessage());
		ruleEvent.setCreatedTime(readingEvent.getCreatedTime());
		ruleEvent.setResource(readingEvent.getResource());
		ruleEvent.setDescription(readingEvent.getDescription());
		ruleEvent.setSiteId(readingEvent.getSiteId());
		ruleEvent.setDescription(readingEvent.getDescription());
		ruleEvent.setReadingFieldId(readingEvent.getReadingFieldId());
		AlarmSeverityContext alarmSeverity = AlarmAPI.getAlarmSeverity(readingEvent.getSeverity().getId());
		ruleEvent.setSeverity(alarmSeverity);
		ruleEvent.setRule(readingEvent.getRule());
		ruleEvent.setSubRule(readingEvent.getSubRule());
		return ruleEvent;

	}

	public RuleRollUpEvent constructClearRuleRollUpEvent (ReadingEventContext readingEvent) throws Exception {
		RuleRollUpEvent ruleEvent = new RuleRollUpEvent();
		ruleEvent.setResource(readingEvent.getResource());
		ruleEvent.setReadingFieldId(readingEvent.getReadingFieldId());
		ruleEvent.setRule(readingEvent.getRule());
		ruleEvent.setSubRule(readingEvent.getSubRule());
		ruleEvent.setEventMessage(readingEvent.getMessage());
		ruleEvent.setComment("System auto cleared Alarm because associated rule executed clear condition for the associated resource");
		ruleEvent.setCreatedTime(readingEvent.getCreatedTime());
		ruleEvent.setAutoClear(true);
		ruleEvent.setSiteId(readingEvent.getSiteId());
		ruleEvent.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
		return ruleEvent;
	}

	private AssetRollUpEvent assetRollUpEvent;

	private RuleRollUpEvent ruleRollUpEvent;
}
