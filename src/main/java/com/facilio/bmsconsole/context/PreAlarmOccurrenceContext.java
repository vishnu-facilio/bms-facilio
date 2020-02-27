package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections.CollectionUtils;
import org.apache.struts2.json.annotations.JSON;

import java.util.ArrayList;
import java.util.List;

public class PreAlarmOccurrenceContext extends AlarmOccurrenceContext {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private ReadingAlarmCategoryContext readingAlarmCategory;
    public ReadingAlarmCategoryContext getReadingAlarmCategory() {
        return readingAlarmCategory;
    }
    public void setReadingAlarmCategory(ReadingAlarmCategoryContext readingAlarmCategory) {
        this.readingAlarmCategory = readingAlarmCategory;
    }

    @JsonIgnore
    public List<PreEventContext> getPreEvents() throws Exception {
        if (CollectionUtils.isEmpty(preEvents)) {
            getActivePreEventList();
        }
        return preEvents;
    }

    public void setPreEvents(List<PreEventContext> preEvents) {
        this.preEvents = preEvents;
    }

    public void addPreEvent(PreEventContext preBaseEvent) {
        if (preEvents == null) {
            preEvents = new ArrayList<>();
        }
        preEvents.add(preBaseEvent);
    }
    private List<PreEventContext> preEvents;

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

    private long readingFieldId;
    public long getReadingFieldId() {
        return readingFieldId;
    }
    public void setReadingFieldId(long readingFieldId) {
        this.readingFieldId = readingFieldId;
    }

    @Override
    public Type getTypeEnum() {
        return Type.PRE_OCCURRENCE;
    }

    private void getActivePreEventList () throws Exception {
        List<PreEventContext> preEvents = NewAlarmAPI.getPreEventforOccurrence(getId());
        setPreEvents(preEvents);
    }

    public Boolean getReadingEventCreated() {
        return isReadingEventCreated;
    }
    public void setReadingEventCreated(Boolean readingEventCreated) {
        isReadingEventCreated = readingEventCreated;
    }

    public Boolean isReadingEventCreated;
}
