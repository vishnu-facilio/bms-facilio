package com.facilio.bmsconsole.calendarview;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.recordcustomization.RecordCustomizationContext;
import com.facilio.weekends.WeekendContext;

import java.io.Serializable;

public class CommonCalendarViewContext implements Serializable {
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
    private long endDateFieldId;
    public void setEndDateFieldId(long endDateFieldId) {
        this.endDateFieldId = endDateFieldId;
    }
    public long getEndDateFieldId() {
        return endDateFieldId;
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
    private FacilioConstants.ViewConstants.CalendarViewType defaultCalendarView;
    public int getDefaultCalendarView() {
        if(defaultCalendarView != null) {
            return defaultCalendarView.getIntVal();
        }
        return FacilioConstants.ViewConstants.CalendarViewType.MONTH.getIntVal();
    }

    public FacilioConstants.ViewConstants.CalendarViewType getDefaultCalendarViewEnum() {
        return defaultCalendarView;
    }
    public void setDefaultCalendarView(int defaultCalendarView) {
        this.defaultCalendarView = FacilioConstants.ViewConstants.CalendarViewType.TYPE_MAP.get(defaultCalendarView);
    }
    public void setDefaultCalendarView(FacilioConstants.ViewConstants.CalendarViewType defaultCalendarView) {
        this.defaultCalendarView = defaultCalendarView;
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
}
