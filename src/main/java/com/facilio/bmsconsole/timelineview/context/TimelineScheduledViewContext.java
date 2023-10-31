package com.facilio.bmsconsole.timelineview.context;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.calendarview.CommonCalendarViewContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;

public class TimelineScheduledViewContext extends CommonCalendarViewContext implements Serializable {
    private long groupByFieldId;
    public void setGroupByFieldId(long groupByFieldId) {
        this.groupByFieldId = groupByFieldId;
    }
    public long getGroupByFieldId() {
        return groupByFieldId;
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

    @Getter
    private long groupByChildModuleId = -1;
    @Getter
    @Setter
    private FacilioModule childModule;
    public void setGroupByChildModuleId(long groupByChildModuleId) throws Exception {
        ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        this.groupByChildModuleId = groupByChildModuleId;
        if (groupByChildModuleId > 0) {
            setChildModule(modbean.getModule(groupByChildModuleId));
        }
    }
}