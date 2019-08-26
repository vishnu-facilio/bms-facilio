package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.struts2.json.annotations.JSON;

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
}
