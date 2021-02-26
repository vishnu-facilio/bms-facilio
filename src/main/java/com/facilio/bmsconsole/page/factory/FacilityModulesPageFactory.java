package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.modules.FacilioModule;

public class FacilityModulesPageFactory extends PageFactory{
    public static Page getFacilityPage(FacilityContext record, FacilioModule module) throws Exception {

        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        PageWidget previewWidget = new PageWidget(PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET);
        previewWidget.addToLayoutParams(tab1Sec1, 24, 6);
        tab1Sec1.addWidget(previewWidget);

        Page.Section tab1Sec2 = page.new Section("Photos");
        tab1.addSection(tab1Sec2);
        PageWidget photosWidget = new PageWidget(PageWidget.WidgetType.FACILITY_PHOTOS);
        photosWidget.addToLayoutParams(tab1Sec2, 24, 3);
        tab1Sec2.addWidget(photosWidget);

        Page.Section tab1Sec3 = page.new Section("Features Available");
        tab1.addSection(tab1Sec3);
        PageWidget featuresWidget = new PageWidget(PageWidget.WidgetType.FACILITY_FEATURES);
        featuresWidget.addToLayoutParams(tab1Sec3, 24, 2);
        tab1Sec3.addWidget(featuresWidget);

        Page.Section tab1Sec4 = page.new Section("Slot Information");
        tab1.addSection(tab1Sec4);
        PageWidget slotInfoWidget = new PageWidget(PageWidget.WidgetType.FACILITY_SLOT_INFORMATION);
        slotInfoWidget.addToLayoutParams(tab1Sec4, 24, 4);
        tab1Sec4.addWidget(slotInfoWidget);

        Page.Section tab1Sec5 = page.new Section();
        tab1.addSection(tab1Sec5);
        PageWidget specialAvailabilityWidget = new PageWidget(PageWidget.WidgetType.FACILITY_SPECIAL_AVAILABILITY);
        specialAvailabilityWidget.addToLayoutParams(tab1Sec5, 24, 4);
        tab1Sec5.addWidget(specialAvailabilityWidget);

        addCommonSubModuleWidget(tab1Sec5, module, record);


        return page;
    }
    public static Page getFacilityBookingPage(V3FacilityBookingContext record, FacilioModule module) throws Exception {

        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        PageWidget bookingInfoWidget = new PageWidget(PageWidget.WidgetType.BOOKING_INFO);
        bookingInfoWidget.addToLayoutParams(tab1Sec1, 24, 2);
        tab1Sec1.addWidget(bookingInfoWidget);

        Page.Section tab1Sec2 = page.new Section("Slot Information");
        tab1.addSection(tab1Sec2);
        PageWidget slotInfo = new PageWidget(PageWidget.WidgetType.BOOKING_SLOT_INFORMATION);
        slotInfo.addToLayoutParams(tab1Sec2, 24, 2);
        tab1Sec2.addWidget(slotInfo);

        Page.Section tab1Sec3 = page.new Section();
        tab1.addSection(tab1Sec3);
        PageWidget bookingInternalAttendeesWidget = new PageWidget(PageWidget.WidgetType.BOOKING_INTERNAL_ATTENDEES);
        bookingInternalAttendeesWidget.addToLayoutParams(tab1Sec3, 24, 2);
        tab1Sec3.addWidget(bookingInternalAttendeesWidget);

        Page.Section tab1Sec4 = page.new Section();
        tab1.addSection(tab1Sec4);
        PageWidget bookingExternalAttendeesWidget = new PageWidget(PageWidget.WidgetType.BOOKING_EXTERNAL_ATTENDEES);
        bookingExternalAttendeesWidget.addToLayoutParams(tab1Sec4, 24, 2);
        tab1Sec4.addWidget(bookingExternalAttendeesWidget);

        Page.Section tab1Sec5 = page.new Section();
        tab1.addSection(tab1Sec5);

        addCommonSubModuleWidget(tab1Sec5, module, record);


        return page;
    }

}
