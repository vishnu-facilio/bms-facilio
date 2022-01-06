package com.facilio.bmsconsole.timelineview.context;


import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.modules.fields.FacilioField;

public class TimelineViewContext extends FacilioView {

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


}