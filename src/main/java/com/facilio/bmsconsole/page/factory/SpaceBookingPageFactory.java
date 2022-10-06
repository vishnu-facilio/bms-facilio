package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

public class SpaceBookingPageFactory extends PageFactory{
   
    public static Page getSpaceBookingPage(V3SpaceBookingContext record, FacilioModule module) throws Exception {
        return getEmployeePortalBookingPage(record, module);
    }

    public static Page getEmployeePortalBookingPage(V3SpaceBookingContext record, FacilioModule module) throws Exception {

        Page page = new Page();

        //  Overview Tab

        Page.Tab tab1 = page.new Tab("Overview");
        page.addTab(tab1);

        Page.Section tab1Sec1 = page.new Section();
        tab1.addSection(tab1Sec1);

//        tab1.setCustomFullPage(true);

        PageWidget bookingFullSmmary = new PageWidget(PageWidget.WidgetType.SPACE_BOOKING_FULL_SUMMARY);
        bookingFullSmmary.addToLayoutParams(tab1Sec1,0,0, 16, 10);
        PageWidget bookingComment = new PageWidget(PageWidget.WidgetType.SPACE_BOOKING_NOTES);
        bookingComment.addToLayoutParams(tab1Sec1,0,10,16,8);
        PageWidget bookingAttachment = new PageWidget(PageWidget.WidgetType.ATTACHMENT);
        bookingAttachment.addToLayoutParams(tab1Sec1,0,18,16,6);
        PageWidget bookingCardDetails = new PageWidget(PageWidget.WidgetType.SPACE_BOOKING_CARD_DETAILS);
        bookingCardDetails.addToLayoutParams(tab1Sec1,16,0,8,24);


        tab1Sec1.addWidget(bookingFullSmmary);
        tab1Sec1.addWidget(bookingComment);
        tab1Sec1.addWidget(bookingAttachment);
        tab1Sec1.addWidget(bookingCardDetails);


        // Activities Tab

        Page.Tab tab2 = page.new Tab("Activities");
        page.addTab(tab2);
        Page.Section tab4Sec1 = page.new Section();
        tab2.addSection(tab4Sec1);
        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
        activityWidget.addToLayoutParams(tab4Sec1, 24, 3);
        activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
        tab4Sec1.addWidget(activityWidget);

        // Service Request Tab

        Page.Tab tab3 = page.new Tab("Service Request");
        page.addTab(tab3);

        Page.Section tab3Sec1 = page.new Section();
        tab1.addSection(tab3Sec1);

        // Tasks Tab

        Page.Tab tab4 = page.new Tab("Tasks");
        page.addTab(tab4);

//        Page.Section tab4Sec1 = page.new Section();
//        tab1.addSection(tab2Sec1);

        return page;
    }

}
