package com.facilio.bmsconsole.calendarview;

import com.facilio.constants.FacilioConstants.ViewConstants;
import com.facilio.modules.fields.FacilioField;

import java.io.Serializable;

public class CalendarViewContext implements Serializable {
    private long id;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private long startDateFieldId;
    public void setStartDateFieldId(long startDateFieldId) {
        this.startDateFieldId = startDateFieldId;
    }
    public long getStartDateFieldId() {
        return startDateFieldId;
    }

    private FacilioField startDateField;
    public FacilioField getStartDateField() {
        return startDateField;
    }
    public void setStartDateField(FacilioField startDateField) {
        this.startDateField = startDateField;
    }

    private long endDateFieldId;
    public void setEndDateFieldId(long endDateFieldId) {
        this.endDateFieldId = endDateFieldId;
    }
    public long getEndDateFieldId() {
        return endDateFieldId;
    }

    private FacilioField endDateField;
    public FacilioField getEndDateField() {
        return endDateField;
    }
    public void setEndDateField(FacilioField endDateField) {
        this.endDateField = endDateField;
    }

    private ViewConstants.CalendarViewType defaultCalendarView;
    public int getDefaultCalendarView() {
        if(defaultCalendarView != null) {
            return defaultCalendarView.getIntVal();
        }
        return ViewConstants.CalendarViewType.DAY.getIntVal();
    }
    public void setDefaultCalendarView(int defaultCalendarView) {
        this.defaultCalendarView = ViewConstants.CalendarViewType.TYPE_MAP.get(defaultCalendarView);
    }
    public void setDefaultCalendarView(ViewConstants.CalendarViewType defaultCalendarView) {
        this.defaultCalendarView = defaultCalendarView;
    }
}
