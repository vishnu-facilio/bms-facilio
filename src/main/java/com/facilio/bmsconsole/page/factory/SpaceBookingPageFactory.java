package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingContext;
import com.facilio.modules.FacilioModule;

public class SpaceBookingPageFactory extends PageFactory{
   
    public static Page getSpaceBookingPage(V3SpaceBookingContext record, FacilioModule module) throws Exception {
        return getEmployeePortalBookingPage(record, module);
    }

    public static Page getEmployeePortalBookingPage(V3SpaceBookingContext record, FacilioModule module) throws Exception {

        Page page = new Page();

        Page.Tab tab1 = page.new Tab("summary");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);
        tab1.setCustomFullPage(true);
        PageWidget bookingFullSmmary = new PageWidget(PageWidget.WidgetType.SPACE_BOOKING_FULL_SUMMARY);
        bookingFullSmmary.addToLayoutParams(tab1Sec1, 24, 10);
        tab1Sec1.addWidget(bookingFullSmmary);

        return page;
    }

}
