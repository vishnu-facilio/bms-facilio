package com.facilio.bmsconsole.calendarview;

import com.facilio.constants.FacilioConstants.ViewConstants;
import com.facilio.modules.fields.FacilioField;

import java.io.Serializable;

public class CalendarViewContext extends CommonCalendarViewContext implements Serializable {

    public CalendarViewContext() {}

    public CalendarViewContext(long startDateFieldId, long endDateFieldId, int defaultCalendarView) {
        this.setStartDateFieldId(startDateFieldId);
        this.setEndDateFieldId(endDateFieldId);
        setDefaultCalendarView(defaultCalendarView);
    }
}
