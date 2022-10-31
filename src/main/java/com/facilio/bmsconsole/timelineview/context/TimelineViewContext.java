package com.facilio.bmsconsole.timelineview.context;


import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.FacilioField;
import com.facilio.recordcustomization.RecordCustomizationContext;
import com.facilio.weekends.WeekendContext;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TimelineViewContext extends FacilioView implements Serializable {

    private long startDateFieldId;

    public void setStartDateFieldId(long startDateFieldId) {
        this.startDateFieldId = startDateFieldId;
    }

    public long getStartDateFieldId() {
        return startDateFieldId;
    }

    private long endDateFieldId;
    public void setEndDateFieldId(long endDateFieldId) {
        this.endDateFieldId = endDateFieldId;
    }
    public long getEndDateFieldId() {
        return endDateFieldId;
    }

    private long groupByFieldId;
    public void setGroupByFieldId(long groupByFieldId) {
        this.groupByFieldId = groupByFieldId;
    }
    public long getGroupByFieldId() {
        return groupByFieldId;
    }

    private FacilioField startDateField;
    public FacilioField getStartDateField() {
        return startDateField;
    }
    public void setStartDateField(FacilioField startDateField) {
        this.startDateField = startDateField;
    }

    private FacilioField endDateField;
    public FacilioField getEndDateField() {
        return endDateField;
    }
    public void setEndDateField(FacilioField endDateField) {
        this.endDateField = endDateField;
    }

    private FacilioField groupByField;
    public FacilioField getGroupByField() {
        return groupByField;
    }
    public void setGroupByField(FacilioField groupByField) {
        this.groupByField = groupByField;
    }

    @JsonInclude
    private boolean allowRescheduling;
    public boolean isAllowRescheduling() { return allowRescheduling; }
    public void setAllowRescheduling(boolean allowRescheduling) {
        this.allowRescheduling = allowRescheduling;
    }

    @JsonInclude
    private boolean allowGroupAssignment;
    public boolean isAllowGroupAssignment() { return allowGroupAssignment; }
    public void setAllowGroupAssignment(boolean allowGroupAssignment) { this.allowGroupAssignment = allowGroupAssignment; }

    @JsonInclude
    private boolean allowReAssignment;
    public boolean isAllowReAssignment() {
        return allowReAssignment;
    }
    public void setAllowReAssignment(boolean allowReAssignment) {
        this.allowReAssignment = allowReAssignment;
    }

    @JsonInclude
    private boolean allowPastAssignment;
    public boolean isAllowPastAssignment() {
        return allowPastAssignment;
    }
    public void setAllowPastAssignment(boolean allowPastAssignment) { this.allowPastAssignment = allowPastAssignment; }

    @JsonInclude
    private boolean allowCreate;
    public boolean isAllowCreate() {
        return allowCreate;
    }
    public void setAllowCreate(boolean allowCreate) {
        this.allowCreate = allowCreate;
    }

    @JsonInclude
    private boolean disableWeekends;
    public boolean isDisableWeekends() {
        return disableWeekends;
    }
    public void setDisableWeekends(boolean disableWeekends) {
        this.disableWeekends = disableWeekends;
    }

    @JsonInclude
    private boolean disablePastEvents;
    public boolean isDisablePastEvents() {
        return disablePastEvents;
    }
    public void setDisablePastEvents(boolean disablePastEvents) {
        this.disablePastEvents = disablePastEvents;
    }

    private long weekendId;
    public long getWeekendId() { return weekendId; }
    public void setWeekendId(long weekendId) { this.weekendId = weekendId; }

    private WeekendContext weekend;
    public WeekendContext getWeekend() {
        return weekend;
    }
    public void setWeekend(WeekendContext weekend) {
        this.weekend = weekend;
    }

    private JSONObject configJson;
    public String getConfigJson() {
        return (configJson != null) ? configJson.toJSONString() : null;
    }
    public void setConfigJson(JSONObject configJson) {
        this.configJson = configJson;
    }
    public void setConfigJson(String data) throws ParseException{
        if (StringUtils.isNotEmpty(data)) {
            this.configJson = (JSONObject) new JSONParser().parse(data);
        }
    }

    private long recordCustomizationId;
    public long getRecordCustomizationId() {
        return recordCustomizationId;
    }
    public void setRecordCustomizationId(long recordCustomizationId) {
        this.recordCustomizationId = recordCustomizationId;
    }

    private RecordCustomizationContext recordCustomization;
    public RecordCustomizationContext getRecordCustomization() {
        return recordCustomization;
    }
    public void setRecordCustomization(RecordCustomizationContext recordCustomization) {
        this.recordCustomization = recordCustomization;
    }

    private CalendarViewType defaultCalendarView;
    public int getDefaultCalendarView() {
        if(defaultCalendarView != null) {
            return defaultCalendarView.getIntVal();
        }
        return CalendarViewType.DAY.getIntVal();
    }
    public void setDefaultCalendarView(int defaultCalendarView) {
        this.defaultCalendarView = CalendarViewType.TYPE_MAP.get(defaultCalendarView);
    }
    public void setDefaultCalendarView(CalendarViewType defaultCalendarView) {
        this.defaultCalendarView = defaultCalendarView;
    }

    /*
    private long nameFieldPatternId;
    public long getNameFieldPatternId() {
        return nameFieldPatternId;
    }
    public void setNameFieldPatternId(long nameFieldPatternId) {
        this.nameFieldPatternId = nameFieldPatternId;
    }

    private FieldDisplayPattern nameFieldPattern;
    public FieldDisplayPattern getNameFieldPattern() {
        return nameFieldPattern;
    }
    public void setNameFieldPattern(FieldDisplayPattern nameFieldPattern) {
        this.nameFieldPattern = nameFieldPattern;
    }
    */

    private long groupCriteriaId;
    public long getGroupCriteriaId() {
        return groupCriteriaId;
    }
    public void setGroupCriteriaId(long groupCriteriaId) {
        this.groupCriteriaId = groupCriteriaId;
    }

    private Criteria groupCriteria;
    public Criteria getGroupCriteria() {
        return groupCriteria;
    }
    public void setGroupCriteria(Criteria groupCriteria) {
        this.groupCriteria = groupCriteria;
    }

    public static enum CalendarViewType {
        DAY(1),
        WEEK(2),
        MONTH(3),
        YEAR(4);

        private int intVal;

        private CalendarViewType(int val) {
            // TODO Auto-generated constructor stub
            this.intVal = val;
        }
        public static CalendarViewType getCalendarViewType(int val){
            return TYPE_MAP.get(val);
        }
        public int getIntVal() {
            return intVal;
        }

        private static final Map<Integer, CalendarViewType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
        private static Map<Integer, CalendarViewType> initTypeMap() {
            Map<Integer, CalendarViewType> typeMap = new HashMap<>();
            for(CalendarViewType type : values()) {
                typeMap.put(type.getIntVal(), type);
            }
            return typeMap;
        }
    }
}