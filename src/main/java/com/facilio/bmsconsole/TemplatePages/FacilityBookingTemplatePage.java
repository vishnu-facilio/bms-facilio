package com.facilio.bmsconsole.TemplatePages;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

public class FacilityBookingTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {

        return new PagesContext(null, null, "", null, true, false, false)
                .addWebLayout()
                .addTab("bookinginfo", null, PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("bookinginfo", null, null)
                .addWidget("bookinginfowidget", null, PageWidget.WidgetType.BOOKING_INFO, "webbookinginfo_5_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("bookingslotinformation", null, null)
                .addWidget("bookingslotinformationwidget", "Slot Information", PageWidget.WidgetType.BOOKING_SLOT_INFORMATION, "flexiblewebbookinginfo_12", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .addSection("bookinginternalattendees", null, null)
                .addWidget("bookinginternalattendeeswidget", "Internal Attendee List", PageWidget.WidgetType.BOOKING_INTERNAL_ATTENDEES, "flexiblewebbookinginternalattendees_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("bookingexternalattendees", null, null)
                .addWidget("bookingexternalattendeeswidget", "External Attendee List", PageWidget.WidgetType.BOOKING_EXTERNAL_ATTENDEES, "flexiblewebbookingexternalattendees_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }
}
